package br.gov.mec.aghu.exames.questionario.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrupoQuestaoDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoQuestaoJnDao;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelGrupoQuestaoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class GrupoQuestaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GrupoQuestaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelGrupoQuestaoDAO aelGrupoQuestaoDAO;
	
	@Inject
	private AelGrupoQuestaoJnDao aelGrupoQuestaoJnDao;

	private static final long serialVersionUID = 4547421033969073780L;

	public void persistir(final AelGrupoQuestao aelGrupoQuestao) throws ApplicationBusinessException {
		if (aelGrupoQuestao.getSeq() == null) {
			this.getAelGrupoQuestaoDAO().persistir(aelGrupoQuestao);
			
		} else {
			final AelGrupoQuestao aelGrupoQuestaoOld = this.getAelGrupoQuestaoDAO().obterOriginal(aelGrupoQuestao.getSeq());
			this.getAelGrupoQuestaoDAO().merge(aelGrupoQuestao);
			this.executarAposAtualizar(aelGrupoQuestao, aelGrupoQuestaoOld);
		}
	}

	/**
	 * @ORADB AELT_GRQ_ARU
	 */
	protected void executarAposAtualizar(AelGrupoQuestao aelGrupoQuestao, AelGrupoQuestao aelGrupoQuestaoOld) throws ApplicationBusinessException {
		if (!CoreUtil.igual(aelGrupoQuestao.getSeq(), aelGrupoQuestaoOld.getSeq())
				|| !CoreUtil.igual(aelGrupoQuestao.getDescricao(), aelGrupoQuestaoOld.getDescricao())) {
			this.inserirJn(aelGrupoQuestaoOld, DominioOperacoesJournal.UPD);
		}
	}

	protected void inserirJn(final AelGrupoQuestao aelGrupoQuestao, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelGrupoQuestaoJn jn = BaseJournalFactory.getBaseJournal(operacao, AelGrupoQuestaoJn.class, servidorLogado.getUsuario());
		jn.setSeq(aelGrupoQuestao.getSeq());
		jn.setDescricao(aelGrupoQuestao.getDescricao());

		this.getAelGrupoQuestaoJnDao().persistir(jn);
	}

	private AelGrupoQuestaoJnDao getAelGrupoQuestaoJnDao() {
		return aelGrupoQuestaoJnDao;
	}

	public void remover(final AelGrupoQuestao aelGrupoQuestao) throws ApplicationBusinessException {
		this.getAelGrupoQuestaoDAO().remover(aelGrupoQuestao);
		this.executarAposRemover(aelGrupoQuestao);
	}

	/**
	 *  @ORADB AELT_GRQ_ARD
	 */
	private void executarAposRemover(AelGrupoQuestao aelGrupoQuestao) throws ApplicationBusinessException {
		this.inserirJn(aelGrupoQuestao, DominioOperacoesJournal.DEL);
	}

	protected AelGrupoQuestaoDAO getAelGrupoQuestaoDAO() {
		return aelGrupoQuestaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
