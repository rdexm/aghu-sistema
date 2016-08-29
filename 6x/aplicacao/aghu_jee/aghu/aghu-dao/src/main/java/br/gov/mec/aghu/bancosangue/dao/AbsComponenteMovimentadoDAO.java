package br.gov.mec.aghu.bancosangue.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AbsComponenteMovimentado;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedor;

public class AbsComponenteMovimentadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsComponenteMovimentado> {
	
	private static final long serialVersionUID = -6592796368245019148L;

	public Boolean existeComponentePesoFornecedor(AbsComponentePesoFornecedor absComponentePesoFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponenteMovimentado.class);
		criteria.add(Restrictions.eq(AbsComponenteMovimentado.Fields.COMPONENTE_PESO_FORNECEDOR.toString(),absComponentePesoFornecedor));
		return executeCriteriaExists(criteria);
	}
	
}
