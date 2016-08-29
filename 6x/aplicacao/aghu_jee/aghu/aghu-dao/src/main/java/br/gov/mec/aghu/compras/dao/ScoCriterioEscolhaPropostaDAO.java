package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;

public class ScoCriterioEscolhaPropostaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoCriterioEscolhaProposta> {

	private static final long serialVersionUID = -594518991954904231L;

	public List<ScoCriterioEscolhaProposta> pesquisarCriterioEscolhaProposta (
		    Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
		    Short codigoCriterio, String descricaoCriterio, DominioSituacao situacaoCriterio) {

		DetachedCriteria criteria = this.obterCriteriaBasica(codigoCriterio, descricaoCriterio, situacaoCriterio);

		criteria.addOrder(Order.asc(ScoCriterioEscolhaProposta.Fields.CODIGO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarCriterioEscolhaPropostaCount(Short codigoCriterio, String descricaoCriterio, DominioSituacao situacaoCriterio) {
		DetachedCriteria criteria = this.obterCriteriaBasica(codigoCriterio, descricaoCriterio, situacaoCriterio);
		return this.executeCriteriaCount(criteria);
	}

	public List<ScoCriterioEscolhaProposta> pesquisarCriterioEscolhaAtivos () {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCriterioEscolhaProposta.class);
		
		criteria.add(Restrictions.eq(
				ScoCriterioEscolhaProposta.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.addOrder(Order.asc(ScoCriterioEscolhaProposta.Fields.CODIGO.toString()));
		
		return this.executeCriteria(criteria);
	}

	
	private DetachedCriteria obterCriteriaBasica (Short codigoCriterio, String descricaoCriterio, DominioSituacao situacaoCriterio) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoCriterioEscolhaProposta.class);

		if (codigoCriterio != null) {			
			criteria.add(Restrictions.eq(
					ScoCriterioEscolhaProposta.Fields.CODIGO.toString(), codigoCriterio));
		}

		if (situacaoCriterio != null) {
			criteria.add(Restrictions.eq(
					ScoCriterioEscolhaProposta.Fields.SITUACAO.toString(), situacaoCriterio));
		}

		
		if (StringUtils.isNotBlank(descricaoCriterio)) {
			criteria.add(Restrictions.ilike(
					ScoCriterioEscolhaProposta.Fields.DESCRICAO.toString(),
					descricaoCriterio, MatchMode.ANYWHERE));
		}

		return criteria;
	}	
}
