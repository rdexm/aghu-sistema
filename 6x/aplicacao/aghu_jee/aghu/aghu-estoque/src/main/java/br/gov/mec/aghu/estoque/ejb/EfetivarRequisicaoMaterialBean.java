package br.gov.mec.aghu.estoque.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class EfetivarRequisicaoMaterialBean extends BaseBusiness implements EfetivarRequisicaoMaterialBeanLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5506145209111954031L;
	
	private static final Log LOG = LogFactory.getLog(EfetivarRequisicaoMaterialBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private SceReqMateriaisDAO sceReqMateriaisDAO;
		
	@Resource
	protected SessionContext ctx;
	
	public enum EfetivarRequisicaoMaterialBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_EFETIVAR_REQUISICAO_MATERIAL;
	}

	@Override
	public void efetivarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		
		try {
		
			getEstoqueFacade().efetivarRequisicaoMaterial(sceReqMateriais, nomeMicrocomputador);
			getReqMateriaisDAO().flush();
		
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(EfetivarRequisicaoMaterialBeanExceptionCode.ERRO_AO_TENTAR_EFETIVAR_REQUISICAO_MATERIAL);
		}
	
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected SceReqMateriaisDAO getReqMateriaisDAO() {
		return sceReqMateriaisDAO;
	}

}
