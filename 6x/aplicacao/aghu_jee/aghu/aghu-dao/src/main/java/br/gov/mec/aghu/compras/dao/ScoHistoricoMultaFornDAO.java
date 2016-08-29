package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoHistoricoMultaForn;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoHistoricoMultaFornDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoHistoricoMultaForn> {

	private static final long serialVersionUID = -28577565572137299L;
	
	private DetachedCriteria criarCriteriaHistoricoMultaForn(
			final Object pesquisa) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoHistoricoMultaForn.class);
		
		criteria.add(Restrictions.eq(ScoHistoricoMultaForn.Fields.FRN_NUMERO.toString(),
				pesquisa));
		return criteria;
	}
	
	public Short obterNumeroMaximoPeloFornecedor(Integer frnNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoHistoricoMultaForn.class);
		criteria.add(Restrictions.eq(ScoHistoricoMultaForn.Fields.FRN_NUMERO.toString(), frnNumero));
		criteria.setProjection(Projections.max(ScoHistoricoMultaForn.Fields.NUMERO.toString()));
		
		Object numero = executeCriteriaUniqueResult(criteria);
		
		return numero == null ? Short.valueOf("1") : NumberUtil.obterShort(((Short)numero).intValue()+1);
	}
	
	public List<ScoHistoricoMultaForn> listarMultasPorFornecedorOuPeriodo(Integer numero, Date inicio, Date fim) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoHistoricoMultaForn.class, "scoHistoricoMultaForn");
		criteria.createAlias("scoHistoricoMultaForn."+ScoHistoricoMultaForn.Fields.SCO_FORNECEDOR.toString(), "scoFornecedor");
		
		if(numero != null) {
			criteria.add(Restrictions.eq("scoHistoricoMultaForn."+ScoHistoricoMultaForn.Fields.FRN_NUMERO.toString(),numero));
		}
		
		if(inicio != null && fim != null) {
			criteria.add(Restrictions.between("scoHistoricoMultaForn."+ScoHistoricoMultaForn.Fields.DT_EMISSAO.toString(), DateUtil.truncaData(inicio), DateUtil.truncaData(fim)));
		}
		
//		criteria.addOrder(Order.desc(ScoHistoricoMultaForn.Fields.FRN_NUMERO.toString()));
		criteria.addOrder(Order.asc("scoFornecedor."+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
		criteria.addOrder(Order.asc("scoHistoricoMultaForn."+ScoHistoricoMultaForn.Fields.NUMERO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Long listarHistoricoMultaFornCount(final Object pesquisa) {
		return executeCriteriaCount(criarCriteriaHistoricoMultaForn(pesquisa));
	}
}
