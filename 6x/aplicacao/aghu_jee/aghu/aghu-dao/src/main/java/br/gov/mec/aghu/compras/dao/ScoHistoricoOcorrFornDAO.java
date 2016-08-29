package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoHistoricoOcorrForn;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoHistoricoOcorrFornDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoHistoricoOcorrForn> {

	private static final long serialVersionUID = 401662598934103837L;
	
	private DetachedCriteria criarCriteriaHistoricoOcorrForn(
			final Object pesquisa) {
		final DetachedCriteria criteria = DetachedCriteria
		.forClass(ScoHistoricoOcorrForn.class);
		
		criteria.add(Restrictions.eq(ScoHistoricoOcorrForn.Fields.FRN_NUMERO.toString(),
				pesquisa));
		
		return criteria;
	}
	
	public Short obterNumeroMaximoPeloFornecedor(Integer frnNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoHistoricoOcorrForn.class);
		criteria.add(Restrictions.eq(ScoHistoricoOcorrForn.Fields.FRN_NUMERO.toString(), frnNumero));
		criteria.setProjection(Projections.max(ScoHistoricoOcorrForn.Fields.NUMERO.toString()));
		
		Object numero = executeCriteriaUniqueResult(criteria);
		
		return numero == null ? Short.valueOf("1") : NumberUtil.obterShort(((Short)numero).intValue()+1);
	}

	public List<ScoHistoricoOcorrForn> listarOcorrenciasPorFornecedorOuPeriodo(Integer numero, Date inicio, Date fim) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoHistoricoOcorrForn.class, "scoHistoricoOcorrForn");
		criteria.createAlias("scoHistoricoOcorrForn."+ScoHistoricoOcorrForn.Fields.SCO_FORNECEDOR.toString(), "scoFornecedor");
		criteria.createAlias("scoHistoricoOcorrForn."+ScoHistoricoOcorrForn.Fields.SCO_TIPOS_OCORR_FORN.toString(), "tpOcorrenciaForn");
		
		if(numero != null) {
			criteria.add(Restrictions.eq("scoHistoricoOcorrForn."+ScoHistoricoOcorrForn.Fields.FRN_NUMERO.toString(),numero));
		}
		
		if(inicio != null && fim != null) {
			criteria.add(Restrictions.between("scoHistoricoOcorrForn."+ScoHistoricoOcorrForn.Fields.DT_EMISSAO.toString(), DateUtil.truncaData(inicio), DateUtil.truncaData(fim)));
		}
		
//		criteria.addOrder(Order.desc(ScoHistoricoOcorrForn.Fields.FRN_NUMERO.toString()));
		criteria.addOrder(Order.asc("scoFornecedor."+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
		criteria.addOrder(Order.asc("scoHistoricoOcorrForn."+ScoHistoricoOcorrForn.Fields.NUMERO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Long listarHistoricoOcorrFornCount(final Object pesquisa) {
		return executeCriteriaCount(criarCriteriaHistoricoOcorrForn(pesquisa));
	}
	
}
