package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghPaisBcb;

/**
 * 
 * @see AghPaisBcb
 * 
 */
public class AghPaisBcbDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghPaisBcb> {

	

	private static final long serialVersionUID = 5774660240922174354L;
	
	
	public List<AghPaisBcb> listarPaisesBcb(String parametro, AghPaisBcb paisBrasil) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghPaisBcb.class);
		
		if (!StringUtils.isEmpty((String) parametro)) {
				criteria.add(Restrictions.ilike(AghPaisBcb.Fields.NOME.toString(), (String) parametro, MatchMode.ANYWHERE));
		}
		if(paisBrasil != null){
			criteria.add(Restrictions.ne(AghPaisBcb.Fields.SEQ.toString(), paisBrasil.getSeq()));
		}
				
		return executeCriteria(criteria);
	}

	
		
}