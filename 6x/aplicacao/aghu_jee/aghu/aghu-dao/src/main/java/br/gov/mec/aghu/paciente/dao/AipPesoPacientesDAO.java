package br.gov.mec.aghu.paciente.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipPesoPacientesId;

public class AipPesoPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPesoPacientes> {

	private static final long serialVersionUID = 1258735498913665597L;

	public AipPesoPacientes buscarPrimeiroPesosPacienteOrdenadoCriadoEm(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPesoPacientes.class);
		
		criteria.add(Restrictions.eq(AipPesoPacientes.Fields.CODIGO.toString(), pacCodigo));
		
		criteria.addOrder(Order.asc(AipPesoPacientes.Fields.CRIADO_EM.toString()));
		
		List<AipPesoPacientes> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null; 
	}
	
	public List<AipPesoPacientes> listarPesosPacientesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPesoPacientes.class);
		
		criteria.add(Restrictions.eq(AipPesoPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<AipPesoPacientes> listarPesosPacientePorCodigoPacienteOrdenadoPorCriadoEmAsc(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPesoPacientes.class);

		criteria.add(Restrictions.eq(AipPesoPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));

		criteria.addOrder(Order.asc(AipPesoPacientes.Fields.CRIADO_EM.toString()));

		return executeCriteria(criteria);
	}
	
	public Date obterMaxDataCriadoEmPesoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteriaCriadoEm = DetachedCriteria.forClass(AipPesoPacientes.class);
		criteriaCriadoEm.add(Restrictions.eq(AipPesoPacientes.Fields.COD_PACIENTE.toString(),
				codigoPaciente));
		criteriaCriadoEm
				.setProjection(Projections.max(AipPesoPacientes.Fields.CRIADO_EM.toString()));

		return (Date) executeCriteriaUniqueResult(criteriaCriadoEm);
	}

	public AipPesoPacientes obterPesoPacienteAtual(AipPacientes aipPaciente) {
		AipPesoPacientes aipPesoPacientes = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPesoPacientes.class);
		criteria.add(Restrictions.eq(AipPesoPacientes.Fields.PACIENTE.toString(), aipPaciente));
		criteria.add(Restrictions.eq(AipPesoPacientes.Fields.MOMENTO.toString(), DominioMomento.N));
		criteria.addOrder(Order.desc(AipPesoPacientes.Fields.CRIADO_EM.toString()));

		List<AipPesoPacientes> listaPesosPacientes = executeCriteria(criteria);

		if (listaPesosPacientes.size() > 0) {
			aipPesoPacientes = listaPesosPacientes.get(0);
		} else {
			aipPesoPacientes = new AipPesoPacientes();
			AipPesoPacientesId id = new AipPesoPacientesId();
			id.setPacCodigo(aipPaciente.getCodigo());
			// id.setCriadoEm(new Date());
			aipPesoPacientes.setAipPaciente(aipPaciente);
			aipPesoPacientes.setId(id);
			aipPesoPacientes.setIndMomento(DominioMomento.N);
		}

		return aipPesoPacientes;
	}

	public AipPesoPacientes obterPesoPaciente(Integer codPaciente,
			DominioMomento momento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPesoPacientes.class);
		criteria.add(Restrictions.eq(AipPesoPacientes.Fields.COD_PACIENTE.toString(), codPaciente));
		criteria.add(Restrictions.eq(AipPesoPacientes.Fields.MOMENTO.toString(), momento));
		
		List<AipPesoPacientes> listaPesosPacientes = executeCriteria(criteria);

		if (listaPesosPacientes != null && listaPesosPacientes.size() > 0) {
			return listaPesosPacientes.get(0);
		} 
		return null;
	}

	public AipPesoPacientes obterAipPesoPaciente(Integer codPaciente, DominioMomento momento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPesoPacientes.class);
		criteria.add(Restrictions.eq(AipPesoPacientes.Fields.MOMENTO.toString(), momento));
		criteria.add(Restrictions.eq(AipPesoPacientes.Fields.COD_PACIENTE.toString(), codPaciente));
		
		List<AipPesoPacientes> listaPesoPacientes = executeCriteria(criteria);
	
		if (listaPesoPacientes != null && listaPesoPacientes.size() > 0) {
			return listaPesoPacientes.get(0);
		}
		
		return null;
	}
	
	public AipPesoPacientes obterPesoPacientesPorNumeroConsulta(Integer conNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPesoPacientes.class);
		
		criteria.add(Restrictions.eq(AipPesoPacientes.Fields.CON_NUMERO.toString(), conNumero));
		criteria.addOrder(Order.desc(AipPesoPacientes.Fields.CRIADO_EM.toString()));
		
		List<AipPesoPacientes> listaPesosPacientes = executeCriteria(criteria);

		if (listaPesosPacientes != null && listaPesosPacientes.size() > 0) {
			return listaPesosPacientes.get(0);
		} 
		return null;
	}
	
	
	/**
	 * #27482 - C3
	 * @param pacCodigo
	 * @return
	 */
	public BigDecimal obterPesoPacientesPorCodigoPaciente(Integer pacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPesoPacientes.class);
		criteria.add(Restrictions.eq(AipPesoPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.or(Restrictions.eq(AipPesoPacientes.Fields.MOMENTO.toString(), DominioMomento.N), Restrictions.isNull(AipPesoPacientes.Fields.MOMENTO.toString())));

		List<AipPesoPacientes> listaPesosPacientes = executeCriteria(criteria);

		if (listaPesosPacientes != null && listaPesosPacientes.size() > 0) {
			return listaPesosPacientes.get(0).getPeso();
		} 
		return null;
	}
}
