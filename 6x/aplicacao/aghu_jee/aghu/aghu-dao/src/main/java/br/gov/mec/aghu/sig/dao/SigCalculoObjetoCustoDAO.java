package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigCalculoAtividadeEquipamento;
import br.gov.mec.aghu.model.SigCalculoAtividadeInsumo;
import br.gov.mec.aghu.model.SigCalculoAtividadePessoa;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoIndiretoEquipamento;
import br.gov.mec.aghu.model.SigCalculoIndiretoInsumo;
import br.gov.mec.aghu.model.SigCalculoIndiretoPessoa;
import br.gov.mec.aghu.model.SigCalculoIndiretoServico;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCalculoRateioEquipamento;
import br.gov.mec.aghu.model.SigCalculoRateioInsumo;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoPesos;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProducaoObjetoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.CalculoSubProdutoVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoProducaoExamesVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoProducaoRateioVO;
import br.gov.mec.aghu.sig.custos.vo.ValoresIndiretosPesosRateioVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseTabCustosObjetosCustoVO;

public class SigCalculoObjetoCustoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoObjetoCusto> {

	private static final long serialVersionUID = 7418655426678789677L;

	public void removerPorProcessamento(Integer idProcessamentoCusto) {
		StringBuilder sql = new StringBuilder(45);
		sql.append(" DELETE " ).append( SigCalculoObjetoCusto.class.getSimpleName().toString() ).append( " ca ")
		.append(" WHERE ca." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' )
		.append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pSeq");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamentoCusto);
		query.executeUpdate();
	}
	
	/**
	 * Busca todos os Calculos de Objeto de Custo de um processamento.
	 * 
	 * @author rmalvezzi
	 * @param processamentoCusto		Processamento usado como filtro.
	 * @return							Lista de objetos.
	 */
	public List<SigCalculoObjetoCusto> buscarCalculosObjetoCustoPorCompetencia(SigProcessamentoCusto processamentoCusto){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoObjetoCusto.class);
		criteria.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString(), processamentoCusto));		
		return executeCriteria(criteria);
	}

	/**
	 * Busca todos os cálculos dos subprodutos juntamente com o produto pai que deve ser atualizado.
	 * 
	 * @author rogeriovieira
	 * @param seqProcessamentoCusto 		processamento que representa a competência executada
	 * @return 								lista com todos as composições com sub-produtos da competência
	 */
	@SuppressWarnings("unchecked")
	public List<CalculoSubProdutoVO> pesquisarCalculoObjetoCustoSubProduto(Integer seqProcessamentoCusto) {
		StringBuilder hql = new StringBuilder(160);

		hql.append(" select")
		.append(" cmt, ") //representa o produto pai
		.append(" cbj ") //representa o sub-produto filho (composição) 
		.append(" from ")
		.append(SigObjetoCustoComposicoes.class.getSimpleName() ).append( " cbt, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ")
		.append(" where ")
		.append(" cbt." ).append( SigObjetoCustoComposicoes.Fields.IND_SITUACAO.toString()).append( " = :situacao ")
		.append(" and cbt." ).append( SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES_COMPOE.toString() )
		.append( " is not null ")
		.append(" and cbt." ).append( SigObjetoCustoComposicoes.Fields.SEQ.toString() ).append( " = cmt." )
		.append( SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO.toString() ).append( '.')
		.append(SigObjetoCustoComposicoes.Fields.SEQ.toString()).append(" and cbt." )
		.append( SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES_COMPOE_SEQ.toString() ).append( " = cbj.")
		.append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' )
		.append( SigObjetoCustoVersoes.Fields.SEQ.toString())
		.append(" and cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() )
		.append( " = :seqProcessamento ");

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("situacao", DominioSituacao.A);
		query.setParameter("seqProcessamento", seqProcessamentoCusto);
		query.setResultTransformer(new CalculoSubProdutoVO());

		return (List<CalculoSubProdutoVO>) query.list();
	}

	
	/**
	 * Busca todos os Calculos de um objeto custo versão de um processamento.	
	 * 
	 * @author rmalvezzi
	 * @param pmuSeq			Seq do Processamento selecionado.
	 * @param ocvSeq			Seq do Objeto Custo Versão selecionado.
	 * @return					Lista de todos os Calculos Objeto Custo após ser aplicado os filtros.
	 */
	public List<SigCalculoObjetoCusto> listarCalculoObjetoCusto(Integer pmuSeq, Integer ocvSeq, FccCentroCustos centroCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoObjetoCusto.class);
		criteria.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString(), pmuSeq));
		criteria.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() + '.' + SigObjetoCustoVersoes.Fields.SEQ.toString(), ocvSeq));
		criteria.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString(), centroCusto));
		return executeCriteria(criteria);
	}

	/**
	 * Busca os objetos de custo do centro de custo com produção no mês de processamento e seus respectivos pesos.
	 * Método genérico/único para todos os processamentos de rateio. 
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamentoCusto		Código do processamento atual.
	 * @param codCentroCusto			Código do centro de custo.
	 * @return							Retorna ScrollableResults com os objetos de custos com produção.
	 */
	@SuppressWarnings("unchecked")
	public List<ObjetoCustoProducaoRateioVO> buscarObjetosCustoComProducaoParaRateio(Integer seqProcessamentoCusto, Integer codCentroCusto) {
		StringBuilder hql = new StringBuilder(800);
		hql.append("SELECT ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ", ")
			.append("(SELECT sum(coalesce(ope1." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ",1) * sum(coalesce(dhp1.").append( SigDetalheProducao.Fields.QTDE.toString() ).append( ",1))) ")
			.append("FROM ")
			.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu1, ")
			.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj1 ")
			.append("LEFT JOIN cbj1." ).append( SigCalculoObjetoCusto.Fields.LISTA_PRODUCAO_OBJETO_CUSTO.toString() ).append( " pjc1 ")
			.append("LEFT JOIN pjc1." ).append( SigProducaoObjetoCusto.Fields.DETALHE_PRODUCAO.toString() ).append( " dhp1, ")
			.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv1, ")
			.append(SigObjetoCustos.class.getSimpleName() ).append( " obj1 ")
			.append("LEFT JOIN obj1." ).append( SigObjetoCustos.Fields.OBJETO_CUSTO_PESO.toString() ).append( " ope1 ")
			.append("WHERE ")
			.append("pmu1." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqProcessamentoCusto ")
			.append("AND cbj1." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :codCentroCusto ")
			.append("AND pmu1." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj1." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString()).append(' ')
			.append("AND cbj1." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv1.").append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append(' ')
			.append("AND ocv1." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj1.").append( SigObjetoCustos.Fields.SEQ.toString() ).append(' ')
			.append("AND (dhp1." ).append( SigDetalheProducao.Fields.GRUPO.toString() ).append( " in (:grupos) or dhp1.").append( SigDetalheProducao.Fields.GRUPO.toString() ).append( " is null) ")
			.append("AND cbj1." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = cbj.").append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append(' ')
			.append("GROUP BY ")
			.append("ope1." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ") as soma_pesos, ")
			.append("coalesce(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ",1) * sum(coalesce(dhp." ).append( SigDetalheProducao.Fields.QTDE.toString()).append( ",1)) as peso_oc ")
		.append("FROM ")
		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ")
		.append("LEFT JOIN cbj." ).append( SigCalculoObjetoCusto.Fields.LISTA_PRODUCAO_OBJETO_CUSTO.toString() ).append( " pjc ")
		.append("LEFT JOIN pjc." ).append( SigProducaoObjetoCusto.Fields.DETALHE_PRODUCAO.toString() ).append( " dhp, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj ")
		.append("LEFT JOIN obj." ).append( SigObjetoCustos.Fields.OBJETO_CUSTO_PESO.toString() ).append( " ope ")
		.append("WHERE ")
		.append("pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqProcessamentoCusto ")
		.append("AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :codCentroCusto ")
		.append("AND pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString()).append(' ')
		.append("AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append(' ')
		.append("AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj.").append( SigObjetoCustos.Fields.SEQ.toString() ).append(' ')
		.append("AND (dhp." ).append( SigDetalheProducao.Fields.GRUPO.toString() ).append( " in (:grupos) or dhp." ).append( SigDetalheProducao.Fields.GRUPO.toString()).append( " is null) ")
		.append("GROUP BY ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", cbj.").append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ", ")
		.append("ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append(' ')
		.append("ORDER BY 1,2 ");

		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("seqProcessamentoCusto", seqProcessamentoCusto);
		createQuery.setParameter("codCentroCusto", codCentroCusto);
		createQuery.setParameterList("grupos", new DominioGrupoDetalheProducao[]{ DominioGrupoDetalheProducao.PHI, DominioGrupoDetalheProducao.PAC});

		List<Object[]> valores = createQuery.list();
		List<ObjetoCustoProducaoRateioVO> lstRetorno = new ArrayList<ObjetoCustoProducaoRateioVO>();
		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(new ObjetoCustoProducaoRateioVO(objects));
			}
		}
		return lstRetorno;
	}

	/**
	 * SQL Busca Movimentos de Insumos do Objeto de Custo - Parte 1	
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro da Compentencia do mês. 
	 * @param seqObjetoVersao			Filtro pelo Objeto Custo Versão.
	 * @param seqCentroCusto 
	 * @return							Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosInsumosObjetoCustoParte1(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto) {
		StringBuilder sql = new StringBuilder(500);
		sql.append(" SELECT ")
				.append("  mat." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " as mat_codigo ")
				.append(", mat." ).append( ScoMaterial.Fields.NOME.toString() ).append( " as mat_nome ")
				.append(", mat." ).append( ScoMaterial.Fields.UNIDADE_MEDIDA.toString() ).append( '.' ).append( ScoUnidadeMedida.Fields.CODIGO.toString() ).append( " as mat_un ")
				.append(", sum(cnv." ).append( SigCalculoAtividadeInsumo.Fields.QUANTIDADE_REALIZADA.toString() ).append( ") as qtde ")
				.append(", (case ")
				.append(" when mat." ).append( ScoMaterial.Fields.UNIDADE_MEDIDA.toString() ).append( '.' ).append( ScoUnidadeMedida.Fields.CODIGO.toString() ).append( " is not null ") 
				.append(" then sum(cnv." ).append( SigCalculoAtividadeInsumo.Fields.VALOR_INSUMO.toString() ).append( ") else 0 end) as vlr_atividade ") 
				.append(", sum(cnv." ).append( SigCalculoAtividadeInsumo.Fields.VALOR_INSUMO.toString() ).append( ") as vlr_atividade ")
				.append(", (case ")
				.append(" when mat." ).append( ScoMaterial.Fields.UNIDADE_MEDIDA.toString() ).append( '.' ).append( ScoUnidadeMedida.Fields.CODIGO.toString() ).append( " is null ") 
				.append(" then sum(cnv." ).append( SigCalculoAtividadeInsumo.Fields.VALOR_INSUMO.toString() ).append( ") else 0 end) as vlr_rateio ")
		.append(" FROM ").append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ").append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
				.append(SigCalculoAtividadeInsumo.class.getSimpleName() ).append( " cnv, ").append(ScoMaterial.class.getSimpleName() ).append( " mat, ")
				.append(SigAtividadeInsumos.class.getSimpleName() ).append( " ais ")
		.append(" WHERE ")
				.append("     cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString()).append( " = :seqObjetoVersao")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()).append( " = :seqCentroCusto")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = cmt.").append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
				.append(" AND cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " = cnv." ).append( SigCalculoAtividadeInsumo.Fields.CALCULO_COMPONENTE.toString()).append( '.' ).append( SigCalculoComponente.Fields.SEQ.toString())
				.append(" AND cnv." ).append( SigCalculoAtividadeInsumo.Fields.MATERIAL.toString() ).append( '.' ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " = mat.").append( ScoMaterial.Fields.CODIGO.toString())
				.append(" AND cnv." ).append( SigCalculoAtividadeInsumo.Fields.ATIVIDADE_INSUMO.toString() ).append( '.' ).append( SigAtividadeInsumos.Fields.SEQ.toString()).append( " = ais." ).append( SigAtividadeInsumos.Fields.SEQ.toString())
		.append(" GROUP BY ")
			.append("  mat." ).append( ScoMaterial.Fields.CODIGO.toString())
			.append(", mat." ).append( ScoMaterial.Fields.NOME.toString())
			.append(", mat." ).append( ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString());

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoVersao", seqObjetoVersao);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoInsumo(objects));
			}
		}
		return lstRetorno;
	}

	/**
	 * SQL Busca Movimentos de Insumos do Objeto de Custo - Parte 2
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro da Compentencia do mês. 
	 * @param seqObjetoVersao			Filtro pelo Objeto Custo Versão.
	 * @param seqCentroCusto 
	 * @return							Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosInsumosObjetoCustoParte2(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto) {
		StringBuilder sql = new StringBuilder(300);
		sql.append(" SELECT ").append("  '' as codigo ").append(", gmt." ).append( ScoGrupoMaterial.Fields.DESCRICAO.toString() ).append( " as nome ")
				.append(", '' as mat_un ").append(", sum(cri." ).append( SigCalculoRateioInsumo.Fields.QUANTIDADE.toString() ).append( ") as qtde ")
				.append(", 0 as vlr_atividade ").append(", sum(cri." ).append( SigCalculoRateioInsumo.Fields.VALOR_INSUMO.toString() ).append( ") as vlr_rateio ")
		.append(" FROM ").append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ,").append(SigCalculoRateioInsumo.class.getSimpleName() ).append( " cri ,")
				.append(ScoGrupoMaterial.class.getSimpleName() ).append( " gmt ")
		.append(" WHERE ")
		.append("     cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia")
		.append(" and cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString()).append( " = :seqObjetoVersao")
		.append(" and cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()).append( " = :seqCentroCusto")
		.append(" and cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = cri." ).append( SigCalculoRateioInsumo.Fields.CALCULO_OBJETO_CUSTO.toString()).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" and cri." ).append( SigCalculoRateioInsumo.Fields.GRUPO_MATERIAL.toString() ).append( '.' ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " = gmt.").append( ScoGrupoMaterial.Fields.CODIGO.toString())
		.append(" group by ").append("  gmt." ).append( ScoGrupoMaterial.Fields.DESCRICAO.toString());	

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoVersao", seqObjetoVersao);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		

		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoInsumo(objects));
			}
		}

		return lstRetorno;
	}

	/**
	* SQL Busca Movimentos de Equipamentos do Objeto de Custo - Parte 1
	* 
	* @author rmalvezzi
	* @param seqCompetencia			Filtro da Compentencia do mês. 
	* @param seqObjetoVersao			Filtro pelo Objeto Custo Versão.
	* @param seqCentroCusto 	
	* @return							Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	*/
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosEquipamentoObjetoCustoParte1(Integer seqCompetencia, Integer seqObjetoCustoVersao, Integer seqCentroCusto) {
		StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT ")
		.append("ave." ).append( SigAtividadeEquipamentos.Fields.CODIGO_PATRIMONIO.toString() ).append( " as cod_patrimonio, ")
		.append("sum(cae." ).append( SigCalculoAtividadeEquipamento.Fields.PESO.toString() ).append( ") as qtde, ")
		.append("0 as qtde_realizada, ")
		.append("sum(cae." ).append( SigCalculoAtividadeEquipamento.Fields.VALOR_DEPRECIACAO.toString() ).append( ") as vlr_atividade, ")
		.append("0 as vlr_rateio ")
		.append("FROM " ).append( SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ")
		.append(SigCalculoAtividadeEquipamento.class.getSimpleName() ).append( " cae, ")
		.append(SigAtividadeEquipamentos.class.getSimpleName() ).append( " ave ")
		.append("WHERE ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString()).append( " = :seqObjetoCustoVersao")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()).append( " = :seqCentroCusto")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString()).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" AND cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " = cae." ).append( SigCalculoAtividadeEquipamento.Fields.CALCULO_COMPONENTE.toString()).append( '.' ).append( SigCalculoComponente.Fields.SEQ.toString())
		.append(" AND cae." ).append( SigCalculoAtividadeEquipamento.Fields.ATIVIDADE_EQUIPAMENTO.toString() ).append( '.' ).append( SigAtividadeEquipamentos.Fields.SEQ.toString()).append( " = ave." ).append( SigAtividadeEquipamentos.Fields.SEQ.toString())
		.append(" GROUP BY ave." ).append( SigAtividadeEquipamentos.Fields.CODIGO_PATRIMONIO);

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoCustoVersao", seqObjetoCustoVersao);
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
	* SQL Busca Movimentos de Equipamentos do Objeto de Custo - Parte 2
	* 
	* @author rmalvezzi
	* @param seqCompetencia			Filtro da Compentencia do mês. 
	* @param seqObjetoVersao			Filtro pelo Objeto Custo Versão.
	* @param seqCentroCusto 
	* @return							Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	*/
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosEquipamentoObjetoCustoParte2(Integer seqCompetencia, Integer seqObjetoCustoVersao, Integer seqCentroCusto) {
		StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT ")
		.append("crq." ).append( SigCalculoRateioEquipamento.Fields.CODIGO_PATRIMONIO.toString() ).append( " as cod_patrimonio, ")
		.append("0 as qtde, ")
		.append("sum(crq." ).append( SigCalculoRateioEquipamento.Fields.QUANTIDADE.toString() ).append( ") as qtde_realizada, ")
		.append("0 as vlr_atividade, ")
		.append("sum(crq." ).append( SigCalculoRateioEquipamento.Fields.VALOR_DEPRECIACAO.toString() ).append( ") as vlr_rateio ")
		.append("FROM " ).append( SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigCalculoRateioEquipamento.class.getSimpleName() ).append( " crq ")
		.append("WHERE ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia ")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ).append( " = :seqObjetoCustoVersao ")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO).append( " = :seqCentroCusto ")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = crq." ).append( SigCalculoRateioEquipamento.Fields.CALCULO_OBJETO_CUSTO.toString()).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" GROUP BY crq." ).append( SigCalculoRateioEquipamento.Fields.CODIGO_PATRIMONIO);

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoCustoVersao", seqObjetoCustoVersao);
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
	 * Busca os valores de contratos a ratear 
	 * 
	 * @author rogeriovieira
	 * @param seqProcessamentoCusto 	Código do processamento atual.
	 * @return 							ScrollableResults com todos os itens de contrato ordenados por centro de custo, item do contrato e tipo de serviço contratado.	
	 */
	public ScrollableResults buscarCustosContrato(Integer seqProcessamentoCusto) {
		StringBuilder hql = new StringBuilder(350);
		hql.append(" SELECT ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.AF_CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.AUTORIZACAO_FORNEC.toString() ).append( '.' ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.SERVICO.toString() ).append( '.' ).append( ScoServico.Fields.CODIGO.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.SOLICITACAO_SERVICO.toString() ).append( '.' ).append( ScoSolicitacaoServico.Fields.NUMERO.toString() ).append( ", ")
		.append(" SUM(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") ")
		.append(" FROM ")
		.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append(" WHERE ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqProcessamento")
		.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = :tipoValor")
		.append(" GROUP BY ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.AF_CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.AUTORIZACAO_FORNEC.toString() ).append( '.' ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.SERVICO.toString() ).append( '.' ).append( ScoServico.Fields.CODIGO.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.SOLICITACAO_SERVICO.toString() ).append( '.' ).append( ScoSolicitacaoServico.Fields.NUMERO.toString() ).append(' ')
		.append(" HAVING ")
		.append(" SUM(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") > 0")
		.append(" ORDER BY ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.AF_CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.AUTORIZACAO_FORNEC.toString() ).append( '.' ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.SERVICO.toString() ).append( '.' ).append( ScoServico.Fields.CODIGO.toString() ).append( ", ")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.SOLICITACAO_SERVICO.toString() ).append( '.' ).append( ScoSolicitacaoServico.Fields.NUMERO.toString());
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("tipoValor", DominioTipoValorConta.DS);
		createQuery.setParameter("seqProcessamento", seqProcessamentoCusto);
		return createQuery.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}

	/**
	 * Busca soma dos pesos dos objetos de custo do centro de custo
	 * 
	 * @author rogeriovieira
	 * @param seqProcessamentoCusto 	Identificador do processamento atual
	 * @param cctCodigo 				Identificador do centro de custo
	 * @param cliente 					Indicador que define se a consulta deverá fazer referencia aos clientes do objeto de custo
	 * @return 							Soma dos pesos de todos os objetos de custo da área recebedora dos indiretos
	 */
	public BigDecimal buscarSomaPesosObjetosCustoCliente(Integer seqProcessamentoCusto, Integer cctCodigo) {
		StringBuilder hql = new StringBuilder(350);
		hql.append(" SELECT ")
		.append(" sum(coalesce(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ",1) * ")
		.append(" coalesce(dhp." ).append( SigDetalheProducao.Fields.QTDE.toString() ).append( ",1)) ")
		.append(" FROM ")
		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv ")
		.append(" LEFT OUTER JOIN ocv." ).append( SigObjetoCustoVersoes.Fields.DETALHE_PRODUCAO.toString() ).append( " dhp with (").append("dhp.grupo = :grupo or dhp.grupo is null")
		.append(") , ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj ")
		.append(" LEFT OUTER JOIN obj." ).append( SigObjetoCustos.Fields.OBJETO_CUSTO_PESO.toString() ).append( " ope ")
		.append(" WHERE ")
		.append(" pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqProcessamento")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :cctCodigo")
		.append(" AND pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ)
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString())
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj.").append( SigObjetoCustos.Fields.SEQ.toString())
		.append(" AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :tipoAP ");
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("seqProcessamento", seqProcessamentoCusto);
		createQuery.setParameter("cctCodigo", cctCodigo);
		createQuery.setParameter("grupo", DominioGrupoDetalheProducao.PAM);
		createQuery.setParameter("tipoAP", DominioTipoObjetoCusto.AP);
		return (BigDecimal) createQuery.uniqueResult();
	}
	
	public BigDecimal buscarSomaPesosObjetosCustoOC(Integer seqProcessamentoCusto, Integer cctCodigo) {
		StringBuilder hql = new StringBuilder(391);
		hql.append(" SELECT ")
		.append(" sum(coalesce(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ",1) * ")
		.append(" coalesce(dhp." ).append( SigDetalheProducao.Fields.QTDE.toString() ).append( ",1)) ")
		.append(" FROM ")
		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ")
		.append(" LEFT OUTER JOIN cbj." ).append( SigCalculoObjetoCusto.Fields.LISTA_PRODUCAO_OBJETO_CUSTO.toString() ).append( " pjc ")
		.append(" LEFT OUTER JOIN pjc." ).append( SigProducaoObjetoCusto.Fields.DETALHE_PRODUCAO.toString() ).append( " dhp, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv ")
		.append(" LEFT OUTER JOIN ocv." ).append( SigObjetoCustoVersoes.Fields.PHI.toString() ).append( " phi ")
		.append(" LEFT OUTER JOIN phi." ).append( SigObjetoCustoPhis.Fields.FAT_PHI.toString() ).append( " fat, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj ")
		.append(" LEFT OUTER JOIN obj." ).append( SigObjetoCustos.Fields.OBJETO_CUSTO_PESO.toString() ).append( " ope ")
		.append(" WHERE ")
		.append(" pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqProcessamento")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :cctCodigo")
		.append(" AND pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ)
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString())
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj.").append( SigObjetoCustos.Fields.SEQ.toString())
		.append(" AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :tipoAP ")
		.append(" AND (dhp." ).append( SigDetalheProducao.Fields.GRUPO.toString() ).append( " in (:grupos) or dhp." ).append( SigDetalheProducao.Fields.GRUPO.toString()).append( " is null) ");
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("seqProcessamento", seqProcessamentoCusto);
		createQuery.setParameter("cctCodigo", cctCodigo);
		createQuery.setParameter("tipoAP", DominioTipoObjetoCusto.AS);
		createQuery.setParameterList("grupos", new DominioGrupoDetalheProducao[]{ DominioGrupoDetalheProducao.PHI, DominioGrupoDetalheProducao.PAC});
		return (BigDecimal) createQuery.uniqueResult();
	}

	/**
	 * Busca os objetos de custo do centro de custo para os quais o custo indireto, recebido de outras áreas, deve ser rateado.
	 * 
	 * @author rogeriovieira
	 * @param seqProcessamentoCusto 		Identificador do processamento atual
	 * @param cctCodigo 					Identificador do centro de custo
	 * @param cliente 						Indicador que define se a consulta deverá fazer referencia aos clientes do objeto de custo
	 * @return 								Retorna os objetos de custo da área a receber os custos indiretos
	 */
	public List<ValoresIndiretosPesosRateioVO> buscarObjetosCustoPesosRateioCliente(Integer seqProcessamentoCusto, Integer cctCodigo) {
		StringBuilder hql = new StringBuilder(350);
		hql.append(" SELECT ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ", ")
		.append(" coalesce(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ",1) * ")
		.append(" coalesce(dhp." ).append( SigDetalheProducao.Fields.QTDE.toString() ).append( ",1) ")
		.append(" FROM ")
		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv ")
		.append(" LEFT OUTER JOIN ocv." ).append( SigObjetoCustoVersoes.Fields.DETALHE_PRODUCAO.toString() ).append( " dhp with (").append("dhp.grupo = :grupo or dhp.grupo is null")
		.append("), ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj ")
		.append(" LEFT OUTER JOIN obj." ).append( SigObjetoCustos.Fields.OBJETO_CUSTO_PESO.toString() ).append( " ope ")
		.append(" WHERE ")
		.append(" pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqProcessamento")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :cctCodigo")
		.append(" AND pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ)
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString())
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj.").append( SigObjetoCustos.Fields.SEQ.toString())
		.append(" AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :tipoAP ");
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("seqProcessamento", seqProcessamentoCusto);
		createQuery.setParameter("cctCodigo", cctCodigo);
		createQuery.setParameter("tipoAP", DominioTipoObjetoCusto.AP); 
		createQuery.setParameter("grupo", DominioGrupoDetalheProducao.PAM);
		List<Object[]> resultado = createQuery.list();
		List<ValoresIndiretosPesosRateioVO> retorno = new ArrayList<ValoresIndiretosPesosRateioVO>(resultado.size());
		for (Object[] objects : resultado) {
			retorno.add(new ValoresIndiretosPesosRateioVO(objects));
		}
		return retorno;
	}
	
	public List<ValoresIndiretosPesosRateioVO> buscarObjetosCustoPesosRateioOC(Integer seqProcessamentoCusto, Integer cctCodigo) {
		StringBuilder hql = new StringBuilder(450);
		hql.append(" SELECT ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ", ")
		.append(" coalesce(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ",1) * ")
		.append(" sum(coalesce(dhp." ).append( SigDetalheProducao.Fields.QTDE.toString() ).append( ",1)) ")
		.append(" FROM ")
		.append(SigProcessamentoCusto.class.getSimpleName() ).append( " pmu, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ")
		.append(" LEFT OUTER JOIN cbj." ).append( SigCalculoObjetoCusto.Fields.LISTA_PRODUCAO_OBJETO_CUSTO.toString() ).append( " pjc ")
		.append(" LEFT OUTER JOIN pjc." ).append( SigProducaoObjetoCusto.Fields.DETALHE_PRODUCAO.toString() ).append( " dhp, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv ")
		.append(" LEFT OUTER JOIN ocv." ).append( SigObjetoCustoVersoes.Fields.PHI.toString() ).append( " phi ")
		.append(" LEFT OUTER JOIN phi." ).append( SigObjetoCustoPhis.Fields.FAT_PHI.toString() ).append( " fat, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj ")
		.append(" LEFT OUTER JOIN obj." ).append( SigObjetoCustos.Fields.OBJETO_CUSTO_PESO.toString() ).append( " ope ")
		.append(" WHERE ")
		.append(" pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqProcessamento")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :cctCodigo")
		.append(" AND pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ)
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString())
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj.").append( SigObjetoCustos.Fields.SEQ.toString())
		.append(" AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :tipoAP ")
		.append(" AND (dhp." ).append( SigDetalheProducao.Fields.GRUPO.toString() ).append( " in (:grupos) or dhp." ).append( SigDetalheProducao.Fields.GRUPO.toString()).append( " is null) ")
		.append(" GROUP BY ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ", ")
		.append(" ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString())
		.append(" ORDER BY ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO);
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("seqProcessamento", seqProcessamentoCusto);
		createQuery.setParameter("cctCodigo", cctCodigo);
		createQuery.setParameter("tipoAP", DominioTipoObjetoCusto.AS);
		createQuery.setParameterList("grupos", new DominioGrupoDetalheProducao[]{ DominioGrupoDetalheProducao.PHI, DominioGrupoDetalheProducao.PAC});
		List<Object[]> resultado = createQuery.list();
		List<ValoresIndiretosPesosRateioVO> retorno = new ArrayList<ValoresIndiretosPesosRateioVO>(resultado.size());
		for (Object[] objects : resultado) {
			retorno.add(new ValoresIndiretosPesosRateioVO(objects));
		}
		return retorno;
	}

	/**
	 * SQL Busca Custos Indiretos do Objeto de Custo Parte 1.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia		Filtro pela competencia do mês.
	 * @param seqObjetoCusto		Filtro pelo Objeto custo.
	 * @param seqCentroCusto 
	 * @return						Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarCustosIndiretosObjetoCustoParte1(Integer seqCompetencia, Integer seqObjetoCusto, Integer seqCentroCusto, Integer codigoDebita) {
		StringBuilder hql = new StringBuilder(350);
		hql.append(" SELECT ")
		.append(" cct." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME.toString() ).append( ", ")
		.append(" cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ", ");
		
		if(codigoDebita != null){
			hql.append(" cii." ).append( SigCalculoIndiretoInsumo.Fields.ITERACAO.toString() ).append( ", ");
		}
		
		hql.append(" sum(cii." ).append( SigCalculoIndiretoInsumo.Fields.VALOR_INSUMOS.toString() ).append( "), ")
		.append(" 0, ").append(" 0, ").append(" 0 ")
		.append(" FROM ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigCalculoIndiretoInsumo.class.getSimpleName() ).append( " cii, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj_d, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")		
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " cct ")
		.append(" WHERE ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqProcessamento")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString()).append( " = :seqObjetoCusto")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()).append( " = :seqCentroCusto")
		.append(" AND cii." ).append( SigCalculoIndiretoInsumo.Fields.CALCULO_OBJETO_CUSTO.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString()).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" AND cbj_d." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString())		
		.append(" AND cii." ).append( SigCalculoIndiretoInsumo.Fields.CALCULO_OBJETO_CUSTO_DEBITA.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString()).append( " = cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj.").append( SigObjetoCustos.Fields.SEQ.toString())		
		.append(" AND cct." ).append( FccCentroCustos.Fields.CODIGO.toString() )
			.append( " = cbj_d." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.').append( FccCentroCustos.Fields.CODIGO.toString());
		
		if(codigoDebita != null){
			hql.append(" AND cbj_d.").append( SigCalculoObjetoCusto.Fields.SEQ.toString()).append(" = ").append(codigoDebita);
		}
			
		hql.append(" GROUP BY ")
		.append(" cct." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME.toString()).append( ", ")
		.append(" cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() );
		
		if(codigoDebita != null){
			hql.append( ", ").append(" cii." ).append( SigCalculoIndiretoInsumo.Fields.ITERACAO.toString() )
			.append(" ORDER BY ").append(" cii." ).append( SigCalculoIndiretoInsumo.Fields.ITERACAO.toString() ).append( ", ");
		}
		else{
			hql.append(" ORDER BY ");
		}
		
		hql.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString());
		
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("seqProcessamento", seqCompetencia);
		createQuery.setParameter("seqObjetoCusto", seqObjetoCusto);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = null;
		if (valores != null && valores.size() > 0) {
			lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();
			for (Object[] objects : valores) {
				if(codigoDebita == null){
					lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoIndireto(objects, false));
				}
				else{
					lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoIndiretoIteracao(objects, false));
				}
			}
		}
		return lstRetorno;
	}
	
	/**
	 * SQL Busca Custos Indiretos do Objeto de Custo parte 2.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia		Filtro pela competencia do mês.
	 * @param seqObjetoCusto		Filtro pelo Objeto custo.
	 * @return						Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarCustosIndiretosObjetoCustoParte2(Integer seqCompetencia, Integer seqObjetoCusto, Integer seqCentroCusto, Integer codigoDebita) {
		StringBuilder hql = new StringBuilder(350);
		hql.append(" SELECT ")
		.append(" cct." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME.toString() ).append( ", ")
		.append(" cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ", ");
		
		if(codigoDebita != null){
			hql.append(" cip." ).append( SigCalculoIndiretoPessoa.Fields.ITERACAO.toString() ).append( ", ");
		}
		
		hql.append(" 0, ")
		.append(" sum(cip." ).append( SigCalculoIndiretoPessoa.Fields.VALOR_PESSOAS.toString() ).append( "), ")
		.append(" 0, ").append(" 0 ")
		.append(" FROM ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigCalculoIndiretoPessoa.class.getSimpleName() ).append( " cip, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj_d, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")		
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " cct ")
		.append(" WHERE ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqProcessamento")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString()).append( " = :seqObjetoCusto")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()).append( " = :seqCentroCusto")
		.append(" AND cip." ).append( SigCalculoIndiretoPessoa.Fields.CALCULO_OBJETO_CUSTO.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString()).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" AND cbj_d." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString())		
		.append(" AND cip." ).append( SigCalculoIndiretoPessoa.Fields.CALCULO_OBJETO_CUSTO_DEBITA.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString()).append( " = cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append(" = obj.").append( SigObjetoCustos.Fields.SEQ.toString())		
		.append(" AND cct." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = cbj_d." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.').append( FccCentroCustos.Fields.CODIGO.toString());		
		
		if(codigoDebita != null){
			hql.append(" AND cbj_d.").append( SigCalculoObjetoCusto.Fields.SEQ.toString()).append(" = ").append(codigoDebita);
		}	
		
		hql.append(" GROUP BY ")
		.append(" cct." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME.toString()).append( ", ")
		.append(" cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() );
		
		if(codigoDebita != null){
			hql.append( ", ").append(" cip." ).append( SigCalculoIndiretoPessoa.Fields.ITERACAO.toString() )
			.append(" ORDER BY ").append(" cip." ).append( SigCalculoIndiretoPessoa.Fields.ITERACAO.toString() ).append( ", ");
		}
		else{
			hql.append(" ORDER BY ");
		}
		
		hql.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString());
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("seqProcessamento", seqCompetencia);
		createQuery.setParameter("seqObjetoCusto", seqObjetoCusto);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = null;
		if (valores != null && valores.size() > 0) {
			lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();
			for (Object[] objects : valores) {
				if(codigoDebita == null){
					lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoIndireto(objects, false));
				}
				else{
					lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoIndiretoIteracao(objects, false));
				}
			}
		}
		return lstRetorno;
	}

	
	/**
	 * SQL Busca Custos Indiretos do Objeto de Custo parte 3.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia		Filtro pela competencia do mês.
	 * @param seqObjetoCusto		Filtro pelo Objeto custo.
	 * @return						Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarCustosIndiretosObjetoCustoParte3(Integer seqCompetencia, Integer seqObjetoCusto, Integer seqCentroCusto, Integer codigoDebita) {
		StringBuilder hql = new StringBuilder(350);
		hql.append(" SELECT ")
		.append(" cct." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME.toString() ).append( ", ")
		.append(" cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ", ");
		
		if(codigoDebita != null){
			hql.append(" cie." ).append( SigCalculoIndiretoEquipamento.Fields.ITERACAO.toString() ).append( ", ");
		}
		
		hql.append(" 0, ").append(" 0, ")
		.append(" sum(cie." ).append( SigCalculoIndiretoEquipamento.Fields.VALOR_EQUIPAMENTOS.toString() ).append( "), ")
		.append(" 0 ")
		.append(" FROM ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigCalculoIndiretoEquipamento.class.getSimpleName() ).append( " cie, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj_d, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")		
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " cct ")
		.append(" WHERE ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqProcessamento")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString()).append( " = :seqObjetoCusto")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()).append( " = :seqCentroCusto")
		.append(" AND cie." ).append( SigCalculoIndiretoEquipamento.Fields.CALCULO_OBJETO_CUSTO.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString()).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" AND cbj_d." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString())		
		.append(" AND cie." ).append( SigCalculoIndiretoEquipamento.Fields.CALCULO_OBJETO_CUSTO_DEBITA.toString() ).append( '.').append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj.").append( SigObjetoCustos.Fields.SEQ.toString())
		.append(" AND cct." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = cbj_d." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.').append( FccCentroCustos.Fields.CODIGO.toString());	
		
		if(codigoDebita != null){
			hql.append(" AND cbj_d.").append( SigCalculoObjetoCusto.Fields.SEQ.toString()).append(" = ").append(codigoDebita);
		}	
		
		hql.append(" GROUP BY ")
		.append(" cct." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME.toString()).append( ", ")
		.append(" cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() );
		
		if(codigoDebita != null){
			hql.append( ", ").append(" cie." ).append( SigCalculoIndiretoEquipamento.Fields.ITERACAO.toString() )
			.append(" ORDER BY ").append(" cie." ).append( SigCalculoIndiretoEquipamento.Fields.ITERACAO.toString() ).append( ", ");
		}
		else{
			hql.append(" ORDER BY ");
		}
		
		hql.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString());
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("seqProcessamento", seqCompetencia);
		createQuery.setParameter("seqObjetoCusto", seqObjetoCusto);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = null;
		if (valores != null && valores.size() > 0) {
			lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();
			for (Object[] objects : valores) {
				if(codigoDebita == null){
					lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoIndireto(objects, false));
				}
				else{
					lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoIndiretoIteracao(objects, false));
				}
			}
		}
		return lstRetorno;
	}
	
	/**
	 * SQL Busca Custos Indiretos do Objeto de Custo parte 4.
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia		Filtro pela competencia do mês.
	 * @param seqObjetoCusto		Filtro pelo Objeto custo.
	 * @return						Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarCustosIndiretosObjetoCustoParte4(Integer seqCompetencia, Integer seqObjetoCusto, Integer seqCentroCusto, Integer codigoDebita) {
		StringBuilder hql = new StringBuilder(400);
		hql.append(" SELECT ")
		.append(" cct." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME.toString() ).append( ", ")
		.append(" cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ", ");
		
		if(codigoDebita != null){
			hql.append(" cis." ).append( SigCalculoIndiretoServico.Fields.ITERACAO.toString() ).append( ", ");
		}
		
		hql.append(" 0, ").append(" 0, ").append(" 0, ")
		.append(" sum(cis." ).append( SigCalculoIndiretoServico.Fields.VALOR_SERVICOS.toString() ).append( ") ")
		.append(" FROM ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigCalculoIndiretoServico.class.getSimpleName() ).append( " cis, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj_d, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")		
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
		.append(FccCentroCustos.class.getSimpleName() ).append( " cct ")
		.append(" WHERE ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' )
		.append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqProcessamento")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' )
		.append( SigObjetoCustoVersoes.Fields.SEQ.toString()).append( " = :seqObjetoCusto")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()).append( " = :seqCentroCusto")
		.append(" AND cis." ).append( SigCalculoIndiretoServico.Fields.CALCULO_OBJETO_CUSTO.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString()).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" AND cbj_d." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocv.").append( SigObjetoCustoVersoes.Fields.SEQ.toString())		
		.append(" AND cis." ).append( SigCalculoIndiretoServico.Fields.CALCULO_OBJETO_CUSTO_DEBITA.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString()).append( " = cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append(SigObjetoCustos.Fields.SEQ.toString() ).append( " = obj.").append( SigObjetoCustos.Fields.SEQ.toString())		
		.append(" AND cct." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = cbj_d." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.').append( FccCentroCustos.Fields.CODIGO.toString());	
		
		if(codigoDebita != null){
			hql.append(" AND cbj_d.").append( SigCalculoObjetoCusto.Fields.SEQ.toString()).append(" = ").append(codigoDebita);
		}
			
		hql.append(" GROUP BY ")
		.append(" cct." ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append(" obj." ).append( SigObjetoCustos.Fields.NOME.toString()).append( ", ")
		.append(" cbj_d." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() );
		
		if(codigoDebita != null){
			hql.append( ", ").append(" cis." ).append( SigCalculoIndiretoServico.Fields.ITERACAO.toString() )
			.append(" ORDER BY ").append(" cis." ).append( SigCalculoIndiretoServico.Fields.ITERACAO.toString()).append( ", ");
		}
		else{
			hql.append(" ORDER BY ");
		}
		
		hql.append(" cct." ).append( FccCentroCustos.Fields.DESCRICAO.toString());
		
		org.hibernate.Query createQuery = this.createHibernateQuery(hql.toString());
		createQuery.setParameter("seqProcessamento", seqCompetencia);
		createQuery.setParameter("seqObjetoCusto", seqObjetoCusto);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = null;
		if (valores != null && valores.size() > 0) {
			lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();
			for (Object[] objects : valores) {
				if(codigoDebita == null){
					lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoIndireto(objects, false));
				}
				else{
					lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoIndiretoIteracao(objects, false));
				}
			}
		}
		return lstRetorno;
	}
	
	public List<ObjetoCustoProducaoExamesVO> pesquisarCentroCustoCalcRateio(Integer pmuSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoObjetoCusto.class);
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.groupProperty(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()),
						ObjetoCustoProducaoExamesVO.Fields.CENTRO_CUSTO.toString())
				.add(Projections.count(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()), ObjetoCustoProducaoExamesVO.Fields.QTDE_INTEIRA.toString()));
		criteria.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString(), pmuSeq));
		criteria.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.VALOR_ATV_INSUMOS.toString(), BigDecimal.ZERO));
		criteria.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.VALOR_ATV_PESSOAL.toString(), BigDecimal.ZERO));
		criteria.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.VALOR_ATV_EQUIPAMENTO.toString(), BigDecimal.ZERO));
		criteria.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.VALOR_ATV_SERVICO.toString(), BigDecimal.ZERO));
		criteria.setResultTransformer(Transformers.aliasToBean(ObjetoCustoProducaoExamesVO.class));
		return executeCriteria(criteria);
	}

	public List<ObjetoCustoProducaoExamesVO> pesquisarCentroCustoAtvAjusteQtde(Integer pmuSeq) {
		List<ObjetoCustoProducaoExamesVO> listCentroCustosPessoal = new ArrayList<ObjetoCustoProducaoExamesVO>();
		List<ObjetoCustoProducaoExamesVO> listCentroCustosInsumos = new ArrayList<ObjetoCustoProducaoExamesVO>();
		List<ObjetoCustoProducaoExamesVO> resultList = new ArrayList<ObjetoCustoProducaoExamesVO>();

		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoObjetoCusto.class, "cbj");

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.groupProperty(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()),
						ObjetoCustoProducaoExamesVO.Fields.CENTRO_CUSTO.toString())
				.add(Projections.count(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()), ObjetoCustoProducaoExamesVO.Fields.QTDE_INTEIRA.toString()));
		criteria.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString(), pmuSeq));

		DetachedCriteria criteriaCmt = criteria.createCriteria(SigCalculoObjetoCusto.Fields.LISTA_CALCULO_COMPONENTES_CBJSEQ.toString(), "cmt");

		DetachedCriteria criteriaCap = DetachedCriteria.forClass(SigCalculoAtividadePessoa.class, "cap");

		criteriaCap.add(Restrictions.eqProperty(
				"cap." + SigCalculoAtividadePessoa.Fields.CALCULO_COMPONENTE.toString() + '.' + SigCalculoComponente.Fields.SEQ.toString(), "cmt."
						+ SigCalculoComponente.Fields.SEQ.toString()));

		criteriaCap.setProjection(Projections.property(SigCalculoAtividadePessoa.Fields.CALCULO_COMPONENTE.toString() + '.'
				+ SigCalculoComponente.Fields.SEQ.toString()));
		criteriaCap.add(Restrictions.gtProperty(SigCalculoAtividadePessoa.Fields.QUANTIDADE_PREVISTA.toString(),
				SigCalculoAtividadePessoa.Fields.QUANTIDADE_REALIZADA.toString()));

		criteriaCmt.add(Subqueries.propertyIn("cmt." + SigCalculoComponente.Fields.SEQ.toString(), criteriaCap));
		criteria.setResultTransformer(Transformers.aliasToBean(ObjetoCustoProducaoExamesVO.class));

		DetachedCriteria criteriaUnion = DetachedCriteria.forClass(SigCalculoObjetoCusto.class, "cbj");

		criteriaUnion.setProjection(Projections
				.projectionList()
				.add(Projections.groupProperty(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()),
						ObjetoCustoProducaoExamesVO.Fields.CENTRO_CUSTO.toString())
				.add(Projections.count(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()), ObjetoCustoProducaoExamesVO.Fields.QTDE_INTEIRA.toString()));
		criteriaUnion.add(Restrictions.eq(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString(), pmuSeq));

		DetachedCriteria criteriaCmtUnion = criteriaUnion.createCriteria(SigCalculoObjetoCusto.Fields.LISTA_CALCULO_COMPONENTES_CBJSEQ.toString(), "cmt");

		DetachedCriteria criteriaCvn = DetachedCriteria.forClass(SigCalculoAtividadeInsumo.class, "cvn");
		criteriaCvn.setProjection(Projections.property(SigCalculoAtividadeInsumo.Fields.CALCULO_COMPONENTE.toString() + '.'
				+ SigCalculoComponente.Fields.SEQ.toString()));
		criteriaCvn.add(Restrictions.eqProperty(
				"cvn." + SigCalculoAtividadeInsumo.Fields.CALCULO_COMPONENTE.toString() + '.' + SigCalculoComponente.Fields.SEQ.toString(), "cmt."
						+ SigCalculoComponente.Fields.SEQ.toString()));
		criteriaCvn.add(Restrictions.gtProperty(SigCalculoAtividadeInsumo.Fields.QUANTIDADE_PREVISTA.toString(),
				SigCalculoAtividadeInsumo.Fields.QUANTIDADE_REALIZADA.toString()));
		criteriaCmtUnion.add(Subqueries.propertyIn("cmt." + SigCalculoComponente.Fields.SEQ.toString(), criteriaCvn));
		criteriaUnion.setResultTransformer(Transformers.aliasToBean(ObjetoCustoProducaoExamesVO.class));
		listCentroCustosPessoal = executeCriteria(criteria);
		listCentroCustosInsumos = executeCriteria(criteriaUnion);
		if ((listCentroCustosPessoal == null || listCentroCustosPessoal.isEmpty()) && listCentroCustosInsumos != null && !listCentroCustosInsumos.isEmpty()) {
			return listCentroCustosInsumos;
		}
		if ((listCentroCustosInsumos == null || listCentroCustosInsumos.isEmpty()) && listCentroCustosPessoal != null && !listCentroCustosPessoal.isEmpty()) {
			return listCentroCustosInsumos;
		}
		if (listCentroCustosPessoal == null || listCentroCustosPessoal.isEmpty() || listCentroCustosInsumos == null || listCentroCustosInsumos.isEmpty()) {
			return resultList;
		}
		resultList.addAll(listCentroCustosInsumos);
		for (ObjetoCustoProducaoExamesVO voPessoal : listCentroCustosPessoal) {
			FccCentroCustos ccPessoal = voPessoal.getFccCentroCustos();
			for (ObjetoCustoProducaoExamesVO voInsumos : listCentroCustosInsumos) {
				if (ccPessoal.getCodigo().intValue() == voInsumos.getFccCentroCustos().getCodigo().intValue()) {
					resultList.remove(voInsumos);
					break;
				}
			}
		}
		resultList.addAll(listCentroCustosPessoal);
		return resultList;
	}
}