package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.dao.AelCadGuicheDAO;
import br.gov.mec.aghu.exames.dao.AelCadGuicheJnDAO;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.model.AelCadGuicheJn;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class AelCadastroGuicheRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelCadastroGuicheRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelCadGuicheDAO aelCadGuicheDAO;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@Inject
	private AelCadGuicheJnDAO aelCadGuicheJnDAO;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;

	private static final long serialVersionUID = -798406697870064652L;

	public enum AelCadastroGuicheRNExceptionCode implements BusinessExceptionCode {
		VIOLACAO_FK_MOVIMENTO_GUICHE;
	}

	public void excluir(final Short seqAelCadGuiche) throws ApplicationBusinessException {
		try {
			final AelCadGuiche aelCadGuiche = aelCadGuicheDAO.obterPorChavePrimaria(seqAelCadGuiche);
			aelCadGuicheDAO.remover(aelCadGuiche);
			
			// @ORADB AELT_CGU_ARD
			this.createJournal(aelCadGuiche, DominioOperacoesJournal.DEL);
			getAelCadGuicheDAO().flush();
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				final ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new ApplicationBusinessException(AelCadastroGuicheRNExceptionCode.VIOLACAO_FK_MOVIMENTO_GUICHE, cve.getConstraintName());
			}
		}
	}

	public void inserir(final AelCadGuiche aelCadGuiche) throws ApplicationBusinessException {
		if (aelCadGuiche.getOcupado() == null) {
			aelCadGuiche.setOcupado(DominioSimNao.N);
		}
		this.aeltCguBri(aelCadGuiche);
		getAelCadGuicheDAO().persistir(aelCadGuiche);
	}

	public void alterar(final AelCadGuiche aelCadGuiche, String nomeMicrocomputador) throws ApplicationBusinessException {
		final AelCadGuiche original = getAelCadGuicheDAO().obterOriginal(aelCadGuiche);
		this.aeltCguBru(aelCadGuiche, original, nomeMicrocomputador);
		getAelCadGuicheDAO().atualizar(aelCadGuiche);
		this.aeltCguAru(aelCadGuiche, original);
	}

	// @ORADB AELT_CGU_BRI
	private void aeltCguBri(final AelCadGuiche aelCadGuiche) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelCadGuiche.setCriadoEm(new Date());
		aelCadGuiche.setServidor(servidorLogado);
	}

	// @ORADB AELT_CGU_ARU
	private void aeltCguAru(final AelCadGuiche alterado, final AelCadGuiche original) throws ApplicationBusinessException {
		if (CoreUtil.modificados(alterado.getDescricao(), original.getDescricao())
				|| CoreUtil.modificados(alterado.getOcupado(), original.getOcupado())
				|| CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao())
				|| CoreUtil.modificados(alterado.getCriadoEm(), original.getCriadoEm())
				|| CoreUtil.modificados(alterado.getServidor(), original.getServidor())
				|| CoreUtil.modificados(alterado.getUnidadeFuncional(), original.getUnidadeFuncional())
				|| CoreUtil.modificados(alterado.getSeq(), original.getSeq())) {
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}

	// @ORADB AELT_CGU_BRU
	private void aeltCguBru(final AelCadGuiche alterado, final AelCadGuiche original, String nomeMicrocomputador) throws ApplicationBusinessException {
		if (CoreUtil.modificados(alterado.getOcupado(), original.getOcupado())) {

			this.getExamesPatologiaFacade().criarAelMovimentoGuiche(alterado.getSeq(), nomeMicrocomputador);

			if (DominioSimNao.S.equals(alterado.getOcupado())) {
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				final AghMicrocomputador aghMicrocomputador = getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIPException(nomeMicrocomputador);
				alterado.setMachine(aghMicrocomputador.getNome());
				alterado.setUsuario(servidorLogado != null ? servidorLogado.getUsuario() : null);
			} else {
				alterado.setMachine(null);
				alterado.setUsuario(null);
			}
		}
	}

	private void createJournal(final AelCadGuiche reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelCadGuicheJn journal = BaseJournalFactory.getBaseJournal(operacao, AelCadGuicheJn.class, servidorLogado.getUsuario());

		journal.setSeq(reg.getSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());
		journal.setOcupado(reg.getOcupado());

		this.getAelCadGuicheJnDAO().persistir(journal);
	}

	protected AelCadGuicheDAO getAelCadGuicheDAO() {
		return aelCadGuicheDAO;
	}

	protected AelCadGuicheJnDAO getAelCadGuicheJnDAO() {
		return aelCadGuicheJnDAO;
	}

	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return this.examesPatologiaFacade;
	}
	
	protected IAdministracaoFacade getAdministracaoFacade(){
		return administracaoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}