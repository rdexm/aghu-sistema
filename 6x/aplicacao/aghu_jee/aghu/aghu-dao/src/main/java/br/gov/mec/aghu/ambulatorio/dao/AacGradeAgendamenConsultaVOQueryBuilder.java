package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.AacGradeAgendamenConsultasVO;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class AacGradeAgendamenConsultaVOQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	private static final long serialVersionUID = 495758120434692123L;
	
	private final static String ALIAS_ESP = "ESP";
	private final static String ALIAS_EQP = "EQP";
	private final static String ALIAS_GRD = "GRD";
	
	private DetachedCriteria criteria;
	private Integer seq;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, ALIAS_GRD);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setProjecao();
		setJoin();
	}
	
	private void setJoin(){
		criteria.createAlias(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), ALIAS_EQP, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESP, JoinType.INNER_JOIN);
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.SEQ.toString(), this.seq));
	}
	
	
	private void setProjecao() {
		
		ProjectionList listProj = Projections.projectionList();
		
		listProj.add(Projections.property(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString()), 
				AacGradeAgendamenConsultasVO.Fields.PRE_SER_MATRICULA.toString());
		listProj.add(Projections.property(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString()), 
				AacGradeAgendamenConsultasVO.Fields.PRE_SER_VIN_CODIGO.toString());
		listProj.add(Projections.property(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()), 
				AacGradeAgendamenConsultasVO.Fields.ESP_SEQ.toString());
		listProj.add(Projections.property(ALIAS_ESP+"."+AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), 
				AacGradeAgendamenConsultasVO.Fields.NOME_ESPECIALIDADE.toString());
		listProj.add(Projections.property(ALIAS_EQP+"."+AghEquipes.Fields.NOME.toString()), 
				AacGradeAgendamenConsultasVO.Fields.NOME.toString());
		
		criteria.setProjection(listProj);
		criteria.setResultTransformer(Transformers.aliasToBean(AacGradeAgendamenConsultasVO.class));
	}

	public DetachedCriteria build(Integer seq) {
		this.seq = seq;
		return super.build();
	}

	public DetachedCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DetachedCriteria criteria) {
		this.criteria = criteria;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}
