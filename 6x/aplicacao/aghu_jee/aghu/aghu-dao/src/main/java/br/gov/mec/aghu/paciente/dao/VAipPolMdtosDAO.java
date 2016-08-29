package br.gov.mec.aghu.paciente.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAipPolMdtos;
import br.gov.mec.aghu.model.VAipPolMdtosBase;

public class VAipPolMdtosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAipPolMdtos> {

	private static final long serialVersionUID = 4460627583526709971L;

	public List<VAipPolMdtos> obterMedicamentos(Integer codPaciente, String tipo, Date dataInicio, int firstResult, int maxResults) {
		DetachedCriteria cri = obterCriteria(codPaciente, tipo, dataInicio, false);

		return executeCriteria(cri, firstResult, maxResults, null, true);
	}

	public List<VAipPolMdtos> obterMedicamentos(Integer codPaciente, String tipo, Date dataInicio) {
		DetachedCriteria cri = obterCriteria(codPaciente, tipo, dataInicio, false);

		return executeCriteria(cri);
	}	
	
	public Long obterMedicamentosCount(Integer codPaciente, String tipo, Date dataInicio) {
		return executeCriteriaCount(obterCriteria(codPaciente, tipo, dataInicio, false));
	}
	
	public List<Date> obterDataMedicamentos(Integer codPaciente, String tipo, Date dataInicio) {
		DetachedCriteria cri = obterCriteria(codPaciente, tipo, dataInicio, false);
		cri.setProjection(Projections.distinct(Projections.property(VAipPolMdtosBase.Fields.DATA_INICIO.toString())));

		return executeCriteria(cri);
	}
	
	public boolean verificarExisteMedicamento(Integer codPaciente, String tipo){
		DetachedCriteria criteria = obterCriteria(codPaciente, tipo, null, true);
		
		return executeCriteriaCount(criteria) > 0;
	}

	private DetachedCriteria obterCriteria(Integer codPaciente, String tipo, Date dataInicio, boolean isCount) {
		DetachedCriteria cri = DetachedCriteria.forClass(VAipPolMdtos.class);

		cri.add(Restrictions.eq(VAipPolMdtosBase.Fields.COD_PACIENTE.toString(), codPaciente));

		if (dataInicio != null) {

			Calendar hoje = Calendar.getInstance();
			Calendar ontem = Calendar.getInstance();
			hoje.add(Calendar.DAY_OF_YEAR, +1);

			hoje.setTime(dataInicio);
			hoje.add(Calendar.DAY_OF_YEAR, +1);

			// Calendar ontem = Calendar.getInstance();
			ontem.setTime(dataInicio);

			// epi.dt_fim between TRUNC(sysdate - 1) and TRUNC(sysdate)
			cri.add(Restrictions.between(VAipPolMdtosBase.Fields.DATA_INICIO.toString(),
					DateUtils.truncate(ontem.getTime(), Calendar.DAY_OF_MONTH),
					DateUtils.truncate(hoje.getTime(), Calendar.DAY_OF_MONTH)));
		}

		if ("antibioticos".equals(tipo)) {
			cri.add(Restrictions.eq(VAipPolMdtosBase.Fields.IND_ANTIMICROBIANO.toString(), true));
		}

		if ("quimioterapicos".equals(tipo)) {
			cri.add(Restrictions.eq(VAipPolMdtosBase.Fields.IND_QUIMIOTERAPICO.toString(), true));
		}

		if ("tuberculostaticos".equals(tipo)) {
			cri.add(Restrictions.eq(VAipPolMdtosBase.Fields.IND_TUBERCULOSTATICO.toString(), true));
		}

		if (!isCount){
			cri.addOrder(Order.desc(VAipPolMdtosBase.Fields.DATA_INICIO.toString()));
		}	

		return cri;
	}

}
