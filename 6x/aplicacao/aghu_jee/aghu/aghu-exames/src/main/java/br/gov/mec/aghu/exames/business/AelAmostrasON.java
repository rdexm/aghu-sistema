package br.gov.mec.aghu.exames.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lsamberg
 * 
 */
@Stateless
public class AelAmostrasON extends BaseBusiness {


@EJB
private AelAmostrasRN aelAmostrasRN;

private static final Log LOG = LogFactory.getLog(AelAmostrasON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAmostrasDAO aelAmostrasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2025818928671028609L;
	
	public enum AelAmostrasONExceptionCode implements BusinessExceptionCode {

		AEL_01928,AEL_01924,AEL_01925,AEL_01926;

	}
	
	public void persistirAelAmostra(final AelAmostras aelAmostras,
			final Boolean flush) throws BaseException {
		if (aelAmostras.getId() != null) {
			atualizarAelAmostra(aelAmostras, flush);
		}
		// TODO: IMPLEMENTAR INSERT
	}
	
	/*
	 * P_CONSISTENCIA_COPIA 
	 */
	public void ajustarNumeroUnicoAelAmostra(final AelAmostras aelAmostraOrigem, final AelAmostras aelAmostraDestino) throws BaseException {
		final AelAmostras aelAmostraOrigemOld = this.getAelAmostrasDAO().obterOriginal(aelAmostraOrigem.getId());

		validarConsistenciaCopiaRN1(aelAmostraDestino, aelAmostraOrigemOld);
		getAelAmostrasRN().atualizarAelAmostra(aelAmostraDestino, true);
		
	}


	private void validarConsistenciaCopiaRN1(final AelAmostras aelAmostraDestino,
			final AelAmostras aelAmostraOrigem) throws ApplicationBusinessException {
		if(aelAmostraOrigem.getNroUnico() == null){
			/*A solicitação de origem deve ter um número único para fazer a cópia.*/
			throw new ApplicationBusinessException(AelAmostrasONExceptionCode.AEL_01928);
		}
		
		if(!aelAmostraOrigem.getMateriaisAnalises().getSeq().equals(aelAmostraDestino.getMateriaisAnalises().getSeq())){
			/*O material de análise correspondente a solicitação de origem é diferente do material de análise da solicitação destino para este número único.*/
			throw new ApplicationBusinessException(AelAmostrasONExceptionCode.AEL_01924);
		}
		
		if(!aelAmostraOrigem.getRecipienteColeta().getSeq().equals(aelAmostraDestino.getRecipienteColeta().getSeq())){
			/*O recipiente correspondente a solicitação de origem é diferente do recipiente da solicitação destino para este número único.*/
			throw new ApplicationBusinessException(AelAmostrasONExceptionCode.AEL_01925);
		}
		
		if(aelAmostraOrigem.getAnticoagulante() != null &&  !aelAmostraOrigem.getAnticoagulante().getSeq().equals(aelAmostraDestino.getAnticoagulante().getSeq())){
			/*O anticoagulante correspondente a solicitação de origem é diferente do anti-coagulante da solicitação destino para este número único.*/
			throw new ApplicationBusinessException(AelAmostrasONExceptionCode.AEL_01926);
		}
	}
	
	private void atualizarAelAmostra(final AelAmostras aelAmostras,
			final Boolean flush) throws BaseException {
		getAelAmostrasRN().atualizarAelAmostra(aelAmostras, flush);
	}

	protected AelAmostrasRN getAelAmostrasRN() {
		return aelAmostrasRN;
	}

	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}

}
