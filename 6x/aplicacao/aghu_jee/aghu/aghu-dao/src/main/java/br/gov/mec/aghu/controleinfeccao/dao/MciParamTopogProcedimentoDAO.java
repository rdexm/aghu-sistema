package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MciParamTopogProcedimento;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;


public class MciParamTopogProcedimentoDAO extends BaseDao<MciParamTopogProcedimento> {

	private static final long serialVersionUID = -7939747145417018310L;
	
	public List<MciParamTopogProcedimento> listarPorTopSeq(Short toiSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciParamTopogProcedimento.class);
		criteria.add(Restrictions.eq(MciParamTopogProcedimento.Fields.MCI_TOPOGRAFIA_PROCEDIMENTO_SEQ.toString(), toiSeq));
		return executeCriteria(criteria);
	}

}
