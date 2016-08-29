package br.gov.mec.aghu.estoque.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class ManterMaterialBean extends BaseBusiness implements ManterMaterialBeanLocal {

	private static final long serialVersionUID = 7371190280783462279L;
	
	private static final Log LOG = LogFactory.getLog(ManterMaterialBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
		
	@Resource
	protected SessionContext ctx;
	
	public enum ManterMaterialBeanBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_MANTER_MATERIAL;
	}
	
	@Override
	public void manterMaterial(ScoMaterial material, String nomeMicrocomputador) throws BaseException {
		try {
			this.getEstoqueFacade().persistirScoMaterial(material, nomeMicrocomputador);
			this.flush();
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterMaterialBeanBeanExceptionCode.ERRO_AO_TENTAR_MANTER_MATERIAL);
		}
	}
	
	@Override
	public void manterMaterialSemFlush(ScoMaterial material, String nomeMicrocomputador) throws BaseException {
		try {
			// Flush feito na controller ManterMaterialController
			this.getEstoqueFacade().persistirScoMaterial(material, nomeMicrocomputador);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterMaterialBeanBeanExceptionCode.ERRO_AO_TENTAR_MANTER_MATERIAL);
		}
		
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
}
