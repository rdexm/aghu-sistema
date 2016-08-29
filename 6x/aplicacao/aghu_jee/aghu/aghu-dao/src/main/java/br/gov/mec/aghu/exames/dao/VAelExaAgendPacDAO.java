package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.model.VAelExaAgendPac;
import br.gov.mec.aghu.model.VAelHrGradeDisp;


public class VAelExaAgendPacDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelExaAgendPac> {
	
	private static final long serialVersionUID = 7889827360691705160L;

	public List<VAelExaAgendPac> obterExamesAgendamentosPaciente(Integer pacCodigo, String exaSigla, Integer manSeq, Short unfSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExaAgendPac.class, "aea");
		
		criteria.add(Restrictions.eq("aea."+VAelExaAgendPac.Fields.PAC_CODIGO.toString(), pacCodigo));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(VAelHrGradeDisp.class, "hgd");
		subCriteria.setProjection(Projections.property("hgd."+VAelHrGradeDisp.Fields.DTHR_AGENDA.toString()));
		
		subCriteria.add(Restrictions.eqProperty("hgd."+VAelHrGradeDisp.Fields.GRADE.toString(),
				"aea."+VAelExaAgendPac.Fields.HED_GAE_UNF_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("hgd."+VAelHrGradeDisp.Fields.SEQ_GRADE.toString(),
				"aea."+VAelExaAgendPac.Fields.HED_GAE_SEQP.toString()));
		subCriteria.add(Restrictions.eqProperty("hgd."+VAelHrGradeDisp.Fields.DTHR_AGENDA.toString(),
				"aea."+VAelExaAgendPac.Fields.HED_DTHR_AGENDA.toString()));
		subCriteria.add(Restrictions.eq("hgd."+VAelHrGradeDisp.Fields.SIGLA.toString(), exaSigla));
		subCriteria.add(Restrictions.eq("hgd."+VAelHrGradeDisp.Fields.MAT_EXAME.toString(), manSeq));
		subCriteria.add(Restrictions.eq("hgd."+VAelHrGradeDisp.Fields.UNF_EXAME.toString(), unfSeq));
		subCriteria.add(Restrictions.eq("hgd."+VAelHrGradeDisp.Fields.IND_AGENDA_EX_MESMO_HOR.toString(), "S"));
		subCriteria.add(Restrictions.eq("hgd."+VAelHrGradeDisp.Fields.SITUACAO_HORARIO.toString(), DominioSituacaoHorario.M));
		
		criteria.add(Subqueries.exists(subCriteria));
		
		criteria.addOrder(Order.asc("aea."+VAelExaAgendPac.Fields.DATA_SOLIC.toString()));
		criteria.addOrder(Order.asc("aea."+VAelExaAgendPac.Fields.ISE_SOE_SEQ.toString()));
		criteria.addOrder(Order.asc("aea."+VAelExaAgendPac.Fields.DESCRICAO_EXAME.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<VAelExaAgendPac> obterInformacoesExamesAgendadosPaciente(Integer pacCodigo, String exaSigla, Integer manSeq, Short unfSeq, 
			Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExaAgendPac.class, "aea");
		
		criteria.add(Restrictions.eq("aea."+VAelExaAgendPac.Fields.PAC_CODIGO.toString(), pacCodigo));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(VAelHrGradeDisp.class, "hgd");
		subCriteria.setProjection(Projections.property("hgd."+VAelHrGradeDisp.Fields.DTHR_AGENDA.toString()));
		subCriteria.add(Restrictions.eqProperty("hgd."+VAelHrGradeDisp.Fields.GRADE.toString(),
				"aea."+VAelExaAgendPac.Fields.HED_GAE_UNF_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("hgd."+VAelHrGradeDisp.Fields.SEQ_GRADE.toString(),
				"aea."+VAelExaAgendPac.Fields.HED_GAE_SEQP.toString()));
		subCriteria.add(Restrictions.eqProperty("hgd."+VAelHrGradeDisp.Fields.DTHR_AGENDA.toString(),
				"aea."+VAelExaAgendPac.Fields.HED_DTHR_AGENDA.toString()));
		subCriteria.add(Restrictions.eq("hgd."+VAelHrGradeDisp.Fields.SIGLA.toString(), exaSigla));
		subCriteria.add(Restrictions.eq("hgd."+VAelHrGradeDisp.Fields.MAT_EXAME.toString(), manSeq));
		subCriteria.add(Restrictions.eq("hgd."+VAelHrGradeDisp.Fields.UNF_EXAME.toString(), unfSeq));
		subCriteria.add(Restrictions.eq("hgd."+VAelHrGradeDisp.Fields.IND_AGENDA_EX_MESMO_HOR.toString(), "S"));
		subCriteria.add(Restrictions.eq("hgd."+VAelHrGradeDisp.Fields.SITUACAO_HORARIO.toString(), DominioSituacaoHorario.M));
		
		criteria.add(Subqueries.exists(subCriteria));
		
		criteria.add(Restrictions.eq("aea."+VAelExaAgendPac.Fields.HED_GAE_UNF_SEQ.toString(), hedGaeUnfSeq));
		criteria.add(Restrictions.eq("aea."+VAelExaAgendPac.Fields.HED_GAE_SEQP.toString(), hedGaeSeqp));
		criteria.add(Restrictions.eq("aea."+VAelExaAgendPac.Fields.HED_DTHR_AGENDA.toString(), hedDthrAgenda));
		
		criteria.addOrder(Order.asc("aea."+VAelExaAgendPac.Fields.DATA_SOLIC.toString()));
		criteria.addOrder(Order.asc("aea."+VAelExaAgendPac.Fields.ISE_SOE_SEQ.toString()));
		criteria.addOrder(Order.asc("aea."+VAelExaAgendPac.Fields.DESCRICAO_EXAME.toString()));
		
		return executeCriteria(criteria);
	}
}
