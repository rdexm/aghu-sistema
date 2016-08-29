package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.vo.ItemProcedimentoVO;
import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatCompetenciaCompatibilid;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ConsultarExcludenciaMaterialBuilder extends QueryBuilder<DetachedCriteria> {

    private static final long serialVersionUID = -5084398328400384358L;
    private Short phoSeq;
    private Integer iphSeq;

    protected DetachedCriteria createProduct() {
	DetachedCriteria criteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "CMP");
	criteria.createAlias("CMP." + FatItensProcedHospitalar.Fields.FAT_COMPAT_EXCLUS_ITEMS.toString(), "EXC");
	criteria.createAlias("EXC." + FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString(), "IPX");
	criteria.createAlias("EXC." + FatCompatExclusItem.Fields.COMPETENCIA_COMPATIBILIDADE.toString(), "CEX");

	return criteria;
    }

    protected void doBuild(DetachedCriteria criteria) {
	criteria.add(Restrictions.le("CEX." + FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.toString(), new Date()));
	criteria.add(Restrictions.isNull("CEX." + FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.toString()));
	criteria.add(Restrictions.eq("CMP." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	criteria.add(Restrictions.eq("IPX." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	criteria.add(Restrictions.eq("EXC." + FatCompatExclusItem.Fields.IND_COMPARACAO.toString(), DominioIndComparacao.I));
	criteria.add(Restrictions.eq("EXC." + FatCompatExclusItem.Fields.IND_INTERNACAO.toString(), true));
	criteria.add(Restrictions.in("EXC." + FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString(), new DominioIndCompatExclus[] {
		DominioIndCompatExclus.PNI, DominioIndCompatExclus.INP }));
	criteria.add(Subqueries.propertyEq("CMP." + FatItensProcedHospitalar.Fields.FOG_SGR_GRP_SEQ.toString(),
		obterCriteriaParaValorParametro(AghuParametrosEnum.P_GRUPO_OPM)));
	criteria.add(Subqueries.propertyEq("IPX." + FatItensProcedHospitalar.Fields.FOG_SGR_GRP_SEQ.toString(),
		obterCriteriaParaValorParametro(AghuParametrosEnum.P_GRUPO_OPM)));
	criteria.add(Restrictions.eq("CMP." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), getPhoSeq()));
	criteria.add(Restrictions.eq("CMP." + FatItensProcedHospitalar.Fields.SEQ.toString(), getIphSeq()));

	criteria.addOrder(Order.asc("CMP." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()));
	criteria.addOrder(Order.asc("IPX." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()));

	ProjectionList pl = Projections
		.projectionList()
		.add(Projections.property("CMP." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()),
			ItemProcedimentoVO.Fields.CMP_PHO_SEQ.toString())
		.add(Projections.property("CMP." + FatItensProcedHospitalar.Fields.SEQ.toString()),
			ItemProcedimentoVO.Fields.CMP_SEQ.toString())
		.add(Projections.property("CMP." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()),
			ItemProcedimentoVO.Fields.CMP_COD_TABELA.toString())
		.add(Projections.property("CMP." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()),
			ItemProcedimentoVO.Fields.CMP_DESCRICAO.toString())
		.add(Projections.property("EXC." + FatCompatExclusItem.Fields.IND_COMPARACAO.toString()),
			ItemProcedimentoVO.Fields.DOMINIO_IND_COMPARACAO.toString())
		.add(Projections.property("EXC." + FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString()),
			ItemProcedimentoVO.Fields.DOMINIO_IND_COMPAT_EXCLUS.toString())
		.add(Projections.property("IPX." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()),
			ItemProcedimentoVO.Fields.IPX_PHO_SEQ.toString())
		.add(Projections.property("IPX." + FatItensProcedHospitalar.Fields.SEQ.toString()),
			ItemProcedimentoVO.Fields.IPX_SEQ.toString())
		.add(Projections.property("IPX." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()),
			ItemProcedimentoVO.Fields.IPX_COD_TABELA.toString())
		.add(Projections.property("IPX." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()),
			ItemProcedimentoVO.Fields.IPX_DESCRICAO.toString());

	criteria.setProjection(pl);
	criteria.setResultTransformer(Transformers.aliasToBean(ItemProcedimentoVO.class));
    }

    private DetachedCriteria obterCriteriaParaValorParametro(AghuParametrosEnum parametro) {
	DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class);
	criteria.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), parametro.name()));
	criteria.setProjection(Projections.projectionList().add(Projections.property(AghParametros.Fields.VLR_NUMERICO.toString())));
	return criteria;
    }

    public Short getPhoSeq() {
	return this.phoSeq;
    }

    public void setPhoSeq(Short phoSeq) {
	this.phoSeq = phoSeq;
    }

    public Integer getIphSeq() {
	return this.iphSeq;
    }

    public void setIphSeq(Integer iphSeq) {
	this.iphSeq = iphSeq;
    }
}