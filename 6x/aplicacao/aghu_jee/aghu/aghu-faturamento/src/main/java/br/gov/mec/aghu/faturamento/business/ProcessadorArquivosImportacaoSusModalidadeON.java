package br.gov.mec.aghu.faturamento.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.dao.FatModalidadeAtendimentoDAO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatModalidadeAtendimento;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;


@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusModalidadeON extends BaseBMTBusiness {
	
	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusModalidadeON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private FatModalidadeAtendimentoDAO fatModalidadeAtendimentoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private static final long serialVersionUID = -1286561990732020966L;
	
	public void atualizarModalidade(final ControleProcessadorArquivosImportacaoSus controle) {
		controle.setPartial(0);
		controle.iniciarLogRetorno();
		final Date inicio = new Date();
		
		final StringBuilder logRetorno = new StringBuilder(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
											.append("Processando registros do arquivo tb_modalidade.txt")
											.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
											.append("Comparando diferen\u00E7as entre os arquivos")
											.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
		
		AghArquivoProcessamento aghArquivo = null;
		BufferedReader br = null;
		
		try {
			final String nomeArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_MODALIDADE).toLowerCase();
			final String parametroFaturamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO).toUpperCase();
			
			aghArquivo = aghuFacade.obterArquivoNaoProcessado(parametroFaturamento, nomeArquivo);
			aghArquivo.setDthrInicioProcessamento(new Date(inicio.getTime()));
			util.atualizarArquivo(aghArquivo, inicio, 0, 100, 0, null, controle);
	
			br = util.abrirArquivo(aghArquivo);
			while (br.readLine() != null) {
				controle.incrementaNrRegistrosProcesso(5);
			}
			util.atualizarArquivo(aghArquivo, new Date(), null, 100, 0, null, controle);
			br = util.abrirArquivo(aghArquivo);

			String strLine;
			int linha = 0;
			final Set<Short> lidos = new HashSet<Short>();
			while ((strLine = br.readLine()) != null) {
				linha++;
				if (StringUtils.isNotEmpty(strLine)) {
					try {
						final Short codigo = Short.valueOf(strLine.substring(0, 2));
						lidos.add(codigo);
						String descricao = strLine.substring(2, 102).trim();
						final FatModalidadeAtendimento fatModalidadeAtendimento = getFatModalidadeAtendimentoDAO().buscarFatModalidadeAtendimento(codigo);
						
						if (fatModalidadeAtendimento == null) {
							// inserir
							persistirModalidade(new FatModalidadeAtendimento(codigo, descricao, DominioSituacao.A, null), logRetorno);
							
						} else {
							descricao = StringUtil.removeCaracteresDiferentesAlfabetoEacentos(descricao);
							descricao = descricao.replaceAll("\\d", "");
							
							if (!descricao.equalsIgnoreCase(fatModalidadeAtendimento.getDescricao())) {
								// atualizar
								fatModalidadeAtendimento.setDescricao(descricao);
								
								try {

									super.beginTransaction();
									fatModalidadeAtendimentoDAO.atualizar(fatModalidadeAtendimento);
									fatModalidadeAtendimentoDAO.flush();
									super.commitTransaction();
									
									logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("MODALIDADE_ATENDIMENTOS;A;")
											  .append(fatModalidadeAtendimento.getCodigo()).append(';')
											  .append(fatModalidadeAtendimento.getDescricao())
											  .append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
									
								} catch (final Exception e) {
									LOG.error(e.getMessage(), e);
									super.rollbackTransaction();
									
									logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
											  .append("Erro ao gravar FAT_MODALIDADE_ATENDIMENTOS: ")
											  .append(e.getMessage()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Valores:");
									
									logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("CODIGO:[")
											  .append(fatModalidadeAtendimento.getCodigo()).append(']');
								}
							}
						}
						
					} catch (final IndexOutOfBoundsException iae) {
						// Tamanho da linha menor q o esperado
						util.tratarExcecaoNaoLancada(iae, controle.getLogRetorno(), ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR +
													  "Erro ao ler Modalidade: Tamanho da linha menor que o esperado." + 
													  ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]:[{1}]", linha, strLine);
						
					} catch (final NumberFormatException nfe) {
						util.tratarExcecaoNaoLancada(nfe, controle.getLogRetorno(), "Erro ao ler Modalidade: CO_MODALIDADE inv\u00E1lido." +
													 ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]:[{1}]", linha,
													 strLine.substring(0, 10));
					}
				}
				
				controle.incrementaNrRegistrosProcessados(4);
				if (controle.getNrRegistrosProcessados() % 50 == 0) {
					util.atualizarArquivo(aghArquivo, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,
										  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				}
			}

			// desabilitartar os que não estão no arquivo
			desabilitarModalidadeAtendimento(aghArquivo, lidos, controle.getLogRetorno(), inicio, controle);
			
			final Date fim = new Date();
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  .append("Tabela Modalidade lida e atualizada com sucesso em ").append(((fim.getTime() - inicio.getTime())) / 1000L)
					  .append(" segundos.").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			
			util.atualizarArquivo(aghArquivo, fim, 100, 100,  0, fim, controle);
			
		} catch (Exception e) { // vai pegar qualquer exception para logar no banco e no log de tela
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();
			
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro inesperado ")
				      .append(DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
				      .append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" + stringException + "].")
				      .append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			
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
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	private void persistirModalidade(final FatModalidadeAtendimento fatModalidadeAtendimento, final StringBuilder logRetorno) {
		try {
			super.beginTransaction();
			fatModalidadeAtendimentoDAO.persistir(fatModalidadeAtendimento);
			fatModalidadeAtendimentoDAO.flush();
			super.commitTransaction();
			
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("MODALIDADE_ATENDIMENTOS;A;")
					  .append(fatModalidadeAtendimento.getCodigo()).append(';')
					  .append(fatModalidadeAtendimento.getDescricao()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			super.rollbackTransaction();
			logRetorno.append("Erro ao gravar FAT_MODALIDADE_ATENDIMENTOS: ").append(e.getMessage()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Valores:");
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("CODIGO:[").append(fatModalidadeAtendimento.getCodigo()).append(']');
		}
	}

	private void desabilitarModalidadeAtendimento(final AghArquivoProcessamento aghArquivo, final Set<Short> lidos,
												  final StringBuilder logRetorno, final Date inicio, 
												  final ControleProcessadorArquivosImportacaoSus controle) {
		 
		final FatModalidadeAtendimentoDAO fatModalidadeAtendimentoDAO = getFatModalidadeAtendimentoDAO();
		
		final List<FatModalidadeAtendimento> desabilitados = fatModalidadeAtendimentoDAO.listarFatModalidadeAtendimentoSemExcluidos(new ArrayList<Short>(lidos));
		
		if (desabilitados != null && !desabilitados.isEmpty()) {
			for (final FatModalidadeAtendimento inabilitado : desabilitados) {
				
				super.beginTransaction();
				FatModalidadeAtendimento fatModalidadeAtend = fatModalidadeAtendimentoDAO.obterOriginal(inabilitado);
				fatModalidadeAtend.setIndSituacao(DominioSituacao.I);
				
				try {
					fatModalidadeAtendimentoDAO.atualizar(fatModalidadeAtend);
					fatModalidadeAtendimentoDAO.flush();
					
					logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("MODALIDADE_ATENDIMENTOS;I;")
							  .append(fatModalidadeAtend.getCodigo()).append(';').append(fatModalidadeAtend.getDescricao())
							  .append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
					
				} catch (final Exception e) {
					LOG.error(e.getMessage(), e);
					super.rollbackTransaction();
					logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro ao inabilitar FAT_MODALIDADE_ATENDIMENTOS: ")
							  .append(e.getMessage()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Valores:")					
					.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("CODIGO:[").append(inabilitado.getCodigo()).append(']');
				}
				
				controle.incrementaNrRegistrosProcessados();
				if (controle.getNrRegistrosProcessados() % 100 == 0) {
					util.atualizarArquivo(aghArquivo, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
										  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				}
				super.commitTransaction();
			}
		}
	}

	protected FatModalidadeAtendimentoDAO getFatModalidadeAtendimentoDAO() {
		return fatModalidadeAtendimentoDAO;
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