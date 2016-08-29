package br.gov.mec.aghu.compras.dao;

import org.hibernate.Query;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ExclusaoParcelasNaoAssinadasItemAFQueryBuilder extends QueryBuilder<Query> {
	
	private Integer iafAfnNumero;
	private Integer iafNumero;
	private Boolean isIndAssinatura;
	private Integer qtdeEntregue;
	
	
	private String makeQuery() {
		
		StringBuilder hql = new StringBuilder(150);

		hql.append(" DELETE FROM ").append(ScoProgEntregaItemAutorizacaoFornecimento.class.getSimpleName())
		.append(" WHERE 1 = 1 ")
		.append(" AND ").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.name()).append(" = ").append(":P_IAF_AFN_NUMERO")
		.append(" AND ").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.name()).append(" = ").append(":P_IAF_NUMERO")
		.append(" AND ").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.name()).append(" != ").append(":P_IND_ASSINATURA")
		.append(" AND ").append("( ").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.name()).append(" = ").append(":P_QTDE_ENTREGUE")
		.append(" OR ").append(ScoProgEntregaItemAutorizacaoFornecimento.Fields.QTDE_ENTREGUE.name()).append(" IS NULL ").append(" )");
		
		return hql.toString();
		
	}

	@Override
	protected Query createProduct() {
		final String hql = this.makeQuery();
		Query query = this.createHibernateQuery(hql);
		return query;
	}

	@Override
	protected void doBuild(Query query) {
		query.setParameter("P_IAF_AFN_NUMERO", iafAfnNumero);
		query.setParameter("P_IAF_NUMERO", iafNumero);
		query.setParameter("P_IND_ASSINATURA", Boolean.TRUE.equals(isIndAssinatura) ? DominioSimNao.S.toString() : DominioSimNao.N.toString());
		query.setParameter("P_QTDE_ENTREGUE", qtdeEntregue);		
	}
	
	public Query build(final Integer iafAfnNumero, final Integer iafNumero, final Boolean isIndAssinatura, final Integer qtdeEntregue) {
		this.iafAfnNumero = iafAfnNumero;
		this.iafNumero = iafNumero;
		this.isIndAssinatura = isIndAssinatura;
		this.qtdeEntregue = qtdeEntregue;
		return super.build();
	}

}
