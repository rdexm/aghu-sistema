package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeSinCaractDefinidora;
import br.gov.mec.aghu.model.EpeSinCaractDefinidoraId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeSinCaractDefinidoraDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EpeSinCaractDefinidoraRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(EpeSinCaractDefinidoraRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpeSinCaractDefinidoraDAO epeSinCaractDefinidoraDAO;

	private static final long serialVersionUID = 2969601492841136788L;
	
	public enum EpeSinCaractDefinidoraRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SINONIMO_CARACTERISTICA_DEFINIDORA, EPE_00076, EPE_00075;
	}
	
	protected EpeSinCaractDefinidoraDAO getEpeSinCaractDefinidoraDAO() {
		return epeSinCaractDefinidoraDAO;
	}

	public void inserirEpeSinCaractDefinidora(EpeSinCaractDefinidora novaCaracteristicaDefinidora) throws ApplicationBusinessException {
		this.preInsert(novaCaracteristicaDefinidora);
		getEpeSinCaractDefinidoraDAO().persistir(novaCaracteristicaDefinidora);
	}
	
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB EPET_SCD_BRI (trigger)
	 * @ORADB EPET_SCD_BRU (trigger)
	 * @ORADB EPET_SCD_AGHU_BRU (trigger)
	 */
	private void preInsert(EpeSinCaractDefinidora novaCaracteristicaDefinidora) throws ApplicationBusinessException{
		
		if(novaCaracteristicaDefinidora.getId().getCdeCodigo().equals(novaCaracteristicaDefinidora.getId().getCdeCodigoPossui())){
			throw new ApplicationBusinessException(EpeSinCaractDefinidoraRNExceptionCode.MENSAGEM_SINONIMO_CARACTERISTICA_DEFINIDORA);
		}
		
		if(novaCaracteristicaDefinidora.getCaractDefinidoraByCdeCodigo().getSituacao() == null || 
				novaCaracteristicaDefinidora.getCaractDefinidoraByCdeCodigoPossui().getSituacao() == null){
			throw new ApplicationBusinessException(EpeSinCaractDefinidoraRNExceptionCode.EPE_00075);
		}
		
		if(!novaCaracteristicaDefinidora.getCaractDefinidoraByCdeCodigo().getSituacao().equals(DominioSituacao.A) || 
				!novaCaracteristicaDefinidora.getCaractDefinidoraByCdeCodigoPossui().getSituacao().equals(DominioSituacao.A)){
			throw new ApplicationBusinessException(EpeSinCaractDefinidoraRNExceptionCode.EPE_00076);
		}
		
	}
	
	public void removerEpeSinCaractDefinidora(EpeSinCaractDefinidoraId id){
		epeSinCaractDefinidoraDAO.removerPorId(id);
	}
	
}
