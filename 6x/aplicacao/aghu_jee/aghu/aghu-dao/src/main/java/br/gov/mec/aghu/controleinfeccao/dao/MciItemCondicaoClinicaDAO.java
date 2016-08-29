package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MciItemCondicaoClinica;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MciItemCondicaoClinicaDAO extends BaseDao<MciItemCondicaoClinica> {

	private static final long serialVersionUID = 2401071951361082672L;
	
	public List<MciItemCondicaoClinica> listarPorTopSeq(Short toiSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciItemCondicaoClinica.class);
		criteria.add(Restrictions.eq(MciItemCondicaoClinica.Fields.MCI_TOPOGRAFIA_PROCEDIMENTO_SEQ.toString(), toiSeq));
		return executeCriteria(criteria);
	}

}
