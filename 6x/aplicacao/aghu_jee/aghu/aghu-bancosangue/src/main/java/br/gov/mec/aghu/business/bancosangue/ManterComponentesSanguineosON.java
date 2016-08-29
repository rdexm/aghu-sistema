package br.gov.mec.aghu.business.bancosangue;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsComponenteSanguineoDAO;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterComponentesSanguineosON extends BaseBusiness
{
	
	@EJB
	private AbsComponenteSanguineoRN absComponenteSanguineoRN;
	
	private static final Log LOG = LogFactory.getLog(ManterComponentesSanguineosON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@Inject
	private AbsComponenteSanguineoDAO absComponenteSanguineoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -744446475880998410L;

	public enum ManterComponentesSanguineosONExceptionCode implements BusinessExceptionCode {
		ABS_00757 ,ABS_00768;
	}
	
	public void gravarRegistro(AbsComponenteSanguineo absComponenteSanguineo, Boolean edita) throws ApplicationBusinessException{
		getAbsComponenteSanguineoRN().persistir(absComponenteSanguineo, edita);
	}
	
	public void verificaInativo(AbsComponenteSanguineo absComponenteSanguineo) throws ApplicationBusinessException{
		absComponenteSanguineo = getAbsComponenteSanguineoDAO().obterOriginal(absComponenteSanguineo);
		if(!absComponenteSanguineo.getIndSituacao().isAtivo()){
			throw new ApplicationBusinessException(ManterComponentesSanguineosONExceptionCode.ABS_00757);
		}
	}
	
	public void verificaJustificativa(AbsComponenteSanguineo absComponenteSanguineo) throws ApplicationBusinessException{
		if(!absComponenteSanguineo.getIndJustificativa()){
			throw new ApplicationBusinessException(ManterComponentesSanguineosONExceptionCode.ABS_00768);
		}
	}
	
	protected AbsComponenteSanguineoRN getAbsComponenteSanguineoRN(){
		return absComponenteSanguineoRN;
	}
	
	protected AbsComponenteSanguineoDAO getAbsComponenteSanguineoDAO(){
		return absComponenteSanguineoDAO;
	}
	
}
