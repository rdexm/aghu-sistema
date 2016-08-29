package br.gov.mec.aghu.faturamento.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class AghCidObterDescricaoPorCodigoQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = -4000637213078364459L;

	private DetachedCriteria criteria;
	private String codigo;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(AghCid.class, "CID");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setProjecao();
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.sqlRestriction(" replace(this_.CODIGO, '.','') =" + "'"+this.codigo+"'"));
	}
	
	private void setProjecao() {
		criteria.setProjection(Projections.property("CID."+ AghCid.Fields.DESCRICAO.toString()));
	}
	
	public DetachedCriteria build(String codigo) {
		this.codigo = codigo;
		return super.build();
	}
}
