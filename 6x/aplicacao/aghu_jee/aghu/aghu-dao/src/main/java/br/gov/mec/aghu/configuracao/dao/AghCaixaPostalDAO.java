package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghCaixaPostal;

public class AghCaixaPostalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghCaixaPostal> {

	private static final long serialVersionUID = -8102137227621857173L;
	
	
	/**
	 * 
	 * @param cxt_seq
	 * Estoria: 40229, C15
	 */
	public void removerAghCaixaPostalPorCaixaPostalServidoresSeq(Long cxt_seq){
		DetachedCriteria criteria= DetachedCriteria.forClass(AghCaixaPostal.class);
		
		criteria.add(Restrictions.eq(AghCaixaPostal.Fields.SEQ.toString(),cxt_seq));
	
		List<AghCaixaPostal> aghCaixaPostalAplicacaos=executeCriteria(criteria);
		
		for(AghCaixaPostal aghCaixaPostalAplicacao:aghCaixaPostalAplicacaos){
			this.remover(aghCaixaPostalAplicacao);
		}
	}
}
