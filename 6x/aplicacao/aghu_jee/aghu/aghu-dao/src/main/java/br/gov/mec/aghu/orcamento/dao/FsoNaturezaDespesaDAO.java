package br.gov.mec.aghu.orcamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CtbRelacionaNatureza;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FsoNaturezaDespesaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FsoNaturezaDespesa> {
	
	private static final long serialVersionUID = 4887421347418918513L;

	/**
	 * Retorna uma lista de naturezas de despesas ativas
	 * 
	 * @param objPesquisa
	 * @return List
	 */
	public List<FsoNaturezaDespesa> listarNaturezaDespesa(Object objPesquisa) {
		List<FsoNaturezaDespesa> lista = null;
		DetachedCriteria criteria = montarCriteriaNaturezaDespesa(objPesquisa);

		criteria.addOrder(Order.asc("GND." + FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(FsoNaturezaDespesa.Fields.DESCRICAO.toString()));
		criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("GND." + FsoGrupoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		lista = executeCriteria(criteria, 0, 100, null, true);

		return lista;
	}

	/**
	 * Retorna a quantidade de naturezas de despesa por codigo/descrição
	 * 
	 * @param objPesquisa
	 * @return Integer
	 */
	public Long listarNaturezaDespesaCount(Object objPesquisa) {
		DetachedCriteria criteria = montarCriteriaNaturezaDespesa(objPesquisa);
		criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("GND." + FsoGrupoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}

	/**
	 * Monta criteria para pesquisa
	 * 
	 * @param objPesquisa
	 * @return DetachedCriteria
	 */
	private DetachedCriteria montarCriteriaNaturezaDespesa(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);
		String strPesquisa = (String) objPesquisa;

		criteria.createAlias(FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND", Criteria.INNER_JOIN);

		if (CoreUtil.isNumeroByte(strPesquisa)) {
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.CODIGO.toString(), Byte.valueOf(strPesquisa)));

		} else if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq("GND." + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString(), Integer.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(FsoNaturezaDespesa.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	/**
	 * Retorna uma lista de naturezas de despesa por grupo de natureza,
	 * descricao ou situacao
	 */
	public List<FsoNaturezaDespesa> pesquisarListaNaturezaDespesa(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			FsoGrupoNaturezaDespesa grupoNatureza, String descricaoNatureza, DominioSituacao indSituacao) {

		DetachedCriteria criteria = this.obterCriteriaPesquisa(grupoNatureza, null, descricaoNatureza, indSituacao);
		criteria.createAlias(FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GN", JoinType.LEFT_OUTER_JOIN);

		criteria.addOrder(Order.asc(FsoNaturezaDespesa.Fields.GND_CODIGO.toString()));
		criteria.addOrder(Order.asc(FsoNaturezaDespesa.Fields.CODIGO.toString()));

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * Retorna a quantidade natureza de despesa por grupo de natureza, descricao
	 * ou situacao
	 */
	public Long countPesquisaListaNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNatureza, String descricaoNatureza, DominioSituacao indSituacao) {
		return executeCriteriaCount(this.obterCriteriaPesquisa(grupoNatureza, null, descricaoNatureza, indSituacao));
	}

	/**
	 * Retorna a quantidade de naturezas de despesa conforme ID na natureza de
	 * despesa e grupo de natureza
	 * 
	 * @param grupoNatureza
	 * @param idNaturezaDespesa
	 * @return Integer
	 */
	public Long verificarNaturezaDespesaEmGrupoNatureza(FsoGrupoNaturezaDespesa grupoNatureza, FsoNaturezaDespesaId idNaturezaDespesa) {

		DetachedCriteria criteria = this.obterCriteriaPesquisa(grupoNatureza, idNaturezaDespesa, null, null);

		return executeCriteriaCount(criteria);
	}

	/**
	 * Verifica se a natureza de despesa é utilizada em algum relacionamento de
	 * contas contabeis
	 * 
	 * @param naturezaDespesa
	 * @return Boolean
	 */
	public Boolean verificarNaturezaDespesaRelacionadaNaturezaPlano(FsoNaturezaDespesa naturezaDespesa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CtbRelacionaNatureza.class);

		if (naturezaDespesa != null) {
			criteria.add(Restrictions.eq(CtbRelacionaNatureza.Fields.NTD_GNDCODIGO.toString(), naturezaDespesa.getId().getGndCodigo()));
			criteria.add(Restrictions.eq(CtbRelacionaNatureza.Fields.NTD_CODIGO.toString(), naturezaDespesa.getId().getCodigo()));

			return (executeCriteriaCount(criteria) > 0);
		}

		return false;
	}

	/**
	 * Retorna DetachedCriteria para pesquisa de natureza de despesa por grupo,
	 * codigo, descricao e situacao
	 */
	private DetachedCriteria obterCriteriaPesquisa(FsoGrupoNaturezaDespesa grupoNatureza, FsoNaturezaDespesaId codigoNatureza,
			String descricaoNatureza, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);

		if (grupoNatureza != null) {
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), grupoNatureza.getCodigo()));
		}

		if (codigoNatureza != null) {
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), codigoNatureza.getGndCodigo()));
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.CODIGO.toString(), codigoNatureza.getCodigo()));
		}

		if (StringUtils.isNotBlank(descricaoNatureza)) {
			criteria.add(Restrictions.ilike(FsoNaturezaDespesa.Fields.DESCRICAO.toString(), descricaoNatureza, MatchMode.ANYWHERE));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(), indSituacao));
		}

		return criteria;
	}

	/**
	 * Desativa naturezas por grupo quando grupo inativo.
	 * 
	 * @param codigo
	 *            ID do grupo.
	 */
	public void desativarPorGrupo(Integer codigo) {
		String hql = new StringBuilder().append("update ").append(FsoNaturezaDespesa.class.getName()).append(" set indSituacao = :situacao")
				.append(" where grupoNaturezaDespesa.codigo = :grupo").toString();

		Query query = createHibernateQuery(hql);
		query.setParameter("situacao", DominioSituacao.I);
		query.setParameter("grupo", codigo);
		query.executeUpdate();
	}

	/**
	 * Pesquisa naturezas de despesa de um determinado grupo, por cÃ³digo ou
	 * descrição.
	 * 
	 * @param grupo
	 *            Grupo de natureza de despesas.
	 * @param filter
	 *            CÃ³digo ou descrição da natureza.
	 * @return Naturezas de despesas encontradas.
	 */
	@SuppressWarnings("unchecked")
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesa(FsoGrupoNaturezaDespesa grupo, Object filter) {
		DetachedCriteria criteria = getNaturezaDespesaPesquisaCriteria(grupo, filter);
		criteria.addOrder(Order.asc(FsoNaturezaDespesa.Fields.ID.toString()));
		// criteria.setMaxResults(100);

		return this.executeCriteria(criteria, 0, 100, null);
	}

	/**
	 * Pesquisa natureza de despesa por codigo/descrição associada a determinado
	 * grupo de natureza de despesa
	 * 
	 * @param grupo
	 * @param filter
	 * @return List
	 */
	public List<FsoNaturezaDespesa> pesquisarNaturezaDespesaPorGrupo(FsoGrupoNaturezaDespesa grupo, Object filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);
		if (grupo != null) {
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), grupo.getCodigo()));
		}
		String filterStr = (String) filter;
		if (StringUtils.isNotBlank(filterStr)) {
			Criterion restriction = Restrictions.ilike(FsoNaturezaDespesa.Fields.DESCRICAO.toString(), filterStr, MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroByte(filter)) {
				restriction = Restrictions
						.or(restriction, Restrictions.eq(FsoNaturezaDespesa.Fields.CODIGO.toString(), Byte.valueOf(filterStr)));
			}
			criteria.add(restriction);
		}
		return this.executeCriteria(criteria);
	}

	public Long pesquisarNaturezaDespesaPorGrupoCount(FsoGrupoNaturezaDespesa grupo, Object filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);
		if (grupo != null) {
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), grupo.getCodigo()));
		}
		String filterStr = (String) filter;
		if (StringUtils.isNotBlank(filterStr)) {
			Criterion restriction = Restrictions.ilike(FsoNaturezaDespesa.Fields.DESCRICAO.toString(), filterStr, MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroByte(filter)) {
				restriction = Restrictions
						.or(restriction, Restrictions.eq(FsoNaturezaDespesa.Fields.CODIGO.toString(), Byte.valueOf(filterStr)));
			}
			criteria.add(restriction);
		}
		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Pesquisa naturezas de despesa.
	 * 
	 * @param grupo
	 *            Grupo de natureza de despesas.
	 * @param filter
	 *            CÃ³digo ou descrição da natureza.
	 * @return Naturezas de despesas encontradas.
	 */
	@SuppressWarnings("unchecked")
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesaAtivas(FsoGrupoNaturezaDespesa grupo, Object filter) {
		DetachedCriteria criteria = getNaturezaDespesaPesquisaCriteria(grupo, filter);

		final String GND = "gnd";
		criteria.createAlias(criteria.getAlias() + "." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), GND);
		criteria.add(Restrictions.eq(GND + "." + FsoGrupoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.eq(criteria.getAlias() + "." + FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(FsoNaturezaDespesa.Fields.ID.toString()));
		return this.executeCriteria(criteria, 0, 100, null);
	}

	private DetachedCriteria getNaturezaDespesaPesquisaCriteria(FsoGrupoNaturezaDespesa grupo, Object filter) {
		// Criteria criteria =
		// getSession().createCriteria(FsoNaturezaDespesa.class);

		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);

		if (grupo != null) {
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), grupo.getCodigo()));

		}
		String filterStr = (String) filter;

		if (StringUtils.isNotBlank(filterStr)) {
			Criterion restriction = Restrictions.ilike(FsoNaturezaDespesa.Fields.DESCRICAO.toString(), filterStr, MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroByte(filter)) {
				restriction = Restrictions
						.or(restriction, Restrictions.eq(FsoNaturezaDespesa.Fields.CODIGO.toString(), Byte.valueOf(filterStr)));
			}

			criteria.add(restriction);
		}

		return criteria;
	}

	/**
	 * Verifica se a natureza de despesa esta ativa
	 * 
	 * @param id
	 * @return Boolean
	 */
	public Boolean existeNaturezaDespesaAtiva(FsoNaturezaDespesaId id) {
		Long qtdNaturezaAtiva = executeCriteriaCount(
				DetachedCriteria
				.forClass(FsoNaturezaDespesa.class)
				.add(Restrictions.eq(
						FsoNaturezaDespesa.Fields.ID.toString(), id))
				.add(Restrictions.eq(
						FsoNaturezaDespesa.Fields.IND_SITUACAO
								.toString(), DominioSituacao.A)));
		
		if (qtdNaturezaAtiva == null){
			return false;
		} else {
			return qtdNaturezaAtiva.equals(Long.valueOf(1));
		}
		
//		return executeCriteriaCount(
//				DetachedCriteria
//						.forClass(FsoNaturezaDespesa.class)
//						.add(Restrictions.eq(
//								FsoNaturezaDespesa.Fields.ID.toString(), id))
//						.add(Restrictions.eq(
//								FsoNaturezaDespesa.Fields.IND_SITUACAO
//										.toString(), DominioSituacao.A)))
//				.equals(1);
	}

	public List<FsoNaturezaDespesa> listarTodasNaturezaDespesa(Object objPesquisa) {
		List<FsoNaturezaDespesa> lista = null;
		DetachedCriteria criteria = montarCriteriaNaturezaDespesa(objPesquisa);

		criteria.addOrder(Order.asc("GND." + FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(FsoNaturezaDespesa.Fields.DESCRICAO.toString()));

		lista = executeCriteria(criteria, 0, 100, null, true);

		return lista;
	}

	/**
	 * Retorna a quantidade de naturezas de despesa por codigo e descricao
	 * 
	 * @param objPesquisa
	 * @return Integer
	 */
	public Long listarTodasNaturezaDespesaCount(Object objPesquisa) {
		DetachedCriteria criteria = montarCriteriaNaturezaDespesa(objPesquisa);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Retorna lista de naturezas de despesa ativas e inativas associadas ao
	 * grupo de natureza de despesa
	 * 
	 * @param grupo
	 * @return List
	 */
	public List<FsoNaturezaDespesa> listarNaturezaDespesaPorGrupoNatureza(FsoGrupoNaturezaDespesa grupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);
		if (grupo != null) {
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), grupo.getCodigo()));
		}
		criteria.addOrder(Order.asc(FsoNaturezaDespesa.Fields.CODIGO.toString()));
		return executeCriteria(criteria, 0, 10, null);
	}

	/**
	 * Pesquisa para preencher o suggestion box da tela de consulta de títulos.
	 * 
	 * @return Coleção com as {@link FsoNaturezaDespesa} que possuem a condição
	 *         igual a 'A'.
	 */
	public List<FsoNaturezaDespesa> listarNaturezaDespesaPorSituacao(final Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);

		criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(), strPesquisa.toString()));
		criteria.addOrder(Order.desc(FsoNaturezaDespesa.Fields.DESCRICAO.toString()));

		List<FsoNaturezaDespesa> despesas = executeCriteria(criteria);

		return despesas;
	}

	/**
	 * Método para obter o valor do número de registros da pesquisa por
	 * situação.
	 * 
	 * @return Númerico com o valor númerico que representa o total de
	 *         registros.
	 */
	public Long countListarNaturezaDespesaPorSituacao(final Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);
		criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(), strPesquisa.toString()));
		criteria.addOrder(Order.desc(FsoNaturezaDespesa.Fields.DESCRICAO.toString()));
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaListarNaturezaDespesaPorSituacao(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);
		criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		if (strPesquisa != null && !strPesquisa.isEmpty()) {

			// Numerico
			if (StringUtils.isNumeric(strPesquisa)) {
				Junction disjunction = Restrictions.disjunction();
				disjunction.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), Integer.valueOf(strPesquisa)));
				disjunction.add(Restrictions.eq(FsoNaturezaDespesa.Fields.CODIGO.toString(), Byte.valueOf(strPesquisa)));
				criteria.add(disjunction);
			} else {
				// Alfa-Numerico
				Criterion cRazao = Restrictions.ilike(FsoNaturezaDespesa.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE);
				Junction disjunction = Restrictions.disjunction().add(cRazao);
				criteria.add(disjunction);
			}
		}
		return criteria;
	}

	/**
	 * Pesquisa para preencher o suggestion box da tela de consulta de títulos.
	 * 
	 * @return Coleção com as {@link FsoNaturezaDespesa} que possuem a condição
	 *         igual ao parâmetro.
	 */
	public List<FsoNaturezaDespesa> listarNaturezaDespesaPorSituacao(final String strPesquisa) {

		DetachedCriteria criteria = montarCriteriaListarNaturezaDespesaPorSituacao(strPesquisa);

		List<FsoNaturezaDespesa> naturezaDespesas = executeCriteria(criteria, 0, 100, FsoNaturezaDespesa.Fields.DESCRICAO.toString(), true);

		return naturezaDespesas;
	}

	/**
	 * Método para obter o valor do número de registros da pesquisa por
	 * situação.
	 * 
	 * @return Númerico com o valor númerico que representa o total de
	 *         registros.
	 */
	public Long countListarNaturezaDespesaPorSituacao(final String strPesquisa) {
		DetachedCriteria criteria = montarCriteriaListarNaturezaDespesaPorSituacao(strPesquisa);
		return executeCriteriaCount(criteria);

	}

	/**
	 * C3 de 31584
	 * 
	 * @param seqGrupo
	 * @param param
	 * @return
	 */
	private DetachedCriteria montarCriteriaAtivosPorGrupo(Integer seqGrupo, String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);
		criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (seqGrupo != null) {
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), seqGrupo));
		}
		if (StringUtils.isNotBlank(param)) {
			if (CoreUtil.isNumeroByte(param)) {
				criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.CODIGO.toString(), Byte.valueOf(param)));
			} else {
				criteria.add(Restrictions.ilike(FsoNaturezaDespesa.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	/**
	 * C3 de 31584
	 * 
	 * @param seqGrupo
	 * @param param
	 * @param maxResults
	 * @return
	 */

	public List<FsoNaturezaDespesa> pesquisarFsoNaturezaDespesaAtivosPorGrupo(Integer seqGrupo, String param, Integer maxResults) {
		DetachedCriteria criteria = this.montarCriteriaAtivosPorGrupo(seqGrupo, param);
		if (maxResults != null) {
			return super.executeCriteria(criteria, 0, maxResults, FsoNaturezaDespesa.Fields.DESCRICAO.toString(), true);
		}
		return super.executeCriteria(criteria);
	}

	/**
	 * C3 de 31584
	 * 
	 * @param seqGrupo
	 * @param param
	 * @return
	 */
	public Long pesquisarFsoNaturezaDespesaAtivosPorGrupoCount(Integer seqGrupo, String param) {
		DetachedCriteria criteria = this.montarCriteriaAtivosPorGrupo(seqGrupo, param);
		return super.executeCriteriaCount(criteria);
	}

    public FsoNaturezaDespesa obterNaturezaDespesa(FsoNaturezaDespesaId id) {

        DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);
        criteria.createAlias(FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GN",JoinType.INNER_JOIN);

        criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GND_CODIGO.toString(),id.getGndCodigo()));
        criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.CODIGO.toString(),id.getCodigo()));

        FsoNaturezaDespesa result = (FsoNaturezaDespesa)executeCriteriaUniqueResult(criteria);
        return result;
    }
    
    /**
	 * #46298 - Obtem Lista de Natureza de Despesa Ativos por Grupo, Codigo ou Descrição
	 */
    public List<FsoNaturezaDespesa> obterListaNaturezaDespesaAtivosPorGrupoCodigoOuDescricao(FsoGrupoNaturezaDespesa grupo, String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);
		if (grupo != null) {
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), grupo.getCodigo()));
		}
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroByte(filter)) {
				criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.CODIGO.toString(), Byte.valueOf(filter)));
			} else {
				criteria.add(Restrictions.ilike(FsoNaturezaDespesa.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteria(criteria, 0, 100, FsoNaturezaDespesa.Fields.DESCRICAO.toString(), true);
	}

    /**
	 * #46298 - Obtem Count de Natureza de Despesa Ativos por Grupo, Codigo ou Descrição
	 */
	public Long obterCountNaturezaDespesaAtivosPorGrupoCodigoOuDescricao(FsoGrupoNaturezaDespesa grupo, String filter) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FsoNaturezaDespesa.class);
		if (grupo != null) {
			criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), grupo.getCodigo()));
		}
		if (StringUtils.isNotBlank(filter)) {
			if (CoreUtil.isNumeroByte(filter)) {
				criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.CODIGO.toString(), Byte.valueOf(filter)));
			} else {
				criteria.add(Restrictions.ilike(FsoNaturezaDespesa.Fields.DESCRICAO.toString(), filter, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(FsoNaturezaDespesa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteriaCount(criteria);
	}
}