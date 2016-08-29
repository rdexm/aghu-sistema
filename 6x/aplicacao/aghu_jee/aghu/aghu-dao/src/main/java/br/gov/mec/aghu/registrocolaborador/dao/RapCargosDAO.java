package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapCargos;

/**
 * 
 * @modulo registrocolaborador.cadastrosbasicos
 *
 */
public class RapCargosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapCargos> {

	private static final long serialVersionUID = 8295719078174519759L;

	protected RapCargosDAO() {
	}
	
	public List<RapCargos> pesquisarCargo(String codigo, String descricao,
			DominioSituacao situacao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {

		DetachedCriteria criteriaFiltro = montarConsulta(codigo, descricao,
				situacao);

		return executeCriteria(criteriaFiltro, firstResult, maxResult,
				orderProperty, asc);
	}
	
	public RapCargos obterDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapCargos.class);

		if (descricao != null && !descricao.equalsIgnoreCase("")) {
			criteria.add(Restrictions.eq(RapCargos.Fields.DESCRICAO.toString(),
					descricao));
		}
		return (RapCargos) executeCriteriaUniqueResult(criteria);
	}	
	
	private DetachedCriteria montarConsulta(String codigo, String descricao,
			DominioSituacao situacao) {

		DetachedCriteria criteriaMontagem = DetachedCriteria
				.forClass(RapCargos.class);

		RapCargos example = new RapCargos();

		example.setDescricao(StringUtils.trimToNull(descricao));
		example.setSituacao(situacao);

		criteriaMontagem.add(Example.create(example).enableLike(
				MatchMode.ANYWHERE).ignoreCase());

		if (codigo != null) {
			criteriaMontagem.add(Restrictions.ilike(RapCargos.Fields.CODIGO
					.toString(), codigo, MatchMode.ANYWHERE));
		}
		return criteriaMontagem;
	}
	
	public Long pesquisarCargoCount(String codigo, String descricao,
			DominioSituacao situacao) {

		DetachedCriteria criteria = montarConsulta(codigo, descricao, situacao);
		
		return executeCriteriaCount(criteria);
	}
	
}
