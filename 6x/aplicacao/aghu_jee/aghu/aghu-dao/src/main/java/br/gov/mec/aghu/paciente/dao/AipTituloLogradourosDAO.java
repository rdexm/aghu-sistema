package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipTituloLogradouros;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AipTituloLogradourosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipTituloLogradouros> {
	
	private static final long serialVersionUID = -3084278736393452859L;

	public List<AipTituloLogradouros> pesquisarTituloLogradouro(
			Integer firstResult, Integer maxResult, Integer codigo,
			String descricao) {
		return executeCriteria(createCriteriaAipTituloLogradouros(codigo,
				descricao, true), firstResult, maxResult, null, true);
	}
	
	public Long obterTituloLogradouroCount(Integer codigo, String descricao) {
		return executeCriteriaCount(createCriteriaAipTituloLogradouros(codigo,
				descricao, false));
	}

	private DetachedCriteria createCriteriaAipTituloLogradouros(Integer codigo,
			String descricao, boolean order) {
		DetachedCriteria cri = DetachedCriteria
				.forClass(AipTituloLogradouros.class);

		if (codigo != null) {
			cri.add(Restrictions.eq("codigo", codigo));
		}

		if (descricao != null && !descricao.trim().equals("")) {
			cri.add(Restrictions.ilike("descricao", descricao,
					MatchMode.ANYWHERE));
		}
		if (order) {
			cri.addOrder(Order.asc("descricao"));
		}
		
		return cri;
	}
	
	public List<AipTituloLogradouros> pesquisarTituloLogradouro(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;
		
		if (StringUtils.isNotBlank(strPesquisa)
				&& CoreUtil.isNumeroInteger(strPesquisa)) {
			DetachedCriteria _criteria = DetachedCriteria
					.forClass(AipTituloLogradouros.class);

			_criteria.add(Restrictions.eq(
					AipTituloLogradouros.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));
			
			List<AipTituloLogradouros> list = executeCriteria(_criteria, 0, 100, null, false);

			if (list.size() > 0) {
				return list;
			}
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipTituloLogradouros.class);
				
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					AipTituloLogradouros.Fields.DESCRICAO.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(AipTituloLogradouros.Fields.DESCRICAO
				.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}

}
