package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaPlano;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPlanoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaPlanoON extends BaseBusiness {


@EJB
private ManterAltaPlanoRN manterAltaPlanoRN;

private static final Log LOG = LogFactory.getLog(ManterAltaPlanoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaPlanoDAO mpmAltaPlanoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2490424262765740393L;

	/**
	 * Cria uma cópia de MpmAltaPlano
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void versionarAltaPlano(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		MpmAltaPlano altaPlano = this.getMpmAltaPlanoDAO().obterMpmAltaPlano(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		if (altaPlano != null) {
			
			MpmAltaPlano novoAltaPlano = new MpmAltaPlano();
			novoAltaPlano.setAltaSumario(altaSumario);
			novoAltaPlano.setComplPlanoPosAlta(altaPlano.getComplPlanoPosAlta());
			novoAltaPlano.setDescPlanoPosAlta(altaPlano.getDescPlanoPosAlta());
			novoAltaPlano.setMpmPlanoPosAltas(altaPlano.getMpmPlanoPosAltas());
			this.getManterAltaPlanoRN().inserirAltaPlano(novoAltaPlano);
			
		}
		
	}
	
	/**
	 * Remove MpmAltaPlano
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void removerAltaPlano(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		altaSumario.setAltaPlanos(null);
		MpmAltaPlano altaPlano = this.getMpmAltaPlanoDAO().obterMpmAltaPlano(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		
		if (altaPlano != null) {
			
			this.getManterAltaPlanoRN().removerPlanoPosAlta(altaPlano);
			
		}
		
	}
	
	protected MpmAltaPlanoDAO getMpmAltaPlanoDAO() {
		return mpmAltaPlanoDAO;
	}
	
	protected ManterAltaPlanoRN getManterAltaPlanoRN() {
		return manterAltaPlanoRN;
	}

}
