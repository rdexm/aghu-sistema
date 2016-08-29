package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipFonemaPacientes;
import br.gov.mec.aghu.model.AipFonemas;

public class AipFonemaPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipFonemaPacientes> {

	private static final long serialVersionUID = 5777127629077962982L;

	public AipFonemaPacientes obterAipFonemaPacientes(Integer codigoPaciente, String fonema) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipFonemaPacientes.class);

		criteria.add(Restrictions.eq(AipFonemaPacientes.Fields.FON_FONEMA.toString(), fonema));
		criteria.add(Restrictions.eq(AipFonemaPacientes.Fields.COD_PACIENTE.toString(), codigoPaciente));

		return (AipFonemaPacientes) executeCriteriaUniqueResult(criteria);
	}
	
	public AipFonemaPacientes obterAipFonemaPaciente(Integer codigoPaciente, AipFonemas aipFonema){
		return obterAipFonemaPacientes(codigoPaciente, aipFonema.getFonema());
	}
	
	public List<AipFonemaPacientes> pesquisarFonemasPaciente(Integer codigoPaciente){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipFonemaPacientes.class);
		criteria.setFetchMode(AipFonemaPacientes.Fields.FONEMA.toString(),
				FetchMode.JOIN);
		criteria.setFetchMode(AipFonemaPacientes.Fields.PACIENTE.toString(),
				FetchMode.JOIN);

		if (codigoPaciente != null) {
			criteria.add(Restrictions.eq(AipFonemaPacientes.Fields.COD_PACIENTE.toString(), codigoPaciente));
		}
		
		return this.executeCriteria(criteria);
	}
	
}
