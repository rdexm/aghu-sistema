package br.gov.mec.aghu.exames.patologia.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoDAO;
import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.exames.dao.AelExameApJnDAO;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelExameApRN extends BaseBusiness {

	@EJB
	private PatologiaRN patologiaRN;
	
	private static final Log LOG = LogFactory.getLog(AelExameApRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExameApJnDAO aelExameApJnDAO;
	
	@Inject
	private AelExameApDAO aelExameApDAO;
	
	@Inject
	private AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO;
	
	private static final long serialVersionUID = 4879378548867391256L;
	
	
	public enum AelExameApRNExceptionCode implements
			BusinessExceptionCode {
		AEL_00353
	}

	public void inserirAelExameApRN(final AelExameAp aelExameAp) throws BaseException {
		final AelExameApDAO dao = getAelExameApDAO();
		this.executarAntesInserirAelExameAp(aelExameAp);
		dao.persistir(aelExameAp);
		this.executarStatementAposInserirAelExameAp(aelExameAp);
	}

	public void atualizarAelExameApRN(final AelExameAp aelExameApNew, AelExameAp aelExameApOld) throws BaseException {
		if(aelExameApOld == null){
			aelExameApOld = aelExameApDAO.obterOriginal(aelExameApNew);
		}
		this.executarAntesAtualizarAelExameAp(aelExameApNew, aelExameApOld);
		aelExameApDAO.atualizar(aelExameApNew);
		this.executarStatementDepoisAtualizarAelExameAp(aelExameApNew, aelExameApOld);
		this.executarDepoisAtualizarAelExameAp(aelExameApNew, aelExameApOld);
	}
	
	public void atualizarAelExameApRN(final AelExameAp aelExameApNew) throws BaseException {
		this.atualizarAelExameApRN(aelExameApNew, null);
	}
	

	public void excluir(final AelExameAp aelExameAp, String usuarioLogado) throws BaseException {
		final AelExameApDAO dao = getAelExameApDAO();
		dao.remover(aelExameAp);
		this.executarAposExcluir(aelExameAp, usuarioLogado);
	}	

	private void executarAposExcluir(AelExameAp aelExameAp, String usuarioLogado) {
		insereJournal(aelExameAp, DominioOperacoesJournal.DEL);		
	}	
	
	/**
	 * ORADB Trigger AELT_LUX_BRI
	 * 
	 * @param aelExameAp
	 * @throws ApplicationBusinessException  
	 */
	protected void executarAntesInserirAelExameAp(final AelExameAp aelExameAp) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final PatologiaRN patologiaRN = getPatologiaRN();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelExameApRNExceptionCode.AEL_00353);
		}
		
		aelExameAp.setServidor(servidorLogado);
		getPatologiaRN().rnLuxpVerNumAp(aelExameAp);
		
		//--colocada na Enforce
		//--A cada nova etapas_laudo deve ser inserido um registro na tabela ael_extrato_exame_aps
		//--aelk_lux_rn.rn_luxp_atu_extrato(:new.seq,:new.etapas_laudo,:new.ser_matricula,:new.ser_vin_codigo);

		//--Se etapa_laudo='LA', o ind_permite_lib_laudo da tab patologistas deve ser 'S'
		if (DominioSituacaoExamePatologia.LA.equals(aelExameAp.getEtapasLaudo())) {
			patologiaRN.rnLuxpVerIndLib(aelExameAp.getServidor());
		}
		
		//--Se ind_impresso = 'S', a dthr_impressao dever ser not null.
		//--Se ind_impresso = 'N', a dthr_impressao dever ser nula.
		patologiaRN.rnLuxpVerDthrImp(aelExameAp.getIndImpresso(), aelExameAp.getDthrImpressao());
		
		//--Enforce
		//--Número Ap Origem deve deve ser informado qdo o indicador de obrigatório está selecionado no cadastro de Exames.
		//--aelk_lux_rn.rn_luxp_ver_exame(:new.seq);

		//--Enforce
		//--IF :new.etapas_laudo = 'LA' AND :new.nro_ap_origem IS NOT NULL THEN
		//--   aelk_lux_rn.rn_luxp_ins_nota(:new.nro_ap_origem);
		//--END IF;
	}
	
	/**
	 * ORADB Trigger AELT_LUX_ASI
	 * 
	 * @param aelExameAp
	 * @throws BaseException
	 */
	protected void executarStatementAposInserirAelExameAp(AelExameAp aelExameAp) throws BaseException {
		
		this.aelpEnforceLuxRulesInsert(aelExameAp);
		
	}
	
	/**
	 * ORADB Trigger AELT_LUX_ARU
	 * 
	 * @param aelExameApNew
	 * @param aelExameApOld
	 * @throws ApplicationBusinessException 
	 */
	protected void executarDepoisAtualizarAelExameAp(final AelExameAp aelExameApNew,
			final AelExameAp aelExameApOld) throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(aelExameApNew.getSeq(), aelExameApOld.getSeq())
		||  CoreUtil.modificados(aelExameApNew.getMateriais(), aelExameApOld.getMateriais())
		||  CoreUtil.modificados(aelExameApNew.getSituacao(), aelExameApOld.getSituacao())
		||  CoreUtil.modificados(aelExameApNew.getAelAnatomoPatologicoOrigem(), aelExameApOld.getAelAnatomoPatologicoOrigem())
		||  CoreUtil.modificados(aelExameApNew.getIndRevisaoLaudo(), aelExameApOld.getIndRevisaoLaudo())
		||  CoreUtil.modificados(aelExameApNew.getEtapasLaudo(), aelExameApOld.getEtapasLaudo())
		||  CoreUtil.modificados(aelExameApNew.getObservacoes(), aelExameApOld.getObservacoes())
		||  CoreUtil.modificados(aelExameApNew.getIndLaudoVisual(), aelExameApOld.getIndLaudoVisual())
		||  CoreUtil.modificados(aelExameApNew.getIndImpresso(), aelExameApOld.getIndImpresso())
		||  CoreUtil.modificados(aelExameApNew.getDthrImpressao(), aelExameApOld.getDthrImpressao())
		||  CoreUtil.modificados(aelExameApNew.getAelAnatomoPatologicos(), aelExameApOld.getAelAnatomoPatologicos())
		||  CoreUtil.modificados(aelExameApNew.getConfigExLaudoUnico(), aelExameApOld.getConfigExLaudoUnico())
		||  CoreUtil.modificados(aelExameApNew.getServidor(), aelExameApOld.getServidor())
		||  CoreUtil.modificados(aelExameApNew.getServidorRespLaudo(), aelExameApOld.getServidorRespLaudo())
		||  CoreUtil.modificados(aelExameApNew.getObservacaoTecnica(), aelExameApOld.getObservacaoTecnica())
		||  CoreUtil.modificados(aelExameApNew.getIndIndiceBloco(), aelExameApOld.getIndIndiceBloco())
		||  CoreUtil.modificados(aelExameApNew.getServidorRespImpresso(), aelExameApOld.getServidorRespImpresso())) {
			
			insereJournal(aelExameApOld, DominioOperacoesJournal.UPD);
		}
		
	}

	private void insereJournal(final AelExameAp aelExameApOld,
			final DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelExameApJn jn = new  AelExameApJn();
		
		jn.setNomeUsuario(servidorLogado.getUsuario());
		jn.setOperacao(operacao);
		jn.setSeq(aelExameApOld.getSeq());
		jn.setMateriais(aelExameApOld.getMateriais());
		jn.setSituacao(aelExameApOld.getSituacao());
		jn.setAelAnatomoPatologicoOrigem(aelExameApOld.getAelAnatomoPatologicoOrigem()!=null?aelExameApOld.getAelAnatomoPatologicoOrigem().getSeq():null);
		jn.setIndRevisaoLaudo(aelExameApOld.getIndRevisaoLaudo());
		jn.setEtapasLaudo(aelExameApOld.getEtapasLaudo());
		jn.setObservacoes(aelExameApOld.getObservacoes());
		jn.setIndLaudoVisual(aelExameApOld.getIndLaudoVisual());
		jn.setIndImpresso(aelExameApOld.getIndImpresso());
		jn.setDthrImpressao(aelExameApOld.getDthrImpressao());
		jn.setLumSeq(aelExameApOld.getAelAnatomoPatologicos().getSeq());
		jn.setLu2Seq(aelExameApOld.getConfigExLaudoUnico() == null ? null : aelExameApOld.getConfigExLaudoUnico().getSeq());
		jn.setServidor(aelExameApOld.getServidor());
		jn.setServidorRespImpresso(aelExameApOld.getServidorRespImpresso());
		jn.setServidorRespLaudo(aelExameApOld.getServidorRespLaudo());
		jn.setObservacaoTecnica(aelExameApOld.getObservacaoTecnica());
		jn.setIndIndiceBloco(aelExameApOld.getIndIndiceBloco());
		
		final AelExameApJnDAO dao = getAelExameApJnDAO();
		dao.persistir(jn);
	}

	/**
	 * ORADB Trigger AELT_LUX_BRU
	 * 
	 * @param aelExameApNew
	 * @param aelExameApOld
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAntesAtualizarAelExameAp(final AelExameAp aelExameApNew,
			final AelExameAp aelExameApOld) throws ApplicationBusinessException {
		final PatologiaRN patologiaRN = getPatologiaRN();
		
		//--Número Ap origem não pode ser o mesmo que o número Ap da ael_anatomo_patologicos
		//--Número Ap origem deve existir como número AP na ael_anatomo_patologicos
		if (CoreUtil.modificados(aelExameApNew.getAelAnatomoPatologicoOrigem(), aelExameApOld.getAelAnatomoPatologicoOrigem())) {
			patologiaRN.rnLuxpVerNumAp(aelExameApNew);
		}
		
		//--colocada na Enforce
		//--A cada nova etapas_laudo deve ser inserido um registro na tabela ael_extrato_exame_aps
		//--aelk_lux_rn.rn_luxp_atu_extrato(:new.seq,:new.etapas_laudo,:new.ser_matricula,:new.ser_vin_codigo);

		//--Se etapa_laudo='LA', o ind_permite_lib_laudo da tab patologistas deve ser 'S'
		if (CoreUtil.modificados(aelExameApNew.getEtapasLaudo(), aelExameApOld.getEtapasLaudo()) &&
				DominioSituacaoExamePatologia.LA.equals(aelExameApNew.getEtapasLaudo())) {
//			if (DominioSituacaoExamePatologia.LA.equals(aelExameApNew.getEtapasLaudo())) {
				patologiaRN.rnLuxpVerIndLib(aelExameApNew.getServidorRespLaudo());
//			}
		}
		
		//--Se ind_impresso = 'S', a dthr_impressao dever ser not null.
		//--Se ind_impresso = 'N', a dthr_impressao dever ser nula.
		if (CoreUtil.modificados(aelExameApNew.getIndImpresso(), aelExameApOld.getIndImpresso()) ||
			CoreUtil.modificados(aelExameApNew.getDthrImpressao(), aelExameApOld.getDthrImpressao())) {
			patologiaRN.rnLuxpVerDthrImp(aelExameApNew.getIndImpresso(), aelExameApNew.getDthrImpressao());
		}
	}


	/**
	 * ORADB Trigger AELT_LUX_ASU
	 * 
	 * @param aelExameApNew
	 * @param aelExameApOld
	 * @throws BaseException 
	 */
	protected void executarStatementDepoisAtualizarAelExameAp(
			AelExameAp aelExameApNew, AelExameAp aelExameApOld) throws BaseException {
		
		this.aelpEnforceLuxRulesUpdate(aelExameApNew, aelExameApOld);
		
	}
	
	
	/**
	 * ORADB Enforce AELP_ENFORCE_LUX_RULES - Update
	 * 
	 * @param aelExameApNew
	 * @param aelExameApOld
	 * @throws BaseException 
	 */
	protected void aelpEnforceLuxRulesUpdate(AelExameAp aelExameApNew, AelExameAp aelExameApOld) throws BaseException {
		
		final PatologiaRN patologiaRN = getPatologiaRN();
		
		if (CoreUtil.modificados(aelExameApNew.getEtapasLaudo(), aelExameApOld.getEtapasLaudo())) {
			patologiaRN.rnLuxpAtuExtrato(aelExameApNew.getSeq(), aelExameApNew.getEtapasLaudo(), aelExameApNew.getServidor());
		}
		
		if (CoreUtil.modificados(aelExameApNew.getSituacao(), aelExameApOld.getSituacao())) {
			patologiaRN.rnLuxpAtuOcorrenc(aelExameApNew.getSeq(), aelExameApNew.getSituacao(), aelExameApNew.getServidor());
		}
				
		if (CoreUtil.modificados(aelExameApNew.getAelAnatomoPatologicoOrigem(), aelExameApOld.getAelAnatomoPatologicoOrigem()) && aelExameApNew.getAelAnatomoPatologicoOrigem() == null) {
			patologiaRN.rnLuxpVerExame(aelExameApNew.getSeq());
		}
		
	}
	
	/**
	 * ORADB Enforce AELP_ENFORCE_LUX_RULES - Insert
	 * 
	 * @param aelExameApNew
	 * @throws BaseException 
	 */
	protected void aelpEnforceLuxRulesInsert(AelExameAp aelExameApNew) throws BaseException {
		
		final PatologiaRN patologiaRN = getPatologiaRN();
		
		patologiaRN.rnLuxpAtuExtrato(aelExameApNew.getSeq(), aelExameApNew.getEtapasLaudo(), aelExameApNew.getServidor());
		
		patologiaRN.rnLuxpAtuOcorrenc(aelExameApNew.getSeq(), aelExameApNew.getSituacao(), aelExameApNew.getServidor());

		if (DominioSituacaoExamePatologia.LA.equals(aelExameApNew.getEtapasLaudo()) && aelExameApNew.getAelAnatomoPatologicoOrigem().getNumeroAp() != null) {
			patologiaRN.rnLuxpInsNota(aelExameApNew);
		}
		
		if (aelExameApNew.getAelAnatomoPatologicoOrigem() == null) {
			patologiaRN.rnLuxpVerExame(aelExameApNew.getSeq());
		}
	}

	protected AelExameApDAO getAelExameApDAO() {
		return aelExameApDAO;
	}
	
	protected AelAnatomoPatologicoDAO getAelAnatomoPatologicoDAO(){
		return aelAnatomoPatologicoDAO;
	}

	protected PatologiaRN getPatologiaRN() {
		return patologiaRN;
	}
	
	protected AelExameApJnDAO getAelExameApJnDAO() {
		return aelExameApJnDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
