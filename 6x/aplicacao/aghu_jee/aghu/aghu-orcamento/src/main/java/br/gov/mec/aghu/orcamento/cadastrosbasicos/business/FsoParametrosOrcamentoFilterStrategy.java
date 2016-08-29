package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;

/**
 * Interface para estratégia de consulta a parâmetros orçamentários.
 */
interface FsoParametrosOrcamentoFilterStrategy<T> {
	/**
	 * Procura parâmetros.
	 *
	 * @param criteria Critério.
	 * @return Indicador.
	 */
	T find(FsoParametrosOrcamentoCriteriaVO criteria);
}