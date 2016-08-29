package br.gov.mec.aghu.exames.ejb;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.business.IColetaExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class AmostraExameBean extends BaseBusiness implements AmostraExameBeanLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6405605608632004441L;

	private static final Log LOG = LogFactory.getLog(AmostraExameBean.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IColetaExamesFacade coletaExamesFacade;
	

	@EJB
	private IExamesFacade examesFacade;

	@Inject
	private AelAmostrasDAO aelAmostrasDAO;
	
	@Resource
	protected SessionContext ctx;
	
	public enum AmostraExameBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_VOLTAR_AMOSTRA
		, ERRO_AO_TENTAR_RECEBER_AMOSTRA
		;
	}
	
	/**
	 * Recebe amostra
	 * @param configExameOrigem 
	 */
	@Override
	public ImprimeEtiquetaVO receberAmostra(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, final String nroFrascoFabricante, List<ExameAndamentoVO> listaExamesAndamento, String nomeMicrocomputador) throws BaseException {
		ImprimeEtiquetaVO vo = null;
		try {
			
			vo = this.getExamesFacade().receberAmostra(unidadeExecutora, amostra, nroFrascoFabricante, listaExamesAndamento, nomeMicrocomputador);
			
			this.getAelAmostrasDAO().flush();
			
		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(AmostraExameBeanExceptionCode.ERRO_AO_TENTAR_RECEBER_AMOSTRA);
		}
		
		return vo;
	}
	
	/**
	 * Recebe amostra por solicitação
	 * @param configExameOrigem 
	 */
	@Override
	public ImprimeEtiquetaVO receberAmostraSolicitacao(
			final AghUnidadesFuncionais unidadeExecutora,
			final AelAmostras amostra, List<ExameAndamentoVO> listaExamesAndamento,
			String nomeMicrocomputador) throws BaseException {
		ImprimeEtiquetaVO vo = null; 
		
		try {
			vo = this.getExamesFacade().receberAmostraSolicitacao(
					unidadeExecutora, amostra, listaExamesAndamento, nomeMicrocomputador);
			
			this.getAelAmostrasDAO().flush();
		} catch (final BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (final Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(AmostraExameBeanExceptionCode.ERRO_AO_TENTAR_RECEBER_AMOSTRA);
		}
		
		return vo;
	}
	
	/**
	 * Retorna situação da amostra
	 */
	@Override
	public boolean voltarAmostra(final AghUnidadesFuncionais unidadeExecutora, final AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		
		boolean retorno = false;
		
		try {
			
			retorno = this.getExamesFacade().voltarAmostra(unidadeExecutora, amostra, nomeMicrocomputador);
			this.getAelAmostrasDAO().flush();
			
		} catch (final BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (final Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(AmostraExameBeanExceptionCode.ERRO_AO_TENTAR_VOLTAR_AMOSTRA, Severity.ERROR, e);
		}
		
		return retorno;
		
	}

	/**
	 * Retorna situação da amostra por solicitação
	 */
	@Override
	public boolean voltarSituacaoAmostraSolicitacao(AghUnidadesFuncionais unidadeExecutora, AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		
		boolean retorno = false;
		
		try {
			
			retorno = this.getExamesFacade().voltarSituacaoAmostraSolicitacao(unidadeExecutora, amostra, nomeMicrocomputador);
			this.getAelAmostrasDAO().flush();
			
		} catch (final BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (final Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(AmostraExameBeanExceptionCode.ERRO_AO_TENTAR_VOLTAR_AMOSTRA, Severity.ERROR, e);
		}
		
		return retorno;
		
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}

	//#25907
	@Override
	public void atualizarSituacaoExamesAmostra(AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		try {
			getColetaExamesFacade().atualizarSituacaoExamesAmostra(amostra, nomeMicrocomputador);
			this.getAelAmostrasDAO().flush();
		} catch (final BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (final Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
		}
	}
	
	//#25907
	@Override
	public void atualizarSituacaoExamesAmostraColetada(AelAmostras amostra, String nomeMicrocomputador) throws BaseException {
		try {
			getColetaExamesFacade().atualizarSituacaoExamesAmostraColetada(amostra, nomeMicrocomputador);
			this.getAelAmostrasDAO().flush();
		} catch (final BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (final Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
		}
	}

	private IColetaExamesFacade getColetaExamesFacade() {
		return this.coletaExamesFacade;
	}
}
