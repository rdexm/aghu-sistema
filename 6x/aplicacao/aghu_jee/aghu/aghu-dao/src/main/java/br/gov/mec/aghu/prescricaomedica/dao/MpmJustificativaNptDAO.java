package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmJustificativaNpt;

public class MpmJustificativaNptDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmJustificativaNpt>{


	private static final long serialVersionUID = -817668541685494777L;

	public MpmJustificativaNpt obterMpmJustificativaNPToPorChavePrimaria(Short seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustificativaNpt.class);
		criteria.add(Restrictions.eq(MpmJustificativaNpt.Fields.SEQ.toString(), seq));
		return (MpmJustificativaNpt) executeCriteria(criteria);
	}
	
	public Long listarJustificativaNPTCount(Short seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = createJustificativaNPTCriteria(seq, descricao, situacao);
		return executeCriteriaCount(criteria);
	}
	
	public List<MpmJustificativaNpt> listarJustificativaNPT(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = createJustificativaNPTCriteria(seq, descricao, situacao);
		
		if (StringUtils.isEmpty(orderProperty)) {
			orderProperty =  MpmJustificativaNpt.Fields.SEQ.toString();
			asc = true;
		}
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}


	private DetachedCriteria createJustificativaNPTCriteria(Short seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustificativaNpt.class);
		
		criteria.createAlias(MpmJustificativaNpt.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);

		if (seq != null) {
			criteria.add(Restrictions.eq(MpmJustificativaNpt.Fields.SEQ.toString(), seq));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MpmJustificativaNpt.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(MpmJustificativaNpt.Fields.SITUACAO.toString(), situacao));
		}
		return criteria;
	}
	
//	SELECT JNP.seq       CODIGO, 
//	       JNP.descricao JUSTIFICATIVA 
//	FROM   mpm_justificativa_npts JNP 
//	WHERE  JNP.ind_situacao = ‘A’ 
//	ORDER  BY jnp.descricao
	// #990 C1 
	public List<MpmJustificativaNpt> pesquisarJustificativaNptPorDescricao(String descricao) {
		DetachedCriteria criteria = criarCriteriaPesquisaJustificativa(descricao);
		criteria.addOrder(Order.asc(MpmJustificativaNpt.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria criarCriteriaPesquisaJustificativa(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmJustificativaNpt.class);
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MpmJustificativaNpt.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(MpmJustificativaNpt.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	public Long pesquisarJustificativaNptPorDescricaoCount(String descricao) {
		return executeCriteriaCount(criarCriteriaPesquisaJustificativa(descricao));
	}
}
