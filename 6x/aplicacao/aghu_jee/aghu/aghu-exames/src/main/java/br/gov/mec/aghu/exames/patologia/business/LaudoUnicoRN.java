package br.gov.mec.aghu.exames.patologia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaDAO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelMaterialAp;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/** 
 * Classe que implementa as RNs do Laudo Unico
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class LaudoUnicoRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(LaudoUnicoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExameApDAO aelExameApDAO;
	
	@Inject
	private AelPatologistaDAO aelPatologistaDAO;

	private static final long serialVersionUID = 1038213071309421356L;

	private enum LaudoUnicoRNExceptionCode implements BusinessExceptionCode {
		  AEL_02633
		, AEL_02634
		, AEL_02620
		, AEL_02639
		, AEL_02640
		, AEL_02646
		, AEL_02645
		, AEL_02769
		, AEL_02770
		, AEL_02767
		, AEL_02768
		, AEL_02648
		, AEL_02649
		, AEL_02652
		, AEL_02653
		, AEL_02654
		, AEL_02660
		, AEL_02693
		, AEL_02694
		, AEL_02771
		, AEL_02772				
		;
	}
	
	/**
	 * ORADB Function AELK_AEL_RN.RN_AELP_ATU_SERVIDOR
	 * 
	 * @param aelMaterialAp
	 * @throws ApplicationBusinessException  
	 */
	public void aelpAtuServidor(AelMaterialAp aelMaterialAp) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelMaterialAp.setRapServidores(servidorLogado);
	}

	/**
	 * ORADB Function AELK_AEL_RN.RN_AELP_ATU_SERVIDOR
	 * 
	 * @param aelAnatomoPatologico
	 * @throws ApplicationBusinessException  
	 */
	public void aelpAtuServidor(AelAnatomoPatologico aelAnatomoPatologico) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelAnatomoPatologico.setServidor(servidorLogado);
	}

	/**
	 * ORADB Function AELK_LUR_RN.RN_LURP_VER_ETAP_LAU
	 * 
	 * @param luxSeq
	 * @throws ApplicationBusinessException 
	 */
	public void rnLurpVerEtapLau(Long luxSeq) throws ApplicationBusinessException {
		AelExameAp exameAp = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		
		if (exameAp != null && DominioSituacaoExamePatologia.LA.equals(exameAp.getEtapasLaudo())) {
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02633);
			//Laudo já Assinado(LA), não é permitido nenhum tipo de alteração desse exame!
		}
	}
	

	/**
	 * ORADB Function AELK_LUD_RN.RN_LUDP_VER_ETAP_LAU
	 * 
	 * @param luxSeq
	 * @throws ApplicationBusinessException 
	 */
	public void rnLudpVerEtapLau(Long luxSeq) throws ApplicationBusinessException {
		AelExameAp exameAp = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		
		if (exameAp != null && DominioSituacaoExamePatologia.LA.equals(exameAp.getEtapasLaudo())) {
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02653);
			//--Laudo já Assinado(LA)! Não é permitido nenhum tipo de alteração!!
		}
	}
	

	/**
	 * ORADB Function AELK_LUZ_RN.RN_LUZP_VER_ETAP_LAU
	 * 
	 * @param luxSeq
	 * @throws ApplicationBusinessException 
	 */
	public void rnLuzpVerEtapLau(Long luxSeq) throws ApplicationBusinessException {
		AelExameAp exameAp = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		
		if (exameAp != null && DominioSituacaoExamePatologia.LA.equals(exameAp.getEtapasLaudo())) {
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02694);
			//Laudo já Assinado(LA), não é permitido nenhum tipo de alteração desse exame!
		}
	}
	
	/**
	 * ORADB Function AELK_LO8_RN.RN_LO8P_VER_ETAP_LA
	 * 
	 * @param luxSeq
	 * @throws ApplicationBusinessException 
	 */
	public void rnLo8pVerEtapLa(Long luxSeq) throws ApplicationBusinessException {
		AelExameAp exameAp = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		
		if (exameAp != null && DominioSituacaoExamePatologia.LA.equals(exameAp.getEtapasLaudo())) {
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02771);
			//Laudo já Assinado(LA), não é permitido nenhum tipo de alteração desse exame!
		}
	}
	
	
	/**
	 * ORADB Function AELK_LUP_RN.RN_LUPP_VER_ETAP_LAU
	 * 
	 * @param luxSeq
	 * @throws ApplicationBusinessException 
	 */
	public void rnLuppVerEtapLau(Long luxSeq) throws ApplicationBusinessException {
		AelExameAp exameAp = getAelExameApDAO().obterOriginal(luxSeq);
		
		if (exameAp != null && DominioSituacaoExamePatologia.LA.equals(exameAp.getEtapasLaudo())) {
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02648);
			//Laudo já Assinado(LA)! Não é permitido nenhum tipo de alteração!
		}
	}
		
	/**
	 * ORADB Function AELK_LUD_RN.RN_LUDP_VER_SIT_EXME
	 * 
	 * @param luxSeq
	 * @throws ApplicationBusinessException 
	 */
	public void rnLudpVerSitExme(Long luxSeq) throws ApplicationBusinessException {
		AelExameAp exameAp = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		
		if (exameAp != null && !DominioSituacaoExamePatologia.LA.equals(exameAp.getEtapasLaudo())) {
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02652);
			//--Para inclusão de Nota Adicional, a situação do Laudo deve ser Assinada(LA)!
		}
	}
		
	/**
	 * ORADB Function AELK_LUO_RN.RN_LUOP_VER_ETAP_LAU
	 * 
	 * @param luxSeq
	 * @throws ApplicationBusinessException 
	 */
	public void rnLuopVerEtapLau(Long luxSeq) throws ApplicationBusinessException {
		AelExameAp exameAp = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		
		if (exameAp != null && DominioSituacaoExamePatologia.LA.equals(exameAp.getEtapasLaudo())) {
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02639);
			// Laudo já Assinado(LA), não é permitido nenhum tipo de alteração!
		}
	}

	/**
	 * ORADB Function AELK_LO7_RN.RN_LO7P_VER_ETAP_LAU
	 * 
	 * @param luxSeq
	 * @throws ApplicationBusinessException 
	 */
	public void rnL07VerEtapLau(Long luxSeq) throws ApplicationBusinessException {
		AelExameAp exameAp = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		
		if (exameAp != null && DominioSituacaoExamePatologia.LA.equals(exameAp.getEtapasLaudo())) {
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02769);
			// Laudo já Assinado(LA), não é permitido nenhum tipo de alteração!
		}
	}

	/**
	 * ORADB Function AELK_LO7_RN.RN_LO7P_VER_SERVIDOR
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException 
	 */
	public void rnL07VerServidor(RapServidores servidor) throws ApplicationBusinessException {
		if (!getAelPatologistaDAO().isPatologistaAtivoPorServidor(servidor)) {
			//--Servidor deve estar cadastrado como Patologista!
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02770);
		}
	}

	/**
	 * ORADB Function AELK_LO6_RN.RN_LO6P_VER_ETAP_LAU
	 * 
	 * @param luxSeq
	 * @throws ApplicationBusinessException 
	 */
	public void rnL06VerEtapLau(Long luxSeq) throws ApplicationBusinessException {
		AelExameAp exameAp = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		
		if (exameAp != null && DominioSituacaoExamePatologia.LA.equals(exameAp.getEtapasLaudo())) {
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02767);
			// Laudo já Assinado(LA), não é permitido nenhum tipo de alteração desse exame!
		}
	}

	/**
	 * ORADB Function AELK_LO6_RN.RN_LO6P_VER_SERVIDOR
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException 
	 */
	public void rnL06VerServidor(RapServidores servidor) throws ApplicationBusinessException {
		if (!getAelPatologistaDAO().isPatologistaAtivoPorServidor(servidor)) {
			//--Este servidor não está cadastrado como Patologista!
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02768);
		}
	}

	
	/**
	 * ORADB Function AELK_LUNP_RN.RN_LUNP_VER_ETAP_LAU
	 * 
	 * @param luxSeq
	 * @throws ApplicationBusinessException 
	 */
	public void rnLunpVerEtapLau(Long luxSeq) throws ApplicationBusinessException {
		AelExameAp exameAp = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		
		if (exameAp != null && DominioSituacaoExamePatologia.LA.equals(exameAp.getEtapasLaudo())) {
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02645);
			// Laudo já Assinado(LA), não é permitido nenhum tipo de alteração!
		}
	}

	
	/**
	 * ORADB Function AELK_LUNP_RN.RN_LUNP_VER_SERVIDOR
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException 
	 */
	public void rnLunpVerServidor(RapServidores servidor) throws ApplicationBusinessException {
		if (!getAelPatologistaDAO().isPatologistaAtivoPorServidor(servidor)) {
			//--Servidor deve estar cadastrado como Patologista!
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02646);
		}
	}

	
	/**
	 * ORADB Function AELK_LUR_RN.RN_LURP_VER_SERVIDOR
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException 
	 */
	public void rnLurpVerServidor(RapServidores servidor) throws ApplicationBusinessException {
		if (!getAelPatologistaDAO().isPatologistaAtivoPorServidor(servidor)) {
			//--Este servidor não está cadastrado como Patologista!
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02634);
		}
	}
	
	/**
	 * ORADB Function AELK_LUO_RN.RN_LUOP_VER_SERVIDOR
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException 
	 */
	public void rnLuopVerServidor(RapServidores servidor) throws ApplicationBusinessException {
		if (!getAelPatologistaDAO().isPatologistaAtivoPorServidor(servidor)) {
			//--Servidor deve estar cadastrado como Patologista!
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02640);
		}
	}

	
	/**
	 * ORADB Function AELK_LUM_RN.RN_LUMP_VER_SERVIDOR
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException 
	 */
	public void rnLumpVerServidor(RapServidores servidor) throws BaseException {
		if (!getAelPatologistaDAO().isPatologistaAtivoPorServidor(servidor)) {
			//--Servidor Responsável Não está cadastrado como Patologista!
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02620);
		}
	}

	/**
	 * ORADB Function AELK_LUD_RN.RN_LUDP_VER_SERVIDOR
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException 
	 */
	public void rnLudpVerServidor(RapServidores servidor) throws ApplicationBusinessException {
		if (!getAelPatologistaDAO().isPatologistaAtivoPorServidor(servidor)) {
			//--Servidor Responsável Não está cadastrado como Patologista!
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02654);
		}
	}
		
	/**
	 * ORADB Function AELK_LUZ_RN.RN_LUZP_VER_SERVIDOR
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException 
	 */
	public void rnLuzpVerServidor(RapServidores servidor) throws ApplicationBusinessException {
		if (!getAelPatologistaDAO().isPatologistaAtivoPorServidor(servidor)) {
			//--Servidor deve estar cadastrado como Patologista!
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02660);
		}
	}
	
	/**
	 * ORADB Function AELK_LO8_RN.RN_LO8P_VER_SERVIDOR
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException 
	 */
	public void rnLo8pVerServidor(RapServidores servidor) throws ApplicationBusinessException {
		if (!getAelPatologistaDAO().isPatologistaAtivoPorServidor(servidor)) {
			//--Este servidor não está cadastrado como Patologista!
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02772);
		}
	}

	/**
	 * ORADB Function AELK_LUP_RN.RN_LUPP_VER_SERVIDOR
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException 
	 */
	public void rnLuppVerServidor(RapServidores servidor) throws ApplicationBusinessException {
		if (!getAelPatologistaDAO().isPatologistaAtivoPorServidor(servidor)) {
			//--Este servidor não está cadastrado como Patologista!
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02649);
		}
	}
	/**
	 * ORADB Function AELK_LUZ_RN.RN_LUZP_VER_ETAP_LA2
	 * 
	 * @param seq
	 * @throws ApplicationBusinessException 
	 */
	public void rnLuzpVerEtapLa2(Long luxSeq) throws ApplicationBusinessException {
		List<AelExameAp> listaExameAp = getAelExameApDAO().listarExameApPorSeqESituacao(luxSeq, DominioSituacaoExamePatologia.DC, DominioSituacaoExamePatologia.LA);
		if (!listaExameAp.isEmpty()) {
			throw new ApplicationBusinessException(LaudoUnicoRNExceptionCode.AEL_02693);
			//Não é permitido nenhum tipo de alteração! Laudo está Assinado ou Diagnóstico Concluído('MC','DC','LA').
		}
		
	}

	protected AelExameApDAO getAelExameApDAO() {
		return aelExameApDAO;
	}
	
	protected AelPatologistaDAO getAelPatologistaDAO() {
		return aelPatologistaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
