package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipRegSanguineos;

public class AipRegSanguineosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipRegSanguineos> {

	private static final long serialVersionUID = 1618143875333857611L;

	public Byte buscaMaxSeqp(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipRegSanguineos.class);

		criteria.add(Restrictions.eq(AipRegSanguineos.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.setProjection(Projections.max(AipRegSanguineos.Fields.SEQUENCE.toString()));

		return (Byte) executeCriteriaUniqueResult(criteria);
	}

	public List<AipRegSanguineos> listarRegSanguineosPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipRegSanguineos.class);

		criteria.add(Restrictions.eq(AipRegSanguineos.Fields.CODIGO_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	/**
	 * Monta consulta de AipRegSanguineos por código do paciente
	 * 
	 * @param pacCodigo
	 * @return
	 */
	private DetachedCriteria montarConsultaPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipRegSanguineos.class);
		criteria.add(Restrictions.eq(AipRegSanguineos.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		return criteria;
	}

	/**
	 * Verifica se existe algum AipRegSanguineos filtrando por código do paciente
	 * 
	 * Web Service #37245
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public Boolean existsRegSanguineosPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = this.montarConsultaPorCodigoPaciente(pacCodigo);
		return executeCriteriaExists(criteria);
	}

	/**
	 * Busca AipRegSanguineos filtrando por código do paciente e seqp
	 * 
	 * Web Service #36968
	 * 
	 * @param pacCodigo
	 * @param seqp
	 * @return
	 */
	public AipRegSanguineos obterRegSanguineosPorCodigoPaciente(Integer pacCodigo, Byte seqp) {
		DetachedCriteria criteria = this.montarConsultaPorCodigoPaciente(pacCodigo);
		criteria.add(Restrictions.eq(AipRegSanguineos.Fields.SEQUENCE.toString(), seqp));
		List<AipRegSanguineos> result = this.executeCriteria(criteria, 0, 1, null, true);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	/**
	 * Buscar registro sem dados na tabela de AIP_REG_SANGUINEOS através do código do paciente
	 * 
	 * Web Service #36971
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public List<AipRegSanguineos> listarRegSanguineosSemDadosPorPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = this.montarConsultaPorCodigoPaciente(pacCodigo);
		criteria.add(Restrictions.isNull(AipRegSanguineos.Fields.GRUPO_SANGUINEO.toString()));
		criteria.add(Restrictions.isNull(AipRegSanguineos.Fields.FATOR_RH.toString()));
		criteria.add(Restrictions.isNull(AipRegSanguineos.Fields.COOMBS.toString()));
		return super.executeCriteria(criteria);
	}
	
	/**
	 * Verificar se existe registro sem dados na tabela de AIP_REG_SANGUINEOS através do código do paciente
	 * 
	 * Web Service #37235
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public Boolean existsRegSanguineosSemDadosPorPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = this.montarConsultaPorCodigoPaciente(pacCodigo);
		criteria.add(Restrictions.isNull(AipRegSanguineos.Fields.GRUPO_SANGUINEO.toString()));
		criteria.add(Restrictions.isNull(AipRegSanguineos.Fields.FATOR_RH.toString()));
		criteria.add(Restrictions.isNull(AipRegSanguineos.Fields.COOMBS.toString()));
		return super.executeCriteriaExists(criteria);
	}
	
	/**
	 * Obtem Registro Sanguineo mais recente do Paciente.
	 * 
	 * @param pacCodigo Código do {@link AipPacientes}
	 * @return Instancia de {@link AipRegSanguineos}
	 */
	public AipRegSanguineos obterRegSanguineoPorCodigoPaciente(Integer pacCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AipRegSanguineos.class);
		criteria.add(Restrictions.eq(AipRegSanguineos.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.addOrder(Order.desc(AipRegSanguineos.Fields.SEQUENCE.toString()));
		List<AipRegSanguineos> result = this.executeCriteria(criteria);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
}
