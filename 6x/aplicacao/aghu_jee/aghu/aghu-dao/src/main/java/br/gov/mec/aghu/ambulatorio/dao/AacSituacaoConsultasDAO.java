package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.ambulatorio.vo.FiltroConsultaBloqueioConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroParametrosPadraoConsultaVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacSituacaoConsultas;

public class AacSituacaoConsultasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacSituacaoConsultas>{

	private static final long serialVersionUID = -2173618050668874781L;

	public AacSituacaoConsultas obterSituacaoConsultaPeloId(String situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacSituacaoConsultas.class);
		criteria.add(Restrictions.eq(AacSituacaoConsultas.Fields.SITUACAO.toString(), situacao));
		return (AacSituacaoConsultas) executeCriteriaUniqueResult(criteria);
	}	
	
	public DetachedCriteria montarQuerySituacaoPorSiglaAtiva(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacSituacaoConsultas.class);
		String sigla = StringUtils.trimToNull(parametro);
		if (StringUtils.isNotEmpty(sigla)) {
			criteria.add(Restrictions.ilike(AacSituacaoConsultas.Fields.SITUACAO.toString(), sigla, MatchMode.EXACT));
		}
		criteria.add(Restrictions.eq(AacSituacaoConsultas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	public DetachedCriteria montarQuerySituacaoSemMarcadaPorSiglaAtiva(String parametro) {
		DetachedCriteria criteria = montarQuerySituacaoPorSiglaAtiva(parametro);
		criteria.add(Restrictions.ne(AacSituacaoConsultas.Fields.SITUACAO.toString(), "M"));
		return criteria;
	}
	
	public DetachedCriteria montarQuerySituacaoPorDescricaoAtiva(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacSituacaoConsultas.class);
		if(!StringUtils.isBlank(parametro)) {
			Criterion descricao = Restrictions.ilike(AacSituacaoConsultas.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE);
			Criterion situacao = Restrictions.ilike(AacSituacaoConsultas.Fields.SITUACAO.toString(), parametro, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(descricao, situacao));
		}
		criteria.add(Restrictions.eq(AacSituacaoConsultas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	public DetachedCriteria montarQuerySituacaoSemMarcadaPorDescricaoAtiva(String parametro) {
		DetachedCriteria criteria = montarQuerySituacaoPorDescricaoAtiva(parametro);
		criteria.add(Restrictions.ne(AacSituacaoConsultas.Fields.SITUACAO.toString(), "M"));
		return criteria;
	}
	
	/**
	 * Retorna a lista de situações de consultas ativas.
	 * Utiliza sigla como critério de pesquisa.
	 * 
	 * @param parametro
	 * @return List de AacSituacaoConsultas
	 */
	public List<AacSituacaoConsultas> pesquisarSituacaoPorSiglaAtiva(String parametro) {
		DetachedCriteria criteria = montarQuerySituacaoPorSiglaAtiva(parametro);
		criteria.addOrder(Order.asc(AacSituacaoConsultas.Fields.SITUACAO.toString()));
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Retorna a lista de situações de consultas ativas (exceto Marcadas).
	 * Utiliza sigla como critério de pesquisa.
	 * 
	 * @param parametro
	 * @return List de AacSituacaoConsultas
	 */
	public List<AacSituacaoConsultas> pesquisarSituacaoSemMarcadaPorSiglaAtiva(String parametro) {
		DetachedCriteria criteria = montarQuerySituacaoSemMarcadaPorSiglaAtiva(parametro);
		criteria.addOrder(Order.asc(AacSituacaoConsultas.Fields.SITUACAO.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna a lista de situações de consultas ativas.
	 * Utiliza descrição como critério de pesquisa.
	 * 
	 * @param parametro
	 * @return List de AacSituacaoConsultas
	 */
	public List<AacSituacaoConsultas> pesquisarSituacaoPorDescricaoAtiva(String parametro) {
		DetachedCriteria criteria = montarQuerySituacaoPorDescricaoAtiva(parametro);
		criteria.addOrder(Order.asc(AacSituacaoConsultas.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna a lista de situações de consultas ativas (exceto Marcadas).
	 * Utiliza descrição como critério de pesquisa.
	 * 
	 * @param parametro
	 * @return List de AacSituacaoConsultas
	 */
	public List<AacSituacaoConsultas> pesquisarSituacaoSemMarcadaPorDescricaoAtiva(String parametro) {
		DetachedCriteria criteria = montarQuerySituacaoSemMarcadaPorDescricaoAtiva(parametro);
		criteria.addOrder(Order.asc(AacSituacaoConsultas.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna a lista de situações de consultas ativas.
	 * 
	 * @return
	 */
	public List<AacSituacaoConsultas> obterSituacoesAtivas() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacSituacaoConsultas.class);
		criteria.add(Restrictions.eq(AacSituacaoConsultas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.desc(AacSituacaoConsultas.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public AacSituacaoConsultas obterSituacaoPorSiglaAtiva(String parametro) {
		DetachedCriteria criteria = montarQuerySituacaoPorSiglaAtiva(parametro);
		criteria.addOrder(Order.asc(AacSituacaoConsultas.Fields.SITUACAO.toString()));
		return (AacSituacaoConsultas) executeCriteriaUniqueResult(criteria);
	}

	public Long pesquisarConsultaCount(FiltroConsultaBloqueioConsultaVO filtroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacSituacaoConsultas.class); 
		criteria = montarCriteriaBloqueioConsulta(filtroConsulta, criteria);
		return executeCriteriaCount(criteria);
	}
	
	public List<AacSituacaoConsultas> pesquisarConsultaPaginada(FiltroParametrosPadraoConsultaVO filtro, FiltroConsultaBloqueioConsultaVO filtroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacSituacaoConsultas.class); 
		criteria = montarCriteriaBloqueioConsulta(filtroConsulta, criteria);
		if (StringUtils.isBlank(filtro.getOrderProperty())){
			filtro.setOrderProperty(AacSituacaoConsultas.Fields.SITUACAO.toString());
			filtro.setOrdenacaoAscDesc(true);
		}
		return executeCriteria(criteria, filtro.getFirstResult(), filtro.getMaxResult(), filtro.getOrderProperty(), filtro.isOrdenacaoAscDesc());
	}
	
	private DetachedCriteria montarCriteriaBloqueioConsulta(FiltroConsultaBloqueioConsultaVO filtro, DetachedCriteria criteria) {
		if (StringUtils.isNotBlank(filtro.getSituacao())) {
			criteria.add(Restrictions.eq(AacSituacaoConsultas.Fields.SITUACAO.toString(), filtro.getSituacao()));
		}
		if (filtro.getDescricao() != null) {
			criteria.add(Restrictions.ilike(AacSituacaoConsultas.Fields.DESCRICAO.toString(), filtro.getDescricao(), MatchMode.ANYWHERE));
		}
		if (filtro.getDominioSimNao() != null && filtro.getDominioSimNao().isSim()) {
			criteria.add(Restrictions.eq(AacSituacaoConsultas.Fields.AUSENCIA_PROFICIONAL.toString(), true));  
		}else if (filtro.getDominioSimNao() != null && !filtro.getDominioSimNao().isSim()) {
			criteria.add(Restrictions.eq(AacSituacaoConsultas.Fields.AUSENCIA_PROFICIONAL.toString(), false));  
		}
		
		if (filtro.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(AacSituacaoConsultas.Fields.IND_SITUACAO.toString(), filtro.getIndSituacao()));
		}
		if (StringUtils.isNotBlank(filtro.getSituacao())) {
			criteria.add(Restrictions.eq(AacSituacaoConsultas.Fields.SITUACAO.toString(), filtro.getSituacao()));
		}
		return criteria;
	}	
	/**
	 * Retorna a lista de situações de consultas ativas sem ordenação.
	 * #6807 C5
	 */
	public List<AacSituacaoConsultas> obterSituacoesAtivasSemOrder() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacSituacaoConsultas.class);
		criteria.add(Restrictions.eq(AacSituacaoConsultas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
}
