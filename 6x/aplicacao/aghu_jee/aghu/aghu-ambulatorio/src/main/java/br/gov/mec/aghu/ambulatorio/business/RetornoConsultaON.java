package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RetornoConsultaON extends BaseBusiness {

	private static final long serialVersionUID = 1551128270405728467L;
	
	private static final Log LOG = LogFactory.getLog(RetornoConsultaON.class);
	
	@Override
	protected Log getLogger() {
	return LOG;
	}

	public Boolean verificarCampoFaturaSus(AacRetornos retornoConsulta) {
		Boolean retorno = true;
		if (retornoConsulta.getFaturaSus() == null) {
			retorno = false;
		}else {
			retorno = retornoConsulta.getFaturaSus();
		}
		return retorno;
	}
}
