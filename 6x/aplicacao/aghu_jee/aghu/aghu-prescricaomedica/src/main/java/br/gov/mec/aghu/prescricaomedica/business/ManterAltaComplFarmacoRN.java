package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaComplFarmaco;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaComplFarmacoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author bsoliveira
 *
 */
@Stateless
public class ManterAltaComplFarmacoRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaComplFarmacoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaComplFarmacoDAO mpmAltaComplFarmacoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3810443296232982607L;

	/**
	 * Insere objeto MpmAltaComplFarmaco.
	 * 
	 * @param {MpmAltaComplFarmaco} altaComplFarmaco
	 * @throws ApplicationBusinessException
	 */
	public void inserirAltaComplFarmaco(
			MpmAltaComplFarmaco altaComplFarmaco)
			throws ApplicationBusinessException {
	
			this.preInserirAltaComplFarmaco(altaComplFarmaco);
			this.getAltaComplFarmacoDAO().persistir(altaComplFarmaco);
			this.getAltaComplFarmacoDAO().flush();

	}
	
	/**
	 * Insere objeto MpmAltaComplFarmaco.
	 * 
	 * @param {MpmAltaComplFarmaco} altaComplFarmaco
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAltaComplFarmaco(
			MpmAltaComplFarmaco altaComplFarmaco)
			throws ApplicationBusinessException {

			this.preAtualizaraltaComplFarmaco(altaComplFarmaco);
			this.getAltaComplFarmacoDAO().atualizar(altaComplFarmaco);
			this.getAltaComplFarmacoDAO().flush();
	}
	
	/**
	 * Remove objeto MpmAltaComplFarmaco.
	 * 
	 * @param {MpmAltaComplFarmaco} altaComplFarmaco
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaComplFarmaco(MpmAltaComplFarmaco altaComplFarmaco)throws ApplicationBusinessException {

		this.preRemoverAltaComplFarmaco(altaComplFarmaco);
		this.getAltaComplFarmacoDAO().remover(altaComplFarmaco);
		this.getAltaComplFarmacoDAO().flush();
		
	}
	
	/**
	 * ORADB Trigger MPMT_CFA_BRI
	 * 
	 * @param {MpmAltaComplFarmaco} altaComplFarmaco
	 * @throws ApplicationBusinessException
	 */
	protected void preInserirAltaComplFarmaco(
			MpmAltaComplFarmaco altaComplFarmaco)
			throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaComplFarmaco.getAltaSumario());

	}
	
	/**
	 * ORADB Trigger MPMT_CFA_BRI
	 * 
	 * @param {MpmAltaComplFarmaco} altaComplFarmaco
	 * @throws ApplicationBusinessException
	 */
	protected void preAtualizaraltaComplFarmaco(
		MpmAltaComplFarmaco altaComplFarmaco) throws ApplicationBusinessException {
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaComplFarmaco.getAltaSumario());
	}
	
	/**
	 * ORADB Trigger MPMT_CFA_BRD
	 * 
	 * @param {MpmAltaComplFarmaco} altaComplFarmaco
	 * @throws ApplicationBusinessException
	 */
	protected void preRemoverAltaComplFarmaco(MpmAltaComplFarmaco altaComplFarmaco) throws ApplicationBusinessException {
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaComplFarmaco.getAltaSumario());
	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaComplFarmacoDAO getAltaComplFarmacoDAO() {
		return mpmAltaComplFarmacoDAO;
	}

}
