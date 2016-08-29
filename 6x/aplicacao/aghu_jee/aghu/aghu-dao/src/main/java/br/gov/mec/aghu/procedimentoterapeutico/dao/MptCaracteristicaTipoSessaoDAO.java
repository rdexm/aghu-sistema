package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MptCaracteristica;
import br.gov.mec.aghu.model.MptCaracteristicaTipoSessao;
import br.gov.mec.aghu.model.MptTipoSessao;


public class MptCaracteristicaTipoSessaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptCaracteristicaTipoSessao>{

	private static final long serialVersionUID = 843549845L;

	public boolean existeCaracteristicaTipoSessao(String sigla, Short tpsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristicaTipoSessao.class, "CTS");
		criteria.createAlias("CTS." + MptCaracteristicaTipoSessao.Fields.CAR.toString(), "CAR", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("CTS." + MptCaracteristicaTipoSessao.Fields.TPS_SEQ.toString(), tpsSeq));
		criteria.add(Restrictions.eq("CAR." + MptCaracteristica.Fields.SIGLA.toString(), sigla));
		
		return executeCriteriaExists(criteria);
	}

	/**C4 #46468 **/
	public boolean existeVinculoEntreCaracteristicaTipoSessao(Short carSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristicaTipoSessao.class);
		criteria.add(Restrictions.eq(MptCaracteristicaTipoSessao.Fields.CAR_SEQ.toString(), carSeq));
		return executeCriteriaExists(criteria);
	}

	public MptCaracteristicaTipoSessao obterVinculoCaracteristicaTipoSessao(MptCaracteristica mptCaracteristica, MptTipoSessao mptTipoSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristicaTipoSessao.class, "CTS");
		
		criteria.createAlias("CTS." + MptCaracteristicaTipoSessao.Fields.CAR.toString(),"CARD",JoinType.INNER_JOIN);
		criteria.createAlias("CTS." + MptCaracteristicaTipoSessao.Fields.TPS.toString(),"TPST",JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("CTS."+MptCaracteristicaTipoSessao.Fields.CAR_SEQ.toString(), mptCaracteristica.getSeq()));
		criteria.add(Restrictions.eq("CTS."+MptCaracteristicaTipoSessao.Fields.TPS_SEQ.toString(), mptTipoSessao.getSeq()));
		return (MptCaracteristicaTipoSessao) executeCriteriaUniqueResult(criteria);
	}
	
	public MptCaracteristicaTipoSessao obterCaracteristicaTipoSessao(MptTipoSessao mptTipoSessao, MptCaracteristicaTipoSessao mptCaracteristicaTipoSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristicaTipoSessao.class, "CTS");
		
		criteria.createAlias("CTS." + MptCaracteristicaTipoSessao.Fields.CAR.toString(),"CARD",JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(MptCaracteristicaTipoSessao.Fields.TPS.toString(), mptTipoSessao));
		
		return (MptCaracteristicaTipoSessao) executeCriteria(criteria);
	}
	
	public List<MptCaracteristicaTipoSessao> obterCaracteristicaTipoSessaoPorTpsSeq(MptTipoSessao mptTipoSessao){

		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristicaTipoSessao.class, "CTS");	
		criteria.createAlias("CTS."+MptCaracteristicaTipoSessao.Fields.CAR.toString(), "CAR", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("CTS."+MptCaracteristicaTipoSessao.Fields.TPS_SEQ_S.toString(), mptTipoSessao.getSeq()));
		return executeCriteria(criteria);
	}
	
	public MptCaracteristicaTipoSessao obterCaracteristicaTipoSessaoPorCarSeq(MptCaracteristica mptCaracteristica){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristicaTipoSessao.class);
		criteria.add(Restrictions.eq(MptCaracteristicaTipoSessao.Fields.CAR_SEQ.toString(), mptCaracteristica.getSeq()));
		return (MptCaracteristicaTipoSessao) executeCriteriaUniqueResult(criteria);
	}
	
	public Long validarExclusaoTipoSessao(Short tpsSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCaracteristicaTipoSessao.class);
		criteria.add(Restrictions.eq(MptCaracteristicaTipoSessao.Fields.TPS_SEQ.toString(), tpsSeq));
		return executeCriteriaCount(criteria);
	}
}
