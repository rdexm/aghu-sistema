package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeSituacao;
import br.gov.mec.aghu.model.AacHorarioGradeConsulta;
import br.gov.mec.aghu.model.AacHorarioGradeConsultaId;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;

public class AacHorarioGradeConsultaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AacHorarioGradeConsulta> {

	private static final long serialVersionUID = -9096501865499868652L;

	public void criaRegistroId(AacHorarioGradeConsulta entity,
			AacGradeAgendamenConsultas entityPai) {
		entity.setGradeAgendamentoConsulta(entityPai);
		entity.setCriadoEm(new Date());

		AacHorarioGradeConsultaId id = new AacHorarioGradeConsultaId();
		id.setSeqp(nextSeqp(entityPai));
		id.setGrdSeq(entityPai.getSeq());
		entity.setId(id);
	}

	public Integer nextSeqp(AacGradeAgendamenConsultas entityPai) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacHorarioGradeConsulta.class);
		criteria.setProjection(Projections
				.max(AacHorarioGradeConsulta.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(
				AacHorarioGradeConsulta.Fields.GRADE_AGENDAMENTO.toString(),
				entityPai));
		Integer seqp = (Integer) this.executeCriteriaUniqueResult(criteria);
		if (seqp == null) {
			seqp = 0;
		}
		return ++seqp;
	}

	public AacHorarioGradeConsulta inserir(AacHorarioGradeConsulta entity,
			AacGradeAgendamenConsultas entityPai) {
		//criaRegistroId(entity, entityPai);
		 super.persistir(entity);
		 this.flush();
		 return entity;
		
	}

	public List<AacHorarioGradeConsulta> pesquisaPorGrade(
			AacGradeAgendamenConsultas grade) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacHorarioGradeConsulta.class);
		criteria.add(Restrictions.eq(
				AacHorarioGradeConsulta.Fields.GRADE_AGENDAMENTO.toString(),
				grade));
		criteria.addOrder(Order.asc(AacHorarioGradeConsulta.Fields.IND_SITUACAO.toString()));
		criteria.addOrder(Order.asc(AacHorarioGradeConsulta.Fields.DIA_SEMANA.toString()));		
		criteria.addOrder(Order.asc(AacHorarioGradeConsulta.Fields.HORA_INICIO.toString()));
		
		criteria.setFetchMode(AacHorarioGradeConsulta.Fields.FORMA_AGENDAMENTO.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AacHorarioGradeConsulta.Fields.FORMA_AGENDAMENTO.toString() + "." + AacFormaAgendamento.Fields.PAGADOR.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AacHorarioGradeConsulta.Fields.FORMA_AGENDAMENTO.toString() + "." + AacFormaAgendamento.Fields.TIPO_AGENDAMENTO.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AacHorarioGradeConsulta.Fields.FORMA_AGENDAMENTO.toString() + "." + AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO.toString(), FetchMode.JOIN);

		return this.executeCriteria(criteria);
	}
	
	public List<AacHorarioGradeConsulta> buscaHorarioGradeConsulta(Integer seqP,
			Integer grdSeq, DominioDiaSemana diaSemana, Short unfSeq, Byte sala) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacHorarioGradeConsulta.class);
		criteria.createAlias("gradeAgendamentoConsulta", "gradeAgendamentoConsulta");
		
		criteria.add(Restrictions.eq(AacHorarioGradeConsulta.Fields.DIA_SEMANA.toString(), diaSemana));
		criteria.add(Restrictions.eq(AacHorarioGradeConsulta.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		
		if (seqP!=null && grdSeq!=null){
			criteria.add(Restrictions.or(
				Restrictions.and(
						Restrictions.eq(AacHorarioGradeConsulta.Fields.GRD_SEQ.toString(), grdSeq),		
						Restrictions.ne(AacHorarioGradeConsulta.Fields.SEQP.toString(), seqP)),
						Restrictions.ne(AacHorarioGradeConsulta.Fields.GRD_SEQ.toString(),grdSeq)));
		}	
		
		criteria.add(Restrictions.eq("gradeAgendamentoConsulta." 
				+ AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("gradeAgendamentoConsulta." 
				+ AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_ID_SALA.toString(), sala));
		
		return this.executeCriteria(criteria);
	}
	
	public List<AacHorarioGradeConsulta> buscaHorarioGradeConsultaParaGeracao(Date dataInicio, Integer grdSeq, Calendar dataBaseCal){

		DominioDiaSemana diaSemana = CoreUtil.retornaDiaSemana(dataBaseCal.getTime());
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacHorarioGradeConsulta.class);
		criteria.createAlias("gradeAgendamentoConsulta", "grd");		
		criteria.createAlias("grd.gradeSituacao", "gst");
		criteria.createAlias("grd.especialidade", "esp");
		
		criteria.add(Restrictions.eq("grd."+ AacGradeAgendamenConsultas.Fields.SEQ.toString(), grdSeq));
		criteria.add(Restrictions.eq("gst."+ AacGradeSituacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.le("gst."+ AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString(), dataInicio));

		criteria.add(Restrictions.or(
				Restrictions.isNull("gst."+ AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString()),
				Restrictions.ge("gst."+ AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString(),dataInicio)
		));
		criteria.add(Restrictions.eq(AacHorarioGradeConsulta.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		/* Melhoria retirada para funcionar igual ao AGH.
		criteria.add(Restrictions.or(
				Restrictions.isNull(AacHorarioGradeConsulta.Fields.DT_ULTIMA_GERACAO.toString()),
				Restrictions.lt(AacHorarioGradeConsulta.Fields.DT_ULTIMA_GERACAO.toString(),dataInicio)
		));*/
		criteria.add(Restrictions.eq(AacHorarioGradeConsulta.Fields.DIA_SEMANA.toString(), diaSemana));		
		
		return executeCriteria(criteria);
	}
	
	public List<AacHorarioGradeConsulta> buscarHoraGradeConsultaComDataUltimaGeracao(Integer grdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacHorarioGradeConsulta.class);
		criteria.add(Restrictions.eq(AacHorarioGradeConsulta.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.isNotNull(AacHorarioGradeConsulta.Fields.DT_ULTIMA_GERACAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AacHorarioGradeConsulta> pesquisarHorariosGradeConsulta(Integer grdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacHorarioGradeConsulta.class);
		criteria.add(Restrictions.eq(AacHorarioGradeConsulta.Fields.GRD_SEQ.toString(), grdSeq));
		return executeCriteria(criteria);
	}

	public Long listarHorariosGradeConsultaCount(AacFormaAgendamento formaAgendamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacHorarioGradeConsulta.class);

		criteria.add(Restrictions.eq(AacHorarioGradeConsulta.Fields.FORMA_AGENDAMENTO.toString(), formaAgendamento));

		return executeCriteriaCount(criteria);
	}

	public boolean possuiHorariosGradeConsultaPorGradeAgendamento(Integer grdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacHorarioGradeConsulta.class);

		criteria.add(Restrictions.eq(AacHorarioGradeConsulta.Fields.ID_GRD_SEQ.toString(), grdSeq));

		return executeCriteriaCount(criteria) > 0;
	}
		
}
