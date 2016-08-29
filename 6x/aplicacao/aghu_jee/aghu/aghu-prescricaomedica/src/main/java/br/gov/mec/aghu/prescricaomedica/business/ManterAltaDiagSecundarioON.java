package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaDiagSecundario;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagSecundarioDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterAltaDiagSecundarioON extends BaseBusiness {


@EJB
private ManterAltaDiagSecundarioRN manterAltaDiagSecundarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaDiagSecundarioON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaDiagSecundarioDAO mpmAltaDiagSecundarioDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 907059193727141713L;

	/**
	 * Atualiza diagnóstico secundário do sumario ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void versionarMpmAltaDiagSecundario(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		List<MpmAltaDiagSecundario> retorno = this.getMpmAltaDiagSecundarioDAO().obterAltaDiagSecundario(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		for (MpmAltaDiagSecundario altaDiagSecundario : retorno) {
			
			MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
			novoAltaDiagSecundario.setMpmAltaSumarios(altaSumario);
			novoAltaDiagSecundario.setCidSeq(altaDiagSecundario.getCidSeq());
			novoAltaDiagSecundario.setComplCid(altaDiagSecundario.getComplCid());
			novoAltaDiagSecundario.setDescCid(altaDiagSecundario.getDescCid());
			novoAltaDiagSecundario.setDiaSeq(altaDiagSecundario.getDiaSeq());
			novoAltaDiagSecundario.setIndCarga(altaDiagSecundario.getIndCarga());
			novoAltaDiagSecundario.setIndSituacao(altaDiagSecundario.getIndSituacao());
			novoAltaDiagSecundario.setMpmCidAtendimentos(altaDiagSecundario.getMpmCidAtendimentos());
			this.getManterAltaDiagSecundarioRN().inserirAltaDiagSecundario(novoAltaDiagSecundario);
		}
		
	}
	
	/**
	 * Remove diagnóstico secundário do sumario.
	 * 
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	public void removerMpmAltaDiagSecundario(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		List<MpmAltaDiagSecundario> retorno = this.getMpmAltaDiagSecundarioDAO().obterAltaDiagSecundario(
				altaSumario.getId().getApaAtdSeq(), 
				altaSumario.getId().getApaSeq(), 
				altaSumario.getId().getSeqp(),
				false
		);
		
		for (MpmAltaDiagSecundario altaDiagSecundario : retorno) {
			this.getManterAltaDiagSecundarioRN().removerAltaDiagSecundario(altaDiagSecundario);
		}
	}
	
	protected ManterAltaDiagSecundarioRN getManterAltaDiagSecundarioRN() {
		return manterAltaDiagSecundarioRN;
	}
	
	protected MpmAltaDiagSecundarioDAO getMpmAltaDiagSecundarioDAO() {
		return mpmAltaDiagSecundarioDAO;
	}
	
}
