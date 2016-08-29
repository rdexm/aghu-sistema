 package br.gov.mec.aghu.prescricaomedica.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaOutraEquipeSumr;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaOutraEquipeSumrDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterAltaOutraEquipeSumrRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaOutraEquipeSumrRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaOutraEquipeSumrDAO mpmAltaOutraEquipeSumrDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 852616549405957143L;

	public enum ManterAltaOutraEquipeSumrExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR_ALTA_OUTRA_EQUIPE_SUMR;

		public void throwException(Throwable cause, Object... params)
				throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}

	/**
	 * Insere objeto MpmAltaOutraEquipeSumr.
	 * 
	 * @param {MpmAltaOutraEquipeSumr} altaOutraEquipeSumr
	 * @throws ApplicationBusinessException
	 */
	public void inserirAltaOutraEquipeSumr(MpmAltaOutraEquipeSumr altaOutraEquipeSumr) throws ApplicationBusinessException {

		try {
			
			this.preInserirAltaOutraEquipeSumr(altaOutraEquipeSumr);
			this.getAltaOutraEquipeSumrDAO().persistir(altaOutraEquipeSumr);
			this.getAltaOutraEquipeSumrDAO().flush();

		} catch (ApplicationBusinessException e) {

			logError(e.getMessage(), e);	
			ManterAltaOutraEquipeSumrExceptionCode.ERRO_INSERIR_ALTA_OUTRA_EQUIPE_SUMR
					.throwException(e);

		}

	}

	/**
	 * ORADB Trigger MPMT_OES_BRI
	 * 
	 * @param {MpmAltaOutraEquipeSumr} altaOutraEquipeSumr
	 * @throws ApplicationBusinessException
	 */
	protected void preInserirAltaOutraEquipeSumr(
			MpmAltaOutraEquipeSumr altaOutraEquipeSumr) throws ApplicationBusinessException {

		getAltaSumarioRN().verificarAltaSumarioAtivo(
				altaOutraEquipeSumr.getMpmAltaSumarios());

	}
	
	/**
	 * Remove objeto MpmAltaOutraEquipeSumr.
	 * 
	 * @param {MpmAltaOutraEquipeSumr} altaOutraEquipeSumr
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaOutraEquipeSumr(MpmAltaOutraEquipeSumr altaOutraEquipeSumr) throws ApplicationBusinessException {

		this.preRemoverAltaOutraEquipeSumr(altaOutraEquipeSumr);
		this.getAltaOutraEquipeSumrDAO().remover(altaOutraEquipeSumr);
		this.getAltaOutraEquipeSumrDAO().flush();
		
	}
	
	/**
	 * ORADB Trigger MPMT_OES_BRD
	 * 
	 * @param {MpmAltaOutraEquipeSumr} altaOutraEquipeSumr
	 * @throws ApplicationBusinessException
	 */
	protected void preRemoverAltaOutraEquipeSumr(MpmAltaOutraEquipeSumr altaOutraEquipeSumr) throws ApplicationBusinessException {

		getAltaSumarioRN().verificarAltaSumarioAtivo(altaOutraEquipeSumr.getMpmAltaSumarios());

	}
	
	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}
	
	protected MpmAltaOutraEquipeSumrDAO getAltaOutraEquipeSumrDAO() {
		return mpmAltaOutraEquipeSumrDAO;
	}

}
