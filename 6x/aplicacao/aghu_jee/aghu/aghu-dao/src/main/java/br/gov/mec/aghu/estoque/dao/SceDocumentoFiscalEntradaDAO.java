package br.gov.mec.aghu.estoque.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceItemNotaRecebimentoHist;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.SceNotaRecebimentoHist;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class SceDocumentoFiscalEntradaDAO extends BaseDao<SceDocumentoFiscalEntrada> {

	public List<ScoUltimasComprasMaterialVO> pesquisarUltimasComprasMateriasHistorico(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, String modl, Integer matCodigo) {
		DetachedCriteria criteria = createUltimasComprasMateriasHistorico(modl, matCodigo);
		criteria.addOrder(Order.desc("NRS." + SceNotaRecebimento.Fields.NUMERO_NR.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarUltimasComprasMateriasHistoricoCount(String modl, Integer matCodigo) {
		DetachedCriteria criteria = createUltimasComprasMateriasHistorico(modl, matCodigo);
		return this.executeCriteriaCount(criteria);
	}

	public DetachedCriteria createUltimasComprasMateriasHistorico(String modl, Integer matCodigo) {
		DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
		subCriteria.createAlias("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC");
		if (modl != null) {
			subCriteria.add(Restrictions.eq("LCT." + ScoLicitacao.Fields.MLC_CODIGO.toString(), modl));
		}
		subCriteria.setProjection(Projections.property("LCT." + ScoLicitacao.Fields.NUMERO.toString()));

		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoFiscalEntrada.class, "DFE");
		criteria.createAlias("DFE." + SceDocumentoFiscalEntrada.Fields.NOTA_RECEBIMENTO_HIST.toString(), "NRS");
		criteria.createAlias("NRS." + SceNotaRecebimentoHist.Fields.ITEM_NOTA_RECEBIMENTO.toString(), "INR");
		criteria.createAlias("INR." + SceItemNotaRecebimentoHist.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		subCriteria.add(Property.forName("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()).eqProperty(
				"LCT." + ScoLicitacao.Fields.NUMERO.toString()));
		criteria.add(Subqueries.exists(subCriteria));

		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), "CDP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CDP." + ScoCondicaoPagamentoPropos.Fields.FORMA_PAGAMENTO.toString(), "FPG", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.NOME_COMERCIAL.toString(), "NC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "MCM", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("INR." + SceItemNotaRecebimento.Fields.MAT_CODIGO.toString(), matCodigo));
		criteria.setProjection(Projections.distinct(Projections
				.projectionList()
				.add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()),
						ScoUltimasComprasMaterialVO.Fields.SOLICITACAO.toString())
				.

				add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()),
						ScoUltimasComprasMaterialVO.Fields.NRO_AF.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()),
						ScoUltimasComprasMaterialVO.Fields.CP.toString())
				.add(Projections.property("NRS." + SceNotaRecebimento.Fields.NUMERO_NR.toString()),
						ScoUltimasComprasMaterialVO.Fields.NUMERO_NR.toString())
				.add(Projections.property("NRS." + SceNotaRecebimento.Fields.DATA_GERACAO.toString()),
						ScoUltimasComprasMaterialVO.Fields.DATA_NR.toString())
				.add(Projections.property("DFE." + SceDocumentoFiscalEntrada.Fields.NUMERO.toString()),
						ScoUltimasComprasMaterialVO.Fields.NOTA_FISCAL.toString())
				.add(Projections.property("FPG." + ScoFormaPagamento.Fields.DESCRICAO.toString()),
						ScoUltimasComprasMaterialVO.Fields.FORMA_PGTO.toString())
				.add(Projections.property("INR." + SceItemNotaRecebimento.Fields.QUANTIDADE.toString()),
						ScoUltimasComprasMaterialVO.Fields.QTDE.toString())
				.add(Projections.property("INR." + SceItemNotaRecebimento.Fields.VALOR.toString()),
						ScoUltimasComprasMaterialVO.Fields.VALOR.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString()),
						ScoUltimasComprasMaterialVO.Fields.FORNECEDOR_NRO.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()),
						ScoUltimasComprasMaterialVO.Fields.RAZAO_SOCIAL.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC.toString()), ScoUltimasComprasMaterialVO.Fields.CNPJ.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF.toString()), ScoUltimasComprasMaterialVO.Fields.CPF.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.DDI.toString()), ScoUltimasComprasMaterialVO.Fields.DDI.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.DDD.toString()), ScoUltimasComprasMaterialVO.Fields.DDD.toString())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.FONE.toString()),
						ScoUltimasComprasMaterialVO.Fields.FONE.toString())
				.add(Projections.property("MCM." + ScoMarcaComercial.Fields.DESCRICAO.toString()),
						ScoUltimasComprasMaterialVO.Fields.MARCA_COMERCIAL.toString())
				.add(Projections.property("NC." + ScoNomeComercial.Fields.NOME.toString()),
						ScoUltimasComprasMaterialVO.Fields.NOME_COMERCIAL.toString())));

		criteria.setResultTransformer(Transformers.aliasToBean(ScoUltimasComprasMaterialVO.class));
		return criteria;

	}

	private static final long serialVersionUID = 5162368522294225507L;

	/**
	 * Obtem documento fiscal de entrada por seq
	 * 
	 * @param seq
	 * @return
	 */
	public SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoFiscalEntrada.class);
		criteria.createAlias(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceDocumentoFiscalEntrada.Fields.FORNECEDOR_EVENTUAL.toString(), "FRE", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.SEQ.toString(), seq));
		return (SceDocumentoFiscalEntrada) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem documento fiscal de entrada por seq e fornecedor
	 * 
	 * @param seq
	 * @param fornecedor
	 * @return
	 */
	public SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorSeqFornecedor(Integer seq, Integer numeroFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoFiscalEntrada.class);
		criteria.createAlias(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
		criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedor));
		return (SceDocumentoFiscalEntrada) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem documento fiscal de entrada através do número, série e fornecedor
	 * 
	 * @param numero
	 * @param serie
	 * @param fornecedor
	 * @return
	 */
	public SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorNumeroSerieFornecedor(Long numero, String serie, Integer numeroFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoFiscalEntrada.class);
		criteria.createAlias(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
		criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.SERIE.toString(), StringUtils.trim(serie)));
		criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.TIPO_DOCUMENTO_FISCAL_ENTRADA.toString(),
				DominioTipoDocumentoFiscalEntrada.NFS));
		criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedor));
		return (SceDocumentoFiscalEntrada) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem documento fiscal de entrada através do número esérie
	 * 
	 * @param numero
	 * @param serie
	 * @param fornecedor
	 * @return
	 */
	public SceDocumentoFiscalEntrada obterDocumentoFiscalEntradaPorNumeroSerie(Long numero, String serie) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoFiscalEntrada.class);
		criteria.createAlias(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
		criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.SERIE.toString(), StringUtils.trim(serie)));
		criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.TIPO_DOCUMENTO_FISCAL_ENTRADA.toString(),
				DominioTipoDocumentoFiscalEntrada.NFS));
		return (SceDocumentoFiscalEntrada) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem a criteria necessária para a consulta da estória
	 * 
	 * @param material
	 * @return
	 */
	public DetachedCriteria getCriteriaPesquisarDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoFiscalEntrada.class);

		if (documentoFiscalEntrada.getSeq() != null) {
			criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.SEQ.toString(), documentoFiscalEntrada.getSeq()));
		}

		if (documentoFiscalEntrada.getNumero() != null) {
			criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), documentoFiscalEntrada.getNumero()));
		}

		if (StringUtils.isNotBlank(documentoFiscalEntrada.getSerie())) {
			criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.SERIE.toString(), documentoFiscalEntrada.getSerie().trim()));
		}

		if (documentoFiscalEntrada.getTipo() != null) {
			criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.TIPO.toString(), documentoFiscalEntrada.getTipo()));
		}

		if (documentoFiscalEntrada.getTipoDocumentoFiscalEntrada() != null) {
			criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.TIPO_DOCUMENTO_FISCAL_ENTRADA.toString(),
					documentoFiscalEntrada.getTipoDocumentoFiscalEntrada()));
		}

		SimpleDateFormat formatador = null;

		if (documentoFiscalEntrada.getDtGeracao() != null) {
			formatador = new SimpleDateFormat("dd/MM/yyyy");
			final String sqlRestrictionToChar = "TO_CHAR(DT_GERACAO,'dd/MM/yyyy') = ?";
			criteria.add(Restrictions.sqlRestriction(sqlRestrictionToChar, formatador.format(documentoFiscalEntrada.getDtGeracao()),
					StringType.INSTANCE));
		}

		if (documentoFiscalEntrada.getDtAutorizada() != null) {
			formatador = new SimpleDateFormat("dd/MM/yyyy");
			final String sqlRestrictionToChar = "TO_CHAR(DT_AUTORIZADA,'dd/MM/yyyy') = ?";
			criteria.add(Restrictions.sqlRestriction(sqlRestrictionToChar, formatador.format(documentoFiscalEntrada.getDtAutorizada()),
					StringType.INSTANCE));
		}

		if (documentoFiscalEntrada.getFornecedor() != null) {
			criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), documentoFiscalEntrada.getFornecedor()));
		}

		return criteria;
	}

	public Long pesquisarDocumentoFiscalEntradaCount(SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		DetachedCriteria criteria = this.getCriteriaPesquisarDocumentoFiscalEntrada(documentoFiscalEntrada);
		return this.executeCriteriaCount(criteria);
	}

	public List<SceDocumentoFiscalEntrada> pesquisarDocumentoFiscalEntrada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		DetachedCriteria criteria = this.getCriteriaPesquisarDocumentoFiscalEntrada(documentoFiscalEntrada);
		criteria.addOrder(Order.desc(SceDocumentoFiscalEntrada.Fields.DT_GERACAO.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, null, false);
	}

	public List<SceDocumentoFiscalEntrada> pesquisarDocumentoFiscaisPorNumeroSerieFornecedorOuFornecedorEventual(Long numero, String serie,
			ScoFornecedor fornecedor, SceFornecedorEventual fornecedorEventual) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoFiscalEntrada.class);

		criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.SERIE.toString(), serie));

		criteria.add(Restrictions.or(Restrictions.and(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), fornecedor),
				Restrictions.isNull(SceDocumentoFiscalEntrada.Fields.FORNECEDOR_EVENTUAL.toString())), Restrictions.and(
				Restrictions.eq(SceDocumentoFiscalEntrada.Fields.FORNECEDOR_EVENTUAL.toString(), fornecedorEventual),
				Restrictions.isNull(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString()))));
		return this.executeCriteria(criteria);
	}

	public final ApplicationBusinessException obterNegocioExceptionDependencias(Object elemento, Class classe, Enum campo,
			BusinessExceptionCode negocioExceptionCode) throws BaseException {

		CoreUtil.validaParametrosObrigatorios(elemento, classe, campo, negocioExceptionCode);

		DetachedCriteria criteria = DetachedCriteria.forClass(classe);
		criteria.add(Restrictions.eq(campo.toString(), elemento));

		if (executeCriteriaCount(criteria) > 0) {
			return new ApplicationBusinessException(negocioExceptionCode);
		}

		return null;
	}

	public List<SceDocumentoFiscalEntrada> pesquisarNotafiscalEntradaNumeroOuFornecedor(Object param, ScoFornecedor fornecedor) {
		// boolean aliasFornecedor = false;

		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoFiscalEntrada.class);

		criteria.createAlias(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(),
				SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), JoinType.LEFT_OUTER_JOIN);

		if (fornecedor != null) {
			criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), fornecedor));
		}

		String strPesquisa = (String) param;
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), Long.valueOf(strPesquisa)));
			} else {
				criteria.createAlias(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");
				criteria.add(Restrictions.ilike("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), strPesquisa, MatchMode.ANYWHERE));
				// aliasFornecedor = true;
			}
		}

		if (fornecedor != null) {
			if (fornecedor.getCnpjCpf() != null) {
				List<Integer> listaFornecedores = buscarListaMatrizFilialForn(fornecedor);
				// if (!aliasFornecedor){
				// criteria.createAlias(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(),
				// "FRN");
				// }
				criteria.add(Restrictions.in(
						SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString() + "." + ScoFornecedor.Fields.NUMERO.toString(),
						listaFornecedores));
			} else {
				criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), fornecedor));
			}
		}

		criteria.addOrder(Order.desc(SceDocumentoFiscalEntrada.Fields.NUMERO.toString()));
		criteria.addOrder(Order.desc(SceDocumentoFiscalEntrada.Fields.DT_GERACAO.toString()));

		return executeCriteria(criteria, 0, 200, null, true);
	}

	public List<Integer> buscarListaMatrizFilialForn(ScoFornecedor fornecedor) {
		List<Integer> listaNumForn = null;
		if (fornecedor.getCnpjCpf() != null) {
			String cnpjRaiz = fornecedor.getCnpjCpf().toString().substring(0, 7);
			StringBuilder hql = new StringBuilder("select numero ");
			hql.append(" from ").append(ScoFornecedor.class.getName());
			hql.append(" where ");
			hql.append(" substring(cast(").append(ScoFornecedor.Fields.CGC.toString()).append(" as string), 0, 8)");
			hql.append(" = :cnpjRaiz ");
			org.hibernate.Query query = createHibernateQuery(hql.toString());
			query.setParameter("cnpjRaiz", cnpjRaiz);
			query.setReadOnly(true);
			listaNumForn = query.list();
		}
		return listaNumForn;
	}

	public List<SceDocumentoFiscalEntrada> pesquisarNFEntradaGeracaoNumeroOuFornecedor(Object param, ScoFornecedor fornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDocumentoFiscalEntrada.class);
		criteria.createAlias(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), "FRN");

		Calendar calDtUltimaGeracaoPassada = Calendar.getInstance();
		calDtUltimaGeracaoPassada.setTime(new Date());
		calDtUltimaGeracaoPassada.add(Calendar.DAY_OF_MONTH, -100);
		Date dtUltimaGeracaoPassada = calDtUltimaGeracaoPassada.getTime();

		criteria.add(Restrictions.gt(SceDocumentoFiscalEntrada.Fields.DT_GERACAO.toString(), dtUltimaGeracaoPassada));

		String strPesquisa = (String) param;
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.NUMERO.toString(), Long.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}

		if (fornecedor != null && fornecedor.getCnpjCpf().toString().length() >= 8) {
			if (fornecedor.getCnpjCpf() != null) {
				List<Integer> listaFornecedores = buscarListaMatrizFilialForn(fornecedor);
				if (!listaFornecedores.isEmpty()) {
					criteria.add(Restrictions.in("FRN." + ScoFornecedor.Fields.NUMERO.toString(), listaFornecedores));
				} else {
					criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), fornecedor));
				}
			} else {
				criteria.add(Restrictions.eq(SceDocumentoFiscalEntrada.Fields.FORNECEDOR.toString(), fornecedor));
			}
		}

		criteria.addOrder(Order.asc(SceDocumentoFiscalEntrada.Fields.NUMERO.toString()));
		criteria.addOrder(Order.asc(SceDocumentoFiscalEntrada.Fields.TIPO_DOCUMENTO_FISCAL_ENTRADA.toString()));
		criteria.addOrder(Order.asc(SceDocumentoFiscalEntrada.Fields.DT_EMISSAO.toString()));
		criteria.addOrder(Order.asc(SceDocumentoFiscalEntrada.Fields.DT_ENTRADA.toString()));
		criteria.addOrder(Order.asc("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));

		return executeCriteria(criteria, 0, 200, null, true);
	}
	
}