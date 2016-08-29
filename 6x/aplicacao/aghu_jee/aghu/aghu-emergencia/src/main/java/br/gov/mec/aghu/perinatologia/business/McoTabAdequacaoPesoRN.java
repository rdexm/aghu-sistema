
package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.McoTabAdequacaoPeso;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.perinatologia.dao.McoTabAdequacaoPesoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * @author Rafael Garcia
 */
@Stateless
public class McoTabAdequacaoPesoRN extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private McoTabAdequacaoPesoDAO mcoTabAdequacaoPesoDAO;
	
	public enum McoTabAdequacaoPesoRNExceptionCode implements BusinessExceptionCode {
		MCO_00502, MCO_00503, MCO_00504, ;
    }
				
	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void pesistirMcoTabAdequacaoPeso(McoTabAdequacaoPeso tabAdequacaoPeso) throws ApplicationBusinessException{
		if(tabAdequacaoPeso.getSeq() == null){
			validarAdequacaoPeso(tabAdequacaoPeso);
			preInserir(tabAdequacaoPeso);
			mcoTabAdequacaoPesoDAO.persistir(tabAdequacaoPeso);			
		}else{
			validarAdequacaoPeso(tabAdequacaoPeso);
			preAtualizar(tabAdequacaoPeso);
			mcoTabAdequacaoPesoDAO.merge(tabAdequacaoPeso);
		}
	}
	
	//@ORADB MCOT_TAD_BRI
	private void preInserir(McoTabAdequacaoPeso tabAdequacaoPeso){
		tabAdequacaoPeso.setCriadoEm(new Date());
		tabAdequacaoPeso.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		
	}
	
	//@ORADB MCOT_TAD_BRU
	private void preAtualizar(McoTabAdequacaoPeso tabAdequacaoPeso){
		tabAdequacaoPeso.setRapServidores(servidorLogadoFacade.obterServidorLogado());
	}
	
	private void validarAdequacaoPeso(McoTabAdequacaoPeso tabAdequacaoPeso) throws ApplicationBusinessException{
		if(!((tabAdequacaoPeso.getPercentil3() != null && tabAdequacaoPeso.getPercentil10() == null && tabAdequacaoPeso.getPercentil90() == null)
				|| (tabAdequacaoPeso.getPercentil3() == null && tabAdequacaoPeso.getPercentil10() != null && tabAdequacaoPeso.getPercentil90() == null)
				|| (tabAdequacaoPeso.getPercentil3() == null && tabAdequacaoPeso.getPercentil10() == null && tabAdequacaoPeso.getPercentil90() != null)
				|| (tabAdequacaoPeso.getPercentil3() == null && tabAdequacaoPeso.getPercentil10() != null && tabAdequacaoPeso.getPercentil90() != null)
				|| (tabAdequacaoPeso.getPercentil3() != null && tabAdequacaoPeso.getPercentil10() == null && tabAdequacaoPeso.getPercentil90() != null)
				|| (tabAdequacaoPeso.getPercentil3() != null && tabAdequacaoPeso.getPercentil10() != null && tabAdequacaoPeso.getPercentil90() == null)
				|| (tabAdequacaoPeso.getPercentil3() != null && tabAdequacaoPeso.getPercentil10() != null && tabAdequacaoPeso.getPercentil90() != null))){
			throw new ApplicationBusinessException(McoTabAdequacaoPesoRNExceptionCode.MCO_00503);
		}

		validarIdadeGestacional(tabAdequacaoPeso);
		
		if(tabAdequacaoPeso.getIgSemanas() < 1 || tabAdequacaoPeso.getIgSemanas() > 44){
			throw new ApplicationBusinessException(McoTabAdequacaoPesoRNExceptionCode.MCO_00502);
		}
	}
	
	public void validarIdadeGestacional(McoTabAdequacaoPeso tabAdequacaoPeso) throws ApplicationBusinessException {
		Long count = mcoTabAdequacaoPesoDAO.pesquisarCapacidadesAdequacaoPesoIgSemanasCount(tabAdequacaoPeso.getIgSemanas());
		if (tabAdequacaoPeso.getSeq() == null) {
			if ((count != null && count > 0)) {
				throw new ApplicationBusinessException(McoTabAdequacaoPesoRNExceptionCode.MCO_00504);
			}
		} else {
			McoTabAdequacaoPeso mcoTabAdequacaoPesoOriginal = mcoTabAdequacaoPesoDAO.obterOriginal(tabAdequacaoPeso.getSeq());
			
			if ((count != null && count > 0) && !tabAdequacaoPeso.getIgSemanas().equals(mcoTabAdequacaoPesoOriginal.getIgSemanas())) {
				throw new ApplicationBusinessException(McoTabAdequacaoPesoRNExceptionCode.MCO_00504);
			}			
		}
	}
	
	public void excluirAdequacaoPeso(Short seq){
		McoTabAdequacaoPeso tabAdequacaopeso = this.mcoTabAdequacaoPesoDAO.obterPorChavePrimaria(seq);
		
		this.mcoTabAdequacaoPesoDAO.remover(tabAdequacaopeso);
	}

}
