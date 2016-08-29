package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.RapGrupoFuncional;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
/**
 * 
 * @modulo registrocolaborador.cadastrosbasicos
 *
 */
public class RapGrupoFuncionalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapGrupoFuncional> {
	
	private static final long serialVersionUID = -3466168048975594319L;

	public enum GrupoFuncionalDAOExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_DESCRICAO_GRUPO_FUNCIONAL_JA_EXISTE, MENSAGEM_ERRO_CODIGO_GRUPO_FUNCIONAL_JA_EXISTE, ERRO_ALTERAR_CONSTRAINT_GRUPO_FUNCIONAL, MENSAGEM_GRUPO_FUNCIONAL_NAO_INFORMADO;
	}	

	protected RapGrupoFuncionalDAO() {
	}
	
	private DetachedCriteria montarConsulta(Short codigo, String descricao) {

		DetachedCriteria criteriaMontagem = DetachedCriteria
				.forClass(RapGrupoFuncional.class);

		// Código
		if (codigo != null) {
			criteriaMontagem.add(Restrictions.eq(
					RapGrupoFuncional.Fields.CODIGO.toString(), codigo));
		}

		// Descrição
		if (StringUtils.isNotBlank(descricao)) {
			criteriaMontagem.add(Restrictions.ilike(
					RapGrupoFuncional.Fields.DESCRICAO.toString(), descricao,
					MatchMode.ANYWHERE));
		}
		
		return criteriaMontagem;
	}
	
	public Long montarConsultaCount(Short codigo, String descricao) {
		DetachedCriteria criteria = montarConsulta(codigo, descricao);
		return executeCriteriaCount(criteria);
	}
	
	public List<RapGrupoFuncional> pesquisarGrupoFuncional(Short codigo,
			String descricao, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {

		DetachedCriteria criteriaFiltro = montarConsulta(codigo, descricao);
		criteriaFiltro.addOrder(Order.asc(RapGrupoFuncional.Fields.CODIGO
				.toString()));
		return executeCriteria(criteriaFiltro, firstResult, maxResult,
				orderProperty, asc);
	}

	
	public Long countRapGrupoFuncionalDuplicado(RapGrupoFuncional rapGrupoFuncional){
		RapGrupoFuncional example = new RapGrupoFuncional();
		example.setDescricao(rapGrupoFuncional.getDescricao());

		DetachedCriteria criteria = DetachedCriteria.forClass(RapGrupoFuncional.class);
		criteria.add(Restrictions.eq(RapGrupoFuncional.Fields.DESCRICAO.toString(), example.getDescricao()));

		criteria.add(Restrictions.not(Restrictions.eq(RapGrupoFuncional.Fields.CODIGO.toString(), rapGrupoFuncional.getCodigo())));
		
		return executeCriteriaCount(criteria);
	} 
	
	/**
	 * Retorna um lista de grupos funcionais encontrados com a string fornecida no atributo descrição.
	 */
	public List<RapGrupoFuncional> pesquisarGrupoFuncional(
			Object grupoFuncional) {

		DetachedCriteria criteria = DetachedCriteria.forClass(RapGrupoFuncional.class);

		String stParametro = (String) grupoFuncional;
		Short codigo = null;

		if (CoreUtil.isNumeroShort(stParametro)) {
			codigo = Short.valueOf(stParametro);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					RapGrupoFuncional.Fields.CODIGO.toString(), codigo));

		} else {
			criteria.add(Restrictions.ilike(
					RapGrupoFuncional.Fields.DESCRICAO.toString(), stParametro,
					MatchMode.ANYWHERE));
		}

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	
	public Long countDescricaoRapGrupoFuncional(RapGrupoFuncional rapGrupoFuncional){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapGrupoFuncional.class);
		criteria.add(Restrictions.eq(RapGrupoFuncional.Fields.DESCRICAO.toString(), rapGrupoFuncional.getDescricao()));
		return executeCriteriaCount(criteria);
	}	
}