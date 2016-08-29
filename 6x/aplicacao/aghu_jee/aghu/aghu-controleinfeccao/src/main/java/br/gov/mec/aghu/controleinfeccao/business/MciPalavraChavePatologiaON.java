package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciPalavraChavePatologiaDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciPalavraChavePatologiaJnDAO;
import br.gov.mec.aghu.model.MciPalavraChavePatologia;
import br.gov.mec.aghu.model.MciPalavraChavePatologiaId;
import br.gov.mec.aghu.model.MciPalavraChavePatologiaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciPalavraChavePatologiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MciPalavraChavePatologiaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MciPalavraChavePatologiaDAO mciPalavraChavePatologiaDAO;
	
	@Inject
	private MciPalavraChavePatologiaJnDAO mciPalavraChavePatologiaJnDAO;
	
	private static final long serialVersionUID = -7284594109352751522L;

	
	public void inserirMciPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia, Integer codigoPatologia) {

		MciPalavraChavePatologiaId id = new MciPalavraChavePatologiaId();
		id.setPaiSeq(codigoPatologia);
		id.setSeqp(this.mciPalavraChavePatologiaDAO.obterMaxSeqpPalavraChavePatologia(codigoPatologia));
		
		palavraChavePatologia.setId(id);
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		palavraChavePatologia.setRapServidores(servidorLogado);
		palavraChavePatologia.setCriadoEm(new Date());
		
		this.getMciPalavraChavePatologiaDAO().persistir(palavraChavePatologia);
	}
	
	public void alterarMciPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia, MciPalavraChavePatologia palavraChavePatologiaOriginal) {
		this.inserirJournalMciPalavraChavePatologia(palavraChavePatologiaOriginal, DominioOperacoesJournal.UPD);
		
		palavraChavePatologia.setAlteradoEm(new Date());
		
		this.getMciPalavraChavePatologiaDAO().merge(palavraChavePatologia);
	}
	
	public void excluirMciPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologia) {
		this.inserirJournalMciPalavraChavePatologia(palavraChavePatologia, DominioOperacoesJournal.DEL);
		
		MciPalavraChavePatologia itemExclusao = this.mciPalavraChavePatologiaDAO.obterPorChavePrimaria(palavraChavePatologia.getId());
		
		this.getMciPalavraChavePatologiaDAO().remover(itemExclusao);
	}
	
	public void inserirJournalMciPalavraChavePatologia(MciPalavraChavePatologia palavraChavePatologiaOriginal, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		MciPalavraChavePatologiaJn mciPalavraChavePatologiaJn = BaseJournalFactory.getBaseJournal(
				operacao, MciPalavraChavePatologiaJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		mciPalavraChavePatologiaJn.setPaiSeq(palavraChavePatologiaOriginal.getId().getPaiSeq());
		mciPalavraChavePatologiaJn.setSeqp(palavraChavePatologiaOriginal.getId().getSeqp());
		mciPalavraChavePatologiaJn.setDescricao(palavraChavePatologiaOriginal.getDescricao());
		mciPalavraChavePatologiaJn.setIndSituacao(palavraChavePatologiaOriginal.getIndSituacao());
		mciPalavraChavePatologiaJn.setCriadoEm(palavraChavePatologiaOriginal.getCriadoEm());
		mciPalavraChavePatologiaJn.setSerMatricula(palavraChavePatologiaOriginal.getRapServidores().getId().getMatricula());
		mciPalavraChavePatologiaJn.setSerVinCodigo(palavraChavePatologiaOriginal.getRapServidores().getId().getVinCodigo());
		// não existe no banco
		//mciPalavraChavePatologiaJn.setAlteradoEm(palavraChavePatologiaOriginal.getAlteradoEm());
		
		this.getMciPalavraChavePatologiaJnDAO().persistir(mciPalavraChavePatologiaJn);
	}
	
	protected MciPalavraChavePatologiaDAO getMciPalavraChavePatologiaDAO() {
		return this.mciPalavraChavePatologiaDAO;
	}
	
	protected MciPalavraChavePatologiaJnDAO getMciPalavraChavePatologiaJnDAO() {
		return this.mciPalavraChavePatologiaJnDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
