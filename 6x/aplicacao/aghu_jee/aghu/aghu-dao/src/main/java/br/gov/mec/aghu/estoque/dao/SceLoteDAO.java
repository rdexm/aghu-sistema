package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.SceLote;
import br.gov.mec.aghu.model.ScoMarcaComercial;

public class SceLoteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceLote> {
	
	private static final long serialVersionUID = 1297382069090350237L;

	public List<SceLote> listarLotesPorCodigoOuMarcaComercialEMaterial(Object objPesquisa, Integer codigoMaterial){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLote.class);
		criteria.createAlias(SceLote.Fields.MARCA_COMERCIAL.toString(), "mcm", JoinType.LEFT_OUTER_JOIN);

		if(objPesquisa!=null && !objPesquisa.toString().trim().equals("")){
			Criterion rest1 = Restrictions.eq(SceLote.Fields.CODIGO.toString(), objPesquisa.toString());
			Criterion rest2 = Restrictions.ilike("mcm."+ScoMarcaComercial.Fields.DESCRICAO.toString(), objPesquisa.toString(), MatchMode.ANYWHERE);
	
			criteria.add(Restrictions.or(rest1, rest2));
		}

		criteria.add(Restrictions.eq(SceLote.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.addOrder(Order.asc("mcm."+ScoMarcaComercial.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
}
