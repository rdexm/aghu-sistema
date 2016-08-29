package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MciCidNotificacao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MciCidNotificacaoDAO extends BaseDao<MciCidNotificacao> {

	private static final long serialVersionUID = 3955739585489432183L;
	
	public List<MciCidNotificacao> listarPorToiSeq(Short toiSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciCidNotificacao.class);
		criteria.add(Restrictions.eq(MciCidNotificacao.Fields.TOPOGRAFIA_INFECCAO_SEQ.toString(), toiSeq));
		return executeCriteria(criteria);
	}
	
	public List<MciCidNotificacao> listarPorTopSeq(Short toiSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciCidNotificacao.class);
		criteria.add(Restrictions.eq(MciCidNotificacao.Fields.TOPOGRAFIA_PROCEDIMENTO_SEQ.toString(), toiSeq));
		return executeCriteria(criteria);
	}

}
