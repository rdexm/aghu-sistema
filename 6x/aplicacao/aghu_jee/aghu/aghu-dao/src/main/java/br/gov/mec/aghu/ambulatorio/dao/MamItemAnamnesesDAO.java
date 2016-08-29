package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamItemAnamneses;

public class MamItemAnamnesesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamItemAnamneses> {

	private static final long serialVersionUID = 1736270832046447945L;

	public DetachedCriteria criteriaPesquisarItemAnamnesesPorAnamneses(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamItemAnamneses.class);
		criteria.add(Restrictions.eq(MamItemAnamneses.Fields.ANA_SEQ.toString(),
				anaSeq));
		return criteria;
	}
	
	public List<MamItemAnamneses> pesquisarItemAnamnesesPorAnamneses(Long anaSeq) {
		DetachedCriteria criteria = this.criteriaPesquisarItemAnamnesesPorAnamneses(anaSeq);
		return executeCriteria(criteria);
	}
	
	public MamItemAnamneses primeiroItemAnamnesesPorAnamneses(Long anaSeq) {
		DetachedCriteria criteria = this.criteriaPesquisarItemAnamnesesPorAnamneses(anaSeq);
		List<MamItemAnamneses> anam = executeCriteria(criteria, 0, 1, null, true);
		if(anam!=null && !anam.isEmpty()){
			return anam.get(0);
		}
		return null;
	}
		
	public List<MamItemAnamneses> pesquisarItemAnamnesesPorAnamnesesTipoItem(Long anaSeq, Integer tinSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemAnamneses.class,"IAN");
		criteria.add(Restrictions.eq("IAN.".concat(MamItemAnamneses.Fields.ANA_SEQ.toString()), anaSeq));
		criteria.add(Restrictions.eq("IAN.".concat(MamItemAnamneses.Fields.TIN_SEQ.toString()), tinSeq));
		
		return executeCriteria(criteria);
	}
	
	public List<MamItemAnamneses> pesquisarItemAnamnesePorAnamnesesEDescricaoNula(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemAnamneses.class);
		criteria.add(Restrictions.eq(MamItemAnamneses.Fields.ANA_SEQ.toString(),anaSeq));
		criteria.add(Restrictions.eq(MamItemAnamneses.Fields.DESCRICAO.toString(),null));

		return executeCriteria(criteria);
	}

	/**
	 * #50745 - P1 - Cursor do item de anamnese
	 */
	public Long obterCountItemAnamnesePorAnaSeq(Long anaSeq) {
		DetachedCriteria criteria = this.criteriaPesquisarItemAnamnesesPorAnamneses(anaSeq);
		criteria.add(Restrictions.isNotNull(MamItemAnamneses.Fields.DESCRICAO.toString()));
		return executeCriteriaCount(criteria);
	}

	/**
	 * #50745 - P2 - Consulta os itens de anamnese
	 */
	public List<MamItemAnamneses> obterListaItemAnamnesePorAnaSeq(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemAnamneses.class);
		criteria.add(Restrictions.eq(MamItemAnamneses.Fields.ANA_SEQ.toString(), anaSeq));
		return executeCriteria(criteria);
	}
}