package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.LwsComAmostra;
import br.gov.mec.aghu.model.LwsComSolicitacaoExame;
import br.gov.mec.aghu.model.LwsComunicacao;

public class LwsComSolicitacaoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<LwsComSolicitacaoExame>{


	private static final long serialVersionUID = 544922395561828120L;

	/**
	 * Pesquisa LwsComSolicitacaoExame através da comunicação LWS e amostra LWS
	 * @param idComunicacao
	 * @param idAmostra
	 * @return
	 */
	public List<LwsComSolicitacaoExame> pesquisarLwsComSolicitacaoExame(Integer idComunicacao, Integer idAmostra) {

		DetachedCriteria criteria = DetachedCriteria.forClass(LwsComSolicitacaoExame.class, "cse");
		criteria.createAlias("cse." + LwsComSolicitacaoExame.Fields.LWS_COMUNICACAO.toString(), "lco");
		criteria.createAlias("cse." + LwsComSolicitacaoExame.Fields.LWS_COM_AMOSTRAS.toString(), "lca");

		criteria.add(Restrictions.eq("lco." + LwsComunicacao.Fields.ID_COMUNICACAO.toString(), idComunicacao));
		criteria.add(Restrictions.eq("lca." + LwsComAmostra.Fields.ID.toString(), idAmostra));

		return executeCriteria(criteria);
	}

	public AelMateriaisAnalises obterAelMateriaisAnalisesPorLwsExameLis(Integer lwsExameLis) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMateriaisAnalises.class);
		criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.SEQ.toString(), lwsExameLis)); //SUBSTR(lws.exame_lis, 6, 5) AND
		criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.IND_COLETAVEL.toString(), Boolean.TRUE));

		return (AelMateriaisAnalises) executeCriteriaUniqueResult(criteria);
	}
	
	
}
