package br.gov.mec.aghu.perinatologia.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoAtendTrabPartosId;

public class McoAtendTrabPartosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoAtendTrabPartos> {

	private static final long serialVersionUID = 6987691610369315298L;
	
	
	public List<Date> listarDthrAtendPorGestacao(Short gsoSeqp, Integer gsoPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class);
		criteria.setProjection(Projections.property(McoAtendTrabPartos.Fields.DTHR_ATEND.toString().toString()));
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_SEQP.toString(), gsoSeqp));
		criteria.addOrder(Order.asc(McoAtendTrabPartos.Fields.DTHR_ATEND.toString()));
		
		return executeCriteria(criteria);
	}
	
	
	
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
	
	/**
	 * Consulta o max do GSO_SEQP 
	 * @param gsoSeqp
	 * @param gsoPacCodigo
	 * @return
	 */
	public Integer obterMaxSeqpMcoAtendTrabParto(Short gsoSeqp, Integer gsoPacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class);
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_PAC_CODIGO.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_SEQP.toString(), gsoSeqp));
		criteria.setProjection(Projections.max(McoAtendTrabPartos.Fields.ID_SEQP.toString().toString()));
		Integer maxSeqP = (Integer) this.executeCriteriaUniqueResult(criteria);
			if (maxSeqP != null) {
				return Integer.valueOf(String.valueOf(maxSeqP + 1));
			}
			return (int) 1;
		}

	public DetachedCriteria listarAtendTrabPartos(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class);

		getCriteriaListarAtendTrabPartos(codigoPaciente, sequence, criteria);

		return criteria;
	}

	private void getCriteriaListarAtendTrabPartos(Integer codigoPaciente,
			Short sequence, DetachedCriteria criteria) {
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.GSO_PAC_CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.GSO_SEQP.toString(), sequence));
	}
	
	public List<McoAtendTrabPartos> listarAtendTrabPartos(Integer codigoPaciente, Short sequence, McoAtendTrabPartos.Fields order){
		DetachedCriteria criteria = listarAtendTrabPartos(codigoPaciente, sequence);
		
		if(order != null){
			criteria.addOrder(Order.asc(order.toString()));
		}
		
		return executeCriteria(criteria);
	}

	public List<McoAtendTrabPartos> listarAtendTrabPartosPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class);

		criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.GSO_PAC_CODIGO.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}
	
	public McoAtendTrabPartos obterAtendTrabPartos(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class,"TBP");
		getCriteriaListarAtendTrabPartos(codigoPaciente, sequence, criteria);
		criteria.addOrder(Order.asc("TBP."+McoAtendTrabPartos.Fields.DTHR_ATEND.toString()));

		DetachedCriteria subquery = DetachedCriteria.forClass(McoAtendTrabPartos.class, "TBP2");
		subquery.setProjection(Projections.max("TBP2."+McoAtendTrabPartos.Fields.SEQP.toString()));
		subquery.add(Restrictions.eqProperty("TBP2."+McoAtendTrabPartos.Fields.GSO_PAC_CODIGO.toString(), "TBP."+McoAtendTrabPartos.Fields.GSO_PAC_CODIGO.toString()));
		subquery.add(Restrictions.isNotNull("TBP2."+McoAtendTrabPartos.Fields.SEQP.toString()));
		
		criteria.add(Subqueries.propertyEq(McoAtendTrabPartos.Fields.SEQP.toString(), subquery));
		
		return (McoAtendTrabPartos) executeCriteriaUniqueResult(criteria);
	}

	public McoAtendTrabPartos obterAtendTrabPartosMaxSeqp(Integer pacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class,"TBP");
		
		getCriteriaListarAtendTrabPartos(pacCodigo, gsoSeqp, criteria);		

		DetachedCriteria subquery = DetachedCriteria.forClass(McoAtendTrabPartos.class, "TBP2");
		subquery.setProjection(Projections.max("TBP2."+McoAtendTrabPartos.Fields.SEQP.toString()));
		subquery.add(Restrictions.eqProperty("TBP2."+McoAtendTrabPartos.Fields.GSO_PAC_CODIGO.toString(), "TBP."+McoAtendTrabPartos.Fields.GSO_PAC_CODIGO.toString()));
		subquery.add(Restrictions.eqProperty("TBP2."+McoAtendTrabPartos.Fields.GSO_SEQP.toString(), "TBP."+McoAtendTrabPartos.Fields.GSO_SEQP.toString()));
		subquery.add(Restrictions.isNotNull("TBP2."+McoAtendTrabPartos.Fields.SEQP.toString()));
		
		criteria.add(Subqueries.propertyEq(McoAtendTrabPartos.Fields.SEQP.toString(), subquery));
		
		return (McoAtendTrabPartos) executeCriteriaUniqueResult(criteria);
	}

	public List<McoAtendTrabPartos> listarAtendTrabPartos(Integer pacCodigo,
			Short gsoSeqp,
			McoAtendTrabPartos.Fields order,
			Boolean indAnalgesiaBpd, Boolean indAnalgesiaBsd) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class,"TBP");
		
		getCriteriaListarAtendTrabPartos(pacCodigo, gsoSeqp, criteria);
		
		criteria.add(Restrictions.or(
				Restrictions.eq(McoAtendTrabPartos.Fields.IND_ANALGESIA_BPD.toString(), indAnalgesiaBpd),
				Restrictions.eq(McoAtendTrabPartos.Fields.IND_ANALGESIA_BPD.toString(), indAnalgesiaBsd)));
		
		if(order != null){
			criteria.addOrder(Order.asc(order.toString()));
		}
		
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaMcoTrabPartos(Integer pacCodigo, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoAtendTrabPartos.class);
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_PAC_CODIGO.toString(), pacCodigo));
		}
		if (seqp != null) {
			criteria.add(Restrictions.eq(McoAtendTrabPartos.Fields.ID_GSO_SEQP.toString(), seqp));
		}
		return criteria;
	}
	
	public McoAtendTrabPartos obterMcoTrabPartosPorId(Integer pacCodigo, Short seqp) {
		DetachedCriteria criteria = obterCriteriaMcoTrabPartos(pacCodigo, seqp);
		return (McoAtendTrabPartos) executeCriteriaUniqueResult(criteria);
	}


	public List<McoAtendTrabPartos> buscarListaMcoAtendTrabPartos(
			Integer pacCodigo, Short seqp) {
		DetachedCriteria criteria = obterCriteriaMcoTrabPartos(pacCodigo, seqp);
		return executeCriteria(criteria);
	}


}
