package br.gov.mec.aghu.centrocusto.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.centrocusto.vo.CentroCustosVO;
import br.gov.mec.aghu.compras.contaspagar.vo.BuscaDivHospNatDespVO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloBloqueadoVO;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.CtbRelacionaNatureza;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcpPagamento;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FcuGrupoCentroCustos;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.RapChefias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.model.SceItemBoc;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoAutTempSolicita;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoCaracteristica;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.model.ScoDireitoAutorizacaoTemp;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.paciente.vo.SituacaoPacienteVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;

/**
 * 
 * @modulo financeiro.centrocusto
 * @author lalegre
 * 
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
public class FccCentroCustosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FccCentroCustos> {

	private static final String ALIAS_FCC_PONTO = "FCC.";
	private static final String ALIAS_FCC = "FCC";
	private static final long serialVersionUID = -8630170010697191884L;

	/**
	 * Retorna centro de custos
	 */
	public List<FccCentroCustos> pesquisarCentroCustos(Object objPesquisa) {
		return executeCriteria(montarCriteriaCentroCustosOrdemDescricao(objPesquisa, true), 0, 100, null, true);
	}

	public Long pesquisarCentroCustosCount(Object objPesquisa) {
		return executeCriteriaCount(montarCriteriaCentroCustosOrdemDescricao(objPesquisa, false));
	}

	public List<FccCentroCustos> pesquisarCentroCustosAtivosOrdemDescricao(Object centroCusto) {

		DetachedCriteria criteria = montarCriteriaCentroCustosOrdemDescricao(centroCusto, true);
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria);
	}

	public Integer pesquisarCentroCustosAtivosOrdemDescricaoCount(Object centroCusto) {

		DetachedCriteria criteria = montarCriteriaCentroCustosOrdemDescricao(centroCusto, true);
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria).size();
	}

	public List<FccCentroCustos> pesquisarCentroCustosServidorOrdemDescricao(Object centroCusto, RapServidores servidor) {
		DetachedCriteria criteria = montarCriteriaCentroCustosOrdemDescricao(centroCusto, true);

		StringBuffer sql = new StringBuffer(180);
		sql.append(" codigo in (  	select  rap.CCT_CODIGO_ATUA" + "               	from    agh.RAP_SERVIDORES rap"
				+ "					where   rap.MATRICULA = ?" + "					and     rap.VIN_CODIGO = ?" + "              ) ");
		Object[] values = { servidor.getId().getMatricula(), servidor.getId().getVinCodigo() };
		Type[] types = { IntegerType.INSTANCE, ShortType.INSTANCE };
		criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));

		List<FccCentroCustos> retorno = executeCriteria(criteria);

		if (retorno != null && retorno.isEmpty()) {

			// Pesquisa por centro de custo locação quando o centro de custo da
			// aplicação está inválido
			retorno = this.pesquisarCentroCustosLotacaoServidorOrdemDescricao(centroCusto, servidor);

		}
		return retorno;
	}

	public List<FccCentroCustos> pesquisarCentroCustosLotacaoServidorOrdemDescricao(Object centroCusto, RapServidores servidor) {
		DetachedCriteria criteria = montarCriteriaCentroCustosOrdemDescricao(centroCusto, true);

		StringBuffer sql = new StringBuffer(180);
		sql.append(" codigo in (  	select  rap.CCT_CODIGO" + "               	from    agh.RAP_SERVIDORES rap" + "					where   rap.MATRICULA = ?"
				+ "					and     rap.VIN_CODIGO = ?" + "              ) ");
		Object[] values = { servidor.getId().getMatricula(), servidor.getId().getVinCodigo() };
		Type[] types = { IntegerType.INSTANCE, ShortType.INSTANCE };
		criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));

		return executeCriteria(criteria);
	}

	public List<FccCentroCustos> pesquisarCentroCustosAplicacaoOrdemDescricao(Object centroCusto) {
		DetachedCriteria criteria = montarCriteriaCentroCustosOrdemDescricao(centroCusto, true);

		return executeCriteria(criteria);
	}

	public List<FccCentroCustos> pesquisarCentroCustosServidor(Object centroCusto, RapServidores servidor) {

		DetachedCriteria criteria = montarCriteriaCentroCustosOrdemDescricao(centroCusto, true);

		StringBuffer sql = new StringBuffer(250);
		sql.append(" codigo in (  	select   CASE WHEN rap.CCT_codigo_atua IS NULL THEN rap.CCT_codigo ELSE rap.CCT_codigo_atua END"
				+ "               	from    agh.RAP_SERVIDORES rap" + "					where   rap.MATRICULA = ?" + "					and     rap.VIN_CODIGO = ?"
				+ "              ) ");
		Object[] values = { servidor.getId().getMatricula(), servidor.getId().getVinCodigo() };
		Type[] types = { IntegerType.INSTANCE, ShortType.INSTANCE };
		criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));

		return executeCriteria(criteria);
	}

	public Long pesquisarCentroCustosServidorCount(Object centroCusto, RapServidores servidor) {

		DetachedCriteria criteria = montarCriteriaCentroCustosOrdemDescricao(centroCusto, false);

		StringBuffer sql = new StringBuffer(250);
		sql.append(" codigo in (  	select   CASE WHEN rap.CCT_codigo_atua IS NULL THEN rap.CCT_codigo ELSE rap.CCT_codigo_atua END"
				+ "               	from    agh.RAP_SERVIDORES rap" + "					where   rap.MATRICULA = ?" + "					and     rap.VIN_CODIGO = ?"
				+ "              ) ");
		Object[] values = { servidor.getId().getMatricula(), servidor.getId().getVinCodigo() };
		Type[] types = { IntegerType.INSTANCE, ShortType.INSTANCE };
		criteria.add(Restrictions.sqlRestriction(sql.toString(), values, types));

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaCentroCustosOrdemDescricao(Object centroCusto, Boolean order) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		String srtPesquisa = (String) centroCusto;

		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(centroCusto)) {
				criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}

		if (order) {
			criteria.addOrder(Order.asc(FccCentroCustos.Fields.DESCRICAO.toString()));
		}
		return criteria;
	}

	public List<FccCentroCustos> pesquisarCentroCustosSuperior(String strPesquisa) {
		final DetachedCriteria criteria = getCriteriaSuggestionCentroCustosSuperior(strPesquisa);

		criteria.addOrder(Order.asc(FccCentroCustos.Fields.CODIGO.toString()));

		return executeCriteria(criteria, 0, 25, null, false);
	}

	public Long pesquisarCentroCustosSuperiorCount(String strPesquisa) {
		return executeCriteriaCount(getCriteriaSuggestionCentroCustosSuperior(strPesquisa));
	}

	private DetachedCriteria getCriteriaSuggestionCentroCustosSuperior(String filtro) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);

		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (filtro != null && !StringUtils.isEmpty(filtro)) {
			if (CoreUtil.isNumeroInteger(filtro)) {
				criteria.add(Restrictions.idEq(Integer.parseInt(filtro)));
			} else {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}

	public FccCentroCustos pesquisarCentroCustosPorMatriculaVinculo(Integer matricula, Short vinCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		criteria.add(Restrictions.and(Restrictions.eq(FccCentroCustos.Fields.SERVIDOR_MATRICULA.toString(), matricula),
				Restrictions.eq(FccCentroCustos.Fields.SERVIDOR_VINCULO.toString(), vinCodigo)));

		FccCentroCustos centroCusto = null;

		List<FccCentroCustos> li = executeCriteria(criteria);
		if (li != null && !li.isEmpty()) {
			centroCusto = li.get(0);
		}

		return centroCusto;
	}

	public Long obterFccCentroCustoCount(FccCentroCustos centroCusto, FcuGrupoCentroCustos grupoCentroCusto,
			FccCentroCustos centroCustoSuperior, RapServidores servidor, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		DetachedCriteria criteria = criarCriteriaCentroCusto(centroCusto, grupoCentroCusto, centroCustoSuperior, servidor, tiposCentroProducao);

		return executeCriteriaCount(criteria);
	}

	public List<FccCentroCustos> pesquisarCentroCustos(Integer firstResult, Integer maxResults, FccCentroCustos centroCusto,
			FcuGrupoCentroCustos grupoCentroCusto, FccCentroCustos centroCustoSuperior, RapServidores servidor, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {

		DetachedCriteria criteria = criarCriteriaCentroCusto(centroCusto, grupoCentroCusto, centroCustoSuperior, servidor, tiposCentroProducao);

		criteria.createAlias(FccCentroCustos.Fields.GRUPO_CC.toString(), "GCC", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(FccCentroCustos.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV." + RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PES", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(FccCentroCustos.Fields.CENTRO_CUSTO.toString(), "CC", JoinType.LEFT_OUTER_JOIN);

		criteria.addOrder(Order.asc("codigo"));

		List<FccCentroCustos> li = executeCriteria(criteria, firstResult, maxResults, null, false);
		return li;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public DetachedCriteria criarCriteriaCentroCusto(FccCentroCustos centroCusto, FcuGrupoCentroCustos grupoCentroCusto,
			FccCentroCustos centroCustoSuperior, RapServidores servidor, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, "cct");
		criteria.createAlias("cct."+FccCentroCustos.Fields.CENTRO_PRODUCAO, "cto", JoinType.LEFT_OUTER_JOIN);

		if (centroCusto != null) {
			if (centroCusto.getCodigo() != null) {
				criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), centroCusto.getCodigo()));
			}

			if (centroCusto.getDescricao() != null && !centroCusto.getDescricao().equals("")) {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), centroCusto.getDescricao(), MatchMode.ANYWHERE));
			}

			if (centroCusto.getNomeReduzido() != null && !centroCusto.getNomeReduzido().equals("")) {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.NOME_REDUZIDO.toString(), centroCusto.getNomeReduzido(),
						MatchMode.ANYWHERE));
			}

			if (centroCusto.getCcustRh() != null && !centroCusto.getCcustRh().equals("")) {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.CC_RH.toString(), centroCusto.getCcustRh(), MatchMode.ANYWHERE));
			}

			if (centroCusto.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), centroCusto.getIndSituacao()));
			}

			if (centroCusto.getCentroProducao() != null) {
				criteria.add(Restrictions.eq(FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), centroCusto.getCentroProducao()));
			}

		}

		if (grupoCentroCusto != null && grupoCentroCusto.getSeq() != null) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.GRUPO_CC_SEQ.toString(), grupoCentroCusto.getSeq()));
		}

		if (centroCustoSuperior != null && centroCustoSuperior.getCodigo() != null) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.CC_SUPERIOR.toString(), centroCustoSuperior.getCodigo()));
		}

		if (servidor != null && servidor.getId() != null && ((RapServidoresId) servidor.getId()).getVinCodigo() != null) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SERVIDOR.toString(), servidor));
		}
		
		if(tiposCentroProducao != null && tiposCentroProducao.length > 0){
			criteria.add(Restrictions.in("cto."+SigCentroProducao.Fields.IND_TIPO.toString(), tiposCentroProducao));
		}

		return criteria;
	}

	public FccCentroCustos obterCentroCusto(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);

		criteria.createAlias(ALIAS_FCC_PONTO + FccCentroCustos.Fields.GRUPO_CC.toString(), "GCC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CENTRO_CUSTO.toString(), "CC", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), "CP", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(ALIAS_FCC_PONTO + FccCentroCustos.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV." + RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CIDADE.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CID." + AipCidades.Fields.UF.toString(), "CID_UF", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CODIGO.toString(), codigo));

		return (FccCentroCustos) executeCriteriaUniqueResult(criteria);
	}

	public FccCentroCustos obterFccCentroCustos(Integer codigo) {
		DetachedCriteria cri = DetachedCriteria.forClass(FccCentroCustos.class);

		cri.setFetchMode(FccCentroCustos.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		cri.setFetchMode(FccCentroCustos.Fields.GRUPO_CC.toString(), FetchMode.JOIN);
		cri.setFetchMode(FccCentroCustos.Fields.SERVIDOR.toString(), FetchMode.JOIN);

		cri.add(Restrictions.idEq(codigo));

		return (FccCentroCustos) executeCriteriaUniqueResult(cri);
	}

	public FccCentroCustos obterFccCentroCustosAtivos(Integer codigo) {
		DetachedCriteria cri = DetachedCriteria.forClass(FccCentroCustos.class);

		cri.setFetchMode(FccCentroCustos.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		cri.setFetchMode(FccCentroCustos.Fields.GRUPO_CC.toString(), FetchMode.JOIN);
		cri.setFetchMode(FccCentroCustos.Fields.SERVIDOR.toString(), FetchMode.JOIN);

		cri.add(Restrictions.idEq(codigo));
		cri.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

		return (FccCentroCustos) executeCriteriaUniqueResult(cri);
	}

	/**
	 * @param filtro
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustosAtivosComChefiaPorCodigoDescricao(String filtro) {
		final DetachedCriteria criteria = obterCriteriaPesquisarCentroCustosAtivosComChefiaPorCodigoDescricao(filtro);
		criteria.addOrder(Order.asc(FccCentroCustos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}

	public Long pesquisarCentroCustosAtivosComChefiaPorCodigoDescricaoCount(String filtro) {
		final DetachedCriteria criteria = obterCriteriaPesquisarCentroCustosAtivosComChefiaPorCodigoDescricao(filtro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaPesquisarCentroCustosAtivosComChefiaPorCodigoDescricao(String filtro) {
		final DetachedCriteria criteria = createCriteriaPorCodigoDescricao(filtro);
		criteria.createAlias(FccCentroCustos.Fields.SERVIDOR.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");

		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.isNotNull(FccCentroCustos.Fields.SERVIDOR_MATRICULA.toString()));
		criteria.add(Restrictions.isNotNull(FccCentroCustos.Fields.SERVIDOR_VINCULO.toString()));
		return criteria;
	}

	private DetachedCriteria createCriteriaPorCodigoDescricao(String strPesquisa) {
		// Se for número pesquida por código = chave primária. Caso contrário
		// pesquisa por descrição.
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		if (StringUtils.isNotBlank(strPesquisa)) {
			Criterion cNome = Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE);
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.or(cNome, Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(strPesquisa))));
			} else {
				criteria.add(cNome);
			}
		}

		return criteria;
	}

	/**
	 * @param centroCusto
	 * @param somenteAtivo
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustosAtivosPorCodigoDescricaoOrdemCodigo(String centroCusto, boolean somenteAtivo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);

		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(centroCusto)) {
			codigo = Integer.valueOf(centroCusto);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), codigo));
		} else {
			criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), centroCusto, MatchMode.ANYWHERE));
		}

		if (somenteAtivo) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));
		}
		criteria.addOrder(Order.asc(FccCentroCustos.Fields.CODIGO.toString()));

		return executeCriteria(criteria, 0, 100, null, false);

	}

	/**
	 * @param centroCusto
	 * @param somenteAtivo
	 * @return
	 */
	public FccCentroCustos pesquisarCentroCustoAtivoPorCodigo(Integer cCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class,ALIAS_FCC);
		
		criteria.createAlias(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CENTRO_CUSTO.toString(), "FCC_SUP", JoinType.LEFT_OUTER_JOIN);

		if (cCodigo != null) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), cCodigo));
		}
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

		return (FccCentroCustos) this.executeCriteriaUniqueResult(criteria);

	}

	/**
	 * @param filtro
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustosPorCodigoDescricao(String filtro) {

		final DetachedCriteria criteria = createCriteriaPorCodigoDescricao(filtro);
		criteria.addOrder(Order.asc(FccCentroCustos.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}

	public Long pesquisarCentroCustosPorCodigoDescricaoCount(String filtro) {
		return executeCriteriaCount(createCriteriaPorCodigoDescricao(filtro));
	}

	/**
	 * @param centroCusto
	 * @return
	 */
	public boolean verificarCentroCustosServidoresLotados(FccCentroCustos centroCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class);

		criteria.add(Restrictions.eq(RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), centroCusto));

		Criterion notNullDtFimVinculo = Restrictions.isNotNull(RapServidores.Fields.DATA_FIM_VINCULO.toString());

		Criterion dtfimVinculoAntesAgora = Restrictions.gt(RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date());

		criteria.add(Restrictions.or(notNullDtFimVinculo, dtfimVinculoAntesAgora));

		return this.executeCriteriaCount(criteria) > 0;
	}

	/**
	 * @param centroCusto
	 * @return
	 */
	public RapChefias obterChefiaAtivaCentroCusto(FccCentroCustos centroCusto) {
		DetachedCriteria criteriaChefiaAtiva = DetachedCriteria.forClass(RapChefias.class);

		criteriaChefiaAtiva.add(Restrictions.eq(RapChefias.Fields.CENTRO_CUSTO.toString(), centroCusto));

		criteriaChefiaAtiva.add(Restrictions.isNull(RapChefias.Fields.DATA_FIM.toString()));

		return (RapChefias) this.executeCriteriaUniqueResult(criteriaChefiaAtiva);
	}

	/**
	 * @param centroCusto
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesCentroCusto(FccCentroCustos centroCusto) {
		DetachedCriteria criteriaEspecialidadesPorCentroCusto = DetachedCriteria.forClass(AghEspecialidades.class);

		criteriaEspecialidadesPorCentroCusto.add(Restrictions.eq(AghEspecialidades.Fields.CENTRO_CUSTO.toString(), centroCusto));

		return this.executeCriteria(criteriaEspecialidadesPorCentroCusto);
	}

	/**
	 * @param centroCusto
	 * @return
	 */
	public RapServidoresId obterChaveChefiaAnterior(FccCentroCustos centroCusto) {
		DetachedCriteria criteriaChefiaCentroCusto = DetachedCriteria.forClass(FccCentroCustos.class);

		criteriaChefiaCentroCusto.add(Restrictions.idEq(centroCusto.getCodigo()));

		criteriaChefiaCentroCusto.setProjection(Projections.projectionList()
				.add(Projections.property(FccCentroCustos.Fields.SERVIDOR_MATRICULA.toString()))
				.add(Projections.property(FccCentroCustos.Fields.SERVIDOR_VINCULO.toString())));

		RapServidoresId idChefiaAnterior = null;

		Object[] chavesChefiaAnterior = (Object[]) this.executeCriteriaUniqueResult(criteriaChefiaCentroCusto);
		if (chavesChefiaAnterior != null && chavesChefiaAnterior[0] != null && chavesChefiaAnterior[1] != null) {
			idChefiaAnterior = new RapServidoresId((Integer) chavesChefiaAnterior[0], (Short) chavesChefiaAnterior[1]);
		}
		return idChefiaAnterior;
	}

	/**
	 * @param centroCusto
	 * @return
	 */
	public DominioSituacao obterSituacaoAnteriorCentroCusto(FccCentroCustos centroCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), centroCusto.getCodigo()));
		criteria.setProjection(Projections.property(FccCentroCustos.Fields.SITUACAO.toString()));

		return (DominioSituacao) this.executeCriteriaUniqueResult(criteria);
	}

	public List<FccCentroCustos> obterServicosEmEspecialidades(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		criteria.add(Restrictions.isNotEmpty(FccCentroCustos.Fields.ESPECIALIDADES.toString()));
		criteria.addOrder(Order.asc(FccCentroCustos.Fields.DESCRICAO.toString()));
		if (objPesquisa != null && !((String) objPesquisa).isEmpty()) {
			String pesq = (String) objPesquisa;
			if (StringUtils.isNumeric(pesq) && pesq.length() < 10) {
				criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(pesq)));
			} else {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), pesq, MatchMode.ANYWHERE));
			}
		}
		return executeCriteria(criteria);
	}

	public FccCentroCustos pesquisaCentroCustoPorSituacaoPacienteVO(SituacaoPacienteVO situacaoPacienteVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);

		if (situacaoPacienteVO.getAipPacienteJn().getCodigoCentroCustoRecadastro() == null) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), situacaoPacienteVO.getAipPacienteJn()
					.getCodigoCentroCustoCadastro()));
		} else {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), situacaoPacienteVO.getAipPacienteJn()
					.getCodigoCentroCustoRecadastro()));
		}

		return (FccCentroCustos) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Retorna centro de custos ativos e ordenados pela descricao
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustosAtivos(Object objPesquisa) {
		return executeCriteria(montarCriteriaCentroCustosAtivosOrdemDescricao(objPesquisa, true, FccCentroCustos.Fields.DESCRICAO.toString()), 0, 100, null, true);
	}
	
	public Long pesquisarCentroCustosAtivosCount(Object objPesquisa){
		return executeCriteriaCount(montarCriteriaCentroCustosAtivosOrdemDescricao(objPesquisa, true, FccCentroCustos.Fields.DESCRICAO.toString()));
	}

	/**
	 * Retorna centro de custos ativos e ordenados pelo código
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustosAtivosOrdenadosPeloCodigo(Object objPesquisa) {
		return executeCriteria(montarCriteriaCentroCustosAtivosOrdemDescricao(objPesquisa, true, FccCentroCustos.Fields.CODIGO.toString()));
	}

	private DetachedCriteria montarCriteriaCentroCustosAtivosOrdemDescricao(Object centroCusto, boolean apenasAtivos, String ordenacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		String srtPesquisa = (String) centroCusto;

		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(centroCusto)) {
				criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}
		if (apenasAtivos) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));
		}
		criteria.addOrder(Order.asc(ordenacao));
		return criteria;
	}

	/**
	 * Não usar este método não funciona no Oracle
	 * ******************************* Não existe comando WITH RECURSIVE no
	 * Oracle
	 * *******************************************************************
	 * ********** Pesquisa centros de custos do usuário, tanto o centro de custo
	 * de atuação do usuário, como os centros de custo filhos deste
	 * 
	 * @param parametro
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentrosCustosAtuacaoEFilhosAtivosOrdenadosPeloCodigo(Object parametro, Integer codigoCentroCustoAtuacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);

		String srtPesquisa = (String) parametro;

		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}

		String restricao = " exists(WITH RECURSIVE hierarquiaCentrosCustos(codigo) AS ( "
				+ "SELECT * from AGH.FCC_CENTRO_CUSTOS cctHier where cctHier.codigo = " + codigoCentroCustoAtuacao + " UNION ALL "
				+ "SELECT cctHier.* FROM hierarquiaCentrosCustos hier, agh.FCC_CENTRO_CUSTOS cctHier "
				+ "WHERE hier.codigo = cctHier.cct_superior) " + "SELECT * FROM hierarquiaCentrosCustos h where h.codigo = {alias}.codigo)";
		criteria.add(Restrictions.sqlRestriction(restricao));

		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(FccCentroCustos.Fields.CODIGO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}

	public List<FccCentroCustos> pesquisarCentroCustosComLimitResult(Object objPesquisa, Integer limit) {
		DetachedCriteria dc = montarCriteriaCentroCustosOrdemDescricao(objPesquisa, false);

		return executeCriteria(dc, 0, limit, FccCentroCustos.Fields.CODIGO.toString(), Boolean.TRUE);
	}

	public List<FccCentroCustos> pesquisarCentrosCustosUsuarioHierarquiaParte1(final String parametro, final DominioSituacao indSituacao,
			final List<Integer> ccsCodigos) {
		if (ccsCodigos == null || ccsCodigos.isEmpty()) {
			return new ArrayList<FccCentroCustos>(0);
		}
		final DetachedCriteria criteria = this.criarCriteriaCentroCustoAtivo(parametro, indSituacao);
		this.adicinarRestricaoCentroCusto(criteria, ccsCodigos);
		return executeCriteria(criteria);
	}

	public List<FccCentroCustos> pesquisarCentrosCustosUsuarioHierarquiaParte2(final RapServidores servidorLogado, final String parametro,
			final DominioSimNao hierarquiaCcusto, DominioCaracteristicaCentroCusto caracteristica, final List<Integer> ccsCodigos) {
		final DetachedCriteria criteria = this.criarCriteriaCentroCustoAtivo(parametro, null);
		this.adicinarRestricaoCaracteristicaUsuarioCentroCusto(criteria, servidorLogado, hierarquiaCcusto, caracteristica);
		return executeCriteria(criteria);
	}

	public List<FccCentroCustos> pesquisarCentrosCustosUsuarioHierarquiaParte3(final RapServidores servidorLogado, final String parametro,
			final DominioSituacao indSituacao, final DominioSimNao hierarquiaCcusto, DominioCaracteristicaCentroCusto caracteristica,
			final List<Integer> ccsCodigos) {
		if (ccsCodigos == null || ccsCodigos.isEmpty()) {
			return new ArrayList<FccCentroCustos>(0);
		}
		final DetachedCriteria criteria = this.criarCriteriaCentroCustoAtivo(parametro, indSituacao);
		this.adicinarRestricaoCaracteristicaUsuarioCentroCusto(criteria, servidorLogado, hierarquiaCcusto, caracteristica);
		this.adicinarRestricaoCentroCusto(criteria, ccsCodigos);
		return executeCriteria(criteria);
	}

	private void adicinarRestricaoCaracteristicaUsuarioCentroCusto(final DetachedCriteria criteria, final RapServidores servidorLogado,
			final DominioSimNao hierarquiaCcusto, final DominioCaracteristicaCentroCusto caracteristica) {
		criteria.createAlias(FccCentroCustos.Fields.CARACTERISTICAS_USUARIOS_CENTRO_CUSTOS.toString(), "cus");
		criteria.createAlias("cus." + ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), "car");
		criteria.add(Restrictions.eq("cus." + ScoCaracteristicaUsuarioCentroCusto.Fields.MATRICULA_SERVIDOR.toString(), servidorLogado.getId()
				.getMatricula()));
		criteria.add(Restrictions.eq("cus." + ScoCaracteristicaUsuarioCentroCusto.Fields.IND_HIERARQUIA.toString(), hierarquiaCcusto));
		criteria.add(Restrictions.eq("car." + ScoCaracteristica.Fields.CARACTERISTICA.toString(), caracteristica.getCodigo()));

	}

	private void adicinarRestricaoCentroCusto(final DetachedCriteria criteria, List<Integer> ccsCodigos) {
		if (ccsCodigos != null && !ccsCodigos.isEmpty()) {
			criteria.add(Restrictions.in(FccCentroCustos.Fields.CODIGO.toString(), ccsCodigos));
		}
	}

	private DetachedCriteria criarCriteriaCentroCustoAtivo(String parametro, DominioSituacao indSituacao) {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), indSituacao));
		}
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	/**
	 * 
	 * Busca todos os centros de custos levando em consideração os filtros
	 * informados por parametro.
	 * 
	 * @author rmalvezzi
	 * @param paramPesquisa
	 *            Possiveis filtros de Código ou Descrição do Centro de Custo
	 *            (NULL se for para desconsiderar esse parametro).
	 * @param gccCodigo
	 *            Filtro pelo Grupo do Centro de Custo (NULL se for para
	 *            desconsiderar esse parametro).
	 * @param seqCentroProducao
	 *            Filtro (que não seja igual) pelo Código do Centro de Produção
	 *            (NULL se for para desconsiderar esse parametro).
	 * @param situacao
	 *            Filtro pela Situação do Centro de Custo (NULL se for para
	 *            desconsiderar esse parametro).
	 * @return Retorna Lista dos Centros de Custos que correspondem aos filtros
	 *         informados por parametro.
	 */
	public List<FccCentroCustos> pesquisarCentroCustosPorCentroProdExcluindoGcc(Object paramPesquisa, Integer gccCodigo,
			Integer seqCentroProducao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		String srtPesquisa = (String) paramPesquisa;

		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(paramPesquisa)) {
				criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}

		if (gccCodigo != null) {
			criteria.add(Restrictions.ne(FccCentroCustos.Fields.GRUPO_CC_CODIGO.toString(), Short.valueOf(gccCodigo.toString())));
		}

		if (seqCentroProducao != null) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.CENTRO_PRODUCAO.toString() + '.' + SigCentroProducao.Fields.SEQ.toString(),
					seqCentroProducao));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), situacao));
		}

		criteria.addOrder(Order.asc(FccCentroCustos.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}

	public List<FccCentroCustos> pesquisarCentroCustosPorCentroProdAtivo(SigCentroProducao centroProducao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);

		criteria.add(Restrictions.eq(FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), centroProducao));

		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.addOrder(Order.asc(FccCentroCustos.Fields.CODIGO.toString()));

		return executeCriteria(criteria);
	}

	public boolean existeCentroCustoAssociado(SigCentroProducao centroProducao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), centroProducao));

		return executeCriteriaExists(criteria);
	}

	/**
	 * Retorna Centro de Custo com determinada descrição
	 * 
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoDescricaoReplicada(Integer codigo, String descricao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.DESCRICAO.toString(), descricao));
		if (codigo != null) {
			criteria.add(Restrictions.ne(FccCentroCustos.Fields.CODIGO.toString(), codigo));
		}
		return this.executeCriteria(criteria);
	}

	/**
	 * 
	 * @param servidor
	 * @return FccCentroCustos
	 */
	public FccCentroCustos pesquisarCentroCustoAtuacaoLotacaoServidor(RapServidores servidor) {

		if (servidor != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);

			/**** SOMENTE CENTRO DE CUSTOS ATIVOS ***/
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

			final DetachedCriteria subQuerie = DetachedCriteria.forClass(RapServidores.class, "SERV");

			subQuerie.add(Restrictions.eq("SERV." + RapServidores.Fields.CODIGO_VINCULO.toString(), servidor.getId().getVinCodigo()));
			subQuerie.add(Restrictions.eq("SERV." + RapServidores.Fields.MATRICULA.toString(), servidor.getId().getMatricula()));
			subQuerie.setProjection(Projections.property("SERV." + RapServidores.Fields.MATRICULA.toString()));

			if (servidor.getCentroCustoAtuacao() != null) {
				subQuerie.add(Property.forName(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CODIGO.toString()).eqProperty(
						"SERV." + RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString()));
			} else {
				subQuerie.add(Property.forName(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CODIGO.toString()).eqProperty(
						"SERV." + RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString()));
			}

			criteria.add(Subqueries.exists(subQuerie));

		    List<FccCentroCustos> li = executeCriteria(criteria);
			if (li != null && !li.isEmpty()) {
				return li.get(0);
			}
			else {
				return null;
			}
		} else {
			return null;
		}

	}

	public List<FccCentroCustos> pesquisarCentroCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigCentroProducao centroProducao, DominioTipoCentroProducaoCustos tipo, String descricao, DominioSituacao situacao) {

		DetachedCriteria criteria = this.criarCriteriaPesquisaCentroCusto(centroProducao, tipo, descricao, situacao);
		criteria.addOrder(Order.asc(FccCentroCustos.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarCentroCustoCount(SigCentroProducao centroProducao, DominioTipoCentroProducaoCustos tipo, String descricao,
			DominioSituacao situacao) {
		return this.executeCriteriaCount(this.criarCriteriaPesquisaCentroCusto(centroProducao, tipo, descricao, situacao));
	}

	protected DetachedCriteria criarCriteriaPesquisaCentroCusto(SigCentroProducao centroProducao, DominioTipoCentroProducaoCustos tipo,
			String descricao, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);

		criteria.setFetchMode(FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), FetchMode.JOIN);

		if (centroProducao != null) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), centroProducao));
		}

		if (tipo != null) {
			criteria.createCriteria(FccCentroCustos.Fields.CENTRO_PRODUCAO.toString()).add(
					Restrictions.eq(SigCentroProducao.Fields.IND_TIPO.toString(), tipo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), situacao));
		}

		return criteria;
	}

	/**
	 * BUSCA CENTRO DE CUSTO DE ATUACAO OU LOTACAO DO USUARIO
	 * 
	 * @autor: Flavio Rutkowski
	 */
	public List<FccCentroCustos> pesquisarCentroCustoAtuacaoLotacaoUsuario(RapServidores servidor) {

		if (servidor != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);
			/**** SOMENTE CENTRO DE CUSTOS ATIVOS ***/
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

			final DetachedCriteria subQuerie = DetachedCriteria.forClass(RapServidores.class, "SERV");

			subQuerie.add(Restrictions.eq("SERV." + RapServidores.Fields.CODIGO_VINCULO.toString(), servidor.getId().getVinCodigo()));
			subQuerie.add(Restrictions.eq("SERV." + RapServidores.Fields.MATRICULA.toString(), servidor.getId().getMatricula()));
			subQuerie.setProjection(Projections.property("SERV." + RapServidores.Fields.MATRICULA.toString()));

			if (servidor.getCentroCustoAtuacao() != null) {
				subQuerie.add(Property.forName(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CODIGO.toString()).eqProperty(
						"SERV." + RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString()));
			} else {
				subQuerie.add(Property.forName(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CODIGO.toString()).eqProperty(
						"SERV." + RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString()));
			}

			criteria.add(Subqueries.exists(subQuerie));

			return this.executeCriteria(criteria);
		} else {
			return null;
		}

	}

	/**
	 * BUSCA CCUSTO VIA CARACTERÍSTICAS DO USUÁRIO
	 * 
	 * @autor: Flavio Rutkowski
	 */
	public List<FccCentroCustos> pesquisarCentroCustoCaracteristicaUsuario(RapServidores servidor, String caracteristica, boolean hierarquia) {

		if (servidor != null) {

			DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);

			/**** SOMENTE CENTRO DE CUSTOS ATIVOS ***/
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

			final DetachedCriteria subQuerie = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "CARAC_USER");
			subQuerie.createAlias("CARAC_USER." + ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), "CARAC",
					JoinType.INNER_JOIN);
			subQuerie.add(Restrictions.ilike("CARAC." + ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), caracteristica,
					MatchMode.ANYWHERE));

			subQuerie.add(Restrictions.eq("CARAC_USER." + ScoCaracteristicaUsuarioCentroCusto.Fields.TIPO_CCUSTO.toString(), DominioSimNao.S));
			subQuerie.add(Restrictions.eq("CARAC_USER." + ScoCaracteristicaUsuarioCentroCusto.Fields.IND_HIERARQUIA.toString(),
					DominioSimNao.getInstance(hierarquia)));
			subQuerie.add(Restrictions.eq("CARAC_USER." + ScoCaracteristicaUsuarioCentroCusto.Fields.VINCULO_SERVIDOR.toString(), servidor
					.getId().getVinCodigo()));
			subQuerie.add(Restrictions.eq("CARAC_USER." + ScoCaracteristicaUsuarioCentroCusto.Fields.MATRICULA_SERVIDOR.toString(), servidor
					.getId().getMatricula()));
			subQuerie.setProjection(Projections.property("CARAC_USER."
					+ ScoCaracteristicaUsuarioCentroCusto.Fields.MATRICULA_SERVIDOR.toString()));

			subQuerie.add(Property.forName(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CODIGO.toString()).eqProperty(
					"CARAC_USER." + ScoCaracteristicaUsuarioCentroCusto.Fields.CENTRO_CUSTO.toString()));

			criteria.add(Subqueries.exists(subQuerie));

			return this.executeCriteria(criteria);
		} else {
			return null;
		}

	}

	/**
	 * BUSCA CCUSTO VIA CARACTERÍSTICAS DO USUÁRIO
	 * 
	 * @autor: Flavio Rutkowski
	 */
	public List<FccCentroCustos> pesquisarCentroCustoCaracteristica(String caracteristica, boolean hierarquia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);

		/**** SOMENTE CENTRO DE CUSTOS ATIVOS ***/
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

		final DetachedCriteria subQuerie = DetachedCriteria.forClass(ScoCaracteristicaUsuarioCentroCusto.class, "CARAC_USER");
		subQuerie
				.createAlias("CARAC_USER." + ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), "CARAC", JoinType.INNER_JOIN);
		subQuerie.add(Restrictions.ilike("CARAC." + ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA.toString(), caracteristica,
				MatchMode.ANYWHERE));

		subQuerie.add(Restrictions.eq("CARAC_USER." + ScoCaracteristicaUsuarioCentroCusto.Fields.TIPO_CCUSTO.toString(), DominioSimNao.S));
		subQuerie.add(Restrictions.eq("CARAC_USER." + ScoCaracteristicaUsuarioCentroCusto.Fields.IND_HIERARQUIA.toString(),
				DominioSimNao.getInstance(hierarquia)));
		subQuerie.setProjection(Projections.property("CARAC_USER." + ScoCaracteristicaUsuarioCentroCusto.Fields.MATRICULA_SERVIDOR.toString()));

		subQuerie.add(Property.forName(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CODIGO.toString()).eqProperty(
				"CARAC_USER." + ScoCaracteristicaUsuarioCentroCusto.Fields.CENTRO_CUSTO.toString()));

		criteria.add(Subqueries.exists(subQuerie));

		return this.executeCriteria(criteria);

	}

	/**
	 * Pesquisar centro de custos cujo servidor possua permissão temporária para
	 * autorizar Solicitações de Compra
	 * 
	 * @param servidor
	 * @return
	 */

	public List<FccCentroCustos> pesquisarCentroCustoAutorizacaoTemporariaSCUser(RapServidores servidor) {
		if (servidor != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);

			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

			final DetachedCriteria subQuerie = DetachedCriteria.forClass(ScoDireitoAutorizacaoTemp.class, "AUT_TEMP");

			subQuerie
					.add(Restrictions.eq("AUT_TEMP." + ScoDireitoAutorizacaoTemp.Fields.VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));
			subQuerie.add(Restrictions.eq("AUT_TEMP." + ScoDireitoAutorizacaoTemp.Fields.MATRICULA.toString(), servidor.getId().getMatricula()));

			subQuerie.add(Restrictions.le("AUT_TEMP." + ScoDireitoAutorizacaoTemp.Fields.DT_INICIO.toString(), new Date()));
			subQuerie.add(Restrictions.or(Restrictions.ge("AUT_TEMP." + ScoDireitoAutorizacaoTemp.Fields.DT_FIM.toString(), new Date()),
					Restrictions.isNull("AUT_TEMP." + ScoDireitoAutorizacaoTemp.Fields.DT_FIM.toString())));

			subQuerie.setProjection(Projections.property("AUT_TEMP." + ScoDireitoAutorizacaoTemp.Fields.MATRICULA.toString()));

			subQuerie.add(Property.forName(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CODIGO.toString()).eqProperty(
					"AUT_TEMP." + ScoDireitoAutorizacaoTemp.Fields.FCC_CENTRO_CUSTOS.toString()));
			criteria.add(Subqueries.exists(subQuerie));

			return this.executeCriteria(criteria);

		} else {
			return null;
		}
	}

	/**
	 * CCUSTO VIA AUTORIZAÇÃO TEMPORÁRIA DE GERAÇÃO DE SC PARA O USUÁRIO
	 * 
	 * @autor: Flavio Rutkowski
	 */
	public List<FccCentroCustos> pesquisarCentroCustoAutorizacaoTemporariaGerSCUser(RapServidores servidor) {

		if (servidor != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);

			/**** SOMENTE CENTRO DE CUSTOS ATIVOS ***/
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

			final DetachedCriteria subQuerie = DetachedCriteria.forClass(ScoAutTempSolicita.class, "AUT_TEMP_SOL");

			subQuerie.add(Restrictions.eq("AUT_TEMP_SOL." + ScoAutTempSolicita.Fields.VINCULO_SERVIDOR.toString(), servidor.getId()
					.getVinCodigo()));
			subQuerie.add(Restrictions.eq("AUT_TEMP_SOL." + ScoAutTempSolicita.Fields.MATRICULA_SERVIDOR.toString(), servidor.getId()
					.getMatricula()));

			subQuerie.add(Restrictions.le("AUT_TEMP_SOL." + ScoAutTempSolicita.Fields.DT_INICIO.toString(), new Date()));
			// subQuerie.add();
			subQuerie.add(Restrictions.or(Restrictions.ge("AUT_TEMP_SOL." + ScoAutTempSolicita.Fields.DT_FIM.toString(), new Date()),
					Restrictions.isNull("AUT_TEMP_SOL." + ScoAutTempSolicita.Fields.DT_FIM.toString())));

			subQuerie.setProjection(Projections.property("AUT_TEMP_SOL." + ScoAutTempSolicita.Fields.MATRICULA_SERVIDOR.toString()));

			subQuerie.add(Property.forName(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CODIGO.toString()).eqProperty(
					"AUT_TEMP_SOL." + ScoAutTempSolicita.Fields.CENTRO_CUSTO.toString()));

			criteria.add(Subqueries.exists(subQuerie));

			return this.executeCriteria(criteria);
		} else {
			return null;
		}

	}

	/**
	 * BUSCA CENTRO DE CUSTOS ATIVOS
	 * 
	 * @autor: Flavio Rutkowski
	 */
	public List<FccCentroCustos> pesquisarCentroCustoAtivo(FccCentroCustos fccCentroCusto) {

		if (fccCentroCusto != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);
			/**** SOMENTE CENTRO DE CUSTOS ATIVOS ***/
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

			criteria.add(Restrictions.eq(FccCentroCustos.Fields.CC_SUPERIOR.toString(), fccCentroCusto.getCodigo()));

			return this.executeCriteria(criteria);
		} else {
			return null;
		}
	}

	/**
	 * MONTA CENTRO DE CUSTOS
	 * 
	 * @autor: Flavio Rutkowski
	 */
	public List<FccCentroCustos> montaCentroCustoSuperior(List<FccCentroCustos> listaCCInput) {

		List<FccCentroCustos> listaCCRetorno = new ArrayList<FccCentroCustos>();
		boolean isAdicionouItem = false;
		for (FccCentroCustos itemFccCentroCustos : listaCCInput) {

			listaCCRetorno.addAll(this.pesquisarCentroCustoAtivo(itemFccCentroCustos));
			isAdicionouItem = true;

		}
		if (isAdicionouItem == true) {
			listaCCRetorno.addAll(montaCentroCustoSuperior(listaCCRetorno));
		}

		return listaCCRetorno;

	}

	/**
	 * 
	 * @param servidor
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSC(RapServidores servidor) {

		List<FccCentroCustos> listaCCRetorno = new ArrayList<FccCentroCustos>();
		if (servidor != null) {
			listaCCRetorno.addAll(this.pesquisarCentroCustoAtuacaoLotacaoUsuario(servidor));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.GERAR_SC.getCodigo(), true));
			listaCCRetorno.addAll(this.montaCentroCustoSuperior(listaCCRetorno));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.GERAR_SC.getCodigo(), false));
			listaCCRetorno.addAll(this.pesquisarCentroCustoAutorizacaoTemporariaGerSCUser(servidor));

			Collections.sort(listaCCRetorno);

			return listaCCRetorno;
		} else {
			return null;
		}

	}

	/**
	 * 
	 * @param servidor
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSs(RapServidores servidor) {

		List<FccCentroCustos> listaCCRetorno = new ArrayList<FccCentroCustos>();
		if (servidor != null) {
			listaCCRetorno.addAll(this.pesquisarCentroCustoAtuacaoLotacaoUsuario(servidor));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.GERAR_SS.getCodigo(), true));
			listaCCRetorno.addAll(this.montaCentroCustoSuperior(listaCCRetorno));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.GERAR_SS.getCodigo(), false));
			listaCCRetorno.addAll(this.pesquisarCentroCustoAutorizacaoTemporariaGerSCUser(servidor));

			Collections.sort(listaCCRetorno);

			return listaCCRetorno;
		} else {
			return null;
		}

	}

	/**
	 * 
	 * @param servidor
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizaSC(RapServidores servidor) {

		List<FccCentroCustos> listaCCRetorno = new ArrayList<FccCentroCustos>();
		if (servidor != null) {
			listaCCRetorno.addAll(this.pesquisarCentroCustoAtuacaoLotacaoUsuario(servidor));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.AUTORIZAR_SC.getCodigo(), true));
			listaCCRetorno.addAll(this.montaCentroCustoSuperior(listaCCRetorno));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.AUTORIZAR_SC.getCodigo(), false));
			listaCCRetorno.addAll(this.pesquisarCentroCustoAutorizacaoTemporariaGerSCUser(servidor));

			Collections.sort(listaCCRetorno);

			return listaCCRetorno;
		} else {
			return null;
		}
	}

	/**
	 * Pesquisar centros de custo que o usuário possui permissão para autorizar
	 * uma Solicitação de Compra
	 * 
	 * @param servidor
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizacaoSC(RapServidores servidor) {

		List<FccCentroCustos> listaCCRetorno = new ArrayList<FccCentroCustos>();
		if (servidor != null) {
			listaCCRetorno.addAll(this.pesquisarCentroCustoUsuario(servidor));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.AUTORIZAR_SC.getCodigo(), true));
			listaCCRetorno.addAll(this.montaCentroCustoSuperior(listaCCRetorno));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.AUTORIZAR_SC.getCodigo(), false));
			listaCCRetorno.addAll(this.pesquisarCentroCustoAutorizacaoTemporariaSCUser(servidor));

			Collections.sort(listaCCRetorno);

			return listaCCRetorno;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param paramPesquisa
	 * @param servidor
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSCSuggestion(Object paramPesquisa, RapServidores servidor,
			DominioCaracteristicaCentroCusto caracteristicaCentroCusto) {

		List<FccCentroCustos> listaCCResult = new ArrayList<FccCentroCustos>();
		List<FccCentroCustos> listaCCRetorno = new ArrayList<FccCentroCustos>();
		if (servidor != null) {
			listaCCResult.addAll(this.pesquisarCentroCustoAtuacaoLotacaoUsuario(servidor));
			listaCCResult.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor, caracteristicaCentroCusto.getCodigo(), true));
			listaCCResult.addAll(this.montaCentroCustoSuperior(listaCCResult));
			listaCCResult.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor, caracteristicaCentroCusto.getCodigo(), false));
			listaCCResult.addAll(this.pesquisarCentroCustoAutorizacaoTemporariaGerSCUser(servidor));

			List<FccCentroCustos> listFccCentroCustos = pesquisarCentroCustosAtivosOrdenadosPeloCodigo(paramPesquisa);

			if (listFccCentroCustos != null) {
				listFccCentroCustos.retainAll(listaCCResult);

				listaCCRetorno.addAll(listFccCentroCustos);
			}
			/*
			 * String srtPesquisa = (String) paramPesquisa;
			 * 
			 * if (StringUtils.isNotBlank(srtPesquisa)) {
			 * listFccCentroCustos.retainAll(listaCCResult);
			 * 
			 * listaCCRetorno.addAll(listFccCentroCustos);
			 * 
			 * for (FccCentroCustos itemFccCentroCustos:listFccCentroCustos) {
			 * if (listaCCResult.contains(itemFccCentroCustos)){
			 * listaCCRetorno.add(itemFccCentroCustos); } }
			 * 
			 * }
			 */
			else {
				listaCCRetorno.addAll(listaCCResult);
			}

			Collections.sort(listaCCRetorno);

			return listaCCRetorno;
		} else {
			return null;
		}

	}

	public Long pesquisarCentroCustoUsuarioGerarSCSuggestionCount(Object paramPesquisa, RapServidores servidor,
			DominioCaracteristicaCentroCusto caracteristicaCentroCusto) {
		List<FccCentroCustos> listaCCRetorno = this.pesquisarCentroCustoUsuarioGerarSCSuggestion(paramPesquisa, servidor,
				caracteristicaCentroCusto);
		if (listaCCRetorno != null) {
			int retorno = listaCCRetorno.size();
			return Long.valueOf(retorno);
		}
		return 0L;
	}

	/**
	 * Metodo que verifica se centro de custo exige projeto
	 * 
	 * @param servidor
	 * @return
	 * @autor: Flavio Rutkowski
	 */
	public boolean centroCustoAceitaProjeto(RapServidores servidor, FccCentroCustos centroCusto) {

		List<FccCentroCustos> listaCCRetorno = new ArrayList<FccCentroCustos>();
		if (servidor != null && centroCusto != null) {

			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristica("CCUSTO EXIGE PROJETO", true));
			listaCCRetorno.addAll(this.montaCentroCustoSuperior(listaCCRetorno));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristica("CCUSTO EXIGE PROJETO", false));

			return listaCCRetorno.contains(centroCusto);
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param servidor
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizaSS(RapServidores servidor) {

		List<FccCentroCustos> listaCCRetorno = new ArrayList<FccCentroCustos>();
		if (servidor != null) {
			listaCCRetorno.addAll(this.pesquisarCentroCustoUsuario(servidor));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.AUTORIZAR_SS.getCodigo(), true));
			listaCCRetorno.addAll(this.montaCentroCustoSuperior(listaCCRetorno));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.AUTORIZAR_SS.getCodigo(), false));
			listaCCRetorno.addAll(this.pesquisarCentroCustoAutorizacaoTemporariaGerSCUser(servidor));

			Collections.sort(listaCCRetorno);

			return listaCCRetorno;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param servidor
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioAutorizacaoSS(RapServidores servidor) {

		List<FccCentroCustos> listaCCRetorno = new ArrayList<FccCentroCustos>();
		if (servidor != null) {

			listaCCRetorno.addAll(this.pesquisarCentroCustoUsuario(servidor));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.AUTORIZAR_SS.getCodigo(), true));
			listaCCRetorno.addAll(this.montaCentroCustoSuperior(listaCCRetorno));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor,
					DominioCaracteristicaCentroCusto.AUTORIZAR_SS.getCodigo(), false));
			listaCCRetorno.addAll(this.pesquisarCentroCustoAutorizacaoTemporariaSCUser(servidor));

			Collections.sort(listaCCRetorno);

			return listaCCRetorno;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param paramPesquisa
	 * @param servidor
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSSSuggestion(Object paramPesquisa, RapServidores servidor) {

		List<FccCentroCustos> listaCCResult = new ArrayList<FccCentroCustos>();
		List<FccCentroCustos> listaCCRetorno = new ArrayList<FccCentroCustos>();
		if (servidor != null) {
			listaCCResult.addAll(this.pesquisarCentroCustoAtuacaoLotacaoUsuario(servidor));
			listaCCResult.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor, DominioCaracteristicaCentroCusto.GERAR_SS.getCodigo(),
					true));
			listaCCResult.addAll(this.montaCentroCustoSuperior(listaCCResult));
			listaCCResult.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidor, DominioCaracteristicaCentroCusto.GERAR_SS.getCodigo(),
					false));
			listaCCResult.addAll(this.pesquisarCentroCustoAutorizacaoTemporariaGerSCUser(servidor));

			List<FccCentroCustos> listFccCentroCustos = pesquisarCentroCustosAtivosOrdenadosPeloCodigo(paramPesquisa);

			if (listFccCentroCustos != null) {
				listFccCentroCustos.retainAll(listaCCResult);

				listaCCRetorno.addAll(listFccCentroCustos);
			}

			else {
				listaCCRetorno.addAll(listaCCResult);
			}

			Collections.sort(listaCCRetorno);

			return listaCCRetorno;
		} else {
			return null;
		}
	}

	public List<FccCentroCustos> pesquisarCentroCustoComStatusDaEspecialidade(Object paramPesquisa, DominioSituacao ativaOuInativa) {
		String strParamPesquisa = (String) paramPesquisa;
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, "fcc");

		DetachedCriteria criteriaIn = DetachedCriteria.forClass(AghEspecialidades.class, "aes");
		criteriaIn.add(Restrictions.eq(AghEspecialidades.Fields.INDSITUACAO.toString(), ativaOuInativa));
		criteriaIn.setProjection(Projections.property(AghEspecialidades.Fields.CENTRO_CUSTO.toString()));

		criteria.add(Subqueries.propertyIn("fcc." + FccCentroCustos.Fields.CODIGO, criteriaIn));

		if (StringUtils.isNotBlank(strParamPesquisa)) {
			if (CoreUtil.isNumeroInteger(strParamPesquisa)) {
				criteria.add(Restrictions.eq("fcc." + FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(strParamPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), strParamPesquisa, MatchMode.ANYWHERE));
			}
		}
		return this.executeCriteria(criteria);
	}

	/**
	 * BUSCA CENTRO DE CUSTO DO USUARIO
	 * 
	 * @autor: Flavio Rutkowski
	 */
	public List<FccCentroCustos> pesquisarCentroCustoUsuario(RapServidores servidor) {

		if (servidor != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);
			/**** SOMENTE CENTRO DE CUSTOS ATIVOS ***/
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.SERVIDOR.toString(), servidor));

			return this.executeCriteria(criteria);

		} else {
			return null;
		}

	}

	public List<FccCentroCustos> pesquisarCentroCustoUsuarioCaracteristica(final RapServidores servidorLogado,
			final DominioCaracteristicaCentroCusto caracteristica) {
		List<FccCentroCustos> listaCCRetorno = new ArrayList<FccCentroCustos>();
		if (servidorLogado == null) {
			return null;
		} else {
			listaCCRetorno.addAll(this.pesquisarCentroCustoUsuario(servidorLogado));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidorLogado, caracteristica.getCodigo(), true));
			listaCCRetorno.addAll(this.montaCentroCustoSuperior(listaCCRetorno));
			listaCCRetorno.addAll(this.pesquisarCentroCustoCaracteristicaUsuario(servidorLogado, caracteristica.getCodigo(), false));
			listaCCRetorno.addAll(this.pesquisarCentroCustoAutorizacaoTemporariaGerSCUser(servidorLogado));

			Collections.sort(listaCCRetorno);

			return listaCCRetorno;
		}
	}

	@SuppressWarnings("unchecked")
	public Set<Integer> pesquisarCentroCustoComHierarquia(Integer cctCodigo) {
		final StringBuilder sql = new StringBuilder(100);

		if (isOracle()) {
			sql.append("SELECT fcc.codigo ");
			sql.append(" FROM ").append(" agh. ").append(FccCentroCustos.class.getAnnotation(Table.class).name()).append(" fcc ");
			sql.append("WHERE fcc.ind_situacao  = 'A' ");
			sql.append("START WITH fcc.codigo = ").append(cctCodigo);
			sql.append(" CONNECT BY prior fcc.codigo = fcc.cct_superior ");
		} else {
			sql.append("WITH RECURSIVE hierarquiaCentrosCustos(codigo) AS ( ");
			sql.append("		SELECT * from AGH.FCC_CENTRO_CUSTOS cctHier where cctHier.codigo = ").append(cctCodigo);
			sql.append("		UNION ALL ");
			sql.append("		SELECT cctHier.* FROM hierarquiaCentrosCustos hier, agh.FCC_CENTRO_CUSTOS cctHier ");
			sql.append("		WHERE hier.codigo = cctHier.cct_superior) ");
			sql.append("		SELECT codigo FROM hierarquiaCentrosCustos h where ind_situacao = 'A' ");
			
		}

		javax.persistence.Query query = this.createNativeQuery(sql.toString());

		Set<Integer> cctList = new HashSet<Integer>();

		if (isOracle()) {
			List<BigDecimal> result = (List<BigDecimal>) query.getResultList();
			for (BigDecimal obj : result) {
				cctList.add(obj.intValue());
			}
		} else {
			List<Integer> result = (List<Integer>) query.getResultList();
			for (Integer obj : result) {
				cctList.add(obj);
			}
		}

		return cctList;
	}

	public List<FccCentroCustos> pesquisarCentroCustoPorCodigoEDescricao(Object centroCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, "fcc");
		criteria.createAlias(FccCentroCustos.Fields.SIG_CALCULO_ATD_PERMANENCIA.toString(), "cap");

		String srtPesquisa = (String) centroCusto;
		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(centroCusto)) {
				criteria.add(Restrictions.eq("fcc." + FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike("fcc." + FccCentroCustos.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.distinct(Projections.property("cap." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO_CODIGO.toString())),
						FccCentroCustos.Fields.CODIGO.toString())
				.add(Projections.property("fcc." + FccCentroCustos.Fields.DESCRICAO.toString()), FccCentroCustos.Fields.DESCRICAO.toString()));

		criteria.add(Restrictions.isNotNull("cap." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO_CODIGO.toString()));

		criteria.addOrder(Order.asc(FccCentroCustos.Fields.DESCRICAO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(FccCentroCustos.class));

		return executeCriteria(criteria);
	}

	public Long pesquisarCentroCustoPorCodigoEDescricaoCount(Object centroCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, "fcc");
		criteria.createAlias(FccCentroCustos.Fields.SIG_CALCULO_ATD_PERMANENCIA.toString(), "cap");

		String srtPesquisa = (String) centroCusto;
		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(centroCusto)) {
				criteria.add(Restrictions.eq("fcc." + FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike("fcc." + FccCentroCustos.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.distinct(Projections.property("cap." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO_CODIGO.toString())),
						FccCentroCustos.Fields.CODIGO.toString())
				.add(Projections.property("fcc." + FccCentroCustos.Fields.DESCRICAO.toString())));

		criteria.add(Restrictions.isNotNull("cap." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO_CODIGO.toString()));

		return executeCriteriaCount(criteria);
	}

	// #24878 - C8
	public CentroCustosVO obterCentroCustoParaSolicitacaoCompraOuServico(Integer numeroSolicitacaoCompraServico, boolean isSolicitacaoCompra) {
		StringBuilder hql = new StringBuilder(200);
		hql.append("select fcc1 as fcc1, fcc2 as fcc2 ").append('\n');
		hql.append(" from ").append('\n');
		if (isSolicitacaoCompra) {
			hql.append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" slcs ,").append('\n');
		} else {
			hql.append(ScoSolicitacaoServico.class.getSimpleName()).append(" slcs ,").append('\n');
		}
		hql.append(FccCentroCustos.class.getSimpleName()).append(" fcc1,").append('\n');
		hql.append(FccCentroCustos.class.getSimpleName()).append(" fcc2 ").append('\n');
		hql.append(" where ").append('\n');
		hql.append(" slcs.").append(ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString());
		hql.append(" = fcc1.").append(FccCentroCustos.Fields.CODIGO.toString()).append(" and \n");
		hql.append(" slcs.").append(ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString());
		hql.append(" = fcc2.").append(FccCentroCustos.Fields.CODIGO.toString()).append(" and \n");

		if (isSolicitacaoCompra) {
			hql.append(" slcs.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" = :numeroSolicitacaoCompraServico");
		} else {
			hql.append(" slcs.").append(ScoSolicitacaoServico.Fields.NUMERO.toString()).append(" = :numeroSolicitacaoCompraServico");
		}

		Query query = this.createHibernateQuery(hql.toString());
		query.setParameter("numeroSolicitacaoCompraServico", numeroSolicitacaoCompraServico);

		query.setResultTransformer(Transformers.aliasToBean(CentroCustosVO.class));

		return (CentroCustosVO) query.uniqueResult();
	}

	public List<BuscaDivHospNatDespVO> pesquisarBuscaDividaHispitalNaturezaDespesaCriteria(ScoFornecedor fornecedor, Date dataDividaInicial,
			Date dataDividaFinal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class, "TTL");

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.sqlProjection("to_char({alias}.DT_VENCIMENTO, 'YYYY') as ano", // as
																								// ano
						new String[] { BuscaDivHospNatDespVO.Fields.ANO.toString() }, new Type[] { StringType.INSTANCE }),
						BuscaDivHospNatDespVO.Fields.ANO.toString())
				.add(Projections.property("TTL." + FcpTitulo.Fields.NUMERO_TITULO.toString()), BuscaDivHospNatDespVO.Fields.SEQ.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NTD_GND_CODIGO.toString()),
						BuscaDivHospNatDespVO.Fields.AFN_NTD_GND.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NTD_CODIGO.toString()),
						BuscaDivHospNatDespVO.Fields.AFN_NTD.toString())
				.add(Projections.property("CTB." + CtbRelacionaNatureza.Fields.NTPGNPCODIGO.toString()),
						BuscaDivHospNatDespVO.Fields.NTD.toString())
				.add(Projections.property("CTB." + CtbRelacionaNatureza.Fields.NTPCODIGO.toString()),
						BuscaDivHospNatDespVO.Fields.NTP.toString())
				.add(Projections.property("TTL." + FcpTitulo.Fields.VALOR.toString()),
						BuscaDivHospNatDespVO.Fields.VALOR.toString()));
		
		criteria.createAlias("TTL." + FcpTitulo.Fields.NOTA_RECEBIMENTO.toString(), "NRS");		
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.NATUREZA_DESPESA.toString(), "NTD");
		criteria.createAlias("NTD." + FsoNaturezaDespesa.Fields.RELACIONA_NATUREZAS.toString(), "CTB");
		
		criteria.add(Restrictions.eqProperty("CTB." + CtbRelacionaNatureza.Fields.NTD_CODIGO.toString(),
				"AFN." + ScoAutorizacaoForn.Fields.NTD_CODIGO.toString()));
		criteria.add(Restrictions.eqProperty("CTB." + CtbRelacionaNatureza.Fields.NTD_GNDCODIGO.toString(),
				"AFN." + ScoAutorizacaoForn.Fields.NTD_GND_CODIGO.toString()));
		
		Criterion criTtlEst = Restrictions.eq("TTL." + FcpTitulo.Fields.IND_ESTORNO.toString(), Boolean.FALSE);
		Criterion criNrsEst = Restrictions.eq("NRS." + SceNotaRecebimento.Fields.IND_ESTORNO.toString(), Boolean.FALSE);
		
		criteria.add(Restrictions.le("TTL." + FcpTitulo.Fields.DT_GERACAO.toString(), dataDividaFinal));
		criteria.add(Restrictions.le("TTL." + FcpTitulo.Fields.DT_VENCIMENTO.toString(), dataDividaFinal));
		criteria.add(Restrictions.or(criTtlEst, Restrictions.gt("TTL." + FcpTitulo.Fields.DT_ESTORNO.toString(), dataDividaFinal)));
		criteria.add(Restrictions.or(criNrsEst, Restrictions.gt("NRS." + SceNotaRecebimento.Fields.DATA_ESTORNO.toString(), dataDividaFinal)));
		
		if (dataDividaInicial != null) {
			criteria.add(Restrictions.ge("TTL." + FcpTitulo.Fields.DT_GERACAO.toString(), dataDividaInicial));
			criteria.add(Restrictions.ge("TTL." + FcpTitulo.Fields.DT_VENCIMENTO.toString(), dataDividaInicial));
		}

		if (fornecedor != null && fornecedor.getNumero() != null) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), fornecedor.getNumero()));
		}
		criteria.addOrder(OrderBySql.sql("to_char(this_.DT_VENCIMENTO, 'YYYY')"))
				.addOrder(Order.asc("AFN." + ScoAutorizacaoForn.Fields.NTD_GND_CODIGO.toString()))
				.addOrder(Order.asc("AFN." + ScoAutorizacaoForn.Fields.NTD_CODIGO.toString()))
				.addOrder(Order.asc("CTB." + CtbRelacionaNatureza.Fields.NTPGNPCODIGO.toString()))
				.addOrder(Order.asc("CTB." + CtbRelacionaNatureza.Fields.NTPCODIGO.toString()));

		
		for(int i = 1; i < BuscaDivHospNatDespVO.Fields.values().length - 1; i++){
			criteria.addOrder(Order.asc(BuscaDivHospNatDespVO.Fields.values()[i].toString()));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(BuscaDivHospNatDespVO.class));

		return executeCriteria(criteria);
	}

	public List<BuscaDivHospNatDespVO> pesquisarBuscaDividaHispitalNaturezaDespesa(
			ScoFornecedor fornecedor, Date dataDividaInicial,
			Date dataDividaFinal) {

		StringBuilder hql = new StringBuilder(500);

		hql.append("select new ").append(BuscaDivHospNatDespVO.class.getName())
				.append('(');
		// hql.append("select ");
		hql.append("to_char(ttl.dtVencimento, 'YYYY')");
		hql.append(" ,ttl.seq");
		hql.append(" ,afn.").append(ScoAutorizacaoForn.Fields.NTD_GND_CODIGO);
		hql.append(" ,afn.").append(ScoAutorizacaoForn.Fields.NTD_CODIGO);
		hql.append(" ,ctb.").append("ntpGnpCodigo"); // .append(CtbRelacionaNatureza.Fields.NTP_GNP_CODIGO);
		hql.append(" ,ctb.").append("ntpCodigo"); // .append(CtbRelacionaNatureza.Fields.NTP_CODIGO);
		hql.append(" ,ttl.valor");

		hql.append(") from ");
		hql.append(CtbRelacionaNatureza.class.getName()).append(" ctb,");
		hql.append(ScoAutorizacaoForn.class.getName()).append(" afn,");
		hql.append(SceNotaRecebimento.class.getName()).append(" nrs,");
		hql.append(FcpTitulo.class.getName()).append(" ttl ");

		hql.append(" where nrs.seq = ttl.notaRecebimento.seq");
		hql.append(" and afn.numero = nrs.").append(
				SceNotaRecebimento.Fields.AFN_NUMERO);// afn_numero");
		hql.append(" and ctb.ntdGndCodigo = afn.").append(
				ScoAutorizacaoForn.Fields.NTD_GND_CODIGO); // ntd_gnd_codigo");

		if (dataDividaInicial != null) {
			hql.append(" and ttl.dtGeracao >= :dataDividaInicial");
			hql.append(" and ttl.dtVencimento >= :dataDividaInicial");
			hql.append(" and (ttl.indEstorno = 'N' or ttl.dtEstorno >= :dataDividaInicial)");
			hql.append(" and (nrs.estorno = 'N' or nrs.dtEstorno >= :dataDividaInicial)");
		}

		if (dataDividaFinal != null) {
			hql.append(" and ttl.dtGeracao <= :dataDividaFinal");
			hql.append(" and ttl.dtVencimento <= :dataDividaFinal");
			hql.append(" and (ttl.indEstorno = 'N' or ttl.dtEstorno <= :dataDividaFinal)");
			hql.append(" and (nrs.estorno = 'N' or nrs.dtEstorno <= :dataDividaFinal)");
		}

		if (fornecedor != null && fornecedor.getNumero() != null) {
			hql.append(" and afn.propostaFornecedor.fornecedor.numero = :fornecedor_id");
		}

//		hql.append(" group by to_char(ttl.dtVencimento, 'YYYY')");
//		hql.append(" , afn.").append(ScoAutorizacaoForn.Fields.NTD_GND_CODIGO); // ntd_gnd_codigo,
//		hql.append(" , ttl.seq");
//		hql.append(" , afn.").append(ScoAutorizacaoForn.Fields.NTD_CODIGO);// afn.ntd_codigo,
//		hql.append(" , ctb.ntpGnpCodigo, ctb.ntpCodigo ");
//		hql.append(" order by 1,3,4,5");
		
		hql.append(" order by 1,2,3,4,5,6 ");

		Query query = this.createHibernateQuery(hql.toString());
		if (fornecedor != null && fornecedor.getNumero() != null) {
			query.setParameter("fornecedor_id", fornecedor.getNumero());
		}
		if (dataDividaInicial != null) {
			query.setParameter("dataDividaInicial", dataDividaInicial);
		}
		if (dataDividaFinal != null) {
			query.setParameter("dataDividaFinal", dataDividaFinal);
		}

		@SuppressWarnings("unchecked")
		List<BuscaDivHospNatDespVO> dividas = query.list();

		return dividas;

	}
	
	/**
	 * Consulta de títulos bloqueados para relatório
	 * 
	 * @param dominioSituacaoTitulo
	 * @return
	 */
	public List<TituloBloqueadoVO> pesquisarTitulosBloqueadosPorBo(DominioSituacaoTitulo dominioSituacaoTitulo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTitulo.class, "TTL");
		criteria.createAlias("TTL." + FcpTitulo.Fields.NOTA_RECEBIMENTO.toString(), "NRS", JoinType.INNER_JOIN);
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFN", JoinType.INNER_JOIN);
		criteria.createAlias("PFN." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("TTL." + FcpTitulo.Fields.BOC.toString(), "BOC", JoinType.INNER_JOIN);
		criteria.createAlias("BOC." + SceBoletimOcorrencias.Fields.ITENS_BOLETIM_OCORRENCIA.toString(), "IBO");
		criteria.add(Restrictions.eq(FcpTitulo.Fields.IND_SITUACAO.toString(), dominioSituacaoTitulo));

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property(FcpTitulo.Fields.BOC_SEQ.toString()), "bo");
		projectionList.add(Projections.property(FcpTitulo.Fields.NUMERO_TITULO.toString()), "seq");
		projectionList.add(Projections.property("BOC." + SceBoletimOcorrencias.Fields.SITUACAO.toString()), "indSituacao");
		projectionList.add(Projections.property(FcpTitulo.Fields.DT_VENCIMENTO.toString()), "dtVencimento");
		projectionList.add(Projections.property(FcpTitulo.Fields.VALOR.toString()), "valorTitulo");
		projectionList.add(Projections.property("NRS." + SceNotaRecebimento.Fields.NUMERO_NR.toString()), "nrsSeq");
		projectionList.add(Projections.property("BOC." + SceBoletimOcorrencias.Fields.NOTA_RECEBIMENTO_SEQ.toString()), "nrsSeqOrigemNf");
		projectionList.add(Projections.property("BOC." + SceBoletimOcorrencias.Fields.DESCRICAO.toString()), "motivo");
		projectionList.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), "fornecedor");
		projectionList.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString()), "cnpj");
		projectionList.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString()), "cpf");

		projectionList.add(Projections.sum("IBO." + SceItemBoc.Fields.VALOR), "valorBo");

		projectionList.add(Projections.groupProperty(FcpTitulo.Fields.BOC_SEQ.toString()));
		projectionList.add(Projections.groupProperty(FcpTitulo.Fields.NUMERO_TITULO.toString()));
		projectionList.add(Projections.groupProperty("BOC." + SceBoletimOcorrencias.Fields.SITUACAO.toString()));
		projectionList.add(Projections.groupProperty(FcpTitulo.Fields.DT_VENCIMENTO.toString()));
		projectionList.add(Projections.groupProperty(FcpTitulo.Fields.VALOR.toString()));
		projectionList.add(Projections.groupProperty("NRS." + SceNotaRecebimento.Fields.NUMERO_NR.toString()));
		projectionList.add(Projections.groupProperty("BOC." + SceBoletimOcorrencias.Fields.NOTA_RECEBIMENTO_SEQ.toString()));
		projectionList.add(Projections.groupProperty("BOC." + SceBoletimOcorrencias.Fields.DESCRICAO.toString()));
		projectionList.add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
		projectionList.add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.CGC.toString()));
		projectionList.add(Projections.groupProperty("FRN." + ScoFornecedor.Fields.CPF.toString()));

		criteria.setProjection(projectionList);

		criteria.addOrder(Order.desc(FcpTitulo.Fields.BOC_SEQ.toString()));
		criteria.addOrder(Order.asc(FcpTitulo.Fields.NUMERO_TITULO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(TituloBloqueadoVO.class));
		List<TituloBloqueadoVO> results = executeCriteria(criteria);

		if (results != null && results.size() > 0) {

			// Obtém valor total da nota de recebimento
			for (TituloBloqueadoVO tituloBloqueadoVO : results) {
				DetachedCriteria criteriaValorBo = DetachedCriteria.forClass(SceItemNotaRecebimento.class);
				criteriaValorBo
						.add(Restrictions.eq(SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString(), tituloBloqueadoVO.getNrsSeq()));

				ProjectionList projectionListBo = Projections.projectionList();
				projectionListBo.add(Projections.sum(SceItemNotaRecebimento.Fields.VALOR.toString()));
				criteriaValorBo.setProjection(projectionListBo);

				List<Double> resultBO = executeCriteria(criteriaValorBo);

				if (resultBO != null && resultBO.size() > 0) {
					int index = results.indexOf(tituloBloqueadoVO);
					results.get(index).setValorNr(resultBO.get(0));
				}
			}
		}
		return results;
	}

	public List<FcpPagamento> createEstornoPagoNaoPagoCriteria(Integer tituloPagamento, String indEstorno, String order) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpPagamento.class);
		criteria.add(Restrictions.eq(FcpPagamento.Fields.ID_TTL_SEQ.toString(), tituloPagamento));
		if (indEstorno != null) {
			criteria.add(Restrictions.eq(FcpPagamento.Fields.IND_ESTORNO.toString(), indEstorno));
		}
		
		if (order != null) {
			if (order.equalsIgnoreCase("dtPag")) {
				criteria.addOrder(Order.asc(FcpPagamento.Fields.DT_PAGAMENTO.toString()));
			} else {
				criteria.addOrder(Order.asc(FcpPagamento.Fields.ID_NUMERO.toString()));
			}
		}
		return executeCriteria(criteria);
	}

	/**
	 * Pesquisa para preencher o suggestion box da tela de consulta de títulos.
	 * 
	 * @return Coleção com as {@link FcpTipoDocumentoPagamento} que possuem a
	 *         condição igual ao parâmetro.
	 */
	public List<FcpTipoDocumentoPagamento> listarDocumentosPorSituacao(final Object strPesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTipoDocumentoPagamento.class);
		criteria.add(Restrictions.eq(FcpTipoDocumentoPagamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		if (strPesquisa != null && !strPesquisa.toString().isEmpty()) {

			// Numerico
			if (strPesquisa != null && StringUtils.isNumeric(strPesquisa.toString())) {
				Junction disjunction = Restrictions.disjunction();
				disjunction.add(Restrictions.eq(FcpTipoDocumentoPagamento.Fields.SEQ.toString(), Short.valueOf(strPesquisa.toString())));
				criteria.add(disjunction);
			} else {
				// Alfa-Numerico
				Criterion cRazao = Restrictions.ilike(FcpTipoDocumentoPagamento.Fields.DESCRICAO.toString(), strPesquisa.toString(),
						MatchMode.ANYWHERE);
				Junction disjunction = Restrictions.disjunction().add(cRazao);
				criteria.add(disjunction);
			}
		}

		return executeCriteria(criteria, 0, 100, FcpTipoDocumentoPagamento.Fields.DESCRICAO.toString(), true);
	}

	/**
	 * Método para obter o valor do número de registros da pesquisa por
	 * situação.
	 * 
	 * @return Númerico com o valor númerico que representa o total de
	 *         registros.
	 */
	public Long countListarDocumentosPorSituacao(final Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTipoDocumentoPagamento.class);
		criteria.add(Restrictions.eq(FcpTipoDocumentoPagamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}

	/** #8990 - Pesquisa suggestionBox do CC Lotacao e Atuacao */
	private DetachedCriteria montarConsultaPesquisaCCLotacaoEAtuacao(Object parametro, boolean ordenar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		String strParametro = (String) parametro;

		if (StringUtils.isNotBlank(strParametro) && StringUtils.isNumeric(strParametro)) {
			criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(strParametro)));
		} else if (StringUtils.isNotBlank(strParametro)) {
			criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
		}

		if (ordenar) {
			criteria.addOrder(Order.asc(FccCentroCustos.Fields.DESCRICAO.toString()));
		}
		return criteria;
	}

	public List<FccCentroCustos> pesquisarCCLotacaoEAtuacao(Object parametro) {
		return this.executeCriteria(montarConsultaPesquisaCCLotacaoEAtuacao(parametro, true));
	}

	public Long pesquisarCCLotacaoEAtuacaoCount(Object parametro) {
		return this.executeCriteriaCount(montarConsultaPesquisaCCLotacaoEAtuacao(parametro, false));
	}

	/**
	 * Obtem count de lista de {@link FccCentroCustos} para Suggestion Box.
	 * #35688, #35689
	 * 
	 * @param parametro
	 *            {@link String}
	 * @return {@link Long}
	 */
	public Long obterFccCentroCustosParaSuggestionBoxCount(final String parametro) {
		return executeCriteriaCount(obterCriteriaFccCentroCustosPorCodigoNome(parametro, true));
	}

	/**
	 * Obtem lista de {@link FccCentroCustos} para Suggestion Box. #35688,
	 * #35689
	 * 
	 * @param parametro
	 *            {@link String}
	 * @return {@link List} de {@link FccCentroCustos}
	 */
	public List<FccCentroCustos> obterFccCentroCustosParaSuggestionBox(final String parametro) {
		return executeCriteria(obterCriteriaFccCentroCustosPorCodigoNome(parametro, false), 0, 100, null);
	}

	/**
	 * Obtem criteria para consulta de {@link FccCentroCustos} para Suggestion
	 * Box. #35688, #35689
	 * 
	 * @param parametro
	 *            {@link String}
	 * @param isCount
	 *            {@link Boolean}
	 * @return {@link DetachedCriteria}
	 */
	private DetachedCriteria obterCriteriaFccCentroCustosPorCodigoNome(final String parametro, final Boolean isCount) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);

		if (StringUtils.isNotBlank(parametro)) {
			if (StringUtils.isNumeric(parametro)) {
				criteria.add(Restrictions.or(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.parseInt(parametro)),
						Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE)));
			} else {
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}

	/**
	 * #35690 C7 Monta consulta para suggestionBox de Centro de Custo
	 * 
	 * @param filtro
	 *            O que for digitado no suggestionBox que será utilizado como
	 *            filtro.
	 * @return Consulta de Centro de Custo de acordo com o filtro
	 */
	private DetachedCriteria montarCriteriaCentroCustoCodDescricaoGrupo(String pesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);

		if (pesquisa != null && StringUtils.isNotBlank(pesquisa)) {
			Disjunction ou = Restrictions.or(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), pesquisa, MatchMode.ANYWHERE));
			if (CoreUtil.isNumeroInteger(pesquisa)) {
				ou.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(pesquisa)));
			}
			criteria.add(ou);
		}
		return criteria;
	}

	/**
	 * #35690 C7 Realiza a consulta para suggestionBox de Centro de Custo
	 * 
	 * @param filtro
	 *            O que for digitado no suggestionBox que será utilizado como
	 *            filtro.
	 * @return Lista de Centro de Custo de acordo com o filtro
	 */
	public List<FccCentroCustos> listarCentroCustoCodDescricaoGrupo(String pesquisa) {
		DetachedCriteria criteria = montarCriteriaCentroCustoCodDescricaoGrupo(pesquisa);
		return executeCriteria(criteria, 0, 100, FccCentroCustos.Fields.DESCRICAO.toString(), true);
	}

	/**
	 * #35690 C7 Count da consulta para suggestionBox de Centro de Custo
	 * 
	 * @param filtro
	 *            O que for digitado no suggestionBox que será utilizado como
	 *            filtro.
	 * @return Quantidade de Centro de Custo retornado de acordo com o filtro.
	 */
	public Long listarCentroCustoCodDescricaoGrupoCount(String pesquisa) {
		DetachedCriteria criteria = montarCriteriaCentroCustoCodDescricaoGrupo(pesquisa);
		return executeCriteriaCount(criteria);
	}

	public FccCentroCustos obterCentroCustoPorUnidadeFuncional(Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AghUnidadesFuncionais.Fields.CENTRO_CUSTO.toString())
		));
		criteria.add(Restrictions.eq(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		return (FccCentroCustos) executeCriteriaUniqueResult(criteria);
	}

	/***
	 * 43443
	 */
	public Long pesquisarCentroCustosAtivosOrdemDescricaoCountL(Object objPesquisa) {

		DetachedCriteria criteria = montarCriteriaCentroCustosOrdemOuDescricao(objPesquisa, false);
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteriaCount(criteria);
	}
	
	public Long obterCentroCustoAtivosCount(){
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<FccCentroCustos> pesquisarCentroCustosAtivosOrdemOuDescricao(Object centroCusto) {

		DetachedCriteria criteria = montarCriteriaCentroCustosOrdemOuDescricao(centroCusto, true);
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria);
	}
	
	private DetachedCriteria montarCriteriaCentroCustosOrdemOuDescricao(Object centroCusto, Boolean order) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		String srtPesquisa = (String) centroCusto;

		if (srtPesquisa != null && StringUtils.isNotBlank(srtPesquisa)) {
			Disjunction ou = Restrictions.or(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
			if (CoreUtil.isNumeroInteger(srtPesquisa)) {
				ou.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(srtPesquisa)));
			}
			criteria.add(ou);
		}

		if (order) {
			criteria.addOrder(Order.asc(FccCentroCustos.Fields.DESCRICAO.toString()));
		}
		return criteria;
	}
	
	public FccCentroCustos obterDescricaoCentroCustoPorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, "CC");

		criteria.add(Restrictions.eq("CC."+FccCentroCustos.Fields.CODIGO.toString(), codigo));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CC."+FccCentroCustos.Fields.DESCRICAO.toString()).as(FccCentroCustos.Fields.DESCRICAO.toString()))
				);
		criteria.setResultTransformer(Transformers.aliasToBean(FccCentroCustos.class));
		return (FccCentroCustos) executeCriteriaUniqueResult(criteria);
	}
	
	/***
	 * #6810 SB4
	 */
	public List<FccCentroCustos> obterCentroCustoPorCodigoDescricao(String parametro){
		DetachedCriteria criteria = obterCriteriaCentroCustoPorCodigoDescricao(parametro);
		return executeCriteria(criteria, 0, 100, FccCentroCustos.Fields.DESCRICAO.toString(), true);
	}
	
	/***
	 * #6810 SB4
	 */
	public Long obterCentroCustoPorCodigoDescricaoCount(String parametro){
		DetachedCriteria criteria = obterCriteriaCentroCustoPorCodigoDescricao(parametro);
		return executeCriteriaCount(criteria);
	}
	
	/***
	 * #6810 SB4
	 */
	private DetachedCriteria obterCriteriaCentroCustoPorCodigoDescricao(
			String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		if(parametro != null && !parametro.isEmpty()){
			if(StringUtils.isNumeric(parametro)){
				criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), Integer.valueOf(parametro)));
			}else{
				criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	/***
	 * #43473 C2
	 */
	public FccCentroCustos obterNomeSeqCentroCusto(Integer seqAreaTec){
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, "CC");
		criteria.createAlias("CC."+FccCentroCustos.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA");
		criteria.add(Restrictions.eq("ATA."+PtmAreaTecAvaliacao.Fields.SEQ.toString(), seqAreaTec));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CC."+FccCentroCustos.Fields.DESCRICAO.toString()).as(FccCentroCustos.Fields.DESCRICAO.toString()))
				.add(Projections.property("CC."+FccCentroCustos.Fields.CODIGO.toString()).as(FccCentroCustos.Fields.CODIGO.toString()))
				);
		criteria.setResultTransformer(Transformers.aliasToBean(FccCentroCustos.class));
		return (FccCentroCustos) executeCriteriaUniqueResult(criteria);
	}
	
	/***
	 * #44276
	 * C3 - Consultar centro de custo superior.
	 */
	public List<FccCentroCustos> obterCentroCustoSuperiorPorSituacao(Integer cctSuperior){
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, "CC");
		
		criteria.add(Restrictions.eq("CC." + FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("CC." + FccCentroCustos.Fields.CC_SUPERIOR.toString(), cctSuperior));
		
		criteria.setResultTransformer(Transformers.aliasToBean(FccCentroCustos.class));
		
		return executeCriteria (criteria);
	}
	
	/***
	 * #44276
	 * C5 - Consultar quais centro de custo um usuário é responsável
	 */
	public List<FccCentroCustos> obterCentroCustoResponsavelPorUsuario(Integer matriculaChefia, Short vinculoChefia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, "CC");
		
		criteria.add(Restrictions.eq("CC." + FccCentroCustos.Fields.SERVIDOR_MATRICULA.toString(), matriculaChefia));
		criteria.add(Restrictions.eq("CC." + FccCentroCustos.Fields.SERVIDOR_VINCULO.toString(), vinculoChefia));
		criteria.add(Restrictions.eq("CC." + FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	/***
	 * #44276
	 * C5 - Consultar quais centro de custo um usuário é responsável
	 */
	public FccCentroCustos obterCentroCustoAntigo(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, "CC");
		
		criteria.createAlias("CC."+FccCentroCustos.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("CC." + FccCentroCustos.Fields.CODIGO.toString(), codigo));
		
		return (FccCentroCustos) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #43467
	 * @param filtro, servidor
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustosPorServidorLogadoDescricao(String filtro, RapServidores servidor) {
		final DetachedCriteria criteria = createCriteriaPorServidorLogadoDescricao(filtro, servidor);
		criteria.addOrder(Order.asc(FccCentroCustos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}

	public Long pesquisarCentroCustosPorServidorLogadoDescricaoCount(String filtro, RapServidores servidor) {
		return executeCriteriaCount(createCriteriaPorServidorLogadoDescricao(filtro, servidor));
	}
	
	public DetachedCriteria createCriteriaPorServidorLogadoDescricao(String filtro, RapServidores servidor ){
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);
		
		if (StringUtils.isNotBlank(filtro)) {
			criteria.add(Restrictions.ilike(FccCentroCustos.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
		}
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FccCentroCustos.class);
		subCriteria.add(Restrictions.eq(FccCentroCustos.Fields.SERVIDOR.toString(), servidor));
		
		subCriteria.setProjection(Projections.projectionList()
				.add(Projections.property(FccCentroCustos.Fields.CODIGO.toString()).as(FccCentroCustos.Fields.CODIGO.toString()))
				);
		subCriteria.setResultTransformer(Transformers.aliasToBean(FccCentroCustos.class));
		
		criteria.add(Subqueries.propertyIn(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CODIGO.toString(), subCriteria));
		return criteria;
	}
	
//	public List<FccCentroCustos> centroCustoPorServidorLogado(RapServidores servidor ){
//		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);
//		DetachedCriteria subCriteria = DetachedCriteria.forClass(FccCentroCustos.class);
//		subCriteria.add(Restrictions.eq(FccCentroCustos.Fields.SERVIDOR.toString(), servidor));
//		
//		subCriteria.setProjection(Projections.projectionList()
//				.add(Projections.property(FccCentroCustos.Fields.CODIGO.toString()).as(FccCentroCustos.Fields.CODIGO.toString()))
//				);
//		subCriteria.setResultTransformer(Transformers.aliasToBean(FccCentroCustos.class));
//		
//		criteria.add(Subqueries.propertyIn(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CODIGO.toString(), subCriteria));
//		return executeCriteria(criteria);
//	}
//	
//	public List<FccCentroCustos> obterCentroCustoPorCentroCustoInferior(List<FccCentroCustos> cc){
//		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class, ALIAS_FCC);
//		criteria.add(Restrictions.in(ALIAS_FCC_PONTO + FccCentroCustos.Fields.CENTRO_CUSTO.toString(), cc));
//		return executeCriteria(criteria);
//	}
	
}