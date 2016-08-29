package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.faturamento.dao.FatExmPacPreTransDAO;
import br.gov.mec.aghu.faturamento.dao.FatListaPacApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoTratamentosDAO;
import br.gov.mec.aghu.faturamento.vo.CursorTipoListaPmrKitPreVO;
import br.gov.mec.aghu.faturamento.vo.RnCapcCboProcResVO;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatExmPacPreTrans;
import br.gov.mec.aghu.model.FatListaPacApac;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * ORADB: FATP_ATUALIZA_PMR_KITS_PRE
 * @author ESCHWEIGERT
 * @since 19/08/2013
 */
@Stateless
public class FaturamentoAtualizaExamesPreTransplantesRN extends BaseBusiness {


@EJB
private FatExmPacPreTransRN fatExmPacPreTransRN;

@EJB
private FaturamentoFatkCapUniRN faturamentoFatkCapUniRN;

@EJB
private ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON;

private static final Log LOG = LogFactory.getLog(FaturamentoAtualizaExamesPreTransplantesRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;

@EJB
private ISolicitacaoExameFacade solicitacaoExameFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@Inject
private FatTipoTratamentosDAO fatTipoTratamentosDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private FatProcedHospInternosDAO fatProcedHospInternosDAO;

@EJB
private IAghuFacade aghuFacade;

@Inject
private FatListaPacApacDAO fatListaPacApacDAO;

@Inject
private FatExmPacPreTransDAO fatExmPacPreTransDAO;

	private static final long serialVersionUID = -2157594190016428942L;
	
	enum FaturamentoAtualizaExamesPreTransplantesRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_TRATAMENTO_SEM_PHI, MENSAGEM_TRATAMENTO_SEM_PHI_SEGUNDO
		;
	}	
	
	protected final void atualizaPmrKitsPre(final FatCompetencia competencia, String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException {
		final FatListaPacApacDAO fatListaPacApacDAO = getFatListaPacApacDAO();
		final FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
		final FatExmPacPreTransDAO fatExmPacPreTransDAO = getFatExmPacPreTransDAO();
		final FatTipoTratamentosDAO fatTipoTratamentosDAO = getFatTipoTratamentosDAO();
		final FatProcedHospInternosDAO fatProcedHospInternosDAO = getFatProcedHospInternosDAO();
		final ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON = getProcedimentosAmbRealizadosON();
		final FaturamentoFatkCapUniRN faturamentoFatkCapUniRN = getFaturamentoFatkCapUniRN();
		final FatExmPacPreTransRN fatExmPacPreTransRN = getFatExmPacPreTransRN();
		final IRegistroColaboradorFacade registroColaboradorFacade = getRegistroColaboradorFacade();
		final IAghuFacade aghuFacade = getAghuFacade();
		final IParametroFacade parametroFacade = getParametroFacade();
		final ISolicitacaoExameFacade solicitacaoExameFacade = getSolicitacaoExameFacade();

		final Short cpgCphCspCnvCodigo = parametroFacade.buscarValorShort(AghuParametrosEnum.P_CONVENIO_SUS);	// 1 
		final Byte cpgCphCspSeq1 = parametroFacade.buscarValorByte(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);	// 1
		final Byte cpgCphCspSeq2 = parametroFacade.buscarValorByte(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);	// 2

		logInfo("Iniciando execução em "+DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		final Date[] vals = obterDataRealizacaoELimite(competencia.getId().getMes(), competencia.getId().getAno());
		
		final Date vDataRealizacao = vals[0];
		final Date vLimite = vals[1];
		
		// Seleciona pacientes ativos em lista de pre-transplante
		List<FatListaPacApac> cLista = fatListaPacApacDAO.buscarFatListaPacApac();

		for (FatListaPacApac rLista : cLista) {
			// Verifica se tem exames pertencentes ao kit do tratamento
			//logInfo("Paciente código:"+rLista.getPaciente().getCodigo()+" tpt_Seq:"+rLista.getTipoTratamento().getSeq());
			
			FatProcedAmbRealizado ultimoFatProcedAmbRealizado = verificaExames( rLista, competencia, fatProcedAmbRealizadoDAO, 
													 						    procedimentosAmbRealizadosON, nomeMicrocomputador, 
													 						    dataFimVinculoServidor);
			if(ultimoFatProcedAmbRealizado != null){
				// eSchweigert: externalizei a chamada de (PROCEDURE inclui_epp) pois na lógica do oracle estava
				//				armazendo em variável global à procedure
				
				final CursorTipoListaPmrKitPreVO vo = buscaPhiSeq( rLista, cpgCphCspCnvCodigo, cpgCphCspSeq1, cpgCphCspSeq2, 
																   fatTipoTratamentosDAO);
				
				// Verifica se foi cobrado um dos códigos de faturamento do kit em FAT_EXM_PAC_PRE_TRANS
				boolean verificaCobranca = verificaCobranca( rLista, vLimite, vDataRealizacao, vo,
															 ultimoFatProcedAmbRealizado, nomeMicrocomputador, 
															 fatExmPacPreTransDAO, fatProcedHospInternosDAO, fatProcedAmbRealizadoDAO, 
															 procedimentosAmbRealizadosON, fatExmPacPreTransRN, faturamentoFatkCapUniRN, 
															 aghuFacade, registroColaboradorFacade, solicitacaoExameFacade);
				if(!verificaCobranca){
					// Se houver, negativo
					// Cobra código de primeiro kit (inclui pmr e epp)
					// p_primeiro, S=Primeiro, N=Proximos
					incluirEPP(true, rLista, vDataRealizacao, vo, fatProcedHospInternosDAO, fatExmPacPreTransRN);
					
					// p_primeiro, S=Primeiro, N=Proximos
					incluirPmr( true, vo, ultimoFatProcedAmbRealizado, 
							    nomeMicrocomputador, dataFimVinculoServidor, vDataRealizacao, 
							    fatProcedAmbRealizadoDAO, fatProcedHospInternosDAO, procedimentosAmbRealizadosON, 
							    aghuFacade, faturamentoFatkCapUniRN, registroColaboradorFacade, solicitacaoExameFacade);
				}
			}
		}
	}
	
	/**
	 * ORADB: F_VERIFICA_COBRANCA
	 */
	private boolean verificaCobranca( final FatListaPacApac rLista, 
									  final Date vLimite, final Date vDataRealizacao,
									  final CursorTipoListaPmrKitPreVO vo,
									  final FatProcedAmbRealizado ultimoFatProcedAmbRealizado, final String nomeMicrocomputador, 

									  final FatExmPacPreTransDAO fatExmPacPreTransDAO, 
									  final FatProcedHospInternosDAO fatProcedHospInternosDAO, 
									  final FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO,
									  
									  final ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON,
									  final FatExmPacPreTransRN fatExmPacPreTransRN,
									  final FaturamentoFatkCapUniRN faturamentoFatkCapUniRN, 
									  
									  final IAghuFacade aghuFacade, 
									  final IRegistroColaboradorFacade registroColaboradorFacade,
									  final ISolicitacaoExameFacade solicitacaoExameFacade) throws BaseException {
		boolean vRet = false;
		
		final List<FatExmPacPreTrans> rEpp = fatExmPacPreTransDAO.
						obterFatExmPacPreTransPorPacienteETipoTratamento( rLista.getPaciente().getCodigo(), 
																		  rLista.getTipoTratamento().getSeq()
																		);
		if(!rEpp.isEmpty()){
			final FatExmPacPreTrans fatExmPacPreTrans = rEpp.get(0);
			
			if(fatExmPacPreTrans != null){
				vRet = true;

				logInfo("Encontrado epp, seq:"+fatExmPacPreTrans.getSeq()+
						" data_realizacao:"+DateUtil.obterDataFormatada(fatExmPacPreTrans.getDtRealizacao(), DateConstants.DATE_PATTERN_DDMMYYYY)+
						" data limite:"+DateUtil.obterDataFormatada(vLimite, DateConstants.DATE_PATTERN_DDMMYYYY));
				
				// Se data_realizacao (yyyymm) < competencia - 6 meses
				if(DateValidator.validaDataMenor(fatExmPacPreTrans.getDtRealizacao(), vLimite)){
					// Cobra código de continuacao do kit (inclui pmr e epp)
					logInfo("Data_realizacao:"+DateUtil.obterDataFormatada(fatExmPacPreTrans.getDtRealizacao(), DateConstants.DATE_PATTERN_DDMMYYYY)+
						    " menor que data limite:"+DateUtil.obterDataFormatada(vLimite, DateConstants.DATE_PATTERN_DDMMYYYY)+
						    ", deve gerar novo.");
					
					// p_primeiro, S=Primeiro, N=Proximos
					incluirEPP(false, rLista, vDataRealizacao, vo, fatProcedHospInternosDAO, fatExmPacPreTransRN);
					
					// p_primeiro, S=Primeiro, N=Proximos
					incluirPmr( false, vo, ultimoFatProcedAmbRealizado,
							    nomeMicrocomputador, new Date(),
							    vDataRealizacao, fatProcedAmbRealizadoDAO, fatProcedHospInternosDAO, 
							    procedimentosAmbRealizadosON, aghuFacade, faturamentoFatkCapUniRN,
							    registroColaboradorFacade, solicitacaoExameFacade);
				}
			}
		}
		return vRet;
	}
	
	private void incluirPmr(final boolean pPrimeiro, final CursorTipoListaPmrKitPreVO vo,
							final FatProcedAmbRealizado ultimoFatProcedAmbRealizado, // rPmr
				  		    final String nomeMicrocomputador, 
				  		    final Date dataFimVinculoServidor, Date vRealizacao,  
				  		    final FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO,
				  		    final FatProcedHospInternosDAO fatProcedHospInternosDAO, 
				  		    final ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON,
				  		    final IAghuFacade aghuFacade, 
				  		    final FaturamentoFatkCapUniRN faturamentoFatkCapUniRN,
				  		    final IRegistroColaboradorFacade registroColaboradorFacade,
				  		    final ISolicitacaoExameFacade solicitacaoExameFacade) throws BaseException {
		
		if(vo != null){
			Integer vPhiSeq = null;

			Short vIphPhoSeq = null;
			Integer vIphSeq = null;
			
			if(pPrimeiro){
				vPhiSeq = vo.getPhiPrimeira();
				vIphPhoSeq = vo.getPhoSeqPrimeira();
				vIphSeq    = vo.getIphSeqPrimeira();
				
			} else {
				vPhiSeq = vo.getPhiSegunda();
				vIphPhoSeq = vo.getPhoSeqSegunda();
				vIphSeq    = vo.getIphSeqSegunda();
			}
			
			// verifica_pmr_cancelada(r_pmr.ISE_SOE_SEQ,r_pmr.ISE_SEQP,v_phi_seq)
			boolean verificaPmrCancelada = verificaPmrCancelada(vPhiSeq, 
															    ultimoFatProcedAmbRealizado.getItemSolicitacaoExame().getId().getSeqp(), 
															    ultimoFatProcedAmbRealizado.getItemSolicitacaoExame().getId().getSoeSeq(), 
																nomeMicrocomputador, dataFimVinculoServidor, 
																fatProcedAmbRealizadoDAO, procedimentosAmbRealizadosON);
			
			if(!verificaPmrCancelada){
				logInfo("Incluindo pmr, soe_seq/seqp:"+ultimoFatProcedAmbRealizado.getItemSolicitacaoExame().getId().getSoeSeq()+
						"/"+ultimoFatProcedAmbRealizado.getItemSolicitacaoExame().getId().getSeqp()+" phi_seq:"+vPhiSeq+
						" cid_seq:"+vo.getCidSeq() );
				
				final FatProcedAmbRealizado proced = new FatProcedAmbRealizado();
				
				proced.setDthrRealizado(ultimoFatProcedAmbRealizado.getDthrRealizado());
				proced.setSituacao(ultimoFatProcedAmbRealizado.getSituacao());
				proced.setLocalCobranca(ultimoFatProcedAmbRealizado.getLocalCobranca());
				proced.setQuantidade(ultimoFatProcedAmbRealizado.getQuantidade());
				proced.setFatCompetencia(ultimoFatProcedAmbRealizado.getFatCompetencia());
				
				// De acordo com Ney em 19/08/13 não é necessário gravar a consulta
				// proced.setConsultaProcedHospitalar(ultimoFatProcedAmbRealizado.getConsultaProcedHospitalar());

				final FatProcedHospInternos procedimentoHospitalarInterno = fatProcedHospInternosDAO.obterPorChavePrimaria(vPhiSeq);
				proced.setProcedimentoHospitalarInterno(procedimentoHospitalarInterno);

				proced.setServidor(ultimoFatProcedAmbRealizado.getServidor());
				proced.setUnidadeFuncional(ultimoFatProcedAmbRealizado.getUnidadeFuncional());
				proced.setEspecialidade(ultimoFatProcedAmbRealizado.getEspecialidade());
				proced.setPaciente(ultimoFatProcedAmbRealizado.getPaciente());
				proced.setConvenioSaudePlano(ultimoFatProcedAmbRealizado.getConvenioSaudePlano());
				proced.setIndOrigem(ultimoFatProcedAmbRealizado.getIndOrigem());
				proced.setPpcCrgSeq(ultimoFatProcedAmbRealizado.getPpcCrgSeq());
				proced.setPpcEprPciSeq(ultimoFatProcedAmbRealizado.getPpcEprPciSeq());
				proced.setPpcEprEspSeq(ultimoFatProcedAmbRealizado.getPpcEprEspSeq());
				proced.setPpcIndRespProc(ultimoFatProcedAmbRealizado.getPpcIndRespProc());
				proced.setAtendimento(ultimoFatProcedAmbRealizado.getAtendimento());
				proced.setIndPendente(ultimoFatProcedAmbRealizado.getIndPendente());
				proced.setQtdeFaturada(ultimoFatProcedAmbRealizado.getQtdeFaturada());
				proced.setTxaSeqp(ultimoFatProcedAmbRealizado.getTxaSeqp());
				proced.setTxaTrgSeq(ultimoFatProcedAmbRealizado.getTxaTrgSeq());
				proced.setTdcSeqp(ultimoFatProcedAmbRealizado.getTdcSeqp());
				proced.setTdcTrgSeq(ultimoFatProcedAmbRealizado.getTdcTrgSeq());
				
				
				final AghCid cid = aghuFacade.obterAghCidsPorChavePrimaria(vo.getCidSeq());
				proced.setCid(cid);
				
				// v_SER_MATR := obtem_ser_matricula(r_pmr.ISE_SOE_SEQ ,v_iph_pho_seq ,v_iph_seq ,TRUNC(v_realizacao,'month') ,v_SER_VIN_CODIGO);
				final RapServidores servidor = obterServidor( ultimoFatProcedAmbRealizado, 
															  vIphPhoSeq, vIphSeq, vRealizacao, 
															  faturamentoFatkCapUniRN, 
															  registroColaboradorFacade, solicitacaoExameFacade); 
				proced.setServidorResponsavel(servidor);
				
				procedimentosAmbRealizadosON.inserirFatProcedAmbRealizado(proced, nomeMicrocomputador, dataFimVinculoServidor);
			}
		} 
	}

	private void incluirEPP( final boolean pPrimeiro,
							   final FatListaPacApac rLista,
							   final Date vDataRealizacao,
							   final CursorTipoListaPmrKitPreVO vo,
							   final FatProcedHospInternosDAO fatProcedHospInternosDAO,
							   final FatExmPacPreTransRN fatExmPacPreTransRN) throws ApplicationBusinessException {
		if(vo != null){
			Integer vPhiSeq = null;
			
			if(pPrimeiro){
				final int vPhiSeqPrimeira = (Integer) CoreUtil.nvl(vo.getPhiPrimeira(), 0);
				if(vPhiSeqPrimeira > 0){
					vPhiSeq    = vPhiSeqPrimeira;
				} else {
					logInfo("Não encontrado phi para o tratamento tpt_Seq:"+rLista.getTipoTratamento().getSeq());
					throw new ApplicationBusinessException(FaturamentoAtualizaExamesPreTransplantesRNExceptionCode.MENSAGEM_TRATAMENTO_SEM_PHI,
																rLista.getTipoTratamento().getSeq());
				} 
				
			} else {
				final int vPhiSeqSegunda = (Integer) CoreUtil.nvl(vo.getPhiSegunda(), 0);
				
				if(vPhiSeqSegunda > 0){
					vPhiSeq    = vPhiSeqSegunda;
				} else {
					logInfo("Não encontrado phi de segundo para o tratamento tpt_Seq:"+rLista.getTipoTratamento().getSeq());
					throw new ApplicationBusinessException(FaturamentoAtualizaExamesPreTransplantesRNExceptionCode.MENSAGEM_TRATAMENTO_SEM_PHI_SEGUNDO, 
																rLista.getTipoTratamento().getSeq());
				}
			}
			
			final FatExmPacPreTrans eppt = new FatExmPacPreTrans();
			
			eppt.setFatListaPacApac(rLista);
			eppt.setDtRealizacao(vDataRealizacao);
			eppt.setPaciente(rLista.getPaciente());
			
			final FatProcedHospInternos procedimentoHospitalarInterno = fatProcedHospInternosDAO.obterPorChavePrimaria(vPhiSeq);
			eppt.setProcedimentoHospitalarInterno(procedimentoHospitalarInterno);
			
			fatExmPacPreTransRN.persistir(eppt);
		}
	}

	
	/**
	 * ORADB: OBTEM_SER_MATRICULA
	 */
	public RapServidores obterServidor( final FatProcedAmbRealizado ultimoFatProcedAmbRealizado,
										final Short vIphPhoSeq, final Integer vIphSeq, final Date vRealizacao,
										final FaturamentoFatkCapUniRN faturamentoFatkCapUniRN,
										final IRegistroColaboradorFacade registroColaboradorFacade,
										final ISolicitacaoExameFacade solicitacaoExameFacade) throws ApplicationBusinessException{
		
		
		logDebug(" vai ver o cbo ");
		final AelSolicitacaoExames ase = solicitacaoExameFacade.obterSolicitacaoExame(ultimoFatProcedAmbRealizado.getItemSolicitacaoExame().getId().getSoeSeq());
		final Integer conNumero = ase.getAtendimento().getConsulta().getNumero();
		
		RnCapcCboProcResVO rnCapcCboProcRes = faturamentoFatkCapUniRN.rnCapcCboProcRes(
													null, // regProcAmb.getMatriculaResp()
													null, // regProcAmb.getVinCodigoResp(),
													null, // regProcAmb.getIseSoeSeq(),
													null, // regProcAmb.getIseSeqp(),
													conNumero, 
													null, // regProcAmb.getPpcCrgSeq(),
													vIphPhoSeq, 
													vIphSeq, 
													vRealizacao,
													null,
													null);
		// Profissional não tem cbo
		if(("NTC").equalsIgnoreCase(rnCapcCboProcRes.getpErro())){
			logDebug(" NTC cbo ");

		// Profissional não tem cbo compatÃ­vel com o procedimento
		} else if(("INC").equalsIgnoreCase(rnCapcCboProcRes.getpErro())){
			logDebug(" INC cbo "+rnCapcCboProcRes.getpSerVinCodigo()+"/"+rnCapcCboProcRes.getpSerMatr());
			
		} else {
			logDebug("Prof:"+rnCapcCboProcRes.getpSerVinCodigo()+"/"+rnCapcCboProcRes.getpSerMatr());
		}
		
		RapServidores servidorResp = registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(rnCapcCboProcRes.getpSerMatr(), rnCapcCboProcRes.getpSerVinCodigo()));
		
		return servidorResp;
	}
	
	/**
	 * ORADB: F_PHI_SEQ
	 */
	private CursorTipoListaPmrKitPreVO buscaPhiSeq( final FatListaPacApac rLista, final Short cpgCphCspCnvCodigo,
													final Byte cpgCphCspSeq1, final Byte cpgCphCspSeq2,
													final FatTipoTratamentosDAO fatTipoTratamentosDAO){
		
		//  v_phi_seq_primeira            := f_phi_seq(r_lista.tpt_seq,v_phi_seq_segunda ,v_iph_pho_seq_primeira_cob ,v_iph_seq_primeira_cob ,v_iph_pho_seq_segunda_cob ,v_iph_seq_segunda_cob ,v_cid_seq );
		List<CursorTipoListaPmrKitPreVO> vos = fatTipoTratamentosDAO.obterCursorCTipoLista( cpgCphCspCnvCodigo, cpgCphCspSeq1, cpgCphCspSeq2,
																						    rLista.getTipoTratamento().getSeq(), true);

		if(!vos.isEmpty()){
			return vos.get(0);
		}
		
		return null;
	}
	
	
	/**
	 * ORADB: F_VERIFICA_EXAMES
	 */
	private FatProcedAmbRealizado verificaExames( final FatListaPacApac rLista, final FatCompetencia competencia,
									  final FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO,
									  final ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON, 
									  final String nomeMicrocomputador, 
									  final Date dataFimVinculoServidor) throws BaseException {
		
		List<FatProcedAmbRealizado> rPmr = fatProcedAmbRealizadoDAO.obterCursorCPmr( rLista.getPaciente().getCodigo(), 
																					 DominioSituacaoProcedimentoAmbulatorio.ABERTO, 
																					 competencia.getId().getDtHrInicio(), 
																					 competencia.getId().getMes(), 
																					 competencia.getId().getAno(), 
																					 rLista.getTipoTratamento().getSeq(), 
																					 true, DominioGrupoConvenio.S);
		FatProcedAmbRealizado ultimoFatProcedAmbRealizado = null;

		if(!rPmr.isEmpty()){
			logInfo("cancelados pmr, seq:"+rPmr.get(0).getSeq()+" phi_Seq:"+rPmr.get(0).getProcedimentoHospitalarInterno().getSeq()+
					" qtde cancelados:"+rPmr.size());
			
			for (FatProcedAmbRealizado fatProcedAmbRealizado : rPmr) {
				// Cancela PMR do vinculada ao exame
				cancelaPmr(fatProcedAmbRealizado, procedimentosAmbRealizadosON, nomeMicrocomputador, dataFimVinculoServidor);
				ultimoFatProcedAmbRealizado = fatProcedAmbRealizado;
			}
		}
		
		return ultimoFatProcedAmbRealizado; 
	}

	/**
	 * ORADB: CANCELA_PMR
	 */
	private void cancelaPmr(final FatProcedAmbRealizado fatProcedAmbRealizado, final ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON, String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException {
		final FatProcedAmbRealizado oldAmbRealizado = procedimentosAmbRealizadosON.clonarFatProcedAmbRealizado(fatProcedAmbRealizado);
		fatProcedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
		procedimentosAmbRealizadosON.atualizarProcedimentoAmbulatorialRealizado(fatProcedAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor, null);
	}
	
	
	/**
	 * ORADB: VERIFICA_PMR_CANCELADA
	 */
	private boolean verificaPmrCancelada( final Integer pPhiSeq, final Short iseSeqp, final Integer iseSoeSeqp, 
			   							  final String nomeMicrocomputador, 
			   							  final Date dataFimVinculoServidor, 
									      final FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO, 
									      final ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON
									   ) throws BaseException {
		boolean vRet = false;
		
		final List<FatProcedAmbRealizado> proceds = fatProcedAmbRealizadoDAO.
				buscarPorProcedHospInternosEItemSolicitacaoExameESituacao( iseSeqp, iseSoeSeqp, pPhiSeq, 
																		   DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
		if(!proceds.isEmpty()){
			vRet = true;
		    logInfo("Já existia pmr cancelada, soe_seq/seqp:"+iseSoeSeqp+"/"+iseSeqp+" phi_seq:"+pPhiSeq+" qtde atualizada:"+proceds.size());
		    
			for (FatProcedAmbRealizado fatProcedAmbRealizado : proceds) {
				final FatProcedAmbRealizado oldAmbRealizado = procedimentosAmbRealizadosON.clonarFatProcedAmbRealizado(fatProcedAmbRealizado);
				fatProcedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
				procedimentosAmbRealizadosON.atualizarProcedimentoAmbulatorialRealizado(fatProcedAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor, null);
			}
		}
		
		return vRet;
	}
	
	
	/**
	 * ORADB: F_REALIZACAO
	 */
	private Date[] obterDataRealizacaoELimite(int mes, int ano){
		Date vRealizacao = DateUtil.obterData(ano, (mes-1), 1); // -1 pois mês inicia em ZERO
		Date[] result = {vRealizacao, DateUtil.adicionaMeses(vRealizacao, -6)};
		return result;
	}

	protected FatListaPacApacDAO getFatListaPacApacDAO() {
		return fatListaPacApacDAO;
	}
	
	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}
	
	protected FatExmPacPreTransDAO getFatExmPacPreTransDAO() {
		return fatExmPacPreTransDAO;
	}
	
	protected FatTipoTratamentosDAO getFatTipoTratamentosDAO() {
		return fatTipoTratamentosDAO;
	}

	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}

	protected ProcedimentosAmbRealizadosON getProcedimentosAmbRealizadosON() {
		return procedimentosAmbRealizadosON;
	}
	
	protected FaturamentoFatkCapUniRN getFaturamentoFatkCapUniRN() {
		return faturamentoFatkCapUniRN;
	}
	
	protected FatExmPacPreTransRN getFatExmPacPreTransRN() {
		return fatExmPacPreTransRN;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return solicitacaoExameFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}