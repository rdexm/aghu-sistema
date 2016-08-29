package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.vo.FatCidContaHospitalarVO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatProcedHospIntCid;

public class FatCidContaHospitalarDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCidContaHospitalar> {

	private static final long serialVersionUID = 4531222491104904059L;

	public Long buscaCountQtdCids(Integer cthSeq, Integer cidSeq) {
		DetachedCriteria criteria = DetachedCriteria
		.forClass(FatCidContaHospitalar.class);

		criteria.add(Restrictions.eq(FatCidContaHospitalar.Fields.CTH_SEQ
				.toString(), cthSeq));
		
		criteria.add(Restrictions.eq(
				FatCidContaHospitalar.Fields.CID_SEQ.toString(),
				cidSeq));
		
		return executeCriteriaCount(criteria);
	}
	
	public Long buscaCountQtdCids(Integer cthSeq,
			DominioPrioridadeCid prioridade) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatCidContaHospitalar.class);

		criteria.add(Restrictions.eq(FatCidContaHospitalar.Fields.CTH_SEQ
				.toString(), cthSeq));

		criteria.add(Restrictions.eq(
				FatCidContaHospitalar.Fields.PRIORIDADE_CID.toString(),
				prioridade));

		return executeCriteriaCount(criteria);
	}

	/**
	 * Pesquisa CID Conta Hospitalar pelo chtSeq
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param cthSeq
	 * @return
	 */
	public List<FatCidContaHospitalar> pesquisarCidContaHospitalar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer cthSeq) {
		DetachedCriteria criteria = montaCriteriaCidContaHospitalar(cthSeq);
		
		return executeCriteria(criteria, firstResult,maxResult, orderProperty, asc);
	}	
	
	/**
	 * Pesquisa CID Conta Hospitalar pelo chtSeq
	 * 
	 * @param cthSeq
	 * @return
	 */
	public List<FatCidContaHospitalar> pesquisarCidContaHospitalar(Integer cthSeq) {
		DetachedCriteria criteria = montaCriteriaCidContaHospitalar(cthSeq);
		return executeCriteria(criteria);
	}
	
	/** Conta CID Conta Hospitalar pelo chtSeq
	 * 
	 * @param cthSeq
	 * @return
	 */
	public Long pesquisarCidContaHospitalarCount(Integer cthSeq) {
		DetachedCriteria criteria = montaCriteriaCidContaHospitalar(cthSeq);
		
		return executeCriteriaCount(criteria);
	}	
	
	/**
	 * Pesquisa o CID pela descrição ou pelo Código
	 * @param descricao
	 * @param limiteRegistros
	 * @return
	 */
	public List<AghCid> pesquisarCidsPorDescricaoOuCodigo(String descricao,
			Integer limiteRegistros) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);

		if (descricao != null && !"".equals(descricao)) {
			Criterion criterionDescricao = Restrictions.ilike(
					AghCid.Fields.DESCRICAO.toString(), descricao,
					MatchMode.ANYWHERE);

			Criterion criterionCodigo = Restrictions.ilike(
					AghCid.Fields.CODIGO.toString(), descricao,
					MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(criterionCodigo, criterionDescricao));
		}

		criteria.addOrder(Order.asc(AghCid.Fields.DESCRICAO.toString()));

		if (limiteRegistros != null) {
			return executeCriteria(criteria, 0, limiteRegistros.intValue(),
					null, true);
		} else {
			return executeCriteria(criteria);
		}
	}
	
	/**
	 * Pesquisa o CID pela descrição ou pelo Código
	 * @param descricao
	 * @param limiteRegistros
	 * @param order
	 * @return
	 */
	public List<AghCid> pesquisarCidsPorDescricaoOuCodigo(String descricao,
			Integer limiteRegistros, String order) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);

		if (descricao != null && !"".equals(descricao)) {
			Criterion criterionDescricao = Restrictions.ilike(
					AghCid.Fields.DESCRICAO.toString(), descricao,
					MatchMode.ANYWHERE);

			Criterion criterionCodigo = Restrictions.ilike(
					AghCid.Fields.CODIGO.toString(), descricao,
					MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(criterionCodigo, criterionDescricao));
		}

		if (order != null){
			criteria.addOrder(Order.asc(order));
		}
		if (limiteRegistros != null) {
			return executeCriteria(criteria, 0, limiteRegistros.intValue(),
					null, true);
		} else {
			return executeCriteria(criteria);
		}
	}	
	
	/**
	 * Pesquisa o CID pela descrição ou pelo Código
	 * @param descricao
	 * @return
	 */
	public Long pesquisarCidsPorDescricaoOuCodigoCount(String descricao) {

		DetachedCriteria criteria = montaCriteriaCidDescricaoCodigo(descricao);
		
		return executeCriteriaCount(criteria);
	}	
	
	private DetachedCriteria montaCriteriaCidDescricaoCodigo(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class);

		if (descricao != null && !"".equals(descricao)) {
			Criterion criterionDescricao = Restrictions.ilike(
					AghCid.Fields.DESCRICAO.toString(), descricao,
					MatchMode.ANYWHERE);

			Criterion criterionCodigo = Restrictions.ilike(
					AghCid.Fields.CODIGO.toString(), descricao,
					MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(criterionCodigo, criterionDescricao));
		}
		return criteria;
	}
	
	private DetachedCriteria montaCriteriaCidContaHospitalar(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria
			.forClass(FatCidContaHospitalar.class);
		
		criteria.add(Restrictions.eq(FatCidContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.createAlias(FatCidContaHospitalar.Fields.CID.toString(), "CID",JoinType.LEFT_OUTER_JOIN);
		
		return criteria;
	}

	public FatCidContaHospitalar buscarPrimeiroCidContasHospitalares(Integer cthSeq, DominioPrioridadeCid prioridadeCid){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCidContaHospitalar.class);

		criteria.add(Restrictions.eq(FatCidContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatCidContaHospitalar.Fields.PRIORIDADE_CID.toString(), prioridadeCid));

		List<FatCidContaHospitalar> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	} 
	
	public Integer buscarPrimeiroCidSeq(Integer cthSeq, DominioPrioridadeCid prioridadeCid){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCidContaHospitalar.class);

		criteria.add(Restrictions.eq(FatCidContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatCidContaHospitalar.Fields.PRIORIDADE_CID.toString(), prioridadeCid));
		
		criteria.setProjection(Projections.property(FatCidContaHospitalar.Fields.CID_SEQ.toString()));

		List<Integer> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	} 

	/**
	 * Remove os FatCidContaHospitalar relacionados a uma conta hospitalar e prioridade.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @param prioridadeCid prioridade
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCthPrioridade(Integer cthSeq, DominioPrioridadeCid prioridadeCid){
		javax.persistence.Query query = createQuery("delete " + FatCidContaHospitalar.class.getName() + 
																	   " where " + FatCidContaHospitalar.Fields.CTH_SEQ.toString() + " = :cthSeq " + 
																	   "   and " + FatCidContaHospitalar.Fields.PRIORIDADE_CID.toString() + " = :prioridadeCid ");
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("prioridadeCid", prioridadeCid);
		return query.executeUpdate();
	}
	
	/**
	 * Remove os FatCidContaHospitalar relacionados a uma conta hospitalar
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCth(Integer cthSeq){
		javax.persistence.Query query = createQuery("delete " + FatCidContaHospitalar.class.getName() + 
																	   " where " + FatCidContaHospitalar.Fields.CTH_SEQ.toString() + " = :cthSeq ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<FatCidContaHospitalarVO> listarFatCidContaHospitalarVOPorCthSeq(Integer cthSeq) {
		StringBuffer hql = new StringBuffer(130);
		hql.append(" select distinct new br.gov.mec.aghu.faturamento.vo.FatCidContaHospitalarVO (");
		hql.append(" 	cch.").append(FatCidContaHospitalar.Fields.CID_SEQ.toString());
		hql.append("	, cch.").append(FatCidContaHospitalar.Fields.PRIORIDADE_CID.toString());
		hql.append(" ) ");
		hql.append(" from ").append(FatCidContaHospitalar.class.getSimpleName()).append(" cch ");
		hql.append(" where cch.").append(FatCidContaHospitalar.Fields.CTH_SEQ.toString()).append(" = :cthSeq ");

		Query query = createQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);

		return query.getResultList();
	}

	/**
	 * Método para listar AghCid de VFatAssocProcCids filtrando por ItemProcedimentoHospitalar e Convenio.
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param descricao
	 * @param limiteRegistros
	 * @return List<AghCid>
	 */

	public List<AghCid> pesquisarCidsPorSSMDescricaoOuCodigo(Integer phiSeq, String descricao, Integer limiteRegistros){
		DetachedCriteria criteria = montaCriteria(phiSeq, descricao);
		if (limiteRegistros != null) {
			return executeCriteria(criteria, 0, limiteRegistros.intValue(),
					null, true);
		} else {
			return executeCriteria(criteria);
		}
	}

	public Long pesquisarCidsPorSSMDescricaoOuCodigoCount( Integer phiSeq, String descricao) {
		DetachedCriteria criteria = montaCriteria(phiSeq, descricao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montaCriteria(Integer phiSeq, String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCid.class, "cid");
		if (descricao != null && !"".equals(descricao)) {
			Criterion criterionCodigo = Restrictions.ilike(AghCid.Fields.CODIGO.toString(), descricao, MatchMode.ANYWHERE);
			Criterion criterionDescricao = Restrictions.ilike(AghCid.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(criterionCodigo, criterionDescricao));
		}
		if(phiSeq != null){
			DetachedCriteria subCriteria = DetachedCriteria.forClass(FatProcedHospIntCid.class, "proced");
			subCriteria.setProjection(Projections.distinct(Projections.property("proced." + FatProcedHospIntCid.Fields.CID_SEQ.toString())));
			subCriteria.add(Restrictions.eq("proced." + FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
			criteria.add(Subqueries.propertyIn("cid." + AghCid.Fields.SEQ.toString(), subCriteria));
		}
		criteria.add(Restrictions.eq(AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AghCid.Fields.DESCRICAO.toString()));
		return criteria;
	}

	public List<FatCidContaHospitalar> buscaFatCidContaHospitalar(Integer cthSeq, Integer cidSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCidContaHospitalar.class);

		criteria.add(Restrictions.eq(FatCidContaHospitalar.Fields.CTH_SEQ
				.toString(), cthSeq));
		
		criteria.add(Restrictions.eq(FatCidContaHospitalar.Fields.CID_SEQ.toString(),cidSeq));
		
		return executeCriteria(criteria);
	}
	
}