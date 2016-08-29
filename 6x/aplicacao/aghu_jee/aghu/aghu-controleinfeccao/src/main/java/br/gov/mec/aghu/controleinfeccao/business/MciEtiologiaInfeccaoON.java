package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciEtiologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciEtiologiaInfeccaoJnDAO;
import br.gov.mec.aghu.model.MciEtiologiaInfeccao;
import br.gov.mec.aghu.model.MciEtiologiaInfeccaoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciEtiologiaInfeccaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MciEtiologiaInfeccaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MciEtiologiaInfeccaoDAO mciEtiologiaInfeccaoDAO;
	
	@Inject
	private MciEtiologiaInfeccaoJnDAO mciEtiologiaInfeccaoJnDAO;
	
	private static final long serialVersionUID = -3059284109352751522L;

	
	public void atualizarMciEtiologiaInfeccao(MciEtiologiaInfeccao etiologiaInfeccao, MciEtiologiaInfeccao etiologiaInfeccaoOriginal) {
		this.inserirJournalMciEtiologiaInfeccao(etiologiaInfeccaoOriginal, DominioOperacoesJournal.UPD);
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		etiologiaInfeccao.setAlteradoEm(new Date());
		etiologiaInfeccao.setServidorMovimentado(servidorLogado);
		
		this.getMciEtiologiaInfeccaoDAO().atualizar(etiologiaInfeccao);
	}
	
	public void inserirJournalMciEtiologiaInfeccao(MciEtiologiaInfeccao etiologiaInfeccaoOriginal, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		MciEtiologiaInfeccaoJn mciEtiologiaInfeccaoJn = BaseJournalFactory.getBaseJournal(
				operacao, MciEtiologiaInfeccaoJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		mciEtiologiaInfeccaoJn.setCodigo(etiologiaInfeccaoOriginal.getCodigo());
		mciEtiologiaInfeccaoJn.setDescricao(etiologiaInfeccaoOriginal.getDescricao());
		mciEtiologiaInfeccaoJn.setSerMatricula(etiologiaInfeccaoOriginal.getServidor().getId().getMatricula());
		mciEtiologiaInfeccaoJn.setSerVinCodigo(etiologiaInfeccaoOriginal.getServidor().getId().getVinCodigo());
		mciEtiologiaInfeccaoJn.setSituacao(etiologiaInfeccaoOriginal.getSituacao());
		mciEtiologiaInfeccaoJn.setCriadoEm(etiologiaInfeccaoOriginal.getCriadoEm());
		mciEtiologiaInfeccaoJn.setAlteradoEm(etiologiaInfeccaoOriginal.getAlteradoEm());
		mciEtiologiaInfeccaoJn.setTextoNotificacao(etiologiaInfeccaoOriginal.getTextoNotificacao());
		if (etiologiaInfeccaoOriginal.getUnidadeFuncional() != null) {
			mciEtiologiaInfeccaoJn.setUnfSeq(etiologiaInfeccaoOriginal.getUnidadeFuncional().getSeq());
		}
		if (etiologiaInfeccaoOriginal.getServidorMovimentado() != null) {
			mciEtiologiaInfeccaoJn.setSerMatriculaMovimentado(etiologiaInfeccaoOriginal.getServidorMovimentado().getId().getMatricula());
			mciEtiologiaInfeccaoJn.setSerVinCodigoMovimentado(etiologiaInfeccaoOriginal.getServidorMovimentado().getId().getVinCodigo());
		}
		this.getMciEtiologiaInfeccaoJnDAO().persistir(mciEtiologiaInfeccaoJn);
	}
	
	protected MciEtiologiaInfeccaoDAO getMciEtiologiaInfeccaoDAO() {
		return this.mciEtiologiaInfeccaoDAO;
	}
	
	protected MciEtiologiaInfeccaoJnDAO getMciEtiologiaInfeccaoJnDAO() {
		return this.mciEtiologiaInfeccaoJnDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
