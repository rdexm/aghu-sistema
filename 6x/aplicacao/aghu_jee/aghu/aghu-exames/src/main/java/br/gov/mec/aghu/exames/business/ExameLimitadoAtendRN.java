package br.gov.mec.aghu.exames.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExameLimitadoAtendJnDAO;
import br.gov.mec.aghu.exames.dao.AelExamesLimitadoAtendDAO;
import br.gov.mec.aghu.model.AelExameLimitadoAtendJn;
import br.gov.mec.aghu.model.AelExamesLimitadoAtend;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ExameLimitadoAtendRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ExameLimitadoAtendRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExameLimitadoAtendJnDAO aelExameLimitadoAtendJnDAO;
	
	@Inject
	private AelExamesLimitadoAtendDAO aelExamesLimitadoAtendDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -468019951548200837L;
	
	public void remover(AelExamesLimitadoAtend examesLimitadoAtend) throws ApplicationBusinessException {
		examesLimitadoAtend = this.getAelExameLimitadoAtendDAO().obterPorChavePrimaria(examesLimitadoAtend.getId());
		this.getAelExameLimitadoAtendDAO().remover(examesLimitadoAtend);
		this.posRemoverExameLimitadoAtend(examesLimitadoAtend);
	}
	
	public void persistir(AelExamesLimitadoAtend examesLimitadoAtend) {
		this.getAelExameLimitadoAtendDAO().persistir(examesLimitadoAtend);
	}
	
	public void atualizar(AelExamesLimitadoAtend examesLimitadoAtend) throws ApplicationBusinessException {
		AelExamesLimitadoAtend examesLimitadoAtendOld =  getAelExameLimitadoAtendDAO().obterOriginal(examesLimitadoAtend.getId());
		this.getAelExameLimitadoAtendDAO().merge(examesLimitadoAtend);
		this.posAtualizarExameLimitadoAtend(examesLimitadoAtend, examesLimitadoAtendOld);
	}
	
	/**
	 * ORADB
	 * Trigger AELT_ELA_ARD
	 * @param examesLimitadoAtend
	 * @throws ApplicationBusinessException 
	 */
	public void posRemoverExameLimitadoAtend(AelExamesLimitadoAtend examesLimitadoAtend) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelExameLimitadoAtendJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL,
				AelExameLimitadoAtendJn.class, servidorLogado.getUsuario());
		jn.setSerMatricula(examesLimitadoAtend.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(examesLimitadoAtend.getServidor().getId().getVinCodigo());
		jn.setAtdSeq(examesLimitadoAtend.getId().getAtdSeq());
		jn.setCriadoEm(examesLimitadoAtend.getCriadoEm());
		jn.setDthrLimite(examesLimitadoAtend.getDthrLimite());
		jn.setEmaExaSigla(examesLimitadoAtend.getId().getEmaExaSigla());
		jn.setEmaManSeq(examesLimitadoAtend.getId().getEmaManSeq());
		this.getAelExameLimitadoAtendJnDAO().persistir(jn);
	}
	
	/**
	 * ORADB
	 * Trigger AELT_ELA_ARU
	 * @param examesLimitadoAtend
	 * @param examesLimitadoAtendOld
	 * @throws ApplicationBusinessException 
	 */
	public void posAtualizarExameLimitadoAtend(AelExamesLimitadoAtend examesLimitadoAtend, AelExamesLimitadoAtend examesLimitadoAtendOld) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (CoreUtil.modificados(examesLimitadoAtend.getCriadoEm(), examesLimitadoAtendOld.getCriadoEm())
				|| CoreUtil.modificados(examesLimitadoAtend.getDthrLimite(), examesLimitadoAtendOld.getDthrLimite())
				|| CoreUtil.modificados(examesLimitadoAtend.getId().getAtdSeq(), examesLimitadoAtendOld.getId().getAtdSeq())
				|| CoreUtil.modificados(examesLimitadoAtend.getId().getEmaExaSigla(), examesLimitadoAtendOld.getId().getEmaExaSigla())
				|| CoreUtil.modificados(examesLimitadoAtend.getId().getEmaManSeq(), examesLimitadoAtendOld.getId().getEmaManSeq())
				|| CoreUtil.modificados(servidorLogado, examesLimitadoAtendOld.getServidor())) {
				AelExameLimitadoAtendJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD,
						AelExameLimitadoAtendJn.class, servidorLogado.getUsuario());
				jn.setSerMatricula(examesLimitadoAtend.getServidor().getId().getMatricula());
				jn.setSerVinCodigo(examesLimitadoAtend.getServidor().getId().getVinCodigo());
				jn.setAtdSeq(examesLimitadoAtend.getId().getAtdSeq());
				jn.setCriadoEm(examesLimitadoAtend.getCriadoEm());
				jn.setDthrLimite(examesLimitadoAtend.getDthrLimite());
				jn.setEmaExaSigla(examesLimitadoAtend.getId().getEmaExaSigla());
				jn.setEmaManSeq(examesLimitadoAtend.getId().getEmaManSeq());
				this.getAelExameLimitadoAtendJnDAO().merge(jn);
		}
	}
	
	protected AelExamesLimitadoAtendDAO getAelExameLimitadoAtendDAO() {
		return aelExamesLimitadoAtendDAO;
	}
	
	protected AelExameLimitadoAtendJnDAO getAelExameLimitadoAtendJnDAO() {
		return aelExameLimitadoAtendJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
