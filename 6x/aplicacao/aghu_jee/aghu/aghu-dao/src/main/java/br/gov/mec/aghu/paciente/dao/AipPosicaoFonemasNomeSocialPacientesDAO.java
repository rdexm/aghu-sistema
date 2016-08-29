package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.AipFonemaNomeSocialPacientes;
import br.gov.mec.aghu.model.AipPosicaoFonemasNomeSocialPacientes;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class AipPosicaoFonemasNomeSocialPacientesDAO extends BaseDao<AipPosicaoFonemasNomeSocialPacientes> {

	private static final long serialVersionUID = 6119600698026332866L;

	public List<AipPosicaoFonemasNomeSocialPacientes> listarPosicoesFonemaPaciente(Integer codigoPaciente) {
		DetachedCriteria selectFonema = DetachedCriteria.forClass(AipFonemaNomeSocialPacientes.class);
		selectFonema.add(Restrictions.eq(AipFonemaNomeSocialPacientes.Fields.COD_PACIENTE.toString(), codigoPaciente))
			.setProjection(Property.forName(AipFonemaNomeSocialPacientes.Fields.SEQ.toString()));
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPosicaoFonemasNomeSocialPacientes.class)
				.add(Subqueries.propertyIn(AipPosicaoFonemasNomeSocialPacientes.Fields.CODIGO_PACIENTE.toString(), selectFonema));
		
		return this.executeCriteria(criteria);
		
	}
	
}