package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghCaixaPostalAplicacao;

public class AghCaixaPostalAplicacaoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AghCaixaPostalAplicacao> {

	
	private static final long serialVersionUID = -8676878452039124222L;

	public List<AghCaixaPostalAplicacao> pesquisaCaixaPostalPorCxtSeq(Long cxtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaixaPostalAplicacao.class);

		if (cxtSeq!=null) {
			criteria.add(Restrictions.eq(AghCaixaPostalAplicacao.Fields.CXT_SEQ.toString(),cxtSeq));
			 
		}

		return this.executeCriteria(criteria);
	}
	
	
	/**
	 * 
	 * @param cxt_seq
	 * Estoria: 40229, C13
	 */
	public void removerAghCaixaPostalAplicacaoPorCaixaPostalServidoresSeq(Long cxt_seq){
		DetachedCriteria criteria= DetachedCriteria.forClass(AghCaixaPostalAplicacao.class);
		
		criteria.add(Restrictions.eq(AghCaixaPostalAplicacao.Fields.CXT_SEQ.toString(),cxt_seq));
	
		List<AghCaixaPostalAplicacao> aghCaixaPostalAplicacaos=executeCriteria(criteria);
		
		for(AghCaixaPostalAplicacao aghCaixaPostalAplicacao:aghCaixaPostalAplicacaos){
			this.remover(aghCaixaPostalAplicacao);			
		}
	}
}
