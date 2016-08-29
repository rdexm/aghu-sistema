package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoSaps3;
import br.gov.mec.aghu.model.MpmEscoreSaps3;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MpmEscoreSaps3DAO extends BaseDao<MpmEscoreSaps3> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1100109485897421626L;

	public List<MpmEscoreSaps3> pesquisarEscorePorAtendimento(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmEscoreSaps3.class);
		criteria.add(Restrictions.eq(MpmEscoreSaps3.Fields.ATD_SEQ.toString(), atdSeq));
		return executeCriteria(criteria);
	}
	
	public List<MpmEscoreSaps3> pesquisarEscorePendentePorAtendimento(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmEscoreSaps3.class);
		criteria.add(Restrictions.eq(MpmEscoreSaps3.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmEscoreSaps3.Fields.IND_SITUACAO.toString(), DominioSituacaoSaps3.P));
		return executeCriteria(criteria);
	}

	public MpmEscoreSaps3 pesquisarEscorePorSeq(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmEscoreSaps3.class,"MPS");
		criteria.createAlias("MPS."+MpmEscoreSaps3.Fields.ATENDIMENTO.toString(),"ATT", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MPS."+MpmEscoreSaps3.Fields.SEQ.toString(), seq));
		return (MpmEscoreSaps3) executeCriteriaUniqueResult(criteria);
	}

}
