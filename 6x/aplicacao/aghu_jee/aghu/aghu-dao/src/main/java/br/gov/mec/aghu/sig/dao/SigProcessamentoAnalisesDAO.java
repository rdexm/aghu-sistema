package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SigProcessamentoAlertas;
import br.gov.mec.aghu.model.SigProcessamentoAnalises;
import br.gov.mec.aghu.model.SigProcessamentoCusto;

public class SigProcessamentoAnalisesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigProcessamentoAnalises> {

	private static final long serialVersionUID = 7762139326955620318L;

	public Long buscarTotalAnalisesSemParecer(SigProcessamentoCusto processamentoCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigProcessamentoAnalises.class);
		criteria.setProjection(Projections.distinct(Projections.property(SigProcessamentoAlertas.Fields.CENTRO_CUSTO.toString())));		
		criteria.add(Restrictions.eq(SigProcessamentoAnalises.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));
		criteria.add(Restrictions.isNull(SigProcessamentoAnalises.Fields.PARECER.toString()));
		List<Object> list = this.executeCriteria(criteria);
		return (list != null) ? (long)list.size() : 0L;
	}

}
