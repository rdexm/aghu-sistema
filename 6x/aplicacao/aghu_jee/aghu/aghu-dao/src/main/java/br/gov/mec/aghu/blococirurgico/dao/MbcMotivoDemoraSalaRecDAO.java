package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcMotivoDemoraSalaRec;

public class MbcMotivoDemoraSalaRecDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcMotivoDemoraSalaRec> {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4782863728369878643L;


	/**
	 * Pesquisa Motivos de demora em sala de recuperação
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @return
	 */
	public Long pesquisarMotivosDemoraSalaRecCount(Short codigo, String descricao, DominioSituacao situacao) {
		
		DetachedCriteria criteria = montaCriteriaPesquisa(codigo, descricao, situacao);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Pesquisa Motivos de demora em sala de recuperação - pesquisa paginada
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @param firstResult
	 * @param maxResult
	 * @return
	 */
	public List<MbcMotivoDemoraSalaRec> pesquisarMotivosDemoraSalaRec(Short codigo, String descricao, DominioSituacao situacao,Integer firstResult, Integer maxResult) {
		DetachedCriteria criteria = montaCriteriaPesquisa(codigo, descricao, situacao);
		return executeCriteria(criteria, firstResult, maxResult, "descricao", true);
	}
	
	private DetachedCriteria montaCriteriaPesquisa(Short codigo, String descricao, DominioSituacao situacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMotivoDemoraSalaRec.class, "MOT");
		criteria.createAlias(MbcMotivoDemoraSalaRec.Fields.SERVIDOR.toString(), "RAP");
		
		if(codigo!=null){
			criteria.add(Restrictions.eq("MOT." + MbcMotivoDemoraSalaRec.Fields.SEQ.toString(), codigo));
		}
		
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike("MOT." + MbcMotivoDemoraSalaRec.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		
		if(situacao!=null){
			criteria.add(Restrictions.eq("MOT." + MbcMotivoDemoraSalaRec.Fields.SITUACAO.toString(), situacao));
		}
		return criteria;
	}


	public MbcMotivoDemoraSalaRec obterMbcMotivoDemoraSalaRecPorDescricao(String descricao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMotivoDemoraSalaRec.class, "MOT");
		
		criteria.add(Restrictions.eq("MOT."+MbcMotivoDemoraSalaRec.Fields.DESCRICAO.toString(), descricao));
		
		return (MbcMotivoDemoraSalaRec) executeCriteriaUniqueResult(criteria);
	}
	
}