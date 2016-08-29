package br.gov.mec.aghu.compras.dao;


import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class QuantidadeOutrasAFsProgramadasQueryBuilder extends QueryBuilder<DetachedCriteria>  {

	private Integer codigoMaterial;
	private Integer afnNumero;
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.ne("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.in("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), 
				new DominioSituacaoAutorizacaoFornecedor[]{DominioSituacaoAutorizacaoFornecedor.AE, DominioSituacaoAutorizacaoFornecedor.PA}));
		
		StringBuilder sqlRestriction = new StringBuilder(300);
		sqlRestriction.append(" {alias}.AFN_NUMERO || '' || {alias}.NUMERO in (select IAF_AFN_NUMERO ||''|| IAF_NUMERO ");
		sqlRestriction.append(" FROM  ").append(" AGH.SCO_PROGR_ENTREGA_ITENS_AF ");
		sqlRestriction.append(" WHERE IAF_AFN_NUMERO  = {alias}.AFN_NUMERO ");
        sqlRestriction.append(" AND IAF_NUMERO = {alias}.NUMERO ");
		sqlRestriction.append(" AND IND_CANCELADA ||'' = 'N' ");
		sqlRestriction.append(" AND COALESCE (QTDE_ENTREGUE,0) < QTDE) ");
		
		criteria.add(Restrictions.sqlRestriction(sqlRestriction.toString()));
		
		ProjectionList projectionList = Projections.projectionList();
		StringBuilder sql = new StringBuilder(150);
		sql.append(" coalesce(sum({alias}.QTDE_SOLICITADA - coalesce({alias}.QTDE_RECEBIDA,0)), 0) quantidadeOutrasAFsProgramadas ");
		
		projectionList.add(Projections.sqlProjection(sql.toString(), new String[]{"quantidadeOutrasAFsProgramadas"}, new Type[]{IntegerType.INSTANCE}));
		criteria.setProjection(projectionList);
	}

	
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

}
