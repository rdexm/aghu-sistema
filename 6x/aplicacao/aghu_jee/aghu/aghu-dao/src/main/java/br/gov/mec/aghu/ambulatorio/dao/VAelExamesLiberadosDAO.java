package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAelExamesLiberados;

public class VAelExamesLiberadosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelExamesLiberados> {

	private static final long serialVersionUID = 4204856372487400L;

	public List<VAelExamesLiberados> obterExamesLiberadosPorCodigo(Integer pacCodigo, Short unfSeq){

		DetachedCriteria criteria = DetachedCriteria.forClass(VAelExamesLiberados.class);
		if(unfSeq != null){
			criteria.add(Restrictions.eq(VAelExamesLiberados.Fields.UNF_SEQ.toString(), unfSeq.intValue()));

		}
		criteria.add(Restrictions.eq(VAelExamesLiberados.Fields.PAC_CODIGO.toString(), pacCodigo));
		return executeCriteria(criteria);
	}
	
	

}