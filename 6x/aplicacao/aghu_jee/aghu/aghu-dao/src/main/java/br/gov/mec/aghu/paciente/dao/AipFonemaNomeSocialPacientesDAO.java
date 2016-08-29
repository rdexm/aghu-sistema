package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipFonemaNomeSocialPacientes;
import br.gov.mec.aghu.model.AipFonemas;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class AipFonemaNomeSocialPacientesDAO extends BaseDao<AipFonemaNomeSocialPacientes> {

	private static final long serialVersionUID = 5777127629077962982L;

	public AipFonemaNomeSocialPacientes obterAipFonemaNomeSocialPacientes(Integer codigoPaciente, String fonema) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipFonemaNomeSocialPacientes.class);

		criteria.add(Restrictions.eq(AipFonemaNomeSocialPacientes.Fields.FON_FONEMA.toString(), fonema));
		criteria.add(Restrictions.eq(AipFonemaNomeSocialPacientes.Fields.COD_PACIENTE.toString(), codigoPaciente));

		return (AipFonemaNomeSocialPacientes) executeCriteriaUniqueResult(criteria);
	}
	
	public AipFonemaNomeSocialPacientes obterAipFonemaNomeSocialPacientes(Integer codigoPaciente, AipFonemas aipFonema) {
		return obterAipFonemaNomeSocialPacientes(codigoPaciente, aipFonema.getFonema());
	}
	
	public List<AipFonemaNomeSocialPacientes> pesquisarFonemasNomeSocialPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipFonemaNomeSocialPacientes.class);
		criteria.setFetchMode(AipFonemaNomeSocialPacientes.Fields.FONEMA.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AipFonemaNomeSocialPacientes.Fields.PACIENTE.toString(), FetchMode.JOIN);

		if (codigoPaciente != null) {
			criteria.add(Restrictions.eq(AipFonemaNomeSocialPacientes.Fields.COD_PACIENTE.toString(), codigoPaciente));
		}
		
		return this.executeCriteria(criteria);
	}
	
}