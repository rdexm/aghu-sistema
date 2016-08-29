package br.gov.mec.aghu.faturamento.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.dao.FatGrupoDAO;
import br.gov.mec.aghu.faturamento.vo.GrupoVO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusGruposON extends BaseBMTBusiness {

	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusGruposON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private FatGrupoDAO fatGrupoDAO;

	private static final long serialVersionUID = 591228041348252491L;

	private static final Object STRING_1 = "Processando registros do arquivo ";
	private static final Object STRING_2 = "Comparando diferenças entre os arquivos";
	private static final Object STRING_3 = "Tabela Grupos lida e atualizada com sucesso em ";
	private static final Object STRING_4 = " segundos.";



	public void atualizarGrupo(final ControleProcessadorArquivosImportacaoSus controle){
		final Date inicio = new Date();
		controle.iniciarLogRetorno();			
		
		AghArquivoProcessamento aghArquivo = null;

		BufferedReader br = null;
		try {
			final String nomeArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_GRUPO).toLowerCase();
			final String parametroFaturamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO).toUpperCase();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(STRING_1).append(nomeArquivo)
			  .append('.').append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(STRING_2);
			
			aghArquivo = aghuFacade.obterArquivoNaoProcessado(parametroFaturamento, nomeArquivo);
			aghArquivo.setDthrInicioProcessamento(new Date(inicio.getTime()));
			util.atualizarArquivo(aghArquivo, inicio, 0, 100, 0, null, controle);
			br = util.abrirArquivo(aghArquivo);
	
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Carregando Grupos...").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);

			final List<GrupoVO> listaGrupos = lerArquivoGrupo(br, controle);

			util.atualizarArquivo(aghArquivo, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			persisteGrupo(inicio, aghArquivo, listaGrupos, controle);

			final List<FatGrupo> gruposAtivos = fatGrupoDAO.listarGruposPorSituacao(DominioSituacao.A);

			inativarGruposNaoSUS(inicio, aghArquivo, listaGrupos, gruposAtivos, controle);
			
			final Date fim = new Date();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
			  .append(STRING_3).append((fim.getTime() - inicio.getTime()) / 1000L)
			  .append(STRING_4).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);

		} catch (Exception e) { // vai pegar qualquer exception para logar no banco e no log de tela
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro inesperado " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO)).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" + stringException + "].").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			controle.gravarLog("Erro inesperado " + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + stringException);
			
		} finally {
			final Date fim = new Date();
			//atualiza os logs
			if (aghArquivo != null) {
				util.atualizarArquivo(aghArquivo, fim, 100, 100, 0, fim, controle);
			}
			
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
					LOG.error(e.getMessage());
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	private void inativarGruposNaoSUS(final Date inicio, AghArquivoProcessamento aghArquivo, 
								      final List<GrupoVO> listaGrupos, final List<FatGrupo> gruposAtivos, 
								      final ControleProcessadorArquivosImportacaoSus controle) {
		boolean achou;
		for (final FatGrupo grupo : gruposAtivos) {
			achou = false;
			for (final GrupoVO grupoVO : listaGrupos) {
				if (grupoVO.getCodigo().equals(grupo.getCodigo())) {
					// nao existir inativa
					achou = true;
					break;
				}
			}
			if (!achou) {
				super.beginTransaction();
				grupo.setIndSituacao(DominioSituacao.I);
				fatGrupoDAO.atualizar(grupo);
				fatGrupoDAO.flush();
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Grupo;I;").append(grupo.getCodigo()).append(';').append(grupo.getDescricao()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
				super.commitTransaction();
			}
			controle.incrementaNrRegistrosProcessados(1);
			if (controle.getNrRegistrosProcessados() % 50 == 0) {
				util.atualizarArquivo(aghArquivo, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			}
		}
	}

	private List<GrupoVO> lerArquivoGrupo(BufferedReader br, final ControleProcessadorArquivosImportacaoSus controle) throws IOException {

		final List<GrupoVO> listaGrupos = new ArrayList<GrupoVO>();
		
		String strLine;
		String grCodigo;
		String grDescricao;
		int cont = 0;
		
		while ((strLine = br.readLine()) != null) { // faz leitura do TXT para listaGrupos
			try {
				LOG.debug(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Importando linha " + ++cont);
				
				grCodigo = strLine.substring(0, 2).trim();
				grDescricao = StringUtil.removeCaracteresDiferentesAlfabetoEacentos(strLine.substring(2, 100).trim()).toUpperCase();

				listaGrupos.add(new GrupoVO(Short.parseShort(grCodigo), grDescricao));

			} catch (final StringIndexOutOfBoundsException e) {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
				  .append("Erro ao ler Grupo: Tamanho da linha menor que o esperado.")
				  .append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
				  .append("Linha: [").append(cont).append("]:[").append(strLine).append(']')
				  .append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
				
			} catch (final NumberFormatException nfe) {
				util.tratarExcecaoNaoLancada(nfe, controle.getLogRetorno(), ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Erro ao ler Grupo." + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]", strLine);
			}
			controle.incrementaNrRegistrosProcesso(4);
			controle.incrementaNrRegistrosProcessados();
		}
		
		return listaGrupos;
	}

	private void persisteGrupo(final Date inicio, AghArquivoProcessamento aghArquivo, 
								final List<GrupoVO> listaGrupos, final ControleProcessadorArquivosImportacaoSus controle) {
		
		for (final GrupoVO grupoVO : listaGrupos) {
			super.beginTransaction();
			final FatGrupo grupo = fatGrupoDAO.obterGruposPorCodigo(grupoVO.getCodigo());
			
			try {
				if (grupo == null) { // se nao achar insere
					final FatGrupo novoGrupo = new FatGrupo(null, grupoVO.getCodigo(), grupoVO.getDescricao(), DominioSituacao.A, null);
					fatGrupoDAO.merge(novoGrupo);
					fatGrupoDAO.persistir(novoGrupo);
					fatGrupoDAO.flush();
					super.commitTransaction();
					
					controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  .append("Grupo;A;").append(novoGrupo.getCodigo()).append(';')
					  .append(novoGrupo.getDescricao())
					  .append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
					
				} else { // senao atualiza
					if (grupo.getDescricao().equals(grupoVO.getDescricao())) {
						grupo.setDescricao(grupoVO.getDescricao());
						fatGrupoDAO.merge(grupo);
						fatGrupoDAO.atualizar(grupo);
						fatGrupoDAO.flush();
					}
					super.commitTransaction();
				}
			} catch (final Exception e) {
				super.rollbackTransaction();
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
				  .append("Erro inesperado ao ler Grupos: ").append(e.getMessage());
				LOG.error(e.getMessage(), e);
			}
			
			controle.incrementaNrRegistrosProcessados(2);
			
			if (controle.getNrRegistrosProcessados() % 50 == 0) {
				util.atualizarArquivo(aghArquivo, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,
									   ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			}
		}
	}

	protected FatGrupoDAO getFatGrupoDAO() {
		return fatGrupoDAO;
	}
	
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}	
}
