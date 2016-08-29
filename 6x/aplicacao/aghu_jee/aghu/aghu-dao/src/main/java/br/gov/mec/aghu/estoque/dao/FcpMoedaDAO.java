package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FcpMoeda;

public class FcpMoedaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FcpMoeda> {

	private static final long serialVersionUID = 8078848395499456068L;

	public List<FcpMoeda> pesquisarMoeda(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpMoeda.class);
		String strPesquisa = (String) parametro;

		if (StringUtils.isNotBlank(strPesquisa)) {
			
			criteria.add(Restrictions.ilike(FcpMoeda.Fields.DESCRICAO.toString(),
					StringUtils.trim(strPesquisa), MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.asc(FcpMoeda.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
}
