package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAelUnfExecutaExames;

public class VAelUnfExecutaExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelUnfExecutaExames>{

	private static final long serialVersionUID = 1703688351115141933L;


	public List<VAelUnfExecutaExames> pesquisarPorSiglaOuMaterialOuExameOuUnidade(String filtro){
		final DetachedCriteria criteria = obterCriteriaPorSiglaOuMaterialOuExameOuUnidade(filtro);
		criteria.addOrder(Order.asc(VAelUnfExecutaExames.Fields.DESCRICAO_EXAME.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}	

	public Long pesquisarPorSiglaOuMaterialOuExameOuUnidadeCount(String filtro){
		return executeCriteriaCount(obterCriteriaPorSiglaOuMaterialOuExameOuUnidade(filtro));
	}	

	public List<VAelUnfExecutaExames> pesquisarPorSiglaMaterialOuExame(String filtro){
		final DetachedCriteria criteria = obterCriteriaPorSiglaMaterialOuExame(filtro);
		criteria.addOrder(Order.asc(VAelUnfExecutaExames.Fields.DESCRICAO_EXAME.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}	
	
	public Long pesquisarPorSiglaMaterialOuExameCount(String filtro){
		return executeCriteriaCount(obterCriteriaPorSiglaMaterialOuExame(filtro));
	}

	private DetachedCriteria obterCriteriaPorSiglaOuMaterialOuExameOuUnidade(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelUnfExecutaExames.class);
		
		criteria.add(Restrictions.or(
				Restrictions.or(
										Restrictions.or( Restrictions.ilike(VAelUnfExecutaExames.Fields.SIGLA.toString(), filtro, MatchMode.ANYWHERE), 
														 Restrictions.ilike(VAelUnfExecutaExames.Fields.DESCRICAO_EXAME.toString(), filtro, MatchMode.ANYWHERE)
													   ),
									   Restrictions.ilike(VAelUnfExecutaExames.Fields.DESCRICAO_MATERIAL.toString(), filtro, MatchMode.ANYWHERE)
									)
					, Restrictions.ilike(VAelUnfExecutaExames.Fields.DESCRICAO_UNIDADE.toString(), filtro, MatchMode.ANYWHERE)));
		return criteria;
	}

	private DetachedCriteria obterCriteriaPorSiglaMaterialOuExame(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelUnfExecutaExames.class);
		
		criteria.add(Restrictions.or(
										Restrictions.or( Restrictions.ilike(VAelUnfExecutaExames.Fields.SIGLA.toString(), filtro, MatchMode.ANYWHERE), 
														 Restrictions.ilike(VAelUnfExecutaExames.Fields.DESCRICAO_EXAME.toString(), filtro, MatchMode.ANYWHERE)
													   ),
									   Restrictions.ilike(VAelUnfExecutaExames.Fields.DESCRICAO_MATERIAL.toString(), filtro, MatchMode.ANYWHERE)
									)
					);
		return criteria;
	}
	
	public List<VAelUnfExecutaExames> pesquisarExamePorDescricaoOuSigla(String parametro){
		DetachedCriteria criteria = montarCriteriaParaPesquisarPorSiglaOuDescricao(parametro, null);
		return executeCriteria(criteria);
	}
	
	public List<VAelUnfExecutaExames> pesquisarExamePorSiglaOuDescricao(String parametro){
		DetachedCriteria criteria = montarCriteriaParaPesquisarPorSiglaOuDescricao(parametro, null);
		List<String> filter = this.montarFiltroPesquisarExamePorSiglaOuDescricao();
		
		criteria.add(Restrictions.or(Restrictions.in(VAelUnfExecutaExames.Fields.AGENDAM_PREVIO_INT.toString(), filter),
				Restrictions.in(VAelUnfExecutaExames.Fields.AGENDAM_PREVIO_NAO_INT.toString(), filter)));
		return executeCriteria(criteria);
	}
	
	public List<VAelUnfExecutaExames> pesquisarExamePorSiglaOuDescricao(String parametro, Short unfSeq){
		DetachedCriteria criteria = montarCriteriaParaPesquisarPorSiglaOuDescricao(parametro, unfSeq);
		
		return executeCriteria(criteria);
	}
	
	private List<String> montarFiltroPesquisarExamePorSiglaOuDescricao() {
		List<String> filter = new ArrayList<String>();
		filter.add("R");
		filter.add("S");
		return filter;
	}
	
	private DetachedCriteria montarCriteriaParaPesquisarPorSiglaOuDescricao(String parametro, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelUnfExecutaExames.class);
		
		if(StringUtils.isNotBlank(parametro)) {
			criteria.add(Restrictions.or(Restrictions.ilike(VAelUnfExecutaExames.Fields.SIGLA.toString(), parametro, MatchMode.ANYWHERE), 
					Restrictions.or(Restrictions.ilike(VAelUnfExecutaExames.Fields.DESCRICAO_EXAME.toString(), parametro, MatchMode.ANYWHERE),
							Restrictions.ilike(VAelUnfExecutaExames.Fields.DESCRICAO_USUAL_EXAME.toString(), parametro, MatchMode.ANYWHERE))));
		}
		
		if(unfSeq != null) {
			criteria.add(Restrictions.eq(VAelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		}
		
		criteria.addOrder(Order.asc(VAelUnfExecutaExames.Fields.DESCRICAO_EXAME.toString()));
		return criteria;
	}
	
	public List<VAelUnfExecutaExames> pesquisarExamePorSeqUnidadeExecutora(Short unfSeq,
			String sigla, String descricaoMaterial, String descricaoUsualExame,
			String indSituacaoUfe, Integer firstResult, Integer maxResults,
			String orderProperty, Boolean asc) {
		DetachedCriteria criteria = montarCriteriaPorSeqUnidadeExecutora(unfSeq, sigla, descricaoMaterial,
				descricaoUsualExame, indSituacaoUfe);
		criteria.addOrder(Order.desc(VAelUnfExecutaExames.Fields.DESCRICAO_USUAL_EXAME.toString()));
		criteria.addOrder(Order.desc(VAelUnfExecutaExames.Fields.DESCRICAO_MATERIAL.toString()));
		
		return executeCriteria(criteria, firstResult, maxResults, null, true);
	}
	
	public Long pesquisarExamePorSeqUnidadeExecutoraCount(Short unfSeq,
			String sigla, String descricaoMaterial, String descricaoUsualExame,
			String indSituacaoUfe) {
		DetachedCriteria criteria = montarCriteriaPorSeqUnidadeExecutora(unfSeq, sigla, descricaoMaterial,
				descricaoUsualExame, indSituacaoUfe);
		
		return executeCriteriaCount(criteria);
	}
	
	public List<VAelUnfExecutaExames> pesquisarExamePorSeqUnidadeExecutora(Short unfSeq,
			String sigla, String descricaoMaterial, String descricaoUsualExame,
			String indSituacaoUfe) {
		DetachedCriteria criteria = montarCriteriaPorSeqUnidadeExecutora(unfSeq, sigla, descricaoMaterial,
				descricaoUsualExame, indSituacaoUfe);
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria montarCriteriaPorSeqUnidadeExecutora(Short unfSeq,
			String sigla, String descricaoMaterial, String descricaoUsualExame,
			String indSituacaoUfe) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelUnfExecutaExames.class);
		criteria.add(Restrictions.eq(VAelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		if(sigla != null && !sigla.isEmpty()) {
			criteria.add(Restrictions.ilike(VAelUnfExecutaExames.Fields.SIGLA.toString(), sigla, MatchMode.ANYWHERE));
		}
		if(descricaoMaterial != null && !descricaoMaterial.isEmpty()) {
			criteria.add(Restrictions.ilike(VAelUnfExecutaExames.Fields.DESCRICAO_MATERIAL.toString(), descricaoMaterial, MatchMode.ANYWHERE));
		}
		if(descricaoUsualExame != null && !descricaoUsualExame.isEmpty()) {
			criteria.add(Restrictions.ilike(VAelUnfExecutaExames.Fields.DESCRICAO_USUAL_EXAME.toString(), descricaoUsualExame, MatchMode.ANYWHERE));
		}
		if(indSituacaoUfe != null && !indSituacaoUfe.isEmpty()) {
			criteria.add(Restrictions.eq(VAelUnfExecutaExames.Fields.IND_SITUACAO_UFE.toString(), indSituacaoUfe));
		}
		
		return criteria;		
	}
	
	
	public VAelUnfExecutaExames obterVAelUnfExecutaExames(String emaExaSigla, Integer emaManSeq, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelUnfExecutaExames.class);
		
		criteria.add(Restrictions.eq(VAelUnfExecutaExames.Fields.SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(VAelUnfExecutaExames.Fields.MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(VAelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		
		return (VAelUnfExecutaExames) this.executeCriteriaUniqueResult(criteria);
	}

	public VAelUnfExecutaExames obterVAelUnfExecutaExames(String emaExaSigla, Integer emaManSeq, String indSituacao) {
		VAelUnfExecutaExames retorno = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelUnfExecutaExames.class);
		
		criteria.add(Restrictions.eq(VAelUnfExecutaExames.Fields.SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(VAelUnfExecutaExames.Fields.MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(VAelUnfExecutaExames.Fields.IND_SITUACAO_UFE.toString(), indSituacao));
		
		List<VAelUnfExecutaExames> listaRetorno = this.executeCriteria(criteria);
		
		if (listaRetorno != null && !listaRetorno.isEmpty()) {
			retorno = listaRetorno.get(0);
		}
			
		return retorno;
	}

}
