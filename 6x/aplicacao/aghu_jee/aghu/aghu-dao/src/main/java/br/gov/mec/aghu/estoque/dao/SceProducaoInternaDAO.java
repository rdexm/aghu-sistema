package br.gov.mec.aghu.estoque.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceProducaoInterna;

/**
 * 
 * @author aghu
 *
 */
public class SceProducaoInternaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceProducaoInterna> {

	private static final long serialVersionUID = 8707733061516381423L;

	/**
	 * Retorna a quantidade de Produção Internas dependentes do pacote de materiais
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @return
	 */
	public Long obterQuantidadeProducaoInternaPorCentroCustoNumeroPacoteMaterial(Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer numeroPacote){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceProducaoInterna.class, "PI");
		criteria.add(Restrictions.eq("PI." + SceProducaoInterna.Fields.CENTRO_CUSTO_PROPRIETARIO.toString(), codigoCentroCustoProprietario));
		criteria.add(Restrictions.eq("PI." + SceProducaoInterna.Fields.CENTRO_CUSTO_APLICACAO.toString(), codigoCentroCustoAplicacao));
		criteria.add(Restrictions.eq("PI." + SceProducaoInterna.Fields.NUMERO_PACOTE_MATERIAL.toString(), numeroPacote));		
		return executeCriteriaCount(criteria);		
	}
	
	

}
