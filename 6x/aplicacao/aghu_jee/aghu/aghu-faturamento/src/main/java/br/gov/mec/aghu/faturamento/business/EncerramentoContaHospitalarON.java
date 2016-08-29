package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.VFatContaHospitalarPacDAO;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class EncerramentoContaHospitalarON extends BaseBMTBusiness implements Serializable {

	private static final String CONTA_ = "Conta ";

	private static final long serialVersionUID = 1527021487567723412L;

	private static final Log LOG = LogFactory.getLog(EncerramentoContaHospitalarON.class);

	public final static int TRANSACTION_TIMEOUT_24_HORAS = 60 * 60 * 24; //= 1 dia 

	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;

	@EJB
	private EncerramentoContaHospitalarRN encerramentoContaHospitalarRN;
	/**
	 * ORADB: FATK_CTH4_RN_UN.RN_CTHP_ATU_ENC_AUT2
	 * 
	 * @param cth
	 *            - conta a ser encerrada, válido apenas a partir da execução
	 *            não automática
	 * @throws BaseException
	 */
	public Boolean encerrarContasHospitalares(final Integer cth, String nomeMicrocomputador,
			final Date dataFimVinculoServidor, final AghJobDetail job) throws BaseException {
		
		this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);

		if( getEncerramentoContaHospitalarRN().rnCthcAtuGeraEsp(cth, false, nomeMicrocomputador, dataFimVinculoServidor) ){
			logInfo(CONTA_ + cth + " encerrada.");
			this.commitTransaction();
			return true;
		} else {
			logError(CONTA_ + cth + " não encerrada por conter erros.");
			this.commitTransaction();
			return false;
		}
	}

	public Boolean reabrirContaHospitalar(final Integer cthSeqSelected,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {

		this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
		final FatContasHospitalares ctaHosp = this
				.getFatContasHospitalaresDAO().obterPorChavePrimaria(
						cthSeqSelected);
		final VFatContaHospitalarPac ctaHospPac = this
				.getVFatContaHospitalarPacDAO().obterPorChavePrimaria(
						ctaHosp.getSeq());

		logInfo("Inicio de Reabertura de Conta após confirmação: " + ctaHosp.getSeq()
				+ " Prontuario: " + ctaHospPac.getPacProntuario() + " AIH: "
				+ ctaHospPac.getCthNroAih() + " DCIH: "
				+ ctaHosp.getDocumentoCobrancaAih().getCodigoDcih());

		if( getEncerramentoContaHospitalarRN().rnCthcAtuReabre(cthSeqSelected, nomeMicrocomputador, dataFimVinculoServidor) ){
			logInfo(CONTA_ + cthSeqSelected + " reaberta.");
			this.commitTransaction();
			return true;
		} else {
			logError(CONTA_ + cthSeqSelected + " não reaberta por conter erros.");
			this.commitTransaction();
			return false;
		}
	}

	protected EncerramentoContaHospitalarRN getEncerramentoContaHospitalarRN() {
		return encerramentoContaHospitalarRN;
	}

	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}
	
	private VFatContaHospitalarPacDAO getVFatContaHospitalarPacDAO() {
		return vFatContaHospitalarPacDAO;
	}

	@Inject
	private VFatContaHospitalarPacDAO vFatContaHospitalarPacDAO;

}