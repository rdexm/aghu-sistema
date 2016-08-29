package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelMetodoUnfExameDAO;
import br.gov.mec.aghu.exames.dao.AelMetodoUnfExameJnDAO;
import br.gov.mec.aghu.model.AelMetodoUnfExame;
import br.gov.mec.aghu.model.AelMetodoUnfExameJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;


/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterMetodoUnfExameRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterMetodoUnfExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelMetodoUnfExameDAO aelMetodoUnfExameDAO;
	
	@Inject
	private AelMetodoUnfExameJnDAO aelMetodoUnfExameJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3167925389833832356L;

	public enum ManterMetodoUnfExameRNExceptionCode implements BusinessExceptionCode {

		DATA_FIM_MENOR, AEL_01733, AEL_01732, AEL_01735, AEL_01736;

	}

	/**
	 * Insere metodoUnfExame
	 * @param metodoUnfExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserirMetodoUnfExame(AelMetodoUnfExame metodoUnfExame) throws ApplicationBusinessException {
		preInserir(metodoUnfExame);
		getAelMetodoUnfExameDAO().persistir(metodoUnfExame);
		posInserir(metodoUnfExame);
		getAelMetodoUnfExameDAO().flush();

	}

	/**
	 * Atualiza metodoUnfExame
	 * @param metodoUnfExame
	 * @throws ApplicationBusinessException
	 */
	public void atualizarMetodoUnfExame(AelMetodoUnfExame metodoUnfExame) throws ApplicationBusinessException {

		AelMetodoUnfExame oldMetodoUnfExame = getAelMetodoUnfExameDAO().obterOriginal(metodoUnfExame.getId());

		preAtualizar(metodoUnfExame, oldMetodoUnfExame);
		getAelMetodoUnfExameDAO().merge(metodoUnfExame);
		posAtualizar(metodoUnfExame, oldMetodoUnfExame);
		getAelMetodoUnfExameDAO().flush();

	}

	/**
	 * @ORADB TRIGGER AELT_MUE_BRI
	 * @param metodoUnfExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	protected void preInserir(AelMetodoUnfExame metodoUnfExame) throws ApplicationBusinessException {
		

		
		metodoUnfExame.setCriadoEm(new Date());
		validarDataFim(metodoUnfExame.getDthrInicio(), metodoUnfExame.getDthrFim());
		atualizarServidor(metodoUnfExame);
		atualizarDataFim(metodoUnfExame);
		validarDatasSobrepostas(metodoUnfExame);

	}

	/**
	 * @ORADB TRIGGER AELT_MUE_BRU
	 * @param metodoUnfExame
	 * @throws ApplicationBusinessException
	 */
	protected void preAtualizar(AelMetodoUnfExame metodoUnfExame, AelMetodoUnfExame oldMetodoUnfExame) throws ApplicationBusinessException {

		validarDataFim(metodoUnfExame.getDthrInicio(), metodoUnfExame.getDthrFim());
		validarAlteracoes(metodoUnfExame, oldMetodoUnfExame);

		if (metodoUnfExame.getDthrFim() == null && oldMetodoUnfExame.getDthrFim() == null) {
			
			atualizarDataFim(metodoUnfExame);
			
		}
		
		validarDatasSobrepostas(metodoUnfExame);

	}

	/**
	 * @ORADB TRIGGER AELT_MUE_ASI - AELP_ENFORCE_MUE_RULES
	 * @param metodoUnfExame
	 * @throws ApplicationBusinessException
	 */
	protected void posInserir(AelMetodoUnfExame metodoUnfExame) throws ApplicationBusinessException {

		validarItensAtivo(metodoUnfExame);
		//validarDatasSobrepostas(metodoUnfExame);

	}

	/**
	 * @ORADB TRIGGER AELT_MUE_ASU - AELP_ENFORCE_MUE_RULES
	 * @param metodoUnfExame
	 * @throws ApplicationBusinessException
	 */
	protected void posAtualizar(AelMetodoUnfExame metodoUnfExame, AelMetodoUnfExame oldMetodoUnfExame) throws ApplicationBusinessException {

		if (metodoUnfExame.getSituacao().equals(DominioSituacao.A) && metodoUnfExame.getDthrFim() == null) {
			
			validarItensAtivo(metodoUnfExame);
			
		}
		
		//validarDatasSobrepostas(metodoUnfExame);
		insereAelMetodoUnfExameJn(metodoUnfExame, oldMetodoUnfExame);

	}

	/**
	 * @ORADB CONSTRAINT AEL_MUE_CK2
	 * @param dthrInicio
	 * @param dthrFim
	 * @throws ApplicationBusinessException 
	 */
	protected void validarDataFim(Date dthrInicio, Date dthrFim) throws ApplicationBusinessException {

		if (dthrFim != null && DateUtil.validaDataMaior(dthrInicio, dthrFim)) {

			throw new ApplicationBusinessException(ManterMetodoUnfExameRNExceptionCode.DATA_FIM_MENOR);

		}

	}

	/**
	 * @ORADB aelk_ael_rn.rn_aelp_atu_servidor
	 * @param metodoUnfExame
	 * @throws ApplicationBusinessException  
	 */
	protected void atualizarServidor(AelMetodoUnfExame metodoUnfExame) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		metodoUnfExame.setServidor(servidorLogado);
	}

	/**
	 * @ORADB aelk_mue_rn.rn_muep_atu_datafim
	 * @param metodoUnfExame
	 */
	protected void atualizarDataFim(AelMetodoUnfExame metodoUnfExame) {

		if (metodoUnfExame.getDthrFim() == null && metodoUnfExame.getSituacao().equals(DominioSituacao.I)) {

			if (DateUtil.validaDataMaior(metodoUnfExame.getDthrInicio(), new Date())) {

				metodoUnfExame.setDthrFim(metodoUnfExame.getDthrInicio());

			} else {

				metodoUnfExame.setDthrFim(new Date());

			}

		}

	}

	/**
	 * @ORADB  AELK_MUE_RN.RN_MUEP_VER_ATIVO
	 * @param metodoUnfExame
	 * @throws ApplicationBusinessException 
	 */
	protected void validarItensAtivo(AelMetodoUnfExame metodoUnfExame) throws ApplicationBusinessException {

		if (metodoUnfExame.getDthrFim() == null && metodoUnfExame.getSituacao().equals(DominioSituacao.A)) {

			if (getAelMetodoUnfExameDAO().possuiMetodosAtivos(metodoUnfExame.getId())) {

				throw new ApplicationBusinessException(ManterMetodoUnfExameRNExceptionCode.AEL_01733);

			}

		}

	}

	/**
	 * @ORADB aelk_mue_rn.rn_muep_ver_sobrep
	 * @param metodoUnfExame
	 * @throws ApplicationBusinessException 
	 */
	protected void validarDatasSobrepostas(AelMetodoUnfExame metodoUnfExame) throws ApplicationBusinessException {

		List<AelMetodoUnfExame> metodos = getAelMetodoUnfExameDAO().obterAelMetodoUnfExame(metodoUnfExame);

		for (AelMetodoUnfExame oldMetodoUnfExame : metodos) {
			if (!oldMetodoUnfExame.equals(metodoUnfExame) && ((oldMetodoUnfExame.getDthrFim() != null && DateUtil.entreTruncado(metodoUnfExame.getDthrInicio(), oldMetodoUnfExame.getDthrInicio(), oldMetodoUnfExame.getDthrFim()))
					|| (metodoUnfExame.getDthrFim() != null && DateUtil.entreTruncado(metodoUnfExame.getDthrFim(), oldMetodoUnfExame.getDthrInicio(), oldMetodoUnfExame.getDthrFim()))
					|| (metodoUnfExame.getDthrFim() != null && DateValidator.validaDataMenor(metodoUnfExame.getDthrInicio(), oldMetodoUnfExame.getDthrInicio()) && DateUtil.validaDataTruncadaMaior(
							metodoUnfExame.getDthrFim(), oldMetodoUnfExame.getDthrFim()))
					|| (metodoUnfExame.getDthrFim() == null && DateValidator.validaDataMenor(metodoUnfExame.getDthrInicio(), oldMetodoUnfExame.getDthrInicio())))) {
				throw new ApplicationBusinessException(ManterMetodoUnfExameRNExceptionCode.AEL_01732);
			}
		}
	}

	/**
	 * @ORADB aelk_mue_rn.rn_muep_ver_update
	 * @param metodoUnfExame
	 * @param oldMetodoUnfExame
	 * @throws ApplicationBusinessException 
	 */
	protected void validarAlteracoes(AelMetodoUnfExame metodoUnfExame, AelMetodoUnfExame oldMetodoUnfExame) throws ApplicationBusinessException {

		if (CoreUtil.modificados(metodoUnfExame.getCriadoEm(), oldMetodoUnfExame.getCriadoEm()) 
				|| CoreUtil.modificados(metodoUnfExame.getServidor().getId().getMatricula(), oldMetodoUnfExame.getServidor().getId().getMatricula())
				|| CoreUtil.modificados(metodoUnfExame.getServidor().getId().getVinCodigo(), oldMetodoUnfExame.getServidor().getId().getVinCodigo())) {

			throw new ApplicationBusinessException(ManterMetodoUnfExameRNExceptionCode.AEL_01735);

		}
		
		if (CoreUtil.modificados(metodoUnfExame.getDthrInicio(), oldMetodoUnfExame.getDthrInicio())) {
			
			throw new ApplicationBusinessException(ManterMetodoUnfExameRNExceptionCode.AEL_01736);
			
		}

	}
	
	/**
	 * @ORADB AELT_MUE_ARU
	 * @param metodoUnfExame
	 * @throws ApplicationBusinessException 
	 */
	protected void insereAelMetodoUnfExameJn(AelMetodoUnfExame metodoUnfExame, AelMetodoUnfExame oldMetodoUnfExame) throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(metodoUnfExame.getCriadoEm(), oldMetodoUnfExame.getCriadoEm()) 
				|| CoreUtil.modificados(metodoUnfExame.getServidor().getId().getMatricula(), oldMetodoUnfExame.getServidor().getId().getMatricula())
				|| CoreUtil.modificados(metodoUnfExame.getServidor().getId().getVinCodigo(), oldMetodoUnfExame.getServidor().getId().getVinCodigo())
				|| CoreUtil.modificados(metodoUnfExame.getSituacao(), oldMetodoUnfExame.getSituacao())
				|| CoreUtil.modificados(metodoUnfExame.getDthrInicio(), oldMetodoUnfExame.getDthrInicio())
				|| CoreUtil.modificados(metodoUnfExame.getDthrFim(), oldMetodoUnfExame.getDthrFim())
				|| CoreUtil.modificados(metodoUnfExame.getId(), oldMetodoUnfExame.getId())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelMetodoUnfExameJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelMetodoUnfExameJn.class, servidorLogado.getUsuario());

			jn.setDthrFim(metodoUnfExame.getDthrFim());
			jn.setDthrInicio(metodoUnfExame.getDthrInicio());
			jn.setMtdSeq(metodoUnfExame.getMetodo().getSeq());
			jn.setSeqp(metodoUnfExame.getId().getSeqp());
			jn.setSerMatricula(metodoUnfExame.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(metodoUnfExame.getServidor().getId().getVinCodigo());
			jn.setSituacao(metodoUnfExame.getSituacao());
			jn.setUfeEmaExaSigla(metodoUnfExame.getUnfExecutaExames().getId().getEmaExaSigla());
			jn.setUfeEmaManSeq(metodoUnfExame.getUnfExecutaExames().getId().getEmaManSeq());
			jn.setUfeUnfSeq(metodoUnfExame.getUnfExecutaExames().getId().getUnfSeq().getSeq());
			jn.setCriadoEm(new Date());
			
			getAelMetodoUnfExameJnDAO().persistir(jn);
			
		}
		
	}
	
	protected AelMetodoUnfExameDAO getAelMetodoUnfExameDAO() {
		return aelMetodoUnfExameDAO;
	}
	
	protected AelMetodoUnfExameJnDAO getAelMetodoUnfExameJnDAO() {
		return aelMetodoUnfExameJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
