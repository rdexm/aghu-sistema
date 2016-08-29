package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipFonemas;
import br.gov.mec.aghu.model.AipFonemasMaePaciente;

public class AipFonemasMaePacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipFonemasMaePaciente> {

	private static final long serialVersionUID = 7159992717457922137L;

	public AipFonemasMaePaciente obterAipFonemasMaePaciente(Integer codigoPaciente, String fonema) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipFonemasMaePaciente.class);
		
		criteria.add(Restrictions.eq(AipFonemasMaePaciente.Fields.FON_FONEMA.toString(), fonema));
		criteria.add(Restrictions.eq(AipFonemasMaePaciente.Fields.COD_PACIENTE.toString(), codigoPaciente));

		return (AipFonemasMaePaciente) executeCriteriaUniqueResult(criteria);
	}

	public AipFonemasMaePaciente obterAipFonemaMaePaciente(Integer codigoPaciente, AipFonemas aipFonema){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipFonemasMaePaciente.class);
		criteria.add(Restrictions.eq(AipFonemasMaePaciente.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(AipFonemasMaePaciente.Fields.FONEMA.toString(), aipFonema));
		AipFonemasMaePaciente aipFonemaMaePaciente = (AipFonemasMaePaciente) this.executeCriteriaUniqueResult(criteria);
		return aipFonemaMaePaciente;
	}
	
	public List<AipFonemasMaePaciente> pesquisarFonemasMaePaciente(Integer codigoPaciente){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipFonemasMaePaciente.class);
		criteria.setFetchMode(AipFonemasMaePaciente.Fields.FONEMA.toString(),
				FetchMode.JOIN);
		criteria.setFetchMode(AipFonemasMaePaciente.Fields.PACIENTE.toString(),
				FetchMode.JOIN);
		
		if (codigoPaciente != null) {
			criteria.add(Restrictions.eq(AipFonemasMaePaciente.Fields.COD_PACIENTE.toString(), codigoPaciente));
		}
		
		return this.executeCriteria(criteria);
	}
	
}
