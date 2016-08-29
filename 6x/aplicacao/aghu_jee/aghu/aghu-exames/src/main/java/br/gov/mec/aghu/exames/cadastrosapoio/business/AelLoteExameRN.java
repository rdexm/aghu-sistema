package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelLoteExameDAO;
import br.gov.mec.aghu.model.AelLoteExame;
import br.gov.mec.aghu.model.AelLoteExameId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelLoteExameRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(AelLoteExameRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelLoteExameDAO aelLoteExameDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5648788938203942823L;

	public enum AelLoteExameRNExceptionCode implements BusinessExceptionCode {
		AEL_00436, AEL_01208,AEL_LOTE_EXAME_PK;
	}

	/***
	 * Inserir lote de exame
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	public void inserirAelLoteExame(AelLoteExame loteExame) throws ApplicationBusinessException {
		
		// TODO
		if(loteExame == null){
			throw new IllegalArgumentException("Argumentos obrigatórios.");
		}
		
		// TRIGGER AELT_LOE_BRI	
		this.beforeInsertAelLoteExame(loteExame);
		// Insere o lote de exame
		this.getAelLoteExameDAO().persistir(loteExame);
		this.getAelLoteExameDAO().flush();
			
	}
	
	/**
	 * ORADB TRIGGER AELT_LOE_BRI
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	public void beforeInsertAelLoteExame(AelLoteExame loteExame) throws ApplicationBusinessException {
		// RN1: aelk_loe_rn.rn_loep_ver_lote_usu
		this.validarExamesUsualAtivo(loteExame);
		// RN2: aelk_loe_rn.rn_loep_ver_dependen
		this.validarExamesDependentes(loteExame);
		//verificando constrant de pk
		this.validarExamesIguais(loteExame);
	}
	
	
	private void validarExamesIguais(AelLoteExame loteExame) throws ApplicationBusinessException {
		List<AelLoteExame> lista = getAelLoteExameDAO().pesquisaLotesExamesPorLoteExameUsual(loteExame.getId().getLeuSeq());
		
		for(AelLoteExame lote : lista){
			if(lote.getId().getEmaExaSigla().equals(loteExame.getId().getEmaExaSigla()) && 
					lote.getId().getEmaManSeq().equals(loteExame.getId().getEmaManSeq()) && 
					lote.getId().getLeuSeq().equals(loteExame.getId().getLeuSeq())){
				throw new ApplicationBusinessException(AelLoteExameRNExceptionCode.AEL_LOTE_EXAME_PK);
			}
		}
		
	}

	/**
	 * ORADB aelk_loe_rn.rn_loep_ver_lote_us
	 * Verifica se o lote usual esta ativo
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	public void validarExamesUsualAtivo(AelLoteExame loteExame) throws ApplicationBusinessException {
		if(DominioSituacao.I.equals(loteExame.getAelLoteExamesUsuais().getIndSituacao())){
			throw new ApplicationBusinessException(AelLoteExameRNExceptionCode.AEL_00436);
		}
	}

	/**
	 * ORADB aelk_loe_rn.rn_loep_ver_dependen
	 * Verifica a existencia de exames dependentes (este tipo de exame nao sera permitido).
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	public void validarExamesDependentes(AelLoteExame loteExame) throws ApplicationBusinessException {
		if(loteExame.getExamesMaterialAnalise().getIndDependente()){
			throw new ApplicationBusinessException(AelLoteExameRNExceptionCode.AEL_01208);
		}
	}
	
	/***
	 * remover lote de exame
	 * @param loteExame
	 */
	public void removerAelLoteExame(AelLoteExameId loteExameId) throws ApplicationBusinessException {
		getAelLoteExameDAO().removerPorId(loteExameId);
	}
	
	/**
	 * Dependencias
	 */
	protected AelLoteExameDAO getAelLoteExameDAO(){
		return aelLoteExameDAO;
	}
}