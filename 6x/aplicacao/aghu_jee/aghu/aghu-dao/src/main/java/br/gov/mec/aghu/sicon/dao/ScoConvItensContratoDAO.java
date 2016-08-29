package br.gov.mec.aghu.sicon.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.ScoConvItensContrato;
import br.gov.mec.aghu.model.ScoItensContrato;
/**
 * @modulo sicon
 * @author cvagheti
 *
 */
public class ScoConvItensContratoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoConvItensContrato> {

	
	private static final long serialVersionUID = 3148172174111677538L;

	public List<ScoConvItensContrato> getItensContratoConvenioByContrato(ScoItensContrato input){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoConvItensContrato.class);		
		criteria.createAlias(ScoConvItensContrato.Fields.FSO_CONV_FINANC.toString(), "fsco_conv_financ", JoinType.LEFT_OUTER_JOIN);		
		criteria.add(Restrictions.eq(ScoConvItensContrato.Fields.ITEM_CONT_SEQ.toString(),input.getSeq()));
		return executeCriteria(criteria);
	}
	
}
