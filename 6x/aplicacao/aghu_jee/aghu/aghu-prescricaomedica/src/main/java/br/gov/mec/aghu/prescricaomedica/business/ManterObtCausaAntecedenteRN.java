package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObtCausaAntecedente;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaAntecedenteDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterObtCausaAntecedenteRN extends BaseBusiness {


@EJB
private ManterObtCausaDiretaRN manterObtCausaDiretaRN;

@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterObtCausaAntecedenteRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmObtCausaAntecedenteDAO mpmObtCausaAntecedenteDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3434954053825943420L;

	/**
	 * TRIGGER MPMT_OCN_BRI, MPMT_OCN_BASE_BRI
	 * @param obtCausaAntecedente
	 */
	public void inserirObtCausaAntecedente(MpmObtCausaAntecedente obtCausaAntecedente) throws ApplicationBusinessException {
		
		this.preInserirObtCausaAntecedente(obtCausaAntecedente);
		this.getMpmObtCausaAntecedenteDAO().persistir(obtCausaAntecedente);
		this.getMpmObtCausaAntecedenteDAO().flush();
		
	}
	
	protected void preInserirObtCausaAntecedente(MpmObtCausaAntecedente obtCausaAntecedente) throws ApplicationBusinessException {
		
		if (obtCausaAntecedente.getMpmCidAtendimentos() != null) {
			this.getAltaSumarioRN().verificarCidAtendimento(obtCausaAntecedente.getMpmCidAtendimentos().getSeq());
		}
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(obtCausaAntecedente.getMpmAltaSumarios());
		this.getManterObtCausaDiretaRN().verificarTipoAltaSumario(obtCausaAntecedente.getMpmAltaSumarios());
		
	}
	
	/**
	 * TRIGGER MPMT_OCD_BRD
	 * @param obtCausaAntecedente
	 */
	public void removerObtCausaAntecedente(MpmObtCausaAntecedente obtCausaAntecedente) throws ApplicationBusinessException {
		
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(obtCausaAntecedente.getMpmAltaSumarios());
		this.getMpmObtCausaAntecedenteDAO().remover(obtCausaAntecedente);
		this.getMpmObtCausaAntecedenteDAO().flush();
		
	}
	
	public boolean validarAoMenosUmaCausaAntecedente(
			MpmAltaSumarioId altaSumarioId) throws ApplicationBusinessException {
		List<MpmObtCausaAntecedente> result = this.getMpmObtCausaAntecedenteDAO().obterMpmObtCausaAntecedente(altaSumarioId.getApaAtdSeq(), altaSumarioId.getApaSeq(), altaSumarioId.getSeqp());

		return !result.isEmpty();
	}
	
	protected ManterObtCausaDiretaRN getManterObtCausaDiretaRN() {
		return manterObtCausaDiretaRN;
	}
	
	protected MpmObtCausaAntecedenteDAO getMpmObtCausaAntecedenteDAO() {
		return mpmObtCausaAntecedenteDAO;
	}
	
	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}
	
	
}
