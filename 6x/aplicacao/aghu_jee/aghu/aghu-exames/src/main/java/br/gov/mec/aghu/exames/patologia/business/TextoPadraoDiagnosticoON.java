package br.gov.mec.aghu.exames.patologia.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.exames.dao.AelGrpDiagLacunasDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtPadraoDiagsDAO;
import br.gov.mec.aghu.exames.dao.AelTextoPadraoDiagsDAO;
import br.gov.mec.aghu.exames.dao.AelTxtDiagLacunasDAO;
import br.gov.mec.aghu.model.AelGrpDiagLacunas;
import br.gov.mec.aghu.model.AelGrpDiagLacunasId;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiagsId;
import br.gov.mec.aghu.model.AelTxtDiagLacunas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException.BaseOptimisticLockExceptionCode;

@Stateless
public class TextoPadraoDiagnosticoON extends BaseBusiness {

	
	@EJB
	private TextoPadraoDiagnosticoRN textoPadraoDiagnosticoRN;
	
	private static final Log LOG = LogFactory.getLog(TextoPadraoDiagnosticoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelTxtDiagLacunasDAO aelTxtDiagLacunasDAO;
	
	@Inject
	private AelGrpDiagLacunasDAO aelGrpDiagLacunasDAO;
	
	@Inject
	private AelTextoPadraoDiagsDAO aelTextoPadraoDiagsDAO;
	
	@Inject
	private AelGrpTxtPadraoDiagsDAO aelGrpTxtPadraoDiagsDAO;
	
	private static final long serialVersionUID = 3034871499667990395L;

	public enum TextoPadraoDiagnosticoONExceptionCode implements BusinessExceptionCode {
		VIOLACAO_FK_TEXTO_PADRAO_DIAGNOSTICO;
	}

	public void persistirAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiagsNew) throws BaseException {

		if (aelGrpTxtPadraoDiagsNew.getSeq() == null) {
			this.inserirAelGrpTxtPadraoDiags(aelGrpTxtPadraoDiagsNew);
		} else {
			final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiagsOld = getAelGrpTxtPadraoDiagsDAO().obterOriginal(aelGrpTxtPadraoDiagsNew);

			if(aelGrpTxtPadraoDiagsOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			this.atualizarAelGrpTxtPadraoDiags(aelGrpTxtPadraoDiagsNew, aelGrpTxtPadraoDiagsOld);
		}
	}

	private void inserirAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiagsNew) throws ApplicationBusinessException {
		textoPadraoDiagnosticoRN.executarAntesInserirAelGrpTxtPadraoDiags(aelGrpTxtPadraoDiagsNew);
		aelGrpTxtPadraoDiagsDAO.persistir(aelGrpTxtPadraoDiagsNew);
	}

	private void atualizarAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiagsNew,
			final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiagsOld) throws ApplicationBusinessException {
		aelGrpTxtPadraoDiagsDAO.merge(aelGrpTxtPadraoDiagsNew);
		textoPadraoDiagnosticoRN.executarAposAtualizarAelGrpTxtPadraoDiags(aelGrpTxtPadraoDiagsNew, aelGrpTxtPadraoDiagsOld);
	}

	public void removerAelGrpTxtPadraoDiags(final Short seq) throws BaseException {
		try {
			final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags = aelGrpTxtPadraoDiagsDAO.obterPorChavePrimaria(seq);

			if (aelGrpTxtPadraoDiags == null) {
				throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
			}
			
			if (aelGrpTxtPadraoDiags.getAelTextoPadraoDiagses() != null && !aelGrpTxtPadraoDiags.getAelTextoPadraoDiagses().isEmpty()) {
				throw new ApplicationBusinessException(TextoPadraoDiagnosticoONExceptionCode.VIOLACAO_FK_TEXTO_PADRAO_DIAGNOSTICO);
			}
			aelGrpTxtPadraoDiagsDAO.remover(aelGrpTxtPadraoDiags);
			textoPadraoDiagnosticoRN.executarAposExcluirAelGrpTxtPadraoDiags(aelGrpTxtPadraoDiags);

		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				final ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new ApplicationBusinessException(TextoPadraoDiagnosticoONExceptionCode.VIOLACAO_FK_TEXTO_PADRAO_DIAGNOSTICO, cve.getConstraintName());
			}
		}
	}

	protected AelGrpTxtPadraoDiagsDAO getAelGrpTxtPadraoDiagsDAO() {
		return aelGrpTxtPadraoDiagsDAO;
	}

	protected TextoPadraoDiagnosticoRN getTextoPadraoDiagnosticoRN() {
		return textoPadraoDiagnosticoRN;
	}

	// -----------------------

	public void persistirAelGrpDiagsLacuna(final AelGrpDiagLacunas aelGrpDiagLacunasNew) throws BaseException {
		if (aelGrpDiagLacunasNew.getId() == null || aelGrpDiagLacunasNew.getId().getSeqp() == null) {
			aelGrpDiagLacunasNew.setId(getAelGrpDiagLacunasDAO().gerarId(aelGrpDiagLacunasNew.getAelTextoPadraoDiags()));
			this.inserirAelGrpDiagsLacuna(aelGrpDiagLacunasNew);
		} else {
			final AelGrpDiagLacunas aelGrpDiagsLacunaOld = this.getAelGrpDiagLacunasDAO().obterOriginal(aelGrpDiagLacunasNew);

			if(aelGrpDiagsLacunaOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			this.atualizarAelGrpDiagsLacuna(aelGrpDiagLacunasNew, aelGrpDiagsLacunaOld);
		}
	}

	private void inserirAelGrpDiagsLacuna(final AelGrpDiagLacunas aelGrpDiagLacunasNew) throws ApplicationBusinessException {
		textoPadraoDiagnosticoRN.executarAntesInserirAelGrpDiagLacunas(aelGrpDiagLacunasNew);
		aelGrpDiagLacunasDAO.persistir(aelGrpDiagLacunasNew);
	}

	private void atualizarAelGrpDiagsLacuna(final AelGrpDiagLacunas aelGrpDiagsLacunaNew, final AelGrpDiagLacunas aelGrpDiagsLacunaOld) throws ApplicationBusinessException {

		// textoPadraoDiagnosticoRN.executarAntesAtualizarAelGrpDiagsLacuna(aelGrpDiagsLacunaNew,
		// aelGrpDiagsLacunaOld);
		aelGrpDiagLacunasDAO.merge(aelGrpDiagsLacunaNew);
		textoPadraoDiagnosticoRN.executarAposAtualizarAelGrpDiagLacunas(aelGrpDiagsLacunaNew, aelGrpDiagsLacunaOld);
	}

	public void removerAelGrpDiagsLacuna(final AelGrpDiagLacunasId id) throws BaseException {
		try { 
			AelGrpDiagLacunas aelGrpDiagLacunas = aelGrpDiagLacunasDAO.obterPorChavePrimaria(id);
			
			if (aelGrpDiagLacunas == null) {
				throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
			}
			
			if (aelGrpDiagLacunas.getAelTxtDiagLacunas() != null && !aelGrpDiagLacunas.getAelTxtDiagLacunas().isEmpty()) {
				throw new ApplicationBusinessException(TextoPadraoDiagnosticoONExceptionCode.VIOLACAO_FK_TEXTO_PADRAO_DIAGNOSTICO);
			}

			aelGrpDiagLacunasDAO.remover(aelGrpDiagLacunas);
			textoPadraoDiagnosticoRN.executarAposExcluirAelGrpDiagLacunas(aelGrpDiagLacunas);
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				final ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new ApplicationBusinessException(TextoPadraoDiagnosticoONExceptionCode.VIOLACAO_FK_TEXTO_PADRAO_DIAGNOSTICO,
						cve.getConstraintName());
			}
		}
	}

	protected AelGrpDiagLacunasDAO getAelGrpDiagLacunasDAO() {
		return aelGrpDiagLacunasDAO;
	}

	// -----------------------

	public void persistirAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiagsNew) throws BaseException {

		if (aelTextoPadraoDiagsNew.getId() == null || aelTextoPadraoDiagsNew.getId().getSeqp() == null) {
			aelTextoPadraoDiagsNew.setId(getAelTextoPadraoDiagsDAO().gerarId(aelTextoPadraoDiagsNew.getAelGrpTxtPadraoDiags()));
			this.inserirAelTextoPadraoDiags(aelTextoPadraoDiagsNew);
		} else {
			final AelTextoPadraoDiags aelTextoPadraoDiagsOld = getAelTextoPadraoDiagsDAO().obterOriginal(aelTextoPadraoDiagsNew);

			if(aelTextoPadraoDiagsOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}

			this.atualizarAelTextoPadraoDiags(aelTextoPadraoDiagsNew, aelTextoPadraoDiagsOld);
		}
	}

	private void inserirAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiagsNew) throws ApplicationBusinessException {
		textoPadraoDiagnosticoRN.executarAntesInserirAelTextoPadraoDiags(aelTextoPadraoDiagsNew);
		aelTextoPadraoDiagsDAO.persistir(aelTextoPadraoDiagsNew);
	}

	protected void atualizarAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiagsNew, final AelTextoPadraoDiags aelTextoPadraoDiagsOld) throws ApplicationBusinessException {
		// textoPadraoDiagnosticoRN.executarAntesAtualizarAelTextoPadraoDiags(aelTextoPadraoDiagsNew,
		// aelTextoPadraoDiagsOld);
		aelTextoPadraoDiagsDAO.merge(aelTextoPadraoDiagsNew);
		textoPadraoDiagnosticoRN.executarAposAtualizarAelTextoPadraoDiags(aelTextoPadraoDiagsNew, aelTextoPadraoDiagsOld);
	}

	public void removerAelTextoPadraoDiags(final AelTextoPadraoDiagsId id) throws BaseException {
		try {
			final AelTextoPadraoDiags aelTextoPadraoDiags = aelTextoPadraoDiagsDAO.obterPorChavePrimaria(id);

			if (aelTextoPadraoDiags == null) {
				throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
			}
			
			if (aelTextoPadraoDiags.getAelGrpDiagLacunas() != null && !aelTextoPadraoDiags.getAelGrpDiagLacunas().isEmpty()) {
				throw new ApplicationBusinessException(TextoPadraoDiagnosticoONExceptionCode.VIOLACAO_FK_TEXTO_PADRAO_DIAGNOSTICO);
			}
			aelTextoPadraoDiagsDAO.remover(aelTextoPadraoDiags);
			textoPadraoDiagnosticoRN.executarAposExcluirAelTextoPadraoDiags(aelTextoPadraoDiags);

		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				final ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new ApplicationBusinessException(TextoPadraoDiagnosticoONExceptionCode.VIOLACAO_FK_TEXTO_PADRAO_DIAGNOSTICO,
						cve.getConstraintName());
			}
		}
	}

	protected AelTextoPadraoDiagsDAO getAelTextoPadraoDiagsDAO() {
		return aelTextoPadraoDiagsDAO;
	}

	// -----------------------

	public void persistirAelTxtDiagLacunas(final AelTxtDiagLacunas aelTxtDiagLacunasNew) throws BaseException {

		if (aelTxtDiagLacunasNew.getId() == null || aelTxtDiagLacunasNew.getId().getSeqp() == null) {
			aelTxtDiagLacunasNew.setId(getAelTxtDiagLacunasDAO().gerarId(aelTxtDiagLacunasNew.getAelGrpDiagLacunas()));
			this.inserirAelTxtDiagsLacuna(aelTxtDiagLacunasNew);
		} else {
			final AelTxtDiagLacunas aelTxtDiagsLacunaOld = getAelTxtDiagLacunasDAO().obterOriginal(aelTxtDiagLacunasNew);

			if(aelTxtDiagsLacunaOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			this.atualizarAelTxtDiagsLacuna(aelTxtDiagLacunasNew, aelTxtDiagsLacunaOld);
		}
	}

	protected void inserirAelTxtDiagsLacuna(final AelTxtDiagLacunas aelTxtDiagsLacunaNew) throws ApplicationBusinessException {
		textoPadraoDiagnosticoRN.executarAntesInserirAelTxtDiagsLacuna(aelTxtDiagsLacunaNew);
		aelTxtDiagLacunasDAO.persistir(aelTxtDiagsLacunaNew);
	}

	protected void atualizarAelTxtDiagsLacuna(final AelTxtDiagLacunas aelTxtDiagsLacunaNew, final AelTxtDiagLacunas aelTxtDiagsLacunaOld) throws ApplicationBusinessException {
		// textoPadraoDiagnosticoRN.executarAntesAtualizarAelTxtDiagsLacuna(aelTxtDiagsLacunaNew,
		// aelTxtDiagsLacunaOld);
		aelTxtDiagLacunasDAO.merge(aelTxtDiagsLacunaNew);
		textoPadraoDiagnosticoRN.executarAposAtualizarAelTxtDiagsLacuna(aelTxtDiagsLacunaNew, aelTxtDiagsLacunaOld);
	}

	public void removerAelTxtDiagsLacuna(final AelTxtDiagLacunas aelTxtDiagsLacuna) throws BaseException {

		try {
			AelTxtDiagLacunas aelTxtDiagsLacunaExcluir = aelTxtDiagLacunasDAO.obterPorChavePrimaria(aelTxtDiagsLacuna.getId()); 
			aelTxtDiagLacunasDAO.remover(aelTxtDiagsLacunaExcluir);
			textoPadraoDiagnosticoRN.executarAposExcluirAelTxtDiagsLacuna(aelTxtDiagsLacunaExcluir);
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				final ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new ApplicationBusinessException(TextoPadraoDiagnosticoONExceptionCode.VIOLACAO_FK_TEXTO_PADRAO_DIAGNOSTICO,
						cve.getConstraintName());
			}
		}
	}

	protected AelTxtDiagLacunasDAO getAelTxtDiagLacunasDAO() {
		return aelTxtDiagLacunasDAO;
	}
}