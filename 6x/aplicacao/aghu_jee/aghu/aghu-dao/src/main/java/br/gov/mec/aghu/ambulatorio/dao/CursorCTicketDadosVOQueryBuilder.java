package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.ambulatorio.vo.CursorCTicketDadosVO;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class CursorCTicketDadosVOQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	private static final long serialVersionUID = 229774329736479670L;
	private final static String ALIAS_CCT = "CCT";
	private final static String ALIAS_CAA = "CAA";
	private final static String ALIAS_PAC = "PAC";
	private final static String ALIAS_VUSL = "VUSL";
	private final static String ALIAS_ESP = "ESP";
	private final static String ALIAS_EQP = "EQP";
	private final static String ALIAS_GRD = "GRD";
	private final static String ALIAS_CON = "CON";
	private final static String ALIAS_SER = "SER";
	
	private DetachedCriteria criteria;
	private DetachedCriteria subCriteria;
	
	private Integer cConNumero;
	private Integer aipGetCCus;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, ALIAS_GRD);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setSubSelect();
		setFiltro();
		setJoin();
		setProjecao();
	}
	
	private void setJoin(){
		criteria.createAlias(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), ALIAS_VUSL, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), ALIAS_EQP, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESP, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.AAC_CONSULTA.toString(), ALIAS_CON, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_CON+"."+AacConsultas.Fields.PACIENTE.toString(), ALIAS_PAC, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_CON+"."+AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), ALIAS_CAA, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_ESP+"."+AghEspecialidades.Fields.CENTRO_CUSTO.toString(), ALIAS_CCT, JoinType.INNER_JOIN);
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_CON+"."+AacConsultas.Fields.NUMERO.toString(), this.cConNumero));
		criteria.add(Restrictions.or(Property.forName(ALIAS_CCT+"."+FccCentroCustos.Fields.CODIGO.toString()).eq(subCriteria), 
				Subqueries.notExists(subCriteria)));
	}

	private void setSubSelect(){
		subCriteria = DetachedCriteria.forClass(RapServidores.class, ALIAS_SER);
		subCriteria.setProjection(Projections.property(ALIAS_SER+"."+RapServidores.Fields.CCT_CODIGO.toString()));
		subCriteria.add(Restrictions.eqProperty(ALIAS_SER+"."+RapServidores.Fields.VIN_CODIGO.toString(), 
				ALIAS_CON+"."+AacConsultas.Fields.SER_VINCULO_CONSULTADO.toString()));
		subCriteria.add(Restrictions.eqProperty(ALIAS_SER+"."+RapServidores.Fields.MATRICULA.toString(), 
				ALIAS_CON+"."+AacConsultas.Fields.SER_MATRICULA_CONSULTADO.toString()));
	}
	
	private void setProjecao() {
		
		ProjectionList listProj = Projections.projectionList();
		
		listProj.add(Projections.property(ALIAS_CON+"."+AacConsultas.Fields.NUMERO.toString()), 
				CursorCTicketDadosVO.Fields.NUMERO.toString());
		listProj.add(Projections.property(ALIAS_CON+"."+AacConsultas.Fields.DATA_CONSULTA.toString()), 
				CursorCTicketDadosVO.Fields.DT_CONSULTA.toString());
		listProj.add(Projections.property(ALIAS_CON+"."+AacConsultas.Fields.SER_VINCULO_CONSULTADO.toString()), 
				CursorCTicketDadosVO.Fields.SER_VIN_CODIGO_CONSULTADO.toString());
		listProj.add(Projections.property(ALIAS_CON+"."+AacConsultas.Fields.SER_MATRICULA_CONSULTADO.toString()), 
				CursorCTicketDadosVO.Fields.SER_MATRICULA_CONSULTADO.toString());
		listProj.add(Projections.property(ALIAS_CCT+"."+FccCentroCustos.Fields.DESCRICAO.toString()), 
				CursorCTicketDadosVO.Fields.DESCRICAO_CCT.toString());
		listProj.add(Projections.property(ALIAS_CAA+"."+AacCondicaoAtendimento.Fields.DESCRICAO.toString()), 
				CursorCTicketDadosVO.Fields.DESCRICAO_CAA.toString());
		listProj.add(Projections.property(ALIAS_CON+"."+AacConsultas.Fields.IND_EXCEDE_PROGRAMACAO.toString()), 
				CursorCTicketDadosVO.Fields.IND_EXCEDE_PROGRAMACAO.toString());
		listProj.add(Projections.property(ALIAS_ESP+"."+AghEspecialidades.Fields.NOME_REDUZIDO.toString()), 
				CursorCTicketDadosVO.Fields.NOME_REDUZIDO.toString());
		listProj.add(Projections.property(ALIAS_VUSL+"."+VAacSiglaUnfSala.Fields.SIGLA.toString()), 
				CursorCTicketDadosVO.Fields.SIGLA.toString());
		listProj.add(Projections.property(ALIAS_VUSL+"."+VAacSiglaUnfSala.Fields.SALA.toString()), 
				CursorCTicketDadosVO.Fields.SALA.toString());
		listProj.add(Projections.property(ALIAS_EQP+"."+AghEquipes.Fields.NOME.toString()), 
				CursorCTicketDadosVO.Fields.NOME_EQP.toString());
		listProj.add(Projections.property(ALIAS_PAC+"."+AipPacientes.Fields.CODIGO.toString()), 
				CursorCTicketDadosVO.Fields.PAC_CODIGO.toString());
		listProj.add(Projections.property(ALIAS_PAC+"."+AipPacientes.Fields.PRONTUARIO.toString()), 
				CursorCTicketDadosVO.Fields.PRONTUARIO.toString());
		listProj.add(Projections.property(ALIAS_PAC+"."+AipPacientes.Fields.NOME.toString()), 
				CursorCTicketDadosVO.Fields.NOME_PAC.toString());
		listProj.add(Projections.property(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.SEQ.toString()), 
				CursorCTicketDadosVO.Fields.SEQ.toString());
		listProj.add(Projections.property(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA_COD.toString()), 
				CursorCTicketDadosVO.Fields.GRADE_SER_MATRICULA.toString());
		listProj.add(Projections.property(ALIAS_GRD+"."+AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_COD.toString()), 
				CursorCTicketDadosVO.Fields.GRADE_SER_VIN_CODIGO.toString());
		listProj.add(Projections.sqlProjection("USER usuario ", 
				new String[] {"usuario"}, 
				new Type[] {new StringType()}), 
				CursorCTicketDadosVO.Fields.USER.toString());
		
		criteria.setProjection(listProj);
		criteria.setResultTransformer(Transformers.aliasToBean(CursorCTicketDadosVO.class));
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

	public Integer getAipGetCCus() {
		return aipGetCCus;
	}

	public void setAipGetCCus(Integer aipGetCCus) {
		this.aipGetCCus = aipGetCCus;
	}
}
