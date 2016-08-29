/**
 * 
 */
package br.gov.mec.aghu.estoque.business;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceTransferenciaDAO;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
/**
 * @author lessandro.lucas
 *
 */
@Stateless
public class ManterTransferenciaON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterTransferenciaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceTransferenciaDAO sceTransferenciaDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7218428926026706758L;

	protected SceTransferenciaDAO getSceTransferenciaDAO(){
		return sceTransferenciaDAO;
	}
	
	public SceTransferencia obterTransferenciaAutomatica(Integer codigo) {

		return getSceTransferenciaDAO().obterTransferenciaPorId(codigo);
	}
	
}
