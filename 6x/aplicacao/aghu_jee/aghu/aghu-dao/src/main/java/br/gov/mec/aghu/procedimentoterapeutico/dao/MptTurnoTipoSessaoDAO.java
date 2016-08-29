package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;

public class MptTurnoTipoSessaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptTurnoTipoSessao> {

	private static final long serialVersionUID = -8637133291783913321L;

	public MptTurnoTipoSessao obterTurnoTipoSessaoPorTurnoTpsSeq(DominioTurno turno, Short tpsSeq) {
		DetachedCriteria criteria = obterCriteriaPorTurnoTpsSeq(turno, tpsSeq);
		
		return (MptTurnoTipoSessao) executeCriteriaUniqueResult(criteria);
	}
	
	public boolean verificarExistenciaTurnoTipoSessaoPorTurnoTpsSeq(DominioTurno turno, Short tpsSeq) {
		DetachedCriteria criteria = obterCriteriaPorTurnoTpsSeq(turno, tpsSeq);
		
		return executeCriteriaExists(criteria);
	}

	private DetachedCriteria obterCriteriaPorTurnoTpsSeq(DominioTurno turno, Short tpsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTurnoTipoSessao.class);
		criteria.add(Restrictions.eq(MptTurnoTipoSessao.Fields.TURNO.toString(), turno));
		criteria.add(Restrictions.eq(MptTurnoTipoSessao.Fields.TPS_SEQ.toString(), tpsSeq));
		return criteria;
	}
	
	public List<MptTurnoTipoSessao> obterTurnosPorTipoSessao(Short tpsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTurnoTipoSessao.class);
		criteria.add(Restrictions.eq(MptTurnoTipoSessao.Fields.TPS_SEQ.toString(), tpsSeq));
		
		return executeCriteria(criteria);
	}
	
	public List<MptTurnoTipoSessao> obterTurnosPorTipoSessaoOrdenado(Short tpsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTurnoTipoSessao.class);
		criteria.add(Restrictions.eq(MptTurnoTipoSessao.Fields.TPS_SEQ.toString(), tpsSeq));
		criteria.addOrder(Order.asc(MptTurnoTipoSessao.Fields.HR_INICIO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<MptTurnoTipoSessao> obterTurnosPorTipoSessaoETurno(Short tpsSeq, DominioTurno turno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTurnoTipoSessao.class);
		criteria.add(Restrictions.eq(MptTurnoTipoSessao.Fields.TPS_SEQ.toString(), tpsSeq));
		if (turno != null) {
			criteria.add(Restrictions.eq(MptTurnoTipoSessao.Fields.TURNO.toString(), turno));
		}
		criteria.addOrder(Order.asc(MptTurnoTipoSessao.Fields.HR_INICIO.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * #41730 - C6 - Horarios Configurados nos Turnos
	 * @param tpsSeq
	 * @param turno
	 * @return
	 */
	public List<MptTurnoTipoSessao> obterHorariosConfiguradosNosTurnos(Short tipoSessaoSeq, DominioTurno turno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTurnoTipoSessao.class, "TTS");
		criteria.createAlias("TTS." + MptTurnoTipoSessao.Fields.TIPO_SESSAO.toString(), "TSE", JoinType.INNER_JOIN);
		if (tipoSessaoSeq != null) {
			criteria.add(Restrictions.eq("TSE." + MptTipoSessao.Fields.SEQ.toString(), tipoSessaoSeq));
		}
		if (turno != null) {
			criteria.add(Restrictions.eq("TTS." + MptTurnoTipoSessao.Fields.TURNO.toString(), turno));
		}
		return executeCriteria(criteria);
	}
	
	public Integer obterTempoTotalTurnos(Short salSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTurnoTipoSessao.class, "TTS");
		criteria.createAlias("TTS." + MptTurnoTipoSessao.Fields.TIPO_SESSAO.toString(), "TSE", JoinType.INNER_JOIN);
		criteria.createAlias("TSE." + MptTipoSessao.Fields.SALAS.toString(), "SAL", JoinType.INNER_JOIN);
		criteria.createAlias("SAL." + MptSalas.Fields.LOCAIS_ATENDIMENTO.toString(), "LTA", JoinType.INNER_JOIN);
		
		ProjectionList projection = Projections.projectionList();
		if (isOracle()) {
			projection.add(Projections.sqlProjection(" CAST(SUM(({alias}.HR_FIM - {alias}.HR_INICIO) * 1440) AS INTEGER) AS TOTAL_TURNOS ",
					new String[]{"TOTAL_TURNOS"}, new Type[]{IntegerType.INSTANCE}));
			
		} else {
			projection.add(Projections.sqlProjection(" EXTRACT (EPOCH FROM SUM(({alias}.HR_FIM - {alias}.HR_INICIO) / 60))::INTEGER AS TOTAL_TURNOS ",
					new String[]{"TOTAL_TURNOS"}, new Type[]{IntegerType.INSTANCE}));
		}
		criteria.setProjection(projection);
		criteria.add(Restrictions.eq("SAL." + MptSalas.Fields.SEQ.toString(), salSeq));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	
	public MptTurnoTipoSessao obterHorariosTurnos(Short tipoSessaoSeq, DominioTurno turno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptTurnoTipoSessao.class, "TTS");
		criteria.createAlias("TTS." + MptTurnoTipoSessao.Fields.TIPO_SESSAO.toString(), "TSE", JoinType.INNER_JOIN);
		if (tipoSessaoSeq != null) {
			criteria.add(Restrictions.eq("TSE." + MptTipoSessao.Fields.SEQ.toString(), tipoSessaoSeq));
		}
		if (turno != null) {
			criteria.add(Restrictions.eq("TTS." + MptTurnoTipoSessao.Fields.TURNO.toString(), turno));
		}
		return (MptTurnoTipoSessao) executeCriteriaUniqueResult(criteria);
	}
	
}