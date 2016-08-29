package br.gov.mec.aghu.faturamento.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.vo.ProcedimentoRealizadoDadosOPMVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class FatVlrItemProcHospCompsObterListaDadosOPMQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = -2438253244841095590L;

	private DetachedCriteria criteria;
	private DetachedCriteria subCriteriaIphPhoSeq;
	private ProjectionList listProj = Projections.projectionList();
	
	private Integer cthSeq;
	private boolean isOracle;
	

	private static final String ALIAS_VAL  = "VAL";
	private static final String ALIAS_IPH  = "IPH";
	private static final String ALIAS_AMA = "AMA";
	private static final String PONTO = ".";
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(FatVlrItemProcedHospComps.class, "VAL");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setSubSelect();
		setProjecao();
		setJoin();
		setFiltro();
		setOrder();
	}
	
	private void setJoin(){
		
		criteria.createAlias(ALIAS_VAL+PONTO+FatVlrItemProcedHospComps.Fields.PROCEDIMENTO.toString(), ALIAS_IPH, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_IPH+PONTO+FatItensProcedHospitalar.Fields.ATOS_MEDICOS_AIH.toString(), ALIAS_AMA, JoinType.INNER_JOIN);
	}
	
	private void setSubSelect(){
		subCriteriaIphPhoSeq = DetachedCriteria.forClass(AghParametros.class, "AGP");
		subCriteriaIphPhoSeq.setProjection(Projections.property("AGP."+AghParametros.Fields.VLR_NUMERICO.toString()));
		subCriteriaIphPhoSeq.add(Restrictions.eq("AGP."+AghParametros.Fields.NOME.toString(), AghuParametrosEnum.P_TABELA_FATUR_PADRAO.toString()));
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_IPH+PONTO+FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNull(ALIAS_VAL+PONTO+FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()));
		criteria.add(Restrictions.eq(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), this.cthSeq));
		criteria.add(Subqueries.propertyEq(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.IPH_PHO_SEQ.toString(), subCriteriaIphPhoSeq));
		StringBuilder sql = new StringBuilder(500);
		if(isOracle){
			sql.append(" Cast(SUBSTR(LPAD(ama2_.iph_cod_sus,10,0),0,2) AS NUMBER) = ");
		}else{
			sql.append(" CAST(SUBSTR(lpad(CAST(ama2_.IPH_COD_SUS AS VARCHAR),10,'0'),1,2)AS BIGINT) = ");
		}
		sql.append(" ( ");
		sql.append(" select AGP_.VLR_NUMERICO as VLR_NUMERICO ");
		sql.append(" from ");
		sql.append(" AGH.AGH_PARAMETROS AGP_ ");
		sql.append(" where  AGP_.NOME = ");
		sql.append(" '");
		sql.append(AghuParametrosEnum.P_GRUPO_OPM.toString());
		sql.append("' ");
		sql.append(" ) ");
		criteria.add(Restrictions.sqlRestriction(sql.toString()));
	}
	
	private void setProjecao() {
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.SEQ_ARQ_SUS.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.NOTA_FISCAL.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.NOTA_FISCAL.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.CGC.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.CGC.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.LOTE_OPM.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.LOTE_OPM.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.SERIE_OPM.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.SERIE_OPM.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.REG_ANVISA_OPM.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.REG_ANVISA_OPM.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.CNPJ_REG_ANVISA.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.CNPJ_REG_ANVISA.toString());
		
		criteria.setProjection(listProj);
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentoRealizadoDadosOPMVO.class));
	}
	
	private void setOrder(){
		criteria.addOrder(Order.asc(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.toString()));
	}
	
	public DetachedCriteria build(Integer cthSeq, boolean isOracle) {
		this.cthSeq = cthSeq;
		this.isOracle = isOracle;
		
		return super.build();
	}
}
