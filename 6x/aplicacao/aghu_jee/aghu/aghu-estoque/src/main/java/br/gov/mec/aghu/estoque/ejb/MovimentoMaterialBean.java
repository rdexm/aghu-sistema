package br.gov.mec.aghu.estoque.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class MovimentoMaterialBean extends BaseBusiness implements MovimentoMaterialBeanLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7171314234902594315L;

	private static final Log LOG = LogFactory.getLog(MovimentoMaterialBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Resource
	protected SessionContext ctx;

	public void gravarMovimentoMaterial(SceMovimentoMaterial movimento, String nomeMicrocomputador) throws BaseException{
		try {
			getEstoqueFacade().persistirMovimentoMaterial(movimento, nomeMicrocomputador);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		}
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
}