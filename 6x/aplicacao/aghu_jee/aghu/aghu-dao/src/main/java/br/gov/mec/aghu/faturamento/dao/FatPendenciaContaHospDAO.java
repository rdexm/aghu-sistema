package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatPendenciaContaHosp;

public class FatPendenciaContaHospDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FatPendenciaContaHosp> {


	private static final long serialVersionUID = -7606738911809958032L;

	/**
	 * Número de pendências por CTH e situacao
	 * 
	 * @param cthSeq
	 * @return
	 */
	public Long listarPorCthSituacaoCount(Integer cthSeq, DominioSituacao situacao) {
		return executeCriteriaCount(getCriteriaCthSituacao(cthSeq, situacao));
	}
	
	/**
	 * Remove os FatPendenciaContaHosp relacionados a uma conta hospitalar e situacao.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCthSituacao(Integer cthSeq, DominioSituacao situacao){
		javax.persistence.Query query = createQuery("delete " + FatPendenciaContaHosp.class.getName() + 
																	   " where " + FatPendenciaContaHosp.Fields.CTH_SEQ.toString() + " = :cthSeq " +
																	   "   and " + FatPendenciaContaHosp.Fields.IND_SITUACAO.toString() + " = :situacao ");
		
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("situacao", situacao);
		return query.executeUpdate();
	}
	
	/**
	 * Lista as pendências por CTH e situacao
	 * 
	 * @param cthSeq
	 * @return
	 */
	public List<FatPendenciaContaHosp> listarFatPendenciaContaHospPorCthSeq(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc, final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatPendenciaContaHosp.class);
		
		criteria.add(Restrictions.eq(FatPendenciaContaHosp.Fields.CTH_SEQ.toString(), cthSeq));
		
		criteria.createAlias(FatPendenciaContaHosp.Fields.FAT_MOTIVO_PENDENCIA.toString(), "FMP", JoinType.INNER_JOIN);
		criteria.createAlias(FatPendenciaContaHosp.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		
		criteria.addOrder(Order.asc(FatPendenciaContaHosp.Fields.SEQP.toString()));
		
		return executeCriteria( criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarFatPendenciaContaHospPorCthSeqCount(final Integer cthSeq) {
		return executeCriteriaCount(
				DetachedCriteria.forClass(FatPendenciaContaHosp.class)
				.add(Restrictions.eq(FatPendenciaContaHosp.Fields.CTH_SEQ.toString(), cthSeq)));
	}
	
	
	/**
	 * Criteria para listar as pendências por CTH e situacao
	 * 
	 * @param cthSeq
	 * @return
	 */
	private DetachedCriteria getCriteriaCthSituacao(Integer cthSeq, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPendenciaContaHosp.class);
		criteria.add(Restrictions.eq(FatPendenciaContaHosp.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatPendenciaContaHosp.Fields.IND_SITUACAO.toString(), situacao));
		return criteria;
	}
	
	/** Obtem próxima sequencia
	 * 
	 * @param cthSeq
	 * @return
	 */
	public Short obterProximoSeq(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPendenciaContaHosp.class);
		criteria.setProjection(Projections.max(FatPendenciaContaHosp.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(FatPendenciaContaHosp.Fields.CTH_SEQ.toString(), cthSeq));
		Short result = (Short) executeCriteriaUniqueResult(criteria);
		if(result == null){
			result = (short)0;
		}
		result++;
		
		return result;
	}	
}
