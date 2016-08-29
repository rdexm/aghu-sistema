package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.ScoHistoricoAdvertForn;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoHistoricoAdvertFornDAO extends  br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoHistoricoAdvertForn> {

	private static final long serialVersionUID = 1480264037600679141L;
	
	private DetachedCriteria criarCriteriaHistoricoAdvertForn(
			final Object pesquisa) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoHistoricoAdvertForn.class);
		
		criteria.add(Restrictions.eq(ScoHistoricoAdvertForn.Fields.FRN_NUMERO.toString(),
				pesquisa));
		
		return criteria;
	}
	
	public Short obterNumeroMaximoPeloFornecedor(Integer frnNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoHistoricoAdvertForn.class);
		criteria.add(Restrictions.eq(ScoHistoricoAdvertForn.Fields.FRN_NUMERO.toString(), frnNumero));
		criteria.setProjection(Projections.max(ScoHistoricoAdvertForn.Fields.NUMERO.toString()));
		
		Object numero = executeCriteriaUniqueResult(criteria);
		
		return numero == null ? Short.valueOf("1") : NumberUtil.obterShort(((Short)numero).intValue()+1);
	}

	public List<ScoHistoricoAdvertForn> listarAdvertenciasPorFornecedorOuPeriodo(Integer numero, Date inicio, Date fim) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoHistoricoAdvertForn.class, "sha");
		criteria.createAlias("sha."+ScoHistoricoAdvertForn.Fields.SCO_FORNECEDOR.toString(), "scf", JoinType.LEFT_OUTER_JOIN);
		
		if(numero != null) {
			criteria.add(Restrictions.eq("sha."+ScoHistoricoAdvertForn.Fields.FRN_NUMERO.toString(),numero));
		}
		
		if(inicio != null && fim != null) {
			criteria.add(Restrictions.between("sha."+ScoHistoricoAdvertForn.Fields.DT_EMISSAO.toString(), DateUtil.truncaData(inicio), DateUtil.truncaData(fim)));
		}
		
		criteria.addOrder(Order.desc("sha."+ScoHistoricoAdvertForn.Fields.FRN_NUMERO.toString()));
		criteria.addOrder(Order.asc("sha."+ScoHistoricoAdvertForn.Fields.NUMERO.toString()));
		
		return executeCriteria(criteria);
	}
	

	public Long listarHistoricoAdvertFornCount(final Object pesquisa) {
		return executeCriteriaCount(criarCriteriaHistoricoAdvertForn(pesquisa));
	}
}