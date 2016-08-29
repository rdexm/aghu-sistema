package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaComplFarmaco;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaComplFarmacoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaComplFarmacoON extends BaseBusiness {


@EJB
private ManterAltaComplFarmacoRN manterAltaComplFarmacoRN;

private static final Log LOG = LogFactory.getLog(ManterAltaComplFarmacoON.class);

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
	private static final long serialVersionUID = -2136657263597597613L;

	/**
	 * Atualiza informacoes complementares dos farmacos
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param novoSeqp
	 * @throws ApplicationBusinessException
	 */
	public void versionarAltaComplFarmaco(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		MpmAltaComplFarmaco altaComplFarmaco = this.getAltaComplFarmacoDAO().obterMpmAltaComplFarmaco(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		if (altaComplFarmaco != null) {
			
			MpmAltaComplFarmaco novoAltaComplFarmaco = new MpmAltaComplFarmaco();
			novoAltaComplFarmaco.setAltaSumario(altaSumario);
			novoAltaComplFarmaco.setDescricao(altaComplFarmaco.getDescricao());
			this.getManterAltaComplFarmacoRN().inserirAltaComplFarmaco(novoAltaComplFarmaco);
			
		}
		
	}
	
	/**
	 * Remove informacoes complementares dos farmacos
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param novoSeqp
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaComplFarmaco(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		MpmAltaComplFarmaco altaComplFarmaco = this.getAltaComplFarmacoDAO().obterMpmAltaComplFarmaco(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		
		if (altaComplFarmaco != null) {
			
			this.getManterAltaComplFarmacoRN().removerAltaComplFarmaco(altaComplFarmaco);
			
		}
		
	}

	protected MpmAltaComplFarmacoDAO getAltaComplFarmacoDAO() {
		return mpmAltaComplFarmacoDAO;
	}
	
	protected ManterAltaComplFarmacoRN getManterAltaComplFarmacoRN() {
		return manterAltaComplFarmacoRN;
	}
}
