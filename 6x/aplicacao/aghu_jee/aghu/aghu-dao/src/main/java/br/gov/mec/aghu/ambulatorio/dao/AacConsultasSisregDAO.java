package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AacConsultasSisreg;

public class AacConsultasSisregDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacConsultasSisreg> {
		
	private static final long serialVersionUID = 2089394693984132195L;

	public List<AacConsultasSisreg> pesquisarConsultasSisreg(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultasSisreg.class);
		return executeCriteria(criteria);
	}
	
	public List<AacConsultasSisreg> pesquisarConsultasSisregNaoImportadas(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultasSisreg.class);
		criteria.add(Restrictions.eq(AacConsultasSisreg.Fields.IND_MARCADO.toString(),false));
		return executeCriteria(criteria);
	}
	
	public void unirTransacao(){
		joinTransaction();
	}
}