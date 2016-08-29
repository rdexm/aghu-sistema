package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AacCidConsultas;

public class AacCidConsultasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacCidConsultas> {

	private static final long serialVersionUID = -4559767951659089333L;

	public List<AacCidConsultas> obterCidConsultaPorNumeroConsulta(Integer consultaNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacCidConsultas.class);

		criteria.add(Restrictions.eq(AacCidConsultas.Fields.CON_NUMERO.toString(), consultaNumero));

		return executeCriteria(criteria);
	}

}
