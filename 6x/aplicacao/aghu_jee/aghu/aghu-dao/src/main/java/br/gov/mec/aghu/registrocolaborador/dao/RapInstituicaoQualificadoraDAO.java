package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapInstituicaoQualificadora;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @modulo registrocolaborador.cadastrosbasicos
 *
 */
public class RapInstituicaoQualificadoraDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapInstituicaoQualificadora> {

	private static final long serialVersionUID = -1046764557447296891L;

	protected RapInstituicaoQualificadoraDAO() {
	}
	
	public List<RapInstituicaoQualificadora> pesquisarInstituicaoQualificadora(
			Integer codigo, String descricao, DominioSimNao indInterno,
			Boolean indUsoGppg, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {

		DetachedCriteria criteria = montarConsulta(codigo, descricao,
				indInterno, indUsoGppg);

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}
	
	public Long pesquisarInstituicaoQualificadoraCount(Integer codigo,
			String descricao, DominioSimNao indInterno, Boolean indUsoGppg) {

		DetachedCriteria criteria = montarConsulta(codigo, descricao, indInterno, indUsoGppg);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarConsulta(Integer codigo, String descricao,
			DominioSimNao indInterno, Boolean indUsoGppg) {

		RapInstituicaoQualificadora example = new RapInstituicaoQualificadora();
		if (descricao != null) {
			example.setDescricao(StringUtils.trimToNull(descricao));
		}
		example.setIndInterno(indInterno);
		example.setIndUsoGppg(indUsoGppg);	
		DetachedCriteria criteria = DetachedCriteria.forClass(RapInstituicaoQualificadora.class);
		criteria.add(Example.create(example).enableLike(MatchMode.ANYWHERE).ignoreCase());
		if (codigo != null) {
			criteria.add(Restrictions.eq(RapInstituicaoQualificadora.Fields.CODIGO.toString(), codigo));
		}
		return criteria;
	}
	
	/**
	 * Retorna instituições qualificadoras com os parâmetros fornecidos.
	 * 
	 * @param instituicaoObject
	 * @return retorna apenas os 100 primeiros
	 */
	public List<RapInstituicaoQualificadora> pesquisarInstituicao(
			Object instituicaoObject) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapInstituicaoQualificadora.class);

		String stParametro = (String) instituicaoObject;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(stParametro)) {
			codigo = Integer.valueOf(stParametro);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					RapInstituicaoQualificadora.Fields.CODIGO.toString(),
					codigo));
		} else {
			criteria.add(Restrictions.ilike(
					RapInstituicaoQualificadora.Fields.DESCRICAO.toString(),
					stParametro, MatchMode.ANYWHERE));
		}

		// order by
		criteria.addOrder(Order.asc(RapInstituicaoQualificadora.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
}
