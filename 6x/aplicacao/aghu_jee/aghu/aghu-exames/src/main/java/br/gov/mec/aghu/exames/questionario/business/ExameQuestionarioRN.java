package br.gov.mec.aghu.exames.questionario.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelExameQuestionarioJnDao;
import br.gov.mec.aghu.exames.dao.AelExamesQuestionarioDAO;
import br.gov.mec.aghu.model.AelExameQuestionarioJn;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesQuestionario;
import br.gov.mec.aghu.model.AelExamesQuestionarioId;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ExameQuestionarioRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ExameQuestionarioRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExamesQuestionarioDAO aelExamesQuestionarioDAO;
	
	@Inject
	private AelExameQuestionarioJnDao aelExameQuestionarioJnDao;

	private static final long serialVersionUID = 4547421033969073780L;

	public enum ExameQuestionarioRNExceptionCode implements BusinessExceptionCode {
		VALORES_OBRIGATORIOS, EXAME_JA_ASSOCIADO, AEL_00362, AEL_00366, NR_VIAS_INVALIDO,;

	}

	public void persistir(final AelExamesQuestionario examesQuestionario) throws ApplicationBusinessException {
		
		if (examesQuestionario.getId() == null) {
			examesQuestionario.setId(gerarIdExamesQuestionario(examesQuestionario));
			this.executarAntesInserir(examesQuestionario);
			this.getAelExamesQuestionarioDAO().persistir(examesQuestionario);
		} else {
			final AelExamesQuestionario examesQuestionarioOld = this.getAelExamesQuestionarioDAO().obterOriginal(examesQuestionario.getId());
			this.executarAntesAtualizar(examesQuestionario);
			this.getAelExamesQuestionarioDAO().merge(examesQuestionario);
			this.executarDepoisAtualizar(examesQuestionario, examesQuestionarioOld);
		}
	}

	/**
	 * @ORADB AELT_EQE_ARU
	 * @param examesQuestionario
	 * @param examesQuestionarioOld
	 * @throws ApplicationBusinessException 
	 */
	private void executarDepoisAtualizar(final AelExamesQuestionario examesQuestionario, final AelExamesQuestionario examesQuestionarioOld) throws ApplicationBusinessException {
		if (!CoreUtil.igual(examesQuestionario.getId(), examesQuestionarioOld.getId())
				|| !CoreUtil.igual(examesQuestionario.getSituacao(), examesQuestionarioOld.getSituacao())
				|| !CoreUtil.igual(examesQuestionario.getNroVias(), examesQuestionarioOld.getNroVias())
				|| !CoreUtil.igual(examesQuestionario.getAlteradoEm(), examesQuestionarioOld.getAlteradoEm())
				|| !CoreUtil.igual(examesQuestionario.getServidor(), examesQuestionarioOld.getServidor())) {
			this.inserirJn(examesQuestionarioOld, DominioOperacoesJournal.UPD);
		}

	}

	private void inserirJn(AelExamesQuestionario examesQuestionarioOld, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelExameQuestionarioJn jn = BaseJournalFactory.getBaseJournal(operacao, AelExameQuestionarioJn.class, servidorLogado.getUsuario());
		jn.setAlteradoEm(examesQuestionarioOld.getAlteradoEm());
		jn.setEmaExaSigla(examesQuestionarioOld.getId().getEmaExaSigla());
		jn.setEmaManSeq(examesQuestionarioOld.getId().getEmaManSeq());
		jn.setQtnSeq(examesQuestionarioOld.getId().getQtnSeq());
		jn.setSituacao(examesQuestionarioOld.getSituacao());
		if (examesQuestionarioOld.getServidor() != null) {
			jn.setSerMatricula(examesQuestionarioOld.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(examesQuestionarioOld.getServidor().getId().getVinCodigo());
		}

		jn.setNroVias(examesQuestionarioOld.getNroVias());
		this.getAelExameQuestionarioJnDao().persistir(jn);
	}

	/**
	 * @ORADB AELT_EQE_BRU
	 * @param examesQuestionario
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesAtualizar(final AelExamesQuestionario examesQuestionario) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.verificarExameMaterial(examesQuestionario.getExamesMaterialAnalise());
		this.verificarQuestionario(examesQuestionario.getAelQuestionarios());
		this.verificarNrVias(examesQuestionario);
		examesQuestionario.setServidor(servidorLogado);
		examesQuestionario.setAlteradoEm(new Date());

	}

	/**
	 * @ORADB AELT_EQE_BRI
	 * @param examesQuestionario
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesInserir(final AelExamesQuestionario examesQuestionario) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.verificarExameQuestionario(examesQuestionario);
		this.verificarExameMaterial(examesQuestionario.getExamesMaterialAnalise());
		this.verificarQuestionario(examesQuestionario.getAelQuestionarios());
		this.verificarNrVias(examesQuestionario);
		examesQuestionario.setServidor(servidorLogado);
		examesQuestionario.setAlteradoEm(new Date());
	}

	private void verificarNrVias(final AelExamesQuestionario examesQuestionario) throws ApplicationBusinessException {
		if(examesQuestionario.getNroVias() != null && examesQuestionario.getNroVias().intValue() == 0) {
			throw new ApplicationBusinessException(ExameQuestionarioRNExceptionCode.NR_VIAS_INVALIDO);
		}
	}

	private void verificarExameQuestionario(final AelExamesQuestionario examesQuestionario) throws ApplicationBusinessException {
		if (this.getAelExamesQuestionarioDAO().obterAelExamesQuestionario(examesQuestionario.getId().getEmaExaSigla(),
				examesQuestionario.getId().getEmaManSeq(), examesQuestionario.getId().getQtnSeq()) != null) {
			throw new ApplicationBusinessException(ExameQuestionarioRNExceptionCode.EXAME_JA_ASSOCIADO);
		}
	}

	/**
	 * 
	 * @ORADB aelk_eqe_rn.rn_eqep_ver_question
	 * @param aelQuestionarios
	 * @throws ApplicationBusinessException
	 */
	public void verificarQuestionario(final AelQuestionarios aelQuestionarios) throws ApplicationBusinessException {
		if (!DominioSituacao.A.equals(aelQuestionarios.getSituacao())) {
			throw new ApplicationBusinessException(ExameQuestionarioRNExceptionCode.AEL_00366);
		}
	}

	/**
	 * @ORADB aelk_eqe_rn.rn_eqep_ver_exme_mat
	 * 
	 * @param examesMaterialAnalise
	 * @throws ApplicationBusinessException
	 */
	public void verificarExameMaterial(final AelExamesMaterialAnalise examesMaterialAnalise) throws ApplicationBusinessException {
		if (!DominioSituacao.A.equals(examesMaterialAnalise.getIndSituacao())) {
			throw new ApplicationBusinessException(ExameQuestionarioRNExceptionCode.AEL_00362);
		}
	}

	private AelExamesQuestionarioId gerarIdExamesQuestionario(final AelExamesQuestionario examesQuestionario) throws ApplicationBusinessException {
		if (examesQuestionario.getExamesMaterialAnalise() == null || examesQuestionario.getAelQuestionarios() == null) {
			throw new ApplicationBusinessException(ExameQuestionarioRNExceptionCode.VALORES_OBRIGATORIOS);
		}
		return new AelExamesQuestionarioId(examesQuestionario.getExamesMaterialAnalise().getId().getExaSigla(), examesQuestionario.getExamesMaterialAnalise()
				.getId().getManSeq(), examesQuestionario.getAelQuestionarios().getSeq());
	}

	public void remover(final AelExamesQuestionario examesQuestionario) throws ApplicationBusinessException {
		final AelExamesQuestionario examesQuestionarioOld = this.getAelExamesQuestionarioDAO().obterOriginal(examesQuestionario.getId());
		this.getAelExamesQuestionarioDAO().remover(examesQuestionario);
		this.executarDepoisRemover(examesQuestionarioOld);
	}

	/**
	 * @ORADB AELT_EQE_ARD
	 * 
	 * @param examesQuestionario
	 * @throws ApplicationBusinessException 
	 */
	private void executarDepoisRemover(final AelExamesQuestionario examesQuestionario) throws ApplicationBusinessException {
		this.inserirJn(examesQuestionario, DominioOperacoesJournal.DEL);
	}

	protected AelExamesQuestionarioDAO getAelExamesQuestionarioDAO() {
		return aelExamesQuestionarioDAO;
	}

	public  AelExameQuestionarioJnDao getAelExameQuestionarioJnDao() {
		return aelExameQuestionarioJnDao;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
