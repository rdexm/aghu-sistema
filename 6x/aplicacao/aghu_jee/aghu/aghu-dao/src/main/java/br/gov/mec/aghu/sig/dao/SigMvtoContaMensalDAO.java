package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Table;

import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioRepasse;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoAtividadeInsumo;
import br.gov.mec.aghu.model.SigCalculoAtividadePessoa;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCalculoRateioPessoa;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigControleVidaUtil;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoPesos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProducaoObjetoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.DepreciacaoEquipamentoRateioVO;
import br.gov.mec.aghu.sig.custos.vo.DetalhamentoCustosGeralVO;
import br.gov.mec.aghu.sig.custos.vo.InsumosAtividadeRateioPesoQuantidadeVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoProducaoExamesVO;
import br.gov.mec.aghu.sig.custos.vo.ValoresIndiretosVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseTabCustosObjetosCustoVO;

public class SigMvtoContaMensalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigMvtoContaMensal> {

	private static final long serialVersionUID = -3374643157628374303L;
	
	public static final String CONDICAO_MENOR = "<";
	public static final String CONDICAO_MAIOR = ">";

	public void removerPorProcessamento(Integer idProcessamentoCusto) {
		StringBuilder sql = new StringBuilder(40);
		sql.append(" DELETE " ).append( SigMvtoContaMensal.class.getSimpleName().toString() ).append( " ca ")
		.append(" WHERE ca." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pSeq");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamentoCusto);
		query.executeUpdate();
	}

	public BigDecimal buscarSaldoVidaUtil(ScoMaterial material, FccCentroCustos centroCustos, SigProcessamentoCusto processamentoCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigMvtoContaMensal.class);
		criteria.setProjection(Projections.sum(SigMvtoContaMensal.Fields.VALOR.toString()));
		criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString(), DominioTipoMovimentoConta.SIP));
		criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.TIPO_VALOR.toString(), DominioTipoValorConta.DI));
		criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.MATERIAL.toString(), material));
		LogicalExpression codDebitaNotNull = Restrictions.and(Restrictions.isNotNull(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA.toString()),
				Restrictions.eq(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA.toString(), centroCustos));
		LogicalExpression codDebitaNull = Restrictions.and(Restrictions.isNull(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA.toString()),
				Restrictions.eq(SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString(), centroCustos));
		criteria.add(Restrictions.or(codDebitaNotNull, codDebitaNull));
		criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString(), processamentoCusto));
		return (BigDecimal) executeCriteriaUniqueResult(criteria);
	}
	
	public Integer buscarConsumoExcedenteInsumos(Integer materialCodigo, 
			Integer centroCustosCodigo, 
			SigProcessamentoCusto processamentoCusto, 
			Long valorPrevistoTotal, String condicaoMaiorMenor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigMvtoContaMensal.class);
		criteria.setProjection(Projections.sum(SigMvtoContaMensal.Fields.QTDE.toString()));
		criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.MATERIAL_CODIGO.toString(), materialCodigo));
		LogicalExpression codDebitaNotNull = Restrictions.and(Restrictions.isNotNull(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA_CODIGO.toString()),
				Restrictions.eq(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA_CODIGO.toString(), centroCustosCodigo));
		LogicalExpression codDebitaNull = Restrictions.and(Restrictions.isNull(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA_CODIGO.toString()),
				Restrictions.eq(SigMvtoContaMensal.Fields.CENTRO_CUSTO_CODIGO.toString(), centroCustosCodigo));
		criteria.add(Restrictions.or(codDebitaNotNull, codDebitaNull));
		criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString(), processamentoCusto));
		criteria.add(Restrictions.in(SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString(), new Object[]{DominioTipoMovimentoConta.SIP, DominioTipoMovimentoConta.SIT}));
		criteria.add(Restrictions.gt(SigMvtoContaMensal.Fields.VALOR.toString(), BigDecimal.ZERO));
		if(condicaoMaiorMenor != null){
			criteria.add(Restrictions.sqlRestriction(" 1 = 1 having sum({alias}.qtde) "+condicaoMaiorMenor+' '+valorPrevistoTotal));
		}
		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> consultarCentrosCustosComProcessamento(Integer pmuSeq) {
		StringBuilder sql = new StringBuilder(110);
		sql.append("SELECT ")
		.append("  msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString())
		.append(" FROM ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append(" WHERE ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pmuSeq ")
		.append(" GROUP BY  ")
		.append("  msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString())
		.append(" HAVING ")
		.append(" sum(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") > 0 ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pmuSeq", pmuSeq);
		List<Integer> centroCustos = new ArrayList<Integer>();
		List<Object> list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			for (Object object : list) {
				centroCustos.add((Integer) object);
			}
		}
		return centroCustos;
	}

	/**
	 *  Busca informações do consumo mensal do insumo e/ou equipamento.  
	 * 
	 * @author rmalvezzi
	 * @param processamentoCusto			Processamento Atual.
	 * @param material						Material - O Insumo. (Só deve ser informado se a pesquisa é por insumo)
	 * @param grupoOcupacao					Grupo Ocupação do cargo. (Só deve ser informado se a pesquisa é por pessoa)
	 * @param codPatrimonio					Código do patrimonio. (Só deve ser informado se a pesquisa é por equipamento)
	 * @param centroCustos					Centro de custo do Insumo.	 
	 * @return								Um vetor com a soma da coluna QTDE e VALOR.
	 */
	public Object[] buscarSomatorioConsumoInsumoMaterialCentroCusto(SigProcessamentoCusto processamentoCusto, ScoMaterial material,
			SigGrupoOcupacoes grupoOcupacao, String codPatrimonio, FccCentroCustos centroCustos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigMvtoContaMensal.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sum(SigMvtoContaMensal.Fields.QTDE.toString()))
				.add(Projections.sum(SigMvtoContaMensal.Fields.VALOR.toString()))
		);
		criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString(), processamentoCusto));
		if (material != null) {
			criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.MATERIAL.toString(), material));
			criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.TIPO_VALOR.toString(), DominioTipoValorConta.DI));
		}
		if (grupoOcupacao != null) {
			criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.GRUPO_OCUPACAO.toString(), grupoOcupacao));
			criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.TIPO_VALOR.toString(), DominioTipoValorConta.DP));
		}
		if (codPatrimonio != null) {
			criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.CODIGO_PATRIMONIO.toString(), codPatrimonio));
			criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.TIPO_VALOR.toString(), DominioTipoValorConta.DE));
		}
		LogicalExpression codDebitaNotNull = Restrictions.and(Restrictions.isNotNull(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA.toString()),
				Restrictions.eq(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA.toString(), centroCustos));
		LogicalExpression codDebitaNull = Restrictions.and(Restrictions.isNull(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA.toString()),
				Restrictions.eq(SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString(), centroCustos));
		criteria.add(Restrictions.or(codDebitaNotNull, codDebitaNull));
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Busca os equipamentos com valor de depreciaçao que nao foram associados e alocados em atividades de
	 * objetos de custo
	 * 
	 * @author rhrosa
	 * @param seqProcessamentoCusto		Código do processamento atual.
	 * @return							Retorna ScrollableResults com os equipamentos e valores de depreciacao
	 */
	public List<DepreciacaoEquipamentoRateioVO> buscarDepreciacaoRateioEquipamentos(Integer seqProcessamentoCusto) {
		StringBuilder hql = new StringBuilder(250);
		hql.append("SELECT ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.CODIGO_PATRIMONIO.toString() ).append( ", ")
		.append("sum(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") ")
		.append("FROM " ).append( SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append("WHERE ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqProcessamentoCusto ")
		.append("AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = :tipoValorConta ")
		.append("GROUP BY " ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( ", ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.CODIGO_PATRIMONIO.toString() ).append( ", ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ' ')
		.append("HAVING sum(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") > 0 ")
		.append("ORDER BY msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.CODIGO_PATRIMONIO.toString() ).append( ' ');
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setInteger("seqProcessamentoCusto", seqProcessamentoCusto);
		createQuery.setString("tipoValorConta", DominioTipoValorConta.DE.toString());
		List<Object[]> valores = createQuery.list();
		List<DepreciacaoEquipamentoRateioVO> lstRetorno = new ArrayList<DepreciacaoEquipamentoRateioVO>();
		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(new DepreciacaoEquipamentoRateioVO(objects));
			}
		}
		return lstRetorno;
	}

	/**
	* SQL Busca Movimentos de Pessoas de Centro de Custo.
	* 
	* @author rmalvezzi
	* @param seqCompetencia		Filtro de Competencia do mês.
	* @param seqCentroCusto		Filtro por centro de custo.
	* @return						Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	*/
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosPessoasCentroCusto(Integer seqCompetencia, Integer codigoCentroCusto) {
		StringBuilder sql = new StringBuilder(550);
		sql.append("SELECT ")
		.append("goc." ).append( SigGrupoOcupacoes.Fields.DESCRICAO.toString() ).append( " as goc_nome, ")
		.append("SUM(msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( ") as qtde, ")
		.append("0 as qtde_realizada, ")
		.append("SUM(case msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " WHEN 'VAA' THEN msl.")
		.append(SigMvtoContaMensal.Fields.VALOR.toString() ).append( " else 0 end) as total_alocado, ")
		.append("SUM(case msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " WHEN 'VAR' THEN msl.")
		.append(SigMvtoContaMensal.Fields.VALOR.toString() ).append( " when 'VRG' then msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
		.append(" else 0 end) as total_rateado, ")
		.append("SUM(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") as vlr_nao_alocado, ")
		.append("SUM(case msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " WHEN 'SIP' THEN msl.")
		.append(SigMvtoContaMensal.Fields.VALOR.toString() ).append( " when 'SIT' then msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
		.append(" else 0 end) as vlr_total ")
		.append("FROM " ).append( SigMvtoContaMensal.class.getSimpleName() ).append( " msl left outer join ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.GRUPO_OCUPACAO.toString() ).append( " goc ")
		.append("WHERE msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqCompetencia ")
		.append("AND msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :codigoCentroCusto ")
		.append("AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " IN (:tipoMovimento) ")
		.append("AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = 'DP' ")
		.append("GROUP BY goc." ).append( SigGrupoOcupacoes.Fields.DESCRICAO.toString() ).append( ' ')
		.append("ORDER BY goc." ).append( SigGrupoOcupacoes.Fields.DESCRICAO.toString() ).append( ' ');
		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("codigoCentroCusto", codigoCentroCusto);
		createQuery.setParameterList("tipoMovimento", new DominioTipoMovimentoConta[] { DominioTipoMovimentoConta.VAA, DominioTipoMovimentoConta.VAR,
				DominioTipoMovimentoConta.VRG, DominioTipoMovimentoConta.SIP, DominioTipoMovimentoConta.SIT });
		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();
		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoPessoa(objects, false));
			}
		}
		return lstRetorno;
	}
	
	/**
	* SQL Busca Movimentos de Pessoas do Objeto de Custo
	* 
	* @author rmalvezzi
	* @param seqCompetencia		Filtro pela compêntecia do mês.
	* @param seqObjetoVersao	Filtro pelo Objeto Custo Versão.
	* @param seqCentroCusto 	Centro de custo
	* @return					Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	*/
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosPessoasObjetoCustoParte1(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto) {
		StringBuilder sql = new StringBuilder(350);
		sql.append("SELECT ")
		.append("goc." ).append( SigGrupoOcupacoes.Fields.DESCRICAO.toString() ).append( " as goc_descricao, ")
		.append("0 as qtde, ")
		.append("sum(cap." ).append( SigCalculoAtividadePessoa.Fields.QUANTIDADE_REALIZADA ).append( ") as qtde_realizada, ")
		.append("sum(cap." ).append( SigCalculoAtividadePessoa.Fields.VALOR_GRUPO_OCUPACAO ).append( ") as vlr_atividade, ")
		.append("0 as vlr_rateio ")
		.append(" FROM ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
		.append(SigCalculoAtividadePessoa.class.getSimpleName() ).append( " cap, ")
		.append(SigGrupoOcupacoes.class.getSimpleName() ).append( " goc, ")
		.append(SigAtividadePessoas.class.getSimpleName() ).append( " avp ")
		.append(" WHERE ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString()).append( " = :seqObjetoVersao")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()).append( " = :seqCentroCusto")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = " ).append( "cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ ).append( '.'	).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" AND cmt." ).append( SigCalculoComponente.Fields.SEQ ).append( " = cap." ).append( SigCalculoAtividadePessoa.Fields.CALCULO_COMPONENTE ).append( '.').append( SigCalculoComponente.Fields.SEQ)
		.append(" AND cap." ).append( SigCalculoAtividadePessoa.Fields.GRUPO_OCUPACOES ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ.toString() ).append( " = goc.").append( SigGrupoOcupacoes.Fields.SEQ.toString())
		.append(" AND cap." ).append( SigCalculoAtividadePessoa.Fields.ATIVIDADE_PESSOA.toString() ).append( '.' ).append( SigAtividadePessoas.Fields.SEQ.toString() ).append( " = avp.").append( SigAtividadePessoas.Fields.SEQ.toString())
		.append(" GROUP BY goc." ).append( SigGrupoOcupacoes.Fields.DESCRICAO.toString() ).append( ' ');
		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoVersao", seqObjetoVersao);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();
		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.create(objects, true));
			}
		}
		return lstRetorno;
	}

	/**
	* SQL Busca Movimentos de Pessoas do Objeto de Custo - Parte 1.
	* 
	* @author rmalvezzi
	* @param seqCompetencia		Filtro pela compêntecia do mês.
	* @param seqObjetoVersao	Filtro pelo Objeto Custo Versão.
	* @param seqCentroCusto 	Centro de custo
	* @return					Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	*/
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosPessoasObjetoCustoParte2(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto) {
		StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT ")
		.append("goc." ).append( SigGrupoOcupacoes.Fields.DESCRICAO.toString() ).append( " as goc_descricao, ")
		.append("SUM(crp." ).append( SigCalculoRateioPessoa.Fields.QUANTIDADE.toString() ).append( ") as gtde, ")
		.append("0 as qtde_realizada, ")
		.append("0 as vlr_atividade, ")
		.append("SUM(crp." ).append( SigCalculoRateioPessoa.Fields.VALOR_PESSOAL.toString() ).append( ") as vlr_rateio ")
		.append(" FROM ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ")
		.append(" INNER JOIN cbj." ).append( SigCalculoObjetoCusto.Fields.LISTA_CALCULO_RATEIO_PESSOAS.toString() ).append( " crp ")
		.append(" LEFT OUTER JOIN crp." ).append( SigCalculoRateioPessoa.Fields.GRUPO_OCUPACAO.toString() ).append( " as goc ")
		.append(" WHERE ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ ).append( " = :seqObjetoVersao")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()).append( " = :seqCentroCusto")
		//.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = crp." ).append( SigCalculoRateioPessoa.Fields.CALCULO_OBJETO_CUSTO.toString() ).append( '.').append( SigCalculoRateioPessoa.Fields.SEQ)
		//.append(" AND crp." ).append( SigCalculoRateioPessoa.Fields.GRUPO_OCUPACAO.toString() ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ.toString() ).append( " = goc.").append( SigGrupoOcupacoes.Fields.SEQ.toString())
		.append(" GROUP BY goc." ).append( SigGrupoOcupacoes.Fields.DESCRICAO.toString() ).append( ' ');
		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoVersao", seqObjetoVersao);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();
		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.create(objects, true));
			}
		}
		return lstRetorno;
	}

	/**
	 * Busca Custo Geral do Centro de Custo
	 * 
	 * @author rmalvezzi
	 * @param pmuSeq				Seq do processamento selecionado.	
	 * @param fccCentroCustos		Centro de custo selecionado.
	 * @param tiposValorConta		Tipo do Valor de Conta, DI por exemplo.
	 * @return						Lista de DetalhamentoCustosGeralVO.
	 */
	public DetalhamentoCustosGeralVO buscarMvtoContaMensal(Integer pmuSeq, FccCentroCustos fccCentroCustos, DominioTipoValorConta... tiposValorConta) {
		StringBuilder sql = new StringBuilder(400);
		sql.append("SELECT ")
		.append(" sum (case msl.").append(SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString()).append(" when 'VAA' then msl.")
				.append(SigMvtoContaMensal.Fields.VALOR.toString()).append(" else 0 end) as vlr_atividade, ")
		.append(" sum (case msl.").append(SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString()).append(" when 'VAR' then msl.")
				.append(SigMvtoContaMensal.Fields.VALOR.toString()).append(" when 'VRG' then msl.").append(SigMvtoContaMensal.Fields.VALOR.toString())
				.append(" else 0 end) as vlr_rateio, ")
		.append("sum (msl.").append(SigMvtoContaMensal.Fields.VALOR.toString()).append(") as vlr_nao_atividade, ")
		.append(" sum (case msl.").append(SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString()).append(" when 'SIP' then msl.")
				.append(SigMvtoContaMensal.Fields.VALOR.toString()).append(" when 'SIT' then msl.").append(SigMvtoContaMensal.Fields.VALOR.toString())
				.append(" else 0 end) as vlr_total ")
		.append(" FROM ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append(" WHERE ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pmuSeq")
		.append(" and msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :fccCentroCustos")
		.append(" and msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " in (:tipoMovimento) ")
		.append(" and msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " in (:tipoValor)");
		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("pmuSeq", pmuSeq);
		createQuery.setParameter("fccCentroCustos", fccCentroCustos.getCodigo());
		createQuery.setParameterList("tipoMovimento", new DominioTipoMovimentoConta[] { DominioTipoMovimentoConta.VAA, DominioTipoMovimentoConta.VAR,
				DominioTipoMovimentoConta.VRG, DominioTipoMovimentoConta.SIP, DominioTipoMovimentoConta.SIT });
		createQuery.setParameterList("tipoValor", tiposValorConta);
		Object[] mvto = (Object[]) createQuery.uniqueResult();
		DetalhamentoCustosGeralVO result = null;
		if (mvto != null) {
			result = DetalhamentoCustosGeralVO.create(mvto);
		}
		return result;
	}

	/**
	 * Consulta em banco de dados que retorna todos os insumos, agrupados por grupo de material, 
	 * com valor de consumo e que não foram associados e alocados em atividades de objetos de custos. 
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamento			Processamento Atual.
	 * @param centroCustoAplic			Paramentro centroCustoAplic do tabela agh_parametros.
	 * @param centroCustoSolic			Paramentro centroCustoSolic do tabela agh_parametros.
	 * @return							Retorna todos os insumos agrupados por grupo de material e 
	 * 									ordenados por centro de custo e grupo do material (insumo).
	 */
	public ScrollableResults buscarConsumoInsumoRateio(Integer seqProcessamento, Integer centroCustoAplic, Integer centroCustoSolic) {

		StringBuilder hql = new StringBuilder(500);
		hql.append("SELECT ")
		.append("  msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString())
		.append(", mat." ).append( ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString())
		.append(", SUM( case ")
		//.append(" when msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( " < 0 then ( msl.").append(SigMvtoContaMensal.Fields.QTDE.toString() ).append(" * -1 ) " )
		.append(" when msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( " < 0 then ( msl.").append(SigMvtoContaMensal.Fields.QTDE.toString() ).append(" * -1 ) " )
		.append(" else msl.").append(SigMvtoContaMensal.Fields.QTDE.toString()).append(' ')
		.append(" end ) ")
		.append(", SUM(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") ")
		.append(" FROM ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl, ")
		.append(ScoMaterial.class.getSimpleName() ).append( " mat ")
		.append("WHERE msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqProcessamento ")
		.append("  AND msl." ).append( SigMvtoContaMensal.Fields.MATERIAL.toString() ).append( '.' ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " = mat.").append( ScoMaterial.Fields.CODIGO.toString())
		.append("  AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " IN ('DI') ")
		.append(" AND ((msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( " IS NULL) OR ")
		.append(" (msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( " IS NOT NULL AND ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA ).append( '.' ).append( FccCentroCustos.Fields.CODIGO).append( " IN (:centroCustoAplic, :centroCustoSolic))) ")
		.append(" AND NOT EXISTS (SELECT 1 ")
		.append(" FROM " ).append( SigControleVidaUtil.class.getSimpleName() ).append( " vit ")
		.append(" WHERE vit." ).append( SigControleVidaUtil.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( " = msl.").append( SigMvtoContaMensal.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ' ')
		.append(" AND vit." ).append( SigControleVidaUtil.Fields.UNIDADE_MEDIDA ).append( '.' ).append( ScoUnidadeMedida.Fields.CODIGO ).append( " = msl.").append( SigMvtoContaMensal.Fields.UNIDADE_MEDIDA ).append( '.' ).append( ScoUnidadeMedida.Fields.CODIGO ).append( ' ')
		.append(" AND vit." ).append( SigControleVidaUtil.Fields.MATERIAL ).append( '.' ).append( ScoMaterial.Fields.CODIGO ).append( " = msl." ).append( SigMvtoContaMensal.Fields.MATERIAL ).append( '.').append( ScoMaterial.Fields.CODIGO ).append( ' ')
		.append(" AND vit." ).append( SigControleVidaUtil.Fields.SITUACAO ).append( " = 'A') ")
		.append(" GROUP BY ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ",  ")
		.append(" mat." ).append( ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString())
		.append(" HAVING SUM(msl." ).append( SigMvtoContaMensal.Fields.VALOR ).append( ") > 0 ")
		.append(" ORDER BY  ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ",  ")
		.append(" mat." ).append( ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString());
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setInteger("seqProcessamento", seqProcessamento);
		createQuery.setInteger("centroCustoAplic", centroCustoAplic);
		createQuery.setInteger("centroCustoSolic", centroCustoSolic);
		return createQuery.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}

	/**
	 * Consulta em banco de dados que Busca insumos alocados em atividades (os quais a quantidade informada e calculada pode ser inferior ao consumo) com peso para rateio   
	 * 
	 * @author danilosantos
	 * @param seqProcessamento			Processamento Atual.
	 * @return							Retorna todos os insumos agrupados por grupo de material e 
	 * 									ordenados por centro de custo e grupo do material (insumo).
	 */
	@SuppressWarnings("unchecked")
	public List<InsumosAtividadeRateioPesoQuantidadeVO> buscarConsumoInsumoRateioComQuantidade(Integer seqProcessamento) {
		
		StringBuilder sql = montarConsulta();
	
		SQLQuery query = createSQLQuery(sql.toString());
		query.setParameter("seqProcessamento", seqProcessamento);
		query.setParameterList("grupos", new DominioGrupoDetalheProducao[]{ DominioGrupoDetalheProducao.PHI, DominioGrupoDetalheProducao.PAC});
		
		final List<InsumosAtividadeRateioPesoQuantidadeVO> vos = query
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.OBJ_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.OCV_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.CBT_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.TVD_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.AIS_SEQ.toString(), LongType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.CVN_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.CMT_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.CBJ_SEQ.toString(), IntegerType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.CCT_CODIGO.toString(), IntegerType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.MAT_CODIGO.toString(), IntegerType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.QTDE_PREVISTA.toString(), DoubleType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.QTDE_REALIZADA.toString(), DoubleType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.VLR_INSUMO.toString(), BigDecimalType.INSTANCE)
				.addScalar(InsumosAtividadeRateioPesoQuantidadeVO.Fields.PESO.toString(), BigDecimalType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(InsumosAtividadeRateioPesoQuantidadeVO.class)).list();

		return vos;
	}
	
	private StringBuilder montarConsulta() {
		final StringBuilder sql = new StringBuilder(1400);
		sql.append("SELECT ")
		.append(" obj.seq as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.OBJ_SEQ.toString())
		.append(" ,ocv.seq as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.OCV_SEQ.toString())
		.append(" ,cbt.seq as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.CBT_SEQ.toString())
		.append(" ,tvd.seq as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.TVD_SEQ.toString())
		.append(" ,ais.seq as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.AIS_SEQ.toString())
		.append(" ,cvn.seq as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.CVN_SEQ.toString())
		.append(" ,cmt.seq as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.CMT_SEQ.toString())
		.append(" ,cbj.seq as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.CBJ_SEQ.toString())
		.append(" ,cbj.cct_codigo as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.CCT_CODIGO.toString())
		.append(" ,ais.mat_codigo as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.MAT_CODIGO.toString())
		.append(" ,cvn.qtde_prevista as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.QTDE_PREVISTA.toString())
		.append(" ,cvn.qtde_realizada as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.QTDE_REALIZADA.toString())
		.append(" ,cvn.vlr_insumo as " ).append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.VLR_INSUMO.toString())
		.append(" ,coalesce(ope.valor,1) *  sum(coalesce(dhp.qtde,1)) as ").append(InsumosAtividadeRateioPesoQuantidadeVO.Fields.PESO.toString())
		.append(" FROM ")
		.append(SigObjetoCustos.class.getAnnotation(Table.class).name()).append( " obj")
		.append( " LEFT JOIN ").append( SigObjetoCustoVersoes.class.getAnnotation(Table.class).name()).append(" ocv on (ocv.obj_seq = obj.seq) ")
		.append( " LEFT JOIN ").append( SigObjetoCustoComposicoes.class.getAnnotation(Table.class).name()).append(" cbt on (cbt.ocv_seq = ocv.seq) ")
		.append( " LEFT JOIN ").append( SigAtividades.class.getAnnotation(Table.class).name()).append(" tvd on (cbt.tvd_seq = tvd.seq) ")
		.append( " LEFT JOIN ").append( SigAtividadeInsumos.class.getAnnotation(Table.class).name()).append(" ais on (ais.tvd_seq = tvd.seq) ")
		.append( " LEFT JOIN ").append( SigCalculoComponente.class.getAnnotation(Table.class).name()).append(" cmt on (cmt.cbt_seq = cbt.seq) ")
		.append( " LEFT JOIN ").append( SigCalculoAtividadeInsumo.class.getAnnotation(Table.class).name()).append(" cvn on (cvn.cmt_seq = cmt.seq and cvn.ais_seq = ais.seq) ")
		.append( " LEFT JOIN ").append( SigCalculoObjetoCusto.class.getAnnotation(Table.class).name()).append(" cbj on (cmt.cbj_seq = cbj.seq) ")
		.append( " LEFT JOIN ").append( SigProcessamentoCusto.class.getAnnotation(Table.class).name()).append(" pmu on (cbj.pmu_seq = pmu.seq) ")
		.append( " LEFT JOIN ").append( SigObjetoCustoPesos.class.getAnnotation(Table.class).name()).append(" ope on (ope.obj_seq = obj.seq) ")
		.append( " LEFT JOIN ").append( SigProducaoObjetoCusto.class.getAnnotation(Table.class).name()).append(" pjc on (pjc.cbj_seq = cbj.seq) ")
		.append( " LEFT JOIN ").append( SigDetalheProducao.class.getAnnotation(Table.class).name()).append(" dhp on (pjc.dhp_seq = dhp.seq) ")
		
		
		.append("WHERE pmu.seq = :seqProcessamento ")
		.append("  AND cvn.qtde_prevista = cvn.qtde_realizada")
		.append("  AND (dhp.grupo in (:grupos) OR dhp.grupo is NULL)")
		.append("  AND ais.vida_util_qtde IS NULL")
		.append("  AND ais.vida_util_tempo IS NULL")
		.append("  AND ais.qtde_uso IS NOT NULL")
		.append("  AND ais.qtde_uso <> 0")
		.append("  GROUP BY ")
		.append("  obj.seq," )
		.append("  ocv.seq," )
		.append("  cbt.seq," )
		.append("  tvd.seq," )
		.append("  ais.seq," )
		.append("  cvn.seq," )
		.append("  cmt.seq," )
		.append("  cbj.seq," )
		.append("  cbj.cct_codigo," )
		.append("  ais.mat_codigo," )
		.append("  cvn.qtde_prevista," )
		.append("  cvn.qtde_realizada," )
		.append("  cvn.vlr_insumo," )
		.append("  ope.valor" )
		.append("  ORDER BY ")
		.append("  cbj.cct_codigo")
		.append(", ais.mat_codigo" );
		
		return sql;

	}	
	
	

	
	/**
	 * SQL Busca Movimentos de Insumos de Centros de Custos	
	 * @author rmalvezzi
	 * @param seqCompetencia		Filtro pela Competencia do mês.
	 * @param seqCentroCusto		Filtro pelo Centro de custo.
	 * @return						Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosInsumosCentroCusto(Integer seqCompetencia, Integer seqCentroCusto) {

		StringBuilder sql = new StringBuilder(600);
		sql.append(" SELECT ")
				.append("  mat." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " as mat_codigo ")
				.append(", mat." ).append( ScoMaterial.Fields.NOME.toString() ).append( " as mat_nome ")
				.append(", msl." ).append( SigMvtoContaMensal.Fields.UNIDADE_MEDIDA.toString() ).append( '.' ).append( ScoUnidadeMedida.Fields.CODIGO.toString() ).append( " as mat_un ")

				//.append(", SUM (msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( ") as qtde ")
				.append(", SUM (case msl.").append(SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString()).append(" WHEN 'SIP' THEN msl.")
				.append(SigMvtoContaMensal.Fields.QTDE.toString()).append(" WHEN 'SIT' THEN msl.").append(SigMvtoContaMensal.Fields.QTDE.toString())
				.append(" ELSE 0 END) as qtde ")

				.append(", SUM (case msl.").append(SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString()).append("  WHEN 'VAA' THEN msl.")
				.append(SigMvtoContaMensal.Fields.VALOR.toString()).append("  ELSE 0 END) as total_alocado ").append(", SUM (case msl.")
				.append(SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString()).append("  WHEN 'VAR' THEN msl.")
				.append(SigMvtoContaMensal.Fields.VALOR.toString()).append("  WHEN 'VRG' THEN msl.").append(SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  ELSE 0 END) as total_rateado ").append(", SUM (msl.").append(SigMvtoContaMensal.Fields.VALOR.toString())
				.append(") as vlr_nao_alocado ").append(", SUM (case msl.").append(SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString())
				.append("  WHEN 'SIP' THEN msl.").append(SigMvtoContaMensal.Fields.VALOR.toString()).append("  WHEN 'SIT' THEN msl.")
				.append(SigMvtoContaMensal.Fields.VALOR.toString()).append("  ELSE 0 END) as vlr_total ")

		.append(" FROM ").append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl, ").append(ScoMaterial.class.getSimpleName() ).append( " mat ")
		.append(" WHERE ")
				.append(" msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()
						).append( " = :seqCompetencia")
				.append(" AND msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :seqCentroCusto")
				.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " in (:tipoMovimento) ")
				.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = '" ).append( DominioTipoValorConta.DI.toString() ).append( '\'')
				.append(" AND msl." ).append( SigMvtoContaMensal.Fields.MATERIAL.toString() ).append( '.' ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " = mat."
						).append( ScoMaterial.Fields.CODIGO.toString())
		.append(" GROUP BY ").append("  mat." ).append( ScoMaterial.Fields.CODIGO.toString()).append(", mat." ).append( ScoMaterial.Fields.NOME.toString())
				.append(", msl." ).append( SigMvtoContaMensal.Fields.UNIDADE_MEDIDA.toString());

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		createQuery.setParameterList("tipoMovimento", new DominioTipoMovimentoConta[] { DominioTipoMovimentoConta.VAA, DominioTipoMovimentoConta.VAR,
				DominioTipoMovimentoConta.VRG, DominioTipoMovimentoConta.SIP, DominioTipoMovimentoConta.SIT });

		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoInsumoCC(objects));
			}
		}

		Collections.sort(lstRetorno);

		return lstRetorno;
	}	
	
	/**
	 * Busca todos os grupos de ocupação com valor de folha e que não foram associados e alocados em atividades de objetos de custos. 
	 * Esses valores serão divididos (rateados) entre todos os objetos de custo do centro de custo com produção no mês de processamento, 
	 * de acordo com seus pesos e sua produção.
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamento			Seq do processamento atual.
	 * @return							Retorna todos os grupos de ocupação ordenados por centro de custo e código do grupo de ocupação.
	 */
	public ScrollableResults buscarValoresPessoal(Integer seqProcessamento) {
		StringBuilder hql = new StringBuilder(300);
		hql.append("SELECT ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.GRUPO_OCUPACAO.toString() ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ ).append( ", ")
		.append("sum( case ")
		//.append(" when msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( "< 0 then (msl.").append(SigMvtoContaMensal.Fields.QTDE.toString()).append(" * -1) ")
		.append(" when msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( "< 0 then (msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( " * -1) ")
		.append(" else msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( ' ')
		.append("end) as qtd_horas, ")
		.append("sum(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") as qtd_folhas_pessoal ")
		.append("FROM ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append("WHERE ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ ).append( " = :seqProcessamento ")
		.append("and msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " in ('" ).append( DominioTipoValorConta.DP.toString() ).append( "') ")
		.append("GROUP BY ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.GRUPO_OCUPACAO.toString() ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ ).append( ' ')
		.append("HAVING ")
		.append("sum(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") > 0 ")
		.append("ORDER BY ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ',')
		.append("msl." ).append( SigMvtoContaMensal.Fields.GRUPO_OCUPACAO.toString() ).append( '.' ).append( SigGrupoOcupacoes.Fields.SEQ ).append( ' ');
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setInteger("seqProcessamento", seqProcessamento);
		return createQuery.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}

	/**
	 * Busca detalhes da folha de pessoal.
	 * @author rogeriovieira
	 * @param seqProcessamento 			Identificador do processamento atual.
	 * @param codigoCentroCusto 		Identificador do centro de custo.
	 * @param seqGrupoOcupacao 			Identificador do grupo de ocupação.
	 * @return 							Se a consulta não recuperou nenhuma informação, significa que não houve valor alocado no mês.
	 */
	public BigDecimal buscarDetalheFolhaPessoal(Integer seqProcessamento, Integer codigoCentroCusto, Integer seqGrupoOcupacao) {
		Query query = this.montarConsultaBuscarCustoMedio(seqProcessamento, codigoCentroCusto, seqGrupoOcupacao, null);	
		return (BigDecimal) query.getSingleResult();
	}
	
	/**
	 * SQL Busca Custo Médio
	 * @author rogeriovieira
	 * @param seqProcessamento 			Identificador do processamento atual.
	 * @param codigoCentroCusto 		Identificador do centro de custo.
	 * @param codigoMaterial			Identificador do material.
	 * @return							retorna o custo médio do material com a sua unidade de medida;
	 */
	@SuppressWarnings("unchecked")
	public Object[] buscarCustoMedioMaterialComUnidadeMedida(Integer seqProcessamento, Integer codigoCentroCusto, Integer codigoMaterial) {
		Query query = montarConsultaBuscarCustoMedio(seqProcessamento, codigoCentroCusto, null, codigoMaterial);		
		List<Object[]> valores = query.getResultList();
		if (valores != null && valores.size() > 0) {
			return (Object[]) valores.get(0); 
		}
		return null;	
	}
	
	/**
	 * Monta a consulta para buscar o custo médio ou do grupo de ocupação, ou do material
	 * @author rogeriovieira
	 * @param seqProcessamento 			Identificador do processamento atual.
	 * @param codigoCentroCusto			Identificador do centro de custo.
	 * @param seqGrupoOcupacao			Identificador do grupo de ocupação.
	 * @param codigoMaterial			Identificador do material.
	 * @return							retorna a query de acordo com os parâmetros informados
	 */
	public Query montarConsultaBuscarCustoMedio(Integer seqProcessamento, Integer codigoCentroCusto, Integer seqGrupoOcupacao, Integer codigoMaterial){
		String colunaExtra = null;
		String campoIdentificador = null;
		Integer valorIdentificador = null;
		DominioTipoValorConta tipoValorConta = null;
		if(seqGrupoOcupacao != null){
			colunaExtra = " ";
			campoIdentificador = SigMvtoContaMensal.Fields.GRUPO_OCUPACAO + "." + SigGrupoOcupacoes.Fields.SEQ;
			valorIdentificador = seqGrupoOcupacao;
			tipoValorConta = DominioTipoValorConta.DP;
		}
		else if(codigoMaterial != null){
			colunaExtra = ", msl." + SigMvtoContaMensal.Fields.UNIDADE_MEDIDA+'.'+ScoUnidadeMedida.Fields.CODIGO+" as unidade_medida ";
			campoIdentificador = SigMvtoContaMensal.Fields.MATERIAL +  "." + ScoMaterial.Fields.CODIGO;
			valorIdentificador = codigoMaterial;
			tipoValorConta = DominioTipoValorConta.DI;
		}
		StringBuilder hql = new StringBuilder(300);
		hql.append("SELECT ")
		.append("SUM(msl." ).append( SigMvtoContaMensal.Fields.VALOR ).append( ") / SUM(msl." ).append( SigMvtoContaMensal.Fields.QTDE ).append( ") as custo_medio ")
		.append( colunaExtra )
		.append("FROM ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append("WHERE ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqProcessamento ")
		.append("AND coalesce(msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ") = :codigoCentroCusto ")
		.append("AND msl." ).append( campoIdentificador ).append( " = :valorIdentificador ")
		.append("AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR ).append( " = :tipoValor ")
		.append("AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO ).append( " IN ( :tipoSIP, :tipoSIT ) ");

		if(codigoMaterial != null){	
			hql.append("GROUP BY  msl." + SigMvtoContaMensal.Fields.UNIDADE_MEDIDA+'.'+ScoUnidadeMedida.Fields.CODIGO);
		}
		Query query = this.createQuery(hql.toString());
		query.setParameter("seqProcessamento", seqProcessamento);
		query.setParameter("codigoCentroCusto", codigoCentroCusto);
		query.setParameter("valorIdentificador", valorIdentificador);
		query.setParameter("tipoValor", tipoValorConta );
		query.setParameter("tipoSIP", DominioTipoMovimentoConta.SIP);
		query.setParameter("tipoSIT", DominioTipoMovimentoConta.SIT);
		return query;
	}

	/**
	* SQL Busca Folha de Pessoal
	* @author rogeriovieira
	* @param seqProcessamento			Seq do processamento atual.
	* @param codigoCentroCusto			Código do centro de custo
	* @param seqGrupoOcupacao		Cogio do grupo de ocupação
	* @return							Retorna a quantidade de horas e valor da folha de pessoal do grupo de ocupação dentro do centro de custo no processamento.
	*/
	public Object[] buscarFolhaPessoal(Integer seqProcessamento, Integer codigoCentroCusto, Integer seqGrupoOcupacao) {
		return this.buscarQuantidadeEValorConsumo(seqProcessamento, codigoCentroCusto, seqGrupoOcupacao, null);
	}
		
	/**
	* SQL Busca Consumo Insumos alocado em atividades
	* @author rogeriovieira 
	* @param seqProcessamento 			Seq do processamento atual.
	* @param codigoCentroCusto			Código do centro de custo.
	* @param codigoMaterial			Código do material.
	* @return							retorna a quantidade consumo de insumo e o valor de consumo dos insumo
	*/
	public Object[] buscarConsumoInsumoAlocadoAtividades(Integer seqProcessamento, Integer codigoCentroCusto, Integer codigoMaterial){
		return this.buscarQuantidadeEValorConsumo(seqProcessamento, codigoCentroCusto, null, codigoMaterial);
	}
	
	/**
	* Consulta utiliza para retornar o a quantidade e valor de consumo para pessoal e insumos
	* @author rogeriovieira
	* @param seqProcessamento 			Seq do processamento atual.
	* @param codigoCentroCusto			Código do centro de custo.
	* @param seqGrupoOcupacao			Cogio do grupo de ocupação.
	* @param codigoMaterial			Código do material.
	* @return							retorna a quantidade o valor de consumo de acordo com os parâmetros informados
	*/
	private Object[] buscarQuantidadeEValorConsumo(Integer seqProcessamento, Integer codigoCentroCusto, Integer seqGrupoOcupacao, Integer codigoMaterial) {
		String atributoMovimentoMensal = null;
		DominioTipoValorConta tipoValorConta = null;
		Integer identificador = null;
		
		if(seqGrupoOcupacao != null){
			atributoMovimentoMensal = SigMvtoContaMensal.Fields.GRUPO_OCUPACAO.toString() + '.' + SigGrupoOcupacoes.Fields.SEQ;
			tipoValorConta = DominioTipoValorConta.DP;
			identificador = seqGrupoOcupacao;
		}
		else if(codigoMaterial != null){
			atributoMovimentoMensal =  SigMvtoContaMensal.Fields.MATERIAL.toString() + '.' + ScoMaterial.Fields.CODIGO;
			tipoValorConta = DominioTipoValorConta.DI;
			identificador = codigoMaterial;
		}
		StringBuilder hql = new StringBuilder(300);
		hql.append("SELECT ")
		.append("sum( case ")
		.append(" when msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( "< 0 then (msl.").append(SigMvtoContaMensal.Fields.QTDE.toString()).append(" * -1) ")
		.append(" else msl.").append(SigMvtoContaMensal.Fields.QTDE.toString()).append(' ')
		.append("end) as qtd_horas, ")
		.append("sum(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") as vlr_folha_pessoal ")
		.append("FROM ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append("WHERE ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ ).append( " = :seqProcessamento ")
		.append("and coalesce( msl.").append( SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA).append('.').append( FccCentroCustos.Fields.CODIGO ).append(" , msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( " ) = :codigoCentroCusto ")	
		.append("and msl." ).append( atributoMovimentoMensal ).append(" = :identificador ")
		.append("and msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " in ( :tipoValorConta ) ");
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setInteger("seqProcessamento", seqProcessamento);
		createQuery.setInteger("codigoCentroCusto", codigoCentroCusto);
		createQuery.setInteger("identificador", identificador);
		createQuery.setParameter("tipoValorConta", tipoValorConta);
		return (Object[]) createQuery.uniqueResult();
	}
	
	/**
	 * Busca os valores indiretos a ratear
	 * 
	 * @author rogeriovieira
	 * @param pmuSeq identificador do processamento atual
	 * @param cliente indicador
	 * @return retorna todos os valores indiretos debitados na área por objeto de custo, ordenado por centro de custo, objeto de custo calculado e tipo de valor (insumos, pessoas, equipamentos e serviços).
	 */
	public List<ValoresIndiretosVO> buscarValoresIndiretos(Integer pmuSeq, boolean cliente, Integer iteracao) {
		StringBuilder hql = new StringBuilder(600);
		hql.append(" SELECT ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CALCULO_OBJETO_CUSTO.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ ).append( ", ")
		.append(" sum(case when msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = :tipoValorII then msl.").append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( " else 0 end), ")
		.append(" sum(case when msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = :tipoValorIP then msl.").append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( " else 0 end), ")
		.append(" sum(case when msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = :tipoValorIE  then msl.").append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( " else 0 end), ")
		.append(" sum(case when msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = :tipoValorIS  then msl.").append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( " else 0 end) ")
		.append(" FROM ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append(" inner join msl.").append(SigMvtoContaMensal.Fields.CENTRO_CUSTO).append( " cct ")
		.append(" inner join cct.").append(FccCentroCustos.Fields.CENTRO_PRODUCAO).append( " cto ")
		.append(" inner join msl.").append(SigMvtoContaMensal.Fields.CALCULO_OBJETO_CUSTO).append( " cbj ")
		.append(" inner join cbj.").append(SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO).append( " obj ")
		.append(" WHERE ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqProcessamento")
		.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " in ( :tipoValorII, :tipoValorIP, :tipoValorIE, :tipoValorIS ) ")
		.append(" AND cto." ).append( SigCentroProducao.Fields.IND_TIPO.toString() ).append( " in (:tipo)");
		
		if(cliente){
			hql.append(" AND msl." ).append( SigMvtoContaMensal.Fields.ITERACAO.toString() ).append(" = :iteracao "); 
		}
		else{
			//Buscar somente os que não vão ser repassado diretamente para o cliente
			hql.append(" AND obj." ).append( SigObjetoCustoVersoes.Fields.IND_REPASSE.toString() ).append(" <> :repassePaciente ");
		}
		
		hql.append(" GROUP BY ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CALCULO_OBJETO_CUSTO.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ)
		.append(" HAVING ")
		.append(" sum(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") > 0 ")
		.append(" ORDER BY ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CALCULO_OBJETO_CUSTO.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ);
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("seqProcessamento", pmuSeq);
		createQuery.setParameter("tipoValorII", DominioTipoValorConta.II);
		createQuery.setParameter("tipoValorIP", DominioTipoValorConta.IP);
		createQuery.setParameter("tipoValorIE", DominioTipoValorConta.IE);
		createQuery.setParameter("tipoValorIS", DominioTipoValorConta.IS);
		
		if (cliente) {
			//TODO Ainda não definidio se deveria passar só para os centros de custo de apoio, ou para todos
			//createQuery.setParameter("tipo", DominioTipoCentroProducaoCustos.A);
			createQuery.setParameter("iteracao", iteracao); 
			createQuery.setParameterList("tipo", new DominioTipoCentroProducaoCustos[] { DominioTipoCentroProducaoCustos.A, DominioTipoCentroProducaoCustos.I, DominioTipoCentroProducaoCustos.F });
		} else {
			createQuery.setParameter("repassePaciente", DominioRepasse.P);
			createQuery.setParameterList("tipo", new DominioTipoCentroProducaoCustos[] { DominioTipoCentroProducaoCustos.I, DominioTipoCentroProducaoCustos.F });
		}
		
		List<Object[]> resultado = createQuery.list();
		List<ValoresIndiretosVO> retorno = new ArrayList<ValoresIndiretosVO>(resultado.size());
		for (Object[] objects : resultado) {
			retorno.add(new ValoresIndiretosVO(objects));
		}
		return retorno;
	}

	
	/**
	* SQL Busca Movimentos de Indiretos do Centro de Custo
	* @author rmalvezzi
	* @param seqCompetencia		Filtro de Competencia do mês.
	* @param seqCentroCusto		Filtro por centro de custo.
	* @return					Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	*/
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosIndiretosCentroCusto(Integer seqCompetencia, Integer seqCentroCusto, Integer codigoDebita) {
		StringBuilder hql = new StringBuilder(600);
		
		hql.append(" SELECT ")
		.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME.toString() ).append( ", ")
		.append(" ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( ", ");
		
		if(codigoDebita != null){
			hql.append(" msl." ).append( SigMvtoContaMensal.Fields.ITERACAO.toString() ).append( ", ");
		}
		
		hql.append(" sum(case when msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = '" ).append( DominioTipoValorConta.II ).append( "' then msl.").append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( " else 0 end) as tot_insumos, ")
		.append(" sum(case when msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = '" ).append( DominioTipoValorConta.IP ).append( "' then msl.").append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( " else 0 end) as tot_pessoas, ")
		.append(" sum(case when msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = '" ).append( DominioTipoValorConta.IE ).append( "' then msl.").append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( " else 0 end) as tot_equipamentos, ")
		.append(" sum(case when msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = '" ).append( DominioTipoValorConta.IS ).append( "' then msl.").append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( " else 0 end) as tot_servicos, ")
		.append(" sum(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") as total ")
		.append(" FROM ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append("inner join msl.").append(SigMvtoContaMensal.Fields.CALCULO_OBJETO_CUSTO).append( " cbj ")
		.append("inner join msl.").append(SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA ).append( " cct ")
		.append("inner join cbj.").append(SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO ).append( " ocv ")
		.append("inner join ocv.").append(SigObjetoCustoVersoes.Fields.OBJETO_CUSTO ).append( " obj ")
		.append(" WHERE ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqProcessamento")
		.append(" AND msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :seqCentroCusto")
		.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " in ('" ).append( DominioTipoMovimentoConta.SIP ).append( "') ")
		.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " in ('" ).append( DominioTipoValorConta.II ).append( "','" ).append( DominioTipoValorConta.IP).append( "','" ).append( DominioTipoValorConta.IE ).append( "','" ).append( DominioTipoValorConta.IS ).append( "') ");
		
		if(codigoDebita != null){
			hql.append("AND ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString()).append(" = ").append(codigoDebita);
		}
		
		hql.append(" GROUP BY ")
		.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME.toString()).append( ", ")
		.append(" ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() );
		
		if(codigoDebita != null){
			hql.append( ", ").append(" msl." ).append( SigMvtoContaMensal.Fields.ITERACAO.toString() )
			.append(" ORDER BY ")
			.append(" msl." ).append( SigMvtoContaMensal.Fields.ITERACAO.toString() ).append( ", ");
		}
		else{
			hql.append(" ORDER BY ");
		}
		
		hql.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME.toString());
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("seqProcessamento", seqCompetencia);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();
		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				if(codigoDebita == null){
					lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoIndiretoCC(objects, false));
				}
				else{
					lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoIndiretoCCIteracao(objects, false));
				}
			}
		}
		return lstRetorno;
	}

	/**
	* SQL Busca Movimentos de Pessoas de Centro de Custo.
	* @author rmalvezzi
	* @param seqCompetencia		Filtro de Competencia do mês.
	* @param seqCentroCusto		Filtro por centro de custo.
	* @return						Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	*/
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosEquipamentoCentroCusto(Integer seqCompetencia, Integer seqCentroCusto) {
		StringBuilder sql = new StringBuilder(550);
		sql.append("SELECT ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CODIGO_PATRIMONIO.toString() ).append( " as cod_patrimonio, ")
		.append(" sum(msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( ") as qtde, ")
		.append(" 0 as qtde_realizada, ")
		.append(" sum(case msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " WHEN " ).append( " 'VAA' THEN msl.").append( SigMvtoContaMensal.Fields.VALOR.toString())
		.append(" else 0 end) as total_alocado, ")
		.append(" sum(case msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " WHEN " ).append( " 'VAR' THEN msl.").append( SigMvtoContaMensal.Fields.VALOR.toString())
		.append(" WHEN 'VRG' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
		.append(" else 0 end) as total_rateado, ")
		.append("sum(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") as vlr_nao_alocado, ")
		.append(" sum(case msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " WHEN " ).append( " 'SIP' THEN msl.").append( SigMvtoContaMensal.Fields.VALOR.toString())
		.append(" WHEN 'SIT' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
		.append(" else 0 end) as vlr_total ")
		.append(" FROM " ).append( SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append(" WHERE ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqCompetencia ")
		.append(" AND msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :seqCentroCusto")
		.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " in (:tipoMovimento) ")
		.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = 'DE' ")
		.append(" GROUP BY  msl." ).append( SigMvtoContaMensal.Fields.CODIGO_PATRIMONIO.toString())
		.append(" ORDER BY msl." ).append( SigMvtoContaMensal.Fields.CODIGO_PATRIMONIO.toString());
		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		createQuery.setParameterList("tipoMovimento", new DominioTipoMovimentoConta[] { DominioTipoMovimentoConta.VAA, DominioTipoMovimentoConta.VAR, DominioTipoMovimentoConta.VRG, DominioTipoMovimentoConta.SIP, DominioTipoMovimentoConta.SIT });
		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();
		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoEquipamentoCC(objects));
			}
		}
		return lstRetorno;
	}

	/**
	 * Pesquisa de centros de custos relacionados ao movimentos de conta mensal, onde os centros de custos não tiveram objeto de custo calculado.
	 * 
	 * @author agerling
	 * @param pmuSeq	Identificador do Processamento de Custo
	 * @return 			Lista de movimento de conta mensal.
	 */
	public List<ObjetoCustoProducaoExamesVO> consultarCentroCustoSemObjCustoAlocado(SigProcessamentoCusto processamentoCusto) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigMvtoContaMensal.class, "mov");

		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty(SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString()), ObjetoCustoProducaoExamesVO.Fields.CENTRO_CUSTO.toString())
				.add(Projections.count(SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString()), ObjetoCustoProducaoExamesVO.Fields.QTDE_INTEIRA.toString())
		);
		criteria.add(Restrictions.eq(SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString(), processamentoCusto));
		DetachedCriteria criteriaCbj = DetachedCriteria.forClass(SigCalculoObjetoCusto.class, "cbj");
		criteriaCbj.setProjection(Projections.property(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()));
		criteriaCbj.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString(), processamentoCusto));
		criteria.add(Subqueries.propertyNotIn("mov." + SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString(), criteriaCbj));
		criteria.setResultTransformer(Transformers.aliasToBean(ObjetoCustoProducaoExamesVO.class));
		return executeCriteria(criteria);
	}
}