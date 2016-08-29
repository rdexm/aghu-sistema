package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.model.AipAlturaPacientes;

public class AipAlturaPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipAlturaPacientes> {

	private static final long serialVersionUID = 7050204809069117672L;

	private DetachedCriteria criarCriteriaAlturaPaciente(Integer pPacCodigo,
			DominioMomento momento) {
		DetachedCriteria criteriaAltura = DetachedCriteria.forClass(AipAlturaPacientes.class);
		criteriaAltura.add(Restrictions.eq(AipAlturaPacientes.Fields.COD_PACIENTE.toString(), pPacCodigo));
		criteriaAltura.add(Restrictions.eq(AipAlturaPacientes.Fields.MOMENTO.toString(), momento));
		return criteriaAltura;
	}
	
	public AipAlturaPacientes obterAlturasPaciente(Integer pPacCodigo, DominioMomento momento) {
		DetachedCriteria criteriaAltura = criarCriteriaAlturaPaciente(pPacCodigo, momento);
		criteriaAltura.addOrder(Order.desc(AipAlturaPacientes.Fields.CRIADO_EM.toString()));
		
		List<AipAlturaPacientes> lista = executeCriteria(criteriaAltura);
		if(lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		
		return null;
	}

	public List<AipAlturaPacientes> listarAlturarPacientesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipAlturaPacientes.class);

		criteria.add(Restrictions.eq(AipAlturaPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	/**
	 * Obtém alturaPaciente pelo número da consulta.
	 * 
	 * <br>
	 * Q_ALTURA
	 * 
	 * @param conNumero
	 * @return
	 * @author bruno.mourao
	 * @since 06/08/2012
	 */
	public AipAlturaPacientes obterAlturaPorNumeroConsulta(Integer conNumero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipAlturaPacientes.class);
		
		criteria.add(Restrictions.eq(AipAlturaPacientes.Fields.CON_NUMERO.toString(), conNumero));
		criteria.addOrder(Order.desc(AipAlturaPacientes.Fields.CRIADO_EM.toString()));
		
		List<AipAlturaPacientes> lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		
		return null;
	}
	
}