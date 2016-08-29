package br.gov.mec.aghu.blococirurgico.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class ConsultarInformacaoAgendaBuilder extends QueryBuilder<DetachedCriteria> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5922322279586139652L;
	
	private Integer seq;	
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendas.class);
		criteria.createAlias(MbcAgendas.Fields.UNF.toString(), "UNF");
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		criteria.add(Restrictions.eq(MbcAgendas.Fields.SEQ.toString(), getSeq()));
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}
