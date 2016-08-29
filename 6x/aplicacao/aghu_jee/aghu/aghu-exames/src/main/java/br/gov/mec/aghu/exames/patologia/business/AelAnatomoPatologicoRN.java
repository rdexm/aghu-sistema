package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoDAO;
import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoJnDAO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelAnatomoPatologicosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelAnatomoPatologicoRN extends BaseBusiness {

	private static final long serialVersionUID = 4879378548867391256L;
	
	private static final Log LOG = LogFactory.getLog(AelAnatomoPatologicoRN.class);
	
	@Inject
	private AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO;
	
	@Inject
	private AelAnatomoPatologicoJnDAO aelAnatomoPatologicoJnDAO;
	
	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;	
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	

	/**
	 * ORADB Trigger AELT_LUM_BRI
	 * 
	 * @param anatomoPatologico
	 */
	protected void executarAntesInserir(AelAnatomoPatologico anatomoPatologico) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		anatomoPatologico.setServidor(servidorLogado);
		anatomoPatologico.setCriadoEm(new Date());
		//Migração da Versão 5 Para 7 - Não existe estas 2 linhas na versão 5
		//getLaudoUnicoRN().aelpAtuServidor(anatomoPatologico);
		//getLaudoUnicoRN().rnLumpVerServidor(anatomoPatologico.getServidor());
		// Verificação de perfil retirada e passado para verificação de permissão "receberTodasAmostras"
		//this.verificarPerfilUsuarioPatologista(aelAnatomoPatologico.getServidor());		
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
	protected void executarAposAtualizar(AelAnatomoPatologico anatomoPatologico, AelAnatomoPatologico anatomoPatologicoOld, String usuarioLogado) throws BaseException {
		if(CoreUtil.modificados(anatomoPatologico.getSeq(), anatomoPatologicoOld.getSeq())
			|| CoreUtil.modificados(anatomoPatologico.getNumeroAp(), anatomoPatologicoOld.getNumeroAp())
			|| CoreUtil.modificados(anatomoPatologico.getCriadoEm(), anatomoPatologicoOld.getCriadoEm())
			|| CoreUtil.modificados(anatomoPatologico.getAtendimentoDiversos(), anatomoPatologicoOld.getAtendimentoDiversos())
			|| CoreUtil.modificados(anatomoPatologico.getAtendimento(), anatomoPatologicoOld.getAtendimento())
			|| CoreUtil.modificados(anatomoPatologico.getServidor(), anatomoPatologicoOld.getServidor())
		) {
			AelAnatomoPatologicosJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelAnatomoPatologicosJn.class, usuarioLogado);
			jn.setSeq(anatomoPatologicoOld.getSeq());
			jn.setNumeroAp(anatomoPatologicoOld.getNumeroAp());
			jn.setCriadoEm(anatomoPatologicoOld.getCriadoEm());
			jn.setAtendimentoDiversos(anatomoPatologicoOld.getAtendimentoDiversos());
			jn.setAtendimento(anatomoPatologicoOld.getAtendimento());
			jn.setServidor(anatomoPatologicoOld.getServidor());
			getAelAnatomoPatologicoJnDAO().persistir(jn);
		}
	}

	public void excluir(final AelAnatomoPatologico aelInformacaoClinicaAP, String usuarioLogado) throws BaseException {
		final AelAnatomoPatologicoDAO dao = getAelAnatomoPatologicoDAO();
		dao.remover(aelInformacaoClinicaAP);
		this.executarAposExcluir(aelInformacaoClinicaAP, usuarioLogado);
	}
	
	/**
	 * ORADB Trigger AELT_LUM_ARD
	 * 
 	 * @param anatomoPatologicoOld
	 */
	protected void executarAposExcluir(AelAnatomoPatologico anatomoPatologicoOld, String usuarioLogado) throws BaseException {
		AelAnatomoPatologicosJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelAnatomoPatologicosJn.class, usuarioLogado);
		jn.setSeq(anatomoPatologicoOld.getSeq());
		jn.setNumeroAp(anatomoPatologicoOld.getNumeroAp());
		jn.setCriadoEm(anatomoPatologicoOld.getCriadoEm());
		jn.setAtendimentoDiversos(anatomoPatologicoOld.getAtendimentoDiversos());
		jn.setAtendimento(anatomoPatologicoOld.getAtendimento());
		jn.setServidor(anatomoPatologicoOld.getServidor());
		getAelAnatomoPatologicoJnDAO().persistir(jn);
	}
	
	public void persistir(final AelAnatomoPatologico aelAnatomoPatologico) throws BaseException {
		if (aelAnatomoPatologico.getSeq() == null) {
			this.inserir(aelAnatomoPatologico);
		} else {
			this.atualizar(aelAnatomoPatologico, getAelAnatomoPatologicoDAO().obterPorChavePrimaria(aelAnatomoPatologico.getSeq()));
		}
	}

	public void atualizar(final AelAnatomoPatologico aelAnatomoPatologicoNew, final AelAnatomoPatologico aelAnatomoPatologicoOld) throws ApplicationBusinessException {
		// Verificação de perfil retirada (trigger AELT_LUM_BRU) e passado para verificação de permissão "receberTodasAmostras"
		//this.executarAntesAtualizar(aelAnatomoPatologicoNew, aelAnatomoPatologicoOld);
		final AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO = this.getAelAnatomoPatologicoDAO();
		aelAnatomoPatologicoDAO.atualizar(aelAnatomoPatologicoNew);
		this.executarDepoisAtualizar(aelAnatomoPatologicoNew, aelAnatomoPatologicoOld);
		aelAnatomoPatologicoDAO.flush();
	}

	public void inserir(final AelAnatomoPatologico aelAnatomoPatologico) throws BaseException {
		this.executarAntesInserir(aelAnatomoPatologico);
		final AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO = this.getAelAnatomoPatologicoDAO();
		aelAnatomoPatologicoDAO.persistir(aelAnatomoPatologico);
		aelAnatomoPatologicoDAO.flush();
	}
	
	// @ORABD AELT_LUM_ARU
	private void executarDepoisAtualizar(final AelAnatomoPatologico aelAnatomoPatologicoNew, final AelAnatomoPatologico aelAnatomoPatologicoOld) throws ApplicationBusinessException {
		if (CoreUtil.modificados(aelAnatomoPatologicoNew.getSeq(), aelAnatomoPatologicoOld.getSeq())
				|| CoreUtil.modificados(aelAnatomoPatologicoNew.getNumeroAp(), aelAnatomoPatologicoOld.getNumeroAp())
				|| CoreUtil.modificados(aelAnatomoPatologicoNew.getCriadoEm(), aelAnatomoPatologicoOld.getCriadoEm())
				|| CoreUtil.modificados(aelAnatomoPatologicoNew.getAtendimento(), aelAnatomoPatologicoOld.getAtendimento())
				|| CoreUtil.modificados(aelAnatomoPatologicoNew.getAtendimentoDiversos(), aelAnatomoPatologicoOld.getAtendimentoDiversos())
				|| CoreUtil.modificados(aelAnatomoPatologicoNew.getServidor(), aelAnatomoPatologicoOld.getServidor())) {
			// gravar JN
			this.createJournal(aelAnatomoPatologicoNew, DominioOperacoesJournal.UPD);
		}
	}

	// @ORABD AELT_LUM_BRU
//	private void executarAntesAtualizar(final AelAnatomoPatologico aelAnatomoPatologicoNew, final AelAnatomoPatologico aelAnatomoPatologicoOld)
//			throws ApplicationBusinessException {
//		// Verificação de perfil retirada e passado para verificação de permissão "receberTodasAmostras"	
//		if (CoreUtil.modificados(aelAnatomoPatologicoNew.getServidor(), aelAnatomoPatologicoOld.getServidor())) {
//			this.verificarPerfilUsuarioPatologista(aelAnatomoPatologicoNew.getServidor());
//		}
//	}

	protected void createJournal(final AelAnatomoPatologico reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelAnatomoPatologicosJn journal = BaseJournalFactory.getBaseJournal(operacao, AelAnatomoPatologicosJn.class, servidorLogado.getUsuario());

		journal.setAtendimento(reg.getAtendimento());
		journal.setAtendimentoDiversos(reg.getAtendimentoDiversos());
		journal.setNumeroAp(reg.getNumeroAp());
		
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelAnatomoPatologicoJnDAO().persistir(journal);
	}	

	protected AelAnatomoPatologicoDAO getAelAnatomoPatologicoDAO() {
		return aelAnatomoPatologicoDAO;
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
