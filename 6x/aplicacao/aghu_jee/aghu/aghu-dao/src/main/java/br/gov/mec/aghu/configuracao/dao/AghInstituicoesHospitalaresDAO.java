package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class AghInstituicoesHospitalaresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghInstituicoesHospitalares> {
	
	private static final long serialVersionUID = 3017668412277874586L;
	
	public List<AghInstituicoesHospitalares> listarPorIndLocalAtivo() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghInstituicoesHospitalares.class);
		criteria.add(Restrictions.eq(AghInstituicoesHospitalares.Fields.IND_LOCAL.toString(), Boolean.TRUE));
		
		return this.executeCriteria(criteria);
	}
	
	public List<AghInstituicoesHospitalares> pesquisarInstituicao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo, String descricao, Integer codCidade) {

		DetachedCriteria criteria = obterCriteriaPesquisaPorCodigoOuDescricao(codigo,
				descricao);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}
	
	public Long obterCountInstituicaoCodigoDescricao(Integer codigo, String descricao) {
		return executeCriteriaCount(obterCriteriaPesquisaPorCodigoOuDescricao(codigo,
				descricao));
	}
	
	public List<AghInstituicoesHospitalares> pesquisarInstituicaoPorCodigoOuDescricaoOrdenado(
			Object parametro) {

		List<AghInstituicoesHospitalares> listaResultado;

		DetachedCriteria criteria = obterCriteriaPesquisaPorCodigoOuDescricao(parametro);

		criteria.addOrder(Order.asc(AghInstituicoesHospitalares.Fields.NOME
				.toString()));

		listaResultado = this.executeCriteria(criteria);

		return listaResultado;
	}
	
	public String recuperarNomeInstituicaoLocal() {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghInstituicoesHospitalares.class);

		criteria.add(Restrictions.eq(
				AghInstituicoesHospitalares.Fields.IND_LOCAL.toString(),
				Boolean.TRUE));

		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AghInstituicoesHospitalares.Fields.NOME
						.toString())));

		String nome = (String) executeCriteriaUniqueResult(criteria);

		return nome;

	}
	
	public Integer recuperarCnesInstituicaoHospitalarLocal() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghInstituicoesHospitalares.class);

		criteria.add(Restrictions.eq(
				AghInstituicoesHospitalares.Fields.IND_LOCAL.toString(),
				Boolean.TRUE));

		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AghInstituicoesHospitalares.Fields.CNES
						.toString())));

		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public AghInstituicoesHospitalares recuperarInstituicaoLocal() {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghInstituicoesHospitalares.class);

		criteria.setFetchMode(AghInstituicoesHospitalares.Fields.CODCIDADE.toString(), FetchMode.JOIN);
		
		criteria.add(Restrictions.eq(
				AghInstituicoesHospitalares.Fields.IND_LOCAL.toString(),
				Boolean.TRUE));

		return (AghInstituicoesHospitalares) executeCriteriaUniqueResult(criteria);

	}
	
	
	public AghInstituicoesHospitalares verificarInstituicaoLocal(Integer seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghInstituicoesHospitalares.class);

		criteria.add(Restrictions.eq(
				AghInstituicoesHospitalares.Fields.IND_LOCAL.toString(),
				Boolean.TRUE));
		
		if (seq != null) {
			criteria.add(Restrictions.ne(
					AghInstituicoesHospitalares.Fields.SEQ.toString(),
					seq));
		}
		
		return (AghInstituicoesHospitalares) executeCriteriaUniqueResult(criteria);

	
	}
	
	
	private DetachedCriteria obterCriteriaPesquisaPorCodigoOuDescricao(
			Object parametro) {

		String descricao = null;
		Integer codigo = null;
		String strParam = (String) parametro;

		if (StringUtils.isNotBlank(strParam)
				&& CoreUtil.isNumeroInteger(strParam)) {
			codigo = Integer.valueOf(strParam);
		} else {
			descricao = strParam;
		}

		DetachedCriteria criteria = obterCriteriaPesquisaPorCodigoOuDescricao(codigo, descricao);

		return criteria;

	}
	
	
	private DetachedCriteria obterCriteriaPesquisaPorCodigoOuDescricao(Integer codigo,
			String descricao) {
		DetachedCriteria criteria = obterCriteriaInstituicao();

		if (codigo != null) {
			criteria.add(Restrictions.eq(AghInstituicoesHospitalares.Fields.SEQ
					.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AghInstituicoesHospitalares.Fields.NOME.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
	/**
	 * @return
	 */
	private DetachedCriteria obterCriteriaInstituicao() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghInstituicoesHospitalares.class);
		
		criteria.createAlias(AghInstituicoesHospitalares.Fields.CODCIDADE.toString(), "CIDADE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghInstituicoesHospitalares.Fields.SIGLA.toString(), "UF_SIGLA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CIDADE." + AipCidades.Fields.UF.toString(), "UF", JoinType.LEFT_OUTER_JOIN);
		
		return criteria;
	}
	
	/**
	 * Método resposável por buscar Instituicoes Hospitalares, conforme a string
	 * passada como parametro, que é comparada com o codigo e a nome da
	 * Instituicao Hospitalar É utilizado pelo converter
	 * AghInstituicoesHospitalaresConverter.
	 * 
	 * @param nome
	 *            ou codigo
	 * @return Lista de AghInstituicoesHospitalares
	 */
	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalarPorCodigoENome(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;

		DetachedCriteria criteria = DetachedCriteria.forClass(AghInstituicoesHospitalares.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroByte(strPesquisa)) {
				criteria.add(Restrictions.eq(AghInstituicoesHospitalares.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(AghInstituicoesHospitalares.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
			criteria.addOrder(Order.asc(AghInstituicoesHospitalares.Fields.NOME.toString()));

		}
		return executeCriteria(criteria);
	}
}
