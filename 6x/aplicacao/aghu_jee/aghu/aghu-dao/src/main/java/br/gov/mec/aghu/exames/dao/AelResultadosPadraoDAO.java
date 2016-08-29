package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelResultadosPadraoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelResultadosPadrao> {

	private static final long serialVersionUID = 5507389226386122811L;

	/**
	 * Obtém AelCampoLaudo por seq/id
	 * 
	 * @param seq
	 * @return
	 */
	public AelResultadosPadrao obterResultadosPadraoPorSeq(Integer seq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadosPadrao.class);
		criteria.add(Restrictions.eq(AelResultadosPadrao.Fields.SEQ.toString(), seq));
		criteria.createAlias(AelResultadosPadrao.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);

		return (AelResultadosPadrao) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtém a criteria pra pesquisa de Resultados Padrão
	 * 
	 * @param resultadosPadrao
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisarResultadosPadrao(AelResultadosPadrao resultadosPadrao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadosPadrao.class);

		if (resultadosPadrao.getSeq() != null) {
			criteria.add(Restrictions.eq(AelResultadosPadrao.Fields.SEQ.toString(), resultadosPadrao.getSeq()));
		}

		if (StringUtils.isNotBlank(resultadosPadrao.getDescricao())) {
			criteria.add(Restrictions.ilike(AelResultadosPadrao.Fields.DESCRICAO.toString(), StringUtils.trim(resultadosPadrao.getDescricao()), MatchMode.ANYWHERE));
		}

		if (resultadosPadrao.getSituacao() != null) {
			criteria.add(Restrictions.eq(AelResultadosPadrao.Fields.SITUACAO.toString(), resultadosPadrao.getSituacao()));
		}

		return criteria;
	}

	/**
	 * Pesquisa/Obtém a quantidade de registros da pesquisa de Resultados Padrão
	 * 
	 * @param resultadosPadrao
	 * @return
	 */
	public Long pesquisarResultadosPadraoCount(AelResultadosPadrao resultadosPadrao) {
		DetachedCriteria criteria = this.obterCriteriaPesquisarResultadosPadrao(resultadosPadrao);
		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Pesquisa de Resultados Padrão
	 */
	public List<AelResultadosPadrao> pesquisarResultadosPadrao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelResultadosPadrao resultadosPadrao) {
		DetachedCriteria criteria = this.obterCriteriaPesquisarResultadosPadrao(resultadosPadrao);
		
		if(orderProperty == null){
			orderProperty = AelResultadosPadrao.Fields.DESCRICAO.toString();
			asc = true;
		}
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Retorna uma lista de AelResultadoPadrao atraves da sequencia ou descricao
	 * 
	 * @param param
	 * @return
	 */
	public List<AelResultadosPadrao> listarResultadosPadraoPorSeqOuDescricao(String param) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadosPadrao.class);

		criteria.createAlias(AelResultadosPadrao.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", Criteria.LEFT_JOIN);

		criteria.add(Restrictions.isNull("EMA." + AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString()));
		criteria.add(Restrictions.isNull("EMA." + AelExamesMaterialAnalise.Fields.MAN_SEQ.toString()));

		if (CoreUtil.isNumeroInteger(param)) {
			criteria.add(Restrictions.eq(AelResultadosPadrao.Fields.SEQ.toString(), Integer.valueOf(param)));
		} else if (StringUtils.isNotBlank(param)) {
			criteria.add(Restrictions.ilike(AelResultadosPadrao.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE));
		}

		return executeCriteria(criteria);
	}

	/**
	 * Lista registros da tabela <br>
	 * AEL_RESULTADOS_PADROES <br>
	 * por exame material.
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @return
	 */
	public List<AelResultadosPadrao> listarResultadoPadraoCampoPorExameMaterial(String emaExaSigla, Integer emaManSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadosPadrao.class);

		criteria.createAlias(AelResultadosPadrao.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", Criteria.LEFT_JOIN);

		criteria.add(Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), emaManSeq));

		return this.executeCriteria(criteria);

	}

	/**
	 * Verifica a ocorrência de resultados padrão por exame material de análise
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @return
	 */
	public Boolean verificarOcorrenciaPadraoCampoPorExameMaterial(String emaExaSigla, Integer emaManSeq) {
		return !listarResultadoPadraoCampoPorExameMaterial(emaExaSigla, emaManSeq).isEmpty();
	}

	public AelResultadosPadrao obterResultadosPadraoPorExame(String exaSigla, Integer manSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadosPadrao.class);

		criteria.createAlias(AelResultadosPadrao.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", Criteria.LEFT_JOIN);

		criteria.add(Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), manSeq));

		List<AelResultadosPadrao> result = this.executeCriteria(criteria);

		if (result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

}