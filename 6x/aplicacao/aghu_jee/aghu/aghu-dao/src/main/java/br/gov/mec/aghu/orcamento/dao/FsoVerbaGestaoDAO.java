package br.gov.mec.aghu.orcamento.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoFontesXVerbaGestao;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class FsoVerbaGestaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FsoVerbaGestao> {

	private static final long serialVersionUID = 1495164547845465456L;

	public List<FsoVerbaGestao> pesquisarVerbaGestaoAtivaPorDescricao(FsoVerbaGestao verbaGestao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoVerbaGestao.class);

		if (verbaGestao != null && verbaGestao.getSeq() != null) {
			criteria.add(Restrictions.ne(FsoVerbaGestao.Fields.SEQ.toString(), verbaGestao.getSeq()));
		}

		criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (verbaGestao != null && verbaGestao.getDescricao() != null) {
			criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.DESCRICAO.toString(), verbaGestao.getDescricao()));
		}

		List<FsoVerbaGestao> verbas = executeCriteria(criteria);

		return verbas;
	}

	/**
	 * Retorna uma lista paginada de verbas de gestão na base que atendem os
	 * filtros passados como parâmetro
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param verbaGestaoFiltro
	 * @param situacao
	 * @param convenio
	 * @param descricaoVerba
	 * @param nroInterno
	 * @param nroConvSiafi
	 * @return List
	 */
	public List<FsoVerbaGestao> pesquisarVerbaGestao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FsoVerbaGestao verbaGestaoFiltro, DominioSituacao situacao, Boolean convenio, String descricaoVerba, String nroInterno,
			BigInteger nroConvSiafi, String planoInterno) {

		DetachedCriteria criteria = this.pesquisarCriteria(verbaGestaoFiltro, situacao, convenio, descricaoVerba, nroInterno, nroConvSiafi,
				planoInterno);

		criteria.addOrder(Order.asc(FsoVerbaGestao.Fields.SEQ.toString()));

		List<FsoVerbaGestao> verbas = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);

		return verbas;
	}

	/**
	 * Retorna a quantidade de verbas de gestão na base que atendem os filtros
	 * passados como parâmetro
	 * 
	 * @param verbaGestaoFiltro
	 * @param situacao
	 * @param convenio
	 * @param descricaoVerba
	 * @param nroInterno
	 * @param nroConvSiafi
	 * @return int
	 */
	public long pesquisarVerbaGestaoCount(FsoVerbaGestao verbaGestaoFiltro, DominioSituacao situacao, Boolean convenio, String descricaoVerba,
			String nroInterno, BigInteger nroConvSiafi, String planoInterno) {
		DetachedCriteria criteria = this.pesquisarCriteria(verbaGestaoFiltro, situacao, convenio, descricaoVerba, nroInterno, nroConvSiafi,
				planoInterno);

		long nVerbas = executeCriteriaCount(criteria);

		return nVerbas;
	}

	/**
	 * Pesquisa verba de gestão ATIVAS por código ou descrição
	 * 
	 * @param paramPesquisa
	 * @return List
	 */
	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorSeqOuDescricao(Object paramPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoVerbaGestao.class);

		String srtPesquisa = (String) paramPesquisa;

		if (CoreUtil.isNumeroInteger(paramPesquisa)) {
			criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SEQ.toString(), Integer.valueOf(srtPesquisa)));
		} else {
			if (StringUtils.isNotBlank(srtPesquisa)) {
				criteria.add(Restrictions.ilike(FsoVerbaGestao.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}

		criteria.addOrder(Order.asc(FsoVerbaGestao.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Pesquisa verbas de gestÃ£o conforme data de vigÃªncia das fontes de
	 * verbas.
	 * 
	 * @param filter
	 *            Filtro por ID ou descrição da verba.
	 * @param data
	 *            Data referÃªncia para vigÃªncia das fontes.
	 * @param max
	 *            NÃºmero mÃ¡ximo de verbas a serem retornadas.
	 * @return Verbas de gestÃ£o encontradas.
	 */
	public List<FsoVerbaGestao> pesquisarVerbaGestao(Object filter, Date data, Integer max) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoVerbaGestao.class);

		String filterStr = (String) filter;

		if (StringUtils.isNotBlank(filterStr)) {
			Criterion restriction = Restrictions.ilike(FsoVerbaGestao.Fields.DESCRICAO.toString(), filterStr, MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroInteger(filter)) {
				restriction = Restrictions.or(restriction, Restrictions.eq(FsoVerbaGestao.Fields.SEQ.toString(), Integer.valueOf(filterStr)));
			}

			criteria.add(restriction);
		}

		restrictByFonteVigencia(criteria, data);

		criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.addOrder(Order.asc(FsoVerbaGestao.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc(FsoVerbaGestao.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, 0, max, null, true);
	}

	public Boolean existeVerbaGestaoComFonteVigente(Integer id, Date data) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoVerbaGestao.class);

		criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SEQ.toString(), id));
		restrictByFonteVigencia(criteria, data);
		
		Long qtdVerbaGestaoAtiva = executeCriteriaCount(criteria);
		
		if (qtdVerbaGestaoAtiva == null){
			return false;
		} else {
			return qtdVerbaGestaoAtiva.equals(Long.valueOf(1));
		}

		//return executeCriteriaCount(criteria).equals(1);
	}

	private void restrictByFonteVigencia(DetachedCriteria criteria, Date data) {
		final String FVB = "fvb";
		data = DateUtil.obterDataComHoraInical(data);
		criteria.add(Subqueries.exists(DetachedCriteria
				.forClass(FsoFontesXVerbaGestao.class, FVB)
				.setProjection(Projections.id())
				.add(Restrictions.eqProperty(FVB + "." + FsoFontesXVerbaGestao.Fields.VERBA.toString(), criteria.getAlias() + "."
						+ FsoVerbaGestao.Fields.SEQ.toString()))
				.add(Restrictions.le(FVB + "." + FsoFontesXVerbaGestao.Fields.DT_VIG_INI.toString(), data))
				.add(Restrictions.or(Restrictions.ge(FVB + "." + FsoFontesXVerbaGestao.Fields.DT_VIG_FIM.toString(), data),
						Restrictions.isNull(FVB + "." + FsoFontesXVerbaGestao.Fields.DT_VIG_FIM.toString())))));
	}

	private DetachedCriteria pesquisarCriteria(FsoVerbaGestao verbaGestaoFiltro, DominioSituacao situacao, Boolean convenio,
			String descricaoVerba, String nroInterno, BigInteger nroConvSiafi, String planoInterno) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FsoVerbaGestao.class);

		if (verbaGestaoFiltro != null) {
			if (StringUtils.isNotBlank(verbaGestaoFiltro.getDescricao())) {
				criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.DESCRICAO.toString(), verbaGestaoFiltro.getDescricao()));
			}

			if (verbaGestaoFiltro.getSeq() != null) {
				criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SEQ.toString(), verbaGestaoFiltro.getSeq()));
			}
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SITUACAO.toString(), situacao));
		}

		if (convenio != null) {
			criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.IND_CONV_ESPECIAL.toString(), convenio));
		}

		if (StringUtils.isNotBlank(descricaoVerba)) {
			criteria.add(Restrictions.ilike(FsoVerbaGestao.Fields.DESCRICAO.toString(), descricaoVerba, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(nroInterno)) {
			criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.NRO_INTERNO.toString(), nroInterno));
		}

		if (nroConvSiafi != null) {
			criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.NRO_CONV_SIAFI.toString(), nroConvSiafi));
		}

		if (StringUtils.isNotBlank(planoInterno)) {
			criteria.add(Restrictions.ilike(FsoVerbaGestao.Fields.IND_DET_PI.toString(), planoInterno, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	/**
	 * Pesquisa para preencher o suggestion box da tela de consulta de títulos.
	 * 
	 * @return Coleção com as {@link FsoVerbaGestao} que possuem a condição
	 *         igual ao parâmetro.
	 */
	public List<FsoVerbaGestao> listarVerbaGestaoPorSituacao(final Object strPesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FsoVerbaGestao.class);

		criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SITUACAO.toString(), strPesquisa.toString()));
		criteria.addOrder(Order.desc(FsoVerbaGestao.Fields.DESCRICAO.toString()));

		List<FsoVerbaGestao> verbas = executeCriteria(criteria);

		return verbas;
	}

	/**
	 * Método para obter o valor do número de registros da pesquisa por
	 * situação.
	 * 
	 * @return Númerico com o valor númerico que representa o total de
	 *         registros.
	 */
	public Long countListarVerbaGestaoPorSituacao(final Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoVerbaGestao.class);
		criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SITUACAO.toString(), strPesquisa.toString()));
		criteria.addOrder(Order.desc(FsoVerbaGestao.Fields.DESCRICAO.toString()));
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaListarVerbaGestaoDAO(String strPesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FsoVerbaGestao.class);
		criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (strPesquisa != null && !strPesquisa.isEmpty()) {

			// Numerico
			if (StringUtils.isNumeric(strPesquisa)) {
				Junction disjunction = Restrictions.disjunction();
				disjunction.add(Restrictions.eq(FsoVerbaGestao.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
				criteria.add(disjunction);
			} else {
				// Alfa-Numerico
				Criterion cDescricao = Restrictions.ilike(FsoVerbaGestao.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE);
				Junction disjunction = Restrictions.disjunction().add(cDescricao);
				criteria.add(disjunction);
			}
		}

		return criteria;

	}

	/**
	 * Pesquisa para preencher o suggestion box da tela de consulta de títulos.
	 * 
	 * @param strPesquisa
	 *            Input digitado pelo usuário na tela a ser comparado com a
	 *            descrição do
	 * @return Coleção com as {@link FsoVerbaGestao} que possuem a condição
	 *         igual ao parâmetro.
	 */
	public List<FsoVerbaGestao> listarVerbaGestaoPorSituacao(final String strPesquisa) {

		DetachedCriteria criteria = montarCriteriaListarVerbaGestaoDAO(strPesquisa);

		List<FsoVerbaGestao> verbaGestao = executeCriteria(criteria, 0, 100, FsoVerbaGestao.Fields.DESCRICAO.toString(), true);

		return verbaGestao;
	}

	/**
	 * Método para obter o valor do número de registros da pesquisa por
	 * situação.
	 * 
	 * @return Númerico com o valor númerico que representa o total de
	 *         registros.
	 */
	public Long countListarVerbaGestaoPorSituacao(final String strPesquisa) {
		DetachedCriteria criteria = montarCriteriaListarVerbaGestaoDAO(strPesquisa);
		return executeCriteriaCount(criteria);

	}

	/**
	 * #46298 - Obtem Lista de Verba de Gestão Ativos por Seq ou Descrição
	 */
	public List<FsoVerbaGestao> obterListaVerbaGestaoAtivosPorSeqOuDescricao(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoVerbaGestao.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)) {
				criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SEQ.toString(), Integer.valueOf(filter)));
			} else { 
				criteria.add(Restrictions.ilike(FsoVerbaGestao.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria, 0, 100, FsoVerbaGestao.Fields.DESCRICAO.toString(), true);
	}
	
	/**
	 * #46298 - Obtem Count de Verba de Gestão Ativos por Seq ou Descrição
	 */
	public Long obterCountVerbaGestaoAtivosPorSeqOuDescricao(String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoVerbaGestao.class);
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroInteger(filter)) {
				criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SEQ.toString(), Integer.valueOf(filter)));
			} else { 
				criteria.add(Restrictions.ilike(FsoVerbaGestao.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(FsoVerbaGestao.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
}