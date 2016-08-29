package br.gov.mec.aghu.compras.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/** Builder responsável pela consulta de SC's por código ou descrição. */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ScPorCodigoDescricaoQueryBuilder extends QueryBuilder<DetachedCriteria> {
	/** Filtro ID/Descrição */
	private Object filter;
	
	/** Material */
	private ScoMaterial material;

	/** Natureza de Despesa */
	private FsoNaturezaDespesa naturezaDespesa;

	/** {@inheritDoc} */
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
	}

	/** {@inheritDoc} */
	@Override
	protected void doBuild(DetachedCriteria criteria) {
		criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), "PPS", JoinType.INNER_JOIN);
				
		String filterStr = (String) filter;
		if (StringUtils.isNotBlank(filterStr)) {
			Criterion restriction = Restrictions.ilike(
					"SLC."+ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString(), filterStr,
					MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroInteger(filter)) {
				restriction = Restrictions.or(restriction, Restrictions.eq(
						"SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(),
						Integer.valueOf(filterStr)));
			}
			criteria.add(restriction);
		}

		DetachedCriteria subQueryFasesSolicitacoes = DetachedCriteria.forClass(ScoFaseSolicitacao.class , "FS");
		ProjectionList projectionListSubQueryFasesSolicitacoes = Projections.projectionList()
		.add(Projections.property("FS."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
		subQueryFasesSolicitacoes.setProjection(projectionListSubQueryFasesSolicitacoes);		
		subQueryFasesSolicitacoes.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));				
		subQueryFasesSolicitacoes.add(Restrictions.isNotNull("FS."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
		criteria.add(Subqueries.propertyNotIn("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQueryFasesSolicitacoes));
		criteria.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.isNotNull("SLC."+ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString()));		
		criteria.add(Restrictions.eq("PPS."+ScoPontoParadaSolicitacao.Fields.TIPO.toString(), DominioTipoPontoParada.CP));
		
		if (material != null) {
			criteria.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), material));
		}
		
		if (naturezaDespesa != null) {
			criteria.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.NATUREZA_DESPESA.toString(), naturezaDespesa));
		}
	}
	
	/** Constrói query a partir dos parâmetros. */
	public DetachedCriteria build(Object filter, ScoMaterial material, FsoNaturezaDespesa naturezaDespesa) {
		this.filter = filter;
		this.material = material;
		this.naturezaDespesa = naturezaDespesa;
		return super.build();
	}
}