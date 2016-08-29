package br.gov.mec.aghu.centrocusto.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FcuAgrupaGrupoMaterial;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO de {@link FcuAgrupaGrupoMaterial}
 * 
 * @author luismoura
 * 
 */
public class FcuAgrupaGrupoMaterialDAO extends BaseDao<FcuAgrupaGrupoMaterial> {
	private static final long serialVersionUID = -5646106548688737218L;

	/**
	 * C4 de #31584
	 * 
	 * @param param
	 * @return
	 */
	public List<FcuAgrupaGrupoMaterial> pesquisarFcuAgrupaGrupoMaterialAtivos(String param, Integer maxResults) {
		DetachedCriteria criteria = this.montarCriteriaFcuAgrupaGrupoMaterialAtivos(param);
		if (maxResults != null) {
			return super.executeCriteria(criteria, 0, maxResults, FcuAgrupaGrupoMaterial.Fields.DESCRICAO.toString(), true);
		}
		return super.executeCriteria(criteria);
	}

	/**
	 * C4 de #31584
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarFcuAgrupaGrupoMaterialAtivosCount(String param) {
		DetachedCriteria criteria = this.montarCriteriaFcuAgrupaGrupoMaterialAtivos(param);
		return executeCriteriaCount(criteria);
	}

	/**
	 * C4 de #31584
	 * 
	 * @param param
	 * @return
	 */
	private DetachedCriteria montarCriteriaFcuAgrupaGrupoMaterialAtivos(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcuAgrupaGrupoMaterial.class);
		criteria.add(Restrictions.eq(FcuAgrupaGrupoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
		if (StringUtils.isNotBlank(param)) {
			if (CoreUtil.isNumeroShort(param)) {
				criteria.add(Restrictions.eq(FcuAgrupaGrupoMaterial.Fields.SEQ.toString(), Short.valueOf(param)));
			} else {
				criteria.add(Restrictions.ilike(FcuAgrupaGrupoMaterial.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
}