package br.gov.mec.aghu.exames.contratualizacao.business;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.contratualizacao.util.Detalhes;
import br.gov.mec.aghu.exames.contratualizacao.util.SolicitacaoExame;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.exames.dao.AelArquivoIntegracaoDAO;
import br.gov.mec.aghu.exames.dao.AelArquivoSolicitacaoDAO;
import br.gov.mec.aghu.model.AelArquivoIntegracao;
import br.gov.mec.aghu.model.AelArquivoSolicitacao;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author ebicca
 *
 */
@Stateless
public class InformacoesProcessamentoArquivosON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(InformacoesProcessamentoArquivosON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelArquivoSolicitacaoDAO aelArquivoSolicitacaoDAO;
	
	@Inject
	private AelArquivoIntegracaoDAO aelArquivoIntegracaoDAO;

	private static final long serialVersionUID = 5581545004354993271L;

	protected enum InformacoesProcessamentoArquivosONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DETALHES_NULO, MENSAGEM_LISTA_SOLICITACOES_NULA, MENSAGEM_SERVIDOR_LOGADO_NULO, MENSAGEM_ERRO_NOME_ARQUIVO_ENTRADA, MENSAGEM_ERRO_NOME_ARQUIVO_SAIDA, MENSAGEM_SOLICITACAO_EXAME_NULA, MENSAGEM_MEDICO_NULO, MENSAGEM_PACIENTE_NULO, MENSAGEM_ARQUIVO_SOLICITACAO_NULO, MENSAGEM_ERRO_GRAVAR_SOLICITACAO, MENSAGEM_ERRO_GRAVAR_ARQUIVO
	}


	protected static final String INICIO_ARQUIVO_ENTRADA = "ExamesPrefeitura";
	protected static final String INICIO_ARQUIVO_SAIDA = "RetornoPrefeitura";

	/**
	 * Estória 16112 - Complementar o processamento com as informações de controle
	 * Fase 2 - Incluir registro do arquivo que esta sendo processado (aelArquivoIntegracao)
	 * @throws ApplicationBusinessException 
	 */
	public AelArquivoIntegracao gravarRegistrosControleArquivo(
			Detalhes detalhes, String nomeArquivoEntradaCaminhoAbsoluto,
			String nomeArquivoEntrada, String nomeArquivoRetorno,
			int totalGerados, int totalErros) throws ApplicationBusinessException {
		logInfo("InformacoesProcessamentoArquivosON.gravarRegistrosControleArquivo(): Entrando.");
		logDebug("InformacoesProcessamentoArquivosON.gravarRegistrosControleArquivo(): Detalhes = [" + detalhes + "].");
		logDebug("InformacoesProcessamentoArquivosON.gravarRegistrosControleArquivo(): nomeArquivoEntradaCaminhoAbsoluto = [" + nomeArquivoEntradaCaminhoAbsoluto + "].");
		logDebug("InformacoesProcessamentoArquivosON.gravarRegistrosControleArquivo(): nomeArquivoEntrada = [" + nomeArquivoEntrada + "].");
		logDebug("InformacoesProcessamentoArquivosON.gravarRegistrosControleArquivo(): nomeArquivoRetorno = [" + nomeArquivoRetorno + "].");
		logDebug("InformacoesProcessamentoArquivosON.gravarRegistrosControleArquivo(): totalGerados = [" + totalGerados + "].");
		logDebug("InformacoesProcessamentoArquivosON.gravarRegistrosControleArquivo(): totalErros = [" + totalErros + "].");
		validarDetalhes(detalhes);
		validarNomesArquivos(nomeArquivoEntradaCaminhoAbsoluto, nomeArquivoEntrada, nomeArquivoRetorno);
		validarServidorLogado();
		AelArquivoIntegracao aelArquivoIntegracao = montarDadosArquivoIntegracao(detalhes, nomeArquivoEntradaCaminhoAbsoluto, nomeArquivoEntrada, nomeArquivoRetorno, totalGerados, totalErros);
		try {
			getAelArquivoIntegracaoDAO().persistir(aelArquivoIntegracao);
			getAelArquivoIntegracaoDAO().flush();
			logInfo("InformacoesProcessamentoArquivosON.gravarRegistrosControleArquivo(): Arquivo de log salvo com sucesso. Saindo.");
		} catch (Exception e) {
			logError("InformacoesProcessamentoArquivosON.gravarRegistrosControleArquivo(): Erro ao gravar arquivo. ", e);
			throw new ApplicationBusinessException(InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_GRAVAR_ARQUIVO);
		}
		return aelArquivoIntegracao;
	}
	
	/**
	 * Estória 16112 - Complementar o processamento com as informações de controle
	 * Fase 2 - Gravar cada solicitação processada do arquivo (aelSolicitacaoArquivo) 
	 */
	public void gravarRegistrosControleSolicitacao(List<AelArquivoSolicitacao> listaArquivosSolicitacao, AelArquivoIntegracao aelArquivoIntegracao) throws ApplicationBusinessException {
		logInfo("InformacoesProcessamentoArquivosON.gravarRegistrosControleSolicitacao(): Entrando.");
		if (listaArquivosSolicitacao != null && ! listaArquivosSolicitacao.isEmpty() && aelArquivoIntegracao != null) {
			try {
				for (AelArquivoSolicitacao aelArquivoSolicitacao : listaArquivosSolicitacao) {
					aelArquivoSolicitacao.setAelArquivoIntegracao(aelArquivoIntegracao);
					getAelArquivoSolicitacaoDAO().persistir(aelArquivoSolicitacao);
				}
				getAelArquivoSolicitacaoDAO().flush();
				logDebug("InformacoesProcessamentoArquivosON.gravarRegistrosControleSolicitacao(): Lista de solicitações salva com sucesso.");
			} catch (Exception e) {
				logError("InformacoesProcessamentoArquivosON.gravarRegistrosControleSolicitacao(): Erro ao gravar solicitações.", e);
				throw new ApplicationBusinessException(InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_GRAVAR_SOLICITACAO);
			}
		} else {
			logInfo("InformacoesProcessamentoArquivosON.gravarRegistrosControleSolicitacao(): Lista de solicitações vazia ou arquivo de log vazio. Não há o que gravar.");
		}
		logInfo("InformacoesProcessamentoArquivosON.gravarRegistrosControleSolicitacao(): Saindo.");
	}
	
	public AelArquivoSolicitacao montarDadosPadraoArquivoSolicitacao(SolicitacaoExame solicitacaoExame) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		logDebug("InformacoesProcessamentoArquivosON.montarDadosPadraoArquivoSolicitacao(): Entrando.");
		logDebug("InformacoesProcessamentoArquivosON.montarDadosPadraoArquivoSolicitacao(): solicitacaoExame = [" + solicitacaoExame + "].");
		validarSolicitacaoExame(solicitacaoExame);
		validarServidorLogado();
		AelArquivoSolicitacao aelArquivoSolicitacao = new AelArquivoSolicitacao();
		aelArquivoSolicitacao.setDataHoraColeta(transformarDataHoraMinuto(solicitacaoExame.getDataHoraColeta()));
		boolean semAgenda = StringUtils.equalsIgnoreCase(DominioSimNao.S.name(), solicitacaoExame.getSolicitacaoSemAgenda());
		aelArquivoSolicitacao.setSemAgenda(DominioSimNao.getInstance(semAgenda));
		String crm = StringUtils.trimToNull(solicitacaoExame.getMedicoSolicitante().getCrm());
		if (crm != null && StringUtils.isNumeric(crm)) {
			aelArquivoSolicitacao.setCrmMedico(Integer.valueOf(crm));
		}
		aelArquivoSolicitacao.setNomeMedico(StringUtils.substring(solicitacaoExame.getMedicoSolicitante().getNome(), 0, 60));
		aelArquivoSolicitacao.setNomePaciente(StringUtils.substring(solicitacaoExame.getPaciente().getNome(), 0, 50));
		aelArquivoSolicitacao.setNomeMaePaciente(StringUtils.substring(solicitacaoExame.getPaciente().getNomeMae(), 0, 50));
		aelArquivoSolicitacao.setDataNascimentoPaciente(transformarDataDiaMesAno(solicitacaoExame.getPaciente().getDataNascimento()));
		if (StringUtils.equalsIgnoreCase(DominioSexo.F.name(), solicitacaoExame.getPaciente().getSexo())) {
			aelArquivoSolicitacao.setSexoPaciente(DominioSexo.F);	
		} else if (StringUtils.equalsIgnoreCase(DominioSexo.M.name(), solicitacaoExame.getPaciente().getSexo())) {
			aelArquivoSolicitacao.setSexoPaciente(DominioSexo.M);	
		}
		String numCartaoSaude = StringUtils.trimToNull(solicitacaoExame.getPaciente().getNumeroCartaoSaude());
		if (numCartaoSaude != null && StringUtils.isNumeric(numCartaoSaude)) {
			aelArquivoSolicitacao.setNumeroCartaoSaude(Long.valueOf(numCartaoSaude));
		}
		aelArquivoSolicitacao.setServidor(servidorLogado);
		aelArquivoSolicitacao.setCriadoEm(new Date());
		logDebug("InformacoesProcessamentoArquivosON.montarDadosPadraoArquivoSolicitacao(): Saindo, aelArquivoSolicitacao = ["+ aelArquivoSolicitacao+ "].");
		return aelArquivoSolicitacao;
	}
	
	/**
	 * Complementa informações de resultado do processamento de cada solicitação
	 * solicicaoExames -> solicitação gerada OU Motivo -> todas as mensagens de
	 * erros identificadas 
	 * As mensagens da solicitação como um todo
	 * (paciente/médico/solicitação/etc). 
	 * Ou concatenar os erros de cada item
	 * com “siglaExame - identificadorExterno – mensagem de erro do item”
	 * 
	 * @param aelArquivoSolicitacao - objeto que será gravado no banco com informações genéricas já preenchidas
	 * @param listaItensRetornoVO - lista de itens - usada em caso de sucesso na solicitação ou erro em um item
	 * @param mensagemErroGenericoSolicitacao - mensagem em caso de erro genérico
	 * @throws ApplicationBusinessException
	 */
	public void montarDadosComplementaresArquivoSolicitacao(AelArquivoSolicitacao aelArquivoSolicitacao,
			List<ItemContratualizacaoVO> listaItensRetornoVO, String mensagemErroGenericoSolicitacao) throws ApplicationBusinessException {
		logInfo("InformacoesProcessamentoArquivosON.montarDadosComplementaresArquivoSolicitacao(): Entrando.");
		logDebug("InformacoesProcessamentoArquivosON.montarDadosComplementaresArquivoSolicitacao(): aelArquivoSolicitacao = ["+ aelArquivoSolicitacao+ "].");
		logDebug("InformacoesProcessamentoArquivosON.montarDadosComplementaresArquivoSolicitacao(): listaItensRetornoVO = ["+ listaItensRetornoVO+ "].");
		logDebug("InformacoesProcessamentoArquivosON.montarDadosComplementaresArquivoSolicitacao(): mensagemErroGenericoSolicitacao = ["+ mensagemErroGenericoSolicitacao+ "].");
		if (aelArquivoSolicitacao == null) {
			throw new ApplicationBusinessException(InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ARQUIVO_SOLICITACAO_NULO);
		}
		mensagemErroGenericoSolicitacao = StringUtils.trimToNull(mensagemErroGenericoSolicitacao);
		if (StringUtils.isNotEmpty(mensagemErroGenericoSolicitacao)) {
			aelArquivoSolicitacao.setMotivo(mensagemErroGenericoSolicitacao);
		} else if (listaItensRetornoVO != null && ! listaItensRetornoVO.isEmpty()) {
			String motivoErro = formatarMensagemErroItensSolicitacao(listaItensRetornoVO);
			if (StringUtils.isNotEmpty(motivoErro)) {
				aelArquivoSolicitacao.setMotivo(motivoErro);
			} else {
				// Não deu erro, guarda resultado do processamento da solicitação
				AelSolicitacaoExames solicitacao = listaItensRetornoVO.get(0).getItemSolicitacaoExames().getSolicitacaoExame();
				aelArquivoSolicitacao.setAelSolicitacaoExames(solicitacao);
			}
		}
		logDebug("InformacoesProcessamentoArquivosON.montarDadosComplementaresArquivoSolicitacao(): aelArquivoSolicitacao retorno = ["+ aelArquivoSolicitacao+ "].");
		logInfo("InformacoesProcessamentoArquivosON.montarDadosComplementaresArquivoSolicitacao(): Saindo.");
	}
	
	/**
	 * Concatena os erros de cada item com “siglaExame - identificadorExterno –
	 * mensagem de erro do item” todos separados com ponto e virgula. Não deve
	 * apresentar as mensagens genéricas para itens que processaram OK como foi
	 * feito no arquivo de retorno.
	 * 
	 * @param listaItensRetornoVO
	 * @return
	 */
	private String formatarMensagemErroItensSolicitacao(List<ItemContratualizacaoVO> listaItensRetornoVO) {
		StringBuilder builder = new StringBuilder();
		for (ItemContratualizacaoVO itemContratualizacaoVO : listaItensRetornoVO) {
			if (StringUtils.isNotEmpty(itemContratualizacaoVO.getMensagemErro())) {
				builder.append(itemContratualizacaoVO.getExame());
				builder.append(" - ");
				builder.append(itemContratualizacaoVO.getIdExterno());
				builder.append(" - ");
				builder.append(itemContratualizacaoVO.getMensagemErro());
				builder.append("; ");
			}
		}
		logDebug("InformacoesProcessamentoArquivosON.formatarMensagemErroItensSolicitacao(): retorno = [" + builder.toString() + "].");
		return builder.toString();
	}
	
	private void validarSolicitacaoExame(SolicitacaoExame solicitacaoExame) throws ApplicationBusinessException {
		if (solicitacaoExame == null) {
			throw new ApplicationBusinessException(InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_SOLICITACAO_EXAME_NULA);
		}
		if (solicitacaoExame.getMedicoSolicitante() == null) {
			throw new ApplicationBusinessException(InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_MEDICO_NULO);
		}
		if (solicitacaoExame.getPaciente() == null) {
			throw new ApplicationBusinessException(InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_PACIENTE_NULO);
		}
	}

	protected void validarServidorLogado() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null || servidorLogado.getId() == null) {
			throw new ApplicationBusinessException(InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_SERVIDOR_LOGADO_NULO);
		}
	}

	protected void validarNomesArquivos(String nomeArquivoEntradaCaminhoAbsoluto,
			String nomeArquivoEntrada, String nomeArquivoRetorno) throws ApplicationBusinessException{
		if (StringUtils.isEmpty(nomeArquivoEntradaCaminhoAbsoluto) || StringUtils.isEmpty(nomeArquivoEntrada)) {
			throw new ApplicationBusinessException(InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_NOME_ARQUIVO_ENTRADA);
		}
		if (StringUtils.isEmpty(nomeArquivoRetorno)) {
			throw new ApplicationBusinessException(InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_ERRO_NOME_ARQUIVO_SAIDA);
		}
	}

	protected void validarDetalhes(Detalhes detalhes) throws ApplicationBusinessException {
		if (detalhes == null || detalhes.getHeader() == null || detalhes.getSolicitacoes() == null) {
			throw new ApplicationBusinessException(InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_DETALHES_NULO);
		}
		if (detalhes.getSolicitacoes().getSolicitacaoExame() == null || detalhes.getSolicitacoes().getSolicitacaoExame().size() == 0) {
			throw new ApplicationBusinessException(InformacoesProcessamentoArquivosONExceptionCode.MENSAGEM_LISTA_SOLICITACOES_NULA);
		}
	}
	
	private AelArquivoIntegracao montarDadosArquivoIntegracao(Detalhes detalhes, String nomeArquivoEntradaCaminhoAbsoluto,
			String nomeArquivoEntrada, String nomeArquivoRetorno, int totalGerados, int totalErros) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelArquivoIntegracao aelArquivoIntegracao = new AelArquivoIntegracao();
		aelArquivoIntegracao.setNomeArquivoEntrada(nomeArquivoEntrada);
		aelArquivoIntegracao.setDiretorioEntrada(StringUtils.substringBefore(nomeArquivoEntradaCaminhoAbsoluto, INICIO_ARQUIVO_ENTRADA));
		aelArquivoIntegracao.setQuantidadeSolicitacoes(Integer.valueOf(detalhes.getHeader().getQuantidadeSolicitacoes()));
		aelArquivoIntegracao.setDataGeracao(transformarDataHoraMinuto(detalhes.getHeader().getDataHoraGeracao()));
		aelArquivoIntegracao.setConvenio(detalhes.getHeader().getConvenio());
		aelArquivoIntegracao.setPlano(Integer.valueOf(detalhes.getHeader().getPlanoConvenio()));
		Date dataCorrente = new Date();
		aelArquivoIntegracao.setDataProcessamento(dataCorrente);
		aelArquivoIntegracao.setCriadoEm(dataCorrente);
		aelArquivoIntegracao.setServidor(servidorLogado);
		aelArquivoIntegracao.setNomeArquivoSaida(INICIO_ARQUIVO_SAIDA + StringUtils.substringAfter(nomeArquivoRetorno, INICIO_ARQUIVO_SAIDA));
		aelArquivoIntegracao.setTotalRecebida(detalhes.getSolicitacoes().getSolicitacaoExame().size());
		aelArquivoIntegracao.setTotalSemAgenda(contarSolicitacoesSemAgenda(detalhes.getSolicitacoes().getSolicitacaoExame()));
		aelArquivoIntegracao.setTotalGerada(Integer.valueOf(totalGerados));
		aelArquivoIntegracao.setTotalRecusada(Integer.valueOf(totalErros));
		logDebug("InformacoesProcessamentoArquivosON.montarDadosArquivoIntegracao(): Arquivo a ser gravado = [" + aelArquivoIntegracao + "]");
		return aelArquivoIntegracao;
	}
	
	/*
	 * total semAgenda-> contagem dos pacientes contidos no arquivo com valor S para a tag <solicitacaoSemAgenda>
	 */
	private Integer contarSolicitacoesSemAgenda(List<SolicitacaoExame> listaSolicitacoes) {
		int total = 0;
		for (SolicitacaoExame solicitacaoExame : listaSolicitacoes) {
			if (StringUtils.equalsIgnoreCase(solicitacaoExame.getSolicitacaoSemAgenda(), "S")) {
				total++;
			}
		}
		return Integer.valueOf(total);
	}
	
	private Date transformarDataHoraMinuto(String dataString) {
		DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
		Date retorno = null;
		try {
			retorno = dateFormat.parse(dataString);
		} catch (ParseException e) {
			logError("Impossível fazer parse de [" + dataString + "]", e);
		}
		return retorno;
	}
	
	private Date transformarDataDiaMesAno(String dataString) {
		DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		Date retorno = null;
		try {
			retorno = dateFormat.parse(dataString);
		} catch (ParseException e) {
			logError("Impossível fazer parse de [" + dataString + "]", e);
		}
		return retorno;
	}
	
	private AelArquivoIntegracaoDAO getAelArquivoIntegracaoDAO() {
		return aelArquivoIntegracaoDAO;
	}
	
	private AelArquivoSolicitacaoDAO getAelArquivoSolicitacaoDAO() {
		return aelArquivoSolicitacaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
