package br.gov.mec.aghu.faturamento.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.ByteType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioTipoItemConta;
import br.gov.mec.aghu.faturamento.vo.ContaApresentadaPacienteProcedimentoVO;
import br.gov.mec.aghu.model.FatCampoMedicoAuditAih;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;

public class FatCampoMedicoAuditAihDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCampoMedicoAuditAih> {

	private static final long serialVersionUID = 4859501041398888221L;

	public Integer removerPorCthSeqs(List<Integer> cthSeqs) {
		Query query = createQuery("delete " + FatCampoMedicoAuditAih.class.getName() + 
				 " where " + FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.toString() + " in (:cthSeqs) ");
		query.setParameter("cthSeqs", cthSeqs);
		return query.executeUpdate();
	}
	
	/**
	 * Remove os FatCampoMedicoAuditAih relacionados a uma conta hospitalar e data de prévia não nula.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerRnCthcAtuEncPrv(Integer cthSeq){
		Query query = createQuery("delete " + FatCampoMedicoAuditAih.class.getName() + 
													 " where " + FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.toString() + " = :cthSeq " +
													 "   and " + FatCampoMedicoAuditAih.Fields.DATA_PREVIA.toString() + " is not null ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}
	
	/**
	 * Remove os FatCampoMedicoAuditAih relacionados a uma conta hospitalar.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCth(Integer cthSeq){
		Query query = createQuery("delete " + FatCampoMedicoAuditAih.class.getName() + 
													 " where " + FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.toString() + " = :cthSeq ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}
	
	/**
	 * Remove os FatCampoMedicoAuditAih do mesmo seqp e relacionados a uma conta hospitalar.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @param seqp sequencial da entidade
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCthSeqp(Integer cthSeq, Byte seqp){
		Query query = createQuery("delete " + FatCampoMedicoAuditAih.class.getName() + 
													 " where " + FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.toString() + " = :cthSeq " +
													 "   and " + FatCampoMedicoAuditAih.Fields.SEQP.toString() + " = :seqp ");
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("seqp", seqp);
		return query.executeUpdate();
	}
	
	/**
	 * Pega a proxima seq campo medico aih
	 * 
	 * @param eaiCthSeq
	 * @param eaiSeq
	 * @return
	 */
	public Byte buscarProxSeq(Integer eaiCthSeq, Integer eaiSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCampoMedicoAuditAih.class);
		criteria.add(Restrictions.eq(FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.toString(), eaiCthSeq));
		criteria.add(Restrictions.eq(FatCampoMedicoAuditAih.Fields.EAI_SEQ.toString(), eaiSeq));
		criteria.setProjection(Projections.max(FatCampoMedicoAuditAih.Fields.SEQP.toString()));
		Byte seqp = (Byte) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		seqp++;
		return seqp;
	}

	/**
	 * Busca valores menores no cma
	 * 
	 * @param eaiCthSeq
	 * @param eaiSeq
	 * @param indConsistente
	 * @param vlrCalcCma
	 * @return Object[] onde: </br>
	 * 				Object[0] = seqp as seqp </br>
	 * 				Object[1] = valor_anestesista + valor_procedimento + valor_sadt + valor_serv_hosp + valor_serv_prof as valor </br>
	 */
	public Object[] buscarChaMaxVlr(Integer eaiCthSeq, Integer eaiSeq, DominioTipoItemConta indConsistente, BigDecimal vlrCalcCma) {
		String seq = "seqp";
		String valor = "valor";
		String fieldsSum = "(valor_anestesista + valor_procedimento + valor_sadt + valor_serv_hosp + valor_serv_prof)";
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCampoMedicoAuditAih.class);
		criteria.add(Restrictions.eq(FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.toString(), eaiCthSeq));
		criteria.add(Restrictions.eq(FatCampoMedicoAuditAih.Fields.EAI_SEQ.toString(), eaiSeq));
		criteria.add(Restrictions.ne(FatCampoMedicoAuditAih.Fields.IND_CONSISTENTE.toString(), indConsistente));		  
		criteria.add(Restrictions.sqlRestriction(fieldsSum + " < ? ", vlrCalcCma, BigDecimalType.INSTANCE));
		criteria.setProjection(
				Projections.projectionList()				
					.add(Projections.sqlProjection(seq + " as " + seq, new String[]{seq}, new Type[]{ByteType.INSTANCE}), seq)
					.add(Projections.sqlProjection(fieldsSum + " as " + valor, new String[]{valor}, new Type[]{BigDecimalType.INSTANCE}),valor));
		criteria.addOrder(Order.asc(valor));

		List<Object[]> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	
	public Long qtdCma(Integer eaiSeq, Integer eaiCthSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCampoMedicoAuditAih.class);
		criteria.add(Restrictions.eq(FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.toString(), eaiCthSeq));
		criteria.add(Restrictions.eq(FatCampoMedicoAuditAih.Fields.EAI_SEQ.toString(), eaiSeq));
		return executeCriteriaCount(criteria);
	}
	
	// 2280 - C5
	public List<ContaApresentadaPacienteProcedimentoVO> buscarCamposMedicos(Integer eaiCthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCampoMedicoAuditAih.class, "CAH");
		criteria.createAlias("CAH." + FatCampoMedicoAuditAih.Fields.IPH.toString(), "IPH");
		
		criteria.add(Restrictions.eq("CAH." + FatCampoMedicoAuditAih.Fields.IND_MODO_COBRANCA.toString(), DominioModoCobranca.V));
		criteria.add(Restrictions.sqlRestriction(" COALESCE({alias}.IND_CONSISTENTE, 'D') <> 'R' "));
		criteria.add(Restrictions.eq("CAH." + FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.toString(), eaiCthSeq));		
		
		ProjectionList projection =	Projections.projectionList()
				.add(Projections.groupProperty("CAH." + FatCampoMedicoAuditAih.Fields.EAI_CTH_SEQ.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.CTH_SEQ.toString())
				.add(Projections.groupProperty("CAH." + FatCampoMedicoAuditAih.Fields.EAI_SEQ.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.EAI_SEQ.toString())
				.add(Projections.count("CAH." + FatCampoMedicoAuditAih.Fields.IPH_COD_SUS.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.QUANTIDADE.toString())
				.add(Projections.groupProperty("CAH." + FatCampoMedicoAuditAih.Fields.IPH_COD_SUS.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.COD_SUS.toString())
				.add(Projections.groupProperty("IPH." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.PROCEDIMENTO.toString())
				.add(Projections.sum("CAH." + FatCampoMedicoAuditAih.Fields.VALOR_SADT.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.VALOR_SADT.toString())
				.add(Projections.sum("CAH." + FatCampoMedicoAuditAih.Fields.VALOR_SERV_HOSP.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.VALOR_SERV_HOSP.toString())
				.add(Projections.sum("CAH." + FatCampoMedicoAuditAih.Fields.VALOR_SERV_PROF.toString()), ContaApresentadaPacienteProcedimentoVO.Fields.VALOR_SERV_PROF.toString())
				;
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(ContaApresentadaPacienteProcedimentoVO.class));
		return executeCriteria(criteria);
	}
}
