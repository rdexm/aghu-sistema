package br.gov.mec.aghu.estoque.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.dao.SceTransferenciaDAO;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class TransferenciaEventualMaterialBean extends BaseBusiness implements TransferenciaEventualMaterialBeanLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7171314234902594315L;

	private static final Log LOG = LogFactory.getLog(TransferenciaEventualMaterialBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private SceTransferenciaDAO sceTransferenciaDAO;
	
	@Resource
	protected SessionContext ctx;

	@Override
	public void gravarTransferenciaEventualMaterial(SceTransferencia sceTransferencia) throws BaseException {

		try {

			getEstoqueFacade().inserirTransferenciaMaterialEventual(sceTransferencia);			
			getSceTransferenciaDAO().flush();

		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} 


	}

	protected SceTransferenciaDAO getSceTransferenciaDAO(){
		return sceTransferenciaDAO;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

}
