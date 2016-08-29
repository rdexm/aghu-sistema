package br.gov.mec.aghu.compras.dao;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.EntregaProgramadaGrupoMaterialVO;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class EntregaGlobalGrupoMaterialQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private Integer gmtCodigo;
	private Boolean materialEstocavel;
	
	private Date dataEntregaInicial;
	private Date dataEntregaInicialParametro;
	private Date dataEntregaFinal;
	private Date dataEntregaFinalParametro;
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoGrupoMaterial.class, "GMT");
		return criteria;
	}

	private DetachedCriteria createSubQuery() {
		DetachedCriteria subquery = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		subquery.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), "PEA", JoinType.INNER_JOIN);
		subquery.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		subquery.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);
		subquery.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		subquery.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		subquery.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT", JoinType.INNER_JOIN);
		
		Date dataInicial = dataEntregaInicialParametro;
		Date dataFinal = dataEntregaFinalParametro;
		if (dataEntregaInicial != null) {
			dataInicial = dataEntregaInicial;
		}
		if (dataEntregaFinal != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(dataEntregaFinal);
			c.add(Calendar.DATE, 1);
			dataFinal = c.getTime(); 
		}
		subquery.add(Restrictions.between("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dataInicial, dataFinal));	
		subquery.add(Restrictions.gt("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), 0));
		
		if (materialEstocavel != null) {
			subquery.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), materialEstocavel.booleanValue()));
		}
		
		
		subquery.setProjection(Projections.property("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()));
		
		return subquery;
		
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		if (gmtCodigo != null) {
			criteria.add(Restrictions.eq("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString(), gmtCodigo));
		}
		criteria.add(Subqueries.propertyIn("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString(), createSubQuery()));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString()), EntregaProgramadaGrupoMaterialVO.Fields.GMT_CODIGO.toString())
				.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.DESCRICAO.toString()), EntregaProgramadaGrupoMaterialVO.Fields.GMT_DESCRICAO.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(EntregaProgramadaGrupoMaterialVO.class));
		
		criteria.addOrder(Order.asc("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString()));
	}

	public DetachedCriteria build(Integer gmtCodigo, Boolean materialEstocavel,
			Date dataEntregaInicial, Date dataEntregaInicialParametro, 
			Date dataEntregaFinal, Date dataEntregaFinalParametro) {
		
		this.gmtCodigo = gmtCodigo;
		this.materialEstocavel = materialEstocavel;
		this.dataEntregaInicial = dataEntregaInicial;
		this.dataEntregaInicialParametro = dataEntregaInicialParametro;
		this.dataEntregaFinal = dataEntregaFinal;
		this.dataEntregaFinalParametro = dataEntregaFinalParametro;
		
		return super.build();
	}

}
