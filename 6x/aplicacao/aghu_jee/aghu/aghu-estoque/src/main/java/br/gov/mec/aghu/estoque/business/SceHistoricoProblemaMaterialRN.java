package br.gov.mec.aghu.estoque.business;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class SceHistoricoProblemaMaterialRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(SceHistoricoProblemaMaterialRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7717182133565439430L;
	private final static Integer valor = 0;

	public enum SceHistoricoProblemaMaterialRNExceptionCode implements BusinessExceptionCode {
		SCE_00513, SCE_00286, SCE_00290, SCE_00292, SCE_00724, SCE_00724_2;
	}

	/**
	 * ORADB TRIGGER SCET_HPM_BRI
	 * @param historicoProblemaMaterial
	 * @throws BaseException
	 */
	public void inserir(SceHistoricoProblemaMaterial historicoProblemaMaterial, Boolean flush) throws BaseException {

		preInserir(historicoProblemaMaterial);
		getSceHistoricoProblemaMaterialDAO().persistir(historicoProblemaMaterial);
		if (flush){
		    getSceHistoricoProblemaMaterialDAO().flush();
		}

	}
	
	

	/**
	 * @ORADB SCET_HPM_BRU
	 * @param historicoProblemaMaterial
	 * @param flush
	 * @throws BaseException
	 */
	public void atualizar(SceHistoricoProblemaMaterial historicoProblemaMaterial, Boolean flush) throws BaseException {
		preAtualizar(historicoProblemaMaterial);
		getSceHistoricoProblemaMaterialDAO().merge(historicoProblemaMaterial);
		
		if (flush) {
			getSceHistoricoProblemaMaterialDAO().flush();
		}
	}

	/**
	 * 
	 * @param historicoProblemaMaterial
	 * @throws BaseException
	 */
	private void preInserir(SceHistoricoProblemaMaterial historicoProblemaMaterial) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		historicoProblemaMaterial.setServidor(servidorLogado);
		historicoProblemaMaterial.setDtGeracao(new Date());

		if (historicoProblemaMaterial.getSceEstqAlmox().getIndSituacao().equals(DominioSituacao.I)) {
			throw new ApplicationBusinessException(SceHistoricoProblemaMaterialRNExceptionCode.SCE_00292);
		}

		if (historicoProblemaMaterial.getFornecedor().getSituacao().equals(DominioSituacao.I)) {
			throw new ApplicationBusinessException(SceHistoricoProblemaMaterialRNExceptionCode.SCE_00290);
		}

		if (historicoProblemaMaterial.getMotivoProblema().getIndSituacao().equals(DominioSituacao.I)) {
			throw new ApplicationBusinessException(SceHistoricoProblemaMaterialRNExceptionCode.SCE_00286);
		}

		atualizarIndEfetivado(historicoProblemaMaterial);
		historicoProblemaMaterial.setQtdeDesbloqueada(valor);
		historicoProblemaMaterial.setQtdeDf(valor);

		if (historicoProblemaMaterial.getQtdeProblema() < (historicoProblemaMaterial.getQtdeDesbloqueada() + historicoProblemaMaterial.getQtdeDf())) {
			throw new ApplicationBusinessException(SceHistoricoProblemaMaterialRNExceptionCode.SCE_00724_2);
		}
	}

	/**
	 * 
	 * @param historicoProblemaMaterial
	 * @throws BaseException
	 */
	private void preAtualizar(SceHistoricoProblemaMaterial historicoProblemaMaterial) throws BaseException {

		SceHistoricoProblemaMaterial historicoProblemaMaterialOriginal = getSceHistoricoProblemaMaterialDAO().obterSceHistoricoProblemaMaterial(historicoProblemaMaterial.getSeq());

		if (CoreUtil.modificados(historicoProblemaMaterial.getDtGeracao(), historicoProblemaMaterialOriginal.getDtGeracao())
				|| CoreUtil.modificados(historicoProblemaMaterial.getMotivoProblema().getSeq(), historicoProblemaMaterialOriginal.getMotivoProblema().getSeq())
				|| CoreUtil.modificados(historicoProblemaMaterial.getFornecedor().getNumero(), historicoProblemaMaterialOriginal.getFornecedor().getNumero())
				|| CoreUtil.modificados(historicoProblemaMaterial.getServidor().getId().getMatricula(), historicoProblemaMaterialOriginal.getServidor().getId().getMatricula())) {

			throw new ApplicationBusinessException(SceHistoricoProblemaMaterialRNExceptionCode.SCE_00513);

		}
		
		if (historicoProblemaMaterial.getQtdeProblema() < (historicoProblemaMaterial.getQtdeDesbloqueada() + historicoProblemaMaterial.getQtdeDf())) {
			throw new ApplicationBusinessException(SceHistoricoProblemaMaterialRNExceptionCode.SCE_00724_2);
		}

		atualizarIndEfetivado(historicoProblemaMaterial);

	}
	
	
	/** Valida se a quantidade problema é menor que a soma de quantidade desbloqueada mais a quantidade devolvida.
	 * ORADB CONSTRAINT sce_hpm_ck5
	 * @param historicoProblemaMaterial
	 * @throws BaseException
	 */
	public void validaQtdeProblemaMenorDesbloqueadaMaisDevolvidaAux(SceHistoricoProblemaMaterial historicoProblemaMaterial, Integer qtdeAcaoBloqueioDesbloqueio) throws BaseException {
		if (historicoProblemaMaterial.getQtdeProblema() < ((historicoProblemaMaterial.getQtdeDesbloqueada() + qtdeAcaoBloqueioDesbloqueio) + historicoProblemaMaterial.getQtdeDf())) {
			throw new ApplicationBusinessException(SceHistoricoProblemaMaterialRNExceptionCode.SCE_00724_2);
		}
	}


	/**
	 * ORADB PROCEDURE RN_HPMP_ATU_IND_EFET
	 * @param historicoProblemaMaterial
	 * @throws BaseException
	 */
	private void atualizarIndEfetivado(SceHistoricoProblemaMaterial historicoProblemaMaterial) throws BaseException {

		Integer quantidade = historicoProblemaMaterial.getQtdeDf() + historicoProblemaMaterial.getQtdeDesbloqueada();

		if (quantidade.equals(historicoProblemaMaterial.getQtdeProblema())) {

			historicoProblemaMaterial.setIndEfetivado(Boolean.TRUE);

		} else {

			historicoProblemaMaterial.setIndEfetivado(Boolean.FALSE);

		}

	}

	protected SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaMaterialDAO() {
		return sceHistoricoProblemaMaterialDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
