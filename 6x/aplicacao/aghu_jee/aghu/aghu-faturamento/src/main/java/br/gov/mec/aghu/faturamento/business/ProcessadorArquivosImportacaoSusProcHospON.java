package br.gov.mec.aghu.faturamento.business;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
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
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCobrancaDiaria;
import br.gov.mec.aghu.dominio.DominioFideps;
import br.gov.mec.aghu.dominio.DominioModoLancamentoFat;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioUtilizacaoItem;
import br.gov.mec.aghu.faturamento.dao.FatCaractComplexidadeDAO;
import br.gov.mec.aghu.faturamento.dao.FatCaractFinanciamentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatFormaOrganizacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatSubGrupoDAO;
import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.model.FatCaractComplexidade;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatFormaOrganizacaoId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.FatVlrItemProcedHospCompsId;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusProcHospON extends BaseBMTBusiness {

	
	private static final String ERRO_INESPERADO = "Erro inesperado ";

	@EJB
	private ItemProcedimentoHospitalarON itemProcedimentoHospitalarON;
	
	@EJB
	private ValorItemProcedHospCompsON valorItemProcedHospCompsON;
	
	@EJB
	private ValorItemProcedHospCompsRN valorItemProcedHospCompsRN;
	
	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusProcHospON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private FatCompetenciaDAO fatCompetenciaDAO;
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@Inject
	private FatVlrItemProcedHospCompsDAO fatVlrItemProcedHospCompsDAO;
	
	@Inject
	private FatSubGrupoDAO fatSubGrupoDAO;
	
	@Inject
	private FatFormaOrganizacaoDAO fatFormaOrganizacaoDAO;
	
	@Inject
	private FatCaractFinanciamentoDAO fatCaractFinanciamentoDAO;
	
	@Inject
	private FatCaractComplexidadeDAO fatCaractComplexidadeDAO;
	
	private static final long serialVersionUID = 7621453105275404477L;

	private static final Object STRING_1 = "Comparando diferenças entre os arquivos. ";
	private static final Object STRING_0 = "Processando registros do arquivo tb_procedimento.txt";
	private static final Object ATUALIZANDO_VALORES = "Atualizando valores.";



	
	class ArquivoVO{
		String subGrupoId;
		String grupoId;
		String formaOrgId;
		String descricao;
		String sexoStr;
		String idadeMinStr;
		String idadeMaxStr;
		String valorShStr;
		String valorProcedStr;
		String valorSpStr;
		DominioSexoDeterminante sexo;
		BigDecimal valorSh;
		BigDecimal valorProced;
		BigDecimal valorSp;
		Integer idadeMin;
		Integer idadeMax;
		Short vQtMaximaExecucao;
		Long codTabela;
		FatFormaOrganizacao fatFormaOrganizacao;
		Integer seqSusFinanciamento;
		Integer seqSusComplexidade;
	}
	
	public void atualizarItensProcedimentoHospitalar(final ControleProcessadorArquivosImportacaoSus controle, final List<String> listaNomeArquivos) {
		final Date inicio = new Date();
		controle.iniciarLogRetorno();
		controle.setPartial(0);
		
		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(STRING_0);
		
		AghArquivoProcessamento aghArquivoItensProcedimentoHospitalar = null;
		
		final List<ArquivoVO> listaItens = new ArrayList<ArquivoVO>();
		final List<Long> listaCodTabela = new ArrayList<Long>(0);
		
		try {
			final String nomeArquivoItensProcedimentos = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_ITENS_PROCED_HOSP).toLowerCase(); // "tb_procedimento.txt";
			final String parametroFaturamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO).toUpperCase();
			final Short phoSeq = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			final Integer caracteristicaFinanciamento = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_IMPORTACAO_PROCED_FCF);
			final Integer caracteristicaComplexidade = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_IMPORTACAO_PROCED_FCC);
			
			// Abertura de arquivos
			final AghSistemas sistema = aghuFacade.obterAghSistema(parametroFaturamento);
			aghArquivoItensProcedimentoHospitalar = aghuFacade.obterArquivoNaoProcessado(sistema, nomeArquivoItensProcedimentos);
			aghArquivoItensProcedimentoHospitalar.setDthrInicioProcessamento(new Date(inicio.getTime()));
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(STRING_1);
			util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, inicio, 0, 30, 0, null, controle);
			
			// realiza atualização dos itens de procedimento hospitalar
	
			final Object[] datas = getFatCompetenciaDAO().listarDataInicioEFimCompModAMB();
			final Date dtIncio = (Date) datas[0];
			final Date dtFim = (Date) datas[1];

			final String[] nrsExcecoes = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PROCEDIMENTOS_IGNORADOS_IMPORTACAO_VALOR).getVlrTexto().split(",");
			final List<String> arq = util.processaRegistrosProcedimento(aghArquivoItensProcedimentoHospitalar, listaNomeArquivos, inicio, controle);
			
			for (final String strLine : arq) {
				controle.incrementaNrRegistrosProcessados();

				final ArquivoVO vo = lerLinhaArquivo(strLine);

				listaItens.add(vo);
				listaCodTabela.add(vo.codTabela);
				
				ativarItensProcedHospitalar(vo);
				
				if (!fatItensProcedHospitalarDAO.isProcedimentoExistentePorCodTabela(vo.codTabela)) {
					try {
						beginTransaction();
						final FatItensProcedHospitalar itemProcedHospitalar = criarItemProcedHospitalar(phoSeq, vo.codTabela, vo.descricao, vo.idadeMin, vo.idadeMax, 
																										vo.sexo, vo.fatFormaOrganizacao);

						this.getItemProcedimentoHospitalarON().persistirItemProcedimentoHospitalar(itemProcedHospitalar, null);
						fatItensProcedHospitalarDAO.flush();
						super.commitTransaction();

						controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("ITENS PROCEDIMENTO;A;" + vo.codTabela + ";" + vo.descricao);
						
						persistirFatVlrItemProcedHospComps(controle.getLogRetorno(), phoSeq, dtIncio, vo, itemProcedHospitalar);
						
					} catch (final Exception e) {
						super.rollbackTransaction();
						
						controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
								  				.append("Não foi possível inserir - FAT_ITENS_PROCED_HOSPITALAR: Grupo: ").append( vo.grupoId )
								  				.append( " SubGrupo: " ).append( vo.subGrupoId)
								  				.append( " Forma Organizacao: " ).append( vo.formaOrgId )
								  				.append( " Código Tabela: " ).append( vo.codTabela )
								  				.append( " Descricao: " ).append( vo.descricao)
								  				.append( " Idade mínima: " ).append( vo.idadeMinStr )
								  				.append( " Idade máxima: " ).append( vo.idadeMaxStr )
								  				.append( " Sexo:" ).append( vo.sexoStr);
					}
				}
				if (controle.getNrRegistrosProcessados() % 20 == 0) {
					util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 30, ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				}
			}
			controle.setPartial(30);
			controle.incrementaNrRegistrosProcessados();
			
			controle.setNrRegistrosProcesso(fatItensProcedHospitalarDAO.obterListaAtivosPorPhoSeqCount(phoSeq) + 
										   this.getFatSubGrupoDAO().listarSubGruposPorSituacaoCount(DominioSituacao.I) + 
										   listaItens.size());
			
			
			inativarProcsNaoPresentesNoSUS(controle.getLogRetorno(), aghArquivoItensProcedimentoHospitalar, inicio, phoSeq, listaCodTabela, controle);
			
			inativarProcedsGruposSubgruposInativos(inicio, controle.getLogRetorno(), aghArquivoItensProcedimentoHospitalar, phoSeq, controle);

			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(ATUALIZANDO_VALORES);

			for (final ArquivoVO vo : listaItens) {
				controle.incrementaNrRegistrosProcessados();
				final Long codTab = vo.codTabela;
				boolean isExcesao = false;
			
				for (final String nrsExcecao : nrsExcecoes) {
					if (codTab.equals(nrsExcecao)) {
						isExcesao = true;
						break;
					}
				}
				
				if (!isExcesao) {
					final List<FatItensProcedHospitalar> listItemProcedHsp = fatItensProcedHospitalarDAO
							.obterItemProcedHospPorPhoSeqPorCodTabelaPorSituacao(phoSeq, codTab, DominioSituacao.A,
																				 caracteristicaFinanciamento, 
																				 caracteristicaComplexidade);
					
					if (listItemProcedHsp != null && !listItemProcedHsp.isEmpty()) {
						boolean ligouCarga = false;
						try{
							beginTransaction();
							this.getValorItemProcedHospCompsRN().ligaCarga(true);
							commitTransaction();
							ligouCarga=true;
							for (final FatItensProcedHospitalar itemProcedHsp : listItemProcedHsp) {
								
								// Busca Valor do Ambulatorio, Anestesista, data de inicio
								Date dtInicio = null;
								BigDecimal vlrSrvProfAmb = null, vlrAnest = null;
								
								for (final FatVlrItemProcedHospComps vlrItemProcedHospComps : getFatVlrItemProcedHospCompsDAO()
										.obterListaValorItemProcHospCompPorPhoIphAbertos(itemProcedHsp.getId().getPhoSeq(),
																						 itemProcedHsp.getId().getSeq())) {
									
									vlrSrvProfAmb = vlrItemProcedHospComps.getVlrServProfisAmbulatorio();
									vlrAnest = vlrItemProcedHospComps.getVlrAnestesista();
									dtInicio = vlrItemProcedHospComps.getId().getDtInicioCompetencia();
									break;
								}
	
								if (!DateUtil.isDatasIguais(DateUtil.truncaData(dtInicio), DateUtil.truncaData(dtIncio))) {
									persistirFatVlrItemProcedHospCompsDtInicioDiferentes( controle.getLogRetorno(), dtIncio, dtFim, vo,
																						  codTab, itemProcedHsp, vlrSrvProfAmb, vlrAnest);
	
								} else {
									persistirFatVlrItemProcedHospCompsDtInicioIguais(controle.getLogRetorno(), vo, itemProcedHsp, vlrSrvProfAmb, vlrAnest);
								}
								
								//
								// #34834
								atualizarCaracteristicas(vo,controle,itemProcedHsp,caracteristicaFinanciamento,caracteristicaComplexidade);
							}
							beginTransaction();
							this.getValorItemProcedHospCompsRN().ligaCarga(false);
							commitTransaction();
							ligouCarga = false;
						}catch(final Exception e){
							if(ligouCarga){
								beginTransaction();
								this.getValorItemProcedHospCompsRN().ligaCarga(false);
								commitTransaction();
								ligouCarga = false;
							}
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							e.printStackTrace(pw);
							String stringException = sw.toString();
							
							controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(ERRO_INESPERADO)
									  				.append(DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
									  				.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" ).append( stringException ).append( "].")
									  				.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
							
							controle.gravarLog(ERRO_INESPERADO + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR+ stringException);		
						}
					}
				}
				
				if (controle.getNrRegistrosProcessados() % 20 == 0) {
					util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, inicio, new Date(), 
										  ProcessadorArquivosImportacaoSusUtil.REFRESH, 70,
										  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				}
			}

			final Date fim = new Date();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  				.append("Tabela Procedimento e Itens de Procedimento lidas e atualizadas com sucesso em ")
					  				.append((((fim.getTime() - inicio.getTime())) / 1000L))
					  				.append( " segundos.").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			
			util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, new Date(), 100, 0, 0, new Date(), controle);
			
		} catch (final Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(ERRO_INESPERADO)
					  				.append(DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
					  				.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" ).append( stringException ).append( "].")
					  				.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			
			controle.gravarLog(ERRO_INESPERADO + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR+ stringException);		
		} finally {
			//atualiza os logs
			if (aghArquivoItensProcedimentoHospitalar != null) {
				util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, new Date(), 100, 100, 0, new Date(), controle);
			}
		}
	}
	

	public void atualizarFinanciamentoComplexidade(final ControleProcessadorArquivosImportacaoSus controle, final List<String> listaNomeArquivos) {
		final Date inicio = new Date();
		controle.iniciarLogRetorno();
		controle.setPartial(0);
		
		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(STRING_0);
		
		AghArquivoProcessamento aghArquivoItensProcedimentoHospitalar = null;
		
		final List<ArquivoVO> listaItens = new ArrayList<ArquivoVO>();
		final List<Long> listaCodTabela = new ArrayList<Long>(0);
		
		try {
			final String nomeArquivoItensProcedimentos = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_ITENS_PROCED_HOSP).toLowerCase(); // "tb_procedimento.txt";
			final String parametroFaturamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO).toUpperCase();
			final Short phoSeq = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			final Integer caracteristicaFinanciamento = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_IMPORTACAO_PROCED_FCF);
			final Integer caracteristicaComplexidade = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_IMPORTACAO_PROCED_FCC);
			
			// Abertura de arquivos
			aghArquivoItensProcedimentoHospitalar = aghuFacade.obterArquivoNaoProcessado(parametroFaturamento, nomeArquivoItensProcedimentos);
			aghArquivoItensProcedimentoHospitalar.setDthrInicioProcessamento(new Date(inicio.getTime()));
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(STRING_1);
			util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, inicio, 0, 60, 0, null, controle);
			
			// realiza atualização dos itens de procedimento hospitalar
	
			//final Object[] datas = getFatCompetenciaDAO().listarDataInicioEFimCompModAMB();
			//final Date dtIncio = (Date) datas[0];
			//final Date dtFim = (Date) datas[1];

			final String[] nrsExcecoes = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PROCEDIMENTOS_IGNORADOS_IMPORTACAO_VALOR).getVlrTexto().split(",");
			final List<String> arq = util.processaRegistrosProcedimento(aghArquivoItensProcedimentoHospitalar, listaNomeArquivos, inicio, controle );
			
			for (final String strLine : arq) {
				controle.incrementaNrRegistrosProcessados();

				final ArquivoVO vo = lerLinhaArquivo(strLine);

				listaItens.add(vo);
				listaCodTabela.add(vo.codTabela);

				if (controle.getNrRegistrosProcessados() % 20 == 0) {
					util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 60, ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				}
			}
			controle.setPartial(30);
			controle.incrementaNrRegistrosProcessados();
			
			//controle.setNrRegistrosProcesso(fatItensProcedHospitalarDAO.obterListaAtivosPorPhoSeqCount(phoSeq) + 
										   //this.getFatSubGrupoDAO().listarSubGruposPorSituacaoCount(DominioSituacao.I) + 
										  //listaItens.size());
			
			
			//inativarProcsNaoPresentesNoSUS(controle.getLogRetorno(), aghArquivoItensProcedimentoHospitalar, inicio, phoSeq, listaCodTabela, controle);
			
			//inativarProcedsGruposSubgruposInativos(inicio, controle.getLogRetorno(), aghArquivoItensProcedimentoHospitalar, phoSeq, controle);

			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(ATUALIZANDO_VALORES);

			for (final ArquivoVO vo : listaItens) {
				controle.incrementaNrRegistrosProcessados();
				final Long codTab = vo.codTabela;
				boolean isExcesao = false;
			
				for (final String nrsExcecao : nrsExcecoes) {
					if (codTab.equals(nrsExcecao)) {
						isExcesao = true;
						break;
					}
				}
				
				if (!isExcesao) {
					final List<FatItensProcedHospitalar> listItemProcedHsp = fatItensProcedHospitalarDAO
							.obterItemProcedHospPorPhoSeqPorCodTabelaPorSituacao(phoSeq, codTab, DominioSituacao.A,
																				 caracteristicaFinanciamento, 
																				 caracteristicaComplexidade);
					
					if (listItemProcedHsp != null && !listItemProcedHsp.isEmpty()) {
						boolean ligouCarga = false;
						try{
							beginTransaction();
							this.getValorItemProcedHospCompsRN().ligaCarga(true);
							commitTransaction();
							ligouCarga=true;
							for (final FatItensProcedHospitalar itemProcedHsp : listItemProcedHsp) {
								
								// Busca Valor do Ambulatorio, Anestesista, data de inicio
								//Date dtInicio  = null;
								//BigDecimal vlrSrvProfAmb = null, vlrAnest = null;
								
//								for (final FatVlrItemProcedHospComps vlrItemProcedHospComps : getFatVlrItemProcedHospCompsDAO()
//										.obterListaValorItemProcHospCompPorPhoIphAbertos(itemProcedHsp.getId().getPhoSeq(),
//																						 itemProcedHsp.getId().getSeq())) {
//									
//									//vlrSrvProfAmb = vlrItemProcedHospComps.getVlrServProfisAmbulatorio();
//									//vlrAnest = vlrItemProcedHospComps.getVlrAnestesista();
//									//dtInicio = vlrItemProcedHospComps.getId().getDtInicioCompetencia();
//									break;
//								}
	

								//
								// #34834
								atualizarCaracteristicas(vo,controle,itemProcedHsp,caracteristicaFinanciamento,caracteristicaComplexidade);
							}
							beginTransaction();
							this.getValorItemProcedHospCompsRN().ligaCarga(false);
							commitTransaction();
							ligouCarga = false;
						}catch(final Exception e){
							if(ligouCarga){
								beginTransaction();
								this.getValorItemProcedHospCompsRN().ligaCarga(false);
								commitTransaction();
								ligouCarga = false;
							}
							StringWriter sw = new StringWriter();
							PrintWriter pw = new PrintWriter(sw);
							e.printStackTrace(pw);
							String stringException = sw.toString();
							
							controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(ERRO_INESPERADO)
									  				.append(DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
									  				.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" ).append( stringException ).append( "].")
									  				.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
							
							controle.gravarLog(ERRO_INESPERADO + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR+ stringException);		
						}
					}
				}
				
				if (controle.getNrRegistrosProcessados() % 20 == 0) {
					util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, inicio, new Date(), 
										  ProcessadorArquivosImportacaoSusUtil.REFRESH, 120,
										  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				}
			}
			
			util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, new Date(), 100, 0, 0, new Date(), controle);
		
			final Date fim = new Date();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  				.append("Tabela Procedimento e Itens de Procedimento lidas e atualizadas com sucesso em ")
					  				.append((((fim.getTime() - inicio.getTime())) / 1000L))
					  				.append( " segundos.").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			
			
			
		} catch (final Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(ERRO_INESPERADO)
					  				.append(DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
					  				.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" ).append( stringException ).append( "].")
					  				.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			
			controle.gravarLog(ERRO_INESPERADO + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR+ stringException);		
		} finally {
			//atualiza os logs
			if (aghArquivoItensProcedimentoHospitalar != null) {
				util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, new Date(), 100, 100, 0, new Date(), controle);
				final Date fim = new Date();
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
						  				.append("Caracteristicas de Complexidade e Financiamento lidas e atualizadas com sucesso em ")
						  				.append((((fim.getTime() - inicio.getTime())) / 1000L))
						  				.append( " segundos.").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			}
		}
	}

	private void atualizarCaracteristicas(ArquivoVO vo, final ControleProcessadorArquivosImportacaoSus controle, final FatItensProcedHospitalar itemProcedHsp, final Integer caracteristicaFinanciamento, final Integer caracteristicaComplexidade) {
		boolean fin = false;
		boolean com = false;
		
		// PADRAO PS3
		FatCaractFinanciamento itemPs3 = fatCaractFinanciamentoDAO.obterPorChavePrimaria(caracteristicaFinanciamento);
		FatCaractComplexidade itemPs2 = fatCaractComplexidadeDAO.obterPorChavePrimaria(caracteristicaComplexidade);
		
		FatCaractComplexidade itemComplexidade = null;
		if(vo.seqSusComplexidade != null){
			itemComplexidade = fatCaractComplexidadeDAO.obterPorCodigoSus(vo.seqSusFinanciamento);
		}
		
		//Atualização da característica de financiamento.
		if(vo.seqSusFinanciamento != null ){
			FatCaractFinanciamento itemFinanciamento = fatCaractFinanciamentoDAO.obterPorCodigoSus(vo.seqSusFinanciamento);
			if(itemFinanciamento == null){
				// msg1 no log
				logaFinanciamento(vo, controle, itemProcedHsp);
			}else{
				fin = true;
			}
			if(itemComplexidade == null){
				// msg2 no log
				logaComplexidade(vo, controle, itemProcedHsp);
			}else{
				com = true;
			}
			
			// jorunal
			//RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			//final FatItemProcedHospitalarJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, FatItemProcedHospitalarJn.class, servidorLogado.getUsuario());
			//journal.setAlteradoEm(new Date());
			
			super.beginTransaction();
			if(com && fin){
				persistirAtualizacaoItensProcedimentoFinanciamentoComplexidade(itemProcedHsp,itemFinanciamento,itemComplexidade);
			}else if(com){
				persistirAtualizacaoItensProcedimentoFinanciamentoComplexidade(itemProcedHsp,itemPs3,itemComplexidade);
			}else if(fin){
				persistirAtualizacaoItensProcedimentoFinanciamentoComplexidade(itemProcedHsp,itemFinanciamento,itemPs2);
			}else{
				persistirAtualizacaoItensProcedimentoFinanciamentoComplexidade(itemProcedHsp,itemPs3,itemPs2);
			}
			super.commitTransaction();
		}
		//super.beginTransaction();
		//fatItensProcedHospitalarDAO.ativarItensProcedHospitalar(vo.codTabela);
		//super.commitTransaction();
	}
	
	public void logaFinanciamento(ArquivoVO vo, final ControleProcessadorArquivosImportacaoSus controle, final FatItensProcedHospitalar itemProcedHsp){
		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
		.append("O item de procedimento hospitalar ").append(itemProcedHsp.getId().getSeq())
		.append(" não foi associado à característica de financiamento ").append(vo.seqSusFinanciamento)
		.append(" pois esta característica de financiamento não está relacionada no cadastro. ");
	}
	
	public void logaComplexidade(ArquivoVO vo, final ControleProcessadorArquivosImportacaoSus controle, final FatItensProcedHospitalar itemProcedHsp){
		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
		.append("O item de procedimento hospitalar ").append(itemProcedHsp.getId().getSeq())
		.append(" não foi associado à característica de complexidade ").append(vo.seqSusComplexidade)
		.append(" pois esta característica de complexidade não está relacionada no cadastro. ");
	}
	
	public void persistirAtualizacaoItensProcedimentoFinanciamentoComplexidade(FatItensProcedHospitalar itemProcedHsp,FatCaractFinanciamento financiamento,FatCaractComplexidade complexidade){
		// busca entidade e faz as atualizações necessárias
		FatItensProcedHospitalar entity = fatItensProcedHospitalarDAO.obterPorChavePrimaria(itemProcedHsp.getId());
		if(financiamento!=null){
			entity.setFatCaracteristicaFinanciamento(financiamento);
		}
		if(complexidade!=null){
			entity.setCaracteristicaComplexidade(complexidade);
		}
		fatItensProcedHospitalarDAO.merge(entity);
	}
	
	/**
	 * Ativa todos itens proced Hospitalar feito pq se a importação trancar irá deixar os registros eternamente inativos
	 * @param vo
	 */
	private void ativarItensProcedHospitalar(final ArquivoVO vo) {
		super.beginTransaction();
		fatItensProcedHospitalarDAO.ativarItensProcedHospitalar(vo.codTabela);
		super.commitTransaction();
	}
	
	private void inativarProcsNaoPresentesNoSUS(final StringBuilder logRetorno,	
												AghArquivoProcessamento aghArquivoItensProcedimentoHospitalar,
												final Date inicio, 
												final Short phoSeq, final List<Long> listaCodTabela,
												final ControleProcessadorArquivosImportacaoSus controle) {
		
		// Para as inativações - verifica o que está na nossa tabela e não está no arquivo do SUS
		logRetorno.append("Inativando registros");
		for (final FatItensProcedHospitalar item : fatItensProcedHospitalarDAO.obterListaAtivosPorPhoSeq(phoSeq)) {
			controle.incrementaNrRegistrosProcessados();
			
			if (!listaCodTabela.contains(item.getCodTabela())) {
				
				// Para desconsiderar os criados para ajuste
				if (item.getCodTabela().toString().length() > 5) {
					
					for (final FatItensProcedHospitalar atualizar : fatItensProcedHospitalarDAO
							.listarItensProcedHospPorProcedHospPorCodTabelaPorPhoSeq(phoSeq, item.getCodTabela())) {
						
						try {
							super.beginTransaction();
							
							final FatItensProcedHospitalar itemProcedHospClone = faturamentoFacade.clonarItemProcedimentoHospitalar(atualizar);
							atualizar.setSituacao(DominioSituacao.I);
							this.getItemProcedimentoHospitalarON().atualizarItemProcedimentoHospitalar(atualizar, itemProcedHospClone);
							fatItensProcedHospitalarDAO.flush();
							super.commitTransaction();
							
							logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
									  .append("ITENS PROCEDIMENTO; I; ").append(atualizar.getCodTabela())
									  .append(" ; ").append(atualizar.getDescricao());
							
						} catch (final Exception e) {
							super.rollbackTransaction();
							logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
									  .append("Erro ao invativar registro " ).append( atualizar.getCodTabela());
						}
					}
				}
			}
			if (controle.getNrRegistrosProcessados() % 20 == 0) {
				util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, inicio, new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 70,
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			}
		}
	}

	private void inativarProcedsGruposSubgruposInativos(final Date inicio, 
														final StringBuilder logRetorno,	
														final AghArquivoProcessamento aghArquivoItensProcedimentoHospitalar, 
														final Short phoSeq, final ControleProcessadorArquivosImportacaoSus controle) {
		
		// Inativações através da tabela FAT_SUB_GRUPOS
		logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
				  .append("Inativando procedimentos com grupos ou subgrupos inativos.");
		
		for (final FatSubGrupo subGrp : this.getFatSubGrupoDAO().listarSubGruposPorSituacao(DominioSituacao.I)) {
			controle.incrementaNrRegistrosProcessados();
			
			final List<FatItensProcedHospitalar> listaAtualizacao = fatItensProcedHospitalarDAO
					.obterItemProcedHospPorPhoSeqPorPorGrupoPorSubGrupoPorSituacao( phoSeq,
																					subGrp.getId().getGrpSeq(), 
																					subGrp.getId().getSubGrupo(), 
																					DominioSituacao.A);
			if (listaAtualizacao != null && !listaAtualizacao.isEmpty()) {
				for (FatItensProcedHospitalar atualizar : listaAtualizacao) {
					
					try {
						final FatItensProcedHospitalar itemProcedHospClone = faturamentoFacade.clonarItemProcedimentoHospitalar(atualizar);
						
						if (itemProcedHospClone != null) {
							super.beginTransaction();
							atualizar.setSituacao(DominioSituacao.I);
							this.getItemProcedimentoHospitalarON().atualizarItemProcedimentoHospitalar(atualizar, itemProcedHospClone);
							super.commitTransaction();
							
							logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("ITENS PROCEDIMENTO; I; " )
									  .append( atualizar.getCodTabela() ).append( " ; " )
									  .append(atualizar.getDescricao());
						}
					} catch (final Exception e) {
						super.rollbackTransaction();
						LOG.error(e.getMessage(), e);
						logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
								  .append("Erro ao invativar procedimentos do grupo: ").append(subGrp.getId().getGrpSeq() )
								  .append( " e subgrupo: ").append(subGrp.getId().getSubGrupo());
					}
				}
			}
			if (controle.getNrRegistrosProcessados() % 20 == 0) {
				util.atualizarArquivo(aghArquivoItensProcedimentoHospitalar, inicio, new Date(), ProcessadorArquivosImportacaoSusUtil.REFRESH, 70, ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			}
		}
	}

	private void persistirFatVlrItemProcedHospCompsDtInicioDiferentes(final StringBuilder logRetorno, final Date dtIncio, final Date dtFim,
																	   final ArquivoVO vo, final Long codTab, final FatItensProcedHospitalar itemProcedHsp,
																	   BigDecimal vlrSrvProfAmb, BigDecimal vlrAnest) {
		try {
			super.beginTransaction();
			for (final FatVlrItemProcedHospComps vlrItemProcedHospComps : getFatVlrItemProcedHospCompsDAO()
					.obterListaValorItemProcHospCompPorPhoIphAbertos(itemProcedHsp.getId().getPhoSeq(), itemProcedHsp.getId().getSeq())) {
				
				vlrItemProcedHospComps.setDtFimCompetencia(dtFim);
				getValorItemProcedHospCompsON().persistirVlrItemProcedHospComps(vlrItemProcedHospComps, DominioOperacoesJournal.UPD);
				fatItensProcedHospitalarDAO.flush();
			}
			super.commitTransaction();
		} catch (final Exception e) {
			super.rollbackTransaction();
			LOG.error(e.getMessage(), e);
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro ao fechar competência");
		}
		
		try {
			final FatVlrItemProcedHospComps novoVlrItemProcedHospComps = new FatVlrItemProcedHospComps();
			
			novoVlrItemProcedHospComps.setId(new FatVlrItemProcedHospCompsId(itemProcedHsp.getId().getPhoSeq(), 
																			 itemProcedHsp.getId().getSeq(), 
																			 dtIncio)
											);
			novoVlrItemProcedHospComps.setCpdPhoSeq(itemProcedHsp.getId().getPhoSeq());
			novoVlrItemProcedHospComps.setCpdDtInicio(dtIncio);
			novoVlrItemProcedHospComps.setVlrServHospitalar(vo.valorSh);
			novoVlrItemProcedHospComps.setVlrServProfissional(vo.valorSp);
			novoVlrItemProcedHospComps.setVlrProcedimento(vo.valorProced);
			novoVlrItemProcedHospComps.setQtdMaximaExecucao(vo.vQtMaximaExecucao);
			novoVlrItemProcedHospComps.setVlrAnestesista(vlrAnest);
			novoVlrItemProcedHospComps.setVlrServProfisAmbulatorio(vlrSrvProfAmb);
			novoVlrItemProcedHospComps.setFatItensProcedHospitalar(itemProcedHsp);
			super.beginTransaction();
			
			this.getValorItemProcedHospCompsON().persistirVlrItemProcedHospComps(novoVlrItemProcedHospComps,
																				 DominioOperacoesJournal.INS);
			fatItensProcedHospitalarDAO.flush();
			super.commitTransaction();
			
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  .append("VALORES NOVOS;I;" )
					  .append(codTab).append(';')
					  .append(vo.valorSh).append(';')
					  .append(vo.valorProced).append(';')
					  .append(vo.valorSp);
			
		} catch (final Exception e) {
			super.rollbackTransaction();
			LOG.error(e.getMessage(), e);
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  .append("Erro ao inserir nova competência: PHI_SEQ = " ).append(itemProcedHsp.getId().getSeq())
					  .append(" PHO_SEQ=").append(itemProcedHsp.getId().getPhoSeq() )
					  .append(" e Data inicio= ").append(dtIncio).append(" em FAT_VLR_ITEM_PROCED_HOSP_COMPS");
		}
	}

	private void persistirFatVlrItemProcedHospCompsDtInicioIguais(final StringBuilder logRetorno,	
																   final ArquivoVO vo, final FatItensProcedHospitalar itemProcedHsp,
																   BigDecimal vlrSrvProfAmb, BigDecimal vlrAnest) {
		try {
			
			for (final FatVlrItemProcedHospComps vlrItemProcedHospComps : this.getFatVlrItemProcedHospCompsDAO()
					.obterListaValorItemProcHospCompPorPhoIphAbertos(itemProcedHsp.getId().getPhoSeq(),
							itemProcedHsp.getId().getSeq())) {
				
				super.beginTransaction();
				
				vlrItemProcedHospComps.setVlrServHospitalar((BigDecimal) vo.valorSh);
				vlrItemProcedHospComps.setVlrServProfissional((BigDecimal) vo.valorSp);
				vlrItemProcedHospComps.setVlrProcedimento((BigDecimal) vo.valorProced);
				vlrItemProcedHospComps.setQtdMaximaExecucao( vlrItemProcedHospComps.getQtdMaximaExecucao() == null ? vo.vQtMaximaExecucao: vlrItemProcedHospComps.getQtdMaximaExecucao());
				vlrItemProcedHospComps.setVlrAnestesista(vlrAnest);
				vlrItemProcedHospComps.setVlrServProfisAmbulatorio(vlrSrvProfAmb);
				
				this.getValorItemProcedHospCompsON().persistirVlrItemProcedHospComps(vlrItemProcedHospComps, DominioOperacoesJournal.UPD);
				fatItensProcedHospitalarDAO.flush();
				super.commitTransaction();
			}
		} catch (final Exception e) {
			super.rollbackTransaction();
			LOG.error(e.getMessage(), e);
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  .append("Erro ao atualizar, PHI_SEQ = ").append(itemProcedHsp.getId().getSeq())
					  .append(" PHO_SEQ=").append(itemProcedHsp.getId().getPhoSeq())
					  .append(" sem data de fim de competencia em FAT_VLR_ITEM_PROCED_HOSP_COMPS");
		}
	}

	private void persistirFatVlrItemProcedHospComps(final StringBuilder logRetorno, final Short phoSeq, final Date dtIncio,
													 final ArquivoVO vo, final FatItensProcedHospitalar itemProcedHospitalar) {
		try {
			super.beginTransaction();
			
			final FatVlrItemProcedHospComps vlrItemProcedHospComps = new FatVlrItemProcedHospComps();
			
			vlrItemProcedHospComps.setId(new FatVlrItemProcedHospCompsId(phoSeq, itemProcedHospitalar.getId().getSeq(), dtIncio));
			vlrItemProcedHospComps.setCpdPhoSeq(phoSeq);
			vlrItemProcedHospComps.setCpdDtInicio(dtIncio);
			vlrItemProcedHospComps.setVlrServHospitalar(vo.valorSh);
			vlrItemProcedHospComps.setVlrServProfissional(vo.valorSp);
			vlrItemProcedHospComps.setVlrProcedimento(vo.valorProced);
			vlrItemProcedHospComps.setQtdMaximaExecucao(vo.vQtMaximaExecucao);
			
			this.getValorItemProcedHospCompsRN().ligaCarga(true);
			this.getValorItemProcedHospCompsON().persistirVlrItemProcedHospComps(vlrItemProcedHospComps, DominioOperacoesJournal.INS);
			fatItensProcedHospitalarDAO.flush();
			
			this.getValorItemProcedHospCompsRN().ligaCarga(false);	

			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("VALORES ITENS; I; ")
					  .append(vo.valorShStr).append(" ; vlr_procedimento; ").append(vo.valorSpStr);

		} catch (final Exception e) {
			super.rollbackTransaction();
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  .append("Não foi possível inserir - FAT_VLR_ITEM_PROCED_HOSP_COMPS – IPH SEQ:")
					  .append(itemProcedHospitalar.getId().getSeq() ).append('.');
		} finally{
			super.commitTransaction();
		}
	}

	
	
	private ArquivoVO lerLinhaArquivo(final String strLine) {

		final ArquivoVO vo = new ArquivoVO();
		
		vo.codTabela = Long.valueOf(strLine.substring(0, 10).trim());
		vo.grupoId = strLine.substring(0, 2).trim();
		vo.subGrupoId = strLine.substring(2, 4).trim();
		vo.formaOrgId = strLine.substring(4, 6).trim();

		vo.fatFormaOrganizacao = fatFormaOrganizacaoDAO.obterPorChavePrimaria(
																			   new FatFormaOrganizacaoId(Short.valueOf(vo.grupoId), 
																					   				     Byte.valueOf(vo.subGrupoId), 
																					   				     Byte.valueOf(vo.formaOrgId)
																					   				    )
																			  );
		
		vo.descricao = strLine.substring(10, 250).trim().toUpperCase();
		vo.sexoStr = strLine.substring(261, 262).trim().toUpperCase();
		vo.valorShStr = strLine.substring(282, 292).trim();
		vo.valorProcedStr = strLine.substring(292, 302).trim();
		vo.valorSpStr = strLine.substring(302, 312).trim();
		vo.idadeMinStr = strLine.substring(274, 278).trim();
		vo.idadeMaxStr = strLine.substring(278, 282).trim();
		vo.valorSh = new BigDecimal(vo.valorShStr).divide(new BigDecimal(100));
		vo.valorProced = new BigDecimal(vo.valorProcedStr).divide(new BigDecimal(100));
		vo.valorSp = new BigDecimal(vo.valorSpStr).divide(new BigDecimal(100));

		vo.idadeMin = Integer.valueOf(vo.idadeMinStr);
		vo.idadeMax = Integer.valueOf(vo.idadeMaxStr);
		
		String seqSusString = strLine.substring(312, 314).trim();
		vo.seqSusFinanciamento = Integer.valueOf(seqSusString);
		
		String seqSusString2 = strLine.substring(260, 261).trim();
		vo.seqSusComplexidade = Integer.valueOf(seqSusString2);
		
		try {
			vo.vQtMaximaExecucao = Short.valueOf(strLine.substring(262, 266).trim());
		} catch (final NumberFormatException e) {
			vo.vQtMaximaExecucao = null;
		}
		
		if ("I".equals(vo.sexoStr) || "N".equals(vo.sexoStr)) {
			vo.sexo = DominioSexoDeterminante.valueOf("Q");
		} else {
			vo.sexo = DominioSexoDeterminante.valueOf(vo.sexoStr);
		}

		if (vo.idadeMin == 9999) {
			vo.idadeMin = 0;
		} else {
			vo.idadeMin = vo.idadeMin / 12;
		}

		vo.idadeMax = vo.idadeMax / 12;
		
		return vo;
	}

	private FatItensProcedHospitalar criarItemProcedHospitalar(
			Short phoSeq, Long codTabela, String descricao,
			Integer idadeMin, Integer idadeMax, DominioSexoDeterminante sexo,
			FatFormaOrganizacao fatFormaOrganizacao) {

		final FatItensProcedHospitalar itemProcedHospitalar = new FatItensProcedHospitalar();
		
		itemProcedHospitalar.setId(new FatItensProcedHospitalarId(phoSeq, null));
		itemProcedHospitalar.setCodTabela(codTabela);
		itemProcedHospitalar.setDescricao(descricao);
		itemProcedHospitalar.setIdadeMin(idadeMin);
		itemProcedHospitalar.setIdadeMax(idadeMax);
		itemProcedHospitalar.setSexo(sexo);
		itemProcedHospitalar.setHospDia(false);
		itemProcedHospitalar.setInternacao(false);
		itemProcedHospitalar.setSituacao(DominioSituacao.A);
		itemProcedHospitalar.setExigeValor(false);
		itemProcedHospitalar.setCobrancaCma(false);
		itemProcedHospitalar.setExigeAutorizacaoPrevia(false);
		itemProcedHospitalar.setProcedimentoEspecial(false);
		itemProcedHospitalar.setDiariaAcompanhante(false);
		itemProcedHospitalar.setRealDifereSolic(false);
		itemProcedHospitalar.setCidadeObrigatoria(false);
		itemProcedHospitalar.setDadosParto(false);
		itemProcedHospitalar.setHcpaCadastrado(true);
		itemProcedHospitalar.setCobraProcedimentoEspecial(false);
		itemProcedHospitalar.setQuantidadeMaiorInternacao(false);
		itemProcedHospitalar.setTipoAih5(false);
		itemProcedHospitalar.setCobrancaConta(false);
		itemProcedHospitalar.setCobrancaApac(false);
		itemProcedHospitalar.setPreencheCma(false);
		itemProcedHospitalar.setUtilizacaoItem(DominioUtilizacaoItem.N);
		itemProcedHospitalar.setConsulta(false);
		itemProcedHospitalar.setDadosParto(false);
		itemProcedHospitalar.setCobraExcedenteBpa(false);
		itemProcedHospitalar.setCobrancaAmbulatorio(false);
		itemProcedHospitalar.setAidsPolitraumatizado(false);
		itemProcedHospitalar.setCirurgiaMultipla(false);
		itemProcedHospitalar.setDcihTransplante(false);
		itemProcedHospitalar.setFaec(false);
		itemProcedHospitalar.setSolicDifereReal(false);
		itemProcedHospitalar.setBuscaDoador(false);
		itemProcedHospitalar.setCobrancaDiarias(DominioCobrancaDiaria.N);
		itemProcedHospitalar.setExigeConsulta(false);
		itemProcedHospitalar.setFideps(DominioFideps.N);
		itemProcedHospitalar.setPsiquiatria(false);
		itemProcedHospitalar.setProcPrincipalApac(false);
		itemProcedHospitalar.setModoLancamentoFat(DominioModoLancamentoFat.O);
		itemProcedHospitalar.setFormaOrganizacao(fatFormaOrganizacao);
		itemProcedHospitalar.setValidaIdade(false);
		
		return itemProcedHospitalar;
	}

	
	
	

	protected ValorItemProcedHospCompsRN getValorItemProcedHospCompsRN() {
		return valorItemProcedHospCompsRN;
	}
	

	

	protected ItemProcedimentoHospitalarON getItemProcedimentoHospitalarON() {
		return itemProcedimentoHospitalarON;
	}

	protected ValorItemProcedHospCompsON getValorItemProcedHospCompsON() {
		return valorItemProcedHospCompsON;
	}	
	
	
	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}
	
	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}	

	protected FatFormaOrganizacaoDAO getFatFormaOrganizacaoDAO() {
		return fatFormaOrganizacaoDAO;
	}	

	protected FatSubGrupoDAO getFatSubGrupoDAO() {
		return fatSubGrupoDAO;
	}
	
	protected FatVlrItemProcedHospCompsDAO getFatVlrItemProcedHospCompsDAO() {
		return fatVlrItemProcedHospCompsDAO;
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