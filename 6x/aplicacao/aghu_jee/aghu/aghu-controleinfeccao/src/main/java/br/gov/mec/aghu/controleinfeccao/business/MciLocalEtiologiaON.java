package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciLocalEtiologiaDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciLocalEtiologiaJnDAO;
import br.gov.mec.aghu.model.MciLocalEtiologia;
import br.gov.mec.aghu.model.MciLocalEtiologiaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciLocalEtiologiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MciLocalEtiologiaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MciLocalEtiologiaDAO mciLocalEtiologiaDAO;
	
	@Inject
	private MciLocalEtiologiaJnDAO mciLocalEtiologiaJnDAO;
	
	private static final long serialVersionUID = -2836594109352751522L;

	
	public void inserirMciLocalEtiologia(MciLocalEtiologia localEtiologia) {

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		localEtiologia.setRapServidores(servidorLogado);
		localEtiologia.setCriadoEm(new Date());
		
		this.getMciLocalEtiologiaDAO().persistir(localEtiologia);
	}
	
	public void alterarMciLocalEtiologia(MciLocalEtiologia localEtiologia, MciLocalEtiologia localEtiologiaOriginal) {
		this.inserirJournalMciLocalEtiologia(localEtiologiaOriginal, DominioOperacoesJournal.UPD);
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		localEtiologia.setAlteradoEm(new Date());
		localEtiologia.setServidorMovimentado(servidorLogado);
		
		this.getMciLocalEtiologiaDAO().merge(localEtiologia);
	}
	
	public void excluirMciLocalEtiologia(MciLocalEtiologia localEtiologia) {
		this.inserirJournalMciLocalEtiologia(localEtiologia, DominioOperacoesJournal.DEL);
		
		this.getMciLocalEtiologiaDAO().remover(localEtiologia);
	}
	
	public void inserirJournalMciLocalEtiologia(MciLocalEtiologia localEtiologiaOriginal, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		MciLocalEtiologiaJn mciLocalEtiologiaJn = BaseJournalFactory.getBaseJournal(
				operacao, MciLocalEtiologiaJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		mciLocalEtiologiaJn.setEinTipo(localEtiologiaOriginal.getId().getEinTipo());
		mciLocalEtiologiaJn.setUnfSeq(localEtiologiaOriginal.getId().getUnfSeq());
		mciLocalEtiologiaJn.setIndFormaContabilizacao(localEtiologiaOriginal.getIndFormaContabilizacao());
		mciLocalEtiologiaJn.setIndSituacao(localEtiologiaOriginal.getIndSituacao());
		mciLocalEtiologiaJn.setCriadoEm(localEtiologiaOriginal.getCriadoEm());
		mciLocalEtiologiaJn.setSerMatricula(localEtiologiaOriginal.getRapServidores().getId().getMatricula());
		mciLocalEtiologiaJn.setSerVinCodigo(localEtiologiaOriginal.getRapServidores().getId().getVinCodigo());
		mciLocalEtiologiaJn.setAlteradoEm(localEtiologiaOriginal.getAlteradoEm());
		if (localEtiologiaOriginal.getServidorMovimentado() != null) {
			mciLocalEtiologiaJn.setSerMatriculaMovimentado(localEtiologiaOriginal.getServidorMovimentado().getId().getMatricula());
			mciLocalEtiologiaJn.setSerVinCodigoMovimentado(localEtiologiaOriginal.getServidorMovimentado().getId().getVinCodigo());
		}
		this.getMciLocalEtiologiaJnDAO().persistir(mciLocalEtiologiaJn);
	}
	
	protected MciLocalEtiologiaDAO getMciLocalEtiologiaDAO() {
		return this.mciLocalEtiologiaDAO;
	}
	
	protected MciLocalEtiologiaJnDAO getMciLocalEtiologiaJnDAO() {
		return this.mciLocalEtiologiaJnDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
