package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class EpeGrupoNecesBasicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeGrupoNecesBasica> {

	private static final long serialVersionUID = -4932708828789468480L;

	public List<EpeGrupoNecesBasica> pesquisarGrupoNecesBasicaAtivo(
			String parametro) {
		DetachedCriteria criteria = montarCriteriaBuscaAtivo(parametro);
		criteria.addOrder(Order.asc(EpeGrupoNecesBasica.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarCriteriaParaSeqOuDescricao(String parametro) {
		String seqOuDescricao = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EpeGrupoNecesBasica.class);
		if (StringUtils.isNotEmpty(seqOuDescricao)) {
			Short seq = -1;
			if (CoreUtil.isNumeroShort(seqOuDescricao)){
				seq = Short.parseShort(seqOuDescricao);
			}			
			if (seq != -1) {
				criteria.add(Restrictions.eq(EpeGrupoNecesBasica.Fields.SEQ.toString(),
						seq));
			} else {
				criteria.add(Restrictions.ilike(
						EpeGrupoNecesBasica.Fields.DESCRICAO.toString(),
						seqOuDescricao, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
//	#4957
//	C1
	public List<EpeGrupoNecesBasica> pesquisarGrupoNecesBasicaAtivoOrderSeq(String parametro) {
		DetachedCriteria criteria = montarCriteriaBuscaAtivo(parametro);
		criteria.addOrder(Order.asc(EpeGrupoNecesBasica.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
	
//	#4957
//	C1 count
	public Long pesquisarGrupoNecesBasicaAtivoOrderSeqCount(String parametro) {
		DetachedCriteria criteria = montarCriteriaBuscaAtivo(parametro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaBuscaAtivo(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		criteria.add(Restrictions.eq(EpeGrupoNecesBasica.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
//4956
	public Long pesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacaoCount(Short seq, String descricao, DominioSituacao situacao) {
		
		DetachedCriteria criteria = criarCriteriaPesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacao(seq, descricao, situacao);
		
		return this.executeCriteriaCount(criteria);
	}

	public List<EpeGrupoNecesBasica> pesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacao(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short seq, String descricao, DominioSituacao situacao) {
		
		DetachedCriteria criteria = criarCriteriaPesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacao(seq, descricao, situacao);
		
		criteria.addOrder(Order.asc(EpeGrupoNecesBasica.Fields.SEQ.toString()));
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	private DetachedCriteria criarCriteriaPesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacao(Short seq, String descricao, DominioSituacao situacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeGrupoNecesBasica.class);

		if (seq != null){
			criteria.add(Restrictions.eq(EpeGrupoNecesBasica.Fields.SEQ.toString(), seq));
		}

		if (StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(EpeGrupoNecesBasica.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		
		if (situacao != null){
			criteria.add(Restrictions.eq(EpeGrupoNecesBasica.Fields.SITUACAO.toString(), situacao));
		}

		return criteria;
	}
	
	// #4960 (Manter diagnósticos x cuidados)
	// C1
	public List<EpeGrupoNecesBasica> pesquisarGrupoNecesBasica(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		criteria.addOrder(Order.asc(EpeGrupoNecesBasica.Fields.SEQ.toString())).addOrder(Order.asc(EpeGrupoNecesBasica.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	// #4960 (Manter diagnósticos x cuidados)
	// C1 Count
	public Long pesquisarGrupoNecesBasicaCount(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		return executeCriteriaCount(criteria);
	}
}
