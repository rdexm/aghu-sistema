package br.gov.mec.aghu.exames.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioDiaSemanaColetaExames;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelHorarioGradeExame;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.util.AghuEnumUtils;


public class AelHorarioGradeExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelHorarioGradeExame>  {
	
	private static final long serialVersionUID = 2161114074182818439L;

	public List<AelHorarioGradeExame> pesquisarHorarioGradeExameAtivo(Short gaeUnfSeq, Integer gaeSeqp, Date data) {
		DominioDiaSemanaColetaExames diaSemana =  AghuEnumUtils.retornaDiaSemanaColetaExames(data);
		return pesquisarHorarioGradeExameAtivo(gaeUnfSeq, gaeSeqp, diaSemana);
	}
	
	
	public List<AelHorarioGradeExame> pesquisarHorarioGradeExameAtivo(Short gaeUnfSeq, Integer gaeSeqp, DominioDiaSemanaColetaExames diaSemana){
		
		DetachedCriteria criteria =  DetachedCriteria.forClass(AelHorarioGradeExame.class);
		criteria.add(Restrictions.eq(AelHorarioGradeExame.Fields.GAE_UNF_SEQ.toString(), gaeUnfSeq));
		criteria.add(Restrictions.eq(AelHorarioGradeExame.Fields.GAE_SEQP.toString(), gaeSeqp));
		criteria.add(Restrictions.eq(AelHorarioGradeExame.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AelHorarioGradeExame.Fields.DIA_SEMANA.toString(), diaSemana));
		return executeCriteria(criteria);
	}
	
	public List<AelHorarioGradeExame> pesquisaPorGrade(AelGradeAgendaExame grade) {
		DetachedCriteria criteria = DetachedCriteria .forClass(AelHorarioGradeExame.class);
		criteria.add(Restrictions.eq(AelHorarioGradeExame.Fields.GAE_UNF_SEQ.toString(), grade.getId().getUnfSeq()));
		criteria.add(Restrictions.eq(AelHorarioGradeExame.Fields.GAE_SEQP.toString(), grade.getId().getSeqp()));
		
		criteria.setFetchMode(AelHorarioGradeExame.Fields.TIPO_MARCACAO_EXAME.toString() , FetchMode.JOIN);

		List<AelHorarioGradeExame> lista = this.executeCriteria(criteria);
		
		if(!lista.isEmpty()){
			Collections.sort(lista, new AelHorarioGradeExameComparator());
		}
		
		return lista;
	}
	
	/**
	 * Comparator para ordenar pesquisa dos horarios de grades pelos dias da semana
	 * 
	 * @author okamchen
	 * 
	 */
	class AelHorarioGradeExameComparator implements Comparator<AelHorarioGradeExame> {
		@Override
		public int compare(AelHorarioGradeExame a1, AelHorarioGradeExame a2) {
			return a1.getId().getDiaSemana().getOrder().compareTo(a2.getId().getDiaSemana().getOrder());
		}
	}

	public Long contarAelHorarioGradeExameByAelGradeAgendaExame(final Short unfSeq, final Integer seqp) {
		return executeCriteriaCount(criarCriteriaAelHorarioGradeExameByAelGradeAgendaExame(unfSeq, seqp));
	}

	private DetachedCriteria criarCriteriaAelHorarioGradeExameByAelGradeAgendaExame(final Short unfSeq, final Integer seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelHorarioGradeExame.class);
		criteria.add(Restrictions.eq(AelHorarioGradeExame.Fields.GAE_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AelHorarioGradeExame.Fields.GAE_SEQP.toString(), seqp));
		return criteria;
	}
	
	public List<AelHorarioGradeExame> pesquisaPorTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExame) {
		DetachedCriteria criteria = DetachedCriteria .forClass(AelHorarioGradeExame.class);
		criteria.add(Restrictions.eq(AelHorarioGradeExame.Fields.TIPO_MARCACAO_EXAME.toString(), tipoMarcacaoExame));

		return this.executeCriteria(criteria);
	}
}
