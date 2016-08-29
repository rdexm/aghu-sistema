package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoItemFornecedorVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroPesquisaAssinarAFVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaAutorizacaoFornecimentoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ScoItemAFPVO;
import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoEntregaVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraDataVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraVO;
import br.gov.mec.aghu.compras.vo.DadosPrimeiraAFVO;
import br.gov.mec.aghu.compras.vo.FiltroParcelasAFPendEntVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfFiltroVO;
import br.gov.mec.aghu.compras.vo.PesquisarPlanjProgrEntregaItensAfVO;
import br.gov.mec.aghu.dominio.DominioAndamentoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoFiltroAutorizacaoFornecimento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoAfEmpenho;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoRefCodes;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.validation.CNPJValidator;

public class ScoAutorizacaoFornDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoAutorizacaoForn> {
	private static final long serialVersionUID = 7560943552026609157L;
	private static final String DELIMITADOR_NUMEROAUTORIZACAO_FORNECIMENTO = "/";
	@Inject @New(AFsMateriaisParaEmailAtrasoQueryBuilder.class) private Instance<AFsMateriaisParaEmailAtrasoQueryBuilder> afsMateriaisParaEmailAtrasoQueryBuilder;
	@Inject @New(ConsultaItensMatAFProgramacaoEntregaQueryBuilder.class) private Instance<ConsultaItensMatAFProgramacaoEntregaQueryBuilder> consultaItensMatAFProgramacaoEntregaQueryBuilder;
	@Inject @New(ConsultaItensSerAFProgramacaoEntregaQueryBuilder.class) private Instance<ConsultaItensSerAFProgramacaoEntregaQueryBuilder> consultaItensSerAFProgramacaoEntregaQueryBuilder;
	public List<ScoAutorizacaoForn> listarAfByFornAndLic(ScoLicitacao licitacao, ScoFornecedor fornecedor) {
		StringBuilder hql = new StringBuilder(500);
		hql.append(" select distinct afn from ScoAutorizacaoForn afn ")
				.append(" left outer join afn." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString() + " propostaForn ")
				.append(" left outer join propostaForn." + ScoPropostaFornecedor.Fields.ITEM.toString() + " itensproposta ")
				.append(" left outer join propostaForn." + ScoPropostaFornecedor.Fields.LICITACAO.toString() + " lic ")
				.append(" fetch all properties ").append(" where lic.numero = :NUMLIC")
				.append(" and itensproposta.indEscolhido = :BOOLEAN_SIM").append(" and afn.exclusao = :BOOLEAN_NAO")
				.append(" and not exists( from ScoAfContrato afco where afco.scoAutorizacoesForn.numero = afn.numero)");
		if (fornecedor != null && fornecedor.getNumero() != null) {
			hql.append(" and propostaForn.fornecedor.numero = :NUM_FORN");
		}
		Query q = createHibernateQuery(hql.toString());
		q.setParameter("NUMLIC", licitacao.getNumero());
		q.setParameter("BOOLEAN_SIM", Boolean.TRUE);
		q.setParameter("BOOLEAN_NAO", Boolean.FALSE);
		if (fornecedor != null && fornecedor.getNumero() != null) {
			q.setParameter("NUM_FORN", fornecedor.getNumero());
		}
		return q.list();
	}
	public ScoAutorizacaoForn obterAfByNumero(Integer numeroAf){
		return this.obterPorChavePrimaria(numeroAf);
	}
	public ScoAutorizacaoForn obterAfDetalhadaByNumero(Integer numeroAf){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class);
		criteria.createAlias(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROP."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FORN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROP."+ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LIC."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MOD_LIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), "CDP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CDP." + ScoCondicaoPagamentoPropos.Fields.FORMA_PAGAMENTO.toString(), "FPA", JoinType.LEFT_OUTER_JOIN);		
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.NUMERO.toString(), numeroAf));
		return (ScoAutorizacaoForn) this.executeCriteriaUniqueResult(criteria);
	}
	public Boolean existeAutorizacaoFornecimentoNotaImportacao(Integer numeroAutorizacaoFornecimento, Short codigoFormaPagamentoImportacao){
		CoreUtil.validaParametrosObrigatorios(numeroAutorizacaoFornecimento);
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class);
		criteria.createAlias(ScoAutorizacaoForn.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), "CDP");
		criteria.createAlias("CDP." + ScoCondicaoPagamentoPropos.Fields.FORMA_PAGAMENTO.toString(), "FPA");
		criteria.add(Restrictions.eq("FPA." + ScoFormaPagamento.Fields.CODIGO.toString(), codigoFormaPagamentoImportacao));
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.NUMERO.toString(), numeroAutorizacaoFornecimento));
		return executeCriteriaCount(criteria) > 0;
	}
	public List<ScoAutorizacaoForn> pesquisarAutorizacaoFornecimentoValidasInsercao(Integer numeroAutorizacaoFornecimento){
		CoreUtil.validaParametrosObrigatorios(numeroAutorizacaoFornecimento);
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class);
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.NUMERO.toString(), numeroAutorizacaoFornecimento));
		DominioSituacaoAutorizacaoFornecimento[] situacoesInvalidas = {DominioSituacaoAutorizacaoFornecimento.EX,DominioSituacaoAutorizacaoFornecimento.EP, DominioSituacaoAutorizacaoFornecimento.EF, DominioSituacaoAutorizacaoFornecimento.ES};
		Criterion in = Restrictions.in(ScoAutorizacaoForn.Fields.SITUACAO.toString(), situacoesInvalidas);  
		criteria.add(Restrictions.not(in));
		return executeCriteria(criteria);
	}
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<ScoItemAutorizacaoForn> pesquisarAutorizacoesFornecimentoPorSeqDescricao(Object param){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		List<ScoItemAutorizacaoForn> retornoQuery = new ArrayList<ScoItemAutorizacaoForn>();
		List<ScoItemAutorizacaoForn> retorno = new ArrayList<ScoItemAutorizacaoForn>();
		List<Integer> retornoSubQuery = new ArrayList<Integer>();
		// "Aliases"
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFO", JoinType.INNER_JOIN);
		criteria.createAlias("PFO." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		// Acrescenta a consulta situaçães válidas aos itens da autorização de fornecimento
		DominioSituacaoAutorizacaoFornecimento[] situacoesInvalidas = {DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA};
		criteria.add(Restrictions.in("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), situacoesInvalidas));
		String strPesquisa = (String) param;
		if(StringUtils.isNotBlank(strPesquisa)){
			strPesquisa = strPesquisa.trim();
			// Verifica se a pesquisa utiliza o número de uma autorização de fornecimento
			if(StringUtils.countMatches(strPesquisa, DELIMITADOR_NUMEROAUTORIZACAO_FORNECIMENTO) == 1){
				final String strNumeroAutorizacaoFornecimento = strPesquisa.substring(0, strPesquisa.indexOf(DELIMITADOR_NUMEROAUTORIZACAO_FORNECIMENTO));
				final String strComplementoAutorizacaoFornecimento = strPesquisa.substring(strPesquisa.indexOf(DELIMITADOR_NUMEROAUTORIZACAO_FORNECIMENTO) + 1, strPesquisa.length());
				if(strNumeroAutorizacaoFornecimento.length() <= 7 && strComplementoAutorizacaoFornecimento.length() <=3){
					try {
						final Integer numeroAutorizacaoFornecimento = Integer.parseInt(strNumeroAutorizacaoFornecimento);
						final Short complementoAutorizacaoFornecimento = Short.parseShort(strComplementoAutorizacaoFornecimento);
						// Considera número da autorização de fornecimento do item
						if(numeroAutorizacaoFornecimento != null){
							criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), numeroAutorizacaoFornecimento));
						}
						// Considera número do complemento da autorização de fornecimento
						if(complementoAutorizacaoFornecimento != null){
							criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), complementoAutorizacaoFornecimento));
						}
					} catch (NumberFormatException e) {
						// Retorna uma lista em branco na ocorrência de NumberFormatException, ou seja, nenhum resultado valido
						return new LinkedList<ScoItemAutorizacaoForn>();
					}
				}
			} else{ 
				// Verifica se a pesquisa utiliza um CGC/CNPJ
				if (CoreUtil.isNumeroLong(strPesquisa)) {
					CNPJValidator cnpjValidator  = new CNPJValidator();
					// Acrescenta a consulta o critério do CGC/CNPJ do fornecedor
					if(cnpjValidator.validate(strPesquisa)){
						criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.CGC.toString(), Long.parseLong(strPesquisa)));
					}
				} else {
					// Acrescenta a consulta o critério da razão social do fornecedor
					criteria.add(Restrictions.ilike("FRN." + ScoFornecedor.Fields.NOME_FANTASIA.toString(), StringUtils.trim(strPesquisa), MatchMode.ANYWHERE));
				}
			}
		}
		criteria.addOrder(Order.asc(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
		criteria.addOrder(Order.asc(ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
		retornoQuery = executeCriteria(criteria, 0, 100, null, true);
		List<Integer> listaAfs = new ArrayList<Integer>();
		if (!retornoQuery.isEmpty()) {
			for (ScoItemAutorizacaoForn item : retornoQuery) {
				listaAfs.add(item.getId().getAfnNumero());
			}
			// Subconsulta (EXISTS) de Fases de Solicitação relacionadas à Solicitações de Compras e através da Autorização de Fornecimento
			DetachedCriteria subQueryCompras = DetachedCriteria.forClass(ScoFaseSolicitacao.class);
			subQueryCompras.setProjection(Projections.distinct(Projections.property(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString())));
			subQueryCompras.createAlias(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
			subQueryCompras.createAlias(ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.LEFT_OUTER_JOIN);
			subQueryCompras.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQueryCompras.add(Restrictions.in(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), listaAfs));
			retornoSubQuery = executeCriteria(subQueryCompras);
			for (ScoItemAutorizacaoForn item : retornoQuery) {
				if (retornoSubQuery.contains(item.getId().getAfnNumero())) {
					retorno.add(item);
				}
			}		
		}
		return retorno;
	}
	/**
	 * Visualizar AF’s Geradas / Não Efetivadas do módulo de Compras. 
	 * @return
	 */
	public List<ScoAutorizacaoForn> visualizarAFsGeradasNaoEfetivadas(Integer numeroLicitacao, Short nroComplemento, Date dtGeracao, Date dtPrevEntrega, DominioModalidadeEmpenho modalidadeEmpenho, DominioSituacaoAutorizacaoFornecimento situacao, List<DominioSituacaoAutorizacaoFornecimento> rvLowValues, Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		DetachedCriteria criteria = criaCriteriaVisualizarAFsGeradasNaoEfetivadas(numeroLicitacao, nroComplemento, dtGeracao, dtPrevEntrega, modalidadeEmpenho, situacao, rvLowValues);
		criteria.addOrder(Property.forName("LCT." + ScoLicitacao.Fields.NUMERO.toString()).desc());
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	/**
	 * Visualizar AF’s Geradas / Não Efetivadas do módulo de Compras. 
	 * @return
	 */
	public Long visualizarAFsGeradasNaoEfetivadasCount(Integer numeroLicitacao, Short nroComplemento, Date dtGeracao, Date dtPrevEntrega, DominioModalidadeEmpenho modalidadeEmpenho, DominioSituacaoAutorizacaoFornecimento situacao, List<DominioSituacaoAutorizacaoFornecimento> rvLowValues){
		DetachedCriteria criteria = criaCriteriaVisualizarAFsGeradasNaoEfetivadas(numeroLicitacao, nroComplemento, dtGeracao, dtPrevEntrega, modalidadeEmpenho, situacao, rvLowValues);
		return executeCriteriaCount(criteria);
	}
	/**
	 * Visualizar AF’s Geradas / Não Efetivadas do módulo de Compras. 
	 * @return
	 */
	public DetachedCriteria criaCriteriaVisualizarAFsGeradasNaoEfetivadas(Integer numeroLicitacao, Short nroComplemento, Date dtGeracao, Date dtPrevEntrega, DominioModalidadeEmpenho modalidadeEmpenho, DominioSituacaoAutorizacaoFornecimento situacao, List<DominioSituacaoAutorizacaoFornecimento> rvLowValues){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.CVF_CODIGO.toString(), "CVF", JoinType.INNER_JOIN);
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.NATUREZA_DESPESA.toString(), "NAT", JoinType.INNER_JOIN);
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.GRUPO_NATUREZA_DESPESA.toString(), "GND", JoinType.INNER_JOIN);
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), "CDP", JoinType.INNER_JOIN);
		criteria.createAlias("CDP." + ScoCondicaoPagamentoPropos.Fields.FORMA_PAGAMENTO.toString(), "FPG", JoinType.INNER_JOIN);
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR", JoinType.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LCT", JoinType.INNER_JOIN);
		criteria.createAlias("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC", JoinType.INNER_JOIN);
		//SCO_REF_CODES
		DetachedCriteria criteriaRefCodes = DetachedCriteria.forClass(ScoRefCodes.class, "RFC2");
		criteriaRefCodes.add(Restrictions.eqProperty(ScoRefCodes.Fields.RV_LOW_VALUE.toString(), "AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString()));
		criteriaRefCodes.setProjection(Projections.projectionList().add(Projections.property("RFC2." + ScoRefCodes.Fields.RV_LOW_VALUE.toString())));
		criteria.add(Subqueries.exists(criteriaRefCodes));
		if(rvLowValues != null){
			criteria.add(Restrictions.in("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), rvLowValues));
		}
		if(numeroLicitacao != null){
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString() + "." +ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), numeroLicitacao));
		}
		if(nroComplemento != null){
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), nroComplemento));
		}
		if(dtGeracao != null){// Criado data fim para que a consulta retorne o intervalo completo para data selecionada
			Date dataFim = DateUtil.adicionaDias(dtGeracao, 1);
			criteria.add(Restrictions.between("AFN." + ScoAutorizacaoForn.Fields.DT_GERACAO.toString(), dtGeracao, dataFim));
		}
		if(dtPrevEntrega != null){// Criado data fim para que a consulta retorne o intervalo completo para data selecionada
			Date dataFim = DateUtil.adicionaDias(dtPrevEntrega, 1);
			criteria.add(Restrictions.between("AFN." + ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString(), dtPrevEntrega, dataFim));
		}
		if(modalidadeEmpenho != null){
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), modalidadeEmpenho));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), situacao));
		}	
		return criteria;
	}
	/**
	 * Verifica se proposta está em AF.
	 * 
	 * @param proposta Proposta
	 * @return Flag
	 */
	public boolean isEmAf(ScoPropostaFornecedor proposta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class);
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(),proposta));
		return executeCriteriaCount(criteria) > 0;
	}
	/**
	 * Verifica se um item de proposta está em AF
	 * @param numeroProposta
	 * @param numeroFornecedor
	 * @return Boolean
	 */
	public Boolean verificarItemPropostaEmAf(Integer numeroProposta, Integer numeroFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AF");
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.or(Restrictions.or(Restrictions.or(Restrictions.eq(ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.EF), Restrictions.eq(ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.EP)), Restrictions.eq(ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE)), Restrictions.eq(ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), numeroProposta));
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), numeroFornecedor));
		return (executeCriteriaCount(criteria) > 0);
	}
	public List<ScoItemAFPVO> pesquisarAFsPorLicitacaoComplSeqAlteracao(Integer pacNumero, Short nroComplemento,int espEmpenho){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AF");
		criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.AF_EMPENHO.toString(), "AFE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "IAF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IPO." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ILI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ILI." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "FAS_SE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FAS_SE." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "FAS_SE_SE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "FAS_SC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FAS_SC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "FAS_SC_MAT", JoinType.LEFT_OUTER_JOIN);
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString()), ScoItemAFPVO.Fields.AUT_FORN.toString())
				.add(Projections.property("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()), ScoItemAFPVO.Fields.SCO_COMPRA.toString())
				.add(Projections.property("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString()), ScoItemAFPVO.Fields.SCO_SERVICO.toString())
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()), ScoItemAFPVO.Fields.NUMERO_ITEM.toString());
				criteria.setProjection(projection);
		criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), pacNumero));
		criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), nroComplemento));
		DetachedCriteria subQueryMaxSeqEmpenho = DetachedCriteria.forClass(ScoAfEmpenho.class, "AFO");
		subQueryMaxSeqEmpenho.setProjection(Projections.max("AFO."+ScoAfEmpenho.Fields.SEQ));  
		subQueryMaxSeqEmpenho.add(Restrictions.eqProperty("AFO."+ScoAfEmpenho.Fields.AUTORIZACAO_FORN_NUMERO.toString(), "AF."+ScoAutorizacaoForn.Fields.NUMERO.toString()));
		subQueryMaxSeqEmpenho.add(Restrictions.eq("AFO."+ScoAfEmpenho.Fields.ESPECIE.toString(), espEmpenho));
		DetachedCriteria subQueryNumEmpenho = DetachedCriteria.forClass(ScoAfEmpenho.class , "EMP");
		ProjectionList projectionListSubQueryNumEmpenhos = Projections.projectionList().add(Projections.property("EMP."+ScoAfEmpenho.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
		subQueryNumEmpenho.setProjection(projectionListSubQueryNumEmpenhos);
		subQueryNumEmpenho.add(Restrictions.eqProperty("EMP."+ScoAfEmpenho.Fields.AUTORIZACAO_FORN_NUMERO.toString(), "AF."+ScoAutorizacaoForn.Fields.NUMERO.toString()));
		subQueryNumEmpenho.add(Restrictions.eq("EMP."+ScoAfEmpenho.Fields.ESPECIE.toString(), espEmpenho));
		criteria.add(Restrictions.or(Subqueries.propertyIn("AFE."+ScoAfEmpenho.Fields.SEQ.toString(), subQueryMaxSeqEmpenho),Subqueries.propertyNotIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), subQueryNumEmpenho)));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoItemAFPVO.class));
		return executeCriteria(criteria);
	}

	private DetachedCriteria  obterCriteriaFasesPesquisaAutorizacaoFornecimento(PesquisaAutorizacaoFornecimentoVO filtro) {
		DetachedCriteria subQueryFases = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FS");
		subQueryFases.setProjection(Projections.property("FS." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		subQueryFases.createAlias("FS." + ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);			
		subQueryFases.add(Restrictions.eq(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQueryFases.add(Restrictions.eqProperty("FS."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), "AF."+ScoAutorizacaoForn.Fields.NUMERO.toString()));	
		if (filtro.getTipoFiltroAf() == DominioTipoFiltroAutorizacaoFornecimento.SC) {
			subQueryFases.createAlias("FS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
			subQueryFases.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
			if (filtro.getCodigoFiltroAf() != null) {
				subQueryFases.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), filtro.getCodigoFiltroAf()));					
			}
		}
		if (filtro.getTipoFiltroAf() == DominioTipoFiltroAutorizacaoFornecimento.MATERIAL) {
			subQueryFases.createAlias("FS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
			subQueryFases.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
			if (filtro.getCodigoFiltroAf() != null) {
				subQueryFases.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), filtro.getCodigoFiltroAf()));					
			}
		}
		if (filtro.getTipoFiltroAf() == DominioTipoFiltroAutorizacaoFornecimento.SS) {
			subQueryFases.createAlias("FS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.INNER_JOIN);
			subQueryFases.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SER", JoinType.INNER_JOIN);
			if (filtro.getCodigoFiltroAf() != null) {
				subQueryFases.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString(), filtro.getCodigoFiltroAf()));					
			}
		}
		if (filtro.getTipoFiltroAf() == DominioTipoFiltroAutorizacaoFornecimento.SERVICO) {
			subQueryFases.createAlias("FS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.INNER_JOIN);
			subQueryFases.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SER", JoinType.INNER_JOIN);
			if (filtro.getCodigoFiltroAf() != null) {
				subQueryFases.add(Restrictions.eq("SER." + ScoServico.Fields.CODIGO.toString(), filtro.getCodigoFiltroAf()));					
			}
		}
		return subQueryFases;
	}
	private DetachedCriteria  obterCriteriaAndamentoQuery1(PesquisaAutorizacaoFornecimentoVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, "AFJN1");
		criteria.setProjection(Projections.property("AFJN1."+ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN.toString()));
		criteria.add(Restrictions.eqProperty("AFJN1."+ScoAutorizacaoFornJn.Fields.NUMERO.toString(), "AF."+ScoAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("AFJN1."+ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), "AF."+ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString()));
		if (isOracle()) {
			criteria.add(Restrictions.or(Restrictions.isNull("AF."+ScoAutorizacaoForn.Fields.DT_ALTERACAO.toString()), Restrictions.sqlRestriction("TRUNC("+"afjn1_."+ScoAutorizacaoFornJn.Fields.DT_ALTERACAO.name()+") = TRUNC(this_."+ScoAutorizacaoForn.Fields.DT_ALTERACAO.name()+")")));
		} else {
			criteria.add(Restrictions.or(Restrictions.isNull("AF."+ScoAutorizacaoForn.Fields.DT_ALTERACAO.toString()), 			Restrictions.sqlRestriction("DATE("+"afjn1_."+ScoAutorizacaoFornJn.Fields.DT_ALTERACAO.name()+") = DATE(this_."+ScoAutorizacaoForn.Fields.DT_ALTERACAO.name()+")")));
		}
		return criteria;
	}
	private DetachedCriteria  obterCriteriaAndamentoQuery2(PesquisaAutorizacaoFornecimentoVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF2");
		criteria.createAlias("IAF2."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FS2");
		criteria.createAlias("IAF2."+ScoItemAutorizacaoForn.Fields.ITENS_AF_JN.toString(), "AIFJN");
		criteria.setProjection(Projections.property("IAF2."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("IAF2."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(),"AF."+ScoAutorizacaoForn.Fields.NUMERO.toString()));	
		DetachedCriteria subQueryMax = DetachedCriteria.forClass(ScoItemAutorizacaoFornJn.class, "IAFJN");
		subQueryMax.setProjection(Projections.max("IAFJN."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()));
		subQueryMax.add(Restrictions.eqProperty("IAFJN."+ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString(), "AIFJN."+ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString()));
		subQueryMax.add(Restrictions.eqProperty("IAFJN."+ScoItemAutorizacaoFornJn.Fields.NUMERO.toString(), "AIFJN."+ScoItemAutorizacaoFornJn.Fields.NUMERO.toString()));
		criteria.add(Subqueries.propertyIn("AIFJN."+ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), subQueryMax));
		criteria.add(Restrictions.or(
						Restrictions.and(Restrictions.neProperty("AIFJN."+ScoItemAutorizacaoFornJn.Fields.QTDE_SOLICITADA.toString(), "IAF2."+ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()), Restrictions.eq("FS2."+ScoFaseSolicitacao.Fields.TIPO.toString(), DominioTipoFaseSolicitacao.C)), 
						Restrictions.or(Restrictions.neProperty("AIFJN."+ScoItemAutorizacaoFornJn.Fields.FATOR_CONVERSAO.toString(), "IAF2."+ScoItemAutorizacaoForn.Fields.FATOR_CONVERSAO_FORM.toString()), Restrictions.neProperty("AIFJN."+ScoItemAutorizacaoFornJn.Fields.VALOR_UNITARIO.toString(), "IAF2."+ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString()))));
		return criteria;
	}
	private DetachedCriteria  obterCriteriaAndamentoQuery3(PesquisaAutorizacaoFornecimentoVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, "AFJN3");
		criteria.setProjection(Projections.property("AFJN3."+ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN.toString()));
		criteria.createAlias("AFJN3."+ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN.toString(), "AF3");
		criteria.add(Restrictions.eqProperty("AF3."+ScoAutorizacaoForn.Fields.NUMERO.toString(), "AF."+ScoAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("AFJN3."+ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), "AF3."+ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString()));
		criteria.add(Restrictions.neProperty("AFJN3."+ScoAutorizacaoFornJn.Fields.IND_SITUACAO.toString(), "AF3."+ScoAutorizacaoForn.Fields.SITUACAO.toString()));
		criteria.add(Restrictions.or(Restrictions.eq("AFJN3."+ScoAutorizacaoFornJn.Fields.IND_SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.EX), Restrictions.eq("AF3."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.EX)));	
		return criteria;
	}
	private DetachedCriteria  obterCriteriaAndamentoQuery4(PesquisaAutorizacaoFornecimentoVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, "AFJN4");
		criteria.setProjection(Projections.property("AFJN4."+ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN.toString()));
		criteria.createAlias("AFJN4."+ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN.toString(), "AF4");
		criteria.add(Restrictions.eqProperty("AF4."+ScoAutorizacaoForn.Fields.NUMERO.toString(), "AF."+ScoAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("AFJN4."+ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), "AF4."+ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString()));
		if (filtro.getAndamentoAf() == DominioAndamentoAutorizacaoFornecimento.LIBERAR_AF_ASSINATURA) {
			criteria.add(Restrictions.eq("AFJN4."+ScoAutorizacaoFornJn.Fields.IND_APROVADA.toString(), DominioAprovadaAutorizacaoForn.N));
		} else if (filtro.getAndamentoAf() == DominioAndamentoAutorizacaoFornecimento.AF_LIBERADA_ASSINATURA) {
			criteria.add(Restrictions.eq("AFJN4."+ScoAutorizacaoFornJn.Fields.IND_APROVADA.toString(), DominioAprovadaAutorizacaoForn.C));
		} else if (filtro.getAndamentoAf() == DominioAndamentoAutorizacaoFornecimento.VERSAO_JA_ASSINADA) {
			criteria.add(Restrictions.eq("AFJN4."+ScoAutorizacaoFornJn.Fields.IND_APROVADA.toString(), DominioAprovadaAutorizacaoForn.A));
		} else if (filtro.getAndamentoAf() == DominioAndamentoAutorizacaoFornecimento.VERSAO_EMPENHADA) {
			criteria.add(Restrictions.eq("AFJN4."+ScoAutorizacaoFornJn.Fields.IND_APROVADA.toString(), DominioAprovadaAutorizacaoForn.E));
		}	
		return criteria;
	}
	private LogicalExpression obterFiltroAndamento(PesquisaAutorizacaoFornecimentoVO filtro) {
		LogicalExpression exp = null;
		if (filtro.getAndamentoAf() == DominioAndamentoAutorizacaoFornecimento.ALTERACAO_PENDENTE_JUSTIFICATIVA) {
			exp = Restrictions.or(Subqueries.propertyNotIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(),this.obterCriteriaAndamentoQuery1(filtro)), Restrictions.or(Subqueries.propertyIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), this.obterCriteriaAndamentoQuery2(filtro)), Subqueries.propertyIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), this.obterCriteriaAndamentoQuery3(filtro))));
		} else {
			exp = Restrictions.and(Subqueries.propertyIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), this.obterCriteriaAndamentoQuery1(filtro)), Restrictions.and(Subqueries.propertyNotIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), this.obterCriteriaAndamentoQuery2(filtro)), Restrictions.and(Subqueries.propertyNotIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), this.obterCriteriaAndamentoQuery3(filtro)), Subqueries.propertyIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), this.obterCriteriaAndamentoQuery4(filtro)))));
		}
		return exp;
	}
	private void preencherFiltroNumeroEComplementoAf(DetachedCriteria criteria, PesquisaAutorizacaoFornecimentoVO filtro) {
		if (filtro.getLctNumero() != null) {
			criteria.add(Restrictions.eq("AF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getLctNumero()));
			if (filtro.getNumeroComplemento() != null) {
				criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getNumeroComplemento()));
			}
		}
	}
	private void preencherFiltroPeriodoDataVencimentoAf(DetachedCriteria criteria, PesquisaAutorizacaoFornecimentoVO filtro) {
		if (filtro.getDataInicioContrato() != null && filtro.getDataFimContrato() != null) {
			criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNotNull("AF."+ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString()), Restrictions.between("AF."+ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString(), filtro.getDataInicioContrato(), filtro.getDataFimContrato())), Restrictions.and(Restrictions.isNull("AF."+ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString()), Restrictions.between("AF."+ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString(), filtro.getDataInicioContrato(), filtro.getDataFimContrato()))));			
		}
	}
	private DetachedCriteria obterCriteriaPesquisaAutorizacaoFornecimento(PesquisaAutorizacaoFornecimentoVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AF");
		criteria.createAlias(ScoAutorizacaoForn.Fields.SERVIDOR_GESTOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROP."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FORN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROP."+ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LIC."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MODLIC", JoinType.LEFT_OUTER_JOIN);
		this.preencherFiltroNumeroEComplementoAf(criteria, filtro); 
		if (filtro.getSituacaoAf() != null) {
			criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), filtro.getSituacaoAf()));
		}
		if (filtro.getFornecedor() != null) {
			criteria.add(Restrictions.eq("PROP."+ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString(), filtro.getFornecedor().getNumero()));
		}
		if (filtro.getNumeroContrato() != null) {
			criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString(), filtro.getNumeroContrato()));
		}
		if (filtro.getModalidadeCompra() != null) {
			criteria.add(Restrictions.eq("LIC."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), filtro.getModalidadeCompra()));
		}
		if (filtro.getServidorGestor() != null) {
			criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SERVIDOR_GESTOR.toString(), filtro.getServidorGestor()));
		}
		if (filtro.getTipoFiltroAf() != null) {
			DetachedCriteria subQueryFases = obterCriteriaFasesPesquisaAutorizacaoFornecimento(filtro);
			criteria.add(Subqueries.propertyIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), subQueryFases));
		}
		this.preencherFiltroPeriodoDataVencimentoAf(criteria, filtro);
		this.preencherFiltroAfPendente(criteria, filtro);
		this.preencherFiltroAfParcelasVencidas(criteria, filtro);
		if (filtro.getAndamentoAf() != null) {
			criteria.add(this.obterFiltroAndamento(filtro));
		}
		return criteria;
	}
	private void preencherFiltroAfPendente(DetachedCriteria criteria, PesquisaAutorizacaoFornecimentoVO filtro) {
		if (filtro.getPendente() != null) {
			if (filtro.getPendente().isSim()) {
				criteria.add(Restrictions.or(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
			} else {
				criteria.add(Restrictions.not(Restrictions.or(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA))));				
			}
		}
	}
	private void preencherFiltroAfParcelasVencidas(DetachedCriteria criteria, PesquisaAutorizacaoFornecimentoVO filtro) {
		if (filtro.getVencida() != null) {
			DetachedCriteria subQueryProg = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
			subQueryProg.setProjection(Projections.property("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString()));
			subQueryProg.createAlias("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "IAF");
			subQueryProg.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FS");
			subQueryProg.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));				
			subQueryProg.add(Restrictions.lt("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
			subQueryProg.add(Restrictions.sqlRestriction("(CASE WHEN FS2_.TIPO = 'C' AND (PEA_.QTDE_ENTREGUE   IS NULL OR PEA_.QTDE_ENTREGUE < PEA_.QTDE) THEN 1 WHEN FS2_.TIPO = 'S' AND (PEA_.VALOR_EFETIVADO IS NULL OR PEA_.VALOR_EFETIVADO < PEA_.VALOR_TOTAL) THEN 1 ELSE 0 END) = 1"));
			if (filtro.getVencida().isSim()) {
				criteria.add(Restrictions.or(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA)));
				criteria.add(Subqueries.propertyIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), subQueryProg));
			} else {
				criteria.add(Restrictions.or(Restrictions.not(Restrictions.or(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE), Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.PA))),Restrictions.not(Subqueries.propertyIn("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), subQueryProg))));				
			}
		}
	}
	public List<ScoAutorizacaoForn> pesquisarAutorizacaoFornecimento(Integer first, Integer max, String order, boolean asc, PesquisaAutorizacaoFornecimentoVO filtro) {
		return executeCriteria(this.obterCriteriaPesquisaAutorizacaoFornecimento(filtro).addOrder(Order.asc(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())).addOrder(Order.asc(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())), first, max, order, asc, CacheMode.NORMAL);
	}
	public Long contarAutorizacaoFornecimento(PesquisaAutorizacaoFornecimentoVO filtro) {
		return executeCriteriaCount(this.obterCriteriaPesquisaAutorizacaoFornecimento(filtro));
	}
	private Boolean verificarAndamento(DominioAndamentoAutorizacaoFornecimento andamento, Integer afnNumero, Short numeroComplemento) {
		PesquisaAutorizacaoFornecimentoVO filtroVO = new PesquisaAutorizacaoFornecimentoVO();
		if (afnNumero != null) {
			filtroVO.setLctNumero(afnNumero);
		}
		filtroVO.setAndamentoAf(andamento);
		filtroVO.setNumeroComplemento(numeroComplemento);
		DetachedCriteria criteria = this.obterCriteriaPesquisaAutorizacaoFornecimento(filtroVO);
		return executeCriteriaCount(criteria) >  0;
	}
	public DominioAndamentoAutorizacaoFornecimento obterAndamentoAutorizacaoFornecimento(Integer afnNumero, Short numeroComplemento) {
		DominioAndamentoAutorizacaoFornecimento ret = null;
		if (verificarAndamento(DominioAndamentoAutorizacaoFornecimento.ALTERACAO_PENDENTE_JUSTIFICATIVA, afnNumero, numeroComplemento)) {
			ret = DominioAndamentoAutorizacaoFornecimento.ALTERACAO_PENDENTE_JUSTIFICATIVA;
		} else if (verificarAndamento(DominioAndamentoAutorizacaoFornecimento.VERSAO_JA_ASSINADA, afnNumero, numeroComplemento)) {
			ret = DominioAndamentoAutorizacaoFornecimento.VERSAO_JA_ASSINADA;
		} else if (verificarAndamento(DominioAndamentoAutorizacaoFornecimento.VERSAO_EMPENHADA, afnNumero, numeroComplemento)) {
			ret = DominioAndamentoAutorizacaoFornecimento.VERSAO_EMPENHADA;
		} else if (verificarAndamento(DominioAndamentoAutorizacaoFornecimento.AF_LIBERADA_ASSINATURA, afnNumero, numeroComplemento)) {
			ret = DominioAndamentoAutorizacaoFornecimento.AF_LIBERADA_ASSINATURA;
		} else if (verificarAndamento(DominioAndamentoAutorizacaoFornecimento.LIBERAR_AF_ASSINATURA, afnNumero, numeroComplemento)) {
			ret = DominioAndamentoAutorizacaoFornecimento.LIBERAR_AF_ASSINATURA;
		}		
		return ret;
	}
	public List<ScoAutorizacaoForn> pesquisarAfPorItemProposta(Integer lctNumero, Integer frnNumero, Integer vbgSeq, Integer ntdGndCodigo, Byte ntdCodigo, Integer cdpNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class);
		criteria.createAlias(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR");
		criteria.createAlias(ScoAutorizacaoForn.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), "CDP");
		criteria.add(Restrictions.eq("PFR."+ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), lctNumero));
		criteria.add(Restrictions.eq("PFR."+ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString(), frnNumero));
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.VBG_SEQ.toString(), vbgSeq));
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.NTD_GND_CODIGO.toString(), ntdGndCodigo));
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.NTD_CODIGO.toString(), ntdCodigo));
		criteria.add(Restrictions.eq("CDP."+ScoCondicaoPagamentoPropos.Fields.NUMERO.toString(), cdpNumero));
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.AE));
		return executeCriteria(criteria);
	}
	public Short obterMaxNroComplemento(Integer numPac){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class);
		criteria.createAlias(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR");
		criteria.setProjection(Projections.max(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()));  
		criteria.add(Restrictions.eq("PFR."+ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), numPac));
		return (Short)this.executeCriteriaUniqueResult(criteria);
	}
	public ScoAutorizacaoForn buscarAutFornPorNumPac(Integer numPac, Short numComplemento){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class);
		criteria.createAlias(ScoAutorizacaoForn.Fields.NATUREZA_DESPESA.toString(), "ND", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.GRUPO_NATUREZA_DESPESA.toString(), "GND", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.VERBA_GESTAO.toString(), "VG", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.MOEDA.toString(), "MOEDA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.CVF_CODIGO.toString(), "CONV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.SERVIDOR_GESTOR.toString(), "SERV_GES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_GES."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF_GES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), "CDP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CDP." + ScoCondicaoPagamentoPropos.Fields.FORMA_PAGAMENTO.toString(), "FPA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.SERVIDOR_ASSINA_COORD.toString(), "SERVCOORD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "ITENS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ITENS." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IPO." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ILIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROP."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FORN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROP."+ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LIC."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MOD", JoinType.LEFT_OUTER_JOIN);	
		
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), numPac));
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), numComplemento));
		return (ScoAutorizacaoForn)this.executeCriteriaUniqueResult(criteria);
	}
	public List<PesquisaAutorizacaoFornecimentoVO> pesquisarListaAfs(FiltroPesquisaAssinarAFVO filtroPesquisaAssinarAFVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "SAF");		
		criteria.createAlias("SAF."+ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROP."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList p = Projections.projectionList();

		p.add(Projections.property("SAF." + ScoAutorizacaoForn.Fields.NUMERO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.AFN_NUMERO.toString());
		p.add(Projections.property("SAF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.NUMERO_COMPLEMENTO.toString());
		p.add(Projections.property("SAF." + ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.SEQUENCIA_ALTERACAO.toString());
		p.add(Projections.property("SAF." + ScoAutorizacaoForn.Fields.OBSERVACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.OBSERVACAO.toString());
		p.add(Projections.property("SAF." + ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.NUMERO_CONTRATO.toString());
		p.add(Projections.property("SAF." + ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.DT_PREV_ENTREGA.toString());
		p.add(Projections.property("SAF." + ScoAutorizacaoForn.Fields.DT_ALTERACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.DT_ALTERACAO.toString());
		p.add(Projections.property("SAF." + ScoAutorizacaoForn.Fields.SITUACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.SITUACAO_AF.toString());
		p.add(Projections.property("SAF." + ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.DT_VENCIMENTO_CONTRATO.toString());
		p.add(Projections.property("SAF." + ScoAutorizacaoForn.Fields.DT_GERACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.DT_GERACAO.toString());
		p.add(Projections.property("PROP." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.LCT_NUMERO.toString());
		p.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.RAZAO_SOCIAL.toString());
		p.add(Projections.property("SAF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.PROPOSTA_FORNECEDOR.toString());
		p.add(Projections.property("PROP." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.FORNECEDOR.toString());
		
		
		p.add(Projections.sqlProjection(" {alias}.CDP_NUMERO as " + PesquisaAutorizacaoFornecimentoVO.Fields.CDP_NUMERO.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.CDP_NUMERO.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection(" {alias}.NTD_GND_CODIGO as " + PesquisaAutorizacaoFornecimentoVO.Fields.CODIGO_GRUPO_NATUREZA.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.CODIGO_GRUPO_NATUREZA.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection(" {alias}.SER_MATRICULA_GESTOR as " + PesquisaAutorizacaoFornecimentoVO.Fields.MATRICULA_GESTOR.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.MATRICULA_GESTOR.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection(" {alias}.SER_VIN_CODIGO_GESTOR as " + PesquisaAutorizacaoFornecimentoVO.Fields.VINCULO_SERVIDOR_GESTOR.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.VINCULO_SERVIDOR_GESTOR.toString()}, new Type[]{ShortType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT rap1.usuario FROM rap_servidores rap1 where matricula = {alias}.SER_MATRICULA_GESTOR and vin_codigo = {alias}.SER_VIN_CODIGO_GESTOR) as " + PesquisaAutorizacaoFornecimentoVO.Fields.LOGIN_GESTOR.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.LOGIN_GESTOR.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT nome FROM rap_servidores rap2, AGH.RAP_PESSOAS_FISICAS pes where rap2.pes_codigo = pes.codigo AND rap2.matricula = {alias}.SER_MATRICULA_GESTOR and rap2.vin_codigo = {alias}.SER_VIN_CODIGO_GESTOR) as " + PesquisaAutorizacaoFornecimentoVO.Fields.NOME_GESTOR.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.NOME_GESTOR.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT descricao FROM AGH.SCO_MOTIVOS_ALTERACAO_AF where codigo = {alias}.maa_codigo) as " + PesquisaAutorizacaoFornecimentoVO.Fields.DESCRICAO_MOTIVO_ALTERACAO.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.DESCRICAO_MOTIVO_ALTERACAO.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT MIN(FSC2.TIPO) FROM AGH.SCO_FASES_SOLICITACOES FSC2 WHERE FSC2.IAF_AFN_NUMERO = {alias}.NUMERO) as " + PesquisaAutorizacaoFornecimentoVO.Fields.TIPO_SOLICITACAO.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.TIPO_SOLICITACAO.toString()}, new Type[]{StringType.INSTANCE}));		
		p.add(Projections.sqlProjection(" (SELECT COUNT(scocumxpro0_.seq) FROM AGH.SCO_CUM_X_PROGR_ENTREGAS scocumxpro0_, AGH.SCO_PROGR_ENTREGA_ITENS_AF scoprogent1_ WHERE scocumxpro0_.PEA_IAF_AFN_NUMERO=scoprogent1_.IAF_AFN_NUMERO and scocumxpro0_.PEA_IAF_NUMERO=scoprogent1_.IAF_NUMERO AND SCOCUMXPRO0_.PEA_PARCELA=SCOPROGENT1_.PARCELA AND scoprogent1_.IAF_AFN_NUMERO = {alias}.NUMERO) as " + PesquisaAutorizacaoFornecimentoVO.Fields.QTD_CUM.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.QTD_CUM.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT COALESCE(SUM(fso2.VALOR_ORCADO),0) FROM AGH.FSO_PREVISOES_ORCAMENTARIAS fso2 inner join AGH.FSO_EXERCICIOS_ORCAMENTARIOS tab1x1_ on fso2.EXO_EXERCICIO=tab1x1_.EXERCICIO WHERE TAB1X1_.IND_EXERCICIO_CORRENTE='S' and fso2.GND_CODIGO={alias}.NTD_GND_CODIGO   ) as " + PesquisaAutorizacaoFornecimentoVO.Fields.VLR_ORCAMENTO.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.VLR_ORCAMENTO.toString()}, new Type[]{BigDecimalType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT COALESCE(SUM(fso3.VALOR_COMPROMETIDO),0) FROM AGH.FSO_PREVISOES_ORCAMENTARIAS fso3 inner join AGH.FSO_EXERCICIOS_ORCAMENTARIOS TAB1X1_ on fso3.EXO_EXERCICIO=tab1x1_.EXERCICIO WHERE TAB1X1_.IND_EXERCICIO_CORRENTE='S' and fso3.GND_CODIGO={alias}.NTD_GND_CODIGO ) as " + PesquisaAutorizacaoFornecimentoVO.Fields.VLR_COMPROMETIDO.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.VLR_COMPROMETIDO.toString()}, new Type[]{BigDecimalType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT COALESCE(CGC, CPF) from AGH.SCO_FORNECEDORES WHERE NUMERO = {alias}.PFR_FRN_NUMERO) as " + PesquisaAutorizacaoFornecimentoVO.Fields.CNPJ_CPF_FORN.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.CNPJ_CPF_FORN.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT COALESCE(max(PEA2.AFE_NUMERO),0)+1 FROM AGH.SCO_PROGR_ENTREGA_ITENS_AF pea2 WHERE PEA2.IND_ASSINATURA='S' and PEA2.IND_PLANEJAMENTO='S' AND PEA2.IND_CANCELADA='N' AND PEA2.IAF_AFN_NUMERO= {alias}.NUMERO) as " + PesquisaAutorizacaoFornecimentoVO.Fields.MAX_NUMERO_AFP.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.MAX_NUMERO_AFP.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT COALESCE(sum(pea4.VALOR_TOTAL),0) from AGH.SCO_PROGR_ENTREGA_ITENS_AF pea4 where PEA4.IND_ASSINATURA='N' AND PEA4.IND_PLANEJAMENTO='S' AND PEA4.IND_CANCELADA='N' AND PEA4.IAF_AFN_NUMERO={alias}.NUMERO   ) as " + PesquisaAutorizacaoFornecimentoVO.Fields.VALOR_TOTAL_AFP.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.VALOR_TOTAL_AFP.toString()}, new Type[]{BigDecimalType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT COUNT(nr_contrato) FROM AGH.SCO_CONTRATOS WHERE nr_contrato = {alias}.nro_contrato) as " + PesquisaAutorizacaoFornecimentoVO.Fields.EXISTE_CONTRATO_SICON.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.EXISTE_CONTRATO_SICON.toString()}, new Type[]{IntegerType.INSTANCE}));
		
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisaAutorizacaoFornecimentoVO.class));		
		
		DetachedCriteria subCriteriaNumero = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		subCriteriaNumero.add(Restrictions.eqProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString(), "SAF."+ ScoAutorizacaoForn.Fields.NUMERO.toString()));
		subCriteriaNumero.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), Boolean.TRUE));
		subCriteriaNumero.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.FALSE));
		subCriteriaNumero.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
		subCriteriaNumero.setProjection(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString()));
		criteria.add(Subqueries.propertyIn("SAF." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subCriteriaNumero));
		if (filtroPesquisaAssinarAFVO != null) {
			if (filtroPesquisaAssinarAFVO.getNumeroAf() != null) {
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtroPesquisaAssinarAFVO.getNumeroAf()));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroComplemento() != null) {
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtroPesquisaAssinarAFVO.getNumeroComplemento()));
			}
			if (filtroPesquisaAssinarAFVO.getIndContrato() != null && filtroPesquisaAssinarAFVO.getIndContrato()) {
				final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class);
				subCriteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), filtroPesquisaAssinarAFVO.getIndContrato()));
				subCriteria.setProjection(Projections.property(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
				criteria.add(Subqueries.propertyIn("SAF." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subCriteria));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroContrato() != null) {
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString(), filtroPesquisaAssinarAFVO.getNumeroContrato()));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroFornecedor() != null) {
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), filtroPesquisaAssinarAFVO.getNumeroFornecedor()));
			}
			if (filtroPesquisaAssinarAFVO.getVinCodigoGestor() != null && filtroPesquisaAssinarAFVO.getMatriculaGestor() != null) {
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoFornJn.Fields.SER_VIN_CODIGO_GESTOR.toString(),filtroPesquisaAssinarAFVO.getVinCodigoGestor()));
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoFornJn.Fields.SER_MATRICULA_GESTOR.toString(),filtroPesquisaAssinarAFVO.getMatriculaGestor()));
			}
			if (StringUtils.isNotEmpty(filtroPesquisaAssinarAFVO.getCodigoModalidadeCompra())) {
				final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoLicitacao.class);
				subCriteria.add(Restrictions.eq(ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString(),filtroPesquisaAssinarAFVO.getCodigoModalidadeCompra()));
				subCriteria.setProjection(Projections.property(ScoLicitacao.Fields.NUMERO.toString()));
				criteria.add(Subqueries.propertyIn("SAF." + ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString(), subCriteria));
			}
			if (filtroPesquisaAssinarAFVO.getTipoCompra() != null) {
				this.addSubCriteriaTipoCompra(criteria, filtroPesquisaAssinarAFVO);
			}
		}
		criteria.addOrder(Order.desc("SAF." + ScoAutorizacaoFornJn.Fields.DT_ALTERACAO.toString()));
		criteria.addOrder(Order.desc("SAF." + ScoAutorizacaoFornJn.Fields.NUMERO.toString()));
		return this.executeCriteria(criteria);
	}
	public Long pesquisarListaAfsCount(FiltroPesquisaAssinarAFVO filtroPesquisaAssinarAFVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "SAF");		
		criteria.createAlias("SAF."+ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP", JoinType.LEFT_OUTER_JOIN);

		criteria.setProjection(Projections.count("SAF." + ScoAutorizacaoForn.Fields.NUMERO.toString()));
		
		DetachedCriteria subCriteriaNumero = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class, "PEA");
		subCriteriaNumero.add(Restrictions.eqProperty("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString(), "SAF."+ ScoAutorizacaoForn.Fields.NUMERO.toString()));
		subCriteriaNumero.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), Boolean.TRUE));
		subCriteriaNumero.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.FALSE));
		subCriteriaNumero.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
		subCriteriaNumero.setProjection(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.ITEM_AUTORIZACAO_AFN_NUMERO.toString()));
		criteria.add(Subqueries.propertyIn("SAF." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subCriteriaNumero));
		if (filtroPesquisaAssinarAFVO != null) {
			if (filtroPesquisaAssinarAFVO.getNumeroAf() != null) {
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtroPesquisaAssinarAFVO.getNumeroAf()));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroComplemento() != null) {
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtroPesquisaAssinarAFVO.getNumeroComplemento()));
			}
			if (filtroPesquisaAssinarAFVO.getIndContrato() != null && filtroPesquisaAssinarAFVO.getIndContrato()) {
				final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class);
				subCriteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), filtroPesquisaAssinarAFVO.getIndContrato()));
				subCriteria.setProjection(Projections.property(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
				criteria.add(Subqueries.propertyIn("SAF." + ScoAutorizacaoForn.Fields.NUMERO.toString(), subCriteria));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroContrato() != null) {
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString(), filtroPesquisaAssinarAFVO.getNumeroContrato()));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroFornecedor() != null) {
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), filtroPesquisaAssinarAFVO.getNumeroFornecedor()));
			}
			if (filtroPesquisaAssinarAFVO.getVinCodigoGestor() != null && filtroPesquisaAssinarAFVO.getMatriculaGestor() != null) {
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoFornJn.Fields.SER_VIN_CODIGO_GESTOR.toString(),filtroPesquisaAssinarAFVO.getVinCodigoGestor()));
				criteria.add(Restrictions.eq("SAF." + ScoAutorizacaoFornJn.Fields.SER_MATRICULA_GESTOR.toString(),filtroPesquisaAssinarAFVO.getMatriculaGestor()));
			}
			if (StringUtils.isNotEmpty(filtroPesquisaAssinarAFVO.getCodigoModalidadeCompra())) {
				final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoLicitacao.class);
				subCriteria.add(Restrictions.eq(ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString(),filtroPesquisaAssinarAFVO.getCodigoModalidadeCompra()));
				subCriteria.setProjection(Projections.property(ScoLicitacao.Fields.NUMERO.toString()));
				criteria.add(Subqueries.propertyIn("SAF." + ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString(), subCriteria));
			}
			if (filtroPesquisaAssinarAFVO.getTipoCompra() != null) {
				this.addSubCriteriaTipoCompra(criteria, filtroPesquisaAssinarAFVO);
			}
		}
		criteria.addOrder(Order.desc("SAF." + ScoAutorizacaoFornJn.Fields.DT_ALTERACAO.toString()));
		criteria.addOrder(Order.desc("SAF." + ScoAutorizacaoFornJn.Fields.NUMERO.toString()));
		return (Long) this.executeCriteriaUniqueResult(criteria);
	}
	private void addSubCriteriaTipoCompra(final DetachedCriteria criteria, final FiltroPesquisaAssinarAFVO filtroPesquisaAssinarAFVO) {
		switch (filtroPesquisaAssinarAFVO.getTipoCompra()) {
		case M: {
			final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
			subCriteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
			if (filtroPesquisaAssinarAFVO.getCodigoGrupoMaterial() != null) {
				subCriteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
			}
			subCriteria.add(Restrictions.isNotNull("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			if (filtroPesquisaAssinarAFVO.getCodigoGrupoMaterial() != null) {
				subCriteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(),filtroPesquisaAssinarAFVO.getCodigoGrupoMaterial()));
			}
			this.addFiltroNatureza(subCriteria, filtroPesquisaAssinarAFVO.getCodigoGrupoNaturezaDespesa(),filtroPesquisaAssinarAFVO.getCodigoNaturezaDespesa(), filtroPesquisaAssinarAFVO.getSeqVerbaGestao(), "SLC");
			subCriteria.setProjection(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			criteria.add(Subqueries.propertyIn("SAF." + ScoAutorizacaoFornJn.Fields.NUMERO.toString(), subCriteria));
			break;
		}
		case S: {
			final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
			subCriteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS");
			if (filtroPesquisaAssinarAFVO.getCodigoGrupoServico() != null) {
				subCriteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SVR");
			}
			subCriteria.add(Restrictions.isNotNull("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			if (filtroPesquisaAssinarAFVO.getCodigoGrupoServico() != null) {
				subCriteria.add(Restrictions.eq("SVR." + ScoServico.Fields.GRUPO_SERVICO_CODIGO.toString(), filtroPesquisaAssinarAFVO.getCodigoGrupoServico()));
			}
			this.addFiltroNatureza(subCriteria, filtroPesquisaAssinarAFVO.getCodigoGrupoNaturezaDespesa(),filtroPesquisaAssinarAFVO.getCodigoNaturezaDespesa(), filtroPesquisaAssinarAFVO.getSeqVerbaGestao(), "SLS");
			subCriteria.setProjection(Projections.property("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			criteria.add(Subqueries.propertyIn("SAF." + ScoAutorizacaoFornJn.Fields.NUMERO.toString(), subCriteria));
			break;
		}
		default:
			break;
		}
	}
	private void addFiltroNatureza(final DetachedCriteria subCriteria, final Integer codigoGrupoNaturezaDespesa, final Byte codigoNaturezaDespesa,
			final Integer seqVerbaGestao, String prefixo) {
		if (codigoGrupoNaturezaDespesa != null) {
			subCriteria.add(Restrictions.eq(prefixo + "." + ScoSolicitacaoServico.Fields.NTD_GND_CODIGO.toString(), codigoGrupoNaturezaDespesa));
		}
		if (codigoNaturezaDespesa != null) {
			subCriteria.add(Restrictions.eq(prefixo + "." + ScoSolicitacaoDeCompra.Fields.NTD_CODIGO.toString(), codigoNaturezaDespesa));
		}
		if (seqVerbaGestao != null) {
			subCriteria.add(Restrictions.eq(prefixo + "." + ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO_SEQ.toString(), seqVerbaGestao));
		}
	}
	public List<ScoAutorizacaoForn> pesquisarVerbaGestaoAssociadaAf(FsoVerbaGestao verbaGestao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class);
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.VBG_SEQ.toString(), verbaGestao.getSeq()));
		criteria.add(Restrictions.ne(ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.EX));
		criteria.addOrder(Order.asc(ScoAutorizacaoForn.Fields.NUMERO.toString()));
		return executeCriteria(criteria, 0, 10, null);
	}
	public Long contarAfGerada(final Integer numeroProposta) {
		final DetachedCriteria criteria = this.criarDetachedCriteria();
		criteria.add(Restrictions.eq(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), numeroProposta));
		return executeCriteriaCount(criteria);
	}
	public List<ScoAutorizacaoForn> pesquisarAFComplementoFornecedor(Integer numeroAf, Short numComplementoAf, Object numFornecedor, String tipo) {
		return executeCriteria(new PesquisaAFComplementoFornecedorQueryBuilder().build(numeroAf, numComplementoAf, numFornecedor, tipo));
	}
	public List<ParcelasAFPendEntVO> listarParcelasAFsPendentes(FiltroParcelasAFPendEntVO filtro, Integer firstResult, Integer maxResult,
			String order, boolean asc) {
		final DetachedCriteria criteria = criarCriteriaBaseAFsPendEnt(filtro);
		criteria.addOrder(Order.asc("AFN."+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()));
		criteria.addOrder(Order.asc("AFN."+ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()));
		criteria.addOrder(Order.asc("AFP."+ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()));
		return  executeCriteria(criteria, firstResult, maxResult, order, asc);		
	}
	public Long listarParcelasAFsPendentesCount(FiltroParcelasAFPendEntVO filtro) {
		final DetachedCriteria criteria = criarCriteriaBaseAFsPendEnt(filtro);
		return executeCriteriaCount(criteria);
	}
	private DetachedCriteria criarCriteriaBaseAFsPendEnt(FiltroParcelasAFPendEntVO filtro) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.SCO_AF_PEDIDOS.toString(), "AFP");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PF");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.SERVIDOR_GESTOR.toString(), "RAP");
		criteria.add(Restrictions.not(Restrictions.in("AFN."+ ScoAutorizacaoForn.Fields.SITUACAO.toString(),new DominioSituacaoAutorizacaoFornecimento[] {DominioSituacaoAutorizacaoFornecimento.EF, DominioSituacaoAutorizacaoFornecimento.EP, DominioSituacaoAutorizacaoFornecimento.EX})));
		if (filtro.getNumeroAF() != null) {
			criteria.add(Restrictions.eq("AFN."+ ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getNumeroAF()));
		}
		if (filtro.getComplemento() != null) {
			criteria.add(Restrictions.eq("AFN."+ ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getComplemento()));
		}
		DetachedCriteria subCriteriaItens = this.criarSubCriteriaItensAFPorNumAF();
		criteria.add(Subqueries.notExists(subCriteriaItens));
		Integer qtdDias = filtro.getQtdDiasEntrega();
		DetachedCriteria subCriteriaDtPrevEnt = this.criarSubCriteriaQtdDias(qtdDias, filtro.getDataEntregaInicial(), filtro.getDataEntregaFinal());
		criteria.add(Subqueries.exists(subCriteriaDtPrevEnt));
		if (filtro.getGestor() != null) {
			criteria.add(Restrictions.eq("AFN."+ ScoAutorizacaoForn.Fields.SERVIDOR_GESTOR.toString(), filtro.getGestor()));
		}
		if(filtro.getMaterial() != null && filtro.getMaterial().getCodigo() != null) {
			DetachedCriteria subCriteriaMateriais = this.criarSubCriteriaMaterial(filtro.getMaterial().getCodigo());
			criteria.add(Subqueries.exists(subCriteriaMateriais));
		}
		if(filtro.getGrupoMaterial() != null && filtro.getGrupoMaterial().getCodigo() !=null) {
			DetachedCriteria subCriteriaMateriais = this.criarSubCriteriaGrupoMaterial(filtro.getGrupoMaterial().getCodigo());
			criteria.add(Subqueries.exists(subCriteriaMateriais));
		}
		if (filtro.getFornecedor() != null) {
			criteria.add(Restrictions.eq("PF."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), filtro.getFornecedor()));
		}
		if(filtro.getEntregaAtrasada()!= null) {
			 if(filtro.getEntregaAtrasada().isSim()) {
				DetachedCriteria subCriteriaEntregaAtrasadaSim = this.criarSubCriteriaEntregaAtrasadaSim();
				criteria.add(Subqueries.exists(subCriteriaEntregaAtrasadaSim));
			} else {
				DetachedCriteria subCriteriaEntregaAtrasadaNao = this.criarSubCriteriaEntregaAtrasadaNao();
				criteria.add(Subqueries.exists(subCriteriaEntregaAtrasadaNao));
			}
		}
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("AFN."+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()),"numeroAF")
				.add(Projections.property("AFN."+ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()),"complemento")
				.add(Projections.property("AFN."+ScoAutorizacaoForn.Fields.NUMERO.toString()),"numeroAFN")				
				.add(Projections.property("PF."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString()),"fornecedor")
				.add(Projections.property("PF."+ScoPropostaFornecedor.Fields.LICITACAO.toString()),"licitacao")
				.add(Projections.property("AFN."+ScoAutorizacaoForn.Fields.SITUACAO.toString()),"indSituacao")
				.add(Projections.property("AFN."+ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString()),"modalidadeEmpenho")				
				.add(Projections.property("RAP."+ RapServidores.Fields.PESSOA_FISICA.toString()),"pessoaGestor")
				.add(Projections.property("AFP."+ScoAutorizacaoFornecedorPedido.Fields.DT_ENVIO_FORNECEDOR),"dtEnvioForn")
				.add(Projections.property("AFP."+ScoAutorizacaoFornecedorPedido.Fields.DT_PUBLICACAO),"dtPublicacao")
				.add(Projections.property("AFP."+ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()),"numeroAFP")
				.add(Projections.property("AFP."+ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO),"afeAfn"));
		criteria.setResultTransformer(Transformers.aliasToBean(ParcelasAFPendEntVO.class));
		return criteria;
	}
	private DetachedCriteria criarSubCriteriaEntregaAtrasadaNao() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class,"PEA");
		criteria.add(Restrictions.eqProperty("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), "AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), "AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.ge("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		criteria.setProjection(Projections.projectionList().add(Projections.property("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()),"seq"));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoProgEntregaItemAutorizacaoFornecimento.class));
		return criteria;
	}	
	private DetachedCriteria criarSubCriteriaEntregaAtrasadaSim() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class,"PEA");
		criteria.add(Restrictions.eqProperty("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), "AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), "AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.lt("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), new Date()));
		criteria.add(Restrictions.sqlRestriction("COALESCE(qtde,0) > COALESCE(qtde_entregue,0)"));
		criteria.setProjection(Projections.projectionList().add(Projections.property("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()),"seq"));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoProgEntregaItemAutorizacaoFornecimento.class));
		return criteria;
	}
	private DetachedCriteria criarSubCriteriaQtdDias(Integer qtdDias, Date dataInicial, Date dataFinal) {//  #5562 - C1 - Sub2
			DetachedCriteria criteria = DetachedCriteria.forClass(ScoProgEntregaItemAutorizacaoFornecimento.class,"PEA");
			criteria.add(Restrictions.eqProperty("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_AFN_NUMERO.toString(), "AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()));
			criteria.add(Restrictions.eqProperty("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.AFE_NUMERO.toString(), "AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_NUM.toString()));
			Criterion indFornecedor = (Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ENVIO_FORNECEDOR.toString(), Boolean.TRUE));
			Criterion indImpressa = (Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_IMPRESSA.toString(), Boolean.TRUE));
			criteria.add(Restrictions.or(indFornecedor, indImpressa));
			criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
			criteria.add(Restrictions.eq("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
			if(qtdDias == null || qtdDias <= 0) {
				if(dataInicial != null && dataFinal != null) {
					criteria.add(Restrictions.between("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dataInicial, dataFinal));
				} else if(dataInicial != null) {criteria.add(Restrictions.gt("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dataInicial));}
				else if(dataFinal != null) {criteria.add(Restrictions.lt("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dataFinal));}
			} else {
				criteria.add(Restrictions.lt("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), DateUtil.adicionaDias(new Date(), qtdDias)));
			}
			Criterion criterion1= (Restrictions.sqlRestriction(" COALESCE(qtde_entregue,0) < COALESCE(qtde,0) "));
			DetachedCriteria subquery =  this.criarSubCriteriaNotaRecebimento();
			Criterion criterion2 = Subqueries.exists(subquery);
			criteria.add(Restrictions.or(criterion1, criterion2));
			criteria.setProjection(Projections.projectionList().add(Projections.property("PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()),"iafNumero"));
			criteria.setResultTransformer(Transformers.aliasToBean(ScoProgEntregaItemAutorizacaoFornecimento.class));
			return criteria;
	}
	private DetachedCriteria criarSubCriteriaNotaRecebimento() {// #5562 - C1 - Sub2.1
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRecebProvisorio.class, "IRP");
		criteria.createAlias("IRP."+SceItemRecebProvisorio.Fields.NOTA_RECEBIMENTO_PROVISORIO.toString(), "NRP");
		criteria.add(Restrictions.eq("NRP."+SceNotaRecebProvisorio.Fields.IND_CONFIRMADO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("NRP."+SceNotaRecebProvisorio.Fields.IND_ESTORNO.toString(), Boolean.FALSE));		
		criteria.add(Restrictions.eqProperty("NRP."+SceNotaRecebProvisorio.Fields.NRP_SEQ.toString(), "IRP."+SceItemRecebProvisorio.Fields.NRP_SEQ.toString()));
		criteria.add(Restrictions.eqProperty("IRP."+SceItemRecebProvisorio.Fields.PEA_IAF_NUMERO.toString(), "PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("IRP."+SceItemRecebProvisorio.Fields.PEA_IAF_AFN_NUMERO.toString(), "PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()));
		criteria.add(Restrictions.eqProperty("IRP."+SceItemRecebProvisorio.Fields.PEA_PARCELA.toString(), "PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()));
		criteria.add(Restrictions.eqProperty("IRP."+SceItemRecebProvisorio.Fields.PEA_SEQ.toString(), "PEA."+ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()));
		criteria.setProjection(Projections.projectionList().add(Projections.property("IRP."+SceItemRecebProvisorio.Fields.NRP_SEQ.toString()),"nrpSeq"));
		criteria.setResultTransformer(Transformers.aliasToBean(SceItemRecebProvisorio.class));
		return criteria;
	}
	private DetachedCriteria criarSubCriteriaItensAFPorNumAF() {//  #5562 - C1 - Sub1
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.add(Restrictions.eqProperty("IAF."+ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), "AFN."+ScoAutorizacaoForn.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.IND_CONSIGNADO.toString(), Boolean.TRUE));
		criteria.setProjection(Projections.projectionList().add(Projections.property("IAF."+ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString()),"afnNumero"));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoItemAutorizacaoForn.class));
		return criteria;
	}	
	private DetachedCriteria criarSubCriteriaMaterial(Integer codMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class,"FSC");
		criteria.createAlias("FSC."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SC");
		criteria.createAlias("SC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MT");
		criteria.add(Restrictions.eq("MT."+ScoMaterial.Fields.CODIGO.toString(), codMaterial));
		criteria.setProjection(Projections.projectionList().add(Projections.property("FSC."+ScoFaseSolicitacao.Fields.NUMERO.toString()),"numero"));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoFaseSolicitacao.class));
		return criteria;
	}
	private DetachedCriteria criarSubCriteriaGrupoMaterial(Integer codGrupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class,"FSC");
		criteria.createAlias("FSC."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SC");
		criteria.createAlias("SC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MT");
		criteria.createAlias("MT."+ScoMaterial.Fields.GRUPO_MATERIAL, "GT");
		criteria.add(Restrictions.eqProperty("FSC."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), "AFP." + ScoAutorizacaoFornecedorPedido.Fields.ID_AFN_NUMERO.toString()));
		criteria.add(Restrictions.eq("GT."+ScoGrupoMaterial.Fields.CODIGO.toString(), codGrupo));
		criteria.setProjection(Projections.projectionList().add(Projections.property("FSC."+ScoFaseSolicitacao.Fields.NUMERO.toString()),"numero"));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoFaseSolicitacao.class));
		return criteria;
	}
	public List<AutorizacaoFornecimentoItemFornecedorVO> listarAFsMateriaisParaEmailAtraso(Integer numeroAutorizacaoFornecimento, Integer numeroPedidoAF) {
		AFsMateriaisParaEmailAtrasoQueryBuilder builder = afsMateriaisParaEmailAtrasoQueryBuilder.get();
		List<AutorizacaoFornecimentoItemFornecedorVO> listAutorizacaoFornecimentoMaterialAtraso = builder.build(numeroAutorizacaoFornecimento, numeroPedidoAF).list();
		return listAutorizacaoFornecimentoMaterialAtraso;
	}
	public List<PesquisarPlanjProgrEntregaItensAfVO> listarProgrEntregaItensAfNaoProgramadas(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro, Integer fornecedorPadrao, Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
		ProgrEntregaItensAfProgramadasOuNaoProgramadasQueryBuilder builder = new ProgrEntregaItensAfProgramadasOuNaoProgramadasQueryBuilder();
		if(firstResult != null && maxResults != null){
			return executeCriteria(builder.build(filtro, fornecedorPadrao, false, false),firstResult, maxResults, orderProperty, asc);
		}
		return executeCriteria(builder.build(filtro, fornecedorPadrao, false, false));
	}
	public List<PesquisarPlanjProgrEntregaItensAfVO> listarProgrEntregaItensAfProgramadas(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro, Integer fornecedorPadrao, Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
		ProgrEntregaItensAfProgramadasOuNaoProgramadasQueryBuilder builder = new ProgrEntregaItensAfProgramadasOuNaoProgramadasQueryBuilder();
		if(firstResult != null && maxResults != null){
			return executeCriteria(builder.build(filtro, fornecedorPadrao, true, false), firstResult, maxResults, orderProperty, asc);
		}
		return executeCriteria(builder.build(filtro, fornecedorPadrao, true, false));
	}
	public List<PesquisarPlanjProgrEntregaItensAfVO> listarProgrEntregaItensAfPrevEntrega(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro, Integer fornecedorPadrao, Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
		DetachedCriteria c2 = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN");
		c2.createAlias("AFN." + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "IAF", JoinType.INNER_JOIN);
		c2.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), "PEA", JoinType.INNER_JOIN);
		c2.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getNumeroAF()));
		c2.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		c2.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), false));
		c2.setProjection(Projections.projectionList().add(Projections.property("AFN." + ScoAutorizacaoFornJn.Fields.NUMERO.toString())));
		if(filtro.getComplemento() != null) {
			c2.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getComplemento()));
		}
		ProgrEntregaItensAfComPrevisaoQueryBulder builder = new ProgrEntregaItensAfComPrevisaoQueryBulder();
		builder.build(filtro, fornecedorPadrao, executeCriteriaExists(c2), isOracle(), false);
		if(firstResult != null && maxResults != null){
			return executeCriteria(builder.getResult(), firstResult, maxResults, orderProperty, asc);
		}
		return executeCriteria(builder.getResult());
	}
	public List<PesquisarPlanjProgrEntregaItensAfVO> listarProgrEntregaItensAfPadrao(Integer quantidadeDias, Integer diaDoMes, Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
		ProgrEntregaItensAfPadraoQueryBuilder builder = new ProgrEntregaItensAfPadraoQueryBuilder();
		if(firstResult != null && maxResults != null){
			return executeCriteria(builder.build(quantidadeDias, diaDoMes, false), firstResult, maxResults, orderProperty, asc);
		}
		return executeCriteria(builder.build(quantidadeDias, diaDoMes, false));
	}
	public List<ConsultaItensAFProgramacaoEntregaVO> consultarItensAFProgramacaoEntregaMaterial(final Integer numeroAF, final Boolean isIndExclusao) {
		ConsultaItensMatAFProgramacaoEntregaQueryBuilder builder = consultaItensMatAFProgramacaoEntregaQueryBuilder.get();
		List<ConsultaItensAFProgramacaoEntregaVO> list = builder.build(numeroAF, isIndExclusao).list();
		return list;
	}
	public List<ConsultaItensAFProgramacaoEntregaVO> consultarItensAFProgramacaoEntregaServico(final Integer numeroAF, final Boolean isIndExclusao) {
		ConsultaItensSerAFProgramacaoEntregaQueryBuilder builder = consultaItensSerAFProgramacaoEntregaQueryBuilder.get();
		List<ConsultaItensAFProgramacaoEntregaVO> list = builder.build(numeroAF, isIndExclusao).list();
		return list;
	}
	public Short obterPrazoEntrega(Integer numero) {
		ScoAutorizacaoForn scoAutorizacaoForn = super.obterPorChavePrimaria(numero);
		return scoAutorizacaoForn != null && scoAutorizacaoForn.getPropostaFornecedor() != null ? scoAutorizacaoForn.getPropostaFornecedor().getPrazoEntrega() : null;
	}
	public Long listarProgrEntregaItensAfPadraoCount(Integer quantidadeDias, Integer diaDoMes) {
		ProgrEntregaItensAfPadraoQueryBuilder builder = new ProgrEntregaItensAfPadraoQueryBuilder();
		return executeCriteriaCount(builder.build(quantidadeDias, diaDoMes, true));
	}
	public Long listarProgrEntregaItensAfNaoProgramadasCount(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro, Integer fornecedorPadrao) {
		ProgrEntregaItensAfProgramadasOuNaoProgramadasQueryBuilder builder = new ProgrEntregaItensAfProgramadasOuNaoProgramadasQueryBuilder();
		return executeCriteriaCount(builder.build(filtro, fornecedorPadrao, false, true));
	}
	public Long listarProgrEntregaItensAfProgramadasCount(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro, Integer fornecedorPadrao) {
		ProgrEntregaItensAfProgramadasOuNaoProgramadasQueryBuilder builder = new ProgrEntregaItensAfProgramadasOuNaoProgramadasQueryBuilder();
		return executeCriteriaCount(builder.build(filtro, fornecedorPadrao, true, true));
	}
	public Long listarProgrEntregaItensAfPrevEntregaCount(PesquisarPlanjProgrEntregaItensAfFiltroVO filtro, Integer fornecedorPadrao) {
		DetachedCriteria c2 = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN");
		c2.createAlias("AFN." + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "IAF", JoinType.INNER_JOIN);
		c2.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), "PEA", JoinType.INNER_JOIN);
		c2.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), filtro.getNumeroAF()));
		c2.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		c2.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), false));
		c2.setProjection(Projections.projectionList().add(Projections.property("AFN." + ScoAutorizacaoFornJn.Fields.NUMERO.toString())));
		if(filtro.getComplemento() != null) {
			c2.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getComplemento()));
		}
		ProgrEntregaItensAfComPrevisaoQueryBulder builder = new ProgrEntregaItensAfComPrevisaoQueryBulder();
		builder.build(filtro, fornecedorPadrao, executeCriteriaExists(c2), isOracle(), true);
		return executeCriteriaCount(builder.getResult());
	}
	public Integer buscaNumeroAFsPorPac(ConsultarAndamentoProcessoCompraVO filtro, ConsultarAndamentoProcessoCompraDataVO retorno) {
		ConsultarAndamentoProcessoCompraQueryProvider builder = new ConsultarAndamentoProcessoCompraQueryProvider(filtro);
		SQLQuery query = this.createSQLQuery(builder.buildQueryNumeroAFsPorPac(retorno.getNumeroPac()));
		Number count = (Number) query.uniqueResult();
		return count.intValue();
	}
	public DadosPrimeiraAFVO obterDadosPrimeiraAF(ConsultarAndamentoProcessoCompraVO filtro, ConsultarAndamentoProcessoCompraDataVO retorno) {
		Properties domainPropertie = new Properties();
		domainPropertie.put("enumClass", "br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento");
		domainPropertie.put("type", "12");
		ConsultarAndamentoProcessoCompraQueryProvider builder = new ConsultarAndamentoProcessoCompraQueryProvider(filtro);
		SQLQuery query = this.createSQLQuery(builder.buildQueryDadosPrimeiraAF(retorno.getNumeroPac()));
		return  (DadosPrimeiraAFVO) query.addScalar("af",IntegerType.INSTANCE).
									addScalar("cp",ShortType.INSTANCE).
									addScalar("vencimentoContrato",DateType.INSTANCE).
									addScalar("situacao",  new TypeLocatorImpl(new TypeResolver()).custom(org.hibernate.type.EnumType.class, domainPropertie)).
									addScalar("dataGeracao",DateType.INSTANCE).
									setResultTransformer(Transformers.aliasToBean(DadosPrimeiraAFVO.class)).uniqueResult();
	}
}