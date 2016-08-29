package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtViaAereas;

public class PdtViaAereasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtViaAereas> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4608631097780316720L;

	public List<PdtViaAereas> obterPdtViaAereasAtivasOrdenadas() {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtViaAereas.class);
		criteria.add(Restrictions.eq(PdtViaAereas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(PdtViaAereas.Fields.ORDEM.toString()));
		return executeCriteria(criteria);
	}
	
	public List<PdtViaAereas> obterPdtViaAereasAtivasOrdemDescricao() {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtViaAereas.class);
		criteria.add(Restrictions.eq(PdtViaAereas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.desc(PdtViaAereas.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
		
}
