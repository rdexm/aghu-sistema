package br.gov.mec.aghu.bancosangue.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.faturamento.vo.DoacaoColetaSangueVO;
import br.gov.mec.aghu.model.AbsRegSanguineoPacientes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;

public class AbsRegSanguineoPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsRegSanguineoPacientes> {

	private static final long serialVersionUID = -4243760314326478193L;

	public List<AbsRegSanguineoPacientes> listarRegSanguineoPacientesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsRegSanguineoPacientes.class);

		criteria.add(Restrictions.eq(AbsRegSanguineoPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	/**
	 * Lista reg sanguineo com exists sem agrupamento
	 * 
	 * @param vDtHrInicio
	 * @param vDtHrFim
	 * @param matricula
	 * @param vinCodigo
	 * @param origem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DoacaoColetaSangueVO> listarRegSanguineoPacienteComExistsSemAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, DominioOrigemAtendimento origem) {
		
		List<DoacaoColetaSangueVO> result = null;
		StringBuffer hql = null;
		Query query = null;

		hql = new StringBuffer();
		hql.append(" select trunc(a."); 
		hql.append(AbsRegSanguineoPacientes.Fields.DTHR_REGISTRO.toString());
		hql.append(") as dthrMovimento");
		
		hql.append(", a.");
		hql.append(AbsRegSanguineoPacientes.Fields.COD_PACIENTE.toString());
		hql.append(" as pacCodigo");

		hql.append(", case when a.").append(AbsRegSanguineoPacientes.Fields.SER_MATRICULA.toString());
		hql.append(" is null then ").append(matricula).append(" else a.");
		hql.append(AbsRegSanguineoPacientes.Fields.SER_MATRICULA.toString()).append(" end as serMatricula");
		
		hql.append(", case when a.").append(AbsRegSanguineoPacientes.Fields.SER_VIN_CODIGO.toString());
		hql.append(" is null then ").append(vinCodigo).append(" else a.");
		hql.append(AbsRegSanguineoPacientes.Fields.SER_VIN_CODIGO.toString()).append(" end as serVinCodigo");
		
		//from
		hql.append(" from ");
		hql.append(AbsRegSanguineoPacientes.class.getName());
		hql.append(" as a ");

		//where
		hql.append(" where not exists (");
		
			hql.append("select 1 from ");
			hql.append(AghAtendimentos.class.getName()).append(" d");
			//where
			hql.append(" where d.");
			hql.append(AghAtendimentos.Fields.PAC_CODIGO.toString());
			hql.append(" = a.");
			hql.append(AbsRegSanguineoPacientes.Fields.COD_PACIENTE.toString());
			hql.append(" and a.");
			hql.append(AbsRegSanguineoPacientes.Fields.DTHR_REGISTRO.toString());
			hql.append(" between d.");
			hql.append(AghAtendimentos.Fields.DTHR_INICIO.toString());
			hql.append(" and ");
			hql.append(" case when d.").append(AghAtendimentos.Fields.DTHR_FIM.toString());
			hql.append(" is null then :sysdate else d.").append(AghAtendimentos.Fields.DTHR_FIM.toString()).append(" end");
			hql.append(" and d.");
			hql.append(AghAtendimentos.Fields.ORIGEM.toString());
			hql.append(" = :origem");
			
		hql.append(')');
		
		hql.append(" and a.");
		hql.append(AbsRegSanguineoPacientes.Fields.DTHR_REGISTRO.toString());
		hql.append(" between :vDtHrInicio and :vDtHrFim");

		//query
		query = createHibernateQuery(hql.toString());
		query.setParameter("vDtHrInicio", vDtHrInicio);
		query.setParameter("vDtHrFim", vDtHrFim);
		query.setParameter("sysdate", new Date());
		query.setParameter("origem", origem);

		query.setResultTransformer(Transformers.aliasToBean(DoacaoColetaSangueVO.class));
		result = query.list();
		
		return result;
	}
	
	/*
	 * substituindo ABS_REG_SANGUINEO_PACIENTES, caso exista.
	 *
		update ABS_REG_SANGUINEO_PACIENTES
        set pac_codigo  =  p_codigo_destino
		where pac_codigo  =  p_codigo_origem
		and dthr_registro  >= v_dthr_inicio;
	 */
	public void substituirPacienteRegSanguineoPacientes(AipPacientes pacienteOrigem, AipPacientes pacienteDestino, Date dthrInicio) {
		// Usando JPQL pois não é possível atualizar a PK pelo Hibernate
		javax.persistence.Query q = this.createQuery(
				"UPDATE " + AbsRegSanguineoPacientes.class.getName() + " " + "SET "
						+ AbsRegSanguineoPacientes.Fields.COD_PACIENTE.toString() + " = :codPacienteDestino " + "WHERE "
						+ AbsRegSanguineoPacientes.Fields.COD_PACIENTE.toString() + " = :codPacienteOrigem " + "AND "
						+ AbsRegSanguineoPacientes.Fields.DTHR_REGISTRO.toString() + " >= :dthrInicio");
		q.setParameter("codPacienteDestino", pacienteDestino == null ? null : pacienteDestino.getCodigo());
		q.setParameter("codPacienteOrigem", pacienteOrigem == null ? null : pacienteOrigem.getCodigo());
		q.setParameter("dthrInicio", dthrInicio);
		q.executeUpdate();

		this.flush();
	}

}