package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class AelGrupoExameUsualDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoExameUsual> {


	private static final long serialVersionUID = -8645848507995484050L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoExameUsual.class);
		return criteria;
    }

	public List<AelGrupoExameUsual> obterGrupoPorCodigoDescricao(String parametro) {
		DetachedCriteria criteria = obterCriteria();
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(AelGrupoExameUsual.Fields.SEQ.toString(), Integer.parseInt(parametro)));
			} else {
				criteria.add(Restrictions.ilike(AelGrupoExameUsual.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}		
		}
		criteria.addOrder(Order.asc(AelGrupoExameUsual.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}	
	
	/**
	 * 
	 * @param filtro
	 * @return
	 */
	public Long pesquisaGrupoExamesUsuaisCount(Integer seq, String descricao, DominioSituacao situacao){
		return executeCriteriaCount(this.obterGrupoExamesUsuais(seq, descricao, situacao));
	}
	
	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return
	 */
	public List<AelGrupoExameUsual> pesquisaGrupoExamesUsuais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer seq, 
			String descricao, DominioSituacao situacao) {
		return executeCriteria(this.obterGrupoExamesUsuais(seq,descricao,situacao), firstResult, maxResult, orderProperty, asc);
	}
	
	private DetachedCriteria obterGrupoExamesUsuais(Integer seq, String descricao, DominioSituacao situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoExameUsual.class);
		if (seq!=null){
			criteria.add(Restrictions.eq(AelGrupoExameUsual.Fields.SEQ.toString(), seq));
		}
		if (descricao!=null){
			criteria.add(Restrictions.ilike(AelGrupoExameUsual.Fields.DESCRICAO.toString(), descricao,MatchMode.ANYWHERE));
		}
		if (situacao!=null){
			criteria.add(Restrictions.eq(AelGrupoExameUsual.Fields.SITUACAO.toString(), situacao));
		}
		return criteria;
	}
}