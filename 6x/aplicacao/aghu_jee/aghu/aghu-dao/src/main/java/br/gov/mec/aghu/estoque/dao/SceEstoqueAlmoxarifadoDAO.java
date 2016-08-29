package br.gov.mec.aghu.estoque.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.vo.GeraSolicCompraEstoqueVO;
import br.gov.mec.aghu.dominio.DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque;
import br.gov.mec.aghu.dominio.DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.vo.AtualizarPontoPedidoVO;
import br.gov.mec.aghu.estoque.vo.EstoqueMaterialVO;
import br.gov.mec.aghu.estoque.vo.RelatorioContagemEstoqueParaInventarioVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmProcedEspecialRm;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.ScePacoteMateriaisId;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe de acesso à entidade SceEstoqueAlmoxarifado (SCE_ESTQ_ALMOXS)
 * 
 * @author guilherme.finotti
 * 
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength" })
public class SceEstoqueAlmoxarifadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceEstoqueAlmoxarifado> {

	private static final long serialVersionUID = -2171863024125347758L;

	@Inject @New(MateriaisGeracaoScQueryBuilder.class) 
	private Instance<MateriaisGeracaoScQueryBuilder> materiaisGeracaoScQueryBuilder;
	
	@Inject
	private IParametroFacade parametroFacade;

	/**
	 * Busca SceEstoqueAlmoxarifado atraves do ID
	 * 
	 * @param seq
	 * @return
	 */
	public SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoPorId(Integer seq) {
		if (seq == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), FetchMode.JOIN);
		criteria.createAlias(SceEstoqueAlmoxarifado.Fields.SOLICITACAO_COMPRA.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.SEQ.toString(), seq));
		return (SceEstoqueAlmoxarifado) executeCriteriaUniqueResult(criteria);
	}

	private void adicionarFiltros(DetachedCriteria criteria, String termoLivre, VScoClasMaterial classificacaoMaterial) {
		if (classificacaoMaterial != null) {
			criteria.createAlias("MAT." + ScoMaterial.Fields.MATERIAIS_CLASSIFICACOES.toString(), "CLS");
			criteria.add(Restrictions.eq("CLS." + ScoMateriaisClassificacoes.Fields.CN5.toString(), classificacaoMaterial.getId()
					.getNumero()));
		}
		if (StringUtils.isNotBlank(termoLivre)) {
			criteria.add(Restrictions.or(Restrictions.ilike("MAT." + ScoMaterial.Fields.NOME.toString(), termoLivre, MatchMode.ANYWHERE),
					Restrictions.ilike("MAT." + ScoMaterial.Fields.DESCRICAO.toString(), termoLivre, MatchMode.ANYWHERE)));
		}
	}

	/**
	 * Lista SCE_ESTOQUE_ALMOXARIFADO
	 * 
	 * @param fornecedorNumero
	 * @param ealSeq
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado(Integer fornecedorNumero, Integer ealSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		if (fornecedorNumero != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), fornecedorNumero));
		}

		if (ealSeq != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.SEQ.toString(), ealSeq));
		}
		return executeCriteria(criteria);
	}

	/**
	 * Lista SCE_ESTOQUE_ALMOXARIFADO
	 * 
	 * @param fornecedorNumero
	 * @param ealSeq
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueMaterialPorAlmoxarifado(Short almoxSeq, Integer numeroFornecedor, Object paramPesq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "mat", JoinType.INNER_JOIN);
		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "frn", JoinType.INNER_JOIN);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), FetchMode.JOIN);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almoxSeq));

		if (paramPesq != null && !"".equals(paramPesq)) {
			if (CoreUtil.isNumeroInteger(paramPesq)) {
				criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.CODIGO.toString(), Integer.parseInt(paramPesq.toString())));
			} else {
				criteria.add(Restrictions.ilike("mat." + ScoMaterial.Fields.NOME.toString(), paramPesq.toString(), MatchMode.ANYWHERE));
			}
		}

		criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (numeroFornecedor != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));
		}

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.addOrder(Order.asc("mat." + ScoMaterial.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
		// Não funcionou por causa do StringTokenizer
		// return executeCriteria(criteria, 0, 100,
		// "mat."+ScoMaterial.Fields.NOME.toString(), true);
	}

	/**
	 * Lista SCE_ESTOQUE_ALMOXARIFADO
	 * 
	 * @param fornecedorNumero
	 * @param ealSeq
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueMaterialPorAlmoxarifadoCodigoGrupoMaterial(Short almoxSeq,
			Integer numeroFornecedor, List<Integer> listaGrupos, Object paramPesq, Boolean somenteEstocaveis, Boolean somenteDiretos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.createAlias(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "mat", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), "un_medida", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almoxSeq));

		if (paramPesq != null && !"".equals(paramPesq)) {
			if (CoreUtil.isNumeroInteger(paramPesq)) {
				criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.CODIGO.toString(), Integer.parseInt(paramPesq.toString())));
			} else {
				criteria.add(Restrictions.ilike("mat." + ScoMaterial.Fields.NOME.toString(), paramPesq.toString(), MatchMode.ANYWHERE));
			}
		}

		criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (somenteEstocaveis != null && somenteEstocaveis) {
			criteria.add(Restrictions.or(Restrictions.eq("mat." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), Boolean.TRUE),
					Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_ESTOQUE_TEMPORARIO.toString(), Boolean.TRUE)));
		}

		if (somenteDiretos != null && somenteDiretos) {
			criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), Boolean.FALSE));
		}

		if (listaGrupos != null && !listaGrupos.isEmpty()) {
			criteria.createCriteria("mat." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "gmt", JoinType.INNER_JOIN);
			criteria.add(Restrictions.in("gmt." + ScoGrupoMaterial.Fields.CODIGO.toString(), listaGrupos));
		}

		if (numeroFornecedor != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));
		}

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.addOrder(Order.asc("mat." + ScoMaterial.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
		// Não funcionou por causa do StringTokenizer
		// return executeCriteria(criteria, 0, 100,
		// "mat."+ScoMaterial.Fields.NOME.toString(), true);
	}

	/**
	 * Pesquisa Estoque Almoxarifado através do Almoxarifado, Fornecedor e
	 * Material
	 * 
	 * @param seqAlmoxarifado
	 * @param numeroFornecedor
	 * @param codigoMaterial
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(Short seqAlmoxarifado,
			Integer codigoMaterial, Integer numeroFornecedor) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "frn", JoinType.INNER_JOIN);
		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "alm", JoinType.INNER_JOIN);
		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "mat", JoinType.INNER_JOIN);
		criteria.createCriteria("mat."+ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "umd", JoinType.INNER_JOIN);
		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), "unidadeMedida", JoinType.LEFT_OUTER_JOIN);

		if (seqAlmoxarifado != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), seqAlmoxarifado));
		}

		if (codigoMaterial != null) {
			criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));
		}

		if (numeroFornecedor != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));
		}

		return executeCriteria(criteria);
	}

	/**
	 * Lista SCE_ESTOQUE_ALMOXARIFADO
	 * 
	 * @param fornecedorNumero
	 * @param ealSeq
	 * @return
	 */
	public Long pesquisarEstoqueMaterialPorAlmoxarifadoCount(Short almoxSeq, Integer numeroFornecedor, Integer codMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "frn", JoinType.INNER_JOIN);
		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "alm", JoinType.INNER_JOIN);
		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "mat", JoinType.INNER_JOIN);

		if (almoxSeq != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almoxSeq));
		}

		if (codMaterial != null) {
			criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.CODIGO.toString(), codMaterial));
		}

		if (numeroFornecedor != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));
		}
		return executeCriteriaCount(criteria);
	}

	/**
	 * Lista SCE_ESTOQUE_ALMOXARIFADO
	 * 
	 * @param fornecedorNumero
	 * @param ealSeq
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueMaterialPorAlmoxarifadoOrderByFornEAlmx(Integer firstResult, Integer maxResult,
			Short almoxSeq, Integer numeroFornecedor, Integer codMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.createAlias(SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "frn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "alm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "mat", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), "unidade_medida", JoinType.LEFT_OUTER_JOIN);

		if (almoxSeq != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almoxSeq));
		}

		if (codMaterial != null) {
			criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.CODIGO.toString(), codMaterial));
		}

		if (numeroFornecedor != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));
		}

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.addOrder(Order.asc("frn." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
		criteria.addOrder(Order.asc("alm." + SceAlmoxarifado.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("mat." + ScoMaterial.Fields.NOME.toString()));

		return executeCriteria(criteria, firstResult, maxResult, null, false);
		// Não funcionou por causa do StringTokenizer
		// return executeCriteria(criteria, 0, 100,
		// "mat."+ScoMaterial.Fields.NOME.toString(), true);
	}

	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmPorMaterialAlmoxirafado(Integer matCodigo, Short almSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almSeq));

		List<SceEstoqueAlmoxarifado> listaEstAlm = executeCriteria(criteria);

		return listaEstAlm;

	}

	public Integer obterQuantidadeEstoqueAlmorarifaxoPorMaterialAlmoxarifado(ScoMaterial material, SceAlmoxarifado almoxarifado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.setProjection(Projections.sum(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()));

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), material));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), almoxarifado));
		criteria.add(Restrictions.isNotNull(SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString()));
		criteria.add(Restrictions.isNotNull(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()));
		criteria.add(Restrictions.gt(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString(), 0));
		return 0;
	}

	/**
	 * RN_EALC_VER_SLD_ESTQ Busca a Qtde saldo do material (em todos almox do
	 * HCPA)
	 * 
	 * @param sceEstoqueAlmoxarifado
	 * @return
	 */
	public Integer pesquisarQtdeSaldoMaterial(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.setProjection(Projections.sum(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()
				+ SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString()));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), sceEstoqueAlmoxarifado.getMaterial()));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), 1));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		Integer saldo = (Integer) this.executeCriteriaUniqueResult(criteria);

		if (saldo == null) {
			saldo = 1;
		}

		return saldo;
	}

	/**
	 * scoc_ver_material Se material tem algum saldo em estoque não deve ser
	 * desativado
	 * 
	 * @param sceEstoqueAlmoxarifado
	 * @return
	 */
	public Integer pesquisarSaldoMaterialEstoqueAlmoxarifado(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), sceEstoqueAlmoxarifado.getMaterial()
				.getCodigo()));

		if (sceEstoqueAlmoxarifado.getAlmoxarifado().getSeq() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), sceEstoqueAlmoxarifado
					.getAlmoxarifado().getSeq()));
		}
		if (sceEstoqueAlmoxarifado.getFornecedor().getNumero() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), sceEstoqueAlmoxarifado.getFornecedor()
					.getNumero()));
		}

		SceEstoqueAlmoxarifado estoqueAlmoxarifado = (SceEstoqueAlmoxarifado) this.executeCriteriaUniqueResult(criteria);

		Integer saldo = null;
		if (estoqueAlmoxarifado != null) {
			saldo = estoqueAlmoxarifado.getQtdeDisponivel() + estoqueAlmoxarifado.getQtdeBloqueada() + estoqueAlmoxarifado.getQtdeEmUso();

		}

		return saldo;

	}

	// Criteria para paginação. Segundo parametros. Fcruz
	private DetachedCriteria montarCriteriaPaginacao(Integer seq, Integer codMaterial, Integer numeroFrn, Short seqAlmox,
			DominioSituacao situacao, Boolean estocavel, String codigoUnidadeMedida, Integer codigoGrupoMaterial, String termoLivre,
			VScoClasMaterial classificacaoMaterial) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");

		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL, "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM", JoinType.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL, "GMT", JoinType.INNER_JOIN);

		if (codMaterial != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), codMaterial));
		}
		if (numeroFrn != null) {
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), numeroFrn));
		}
		if (seqAlmox != null) {
			criteria.add(Restrictions.eq("ALM." + SceAlmoxarifado.Fields.SEQ.toString(), seqAlmox));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), situacao));
		}
		if (estocavel != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString(), estocavel));
		}
		if (seq != null) {
			criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.SEQ.toString(), seq));
		}
		if (StringUtils.isNotBlank(codigoUnidadeMedida)) {
			criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.UMD_CODIGO.toString(), codigoUnidadeMedida));
		}
		if (codigoGrupoMaterial != null) {
			criteria.add(Restrictions.eq("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString(), codigoGrupoMaterial));
		}
		this.adicionarFiltros(criteria, termoLivre, classificacaoMaterial);
		return criteria;
	}

	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codMaterial
	 * @param numeroFrn
	 * @param seqAlmox
	 * @param situacao
	 * @param estocavel
	 * @param seq
	 * @param codigoUnidadeMedida
	 * @param codigoGrupoMaterial
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codMaterial, Integer numeroFrn, Short seqAlmox, DominioSituacao situacao, Boolean estocavel, Integer seq,
			String codigoUnidadeMedida, Integer codigoGrupoMaterial, String termoLivre, VScoClasMaterial classificacaoMaterial) {

		DetachedCriteria criteria = montarCriteriaPaginacao(seq, codMaterial, numeroFrn, seqAlmox, situacao, estocavel,
				codigoUnidadeMedida, codigoGrupoMaterial, termoLivre, classificacaoMaterial);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), FetchMode.JOIN);
		criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.NOME.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	// count da lista de paginaçao. Fcruz
	/**
	 * 
	 * @param seq
	 * @param codMaterial
	 * @param numeroFrn
	 * @param seqAlmox
	 * @param situacao
	 * @param estocavel
	 * @return
	 */
	public Long pesquisarEstoqueAlmoxarifadoCount(Integer seq, Integer codMaterial, Integer numeroFrn, Short seqAlmox,
			DominioSituacao situacao, Boolean estocavel, String codigoUnidadeMedida, Integer codigoGrupoMaterial, String termoLivre,
			VScoClasMaterial classificacaoMaterial) {

		DetachedCriteria criteria = montarCriteriaPaginacao(seq, codMaterial, numeroFrn, seqAlmox, situacao, estocavel,
				codigoUnidadeMedida, codigoGrupoMaterial, termoLivre, classificacaoMaterial);
		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Lista SCE_ESTOQUE_ALMOXARIFADO por pacote
	 * 
	 * @param fornecedorNumero
	 * @param ealSeq
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoPorPacote(Integer codigoGrupo, ScePacoteMateriaisId pacote) {
		StringBuilder sbQuery = new StringBuilder(300).append("	SELECT  eal.*," ).append( "	        mat.nome," ).append( "			ipm.quantidade "
				).append( "	FROM    agh.sco_materiais              mat," ).append( "	        agh.sce_estq_almoxs            eal,"
				).append( "	        agh.sce_item_pacote_materiais  ipm" ).append( "	WHERE   1=1 ")
		.append("	AND     eal.seq                   	= ipm.eal_seq")
		.append("	AND     mat.codigo                	= eal.mat_codigo")
		.append("	AND 	ipm.pmt_cct_codigo_refere 	= " ).append( pacote.getCodigoCentroCustoProprietario() ).append(' ')
		.append("	AND 	ipm.pmt_cct_codigo 			= " ).append( pacote.getCodigoCentroCustoAplicacao() ).append(' ')
		.append("	AND 	ipm.pmt_numero 				= " ).append( pacote.getNumero() ).append(' ');
		if (codigoGrupo != null) {
			sbQuery.append("	AND 	trunc(mat.gmt_codigo)		= " ).append( codigoGrupo ).append(' ');
		}
		sbQuery.append("	order by mat.nome");

		Query query = createNativeQuery(sbQuery.toString(), SceEstoqueAlmoxarifado.class);
		List<SceEstoqueAlmoxarifado> lstEstAlmox = query.getResultList();

		StringBuilder sbQueryAux = new StringBuilder(300).append("	SELECT  eal.seq," ).append( "			ipm.quantidade "
				).append( "	FROM    agh.sco_materiais              mat," ).append( "	        agh.sce_estq_almoxs            eal,"
				).append( "	        agh.sce_item_pacote_materiais  ipm" ).append( "	WHERE   1=1 ")
		.append("	AND     eal.seq                   	= ipm.eal_seq")
		.append("	AND     mat.codigo                	= eal.mat_codigo")
		.append("	AND 	ipm.pmt_cct_codigo_refere 	= " ).append( pacote.getCodigoCentroCustoProprietario() ).append(' ')
		.append("	AND 	ipm.pmt_cct_codigo 			= " ).append( pacote.getCodigoCentroCustoAplicacao() ).append(' ')
		.append("	AND 	ipm.pmt_numero 				= " ).append( pacote.getNumero() ).append(' ');
		if (codigoGrupo != null) {
			sbQueryAux.append("	AND 	trunc(mat.gmt_codigo)		= " ).append( codigoGrupo ).append(' ');
		}
		sbQueryAux.append("	order by mat.nome");

		Query queryAux = createNativeQuery(sbQueryAux.toString());
		List<Object[]> lstValues = queryAux.getResultList();

		for (SceEstoqueAlmoxarifado estAlmox : lstEstAlmox) {
			for (Object[] quantidade : lstValues) {
				if (Integer.parseInt(quantidade[0].toString()) == estAlmox.getSeq().intValue()) {
					estAlmox.setQuantidade(Integer.parseInt(quantidade[1].toString()));
				}
			}
		}

		return lstEstAlmox;
	}

	/**
	 * Criteria da pesquisa de estoque almoxarifado por seq e que contenha
	 * material
	 * 
	 * @param seq
	 * @return
	 */
	private DetachedCriteria obterCriteriaEstoqueAlmoxariafoComMaterialPorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "mat", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.SEQ.toString(), seq));
		return criteria;
	}

	/**
	 * Pesquisa estoque almoxarifado por seq e que contenha material
	 * 
	 * @param seq
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxariafoComMaterialPorSeq(Integer seq) {
		return executeCriteria(obterCriteriaEstoqueAlmoxariafoComMaterialPorSeq(seq));
	}

	public SceEstoqueAlmoxarifado pesquisarEstoqueAlmPorMaterialAlmoxFornecedor(SceMovimentoMaterial sceMovimentoMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), sceMovimentoMaterial.getMaterial()
				.getCodigo()));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), sceMovimentoMaterial.getAlmoxarifado()
				.getSeq()));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), sceMovimentoMaterial.getFornecedor()
				.getNumero()));

		List<SceEstoqueAlmoxarifado> listaEstAlm = executeCriteria(criteria);

		if (listaEstAlm != null && !listaEstAlm.isEmpty()) {

			return listaEstAlm.get(0);

		}

		return null;

	}

	@SuppressWarnings("PMD.NPathComplexity")
	public Boolean existeSaldoEstoqueAlmoxarifado(Integer codigoMaterial, Short seqAlmoxarifado, Integer numeroFornecedor) {

		if (codigoMaterial == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));

		if (seqAlmoxarifado != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), seqAlmoxarifado));
		}

		if (numeroFornecedor != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));
		}

		criteria.setProjection(Projections.sqlProjection("coalesce({alias}.qtde_disponivel,0) +  coalesce({alias}.qtde_bloqueada,0) + coalesce({alias}.qtde_em_uso,0) AS qtd", new String[]{"qtd"}, new Type[]{IntegerType.INSTANCE}));
		
		Integer qtd = (Integer) executeCriteriaUniqueResult(criteria);
		
		return qtd > 0;
	}

	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoPorMaterialAlmoxarifadoFornecedor(Integer codigoMaterial,
			Short seqAlmoxarifado, Integer numeroFornecedor) {

		if (codigoMaterial == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}

		if (seqAlmoxarifado == null) {
			throw new IllegalArgumentException("Parâmetro seqAlmoxarifado não foi informado.");
		}

		if (numeroFornecedor == null) {
			throw new IllegalArgumentException("Parâmetro numeroFornecedor não foi informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), seqAlmoxarifado));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));

		return executeCriteria(criteria);

	}

	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoQuantidadeDisponivelBloqueadaPorMaterial(Integer codigoMaterial) {

		if (codigoMaterial == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));

		// Caso a quantidade disponível ou a quantidade bloqueada seja maior que
		// ZERO
		Criterion lhs = Restrictions.gt(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString(), 0);
		Criterion rhs = Restrictions.gt(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString(), 0);

		criteria.add(Restrictions.or(lhs, rhs));

		return executeCriteria(criteria);

	}

	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoPorMaterial(Integer codigoMaterial) {

		if (codigoMaterial == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		return executeCriteria(criteria);
	}

	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoPorMaterialAlmoxarifadoCentral(Integer codigoMaterial,
			Short seqAlmoxarifadoCentral) {

		if (codigoMaterial == null || seqAlmoxarifadoCentral == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), seqAlmoxarifadoCentral));
		return executeCriteria(criteria);
	}

	/**
	 * Pesquisa SceEstoqueAlmoxarifado SEM SALDO através do material
	 * 
	 * @param codigoMaterial
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoMaterialSemSaldo(Integer codigoMaterial) {

		if (codigoMaterial == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString(), 0));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString(), 0));

		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaMaterialCadastradoAlmoxarifado(Integer codigoMaterial, Short seqAlmoxarifado,
			Integer numeroFornecedor) {

		if (codigoMaterial == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}

		if (seqAlmoxarifado == null) {
			throw new IllegalArgumentException("Parâmetro seqAlmoxarifado não foi informado.");
		}

		if (numeroFornecedor == null) {
			throw new IllegalArgumentException("Parâmetro numeroFornecedor não foi informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL, "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL, "GMT", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), seqAlmoxarifado));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));

		return criteria;
	}

	public List<SceEstoqueAlmoxarifado> pesquisarMaterialCadastradoAlmoxarifado(Integer codigoMaterial, Short seqAlmoxarifado,
			Integer numeroFornecedor) {
		DetachedCriteria criteria = this.obterCriteriaMaterialCadastradoAlmoxarifado(codigoMaterial, seqAlmoxarifado, numeroFornecedor);
		return executeCriteria(criteria);
	}

	public List<SceEstoqueAlmoxarifado> pesquisarMaterialCadastradoAlmoxarifadoComSolicitacaoCompra(Integer codigoMaterial,
			Short seqAlmoxarifado, Integer numeroFornecedor) {
		DetachedCriteria criteria = this.obterCriteriaMaterialCadastradoAlmoxarifado(codigoMaterial, seqAlmoxarifado, numeroFornecedor);
		criteria.add(Restrictions.isNotNull(SceEstoqueAlmoxarifado.Fields.SOLICITACAO_COMPRA.toString()));
		return executeCriteria(criteria);
	}

	public List<RelatorioContagemEstoqueParaInventarioVO> pesquisarDadosRelatorioContagemEstoqueInventario(Short seqAlmoxarifado,
			Integer codigoGrupo, DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque estocavel,
			DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario ordem, Boolean disponivelEstoque) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");

		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL, "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL, "GMT", JoinType.INNER_JOIN);

		// PROJECTIONS
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString()),
				RelatorioContagemEstoqueParaInventarioVO.Fields.CODIGO_GRUPO_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()),
				RelatorioContagemEstoqueParaInventarioVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()),
				RelatorioContagemEstoqueParaInventarioVO.Fields.CODIGO_MATERIAL.toString());
		p.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()),
				RelatorioContagemEstoqueParaInventarioVO.Fields.NUMERO_FORNECEDOR.toString());
		p.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.UMD_CODIGO.toString()),
				RelatorioContagemEstoqueParaInventarioVO.Fields.UNIDADE_MEDIDA_CODIGO.toString());
		p.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.ENDERECO.toString()),
				RelatorioContagemEstoqueParaInventarioVO.Fields.ENDERECO_ESTOQUE_ALMOX.toString());
		p.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QUANTIDADE_BLOQUEADA.toString()),
				RelatorioContagemEstoqueParaInventarioVO.Fields.QUANTIDADE_BLOQUEADA_ESTOQUE_ALMOX.toString());
		p.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QUANTIDADE_DISPONIVEL.toString()),
				RelatorioContagemEstoqueParaInventarioVO.Fields.QUANTIDADE_DISPONIVEL_ESTOQUE_ALMOX.toString());

		criteria.setProjection(p);

		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.SITUACAO.toString(), DominioSituacao.A));
		if (codigoGrupo != null) {
			criteria.add(Restrictions.eq(
					"MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString() + "." + ScoGrupoMaterial.Fields.CODIGO.toString(), codigoGrupo));
		}
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), seqAlmoxarifado));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		if (disponivelEstoque) {
			criteria.add(Restrictions.sqlRestriction("((COALESCE({alias}." + "qtde_disponivel, 0)" + " + COALESCE({alias}."
					+ "qtde_bloqueada, 0)) > 0 )"));
		}

		if (estocavel != null && !DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque.T.equals(estocavel)) {
			Boolean flag = null;

			switch (estocavel) {
			case S:
				flag = true;
				break;
			case N:
				flag = false;
				break;
			default:
				break;
			}

			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), flag));
		}

		if (DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario.G.equals(ordem)) {
			criteria.addOrder(Order.asc("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString()));
			criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.NOME.toString()));
		} else if (DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario.N.equals(ordem)) {
			criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.NOME.toString()));
		} else if (DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario.C.equals(ordem)) {
			criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.CODIGO.toString()));
		} else if (DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario.E.equals(ordem)) {
			criteria.addOrder(Order.asc("EAL." + SceEstoqueAlmoxarifado.Fields.ENDERECO.toString()));
		} else {
			criteria.addOrder(Order.asc("EAL." + SceEstoqueAlmoxarifado.Fields.QUANTIDADE_BLOQUEADA.toString()));
		}

		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioContagemEstoqueParaInventarioVO.class));

		return executeCriteria(criteria);
	}

	public SceEstoqueAlmoxarifado pesquisarSaldosEstoque(SceMovimentoMaterial sceMovimentoMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()));
		p.add(Projections.sum(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString()));
		p.add(Projections.sum(SceEstoqueAlmoxarifado.Fields.QTDE_EM_USO.toString()));

		criteria.setProjection(p);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), sceMovimentoMaterial.getMaterial()
				.getCodigo()));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), sceMovimentoMaterial.getFornecedor()
				.getNumero()));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		List<Object[]> listaResultado = executeCriteria(criteria);
		SceEstoqueAlmoxarifado retorno = null;

		if (listaResultado != null) {
			retorno = new SceEstoqueAlmoxarifado();
			Object[] resultado = listaResultado.get(0);
			retorno.setQtdeDisponivel(Integer.parseInt(resultado[0].toString()));
			retorno.setQtdeBloqueada(Integer.parseInt(resultado[1].toString()));
			retorno.setQtdeEmUso(Integer.parseInt(resultado[2].toString()));
		}

		return retorno;

	}

	/**
	 * Pesquisa SceEstoqueAlmoxarifado SEM SALDO através do material
	 * 
	 * @param codigoMaterial
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoParaNotaRecebimento(Integer codigoMaterial, Short almSeq,
			Integer frnHcpa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almSeq));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), frnHcpa));

		return executeCriteria(criteria);
	}

	/*
	 * verifica se viola a CONSTRAINT sce_eal_uk1
	 */
	public Boolean verificaEstoqueAlomxarifadoExistente(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado) {
		List<SceEstoqueAlmoxarifado> result = null;

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), sceEstoqueAlmoxarifado.getMaterial()));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), sceEstoqueAlmoxarifado.getAlmoxarifado()));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), sceEstoqueAlmoxarifado.getFornecedor()));

		if (sceEstoqueAlmoxarifado.getSeq() != null) {
			criteria.add(Restrictions.ne(SceEstoqueAlmoxarifado.Fields.SEQ.toString(), sceEstoqueAlmoxarifado.getSeq()));
		}
		result = executeCriteria(criteria);
		if (result.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Obtém SceEstoqueAlmoxarifado por almoxarifado, código do material e
	 * número do fornecedor sem realizar joins desnecessários.
	 * 
	 * 
	 * @param seqAlmoxarifadoOrigem
	 * @param codigoMaterial
	 * @param numeroFornecedor
	 * @return
	 */
	public SceEstoqueAlmoxarifado pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoMaterialFornecedor(final Short seqAlmoxarifadoOrigem,
			final Integer codigoMaterial, final Integer numeroFornecedor) {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), seqAlmoxarifadoOrigem));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));

		return (SceEstoqueAlmoxarifado) executeCriteriaUniqueResult(criteria);
	}

	public SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoOrigemSaldoMenorIgualEstoqueMinimoPorMaterialFornecedor(
			Short seqAlmoxarifadoOrigem, Integer codigoMaterial, Integer numeroFornecedor) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL_O");

		criteria.createAlias("EAL_O." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM_O", JoinType.INNER_JOIN);
		criteria.createAlias("EAL_O." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN_O", JoinType.INNER_JOIN);
		criteria.createAlias("EAL_O." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT_O", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("EAL_O." + SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("EAL_O." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.eq("ALM_O." + SceAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifadoOrigem));
		criteria.add(Restrictions.eq("MAT_O." + ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.eq("FRN_O." + ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedor));

		return (SceEstoqueAlmoxarifado) executeCriteriaUniqueResult(criteria);

	}

	/**
	 * 
	 * @param numeroFornecedorHospital
	 * @param seqAlmoxarifadoOrigem
	 * @param seqAlmoxarifadoRecebe
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarMaterialSaldoMenorIgualEstoqueMinimoAlmoxarifadoDestino(Integer numeroFornecedorHospital,
			Short seqAlmoxarifadoOrigem, Short seqAlmoxarifadoRecebe, Long classificacaoMaterialInicial, Long classificacaoMaterialFinal) {

		// Criteria principal do estoque almoxarifado de DESTINO!
		DetachedCriteria criteriaEalD = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL_D");

		criteriaEalD.createAlias("EAL_D." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM_D", JoinType.INNER_JOIN);
		criteriaEalD.createAlias("EAL_D." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN_D", JoinType.INNER_JOIN);
		criteriaEalD.createAlias("EAL_D." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT_D", JoinType.INNER_JOIN);

		criteriaEalD.add(Restrictions.eq("ALM_D." + SceAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifadoRecebe));

		criteriaEalD.add(Restrictions.eq("FRN_D." + ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedorHospital));
		criteriaEalD.add(Restrictions.eq("EAL_D." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteriaEalD.add(Restrictions.gt("EAL_D." + SceEstoqueAlmoxarifado.Fields.QTDE_ESTQ_MIN.toString(), 0));

		// Consulta secundária (EXISTS) do intervalo de classificação de
		// materiais
		DetachedCriteria subQueryMcl = DetachedCriteria.forClass(ScoMateriaisClassificacoes.class, "MCL");
		subQueryMcl.setProjection(Projections.property("MCL." + ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString()));
		subQueryMcl.add(Property.forName("MCL." + ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString()).eqProperty(
				"MAT_D." + ScoMaterial.Fields.CODIGO.toString()));
		subQueryMcl.add(Restrictions.between("MCL." + ScoMateriaisClassificacoes.Fields.CN5.toString(), classificacaoMaterialInicial,
				classificacaoMaterialFinal));
		// Acrescenta consulta secundária do intervalo de classificação de
		// materiais...
		criteriaEalD.add(Subqueries.exists(subQueryMcl));

		// Consulta secundária (EXISTS) do estoque almoxarifado de ORIGEM!
		DetachedCriteria subQueryEalO = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL_O");

		subQueryEalO.setProjection(Projections.property("EAL_O." + SceEstoqueAlmoxarifado.Fields.SEQ.toString()));

		subQueryEalO.createAlias("EAL_O." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM_O", JoinType.INNER_JOIN);
		subQueryEalO.createAlias("EAL_O." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN_O", JoinType.INNER_JOIN);
		subQueryEalO.createAlias("EAL_O." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT_O", JoinType.INNER_JOIN);

		subQueryEalO.add(Restrictions.eq("ALM_O." + SceAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifadoOrigem));

		subQueryEalO.add(Property.forName("MAT_O." + ScoMaterial.Fields.CODIGO.toString()).eqProperty(
				"MAT_D." + ScoMaterial.Fields.CODIGO.toString()));
		subQueryEalO.add(Property.forName("FRN_O." + ScoFornecedor.Fields.NUMERO.toString()).eqProperty(
				"FRN_D." + ScoFornecedor.Fields.NUMERO.toString()));

		subQueryEalO.add(Restrictions.eq("EAL_O." + SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString(), Boolean.TRUE));
		subQueryEalO.add(Restrictions.eq("EAL_O." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		// Acrescenta consulta secundária do estoque almoxarifado de ORIGEM...
		criteriaEalD.add(Subqueries.exists(subQueryEalO));

		return executeCriteria(criteriaEalD);

	}

	/**
	 * 
	 * @param numeroFornecedorHospital
	 * @param seqAlmoxarifadoOrigem
	 * @param seqAlmoxarifadoRecebe
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarMaterialClassificacaoGeralMaterialExpediente(Integer numeroFornecedorHospital,
			Short seqAlmoxarifadoOrigem, Short seqAlmoxarifadoRecebe, List<Integer> intervaloGruposMaterialValidos) {

		// Criteria principal do estoque almoxarifado de DESTINO!
		DetachedCriteria criteriaEalD = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL_D");

		criteriaEalD.createAlias("EAL_D." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM_D", JoinType.INNER_JOIN);
		criteriaEalD.createAlias("EAL_D." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN_D", JoinType.INNER_JOIN);
		criteriaEalD.createAlias("EAL_D." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT_D", JoinType.INNER_JOIN);
		criteriaEalD.createAlias("MAT_D." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT_D", JoinType.INNER_JOIN);

		Integer[] gruposMateriaisValidos = { 4, 6, 9, 17, 20, 34, 36, 39, 41 };
		criteriaEalD.add(Restrictions.in("GMT_D." + ScoGrupoMaterial.Fields.CODIGO.toString(), gruposMateriaisValidos));

		// TODO utilizar após criacao do parametro
		// GRUPOS_VALIDOS_MATERIAL_EXPEDIENTE
		// criteriaEalD.add(Restrictions.in("GMT_D." +
		// ScoGrupoMaterial.Fields.CODIGO.toString(),
		// intervaloGruposMaterialValidos));

		criteriaEalD.add(Restrictions.eq("ALM_D." + SceAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifadoRecebe));

		criteriaEalD.add(Restrictions.eq("FRN_D." + ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedorHospital));
		criteriaEalD.add(Restrictions.eq("EAL_D." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteriaEalD.add(Restrictions.gt("EAL_D." + SceEstoqueAlmoxarifado.Fields.QTDE_ESTQ_MIN.toString(), 0));

		// Consulta secundária (EXISTS) do estoque almoxarifado de ORIGEM!
		DetachedCriteria subQueryEalO = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL_O");

		subQueryEalO.setProjection(Projections.property("EAL_O." + SceEstoqueAlmoxarifado.Fields.SEQ.toString()));

		subQueryEalO.createAlias("EAL_O." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM_O", JoinType.INNER_JOIN);
		subQueryEalO.createAlias("EAL_O." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN_O", JoinType.INNER_JOIN);
		subQueryEalO.createAlias("EAL_O." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT_O", JoinType.INNER_JOIN);

		subQueryEalO.add(Restrictions.eq("ALM_O." + SceAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifadoOrigem));

		subQueryEalO.add(Property.forName("MAT_O." + ScoMaterial.Fields.CODIGO.toString()).eqProperty(
				"MAT_D." + ScoMaterial.Fields.CODIGO.toString()));
		subQueryEalO.add(Property.forName("FRN_O." + ScoFornecedor.Fields.NUMERO.toString()).eqProperty(
				"FRN_D." + ScoFornecedor.Fields.NUMERO.toString()));

		subQueryEalO.add(Restrictions.eq("EAL_O." + SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString(), Boolean.TRUE));
		subQueryEalO.add(Restrictions.eq("EAL_O." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		// Acrescenta consulta secundária do estoque almoxarifado de ORIGEM...
		criteriaEalD.add(Subqueries.exists(subQueryEalO));

		return executeCriteria(criteriaEalD);

	}

	/**
	 * Obtem a quantidade de estoque almoxarifado consignado através do
	 * almoxarifado de origem e destino de uma transferência automática
	 * 
	 * @param seqAlmoxarifadoOrigem
	 * @param seqAlmoxarifadoDestino
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoaConsignadoTransferencia(Integer seqEstoqueAlmoxarifadoOrigem,
			Integer seqEstoqueAlmoxarifadoDestino) {

		DetachedCriteria criteriaEal1 = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL1");
		criteriaEal1.createAlias("EAL1." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM1", JoinType.INNER_JOIN);
		criteriaEal1.createAlias("EAL1." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN1", JoinType.INNER_JOIN);
		criteriaEal1.createAlias("EAL1." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT1", JoinType.INNER_JOIN);

		criteriaEal1.add(Restrictions.eq("EAL1." + SceEstoqueAlmoxarifado.Fields.SEQ.toString(), seqEstoqueAlmoxarifadoOrigem));

		// Criteria principal do estoque almoxarifado de DESTINO!
		DetachedCriteria subQueryEal2 = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL2");
		subQueryEal2.setProjection(Projections.property("EAL2." + SceEstoqueAlmoxarifado.Fields.SEQ.toString()));
		subQueryEal2.createAlias("EAL2." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM2", JoinType.INNER_JOIN);
		subQueryEal2.createAlias("EAL2." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN2", JoinType.INNER_JOIN);
		subQueryEal2.createAlias("EAL2." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT2", JoinType.INNER_JOIN);

		subQueryEal2.add(Restrictions.eq("EAL2." + SceAlmoxarifado.Fields.SEQ.toString(), seqEstoqueAlmoxarifadoDestino));

		subQueryEal2.add(Property.forName("ALM2." + SceAlmoxarifado.Fields.SEQ.toString()).neProperty(
				"ALM1." + SceAlmoxarifado.Fields.SEQ.toString().toString()));
		subQueryEal2.add(Property.forName("FRN2." + ScoFornecedor.Fields.NUMERO.toString()).neProperty(
				"FRN1." + ScoFornecedor.Fields.NUMERO.toString()));
		subQueryEal2.add(Property.forName("MAT2." + ScoMaterial.Fields.CODIGO.toString()).eqProperty(
				"MAT1." + ScoMaterial.Fields.CODIGO.toString()));

		criteriaEal1.add(Subqueries.exists(subQueryEal2));

		return executeCriteria(criteriaEal1);

	}

	/**
	 * Pesquisa SceEstoqueAlmoxarifado através do material e que contenha
	 * quantidade disponível ou bloqueada
	 * 
	 * @param codigoMaterial
	 * @return
	 */
	public SceEstoqueAlmoxarifado pesquisarEstoqueAlmoxarifadoQuantidadeBloqEntradaTransf(Integer ealSeq, Integer quantidade) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.SEQ.toString(), ealSeq));

		criteria.add(Restrictions.ge(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQ_ENTR_TRANSF.toString(), quantidade));

		return (SceEstoqueAlmoxarifado) executeCriteriaUniqueResult(criteria);

	}

	public List<SceEstoqueAlmoxarifado> pesquisarMateriaisEstoquePorCodigoDescricaoAlmoxarifado(String parametro, Short seqAlmoxarifado,
			BigDecimal codigoFornecedor, List<Integer> listaGrupos, Boolean somenteEstocaveis, Boolean somenteDiretos) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");

		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), "UND", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM", JoinType.INNER_JOIN);

		ProjectionList pList = Projections.projectionList();

		pList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.SEQ.toString()), SceEstoqueAlmoxarifado.Fields.SEQ.toString());
		pList.add(Projections.property("ALM." + SceAlmoxarifado.Fields.SEQ.toString()),
				SceEstoqueAlmoxarifado.Fields.SEQ_ALMOXARIFADO.toString());
		pList.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()),
				SceEstoqueAlmoxarifado.Fields.COD_MATERIAL.toString());
		pList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), SceEstoqueAlmoxarifado.Fields.MATERIAL_NOME.toString());
		pList.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString()),
				SceEstoqueAlmoxarifado.Fields.FORNECEDOR_NUMERO.toString());
		pList.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()),
				SceEstoqueAlmoxarifado.Fields.RAZAO_SOCIAL_FORNECEDOR.toString());
		pList.add(Projections.property("UND." + ScoUnidadeMedida.Fields.CODIGO.toString()),
				SceEstoqueAlmoxarifado.Fields.CODIGO_UNIDADE_MEDIDA.toString());
		pList.add(Projections.property("UND." + ScoUnidadeMedida.Fields.DESCRICAO.toString()),
				SceEstoqueAlmoxarifado.Fields.DESCRICAO_UNIDADE_MEDIDA.toString());

		criteria.setProjection(pList);

		if (somenteEstocaveis != null && somenteEstocaveis) {
			criteria.add(Restrictions.or(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), Boolean.TRUE),
					Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.IND_ESTOQUE_TEMPORARIO.toString(), Boolean.TRUE)));
		}

		if (somenteDiretos != null && somenteDiretos) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), Boolean.FALSE));
		}

		if (listaGrupos != null && !listaGrupos.isEmpty()) {
			criteria.createCriteria("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT", JoinType.INNER_JOIN);
			criteria.add(Restrictions.in("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString(), listaGrupos));
		}

		String descricao = parametro;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(descricao)) {
			codigo = Integer.valueOf(descricao);
			descricao = null;
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), codigo));
		}

		if (descricao != null) {
			criteria.add(Restrictions.ilike("MAT." + ScoMaterial.Fields.NOME.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (seqAlmoxarifado != null) {
			criteria.add(Restrictions.eq(
					"EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString() + "." + SceAlmoxarifado.Fields.SEQ.toString(),
					seqAlmoxarifado));
		}

		criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), codigoFornecedor.intValue()));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		criteria.setResultTransformer(Transformers.aliasToBean(SceEstoqueAlmoxarifado.class));

		criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}

	// 6617
	public List<SceEstoqueAlmoxarifado> pesquisarSceEstoqueAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceEstoqueAlmoxarifado estoqueAlmox) {
		DetachedCriteria criteria = montaCriteriaPaginacaoConsulta(estoqueAlmox);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), FetchMode.JOIN);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	// 6617
	public Long pesquisarSceEstoqueAlmoxarifadoCount(SceEstoqueAlmoxarifado estoqueAlmox) {

		DetachedCriteria criteria = montaCriteriaPaginacaoConsulta(estoqueAlmox);

		return this.executeCriteriaCount(criteria);
	}

	public SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(Short seqAlmoxarifadoOrigem,
			Integer codigoMaterial, Integer fornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "eal");

		criteria.createAlias("eal." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "alm", JoinType.INNER_JOIN);
		criteria.createAlias("eal." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "mat", JoinType.INNER_JOIN);
		criteria.createAlias("eal." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "frn", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("eal." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("alm." + SceAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifadoOrigem));
		criteria.add(Restrictions.eq("mat." + ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));

		if (fornecedor != null) {
			criteria.add(Restrictions.eq("frn." + ScoFornecedor.Fields.NUMERO.toString(), fornecedor));
		}

		List<SceEstoqueAlmoxarifado> lst = executeCriteria(criteria, 0, 1, null, false);

		if (lst != null && lst.size() > 0) {
			return lst.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Obtém a quantidade de registros
	 * 
	 * @return
	 */
	public Long obterQuantidadeRegistros() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.setProjection(Projections.count(SceEstoqueAlmoxarifado.Fields.SEQ.toString()));
		final Long max = (Long) executeCriteriaUniqueResult(criteria);
		if (max == null) {
			return 0l;
		}
		return max;
	}

	public SceEstoqueAlmoxarifado pesquisarSaldosEstoqueTerceiros(Integer codMaterial, Short almox, Integer codHospital) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()));
		p.add(Projections.sum(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString()));

		criteria.setProjection(p);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codMaterial));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almox));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.ne(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), codHospital));

		List<Object[]> listaResultado = executeCriteria(criteria);
		SceEstoqueAlmoxarifado retorno = null;

		if (listaResultado != null) {
			retorno = new SceEstoqueAlmoxarifado();
			Object[] resultado = listaResultado.get(0);

			if (resultado[0] != null) {
				retorno.setQtdeDisponivel(Integer.valueOf(resultado[0].toString()));
			}
			if (resultado[1] != null) {
				retorno.setQtdeBloqueada(Integer.valueOf(resultado[1].toString()));
			}
		}

		return retorno;
	}

	public Date obterDataUltimaCompraFundoFixo(Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.setProjection(Projections.max(SceEstoqueAlmoxarifado.Fields.DATA_ULTIMA_COMPRA_FF.toString()));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), matCodigo));
		criteria.add(Restrictions.isNotNull(SceEstoqueAlmoxarifado.Fields.DATA_ULTIMA_COMPRA_FF.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public Date obterDataUltimaCompra(Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.setProjection(Projections.max(SceEstoqueAlmoxarifado.Fields.DATA_ULTIMA_COMPRA.toString()));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), matCodigo));
		criteria.add(Restrictions.isNotNull(SceEstoqueAlmoxarifado.Fields.DATA_ULTIMA_COMPRA.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public Date obterDataUltimoConsumo(Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.setProjection(Projections.max(SceEstoqueAlmoxarifado.Fields.DATA_ULTIMO_CONSUMO.toString()));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), matCodigo));
		criteria.add(Restrictions.isNotNull(SceEstoqueAlmoxarifado.Fields.DATA_ULTIMO_CONSUMO.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Pesquisa estoque almoxarifado para geração de ponto pedido
	 * 
	 * @param codigoMaterial
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public List<AtualizarPontoPedidoVO> pesquisarEstoqueAlmoxarifadoAtualizarPontoPedido(Integer codigoMaterial,
			Short seqAlmoxarifadoCentral, Integer numeroFornecedorHu) {

		// Consulta principal
		StringBuilder sqlSelect = new StringBuilder(700);

		// SQL Select 1: Aliases seq, almSeq, matCodigo, tempoReposicao e
		// calculaMediaPonderada
		sqlSelect.append("SELECT EAL2.SEQ as seq,")
		.append("EAL2.ALM_SEQ as almSeq,")
		.append("EAL2.MAT_CODIGO as matCodigo,")
		// .append("NVL(EAL2.TEMPO_REPOSICAO,0) TEMPO_REPOSICAO,") //
		// NVL
		.append("EAL2.TEMPO_REPOSICAO as tempoReposicao,")
		.append("ALM.IND_CALCULA_MEDIA_PONDERADA as calculaMediaPonderada")
		.append(" FROM AGH.SCE_ALMOXARIFADOS ALM,")
		.append("AGH.SCE_ESTQ_ALMOXS EAL2,")
		.append("AGH.SCO_MATERIAIS MAT,")
		.append("AGH.SCE_ESTQ_ALMOXS EAL1")
		// .append("WHERE EAL1.ALM_SEQ  = 1")
		.append(" WHERE EAL1.ALM_SEQ  = " ).append( seqAlmoxarifadoCentral) // ALMOXARIFADO
																				// CENTRAL
		// .append(" AND EAL1.MAT_CODIGO = DECODE(P_MAT_CODIGO,NULL,EAL1.MAT_CODIGO,P_MAT_CODIGO)")
		// // DECODE
		.append(" AND EAL1.MAT_CODIGO = " ).append( (codigoMaterial != null ? codigoMaterial : "EAL1.MAT_CODIGO"))
		// .append("AND EAL1.FRN_NUMERO = 1")
		.append(" AND EAL1.FRN_NUMERO = " ).append( numeroFornecedorHu) // FORNECEDOR
																			// HU

		// ATENÇÃO ALTERAÇÃO: Somente materiais estocáveis serão considerados!
		.append(" AND MAT.IND_ESTOCAVEL = 'S'")

		.append(" AND EAL1.IND_SITUACAO ||'' = 'A'")
		.append(" AND MAT.CODIGO      = EAL1.MAT_CODIGO")
		.append(" AND EAL2.MAT_CODIGO = MAT.CODIGO")
		.append(" AND EAL2.ALM_SEQ    = MAT.ALM_SEQ")
		.append(" AND EAL2.IND_PONTO_PEDIDO_CALC ||'' = 'S'")
		.append(" AND EAL2.IND_SITUACAO ||'' = 'A'")
		// .append("AND EAL2.FRN_NUMERO = 1")
		.append(" AND EAL2.FRN_NUMERO = " ).append( numeroFornecedorHu) // FORNECEDOR
		.append(" AND ALM.SEQ = MAT.ALM_SEQ");

		// Cria SQL nativa
		SQLQuery query = createSQLQuery(sqlSelect.toString());

		// Faz o hibernate detectar o tipo dos aliases
		query.addScalar("seq", IntegerType.INSTANCE);
		query.addScalar("almSeq", ShortType.INSTANCE);
		query.addScalar("matCodigo", IntegerType.INSTANCE);
		query.addScalar("tempoReposicao", ShortType.INSTANCE);
		query.addScalar("calculaMediaPonderada", BooleanType.INSTANCE);

		// Transforma e seta aliases do resultado no VO
		query.setResultTransformer(Transformers.aliasToBean(AtualizarPontoPedidoVO.class));

		// Retorna Lista
		return query.list();
	}

	public SceEstoqueAlmoxarifado buscarSceEstoqueAlmoxarifadoPorItemTransferencia(Integer seq, Short almSeqRecebe) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almSeqRecebe));
		return (SceEstoqueAlmoxarifado) executeCriteriaUniqueResult(criteria);

	}

	public SceEstoqueAlmoxarifado buscarSceEstoqueAlmoxarifadoPorAlmoxarifado(Short almSeq, Integer matCodigo, Integer frnNumero) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almSeq));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), frnNumero));

		List<SceEstoqueAlmoxarifado> retorno = executeCriteria(criteria);

		if (!retorno.isEmpty()) {

			return retorno.get(0);

		}

		return null;

	}

	public List<SceEstoqueAlmoxarifado> pesquisarMateriaisPorTransferencia(Short almoxSeq, Short almoxSeqReceb, Integer frnNumero,
			String paramPesq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL1");
		criteria.createAlias("EAL1." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT1", JoinType.INNER_JOIN);
		criteria.createAlias("EAL1." + SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), "UM", JoinType.LEFT_OUTER_JOIN);

		if (paramPesq != null && !"".equals(paramPesq)) {

			if (CoreUtil.isNumeroInteger(paramPesq)) {

				criteria.add(Restrictions.eq("EAL1." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(),
						Integer.parseInt(paramPesq)));

			} else {

				criteria.add(Restrictions.ilike("MAT1." + ScoMaterial.Fields.NOME.toString(), paramPesq, MatchMode.ANYWHERE));

			}

		}

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almoxSeqReceb));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		StringBuilder sb = new StringBuilder(600).append("{alias}.MAT_CODIGO + coalesce({alias}.FRN_NUMERO,0) in");
		sb.append("       						(	select 	eal1.mat_codigo + coalesce(eal1.frn_numero,0)");
		sb.append("									from  	agh.SCE_ESTQ_ALMOXS  eal2,");
		sb.append("											agh.SCE_ESTQ_ALMOXS  eal1");
		sb.append("									where 	eal1.alm_seq         	= ? ");
		sb.append("									and  	eal2.alm_seq       		= ? ");
		sb.append("									and  	eal2.mat_codigo   		= {alias}.MAT_CODIGO");
		sb.append("									and  	eal1.mat_codigo   		= {alias}.MAT_CODIGO");
		sb.append("									and 	( (eal2.frn_numero  = eal1.frn_numero)");
		sb.append("									or (eal2.frn_numero is null");
		sb.append("									and eal1.frn_numero is null)))");

		Object[] values = { almoxSeq, almoxSeqReceb };
		Type[] types = { ShortType.INSTANCE, ShortType.INSTANCE };

		criteria.add(Restrictions.sqlRestriction(sb.toString(), values, types));

		criteria.addOrder(Order.asc("MAT1." + ScoMaterial.Fields.NOME.toString()));

		return executeCriteria(criteria, 0, 100, null, true);

	}

	public Long pesquisarMateriaisPorTransferenciaCount(Short almoxSeq, Short almoxSeqReceb, Integer frnNumero, String paramPesq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL1");
		criteria.createAlias("EAL1." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT1", JoinType.INNER_JOIN);

		if (paramPesq != null && !"".equals(paramPesq)) {

			if (CoreUtil.isNumeroInteger(paramPesq)) {

				criteria.add(Restrictions.eq("EAL1." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(),
						Integer.parseInt(paramPesq)));

			} else {

				criteria.add(Restrictions.ilike("MAT1." + ScoMaterial.Fields.NOME.toString(), paramPesq, MatchMode.ANYWHERE));

			}

		}

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almoxSeqReceb));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		StringBuilder sb = new StringBuilder(600).append("{alias}.MAT_CODIGO + coalesce({alias}.FRN_NUMERO) in");
		sb.append("       						(	select 	eal1.mat_codigo + coalesce(eal1.frn_numero)");
		sb.append("									from  	agh.SCE_ESTQ_ALMOXS  eal2,");
		sb.append("											agh.SCE_ESTQ_ALMOXS  eal1");
		sb.append("									where 	eal1.alm_seq         	= ? ");
		sb.append("									and  	eal2.alm_seq       		= ? ");
		sb.append("									and  	eal2.mat_codigo   		= {alias}.MAT_CODIGO");
		sb.append("									and  	eal1.mat_codigo   		= {alias}.MAT_CODIGO");
		sb.append("									and 	( (eal2.frn_numero  = eal1.frn_numero)");
		sb.append("									or (eal2.frn_numero is null");
		sb.append("									and eal1.frn_numero is null)))");

		Object[] values = { almoxSeq, almoxSeqReceb };
		Type[] types = { ShortType.INSTANCE, ShortType.INSTANCE };

		criteria.add(Restrictions.sqlRestriction(sb.toString(), values, types));

		return executeCriteriaCount(criteria);
	}

	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoValidadeMaterial(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, SceEstoqueAlmoxarifado estoqueAlmox) {
		DetachedCriteria criteria = montarCriteriaPesquisaEstoqueAlmoxarifadoValidadeMaterial(estoqueAlmox, true);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), FetchMode.JOIN);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarEstoqueAlmoxarifadoValidadeMaterialCount(SceEstoqueAlmoxarifado estoqueAlmox) {
		DetachedCriteria criteria = montarCriteriaPesquisaEstoqueAlmoxarifadoValidadeMaterial(estoqueAlmox, false);

		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaPesquisaEstoqueAlmoxarifadoValidadeMaterial(SceEstoqueAlmoxarifado estoqueAlmox,
			boolean orderByMatNome) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");

		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM");

		if (estoqueAlmox.getMaterial() != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), estoqueAlmox.getMaterial().getCodigo()));
		}
		if (estoqueAlmox.getFornecedor() != null) {
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), estoqueAlmox.getFornecedor().getNumero()));
		}
		if (estoqueAlmox.getAlmoxarifado() != null) {
			criteria.add(Restrictions.eq("ALM." + SceAlmoxarifado.Fields.SEQ.toString(), estoqueAlmox.getAlmoxarifado().getSeq()));
		}
		if (estoqueAlmox.getEndereco() != null && !estoqueAlmox.getEndereco().equals("")) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ENDERECO.toString(), estoqueAlmox.getEndereco()));
		}
		if (estoqueAlmox.getQtdeDisponivel() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString(), estoqueAlmox.getQtdeDisponivel()));
		}
		if (estoqueAlmox.getQtdeBloqueada() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString(), estoqueAlmox.getQtdeBloqueada()));
		}
		if (estoqueAlmox.getIndEstocavel() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString(), estoqueAlmox.getIndEstocavel()));
		}
		if (estoqueAlmox.getIndControleValidade() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_CONTROLE_VALIDADE.toString(),
					estoqueAlmox.getIndControleValidade()));
		}
		if (orderByMatNome) {
			criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.NOME.toString()));
		}
		return criteria;
	}

	// 6617
	public DetachedCriteria montaCriteriaPaginacaoConsulta(SceEstoqueAlmoxarifado almoxarifados) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		if (almoxarifados.getMaterial() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), almoxarifados.getMaterial()));
		}
		if (almoxarifados.getFornecedor() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), almoxarifados.getFornecedor()));
		}
		if (almoxarifados.getAlmoxarifado() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), almoxarifados.getAlmoxarifado()));
		}
		if (almoxarifados.getEndereco() != null && !almoxarifados.getEndereco().isEmpty()) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ENDERECO.toString(), almoxarifados.getEndereco()));
		}
		if (almoxarifados.getIndConsignado() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_CONSIGNADO.toString(), almoxarifados.getIndConsignado()));
		}
		if (almoxarifados.getIndControleValidade() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_CONTROLE_VALIDADE.toString(),
					almoxarifados.getIndControleValidade()));
		}
		if (almoxarifados.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), almoxarifados.getIndSituacao()));
		}
		if (almoxarifados.getIndEstocavel() != null) {
			criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString(), almoxarifados.getIndEstocavel()));
		}

		return criteria;
	}

	// 14497
	public List<SceEstoqueAlmoxarifado> pesquisarDisponibilidadeMaterialEstoque(Integer codigoMaterial, Short unidadeFuncionalSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");

		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM");
		criteria.createAlias("ALM." + SceAlmoxarifado.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UNF");

		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeFuncionalSeq));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), codigoMaterial));
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		// Ordena trazendo primeiramente os fornecedores com maior
		// qtdeDisponivel em estoque
		criteria.addOrder(Order.desc(SceEstoqueAlmoxarifado.Fields.QUANTIDADE_DISPONIVEL.toString()));

		return executeCriteria(criteria);
	}

	public SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoAtivoPorId(Integer ealSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.SEQ.toString(), ealSeq));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return (SceEstoqueAlmoxarifado) executeCriteriaUniqueResult(criteria);
	}

	public Long obterQtdeDispByUnfAndMaterial(Short seqUnf, Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");

		criteria.setProjection(Projections.sum("EAL." + SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()));

		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM");
		criteria.createAlias("ALM." + SceAlmoxarifado.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UNF");

		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seqUnf));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString(), matCodigo));
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.CONTROLE_ESTOQUE.toString(), Boolean.TRUE));

		return (Long) executeCriteriaUniqueResult(criteria);
	}

	public SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoPorSeqEUmdCodigo(Integer seq, String umdCodigo) {
		if (seq == null || umdCodigo == null) {
			throw new IllegalArgumentException("Parametros seq e umdCodigo são obrigatorios!");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.UMD_CODIGO.toString(), umdCodigo));
		return (SceEstoqueAlmoxarifado) executeCriteriaUniqueResult(criteria);
	}

	public List<SceEstoqueAlmoxarifado> buscarEstoqueAlmoxarifadoPorAlmSeqMatCodigoEFrnNumero(Short almSeq, Integer matCodigo,
			Integer frnNumero) {
		if (almSeq == null || matCodigo == null || frnNumero == null) {
			throw new IllegalArgumentException("Parametros almSeq, matCodigo e frnNumero são obrigatorios!");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almSeq));
		criteria.add(Restrictions.or(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), frnNumero),
				Restrictions.isNull(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString())));
		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria);
	}

	/**
	 * Metodo criado para recuperar a quantidade disponivel total e a quantidade
	 * bloqueada total dos estoques por codigo do material. Melhoria #16532
	 * 
	 * @param codigoMaterial
	 * @return
	 */
	public Integer obterQtdeDisponivelQtdeBloqueadaTodosEstoquesPorCodigoMaterial(Integer codigoMaterial) {

		Integer result = 0;

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()));
		p.add(Projections.sum(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString()));

		criteria.setProjection(p);

		criteria.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));

		List<Object[]> lista = executeCriteria(criteria);
		if (lista != null) {

			Integer qtde_disponivel = 0;
			Integer qtde_bloqueada = 0;

			for (Object[] valores : lista) {
				qtde_disponivel += (valores[0] == null) ? 0 : (Integer.parseInt(valores[0].toString()));
				qtde_bloqueada += (valores[1] == null) ? 0 : (Integer.parseInt(valores[1].toString()));
			}
			result = qtde_disponivel + qtde_bloqueada;
		}
		return result;
	}

	public Integer atualizarEstoqueAlmoxarifadoFechamentoMensalEstoqueQuantidadePontoPedido(final Integer quantidadePontoPedido,
			final Integer seqAlmoxarifado) {

		final StringBuilder sbAtualizar = new StringBuilder(100);

		sbAtualizar.append(" UPDATE AGH.SCE_ESTQ_ALMOXS")
		.append(" SET QTDE_PONTO_PEDIDO = " ).append( quantidadePontoPedido)// v_qtde_pp
		.append(" WHERE SEQ = " ).append( seqAlmoxarifado); // r1.seq

		// Cria SQL nativa
		SQLQuery query = this.createSQLQuery(sbAtualizar.toString());

		// Executa a inclusão e retorna a quantidade de registros gravados
		Integer retorno = query.executeUpdate();
		this.flush();

		return retorno;
	}

	public Integer atualizarEstoqueAlmoxarifadoFechamentoMensalEstoqueQuantidadePontoPedidoAlmoxarifadoCentral(
			final Integer quantidadePontoPedido, final Short seqAlmoxarifadoOrigem, final Integer codigoMaterial,
			final Integer numeroFornecedor) {

		// SQL Update
		final StringBuilder sbAtualizar = new StringBuilder(150);

		sbAtualizar.append(" UPDATE AGH.SCE_ESTQ_ALMOXS")
		.append(" SET QTDE_PONTO_PEDIDO = " ).append( quantidadePontoPedido)
		.append(" WHERE mat_codigo = " ).append( codigoMaterial)
		.append(" AND alm_seq = " ).append( seqAlmoxarifadoOrigem)
		.append(" AND frn_numero = " ).append( numeroFornecedor);

		// Cria SQL nativa
		SQLQuery query = this.createSQLQuery(sbAtualizar.toString());

		// Executa a inclusão e retorna a quantidade de registros gravados
		Integer retorno = query.executeUpdate();
		this.flush();

		return retorno;
	}

	public boolean existeEstoqueAlmoxarifadoItemAFConsignado(Integer afnNumero, Integer numero) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EST");
		criteria.createAlias("EST." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("MAT." + ScoMaterial.Fields.SOLICITACAO_COMPRA.toString(), "SC");
		criteria.createAlias("SC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FASE");

		criteria.add(Restrictions.eqProperty("EST." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), "MAT."
				+ ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()));
		criteria.add(Restrictions.eqProperty("EST." + SceEstoqueAlmoxarifado.Fields.NRO_SOLICITACAO_COMPRA.toString(), "SC."
				+ ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq("EST." + SceEstoqueAlmoxarifado.Fields.IND_CONSIGNADO.toString(), true));
		criteria.add(Restrictions.eq("FASE." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("FASE." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), numero));

		return this.executeCriteriaExists(criteria);
	}

	public Integer obterSaldosEstoqueAlmoxarifadoItemAFConsignado(Integer afnNumero, Integer numero) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EST");
		criteria.createAlias("EST." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("MAT." + ScoMaterial.Fields.SOLICITACAO_COMPRA.toString(), "SC");
		criteria.createAlias("SC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FASE");
		criteria.createAlias("FASE." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "ITEM_AF");
		criteria.createAlias("ITEM_AF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AF");

		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()));
		p.add(Projections.sum(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString()));

		criteria.setProjection(p);

		criteria.add(Restrictions.eqProperty("EST." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), "MAT."
				+ ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()));
		criteria.add(Restrictions.eqProperty("EST." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), "AF."
				+ ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString()));
		criteria.add(Restrictions.eq("EST." + SceEstoqueAlmoxarifado.Fields.IND_CONSIGNADO.toString(), true));
		criteria.add(Restrictions.eq("FASE." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("FASE." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), numero));

		List<Object[]> listaResultado = executeCriteria(criteria);
		Integer qtdeDisponivel = 0;
		Integer qtdeBloqueada = 0;

		if (listaResultado != null) {
			Object[] resultado = listaResultado.get(0);
			qtdeDisponivel = (Integer) resultado[0];
			qtdeBloqueada = (Integer) resultado[1];

		}
		return qtdeDisponivel + qtdeBloqueada;

	}

	public List<GeraSolicCompraEstoqueVO> obterMateriaisGeracaoSc(SceAlmoxarifado almoxarifado, SceAlmoxarifado almoxCentral,
			Integer fornecedorPadraoId) {
		return obterMateriaisGeracaoSc(almoxarifado != null ? almoxarifado.getSeq() : null, almoxCentral.getSeq(), fornecedorPadraoId);
	}

	public List<GeraSolicCompraEstoqueVO> obterMateriaisGeracaoSc(Short almoxarifado, Short almoxCentral, Integer fornecedorPadraoId) {
		MateriaisGeracaoScQueryBuilder qb = materiaisGeracaoScQueryBuilder.get();
		qb.setAlmoxarifadoId(almoxarifado);
		qb.setAlmoxCentralId(almoxCentral);
		qb.setFornecPadraoId(fornecedorPadraoId);
		qb.build();
		return qb.getResultList();
	}

	public Boolean verificarExisteReforcoSolicitacaoCompras(Integer slcNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");

		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.SOLICITACAO_COMPRA.toString(), "SLC");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FS");

		criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), slcNumero));
		criteria.add(Restrictions.eq("FS." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.isNotNull("FS." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		criteria.add(Restrictions.isNotNull("SLC." + ScoSolicitacaoDeCompra.Fields.QTDE_REFORCO.toString()));
		criteria.add(Restrictions.gt("SLC." + ScoSolicitacaoDeCompra.Fields.QTDE_REFORCO.toString(), Long.valueOf(0)));

		return executeCriteriaCount(criteria) > 0;
	}

	public EstoqueMaterialVO obterEstoqueMaterial(Integer codigoMaterial, Date dataCompetencia, Integer numeroFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");
		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM");
		criteria.createCriteria(SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT");
		criteria.createCriteria("MAT." + ScoMaterial.Fields.ESTOQUE_GERAL.toString(), "EGR");

		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));

		criteria.add(Restrictions.eqProperty("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), "MAT."
				+ ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));
		criteria.add(Restrictions.eqProperty("EGR." + SceEstoqueGeral.Fields.MAT_CODIGO.toString(), "EAL."
				+ SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString()));
		criteria.add(Restrictions.eqProperty("EGR." + SceEstoqueGeral.Fields.FRN_NUMERO.toString(), "EAL."
				+ SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()));
		criteria.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), dataCompetencia));

		ProjectionList projectionsList = Projections
				.projectionList()
				.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QTDE_ESTQ_MAX.toString()),
						EstoqueMaterialVO.Fields.QUANTIDADE_MAXIMA.toString())
				.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()),
						EstoqueMaterialVO.Fields.QUANTIDADE_DISPONIVEL.toString())
				.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString()),
						EstoqueMaterialVO.Fields.QUANTIDADE_BLOQUEADA.toString())
				.add(Projections.property("ALM." + SceAlmoxarifado.Fields.SEQ.toString()),
						EstoqueMaterialVO.Fields.ALMOXARIFADO_SEQ.toString())
				.add(Projections.property("ALM." + SceAlmoxarifado.Fields.DESCRICAO.toString()),
						EstoqueMaterialVO.Fields.ALMOXARIFADO_DESC.toString())
				.add(Projections.property("ALM." + SceAlmoxarifado.Fields.DESCRICAO.toString()),
						EstoqueMaterialVO.Fields.ALMOXARIFADO_DESC.toString())
				.add(Projections.property("EGR." + SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString()),
						EstoqueMaterialVO.Fields.CLASSIFICACAO_ABC.toString())
				.add(Projections.property("EGR." + SceEstoqueGeral.Fields.SUBCLASSIFICACAO_ABC.toString()),
						EstoqueMaterialVO.Fields.SUB_CLASSIFIFCACAO_ABC.toString());
		criteria.setProjection(projectionsList);
		criteria.setResultTransformer(Transformers.aliasToBean(EstoqueMaterialVO.class));
		return (EstoqueMaterialVO) executeCriteriaUniqueResult(criteria);
	}

	public Integer buscarEstoqueAlmoxarifadoPorAutFornNumeroItlNumeroFornPadrao(Integer afNumero, Short itlNumero, Integer fornecedorPadrao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");
		criteria.createCriteria("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.SOLICITACAO_COMPRA.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);

		DetachedCriteria scoFaseSolicitacao2Criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC2");
		scoFaseSolicitacao2Criteria.add(Restrictions.eqProperty("FSC2." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), "FSC."
				+ ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
		scoFaseSolicitacao2Criteria.add(Restrictions.eq("FSC2." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString(), itlNumero));
		scoFaseSolicitacao2Criteria.setProjection(Projections.projectionList().add(
				Projections.property("FSC2." + ScoFaseSolicitacao.Fields.ITL_NUMERO.toString())));
		criteria.add(Subqueries.exists(scoFaseSolicitacao2Criteria));

		DetachedCriteria scoAutorizacaoFornCriteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN");
		scoAutorizacaoFornCriteria.add(Restrictions.eqProperty("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), "AFN."
				+ ScoAutorizacaoForn.Fields.NUMERO.toString()));
		scoAutorizacaoFornCriteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), afNumero));
		scoAutorizacaoFornCriteria.setProjection(Projections.projectionList().add(
				Projections.property("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString())));
		criteria.add(Subqueries.exists(scoAutorizacaoFornCriteria));

		criteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), fornecedorPadrao));

		criteria.setProjection(Projections.projectionList().add(Projections.max(("EAL." + SceEstoqueAlmoxarifado.Fields.SEQ.toString()))));

		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria montarCriteriaSeqPorMaterialFornecedor(Integer matCodigo, Integer numeroFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");

		if (matCodigo != null) {
			criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT");
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), matCodigo));
		}

		if (numeroFornecedor != null) {
			criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN");
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedor));
		}

		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "ALM_EAL");
		criteria.createAlias("MAT." + ScoMaterial.Fields.ALMOXARIFADO.toString(), "ALM_MAT");
		criteria.add(Restrictions.eqProperty("ALM_EAL." + SceAlmoxarifado.Fields.SEQ.toString(),
				"ALM_MAT." + SceAlmoxarifado.Fields.SEQ.toString()));

		return criteria;
	}

	public SceEstoqueAlmoxarifado buscarPorMaterialAlmoxarifadoFornecedor(Integer matCodigo, Integer numeroFornecedor) {

		DetachedCriteria criteria = this.montarCriteriaSeqPorMaterialFornecedor(matCodigo, numeroFornecedor);

		DetachedCriteria subCriteria = this.montarCriteriaSeqPorMaterialFornecedor(matCodigo, numeroFornecedor);
		subCriteria.setProjection(Projections.max(("EAL." + SceEstoqueAlmoxarifado.Fields.SEQ.toString())));

		criteria.add(Subqueries.propertyIn("EAL." + SceEstoqueAlmoxarifado.Fields.SEQ.toString(), subCriteria));

		List<SceEstoqueAlmoxarifado> result = super.executeCriteria(criteria, 0, 1, null, true);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}

		return null;
	}
	
	/**
	 * obtém lista de SceEstoqueAlmoxarifadoRM
	 * @ORADB RMSP_GERA_RM_PRCS - C3
	 * @param pedSeqProcEspRm, matCodigoProcEspRm
	 * @return
	 */
	public List<SceEstoqueAlmoxarifado> buscarsceEstoqueAlmoxarifadoPorPedSeqeMatCodigo(Short pedSeqProcEspRm, Integer matCodigoProcEspRm)throws ApplicationBusinessException{
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");
		
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "AML");
		criteria.createAlias("MAT." + ScoMaterial.Fields.MPM_PROCED_ESPECIAL_RM.toString(), "PER");
		
		criteria.add(Restrictions.eqProperty("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), "PER." + MpmProcedEspecialRm.Fields.MAT_CODIGO.toString()));
		
		criteria.add(Restrictions.eq("AML." + SceAlmoxarifado.Fields.IND_CENTRAL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("PER." + MpmProcedEspecialRm.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), 
													parametroFacade.buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO).getVlrNumerico().intValue()));
		
		
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.eq("PER." + MpmProcedEspecialRm.Fields.PED_SEQ.toString(), pedSeqProcEspRm), Restrictions.isNotNull("PER." + MpmProcedEspecialRm.Fields.PED_SEQ.toString())),
				                     Restrictions.and(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), matCodigoProcEspRm), Restrictions.isNotNull("MAT." + ScoMaterial.Fields.CODIGO.toString()))));
		
		
		
		return executeCriteria(criteria);
	}
}