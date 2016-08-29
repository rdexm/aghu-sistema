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

public class FatVlrItemProcHospCompsObterListaProcedimentosRealizadosQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = -2438253244841095590L;

	private DetachedCriteria criteria;
	private DetachedCriteria subCriteria;
	private ProjectionList listProj = Projections.projectionList();
	
	private Integer cthSeq;

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
		subCriteria = DetachedCriteria.forClass(AghParametros.class, "AGP");
		subCriteria.setProjection(Projections.property("AGP."+AghParametros.Fields.VLR_NUMERICO.toString()));
		subCriteria.add(Restrictions.eq("AGP."+AghParametros.Fields.NOME.toString(), AghuParametrosEnum.P_TABELA_FATUR_PADRAO.toString()));
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_IPH+PONTO+FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNull(ALIAS_VAL+PONTO+FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString()));
		criteria.add(Restrictions.eq(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), this.cthSeq));
		criteria.add(Subqueries.propertyEq(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.IPH_PHO_SEQ.toString(), subCriteria));
	}
	
	private void setProjecao() {
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.SEQ_ARQ_SUS.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.IPH_COD_SUS.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.IPH_COD_SUS_PROCEDIMENTO.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.IND_EQUIPE.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.IND_EQUIPE.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.CPF_CNS.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.CPF_CNS.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.CBO.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.CBO.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.CGC.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.CGC.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.QUANTIDADE.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.QUANTIDADE.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.COMPETENCIA_UTI.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.COMPETENCIA_UTI.toString());
		listProj.add(Projections.property(ALIAS_IPH+PONTO+FatItensProcedHospitalar.Fields.DESCRICAO.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.DESCRICAO.toString());
		listProj.add(Projections.property(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.IPH_COD_SUS.toString()),
				ProcedimentoRealizadoDadosOPMVO.Fields.IPH_COD_SUS_GRUPO.toString());
		
		criteria.setProjection(listProj);
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedimentoRealizadoDadosOPMVO.class));
	}
	
	private void setOrder(){
		criteria.addOrder(Order.asc(ALIAS_AMA+PONTO+FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.toString()));
	}
	
	public DetachedCriteria build(Integer cthSeq) {
		this.cthSeq = cthSeq;
		return super.build();
	}
}
