package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class EncerramentoContaHospitalarRN extends BaseBMTBusiness implements Serializable {

	private static final long serialVersionUID = 359413284572631541L;
	
	@EJB
	private FatkCth2RN fatkCth2RN;

	@EJB
	private FatkCthRN fatkCthRN;

	private static final Log LOG = LogFactory.getLog(EncerramentoContaHospitalarRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public Boolean rnCthcAtuGeraEsp(final Integer pCthSeq, final Boolean pPrevia, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		return getFatkCth2RN().rnCthcAtuGeraEsp(pCthSeq, pPrevia, nomeMicrocomputador, dataFimVinculoServidor, false);
	}

	public Boolean rnCthcAtuReabre(final Integer pCthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		return getFatkCthRN().rnCthcAtuReabre(pCthSeq, nomeMicrocomputador, dataFimVinculoServidor, null);
	}	

	private FatkCth2RN getFatkCth2RN(){
		return fatkCth2RN;
	}

	private FatkCthRN getFatkCthRN(){
		return fatkCthRN;
	}

}
