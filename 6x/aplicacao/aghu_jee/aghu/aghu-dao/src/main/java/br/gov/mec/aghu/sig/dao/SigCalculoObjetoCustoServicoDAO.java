package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.SigAtividadeServicos;
import br.gov.mec.aghu.model.SigCalculoAtividadeServico;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCalculoRateioServico;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseTabCustosObjetosCustoVO;

public class SigCalculoObjetoCustoServicoDAO extends  br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoObjetoCusto> {

	private static final long serialVersionUID = 7418655426678789677L;


	/**
	 * SQL Busca Movimentos de Serviços do Objeto de Custo - Parte 1.
	 * 
	 *  Contratos automaticos
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro da Compentencia do mês. 
	 * @param seqObjetoVersao			Filtro pelo Objeto Custo Versão.
	 * @return							Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicosObjetoCustoParte1(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto, Integer tipoServico) {

		StringBuilder sql = new StringBuilder(500);

		sql.append(" SELECT ")
				.append("  af." ).append( ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString() ).append( " as contrato ")
				.append(", af." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " as item ")
				.append(", af." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString() ).append( " as compl ")
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString() ).append(" as fornecedor ")
				.append(", srv.").append(ScoServico.Fields.NOME.toString() ).append(" as servico ")
				.append(", sum(cas." ).append( SigCalculoAtividadeServico.Fields.PESO.toString() ).append( ") as qtde ")
				.append(", sum(cas." ).append( SigCalculoAtividadeServico.Fields.VALOR_ITEM_CONTRATO.toString() ).append( ") as vlr_atividade ")
				.append(", 0 as vlr_rateio ")

		.append(" FROM ")
				.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ,")
				.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt ,")
				.append(SigCalculoAtividadeServico.class.getSimpleName() ).append( " cas ,")
				.append(SigAtividadeServicos.class.getSimpleName() ).append( " avs ,")
				.append(ScoAfContrato.class.getSimpleName() ).append( " afc ,")
				.append(ScoAutorizacaoForn.class.getSimpleName() ).append( " af , ")
				.append(ScoServico.class.getSimpleName() ).append( " srv , ")
				.append(ScoFornecedor.class.getSimpleName() ).append( " frn , ")
				.append(ScoContrato.class.getSimpleName() ).append( " cont " )


		.append(" WHERE ")
				.append("     cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString()	).append( " = :seqObjetoVersao")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString()	).append( " = :seqCentroCusto")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
				.append(" AND cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " = cas." ).append( SigCalculoAtividadeServico.Fields.CALCULO_COMPONENTE.toString() 	).append( '.' ).append( SigCalculoComponente.Fields.SEQ.toString())
				.append(" AND cas." ).append( SigCalculoAtividadeServico.Fields.ATIVIDADE_SERVICOS.toString() ).append( '.' ).append( SigAtividadeServicos.Fields.SEQ.toString() ).append( " = avs." ).append( SigAtividadeServicos.Fields.SEQ.toString())
				.append(" AND avs." ).append( SigAtividadeServicos.Fields.CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( " = afc." ).append( ScoAfContrato.Fields.SEQ.toString())
				.append(" AND afc." ).append( ScoAfContrato.Fields.NUMERO_AUT_FORN.toString() ).append( " = af." ).append( ScoAutorizacaoForn.Fields.NUMERO.toString())
				.append(" AND cas." ).append( SigCalculoAtividadeServico.Fields.SERVICO.toString()).append('.' ).append(ScoServico.Fields.CODIGO.toString() ).append( " = srv.").append(ScoServico.Fields.CODIGO.toString())
				.append(" AND cont." ).append( ScoContrato.Fields.SEQ.toString() ).append(" = afc." ).append( ScoAfContrato.Fields.CONTRATO.toString() ).append('.'  ).append( ScoContrato.Fields.SEQ.toString())
				.append(" AND frn." ).append( ScoFornecedor.Fields.NUMERO.toString() ).append( " = cont." ).append( ScoContrato.Fields.FORNECEDOR.toString() ).append( '.' ).append( ScoFornecedor.Fields.NUMERO.toString() )
				

		.append(" GROUP BY ")
				.append("  af." ).append( ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString())
				.append(", af." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
				.append(", af." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString())
				.append(", srv.").append(ScoServico.Fields.NOME.toString());

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoVersao", seqObjetoVersao);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);

		List<Object[]> valores = createQuery.list();
		
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoServico(objects, true, tipoServico));
			}
		}

		return lstRetorno;
	}

	/**
	 * SQL Busca Movimentos de Serviços do Objeto de Custo - Parte 2.
	 *  
	 *  Contratos Manuais associado a atividades
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro da Compentencia do mês. 
	 * @param seqObjetoVersao			Filtro pelo Objeto Custo Versão.
	 * @return							Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicosObjetoCustoParte2(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,  Integer tipoServico) {

		StringBuilder sql = new StringBuilder(500);

		sql.append(" SELECT ")
				.append("  co." ).append( ScoContrato.Fields.NR_CONTRATO.toString() ).append( " as contrato ")
				.append(", ico." ).append( ScoItensContrato.Fields.NR_ITEM.toString() ).append( " as item ")
				.append(", '' as compl ")
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString() ).append(" as fornecedor ")
				.append(", srv.").append(ScoServico.Fields.NOME.toString() ).append(" as servico ")
				.append(", sum(cas." ).append( SigCalculoAtividadeServico.Fields.PESO.toString() ).append( ") as qtde ")
				.append(", sum(cas." ).append( SigCalculoAtividadeServico.Fields.VALOR_ITEM_CONTRATO.toString() ).append( ") as vlr_atividade ")
				.append(", 0 as vlr_rateio ")

		.append(" FROM ")
				.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ,")
				.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt ,")
				.append(SigCalculoAtividadeServico.class.getSimpleName() ).append( " cas ,")
				.append(SigAtividadeServicos.class.getSimpleName() ).append( " avs , ")
				.append(ScoItensContrato.class.getSimpleName() ).append( " ico , ")
				.append(ScoContrato.class.getSimpleName() ).append( " co ,  ")
				.append(ScoServico.class.getSimpleName() ).append( " srv , " )
				.append(ScoFornecedor.class.getSimpleName() ).append( " frn  ")

		.append(" WHERE ")
				.append("     cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append('.').append( SigObjetoCustoVersoes.Fields.SEQ.toString() 	).append( " = :seqObjetoVersao")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append('.').append( FccCentroCustos.Fields.CODIGO.toString()	).append( " = :seqCentroCusto")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append('.').append( SigCalculoObjetoCusto.Fields.SEQ.toString())
				.append(" AND cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " = cas." ).append( SigCalculoAtividadeServico.Fields.CALCULO_COMPONENTE.toString() ).append('.').append( SigCalculoComponente.Fields.SEQ.toString())
				.append(" AND cas." ).append( SigCalculoAtividadeServico.Fields.ATIVIDADE_SERVICOS.toString() ).append('.').append( SigAtividadeServicos.Fields.SEQ.toString() ).append( " = avs." ).append( SigAtividadeServicos.Fields.SEQ.toString())
				.append(" AND avs." ).append( SigAtividadeServicos.Fields.ITEM_CONTRATO.toString() ).append('.').append( ScoItensContrato.Fields.SEQ.toString() ).append( " = ico." ).append( ScoItensContrato.Fields.SEQ.toString())
				.append(" AND ico." ).append( ScoItensContrato.Fields.CONT_SEQ.toString() ).append( " = co." ).append( ScoContrato.Fields.SEQ.toString())
				.append(" AND cas." ).append( SigCalculoAtividadeServico.Fields.SERVICO.toString()).append('.').append(ScoServico.Fields.CODIGO.toString() ).append( " = srv.").append(ScoServico.Fields.CODIGO.toString())
				.append(" AND frn.").append(ScoFornecedor.Fields.NUMERO.toString() ).append(" = co.").append(ScoContrato.Fields.FORNECEDOR.toString()).append('.').append( ScoFornecedor.Fields.NUMERO.toString() )
				

		.append(" GROUP BY ")
				.append("  co." ).append( ScoContrato.Fields.NR_CONTRATO.toString())
				.append(", ico." ).append( ScoItensContrato.Fields.NR_ITEM.toString())
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString())
				.append(", srv.").append(ScoServico.Fields.NOME.toString());


		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoVersao", seqObjetoVersao);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoServico(objects, true, tipoServico));
			}
		}

		return lstRetorno;
	}

	/**
	 * SQL Busca Movimentos de Serviços do Objeto de Custo - Parte 3.
	 * 
	 * Afs Associado as atividades
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro da Compentencia do mês. 
	 * @param seqObjetoVersao			Filtro pelo Objeto Custo Versão.
	 * @return							Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicosObjetoCustoParte3(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto, Integer tipoServico) {
		StringBuilder sql = new StringBuilder(450);

		sql.append(" SELECT ")
				.append("  '' as contrato ")
				.append(", af." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " as item ")
				.append(", af." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString() ).append( " as compl ")
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString() ).append(" as fornecedor ")
				.append(", srv.").append(ScoServico.Fields.NOME.toString() ).append(" as servico ")
				.append(", sum(cas." ).append( SigCalculoAtividadeServico.Fields.PESO.toString() ).append( ") as qtde ")
				.append(", sum(cas." ).append( SigCalculoAtividadeServico.Fields.VALOR_ITEM_CONTRATO.toString() ).append( ") as vlr_atividade ")
				.append(", sum(0) as vlr_rateio  ")

		.append(" FROM ")
				.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ,")
				.append(SigCalculoComponente.class.getSimpleName() ).append( " cmt ,")
				.append(SigCalculoAtividadeServico.class.getSimpleName() ).append( " cas ,")
				.append(SigAtividadeServicos.class.getSimpleName() ).append( " avs ,")
				.append(ScoAutorizacaoForn.class.getSimpleName() ).append( " af , ")
				.append(ScoServico.class.getSimpleName() ).append( " srv , " )
				.append(ScoFornecedor.class.getSimpleName() ).append( " frn  ")
				

		.append(" WHERE ")
				.append("     cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append('.').append( SigObjetoCustoVersoes.Fields.SEQ.toString()).append( " = :seqObjetoVersao")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append('.').append( FccCentroCustos.Fields.CODIGO.toString()	).append( " = :seqCentroCusto")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append('.').append( SigCalculoObjetoCusto.Fields.SEQ.toString())
				.append(" AND cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " = cas." ).append( SigCalculoAtividadeServico.Fields.CALCULO_COMPONENTE.toString() ).append('.').append( SigCalculoComponente.Fields.SEQ.toString())
				.append(" AND cas." ).append( SigCalculoAtividadeServico.Fields.ATIVIDADE_SERVICOS.toString() ).append('.').append( SigAtividadeServicos.Fields.SEQ.toString() ).append( " = avs." ).append( SigAtividadeServicos.Fields.SEQ.toString())
				.append(" AND avs." ).append( SigAtividadeServicos.Fields.AUTORIZACAO_FORNEC.toString() ).append( " = af." ).append( ScoAutorizacaoForn.Fields.NUMERO.toString())
		
				.append(" AND cas." ).append( SigCalculoAtividadeServico.Fields.SERVICO.toString()).append('.').append(ScoServico.Fields.CODIGO.toString() ).append( " = srv.").append(ScoServico.Fields.CODIGO.toString())
				.append(" AND frn.").append(ScoFornecedor.Fields.NUMERO.toString()).append(" = af.").append(ScoAutorizacaoForn.Fields.FORNECEDOR.toString()).append('.').append(ScoFornecedor.Fields.NUMERO.toString())
				

		.append(" GROUP BY ")
				.append(" af." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
				.append(", af." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString())
				.append(", srv.").append(ScoServico.Fields.NOME.toString());


		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoVersao", seqObjetoVersao);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);

		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoServico(objects, true, tipoServico));
			}
		}

		return lstRetorno;
	}

	/**
	 * SQL Busca Movimentos de Serviços do Objeto de Custo - Parte 4.
	 * 
	 * Contratos automáticos rateados
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro da Compentencia do mês. 
	 * @param seqObjetoVersao			Filtro pelo Objeto Custo Versão.
	 * @return							Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicosObjetoCustoParte4(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto, Integer tipoServico) {

		StringBuilder sql = new StringBuilder(450);

		sql.append(" SELECT ")
				.append("  af." ).append( ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString() ).append( " as contrato ")
				.append(", af." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " as item ")
				.append(", af." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString() ).append( " as compl ")
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString() ).append(" as fornecedor ")
				.append(", srv.").append(ScoServico.Fields.NOME.toString() ).append(" as servico ")
				.append(", sum(crs." ).append( SigCalculoRateioServico.Fields.QUANTIDADE.toString() ).append( ") as qtde ")
				.append(", 0 as vlr_atividade ")
				.append(", sum(crs." ).append( SigCalculoRateioServico.Fields.VALOR_ITEM_CONTRATO.toString() ).append( ") as vlr_rateio ")

		.append(" FROM ")
				.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ,")
				.append(SigCalculoRateioServico.class.getSimpleName() ).append( " crs ,")
				.append(ScoAfContrato.class.getSimpleName() ).append( " afc ,")
				.append(ScoAutorizacaoForn.class.getSimpleName() ).append( " af , ")
				.append(ScoServico.class.getSimpleName() ).append( " srv , " )
				.append(ScoFornecedor.class.getSimpleName() ).append( " frn , ")
				.append(ScoContrato.class.getSimpleName() ).append( " cont ")
				

		.append(" WHERE ")
				.append("     cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append('.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = :seqObjetoVersao")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append('.' ).append( FccCentroCustos.Fields.CODIGO.toString()	).append( " = :seqCentroCusto")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = crs." ).append( SigCalculoRateioServico.Fields.CALCULO_OBJETO_CUSTO_SEQ.toString())
				.append(" AND crs." ).append( SigCalculoRateioServico.Fields.AF_CONTRATO_SEQ.toString() ).append( " = afc." ).append( ScoAfContrato.Fields.SEQ.toString())
				.append(" AND afc." ).append( ScoAfContrato.Fields.NUMERO_AUT_FORN.toString() ).append( " = af." ).append( ScoAutorizacaoForn.Fields.NUMERO.toString())
				.append(" AND srv." ).append( ScoServico.Fields.CODIGO.toString() ).append( " = crs." ).append( SigCalculoRateioServico.Fields.SERVICO.toString() ).append('.' ).append( ScoServico.Fields.CODIGO.toString() )
				.append(" AND cont." ).append( ScoContrato.Fields.SEQ.toString() ).append( " = afc." ).append( ScoAfContrato.Fields.CONTRATO.toString() ).append('.' ).append( ScoContrato.Fields.SEQ.toString())
				.append(" AND frn. ").append( ScoFornecedor.Fields.NUMERO.toString() ).append( " =  cont." ).append( ScoContrato.Fields.FORNECEDOR.toString() ).append('.').append( ScoFornecedor.Fields.NUMERO.toString())
				

		.append(" GROUP BY ")
				.append("  af." ).append( ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString())
				.append(", af." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
				.append(", af." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString())
				.append(", srv.").append(ScoServico.Fields.NOME.toString());

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoVersao", seqObjetoVersao);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		List<Object[]> valores = createQuery.list(); 
		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoServico(objects, true, tipoServico));
			}
		}
		return lstRetorno;
	}

	/**
	 * SQL Busca Movimentos de Serviços do Objeto de Custo - Parte 5.
	 * 
	 * Contratos manuais rateados
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia			Filtro da Compentencia do mês. 
	 * @param seqObjetoVersao			Filtro pelo Objeto Custo Versão.
	 * @return							Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicosObjetoCustoParte5(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto,  Integer tipoServico) {

		StringBuilder sql = new StringBuilder(450);

		sql.append(" SELECT ")
				.append("  co." ).append( ScoContrato.Fields.NR_CONTRATO.toString() ).append( " as contrato ")
				.append(", ico." ).append( ScoItensContrato.Fields.NR_ITEM.toString() ).append( " as item ")
				.append(", '' as compl ")
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString() ).append(" as fornecedor ")
				.append(", srv.").append(ScoServico.Fields.NOME.toString() ).append(" as servico ")
				.append(", sum(crs." ).append( SigCalculoRateioServico.Fields.QUANTIDADE.toString() ).append( ") as qtde ")
				.append(", 0 as vlr_atividade ")
				.append(", sum(crs." ).append( SigCalculoRateioServico.Fields.VALOR_ITEM_CONTRATO.toString() ).append( ") as vlr_rateio ")

		.append(" FROM ")
				.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ,")
				.append(SigCalculoRateioServico.class.getSimpleName() ).append( " crs ,")
				.append(ScoItensContrato.class.getSimpleName() ).append( " ico , ")
				.append(ScoContrato.class.getSimpleName() ).append( " co, ")
				.append(ScoServico.class.getSimpleName() ).append( " srv , " )
				.append(ScoFornecedor.class.getSimpleName() ).append( " frn ")


		.append(" WHERE ")
				.append("     cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append('.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString()	).append( " = :seqObjetoVersao")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append('.' ).append( FccCentroCustos.Fields.CODIGO.toString()	).append( " = :seqCentroCusto")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = crs.").append( SigCalculoRateioServico.Fields.CALCULO_OBJETO_CUSTO_SEQ.toString())
				.append(" AND crs." ).append( SigCalculoRateioServico.Fields.ITENS_CONTRATO_SEQ.toString() ).append( " = ico." ).append( ScoItensContrato.Fields.SEQ.toString())
				.append(" AND ico." ).append( ScoItensContrato.Fields.CONT_SEQ.toString() ).append( " = co." ).append( ScoContrato.Fields.SEQ.toString())
				.append(" AND srv." ).append( ScoServico.Fields.CODIGO.toString() ).append( " = crs." ).append( SigCalculoRateioServico.Fields.SERVICO.toString() ).append('.' ).append( ScoServico.Fields.CODIGO.toString() )
				.append(" AND frn." ).append( ScoFornecedor.Fields.NUMERO.toString() ).append( " = co.").append( ScoContrato.Fields.FORNECEDOR.toString() ).append('.' ).append(ScoFornecedor.Fields.NUMERO.toString() )
				
		
		.append(" GROUP BY ")
				.append("  co." ).append( ScoContrato.Fields.NR_CONTRATO.toString())
				.append(", ico." ).append( ScoItensContrato.Fields.NR_ITEM.toString())
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString())
				.append(", srv.").append(ScoServico.Fields.NOME.toString());

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoVersao", seqObjetoVersao);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);

		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoServico(objects, true, tipoServico));
			}
		}

		return lstRetorno;
	}
	
	/**
	 * SQL Busca Movimentos de Serviços do Objeto de Custo - Parte 6.
	 * 
	 * AFs rateados
	 * 
	 * @author jgugel
	 * @param seqCompetencia			Filtro da Compentencia do mês. 
	 * @param seqObjetoVersao			Filtro pelo Objeto Custo Versão.
	 * @return							Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicosObjetoCustoParte6(Integer seqCompetencia, Integer seqObjetoVersao, Integer seqCentroCusto, Integer tipoServico) {

		StringBuilder sql = new StringBuilder(450);

		sql.append(" SELECT ")
				.append("  '' as contrato  ")
				.append(", afn." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " as item ")
				.append(", afn." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString() ).append( " as compl ")
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString() ).append(" as fornecedor ")
				.append(", srv.").append(ScoServico.Fields.NOME.toString() ).append(" as servico ")
				.append(", sum(crs." ).append( SigCalculoRateioServico.Fields.QUANTIDADE.toString() ).append( ") as qtde ")
				.append(", 0 as vlr_atividade ")
				.append(", sum(crs." ).append( SigCalculoRateioServico.Fields.VALOR_ITEM_CONTRATO.toString() ).append( ") as vlr_rateio ")

		.append(" FROM ")
				.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj ,")
				.append(SigCalculoRateioServico.class.getSimpleName() ).append( " crs ,")
				.append(ScoFaseSolicitacao.class.getSimpleName() ).append( " fsc , ")
				.append(ScoAutorizacaoForn.class.getSimpleName() ).append( " afn, ")
				.append(ScoServico.class.getSimpleName() ).append( " srv , " )
				.append(ScoFornecedor.class.getSimpleName() ).append( " frn ")


		.append(" WHERE ")
				.append("     cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqCompetencia")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append('.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString()	).append( " = :seqObjetoVersao")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append('.' ).append( FccCentroCustos.Fields.CODIGO.toString()	).append( " = :seqCentroCusto")
				.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = crs.").append( SigCalculoRateioServico.Fields.CALCULO_OBJETO_CUSTO_SEQ.toString())
				.append(" AND afn." ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( " = crs.").append(SigCalculoRateioServico.Fields.AUTORIZACAO_FORNECEDOR.toString() ).append('.').append(  ScoAutorizacaoForn.Fields.NUMERO.toString())
				.append(" AND frn." ).append( ScoFornecedor.Fields.NUMERO.toString() ).append( " = afn.").append(ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString() )
				.append(" AND fsc." ).append( ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString() ).append( " = afn.").append(ScoAutorizacaoForn.Fields.NUMERO.toString() )
				.append(" AND fsc." ).append( ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString() ).append( " = 'N' " )
				.append(" AND srv." ).append( ScoServico.Fields.CODIGO.toString() ).append( " = crs." ).append( SigCalculoRateioServico.Fields.SERVICO.toString() ).append('.' ).append( ScoServico.Fields.CODIGO.toString() )
				
				
		
		.append(" GROUP BY ")
				.append(" afn." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
				.append(", afn." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
				.append(", frn.").append(ScoFornecedor.Fields.NOME_FANTASIA.toString())
				.append(", srv.").append(ScoServico.Fields.NOME.toString());

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqObjetoVersao", seqObjetoVersao);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);


		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoServico(objects, true, tipoServico));
			}
		}

		return lstRetorno;
	}
}