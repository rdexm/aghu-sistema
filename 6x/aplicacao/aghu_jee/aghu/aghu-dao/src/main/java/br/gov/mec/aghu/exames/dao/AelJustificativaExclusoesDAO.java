package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AelJustificativaExclusoes;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.AipPacientes;

public class AelJustificativaExclusoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelJustificativaExclusoes> {

	private static final long serialVersionUID = -6612769444606139021L;

	public List<AelProjetoPacientes> pesquisarProjetosDePesquisaPorPaciente(AipPacientes paciente) {
		DetachedCriteria criteria = getCriteriaPesquisarProjetosDePesquisaPorPaciente(paciente);

		criteria.addOrder(Order.desc("proj." + AelProjetoPacientes.Fields.DT_INICIO.toString()));

		List<AelProjetoPacientes> listaVolta = executeCriteria(criteria);

		return listaVolta;
	}
	
	public Long pesquisarProjetosDePesquisaPorPacienteCount(AipPacientes paciente) {
		DetachedCriteria criteria = getCriteriaPesquisarProjetosDePesquisaPorPaciente(paciente);

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria getCriteriaPesquisarProjetosDePesquisaPorPaciente(
			AipPacientes paciente) {
		DetachedCriteria subquery = DetachedCriteria.forClass(AelJustificativaExclusoes.class,
				"jex");
		subquery.add(Restrictions.eqProperty(
				"jex." + AelJustificativaExclusoes.Fields.SEQ.toString(), "proj."
						+ AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO_SEQ.toString()));
		subquery.add(Restrictions.eq(
				"jex." + AelJustificativaExclusoes.Fields.IND_MOSTRA_TELAS.toString(),
				DominioSimNao.S));
		subquery.setProjection(Projections.property("jex."
				+ AelJustificativaExclusoes.Fields.SEQ.toString()));

		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoPacientes.class, "proj");
		criteria.createAlias(AelProjetoPacientes.Fields.PROJETO_PESQUISA.toString(), "projPes");
		criteria.add(Restrictions.eq("proj." + AelProjetoPacientes.Fields.PAC_CODIGO.toString(),
				paciente.getCodigo()));
		criteria.add(Restrictions.or(
				Restrictions.isNull("proj." + AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO_SEQ.toString()),
				Property.forName("proj." + AelProjetoPacientes.Fields.JUSTIFICATIVA_EXCLUSAO_SEQ.toString()).in(
						subquery)));
		return criteria;
	}

}
