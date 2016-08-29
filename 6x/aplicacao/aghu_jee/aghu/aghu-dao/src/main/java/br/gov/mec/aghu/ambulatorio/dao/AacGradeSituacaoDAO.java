package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeSituacao;

public class AacGradeSituacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacGradeSituacao> {

	private static final long serialVersionUID = -8915047668516316849L;

	public List<AacGradeSituacao> listarSituacaoGrade(Integer grdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRD_SEQ.toString(), grdSeq));
		return executeCriteria(criteria);
	}
	
	public Long obterCountListaSituacaoGrade(Integer grdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRD_SEQ.toString(), grdSeq));
		return executeCriteriaCount(criteria);
	}
	
	public List<AacGradeSituacao> listarSituacaoGradeData(Integer grdSeq, Date dataReferencia){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.add(Restrictions.eq(
				AacGradeSituacao.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.le(AacGradeSituacao.Fields.DT_INICIO_SITUACAO
				.toString(), dataReferencia));
		criteria.add(Restrictions.or(Restrictions
				.isNull(AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString()),
				Restrictions.ge(AacGradeSituacao.Fields.DT_FIM_SITUACAO
						.toString(), dataReferencia)));
		criteria.addOrder(Order.asc(AacGradeSituacao.Fields.GRD_SEQ.toString()));
		criteria.addOrder(Order.desc(AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AacGradeSituacao> listarSituacaoGradePorDataDecrescente(Integer grdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.add(Restrictions.eq(
				AacGradeSituacao.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.addOrder(Order.desc(AacGradeSituacao.Fields.GRD_SEQ.toString()));
		criteria.addOrder(Order.desc(AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AacGradeSituacao> listarSituacaoGradeDataMaior(Integer grdSeq, Date dataReferencia){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.add(Restrictions.eq(
				AacGradeSituacao.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.gt(AacGradeSituacao.Fields.DT_INICIO_SITUACAO
				.toString(), dataReferencia));
		return executeCriteria(criteria);
	}
	
	public List<AacGradeSituacao> listarSituacaoGradePorSituacaoDiferente(Integer grdSeq, DominioSituacao situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.add(Restrictions.eq(
				AacGradeSituacao.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.ne(AacGradeSituacao.Fields.IND_SITUACAO
				.toString(), situacao));
		criteria.addOrder(Order.asc(AacGradeSituacao.Fields.GRD_SEQ.toString()));
		criteria.addOrder(Order.asc(AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString()));
		return executeCriteria(criteria);
	}

	public List<AacGradeSituacao> listarSituacaoGradePorDataESituacao(Integer grdSeq, Date dtInicioSituacao, DominioSituacao situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.add(Restrictions.eq(
				AacGradeSituacao.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.eq(AacGradeSituacao.Fields.IND_SITUACAO
				.toString(), situacao));
		criteria.add(Restrictions.eq(AacGradeSituacao.Fields.DT_INICIO_SITUACAO
				.toString(), dtInicioSituacao));
		return executeCriteria(criteria);
	}
	
	public List<AacGradeSituacao> listarVerificaSituacaoGrade(Integer grdSeq, Date dataReferencia){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.add(Restrictions.eq(
				AacGradeSituacao.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.le(AacGradeSituacao.Fields.DT_INICIO_SITUACAO
				.toString(), dataReferencia));
		criteria.add(Restrictions.or(Restrictions
				.isNull(AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString()),
				Restrictions.ge(AacGradeSituacao.Fields.DT_FIM_SITUACAO
						.toString(), dataReferencia)));
		criteria.add(Restrictions.eq(
				AacGradeSituacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public Long listarGradeSituacaoComProfCount(AacGradeSituacao gradeSituacao) {
		
		DetachedCriteria criteria = criarCriteriaPesquisaComProf(gradeSituacao);

		return executeCriteriaCount(criteria);
	}
	
	public Long listarGradeSituacaoSemProfCount(AacGradeSituacao gradeSituacao) {
		
		DetachedCriteria criteria = criarCriteriaPesquisaSemProf(gradeSituacao);

		return executeCriteriaCount(criteria);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria criarCriteriaPesquisaComProf(AacGradeSituacao gradeSituacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.createAlias(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString(), AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString());
		
		if (gradeSituacao.getGradeAgendamentoConsulta().getAacUnidFuncionalSala() != null && gradeSituacao.getGradeAgendamentoConsulta().getAacUnidFuncionalSala().getId().getUnfSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString(), gradeSituacao.getGradeAgendamentoConsulta().getAacUnidFuncionalSala().getId().getUnfSeq()));
		}
		
		if (gradeSituacao.getGradeAgendamentoConsulta().getAacUnidFuncionalSala() != null && gradeSituacao.getGradeAgendamentoConsulta().getAacUnidFuncionalSala().getId().getSala() != null) {
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_ID_SALA.toString(), gradeSituacao.getGradeAgendamentoConsulta().getAacUnidFuncionalSala().getId().getSala()));
		}
		
		if (gradeSituacao.getGradeAgendamentoConsulta().getEquipe() != null && gradeSituacao.getGradeAgendamentoConsulta().getEquipe().getSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString(), gradeSituacao.getGradeAgendamentoConsulta().getEquipe().getSeq()));
		}
		
				
		if (gradeSituacao.getGradeAgendamentoConsulta().getEspecialidade() != null && gradeSituacao.getGradeAgendamentoConsulta().getEspecialidade().getSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), gradeSituacao.getGradeAgendamentoConsulta().getEspecialidade().getSeq()));
		}
		
		if(gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade()!= null && gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade().getId()!= null && gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade().getId().getSerMatricula()!=null){
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString(), gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade().getId().getSerMatricula()));
		}	
		
		if(gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade()!= null && gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade().getId()!= null && gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade().getId().getSerVinCodigo()!=null){
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString(), gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade().getId().getSerVinCodigo()));
		}
	
		if (gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade()!= null && gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade().getId()!= null && gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade().getId().getEspSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.PRE_ESP_SEQ.toString(), gradeSituacao.getGradeAgendamentoConsulta().getProfEspecialidade().getId().getEspSeq()));
		}
		
		if (gradeSituacao.getGradeAgendamentoConsulta().getProcedimento() != null) {
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.IND_PROCEDIMENTO.toString(), gradeSituacao.getGradeAgendamentoConsulta().getProcedimento()));
		}
		
		criteria.add(Restrictions.eq(AacGradeSituacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.gt(AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString(), gradeSituacao.getId().getDtInicioSituacao()));
		
		return criteria;
	}
	
	private DetachedCriteria criarCriteriaPesquisaSemProf(AacGradeSituacao gradeSituacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.createAlias(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString(), AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString());
		
		if (gradeSituacao.getGradeAgendamentoConsulta().getAacUnidFuncionalSala().getId().getUnfSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString(), gradeSituacao.getGradeAgendamentoConsulta().getAacUnidFuncionalSala().getId().getUnfSeq()));
		}
		
		if (gradeSituacao.getGradeAgendamentoConsulta().getAacUnidFuncionalSala().getId().getSala() != null) {
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_ID_SALA.toString(), gradeSituacao.getGradeAgendamentoConsulta().getAacUnidFuncionalSala().getId().getSala()));
		}
		
		if (gradeSituacao.getGradeAgendamentoConsulta().getEquipe().getSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString(), gradeSituacao.getGradeAgendamentoConsulta().getEquipe().getSeq()));
		}
		
				
		if (gradeSituacao.getGradeAgendamentoConsulta().getEspecialidade().getSeq() != null) {
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), gradeSituacao.getGradeAgendamentoConsulta().getEspecialidade().getSeq()));
		}
		
		if (gradeSituacao.getGradeAgendamentoConsulta().getProcedimento() != null) {
			criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.IND_PROCEDIMENTO.toString(), gradeSituacao.getGradeAgendamentoConsulta().getProcedimento()));
		}
		
		criteria.add(Restrictions.isNull(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString()));
		
		criteria.add(Restrictions.isNull(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString()+"."+AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString()));
	
		criteria.add(Restrictions.eq(AacGradeSituacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	
		criteria.add(Restrictions.gt(AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString(), gradeSituacao.getId().getDtInicioSituacao()));
		
		return criteria;
	}

	public List<AacGradeSituacao> listarGradeSituacaoPorGradeAgendamentoConsulta(
			AacGradeAgendamenConsultas grade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		
		criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRADE_AGENDAMENTO_CONSULTA.toString(),grade));
		
		
		return this.executeCriteria(criteria);
	}
	
	public AacGradeSituacao obterGradePeriodoInformado(Integer grdSeq, Date dataInicio, Date dataFim){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacGradeSituacao.class);
		criteria.add(Restrictions.eq(AacGradeSituacao.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.le(AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString(), dataInicio));
		
		if(dataFim!=null) {
			criteria.add(Restrictions.or(Restrictions
					.isNull(AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString()),
					Restrictions.ge(AacGradeSituacao.Fields.DT_FIM_SITUACAO
							.toString(), dataFim)));
		}
		else {
			criteria.add(Restrictions.or(Restrictions
					.isNull(AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString()),
					Restrictions.ge(AacGradeSituacao.Fields.DT_FIM_SITUACAO
							.toString(), dataInicio)));
		}
		
		criteria.add(Restrictions.eq(AacGradeSituacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return (AacGradeSituacao) executeCriteriaUniqueResult(criteria);
	}
}