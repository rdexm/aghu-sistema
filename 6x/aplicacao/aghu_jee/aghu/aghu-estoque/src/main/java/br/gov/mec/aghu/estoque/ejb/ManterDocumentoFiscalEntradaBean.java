package br.gov.mec.aghu.estoque.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.dao.SceDocumentoFiscalEntradaDAO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class ManterDocumentoFiscalEntradaBean extends BaseBusiness  implements ManterDocumentoFiscalEntradaBeanLocal{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5272467436841153819L;
	
	private static final Log LOG = LogFactory.getLog(ManterDocumentoFiscalEntradaBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private SceDocumentoFiscalEntradaDAO sceDocumentoFiscalEntradaDAO;
	
	@Resource
	protected SessionContext ctx;
	
	public enum ManterDocumentoFiscalEntradaBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_PERSISTIR_DOCUMENTO_FISCAL_ENTRADA,ERRO_AO_TENTAR_REMOVER_DOCUMENTO_FISCAL_ENTRADA;
	}
	
	@Override
	public void persistirDocumentoFiscalEntrada(
			SceDocumentoFiscalEntrada documentoFiscalEntrada)
			throws BaseException {
		try {
			this.getEstoqueFacade().persistirDocumentoFiscalEntrada(documentoFiscalEntrada);
			this.getSceDocumentoFiscalEntradaDAO().flush();
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterDocumentoFiscalEntradaBeanExceptionCode.ERRO_AO_TENTAR_PERSISTIR_DOCUMENTO_FISCAL_ENTRADA);
		}
	}

	@Override
	public void removerDocumentoFiscalEntrada(Integer seq)throws BaseException {
		try {
			SceDocumentoFiscalEntrada documentoFiscalEntrada = this.getSceDocumentoFiscalEntradaDAO().obterPorChavePrimaria(seq);
			this.getEstoqueFacade().removerDocumentoFiscalEntrada(documentoFiscalEntrada);
			this.getSceDocumentoFiscalEntradaDAO().flush();
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterDocumentoFiscalEntradaBeanExceptionCode.ERRO_AO_TENTAR_REMOVER_DOCUMENTO_FISCAL_ENTRADA);
		}
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected SceDocumentoFiscalEntradaDAO getSceDocumentoFiscalEntradaDAO() {
		return sceDocumentoFiscalEntradaDAO;
	}
	
}
