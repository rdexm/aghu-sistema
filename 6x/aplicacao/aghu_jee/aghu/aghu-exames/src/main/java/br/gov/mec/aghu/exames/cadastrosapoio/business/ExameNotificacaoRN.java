package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExameNotificacaoJnDAO;
import br.gov.mec.aghu.exames.dao.AelExamesNotificacaoDAO;
import br.gov.mec.aghu.model.AelExameNotificacaoJn;
import br.gov.mec.aghu.model.AelExamesNotificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ExameNotificacaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ExameNotificacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExameNotificacaoJnDAO aelExameNotificacaoJnDAO;
	
	@Inject
	private AelExamesNotificacaoDAO aelExamesNotificacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7163968429111406656L;

	public void remover(AelExamesNotificacao exameNotificacao) throws ApplicationBusinessException {
		this.getAelExamesNotificacaoDAO().remover(exameNotificacao);
		this.posRemoverExameNotificacao(exameNotificacao);
	}
	
	public void persistir(AelExamesNotificacao exameNotificacao) throws ApplicationBusinessException {
		this.preInserirExameNotificacao(exameNotificacao);
		this.getAelExamesNotificacaoDAO().persistir(exameNotificacao);
	}
	
	public void atualizar(AelExamesNotificacao exameNotificacao, AelExamesNotificacao exameNotificacaoOld) throws ApplicationBusinessException {
		this.getAelExamesNotificacaoDAO().merge(exameNotificacao);
		this.posAtualizarExameNotificacao(exameNotificacao, exameNotificacaoOld);	
	}
	
	/**
	 * ORADB
	 * Trigger AELT_EXN_BRI
	 * @param exameNotificacao
	 * @throws ApplicationBusinessException  
	 */
	public void preInserirExameNotificacao(AelExamesNotificacao exameNotificacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		exameNotificacao.setCriadoEm(new Date());
		exameNotificacao.setServidor(servidorLogado);
	}
	
		
	
	/**
	 * ORADB
	 * Trigger AELT_EXN_ARD
	 * @param exameNotificacao
	 * @throws ApplicationBusinessException 
	 */
	public void posRemoverExameNotificacao(AelExamesNotificacao exameNotificacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelExameNotificacaoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL,
				AelExameNotificacaoJn.class, servidorLogado.getUsuario());
		jn.setCalSeq(exameNotificacao.getId().getCalSeq());
		jn.setCriadoEm(exameNotificacao.getCriadoEm());
		jn.setEmaExaSigla(exameNotificacao.getId().getEmaExaSigla());
		jn.setEmaManSeq(exameNotificacao.getId().getEmaManSeq());
		jn.setSituacao(exameNotificacao.getSituacao());
		jn.setSerMatricula(exameNotificacao.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(exameNotificacao.getServidor().getId().getVinCodigo());
		this.getAelExameNotificacaoJnDAO().persistir(jn);
	}
	
	/**
	 * ORADB
	 * Trigger AELT_EXN_ARU
	 * @param exameNotifi
	 * @param examesLimitadoAtendOld
	 * @throws ApplicationBusinessException 
	 */
	public void posAtualizarExameNotificacao(AelExamesNotificacao exameNotificacao, AelExamesNotificacao exameNotificacaoOld) throws ApplicationBusinessException{
		if (CoreUtil.modificados(exameNotificacao.getId().getEmaExaSigla(), exameNotificacaoOld.getId().getEmaExaSigla())
				|| CoreUtil.modificados(exameNotificacao.getId().getEmaManSeq(), exameNotificacaoOld.getId().getEmaManSeq())
				|| CoreUtil.modificados(exameNotificacao.getId().getCalSeq(), exameNotificacaoOld.getId().getCalSeq())
				|| CoreUtil.modificados(exameNotificacao.getCriadoEm(), exameNotificacaoOld.getCriadoEm())
				|| CoreUtil.modificados(exameNotificacao.getSituacao(), exameNotificacaoOld.getSituacao())
				|| CoreUtil.modificados(exameNotificacao.getServidor(), exameNotificacaoOld.getServidor())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
				AelExameNotificacaoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD,
						AelExameNotificacaoJn.class, servidorLogado.getUsuario());
			
				jn.setCalSeq(exameNotificacaoOld.getId().getCalSeq());
				jn.setCriadoEm(exameNotificacaoOld.getCriadoEm());
				jn.setEmaExaSigla(exameNotificacaoOld.getId().getEmaExaSigla());
				jn.setEmaManSeq(exameNotificacaoOld.getId().getEmaManSeq());
				jn.setSerMatricula(exameNotificacaoOld.getServidor().getId().getMatricula());
				jn.setSerVinCodigo(exameNotificacaoOld.getServidor().getId().getVinCodigo());
				jn.setSituacao(exameNotificacaoOld.getSituacao());
				this.getAelExameNotificacaoJnDAO().persistir(jn);
		}
	}
	
	protected AelExamesNotificacaoDAO getAelExamesNotificacaoDAO() {
		return aelExamesNotificacaoDAO;
	}
	
	protected AelExameNotificacaoJnDAO getAelExameNotificacaoJnDAO() {
		return aelExameNotificacaoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
