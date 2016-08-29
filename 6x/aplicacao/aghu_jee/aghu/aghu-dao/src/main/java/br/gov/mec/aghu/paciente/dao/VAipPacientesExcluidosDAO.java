package br.gov.mec.aghu.paciente.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAipPacientesExcluidos;

public class VAipPacientesExcluidosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAipPacientesExcluidos> {

	private static final long serialVersionUID = -4688863320672884146L;

	public List<Object[]> pesquisaPacientesExcluidos(Date dataInicial, Date dataFinal) {
		DetachedCriteria criteria = createPesquisaCriteria(dataInicial, dataFinal);
		return executeCriteria(criteria);
	}

	/**
	 * Cria a consulta utlizada no relatório de mesmo nome.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return List
	 */
	private DetachedCriteria createPesquisaCriteria(Date dataInicial, Date dataFinal) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VAipPacientesExcluidos.class);

		Calendar dtInicial = GregorianCalendar.getInstance();
		Calendar dtFinal = GregorianCalendar.getInstance();
		dtInicial.setTime(dataInicial);
		dtInicial.add(Calendar.DATE, -1);

		if (dataFinal != null) {
			dtFinal.setTime(dataFinal);
		}
		dtFinal.add(Calendar.DATE, +1);

		criteria.add(Restrictions.gt(VAipPacientesExcluidos.Fields.DATA_EXCLUSAO.toString(), dtInicial.getTime()));

		criteria.add(Restrictions.lt(VAipPacientesExcluidos.Fields.DATA_EXCLUSAO.toString(), dtFinal.getTime()));

		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(VAipPacientesExcluidos.Fields.PRONT_ATUAL.toString()))
				.add(Projections.property(VAipPacientesExcluidos.Fields.PRONT_EXCL.toString()))
				.add(Projections.property(VAipPacientesExcluidos.Fields.NOME.toString()))
				.add(Projections.property(VAipPacientesExcluidos.Fields.DATA_NASCIMENTO.toString()))
				.add(Projections.property(VAipPacientesExcluidos.Fields.DATA_EXCLUSAO.toString()))
				.add(Projections.property(VAipPacientesExcluidos.Fields.COD_ATUAL.toString()))));

		criteria.addOrder(Order.asc(VAipPacientesExcluidos.Fields.PRONT_EXCL.toString()));

		return criteria;

	}

}
