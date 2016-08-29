package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseTabCustosObjetosCustoVO;

public class SigMvtoContaMensalServicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigMvtoContaMensal> {

	private static final long serialVersionUID = -3374643157628374303L;

	/**
	 * SQL Busca Movimentos de Serviços de Centro de Custo - Parte 1. 
	 *  
	 *  Contratos Automaticos
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia		Filtro pela Competencia do mês.
	 * @param seqCentroCusto		Filtro pelo Centro de custo.
	 * @return						Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicosCentroCustoParte1(Integer seqCompetencia, Integer seqCentroCusto, Integer tipoServico) {

		StringBuilder sql = new StringBuilder(700);

		sql.append(" SELECT ")
			.append("  af." ).append( ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString() ).append( " as contrato ")
			.append(", af." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " as item ")
			.append(", af." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString() ).append( " as compl ")
			.append(", frn." ).append( ScoFornecedor.Fields.NOME_FANTASIA.toString()).append(" as fornecedor ")
			.append(", srv." ).append( ScoServico.Fields.NOME.toString()).append(" as nome ")
			.append(", SUM (msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( ") as qtde ")
			
			.append(", SUM (CASE msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString())
				.append("  WHEN 'VAA' THEN msl." ).append(SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  ELSE 0 END) as total_alocado ")

			.append(", SUM (case msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString())
				.append("  WHEN 'VAR' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  WHEN 'VRG' THEN msl.").append(SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  ELSE 0 END) as total_rateado ")

			.append(", SUM (msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString()).append(") as vlr_nao_alocado ")

			.append(", SUM (case msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString())
				.append("  WHEN 'SIP' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  WHEN 'SIT' THEN msl.").append(SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  ELSE 0 END) as vlr_total ")

		.append(" FROM ")
			.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl, ")
			.append(ScoAfContrato.class.getSimpleName() ).append( " afc, ")
			.append(ScoAutorizacaoForn.class.getSimpleName() ).append( " af , ")
			.append(ScoServico.class.getSimpleName() ).append( " srv , ")
			.append(ScoFornecedor.class.getSimpleName() ).append( " frn , ")
			.append(ScoContrato.class.getSimpleName() ).append( " cont ")

		.append(" WHERE ")
			.append(" msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()	).append( " = :seqCompetencia")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :seqCentroCusto")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " in (:tipoMovimento) ")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = '" ).append( DominioTipoValorConta.DS.toString() ).append( '\'')
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.AF_CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( " = afc." ).append( ScoAfContrato.Fields.SEQ.toString())
			.append(" AND afc." ).append( ScoAfContrato.Fields.NUMERO_AUT_FORN.toString() ).append( " = af." ).append( ScoAutorizacaoForn.Fields.NUMERO.toString())
			.append(" AND srv." ).append( ScoServico.Fields.CODIGO.toString() ).append( " = msl." ).append( SigMvtoContaMensal.Fields.SERVICO.toString() ).append( '.' ).append( ScoServico.Fields.CODIGO.toString())
			.append(" AND cont." ).append( ScoContrato.Fields.SEQ.toString() ).append( " = afc." ).append( ScoAfContrato.Fields.CONTRATO.toString() ).append('.').append( ScoContrato.Fields.SEQ.toString())
			.append(" AND frn." ).append( ScoFornecedor.Fields.NUMERO.toString() ).append( " = cont." ).append( ScoContrato.Fields.FORNECEDOR.toString() ).append( '.' ).append( ScoFornecedor.Fields.NUMERO.toString())
							
		
		.append(" GROUP BY ")
			.append("  af." ).append( ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString())
			.append(", af." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
			.append(", af." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
			.append(", frn." ).append( ScoFornecedor.Fields.NOME_FANTASIA.toString())
			.append(", srv." ).append( ScoServico.Fields.NOME.toString());


		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		createQuery.setParameterList("tipoMovimento", new DominioTipoMovimentoConta[] { DominioTipoMovimentoConta.VAA, DominioTipoMovimentoConta.VAR,
				DominioTipoMovimentoConta.VRG, DominioTipoMovimentoConta.SIP, DominioTipoMovimentoConta.SIT });

		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoServicoCC(objects, false, tipoServico));
			}
		}

		return lstRetorno;
	}

	/**
	 * SQL Busca Movimentos de Serviços de Centro de Custo - Parte 2.
	 * 
	 *  Contratos Manuais
	 * 
	 * @author rmalvezzi
	 * @param seqCompetencia		Filtro pela Competencia do mês.
	 * @param seqCentroCusto		Filtro pelo Centro de custo.
	 * @return						Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicosCentroCustoParte2(Integer seqCompetencia, Integer seqCentroCusto, Integer tipoServico) {

		StringBuilder sql = new StringBuilder(700);

		sql.append(" SELECT ")
			.append("  co." ).append( ScoContrato.Fields.NR_CONTRATO.toString() ).append( " as contrato ")
			.append(", ico." ).append( ScoItensContrato.Fields.NR_ITEM.toString() ).append( " as item ")
			.append(", '' as compl ")
			.append(", frn." ).append( ScoFornecedor.Fields.NOME_FANTASIA.toString()).append(" as fornecedor ")
			.append(", srv." ).append( ScoServico.Fields.NOME.toString()).append(" as nome ")
			.append(", SUM (msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( ") as qtde ")
			.append(", SUM (CASE msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString())
				.append("  WHEN 'VAA' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  ELSE 0 END) as total_alocado ")
			.append(", SUM (case msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString())
				.append("  WHEN 'VAR' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  WHEN 'VRG' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  ELSE 0 END) as total_rateado ")
			.append(", SUM (msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") as vlr_nao_alocado ")
			
			.append(", SUM (case msl."  ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString())
				.append("  WHEN 'SIP' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  WHEN 'SIT' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  ELSE 0 END) as vlr_total ")
		
		.append(" FROM ")
			.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl, ")
			.append(ScoItensContrato.class.getSimpleName() ).append( " ico ,")
			.append(ScoContrato.class.getSimpleName() ).append( " co ,")
			.append(ScoServico.class.getSimpleName() ).append( " srv , ")
			.append(ScoFornecedor.class.getSimpleName() ).append( " frn ")

		
		.append(" WHERE ")
			.append(" msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString()).append( " = :seqCompetencia")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :seqCentroCusto")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " in (:tipoMovimento) ")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = '" ).append( DominioTipoValorConta.DS.toString() ).append( '\'')
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.ITENS_CONTRATO.toString() ).append( '.' ).append( ScoItensContrato.Fields.SEQ.toString() ).append( " = ico.").append( ScoItensContrato.Fields.SEQ.toString())
			.append(" AND ico." ).append( ScoItensContrato.Fields.CONT_SEQ.toString() ).append( " = co." ).append( ScoContrato.Fields.SEQ.toString())
    		.append(" AND srv." ).append( ScoServico.Fields.CODIGO.toString() ).append( " = msl." ).append( SigMvtoContaMensal.Fields.SERVICO.toString() ).append( '.' ).append( ScoServico.Fields.CODIGO.toString())
			.append(" AND frn." ).append( ScoFornecedor.Fields.NUMERO.toString() ).append( " = co." ).append( ScoContrato.Fields.FORNECEDOR.toString() ).append( '.' ).append( ScoFornecedor.Fields.NUMERO.toString())
			

		.append(" GROUP BY ")
			.append("  co." ).append( ScoContrato.Fields.NR_CONTRATO.toString())
			.append(", ico." ).append( ScoItensContrato.Fields.NR_ITEM.toString())
			.append(", frn." ).append( ScoFornecedor.Fields.NOME_FANTASIA.toString())
			.append(", srv." ).append( ScoServico.Fields.NOME.toString());

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		createQuery.setParameterList("tipoMovimento", new DominioTipoMovimentoConta[] { DominioTipoMovimentoConta.VAA, DominioTipoMovimentoConta.VAR,DominioTipoMovimentoConta.VRG, DominioTipoMovimentoConta.SIP, DominioTipoMovimentoConta.SIT });

		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoServicoCC(objects, false, tipoServico));
			}
		}

		return lstRetorno;
	}

	/**
	 * SQL Busca Movimentos de Serviços de Centro de Custo - Parte 3.
	 * 
	 *  AF
	 *  
	 *  
	 * @author rmalvezzi
	 * @param seqCompetencia		Filtro pela Competencia do mês.
	 * @param seqCentroCusto		Filtro pelo Centro de custo.
	 * @return						Lista de VisualizarAnaliseTabCustosObjetosCustoVO.
	 */
	@SuppressWarnings("unchecked")
	public List<VisualizarAnaliseTabCustosObjetosCustoVO> buscarMovimentosServicosCentroCustoParte3(Integer seqCompetencia, Integer seqCentroCusto, Integer tipoServico) {
		StringBuilder sql = new StringBuilder(700);
		sql.append(" SELECT ")
			.append("  '' as contrato ")
			.append(", af." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString() ).append( " as item ")
			.append(", af." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString() ).append( " as compl ")
			.append(", frn." ).append( ScoFornecedor.Fields.NOME_FANTASIA.toString()).append(" as fornecedor ")
			.append(", srv." ).append( ScoServico.Fields.NOME.toString()).append(" as nome ")
			.append(", SUM (msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( ") as qtde ")
			.append(", SUM (CASE msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString())
				.append("  WHEN 'VAA' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  ELSE 0 END) as total_alocado ")
			.append(", SUM (case msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString())
				.append("  WHEN 'VAR' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  WHEN 'VRG' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  ELSE 0 END) as total_rateado ")
			.append(", SUM (msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( ") as vlr_nao_alocado ")
			.append(", SUM (case msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString())
				.append("  WHEN 'SIP' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  WHEN 'SIT' THEN msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString())
				.append("  ELSE 0 END) as vlr_total ")
		
		.append(" FROM ")
			.append(SigMvtoContaMensal.class.getSimpleName() ).append( " msl, ")
			.append(ScoAutorizacaoForn.class.getSimpleName() ).append( " af, ")
			.append(ScoServico.class.getSimpleName() ).append( " srv , ")
			.append(ScoFornecedor.class.getSimpleName() ).append( " frn ")

		.append(" WHERE ")
			.append(" msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqCompetencia")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :seqCentroCusto")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " in (:tipoMovimento) ")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " = '" ).append( DominioTipoValorConta.DS.toString() ).append( '\'')
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.AF_CONTRATO.toString() ).append( '.' ).append( ScoAfContrato.Fields.SEQ.toString() ).append( " is null ")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.AUTORIZACAO_FORNEC.toString() ).append( '.' ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( " = af.").append( ScoAutorizacaoForn.Fields.NUMERO.toString())
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.SERVICO.toString() ).append( '.' ).append( ScoServico.Fields.CODIGO.toString() ).append( " = srv.").append( ScoServico.Fields.CODIGO.toString())
			.append(" AND frn." ).append( ScoFornecedor.Fields.NUMERO.toString() ).append( " =  af." ).append( ScoAutorizacaoForn.Fields.FORNECEDOR.toString()).append('.').append(ScoFornecedor.Fields.NUMERO.toString() )
			
		.append(" GROUP BY ")
			.append(" af." ).append( ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
			.append(", af." ).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString())
			.append(", frn." ).append( ScoFornecedor.Fields.NOME_FANTASIA.toString())
			.append(", srv." ).append( ScoServico.Fields.NOME.toString())
			;
		
		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqCompetencia", seqCompetencia);
		createQuery.setParameter("seqCentroCusto", seqCentroCusto);
		createQuery.setParameterList("tipoMovimento", new DominioTipoMovimentoConta[] { DominioTipoMovimentoConta.VAA, DominioTipoMovimentoConta.VAR,DominioTipoMovimentoConta.VRG, DominioTipoMovimentoConta.SIP, DominioTipoMovimentoConta.SIT });

		List<Object[]> valores = createQuery.list();
		List<VisualizarAnaliseTabCustosObjetosCustoVO> lstRetorno = new ArrayList<VisualizarAnaliseTabCustosObjetosCustoVO>();

		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.add(VisualizarAnaliseTabCustosObjetosCustoVO.createMovimentoServicoCC(objects, false, tipoServico));
			}
		}
		return lstRetorno;
	}

}