package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapAfastamento;
import br.gov.mec.aghu.model.RapTipoAfastamento;

/**
 * 
 * @modulo registrocolaborador.cadastrosbasicos
 *
 */
public class RapTipoAfastamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapTipoAfastamento> {

	private static final long serialVersionUID = 4580546877353229598L;

	protected RapTipoAfastamentoDAO() {
	}
	
	public List<RapTipoAfastamento> pesquisarTipoAfastamento(String codigo,
			String descricao, DominioSituacao indSituacao, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {

		DetachedCriteria criteria = montarConsulta(codigo, descricao,
				indSituacao);

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}
	
	public List<RapTipoAfastamento> pesquisarTipoAfastamento(RapTipoAfastamento tipoAfastamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapTipoAfastamento.class);
		criteria.add(Example.create(tipoAfastamento).enableLike(MatchMode.EXACT).ignoreCase());

		return executeCriteria(criteria);
	}
	
	public Long pesquisarTipoAfastamentoCount(String codigo, String descricao,
			DominioSituacao indSituacao) {

		DetachedCriteria criteria = montarConsulta(codigo, descricao, indSituacao);
		return executeCriteriaCount(criteria);
	}
	
	public Long pesquisarTipoAfastamentoCount(RapTipoAfastamento tipoAfastamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapAfastamento.class);
		
		criteria.add(Restrictions.eq(RapAfastamento.Fields.TIPO_AFASTAMENTO.toString(), tipoAfastamento));

		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarConsulta(String codigo, String descricao,
			DominioSituacao indSituacao) {

		RapTipoAfastamento example = new RapTipoAfastamento();
		if (! StringUtils.isEmpty(descricao)) {
			example.setDescricao(StringUtils.trimToNull(descricao));
		}
		if (indSituacao != null) {
			example.setIndSituacao(indSituacao);	
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(RapTipoAfastamento.class);
		criteria.add(Example.create(example).enableLike(MatchMode.ANYWHERE).ignoreCase());
		if (! StringUtils.isEmpty(codigo)) {
			criteria.add(Restrictions.eq(RapTipoAfastamento.Fields.CODIGO.toString(), StringUtils.upperCase(codigo)));
		}
		return criteria;
	}	
}
