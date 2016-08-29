package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigEscalaPessoa;

public class SigEscalaPessoaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigEscalaPessoa> {

	private static final long serialVersionUID = 3091099970884870119L;

	/*
	 * Busca centro de custos, utilizado na SB da tela de pesquisa e de casdastro de escalas assistenciais
	 */
	public List<SigEscalaPessoa> pesquisarEscalaPessoas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FccCentroCustos centroCustos) {
		return this.executeCriteria(this.pesquisaEscalaPessoasCriteria(centroCustos, true));
	}

	public Long pesquisarEscalaPessoasCount(FccCentroCustos centroCustos) {
		return executeCriteriaCount(pesquisaEscalaPessoasCriteria(centroCustos, false));
	}

	public List<SigEscalaPessoa> pesquisarEscalaPessoasPorCentroDeCusto(FccCentroCustos centroCustos) {
		return executeCriteria(this.pesquisaEscalaPessoasCriteria(centroCustos, true));
	}

	private DetachedCriteria pesquisaEscalaPessoasCriteria(FccCentroCustos centroCustos, boolean addOrderBy) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigEscalaPessoa.class, "escala");
		criteria.createCriteria("escala." + SigEscalaPessoa.Fields.CENTRO_CUSTO.toString(), "centroCusto", Criteria.INNER_JOIN);
		if (centroCustos != null) {
			criteria.add(Restrictions.eq("escala."+SigEscalaPessoa.Fields.CENTRO_CUSTO.toString(), centroCustos));
		}
		if(addOrderBy){
			criteria.addOrder(Order.asc("centroCusto." + FccCentroCustos.Fields.DESCRICAO.toString()));
		}
		return criteria;
	}

	public SigEscalaPessoa verificaExistenciaDaAtividadeNoCentroDeCusto(SigEscalaPessoa sigEscalaPessoas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigEscalaPessoa.class);
		criteria.add(Restrictions.eq(SigEscalaPessoa.Fields.CENTRO_CUSTO.toString(), sigEscalaPessoas.getFccCentroCustos()));
		criteria.add(Restrictions.eq(SigEscalaPessoa.Fields.TIPO_ATIVIDADE.toString(), sigEscalaPessoas.getTipoAtividade()));
		return (SigEscalaPessoa) executeCriteriaUniqueResult(criteria);
	}

}
