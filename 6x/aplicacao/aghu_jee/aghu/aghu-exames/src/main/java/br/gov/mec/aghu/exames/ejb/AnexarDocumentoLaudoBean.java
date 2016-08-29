package br.gov.mec.aghu.exames.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelDocResultadoExameDAO;
import br.gov.mec.aghu.model.AelDocResultadoExame;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class AnexarDocumentoLaudoBean extends BaseBusiness implements AnexarDocumentoLaudoBeanLocal {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6179319009179534983L;

	private static final Log LOG = LogFactory.getLog(AnexarDocumentoLaudoBean.class);

	@EJB
	private IExamesFacade examesFacade;

	@Inject
	private AelDocResultadoExameDAO aelDocResultadoExameDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Resource
	protected SessionContext ctx;

	public enum ArquivoLaudoResultadoExameBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_INSERIR_ARQUIVO;
	}

	/**
	 * 
	 */
	public 	void anexarDocumentoLaudo(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora) throws BaseException {

		try {

			this.getExamesFacade().anexarDocumentoLaudo(doc, unidadeExecutora);
			getAelDocResultadoExameDAO().flush();

		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(ArquivoLaudoResultadoExameBeanExceptionCode.ERRO_AO_TENTAR_INSERIR_ARQUIVO);
		}

	}

	/**
	 * 
	 */
	public void removerDocumentoLaudo(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora) throws BaseException {

		try {

			this.getExamesFacade().removerDocumentoLaudo(doc, unidadeExecutora);
			getAelDocResultadoExameDAO().flush();

		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(ArquivoLaudoResultadoExameBeanExceptionCode.ERRO_AO_TENTAR_INSERIR_ARQUIVO);
		}

	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected AelDocResultadoExameDAO getAelDocResultadoExameDAO(){
		return aelDocResultadoExameDAO;
	}
}
