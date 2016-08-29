package br.gov.mec.aghu.faturamento.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import br.gov.mec.aghu.faturamento.dao.FatFormaOrganizacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatGrupoDAO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatFormaOrganizacaoId;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusFormasOrganizacaoON extends BaseBMTBusiness {

	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusFormasOrganizacaoON.class);

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
	private FatFormaOrganizacaoDAO fatFormaOrganizacaoDAO;
	
	@Inject
	private FatGrupoDAO fatGrupoDAO;
	
	private static final long serialVersionUID = 8783741416931685974L;

	private static final Object STRING_1 = "Processando registros do arquivo tb_forma_organizacao.txt";
	private static final Object STRING_2 = "Comparando diferen\u00E7as entre os arquivos. ";
	private static final String STRING_3 = "Grupo da Forma de Organiza\u00E7\u00E3o n\u00E3o encontrada. Linha [{0}]: Linha Forma Organiza\u00E7\u00E3o: [{1}] C\u00F3digo do Grupo:[{2}]";
	private static final String STRING_4 = "Erro ao ler Forma Organiza\u00E7\u00E3o: Tamanho da linha menor q o esperado.";
	private static final String STRING_5 = "Linha: [{0}]:[{1}]";
	private static final String STRING_6 = "Erro ao ler Forma Organiza\u00E7\u00E3o: Linha: [{0}]:[{1}]";
	private static final Object STRING_7 = "Tabela de Forma de Organiza\u00E7\u00E3o lida e atualizada com sucesso em ";
	private static final Object STRING_8 = " segundos.";
	private static final Object STRING_9 = "Erro inesperado ";
	private static final Object STRING_10 = "FORMAS_ORGANIZACAO;A;";
	private static final Object STRING_11 = "Erro ao gravar FAT_FORMAS_ORGANIZACAO: ";


	public void atualizarFormasOrganizacao(final ControleProcessadorArquivosImportacaoSus controle) {
		controle.setPartial(0);
		controle.iniciarLogRetorno();
		
		final Date inicio = new Date();
		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
								.append(STRING_1)
								.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
								.append(STRING_2)
								.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);

		AghArquivoProcessamento aghArquivo = null;
		BufferedReader br = null;
		
		try {
			final String nomeArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_FORMA_ORGANIZACAO).toLowerCase();
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
			int linha = 0;
			final Set<FatFormaOrganizacaoId> lidos = new HashSet<FatFormaOrganizacaoId>();
			
			String strLine;
			while ((strLine = br.readLine()) != null) {
				linha++;
				if (StringUtils.isNotEmpty(strLine)) {
					
					try {
						
						final FatFormaOrganizacao fatFormaOrganizacaoNovo = montarFatFormaOrganizacao(strLine);
						lidos.add(fatFormaOrganizacaoNovo.getId());
						final FatGrupo grupo = fatGrupoDAO.obterGruposPorCodigo(fatFormaOrganizacaoNovo.getId().getSgrGrpSeq());
						
						if (grupo == null) {
							util.tratarExcecaoNaoLancada(
									new IllegalArgumentException(),
									controle.getLogRetorno(),
									ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + STRING_3,
									linha, strLine, fatFormaOrganizacaoNovo.getId().getSgrGrpSeq());
							
						} else {
							fatFormaOrganizacaoNovo.getId().setSgrGrpSeq(grupo.getSeq());
							atualizarFormasOrganizacao(fatFormaOrganizacaoNovo, controle.getLogRetorno());
						}
					} catch (final IndexOutOfBoundsException iae) {
						
						// Tamanho da linha menor q o esperado
						util.tratarExcecaoNaoLancada(iae, controle.getLogRetorno(), ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + STRING_4 + 
													 ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + STRING_5, linha, strLine);
						
					} catch (final NumberFormatException nfe) {
						util.tratarExcecaoNaoLancada(nfe, controle.getLogRetorno(), STRING_6, linha, strLine);
					}
				}
				controle.incrementaNrRegistrosProcessados(4);
				if (controle.getNrRegistrosProcessados() % 50 == 0) {
					util.atualizarArquivo(aghArquivo, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,
											ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				}
			}
			
			// desabilitartar os que não estão no arquivo
			desabilitarFormasOrganizacao(aghArquivo, lidos, controle.getLogRetorno(), inicio, controle);
		
			final Date fim = new Date();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  				.append(STRING_7)
					  				.append((fim.getTime() - inicio.getTime()) / 1000L)
					  				.append(STRING_8).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			
			util.atualizarArquivo(aghArquivo, fim, 100, 100, 0, fim, controle);
			
		}  catch (Exception e) { // vai pegar qualquer exception para logar no banco e no log de tela
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(STRING_9 )
			 	      				.append(DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
			 	      				.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" + stringException + "].")
			 	      				.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			
			controle.gravarLog(STRING_9 + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + stringException);

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

	private void atualizarFormasOrganizacao(final FatFormaOrganizacao fatFormaOrganizacaoNovo,
											final StringBuilder logRetorno) {
		
		super.beginTransaction();

		final FatFormaOrganizacao fatFormaOrganizacao = fatFormaOrganizacaoDAO.obterPorChavePrimaria(fatFormaOrganizacaoNovo.getId());
		
		try {
			
			if (fatFormaOrganizacao == null) {
				fatFormaOrganizacaoNovo.setIndSituacao(DominioSituacao.A);
				
				// inserir
				fatFormaOrganizacaoDAO.persistir(fatFormaOrganizacaoNovo);
				fatFormaOrganizacaoDAO.flush();
				
				logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
						  .append(STRING_10).append(';')
						  .append(fatFormaOrganizacaoNovo.getId().getCodigo()).append(';')
						  .append(fatFormaOrganizacaoNovo.getDescricao()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
				
			} else {
				if (!fatFormaOrganizacaoNovo.getDescricao().equalsIgnoreCase(fatFormaOrganizacao.getDescricao())) {
					// atualizar
					fatFormaOrganizacao.setDescricao(fatFormaOrganizacaoNovo.getDescricao());
					fatFormaOrganizacaoDAO.atualizar(fatFormaOrganizacao);
					fatFormaOrganizacaoDAO.flush();
				}
			}
			
			
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			super.rollbackTransaction();
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  .append(STRING_11)
					  .append(e.getMessage()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  .append("Valores:");
			
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("CODIGO:[").append(fatFormaOrganizacaoNovo.getId().getCodigo())
			.append(']').append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("SGR_GRP_SEQ:[").append(fatFormaOrganizacaoNovo.getId().getSgrGrpSeq())
			.append(']').append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("SGR_SUB_GRUPO:[").append(fatFormaOrganizacaoNovo.getId().getSgrSubGrupo()).append(']');
		}
		super.commitTransaction();
	}

	private void desabilitarFormasOrganizacao( final AghArquivoProcessamento aghArquivo, final Set<FatFormaOrganizacaoId> lidos,
											   final StringBuilder logRetorno, final Date inicio, final ControleProcessadorArquivosImportacaoSus controle) {
		
		// desabilitar os que não estão no arquivo
		final List<FatFormaOrganizacao> desabilitados = fatFormaOrganizacaoDAO.listarTodosFatModalidadeAtendimento();
		
		if (desabilitados != null && !desabilitados.isEmpty()) {
			for (final FatFormaOrganizacao desabilitado : desabilitados) {
				if (!lidos.contains(desabilitado.getId())) {
					super.beginTransaction();
					FatFormaOrganizacao formaOrganizacaoDesabilitar = fatFormaOrganizacaoDAO.obterOriginal(desabilitado);
					try {
						desabilitado.setIndSituacao(DominioSituacao.I);
						fatFormaOrganizacaoDAO.atualizar(formaOrganizacaoDesabilitar);
						fatFormaOrganizacaoDAO.flush();
						
						logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
								  .append("FORMAS_ORGANIZACAO;I;").append(formaOrganizacaoDesabilitar.getId().getCodigo()).append(';')
								  .append(formaOrganizacaoDesabilitar.getDescricao()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
						
						
					} catch (final Exception e) {
						LOG.error(e.getMessage(), e);
						super.rollbackTransaction();
						logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(STRING_11).append(e.getMessage()).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Valores:")
						.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("CODIGO:[").append(desabilitado.getId().getCodigo()).append(']')
						.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("SGR_GRP_SEQ:[").append(desabilitado.getId().getSgrGrpSeq()).append(']')
						.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("SGR_SUB_GRUPO:[").append(desabilitado.getId().getSgrSubGrupo()).append(']');
					}
					super.commitTransaction();
				}
				
				controle.incrementaNrRegistrosProcessados();
				if (controle.getNrRegistrosProcessados() % 100 == 0) {
					util.atualizarArquivo(aghArquivo, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
											ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				}
			}
		}
	}

	private FatFormaOrganizacao montarFatFormaOrganizacao(final String strLine) throws IndexOutOfBoundsException {
		
		final FatFormaOrganizacao fatFormaOrganizacao = new FatFormaOrganizacao();
		
		fatFormaOrganizacao.setId(new FatFormaOrganizacaoId(Short.valueOf(strLine.substring(0, 2)), 
															Byte.valueOf(strLine.substring(2, 4)), 
															Byte.valueOf(strLine.substring(4, 6))
														   )
								 );
		
		fatFormaOrganizacao.setDescricao(StringUtil.removeCaracteresDiferentesAlfabetoEacentos(StringUtil.rightTrim(strLine.substring(6, 106)))
											.replaceAll("\\d", "").toUpperCase()
									    );
		return fatFormaOrganizacao;
	}
	

	protected FatGrupoDAO getFatGrupoDAO() {
		return fatGrupoDAO;
	}	

	protected FatFormaOrganizacaoDAO getFatFormaOrganizacaoDAO() {
		return fatFormaOrganizacaoDAO;
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