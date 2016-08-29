package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.RelatorioMedicamentosCAPVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
public class ScoPesquisaMaterialQueryBuilder extends
		QueryBuilder<DetachedCriteria> {
	
	private static final long serialVersionUID = 7982190522218359149L;

	private DominioSimNao pEstocavel;
	private DominioSimNao indCapCmed;
	private DominioSimNao indConfaz;
	private static final String matp= "mat.";

	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "mat");
		 
		 ProjectionList p = Projections.projectionList();
		 p.add(Projections.property(matp + ScoMaterial.Fields.DESCRICAO.toString()),RelatorioMedicamentosCAPVO.Fields.DESCRICAO.toString());
		 p.add(Projections.property(matp + ScoMaterial.Fields.NOME.toString()),RelatorioMedicamentosCAPVO.Fields.NOME.toString());
		 p.add(Projections.property(matp + ScoMaterial.Fields.CODIGO.toString()),RelatorioMedicamentosCAPVO.Fields.CODIGO.toString());
		 p.add(Projections.property(matp + ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()),RelatorioMedicamentosCAPVO.Fields.UNIDADE_MEDIDA_CODIGO.toString());
		 p.add(Projections.property(matp + ScoMaterial.Fields.IND_ESTOCAVEL.toString()),RelatorioMedicamentosCAPVO.Fields.IND_ESTOCAVEL.toString());
		 p.add(Projections.property(matp + ScoMaterial.Fields.IND_CAP_CMED.toString()),RelatorioMedicamentosCAPVO.Fields.IND_CAP_CMED.toString());
		 p.add(Projections.property(matp + ScoMaterial.Fields.DT_ALTERACAO_CAP.toString()),RelatorioMedicamentosCAPVO.Fields.DT_ALTERACAO_CAP.toString());
		 p.add(Projections.property(matp + ScoMaterial.Fields.IND_CONFAZ.toString()),RelatorioMedicamentosCAPVO.Fields.IND_CONFAZ.toString());
		 p.add(Projections.property(matp + ScoMaterial.Fields.DT_ALTERACAO_CONFAZ.toString()),RelatorioMedicamentosCAPVO.Fields.DT_ALTERACAO_CONFAZ.toString());
		 criteria.setProjection(p);
		 criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMedicamentosCAPVO.class));
		 
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		if(pEstocavel != null){
			 if(pEstocavel.equals(DominioSimNao.S)){
				 criteria.add(Restrictions.eq(matp + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), true)); 
			 }
			 else if(pEstocavel.equals(DominioSimNao.N)) {
				 criteria.add(Restrictions.eq(matp + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), false)); 
			 }
				 
		 }
		 if(indConfaz.equals(DominioSimNao.N) && indCapCmed.equals(DominioSimNao.N)){//RN 02
			 criteria.add(Restrictions.or(Restrictions.eq(matp + ScoMaterial.Fields.IND_CONFAZ.toString(), DominioSimNao.S),
					 Restrictions.eq(matp + ScoMaterial.Fields.IND_CAP_CMED.toString(), DominioSimNao.S)));
		 }
		 else {
			 if(indConfaz.equals(DominioSimNao.S)){
				 criteria.add(Restrictions.eq(matp + ScoMaterial.Fields.IND_CONFAZ.toString(), indConfaz));
			 }
			 if(indCapCmed.equals(DominioSimNao.S)){
				 criteria.add(Restrictions.eq(matp + ScoMaterial.Fields.IND_CAP_CMED.toString(),indCapCmed));
			 }
			 criteria.addOrder(Order.desc(matp + ScoMaterial.Fields.UMD_CODIGO.toString()));
			 criteria.addOrder(Order.desc(matp + ScoMaterial.Fields.IND_ESTOCAVEL.toString()));
		 }
	}

	public DetachedCriteria build(DominioSimNao pEstocavel,DominioSimNao indCapCmed,DominioSimNao indConfaz) {
		this.pEstocavel = pEstocavel;
		this.indCapCmed = indCapCmed;
		this.indConfaz = indConfaz;
		return super.build();
	}
}