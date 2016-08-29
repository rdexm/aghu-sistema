package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmCuidadoUsualJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MpmCuidadoUsualRN extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(MpmCuidadoUsualRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmCuidadoUsualDAO mpmCuidadoUsualDAO;
	
	@Inject
	private MpmCuidadoUsualJnDAO mpmCuidadoUsualJnDAO;
	
	private static final long serialVersionUID = 3764492448693425121L;
	
	public void atualizarMpmCuidadoUsual(MpmCuidadoUsual mpmCuidadoUsual) {
		MpmCuidadoUsual mpmCuidadoUsualOriginal = this.getMpmCuidadoUsualDAO().obterOriginal(mpmCuidadoUsual.getSeq());
		this.inserirJournalMpmCuidadoUsual(mpmCuidadoUsualOriginal, DominioOperacoesJournal.UPD);
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		mpmCuidadoUsual.setAlteradoEm(new Date());
		mpmCuidadoUsual.setRapServidorMovimentado(servidorLogado);
		
		this.getMpmCuidadoUsualDAO().merge(mpmCuidadoUsual);
	}
	
	public void inserirJournalMpmCuidadoUsual(MpmCuidadoUsual mpmCuidadoUsualOriginal, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		MpmCuidadoUsualJn mpmCuidadoUsualJn = BaseJournalFactory.getBaseJournal(
				operacao, MpmCuidadoUsualJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		mpmCuidadoUsualJn.setSeq(mpmCuidadoUsualOriginal.getSeq());
		mpmCuidadoUsualJn.setMpmTipoFreqAprazamentos(mpmCuidadoUsualOriginal.getMpmTipoFreqAprazamentos());
		mpmCuidadoUsualJn.setRapServidores(mpmCuidadoUsualOriginal.getRapServidores());
		mpmCuidadoUsualJn.setDescricao(mpmCuidadoUsualOriginal.getDescricao());
		mpmCuidadoUsualJn.setIndImpMapaDietas(mpmCuidadoUsualOriginal.getIndImpMapaDietas());
		mpmCuidadoUsualJn.setCriadoEm(mpmCuidadoUsualOriginal.getCriadoEm());
		mpmCuidadoUsualJn.setIndSituacao(mpmCuidadoUsualOriginal.getIndSituacao());
		mpmCuidadoUsualJn.setFrequencia(mpmCuidadoUsualOriginal.getFrequencia());
		mpmCuidadoUsualJn.setIndDigitaComplemento(mpmCuidadoUsualOriginal.getIndDigitaComplemento());
		mpmCuidadoUsualJn.setIndCci(mpmCuidadoUsualOriginal.getIndCci());
		mpmCuidadoUsualJn.setIndOutros(mpmCuidadoUsualOriginal.getIndOutros());
		mpmCuidadoUsualJn.setIndUsoQuimioterapia(mpmCuidadoUsualOriginal.getIndUsoQuimioterapia());
		mpmCuidadoUsualJn.setIndUsoDialise(mpmCuidadoUsualOriginal.getIndUsoDialise());
		mpmCuidadoUsualJn.setTempo(mpmCuidadoUsualOriginal.getTempo());
		mpmCuidadoUsualJn.setIndSondaVesical(mpmCuidadoUsualOriginal.getIndSondaVesical());
		mpmCuidadoUsualJn.setIndCpa(mpmCuidadoUsualOriginal.getIndCpa());
		mpmCuidadoUsualJn.setAlteradoEm(mpmCuidadoUsualOriginal.getAlteradoEm());
		mpmCuidadoUsualJn.setRapServidorMovimentado(mpmCuidadoUsualOriginal.getRapServidorMovimentado());
		this.getMpmCuidadoUsualJnDAO().persistir(mpmCuidadoUsualJn);
	}
	
	protected MpmCuidadoUsualDAO getMpmCuidadoUsualDAO(){
		return mpmCuidadoUsualDAO;
	}
	
	public MpmCuidadoUsualJnDAO getMpmCuidadoUsualJnDAO() {
		return mpmCuidadoUsualJnDAO;
	}

	public void setMpmCuidadoUsualJnDAO(MpmCuidadoUsualJnDAO mpmCuidadoUsualJnDAO) {
		this.mpmCuidadoUsualJnDAO = mpmCuidadoUsualJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
