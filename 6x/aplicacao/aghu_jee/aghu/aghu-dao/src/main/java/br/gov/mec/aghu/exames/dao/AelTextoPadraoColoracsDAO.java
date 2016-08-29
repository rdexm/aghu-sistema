package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelTextoPadraoColoracs;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class AelTextoPadraoColoracsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTextoPadraoColoracs> {

	private static final long serialVersionUID = 6176290591646057198L;

	private DetachedCriteria obterCriteriaBasica(final AelTextoPadraoColoracs aelTextoPadraoColoracs) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTextoPadraoColoracs.class);
		
		if(aelTextoPadraoColoracs != null){
			if(aelTextoPadraoColoracs.getSeq() != null){	
				criteria.add(Restrictions.eq(AelTextoPadraoColoracs.Fields.SEQ.toString(), aelTextoPadraoColoracs.getSeq()));
			}
			
			if(aelTextoPadraoColoracs.getDescricao() != null){	
				criteria.add(Restrictions.ilike(AelTextoPadraoColoracs.Fields.DESCRICAO.toString(), aelTextoPadraoColoracs.getDescricao(), MatchMode.ANYWHERE));
			}

			if(aelTextoPadraoColoracs.getIndSituacao() != null){	
				criteria.add(Restrictions.eq(AelTextoPadraoColoracs.Fields.IND_SITUACAO.toString(), aelTextoPadraoColoracs.getIndSituacao()));
			}
			
			if(aelTextoPadraoColoracs.getCriadoEm() != null){	
				criteria.add(Restrictions.eq(AelTextoPadraoColoracs.Fields.CRIADO_EM.toString(), aelTextoPadraoColoracs.getCriadoEm()));
			}
		}
		
		return criteria;
    }
	
	private DetachedCriteria obterCriteriaBasica(final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTextoPadraoColoracs.class);
		
			
		if(!StringUtils.isEmpty(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)){
				criteria.add(Restrictions.eq(AelTextoPadraoColoracs.Fields.SEQ.toString(), Integer.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike(AelTextoPadraoColoracs.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}

		if(situacao != null){
			criteria.add(Restrictions.eq(AelTextoPadraoColoracs.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		return criteria;
    }
	
	public AelTextoPadraoColoracs obterTextoPadraoColoracsPelaDescricaoExata(String filtro) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTextoPadraoColoracs.class);
		criteria.add(Restrictions.like(AelTextoPadraoColoracs.Fields.DESCRICAO.toString(), filtro, MatchMode.EXACT));
		return (AelTextoPadraoColoracs)executeCriteriaUniqueResult(criteria);
	}
	
	public List<AelTextoPadraoColoracs> pesquisarAelTextoPadraoColoracs(final Integer firstResult, final Integer maxResults, String orderProperty, boolean asc, final AelTextoPadraoColoracs aelTextoPadraoColoracs) {
		final DetachedCriteria criteria = obterCriteriaBasica(aelTextoPadraoColoracs);
		
		if(orderProperty == null){
			orderProperty = AelTextoPadraoColoracs.Fields.DESCRICAO.toString();
			asc = true;
		}
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarAelTextoPadraoColoracsCount(final AelTextoPadraoColoracs aelTextoPadraoColoracs) {
		final DetachedCriteria criteria = obterCriteriaBasica(aelTextoPadraoColoracs);
		return executeCriteriaCount(criteria);
	}
	
	public List<AelTextoPadraoColoracs> listarAelTextoPadraoColoracs(final DominioSituacao situacao) {
		final DetachedCriteria criteria = obterCriteriaBasica(null,situacao);
		criteria.addOrder(Order.asc(AelTextoPadraoColoracs.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	public List<AelTextoPadraoColoracs> pesquisarAelTextoPadraoColoracs(final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = obterCriteriaBasica(filtro,situacao);
		criteria.addOrder(Order.asc(AelTextoPadraoColoracs.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long pesquisarAelTextoPadraoColoracsCount(final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = obterCriteriaBasica(filtro,situacao);
		return executeCriteriaCount(criteria);
	}
}