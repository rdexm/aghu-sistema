package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AltaAmbulatorialRN extends BaseBusiness{
	

	private static final long serialVersionUID = -5773553087140847880L;
	private static final Log LOG = LogFactory.getLog(AltaAmbulatorialRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}


}