package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.exames.dao.AelLaminaApsDAO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaLaminasVO;
import br.gov.mec.aghu.model.AelCestoPatologia;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelLaminaAps;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelLaminaApsON extends BaseBusiness {

	
	@EJB
	private AelLaminaApsRN aelLaminaApsRN;
	
	@Inject
	private AelExameApON aelExameApON;
	
	private static final Log LOG = LogFactory.getLog(AelLaminaApsON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@Inject
	private AelLaminaApsDAO aelLaminaApsDAO;
	
	@Inject
	private AelExameApDAO aelExameApDAO;

	private static final long serialVersionUID = -5984139103739553948L;
	
	public enum AelLaminaApsONExceptionCode implements BusinessExceptionCode {
		ERRO_IMPOSSIVEL_EXCLUIR_TODOS_BLOCOS;
	}
	
	public List<RelatorioMapaLaminasVO> pesquisarRelatorioMapaLaminasVO(final Date dtRelatorio, final AelCestoPatologia cesto){
		return getAelLaminaApsDAO().pesquisarRelatorioMapaLaminasVO(dtRelatorio, cesto);
	} 

	public void persistirAelLaminaAps(final AelLaminaAps aelLaminaAps) throws BaseException {
				
		if (aelLaminaAps.getSeq() == null) {
			this.getAelLaminaApsRN().inserirAelLaminaAps(aelLaminaAps);
		} 
		else {
			this.getAelLaminaApsRN().atulizarAelLaminaAps(aelLaminaAps, this.getAelLaminaApsDAO().obterPorChavePrimaria(aelLaminaAps.getSeq()));
		}
	}
	
	
	public void gravarLaminas(List<AelLaminaAps> laminasEmMemoria, List<AelLaminaAps> laminasExcluir, AelExameAp exame) throws BaseException {
		if(laminasExcluir != null && !laminasExcluir.isEmpty()){
			for(AelLaminaAps lamina : laminasExcluir) {
				excluirAelLaminaAps(lamina);
			}
		}
		
		List<AelLaminaAps> laminasGravadas = getAelLaminaApsDAO().obterListaLaminasPeloExameApSeq(exame.getSeq());
		
		if(laminasEmMemoria != null && !laminasEmMemoria.isEmpty()){
			avaliaLaminas(laminasEmMemoria, exame);
		} else {
			if (laminasGravadas != null && laminasGravadas.isEmpty()){
				avaliaLaminas(laminasEmMemoria, exame);
				laminasEmMemoria = laminasGravadas;
			} else {
				throw new BaseException(AelLaminaApsONExceptionCode.ERRO_IMPOSSIVEL_EXCLUIR_TODOS_BLOCOS);
			}
		}		
	}

	private void avaliaLaminas(List<AelLaminaAps> laminasEmMemoria,	AelExameAp exame)	throws BaseException {
		avaliaIndiceBloco(laminasEmMemoria, exame);
	}

	private void avaliaIndiceBloco(List<AelLaminaAps> laminasEmMemoria, AelExameAp exame) throws BaseException {
		AelExameAp exameNew = aelExameApDAO.obterPorChavePrimaria(exame.getSeq());
		AelExameAp exameOld = clonarAelExameAp(exameNew);
		if(Boolean.TRUE.equals(exameNew.getIndIndiceBloco())){
			persisteLaminas(laminasEmMemoria);
		}else{
			persisteLaminas(laminasEmMemoria);
			exameNew.setIndIndiceBloco(true);
		}
		getAelExameApON().persistirAelExameAp(exameNew, exameOld);
	}

	private AelExameAp clonarAelExameAp(AelExameAp exame) {
		AelExameAp copia = new AelExameAp();
		copia.setAelAnatomoPatologicoOrigem(exame.getAelAnatomoPatologicoOrigem());
		copia.setAelAnatomoPatologicos(exame.getAelAnatomoPatologicos());
		copia.setAelExameApItemSolic(exame.getAelExameApItemSolic());
		copia.setAelExtratoExameApses(exame.getAelExtratoExameApses());
		copia.setAelInformacaoClinicaAP(exame.getAelInformacaoClinicaAP());
		copia.setConfigExLaudoUnico(exame.getConfigExLaudoUnico());
		copia.setDthrImpressao(exame.getDthrImpressao());
		copia.setEtapasLaudo(exame.getEtapasLaudo());
		copia.setIndImpresso(exame.getIndImpresso());
		copia.setIndIndiceBloco(exame.getIndIndiceBloco());
		copia.setIndLaudoVisual(exame.getIndLaudoVisual());
		copia.setIndRevisaoLaudo(exame.getIndRevisaoLaudo());
		copia.setMateriais(exame.getMateriais());
		copia.setNroApOrigem(exame.getNroApOrigem());
		copia.setObservacaoTecnica(exame.getObservacaoTecnica());
		copia.setObservacoes(exame.getObservacoes());
		copia.setSeq(exame.getSeq());
		copia.setServidor(exame.getServidor());
		copia.setServidorRespImpresso(exame.getServidorRespImpresso());
		copia.setServidorRespLaudo(exame.getServidorRespLaudo());
		copia.setSituacao(exame.getSituacao());
		copia.setVersion(exame.getVersion());
		return copia;
	}

	private void persisteLaminas(List<AelLaminaAps> laminasEmMemoria) throws BaseException {
		for (AelLaminaAps lamina : laminasEmMemoria) {
			persistirAelLaminaAps(lamina);
		}
	}


	public void excluirAelLaminaAps(final AelLaminaAps aelLaminaAps) throws BaseException {
		this.getAelLaminaApsRN().excluirAelLaminaAps(aelLaminaAps);
	}
	
	protected AelLaminaApsRN getAelLaminaApsRN() {
		return aelLaminaApsRN;
	}
	
	protected AelExameApON getAelExameApON() {
		return aelExameApON;
	}

	protected AelLaminaApsDAO getAelLaminaApsDAO() {
		return aelLaminaApsDAO;
	}
}