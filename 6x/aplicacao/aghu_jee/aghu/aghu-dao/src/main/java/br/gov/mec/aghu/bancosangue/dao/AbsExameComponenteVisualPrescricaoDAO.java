package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AbsExameComponenteVisualPrescricao;

public class AbsExameComponenteVisualPrescricaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsExameComponenteVisualPrescricao> {

	private static final long serialVersionUID = -7107904515958307836L;

	public List<AbsExameComponenteVisualPrescricao> buscarExamesComponenteVisualPrescricao(
			String codigoComponenteSanguineo,
			String codigoProcedimentoHemoterapico) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsExameComponenteVisualPrescricao.class);

		if (StringUtils.isNotBlank(codigoComponenteSanguineo)) {
			criteria
					.add(Restrictions
							.ilike(
									AbsExameComponenteVisualPrescricao.Fields.COMPONENTE_SANGUINEO_CODIGO
											.toString(),
									codigoComponenteSanguineo, MatchMode.EXACT));
		} else if (StringUtils.isNotBlank(codigoProcedimentoHemoterapico)) {
			criteria
					.add(Restrictions
							.ilike(
									AbsExameComponenteVisualPrescricao.Fields.PROCEDIMENTO_HEMOTERAPICO_CODIGO
											.toString(),
									codigoProcedimentoHemoterapico,
									MatchMode.EXACT));
		}

		return executeCriteria(criteria);
	}
	
	/**
	 * #6389
	 * listaExameComponenteVisualPrescricaoCount
	 * @author lucasbuzzo
	 * @param AbsExameComponenteVisualPrescricao exameComponenteVisualPrescricao
	 * @return MpmCidUnidFuncional
	 */
	public Long listaExameComponenteVisualPrescricaoCount(AbsExameComponenteVisualPrescricao exameComponenteVisualPrescricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsExameComponenteVisualPrescricao.class);
		
		if(exameComponenteVisualPrescricao.getSeq() != null){
			criteria.add(Restrictions.eq(AbsExameComponenteVisualPrescricao.Fields.SEQ.toString(), exameComponenteVisualPrescricao.getSeq()));
		}
		if(exameComponenteVisualPrescricao.getCampoLaudo() != null){
			criteria.add(Restrictions.eq(AbsExameComponenteVisualPrescricao.Fields.CAMPO_LAUDO.toString(), exameComponenteVisualPrescricao.getCampoLaudo()));
		}
		if(exameComponenteVisualPrescricao.getComponenteSanguineo() != null){
			criteria.add(Restrictions.eq(AbsExameComponenteVisualPrescricao.Fields.COMPONENTE_SANGUINEO.toString(), exameComponenteVisualPrescricao.getComponenteSanguineo()));
		}	
		return executeCriteriaCount(criteria);
	}
	
	public List<AbsExameComponenteVisualPrescricao> listaExameComponenteVisualPrescricao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AbsExameComponenteVisualPrescricao exameComponenteVisualPrescricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsExameComponenteVisualPrescricao.class);
		
		if(exameComponenteVisualPrescricao.getSeq() != null){
			criteria.add(Restrictions.eq(AbsExameComponenteVisualPrescricao.Fields.SEQ.toString(), exameComponenteVisualPrescricao.getSeq()));
		}
		if(exameComponenteVisualPrescricao.getCampoLaudo() != null){
			criteria.add(Restrictions.eq(AbsExameComponenteVisualPrescricao.Fields.CAMPO_LAUDO.toString(), exameComponenteVisualPrescricao.getCampoLaudo()));
		}
		if(exameComponenteVisualPrescricao.getComponenteSanguineo() != null){
			criteria.add(Restrictions.eq(AbsExameComponenteVisualPrescricao.Fields.COMPONENTE_SANGUINEO.toString(), exameComponenteVisualPrescricao.getComponenteSanguineo()));
		}	

		
		criteria.createAlias(AbsExameComponenteVisualPrescricao.Fields.CAMPO_LAUDO.toString(), "LAUDO", JoinType.INNER_JOIN);
		criteria.createAlias(AbsExameComponenteVisualPrescricao.Fields.COMPONENTE_SANGUINEO.toString(), "CS", JoinType.INNER_JOIN);
		
		return executeCriteria(criteria, firstResult, maxResult, "seq"  , true);
	}
	
	public Boolean verificaAbsExameComponenteVisualPrescricaoDuplicado(AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao){
		List<AbsExameComponenteVisualPrescricao> result = null;

		DetachedCriteria criteria = DetachedCriteria.forClass(AbsExameComponenteVisualPrescricao.class);
		criteria.add(Restrictions.eq(AbsExameComponenteVisualPrescricao.Fields.SEQ.toString(), absExameComponenteVisualPrescricao.getSeq()));
		
		result = executeCriteria(criteria);
		if(result.size()>0){
			return true;
		}else{
			return false;
		}

	}
	
	public AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricaoId(Integer id){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsExameComponenteVisualPrescricao.class);
		
			criteria.add(Restrictions.eq(AbsExameComponenteVisualPrescricao.Fields.SEQ.toString(), id));
		
		return  (AbsExameComponenteVisualPrescricao) executeCriteriaUniqueResult(criteria);
	}
	
	

}
