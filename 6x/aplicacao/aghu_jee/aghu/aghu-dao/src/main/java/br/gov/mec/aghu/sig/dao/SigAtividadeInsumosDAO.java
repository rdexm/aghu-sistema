package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoAtividadeInsumo;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProducaoObjetoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;



public class SigAtividadeInsumosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigAtividadeInsumos> {

	private static final long serialVersionUID = -7227642137337833416L;

	public List<SigAtividadeInsumos> pesquisarAtividadeInsumos(Integer seqAtividade) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividadeInsumos.class, "atvInsumos");
		criteria.createAlias("atvInsumos."+SigAtividadeInsumos.Fields.MATERIAL.toString(), "mat", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("mat."+ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "um", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atvInsumos."+SigAtividadeInsumos.Fields.DIRECIONADORES.toString(), "dir", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("atvInsumos."+SigAtividadeInsumos.Fields.SIG_ATIVIDADES.toString(), "atv", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("atv."+SigAtividades.Fields.SEQ.toString(), seqAtividade));
		
		//#24619 - Materiais: situação, nome do material
		criteria.addOrder(Order.asc("atvInsumos."+SigAtividadeInsumos.Fields.IND_SITUACAO.toString()));
		criteria.addOrder(Order.asc("mat."+ScoMaterial.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
		
	/**
	 * Validado a busca de Insumos para assistencial.
	 * Consulta validada tanto no PostgreSQL como no Oracle
	 * @param processamento - Objeto que está sendo processado
	 * @param grupoDetalheProducao - Grupo para seleção Default PHI
	 * @return retorna a seleção dos insumos alocados na atividades
	 */
	public ScrollableResults buscarInsumosAlocadosNaAtividadeAssistencial(SigProcessamentoCusto processamento){
		
		StringBuilder sql = new StringBuilder(900);
		sql.append("SELECT ");
		sql.append(" tvd.").append(SigAtividades.Fields.SEQ.toString()).append(" as tvd_seq, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.SEQ.toString()).append(" as ais_seq, ");
		sql.append(" cmt.").append(SigCalculoComponente.Fields.SEQ.toString()).append(" as cmt_seq, ");
		sql.append(" cbj.").append(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()).append(".id as cct_codigo, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.MATERIAL.toString()).append(".id as mat_codigo, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.UNIDADE_MEDIDA.toString()).append(".id as un_uso, ");
		sql.append(" mat.").append(ScoMaterial.Fields.UNIDADE_MEDIDA.toString()).append(".id as un_material, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.QTD_EUSO.toString()).append(" as qtde_uso, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.VIDA_UTIL_QTDE.toString()).append(" as vida_util_qtde, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.VIDA_UTIL_TEMPO.toString()).append(" as vida_util_tempo, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.DIRECIONADORES.toString()).append(".seq as dir_seq_tempo, ");
		sql.append(" cmt.").append(SigCalculoComponente.Fields.DIRECIONADOR.toString()).append(".seq as dir_seq_atividade, ");
		sql.append(" cbt.").append(SigObjetoCustoComposicoes.Fields.NRO_EXECUCAO.toString()).append(", ");
		sql.append(" pmu.").append(SigProcessamentoCusto.Fields.DATA_INICIO.toString()).append(" as proc_data_inicio, ");
		sql.append(" pmu.").append(SigProcessamentoCusto.Fields.DATA_FIM.toString()).append(" as proc_data_fim, ");
		sql.append(" SUM (dhp.").append(SigDetalheProducao.Fields.NRO_DIAS_PRODUCAO.toString()).append("), ");
		sql.append(" SUM (dhp.").append(SigDetalheProducao.Fields.QTDE.toString()).append(") ");

		sql.append(" FROM ").append(SigProcessamentoCusto.class.getSimpleName()).append(" pmu, ");
		sql.append(' ').append(SigDetalheProducao.class.getSimpleName()).append(" dhp, ");
		sql.append(' ').append(SigProducaoObjetoCusto.class.getSimpleName()).append(" pjc, ");
		sql.append(' ').append(SigCalculoObjetoCusto.class.getSimpleName()).append(" cbj, ");
		sql.append(' ').append(SigObjetoCustoVersoes.class.getSimpleName()).append(" ocv, ");
		sql.append(' ').append(SigObjetoCustos.class.getSimpleName()).append(" obj, ");
		sql.append(' ').append(SigCalculoComponente.class.getSimpleName()).append(" cmt, ");
		sql.append(' ').append(SigObjetoCustoComposicoes.class.getSimpleName()).append(" cbt, ");
		sql.append(' ').append(SigAtividades.class.getSimpleName()).append(" tvd, ");
		sql.append(' ').append(SigAtividadeInsumos.class.getSimpleName()).append(" ais, ");
		sql.append(' ').append(ScoMaterial.class.getSimpleName()).append(" mat ");
		
		sql.append(" WHERE pmu.").append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :seqProcessamento ");
		sql.append(" AND dhp.").append(SigDetalheProducao.Fields.GRUPO.toString()).append(" in (:grupos) ");
		
		sql.append(" AND pmu.").append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = dhp.").append(SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO.toString()).append( "  ");
		sql.append(" AND pjc.").append(SigProducaoObjetoCusto.Fields.DETALHE_PRODUCAO.toString()).append(" = dhp.").append(SigDetalheProducao.Fields.SEQ.toString()).append(' ');
		sql.append(" AND pjc.").append(SigProducaoObjetoCusto.Fields.CALCULO_OBJETO_CUSTOS.toString()).append(" = cbj.").append(SigCalculoObjetoCusto.Fields.SEQ.toString()).append(' ');
		sql.append(" AND cmt.").append(SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString()).append(" = cbj.").append(SigCalculoObjetoCusto.Fields.SEQ.toString()).append(' ');
		sql.append(" AND cmt.").append(SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO.toString()).append(" = cbt.").append(SigObjetoCustoComposicoes.Fields.SEQ.toString()).append("  ");
		sql.append(" AND cbt.").append(SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString()).append(" = tvd.").append(SigAtividades.Fields.SEQ.toString()).append(' ');
		sql.append(" AND tvd.").append(SigAtividades.Fields.SEQ.toString()).append(" = ais.").append(SigAtividadeInsumos.Fields.SIG_ATIVIDADES.toString()).append(' ');
		sql.append(" AND ais.").append(SigAtividadeInsumos.Fields.MATERIAL_CODIGO.toString()).append(" = mat.").append(ScoMaterial.Fields.CODIGO.toString()).append( ' ');
		sql.append(" AND cbj.").append(SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString()).append(" = ocv.").append(SigObjetoCustoVersoes.Fields.SEQ.toString()).append( ' ');
		sql.append(" AND ocv.").append(SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString()).append( " = obj.").append(SigObjetoCustos.Fields.SEQ.toString()).append( ' ');
		sql.append(" AND obj.").append(SigObjetoCustos.Fields.IND_TIPO.toString()).append( " = '").append(DominioTipoObjetoCusto.AS).append("' ");
		
		sql.append(" GROUP BY  ");
		sql.append(" tvd.").append(SigAtividades.Fields.SEQ.toString()).append(", ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.SEQ.toString()).append(", ");
		sql.append(" cmt.").append(SigCalculoComponente.Fields.SEQ.toString()).append(", ");
		sql.append(" cbj.").append(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()).append(", ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.MATERIAL.toString()).append(", ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.UNIDADE_MEDIDA.toString()).append(", ");
		sql.append(" mat.").append(ScoMaterial.Fields.CODIGO.toString()).append(", ");
		sql.append(" mat.").append(ScoMaterial.Fields.UNIDADE_MEDIDA.toString()).append(".id, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.QTD_EUSO.toString()).append(", ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.VIDA_UTIL_QTDE.toString()).append(", ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.VIDA_UTIL_TEMPO.toString()).append(", ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.DIRECIONADORES.toString()).append(", ");
		sql.append(" cmt.").append(SigCalculoComponente.Fields.DIRECIONADOR.toString()).append(", ");
		sql.append(" cbt.").append(SigObjetoCustoComposicoes.Fields.NRO_EXECUCAO.toString()).append(", ");
		sql.append(" pmu.").append(SigProcessamentoCusto.Fields.DATA_INICIO.toString()).append(", ");
		sql.append(" pmu.").append(SigProcessamentoCusto.Fields.DATA_FIM.toString()).append(' ');
		
		Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqProcessamento", processamento.getSeq());
		createQuery.setParameterList("grupos", new DominioGrupoDetalheProducao[]{ DominioGrupoDetalheProducao.PHI, DominioGrupoDetalheProducao.PAC});
		
		return createQuery.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}
	
	
	/**
	 * Metodos de que busca os insumos alocados nas atividades do tipo Apoio
	 * @param processamento - Processamento mensal sendo executado
	 * @return retorna o resultado da consulta
	 */
	public ScrollableResults buscaInsumosAlocadosNaAtividadeApoio(SigProcessamentoCusto processamento){
		
		StringBuilder sql = new StringBuilder(700);
		sql.append("SELECT ");
		sql.append(" tvd.").append(SigAtividades.Fields.SEQ.toString()).append(" as tvd_seq, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.SEQ.toString()).append(" as ais_seq, ");
		sql.append(" cmt.").append(SigCalculoComponente.Fields.SEQ.toString()).append(" as cmt_seq, ");
		sql.append(" cbj.").append(SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString()).append(".id as cct_codigo, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.MATERIAL.toString()).append(".id as mat_codigo, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.UNIDADE_MEDIDA.toString()).append(".id as un_uso, ");
		sql.append(" mat.").append(ScoMaterial.Fields.UNIDADE_MEDIDA.toString()).append(".id as un_material, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.QTD_EUSO.toString()).append(" as qtde_uso, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.VIDA_UTIL_QTDE.toString()).append(" as vida_util_qtde, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.VIDA_UTIL_TEMPO.toString()).append(" as vida_util_tempo, ");
		sql.append(" ais.").append(SigAtividadeInsumos.Fields.DIRECIONADORES.toString()).append(".seq as dir_seq_tempo, ");
		sql.append(" cbt.").append(SigObjetoCustoComposicoes.Fields.NRO_EXECUCAO.toString()).append(", ");
		sql.append(" pmu.").append(SigProcessamentoCusto.Fields.DATA_FIM.toString()).append(" as proc_data_inicio, ");
		sql.append(" pmu.").append(SigProcessamentoCusto.Fields.DATA_INICIO.toString()).append(" as proc_data_fim ");

		sql.append(" FROM ").append(SigProcessamentoCusto.class.getSimpleName()).append(" pmu, ");
		sql.append(' ').append(SigCalculoObjetoCusto.class.getSimpleName()).append(" cbj, ");
		sql.append(' ').append(SigObjetoCustoVersoes.class.getSimpleName()).append(" ocv, ");
		sql.append(' ').append(SigObjetoCustos.class.getSimpleName()).append(" obj, ");
		sql.append(' ').append(SigCalculoComponente.class.getSimpleName()).append(" cmt, ");
		sql.append(' ').append(SigObjetoCustoComposicoes.class.getSimpleName()).append(" cbt, ");
		sql.append(' ').append(SigAtividades.class.getSimpleName()).append(" tvd, ");
		sql.append(' ').append(SigAtividadeInsumos.class.getSimpleName()).append(" ais, ");
		sql.append(' ').append(ScoMaterial.class.getSimpleName()).append(" mat ");

		sql.append(" WHERE pmu.").append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :seqProcessamento ");
		sql.append(" AND pmu.").append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = cbj.").append(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString()).append(' ');
		sql.append(" AND cbj.").append(SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString()).append(" =        ocv.").append(SigObjetoCustoVersoes.Fields.SEQ.toString()).append(' ');
		sql.append(" AND ocv.").append(SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString()).append( " =              obj.").append(SigObjetoCustos.Fields.SEQ.toString()).append(' ');
		sql.append(" AND cmt.").append(SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString()).append(" = cbj.").append(SigCalculoObjetoCusto.Fields.SEQ.toString()).append(' ');
		sql.append(" AND cmt.").append(SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO.toString()).append(" =     cbt.").append(SigObjetoCustoComposicoes.Fields.SEQ.toString()).append("  ");
		sql.append(" AND cbt.").append(SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString()).append(" =              tvd.").append(SigAtividades.Fields.SEQ.toString()).append(' ');
		sql.append(" AND tvd.").append(SigAtividades.Fields.SEQ.toString()).append(" =                                ais.").append(SigAtividadeInsumos.Fields.SIG_ATIVIDADES.toString()).append(' ');
		sql.append(" AND ais.").append(SigAtividadeInsumos.Fields.MATERIAL_CODIGO.toString()).append(" =              mat.").append(ScoMaterial.Fields.CODIGO.toString()).append(' ');
		sql.append(" AND obj.").append(SigObjetoCustos.Fields.IND_TIPO.toString()).append( " = '").append(DominioTipoObjetoCusto.AP).append("' ");

		Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqProcessamento", processamento.getSeq());
		
		return createQuery.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	
	}
	
	public BigDecimal buscaDetalheInsumosConsumidos(Integer pmuSeq, Integer codigoCentroCusto, Integer codigoMaterial) {

		StringBuilder sql = new StringBuilder(200);
		sql.append("SELECT ")
		.append(" sum(msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( "), ")
		.append(" sum(msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( ") ")
		.append(" FROM " ).append( SigMvtoContaMensal.class.getSimpleName() ).append( " msl ")
		.append(" WHERE")
		.append(" msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pmuSeq ")
		.append(" AND coalesce(msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO_DEBITA.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append(" msl.").append(SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ") = :codigoCentroCusto ")
		.append(" AND msl." ).append( SigMvtoContaMensal.Fields.MATERIAL.toString() ).append( '.' ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " =  :codigoMaterial ")
		.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " in ('DI') ")
		.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_MOVIMENTO.toString() ).append( " in ('SIP', 'SIT') ");
		
		javax.persistence.Query query = this.createQuery(sql.toString());
		query.setParameter("pmuSeq", pmuSeq);
		query.setParameter("codigoCentroCusto", codigoCentroCusto);
		query.setParameter("codigoMaterial", codigoMaterial);
		
		BigDecimal custoMedio = null;
		Object[] valores = (Object[])query.getSingleResult();
		
		if(valores != null && valores[0] != null && valores[1] != null){
			BigDecimal somatorioValor = (BigDecimal)valores[0];
			BigDecimal somatorioQuantidade = BigDecimal.valueOf((Long)valores[1]);
			
			if(somatorioQuantidade.compareTo(BigDecimal.ZERO) != 0){
				custoMedio = somatorioValor.divide(somatorioQuantidade, 4, RoundingMode.HALF_UP);
			}
		}
		custoMedio = (custoMedio != null) ? custoMedio : BigDecimal.ZERO;
		
		return custoMedio;
	}
	
	
	/**
	 * Consulta validada para busca dos valores realizados para os calculos das atividades.
	 * Consulta validada tanto no PostgreSQL como no Oracle
	 * @param pmuSeq - Seq do processamento que será efetuada a busca
	 * @return Retorna o ScrollableResults com os itens da seleção
	 */
	public ScrollableResults buscaAtividadeValoresRealizadosCalculosAssistencialApoio(Integer pmuSeq, DominioTipoObjetoCusto dominioTipoObjetoCusto){
		StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT ");
		sql.append(" cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( ", ");
		sql.append(" cnv." ).append( SigCalculoAtividadeInsumo.Fields.CALCULO_COMPONENTE.toString() ).append( '.' ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( ", ");
		sql.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ");
		sql.append(" cnv." ).append( SigCalculoAtividadeInsumo.Fields.MATERIAL.toString() ).append( '.' ).append( ScoMaterial.Fields.CODIGO.toString()).append( ", ");
		sql.append(" cnv." ).append( SigCalculoAtividadeInsumo.Fields.QUANTIDADE_REALIZADA.toString() ).append( ", ");
		sql.append(" cnv." ).append( SigCalculoAtividadeInsumo.Fields.VALOR_INSUMO.toString() ).append(' ');

		sql.append(" FROM " ).append( SigCalculoAtividadeInsumo.class.getSimpleName() ).append( " cnv, ");
		sql.append(' ').append(SigCalculoComponente.class.getSimpleName() ).append( " cmt, ");
		sql.append(' ').append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ");
		
		sql.append(' ').append(SigObjetoCustoVersoes.class.getSimpleName()).append(" ocv, ");
		sql.append(' ').append(SigObjetoCustos.class.getSimpleName()).append(" obj ");


		sql.append(" WHERE cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :pmuSeq ");
		sql.append(" AND cnv." ).append( SigCalculoAtividadeInsumo.Fields.CALCULO_COMPONENTE.toString() ).append( '.' ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " = cmt." ).append( SigCalculoComponente.Fields.SEQ.toString());
		sql.append(" AND cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString() ).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString());
		sql.append(" AND cnv." ).append( SigCalculoAtividadeInsumo.Fields.QUANTIDADE_REALIZADA.toString() ).append( " > 0 ");
		
		sql.append(" AND cbj.").append(SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString()).append(" = ocv.").append(SigObjetoCustoVersoes.Fields.SEQ.toString()).append(' ');
		sql.append(" AND ocv.").append(SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString()).append( " = obj.").append(SigObjetoCustos.Fields.SEQ.toString()).append(' ');
		sql.append(" AND obj.").append(SigObjetoCustos.Fields.IND_TIPO.toString()).append( " = :dominioTipoObjetoCusto ");
		
		//javax.persistence.Query query = this.createQuery(sql.toString());
		Query query = this.createHibernateQuery(sql.toString());
		query.setParameter("pmuSeq", pmuSeq);		
		query.setParameter("dominioTipoObjetoCusto", dominioTipoObjetoCusto);
		
		return query.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}
}
