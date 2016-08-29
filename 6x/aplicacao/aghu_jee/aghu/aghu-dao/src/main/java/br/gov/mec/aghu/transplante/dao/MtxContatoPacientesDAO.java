package br.gov.mec.aghu.transplante.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MtxContatoPacientes;

public class MtxContatoPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxContatoPacientes> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1880532176554515214L;

	/**
	 * Obtem lista de Contato de Paciente.
	 * 
	 * @param pacCodigo Código de Paciente
	 * @return Lista de Contatos do Paciente
	 */
	public List<MtxContatoPacientes> obterListaContatoPacientesPorCodigoPaciente(Integer pacCodigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxContatoPacientes.class);
		
		criteria.add(Restrictions.eq(MtxContatoPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));
		
		return executeCriteria(criteria);
	}
}
