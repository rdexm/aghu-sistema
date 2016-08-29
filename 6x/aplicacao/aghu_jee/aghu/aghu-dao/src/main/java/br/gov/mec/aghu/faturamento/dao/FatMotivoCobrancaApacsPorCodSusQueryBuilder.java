package br.gov.mec.aghu.faturamento.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatMotivoCobrancaApac;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class FatMotivoCobrancaApacsPorCodSusQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final String MCA = "MCA.";
	private static final long serialVersionUID = -5471505643369474752L;
	private DetachedCriteria criteria;
	private Byte codigoSus;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(FatMotivoCobrancaApac.class, "MCA");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setProjections();
		setFiltro();
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(MCA+FatMotivoCobrancaApac.Fields.CODIGO.toString(), this.codigoSus));
		criteria.add(Restrictions.eq(MCA+FatMotivoCobrancaApac.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.setResultTransformer(Transformers.aliasToBean(FatMotivoCobrancaApac.class));
	}
	
	private void setProjections(){
		ProjectionList listaProj = Projections.projectionList();
		listaProj.add(Projections.property(MCA + FatMotivoCobrancaApac.Fields.CODIGO.toString()), FatMotivoCobrancaApac.Fields.CODIGO.toString());
		listaProj.add(Projections.property(MCA + FatMotivoCobrancaApac.Fields.DESCRICAO.toString()), FatMotivoCobrancaApac.Fields.DESCRICAO.toString());
		criteria.setProjection(listaProj);
	}
	
	public DetachedCriteria build(Byte codigoSus) {
		this.codigoSus = codigoSus;
		return super.build();
	}
}
