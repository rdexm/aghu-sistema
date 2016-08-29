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
import br.gov.mec.aghu.faturamento.dao.FatSubGrupoDAO;
import br.gov.mec.aghu.faturamento.vo.SubGrupoVO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.model.FatSubGruposId;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusSubGruposON extends BaseBMTBusiness {

	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusSubGruposON.class);
	
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
	private FatSubGrupoDAO fatSubGrupoDAO;

	private static final long serialVersionUID = 991270627400793411L;
	
	public void atualizarSubGrupo(final ControleProcessadorArquivosImportacaoSus controle) {
		final Date inicio = new Date();
		controle.iniciarLogRetorno();
		
		AghArquivoProcessamento aghArquivo = null;
		BufferedReader br = null;
		try {
			final String nomeArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_SUB_GRUPO).toLowerCase();
			final String parametroFaturamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO).toUpperCase();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Processando registros do arquivo ").append(nomeArquivo)
			  .append('.').append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Comparando diferenças entre os arquivos.");
			
			aghArquivo = aghuFacade.obterArquivoNaoProcessado(parametroFaturamento, nomeArquivo);
			aghArquivo.setDthrInicioProcessamento(new Date(inicio.getTime()));
			util.atualizarArquivo(aghArquivo, inicio, 0, 100, 0, null, controle);
	
			br = util.abrirArquivo(aghArquivo);
	
			controle.getLogRetorno().append("Carregando SubGrupos...").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);

			final List<SubGrupoVO> listaSubGrupos = lerArquivoSubGrupo(br, controle);
			
			util.atualizarArquivo(aghArquivo, inicio, inicio, ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 0, controle);

			if(!listaSubGrupos.isEmpty()){
				persistirSubGrupos(inicio, aghArquivo, listaSubGrupos, controle);
			}
			
			final List<FatSubGrupo> subGruposAtivos = fatSubGrupoDAO.listarSubGruposPorSituacao(DominioSituacao.A);

			boolean achou;
			for (final FatSubGrupo subGrupo : subGruposAtivos) {
				achou = false;
				for (final SubGrupoVO subGrupoVO : listaSubGrupos) {
					if (subGrupoVO.getGrupo().equals(subGrupo.getId().getGrpSeq()) && subGrupoVO.getSubGrupo().equals(subGrupo.getId().getSubGrupo())) {
						achou = true;
						break;
					}
				}
				if (!achou) {
					try {
						super.beginTransaction();
						subGrupo.setIndSituacao(DominioSituacao.I);
						fatSubGrupoDAO.atualizar(subGrupo);
						fatSubGrupoDAO.flush();
						super.commitTransaction();
						controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("SubGrupo;I;").append(subGrupo.getId().getSubGrupo()).append(';').append(subGrupo.getDescricao()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
					} catch (final Exception e) {
						LOG.error(e.getMessage(), e);
						super.rollbackTransaction();
						controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro inesperado ao atualizar situação do SubGrupo: ").append(subGrupo.getId().getSubGrupo()).append(';')
								.append(subGrupo.getDescricao()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(e.getMessage());
					}
				}
				controle.incrementaNrRegistrosProcessados();
				if (controle.getNrRegistrosProcessados() % 50 == 0) {
					util.atualizarArquivo(aghArquivo, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 0, controle);
				}
			}
			
			final Date fim = new Date();
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Tabela SubGrupos lida e atualizada com sucesso em ")
			  .append((fim.getTime() - inicio.getTime()) / 1000L).append(" segundos.").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
		
			util.atualizarArquivo(aghArquivo, fim, 100, 100, 0, fim, controle);
			
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

	private void persistirSubGrupos( final Date inicio, AghArquivoProcessamento aghArquivo,
									 final List<SubGrupoVO> listaSubGrupos, final ControleProcessadorArquivosImportacaoSus controle) {
		
		String descricao = listaSubGrupos.get(0).getDescricao();
		
		for (final SubGrupoVO subGrupoVO : listaSubGrupos) {
			
			super.beginTransaction();
			
			final FatSubGrupo subGrupo = fatSubGrupoDAO.obterSubGruposPorGrupoSubGrupo(subGrupoVO.getGrupo(), subGrupoVO.getSubGrupo());
			FatSubGrupo novoSubGrupo = null;
			try {
				if (subGrupo == null) { // se nao achar insere
					novoSubGrupo = new FatSubGrupo();
					final FatSubGruposId id = new FatSubGruposId(subGrupoVO.getGrupo(), subGrupoVO.getSubGrupo());
					novoSubGrupo.setId(id);
					novoSubGrupo.setDescricao(descricao);
					novoSubGrupo.setIndSituacao(DominioSituacao.A);
					
					fatSubGrupoDAO.persistir(novoSubGrupo);
					fatSubGrupoDAO.flush();
					super.commitTransaction();
					
					controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("SubGrupo;A;")
					  .append(novoSubGrupo.getId().getSubGrupo()).append(';')
					  .append(novoSubGrupo.getDescricao()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
					
				} else { // senao atualiza
					if (!subGrupo.getDescricao().equals(subGrupoVO.getDescricao())) {
						subGrupo.setDescricao(subGrupoVO.getDescricao());
						fatSubGrupoDAO.atualizar(subGrupo);
						fatSubGrupoDAO.flush();
					}
					super.commitTransaction();
				}
			} catch (final Exception e) {
				LOG.error(e.getMessage(), e);
				super.rollbackTransaction();
				
				if (novoSubGrupo == null) {
					controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
						.append("Erro inesperado ao gravar SubGrupo: ").append(e.getMessage());
					
				} else {
					controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
						.append("Erro inesperado ao gravar SubGrupo: ").append(novoSubGrupo.getId().getSubGrupo()).append(';')
						.append(novoSubGrupo.getDescricao()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(e.getMessage());
				}
			}
			
			controle.incrementaNrRegistrosProcessados(2);
			if (controle.getNrRegistrosProcessados() % 50 == 0) {
				util.atualizarArquivo(aghArquivo, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 0, controle);
			}
		}
	}



	private List<SubGrupoVO> lerArquivoSubGrupo(BufferedReader br, final ControleProcessadorArquivosImportacaoSus controle) throws IOException {
		
		final List<SubGrupoVO> listaSubGrupos = new ArrayList<SubGrupoVO>();
		
		String sgrGrupo, sgrSubGrupo, descricao = "", strLine = "";
		Integer cont = 0;
		// faz leitura do TXT para listaGrupos
		while ((strLine = br.readLine()) != null) {
			try {
				sgrGrupo = strLine.substring(0, 2).trim();
				sgrSubGrupo = strLine.substring(2, 4).trim();
				descricao = StringUtil.removeCaracteresDiferentesAlfabetoEacentos(strLine.substring(4, 100).trim()).toUpperCase();

				listaSubGrupos.add(new SubGrupoVO(Short.parseShort(sgrGrupo), Byte.parseByte(sgrSubGrupo), descricao));
			} catch (final StringIndexOutOfBoundsException e) {
				final String error = ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Erro ao ler SubGrupo: Tamanho da linha menor que o esperado." + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [" + cont + "]:[" + strLine + "]" + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR;
				LOG.error(error, e);
				controle.getLogRetorno().append(error);
			} catch (final NumberFormatException nfe) {
				util.tratarExcecaoNaoLancada(nfe, controle.getLogRetorno(), ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Erro ao ler Sub-grupo." + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]", strLine);
			}
			controle.incrementaNrRegistrosProcesso(4);
			controle.incrementaNrRegistrosProcessados();
		}
		
		return listaSubGrupos;
	}

	

	protected FatSubGrupoDAO getFatSubGrupoDAO() {
		return fatSubGrupoDAO;
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