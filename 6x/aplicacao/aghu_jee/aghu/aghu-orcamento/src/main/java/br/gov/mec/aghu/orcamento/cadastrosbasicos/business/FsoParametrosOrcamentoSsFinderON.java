package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Finder de Parâmetros Orçamentários de SS
 * 
 * @author mlcruz
 */
@Stateless
public class FsoParametrosOrcamentoSsFinderON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(FsoParametrosOrcamentoSsFinderON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}
	
	private static final long serialVersionUID = 6279324973958115352L;

	/**
	 * Obtem parâemtros orçamentários.
	 * 
	 * @param criteria 
	 * @param strategy 
	 */
	public <T> T getResult(FsoParametrosOrcamentoCriteriaVO criteria, 
			FsoParametrosOrcamentoFilterStrategy<T> strategy) {
		try {
			FsoParametrosOrcamentoCriteriaVO criteria1 = criteria.clone();
			criteria1.setNivel(FsoParametrosOrcamentoCriteriaVO.Nivel.SERVICO);
			logInfo("Buscando parâmetros SS por serviço");
			T result = strategy.find(criteria1);
			
			if (result != null) {
				return result;
			}
		} catch (CloneNotSupportedException e) {
			logError(e.getMessage());
		}

		try {
			FsoParametrosOrcamentoCriteriaVO criteria2 = criteria.clone();
			criteria2.setNivel(FsoParametrosOrcamentoCriteriaVO.Nivel.GRUPO_SERVICO);
			logInfo("Buscando parâmetros SS por grupo de serviço");
			T result = strategy.find(criteria2);
			
			if (result != null) {
				return result;
			}
		} catch (CloneNotSupportedException e) {
			logError(e.getMessage());
		}

		try {
			FsoParametrosOrcamentoCriteriaVO criteria3 = criteria.clone();
			criteria3.setNivel(FsoParametrosOrcamentoCriteriaVO.Nivel.GERAL);
			logInfo("Buscando parâmetros SS geral");
			T result = strategy.find(criteria3);
			
			if (result != null) {
				return result;
			}
		} catch (CloneNotSupportedException e) {
			logError(e.getMessage());
		}
		
		return null;
	}
}
