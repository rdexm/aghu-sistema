package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AinTiposMvtoInternacao;

public class AinTiposMvtoInternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinTiposMvtoInternacao> {

	private static final long serialVersionUID = 3861552178125933675L;

	/**
	 * Metodo que busca a descricao do TipoMovtoIntercao pela PK.<br>
	 * @deprecated Manter compatibilidade com codigo antigo. Onde o codigo ainda eh tratado como Byte. USAR CODIGO INTEGER.
	 * 
	 * @param codigo
	 * @return
	 */
	public String obterDescricaoTipoMovtoInternacao(Byte codigo) {
		String returnValue = "";
		if (codigo != null) {
			returnValue = this.obterDescricaoTipoMovtoInternacao(codigo.intValue());
		}
		return returnValue;
	}
	
	/**
	 * Metodo que busca a descricao do TipoMovtoIntercao pela PK.<br>
	 * 
	 * @param codigo
	 * @return
	 */
	public String obterDescricaoTipoMovtoInternacao(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMvtoInternacao.class);
		if (codigo != null) {
			criteria.add(Restrictions.eq(AinTiposMvtoInternacao.Fields.CODIGO.toString(), codigo));
		}
		
		criteria.setProjection(Projections.projectionList().add(
			// 0 
			Projections.property(AinTiposMvtoInternacao.Fields.DESCRICAO.toString()))
		);
		
		String descricao = "";
		List<String> res = executeCriteria(criteria, 0, 1, null, true);
		if (res != null && !res.isEmpty()) {
			descricao = res.get(0);
		}
		
		return descricao;
	}

	public List<AinTiposMvtoInternacao> pesquisaTiposMvtoInternacao(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigo, String descricao) {
		DetachedCriteria criteria = createPesquisaTiposMvtoInternacaoCriteria(
				codigo, descricao);

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	public Long pesquisaTiposMvtoInternacaoCount(Integer codigo,
			String descricao) {
		return executeCriteriaCount(createPesquisaTiposMvtoInternacaoCriteria(
				codigo, descricao));
	}

	private DetachedCriteria createPesquisaTiposMvtoInternacaoCriteria(
			Integer codigo, String descricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinTiposMvtoInternacao.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					AinTiposMvtoInternacao.Fields.CODIGO.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AinTiposMvtoInternacao.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	public AinTiposMvtoInternacao obterTiposMvtoInternacao(
			Integer ainTiposMvtoInternacaoCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMvtoInternacao.class);
		criteria.add(Restrictions.eq(AinTiposMvtoInternacao.Fields.CODIGO.toString(), ainTiposMvtoInternacaoCodigo));
		AinTiposMvtoInternacao retorno = (AinTiposMvtoInternacao) executeCriteriaUniqueResult(criteria);
		return retorno;
	}

	public Integer obterCodigoAinTiposMvtoInternacaoPorDescricao(String descricao) {
		Integer codigo = null;
		if (StringUtils.isNotBlank(descricao)) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AinTiposMvtoInternacao.class);
			criteria.add(Restrictions.eq(AinTiposMvtoInternacao.Fields.DESCRICAO.toString(), descricao));
			criteria.setProjection(Projections.property(AinTiposMvtoInternacao.Fields.CODIGO.toString()));
			codigo = (Integer) executeCriteriaUniqueResult(criteria);
		}
		return codigo;
	}

	public String buscarPorParametroNumerico(String nomeParam) {
		StringBuffer hql = new StringBuffer(140);
		hql.append(" select mvto.descricao ");
		hql.append(" from AghParametros par, ");
		hql.append(" 	AinTiposMvtoInternacao mvto ");
		hql.append(" where par.vlrNumerico = mvto.codigo ");
		hql.append(" 	and par.nome = :param ");
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("param", nomeParam);
		return (String) query.uniqueResult();
	}
}
