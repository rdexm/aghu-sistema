package br.gov.mec.aghu.exames.patologia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrpMicroLacunaDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtPadraoMicroDAO;
import br.gov.mec.aghu.exames.dao.AelTextoPadraoMicroDAO;
import br.gov.mec.aghu.exames.dao.AelTxtMicroLacunaDAO;
import br.gov.mec.aghu.model.AelGrpMicroLacuna;
import br.gov.mec.aghu.model.AelGrpMicroLacunaId;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMicro;
import br.gov.mec.aghu.model.AelTextoPadraoMicro;
import br.gov.mec.aghu.model.AelTextoPadraoMicroId;
import br.gov.mec.aghu.model.AelTxtMicroLacuna;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException.BaseOptimisticLockExceptionCode;

@Stateless
public class TextoPadraoMicroscopiaON extends BaseBusiness {

	
	@EJB
	private TextoPadraoMicroscopiaRN textoPadraoMicroscopiaRN;
	
	private static final Log LOG = LogFactory.getLog(TextoPadraoMicroscopiaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelTextoPadraoMicroDAO aelTextoPadraoMicroDAO;
	
	@Inject
	private AelGrpMicroLacunaDAO aelGrpMicroLacunaDAO;
	
	@Inject
	private AelGrpTxtPadraoMicroDAO aelGrpTxtPadraoMicroDAO;
	
	@Inject
	private AelTxtMicroLacunaDAO aelTxtMicroLacunaDAO;
	
	private static final long serialVersionUID = 3034871499667990395L;
		
	private enum TextoPadraoMicroscopiaONExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_EXCLUIR_GRUPO_TEXTO_PADRAO_MICRO_REGISTROS_DEPENDENTES, 
		MSG_ERRO_EXCLUIR_TEXTO_PADRAO_MICRO_REGISTROS_DEPENDENTES, 
		MSG_ERRO_EXCLUIR_GRUPO_LACUNA_MICRO_REGISTROS_DEPENDENTES;
	}
	
	
	public void persistirAelGrpTxtPadraoMicro(final AelGrpTxtPadraoMicro aelGrpTxtPadraoMicroNew) throws BaseException {
		
		if(aelGrpTxtPadraoMicroNew.getSeq() != null) {
			final AelGrpTxtPadraoMicro aelGrpTxtPadraoMicroOld = getAelGrpTxtPadraoMicroDAO().obterOriginal(aelGrpTxtPadraoMicroNew);

			if(aelGrpTxtPadraoMicroOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			
			this.atualizarAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicroNew, aelGrpTxtPadraoMicroOld);
		}
		else {
			this.inserirAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicroNew);
		}
	}
	
	protected void inserirAelGrpTxtPadraoMicro(AelGrpTxtPadraoMicro aelGrpTxtPadraoMicroNew) throws ApplicationBusinessException {
		textoPadraoMicroscopiaRN.executarAntesInserirAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicroNew);
		aelGrpTxtPadraoMicroDAO.persistir(aelGrpTxtPadraoMicroNew);
	}

	protected void atualizarAelGrpTxtPadraoMicro(AelGrpTxtPadraoMicro aelGrpTxtPadraoMicroNew, AelGrpTxtPadraoMicro aelGrpTxtPadraoMicroOld) throws ApplicationBusinessException {
		textoPadraoMicroscopiaRN.executarAntesAtualizarAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicroNew, aelGrpTxtPadraoMicroOld);
		aelGrpTxtPadraoMicroDAO.merge(aelGrpTxtPadraoMicroNew);
		textoPadraoMicroscopiaRN.executarAposAtualizarAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicroNew, aelGrpTxtPadraoMicroOld);
	}

	public void removerAelGrpTxtPadraoMicro(Short seq) throws BaseException {
		AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro = aelGrpTxtPadraoMicroDAO.obterPorChavePrimaria(seq);
		
		if (aelGrpTxtPadraoMicro == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		List<AelTextoPadraoMicro> listaAelTextoPadraoMicro = aelTextoPadraoMicroDAO.pesquisarTextoPadraoMicroPorAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicro.getSeq());
		
		if (!listaAelTextoPadraoMicro.isEmpty()) {
			throw new ApplicationBusinessException(TextoPadraoMicroscopiaONExceptionCode.MSG_ERRO_EXCLUIR_GRUPO_TEXTO_PADRAO_MICRO_REGISTROS_DEPENDENTES);
		}
		
		aelGrpTxtPadraoMicroDAO.remover(aelGrpTxtPadraoMicro);
		textoPadraoMicroscopiaRN.executarAposExcluirAelGrpTxtPadraoMicro(aelGrpTxtPadraoMicro);
	}

	protected AelGrpTxtPadraoMicroDAO getAelGrpTxtPadraoMicroDAO() {
		return aelGrpTxtPadraoMicroDAO;
	}
	
	
	//-----------------------
	
	public void persistirAelGrpMicroLacuna(final AelGrpMicroLacuna aelGrpMicroLacunaNew) throws BaseException {
		
		if(aelGrpMicroLacunaNew.getId().getSeqp() != null) {
			final AelGrpMicroLacuna aelGrpMicroLacunaOld = getAelGrpMicroLacunaDAO().obterOriginal(aelGrpMicroLacunaNew);

			if(aelGrpMicroLacunaOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			
			this.atualizarAelGrpMicroLacuna(aelGrpMicroLacunaNew, aelGrpMicroLacunaOld);
		}
		else {
			aelGrpMicroLacunaNew.getId().setSeqp(getAelGrpMicroLacunaDAO().obterProximaSequence(aelGrpMicroLacunaNew.getId().getLuvLuuSeq(), aelGrpMicroLacunaNew.getId().getLuvSeqp()));
			this.inserirAelGrpMicroLacuna(aelGrpMicroLacunaNew);
		}
	}
	
	protected void inserirAelGrpMicroLacuna(AelGrpMicroLacuna aelGrpMicroLacunaNew) throws ApplicationBusinessException {
		textoPadraoMicroscopiaRN.executarAntesInserirAelGrpMicroLacuna(aelGrpMicroLacunaNew);
		aelGrpMicroLacunaDAO.persistir(aelGrpMicroLacunaNew);
	}

	protected void atualizarAelGrpMicroLacuna(AelGrpMicroLacuna aelGrpMicroLacunaNew, AelGrpMicroLacuna aelGrpMicroLacunaOld) throws ApplicationBusinessException {
		textoPadraoMicroscopiaRN.executarAntesAtualizarAelGrpMicroLacuna(aelGrpMicroLacunaNew, aelGrpMicroLacunaOld);
		aelGrpMicroLacunaDAO.merge(aelGrpMicroLacunaNew);
		textoPadraoMicroscopiaRN.executarAposAtualizarAelGrpMicroLacuna(aelGrpMicroLacunaNew, aelGrpMicroLacunaOld);
	}

	public void removerAelGrpMicroLacuna(AelGrpMicroLacunaId id) throws BaseException {
		
		AelGrpMicroLacuna aelGrpMicroLacuna = aelGrpMicroLacunaDAO.obterPorChavePrimaria(id);
		
		if (aelGrpMicroLacuna == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		List<AelTxtMicroLacuna> listaAelTxtMicroLacuna = aelTxtMicroLacunaDAO.pesquisarAelTxtMicroLacunaPorAelGrpMicroLacuna(aelGrpMicroLacuna);
		
		if (!listaAelTxtMicroLacuna.isEmpty()) {
			throw new ApplicationBusinessException(TextoPadraoMicroscopiaONExceptionCode.MSG_ERRO_EXCLUIR_GRUPO_LACUNA_MICRO_REGISTROS_DEPENDENTES);
		}
		
		aelGrpMicroLacunaDAO.remover(aelGrpMicroLacuna);
		textoPadraoMicroscopiaRN.executarAposExcluirAelGrpMicroLacuna(aelGrpMicroLacuna);
	}

	protected AelGrpMicroLacunaDAO getAelGrpMicroLacunaDAO() {
		return aelGrpMicroLacunaDAO;
	}

	
	//-----------------------
	
	public void persistirAelTextoPadraoMicro(final AelTextoPadraoMicro aelTextoPadraoMicroNew) throws BaseException {
		
		if(aelTextoPadraoMicroNew.getId().getSeqp() != null) {
			final AelTextoPadraoMicro aelTextoPadraoMicroOld = getAelTextoPadraoMicroDAO().obterOriginal(aelTextoPadraoMicroNew);

			if(aelTextoPadraoMicroOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			
			this.atualizarAelTextoPadraoMicro(aelTextoPadraoMicroNew, aelTextoPadraoMicroOld);
		}
		else {
			aelTextoPadraoMicroNew.getId().setSeqp(getAelTextoPadraoMicroDAO().obterProximaSequence(aelTextoPadraoMicroNew.getId().getLuuSeq()));
			this.inserirAelTextoPadraoMicro(aelTextoPadraoMicroNew);
		}
	}
	
	protected void inserirAelTextoPadraoMicro(AelTextoPadraoMicro aelTextoPadraoMicroNew) throws ApplicationBusinessException {
		textoPadraoMicroscopiaRN.executarAntesInserirAelTextoPadraoMicro(aelTextoPadraoMicroNew);
		aelTextoPadraoMicroDAO.persistir(aelTextoPadraoMicroNew);
	}

	protected void atualizarAelTextoPadraoMicro(AelTextoPadraoMicro aelTextoPadraoMicroNew, AelTextoPadraoMicro aelTextoPadraoMicroOld) throws ApplicationBusinessException {
		textoPadraoMicroscopiaRN.executarAntesAtualizarAelTextoPadraoMicro(aelTextoPadraoMicroNew, aelTextoPadraoMicroOld);
		aelTextoPadraoMicroDAO.merge(aelTextoPadraoMicroNew);
		textoPadraoMicroscopiaRN.executarAposAtualizarAelTextoPadraoMicro(aelTextoPadraoMicroNew, aelTextoPadraoMicroOld);
	}

	public void removerAelTextoPadraoMicro(AelTextoPadraoMicroId id) throws BaseException {
		
		AelTextoPadraoMicro aelTextoPadraoMicro = aelTextoPadraoMicroDAO.obterPorChavePrimaria(id);
		
		if (aelTextoPadraoMicro == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		List<AelGrpMicroLacuna> listaAelGrpMicroLacuna = aelGrpMicroLacunaDAO.pesquisarAelGrpMicroLacunaPorTextoPadraoMicro(aelTextoPadraoMicro.getId().getLuuSeq(), aelTextoPadraoMicro.getId().getSeqp());
		
		if (!listaAelGrpMicroLacuna.isEmpty()) {
			throw new ApplicationBusinessException(TextoPadraoMicroscopiaONExceptionCode.MSG_ERRO_EXCLUIR_TEXTO_PADRAO_MICRO_REGISTROS_DEPENDENTES);
		}		

		aelTextoPadraoMicroDAO.remover(aelTextoPadraoMicro);
		textoPadraoMicroscopiaRN.executarAposExcluirAelTextoPadraoMicro(aelTextoPadraoMicro);
	}

	protected AelTextoPadraoMicroDAO getAelTextoPadraoMicroDAO() {
		return aelTextoPadraoMicroDAO;
	}	
	

	
	//-----------------------
	
	public void persistirAelTxtMicroLacuna(final AelTxtMicroLacuna aelTxtMicroLacunaNew) throws BaseException {
		
		if(aelTxtMicroLacunaNew.getId().getSeqp() != null) {
			final AelTxtMicroLacuna aelTxtMicroLacunaOld = getAelTxtMicroLacunaDAO().obterOriginal(aelTxtMicroLacunaNew);

			if(aelTxtMicroLacunaOld == null){
				throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);
			}
			
			this.atualizarAelTxtMicroLacuna(aelTxtMicroLacunaNew, aelTxtMicroLacunaOld);
		}
		else {
			aelTxtMicroLacunaNew.getId().setSeqp(getAelTxtMicroLacunaDAO().obterProximaSequence(aelTxtMicroLacunaNew.getId().getLu9LuvLuuSeq(), aelTxtMicroLacunaNew.getId().getLu9LuvSeqp(), aelTxtMicroLacunaNew.getId().getLu9Seqp()));
			this.inserirAelTxtMicroLacuna(aelTxtMicroLacunaNew);
		}
	}
	
	protected void inserirAelTxtMicroLacuna(AelTxtMicroLacuna aelTxtMicroLacunaNew) throws ApplicationBusinessException {
		textoPadraoMicroscopiaRN.executarAntesInserirAelTxtMicroLacuna(aelTxtMicroLacunaNew);
		aelTxtMicroLacunaDAO.persistir(aelTxtMicroLacunaNew);
	}

	protected void atualizarAelTxtMicroLacuna(AelTxtMicroLacuna aelTxtMicroLacunaNew, AelTxtMicroLacuna aelTxtMicroLacunaOld) throws ApplicationBusinessException {
		textoPadraoMicroscopiaRN.executarAntesAtualizarAelTxtMicroLacuna(aelTxtMicroLacunaNew, aelTxtMicroLacunaOld);
		aelTxtMicroLacunaDAO.merge(aelTxtMicroLacunaNew);
		textoPadraoMicroscopiaRN.executarAposAtualizarAelTxtMicroLacuna(aelTxtMicroLacunaNew, aelTxtMicroLacunaOld);
	}

	public void removerAelTxtMicroLacuna(AelTxtMicroLacuna aelTxtMicroLacuna) throws BaseException {
		aelTxtMicroLacunaDAO.remover(aelTxtMicroLacuna);
		textoPadraoMicroscopiaRN.executarAposExcluirAelTxtMicroLacuna(aelTxtMicroLacuna);
	}

	protected AelTxtMicroLacunaDAO getAelTxtMicroLacunaDAO() {
		return aelTxtMicroLacunaDAO;
	}	
		
	protected TextoPadraoMicroscopiaRN getTextoPadraoMicroscopiaRN() {
		return textoPadraoMicroscopiaRN;
	}	
}