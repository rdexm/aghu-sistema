package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.model.FatCidContaHospitalar;

/**
 * Triggers de FAT_CIDS_CONTA_HOSPITALAR<br/>
 * 
 * ORADB: FATT_CCH_ARI
 * ORADB: FATT_CCH_ASI
 * ORADB: FATT_CCH_BSI
 * ORADB: FATT_CCH_ARU
 * ORADB: FATT_CCH_ASU
 * ORADB: FATT_CCH_BSU
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class CidContaHospitalarRN extends AbstractAGHUCrudRn<FatCidContaHospitalar> {

	private static final Log LOG = LogFactory.getLog(CidContaHospitalarRN.class);
	
	private static final long serialVersionUID = 1044847923618564298L;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * As triggers dessa classe estão comentadas no oracle!!!
	 */
	
}
