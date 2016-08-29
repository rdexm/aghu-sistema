package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;

import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoHistoricoSuspensForn;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoHistoricoSuspensFornDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoHistoricoSuspensForn> {
	
	private static final long serialVersionUID = 6287593763698487601L;
	
	private DetachedCriteria criarCriteriaHistoricoSuspensForn(
			final Object pesquisa) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoHistoricoSuspensForn.class);
		
		criteria.add(Restrictions.eq(ScoHistoricoSuspensForn.Fields.FRN_NUMERO.toString(),
				pesquisa));
		
		
		return criteria;
	}
	
	public Integer obterNumeroMaximoPeloFornecedor(Integer frnNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoHistoricoSuspensForn.class);
		criteria.add(Restrictions.eq(ScoHistoricoSuspensForn.Fields.FRN_NUMERO.toString(), frnNumero));
		criteria.setProjection(Projections.max(ScoHistoricoSuspensForn.Fields.NUMERO.toString()));
		
		Object numero = executeCriteriaUniqueResult(criteria);
		
		return numero == null ? 1 : (Integer)numero + 1;
	}

	public Boolean isSobreposicaoSuspensao(Integer numero, Integer processo, Date inicio, Date fim) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoHistoricoSuspensForn.class);
		criteria.add(Restrictions.eq(ScoHistoricoSuspensForn.Fields.FRN_NUMERO.toString(),numero));
		if(processo != null) {
			criteria.add(Restrictions.ne(ScoHistoricoSuspensForn.Fields.NUMERO.toString(),processo));
		}
		
		Disjunction or = Restrictions.disjunction();
		or.add(Restrictions.and(
				Restrictions.isNotNull(ScoHistoricoSuspensForn.Fields.DT_FIM.toString()),
				Restrictions.sqlRestriction("? BETWEEN {alias}.DT_INICIO and {alias}.DT_FIM", DateUtil.truncaData(inicio), DateType.INSTANCE)
		));
		
		if(fim != null) {
			or.add(Restrictions.and(
					Restrictions.isNotNull(ScoHistoricoSuspensForn.Fields.DT_FIM.toString()),
					Restrictions.sqlRestriction("? BETWEEN {alias}.DT_INICIO and {alias}.DT_FIM", DateUtil.truncaData(fim), DateType.INSTANCE)
			));
			
			or.add(Restrictions.and(
					Restrictions.isNotNull(ScoHistoricoSuspensForn.Fields.DT_FIM.toString()),
					Restrictions.and(Restrictions.gt(ScoHistoricoSuspensForn.Fields.DT_INICIO.toString(), DateUtil.truncaData(inicio)),
							Restrictions.lt(ScoHistoricoSuspensForn.Fields.DT_FIM.toString(), DateUtil.truncaData(fim)							
					))
			));
			
			or.add(Restrictions.and(
					Restrictions.isNull(ScoHistoricoSuspensForn.Fields.DT_FIM.toString()),
					Restrictions.le(ScoHistoricoSuspensForn.Fields.DT_INICIO.toString(), DateUtil.truncaData(fim))
			));
		}
	
		if(fim == null) {
			or.add(Restrictions.isNotNull(ScoHistoricoSuspensForn.Fields.DT_FIM.toString()));
		}
		
		criteria.add(or);
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public List<ScoHistoricoSuspensForn> listarSuspensoesPorFornecedorOuPeriodo(Integer numero, Date inicio, Date fim) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoHistoricoSuspensForn.class, "scoHistoricoSuspensForn");
		criteria.createAlias("scoHistoricoSuspensForn."+ScoHistoricoSuspensForn.Fields.SCO_FORNECEDOR.toString(), "scoFornecedor");
		
		if(numero != null) {
			criteria.add(Restrictions.eq("scoHistoricoSuspensForn."+ScoHistoricoSuspensForn.Fields.FRN_NUMERO.toString(),numero));
		}
		
		if(inicio != null && fim != null) {
			criteria.add(
				Restrictions.or(
					Restrictions.between("scoHistoricoSuspensForn."+ScoHistoricoSuspensForn.Fields.DT_INICIO.toString(), DateUtil.truncaData(inicio), DateUtil.truncaData(fim)),
					Restrictions.or(
							Restrictions.and(
									Restrictions.isNotNull("scoHistoricoSuspensForn."+ScoHistoricoSuspensForn.Fields.DT_FIM.toString()),
									Restrictions.between("scoHistoricoSuspensForn."+ScoHistoricoSuspensForn.Fields.DT_FIM.toString(), DateUtil.truncaData(inicio), DateUtil.truncaData(fim))
							), 
							Restrictions.and(
									Restrictions.isNull("scoHistoricoSuspensForn."+ScoHistoricoSuspensForn.Fields.DT_FIM.toString()),
									Restrictions.eq("scoHistoricoSuspensForn."+ScoHistoricoSuspensForn.Fields.DT_INICIO.toString(), DateUtil.truncaData(inicio))
									))));
		}
		
//		criteria.addOrder(Order.desc(ScoHistoricoSuspensForn.Fields.FRN_NUMERO.toString()));
		criteria.addOrder(Order.asc("scoFornecedor."+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
		criteria.addOrder(Order.asc("scoHistoricoSuspensForn."+ScoHistoricoSuspensForn.Fields.NUMERO.toString()));
		
		return executeCriteria(criteria);
	}
	

	public Long listarHistoricoSuspensFornCount(final Object pesquisa) {
		return executeCriteriaCount(criarCriteriaHistoricoSuspensForn(pesquisa));
	}
	
}
