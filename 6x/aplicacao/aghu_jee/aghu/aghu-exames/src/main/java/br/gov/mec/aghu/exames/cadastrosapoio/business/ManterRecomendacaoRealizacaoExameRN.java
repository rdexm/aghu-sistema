package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.lang.reflect.InvocationTargetException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelRecomendacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelRecomendacaoExameJnDAO;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.model.AelRecomendacaoExameJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterRecomendacaoRealizacaoExameRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterRecomendacaoRealizacaoExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelRecomendacaoExameDAO aelRecomendacaoExameDAO;
	
	@Inject
	private AelRecomendacaoExameJnDAO aelRecomendacaoExameJnDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1562531684441740671L;

	public enum ManterRecomendacaoRealizacaoExameRNExceptionCode implements BusinessExceptionCode {

		AEL_00369;

	}
	
	/**
	 * ORADB AELT_REX_BRI
	 * Insere um novo registro de AelRecomendacaoExame
	 * @param recomendacaoExame
	 * @throws ApplicationBusinessException  
	 */
	public void inserirAelRecomendacaoExame(AelRecomendacaoExame recomendacaoExame) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		recomendacaoExame.setServidor(servidorLogado);
		recomendacaoExame.setServidorAlterado(servidorLogado);
		getAelRecomendacaoExameDAO().persistir(recomendacaoExame);
		getAelRecomendacaoExameDAO().flush();
		
	}
	
	/**
	 * ORADB AELT_REX_BRU, AELT_REX_ARU
	 * @param recomendacaoExame
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void atualizarAelRecomendacaoExame(AelRecomendacaoExame novoRecomendacaoExame) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelRecomendacaoExame recomendacaoExame = getAelRecomendacaoExameDAO().obterAelRecomendacaoExamePorID(novoRecomendacaoExame.getId());
		AelRecomendacaoExame auxRecomendacaoExame = new AelRecomendacaoExame();
		
		try {
		
			BeanUtils.copyProperties(auxRecomendacaoExame, recomendacaoExame);
		
		} catch (IllegalAccessException e) {
		
			this.logError(e.getMessage());
		
		} catch (InvocationTargetException e) {
	
			this.logError(e.getMessage());
		
		}
		
		validarUpdate(recomendacaoExame.getServidor(), novoRecomendacaoExame.getServidor());
		novoRecomendacaoExame.setServidorAlterado(servidorLogado);
		getAelRecomendacaoExameDAO().merge(novoRecomendacaoExame);
		getAelRecomendacaoExameDAO().flush();
		posAtualizarAelRecomendacaoExame(auxRecomendacaoExame, novoRecomendacaoExame);
		
	}
	
	/**
	 * Remove objeto
	 * @param novoRecomendacaoExame
	 */
	public void removerAelRecomendacaoExame(AelRecomendacaoExame novoRecomendacaoExame) {
		novoRecomendacaoExame = getAelRecomendacaoExameDAO().obterPorChavePrimaria(novoRecomendacaoExame.getId());
		getAelRecomendacaoExameDAO().remover(novoRecomendacaoExame);
		getAelRecomendacaoExameDAO().flush();
	}
	
	/**
	 * ORADB aelk_rex_rn.rn_rexp_ver_update
	 * @param servidor
	 * @param novoServidor
	 *  
	 */
	protected void validarUpdate(RapServidores servidor, RapServidores novoServidor) throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(servidor.getId().getMatricula(), novoServidor.getId().getMatricula()) || CoreUtil.modificados(servidor.getId().getVinCodigo(), novoServidor.getId().getVinCodigo())) {
			
			throw new ApplicationBusinessException(ManterRecomendacaoRealizacaoExameRNExceptionCode.AEL_00369);	
			
		}
		
	}
	
	/**
	 * Cria o journal
	 * @param recomendacaoExame
	 * @param novoRecomendacaoExame
	 * @throws ApplicationBusinessException 
	 */
	private void posAtualizarAelRecomendacaoExame(AelRecomendacaoExame recomendacaoExame, AelRecomendacaoExame novoRecomendacaoExame) throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(recomendacaoExame.getServidorAlterado(), novoRecomendacaoExame.getServidorAlterado())
				|| CoreUtil.modificados(recomendacaoExame.getId().getEmaExaSigla(), novoRecomendacaoExame.getId().getEmaExaSigla())
				|| CoreUtil.modificados(recomendacaoExame.getId().getEmaManSeq(), novoRecomendacaoExame.getId().getEmaManSeq())
				|| CoreUtil.modificados(recomendacaoExame.getId().getSeqp(), novoRecomendacaoExame.getId().getSeqp())
				|| CoreUtil.modificados(recomendacaoExame.getDescricao(), novoRecomendacaoExame.getDescricao())
				|| CoreUtil.modificados(recomendacaoExame.getAbrangencia(), novoRecomendacaoExame.getAbrangencia())
				|| CoreUtil.modificados(recomendacaoExame.getResponsavel(), novoRecomendacaoExame.getResponsavel())
				|| CoreUtil.modificados(recomendacaoExame.getIndAvisaResp(), novoRecomendacaoExame.getIndAvisaResp())) {

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelRecomendacaoExameJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelRecomendacaoExameJn.class, servidorLogado.getUsuario());
			jn.setAbrangencia(novoRecomendacaoExame.getAbrangencia());
			jn.setDescricao(novoRecomendacaoExame.getDescricao());
			jn.setEmaExaSigla(novoRecomendacaoExame.getId().getEmaExaSigla());
			jn.setEmaManSeq(novoRecomendacaoExame.getId().getEmaManSeq());
			jn.setIndAvisaResp(novoRecomendacaoExame.getIndAvisaResp());
			jn.setResponsavel(novoRecomendacaoExame.getResponsavel());
			jn.setSeqp(novoRecomendacaoExame.getId().getSeqp());
			jn.setSerMatriculaAlterado(novoRecomendacaoExame.getServidorAlterado().getId().getMatricula());
			jn.setSerVinCodigoAlterado(novoRecomendacaoExame.getServidorAlterado().getId().getVinCodigo());
			
			getAelRecomendacaoExameJnDAO().persistir(jn);
			getAelRecomendacaoExameJnDAO().flush();
			
		}
		
	}

	protected AelRecomendacaoExameDAO getAelRecomendacaoExameDAO() {
		return aelRecomendacaoExameDAO;
	}

	protected AelRecomendacaoExameJnDAO getAelRecomendacaoExameJnDAO() {
		return aelRecomendacaoExameJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
