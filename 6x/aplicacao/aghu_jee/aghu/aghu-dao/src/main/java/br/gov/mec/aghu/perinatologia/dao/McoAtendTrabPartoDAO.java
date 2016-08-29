package br.gov.mec.aghu.perinatologia.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoAtendTrabPartosId;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * 
 * @author felipe
 *
 */
public class McoAtendTrabPartoDAO extends BaseDao<McoAtendTrabPartos> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1712613170642413556L;
	
	
	/**
	 * Consulta utilizada para listar os Atendimento Trabalho Parto
	 * @param gsoSeqp
	 * @param gsoPacCodigo
	 * @return
	 */
	public List<McoAtendTrabPartos> listarAtendPartosPorId(Short gsoSeqp, Integer gsoPacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class);
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_SEQP.toString(), gsoSeqp));
		criteria.addOrder(Order.asc(McoAtendTrabPartos.Fields.DTHR_ATEND.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta o max do GSO_SEQP 
	 * @param gsoSeqp
	 * @param gsoPacCodigo
	 * @return
	 */
	public Short obterMaxSeqpMcoAtendTrabParto(Short gsoSeqp, Integer gsoPacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class);
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_SEQP.toString(), gsoSeqp));
		criteria.setProjection(Projections.max(McoAtendTrabPartos.Fields.ID_SEQP.toString().toString()));
		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
			if (maxSeqP != null) {
				return Short.valueOf(String.valueOf(maxSeqP + 1));
			}
			return (short) 1;
		}
		
	
	/**
	 * Obter por id
	 * @param gsoSeqp
	 * @param gsoPacCodigo
	 * @return
	 */
	public McoAtendTrabPartos obterMcoAtendTrabPartosPorId(McoAtendTrabPartosId id){
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class);
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID.toString(), id));
		return (McoAtendTrabPartos) executeCriteriaUniqueResult(criteria);
	}
	
	public List<Date> listarDthrAtendPorGestacao(Short gsoSeqp, Integer gsoPacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class);
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_SEQP.toString(), gsoSeqp));
		criteria.setProjection(Projections.property(McoAtendTrabPartos.Fields.DTHR_ATEND.toString()));
		criteria.addOrder(Order.asc(McoAtendTrabPartos.Fields.DTHR_ATEND.toString()));
		
		return executeCriteria(criteria);
	}
	
}
