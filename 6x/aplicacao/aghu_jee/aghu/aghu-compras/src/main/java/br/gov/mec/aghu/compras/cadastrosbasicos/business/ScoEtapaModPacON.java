package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.ScoEtapaModPac;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoEtapaModPacON extends BaseBusiness {

	private static final long serialVersionUID = -4315028891741261842L;

	private static final Log LOG = LogFactory.getLog(ScoEtapaModPacON.class);

	@EJB
	private ScoEtapaModPacRN scoEtapaModPacRN;

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	public enum ScoEtapaModPacONExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_PARAM_OBRIG; 
	}

	public void inserirEtapaModPac(ScoEtapaModPac etapaModPac)
			throws ApplicationBusinessException {

		if (etapaModPac == null){
			throw new ApplicationBusinessException(ScoEtapaModPacONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoEtapaModPacRN().persistir(etapaModPac);
	}
	
	public void alterarEtapaModPac(ScoEtapaModPac etapaModPac)
			throws ApplicationBusinessException {

		if (etapaModPac == null) {
			throw new ApplicationBusinessException(ScoEtapaModPacONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoEtapaModPacRN().atualizar(etapaModPac);
	}

	public void validaEtapasComTempoPrevistoExecucao(List<ScoEtapaModPac> etapas) throws ApplicationBusinessException{
		getScoEtapaModPacRN().validaEtapasComTempoPrevistoExecucao(etapas);
	}

	protected ScoEtapaModPacRN getScoEtapaModPacRN(){
		return scoEtapaModPacRN;
	}
}