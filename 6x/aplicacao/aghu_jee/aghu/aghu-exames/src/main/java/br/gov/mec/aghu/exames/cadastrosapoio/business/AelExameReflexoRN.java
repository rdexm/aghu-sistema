package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExameReflexoDAO;
import br.gov.mec.aghu.exames.dao.AelExameReflexoJnDao;
import br.gov.mec.aghu.exames.dao.AelExamesDependentesDAO;
import br.gov.mec.aghu.model.AelExameReflexo;
import br.gov.mec.aghu.model.AelExameReflexoId;
import br.gov.mec.aghu.model.AelExameReflexoJn;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelExameReflexoRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AelExameReflexoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExamesDependentesDAO aelExamesDependentesDAO;
	
	@Inject
	private AelExameReflexoJnDao aelExameReflexoJnDao;
	
	@Inject
	private AelExameReflexoDAO aelExameReflexoDAO;

	private static final long serialVersionUID = -5644965260779284112L;

	public enum AelExameReflexoRNExceptionCode implements BusinessExceptionCode {
		AEL_02923, AEL_02918, AEL_02919, ;
	}

	public void persitirAelExameReflexo(final AelExameReflexo aelExameReflexo) throws ApplicationBusinessException {
		if (aelExameReflexo.getId() == null || aelExameReflexo.getId().getSeqp() == null) {
			if (aelExameReflexo.getId() == null) {
				aelExameReflexo.setId(gerarIdExameReflexo(aelExameReflexo.getAelExamesMaterialAnalise()));
			}
			this.executarAntesInserir(aelExameReflexo);
			this.getAelExameReflexoDAO().persistir(aelExameReflexo);
		} else {
			final AelExameReflexo aelExameReflexoOld = this.getAelExameReflexoDAO().obterOriginal(aelExameReflexo.getId());
			this.executarAntesAtualizar(aelExameReflexo);
			this.getAelExameReflexoDAO().merge(aelExameReflexo);
			this.executarDepoisAtualizar(aelExameReflexo, aelExameReflexoOld);
		}
	}
	
	public void excluirAelExameReflexo(AelExameReflexo aelExameReflexo) throws ApplicationBusinessException {
		aelExameReflexo = this.getAelExameReflexoDAO().obterPorChavePrimaria(aelExameReflexo.getId());
		final AelExameReflexo aelExameReflexoOld = this.getAelExameReflexoDAO().obterOriginal(aelExameReflexo.getId());
		this.getAelExameReflexoDAO().remover(aelExameReflexo);
		this.executarDepoisExcluir(aelExameReflexoOld);
	}
	
	/*
	 * criado para satisfazer necessidade levantada pelo analista junto a Rosane
	 * "modificar para que sempre seja criada com o valor 'N', pois este é o default do banco."
	 * 
	 */
	private void valorPadraoIndNovaAmostra(final AelExameReflexo aelExameReflexo) {
		if (aelExameReflexo != null && aelExameReflexo.getIndNovaAmostra() == null) {
			aelExameReflexo.setIndNovaAmostra("N");
		}
	}

	/**
	 * 
	 * @ORADB AELT_ERX_ARD
	 * 
	 * @param aelExameReflexoOld
	 * @throws ApplicationBusinessException 
	 */
	private void executarDepoisExcluir(AelExameReflexo aelExameReflexoOld) throws ApplicationBusinessException {
		this.inserirJn(aelExameReflexoOld, DominioOperacoesJournal.DEL);
	}

	/**
	 * @ORADB AELT_ERX_ARU
	 * 
	 * @param aelExameReflexo
	 * @param aelExameReflexoOld
	 * @throws ApplicationBusinessException 
	 */
	private void executarDepoisAtualizar(final AelExameReflexo aelExameReflexo, final AelExameReflexo aelExameReflexoOld) throws ApplicationBusinessException {
		if (!CoreUtil.igual(aelExameReflexo.getRapServidores(), aelExameReflexoOld.getRapServidores())
				|| !CoreUtil.igual(aelExameReflexo.getSituacao(), aelExameReflexoOld.getSituacao())
				|| !CoreUtil.igual(aelExameReflexo.getCriadoEm(), aelExameReflexoOld.getCriadoEm())
				|| !CoreUtil.igual(aelExameReflexo.getAelExamesMaterialAnaliseReflexo(), aelExameReflexoOld.getAelExamesMaterialAnaliseReflexo())
				|| !CoreUtil.igual(aelExameReflexo.getAelCampoLaudo(), aelExameReflexoOld.getAelCampoLaudo())
				|| !CoreUtil.igual(aelExameReflexo.getResulNumIni(), aelExameReflexoOld.getResulNumIni())
				|| !CoreUtil.igual(aelExameReflexo.getResulNumFim(), aelExameReflexoOld.getResulNumFim())
				|| !CoreUtil.igual(aelExameReflexo.getResulAlfa(), aelExameReflexoOld.getResulAlfa())
				|| !CoreUtil.igual(aelExameReflexo.getAelResultadoCodificado(), aelExameReflexoOld.getAelResultadoCodificado())
				|| !CoreUtil.igual(aelExameReflexo.getAelExamesMaterialAnalise(), aelExameReflexoOld.getAelResultadoCodificado())
				|| !CoreUtil.igual(aelExameReflexo.getIndNovaAmostra(), aelExameReflexoOld.getIndNovaAmostra())) {
			this.inserirJn(aelExameReflexoOld, DominioOperacoesJournal.UPD);
		}
	}

	private void inserirJn(final AelExameReflexo aelExameReflexoOld, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelExameReflexoJn jn = BaseJournalFactory.getBaseJournal(operacao, AelExameReflexoJn.class, servidorLogado.getUsuario());
		if (aelExameReflexoOld.getAelCampoLaudo() != null) {
			jn.setCalSeq(aelExameReflexoOld.getAelCampoLaudo().getSeq());
		}
		jn.setCriadoEm(aelExameReflexoOld.getCriadoEm());
		jn.setEmaExaSigla(aelExameReflexoOld.getId().getEmaExaSigla());
		jn.setEmaManSeq(aelExameReflexoOld.getId().getEmaManSeq());
		jn.setSeqp(aelExameReflexoOld.getId().getSeqp());

		if (aelExameReflexoOld.getAelExamesMaterialAnaliseReflexo() != null) {
			jn.setEmaExaSiglaReflexo(aelExameReflexoOld.getAelExamesMaterialAnaliseReflexo().getId().getExaSigla());
			jn.setEmaManSeqReflexo(aelExameReflexoOld.getAelExamesMaterialAnaliseReflexo().getId().getManSeq());
		}

		jn.setIdadeLimite(aelExameReflexoOld.getIdadeLimite());
		jn.setIndNovaAmostra(aelExameReflexoOld.getIndNovaAmostra());
		jn.setSituacao(aelExameReflexoOld.getSituacao());
		if (aelExameReflexoOld.getAelResultadoCodificado() != null) {
			jn.setRcdGtcSeq(aelExameReflexoOld.getAelResultadoCodificado().getId().getGtcSeq());
			jn.setRcdSeqp(aelExameReflexoOld.getAelResultadoCodificado().getId().getSeqp());
		}
		jn.setResulAlfa(aelExameReflexoOld.getResulAlfa());
		jn.setResulNumFim(aelExameReflexoOld.getResulNumFim());
		jn.setResulNumIni(aelExameReflexoOld.getResulNumIni());
		if (aelExameReflexoOld.getRapServidores() != null) {
			jn.setSerMatricula(aelExameReflexoOld.getRapServidores().getId().getMatricula());
			jn.setSerVinCodigo(aelExameReflexoOld.getRapServidores().getId().getVinCodigo());
		}
		this.getAelExameReflexoJnDao().persistir(jn);
	}

	private AelExameReflexoId gerarIdExameReflexo(AelExamesMaterialAnalise aelExamesMaterialAnalise) {
		final AelExameReflexoId id = new AelExameReflexoId();
		id.setEmaExaSigla(aelExamesMaterialAnalise.getId().getExaSigla());
		id.setEmaManSeq(aelExamesMaterialAnalise.getId().getManSeq());
		id.setSeqp(this.getAelExameReflexoDAO().getNextSeqp(id));
		return id;
	}

	/**
	 * @ORADB AELT_ERX_BRU
	 * 
	 * @param aelExameReflexo
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesAtualizar(final AelExameReflexo aelExameReflexo) throws ApplicationBusinessException {
		this.verificarDependente(aelExameReflexo.getAelExamesMaterialAnaliseReflexo());
		this.verificarPremissa(aelExameReflexo.getAelExamesMaterialAnaliseReflexo());
		this.verificarAutoReflexo(aelExameReflexo);
		this.valorPadraoIndNovaAmostra(aelExameReflexo);
	}

	/**
	 * 
	 * @ORADB AELT_ERX_BRI
	 * 
	 * @param aelExameReflexo
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesInserir(final AelExameReflexo aelExameReflexo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelExameReflexo.setCriadoEm(new Date());
		aelExameReflexo.setRapServidores(servidorLogado);

		this.verificarDependente(aelExameReflexo.getAelExamesMaterialAnaliseReflexo());
		this.verificarPremissa(aelExameReflexo.getAelExamesMaterialAnaliseReflexo());
		this.verificarAutoReflexo(aelExameReflexo);
		this.valorPadraoIndNovaAmostra(aelExameReflexo);
	}

	private void verificarAutoReflexo(final AelExameReflexo aelExameReflexo) throws ApplicationBusinessException {
		if (aelExameReflexo.getAelExamesMaterialAnalise().equals(aelExameReflexo.getAelExamesMaterialAnaliseReflexo())) {
			throw new ApplicationBusinessException(AelExameReflexoRNExceptionCode.AEL_02919);
		}
	}

	/**
	 * @ORADB AELK_ERX_RN.RN_ERXP_VER_DEPEND
	 */
	private void verificarDependente(final AelExamesMaterialAnalise material) throws ApplicationBusinessException {
		/* Exame reflexo não pode ter exames dependentes */
		if (getAelExamesDependentesDAO().contarAelExamesDependentesPorMaterial(material) > 0) {
			throw new ApplicationBusinessException(AelExameReflexoRNExceptionCode.AEL_02918);
		}
	}

	/**
	 * @ORADB AELK_ERX_RN.RN_ERXP_VER_PREMISSA
	 */
	private void verificarPremissa(final AelExamesMaterialAnalise aelExamesMaterialAnalise) throws ApplicationBusinessException {
		/*
		 * Exame reflexo não está preparada para gerar mais de uma amostra ou
		 * para solicitar informações que dependem de resposta do solicitante
		 */
		if (aelExamesMaterialAnalise.getIndSolicInformaColetas() || aelExamesMaterialAnalise.getIndGeraItemPorColetas()
				|| aelExamesMaterialAnalise.getAelMateriaisAnalises().getIndExigeDescMatAnls()) {
			throw new ApplicationBusinessException(AelExameReflexoRNExceptionCode.AEL_02923);
		}
	}

	protected AelExamesDependentesDAO getAelExamesDependentesDAO() {
		return aelExamesDependentesDAO;
	}

	public AelExameReflexoDAO getAelExameReflexoDAO() {
		return aelExameReflexoDAO;
	}
	
	public AelExameReflexoJnDao getAelExameReflexoJnDao() {
		return aelExameReflexoJnDao;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}