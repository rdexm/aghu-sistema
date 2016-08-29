package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MbcNecessidadeCirurgicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcNecessidadeCirurgica> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5547528218491951268L;
	
	
	public Long listarNecessidadesFiltroCount(final Short necesSeq, final String descricaoNecess, final AghUnidadesFuncionais unidFunc, final DominioSituacao situacao, final Boolean requerDescricao) {
		
		DetachedCriteria criteria = montaCriteriaNecessidadesFiltro(necesSeq, descricaoNecess, unidFunc, situacao, requerDescricao);
		
		return executeCriteriaCount(criteria);
	}

	public List<MbcNecessidadeCirurgica> listarNecessidadesFiltro(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc, 
			final Short necesSeq, final String descricaoNecess, final AghUnidadesFuncionais unidFunc, final DominioSituacao situacao, final Boolean requerDescricao) {

		DetachedCriteria criteria = montaCriteriaNecessidadesFiltro(necesSeq, descricaoNecess, unidFunc, situacao, requerDescricao);
		
		return executeCriteria(criteria, firstResult, maxResult, MbcNecessidadeCirurgica.Fields.DESCRICAO.toString(), true);
	}
	
	public DetachedCriteria montaCriteriaNecessidadesFiltro(final Short necesSeq, final String descricaoNecess, final AghUnidadesFuncionais unidFunc, final DominioSituacao situacao, final Boolean requerDescricao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcNecessidadeCirurgica.class);
		criteria.createAlias(MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		
		if(necesSeq != null){
			criteria.add(Restrictions.eq(MbcNecessidadeCirurgica.Fields.SEQ.toString(), necesSeq));
		}
		if(descricaoNecess != null && !descricaoNecess.isEmpty()){
			criteria.add(Restrictions.ilike(MbcNecessidadeCirurgica.Fields.DESCRICAO.toString(), descricaoNecess, MatchMode.ANYWHERE));
		}
		if(unidFunc != null){
			criteria.add(Restrictions.eq(MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), unidFunc));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(MbcNecessidadeCirurgica.Fields.SITUACAO.toString(), situacao));
		}
		if(requerDescricao != null){
			criteria.add(Restrictions.eq(MbcNecessidadeCirurgica.Fields.IND_EXIGE_DESC_SOLIC.toString(), requerDescricao));
		}
		
		return criteria;
	}
	
	public List<MbcNecessidadeCirurgica> buscarNecessidadeCirurgicaPorCodigoOuDescricao(String parametro, boolean somenteAtivos) {
		return executeCriteria(obtemCriteriaParaBuscarNecessidadeCirurgicaPorCodigoOuDescricao(parametro, somenteAtivos));
	}
	
	public Long countBuscarNecessidadeCirurgicaPorCodigoOuDescricao(String parametro, boolean somenteAtivos) {
		return executeCriteriaCount(obtemCriteriaParaBuscarNecessidadeCirurgicaPorCodigoOuDescricao(parametro, somenteAtivos));
	}
	
	private DetachedCriteria obtemCriteriaParaBuscarNecessidadeCirurgicaPorCodigoOuDescricao(String parametro, boolean somenteAtivos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcNecessidadeCirurgica.class);
		criteria.createAlias(MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "unf", Criteria.LEFT_JOIN);
		if (somenteAtivos) {
			criteria.add(Restrictions.eq(MbcNecessidadeCirurgica.Fields.SITUACAO.toString(), DominioSituacao.A));
		}
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroShort(parametro)) {
				final short codigo = Short.parseShort(parametro);
				criteria.add(Restrictions.eq(MbcNecessidadeCirurgica.Fields.SEQ.toString(), codigo));
			} else {
				criteria.add(Restrictions.ilike(MbcNecessidadeCirurgica.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
}
