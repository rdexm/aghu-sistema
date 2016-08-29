package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.vo.DiagnosticoFiltro;
import br.gov.mec.aghu.model.McoDiagnostico;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade McoDiagnosticoDAO
 * 
 * @author felipe.rocha
 * 
 */
public class McoDiagnosticoDAO extends BaseDao<McoDiagnostico> {
	private static final long serialVersionUID = 3734703115837214143L;

	/**
	 * Pesquisa diagnostico
	 * 
	 * C1 #26109 - Consulta Diagnostico utilizando filtros da tela.
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return
	 */
		public List<McoDiagnostico> pesquisarDiagnostico(Integer firstResult,
				Integer maxResults, String orderProperty, boolean asc,
				DiagnosticoFiltro filtro) {
		final DetachedCriteria criteria = this.montarCriteriaDiagnostisco(filtro);
		if(StringUtils.isBlank(orderProperty)){
			criteria.addOrder(Order.asc(McoDiagnostico.Fields.DESCRICAO.toString()));
		}
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	private DetachedCriteria montarCriteriaDiagnostisco(DiagnosticoFiltro filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(McoDiagnostico.class);
		
		if (filtro != null) {
			
			if (filtro.getSeq() != null)  {
				criteria.add(Restrictions.eq(McoDiagnostico.Fields.SEQ.toString(), filtro.getSeq()));
			}
			
			if (StringUtils.isNotEmpty(filtro.getDescricao()) && StringUtils.isNotBlank(filtro.getDescricao())) {
				criteria.add(Restrictions.ilike(McoDiagnostico.Fields.DESCRICAO.toString(), filtro.getDescricao(),MatchMode.ANYWHERE));
			}
			
			if (filtro.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(McoDiagnostico.Fields.IND_SITUACAO.toString(), filtro.getIndSituacao()));
			}
			
			if (filtro.getIndPlacar() != null) {
				criteria.add(Restrictions.eq(McoDiagnostico.Fields.IND_PLACAR.toString(), filtro.getIndPlacar()));
			}
		}
		return criteria;
	}



	public Long obterDiagnosticoCount(DiagnosticoFiltro filtro) {
		final DetachedCriteria criteria = this.montarCriteriaDiagnostisco(filtro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria getCriteriaPesquisarDiagnosticoSuggestion(String strPesquisa, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoDiagnostico.class);
		if (strPesquisa != null){
			
			if(CoreUtil.isNumeroInteger(strPesquisa)){
				criteria.add(Restrictions.eq(McoDiagnostico.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(McoDiagnostico.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq(McoDiagnostico.Fields.IND_SITUACAO.toString(), indSituacao));
		return criteria;
	}
	
	public List<McoDiagnostico> pesquisarDiagnosticoSuggestion(String strPesquisa, DominioSituacao indSituacao){
		DetachedCriteria criteria = getCriteriaPesquisarDiagnosticoSuggestion(strPesquisa, indSituacao);
		return executeCriteria(criteria, 0, 100, McoDiagnostico.Fields.DESCRICAO.toString(), true);
	}
	
	public Long pesquisarDiagnosticoSuggestionCount(String strPesquisa, DominioSituacao indSituacao){
		DetachedCriteria criteria = getCriteriaPesquisarDiagnosticoSuggestion(strPesquisa, indSituacao);
		return executeCriteriaCount(criteria);
	}


}