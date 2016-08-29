package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoEtapaModPacDAO;
import br.gov.mec.aghu.model.ScoEtapaModPac;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoEtapaModPacRN extends BaseBusiness {

	private static final long serialVersionUID = -8765720654494687084L;

	private static final Log LOG = LogFactory.getLog(ScoEtapaModPacRN.class);

	@Inject
	private ScoEtapaModPacDAO scoEtapaModPacDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	public enum ScoEtapaModPacRNExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_PARAM_OBRIG, MENSAGE_ERRO_NUMERO_TEMPO_EXECUCAO_ETAPA_PAC; 
	}

	public void persistir(ScoEtapaModPac etapaModPac)
			throws ApplicationBusinessException {
		this.getScoEtapaModPacDAO().persistir(etapaModPac);
	}
	
	public void atualizar(ScoEtapaModPac etapaModPac)
			throws ApplicationBusinessException {

		if (etapaModPac == null) {
			throw new ApplicationBusinessException(ScoEtapaModPacRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoEtapaModPacDAO().atualizar(etapaModPac);
	}

	public void remover(ScoEtapaModPac tempoLocalizacaoPac)
			throws ApplicationBusinessException {

		if (tempoLocalizacaoPac == null) {
			throw new ApplicationBusinessException(ScoEtapaModPacRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		this.getScoEtapaModPacDAO().remover(tempoLocalizacaoPac);
		this.getScoEtapaModPacDAO().flush();
	}
	
	public void validaEtapasComTempoPrevistoExecucao(List<ScoEtapaModPac> etapas) throws ApplicationBusinessException{
		
		Boolean existeEtapaTempo = Boolean.FALSE;

		//Valida se existe etapa com tempo maior que zero
		for (ScoEtapaModPac etapaModPac : etapas) {
			if(etapaModPac.getNumeroDias() != null && etapaModPac.getNumeroDias().intValue() > 0){
				existeEtapaTempo = Boolean.TRUE;
			}
		}
		
		if(!existeEtapaTempo){
			throw new ApplicationBusinessException(ScoEtapaModPacRNExceptionCode.MENSAGE_ERRO_NUMERO_TEMPO_EXECUCAO_ETAPA_PAC);
		}
		
	}

	private ScoEtapaModPacDAO getScoEtapaModPacDAO() {
		return scoEtapaModPacDAO;
	}
}