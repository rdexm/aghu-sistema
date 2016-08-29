package br.gov.mec.aghu.estoque.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceConversaoUnidMateriais;

public class SceConversaoUnidMateriaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceConversaoUnidMateriais> {

	private static final long serialVersionUID = -9117628270334276911L;

	public List<SceConversaoUnidMateriais> pesquisarConversaoUnidadesPorMatDtGeracao (Integer matCodigo, Date dtGeracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceConversaoUnidMateriais.class);
		
		criteria.add(Restrictions.eq(SceConversaoUnidMateriais.Fields.ID_MAT_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.lt(SceConversaoUnidMateriais.Fields.DT_GERACAO.toString(), dtGeracao));
		criteria.addOrder(Order.desc(SceConversaoUnidMateriais.Fields.DT_GERACAO.toString()));
		
		return executeCriteria(criteria);
	}

}