package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoContaApac;
import br.gov.mec.aghu.faturamento.dao.FatContaApacDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
public class FaturamentoFatkCap2RN extends AbstractFatDebugLogEnableRN {

private static final Log LOG = LogFactory.getLog(FaturamentoFatkCap2RN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatContaApacDAO fatContaApacDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 306503271191043305L;

	/**
	 * ORADB Procedure FATK_CAP2_RN.RN_CAPC_VER_CAP_PAC <br />
	 * 
	 * Este método deve ser revisado quando forem migradas as APACS, ver com Milena a melhor forma de tratar esta consulta.
	 * 
	 * @param tipoTrat  --> parâmetro chumbado em alguns pontos 
	 * @see FatkCth2RN.rnCthcAtuEncPrv, chamada (faturamentoFatkCap2RN.rnCapcVerCapPac)
	 * @author eScwheigert
	 * @since 03/09/2012
	 */
	public Long rnCapcVerCapPac(Integer pacCodigo, Date dtRealiz, Byte tipoTrat, DominioModuloCompetencia pModulo) throws ApplicationBusinessException {
		// FIXME eSchweigert rever comentário acima
		Long retorno = getFatContaApacDAO().countApac(pacCodigo, pModulo, dtRealiz, tipoTrat, DominioSituacaoContaApac.C);
		if(retorno == null){
			retorno = 0l;
		}
		return retorno;
	}

	protected FatContaApacDAO getFatContaApacDAO() {
		return fatContaApacDAO;
	}

}
