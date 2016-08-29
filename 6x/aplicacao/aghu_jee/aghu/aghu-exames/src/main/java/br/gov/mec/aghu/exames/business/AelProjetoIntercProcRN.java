package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelProjetoIntercProcDAO;
import br.gov.mec.aghu.exames.dao.AelProjetoIntercProcJnDAO;
import br.gov.mec.aghu.model.AelProjetoIntercProc;
import br.gov.mec.aghu.model.AelProjetoIntercProcJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de BANCO para AEL_PROJETO_INTERC_PROCS
 * 
 * @author aghu
 * 
 */
@Stateless
public class AelProjetoIntercProcRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelProjetoIntercProcRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelProjetoIntercProcDAO aelProjetoIntercProcDAO;
	
	@Inject
	private AelProjetoIntercProcJnDAO aelProjetoIntercProcJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1187765470916504059L;

	/*
	 * Métodos para PERSISTIR
	 */

	/**
	 * Persistir AelProjetoIntercProc
	 * 
	 * @param projetoIntercProc
	 * @throws BaseException
	 */
	protected void persistirProjetoIntercProc(AelProjetoIntercProc projetoIntercProc) throws BaseException {
		if (projetoIntercProc.getId() == null) { // Inserir
			this.inserirAelProjetoIntercProc(projetoIntercProc);
		} else { // Atualizar
			this.atualizarAelProjetoIntercProc(projetoIntercProc);
		}
	}

	/*
	 * Métodos INSERIR
	 */

	/**
	 * ORADB TRIGGER AELT_PIP_BRI (INSERT)
	 * 
	 * @param projetoIntercProc
	 * @throws BaseException
	 */
	protected void preInserirAelProjetoIntercProc(AelProjetoIntercProc projetoIntercProc) throws BaseException {
		// TODO
	}

	/**
	 * Inserir AelProjetoIntercProc
	 * 
	 * @param projetoIntercProc
	 * @throws BaseException
	 */
	protected void inserirAelProjetoIntercProc(AelProjetoIntercProc projetoIntercProc) throws BaseException {
		this.preInserirAelProjetoIntercProc(projetoIntercProc);
		this.getAelProjetoIntercProcDAO().persistir(projetoIntercProc);
	}

	/*
	 * Métodos ATUALIZAR
	 */

	/**
	 * ORADB TRIGGER AELT_PIP_BRU (UPDATE)
	 * 
	 * @param projetoIntercProc
	 * @throws BaseException
	 */
	protected void preAtualizarAelProjetoIntercProc(AelProjetoIntercProc projetoIntercProc) throws BaseException {

		AelProjetoIntercProc original = getAelProjetoIntercProcDAO().obterOriginal(projetoIntercProc.getId());

		final boolean isModificado = CoreUtil.modificados(projetoIntercProc.getId().getSeqp(), original.getId().getSeqp())
		|| CoreUtil.modificados(projetoIntercProc.getCriadoEm(), original.getCriadoEm()) || CoreUtil.modificados(projetoIntercProc.getQtde(), original.getQtde())
		|| CoreUtil.modificados(projetoIntercProc.getJustificativa(), original.getJustificativa())
		|| CoreUtil.modificados(projetoIntercProc.getEfetivado(), original.getEfetivado())
		|| CoreUtil.modificados(projetoIntercProc.getId().getPpjPjqSeq(), original.getId().getPpjPjqSeq())
		|| CoreUtil.modificados(projetoIntercProc.getId().getPpjPacCodigo(), original.getId().getPpjPacCodigo())
		|| CoreUtil.modificados(projetoIntercProc.getServidor(), original.getServidor())
		|| CoreUtil.modificados(projetoIntercProc.getId().getPciSeq(), original.getId().getPciSeq());

		if (isModificado) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			AelProjetoIntercProcJn projetoIntercProcJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelProjetoIntercProcJn.class, servidorLogado.getUsuario());
			
			projetoIntercProcJn.setSeqp(projetoIntercProc.getId().getSeqp());
			projetoIntercProcJn.setCriadoEm(new Date());
			projetoIntercProcJn.setQtde(projetoIntercProc.getQtde());
			projetoIntercProcJn.setJustificativa(projetoIntercProc.getJustificativa());
			projetoIntercProcJn.setEfetivado(projetoIntercProc.getEfetivado());
			projetoIntercProcJn.setPpjPjqSeq(projetoIntercProc.getId().getPpjPjqSeq());
			projetoIntercProcJn.setPpjPacCodigo(projetoIntercProc.getId().getPpjPacCodigo());
			projetoIntercProcJn.setPciSeq(projetoIntercProc.getId().getPciSeq());
			projetoIntercProcJn.setSerMatricula(projetoIntercProc.getServidor().getId().getMatricula());
			projetoIntercProcJn.setSerVinCodigo(projetoIntercProc.getServidor().getId().getVinCodigo());

			// Insere na JOURNAL
			this.getAelProjetoIntercProcJnDAO().persistir(projetoIntercProcJn);
		}

	}

	/**
	 * Atualizar AelProjetoIntercProc
	 * 
	 * @param projetoIntercProc
	 * @throws BaseException
	 */
	protected void atualizarAelProjetoIntercProc(AelProjetoIntercProc projetoIntercProc) throws BaseException {
		this.preAtualizarAelProjetoIntercProc(projetoIntercProc);
		this.getAelProjetoIntercProcDAO().atualizar(projetoIntercProc);
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected AelProjetoIntercProcDAO getAelProjetoIntercProcDAO() {
		return aelProjetoIntercProcDAO;
	}

	protected AelProjetoIntercProcJnDAO getAelProjetoIntercProcJnDAO() {
		return aelProjetoIntercProcJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
