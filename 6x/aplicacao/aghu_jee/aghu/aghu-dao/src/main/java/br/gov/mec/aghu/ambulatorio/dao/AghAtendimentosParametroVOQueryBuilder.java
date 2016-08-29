package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class AghAtendimentosParametroVOQueryBuilder extends QueryBuilder<DetachedCriteria>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1312758218281433118L;
	
	private DetachedCriteria criteria;
	
	private Integer curConNumero;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(AghAtendimentos.class);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setProjecao();
		
	}
	
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), this.curConNumero));
	}

	private void setProjecao() {
		ProjectionList projList = Projections.projectionList();
		 projList.add(Projections.property(AghAtendimentos.Fields.SEQ.toString()));
		 criteria.setProjection(projList);
	}
	
	public DetachedCriteria build(Integer curConNumero) {
		this.curConNumero = curConNumero;
		return super.build();
	}
	
	
	public DetachedCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DetachedCriteria criteria) {
		this.criteria = criteria;
	}

	public Integer getCurConNumero() {
		return curConNumero;
	}

	public void setCurConNumero(Integer curConNumero) {
		this.curConNumero = curConNumero;
	}

	
}
