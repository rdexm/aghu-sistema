package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.McoAchado;

public class McoAchadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoAchado> {

	private static final long serialVersionUID = 2750084686764293687L;
	
	private DetachedCriteria criarCriteriaBuscaAchadoPorDescSituacao(String descricao, DominioSituacao situacao, Integer seqRegiao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoAchado.class);
		criarFiltroDescricaoMcoAchado(descricao, criteria);
		if(situacao != null) {
			criteria.add(Restrictions.eq(McoAchado.Fields.IND_SITUACAO.toString(), situacao));
		}
		if(seqRegiao != null) {
			criteria.add(Restrictions.eq(McoAchado.Fields.RAN_SEQ.toString(), seqRegiao));
		}
		return criteria;
	}
	
	//#25685 - C3	
	public List<Integer> buscarRanSeqPorDescricaoAchado(String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoAchado.class);
		criarFiltroDescricaoMcoAchado(descricao, criteria);
		criteria.setProjection(Projections.property(McoAchado.Fields.RAN_SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	private void criarFiltroDescricaoMcoAchado(String descricao, final DetachedCriteria criteria) {
		if(StringUtils.isNotBlank(descricao)) 	{
			criteria.add(Restrictions.ilike(McoAchado.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
	}
	//#25685 - C1
	public List<McoAchado> buscarAchadosPorDescSituacao(String descricao, DominioSituacao situacao, Integer seqRegiao, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return executeCriteria(criarCriteriaBuscaAchadoPorDescSituacao(descricao, situacao, seqRegiao), firstResult, maxResults, orderProperty, asc);
	}
	
	public Long buscarAchadosPorDescSituacaoCount(String descricao, DominioSituacao situacao, Integer seqRegiao) {
		return executeCriteriaCount(criarCriteriaBuscaAchadoPorDescSituacao(descricao, situacao, seqRegiao));
	}


}
