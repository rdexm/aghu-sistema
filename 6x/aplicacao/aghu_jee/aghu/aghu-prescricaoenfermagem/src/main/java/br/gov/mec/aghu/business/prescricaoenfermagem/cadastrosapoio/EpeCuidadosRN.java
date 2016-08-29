package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpeCuidadosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadosJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class EpeCuidadosRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(EpeCuidadosRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private EpeCuidadosDAO epeCuidadosDAO;
	
	@Inject
	private EpeCuidadosJnDAO epeCuidadosJnDAO;

	private static final long serialVersionUID = 2562067726841136788L;

	public void atualizarEpeCuidados(EpeCuidados epeCuidados) {
		EpeCuidados epeCuidadosOriginal = this.getEpeCuidadosDAO().obterOriginal(epeCuidados.getSeq());
		this.inserirJournalEpeCuidados(epeCuidadosOriginal, DominioOperacoesJournal.UPD);
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		epeCuidados.setAlteradoEm(new Date());
		epeCuidados.setRapServidorMovimentado(servidorLogado);
		
		this.getEpeCuidadosDAO().merge(epeCuidados);
	}
	
	public void inserirJournalEpeCuidados(EpeCuidados epeCuidadosOriginal, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		EpeCuidadosJn epeCuidadosJn = BaseJournalFactory.getBaseJournal(
				operacao, EpeCuidadosJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		epeCuidadosJn.setSeq(epeCuidadosOriginal.getSeq());
		epeCuidadosJn.setDescricao(epeCuidadosOriginal.getDescricao());
		epeCuidadosJn.setIndSemDiagnostico(epeCuidadosOriginal.getIndSemDiagnostico());
		epeCuidadosJn.setIndSituacao(epeCuidadosOriginal.getIndSituacao());
		epeCuidadosJn.setIndDigitaComplemento(epeCuidadosOriginal.getIndDigitaComplemento());
		epeCuidadosJn.setCriadoEm(epeCuidadosOriginal.getCriadoEm());
		epeCuidadosJn.setServidor(epeCuidadosOriginal.getServidor());
		epeCuidadosJn.setRotina(epeCuidadosOriginal.getRotina());
		epeCuidadosJn.setInformacoesAdicionais(epeCuidadosOriginal.getInformacoesAdicionais());
		epeCuidadosJn.setFrequencia(epeCuidadosOriginal.getFrequencia());
		epeCuidadosJn.setTipoFrequenciaAprazamento(epeCuidadosOriginal.getTipoFrequenciaAprazamento());
		epeCuidadosJn.setIndCci(epeCuidadosOriginal.getIndCci());
		epeCuidadosJn.setIndRotina(epeCuidadosOriginal.getIndRotina());
		epeCuidadosJn.setTempo(epeCuidadosOriginal.getTempo());
		epeCuidadosJn.setAlteradoEm(epeCuidadosOriginal.getAlteradoEm());
		epeCuidadosJn.setRapServidorMovimentado(epeCuidadosOriginal.getRapServidorMovimentado());
		
		this.getEpeCuidadosJnDAO().persistir(epeCuidadosJn);
	}
	
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public EpeCuidadosDAO getEpeCuidadosDAO() {
		return epeCuidadosDAO;
	}

	public void setEpeCuidadosDAO(EpeCuidadosDAO epeCuidadosDAO) {
		this.epeCuidadosDAO = epeCuidadosDAO;
	}

	public EpeCuidadosJnDAO getEpeCuidadosJnDAO() {
		return epeCuidadosJnDAO;
	}

	public void setEpeCuidadosJnDAO(EpeCuidadosJnDAO epeCuidadosJnDAO) {
		this.epeCuidadosJnDAO = epeCuidadosJnDAO;
	}
}
