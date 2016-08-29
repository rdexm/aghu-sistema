package br.gov.mec.aghu.prescricaomedica.business;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaEvolucao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEvolucaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaEvolucaoON extends BaseBusiness {


@EJB
private ManterAltaEvolucaoRN manterAltaEvolucaoRN;

private static final Log LOG = LogFactory.getLog(ManterAltaEvolucaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaEvolucaoDAO mpmAltaEvolucaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6894257574523664197L;

	/**
	 * Atualiza alta evolução do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void versionarAltaEvolucao(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		MpmAltaEvolucao altaEvolucao = this.getAltaEvolucaoDAO().obterMpmAltaEvolucao(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		if (altaEvolucao != null) {
			
			MpmAltaEvolucao novoAltaEvolucao = new MpmAltaEvolucao();
			novoAltaEvolucao.setAltaSumario(altaSumario);
			novoAltaEvolucao.setDescricao(altaEvolucao.getDescricao());
			this.getManterAltaEvolucaoRN().inserirAltaEvolucao(novoAltaEvolucao);
			
		}
		
	}
	
	/**
	 * Remove alta evolução do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaEvolucao(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		altaSumario.setAltaEvolucao(null);
		MpmAltaEvolucao altaEvolucao = this.getAltaEvolucaoDAO().obterMpmAltaEvolucao(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		
		if (altaEvolucao != null) {
			
			this.getManterAltaEvolucaoRN().removerAltaEvolucao(altaEvolucao);
			
		}
		
	}

	protected MpmAltaEvolucaoDAO getAltaEvolucaoDAO() {
		return mpmAltaEvolucaoDAO;
	}
	
	protected ManterAltaEvolucaoRN getManterAltaEvolucaoRN() {
		return manterAltaEvolucaoRN;
	}
	
}
