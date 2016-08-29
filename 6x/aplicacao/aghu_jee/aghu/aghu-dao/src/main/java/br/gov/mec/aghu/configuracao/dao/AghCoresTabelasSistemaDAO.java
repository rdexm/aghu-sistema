package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghCoresTabelasSistema;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AghCoresTabelasSistemaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghCoresTabelasSistema> {

	
	private static final long serialVersionUID = -7431915735737979965L;

	public List<AghCoresTabelasSistema> pesquisarCoresTabelasSistema(Object param){
		String strPesquisa = (String) param;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCoresTabelasSistema.class);
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroInteger(strPesquisa)){
				criteria.add(Restrictions.eq(AghCoresTabelasSistema.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
			}
			else{
				criteria.add(Restrictions.ilike(AghCoresTabelasSistema.Fields.COR.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		return executeCriteria(criteria);
	}

}
