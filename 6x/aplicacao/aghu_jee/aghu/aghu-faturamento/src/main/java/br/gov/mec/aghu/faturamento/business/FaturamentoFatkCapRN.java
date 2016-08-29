package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
public class FaturamentoFatkCapRN extends AbstractFatDebugLogEnableRN {

private static final Log LOG = LogFactory.getLog(FaturamentoFatkCapRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -120381326017732913L;
	
	public Boolean obterAtributoCapEncerramento() {
//		Object obj = obterContextoSessao("FATK_CAP_RN_V_CAP_ENCERRAMENTO");
//		if (obj == null) {
			return Boolean.FALSE;
//		}
//		return (Boolean) obj;
	}
	
}
