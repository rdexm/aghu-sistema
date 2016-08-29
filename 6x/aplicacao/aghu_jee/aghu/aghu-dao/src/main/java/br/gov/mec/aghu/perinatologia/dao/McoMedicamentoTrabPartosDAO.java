package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartos;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartosId;

public class McoMedicamentoTrabPartosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoMedicamentoTrabPartos> {

	private static final long serialVersionUID = 5681297737798202420L;

	public List<McoMedicamentoTrabPartos> listarMedicamentosTrabPartos(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoMedicamentoTrabPartos.class);

		getCriteriaListarMedicamentosTrabPartos(codigoPaciente, sequence,
				criteria);

		return executeCriteria(criteria);
	}

	private void getCriteriaListarMedicamentosTrabPartos(
			Integer codigoPaciente, Short sequence, DetachedCriteria criteria) {
		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.SEQUENCE.toString(), sequence));
	}
	
	public List<McoMedicamentoTrabPartos> listarMedicamentosTrabPartosPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoMedicamentoTrabPartos.class);

		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));


		return executeCriteria(criteria);
	}
	
	public List<McoMedicamentoTrabPartos> listarMedicamentosTrabPartos(
			Integer codigoPaciente, Short sequence, 
			McoMedicamentoTrabPartos.Fields atributoNotNull,
			McoMedicamentoTrabPartos.Fields order, Boolean asc,
			Integer ... codsMdtos) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoMedicamentoTrabPartos.class);

		getCriteriaListarMedicamentosTrabPartos(codigoPaciente, sequence,
				criteria);
		
		if(codsMdtos != null){
			criteria.add(Restrictions.in(McoMedicamentoTrabPartos.Fields.MEDICAMENTO.toString() + "." + AfaMedicamento.Fields.MAT_CODIGO.toString(), codsMdtos));
		}
		
		if(atributoNotNull != null){
			criteria.add(Restrictions.isNotNull(McoMedicamentoTrabPartos.Fields.DTHR_INI.toString()));
		}
		
		if(order != null){
			if(asc){
				criteria.addOrder(Order.asc(order.toString()));
			}else{
				criteria.addOrder(Order.desc(order.toString()));
			}
		}

		return executeCriteria(criteria);
	}
	
	public boolean existeMedicamento(Integer gsoPacCodigo,
			 Short gsoSeqp,
			 Integer medMatCodigo){
 		
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoMedicamentoTrabPartos.class);

		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.MED_MAT_CODIGO.toString(),medMatCodigo));
		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.GSO_CODIGO_PACIENTE.toString(),gsoPacCodigo));
		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.GSO_SEQ_PACIENTE.toString(),gsoSeqp));
		criteria.setProjection(Projections.count(McoMedicamentoTrabPartos.Fields.MED_MAT_CODIGO.toString()));
		
		Long result = (Long) super.executeCriteriaUniqueResult(criteria);
		
		return (result > 0);
	}

	public List<Integer> pesquisarMedMatCodigos(Integer gsoPacCodigo,Short gsoSeqp) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(McoMedicamentoTrabPartos.class);

		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.GSO_CODIGO_PACIENTE.toString(),gsoPacCodigo));
		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.GSO_SEQ_PACIENTE.toString(),gsoSeqp));
		criteria.setProjection(Projections.property(McoMedicamentoTrabPartos.Fields.MED_MAT_CODIGO.toString()));
		
		return executeCriteria(criteria);
	}


	public List<McoMedicamentoTrabPartos> pesquisarMcoMedicamentoTrabPartos(Integer gsoPacCodigo,Short gsoSeqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoMedicamentoTrabPartos.class);
		
		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.GSO_CODIGO_PACIENTE.toString(),gsoPacCodigo));
		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.GSO_SEQ_PACIENTE.toString(),gsoSeqp));
		criteria.addOrder(Order.asc(McoMedicamentoTrabPartos.Fields.CRIADO_EM.toString()));
		return executeCriteria(criteria);
	}

	public McoMedicamentoTrabPartos buscarMcoMedicamentoTrabPartosPorId(
			Integer gsoPacCodigo, Short gsoSeqp, Integer matCodigo) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(McoMedicamentoTrabPartos.class);

		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.GSO_CODIGO_PACIENTE.toString(),gsoPacCodigo));
		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.GSO_SEQ_PACIENTE.toString(),gsoSeqp));
		criteria.add(Restrictions.eq(McoMedicamentoTrabPartos.Fields.MED_MAT_CODIGO.toString(),matCodigo));

		return (McoMedicamentoTrabPartos) executeCriteriaUniqueResult(criteria);
	}

	public void excluirMedicamentoPorId(McoMedicamentoTrabPartosId id) {
		removerPorId(id);
	}
}
