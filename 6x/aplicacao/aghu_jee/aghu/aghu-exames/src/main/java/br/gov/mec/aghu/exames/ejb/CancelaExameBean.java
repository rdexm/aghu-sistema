package br.gov.mec.aghu.exames.ejb;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameCancelamentoVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class CancelaExameBean extends BaseBusiness implements CancelaExameBeanLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5161223941231950212L;

	private static final Log LOG = LogFactory.getLog(CancelaExameBean.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@Resource
	protected SessionContext ctx;

	public enum CancelaExameBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_CANCELAR_EXAME, ERRO_AO_TENTAR_ESTORNAR_EXAME;
	}

	/**
	 * 
	 */
	public void excluirItensExamesSelecionados(List<ItemSolicitacaoExameCancelamentoVO> itens, String nomeMicrocomputador) throws BaseException {

		try {

			this.getSolicitacaoExameFacade().cancelarItensResponsabilidadeSolicitante(itens, nomeMicrocomputador);
			getAelSolicitacaoExamesDAO().flush();

		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(CancelaExameBeanExceptionCode.ERRO_AO_TENTAR_CANCELAR_EXAME);
		}
	}

	/**
	 * 
	 */
	public void cancelarExames(AelItemSolicitacaoExames aelItemSolicitacaoExames, 
			final AelMotivoCancelaExames motivoCancelar, String nomeMicrocomputador) throws BaseException {

		try {

			getPesquisaExamesFacade().cancelarExames(aelItemSolicitacaoExames, motivoCancelar, nomeMicrocomputador);
			getAelSolicitacaoExamesDAO().flush();

		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(CancelaExameBeanExceptionCode.ERRO_AO_TENTAR_CANCELAR_EXAME);
		}

	}

	/**
	 * 
	 */
	public void estornarItemSolicitacaoExame(AelItemSolicitacaoExames item, String nomeMicrocomputador) throws BaseException {

		try {

			getSolicitacaoExameFacade().estornarItemSolicitacaoExame(item, nomeMicrocomputador);

		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(CancelaExameBeanExceptionCode.ERRO_AO_TENTAR_ESTORNAR_EXAME);
		}

	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExamesDAO() {
		return aelSolicitacaoExameDAO;
	}

	protected IPesquisaExamesFacade getPesquisaExamesFacade() {
		return this.pesquisaExamesFacade;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

}
