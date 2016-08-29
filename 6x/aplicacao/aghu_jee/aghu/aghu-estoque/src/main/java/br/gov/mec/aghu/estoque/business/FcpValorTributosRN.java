package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.FcpValorTributos;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class FcpValorTributosRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(FcpValorTributosRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2879570776342184077L;

	public void atualizar(FcpValorTributos valorTributos) {
		// TODO Implementar		
	}
	
}
