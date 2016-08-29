package br.gov.mec.aghu.paciente.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAipPolMdtosAghuHist;
import br.gov.mec.aghu.model.VAipPolMdtosBase;

public class VAipPolMdtosAghuHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAipPolMdtosAghuHist> {

	private static final long serialVersionUID = 5753794185746370915L;
	private static final String  ANTIBIOTICOS = "antibioticos";
	private static final String QUIMIOTERAPICOS = "quimioterapicos";
	private static final String TUBERCULOSTATICOS = "tuberculostaticos";

	public List<VAipPolMdtosAghuHist> obterMedicamentosHist(Integer codPaciente, String tipo, Date dataInicio) {
		DetachedCriteria cri = obterCriteria(codPaciente, tipo, dataInicio);
		
		cri.addOrder(Order.desc(VAipPolMdtosBase.Fields.DATA_INICIO.toString()));
		return executeCriteria(cri);
	}

	public Long obterMedicamentosHistCount(Integer codPaciente, String tipo) {
		DetachedCriteria cri = obterCriteria(codPaciente, tipo, null);
		return executeCriteriaCount(cri);
	}

	private DetachedCriteria obterCriteria(Integer codPaciente, String tipo, Date dataInicio) {
		DetachedCriteria cri = DetachedCriteria.forClass(VAipPolMdtosAghuHist.class);

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

		if (ANTIBIOTICOS.equals(tipo)) {
			cri.add(Restrictions.eq(VAipPolMdtosBase.Fields.IND_ANTIMICROBIANO.toString(), Boolean.TRUE));
		}

		if (QUIMIOTERAPICOS.equals(tipo)) {
			cri.add(Restrictions.eq(VAipPolMdtosBase.Fields.IND_QUIMIOTERAPICO.toString(), Boolean.TRUE));
		}

		if (TUBERCULOSTATICOS.equals(tipo)) {
			cri.add(Restrictions.eq(VAipPolMdtosBase.Fields.IND_TUBERCULOSTATICO.toString(), Boolean.TRUE));
		}

		return cri;
	}

}
