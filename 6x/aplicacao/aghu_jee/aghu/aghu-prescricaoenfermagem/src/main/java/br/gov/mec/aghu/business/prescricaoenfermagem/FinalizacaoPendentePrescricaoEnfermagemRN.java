package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndPendentePrescricoesCuidados;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Regras para deixar prescrição de enfermagem como pendente
 * 
 * @ORADB EPEK_PENDENTE
 * 
 * @author tfelini
 */

@Stateless
public class FinalizacaoPendentePrescricaoEnfermagemRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(FinalizacaoPendentePrescricaoEnfermagemRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

@Inject
private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8494724800653019398L;

	public enum FinalizacaoPendentePrescricaoEnfermagemRNExceptionCode implements BusinessExceptionCode {
		EPE_00281;
		
		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
	/**
	 * atualizar os dados da prescrição de enfermagem para o status pendente
	 * 
	 * @ORADB EPEP_PENDENTE
	 *  
	 * @param prescricaoEnfermagem
	 * @throws BaseException 
	 */
	public void confirmarPendentePrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) throws BaseException{
	
		Date dthrUsado = new Date();
		if (prescricaoEnfermagem == null){
			FinalizacaoPendentePrescricaoEnfermagemRNExceptionCode.EPE_00281.throwException(); 
		}
		
		if (prescricaoEnfermagem.getDthrInicioMvtoPendente() == null){
			dthrUsado = prescricaoEnfermagem.getDthrMovimento();
		}else{
			dthrUsado = prescricaoEnfermagem.getDthrInicioMvtoPendente();			
		}		
		marcarCuidadosPendentes(prescricaoEnfermagem,dthrUsado);
		marcarPrescricaoEnfermagemPendente(prescricaoEnfermagem);
	}
	
	/** Esta procedure marca a prescricao de enfermagem como pendente
	 * 
	 * @ORADB EPEP_PENDENTE_PRCR
	 * 
	 * @param prescricaoEnfermagem
	 * @throws BaseException 
	 */
	private void marcarPrescricaoEnfermagemPendente(EpePrescricaoEnfermagem prescricaoEnfermagem) throws BaseException{
		EpePrescricaoEnfermagem prescricaoEnfermagemOld = getPrescricaoEnfermagemFacade().clonarPrescricaoEnfermagem(prescricaoEnfermagem);
		if (prescricaoEnfermagem.getSituacao().equals(DominioSituacaoPrescricao.U)){
			if (prescricaoEnfermagem.getDthrInicioMvtoPendente() == null ){
				prescricaoEnfermagem.setSituacao(DominioSituacaoPrescricao.L);
				prescricaoEnfermagem.setDthrInicioMvtoPendente(prescricaoEnfermagemOld.getDthrMovimento());
				prescricaoEnfermagem.setDthrMovimento(null);
			}else{
				prescricaoEnfermagem.setSituacao(DominioSituacaoPrescricao.L);
				prescricaoEnfermagem.setDthrMovimento(null);				
			}
		}
		getPrescricaoEnfermagemFacade().atualizarPrescricao(prescricaoEnfermagemOld, prescricaoEnfermagem, true);
	}

	/** Esta procedure marca a prescricao de cuidado como pendente
	 * 
	 * @ORADB EPEP_PENDENTE_CUID
	 * 
	 * @param prescricaoEnfermagem
	 * @param dthrAtual
	 * @throws BaseException 
	 */	
	private void marcarCuidadosPendentes(EpePrescricaoEnfermagem prescricaoEnfermagem, Date dthrAtual) throws BaseException{

		List<EpePrescricoesCuidados> prescricoesCuidados = this.getEpePrescricoesCuidadosDAO()
				.pesquisarCuidadosParaMarcarPendente(
						prescricaoEnfermagem.getId().getAtdSeq(),
						prescricaoEnfermagem.getId().getSeq(), dthrAtual,
						prescricaoEnfermagem.getDthrInicio(),
						prescricaoEnfermagem.getDthrFim());
		
		for (EpePrescricoesCuidados prescricaoCuidado : prescricoesCuidados){
			prescricaoCuidado.setPendente(DominioIndPendentePrescricoesCuidados.P);
			this.getEpePrescricoesCuidadosDAO().merge(prescricaoCuidado);
		}
		this.getEpePrescricoesCuidadosDAO().flush();
	}	
	
	protected IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade(){
		return prescricaoEnfermagemFacade;
	}	

	protected EpePrescricoesCuidadosDAO getEpePrescricoesCuidadosDAO(){
		return epePrescricoesCuidadosDAO; 
	}
}