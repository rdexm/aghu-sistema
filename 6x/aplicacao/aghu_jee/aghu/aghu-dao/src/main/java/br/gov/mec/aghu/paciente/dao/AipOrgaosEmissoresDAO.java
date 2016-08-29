package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipOrgaosEmissores;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AipOrgaosEmissoresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipOrgaosEmissores> {

	private static final long serialVersionUID = -5102809388536669948L;

	public Long obterCountOrgaoEmissorPorCodigoDescricao(Object paramPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipOrgaosEmissores.class);
		String strPesquisa = (String) paramPesquisa;
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroShort(strPesquisa)) {

				Short shortPesquisa = Short.valueOf(strPesquisa);
				cri.add(Restrictions.eq(AipOrgaosEmissores.Fields.CODIGO.toString(), (shortPesquisa)));
			} else {
				cri.add(Restrictions.ilike(AipOrgaosEmissores.Fields.DESCRICAO.toString(), (strPesquisa).toUpperCase(),
						MatchMode.ANYWHERE));
			}

		}

		return this.executeCriteriaCount(cri);
	}

	public List<AipOrgaosEmissores> pesquisarOrgaoEmissorPorCodigoDescricao(Object paramPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipOrgaosEmissores.class);
		String strPesquisa = (String) paramPesquisa;
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroShort(strPesquisa)) {

				Short shortPesquisa = Short.valueOf(strPesquisa);
				cri.add(Restrictions.eq(AipOrgaosEmissores.Fields.CODIGO.toString(), (shortPesquisa)));
			} else {
				cri.add(Restrictions.ilike(AipOrgaosEmissores.Fields.DESCRICAO.toString(), (strPesquisa).toUpperCase(),
						MatchMode.ANYWHERE));
			}

		}

		cri.addOrder(Order.asc(AipOrgaosEmissores.Fields.CODIGO.toString()));
		
		return executeCriteria(cri);
	}
	
}
