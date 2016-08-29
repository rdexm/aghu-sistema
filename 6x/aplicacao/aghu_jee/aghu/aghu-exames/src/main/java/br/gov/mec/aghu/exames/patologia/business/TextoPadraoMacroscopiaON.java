package br.gov.mec.aghu.exames.patologia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrpMacroLacunaDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtPadraoMacroDAO;
import br.gov.mec.aghu.exames.dao.AelTextoPadraoMacroDAO;
import br.gov.mec.aghu.exames.dao.AelTxtMacroLacunaDAO;
import br.gov.mec.aghu.model.AelGrpMacroLacuna;
import br.gov.mec.aghu.model.AelGrpMacroLacunaId;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMacro;
import br.gov.mec.aghu.model.AelTextoPadraoMacro;
import br.gov.mec.aghu.model.AelTextoPadraoMacroId;
import br.gov.mec.aghu.model.AelTxtMacroLacuna;
import br.gov.mec.aghu.model.AelTxtMacroLacunaId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException.BaseOptimisticLockExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TextoPadraoMacroscopiaON extends BaseBusiness {
	
	@EJB
	private TextoPadraoMacroscopiaRN textoPadraoMacroscopiaRN;

	private static final Log LOG = LogFactory.getLog(TextoPadraoMacroscopiaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AelGrpMacroLacunaDAO aelGrpMacroLacunaDAO;
	
	@Inject
	private AelTextoPadraoMacroDAO aelTextoPadraoMacroDAO;
	
	@Inject
	private AelGrpTxtPadraoMacroDAO aelGrpTxtPadraoMacroDAO;
	
	@Inject
	private AelTxtMacroLacunaDAO aelTxtMacroLacunaDAO;

	private static final long serialVersionUID = 3034871499667990395L;
		
	private enum TextoPadraoMacroscopiaONExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_EXCLUIR_GRUPO_TEXTO_PADRAO_MACRO_REGISTROS_DEPENDENTES, 
		MSG_ERRO_EXCLUIR_TEXTO_PADRAO_MACRO_REGISTROS_DEPENDENTES, 
		MSG_ERRO_EXCLUIR_GRUPO_LACUNA_MACRO_REGISTROS_DEPENDENTES;
	}
	
	
	public void persistirAelGrpTxtPadraoMacro(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroNew) throws BaseException {
		
		if(aelGrpTxtPadraoMacroNew.getSeq() != null) {
			final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroOld = getAelGrpTxtPadraoMacroDAO().obterOriginal(aelGrpTxtPadraoMacroNew);

			if(aelGrpTxtPadraoMacroOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			
			this.atualizarAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacroNew, aelGrpTxtPadraoMacroOld);
		}
		else {
			this.inserirAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacroNew);
		}
	}
	
	protected void inserirAelGrpTxtPadraoMacro(AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroNew) throws ApplicationBusinessException {
		textoPadraoMacroscopiaRN.executarAntesInserirAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacroNew);
		aelGrpTxtPadraoMacroDAO.persistir(aelGrpTxtPadraoMacroNew);
	}

	protected void atualizarAelGrpTxtPadraoMacro(
			AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroNew,
			AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroOld) throws ApplicationBusinessException {
		
		textoPadraoMacroscopiaRN.executarAntesAtualizarAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacroNew, aelGrpTxtPadraoMacroOld);
		aelGrpTxtPadraoMacroDAO.merge(aelGrpTxtPadraoMacroNew);
		textoPadraoMacroscopiaRN.executarAposAtualizarAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacroNew, aelGrpTxtPadraoMacroOld);
	}

	public void removerAelGrpTxtPadraoMacro(final Short seq) throws BaseException {
		AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro = aelGrpTxtPadraoMacroDAO.obterPorChavePrimaria(seq);
		
		if (aelGrpTxtPadraoMacro == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		List<AelTextoPadraoMacro> listaAelTextoPadraoMacro = aelTextoPadraoMacroDAO.pesquisarTextoPadraoMacroPorAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacro.getSeq());
		
		if (!listaAelTextoPadraoMacro.isEmpty()) {
			throw new ApplicationBusinessException(TextoPadraoMacroscopiaONExceptionCode.MSG_ERRO_EXCLUIR_GRUPO_TEXTO_PADRAO_MACRO_REGISTROS_DEPENDENTES);
		}
		
		aelGrpTxtPadraoMacroDAO.remover(aelGrpTxtPadraoMacro);
		textoPadraoMacroscopiaRN.executarAposExcluirAelGrpTxtPadraoMacro(aelGrpTxtPadraoMacro);
	}

	protected AelGrpTxtPadraoMacroDAO getAelGrpTxtPadraoMacroDAO() {
		return aelGrpTxtPadraoMacroDAO;
	}
	
	
	//-----------------------
	
	public void persistirAelGrpMacroLacuna(final AelGrpMacroLacuna aelGrpMacroLacunaNew) throws BaseException {
		
		if(aelGrpMacroLacunaNew.getId().getSeqp() != null) {
			final AelGrpMacroLacuna aelGrpMacroLacunaOld = getAelGrpMacroLacunaDAO().obterOriginal(aelGrpMacroLacunaNew);
			
			if(aelGrpMacroLacunaOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			
			this.atualizarAelGrpMacroLacuna(aelGrpMacroLacunaNew, aelGrpMacroLacunaOld);
		}
		else {
			aelGrpMacroLacunaNew.getId().setSeqp(getAelGrpMacroLacunaDAO().obterProximaSequence(aelGrpMacroLacunaNew.getId().getLufLubSeq(), aelGrpMacroLacunaNew.getId().getLufSeqp()));
			this.inserirAelGrpMacroLacuna(aelGrpMacroLacunaNew);
		}
	}
	
	protected void inserirAelGrpMacroLacuna(AelGrpMacroLacuna aelGrpMacroLacunaNew) throws ApplicationBusinessException {
		textoPadraoMacroscopiaRN.executarAntesInserirAelGrpMacroLacuna(aelGrpMacroLacunaNew);
		aelGrpMacroLacunaDAO.persistir(aelGrpMacroLacunaNew);
	}

	protected void atualizarAelGrpMacroLacuna(
			AelGrpMacroLacuna aelGrpMacroLacunaNew,
			AelGrpMacroLacuna aelGrpMacroLacunaOld) throws ApplicationBusinessException {
		
		textoPadraoMacroscopiaRN.executarAntesAtualizarAelGrpMacroLacuna(aelGrpMacroLacunaNew, aelGrpMacroLacunaOld);
		aelGrpMacroLacunaDAO.merge(aelGrpMacroLacunaNew);
		textoPadraoMacroscopiaRN.executarAposAtualizarAelGrpMacroLacuna(aelGrpMacroLacunaNew, aelGrpMacroLacunaOld);
	}

	public void removerAelGrpMacroLacuna(AelGrpMacroLacunaId id) throws BaseException {
		
		AelGrpMacroLacuna aelGrpMacroLacuna = aelGrpMacroLacunaDAO.obterPorChavePrimaria(id);
		
		if (aelGrpMacroLacuna == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		List<AelTxtMacroLacuna> listaAelTxtMacroLacuna = aelTxtMacroLacunaDAO.pesquisarAelTxtMacroLacunaPorAelGrpMacroLacuna(aelGrpMacroLacuna, null);
		
		if (!listaAelTxtMacroLacuna.isEmpty()) {
			throw new ApplicationBusinessException(TextoPadraoMacroscopiaONExceptionCode.MSG_ERRO_EXCLUIR_GRUPO_LACUNA_MACRO_REGISTROS_DEPENDENTES);
		}
		
		aelGrpMacroLacunaDAO.remover(aelGrpMacroLacuna);
		textoPadraoMacroscopiaRN.executarAposExcluirAelGrpMacroLacuna(aelGrpMacroLacuna);
	}

	protected AelGrpMacroLacunaDAO getAelGrpMacroLacunaDAO() {
		return aelGrpMacroLacunaDAO;
	}

	
	//-----------------------
	
	public void persistirAelTextoPadraoMacro(final AelTextoPadraoMacro aelTextoPadraoMacroNew) throws BaseException {
		
		if(aelTextoPadraoMacroNew.getId().getSeqp() != null) {
			final AelTextoPadraoMacro aelTextoPadraoMacroOld = getAelTextoPadraoMacroDAO().obterOriginal(aelTextoPadraoMacroNew);

			if(aelTextoPadraoMacroOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			
			this.atualizarAelTextoPadraoMacro(aelTextoPadraoMacroNew, aelTextoPadraoMacroOld);
		} else {
			aelTextoPadraoMacroNew.getId().setSeqp(getAelTextoPadraoMacroDAO().obterProximaSequence(aelTextoPadraoMacroNew.getId().getLubSeq()));
			this.inserirAelTextoPadraoMacro(aelTextoPadraoMacroNew);
		}
	}
	
	protected void inserirAelTextoPadraoMacro(AelTextoPadraoMacro aelTextoPadraoMacroNew) throws ApplicationBusinessException {
		textoPadraoMacroscopiaRN.executarAntesInserirAelTextoPadraoMacro(aelTextoPadraoMacroNew);
		aelTextoPadraoMacroDAO.persistir(aelTextoPadraoMacroNew);
	}

	protected void atualizarAelTextoPadraoMacro(
			AelTextoPadraoMacro aelTextoPadraoMacroNew,
			AelTextoPadraoMacro aelTextoPadraoMacroOld) throws ApplicationBusinessException {

		textoPadraoMacroscopiaRN.executarAntesAtualizarAelTextoPadraoMacro(aelTextoPadraoMacroNew, aelTextoPadraoMacroOld);
		aelTextoPadraoMacroDAO.merge(aelTextoPadraoMacroNew);
		textoPadraoMacroscopiaRN.executarAposAtualizarAelTextoPadraoMacro(aelTextoPadraoMacroNew, aelTextoPadraoMacroOld);
	}

	public void removerAelTextoPadraoMacro(AelTextoPadraoMacroId id) throws BaseException {
		AelTextoPadraoMacro aelTextoPadraoMacro = aelTextoPadraoMacroDAO.obterPorChavePrimaria(id);
		
		if (aelTextoPadraoMacro == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}

		List<AelGrpMacroLacuna> listaAelGrpMacroLacuna = aelGrpMacroLacunaDAO.pesquisarAelGrpMacroLacunaPorTextoPadraoMacro(aelTextoPadraoMacro.getId().getLubSeq(), aelTextoPadraoMacro.getId().getSeqp(), null);
		
		if (!listaAelGrpMacroLacuna.isEmpty()) {
			throw new ApplicationBusinessException(TextoPadraoMacroscopiaONExceptionCode.MSG_ERRO_EXCLUIR_TEXTO_PADRAO_MACRO_REGISTROS_DEPENDENTES);
		}		

		aelTextoPadraoMacroDAO.remover(aelTextoPadraoMacro);
		textoPadraoMacroscopiaRN.executarAposExcluirAelTextoPadraoMacro(aelTextoPadraoMacro);
	}

	protected AelTextoPadraoMacroDAO getAelTextoPadraoMacroDAO() {
		return aelTextoPadraoMacroDAO;
	}	
	

	
	//-----------------------
	
	public void persistirAelTxtMacroLacuna(final AelTxtMacroLacuna aelTxtMacroLacunaNew) throws BaseException {
		
		if(aelTxtMacroLacunaNew.getId().getSeqp() != null) {
			final AelTxtMacroLacuna aelTxtMacroLacunaOld = getAelTxtMacroLacunaDAO().obterOriginal(aelTxtMacroLacunaNew);

			if(aelTxtMacroLacunaOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			this.atualizarAelTxtMacroLacuna(aelTxtMacroLacunaNew, aelTxtMacroLacunaOld);
		}
		else {
			aelTxtMacroLacunaNew.getId().setSeqp(getAelTxtMacroLacunaDAO().obterProximaSequence(aelTxtMacroLacunaNew.getId().getLo3LufLubSeq(), aelTxtMacroLacunaNew.getId().getLo3LufSeqp(), aelTxtMacroLacunaNew.getId().getLo3Seqp()));
			this.inserirAelTxtMacroLacuna(aelTxtMacroLacunaNew);
		}
	}
	
	protected void inserirAelTxtMacroLacuna(
			AelTxtMacroLacuna aelTxtMacroLacunaNew) throws ApplicationBusinessException {
		
		textoPadraoMacroscopiaRN.executarAntesInserirAelTxtMacroLacuna(aelTxtMacroLacunaNew);
		aelTxtMacroLacunaDAO.persistir(aelTxtMacroLacunaNew);
	}

	protected void atualizarAelTxtMacroLacuna(
			AelTxtMacroLacuna aelTxtMacroLacunaNew,
			AelTxtMacroLacuna aelTxtMacroLacunaOld) throws ApplicationBusinessException {

		textoPadraoMacroscopiaRN.executarAntesAtualizarAelTxtMacroLacuna(aelTxtMacroLacunaNew, aelTxtMacroLacunaOld);
		aelTxtMacroLacunaDAO.merge(aelTxtMacroLacunaNew);
		textoPadraoMacroscopiaRN.executarAposAtualizarAelTxtMacroLacuna(aelTxtMacroLacunaNew, aelTxtMacroLacunaOld);
	}

	public void removerAelTxtMacroLacuna(AelTxtMacroLacunaId id) throws BaseException {
		AelTxtMacroLacuna aelTxtMacroLacuna = aelTxtMacroLacunaDAO.obterPorChavePrimaria(id);

		if (aelTxtMacroLacuna == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		aelTxtMacroLacunaDAO.remover(aelTxtMacroLacuna);
		textoPadraoMacroscopiaRN.executarAposExcluirAelTxtMacroLacuna(aelTxtMacroLacuna);
	}

	protected AelTxtMacroLacunaDAO getAelTxtMacroLacunaDAO() {
		return aelTxtMacroLacunaDAO;
	}	
		
	protected TextoPadraoMacroscopiaRN getTextoPadraoMacroscopiaRN() {
		return textoPadraoMacroscopiaRN;
	}	
}