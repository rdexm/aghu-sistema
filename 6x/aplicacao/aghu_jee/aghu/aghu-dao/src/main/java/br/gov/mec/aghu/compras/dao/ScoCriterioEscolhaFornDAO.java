package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCriterioEscolhaForn;

public class ScoCriterioEscolhaFornDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoCriterioEscolhaForn>{

	private static final long serialVersionUID = 5826065874355846844L;

	public Long listaCriterioEscolhaFornCount(Short codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = obterCriteria(codigo, descricao,	situacao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteria(Short codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCriterioEscolhaForn.class);
		if(codigo!=null){
			criteria.add(Restrictions.eq(ScoCriterioEscolhaForn.Fields.CODIGO.toString(), codigo));
		}
		if(descricao!=null && !descricao.isEmpty()){
			criteria.add(Restrictions.eq(ScoCriterioEscolhaForn.Fields.DESCRICAO.toString(), descricao));
		}
		if(situacao!=null){
			criteria.add(Restrictions.eq(ScoCriterioEscolhaForn.Fields.SITUACAO.toString(), situacao));
		}
		return criteria;
	}
	
	public List<ScoCriterioEscolhaForn> listaCriterioEscolhaForn(Short codigo, String descricao, DominioSituacao situacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = obterCriteria(codigo, descricao,	situacao);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}		
}
