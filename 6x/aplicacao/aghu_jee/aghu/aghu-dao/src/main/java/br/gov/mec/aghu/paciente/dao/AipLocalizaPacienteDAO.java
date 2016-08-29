package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipLocalizaPaciente;

public class AipLocalizaPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipLocalizaPaciente> {

	
	
	private static final long serialVersionUID = -8150986858332063693L;

	public List<AipLocalizaPaciente> listarPacientesPorAtendimento(Integer numeroConsulta, Integer pacCodigo) {
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghAtendimentos.class);
		subCriteria.createCriteria(AghAtendimentos.Fields.LOCALIZA_PACIENTES.toString(), Criteria.INNER_JOIN);
		subCriteria.setProjection(Property.forName(AghAtendimentos.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq(AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), numeroConsulta));
				
		DetachedCriteria criteria = DetachedCriteria.forClass(AipLocalizaPaciente.class);
		criteria.add(Restrictions.eq(AipLocalizaPaciente.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Subqueries.exists(subCriteria));
		
		return executeCriteria(criteria);
	}
	
}
