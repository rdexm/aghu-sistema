package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaMotivo;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaMotivoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaMotivoON extends BaseBusiness {


@EJB
private ManterAltaMotivoRN manterAltaMotivoRN;

private static final Log LOG = LogFactory.getLog(ManterAltaMotivoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaMotivoDAO mpmAltaMotivoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3983977374071787201L;

	/**
	 * Atualiza alta motivo do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void versionarAltaMotivo(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		MpmAltaMotivo altaMotivo = this.getAltaMotivoDAO().obterMpmAltaMotivo(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp, Boolean.FALSE);
		
		if (altaMotivo != null) {
			
			MpmAltaMotivo novoAltaMotivo = new MpmAltaMotivo();
			novoAltaMotivo.setAltaSumario(altaSumario);
			novoAltaMotivo.setComplMotivo(altaMotivo.getComplMotivo());
			novoAltaMotivo.setDescMotivo(altaMotivo.getDescMotivo());
			novoAltaMotivo.setMotivoAltaMedicas(altaMotivo.getMotivoAltaMedicas());
			this.getManterAltaMotivoRN().inserirAltaMotivo(novoAltaMotivo);
			
		}
	}
	
	/**
	 * Remove alta motivo do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaMotivo(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		altaSumario.setAltaMotivos(null);
		MpmAltaMotivo altaMotivo = this.getAltaMotivoDAO().obterMpmAltaMotivo(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp(), null);
		
		if (altaMotivo != null) {
			
			this.getManterAltaMotivoRN().removerAltaMotivo(altaMotivo);
			
		}
		
	}
	
	protected MpmAltaMotivoDAO getAltaMotivoDAO() {
		return mpmAltaMotivoDAO;
	}
	
	protected ManterAltaMotivoRN getManterAltaMotivoRN() {
		return manterAltaMotivoRN;
	}

}
