package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultaProcedHospitalarDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class AacConsultaProcedHospitalarON extends BaseBusiness {

	@EJB
	private ProcedimentoConsultaRN procedimentoConsultaRN;

	private static final Log LOG = LogFactory.getLog(AacConsultaProcedHospitalarON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AacConsultaProcedHospitalarDAO aacConsultaProcedHospitalarDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8557899819722019426L;

	public AacConsultaProcedHospitalar inserir(final AacConsultaProcedHospitalar aacConsultaProcedHospitalar, String nomeMicrocomputador) throws BaseException {
		ProcedimentoConsultaRN processarConsultaProcedimentoHospitalar = getProcedimentoConsultaRN();
		// ORADB Trigger aact_prh_asi
		processarConsultaProcedimentoHospitalar.executarPreInserirConsultaProcedimentoHospitalar(aacConsultaProcedHospitalar);
		
		getAacConsultaProcedHospitalarDAO().persistir(aacConsultaProcedHospitalar);
		getAacConsultaProcedHospitalarDAO().flush();
		
		processarConsultaProcedimentoHospitalar.processarConsultaProcedimentoHospitalar(null, aacConsultaProcedHospitalar, DominioOperacaoBanco.INS, nomeMicrocomputador, new Date());
		return aacConsultaProcedHospitalar;
	}

	protected AacConsultaProcedHospitalarDAO getAacConsultaProcedHospitalarDAO() {
		return aacConsultaProcedHospitalarDAO;
	}

	protected ProcedimentoConsultaRN getProcedimentoConsultaRN() {
		return procedimentoConsultaRN;
	}
}
