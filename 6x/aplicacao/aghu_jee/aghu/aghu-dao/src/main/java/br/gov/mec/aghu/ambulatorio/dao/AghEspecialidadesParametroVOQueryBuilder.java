package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.ParametrosAghEspecialidadesAtestadoVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class AghEspecialidadesParametroVOQueryBuilder extends QueryBuilder<DetachedCriteria>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -35812869490401267L;
	
	private DetachedCriteria criteria;
	
	private Integer cConNumero;
	
	private final static String ALIAS_ESP = "ESP";
	
	private final static String ALIAS_CON = "CON";
	
	private final static String ALIAS_GRD = "GRD";
	
	private final static String PONTO = ".";

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(AacConsultas.class, ALIAS_CON);
		
		
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setJoin();
		setProjecao();
		this.criteria.setResultTransformer(Transformers.aliasToBean(ParametrosAghEspecialidadesAtestadoVO.class));
	}
	
	private void setJoin(){
		criteria.createAlias(ALIAS_CON+PONTO+AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), ALIAS_GRD, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_GRD+PONTO+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESP, JoinType.INNER_JOIN);
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_CON+PONTO+AacConsultas.Fields.NUMERO.toString(), this.cConNumero));
	}

	private void setProjecao() {
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(ALIAS_ESP+PONTO+AghEspecialidades.Fields.SEQ.toString()), ParametrosAghEspecialidadesAtestadoVO.Fields.V_ESP_SEQ.toString());
		projList.add(Projections.property(ALIAS_ESP+PONTO+AghEspecialidades.Fields.ESP_SEQ.toString()), ParametrosAghEspecialidadesAtestadoVO.Fields.V_ESP_PAI.toString());
		projList.add(Projections.property(ALIAS_GRD+PONTO+AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString()), ParametrosAghEspecialidadesAtestadoVO.Fields.V_UNF_SEQ.toString());
		criteria.setProjection(projList);
	}
	
	public DetachedCriteria build(Integer cConNumero) {
		this.cConNumero = cConNumero;
		return super.build();
	}

	public DetachedCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DetachedCriteria criteria) {
		this.criteria = criteria;
	}

	public Integer getcConNumero() {
		return cConNumero;
	}

	public void setcConNumero(Integer cConNumero) {
		this.cConNumero = cConNumero;
	}

}
