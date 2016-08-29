package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoJobDetail;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.FatVlrItemProcedHospCompsId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class ValorItemProcedHospCompsRN extends BaseBusiness implements Serializable {

	@EJB
	private ValorItemProcedHospCompsON valorItemProcedHospCompsON;
	
	private static final Log LOG = LogFactory.getLog(ValorItemProcedHospCompsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ISchedulerFacade schedulerFacade; 
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@Inject
	private FatVlrItemProcedHospCompsDAO fatVlrItemProcedHospCompsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3634784387306648954L;

	public static final String FATK_IPC_RN_V_IPC_INCLUI = "FATK_IPC_RN_V_IPC_INCLUI";

	private enum ValorItemProcedHospCompsExceptionCode implements
	BusinessExceptionCode {
		FAT_00416, FAT_00417
	}

	
	/** ORADB: FATK_IPC_RN.RN_IPCP_LIGA_CARGA
	 * @throws BaseException 
	 * 
	 */
	public void ligaCarga(Boolean ligar) throws BaseException {
		RapServidores serv = servidorLogadoFacade.obterServidorLogado();
		
		
		AghJobDetail job;
		
		if(ligar){
			job = new AghJobDetail(FATK_IPC_RN_V_IPC_INCLUI +'_'+serv.getUsuario(), new Date(), serv, DominioSituacaoJobDetail.E);
			schedulerFacade.persistirJobControleSimples(job);
		} else {
			job = schedulerFacade.obterAghJobDetailPorNome(FATK_IPC_RN_V_IPC_INCLUI +'_'+serv.getUsuario());
			schedulerFacade.removerAghJobDetail(job, Boolean.FALSE);
		}
		
//		if(ligar) {
//			atribuirContextoSessao(
//					VariaveisSessaoEnum.FATK_IPC_RN_V_IPC_INCLUI,
//					Boolean.TRUE);
//		}
//		else {
//			atribuirContextoSessao(
//					VariaveisSessaoEnum.FATK_IPC_RN_V_IPC_INCLUI,
//					Boolean.FALSE);			
//		}
	}
	
	
	/** ORADB: FATK_IPC_RN.RN_IPCP_ATU_VLR_PESP
	 * 
	 */
	public void atualizarVlrResp(FatVlrItemProcedHospComps fatVlrItemProcedHospComps, DominioOperacoesJournal operacao) throws BaseException {
		Long quantidade = Long.valueOf(getFatVlrItemProcedHospCompsDAO().obterQuantidadeValorItemProcHospSemDataFimPorIphSeqEIphPhoSeq(fatVlrItemProcedHospComps.getId().getIphSeq(), fatVlrItemProcedHospComps.getId().getIphPhoSeq()));
		Boolean isProcedimentoEspecial = getFatItensProcedHospitalarDAO().isProcedimentoEspecialPorSeqEPhoSeq(fatVlrItemProcedHospComps.getId().getIphPhoSeq(), fatVlrItemProcedHospComps.getId().getIphSeq());
		isProcedimentoEspecial = (isProcedimentoEspecial == null)?false:isProcedimentoEspecial;
		
		if(DominioOperacoesJournal.INS.equals(operacao)) {
			if(fatVlrItemProcedHospComps.getDtFimCompetencia() == null) {
				if(quantidade > 0) {
					// em virtude da tabela mutante after insert registro já existe
					quantidade--;
				}
				if(quantidade > 0) {
		            // Item_Proced_Hospitalar pode ter somente um registro de valores
		            // sem data de fim de competência em Vlr_Item_Proced_Hosp_Comp
					throw new ApplicationBusinessException(ValorItemProcedHospCompsExceptionCode.FAT_00417);
				}
			}
			else {
				if(quantidade <= 0 && isProcedimentoEspecial) {
		            // Procedimento especial necessita de registro de valores sem
		            //  data de fim de competência em Vlr_Item_Proced_Hosp_Comp
					throw new ApplicationBusinessException(ValorItemProcedHospCompsExceptionCode.FAT_00416);
				}
			}
		} else if(DominioOperacoesJournal.UPD.equals(operacao)) {
			// verifica se pode ficar com/sem data fim e com/sem valores
			if(fatVlrItemProcedHospComps.getDtFimCompetencia() == null) {
				if(quantidade > 0) {
					// em virtude da tabela mutante after insert registro já existe
					quantidade--;
				}
				if(quantidade > 0) {
		             // Item_Proced_Hospitalar pode ter somente um registro de valores
		             // sem data de fim de competência em Vlr_Item_Proced_Hosp_Comp
					throw new ApplicationBusinessException(ValorItemProcedHospCompsExceptionCode.FAT_00417);
				}
			} else {
				if (quantidade == 0 && isProcedimentoEspecial){
					final RapServidores serv = servidorLogadoFacade.obterServidorLogado();
					final AghJobDetail job = schedulerFacade.obterAghJobDetailPorNome(FATK_IPC_RN_V_IPC_INCLUI +'_'+serv.getUsuario());
					
					if(job == null ){  //&& !((Boolean) obterContextoSessao("FATK_IPC_RN_V_IPC_INCLUI"))) {
						Calendar dtFim = Calendar.getInstance();
						dtFim.setTime(fatVlrItemProcedHospComps.getDtFimCompetencia());
						dtFim.add(Calendar.DATE, 1);
						//INSERT
						FatVlrItemProcedHospComps vlrItemProcedHospComps = new FatVlrItemProcedHospComps();
						vlrItemProcedHospComps.setId(new FatVlrItemProcedHospCompsId(fatVlrItemProcedHospComps.getId().getIphPhoSeq(), fatVlrItemProcedHospComps.getId().getIphSeq(), dtFim.getTime()));
						vlrItemProcedHospComps.setVlrServHospitalar(fatVlrItemProcedHospComps.getVlrServHospitalar());
						vlrItemProcedHospComps.setVlrServProfissional(fatVlrItemProcedHospComps.getVlrServProfissional());
						vlrItemProcedHospComps.setVlrSadt(fatVlrItemProcedHospComps.getVlrSadt());
						vlrItemProcedHospComps.setVlrProcedimento(fatVlrItemProcedHospComps.getVlrProcedimento());
						vlrItemProcedHospComps.setVlrAnestesista(fatVlrItemProcedHospComps.getVlrAnestesista());
						
						getValorItemProcedHospCompsON().persistirVlrItemProcedHospComps(vlrItemProcedHospComps, DominioOperacoesJournal.INS);
					}
				}
			}
		}
	}
	
	/** ORADB: FATT_IPC_BRI ON FAT_VLR_ITEM_PROCED_HOSP_COMPS
	 * 
	 * @param itemProcedHospitalar
	 */
	public void executarAntesDeInserirVlrItemProcedHospComps(FatVlrItemProcedHospComps vlrItemProcedHospComps){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		vlrItemProcedHospComps.setCriadoEm(new Date());
		vlrItemProcedHospComps.setAlteradoEm(new Date());
		vlrItemProcedHospComps.setCriadoPor(servidorLogado.getUsuario());
		vlrItemProcedHospComps.setAlteradoPor(servidorLogado.getUsuario());
	}
	
	/** ORADB: FATT_IPC_BRU ON FAT_VLR_ITEM_PROCED_HOSP_COMPS
	 * 
	 * @param itemProcedHospitalar
	 */
	public void executarAntesDeAtualizarVlrItemProcedHospComps(FatVlrItemProcedHospComps vlrItemProcedHospComps){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		vlrItemProcedHospComps.setAlteradoEm(new Date());
		vlrItemProcedHospComps.setAlteradoPor(servidorLogado.getUsuario());
	}

	/** ORADB: FATT_IPC_ASU ON FAT_VLR_ITEM_PROCED_HOSP_COMPS
	 * 
	 * @param itemProcedHospitalar
	 */
	public void executarStatementAposAtualizarVlrItemProcedHospComps(FatVlrItemProcedHospComps vlrItemProcedHospComps) throws BaseException {
		
		this.executarProcessVlrItemProcedHospComps(vlrItemProcedHospComps, DominioOperacoesJournal.UPD);
	}

	/** ORADB: FATT_IPC_ASI ON FAT_VLR_ITEM_PROCED_HOSP_COMPS
	 * 
	 * @param itemProcedHospitalar
	 */
	public void executarStatementAposInserirVlrItemProcedHospComps(FatVlrItemProcedHospComps vlrItemProcedHospComps) throws BaseException {
		
		this.executarProcessVlrItemProcedHospComps(vlrItemProcedHospComps, DominioOperacoesJournal.INS);
	}

	/**
	 * ORADB FATK_IPC.PROCESS_IPC_ROWS
	 * @param vlrItemProcedHospComps
	 * @param operacao
	 */
	protected void executarProcessVlrItemProcedHospComps(FatVlrItemProcedHospComps vlrItemProcedHospComps, DominioOperacoesJournal operacao) throws BaseException {
		if(!DominioOperacoesJournal.DEL.equals(operacao)){
			this.executarEnforceVlrItemProcedHospComps(vlrItemProcedHospComps, operacao);
		}
	}

	/**
	 * ORADB FATP_ENFORCE_IPC_RULES
	 * @param vlrItemProcedHospComps
	 * @param operacao
	 */
	protected void executarEnforceVlrItemProcedHospComps(FatVlrItemProcedHospComps vlrItemProcedHospComps, DominioOperacoesJournal operacao) throws BaseException {
		//verifica proced.especial, data fim, valores
		this.atualizarVlrResp(vlrItemProcedHospComps, operacao);		
	}
	
	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}
	
	protected FatVlrItemProcedHospCompsDAO getFatVlrItemProcedHospCompsDAO() {
		return fatVlrItemProcedHospCompsDAO;
	}
	
	protected ValorItemProcedHospCompsON getValorItemProcedHospCompsON() {
		return valorItemProcedHospCompsON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}


	public ISchedulerFacade getSchedulerFacade() {
		return schedulerFacade;
	}
	
}
