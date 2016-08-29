package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioVisaoCustoPaciente;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCalculoAtividadePessoa;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoPesos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProducaoObjetoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.ConsumoPacienteNodoVO;
import br.gov.mec.aghu.sig.custos.vo.PesquisarConsumoPacienteVO;
import br.gov.mec.aghu.sig.custos.vo.SomatorioAnaliseCustosObjetosCustoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseCustosObjetosCustoVO;

public class SigProcessamentoCustoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigProcessamentoCusto> {

	private static final long serialVersionUID = 8134735267590091828L;

	public List<SigProcessamentoCusto> pesquisarProcessamentoCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigProcessamentoCusto competencia, DominioSituacaoProcessamentoCusto situacao) {
		DetachedCriteria criteria = this.criarCriteriaPesquisaProcessamentoCusto(competencia, situacao, true);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, true);
	}

	public Long pesquisarProcessamentoCustoCount(SigProcessamentoCusto competencia, DominioSituacaoProcessamentoCusto situacao) {
		DetachedCriteria criteria = this.criarCriteriaPesquisaProcessamentoCusto(competencia, situacao, false);
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaPesquisaProcessamentoCusto(SigProcessamentoCusto competencia, DominioSituacaoProcessamentoCusto situacao,
			boolean utilizarOrder) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigProcessamentoCusto.class);

		if (competencia != null) {
			criteria.add(Restrictions.eq(SigProcessamentoCusto.Fields.SEQ.toString(), competencia.getSeq()));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), situacao));
		}

		if (utilizarOrder) {
			criteria.addOrder(Order.desc(SigProcessamentoCusto.Fields.COMPETENCIA.toString()));
		}
		return criteria;
	}

	public boolean verificarExistenciaProcessamentoPendente() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigProcessamentoCusto.class);

		criteria.add(Restrictions.or(
				Restrictions.eq(SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.A),
				Restrictions.eq(SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.E)));

		return this.executeCriteriaExists(criteria);
	}

	/**
	 * Busca todos os processamentos dado um ou mais situações como filtro.
	 * 
	 * @author rmalvezzi
	 * @param situacao		Lista de situações a ser incluida na restrição "in" no SQL.
	 * @return				Lista de Processamentos que satisfazem as restrições.
	 */
	public List<SigProcessamentoCusto> pesquisarCompetencia(DominioSituacaoProcessamentoCusto... situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigProcessamentoCusto.class);

		if (situacao != null && situacao.length > 0) {
			criteria.add(Restrictions.in(SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), situacao));
		}
		criteria.addOrder(Order.desc(SigProcessamentoCusto.Fields.COMPETENCIA.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Cria o HQL de Busca Custos visão de Objeto de Custos.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro pela Competencia (obrigatório). 
	 * @param fccCentroCustos			Filtro por Centro de Custo.
	 * @param nomeProdutoServico		Filtro pelo nome do OC.	
	 * @param sigCentroProducao			Filtro por Centro de Produção.
	 * @param seqObjetoCustoVersao		Filtro pelo Objeto Custo Versão.
	 * @return							Um objeto query contendo o sql para ser executado.
	 */
	private Query criarQueryVisualizarObjetoCusto(Integer seqCompetencia,  Integer codigoCentroCusto, String nomeProdutoServico,
			SigCentroProducao sigCentroProducao, Integer seqObjetoCustoVersao, Short seqPagador, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		StringBuilder sql = new StringBuilder(616);

		sql.append("SELECT ")
		.append("cto.").append(SigCentroProducao.Fields.SEQ.toString()).append(" as cp_seq, ")
		.append("cto.").append(SigCentroProducao.Fields.NOME.toString()).append(" as cp_nome, ")
		.append("fcc.").append(FccCentroCustos.Fields.CODIGO.toString()).append(" as cct_codigo, ")
		.append("fcc.").append(FccCentroCustos.Fields.DESCRICAO.toString()).append(" as cct_nome, ")
		.append("ocv.").append(SigObjetoCustoVersoes.Fields.SEQ.toString()).append(" as ocv_seq, ")
		.append("obj.").append(SigObjetoCustos.Fields.SEQ.toString()).append(" as obj_seq, ")
		.append("obj.").append(SigObjetoCustos.Fields.NOME.toString()).append(" as obj_nome, ")
		.append("obj.").append(SigObjetoCustos.Fields.IND_TIPO.toString()).append(" as obj_tipo, ")

		.append("cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_ATV_INSUMOS.toString()).append(" + ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_ATV_PESSOAL.toString()).append(" + ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_ATV_EQUIPAMENTO.toString()).append(" + ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_ATV_SERVICO.toString()).append(" + ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_RATEIO_INSUMOS.toString()).append(" + ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_RATEIO_PESSOAS.toString()).append(" + ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_RATEIO_EQUIPAMENTOS.toString()).append(" + ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_RATEIO_SERVICO.toString()).append(" as vlr_diretos, ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_RATEIO_INDIRETOS.toString()).append(" as vlr_indiretos, ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.QUANTIDADE_PRODUZIDA.toString()).append(" as prd_assistencial, ")
		.append("cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_OBJETO_CUSTO.toString()).append(" as vlr_objeto_custos, ")
		.append("pgd.").append(AacPagador.Fields.SEQ.toString()).append(" as seq_pagador, ")
		.append("pgd.").append(AacPagador.Fields.DESCRICAO.toString()).append(" as nome_pagador ")
		.append("FROM ")
		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ")
		.append("left join cbj.").append( SigCalculoObjetoCusto.Fields.PAGADOR ).append( " pgd, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " fcc ")
		.append("left join fcc." ).append( FccCentroCustos.Fields.CENTRO_PRODUCAO.toString() ).append( " cto ")
		.append("WHERE ")
		.append("pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqCompetencia ")
		.append(" and pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString())
		.append(" and cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() )
			.append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString())
		.append(" and ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() )
			.append( " = obj.").append( SigObjetoCustos.Fields.SEQ.toString())
		.append(" and cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() )
			.append( " = fcc.").append( FccCentroCustos.Fields.CODIGO.toString());

		if (codigoCentroCusto != null) {
			sql.append(" and fcc.").append(FccCentroCustos.Fields.CODIGO.toString()).append(" = :codigoCentroCusto ");
		}

		if (StringUtils.isNotBlank(nomeProdutoServico)) {
			sql.append(" and upper(obj.").append(SigObjetoCustos.Fields.NOME.toString()).append(") like \'%").append(nomeProdutoServico.toUpperCase()).append("%\'");
		}

		if (sigCentroProducao != null) {
			sql.append(" and cto.").append(SigCentroProducao.Fields.SEQ.toString()).append(" = ").append(sigCentroProducao.getSeq());
		}

		if (seqObjetoCustoVersao != null) {
			sql.append(" and ocv.").append(SigObjetoCustoVersoes.Fields.SEQ.toString()).append(" = :seqObjetoCustoVersao ");
		}
		
		if (seqPagador != null) {
			if(seqPagador.intValue() > 0){
				sql.append(" and pgd.").append(AacPagador.Fields.SEQ.toString()).append(" = :seqPagador ");
			}
		}
		else{
			sql.append(" and pgd.").append(AacPagador.Fields.SEQ.toString()).append(" is null ");
		}
		
		if(tiposCentroProducao != null && tiposCentroProducao.length > 0){
			sql.append(" and cto.").append(SigCentroProducao.Fields.IND_TIPO.toString()).append(" in (:tiposCentroProducao )");
		}

		sql.append(" ORDER BY cto.").append(SigCentroProducao.Fields.NOME).append(", fcc.").append(FccCentroCustos.Fields.DESCRICAO);
		sql.append(", obj.").append(SigObjetoCustos.Fields.NOME);

		Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		
		this.definirParametrosConsulta(createQuery, codigoCentroCusto, seqObjetoCustoVersao, seqPagador, tiposCentroProducao);
		
		return createQuery;
	}

	private void definirParametrosConsulta(Query createQuery, Integer codigoCentroCusto, Integer seqObjetoCustoVersao, Short seqPagador, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		if (codigoCentroCusto != null) {
			createQuery.setParameter("codigoCentroCusto", codigoCentroCusto);
		}
		
		if (seqObjetoCustoVersao != null) {
			createQuery.setParameter("seqObjetoCustoVersao", seqObjetoCustoVersao);
		}
		
		if (seqPagador != null && seqPagador.intValue() > 0) {
			createQuery.setParameter("seqPagador", seqPagador);
		}
		
		if(tiposCentroProducao != null && tiposCentroProducao.length > 0){
			createQuery.setParameterList("tiposCentroProducao", tiposCentroProducao);
		}
	}

	/**
	 * Cria o HQL de Busca Custos visão de Centros de Custos.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia		Filtro pela Competencia (obrigatório). 
	 * @param fccCentroCustos		Filtro por Centro de Custo.
	 * @param sigCentroProducao		Filtro por Centro de Produção.
	 * @return						Um objeto query contendo o sql para ser executado.
	 */
	private Query criarQueryVisualizarCentroCusto(Integer seqCompetencia, FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao, boolean ordenar) {
		StringBuilder sql = new StringBuilder(900);

		sql.append("SELECT ")
		
		.append("cto.").append(SigCentroProducao.Fields.SEQ.toString()).append(" as co_seq, ")
		.append("cto.").append(SigCentroProducao.Fields.NOME.toString()).append(" as cp_nome, ")
		.append("fcc.").append(FccCentroCustos.Fields.CODIGO.toString()).append(" as cct_codigo, ")
		.append("fcc.").append(FccCentroCustos.Fields.DESCRICAO.toString()).append(" as cct_nome, ")

		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DI' then mvt.")
				.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_isumos, ")

		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DP' then mvt.")
				.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_pessoas, ")

		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DE' then mvt.")
				.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_equipamentos, ")

		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DS' then mvt.")
				.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_servicoes, ")

		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'II' then mvt.")
		.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_indiretos_insumos, ")

		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'IP' then mvt.")
		.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_indiretos_pessoas, ")
		
		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'IE' then mvt.")
		.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_indiretos_equipamentos, ")
		
		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'IS' then mvt.")
		.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_indiretos_servicos, ") 	

		.append("sum (mvt.").append(SigMvtoContaMensal.Fields.VALOR.toString()).append(") as total ")

		.append(" FROM ")

		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " mvt, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " fcc ")
		.append("left join fcc." ).append( FccCentroCustos.Fields.CENTRO_PRODUCAO.toString() ).append( " cto ")

		.append(" WHERE ")

		.append("pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqCompetencia ")
		.append(" and pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = mvt." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString())
		.append('.').append(SigProcessamentoCusto.Fields.SEQ.toString())

		.append(" and mvt." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = fcc."
				).append( FccCentroCustos.Fields.CODIGO.toString())
		.append(" and mvt." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " in (:tipoMovimentos) ");

		if (fccCentroCustos != null) {
			sql.append(" and fcc.").append(FccCentroCustos.Fields.CODIGO.toString()).append(" = ").append(fccCentroCustos.getCodigo());
		}

		if (sigCentroProducao != null) {
			sql.append(" and cto.").append(SigCentroProducao.Fields.SEQ.toString()).append(" = ").append(sigCentroProducao.getSeq());
		}
		
		if(tiposCentroProducao != null && tiposCentroProducao.length > 0){
			sql.append(" and cto.").append(SigCentroProducao.Fields.IND_TIPO.toString()).append(" in (:tiposCentroProducao )");
		}

		sql.append(" GROUP BY ").append("cto.").append(SigCentroProducao.Fields.SEQ.toString()).append(", cto.").append(SigCentroProducao.Fields.NOME.toString()).append(", fcc.").append(FccCentroCustos.Fields.CODIGO.toString())
				.append(", fcc.").append(FccCentroCustos.Fields.DESCRICAO.toString());
				
		
		if(ordenar){		
			sql.append(" ORDER BY cto.").append(SigCentroProducao.Fields.NOME).append(", fcc.").append(FccCentroCustos.Fields.DESCRICAO);
		}

		Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameterList("tipoMovimentos", new DominioTipoMovimentoConta[] { DominioTipoMovimentoConta.SIP, DominioTipoMovimentoConta.SIT });
		
		if(tiposCentroProducao != null && tiposCentroProducao.length > 0){
			createQuery.setParameterList("tiposCentroProducao", tiposCentroProducao);
		}
		
		return createQuery;
	}

	/**
	 * Executa a consulta Busca Custos visão de Objeto de Custos com paginação.	
	 * 
	 * @author rmalvezzi
	 * @param firstResult				Primeira posição (para a paginação).
	 * @param maxResult					Tamanho do retorno (para a paginação).	
	 * @param seqCompetencia			Filtro pela Competencia (obrigatório). 
	 * @param fccCentroCustos			Filtro por Centro de Custo.
	 * @param nomeProdutoServico		Filtro pelo nome do OC.	
	 * @param sigCentroProducao			Filtro por Centro de Produção.
	 * @return							Lista de VisualizarAnaliseCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseCustosObjetosCustoVO> buscarCustosVisaoObjetoCustos(final Integer firstResult, final Integer maxResult,
		Integer seqCompetencia, Integer codigoCentroCusto, String nomeProdutoServico, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		Query createQueryVisualizar = this.criarQueryVisualizarObjetoCusto(seqCompetencia, codigoCentroCusto, nomeProdutoServico, sigCentroProducao, null, (short) 0, tiposCentroProducao);
		createQueryVisualizar.setFirstResult(firstResult);
		createQueryVisualizar.setMaxResults(maxResult);
		List<Object[]> valores = createQueryVisualizar.list();
		List<VisualizarAnaliseCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseCustosObjetosCustoVO.createObjetoCusto(objects));
			}
		}
		return lstRetorno;
	}

	/**
	 * Executa a consulta Busca Custos visão de Objeto de Custos e retorna o tamanho dela.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro pela Competencia (obrigatório). 
	 * @param fccCentroCustos			Filtro por Centro de Custo.
	 * @param nomeProdutoServico		Filtro pelo nome do OC.	
	 * @param sigCentroProducao			Filtro por Centro de Produção.
	 * @return							Tamanho da Lista.
	 */
	public Long buscarCustosVisaoObjetoCustosCount(Integer seqCompetencia, Integer codigoCentroCusto,  String nomeProdutoServico,
			SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return (long)this.criarQueryVisualizarObjetoCusto(seqCompetencia, codigoCentroCusto, nomeProdutoServico, sigCentroProducao, null, (short) 0, tiposCentroProducao).list().size();
	}

	/**
	 * Executa a consulta Busca Custos visão de Centros de Custos com paginação.
	 * 
	 * @author rmalvezzi
	 * @param firstResult				Primeira posição (para a paginação).
	 * @param maxResult					Tamanho do retorno (para a paginação).	
	 * @param seqCompetencia			Filtro pela Competencia (obrigatório).
	 * @param fccCentroCustos			Filtro por Centro de Custo.
	 * @param sigCentroProducao			Filtro por Centro de Produção.
	 * @return							Lista de VisualizarAnaliseCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseCustosObjetosCustoVO> buscarCustosVisaoCentroCustos(Integer firstResult, Integer maxResult, Integer seqCompetencia,
			FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {

		Query createQueryVisualizar = this.criarQueryVisualizarCentroCusto(seqCompetencia, fccCentroCustos, sigCentroProducao, tiposCentroProducao, true);
		createQueryVisualizar.setFirstResult(firstResult);
		createQueryVisualizar.setMaxResults(maxResult);
		List<Object[]> valores = createQueryVisualizar.list();
		List<VisualizarAnaliseCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseCustosObjetosCustoVO.createCentroCusto(objects));
			}
		}
		return lstRetorno;
	}
	
	/**
	 * Executa a consulta Busca Custos visão de Centros de Custos com paginação.
	 * 
	 * @author rmalvezzi
	 * @param firstResult				Primeira posição (para a paginação).
	 * @param maxResult					Tamanho do retorno (para a paginação).	
	 * @param seqCompetencia			Filtro pela Competencia (obrigatório).
	 * @param fccCentroCustos			Filtro por Centro de Custo.
	 * @param sigCentroProducao			Filtro por Centro de Produção.
	 * @return							Lista de VisualizarAnaliseCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseCustosObjetosCustoVO> buscarCustosVisaoCentroCustosOtimizacao(Integer seqCompetencia, FccCentroCustos fccCentroCustos, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {

		Query createQueryVisualizar = this.criarQueryVisualizarCentroCusto(seqCompetencia, fccCentroCustos, null, tiposCentroProducao, true);
		List<Object[]> valores = createQueryVisualizar.list();
		List<VisualizarAnaliseCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseCustosObjetosCustoVO.createCentroCusto(objects));
			}
		}
		return lstRetorno;
	}
	
	/**
	 * Executa a consulta Busca Custos visão de Centros de Custos e retorna o tamanho dela.	
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro pela Competencia.
	 * @param fccCentroCustos			Filtro por Centro de Custo.
	 * @param sigCentroProducao			Filtro por Centro de Produção.
	 * @return							VisualizarAnaliseCustosObjetosCustoVO para a tela de detalhe.
	 */
	public Long buscarCustosVisaoCentroCustosCount(Integer seqCompetencia, FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		return (long)this.criarQueryVisualizarCentroCusto(seqCompetencia, fccCentroCustos, sigCentroProducao,tiposCentroProducao, false).list().size();	
	}

	/**
	 * Obtem um VisualizarAnaliseCustosObjetosCustoVO relacioado ao filtros passados por parametro para a Visão Objeto Custo.	
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro pela Competencia. 
	 * @param seqObjetoCustoVersao		Filtro pelo Objeto Custo Versão.
	 * @return							VisualizarAnaliseCustosObjetosCustoVO para a tela de detalhe.
	 */
	public VisualizarAnaliseCustosObjetosCustoVO obterDetalheVisualizacaoAnaliseOC(Integer seqCompetencia, Integer seqObjetoCustoVersao, Integer codigoCentroCusto, Short seqPagador) {
		Query createQueryVisualizar = this.criarQueryVisualizarObjetoCusto(seqCompetencia, codigoCentroCusto, null, null, seqObjetoCustoVersao, seqPagador, null);
		Object[] valores = (Object[]) createQueryVisualizar.uniqueResult();

		if (valores != null) {
			return VisualizarAnaliseCustosObjetosCustoVO.createObjetoCusto(valores);
		}
		return null;
	}

	/**
	 * Obtem um VisualizarAnaliseCustosObjetosCustoVO relacioado ao filtros passados por parametro para a Visão Centro Custo.	
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro pela Competencia. 
	 * @param fccCentroCustos			Filtro pelo Centro de Custo.
	 * @return							VisualizarAnaliseCustosObjetosCustoVO para a tela de detalhe.	
	 */
	public VisualizarAnaliseCustosObjetosCustoVO obterDetalheVisualizacaoAnaliseCC(Integer seqCompetencia, FccCentroCustos fccCentroCustos) {
		Query createQueryVisualizar = this.criarQueryVisualizarCentroCusto(seqCompetencia, fccCentroCustos, null, null, true);
		Object[] valores = (Object[]) createQueryVisualizar.uniqueResult();

		if (valores != null) {
			return VisualizarAnaliseCustosObjetosCustoVO.createCentroCusto(valores);
		}
		return null;
	}

	public List<SigProcessamentoCusto> obterSigProcessamentoCustoCompetencia(Date dataCompetencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigProcessamentoCusto.class);
		criteria.add(Restrictions.eq(SigProcessamentoCusto.Fields.COMPETENCIA.toString(), dataCompetencia));
		return executeCriteria(criteria);
	}

	/**
	 * Busca o consumo de insumos de cada centro de custo no mês de competência do processamento.
	 * Os parâmetros passados são utilizados para calcular o valor final efetivado de um material para um centro de custo, 
	 * que é a soma de todos os movimentos relativos a RMs efetivadas para o centro de custo menos a soma de todos os movimentos 
	 * relativos a devoluções feitas pelo CC ao estoque. 
	 * 
	 * @author rmalvezzi
	 * @param tipoMovimentoConta					SIT -> Fornecedor não HCPA, SIP -> Fornecedor somente HCPA.
	 * @param parametroMvtoRequisicaoMaterial		Tipo de movimento de estoque que representa o documento de requisição de material efetivada (RM).
	 * @param parametroMvtoDevolucaoAlmoxarifado	Tipo de movimento de estoque que representa o documento de devolução ao almoxarifado (DA).
	 * @param processamentoCusto					Processamento Atual
	 * @return										Retorna o consumo de insumos de cada centro de custo no mês de competência do processamento.
	 */
	public ScrollableResults buscarConsumoInsumoMvtoContaMensais(DominioTipoMovimentoConta tipoMovimentoConta, BigDecimal parametroMvtoRequisicaoMaterial,
			BigDecimal parametroMvtoDevolucaoAlmoxarifado, SigProcessamentoCusto processamentoCusto) {

		String sinalComparacao = null;
		switch (tipoMovimentoConta) {
		case SIT:
			sinalComparacao = " <> "; // Fornecedor Não HCPA
			break;
		case SIP:
			sinalComparacao = " = "; // Fornecedor Somente HCPA
			break;
		}

		StringBuilder hql = new StringBuilder(800);
		hql.append("SELECT ")
		.append("mat." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( ", ") // mat_codigo
		.append("mmt." ).append( SceMovimentoMaterial.Fields.CENTRO_CUSTO_CODIGO.toString() ).append( ", ") // cct_codigo
		.append("mmt." ).append( SceMovimentoMaterial.Fields.CENTRO_CUSTO_REQUISITA_CODIGO.toString() ).append( ", ") //cct_codigo_requisita		
		.append("mat." ).append( ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString() ).append( ", ") // umd_codigo
		.append("coalesce(mmt." ).append( SceMovimentoMaterial.Fields.CUSTO_MEDIO_PONDERADOR_GER.toString() ).append( ",0), ") // custo_medio
		.append("sum (case mmt." ).append( SceMovimentoMaterial.Fields.IND_ESTORNO.toString() ).append( ' ')
		.append("when 'N' then ")
		.append("(case mmt." ).append( SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString() ).append( ' ')
		.append("when " ).append( parametroMvtoRequisicaoMaterial.toString() ).append( " then ")
		.append("coalesce(mmt." ).append( SceMovimentoMaterial.Fields.QUANTIDADE.toString() ).append( ",0) ")
		.append("when " ).append( parametroMvtoDevolucaoAlmoxarifado.toString() ).append( " then ")
		.append("(coalesce(mmt." ).append( SceMovimentoMaterial.Fields.QUANTIDADE.toString() ).append( ",0) * -1) ")
		.append("else 0 end) ")
		.append("else 0 end), ") // qtde
		.append("sum (case mmt." ).append( SceMovimentoMaterial.Fields.IND_ESTORNO.toString() ).append( ' ')
		.append("when 'N' then ")
		.append("(case mmt." ).append( SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString() ).append( ' ')
		.append("when " ).append( parametroMvtoRequisicaoMaterial.toString() ).append( " then ")
		.append("coalesce(mmt." ).append( SceMovimentoMaterial.Fields.VALOR.toString() ).append( ",0) ")
		.append("when " ).append( parametroMvtoDevolucaoAlmoxarifado.toString() ).append( " then ")
		.append("(coalesce(mmt." ).append( SceMovimentoMaterial.Fields.VALOR.toString() ).append( ",0) * -1) ")
		.append("else 0 end) ")
		.append("else 0 end) ")
		.append("- ")
		.append("sum (case mmt." ).append( SceMovimentoMaterial.Fields.IND_ESTORNO.toString() ).append( ' ')
		.append("when 'S' then ")
		.append("(case mmt." ).append( SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString() ).append( ' ')
		.append("when " ).append( parametroMvtoRequisicaoMaterial.toString() ).append( " then ")
		.append("coalesce(mmt." ).append( SceMovimentoMaterial.Fields.VALOR.toString() ).append( ",0) ")
		.append("when " ).append( parametroMvtoDevolucaoAlmoxarifado.toString() ).append( " then ")
		.append("(coalesce(mmt." ).append( SceMovimentoMaterial.Fields.VALOR.toString() ).append( ",0) * -1) ")
		.append("else 0 end) ")
		.append("else 0 end) ")// valor
		.append("FROM ")
		.append(SceEstoqueGeral.class.getSimpleName() ).append( " egr, ")
		.append(SceMovimentoMaterial.class.getSimpleName() ).append( " mmt, ")
		.append(ScoMaterial.class.getSimpleName() ).append( " mat ")
		.append("WHERE ")
		.append("mmt." ).append( SceMovimentoMaterial.Fields.DATA_COMPETENCIA.toString() ).append( " between :DataInicioProcessamento and :DataFimProcessamento  ")
		.append("and mmt." ).append( SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString() ).append( " in (" ).append( parametroMvtoRequisicaoMaterial ).append(',')
		.append( parametroMvtoDevolucaoAlmoxarifado ).append( ") ")
		.append("and mat." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " = mmt." ).append( SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString() ).append( ' ')
		.append("and mmt." ).append( SceMovimentoMaterial.Fields.FORNECEDOR_NUMERO.toString() ).append( ' ' ).append( sinalComparacao ).append( " 1 ")
		.append("and egr." ).append( SceEstoqueGeral.Fields.MAT_CODIGO.toString() ).append( " = mmt." ).append( SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString() ).append( ' ')
		.append("and egr." ).append( SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString() ).append( " between :DataInicioProcessamento and :DataFimProcessamento ")
		.append("and egr." ).append( SceEstoqueGeral.Fields.FRN_NUMERO.toString() ).append( ' ' ).append( sinalComparacao ).append( " 1 ")
		.append("and egr." ).append( SceEstoqueGeral.Fields.FRN_NUMERO.toString() ).append( " = mmt." ).append( SceMovimentoMaterial.Fields.FORNECEDOR_NUMERO.toString() ).append( ' ')
		.append("GROUP BY ")

		.append("mat." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( ", ") // mat_codigo
		.append("mmt." ).append( SceMovimentoMaterial.Fields.CENTRO_CUSTO_CODIGO.toString() ).append( ", ") // cct_codigo
		.append("mmt." ).append( SceMovimentoMaterial.Fields.CENTRO_CUSTO_REQUISITA_CODIGO.toString() ).append( ", ") //cct_codigo_requisita	
		.append("mat." ).append( ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO ).append( ", ") // umd_codigo
		.append("coalesce(mmt." ).append( SceMovimentoMaterial.Fields.CUSTO_MEDIO_PONDERADOR_GER.toString() ).append( ",0) "); // custo_medio

		org.hibernate.Query query = this.createHibernateQuery(hql.toString());
		query.setDate("DataInicioProcessamento", processamentoCusto.getDataInicio());
		query.setDate("DataFimProcessamento", processamentoCusto.getDataFim());
		return query.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}

	/**
	 * Busca todos os grupos de ocupação associados em atividades de objetos de custos com produção no mês de competência do processamento, 
	 * que não tinham quantidade específica ou que tinham quantidade específica e sobrou valores para debitar (disponibilidade).
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamento		Seq do processamento atual.
	 * @return						Retorna todos os grupos de ocupação ordenados por centro de custo e código do grupo de ocupação.
	 */
	public ScrollableResults buscarGruposOcupacaoAlocadosAtividades(Integer seqProcessamento) {
		StringBuilder hql = new StringBuilder(600);

		hql.append("SELECT ")
		.append("tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( ", ")
		.append("avp." ).append( SigAtividadePessoas.Fields.SEQ.toString() ).append( ", ")
		.append("cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( ", ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.SEQ.toString() ).append( ", ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append("avp." ).append( SigAtividadePessoas.Fields.GRUPO_OCUPACAO.toString() ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ.toString() ).append( ", ")
		.append("cmt." ).append( SigCalculoComponente.Fields.DIRECIONADOR.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString() ).append( ", ")
		.append("COALESCE(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( " , 1 ) * SUM(coalesce(dhp." ).append( SigDetalheProducao.Fields.QTDE.toString()
				).append( ",1)) ")

		.append("FROM " ).append( SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ")
		.append("LEFT JOIN cbj." ).append( SigCalculoObjetoCusto.Fields.LISTA_PRODUCAO_OBJETO_CUSTO.toString() ).append( " pjc ")
		.append("LEFT JOIN pjc." ).append( SigProducaoObjetoCusto.Fields.DETALHE_PRODUCAO.toString() ).append( " dhp, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt LEFT JOIN cmt." ).append( SigCalculoComponente.Fields.LISTA_CALCULO_ATIVIDADE_PESSOAS.toString()
				).append( " cap , ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj LEFT JOIN obj." ).append( SigObjetoCustos.Fields.OBJETO_CUSTO_PESO.toString() ).append( " ope, ")
		.append(SigObjetoCustoComposicoes.class.getSimpleName() ).append( " cbt, ")
		.append(SigAtividades.class.getSimpleName() ).append( " tvd, ")
		.append(SigAtividadePessoas.class.getSimpleName() ).append( " avp ")

		.append("WHERE pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqProcessamento ")
		.append("AND pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString()
				).append( ' ')
		.append("AND cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString()
				).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ' ')
		.append("AND cmt." ).append( SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO.toString() ).append( '.' ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString()
				).append( " = cbt." ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString() ).append( ' ')
		.append("AND cbt." ).append( SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString() ).append( '.' ).append( SigAtividades.Fields.SEQ.toString() ).append( " = tvd."
				).append( SigAtividades.Fields.SEQ.toString() ).append( ' ')
		.append("AND cbj." ).append( SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES.toString() ).append( " = ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ.toString()
				).append( ' ')
		.append("AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj."
				).append( SigObjetoCustos.Fields.SEQ.toString() ).append( ' ')
		.append("AND tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( " = avp." ).append( SigAtividadePessoas.Fields.ATIVIDADE.toString() ).append( '.'
				).append( SigAtividades.Fields.SEQ.toString() ).append( ' ')
		.append("AND (dhp." ).append( SigDetalheProducao.Fields.GRUPO.toString() ).append( " in (:grupos) or dhp."
				).append( SigDetalheProducao.Fields.GRUPO.toString() ).append( " is null) ")
		.append("AND avp." ).append( SigAtividadePessoas.Fields.QUANTIDADE.toString() ).append( " IS NULL ")
		.append("AND avp." ).append( SigAtividadePessoas.Fields.TEMPO.toString() ).append( " IS NULL ")
		.append("AND (cap." ).append( SigCalculoAtividadePessoa.Fields.QUANTIDADE_PREVISTA.toString() ).append( " = cap."
				).append( SigCalculoAtividadePessoa.Fields.QUANTIDADE_REALIZADA.toString() ).append( " OR cap."
				).append( SigCalculoAtividadePessoa.Fields.QUANTIDADE_PREVISTA.toString() ).append( " IS NULL) ")

		.append("GROUP BY tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( ", ")
		.append("avp." ).append( SigAtividadePessoas.Fields.SEQ.toString() ).append( ", ")
		.append("cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( ", ")
		.append("cap." ).append( SigCalculoAtividadePessoa.Fields.SEQ.toString() ).append( ", ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append("avp." ).append( SigAtividadePessoas.Fields.GRUPO_OCUPACAO.toString() ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ.toString() ).append( ", ")
		.append("cmt." ).append( SigCalculoComponente.Fields.DIRECIONADOR.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString() ).append( ", ")
		.append("ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ' ')
		.append("ORDER BY 5, 6 ");

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqProcessamento", seqProcessamento);
		query.setParameterList("grupos", new DominioGrupoDetalheProducao[]{ DominioGrupoDetalheProducao.PHI, DominioGrupoDetalheProducao.PAC});

		return query.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}

	/**
	 * Buscar os custos relativos a serviços do mês que atuam sobre a tabela de notas de recebimentos selecionando apenas aquelas 
	 * relacionadas a solicitações de serviço
	 * 
	 * @author rmalvezzi
	 * @param processamentoCusto			Processamento Atual.
	 * @return								ScrollableResults com a lista dos custos relativos a serviços.			
	 */
	public ScrollableResults buscarDebitoServicos(SigProcessamentoCusto processamentoCusto) {

		StringBuilder sql = new StringBuilder(500);

		sql.append(" SELECT ")
				.append("  sls." ).append( ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " as cct_codigo ")
				.append(" ,sls." ).append( ScoSolicitacaoServico.Fields.CCT_CODIGO_APLICADA.toString() ).append( " as cct_codigo_aplic ")
				.append(" ,sls." ).append( ScoSolicitacaoServico.Fields.NUMERO.toString() ).append( " as sls_numero ")
				.append(" ,sls." ).append( ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString() ).append( " as srv_codigo ")
				.append(" ,inr." ).append( SceItemNotaRecebimento.Fields.IAF_AFN_NUMERO.toString() ).append( " as iaf_afn_numero ")
				.append(" ,inr." ).append( SceItemNotaRecebimento.Fields.IAF_NUMERO.toString() ).append( " as iaf_numero ")
				.append(" ,SUM(inr." ).append( SceItemNotaRecebimento.Fields.VALOR.toString() ).append( ") as valor ")

		.append(" FROM ").append(ScoSolicitacaoServico.class.getSimpleName() ).append( " sls ,").append(ScoFaseSolicitacao.class.getSimpleName() ).append( " fsc ,")
				.append(SceItemNotaRecebimento.class.getSimpleName() ).append( " inr ,").append(SceNotaRecebimento.class.getSimpleName() ).append( " nrs  ")
				.append(" left outer join nrs." ).append( SceNotaRecebimento.Fields.TITULO.toString() ).append( " ttl ")

		.append(" WHERE ")
				.append("     nrs." ).append( SceNotaRecebimento.Fields.DATA_GERACAO.toString() ).append( " between :iniProc and :fimProc ")
				.append(" and inr." ).append( SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString() ).append( '.' ).append( SceNotaRecebimento.Fields.NUMERO_NR.toString())
				.append(" = nrs." ).append( SceNotaRecebimento.Fields.NUMERO_NR.toString())
				.append(" and fsc." ).append( ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString() ).append( " = inr." ).append( SceItemNotaRecebimento.Fields.IAF_AFN_NUMERO.toString())
				.append(" and fsc." ).append( ScoFaseSolicitacao.Fields.IAF_NUMERO.toString() ).append( " = inr." ).append( SceItemNotaRecebimento.Fields.IAF_NUMERO.toString())
				.append(" and sls." ).append( ScoSolicitacaoServico.Fields.NUMERO.toString() ).append( " = fsc." ).append( ScoFaseSolicitacao.Fields.SLS_NUMERO.toString())
				.append(" and nrs." ).append( SceNotaRecebimento.Fields.IND_ESTORNO.toString() ).append( " = 'N' ")
				.append(" and ttl." ).append( FcpTitulo.Fields.IND_ESTORNO.toString() ).append( " = 'N' ")
				.append(" and fsc." ).append( ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString() ).append( " = 'N' ")

		.append(" GROUP BY ").append("  sls." ).append( ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString())
				.append(", sls." ).append( ScoSolicitacaoServico.Fields.CCT_CODIGO_APLICADA.toString())
				.append(", sls." ).append( ScoSolicitacaoServico.Fields.NUMERO.toString()).append(", sls." ).append( ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString())
				.append(", inr." ).append( SceItemNotaRecebimento.Fields.IAF_AFN_NUMERO.toString())
				.append(", inr." ).append( SceItemNotaRecebimento.Fields.IAF_NUMERO.toString())

		.append(" ORDER BY 1, 3 ");

		org.hibernate.Query query = createHibernateQuery(sql.toString());
		query.setParameter("iniProc", processamentoCusto.getDataInicio());
		query.setParameter("fimProc", processamentoCusto.getDataFim());

		return query.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}

	public List<SigProcessamentoCusto> pesquisarCompetenciaSemProducao(SigObjetoCustoVersoes objetoCustoVersao, SigDirecionadores direcionador) {

		//Só pode retornar algum valor quando for selecionado os dois parâmetros
		if (objetoCustoVersao == null || direcionador == null) {
			return new ArrayList<SigProcessamentoCusto>();
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SigProcessamentoCusto.class, "proc");

		DetachedCriteria subQuery = DetachedCriteria.forClass(SigDetalheProducao.class, "prod");
		subQuery.add(Restrictions.eqProperty("prod." + SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO_SEQ.toString(),
				"proc." + SigProcessamentoCusto.Fields.SEQ.toString()));
		subQuery.add(Restrictions.eq("prod." + SigDetalheProducao.Fields.OBJETO_CUSTO_VERSAO.toString(), objetoCustoVersao));
		subQuery.add(Restrictions.eq("prod." + SigDetalheProducao.Fields.DIRECIONADOR.toString(), direcionador));
		subQuery.setProjection(Projections.property("prod." + SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO_SEQ.toString()));

		criteria.add(Subqueries.notExists(subQuery));

		//competência do processamento não pode estar na situação Em processamento, Processado ou Fechado.
		criteria.add(Restrictions.not(Restrictions.in("proc." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(),
				new DominioSituacaoProcessamentoCusto[] { DominioSituacaoProcessamentoCusto.A, DominioSituacaoProcessamentoCusto.P,
						DominioSituacaoProcessamentoCusto.F })));

		criteria.addOrder(Order.desc("proc." + SigProcessamentoCusto.Fields.COMPETENCIA.toString()));

		return executeCriteria(criteria);
	}
	

	/**
	 * Filtra SigProcessamentoCusto por situação(ões) passada por parametro. 
	 * Se o parametro campoOrderBy não for nulo ou em branco é adicionado 'order by' na consulta acordando com o parametro. 
	 * 
	 * @param situacao
	 * @param campoOrderBy
	 */
	public List<SigProcessamentoCusto> obterSigProcessamentoCustoPorSituacao(DominioSituacaoProcessamentoCusto situacao[], String campoOrderBy){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigProcessamentoCusto.class, "proc");
		
		criteria.add(Restrictions.in("proc." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(),
				situacao));
		
		if(StringUtils.isNotBlank(campoOrderBy)){
			criteria.addOrder(Order.desc("proc." + campoOrderBy));
		}
		
		return executeCriteria(criteria);
	}
	
	public List<ConsumoPacienteNodoVO> pesquisarConsumoPaciente(PesquisarConsumoPacienteVO vo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		criteria.createAlias("atd."+AghAtendimentos.Fields.SIG_CALCULO_ATD_PACIENTE.toString(), "cac");
		criteria.createAlias("cac."+SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cpp."+SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca");
		criteria.createAlias("cca."+SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cac."+SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu");
		
		criteria.setProjection(
				Projections.projectionList()
				.add(Projections.distinct(Projections.property("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO_SEQ.toString())), ConsumoPacienteNodoVO.Fields.ATD_SEQ.toString())
				.add(Projections.property("atd." + AghAtendimentos.Fields.DTHR_INICIO.toString()),ConsumoPacienteNodoVO.Fields.DTH_INICIO.toString())
				.add(Projections.property("atd." + AghAtendimentos.Fields.DTHR_FIM.toString()), ConsumoPacienteNodoVO.Fields.DTH_FIM.toString())
				.add(Projections.property("atd." + AghAtendimentos.Fields.PRONTUARIO.toString()), ConsumoPacienteNodoVO.Fields.PRONTUARIO.toString())
                .add(Projections.property("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()), ConsumoPacienteNodoVO.Fields.SEQ_CATEGORIA.toString())
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()), ConsumoPacienteNodoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()), ConsumoPacienteNodoVO.Fields.ORDEM_VISUALIZACAO.toString())
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.IND_CONTAGEM.toString()), ConsumoPacienteNodoVO.Fields.IND_CONTAGEM.toString())
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()), ConsumoPacienteNodoVO.Fields.AGRUPADOR.toString())
		);
		
		criteria.add(Restrictions.or(Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		
		criteria.add(Restrictions.in("atd."+AghAtendimentos.Fields.PAC_CODIGO.toString(), vo.getCodigoPacientes()));
		
		criteria.add(Restrictions.in("pmu."+SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(),new DominioSituacaoProcessamentoCusto[] {DominioSituacaoProcessamentoCusto.P,
			DominioSituacaoProcessamentoCusto.F} ));
	
		if(DominioVisaoCustoPaciente.COMPETENCIA == vo.getVisao()){
			criteria.add(Restrictions.eq("pmu."+SigProcessamentoCusto.Fields.SEQ.toString(), vo.getProcessoCusto().getSeq()));
			if(vo.getPacienteEmAlta()==null || !vo.getPacienteEmAlta()){
				criteria.add(Restrictions.or(Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I), Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
			} else {
				Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA);
				criteria.add(Restrictions.isNotNull("atd."+AghAtendimentos.Fields.DTHR_FIM.toString()));
				criteria.add(Restrictions.between("atd."+AghAtendimentos.Fields.DTHR_FIM.toString(), vo.getProcessoCusto().getDataInicio(), vo.getProcessoCusto().getDataFim()));
			}
		} 
		
		criteria.add(Restrictions.eq("ctc."+SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()));
		criteria.addOrder(Order.asc("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO_SEQ.toString()));
		criteria.addOrder(Order.asc("atd." + AghAtendimentos.Fields.DTHR_INICIO.toString()));
		criteria.addOrder(Order.asc("atd." + AghAtendimentos.Fields.DTHR_FIM.toString()));
		criteria.addOrder(Order.asc("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("ctc." + SigCategoriaConsumos.Fields.IND_CONTAGEM.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ConsumoPacienteNodoVO.class));
		
		return executeCriteria(criteria);
	}
	
	 /**
     * Cria o HQL de Busca Custos visão de Objeto de Custos.
     * 
     * @author rmalvezzi
     * @param seqCompetencia            Filtro pela Competencia (obrigatório). 
     * @param fccCentroCustos            Filtro por Centro de Custo.
     * @param nomeProdutoServico        Filtro pelo nome do OC.    
     * @param sigCentroProducao            Filtro por Centro de Produção.
     * @param seqObjetoCustoVersao        Filtro pelo Objeto Custo Versão.
     * @return                            Um objeto query contendo o sql para ser executado.
     */
    public SomatorioAnaliseCustosObjetosCustoVO obterSomatorioVisualizarObjetoCusto(Integer seqCompetencia,  Integer codigoCentroCusto, String nomeProdutoServico,
            SigCentroProducao sigCentroProducao, Integer seqObjetoCustoVersao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
        StringBuilder sql = new StringBuilder(600);
        
        sql.append("SELECT ")
        .append("sum(cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_ATV_INSUMOS.toString()).append(") + ")
        .append("sum(cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_ATV_PESSOAL.toString()).append(") + ")
        .append("sum(cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_ATV_EQUIPAMENTO.toString()).append(") + ")
        .append("sum(cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_ATV_SERVICO.toString()).append(") + ")
        .append("sum(cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_RATEIO_INSUMOS.toString()).append(") + ")
        .append("sum(cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_RATEIO_PESSOAS.toString()).append(") + ")
        .append("sum(cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_RATEIO_EQUIPAMENTOS.toString()).append(") + ")
        .append("sum(cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_RATEIO_SERVICO.toString()).append(") as vlr_diretos, ")

        .append("sum(cbj.").append(SigCalculoObjetoCusto.Fields.VALOR_RATEIO_INDIRETOS.toString()).append(") as vlr_indiretos ")
        
        .append("FROM ")

        .append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
        .append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
        .append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
        .append(SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
        .append(FccCentroCustos.class.getSimpleName() ).append( " fcc ")
        .append("left join fcc." ).append( FccCentroCustos.Fields.CENTRO_PRODUCAO.toString() ).append( " cto ")

        .append("WHERE ")

        .append("pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqCompetencia ")
        .append(" and pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString())
        .append(" and cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv."
                ).append( SigObjetoCustoVersoes.Fields.SEQ.toString())
        .append(" and ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj."
                ).append( SigObjetoCustos.Fields.SEQ.toString())
        .append(" and cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = fcc."
                ).append( FccCentroCustos.Fields.CODIGO.toString());

        if (codigoCentroCusto != null) {
            sql.append(" and fcc.").append(FccCentroCustos.Fields.CODIGO.toString()).append(" = :codigoCentroCusto ");
        }

        if (StringUtils.isNotBlank(nomeProdutoServico)) {
            sql.append(" and upper(obj.").append(SigObjetoCustos.Fields.NOME.toString()).append(") like \'%").append(nomeProdutoServico.toUpperCase()).append("%\'");
        }

        if (sigCentroProducao != null) {
            sql.append(" and cto.").append(SigCentroProducao.Fields.SEQ.toString()).append(" = ").append(sigCentroProducao.getSeq());
        }
        
        if(tiposCentroProducao != null && tiposCentroProducao.length > 0){
			sql.append(" and cto.").append(SigCentroProducao.Fields.IND_TIPO.toString()).append(" in (:tiposCentroProducao )");
		}

        if (seqObjetoCustoVersao != null) {
            sql.append(" and ocv.").append(SigObjetoCustoVersoes.Fields.SEQ.toString()).append(" = :seqObjetoCustoVersao ");
        }

        Query createQuery = this.createHibernateQuery(sql.toString());
        createQuery.setParameter("seqCompetencia", seqCompetencia);
        
        this.definirParametrosConsulta(createQuery, codigoCentroCusto, seqObjetoCustoVersao, null, tiposCentroProducao);
        
        @SuppressWarnings("unchecked")
        List<Object[]> valores = createQuery.list();
        SomatorioAnaliseCustosObjetosCustoVO retorno = new SomatorioAnaliseCustosObjetosCustoVO();

        if (!valores.isEmpty()) {
            retorno.setSomatorioDireto((BigDecimal)valores.get(0)[0]);
            retorno.setSomatorioIndiretos((BigDecimal)valores.get(0)[1]);
        }
        
        return retorno;
    }
    
    public SomatorioAnaliseCustosObjetosCustoVO obterSomatorioVisualizarCentroCusto(Integer seqCompetencia, FccCentroCustos fccCentroCustos, SigCentroProducao sigCentroProducao, DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		StringBuilder sql = new StringBuilder(900);

		sql.append("SELECT ")
		
		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DI' then mvt.")
				.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_isumos, ")

		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DP' then mvt.")
				.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_pessoas, ")

		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DE' then mvt.")
				.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_equipamentos, ")

		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DS' then mvt.")
				.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_servicoes, ")
		
		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DI' then mvt.")
			.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) + ")
		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DP' then mvt.")
			.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) + ")
		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DE' then mvt.")
			.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) + ")
		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'DS' then mvt.")
			.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_diretos, ") 

		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'II' then mvt.")
			.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) + ")	
		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'IP' then mvt.")
			.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) + ")
		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'IE' then mvt.")
			.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) + ")
		.append("sum (case mvt.").append(SigMvtoContaMensal.Fields.TIPO_VALOR.toString()).append(" when 'IS' then mvt.")
			.append(SigMvtoContaMensal.Fields.VALOR).append(" else 0 end) as tot_indiretos, ") 	

		.append("sum (mvt.").append(SigMvtoContaMensal.Fields.VALOR.toString()).append(") as total ")

		.append(" FROM ")

		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " mvt, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " fcc ")
		.append("left join fcc." ).append( FccCentroCustos.Fields.CENTRO_PRODUCAO.toString() ).append( " cto ")

		.append(" WHERE ")

		.append("pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqCompetencia ")
		.append(" and pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = mvt." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString())
		.append('.').append(SigProcessamentoCusto.Fields.SEQ.toString())

		.append(" and mvt." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = fcc."
				).append( FccCentroCustos.Fields.CODIGO.toString())
		.append(" and mvt." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " in (:tipoMovimentos) ");

		if (fccCentroCustos != null) {
			sql.append(" and fcc.").append(FccCentroCustos.Fields.CODIGO.toString()).append(" = ").append(fccCentroCustos.getCodigo());
		}

		if (sigCentroProducao != null) {
			sql.append(" and cto.").append(SigCentroProducao.Fields.SEQ.toString()).append(" = ").append(sigCentroProducao.getSeq());
		}
		
		if(tiposCentroProducao != null && tiposCentroProducao.length > 0){
			sql.append(" and cto.").append(SigCentroProducao.Fields.IND_TIPO.toString()).append(" in (:tiposCentroProducao )");
		}
		
		Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameterList("tipoMovimentos", new DominioTipoMovimentoConta[] { DominioTipoMovimentoConta.SIP, DominioTipoMovimentoConta.SIT });	
		
		if(tiposCentroProducao != null && tiposCentroProducao.length > 0){
			createQuery.setParameterList("tiposCentroProducao", tiposCentroProducao);
		}
		
		@SuppressWarnings("unchecked")
        List<Object[]> valores = createQuery.list();
        SomatorioAnaliseCustosObjetosCustoVO retorno = new SomatorioAnaliseCustosObjetosCustoVO();
        if (!valores.isEmpty()) {
        	retorno.setSomatorioInsumos((BigDecimal)valores.get(0)[0]);
            retorno.setSomatorioPessoal((BigDecimal)valores.get(0)[1]);
            retorno.setSomatorioEquipamentos((BigDecimal)valores.get(0)[2]);
            retorno.setSomatorioServicos((BigDecimal)valores.get(0)[3]);
            retorno.setSomatorioDireto((BigDecimal)valores.get(0)[4]);
            retorno.setSomatorioIndiretos((BigDecimal)valores.get(0)[5]);
            retorno.setSomatorioTotal((BigDecimal)valores.get(0)[6]);
        }
        return retorno;
	}

}
