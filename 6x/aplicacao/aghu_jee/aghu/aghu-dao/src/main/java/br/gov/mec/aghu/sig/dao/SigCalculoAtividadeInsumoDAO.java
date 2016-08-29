package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;

import javax.persistence.Query;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigCalculoAtividadeInsumo;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;

public class SigCalculoAtividadeInsumoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoAtividadeInsumo> {

	private static final long serialVersionUID = -419319503213779547L;

	public void removerPorProcessamento(Integer idProcessamento) {

		StringBuilder sql = new StringBuilder(100);

		sql.append(" DELETE " ).append( SigCalculoAtividadeInsumo.class.getSimpleName().toString() ).append( " ca ")
		.append(" WHERE ca." ).append( SigCalculoAtividadeInsumo.Fields.CALCULO_COMPONENTE.toString() ).append('.').append( SigCalculoComponente.Fields.SEQ.toString())
		.append(" IN ( ")
		.append(" SELECT c." ).append( SigCalculoComponente.Fields.SEQ.toString())
		.append(" FROM " ).append( SigCalculoComponente.class.getSimpleName() ).append( " c ")
		.append(" WHERE c." ).append( SigCalculoComponente.Fields.PROCESSAMENTO_CUSTO.toString() ).append('.').append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pSeq")
		.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}

	/**
	 * Método testado e alterado para o objeto de custo assistencial, e tambem para Apoio
	 * Consulta validada tanto no PostgreSQL como no Oracle 
	 * @param pmuSeq -  Integer codigo do processamento sendo executado
	 * @param tipoObjetoCusto - Tipo de objeto de custo se é apoio ou assistencial
	 * @return retorna a seleção dos itens para o processo
	 */
	public ScrollableResults buscaMateriaisComConsumoExcedenteAssistencialApoio(Integer pmuSeq, DominioTipoObjetoCusto tipoObjetoCusto) {
		
		StringBuilder sql = new StringBuilder(400);

		sql.append("SELECT DISTINCT ")
		.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append('.').append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
		.append("cvn." ).append( SigCalculoAtividadeInsumo.Fields.MATERIAL.toString() ).append('.').append( ScoMaterial.Fields.CODIGO ).append( ", ")
		.append("msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString() ).append( ", ")
		.append("sum(cvn." ).append( SigCalculoAtividadeInsumo.Fields.QUANTIDADE_PREVISTA.toString() ).append( ") ")
		
		.append("FROM ")
		.append(' ').append( SigCalculoAtividadeInsumo.class.getSimpleName().toString() ).append( " cvn, ")
		.append(' ').append( SigCalculoComponente.class.getSimpleName().toString() ).append( " cmt, ")
		.append(' ').append( SigCalculoObjetoCusto.class.getSimpleName().toString() ).append( " cbj, ")
		.append(' ').append( SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(' ').append( SigObjetoCustos.class.getSimpleName() ).append( " obj, ")
		.append(SigMvtoContaMensal.class.getSimpleName().toString() ).append( " msl ")
		
		.append("WHERE ")
			.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :pmuSeq ")
			.append(" AND cvn." ).append( SigCalculoAtividadeInsumo.Fields.CALCULO_COMPONENTE.toString() ).append('.').append( SigCalculoComponente.Fields.SEQ.toString() ).append( " = cmt."
					).append( SigCalculoComponente.Fields.SEQ.toString())
			.append(" AND cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append('.').append( SigCalculoObjetoCusto.Fields.SEQ.toString()
					).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.CENTRO_CUSTO.toString() ).append('.').append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = cbj."
					).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append('.').append( FccCentroCustos.Fields.CODIGO.toString())
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.MATERIAL.toString() ).append('.').append( ScoMaterial.Fields.CODIGO.toString() ).append( " = cvn."
					).append( SigCalculoAtividadeInsumo.Fields.MATERIAL.toString() ).append('.').append( ScoMaterial.Fields.CODIGO.toString())
			.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( " = ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append(' ')
			.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( " = obj." ).append( SigObjetoCustos.Fields.SEQ.toString() ).append(' ')
			.append(" AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :tipoObjetoCusto  ")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.VALOR.toString() ).append( " > 0 ")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.TIPO_VALOR.toString() ).append( " in ('SIP', 'SIT') ")
			.append(" AND msl." ).append( SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString() ).append('.').append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pmuSeq ")

			.append(" GROUP BY ")
				.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append('.').append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ")
				.append(" cvn." ).append( SigCalculoAtividadeInsumo.Fields.MATERIAL.toString() ).append('.').append( ScoMaterial.Fields.CODIGO ).append( ", ")
				.append(" msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString())

				.append(" HAVING ")
				.append(" SUM(cvn." ).append( SigCalculoAtividadeInsumo.Fields.QUANTIDADE_PREVISTA.toString() ).append( ") > msl." ).append( SigMvtoContaMensal.Fields.QTDE.toString());
		
		org.hibernate.Query query = createHibernateQuery(sql.toString());
		query.setParameter("pmuSeq", pmuSeq);
		query.setParameter("tipoObjetoCusto", tipoObjetoCusto);
		
		return query.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}

	/**
	 * Metodo que busca o resultado da seleção das atividades com custo que o material faz parte,
	 * Consulta validada tanto no PostgreSQL como no Oracle, ajustada para processamento mensal de objeto de custo assitencial
	 * 
	 * @param materialSeq 			Codigo do material.
	 * @param pmuSeq 				Codigo do processamento atual.
	 * @param cctCodigo 			Codigo do centro de custo do material.
	 * @return 						Retorna o resultado da seleção das atividades com custo que o material faz parte.
	 */
	public ScrollableResults buscaAtividadesComConsumoExcedenteAssistencialApoio(Integer materialSeq, Integer pmuSeq, Integer cctCodigo,
			DominioTipoObjetoCusto tipoObjetoCusto) {
		StringBuilder sql = new StringBuilder(300);

		sql.append("SELECT ")

		.append("cvn." ).append( SigCalculoAtividadeInsumo.Fields.SEQ.toString() ).append( ", ")
		.append("cvn." ).append( SigCalculoAtividadeInsumo.Fields.QUANTIDADE_PREVISTA.toString() ).append( ", ")
		.append("cvn." ).append( SigCalculoAtividadeInsumo.Fields.VALOR_INSUMO.toString())
		.append(" FROM ")
		.append(SigCalculoAtividadeInsumo.class.getSimpleName().toString() ).append( " cvn, ")
		.append(SigCalculoComponente.class.getSimpleName().toString() ).append( " cmt, ")
		.append(SigCalculoObjetoCusto.class.getSimpleName().toString() ).append( " cbj, ")
		.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ")
		.append(SigObjetoCustos.class.getSimpleName() ).append( " obj ")
		.append("WHERE ")
		.append(" cvn." ).append( SigCalculoAtividadeInsumo.Fields.MATERIAL.toString() ).append('.').append( ScoMaterial.Fields.CODIGO.toString() ).append( " = :materialSeq ")
		.append(" AND cvn." ).append( SigCalculoAtividadeInsumo.Fields.CALCULO_COMPONENTE.toString() ).append('.').append( SigCalculoComponente.Fields.SEQ.toString() ).append( " = cmt."
				).append( SigCalculoComponente.Fields.SEQ.toString())
		.append(" AND cmt." ).append( SigCalculoComponente.Fields.CALCULO_OBJETO_CUSTO_CBJSEQ.toString() ).append('.').append( SigCalculoObjetoCusto.Fields.SEQ.toString()
				).append( " = cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( " = ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append(' ')
		.append(" AND ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( " = obj." ).append( SigObjetoCustos.Fields.SEQ.toString() ).append(' ')
		.append(" AND obj." ).append( SigObjetoCustos.Fields.IND_TIPO.toString() ).append( " = :tipoObjetoCusto ")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :pmuSeq ")
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append('.').append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :cctCodigo ");
		
		org.hibernate.Query query = createHibernateQuery(sql.toString());
		query.setParameter("materialSeq", materialSeq);
		query.setParameter("pmuSeq", pmuSeq);
		query.setParameter("cctCodigo", cctCodigo);
		query.setParameter("tipoObjetoCusto", tipoObjetoCusto);
		
		return query.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}
	
	/**
	 * Consulta auxiliar, da SigObjetoCustoVersoesDAO->buscarInsumosAlocadosAtividadePesoPorRateio, que retorna o calculo da atividade de insumo ligado ao componente
	 * 
	 * @param seqCalculoComponente
	 * @param seqAtividadeInsumo
	 * @return
	 */
	public SigCalculoAtividadeInsumo obterCalculoAtividadeInsumo(Integer seqCalculoComponente, Long seqAtividadeInsumo){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtividadeInsumo.class);
		criteria.add(Restrictions.eq(SigCalculoAtividadeInsumo.Fields.CALCULO_COMPONENTE.toString()+"."+SigCalculoComponente.Fields.SEQ, seqCalculoComponente));
		criteria.add(Restrictions.eq(SigCalculoAtividadeInsumo.Fields.ATIVIDADE_INSUMO.toString()+"."+SigAtividadeInsumos.Fields.SEQ, seqAtividadeInsumo));
		criteria.add(Restrictions.or(
				Restrictions.eq(SigCalculoAtividadeInsumo.Fields.VALOR_INSUMO.toString(), BigDecimal.ZERO),
				Restrictions.isNull(SigCalculoAtividadeInsumo.Fields.VALOR_INSUMO.toString())	
		));
		return (SigCalculoAtividadeInsumo) this.executeCriteriaUniqueResult(criteria);
	}

}