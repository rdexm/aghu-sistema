package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaDiagPrincipal;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagPrincipalDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaDiagPrincipalON extends BaseBusiness {


@EJB
private ManterAltaDiagPrincipalRN manterAltaDiagPrincipalRN;

private static final Log LOG = LogFactory.getLog(ManterAltaDiagPrincipalON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaDiagPrincipalDAO mpmAltaDiagPrincipalDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2993508915553824106L;

	/**
	 * Atualiza diag principal do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void versionarAltaDiagPrincipal(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws BaseException {
		
		MpmAltaDiagPrincipal altaDiagPrincipal = this.getAltaDiagPrincipalDAO().obterAltaDiagPrincipal(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		if (altaDiagPrincipal != null) {
			
			MpmAltaDiagPrincipal novoAltaDiagPrincipal = new MpmAltaDiagPrincipal();
			novoAltaDiagPrincipal.setAltaSumario(altaSumario);
			novoAltaDiagPrincipal.setCid(altaDiagPrincipal.getCid());
			novoAltaDiagPrincipal.setCidAtendimento(altaDiagPrincipal.getCidAtendimento());
			novoAltaDiagPrincipal.setComplCid(altaDiagPrincipal.getComplCid());
			novoAltaDiagPrincipal.setDescCid(altaDiagPrincipal.getDescCid());
			novoAltaDiagPrincipal.setDiagnostico(altaDiagPrincipal.getDiagnostico());
			novoAltaDiagPrincipal.setIndCarga(altaDiagPrincipal.getIndCarga());
			novoAltaDiagPrincipal.setIndSituacao(altaDiagPrincipal.getIndSituacao());
			this.getManterAltaDiagPrincipalRN().inserirAltaDiagPrincipal(novoAltaDiagPrincipal);
			
		}
		
	}
	
	/**
	 * Remove diag principal do sumário ativo
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaDiagPrincipal(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		altaSumario.setAltaDiagPrincipal(null);
		MpmAltaDiagPrincipal altaDiagPrincipal = this.getAltaDiagPrincipalDAO().obterAltaDiagPrincipal(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		
		if (altaDiagPrincipal != null) {
			
			this.getManterAltaDiagPrincipalRN().removerAltaDiagPrincipal(altaDiagPrincipal);
			
		}
		
	}
	
	protected MpmAltaDiagPrincipalDAO getAltaDiagPrincipalDAO() {
		return mpmAltaDiagPrincipalDAO;
	}
	
	protected ManterAltaDiagPrincipalRN getManterAltaDiagPrincipalRN() {
		return manterAltaDiagPrincipalRN;
	}
	

}
