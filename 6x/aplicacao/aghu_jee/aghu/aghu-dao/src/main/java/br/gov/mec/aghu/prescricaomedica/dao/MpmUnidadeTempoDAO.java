package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmUnidadeTempo;
import br.gov.mec.aghu.model.RapServidores;

public class MpmUnidadeTempoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmUnidadeTempo>{
	
	
	private static final long serialVersionUID = 1932595018467339950L;

	/**
	 * Busca MpmUnidadeTempo atraves da coluna seq.
	 * @param seq
	 * @return
	 */
	public MpmUnidadeTempo buscarUnidadeTempoPorId(Integer seq) {
		if(seq == null){
			return null;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmUnidadeTempo.class);
		criteria.add(Restrictions.eq(MpmUnidadeTempo.Fields.SEQ.toString(), seq.shortValue()));
		return (MpmUnidadeTempo) executeCriteriaUniqueResult(criteria);
	}
	
	//Lista recuperada para paginaçao. 
	public List<MpmUnidadeTempo> pesquisarUnidadeTempo(Integer firstResult, Integer maxResult, String orderProperty, 
																	boolean asc, MpmUnidadeTempo unidadeTempo){
		
		DetachedCriteria criteria = montaCriteriaMpmUnidadeTempo(unidadeTempo);
		
		return this.executeCriteria(criteria, firstResult, maxResult,orderProperty, asc);
	}
	
	//count da lista de paginaçao.
	public Long pesquisarUnidadeTempoCount(MpmUnidadeTempo unidadeTempo){

		DetachedCriteria criteria = montaCriteriaMpmUnidadeTempo(unidadeTempo);
		
		return this.executeCriteriaCount(criteria);
	}
	
	//Criteria para paginação. Segundo parametros.
	public DetachedCriteria montaCriteriaMpmUnidadeTempo(MpmUnidadeTempo unidadeTempo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmUnidadeTempo.class);
		
		criteria.createAlias("servidor", "servidor");
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica");
		
		if(unidadeTempo.getSeq() != null){
			criteria.add(Restrictions.eq(MpmUnidadeTempo.Fields.SEQ.toString(), unidadeTempo.getSeq()));
		}
		if(StringUtils.isNotBlank(unidadeTempo.getDescricao())){
			criteria.add(Restrictions.ilike(MpmUnidadeTempo.Fields.DESCRICAO.toString(), unidadeTempo.getDescricao(), MatchMode.ANYWHERE));
		}
		if(unidadeTempo.getIndSituacao() != null){
			criteria.add(Restrictions.eq(MpmUnidadeTempo.Fields.INDSITUACAO.toString(), unidadeTempo.getIndSituacao()));
		}
		
		return criteria;
	}
}
