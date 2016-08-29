package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExameResuNotificacaoDAO;
import br.gov.mec.aghu.exames.dao.AelExameResuNotificacaoJnDAO;
import br.gov.mec.aghu.model.AelExameResuNotificacao;
import br.gov.mec.aghu.model.AelExameResuNotificacaoId;
import br.gov.mec.aghu.model.AelExameResuNotificacaoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ExameResultadoNotificacaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ExameResultadoNotificacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExameResuNotificacaoJnDAO aelExameResuNotificacaoJnDAO;
	
	@Inject
	private AelExameResuNotificacaoDAO aelExameResuNotificacaoDAO;
	
	public enum ExameResultadoNotificaoNotificacaoRNExceptionCode implements BusinessExceptionCode {
		INFORMAR_RESULTADO_EXAME_RESU_NOTIFICACAO;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3020113252516246681L;


	public void remover(AelExameResuNotificacaoId id) throws ApplicationBusinessException {
		AelExameResuNotificacao exameResultadoNotificacao = this.getAelExameResuNotificacaoDAO().obterPorChavePrimaria(id);
		this.getAelExameResuNotificacaoDAO().remover(exameResultadoNotificacao);
		this.posRemoverExameResultadoNotificacao(exameResultadoNotificacao);
	}
	
	public void persistir(AelExameResuNotificacao exameResuNotificacao) throws ApplicationBusinessException {
		this.validarExameResultadoNotificacao(exameResuNotificacao);
		this.preInserirExameResultadoNotificacao(exameResuNotificacao);
		this.getAelExameResuNotificacaoDAO().persistir(exameResuNotificacao);	
	}
	
	public void atualizar(AelExameResuNotificacao exameResuNotificacao) throws ApplicationBusinessException {
		AelExameResuNotificacao exameResuNotificacaoOld = this.getAelExameResuNotificacaoDAO().obterOriginal(exameResuNotificacao.getId());
		this.validarExameResultadoNotificacao(exameResuNotificacao);
		this.getAelExameResuNotificacaoDAO().merge(exameResuNotificacao);
		this.posAtualizarExameResuNotificacao(exameResuNotificacao, exameResuNotificacaoOld);
	}
	
	/**
	 * ORADB
	 * Trigger AELT_ERN_BRI
	 * @param exameResultadoNotificacao
	 * @throws ApplicationBusinessException  
	 */
	public void preInserirExameResultadoNotificacao(AelExameResuNotificacao exameResultadoNotificacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		exameResultadoNotificacao.setCriadoEm(new Date());
		exameResultadoNotificacao.setRapServidores(servidorLogado);
	}
	
	public void validarExameResultadoNotificacao(AelExameResuNotificacao exameResultadoNotificacao) throws ApplicationBusinessException{
		if(("").equals(exameResultadoNotificacao.getResultadoAlfanum())){
			exameResultadoNotificacao.setResultadoAlfanum(null);
		}
		if(!((exameResultadoNotificacao.getAelResultadoCodificado()!=null && StringUtils.isBlank(exameResultadoNotificacao.getResultadoAlfanum()) && exameResultadoNotificacao.getResultadoNumExp()==null)||
		   (exameResultadoNotificacao.getAelResultadoCodificado()==null && StringUtils.isNotBlank(exameResultadoNotificacao.getResultadoAlfanum()) && exameResultadoNotificacao.getResultadoNumExp()==null)||
		   (exameResultadoNotificacao.getAelResultadoCodificado()==null && StringUtils.isBlank(exameResultadoNotificacao.getResultadoAlfanum()) && exameResultadoNotificacao.getResultadoNumExp()!=null))){
			throw new ApplicationBusinessException(ExameResultadoNotificaoNotificacaoRNExceptionCode.INFORMAR_RESULTADO_EXAME_RESU_NOTIFICACAO);
		}
	}
	
	/**
	 * ORADB
	 * Trigger AELT_ERN_ARD
	 * @param exameResultadoNotificacao
	 * @throws ApplicationBusinessException 
	 */
	public void posRemoverExameResultadoNotificacao(AelExameResuNotificacao exameResultadoNotificacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelExameResuNotificacaoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL,
				AelExameResuNotificacaoJn.class, servidorLogado.getUsuario());
		jn.setCriadoEm(exameResultadoNotificacao.getCriadoEm());
		jn.setExnCalSeq(exameResultadoNotificacao.getId().getExnCalSeq());
		jn.setExnEmaExaSigla(exameResultadoNotificacao.getId().getExnEmaExaSigla());
		jn.setExnEmaManSeq(exameResultadoNotificacao.getId().getExnEmaManSeq());
		jn.setSituacao(exameResultadoNotificacao.getSituacao());
		if(exameResultadoNotificacao.getAelResultadoCodificado()!=null){
			jn.setRcdGtcSeq(exameResultadoNotificacao.getAelResultadoCodificado().getId().getGtcSeq());
			jn.setRcdSeqp(exameResultadoNotificacao.getAelResultadoCodificado().getId().getSeqp());
		}
		jn.setResultadoAlfanum(exameResultadoNotificacao.getResultadoAlfanum());
		jn.setResultadoNumExp(exameResultadoNotificacao.getResultadoNumExp());
		jn.setSeqp(exameResultadoNotificacao.getId().getSeqp());
		jn.setSerMatricula(exameResultadoNotificacao.getRapServidores().getId().getMatricula());
		jn.setSerVinCodigo(exameResultadoNotificacao.getRapServidores().getId().getVinCodigo());
		this.getAelExameResuNotificacaoJnDAO().persistir(jn);
	}
	
	/**
	 * ORADB
	 * Trigger AELT_ERN_ARU
	 * @param exameResuNotificacao
	 * @param exameResuNotificacaoOld
	 * @throws ApplicationBusinessException 
	 */
	public void posAtualizarExameResuNotificacao(AelExameResuNotificacao exameResuNotificacao, AelExameResuNotificacao exameResuNotificacaoOld) throws ApplicationBusinessException{
		if (CoreUtil.modificados(exameResuNotificacao.getId().getExnEmaExaSigla(), exameResuNotificacaoOld.getId().getExnEmaExaSigla())
				|| CoreUtil.modificados(exameResuNotificacao.getId().getExnEmaManSeq(), exameResuNotificacaoOld.getId().getExnEmaManSeq())
				|| CoreUtil.modificados(exameResuNotificacao.getId().getExnCalSeq(), exameResuNotificacaoOld.getId().getExnCalSeq())
				|| CoreUtil.modificados(exameResuNotificacao.getId().getSeqp(), exameResuNotificacaoOld.getId().getSeqp())
				|| CoreUtil.modificados(exameResuNotificacao.getAelResultadoCodificado(), exameResuNotificacaoOld.getAelResultadoCodificado())
				|| CoreUtil.modificados(exameResuNotificacao.getResultadoNumExp(), exameResuNotificacaoOld.getResultadoNumExp())
				|| CoreUtil.modificados(exameResuNotificacao.getResultadoAlfanum(), exameResuNotificacaoOld.getResultadoAlfanum())
				|| CoreUtil.modificados(exameResuNotificacao.getCriadoEm(), exameResuNotificacaoOld.getCriadoEm())
				|| CoreUtil.modificados(exameResuNotificacao.getSituacao(), exameResuNotificacaoOld.getSituacao())
				|| CoreUtil.modificados(exameResuNotificacao.getRapServidores().getId().getMatricula(), exameResuNotificacaoOld.getRapServidores().getId().getMatricula())
				|| CoreUtil.modificados(exameResuNotificacao.getRapServidores().getId().getVinCodigo(), exameResuNotificacaoOld.getRapServidores().getId().getVinCodigo()))
				 {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
				AelExameResuNotificacaoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD,
						AelExameResuNotificacaoJn.class, servidorLogado.getUsuario());
				jn.setCriadoEm(exameResuNotificacaoOld.getCriadoEm());
				jn.setExnCalSeq(exameResuNotificacaoOld.getId().getExnCalSeq());
				jn.setExnEmaExaSigla(exameResuNotificacaoOld.getId().getExnEmaExaSigla());
				jn.setExnEmaManSeq(exameResuNotificacaoOld.getId().getExnEmaManSeq());
				jn.setSituacao(exameResuNotificacaoOld.getSituacao());
				if(exameResuNotificacaoOld.getAelResultadoCodificado()!=null){
					jn.setRcdGtcSeq(exameResuNotificacaoOld.getAelResultadoCodificado().getId().getGtcSeq());
					jn.setRcdSeqp(exameResuNotificacaoOld.getAelResultadoCodificado().getId().getSeqp());
				}
				jn.setResultadoAlfanum(exameResuNotificacaoOld.getResultadoAlfanum());
				jn.setResultadoNumExp(exameResuNotificacaoOld.getResultadoNumExp());
				jn.setSeqp(exameResuNotificacaoOld.getId().getSeqp());
				jn.setSerMatricula(exameResuNotificacaoOld.getRapServidores().getId().getMatricula());
				jn.setSerVinCodigo(exameResuNotificacaoOld.getRapServidores().getId().getVinCodigo());
				this.getAelExameResuNotificacaoJnDAO().persistir(jn);
		}
	}
	
	protected AelExameResuNotificacaoDAO getAelExameResuNotificacaoDAO() {
		return aelExameResuNotificacaoDAO;
	}
	
	protected AelExameResuNotificacaoJnDAO getAelExameResuNotificacaoJnDAO() {
		return aelExameResuNotificacaoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
