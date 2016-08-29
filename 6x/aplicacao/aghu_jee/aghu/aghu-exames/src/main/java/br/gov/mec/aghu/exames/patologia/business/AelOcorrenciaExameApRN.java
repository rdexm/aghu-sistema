package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelOcorrenciaExameApDAO;
import br.gov.mec.aghu.exames.dao.AelOcorrenciaExameApJnDAO;
import br.gov.mec.aghu.model.AelOcorrenciaExameAp;
import br.gov.mec.aghu.model.AelOcorrenciaExameApJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelOcorrenciaExameApRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelOcorrenciaExameApRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelOcorrenciaExameApDAO aelOcorrenciaExameApDAO;
	
	@Inject
	private AelOcorrenciaExameApJnDAO aelOcorrenciaExameApJnDAO;

	private static final long serialVersionUID = 4879378548867391256L;
	

	public void inserirAelOcorrenciaExameAp(final AelOcorrenciaExameAp aelOcorrenciaExameAp) throws ApplicationBusinessException {
		final AelOcorrenciaExameApDAO dao = getAelOcorrenciaExameApDAO();
		this.executarAntesInserirAelExtatoExameAp(aelOcorrenciaExameAp);
		dao.persistir(aelOcorrenciaExameAp);
		dao.flush();
	}
	
	public void excluir(final AelOcorrenciaExameAp aelOcorrenciaExameAp, String usuarioLogado) throws BaseException {
		final AelOcorrenciaExameApDAO dao = getAelOcorrenciaExameApDAO();
		dao.remover(aelOcorrenciaExameAp);
		this.executarAposExcluir(aelOcorrenciaExameAp, usuarioLogado);
		dao.flush();
	}
	
	private void executarAposExcluir(AelOcorrenciaExameAp aelOcorrenciaExameAp, String usuarioLogado) {
		insereJournal(aelOcorrenciaExameAp, DominioOperacoesJournal.DEL, usuarioLogado);		
	}

	private void insereJournal(AelOcorrenciaExameAp aelOcorrenciaExameAp, DominioOperacoesJournal operacao, String usuarioLogado) {
		final AelOcorrenciaExameApJn jn =  BaseJournalFactory.getBaseJournal(operacao, AelOcorrenciaExameApJn.class, usuarioLogado);
		
		jn.setNomeUsuario(usuarioLogado);
		jn.setOperacao(operacao);
		jn.setLuxSeq(aelOcorrenciaExameAp.getId().getLuxSeq());
		jn.setSeqp(aelOcorrenciaExameAp.getId().getSeqp());
		
		jn.setCriadoEm(aelOcorrenciaExameAp.getCriadoEm());
		jn.setDescricao(aelOcorrenciaExameAp.getDescricao());
		jn.setSituacao(aelOcorrenciaExameAp.getSituacao().toString());
		jn.setSerMatricula(aelOcorrenciaExameAp.getRapServidores().getId().getMatricula());
		jn.setSerVinCodigo(aelOcorrenciaExameAp.getRapServidores().getId().getVinCodigo());
		
		final AelOcorrenciaExameApJnDAO dao = getAelOcorrenciaExameApJnDAO();
		dao.persistir(jn);
		
	}

		
	/**
	 * ORADB Trigger AELT_LU6_BRI
	 * 
	 * @param AelOcorrenciaExameAp
	 * @throws ApplicationBusinessException  
	 */
	protected void executarAntesInserirAelExtatoExameAp(
			AelOcorrenciaExameAp AelOcorrenciaExameAp) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelOcorrenciaExameAp.setCriadoEm(new Date());
		AelOcorrenciaExameAp.setRapServidores(servidorLogado);
		
	}

	private AelOcorrenciaExameApDAO getAelOcorrenciaExameApDAO() {
		return aelOcorrenciaExameApDAO;
	}

	private IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	private AelOcorrenciaExameApJnDAO getAelOcorrenciaExameApJnDAO() {
		return aelOcorrenciaExameApJnDAO;
	}	
}
