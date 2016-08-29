package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MciParamTopogInfeccao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;


public class MciParamTopogInfeccaoDAO extends BaseDao<MciParamTopogInfeccao> {

	private static final long serialVersionUID = -7939747145417018310L;
	
	public List<MciParamTopogInfeccao> listarPorToiSeq(Short toiSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciParamTopogInfeccao.class);
		criteria.add(Restrictions.eq(MciParamTopogInfeccao.Fields.MCI_TOPOGRAFIA_INFECCAO_SEQ.toString(), toiSeq));
		return executeCriteria(criteria);
	}

}
