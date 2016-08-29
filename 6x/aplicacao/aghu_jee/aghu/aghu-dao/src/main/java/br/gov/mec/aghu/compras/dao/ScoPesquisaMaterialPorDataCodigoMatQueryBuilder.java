package br.gov.mec.aghu.compras.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.RelatorioMedicamentosCAPVO;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
public class ScoPesquisaMaterialPorDataCodigoMatQueryBuilder extends
		QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = -4654538824985213358L;

	private Integer codigo;
	private Date dataInicial;
	private Date dataFinal;
	private static final String matp= "mat.";

	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "mat");
		 
		 ProjectionList p = Projections.projectionList();
		 p.add(Projections.distinct(Projections.property(matp + ScoMaterial.Fields.NOME.toString())));
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
		 if(codigo!=null){
			 criteria.add(Restrictions.eq(matp + ScoMaterial.Fields.CODIGO.toString(), codigo));
		 }
		 if(dataInicial!=null){
			 criteria.add(Restrictions.between(matp +ScoMaterial.Fields.DATA_DIGITACAO.toString(), dataInicial, dataFinal));
		 }
	}

	public DetachedCriteria build(Integer codigo, Date dataInicial, Date dataFinal) {
		this.codigo = codigo;
		this.dataFinal = dataFinal;
		this.dataInicial = dataInicial;
		return super.build();
	}
}