package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoJN;
import br.gov.mec.aghu.core.model.BaseJournal;

public class AfaViaAdministracaoMedicamentoJNDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaViaAdministracaoMedicamentoJN> {

	private static final long serialVersionUID = -189577707913136732L;

	public Long pesquisarViasAdministracaoJnCount(AfaMedicamento medicamento) {
		DetachedCriteria cri = obterCriteriaPesquisarViasAdministracaoJn(medicamento);
		return executeCriteriaCount(cri);
	}

	private DetachedCriteria obterCriteriaPesquisarViasAdministracaoJn(
			AfaMedicamento medicamento) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaViaAdministracaoMedicamentoJN.class);
		cri.add(Restrictions.eq(AfaViaAdministracaoMedicamentoJN.Fields.MED_MAT_CODIGO.toString(), medicamento.getMatCodigo()));
		return cri;
	}

	public List<AfaViaAdministracaoMedicamentoJN> pesquisarViasAdministracaoJn(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaMedicamento medicamento) {
		DetachedCriteria cri = obterCriteriaPesquisarViasAdministracaoJn(medicamento);
		
		cri.addOrder(Order.desc(BaseJournal.Fields.DATA_ALTERACAO.toString()));
		
		return executeCriteria(cri, firstResult, maxResult, orderProperty, asc);
	}

}
