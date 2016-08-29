package br.gov.mec.aghu.compras.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AcessoFornecedorON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6051778108777516070L;

	private static final Log LOG = LogFactory.getLog(AcessoFornecedorON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
}
