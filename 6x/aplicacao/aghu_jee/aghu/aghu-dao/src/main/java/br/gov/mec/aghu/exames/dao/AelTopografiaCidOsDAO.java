package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelTopografiaCidOs;

public class AelTopografiaCidOsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTopografiaCidOs> {

	private static final long serialVersionUID = 3846161295734091849L;

	/**
	 * Método listarTopografiaCidOs, lista tabela Ael_topografia_CidOs
	 * 
	 * @return
	 */
	public List<AelTopografiaCidOs> listarTopografiaCidOs() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaCidOs.class, "CIDO");
		criteria.addOrder(Order.desc("CIDO." + AelTopografiaCidOs.Fields.CODIGO.toString()));
		return executeCriteria(criteria);
	}

	public List<AelTopografiaCidOs> listarTopografiaCidOsPorGrupo(Long seqGrupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaCidOs.class, "CIDO");
		
		criteria.add(Restrictions.eq(AelTopografiaCidOs.Fields.GRUPO_SEQ.toString(), seqGrupo));
		criteria.addOrder(Order.desc("CIDO." + AelTopografiaCidOs.Fields.CODIGO.toString()));
		
		return executeCriteria(criteria);
	}
	/**
	 * A pesquisa uma topografia(CID-O) cadastrada na tabela
	 * Ael_topografia_CidOs. A pesquisa pode ser feita pelo código, ou por
	 * palavras que compôe a descrição.
	 * 
	 * @param pesquisa
	 * @return
	 */
	public List<AelTopografiaCidOs> listarTopografiaCidOs(Object pesquisa){
		List<AelTopografiaCidOs> retorno = null;
		String codDesc = (String) pesquisa;
		retorno = executeCriteria(criarCriteriaPesquisarCidsPorCodigo(codDesc), 0, 100, null, false);
		if (retorno == null || retorno.isEmpty()) {
			retorno = executeCriteria(criarCriteriaPesquisarCidsPorDescricao(codDesc), 0, 100, null, false);
		}
		return retorno;
	
	}
	
	/**
	 * Método que retorna o total de registros encontrados pela Criteria 
	 * @param pesquisa
	 * @return int
	 * @author ndeitch
	 */
	public Long listarTopografiaCidOsCount(Object pesquisa){
		Long retorno = null;
		String codDesc = (String) pesquisa;
		retorno = executeCriteriaCount(criarCriteriaPesquisarCidsPorCodigo(codDesc));
		if (retorno == null || retorno == 0L) {
			retorno = executeCriteriaCount(criarCriteriaPesquisarCidsPorDescricao(codDesc));
		}
		return retorno;
	}
	
	private DetachedCriteria criarCriteriaPesquisarCidsPorCodigo(final String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaCidOs.class);
		criteria.add(Restrictions.eq(AelTopografiaCidOs.Fields.CODIGO.toString(), descricao.toUpperCase()));
		return criteria;
	}

	private DetachedCriteria criarCriteriaPesquisarCidsPorDescricao(final String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaCidOs.class);
		criteria.add(Restrictions.ilike(AelTopografiaCidOs.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		return criteria;
	}
	
}
