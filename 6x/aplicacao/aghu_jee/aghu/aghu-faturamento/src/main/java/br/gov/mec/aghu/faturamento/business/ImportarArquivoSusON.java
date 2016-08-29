package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.FileUtil;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportarArquivoSusON extends BaseBMTBusiness {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final Log LOG = LogFactory.getLog(ImportarArquivoSusON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatCompetenciaDAO fatCompetenciaDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ProcessadorArquivosImportacaoSus processadorArquivosImportacaoSus;

	private static final long serialVersionUID = -9083355940720431539L;

	protected enum ImportarArquivoSusONExceptionCode implements BusinessExceptionCode {
		ERRO_LIMPAR_PROCEDIMENTOS_CBO, MSG_ITENS_PROCEDIMENTO_NAO_ENCONTRADO, MSG_INICIO_PROCESSAMENTO, MSG_FIM_PROCESSAMENTO, MSG_EXCLUSAO_PROCEDIMENTOS, MSG_ERRO_LEITURA_CO_PROCEDIMENTO, MSG_ERRO_LEITURA_TAMANHO, MSG_INICIO_PROCESSAMENTO_MODALIDADE, MSG_ERRO_LEITURA_CO_MODALIDADE, ARQUIVO_NAO_ENCONTRADO, ERRO_LER_COMPETENCIA, NOME_ARQUIVO_INVALIDO, MSG_COMPARANDO_DIFERENCAS, MSG_ERRO_LEITURA_PROCEDIMENTO, PROCESSANDO_REGISTROS_ARQUIVO, CARREGANDO, INCLUINDO, TABELA_LIDA_ATUALIZADA_SUCESSO, LEIAUTE_INCOMPATIVEL, ERRO_ATUALIZACAO_TABELA, EXCLUINDO_REGISTROS_FAT_PROCED_HOSP_INT_CID, MSG_ERRO_LEITURA_LINHA_FORMA_ORGANIZACAO, MSG_ERRO_BUSCA_GRUPO, ARQUIVO_NAO_ENCONTRADO_COM_NOME, IMPORTACAO_PENDENTE, PROBLEMAS_CRIAR_LOG
	}

	public void verificaNomeArquivoZip(final String arquivo) throws ApplicationBusinessException {
		final List<FatCompetencia> competencias = getFatCompetenciaDAO().obterListaAtivaPorModulo(DominioModuloCompetencia.AMB);
		
		if (competencias.isEmpty()) {
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ERRO_LER_COMPETENCIA);
		} else {
			String mes;
			if (competencias.get(0).getId().getMes() < 10) {
				mes = "0" + competencias.get(0).getId().getMes().toString();
			} else {
				mes = competencias.get(0).getId().getMes().toString();
			}
			final String nomeArq = "TabelaUnificada_" + competencias.get(0).getId().getAno() + mes + ".zip";

			if (!arquivo.equalsIgnoreCase(nomeArq)) {
				throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.NOME_ARQUIVO_INVALIDO, nomeArq);
			}
		}
	}

	private void verificarConcorrencia(final String arquivo, final AghSistemas sistema) throws ApplicationBusinessException {
		final AghArquivoProcessamento arquivoProcessamento = aghuFacade.obterArquivoNaoProcessado(sistema, arquivo);
		if (arquivoProcessamento != null) {
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.IMPORTACAO_PENDENTE);
		}
	}

	private void atualizarProcessosAbortados(final AghSistemas sistema) throws ApplicationBusinessException {
		final BigDecimal minutos = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TEMPO_LIMITE_PROCESSAMENTO_ARQUIVOS).getVlrNumerico();
		// BigDecimal minutos = new BigDecimal(30);
		final List<Integer> arquivosAbortados = aghuFacade.pesquisarIdsArquivosAbortados(sistema, minutos.intValue());
		if (arquivosAbortados != null && !arquivosAbortados.isEmpty()) {
			aghuFacade.finalizarAghArquivoProcessamento(arquivosAbortados);
		}
	}

	public Map<String, Object> atualizarItensProcedimentoHospitalar(final List<String> lista) throws BaseException {

		// Argumentos paramentrizados
		String nomeArquivoProcedimentos = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_ITENS_PROCED_HOSP).toLowerCase(); // "tb_procedimento";
		
		// confere parâmetro necessário
		parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PROCEDIMENTOS_IGNORADOS_IMPORTACAO_VALOR);
		parametroFacade.buscarAghParametro(AghuParametrosEnum.P_IMPORTACAO_PROCED_FCF);
		parametroFacade.buscarAghParametro(AghuParametrosEnum.P_IMPORTACAO_PROCED_FCC);
		
		// Abertura de arquivos
		try {
			nomeArquivoProcedimentos = FileUtil.arquivoExiste(lista, nomeArquivoProcedimentos);
		} catch (final ApplicationBusinessException e1) {
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, nomeArquivoProcedimentos);
		}
		final File arquivoProcedimentos = new File(nomeArquivoProcedimentos);
		final AghSistemas sistema = aghuFacade.obterAghSistema(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_FATURAMENTO).getVlrTexto());
		atualizarProcessosAbortados(sistema);
		verificarConcorrencia(arquivoProcedimentos.getName(), sistema);
		final List<AghArquivoProcessamento> arquivos = new ArrayList<AghArquivoProcessamento>(1);
			
		try {
			arquivos.add(criarArquivo(arquivoProcedimentos, sistema, 1));
		} catch (final IOException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, nomeArquivoProcedimentos);
		}
		
		File file = null;
		
		try {
			file = File.createTempFile(IFaturamentoFacade.NOME_ARQUIVO_LOG_PROCEDIMENTOS + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM), IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG);
			} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}
		
		getProcessadorArquivosImportacaoSus().processarItensProcedimentoHospitalar(file, lista);
		final Map<String, Object> retorno = new HashMap<String, Object>();
		retorno.put(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS, arquivos);
		retorno.put(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS, file.getAbsolutePath());
		return retorno;	
						
	}

	private AghArquivoProcessamento criarArquivo(final File arquivo, final AghSistemas sistema, final int ordem) throws IOException, ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final FileInputStream reader = new FileInputStream(arquivo);
		final AghArquivoProcessamento aghArquivo = new AghArquivoProcessamento();
		aghArquivo.setArquivo(new byte[(int) arquivo.length()]);
		reader.read(aghArquivo.getArquivo());
		aghArquivo.setNome(arquivo.getName());
		aghArquivo.setDthrCriadoEm(new Date());
		aghArquivo.setOrdemProcessamento(ordem);
		aghArquivo.setPercentualProcessado(0);
		aghArquivo.setSistema(sistema);
		aghArquivo.setUsuario(servidorLogado);
		return aghuFacade.persistirAghArquivoProcessamento(aghArquivo);
	}


	public Map<String, Object> atualizarGeral(final List<String> lista) throws ApplicationBusinessException {
		
		// Argumentos paramentrizados
		String nomeArquivoGrupo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_GRUPO).toLowerCase(); // "tb_ocupacao.txt";
		String nomeArquivoSubGrupo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_SUB_GRUPO).toLowerCase(); // "rl_procedimento_ocupacao.txt";
		String nomeArquivoFormasOrganizacao = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_FORMA_ORGANIZACAO).toLowerCase(); // "tb_ocupacao.txt";
		String nomeArquivoModalidade = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_MODALIDADE).toLowerCase(); // "rl_procedimento_ocupacao.txt";
		String nomeArquivoModalidadeProcedimento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_MODALIDADE).toLowerCase(); // "rl_procedimento_modalidade.txt";
		String nomeArquivoItensProcedHosp = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_ITENS_PROCED_HOSP).toLowerCase(); // "tb_procedimento.txt";

		String nomeArquivoCompatibilidade = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_COMPATIBILIDADE).toLowerCase(); 
		String nmArquivoProcedServ = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_PROCED_SERVICO).toLowerCase(); 
		String nmArquivoServ = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_SERVICO).toLowerCase();
		String nmArquivoServClass = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_SERVICO_CLASSIFICACAO).toLowerCase(); 
		
		//TODO Toni: chamada comentanda pq nao entrará na versão 4.0, somente na 4.1
		String nomeArquivoOcupacao = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_OCUPACAO_CBO).getVlrTexto().toLowerCase(); // "tb_ocupacao.txt";
		String nomeArquivoProcedimentosCBO = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_CBO).getVlrTexto().toLowerCase(); // "rl_procedimento_ocupacao.txt";
		String nomeArquivoProcedimentoCID = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_CID).toLowerCase();

		String arquivoAbrindo = "";
		// Abertura de arquivos
		try {
			arquivoAbrindo = nomeArquivoGrupo;
			nomeArquivoGrupo = FileUtil.arquivoExiste(lista, nomeArquivoGrupo);
			arquivoAbrindo = nomeArquivoSubGrupo;
			nomeArquivoSubGrupo = FileUtil.arquivoExiste(lista, nomeArquivoSubGrupo);
			arquivoAbrindo = nomeArquivoFormasOrganizacao;
			nomeArquivoFormasOrganizacao = FileUtil.arquivoExiste(lista, nomeArquivoFormasOrganizacao);
			arquivoAbrindo = nomeArquivoModalidade;
			nomeArquivoModalidade = FileUtil.arquivoExiste(lista, nomeArquivoModalidade);
			arquivoAbrindo = nomeArquivoModalidadeProcedimento;
			nomeArquivoModalidadeProcedimento = FileUtil.arquivoExiste(lista, nomeArquivoModalidadeProcedimento);
			arquivoAbrindo = nomeArquivoItensProcedHosp;
			nomeArquivoItensProcedHosp = FileUtil.arquivoExiste(lista, nomeArquivoItensProcedHosp);
			arquivoAbrindo = nomeArquivoOcupacao;
			nomeArquivoOcupacao = FileUtil.arquivoExiste(lista, nomeArquivoOcupacao);
			arquivoAbrindo = nomeArquivoProcedimentosCBO;
			nomeArquivoProcedimentosCBO = FileUtil.arquivoExiste(lista, nomeArquivoProcedimentosCBO);
			arquivoAbrindo = nomeArquivoProcedimentoCID;
			nomeArquivoProcedimentoCID = FileUtil.arquivoExiste(lista, nomeArquivoProcedimentoCID);

			arquivoAbrindo = nomeArquivoCompatibilidade;
			nomeArquivoCompatibilidade = FileUtil.arquivoExiste(lista, nomeArquivoCompatibilidade);
			arquivoAbrindo = nmArquivoProcedServ;
			nmArquivoProcedServ = FileUtil.arquivoExiste(lista, nmArquivoProcedServ);
			arquivoAbrindo = nmArquivoServ;
			nmArquivoServ = FileUtil.arquivoExiste(lista, nmArquivoServ);
			arquivoAbrindo = nmArquivoServClass;
			nmArquivoServClass = FileUtil.arquivoExiste(lista, nmArquivoServClass);
		} catch (final ApplicationBusinessException e1) {
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, arquivoAbrindo);
		}

		final File arquivoGrupo = new File(nomeArquivoGrupo);
		final File arquivoSubGrupo = new File(nomeArquivoSubGrupo);
		final File arquivoFormasOrganizacao = new File(nomeArquivoFormasOrganizacao);
		final File arquivoModalidade = new File(nomeArquivoModalidade);
		final File arquivoModalidadeProcedimento = new File(nomeArquivoModalidadeProcedimento);
		final File arquivoItensProcedHosp = new File(nomeArquivoItensProcedHosp);
		final File arquivoOcupacao = new File(nomeArquivoOcupacao);
		final File arquivoProcedimentosCBO = new File(nomeArquivoProcedimentosCBO);
		final File arquivoProcedimentoCID = new File(nomeArquivoProcedimentoCID);

		final File arquivoCompatibilidade = new File(nomeArquivoCompatibilidade);
		final File arquivoProcedServ = new File(nmArquivoProcedServ);
		final File arquivoServ = new File(nmArquivoServ);
		final File arquivoServClass = new File(nmArquivoServClass);

		final AghSistemas sistema = aghuFacade.obterAghSistema(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_FATURAMENTO).getVlrTexto());

		atualizarProcessosAbortados(sistema);
		verificarConcorrencia(arquivoProcedimentoCID.getName(), sistema);
		verificarConcorrencia(arquivoItensProcedHosp.getName(), sistema);

		verificarConcorrencia(arquivoProcedimentosCBO.getName(), sistema);
		verificarConcorrencia(arquivoCompatibilidade.getName(), sistema);
		verificarConcorrencia(arquivoProcedServ.getName(), sistema);

		final List<AghArquivoProcessamento> arquivos = new ArrayList<AghArquivoProcessamento>(8);
		try {
			arquivoAbrindo = nomeArquivoGrupo;
			arquivos.add(criarArquivo(arquivoGrupo, sistema, 1));
			arquivoAbrindo = nomeArquivoSubGrupo;
			arquivos.add(criarArquivo(arquivoSubGrupo, sistema, 2));
			arquivoAbrindo = nomeArquivoFormasOrganizacao;
			arquivos.add(criarArquivo(arquivoFormasOrganizacao, sistema, 3));
			arquivoAbrindo = nomeArquivoModalidade;
			arquivos.add(criarArquivo(arquivoModalidade, sistema, 4));
			arquivoAbrindo = nomeArquivoModalidadeProcedimento;
			arquivos.add(criarArquivo(arquivoModalidadeProcedimento, sistema, 8));
			arquivoAbrindo = nomeArquivoItensProcedHosp;
			arquivos.add(criarArquivo(arquivoItensProcedHosp, sistema, 5));
			arquivoAbrindo = nomeArquivoOcupacao;
			arquivos.add(criarArquivo(arquivoOcupacao, sistema, 6));
			arquivoAbrindo = nomeArquivoProcedimentosCBO;
			arquivos.add(criarArquivo(arquivoProcedimentosCBO, sistema, 7));
			arquivoAbrindo = nomeArquivoProcedimentoCID;
			arquivos.add(criarArquivo(arquivoProcedimentoCID, sistema, 9));

			arquivoAbrindo = nomeArquivoCompatibilidade;
			arquivos.add(criarArquivo(arquivoCompatibilidade, sistema, 10));
			arquivoAbrindo = nmArquivoProcedServ;
			arquivos.add(criarArquivo(arquivoProcedServ, sistema, 11));
			arquivoAbrindo = nmArquivoServ;
			arquivos.add(criarArquivo(arquivoServ, sistema, 12));
			arquivoAbrindo = nmArquivoServClass;
			arquivos.add(criarArquivo(arquivoServClass, sistema, 13));
		} catch (final IOException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, arquivoAbrindo);
		}
		
		File file = null;
		try {
			file = File.createTempFile(IFaturamentoFacade.NOME_ARQUIVO_LOG_GERAL + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM), IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG);
			} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}

		getProcessadorArquivosImportacaoSus().processarGeral(file, lista);
		final Map<String, Object> retorno = new HashMap<String, Object>();
		retorno.put(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS, arquivos);
		retorno.put(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS, file.getAbsolutePath());
		return retorno;
	}
	
	public Map<String, Object> atualizarFinanciamento(final List<String> lista) throws ApplicationBusinessException {
		
		
		String nomeArquivoItensProcedHosp = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_ITENS_PROCED_HOSP).toLowerCase(); // "tb_procedimento.txt";
		String nomeArquivoFinanciamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_FINANCIAMENTO).toLowerCase(); // "tb_procedimento.txt";

		
		String arquivoAbrindo = "";
		// Abertura de arquivos
		try {
			
			arquivoAbrindo = nomeArquivoItensProcedHosp;
			nomeArquivoItensProcedHosp = FileUtil.arquivoExiste(lista, nomeArquivoItensProcedHosp);
			
			arquivoAbrindo = nomeArquivoFinanciamento;
			nomeArquivoFinanciamento = FileUtil.arquivoExiste(lista, nomeArquivoFinanciamento);
			
		} catch (final ApplicationBusinessException e1) {
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, arquivoAbrindo);
		}

		
		final File arquivoItensProcedHosp = new File(nomeArquivoItensProcedHosp);
		final File arquivoFinancimaneto = new File(nomeArquivoFinanciamento);

		final AghSistemas sistema = aghuFacade.obterAghSistema(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_FATURAMENTO).getVlrTexto());

		atualizarProcessosAbortados(sistema);
		
		verificarConcorrencia(arquivoItensProcedHosp.getName(), sistema);
		verificarConcorrencia(arquivoFinancimaneto.getName(), sistema);

		final List<AghArquivoProcessamento> arquivos = new ArrayList<AghArquivoProcessamento>(8);
		try {
			
			arquivoAbrindo = nomeArquivoItensProcedHosp;
			arquivos.add(criarArquivo(arquivoItensProcedHosp, sistema, 5));
			
			arquivoAbrindo = nomeArquivoFinanciamento;
			arquivos.add(criarArquivo(arquivoFinancimaneto, sistema, 6));

		} catch (final IOException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, arquivoAbrindo);
		}
		
		File file = null;
		try {
			file = File.createTempFile(IFaturamentoFacade.NOME_ARQUIVO_LOG_GERAL + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM), IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG);
			} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}

		getProcessadorArquivosImportacaoSus().processarFinancimanto(file);
		final Map<String, Object> retorno = new HashMap<String, Object>();
		retorno.put(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS, arquivos);
		retorno.put(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS, file.getAbsolutePath());
		return retorno;
	}
	
	public Map<String, Object> atualizarCidProcedimentoNovo(final List<String> lista) throws ApplicationBusinessException {

		// Argumentos paramentrizados
		String nomeArquivo = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_CID).getVlrTexto().toLowerCase();
		
		// confere parâmetro necesário
		parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PHI_SEQ_IMPORTACAO_CID);
		parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_FATURAMENTO);
		parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
		parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		parametroFacade.buscarAghParametro(AghuParametrosEnum.P_IMPORTACAO_CID_PLANOS_SUS);
		// Abertura de arquivos
		try {
			nomeArquivo = FileUtil.arquivoExiste(lista, nomeArquivo);
		} catch (final ApplicationBusinessException e1) {
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, nomeArquivo);
		}

		final File arquivo = new File(nomeArquivo);
		final AghSistemas sistema = aghuFacade.obterAghSistema(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_FATURAMENTO).getVlrTexto());
		atualizarProcessosAbortados(sistema);
		verificarConcorrencia(arquivo.getName(), sistema);

		final AghArquivoProcessamento arquivoProcessamento;
		try {
			arquivoProcessamento = criarArquivo(arquivo, sistema, 1);
		} catch (final IOException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, nomeArquivo);
		}
		
		//-- Arquivo das modalidades --//
		String nomeArquivoModalidade = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_MODALIDADE).getVlrTexto().toLowerCase();
		// Abertura de arquivos
		try {
			nomeArquivoModalidade = FileUtil.arquivoExiste(lista, nomeArquivoModalidade);
		} catch (final ApplicationBusinessException e1) {
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, nomeArquivoModalidade);
		}

		final File arquivoModalidade = new File(nomeArquivoModalidade);

		//final AghArquivoProcessamento arquivoProcessamentoModalidade;
		try {
			criarArquivo(arquivoModalidade, sistema, 1);
		} catch (final IOException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, nomeArquivoModalidade);
		}
		//-- Fim Arquivo das modalidades --//
		
		File file = null;
		try {
			file = File.createTempFile(IFaturamentoFacade.NOME_ARQUIVO_LOG_CID + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM), IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG);
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}

		getProcessadorArquivosImportacaoSus().processarCidProcedimentoNovo(file, lista);
		final List<AghArquivoProcessamento> arquivos = new ArrayList<AghArquivoProcessamento>(1);
		arquivos.add(arquivoProcessamento);
		final Map<String, Object> retorno = new HashMap<String, Object>();
		retorno.put(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS, arquivos);
		retorno.put(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS, file.getAbsolutePath());
		return retorno;
	}
	
	public Map<String, Object> atualizarCboProcedimento(final List<String> lista) throws BaseException {
		// Argumentos paramentrizados
		String nomeArquivoOcupacao = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_OCUPACAO_CBO).toLowerCase(); // "tb_ocupacao.txt";
		String nomeArquivoProcedimentosCBO = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_CBO).toLowerCase(); // "rl_procedimento_ocupacao.txt";
		
		// confere parametros necessários
		parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PHI_SEQ_IMPORTACAO_PROCEDIMENTO_CBO);
		// Abertura de arquivos
		String arquivoAbrindo = "";
		try {
			arquivoAbrindo = nomeArquivoOcupacao;
			nomeArquivoOcupacao = FileUtil.arquivoExiste(lista, nomeArquivoOcupacao);
			arquivoAbrindo = nomeArquivoProcedimentosCBO;
			nomeArquivoProcedimentosCBO = FileUtil.arquivoExiste(lista, nomeArquivoProcedimentosCBO);
		} catch (final ApplicationBusinessException e1) {
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, arquivoAbrindo);
		}
		final File arquivoOcupacao = new File(nomeArquivoOcupacao);
		final File arquivoProcedimentosCBO = new File(nomeArquivoProcedimentosCBO);
		final AghSistemas sistema = aghuFacade.obterAghSistema(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIGLA_FATURAMENTO).getVlrTexto());
		atualizarProcessosAbortados(sistema);
		verificarConcorrencia(arquivoProcedimentosCBO.getName(), sistema);
		final List<AghArquivoProcessamento> arquivos = new ArrayList<AghArquivoProcessamento>(2);
		try {
			arquivoAbrindo = nomeArquivoOcupacao;
			arquivos.add(criarArquivo(arquivoOcupacao, sistema, 1));
			arquivoAbrindo = nomeArquivoProcedimentosCBO;
			arquivos.add(criarArquivo(arquivoProcedimentosCBO, sistema, 2));
		} catch (final IOException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, arquivoAbrindo);
		}

		File file = null;
		try {
			file = File.createTempFile(IFaturamentoFacade.NOME_ARQUIVO_LOG_CBO + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM), IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG);
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}

		getProcessadorArquivosImportacaoSus().processarCboProcedimento(file, lista);
		final Map<String, Object> retorno = new HashMap<String, Object>();
		retorno.put(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS, arquivos);
		retorno.put(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS, file.getAbsolutePath());
		return retorno;
	}
	
	public Map<String, Object> atualizarCompatibilidade(final List<String> lista) throws BaseException {
		
		// Argumentos paramentrizados
		String nomeArquivoCompatibilidade = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_COMPATIBILIDADE).toLowerCase(); 
		
		// Abertura de arquivos
		String arquivoAbrindo = "";
		try {
			arquivoAbrindo = nomeArquivoCompatibilidade;
			nomeArquivoCompatibilidade = FileUtil.arquivoExiste(lista, nomeArquivoCompatibilidade);
		} catch (final ApplicationBusinessException e1) {
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, arquivoAbrindo);
		}
		
		final File arquivoCompatibilidade = new File(nomeArquivoCompatibilidade);
		final AghSistemas sistema = aghuFacade.obterAghSistema(parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO));
		atualizarProcessosAbortados(sistema);
		verificarConcorrencia(arquivoCompatibilidade.getName(), sistema);
		final List<AghArquivoProcessamento> arquivos = new ArrayList<AghArquivoProcessamento>(2);
		try {
			arquivoAbrindo = nomeArquivoCompatibilidade;
			arquivos.add(criarArquivo(arquivoCompatibilidade, sistema, 1));
		} catch (final IOException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, arquivoAbrindo);
		}

		File file = null;
		try {
			file = File.createTempFile(IFaturamentoFacade.NOME_ARQUIVO_LOG_COMPATIBILIDADE + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM), IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG);
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}

		getProcessadorArquivosImportacaoSus().processarCompatibilidade(file);
		final Map<String, Object> retorno = new HashMap<String, Object>();
		retorno.put(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS, arquivos);
		retorno.put(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS, file.getAbsolutePath());
		return retorno;
	}

	public Map<String, Object> atualizarServicoClassificacao(final List<String> lista) throws BaseException {
	
		// Argumentos paramentrizados
		String nmArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_PROCED_SERVICO).toLowerCase(); 
		String nmArquivoServ = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_SERVICO).toLowerCase();
		String nmArquivoServClass = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_SERVICO_CLASSIFICACAO).toLowerCase(); 

		
		
		// Abertura de arquivos
		String arquivoAbrindo = nmArquivo;
		nmArquivo = FileUtil.arquivoExiste(lista, nmArquivo);
		
		arquivoAbrindo = nmArquivoServ;
		nmArquivoServ = FileUtil.arquivoExiste(lista, nmArquivoServ);
		
		arquivoAbrindo = nmArquivoServClass;
		nmArquivoServClass = FileUtil.arquivoExiste(lista, nmArquivoServClass);
			
		final File arquivo = new File(nmArquivo);
		final File arquivoServ = new File(nmArquivoServ);
		final File arquivoServClass = new File(nmArquivoServClass);
		
		final AghSistemas sistema = aghuFacade.obterAghSistema(parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO));
		
		atualizarProcessosAbortados(sistema);
		
		verificarConcorrencia(arquivo.getName(), sistema);
		
		final List<AghArquivoProcessamento> arquivos = new ArrayList<AghArquivoProcessamento>(3);
		try {
			arquivoAbrindo = nmArquivo;
			arquivos.add(criarArquivo(arquivo, sistema, 1));
			
			arquivoAbrindo = nmArquivoServ;
			arquivos.add(criarArquivo(arquivoServ, sistema, 2));
			
			arquivoAbrindo = nmArquivoServClass;
			arquivos.add(criarArquivo(arquivoServClass, sistema, 3));
		} catch (final IOException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, arquivoAbrindo);
		}

		File file = null;
		try {
			file = File.createTempFile(IFaturamentoFacade.NOME_ARQUIVO_LOG_SERVICO_CLASSIFICACAO + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM), IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG);
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}

		getProcessadorArquivosImportacaoSus().processarServicoClassificacao(file);
		final Map<String, Object> retorno = new HashMap<String, Object>();
		retorno.put(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS, arquivos);
		retorno.put(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS, file.getAbsolutePath());
		
		return retorno;
	}
	
    public Map<String, Object> atualizarInstrumentoRegistro(final List<String> lista) throws BaseException,ApplicationBusinessException {
		
		// Argumentos paramentrizados
		String nomeArqRegistro = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_REGISTRO).toLowerCase(); 
		String nomeArqProcedRegistro = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_PROCEDIMENTO_REGISTRO).toLowerCase();

		
		
		// Abertura de arquivos
		String arquivoAbrindo = nomeArqRegistro;
		nomeArqRegistro = FileUtil.arquivoExiste(lista, nomeArqRegistro);
		
		arquivoAbrindo = nomeArqProcedRegistro;
		nomeArqProcedRegistro = FileUtil.arquivoExiste(lista, nomeArqProcedRegistro);

			
		final File arquivoRegistro = new File(nomeArqRegistro);
		final File arquivoProcedRegistro = new File(nomeArqProcedRegistro);
		
		final AghSistemas sistema = aghuFacade.obterAghSistema(parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO));
		
		atualizarProcessosAbortados(sistema);
		
		verificarConcorrencia(arquivoRegistro.getName(), sistema);

		verificarConcorrencia(arquivoProcedRegistro.getName(), sistema);
		
		final List<AghArquivoProcessamento> arquivos = new ArrayList<AghArquivoProcessamento>(3);
		try {
			arquivoAbrindo = nomeArqRegistro;
			arquivos.add(criarArquivo(arquivoRegistro, sistema, 1));
			
			arquivoAbrindo = nomeArqProcedRegistro;
			arquivos.add(criarArquivo(arquivoProcedRegistro, sistema, 2));
			
		} catch (final IOException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO_COM_NOME, arquivoAbrindo);
		}

		File file = null;
		try {
			file = File.createTempFile(IFaturamentoFacade.NOME_ARQUIVO_LOG_INSTRUMENTO_REGISTRO + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY_MM_DD_HH_MM), IFaturamentoFacade.EXTENCAO_ARQUIVO_LOG);
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}

		getProcessadorArquivosImportacaoSus().processarInstrumentoRegistro(file);
		final Map<String, Object> retorno = new HashMap<String, Object>();
		retorno.put(IFaturamentoFacade.ARQUIVOS_IMPORTACAO_SUS, arquivos);
		retorno.put(IFaturamentoFacade.LOG_FILE_IMPORTACAO_SUS, file.getAbsolutePath());
		
		return retorno;
	}

	protected ProcessadorArquivosImportacaoSus getProcessadorArquivosImportacaoSus() {
		return this.processadorArquivosImportacaoSus;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}