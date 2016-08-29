package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatCandidatosApacOtorrinoDAO;
import br.gov.mec.aghu.model.FatCandidatosApacOtorrino;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FatCandidatosApacOtorrinoON extends BaseBusiness {


@EJB
private FatCandidatosApacOtorrinoRN fatCandidatosApacOtorrinoRN;

private static final Log LOG = LogFactory.getLog(FatCandidatosApacOtorrinoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatCandidatosApacOtorrinoDAO fatCandidatosApacOtorrinoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -780295309705716361L;

	/**
	 * Método para persistir Candidatos Apac Otorrino
	 * @param candidatosApacOtorrino
	 * @throws ApplicationBusinessException  
	 */
	public void persistirCandidatosApacOtorrino(FatCandidatosApacOtorrino candidatosApacOtorrino, Boolean flush, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		if (candidatosApacOtorrino.getSeq() == null) {
			//TODO implementar trigger before insert => FATT_CAOT_BRI, estava fora do nosso escopo
			getFatCandidatosApacOtorrinoDAO().persistir(candidatosApacOtorrino);
			if (flush){
				getFatCandidatosApacOtorrinoDAO().flush();
			}
		}
		else {
			getFatCandidatosApacOtorrinoRN().executarAntesAtualizarFatCandidatosApacOtorrino(candidatosApacOtorrino, dataFimVinculoServidor);
			getFatCandidatosApacOtorrinoDAO().atualizar(candidatosApacOtorrino);
			if (flush){
				getFatCandidatosApacOtorrinoDAO().flush();
			}
		}
	} 
	
	
	protected FatCandidatosApacOtorrinoDAO getFatCandidatosApacOtorrinoDAO() {
		return fatCandidatosApacOtorrinoDAO;
	}
	
	protected FatCandidatosApacOtorrinoRN getFatCandidatosApacOtorrinoRN() {
		return fatCandidatosApacOtorrinoRN;
	}
}
