package br.gov.mec.aghu.compras.contaspagar.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FcpTipoTitulo;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FcpTipoTituloDAO extends BaseDao<FcpTipoTitulo> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1121796377611166696L;

	/**
	 * #47312 - SB1 - Lista
	 * @param parametro
	 * @return
	 */
	public List<FcpTipoTitulo> obterListaTipoTitulo(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTipoTitulo.class);
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property(FcpTipoTitulo.Fields.CODIGO.toString())
					.as(FcpTipoTitulo.Fields.CODIGO.toString()))
			.add(Projections.property(FcpTipoTitulo.Fields.DESCRICAO.toString())
					.as(FcpTipoTitulo.Fields.DESCRICAO.toString())));
		
		
			if (CoreUtil.isNumeroShort(parametro)) {
				criteria.add(Restrictions.eq(FcpTipoTitulo.Fields.CODIGO.toString(), Short.valueOf(parametro)));
			} else if(StringUtils.isNotBlank(parametro)) {
				criteria.add(Restrictions.ilike(FcpTipoTitulo.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		
		criteria.add(Restrictions.eq(FcpTipoTitulo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.setResultTransformer(Transformers.aliasToBean(FcpTipoTitulo.class));	
		return executeCriteria(criteria, 0, 100, FcpTipoTitulo.Fields.DESCRICAO.toString(), true);
	}
	
	/**
	 * #47312 - SB1 - Count
	 * @param parametro
	 * @return
	 */
	public Long obterCountTipoTitulo(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTipoTitulo.class);
		if (StringUtils.isNotBlank(parametro)) {
			Criterion restrictionDescricao = Restrictions.ilike(FcpTipoTitulo.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE);
			if (CoreUtil.isNumeroShort(parametro)) {
				Criterion restrictionCodigo = Restrictions.eq(FcpTipoTitulo.Fields.CODIGO.toString(), Short.valueOf(parametro));
				criteria.add(Restrictions.or(restrictionCodigo, restrictionDescricao));
			} else {
				criteria.add(restrictionDescricao);
			}
		}
		criteria.add(Restrictions.eq(FcpTipoTitulo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
	
}
