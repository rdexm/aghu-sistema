package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioLimiteValorPatrimonio;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Finder de Parâmetros Orçamentários de SC
 * 
 * @author mlcruz
 */
@Stateless
public class FsoParametrosOrcamentoScFinderON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(FsoParametrosOrcamentoScFinderON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}
	
	private static final long serialVersionUID = 5783502927720041550L;

	/**
	 * Obtem parâmetros orçamentários.
	 * @param strategy 
	 */
	public <T> T getResult(FsoParametrosOrcamentoCriteriaVO criteria, 
			FsoParametrosOrcamentoFilterStrategy<T> strategy) {
		try {
			FsoParametrosOrcamentoCriteriaVO criteria1 = criteria.clone();
			criteria1.setNivel(FsoParametrosOrcamentoCriteriaVO.Nivel.MATERIAL);
			logInfo("Buscando parâmetros SC por material");
			T result = find(criteria1, strategy);
			
			if (result != null) {
				return result;
			}
		} catch (CloneNotSupportedException e) {
			logError(e.getMessage());
		}

		try {
			FsoParametrosOrcamentoCriteriaVO criteria2 = criteria.clone();
			criteria2.setNivel(FsoParametrosOrcamentoCriteriaVO.Nivel.GRUPO_MATERIAL);
			logInfo("Buscando parâmetros SC por grupo de material");
			T result = find(criteria2, strategy);
			
			if (result != null) {
				return result;
			}
		} catch (CloneNotSupportedException e) {
			logError(e.getMessage());
		}

		try {
			FsoParametrosOrcamentoCriteriaVO criteria3 = criteria.clone();
			criteria3.setNivel(FsoParametrosOrcamentoCriteriaVO.Nivel.INDICADOR);
			logInfo("Buscando parâmetros SC por indicador");
			T result = find(criteria3, strategy);
			
			if (result != null) {
				return result;
			}
		} catch (CloneNotSupportedException e) {
			logError(e.getMessage());
		}
		
		try {
			FsoParametrosOrcamentoCriteriaVO criteria4 = criteria.clone();
			criteria4.setNivel(FsoParametrosOrcamentoCriteriaVO.Nivel.GERAL);
			criteria4.setLimite(null);
			criteria4.setValor(null);
			criteria4.setCentroCusto(null);
			logInfo("Buscando parâmetros SC geral");
			T result = strategy.find(criteria4);
			
			if (result != null) {
				return result;
			} else {
				try {
					FsoParametrosOrcamentoCriteriaVO criteria5 = criteria4.clone();
					criteria5.setCentroCusto(criteria.getCentroCusto());
					logInfo("Buscando parâmetros SC geral por centro de custo");
					T result2 = strategy.find(criteria5);
					
					if (result2 != null) {
						return result2;
					}
				} catch (CloneNotSupportedException e) {
					logError(e.getMessage());
				}
			}
		} catch (CloneNotSupportedException e) {
			logError(e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Obtem parâmetros de orçamento conforme critério e estratégia.
	 * @param strategy 
	 */
	private <T> T find(FsoParametrosOrcamentoCriteriaVO criteria, 
			FsoParametrosOrcamentoFilterStrategy<T> strategy) {		
		// Passo 1/2
		try {
			FsoParametrosOrcamentoCriteriaVO criteria1 = criteria.clone();
			criteria1.setLimite(null);
			criteria1.setValor(null);
			criteria1.setCentroCusto(null);
			logInfo("Buscando parâmetros SC (passo 1/2)");
			T result = strategy.find(criteria1);
			
			if (result != null) {
				return result;
			}
		} catch (CloneNotSupportedException e) {
			logError(e.getMessage());
		}
		
		// Passo 3/4
		try {
			FsoParametrosOrcamentoCriteriaVO criteria2 = criteria.clone();
			criteria2.setLimite(DominioLimiteValorPatrimonio.A);
			criteria2.setCentroCusto(null);
			logInfo("Buscando parâmetros SC (passo 3/4)");
			T result = strategy.find(criteria2);
			
			if (result != null) {
				return result;
			} else {				
				// Passo 5
				try {
					FsoParametrosOrcamentoCriteriaVO criteria3 = criteria2.clone();
					criteria3.setLimite(DominioLimiteValorPatrimonio.M);
					logInfo("Buscando parâmetros SC (passo 5)");
					T result2 = strategy.find(criteria3);
					
					if (result2 != null) {
						return result2;
					}
				} catch (CloneNotSupportedException e) {
					logError(e.getMessage());
				}
			}
		} catch (CloneNotSupportedException e) {
			logError(e.getMessage());
		}
		
		// Passo 6
		try {
			FsoParametrosOrcamentoCriteriaVO criteria4 = criteria.clone();
			criteria4.setLimite(DominioLimiteValorPatrimonio.A);
			logInfo("Buscando parâmetros SC (passo 6)");
			T result = strategy.find(criteria4);
			
			if (result != null) {
				return result;
			} else {				
				// Passo 7
				try {
					FsoParametrosOrcamentoCriteriaVO criteria5 = criteria4.clone();
					criteria5.setLimite(DominioLimiteValorPatrimonio.M);
					logInfo("Buscando parâmetros SC (passo 7)");
					T result2 = strategy.find(criteria5);
					
					if (result2 != null) {
						return result2;
					}
				} catch (CloneNotSupportedException e) {
					logError(e.getMessage());
				}
			}
		} catch (CloneNotSupportedException e) {
			logError(e.getMessage());
		}
		
		// Passo 8
		try {
			FsoParametrosOrcamentoCriteriaVO criteria6 = criteria.clone();
			criteria6.setLimite(null);
			criteria6.setValor(null);
			logInfo("Buscando parâmetros SC (passo 8)");
			T result = strategy.find(criteria6);
			
			if (result != null) {
				return result;
			}
		} catch (CloneNotSupportedException e) {
			logError(e.getMessage());
		}
		
		return null;
	}
}