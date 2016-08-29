package br.gov.mec.aghu.business.bancosangue;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsOrientacaoComponentesDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsOrientacaoComponentesJnDAO;
import br.gov.mec.aghu.model.AbsOrientacaoComponentes;
import br.gov.mec.aghu.model.AbsOrientacaoComponentesJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AbsOrientacaoComponentesON  extends BaseBusiness {

	
	private static final Log LOG = LogFactory.getLog(AbsOrientacaoComponentesON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AbsOrientacaoComponentesDAO absOrientacaoComponentesDAO;
	
	@Inject
	private AbsOrientacaoComponentesJnDAO absOrientacaoComponentesJnDAO;

	private static final long serialVersionUID = 1061239213249749561L;
	
	protected AbsOrientacaoComponentesJnDAO getAbsOrientacaoComponentesJnDAO(){
		return absOrientacaoComponentesJnDAO;
	}
	
	protected AbsOrientacaoComponentesDAO getAbsOrientacaoComponentesDAO(){
		return absOrientacaoComponentesDAO;
	}
	
	public void atualizarAbsOrientacaoComponentes(AbsOrientacaoComponentes absOrientacaoComponentes) throws ApplicationBusinessException {
		absOrientacaoComponentes.setDescricao(absOrientacaoComponentes.getDescricao().toUpperCase());
		getAbsOrientacaoComponentesDAO().atualizar(absOrientacaoComponentes);
		inserirAbsOrientacaoComponentesJournal(absOrientacaoComponentes);
	}

	private void inserirAbsOrientacaoComponentesJournal(AbsOrientacaoComponentes absOrientacaoComponentes) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AbsOrientacaoComponentes original = getAbsOrientacaoComponentesDAO().obterOriginal(absOrientacaoComponentes);
		AbsOrientacaoComponentesJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AbsOrientacaoComponentesJn.class, servidorLogado.getUsuario());
		journal.setCsaCodigo(original.getId().getCsaCodigo());
		journal.setSeqp(original.getId().getSeqp());
		journal.setDescricao(original.getDescricao());
		journal.setIndSituacao(original.getIndSituacao());
		journal.setOrdem(original.getOrdem());
		journal.setCriadoEm(original.getCriadoEm());
		journal.setServidor(original.getServidor());
		getAbsOrientacaoComponentesJnDAO().persistir(journal);
		getAbsOrientacaoComponentesJnDAO().flush();
	}

	public void persistirAbsOrientacaoComponentes(AbsOrientacaoComponentes absOrientacaoComponentes) throws ApplicationBusinessException {
		Short seqp = getAbsOrientacaoComponentesDAO().pesquisarMaxSeqp(absOrientacaoComponentes.getId().getCsaCodigo());
		
		if(seqp != null) {
			seqp = (short) (seqp + Short.valueOf("1"));
		} else {
			seqp = Short.valueOf("1");
		}
		
		absOrientacaoComponentes.getId().setSeqp(seqp);
		absOrientacaoComponentes.setCriadoEm(new Date());
		absOrientacaoComponentes.setServidor(getServidorLogadoFacade().obterServidorLogado());
		absOrientacaoComponentes.setDescricao(absOrientacaoComponentes.getDescricao().toUpperCase());
		getAbsOrientacaoComponentesDAO().persistir(absOrientacaoComponentes);
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
