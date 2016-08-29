package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamFuncaoGravacao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamFuncaoGravacaoDAO extends BaseDao<MamFuncaoGravacao>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8369943782908937781L;

	public String obterComandoFuncaoGravacaoPorSeq(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamFuncaoGravacao.class);
		criteria.setProjection(Projections.projectionList()	
				.add(Projections.property(MamFuncaoGravacao.Fields.COMANDO.toString())));
		criteria.add(Restrictions.eq(MamFuncaoGravacao.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MamFuncaoGravacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MamFuncaoGravacao.class));
		return (String) executeCriteriaUniqueResult(criteria);
	}
}
