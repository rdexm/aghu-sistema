package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoRamoComercialDAO;
import br.gov.mec.aghu.compras.vo.ScoRamoComercialCriteriaVO;
import br.gov.mec.aghu.model.ScoRamoComercial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe ON para Ramos Comerciais
 * 
 * @author mlcruz
 */
@Stateless
public class ScoRamoComercialON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoRamoComercialON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoRamoComercialDAO scoRamoComercialDAO;
	/**/
	private static final long serialVersionUID = -2841526341800361539L;

	public ScoRamoComercial obterScoRamoComercial(Short codigo) {
		return getScoRamoComercialDAO().obterPorChavePrimaria(codigo);
	}
	
	public List<ScoRamoComercial> pesquisarScoRamosComerciais(
			ScoRamoComercialCriteriaVO criteria, 
			int firstResult, int maxResults, 
			String orderProperty, boolean asc) {
		return getScoRamoComercialDAO().pesquisarScoRamosComerciais(
				criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long contarScoRamosComerciais(
			ScoRamoComercialCriteriaVO criteria) {
		return getScoRamoComercialDAO().contarScoRamosComerciais(criteria);
	}

	/**
	 * Altera ramo comercial.
	 */
	public void persistir(ScoRamoComercial ramo) throws ApplicationBusinessException {
		validar(ramo);
		
		if (ramo.getCodigo() != null) {
			getScoRamoComercialDAO().merge(ramo);
		} else {
			getScoRamoComercialDAO().persistir(ramo);
		}
	}
	
	private void validar(ScoRamoComercial ramo) throws ApplicationBusinessException {
		if (getScoRamoComercialDAO().existeScoRamoComercial(ramo)) {
			throw new ApplicationBusinessException(
					ManterRamoComercialONExceptionCode.MENSAGEM_DESCRICAO_RAMO_COMERCIAL_DUPLICADA, 
					ramo.getDescricao());
		}
	}
	
	private ScoRamoComercialDAO getScoRamoComercialDAO() {
		return scoRamoComercialDAO;
	}
	
	public enum ManterRamoComercialONExceptionCode
		implements BusinessExceptionCode {
		MENSAGEM_DESCRICAO_RAMO_COMERCIAL_DUPLICADA
	}
}
