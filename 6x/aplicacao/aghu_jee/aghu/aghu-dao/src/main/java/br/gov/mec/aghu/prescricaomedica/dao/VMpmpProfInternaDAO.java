package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.VMpmpProfInterna;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class VMpmpProfInternaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMpmpProfInterna>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -396706907496007491L;

	
	/** Obtém criteria para consulta de VMpmpProfInterna para Suggestion Box 05.
	 * #1291
	 * @param parametro {@link Object}
	 * @return {@link List<VMpmpProfInterna>} */
	public List<VMpmpProfInterna> pesquisarVMpmpProfInternaSB5(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmpProfInterna.class);

		criteria.add(Restrictions.eq(VMpmpProfInterna.Fields.IND_SITUACAO.toString(), "A" ));
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(VMpmpProfInterna.Fields.MATRICULA.toString(), Integer.valueOf((String) strPesquisa)));
				if(executeCriteria(criteria) != null && executeCriteria(criteria).size() == 1){
					ProjectionList p = Projections.projectionList();
					p.add(Projections.property(VMpmpProfInterna.Fields.MATRICULA.toString()), VMpmpProfInterna.Fields.MATRICULA.toString());
					p.add(Projections.property(VMpmpProfInterna.Fields.RESPONSAVEL.toString()), VMpmpProfInterna.Fields.RESPONSAVEL.toString());
					p.add(Projections.property(VMpmpProfInterna.Fields.VIN_CODIGO.toString()), VMpmpProfInterna.Fields.VIN_CODIGO.toString());
					criteria.setProjection(p);
					criteria.setResultTransformer(Transformers.aliasToBean(VMpmpProfInterna.class));
					return this.executeCriteria(criteria, 0, 100, VMpmpProfInterna.Fields.RESPONSAVEL.toString(), true);
				}else{
					criteria = null;
					criteria = DetachedCriteria.forClass(VMpmpProfInterna.class);
					criteria.add(Restrictions.ilike(VMpmpProfInterna.Fields.RESPONSAVEL
							.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
				}
			}else{ 
				criteria = null;
				criteria = DetachedCriteria.forClass(VMpmpProfInterna.class);
				criteria.add(Restrictions.ilike(VMpmpProfInterna.Fields.RESPONSAVEL
						.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(VMpmpProfInterna.Fields.MATRICULA.toString()), VMpmpProfInterna.Fields.MATRICULA.toString());
		p.add(Projections.property(VMpmpProfInterna.Fields.RESPONSAVEL.toString()), VMpmpProfInterna.Fields.RESPONSAVEL.toString());
		p.add(Projections.property(VMpmpProfInterna.Fields.VIN_CODIGO.toString()), VMpmpProfInterna.Fields.VIN_CODIGO.toString());
		criteria.setProjection(p);
		
		criteria.setResultTransformer(Transformers.aliasToBean(VMpmpProfInterna.class));
		return this.executeCriteria(criteria, 0, 100, VMpmpProfInterna.Fields.RESPONSAVEL.toString(), true);
	}
	
	public Long pesquisarVMpmpProfInternaSB5Count(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmpProfInterna.class);

		criteria.add(Restrictions.eq(VMpmpProfInterna.Fields.IND_SITUACAO.toString(), "A" ));
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(VMpmpProfInterna.Fields.MATRICULA.toString(), Integer.valueOf((String) strPesquisa)));
				if(executeCriteria(criteria) != null && executeCriteria(criteria).size() == 1){
					return this.executeCriteriaCount(criteria);
				}else{
					criteria = null;
					criteria = DetachedCriteria.forClass(VMpmpProfInterna.class);
					criteria.add(Restrictions.ilike(VMpmpProfInterna.Fields.RESPONSAVEL
							.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
				}
			}else{ 
				criteria = null;
				criteria = DetachedCriteria.forClass(VMpmpProfInterna.class);
				criteria.add(Restrictions.ilike(VMpmpProfInterna.Fields.RESPONSAVEL
						.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		return this.executeCriteriaCount(criteria);
	}
	
}
