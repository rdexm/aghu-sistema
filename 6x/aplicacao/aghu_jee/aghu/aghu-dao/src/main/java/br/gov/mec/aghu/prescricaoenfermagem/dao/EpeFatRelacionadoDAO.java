package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class EpeFatRelacionadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeFatRelacionado> {
	private static final long serialVersionUID = 5409509275017652625L;

	@SuppressWarnings("unchecked")
	public List<EpeFatRelacionado> pesquisarEtiologias(String filtroParametro, Short filtroGnbSeq, Short filtroSnbSequencia, Short filtroSequencia) {
		Query query = montarQueryEtiologias(filtroParametro, filtroGnbSeq, filtroSnbSequencia, filtroSequencia);
		List<EpeFatRelacionado> lista = query.getResultList();
		return 	lista;
	}
	
	private Query montarQueryEtiologias(String filtroParametro, Short filtroGnbSeq, Short filtroSnbSequencia, Short filtroSequencia) {

		Query query = createQuery(
				montarHQLEtiologias(filtroParametro, filtroGnbSeq, filtroSnbSequencia, filtroSequencia));

		if (filtroGnbSeq != null) {
			query.setParameter("filtroGnbSeq", filtroGnbSeq);
		}
		if (filtroSnbSequencia != null) {
			query.setParameter("filtroSnbSequencia", filtroSnbSequencia);
		}
		if (filtroSequencia != null) {
			query.setParameter("filtroSequencia", filtroSequencia);
		}
		return query;
	}
	
	private String montarHQLEtiologias(String filtroParametro, Short filtroGnbSeq, Short filtroSnbSequencia,
			Short filtroSequencia) {
		
		String seqOuDescricao = StringUtils.trimToNull(filtroParametro);
		
		StringBuilder sb = new StringBuilder(500);
		sb.append("select fatRelacionado from EpeFatRelacionado fatRelacionado")
		.append(" inner join fatRelacionado.fatRelDiagnosticos fatRelDiagnostico")
		.append(" where fatRelacionado.seq = fatRelDiagnostico.id.freSeq")
		.append(" and fatRelDiagnostico.situacao='").append(DominioSituacao.A).append('\'')
		.append(" and fatRelacionado.situacao='").append(DominioSituacao.A).append('\'');
		
		
		if (StringUtils.isNotEmpty(seqOuDescricao)) {
			Short seq = -1;
			if (CoreUtil.isNumeroShort(seqOuDescricao)){
				seq = Short.parseShort(seqOuDescricao);
			}			
			if (seq != -1) {
				sb.append(" and fatRelacionado.seq= ").append(seq);
			} else {
				sb.append(" and fatRelacionado.descricao like  '%").append(seqOuDescricao.toUpperCase()).append("%'");
			}
		}
		
		if (filtroGnbSeq != null) {
			sb.append(" and fatRelDiagnostico."+EpeFatRelDiagnostico.Fields.DGN_SNB_GNB_SEQ.toString() + " = :filtroGnbSeq");
		}
		if (filtroSnbSequencia != null) {
			sb.append(" and fatRelDiagnostico."+EpeFatRelDiagnostico.Fields.DGN_SNB_SEQUENCIA.toString() + " = :filtroSnbSequencia");
		}
		if (filtroSequencia != null) {
			sb.append(" and fatRelDiagnostico."+EpeFatRelDiagnostico.Fields.DGN_SEQUENCIA.toString() + " = :filtroSequencia");
		}
		
		sb.append(" order by fatRelacionado." ).append( EpeFatRelacionado.Fields.DESCRICAO.toString());
		
		return sb.toString();
	}

	public Long pesquisarEtiologiasPorSeqDescricaoCount(Short seq, String descricao) {
		
		DetachedCriteria criteria = criarCriteriaPesquisarEtiologiasPorSeqDescricao(seq, descricao);
		
		return this.executeCriteriaCount(criteria);
	}

	public List<EpeFatRelacionado> pesquisarEtiologiasPorSeqDescricao(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short seq, String descricao) {
		
		DetachedCriteria criteria = criarCriteriaPesquisarEtiologiasPorSeqDescricao(seq, descricao);
		
		criteria.addOrder(Order.asc(EpeFatRelacionado.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(EpeFatRelacionado.Fields.SEQ.toString()));
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	private DetachedCriteria criarCriteriaPesquisarEtiologiasPorSeqDescricao(Short seq, String descricao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeFatRelacionado.class);

		if (seq != null){
			criteria.add(Restrictions.eq(EpeFatRelacionado.Fields.SEQ.toString(), seq));
		}

		if (StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(EpeFatRelacionado.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
	// #4960 (Manter diagnósticos x cuidados)
	// C4
	public List<EpeFatRelacionado> pesquisarEtiologias(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaSeqOuDescricao(parametro);
		criteria.addOrder(Order.asc(EpeFatRelacionado.Fields.SEQ.toString())).addOrder(Order.asc(EpeFatRelacionado.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	// #4960 (Manter diagnósticos x cuidados)
	// C4 Count
	public Long pesquisarEtiologiasCount(String parametro) {
		DetachedCriteria criteria = montarCriteriaParaSeqOuDescricao(parametro);
		return executeCriteriaCount(criteria);
	}
	
	// #5048 (Manter diagnósticos x etiologias) - somente as etiologias não relacionadas com diagnosticos 

	public List<EpeFatRelacionado> pesquisarEtiologiasNaoRelacionadas(String parametro, Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia) {
		DetachedCriteria criteria = montarCriteriaParaSeqOuDescricao(parametro);		
		criteria.add(Subqueries.propertyNotIn(EpeFatRelacionado.Fields.SEQ.toString(), subCriteriaRelacionados(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia)));		
		criteria.addOrder(Order.asc(EpeFatRelacionado.Fields.SEQ.toString())).addOrder(Order.asc(EpeFatRelacionado.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	// #5048 (Manter diagnósticos x etiologias) - somente as etiologias não relacionadas com diagnosticos 

	public Long pesquisarEtiologiasNaoRelacionadasCount(String parametro, Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia) {
		DetachedCriteria criteria = montarCriteriaParaSeqOuDescricao(parametro);  
		criteria.add(Subqueries.propertyNotIn(EpeFatRelacionado.Fields.SEQ.toString(), subCriteriaRelacionados(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia))); 
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria subCriteriaRelacionados(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeFatRelDiagnostico.class, "rel")
		.setProjection(Projections.property(EpeFatRelDiagnostico.Fields.FRE_SEQ.toString()));
			criteria.add(Restrictions.eq("rel."+EpeFatRelDiagnostico.Fields.DGN_SNB_GNB_SEQ.toString(), dgnSnbGnbSeq));
			criteria.add(Restrictions.eq("rel."+EpeFatRelDiagnostico.Fields.DGN_SNB_SEQUENCIA.toString(), dgnSnbSequencia));
			criteria.add(Restrictions.eq("rel."+EpeFatRelDiagnostico.Fields.DGN_SEQUENCIA.toString(), dgnSequencia));
		return criteria;
	}
	
	private DetachedCriteria montarCriteriaParaSeqOuDescricao(String parametro) {
		String seqOuDescricao = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeFatRelacionado.class);
		if (StringUtils.isNotEmpty(seqOuDescricao)) {
			Short seq = -1;
			if (CoreUtil.isNumeroShort(seqOuDescricao)) {
				seq = Short.parseShort(seqOuDescricao);
			}
			if (seq != -1) {
				criteria.add(Restrictions.eq(EpeFatRelacionado.Fields.SEQ.toString(), seq));
			} else {
				criteria.add(Restrictions.ilike(EpeFatRelacionado.Fields.DESCRICAO.toString(), seqOuDescricao, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
}