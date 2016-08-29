package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.vo.FatCnesVO;
import br.gov.mec.aghu.model.FatServClassificacoes;
import br.gov.mec.aghu.model.FatServicos;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class FatServClassificacoesDAO extends BaseDao<FatServClassificacoes> {

	private static final long serialVersionUID = 3064790328857477375L;

	public List<FatServClassificacoes> buscarFatServClassificacoesAtivos() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatServClassificacoes.class);
		
		criteria.add(Restrictions.eq(FatServClassificacoes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}

	// #41079 - C3

	public List<FatCnesVO> pesquisarFatCnesPorSeqDescricao(Object param) {
		final DetachedCriteria criteria = criarCriteriaFatCnesPorSeqDescricao(param);
		
		criteria.addOrder(Order.asc("L_FSE." + FatServicos.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("L_FCS." + FatServClassificacoes.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("L_FCS." + FatServClassificacoes.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	private DetachedCriteria criarCriteriaFatCnesPorSeqDescricao(Object param) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatServClassificacoes.class, "L_FCS");
		criteria.createAlias("L_FCS." + FatServClassificacoes.Fields.FSE.toString(), "L_FSE");
		
		String strPesquisa = (String) param;
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion c1 = Restrictions.ilike("L_FSE." + FatServicos.Fields.CODIGO.toString(), strPesquisa, MatchMode.ANYWHERE);
			Criterion c2 = Restrictions.ilike("L_FCS." + FatServClassificacoes.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(c1, c2));
		}
		ProjectionList projection =	Projections.projectionList()
				.add(Projections.property("L_FSE." + FatServicos.Fields.CODIGO.toString()), FatCnesVO.Fields.SERVICO.toString())
				.add(Projections.property("L_FCS." + FatServClassificacoes.Fields.CODIGO.toString()), FatCnesVO.Fields.CLASSIFICACAO.toString())
				.add(Projections.property("L_FCS." + FatServClassificacoes.Fields.DESCRICAO.toString()), FatCnesVO.Fields.DESCRICAO.toString())
				.add(Projections.property("L_FCS." + FatServClassificacoes.Fields.FSE_SEQ.toString()), FatCnesVO.Fields.COD_SERVICO.toString())
				.add(Projections.property("L_FCS." + FatServClassificacoes.Fields.SEQ.toString()), FatCnesVO.Fields.CODIGO_CLASSIFICACAO.toString())
				;
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(FatCnesVO.class));
		return criteria;
	}

	public Long pesquisarFatCnesPorSeqDescricaoCount(String param) {
		final DetachedCriteria criteria = criarCriteriaFatCnesPorSeqDescricao(param);
		return executeCriteriaCount(criteria);
	}
}