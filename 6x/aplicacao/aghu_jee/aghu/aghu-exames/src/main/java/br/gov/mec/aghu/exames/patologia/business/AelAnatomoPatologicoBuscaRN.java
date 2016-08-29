package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoJnDAO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelAnatomoPatologicosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@SuppressWarnings("ucd")
@Stateless
public class AelAnatomoPatologicoBuscaRN extends BaseBusiness {
	
	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelAnatomoPatologicoBuscaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelAnatomoPatologicoJnDAO aelAnatomoPatologicoJnDAO;

	private static final long serialVersionUID = 4879378548867391256L;

	/**
	 * ORADB Trigger AELT_LUM_BRI
	 * 
	 * @param anatomoPatologico
	 */
	protected void executarAntesInserir(AelAnatomoPatologico anatomoPatologico) throws BaseException {
		anatomoPatologico.setCriadoEm(new Date());
		getLaudoUnicoRN().aelpAtuServidor(anatomoPatologico);
		getLaudoUnicoRN().rnLumpVerServidor(anatomoPatologico.getServidor());
	}
	

	/**
	 * ORADB Trigger AELT_LUM_BRU
	 * 
	 * @param anatomoPatologico
 	 * @param anatomoPatologicoOld
	 */
	protected void executarAntesAtualizar(AelAnatomoPatologico anatomoPatologico, AelAnatomoPatologico anatomoPatologicoOld) throws BaseException {
		if(CoreUtil.modificados(anatomoPatologico.getServidor(), anatomoPatologicoOld.getServidor())) {
			getLaudoUnicoRN().rnLumpVerServidor(anatomoPatologico.getServidor());			
		}
	}

	/**
	 * ORADB Trigger AELT_LUM_ARU
	 * 
	 * @param anatomoPatologico
 	 * @param anatomoPatologicoOld
	 */
	protected void executarAposAtualizar(AelAnatomoPatologico anatomoPatologico, AelAnatomoPatologico anatomoPatologicoOld) throws BaseException {
		if(CoreUtil.modificados(anatomoPatologico.getSeq(), anatomoPatologicoOld.getSeq())
			|| CoreUtil.modificados(anatomoPatologico.getNumeroAp(), anatomoPatologicoOld.getNumeroAp())
			|| CoreUtil.modificados(anatomoPatologico.getCriadoEm(), anatomoPatologicoOld.getCriadoEm())
			|| CoreUtil.modificados(anatomoPatologico.getAtendimentoDiversos(), anatomoPatologicoOld.getAtendimentoDiversos())
			|| CoreUtil.modificados(anatomoPatologico.getAtendimento(), anatomoPatologicoOld.getAtendimento())
			|| CoreUtil.modificados(anatomoPatologico.getServidor(), anatomoPatologicoOld.getServidor())
		) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelAnatomoPatologicosJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelAnatomoPatologicosJn.class, servidorLogado.getUsuario());
			jn.setSeq(anatomoPatologicoOld.getSeq());
			jn.setNumeroAp(anatomoPatologicoOld.getNumeroAp());
			jn.setCriadoEm(anatomoPatologicoOld.getCriadoEm());
			jn.setAtendimentoDiversos(anatomoPatologicoOld.getAtendimentoDiversos());
			jn.setAtendimento(anatomoPatologicoOld.getAtendimento());
			jn.setServidor(anatomoPatologicoOld.getServidor());
			getAelAnatomoPatologicoJnDAO().persistir(jn);
		}
	}


	/**
	 * ORADB Trigger AELT_LUM_ARD
	 * 
 	 * @param anatomoPatologicoOld
	 */
	protected void executarAposExcluir(AelAnatomoPatologico anatomoPatologicoOld) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelAnatomoPatologicosJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelAnatomoPatologicosJn.class, servidorLogado.getUsuario());
		jn.setSeq(anatomoPatologicoOld.getSeq());
		jn.setNumeroAp(anatomoPatologicoOld.getNumeroAp());
		jn.setCriadoEm(anatomoPatologicoOld.getCriadoEm());
		jn.setAtendimentoDiversos(anatomoPatologicoOld.getAtendimentoDiversos());
		jn.setAtendimento(anatomoPatologicoOld.getAtendimento());
		jn.setServidor(anatomoPatologicoOld.getServidor());
		getAelAnatomoPatologicoJnDAO().persistir(jn);
	}


	protected AelAnatomoPatologicoJnDAO getAelAnatomoPatologicoJnDAO() {
		return aelAnatomoPatologicoJnDAO;
	}

	private LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
