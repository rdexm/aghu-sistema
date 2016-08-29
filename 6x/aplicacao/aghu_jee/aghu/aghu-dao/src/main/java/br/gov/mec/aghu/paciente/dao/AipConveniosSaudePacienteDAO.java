/**
 * 
 */
package br.gov.mec.aghu.paciente.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;

/**
 * @author rafael
 * 
 */
public class AipConveniosSaudePacienteDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AipConveniosSaudePaciente> {
	
	private static final long serialVersionUID = -5244058103308721140L;

	/**
	 * Método usado para obter todos os convênios (AipConveniosSaudePaciente) de
	 * um paciente.
	 * 
	 * @dbtables AipConveniosSaudePaciente select
	 * 
	 * @param paciente
	 * @return
	 */
	public List<AipConveniosSaudePaciente> pesquisarConveniosPaciente(AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipConveniosSaudePaciente.class);

		criteria.add(Restrictions.eq(AipConveniosSaudePaciente.Fields.PACIENTE.toString(), paciente));

		criteria.setFetchMode(AipConveniosSaudePaciente.Fields.CONVENIO.toString(),	FetchMode.JOIN);

		criteria.createAlias(AipConveniosSaudePaciente.Fields.CONVENIO.toString(),	AipConveniosSaudePaciente.Fields.CONVENIO.toString());

		criteria.setFetchMode(AipConveniosSaudePaciente.Fields.CONVENIO.toString()
						+ "."
						+ FatConvenioSaudePlano.Fields.CONVENIO_SAUDE
								.toString(), FetchMode.JOIN);

		return this.executeCriteria(criteria);
	}
	
	public List<AipConveniosSaudePaciente> pesquisarConveniosPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipConveniosSaudePaciente.class);
		criteria.add(Restrictions.eq(AipConveniosSaudePaciente.Fields.CODIGO_PACIENTE.toString(), pacCodigo));

		return this.executeCriteria(criteria);
	}

	/**
	 * ORADB AIPC_GET_MATRICULA_CONV
	 * 
	 */
	public BigDecimal buscaMatriculaConvenio(Integer prontuario, Short convenio, Byte plano) {
		BigDecimal matricula = null;
		StringBuilder hql = new StringBuilder(300);
		hql.append(" SELECT ")
		.append(" cvp.matricula ")
		.append(" FROM ")
		.append(" AipConveniosSaudePaciente cvp ")
		.append(" join cvp.paciente pac ")
		.append(" WHERE ")
		.append(" pac.prontuario	= :prontuario ")
		.append(" AND CVP.convenio.id.cnvCodigo = :convenio ")
		.append(" AND cvp.convenio.id.seq = :plano ")
		.append(" AND cvP.situacao = :situacao ");

		javax.persistence.Query query = this.createQuery(hql.toString());

		query.setParameter("prontuario", prontuario);
		query.setParameter("convenio", convenio);
		query.setParameter("plano", plano);
		query.setParameter("situacao", DominioSituacao.A);

		List result = query.getResultList();
		if (result != null && result.size() > 0) {
			Object matric = (Object) result.get(0);
			if (matric != null) {
				matricula = (BigDecimal) matric;
			}
		}

		return matricula;
	}
	
	public Short obterValorSeqPlanoPaciente(AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria
		.forClass(AipConveniosSaudePaciente.class);
		criteria.add(Restrictions.eq(
				AipConveniosSaudePaciente.Fields.PACIENTE.toString(), paciente));
		criteria.setProjection(Projections
				.max(AipConveniosSaudePaciente.Fields.SEQUENCIAL.toString()));

		Short valor = (Short) this.executeCriteriaUniqueResult(criteria);

		if (valor == null) {
			valor = 0;
		}

		valor++;

		return valor;
	}
	
	public Short obterValorSeqPlanoCodPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipConveniosSaudePaciente.class);
		criteria.add(Restrictions.eq(AipConveniosSaudePaciente.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.setProjection(Projections.max(AipConveniosSaudePaciente.Fields.SEQUENCIAL.toString()));
		Short valor = (Short) this.executeCriteriaUniqueResult(criteria);
		if (valor == null) {
			valor = 0;
		}
		valor++;
		return valor;
	}
	
	public DominioSituacao obterSituacaoAnteriorPlanoPaciente(
			AipConveniosSaudePaciente planoPaciente) {
		DominioSituacao situacaoAnterior;

		DetachedCriteria criteria = DetachedCriteria.forClass(AipConveniosSaudePaciente.class);

		criteria.add(Restrictions.eq(AipConveniosSaudePaciente.Fields.IDENTIFICADOR.toString(), planoPaciente.getId()));
		criteria.setProjection(Projections.property(AipConveniosSaudePaciente.Fields.SITUACAO.toString()));
		situacaoAnterior = (DominioSituacao) executeCriteriaUniqueResult(criteria);

		return situacaoAnterior;
	}

	public List<AipConveniosSaudePaciente> pesquisarAtivosPorPacienteCodigoSeqConvenio(Integer codigoPaciente,
			Short codigoConvenioSaudePlano, Byte seqConvenioSaudePlano) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipConveniosSaudePaciente.class);
		criteria.add(Restrictions.eq(AipConveniosSaudePaciente.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(AipConveniosSaudePaciente.Fields.CODIGO_CONVENIO.toString(), codigoConvenioSaudePlano));
		criteria.add(Restrictions.eq(AipConveniosSaudePaciente.Fields.SEQ_CONVENIO.toString(), seqConvenioSaudePlano));
		criteria.add(Restrictions.eq(AipConveniosSaudePaciente.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
}
