package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class EpeSubgrupoNecesBasicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeSubgrupoNecesBasica> {

	private static final long serialVersionUID = -2917837064525874332L;

	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupoNecessBasicaAtivo(
			String parametro, Short gnbSeq) {
		DetachedCriteria criteria = montarCriteriaSubgrupoNecessBasicaAtivo(
				parametro, gnbSeq);
		criteria.addOrder(Order.asc(EpeSubgrupoNecesBasica.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarCriteriaParaSeqOuDescricao(String parametro) {
		String seqOuDescricao = StringUtils.trimToNull(parametro);
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EpeSubgrupoNecesBasica.class);
		if (StringUtils.isNotEmpty(seqOuDescricao)) {
			Short seq = -1;
			if (CoreUtil.isNumeroShort(seqOuDescricao)){
				seq = Short.parseShort(seqOuDescricao);
			}			
			if (seq != -1) {
				criteria.add(Restrictions.eq(EpeSubgrupoNecesBasica.Fields.SEQ.toString(),
						seq));
			} else {
				criteria.add(Restrictions.ilike(
						EpeSubgrupoNecesBasica.Fields.DESCRICAO.toString(),
						seqOuDescricao, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	// #4957
	// C2
	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupoNecessBasicaAtivoOrderSeq(String parametro, Short gnbSeq) {
		DetachedCriteria criteria = montarCriteriaSubgrupoNecessBasicaAtivo(parametro, gnbSeq);
		criteria.addOrder(Order.asc(EpeSubgrupoNecesBasica.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	// #4957
	// C2 Count
	public Long pesquisarSubgrupoNecessBasicaAtivoOrderSeqCount(String parametro, Short gnbSeq) {
		DetachedCriteria criteria = montarCriteriaSubgrupoNecessBasicaAtivo(parametro, gnbSeq);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaSubgrupoNecessBasicaAtivo(
			String parametro, Short gnbSeq) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		criteria.add(Restrictions.eq(EpeSubgrupoNecesBasica.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(gnbSeq!=null){
			criteria.add(Restrictions.eq(EpeSubgrupoNecesBasica.Fields.GNB_SEQ.toString(), gnbSeq));
		}
		return criteria;
	}
	
	// #4956
	// C2
	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupoNecessBasicaOrderSeq(Short gnbSeq){
		DetachedCriteria criteria = montarCriteriaSubgrupoNecessBasica(gnbSeq);
		criteria.addOrder(Order.asc(EpeSubgrupoNecesBasica.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria montarCriteriaSubgrupoNecessBasica(Short gnbSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeSubgrupoNecesBasica.class);
		criteria.add(Restrictions.eq(EpeSubgrupoNecesBasica.Fields.GNB_SEQ.toString(), gnbSeq));
		return criteria;
	}
	
	public Short obterMaxSequenciaSubgrupoNecessBasica(Short gnbSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeSubgrupoNecesBasica.class);
		criteria.add(Restrictions.eq(EpeSubgrupoNecesBasica.Fields.GNB_SEQ.toString(), gnbSeq));
		criteria.setProjection(Projections.max(EpeSubgrupoNecesBasica.Fields.SEQ.toString()));
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupoNecessidadesBasicasPorSeq(Short gnbSeq, DominioSituacao situacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeSubgrupoNecesBasica.class);
		criteria.add(Restrictions.eq(EpeSubgrupoNecesBasica.Fields.GNB_SEQ.toString(), gnbSeq));
		
		if(situacao != null){
			criteria.add(Restrictions.eq(EpeSubgrupoNecesBasica.Fields.SITUACAO.toString(), situacao));
		}
		

		return executeCriteria(criteria);
		
	}
	
	// #4960 (Manter diagnósticos x cuidados)
	// C2
	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupoNecessBasica(String parametro, Short gnbSeq) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		if (gnbSeq != null) {
			criteria.add(Restrictions.eq(EpeSubgrupoNecesBasica.Fields.GNB_SEQ.toString(), gnbSeq));
		}
		criteria.addOrder(Order.asc(EpeSubgrupoNecesBasica.Fields.SEQ.toString())).addOrder(Order.asc(EpeSubgrupoNecesBasica.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	// #4960 (Manter diagnósticos x cuidados)
	// C2 Count
	public Long pesquisarSubgrupoNecessBasicaCount(String parametro, Short gnbSeq) {
		DetachedCriteria criteria = this.montarCriteriaParaSeqOuDescricao(parametro);
		if (gnbSeq != null) {
			criteria.add(Restrictions.eq(EpeSubgrupoNecesBasica.Fields.GNB_SEQ.toString(), gnbSeq));
		}
		return executeCriteriaCount(criteria);
	}
	
}
