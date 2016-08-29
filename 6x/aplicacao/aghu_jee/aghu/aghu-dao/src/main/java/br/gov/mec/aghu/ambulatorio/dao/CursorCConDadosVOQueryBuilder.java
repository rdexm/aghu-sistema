package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.ambulatorio.vo.CursorConDadosVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.MamSolicitacaoRetorno;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class CursorCConDadosVOQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	private static final long serialVersionUID = 495758120434692123L;
	
	private final static String ALIAS_VUSL = "VUSL";
	private final static String ALIAS_ESP_CON = "ESP_CON";
	private final static String ALIAS_ESP_SOR = "ESP_SOR";
	private final static String ALIAS_EQP_CON = "EQP_CON";
	private final static String ALIAS_EQP_SOR = "EQP_SOR";
	private final static String ALIAS_GRD = "GRD";
	private final static String ALIAS_CON = "CON";
	private final static String ALIAS_SOR = "SOR";
	
	private final static String PONTO = ".";
	
	private DetachedCriteria criteria;
	private Long cSorSeq;
	private boolean isOracle;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, ALIAS_GRD);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setJoin();
		setProjecao();
	}
	
	private void setJoin(){
		criteria.createAlias(ALIAS_GRD+PONTO+AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), ALIAS_VUSL, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_GRD+PONTO+AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), ALIAS_EQP_CON, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_GRD+PONTO+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESP_CON, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_GRD+PONTO+AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), ALIAS_CON, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_CON+PONTO+AacConsultas.Fields.MAM_SOLICITACAO_RETORNO.toString(), ALIAS_SOR, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_SOR+PONTO+MamSolicitacaoRetorno.Fields.AGH_EQUIPES.toString(), ALIAS_EQP_SOR, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_SOR+PONTO+MamSolicitacaoRetorno.Fields.AGH_ESPECIALIDADES.toString(), ALIAS_ESP_SOR, JoinType.LEFT_OUTER_JOIN);
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_SOR+PONTO+MamSolicitacaoRetorno.Fields.SEQ.toString(), this.cSorSeq));
	}
	
	private String obterProjEspecialidade(){
		
		StringBuilder projEspecialidade = new StringBuilder(200);

		if(isOracle){
			projEspecialidade.append("NVL(");
		}else{
			projEspecialidade.append("COALESCE(");
		}
		projEspecialidade.append("esp_sor7_.NOME_REDUZIDO , esp_con3_.NOME_REDUZIDO) nome_reduzido ");
		
		return projEspecialidade.toString();
	}
	
	private String obterProjEquipe(){
		
		StringBuilder projEquipe = new StringBuilder(200);
		
		if(isOracle){
			projEquipe.append("NVL(");
		}else{
			projEquipe.append("COALESCE(");
		}
		
		projEquipe.append("eqp_sor6_").append(PONTO).append(AghEquipes.Fields.NOME.toString())
		.append(" ,").append("eqp_con2_").append(PONTO).append(AghEquipes.Fields.NOME.toString()).append(") nome ");
		
		return projEquipe.toString();
	}	
	private void setProjecao() {
		
		ProjectionList listProj = Projections.projectionList();
		
		listProj.add(Projections.property(ALIAS_VUSL+PONTO+VAacSiglaUnfSala.Fields.SIGLA.toString()), 
				CursorConDadosVO.Fields.SIGLA.toString());
		
		listProj.add(Projections.sqlProjection(obterProjEspecialidade(), new String[] {"nome_reduzido"}, new Type[] {new StringType()}), CursorConDadosVO.Fields.NOME_REDUZIDO.toString());
		listProj.add(Projections.sqlProjection(obterProjEquipe(), new String[] {"nome"}, new Type[] {new StringType()}), CursorConDadosVO.Fields.NOME.toString());
				
		listProj.add(Projections.property(ALIAS_GRD+PONTO+AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString()), 
				CursorConDadosVO.Fields.PRE_SER_VIN_CODIGO.toString());
		listProj.add(Projections.property(ALIAS_GRD+PONTO+AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString()), 
				CursorConDadosVO.Fields.PRE_SER_MATRICULA.toString());
		
		criteria.setProjection(listProj);
		criteria.setResultTransformer(Transformers.aliasToBean(CursorConDadosVO.class));
	}

	public DetachedCriteria build(Long cSorSeq, boolean isOracle) {
		this.cSorSeq = cSorSeq;
		this.isOracle = isOracle;
		
		return super.build();
	}

	public DetachedCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DetachedCriteria criteria) {
		this.criteria = criteria;
	}

	public Long getcSorSeq() {
		return cSorSeq;
	}

	public void setcSorSeq(Long cSorSeq) {
		this.cSorSeq = cSorSeq;
	}

	public boolean isOracle() {
		return isOracle;
	}

	public void setOracle(boolean isOracle) {
		this.isOracle = isOracle;
	}
}
