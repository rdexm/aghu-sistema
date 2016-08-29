package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.SigAtividadeServicos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoPesos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProducaoObjetoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;

public class SigAtividadeServicosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigAtividadeServicos> {

	private static final long serialVersionUID = -5476813602395233969L;

	public List<SigAtividadeServicos> pesquisarServicosPorSeqAtividade(Integer seqAtividade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividadeServicos.class, "atvServicos");
		criteria.createAlias("atvServicos." + SigAtividadeServicos.Fields.CONTRATO.toString(), "afContrato", JoinType.INNER_JOIN);
		criteria.createAlias("afContrato." + ScoAfContrato.Fields.CONTRATO.toString(), "contrato", JoinType.INNER_JOIN);
		criteria.createAlias("afContrato." + ScoAfContrato.Fields.AUT_FORN.toString(), "contratoAutFornecimento", JoinType.INNER_JOIN);
		
		this.adicionarDetalhamentoCriteria(criteria);
		criteria.add(Restrictions.eq(SigAtividadeServicos.Fields.ATIVIDADE_SEQ.toString(), seqAtividade));
		//#24619 - Serviços: situação, fornecedor
		criteria.addOrder(Order.asc("atvServicos." + SigAtividadeServicos.Fields.IND_SITUACAO.toString()));
		criteria.addOrder(Order.asc("fornecedor." + ScoFornecedor.Fields.NOME_FANTASIA.toString()));
		return executeCriteria(criteria);
	}
	
	
	public SigAtividadeServicos obterAtividadeServicoDetalhada(Integer atvServSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividadeServicos.class, "atvServicos");
		criteria.createAlias("atvServicos." + SigAtividadeServicos.Fields.CONTRATO.toString(), "afContrato", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("afContrato." + ScoAfContrato.Fields.CONTRATO.toString(), "contrato", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("afContrato." + ScoAfContrato.Fields.AUT_FORN.toString(), "contratoAutFornecimento", JoinType.LEFT_OUTER_JOIN);
		this.adicionarDetalhamentoCriteria(criteria);
		criteria.add(Restrictions.eq(SigAtividadeServicos.Fields.SEQ.toString(), atvServSeq));
		return (SigAtividadeServicos) executeCriteriaUniqueResult(criteria);
	}
	
	private void  adicionarDetalhamentoCriteria(DetachedCriteria criteria){
		
		criteria.createAlias("contrato." + ScoContrato.Fields.FORNECEDOR.toString(), "fornecedor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("contratoAutFornecimento." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "contratoAutFornecimentoProposta", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("atvServicos." + SigAtividadeServicos.Fields.ITEM_CONTRATO.toString(), "itemContrato", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("itemContrato." + ScoItensContrato.Fields.CONTRATO.toString(), "contratoItemContrato", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("atvServicos." + SigAtividadeServicos.Fields.AUTORIZACAO_FORNEC.toString(), "autorizacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("autorizacao." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "proposta", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("proposta." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "licitacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("proposta." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "fonecedor", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("atvServicos." + SigAtividadeServicos.Fields.SERVICO.toString(), "servico", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atvServicos." + SigAtividadeServicos.Fields.DIRECIONADOR.toString(), "direcionador", JoinType.LEFT_OUTER_JOIN);
	}
	

	/**
	 * 
	 * Retorna todos os contratos associados em atividades de objetos de 
	 * custos com produção no mês de competência do processamento.
	 * 
	 * @param pmuSeq - seq do processamento mensal sendo executado
	 * @return ScrollableResults - itens de retorno
	 * @author jgugel
	 */
	public ScrollableResults buscaItensContratoAlocadosAtividades(Integer pmuSeq) {
		StringBuilder sql = new StringBuilder(650);

		sql.append("SELECT ")

		.append("tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( " as tvd_seq, ")
		.append("arv." ).append( SigAtividadeServicos.Fields.SEQ.toString() ).append( " as arv_seq, ")
		.append("cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " as cmt_seq, ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( " as cct_codigo, ")
		.append("arv." ).append( SigAtividadeServicos.Fields.CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( " as afco_seq, ")
		.append("arv." ).append( SigAtividadeServicos.Fields.AUTORIZACAO_FORNEC.toString() ).append( '.' ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( " as afn_numero, ")
		.append("arv." ).append( SigAtividadeServicos.Fields.SERVICO.toString() ).append( '.' ).append( ScoServico.Fields.CODIGO.toString() ).append( " as srv_codigo, ")
		.append("cmt." ).append( SigCalculoComponente.Fields.DIRECIONADOR.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString() ).append( " as dir_seq_atividade, ")
		.append("coalesce(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ", 1) * sum(dhp." ).append( SigDetalheProducao.Fields.QTDE.toString()
				).append( ") as peso_oc ")

		.append("FROM ")

		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigDetalheProducao.class.getSimpleName() ).append( " dhp, ")
		.append(SigProducaoObjetoCusto.class.getSimpleName() ).append( " pjc, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj ")
		.append("left join obj." ).append( SigObjetoCustos.Fields.OBJETO_CUSTO_PESO.toString() ).append( " ope, ")
		.append(SigObjetoCustoComposicoes.class.getSimpleName() ).append( " cbt, ")
		.append(SigAtividades.class.getSimpleName() ).append( " tvd, ")
		.append(SigAtividadeServicos.class.getSimpleName() ).append( " arv ")

		.append(" WHERE ")

		.append("pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pmuSeq ")
		.append(" AND pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = dhp." ).append( SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO_SEQ.toString() ).append(' ')
		.append(" AND pjc." ).append( SigProducaoObjetoCusto.Fields.DETALHE_PRODUCAO.toString() ).append( '.' ).append( SigDetalheProducao.Fields.SEQ.toString() ).append( " = dhp."
				).append( SigDetalheProducao.Fields.SEQ.toString() ).append(' ')
		.append(" AND pjc." ).append( SigProducaoObjetoCusto.Fields.CALCULO_OBJETO_CUSTOS.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = cbj."
				).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append(' ')

		.append(" AND cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString()
				).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append(' ')
		.append(" AND cmt." ).append( SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO.toString() ).append( '.' ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString()
				).append( " = cbt." ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString() ).append(' ')
		.append(" AND cbt." ).append( SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString() ).append( '.' ).append( SigAtividades.Fields.SEQ.toString() ).append( " = tvd."
				).append( SigAtividades.Fields.SEQ.toString() ).append(' ')
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv."
				).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append(' ')
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj."
				).append( SigObjetoCustos.Fields.SEQ.toString() ).append(' ')

		.append(" AND tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( " = arv." ).append( SigAtividadeServicos.Fields.ATIVIDADE.toString() ).append( '.'
				).append( SigAtividades.Fields.SEQ.toString() ).append(' ')
		.append(" AND dhp." ).append( SigDetalheProducao.Fields.GRUPO.toString() ).append( " in (:grupos)")
		.append(" AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :dominioTipo ")

		.append("GROUP BY ")

		.append(" tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.SEQ.toString() ).append( ", ")
		.append(" cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( ", ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.AUTORIZACAO_FORNEC.toString() ).append( '.' ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.SERVICO.toString() ).append( '.' ).append( ScoServico.Fields.CODIGO.toString() ).append( ", ")
		.append(" cmt." ).append( SigCalculoComponente.Fields.DIRECIONADOR.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString() ).append( ", ")
		.append(" ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append(' ')
		.append(" ORDER BY ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.AUTORIZACAO_FORNEC.toString() ).append( '.' ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append(' ');

		org.hibernate.Query query = createHibernateQuery(sql.toString());
		query.setInteger("pmuSeq", pmuSeq);
		query.setParameterList("grupos", new DominioGrupoDetalheProducao[]{ DominioGrupoDetalheProducao.PHI, DominioGrupoDetalheProducao.PAC});
		query.setParameter("dominioTipo", DominioTipoObjetoCusto.AS);
		return query.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}

	/**
	 * Busca o somatório de valor para um determinado item de contrato ou um item de AF. 
	 * Para buscar somatório de item de contrato passar o seqAFContrato como not null, 
	 * caso contrário a busca será feita sobre os itens de AF passando o seqAutorizacaoFornecimento e seqScoServico
	 * 
	 * @param sigProcessamentoCusto - processamento mensal sendo executado
	 * @param codigoCentroCusto - centro de custo do objeto de custo
	 * @param seqAFContrato - código do item do contrato alocado na atividade
	 * @param seqAutorizacaoFornecimento - número do item da AF alocada na atividade
	 * @param seqScoServico - código do serviço da AF
	 * @return somatório do valor
	 * @author jgugel
	 */
	public BigDecimal buscaDetalheValorItemContrato(SigProcessamentoCusto sigProcessamentoCusto, Integer codigoCentroCusto, Integer seqAFContrato,
			Integer seqAutorizacaoFornecimento, Integer seqScoServico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigMvtoContaMensal.class);

		criteria.setProjection(Projections.sum(SigMvtoContaMensal.Fields.VALOR.toString()));

		criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString(), sigProcessamentoCusto));
		criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.TIPO_VALOR.toString(), DominioTipoValorConta.DS));

		//Funcao coalesce do select
		LogicalExpression codDebitaNotNull = Restrictions.and(Restrictions.isNotNull(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA.toString()),
				Restrictions.eq(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA.toString() + '.' + FccCentroCustos.Fields.CODIGO.toString(), codigoCentroCusto));

		LogicalExpression codDebitaNull = Restrictions.and(Restrictions.isNull(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA.toString()),
				Restrictions.eq(SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() + '.' + FccCentroCustos.Fields.CODIGO.toString(), codigoCentroCusto));
		criteria.add(Restrictions.or(codDebitaNotNull, codDebitaNull));

		if (seqAFContrato != null) {
			criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.AF_CONTRATO.toString() + '.' + ScoAfContrato.Fields.SEQ.toString(), seqAFContrato));
		} else {
			criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.AUTORIZACAO_FORNEC.toString() + '.' + ScoAutorizacaoForn.Fields.NUMERO.toString(),
					seqAutorizacaoFornecimento));
			criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.SERVICO.toString() + '.' + ScoServico.Fields.CODIGO.toString(), seqScoServico));
		}

		BigDecimal retorno = (BigDecimal) executeCriteriaUniqueResult(criteria);
		return retorno;

	}

	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarAutorizFornecServico(Integer tvdSeq) {

		StringBuilder hql = new StringBuilder(650);

		hql.append(" SELECT ASRV." ).append( SigAtividadeServicos.Fields.SEQ.toString() ).append( " as ATV_SERV_SEQ")
		.append(" , LCT." ).append( ScoLicitacao.Fields.DESCRICAO.toString() ).append( " as OBJETO_CONTRATO ")
		.append(" , AFN." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " as ITEM_CONTRATO ")
		.append(" , AFN." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString() ).append( " as COMPL_ITEM ")
		.append(" , FRN." ).append( ScoFornecedor.Fields.NOME_FANTASIA.toString() ).append( " as NOME_FANTASIA ")
		.append(" , LCT." ).append( ScoLicitacao.Fields.DT_INICIO_FORNECIMENTO.toString() ).append( " as DT_INICIO_VIGENCIA ")
		.append(" , AFN." ).append( ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString() ).append( " as DT_FIM_VIGENCIA ")
		.append(" , LCT." ).append( ScoLicitacao.Fields.TEMPO_ATENDIMENTO.toString() ).append( " as PRAZO_MESES ")
		.append(" , DIR." ).append( SigDirecionadores.Fields.NOME.toString() ).append( " as DIR_NOME ")
		.append(" , ASRV." ).append( SigAtividadeServicos.Fields.IND_SITUACAO.toString() ).append( " as IND_SITUACAO ")
		.append(" , SUM(coalesce(IPF." ).append( ScoItemPropostaFornecedor.Fields.QUANTIDADE.toString() ).append( ",1) * " ).append( "coalesce(IPF."
				).append( ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString() ).append( ",0) * " ).append( "coalesce(LCT." ).append( ScoLicitacao.Fields.FREQUENCIA_ENTREGA.toString()
				).append( ",1) " ).append( ") as VALOR_TOTAL_ITEM ")

		.append(" FROM " ).append( ScoAutorizacaoForn.class.getSimpleName() ).append( " AFN, ")
		.append(ScoItemAutorizacaoForn.class.getSimpleName() ).append( " IAFN, ")
		.append(ScoItemPropostaFornecedor.class.getSimpleName() ).append( " IPF, ")
		.append(ScoLicitacao.class.getSimpleName() ).append( " LCT, ")
		.append(SigAtividadeServicos.class.getSimpleName() ).append( " ASRV, ")
		.append(SigDirecionadores.class.getSimpleName() ).append( " DIR, ")
		.append(ScoFornecedor.class.getSimpleName() ).append( " FRN ")

		.append(" WHERE AFN." ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( " = IAFN." ).append( ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
		.append(" AND IAFN." ).append( ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString() ).append( '.' ).append( ScoItemPropostaFornecedor.Fields.NUMERO.toString()
				).append( " = IPF." ).append( ScoItemPropostaFornecedor.Fields.NUMERO.toString())
		.append(" AND IAFN." ).append( ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString() ).append( '.'
				).append( ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString() ).append( " = IPF."
				).append( ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString())
		.append(" AND IAFN." ).append( ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString() ).append( '.'
				).append( ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString() ).append( " = IPF."
				).append( ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString())
		.append(" AND AFN." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " = LCT." ).append( ScoLicitacao.Fields.NUMERO.toString())
		.append(" AND IAFN." ).append( ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString() ).append( " = ASRV."
				).append( SigAtividadeServicos.Fields.AUTORIZACAO_FORNEC.toString() ).append( '.' ).append( ScoAutorizacaoForn.Fields.NUMERO.toString())
		.append(" AND DIR." ).append( SigDirecionadores.Fields.SEQ.toString() ).append( " = ASRV." ).append( SigAtividadeServicos.Fields.DIRECIONADOR.toString() ).append( '.'
				).append( SigDirecionadores.Fields.SEQ.toString())
		.append(" AND ASRV." ).append( SigAtividadeServicos.Fields.ATIVIDADE_SEQ.toString() ).append( " = " ).append( tvdSeq)
		.append(" AND IPF." ).append( ScoItemPropostaFornecedor.Fields.FRN.toString() ).append( '.' ).append( ScoFornecedor.Fields.NUMERO.toString() ).append( " = FRN."
				).append( ScoFornecedor.Fields.NUMERO.toString())

		.append(" GROUP BY ASRV." ).append( SigAtividadeServicos.Fields.SEQ.toString())
		.append(" , LCT." ).append( ScoLicitacao.Fields.DESCRICAO.toString())
		.append(" , AFN." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
		.append(" , AFN." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
		.append(" , FRN." ).append( ScoFornecedor.Fields.NOME_FANTASIA.toString())
		.append(" , LCT." ).append( ScoLicitacao.Fields.DT_INICIO_FORNECIMENTO.toString())
		.append(" , AFN." ).append( ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString())
		.append(" , LCT." ).append( ScoLicitacao.Fields.TEMPO_ATENDIMENTO.toString())
		.append(" , DIR." ).append( SigDirecionadores.Fields.NOME.toString())
		.append(" , ASRV." ).append( SigAtividadeServicos.Fields.IND_SITUACAO.toString());

		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());

		return createQuery.list();
	}

	/**
	 * 
	 * Retorna todos os contratos associados em atividades de apoio 
	 * com produção no mês de competência do processamento.
	 * 
	 * @param pmuSeq - seq do processamento mensal sendo executado
	 * @return ScrollableResults - itens de retorno
	 * @author jgugel
	 */
	public ScrollableResults buscaItensContratoAlocadosAtividadesApoio(Integer pmuSeq) {
		StringBuilder sql = new StringBuilder(600);

		sql.append("SELECT ")
		.append(" tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( " as tvd_seq, ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.SEQ.toString() ).append( " as arv_seq, ")
		.append(" cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " as cmt_seq, ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( " as cct_codigo, ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( " as afco_seq, ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.AUTORIZACAO_FORNEC.toString() ).append( '.' ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( " as afn_numero, ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.SERVICO.toString() ).append( '.' ).append( ScoServico.Fields.CODIGO.toString() ).append( " as srv_codigo, ")
		.append(" cmt." ).append( SigCalculoComponente.Fields.DIRECIONADOR.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString() ).append( " as dir_seq_atividade, ")
		.append(" coalesce(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ", 1) as peso_oc ")

		.append(" FROM ")
		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj ")
		.append(" LEFT JOIN obj." ).append( SigObjetoCustos.Fields.OBJETO_CUSTO_PESO.toString() ).append( " ope, ")
		.append(SigObjetoCustoComposicoes.class.getSimpleName() ).append( " cbt, ")
		.append(SigAtividades.class.getSimpleName() ).append( " tvd, ")
		.append(SigAtividadeServicos.class.getSimpleName() ).append( " arv ")

		.append(" WHERE ")
		.append(" pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pmuSeq ")
		.append(" AND pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString()
				).append(' ')
		.append(" AND cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString()
				).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append(' ')
		.append(" AND cmt." ).append( SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO.toString() ).append( '.' ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString()
				).append( " = cbt." ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString() ).append(' ')
		.append(" AND cbt." ).append( SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString() ).append( '.' ).append( SigAtividades.Fields.SEQ.toString() ).append( " = tvd."
				).append( SigAtividades.Fields.SEQ.toString() ).append(' ')
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv."
				).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append(' ')
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj."
				).append( SigObjetoCustos.Fields.SEQ.toString() ).append(' ')
		.append(" AND tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( " = arv." ).append( SigAtividadeServicos.Fields.ATIVIDADE.toString() ).append( '.'
				).append( SigAtividades.Fields.SEQ.toString() ).append(' ')
		.append(" AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :dominioTipo ")

		.append(" GROUP BY ")
		.append(" tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.SEQ.toString() ).append( ", ")
		.append(" cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( ", ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.AUTORIZACAO_FORNEC.toString() ).append( '.' ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.SERVICO.toString() ).append( '.' ).append( ScoServico.Fields.CODIGO.toString() ).append( ", ")
		.append(" cmt." ).append( SigCalculoComponente.Fields.DIRECIONADOR.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString() ).append( ", ")
		.append(" coalesce(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ", 1) ")

		.append(" ORDER BY ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( ", ")
		.append(" arv." ).append( SigAtividadeServicos.Fields.AUTORIZACAO_FORNEC.toString() ).append( '.' ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append(' ');

		org.hibernate.Query query = createHibernateQuery(sql.toString());
		query.setInteger("pmuSeq", pmuSeq);
		query.setParameter("dominioTipo", DominioTipoObjetoCusto.AP);
		return query.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);

	}

}
