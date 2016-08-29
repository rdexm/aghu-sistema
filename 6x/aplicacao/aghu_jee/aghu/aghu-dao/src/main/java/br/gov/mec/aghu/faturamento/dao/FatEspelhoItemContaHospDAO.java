package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ByteType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioLocalSoma;
import br.gov.mec.aghu.dominio.DominioTipoItemEspelhoContaHospitalar;
import br.gov.mec.aghu.faturamento.vo.BuscaTotalHemoterapiasQueExigemLancamentoModuloTransfusionalVO;
import br.gov.mec.aghu.faturamento.vo.FatEspelhoItemContaHospVO;
import br.gov.mec.aghu.model.FatAtoObrigatorioProced;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;


public class FatEspelhoItemContaHospDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FatEspelhoItemContaHosp> {

	private static final long serialVersionUID = 553862333064875073L;

	public List<FatEspelhoItemContaHosp> listarFatEspelhoItemContaHospRnCthcAtuEncPrv(
			Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatEspelhoItemContaHosp.class);

		if (cthSeq != null) {
			criteria.add(Restrictions.eq(
					FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(),
					cthSeq));
		}

		criteria.add(Restrictions
				.isNotNull(FatEspelhoItemContaHosp.Fields.DATA_PREVIA
						.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Remove os FatEspelhoItemContaHosp relacionados a uma conta hospitalar e com data de previa não nula.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerRnCthcAtuEncPrv(Integer cthSeq){
		javax.persistence.Query query = createQuery("delete " + FatEspelhoItemContaHosp.class.getName() + 
																	   " where " + FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString() + " = :cthSeq " +
																	   "   and " + FatEspelhoItemContaHosp.Fields.DATA_PREVIA.toString() + " is not null ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}

	public List<FatEspelhoItemContaHosp> listarEspelhosItensContaHospitalar(
			Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatEspelhoItemContaHosp.class);

		if (cthSeq != null) {
			criteria.add(Restrictions.eq(
					FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(),
					cthSeq));
		}

		return executeCriteria(criteria, true);
	}
	
	/**
	 * Remove os FatEspelhoItemContaHosp relacionados a uma conta hospitalar.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorContaHospitalar(Integer cthSeq){
		javax.persistence.Query query = createQuery("delete " + FatEspelhoItemContaHosp.class.getName() + 
																	   " where " + FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString() + " = :cthSeq ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}
	
	public Integer removerPorCthSeqs(List<Integer> cthSeqs) {
		javax.persistence.Query query = createQuery("delete " + FatEspelhoItemContaHosp.class.getName() + 
				 " where " + FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString() + " in (:cthSeqs) ");
		query.setParameter("cthSeqs", cthSeqs);
		return query.executeUpdate();
	}
	
	private DetachedCriteria obterCriteriaProximaSeqTabelaEspelho(final Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class);

		criteria.setProjection(Projections.max(FatEspelhoItemContaHosp.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), cthSeq));
		return criteria;
	}

	public Short obterUltimaSeqTabelaEspelho(final Integer cthSeq) {
		Object result = this.executeCriteriaUniqueResult(obterCriteriaProximaSeqTabelaEspelho(cthSeq));

		if (result == null) {
			return null;
		}
		return (Short) result;
	}

	public Short buscaProximaSeqTabelaEspelho(final Integer cthSeq) {

		Short seqp = (Short) this.executeCriteriaUniqueResult(obterCriteriaProximaSeqTabelaEspelho(cthSeq));

		if (seqp == null) {
			seqp = 1;
		} else {
			seqp++;
		}

		return seqp;
	}

	public Long listarEspelhosPorItemProcedHospCount(Short iphPhoSeq, Integer iphSeq, Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class);

		criteria.createAlias(FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString());
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.IPH_SEQ.toString(), iphSeq));
		
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), cthSeq));
		
		return executeCriteriaCount(criteria);
	}

	
	/**
	 * Busca os itens lancados no espelho
	 * 
	 * @param cthSeq
	 * @return
	 */
	public List<FatEspelhoItemContaHosp> listarItensLancadosEspelho(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class);
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.ne(FatEspelhoItemContaHosp.Fields.IND_TIPO_ITEM.toString(), DominioTipoItemEspelhoContaHospitalar.O));
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.IND_CONSISTENTE.toString(), Boolean.TRUE));

		// TODO eSchweigert, confirmar esta reodernação com o NEY para facilitar comparação de LOGS>>>
		criteria.addOrder(Order.asc(FatEspelhoItemContaHosp.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	
	/**
	 * @param cthSeq
	 * @param pPrevia
	 * @return
	 */
	public List<FatEspelhoItemContaHosp> listarItensAgrup(Integer cthSeq, Boolean pPrevia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class);
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.IND_TIPO_ITEM.toString(), DominioTipoItemEspelhoContaHospitalar.D));
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.IND_LOCAL_SOMA.toString(), DominioLocalSoma.R));
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.IND_CONSISTENTE.toString(), Boolean.TRUE));
		if(pPrevia){
			criteria.add(Restrictions.isNotNull(FatEspelhoItemContaHosp.Fields.DATA_PREVIA.toString()));
		} else {
			criteria.add(Restrictions.isNull(FatEspelhoItemContaHosp.Fields.DATA_PREVIA.toString()));
		}
		criteria.createAlias(FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString());
		
		criteria.addOrder(Order.desc(FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString() + "." + FatItensProcedHospitalar.Fields.QUANT_DIAS_FATURAMENTO.toString()));
		
		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<BuscaTotalHemoterapiasQueExigemLancamentoModuloTransfusionalVO> buscaTotalHemoterapiasQueExigemLancamentoModuloTransfusional(
			Integer cthSeq, Short phoModuloTransfusional,
			Integer iphModuloTransfusional) {
		StringBuffer hql = new StringBuffer(400);

		hql.append(" select new br.gov.mec.aghu.faturamento.vo.BuscaTotalHemoterapiasQueExigemLancamentoModuloTransfusionalVO(");
		hql.append("   eic.").append(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString());
		hql.append(" , eic.").append(FatEspelhoItemContaHosp.Fields.IPH_PHO_SEQ.toString());
		hql.append(" , eic.").append(FatEspelhoItemContaHosp.Fields.IPH_SEQ.toString());
		hql.append(" , cast(sum(eic.").append(FatEspelhoItemContaHosp.Fields.QUANTIDADE.toString()).append(") as integer ) as ").append(FatEspelhoItemContaHosp.Fields.QUANTIDADE.toString()).append(')');
		hql.append(" from ").append(FatEspelhoItemContaHosp.class.getSimpleName()).append(" as eic ");
		hql.append("  join eic.").append(FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString()).append(" as iph ");
		hql.append("  join iph.").append(FatItensProcedHospitalar.Fields.ATOS_OBRIGATORIOS_PROCEDIMENTOS.toString())
				.append(" as aop ");
		hql.append(" where aop.").append(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ_COBRADO.toString()).append(
				" = :phoModuloTransfusional ");
		hql.append("  and aop.").append(FatAtoObrigatorioProced.Fields.IPH_SEQ_COBRADO.toString()).append(
				" = :iphModuloTransfusional ");
		hql.append("  and eic.").append(FatEspelhoItemContaHosp.Fields.IND_CONSISTENTE.toString()).append(" = :situacaoConsistente ");
		hql.append("  and eic.").append(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString()).append(" = :cthSeq ");
		hql.append(" group by eic.").append(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString());
		hql.append("  , eic.").append(FatEspelhoItemContaHosp.Fields.IPH_PHO_SEQ.toString());
		hql.append("  , eic.").append(FatEspelhoItemContaHosp.Fields.IPH_SEQ.toString());

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("phoModuloTransfusional", phoModuloTransfusional);
		query.setParameter("iphModuloTransfusional", iphModuloTransfusional);
		query.setParameter("situacaoConsistente", true);
		
		return query.list();
	}

	public Integer sumatorioEspelhoItensContaHospitalar(Integer cthSeq, Long codigoProcedimentoHospitalarSus, Boolean consistente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class);

		criteria.setProjection(Projections.sum(FatEspelhoItemContaHosp.Fields.QUANTIDADE.toString()));

		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString(),
				codigoProcedimentoHospitalarSus));

		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.IND_CONSISTENTE.toString(), consistente));

		return (Integer) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * 
	 * @param ichCthSeq
	 * @param procedimentoHospitalarSus
	 * @param indConsistente
	 * @return
	 */
	public Boolean existePorCthProcedIndConsistente(Integer ichCthSeq, Boolean indConsistente, Long...procedimentoHospitalarSus) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class);
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), ichCthSeq));
		criteria.add(Restrictions.in(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString(), procedimentoHospitalarSus));
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.IND_CONSISTENTE.toString(), indConsistente));
		return executeCriteriaCount(criteria) > 0;
	}

	public List<FatEspelhoItemContaHosp> listarEspelhosItensContaHospitalarOrdenadoPorQuantidadeDesc(Integer cthSeq,
			Long codigoProcedimentoHospitalarSus, Boolean consistente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class);

		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString(),
				codigoProcedimentoHospitalarSus));

		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.IND_CONSISTENTE.toString(), consistente));

		criteria.addOrder(Order.desc(FatEspelhoItemContaHosp.Fields.QUANTIDADE.toString()));

		return executeCriteria(criteria);
	}

	public List<FatEspelhoItemContaHosp> listarEspelhosItensContaHospitalar(Integer cthSeq, Short ichSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class);
		
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_SEQ.toString(), ichSeq));

		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		return executeCriteria(criteria, true);
	}
	
	
	private DetachedCriteria getCItemCriteria(Integer pCth, DominioTipoItemEspelhoContaHospitalar pTipo, Boolean pPrevia){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class);
		criteria.createAlias(FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString());
		criteria
			//Foi utilizado aqui "sqlRestriction" pois o com "Restrictions.eq" Hibernate estava gerando um alias e tentando
			//utilizá-lo na clausula WHERE, o que não estava funcionando.
			.add(Restrictions.sqlRestriction("{alias}.IND_TIPO_ITEM = ?", pTipo.toString(), StringType.INSTANCE))
			.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.IND_CONSISTENTE.toString(), Boolean.TRUE))
			.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), pCth));
		
		if(pPrevia){
			criteria.add(Restrictions.isNotNull(FatEspelhoItemContaHosp.Fields.DATA_PREVIA.toString()));
		} else {
			criteria.add(Restrictions.isNull(FatEspelhoItemContaHosp.Fields.DATA_PREVIA.toString()));
		}
		
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatEspelhoAih.class);
				
		subCriteria.add(Restrictions.eq(FatEspelhoAih.Fields.CTH_SEQ.toString(), pCth));		
		if(pPrevia){
			subCriteria.add(Restrictions.isNotNull(FatEspelhoAih.Fields.DATA_PREVIA.toString()));
		} else {
			subCriteria.add(Restrictions.isNull(FatEspelhoAih.Fields.DATA_PREVIA.toString()));
		}
		subCriteria.setProjection(Projections.property(FatEspelhoAih.Fields.SEQP.toString()));
		
		criteria.add(Subqueries.exists(subCriteria));
		
		return criteria;
	}

	private DetachedCriteria getCItemCriteriaPorProcedimentoCompetencia(Integer pCth, DominioTipoItemEspelhoContaHospitalar pTipo, 
			Boolean pPrevia, Long procHospSus, Byte tivSeq, Byte taoSeq, Short iphPhoSeq, Integer iphSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class);
		 
		criteria.createAlias(FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString());
		criteria
			//Foi utilizado aqui "sqlRestriction" pois o com "Restrictions.eq" Hibernate estava gerando um alias e tentando
			//utilizá-lo na clausula WHERE, o que não estava funcionando.
			.add(Restrictions.sqlRestriction("{alias}.IND_TIPO_ITEM = ?", pTipo.toString(), StringType.INSTANCE))
			.add(Restrictions.sqlRestriction("{alias}.PROCEDIMENTO_HOSPITALAR_SUS = ?", procHospSus, LongType.INSTANCE))
			.add(Restrictions.sqlRestriction("({alias}.tiv_seq is null or ({alias}.tiv_seq is not null and {alias}.tiv_seq = ?))", tivSeq, ByteType.INSTANCE))
			.add(Restrictions.sqlRestriction("({alias}.tao_seq is null or ({alias}.tao_seq is not null and {alias}.tao_seq = ?))", taoSeq, ByteType.INSTANCE))
			.add(Restrictions.sqlRestriction("{alias}.iph_pho_seq = ?", iphPhoSeq, ShortType.INSTANCE))
			.add(Restrictions.sqlRestriction("{alias}.iph_seq = ?", iphSeq, IntegerType.INSTANCE))
			.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.IND_CONSISTENTE.toString(), Boolean.TRUE))
			.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), pCth))
			;
		if(pPrevia){
			criteria.add(Restrictions.isNotNull(FatEspelhoItemContaHosp.Fields.DATA_PREVIA.toString()));
		} else {
			criteria.add(Restrictions.isNull(FatEspelhoItemContaHosp.Fields.DATA_PREVIA.toString()));
		}
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatEspelhoAih.class);
				
		subCriteria.add(Restrictions.eq(FatEspelhoAih.Fields.CTH_SEQ.toString(), pCth));		
		if(pPrevia){
			subCriteria.add(Restrictions.isNotNull(FatEspelhoAih.Fields.DATA_PREVIA.toString()));
		} else {
			subCriteria.add(Restrictions.isNull(FatEspelhoAih.Fields.DATA_PREVIA.toString()));
		}
		subCriteria.setProjection(Projections.property(FatEspelhoAih.Fields.SEQP.toString()));
		
		criteria.add(Subqueries.exists(subCriteria));
		
		return criteria;
	}
	
	public List<FatEspelhoItemContaHospVO> cItemRealiz(Integer pCth, DominioTipoItemEspelhoContaHospitalar pTipo, Boolean pPrevia){
		DetachedCriteria criteria = this.getCItemCriteria(pCth, pTipo, pPrevia);
		
		criteria.setProjection(Projections.projectionList()				
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString()), FatEspelhoItemContaHospVO.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.ICH_SEQ.toString()), FatEspelhoItemContaHospVO.Fields.ICH_SEQ.toString())) // Marina 09/11/2009
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IPH_PHO_SEQ.toString()), FatEspelhoItemContaHospVO.Fields.IPH_PHO_SEQ.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IPH_SEQ.toString()), FatEspelhoItemContaHospVO.Fields.IPH_SEQ.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IND_MODO_COBRANCA.toString()), FatEspelhoItemContaHospVO.Fields.IND_MODO_COBRANCA.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IND_LOCAL_SOMA.toString()), FatEspelhoItemContaHospVO.Fields.IND_LOCAL_SOMA.toString()))
				
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.QUANTIDADE.toString()), FatEspelhoItemContaHospVO.Fields.QUANTIDADE.toString()))				
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_SERV_HOSP.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_SERV_HOSP.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_SERV_PROF.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_SERV_PROF.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_SADT.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_SADT.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_PROCEDIMENTO.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_PROCEDIMENTO.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_ANESTESISTA.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_ANESTESISTA.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.PONTOS_ANESTESISTA.toString()), FatEspelhoItemContaHospVO.Fields.PONTOS_ANESTESISTA.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.PONTOS_CIRURGIAO.toString()), FatEspelhoItemContaHospVO.Fields.PONTOS_CIRURGIAO.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.PONTOS_SADT.toString()), FatEspelhoItemContaHospVO.Fields.PONTOS_SADT.toString()))
				);
		
		criteria
			.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_HOSP.toString()))
			.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_PROF.toString()))
			.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_SADT.toString()))
			.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_PROCEDIMENTO.toString()))
			.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_ANESTESISTA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(FatEspelhoItemContaHospVO.class));
		 
		return executeCriteria(criteria);
	}
	
	public List<FatEspelhoItemContaHospVO> cItemAgrup(Integer pCth, DominioTipoItemEspelhoContaHospitalar pTipo, Boolean pPrevia){
		DetachedCriteria criteria = this.getCItemCriteria(pCth, pTipo, pPrevia);
		
		criteria.setProjection(Projections.projectionList()				
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString()), FatEspelhoItemContaHospVO.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.TIV_SEQ.toString()), FatEspelhoItemContaHospVO.Fields.TIV_SEQ.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.TAO_SEQ.toString()), FatEspelhoItemContaHospVO.Fields.TAO_SEQ.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IPH_PHO_SEQ.toString()), FatEspelhoItemContaHospVO.Fields.IPH_PHO_SEQ.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IPH_SEQ.toString()), FatEspelhoItemContaHospVO.Fields.IPH_SEQ.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IND_MODO_COBRANCA.toString()), FatEspelhoItemContaHospVO.Fields.IND_MODO_COBRANCA.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IND_LOCAL_SOMA.toString()), FatEspelhoItemContaHospVO.Fields.IND_LOCAL_SOMA.toString()))				
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString() + "." + FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString()), FatEspelhoItemContaHospVO.Fields.IPH_INTERNACAO.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IND_TIPO_ITEM.toString()), FatEspelhoItemContaHospVO.Fields.IND_TIPO_ITEM.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.CGC.toString()), FatEspelhoItemContaHospVO.Fields.CGC.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.COMPETENCIA_UTI.toString()), FatEspelhoItemContaHospVO.Fields.COMPETENCIA_UTI.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IND_EQUIPE.toString()), FatEspelhoItemContaHospVO.Fields.IND_EQUIPE.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.CBO.toString()), FatEspelhoItemContaHospVO.Fields.CBO.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.CPF_CNS.toString()), FatEspelhoItemContaHospVO.Fields.CPF_CNS.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.SERIE_OPM.toString()), FatEspelhoItemContaHospVO.Fields.SERIE_OPM.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.LOTE_OPM.toString()), FatEspelhoItemContaHospVO.Fields.LOTE_OPM.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.REG_ANVISA_OPM.toString()), FatEspelhoItemContaHospVO.Fields.REG_ANVISA_OPM.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.CNPJ_REG_ANVISA.toString()), FatEspelhoItemContaHospVO.Fields.CNPJ_REG_ANVISA.toString()))
				
				.add(Projections.alias(Projections.min(FatEspelhoItemContaHosp.Fields.NOTA_FISCAL.toString()), FatEspelhoItemContaHospVO.Fields.NOTA_FISCAL.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.QUANTIDADE.toString()), FatEspelhoItemContaHospVO.Fields.QUANTIDADE.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_SERV_HOSP.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_SERV_HOSP.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_SERV_PROF.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_SERV_PROF.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_SADT.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_SADT.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_PROCEDIMENTO.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_PROCEDIMENTO.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_ANESTESISTA.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_ANESTESISTA.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.PONTOS_ANESTESISTA.toString()), FatEspelhoItemContaHospVO.Fields.PONTOS_ANESTESISTA.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.PONTOS_CIRURGIAO.toString()), FatEspelhoItemContaHospVO.Fields.PONTOS_CIRURGIAO.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.PONTOS_SADT.toString()), FatEspelhoItemContaHospVO.Fields.PONTOS_SADT.toString()))
				);
							
		criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.IPH_INTERNACAO.toString()));
		
		if(DominioTipoItemEspelhoContaHospitalar.O.equals(pTipo)){
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString()));
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_HOSP.toString()));			
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_PROF.toString()));
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.VALOR_SADT.toString()));
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.VALOR_PROCEDIMENTO.toString()));
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.VALOR_ANESTESISTA.toString()));
		} else if(DominioTipoItemEspelhoContaHospitalar.D.equals(pTipo)){
			//FIXME: CORRIGIR ESSA ORDENAÇÃO, "DECODES" 
			criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_HOSP.toString()));			
			criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_PROF.toString()));
			criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_SADT.toString()));
			criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_PROCEDIMENTO.toString()));
			criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_ANESTESISTA.toString()));
		}
		
	
		criteria.setResultTransformer(Transformers.aliasToBean(FatEspelhoItemContaHospVO.class));
		
		return executeCriteria(criteria);
	}
	
	
	public List<FatEspelhoItemContaHospVO> cItemAgrupPorProc(Integer pCth, DominioTipoItemEspelhoContaHospitalar pTipo, Boolean pPrevia){
       final StringBuffer hql = new StringBuffer(700);

		hql.append("SELECT ")
		   .append("  EIC.").append(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString()).append(" as ").append(FatEspelhoItemContaHospVO.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString())
		   .append(" ,EIC.").append(FatEspelhoItemContaHosp.Fields.TIV_SEQ.toString()).append(" as ").append(FatEspelhoItemContaHospVO.Fields.TIV_SEQ.toString())
		   .append(" ,EIC.").append(FatEspelhoItemContaHosp.Fields.TAO_SEQ.toString()).append(" as ").append(FatEspelhoItemContaHospVO.Fields.TAO_SEQ.toString())
		   .append(" ,EIC.").append(FatEspelhoItemContaHosp.Fields.IPH_PHO_SEQ.toString()).append(" as ").append(FatEspelhoItemContaHospVO.Fields.IPH_PHO_SEQ.toString())
		   .append(" ,EIC.").append(FatEspelhoItemContaHosp.Fields.IPH_SEQ.toString()).append(" as ").append(FatEspelhoItemContaHospVO.Fields.IPH_SEQ.toString())
		   
		   .append(" ,SUM(COALESCE(EIC.").append(FatEspelhoItemContaHosp.Fields.QUANTIDADE.toString()).append(",0)) AS ").append(FatEspelhoItemContaHospVO.Fields.QUANTIDADE.toString())
		   .append(" ,SUM(COALESCE(EIC.").append(FatEspelhoItemContaHosp.Fields.VALOR_SERV_HOSP.toString()).append(",0)) AS ").append(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_HOSP.toString())
		   .append(" ,SUM(COALESCE(EIC.").append(FatEspelhoItemContaHosp.Fields.VALOR_SERV_PROF.toString()).append(",0)) AS ").append(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_PROF.toString())
		   .append(" ,SUM(COALESCE(EIC.").append(FatEspelhoItemContaHosp.Fields.VALOR_SADT.toString()).append(",0)) AS ").append(FatEspelhoItemContaHospVO.Fields.VALOR_SADT.toString())
		   .append(" ,SUM(COALESCE(EIC.").append(FatEspelhoItemContaHosp.Fields.VALOR_PROCEDIMENTO.toString()).append(",0)) AS ").append(FatEspelhoItemContaHospVO.Fields.VALOR_PROCEDIMENTO.toString())
		   .append(" ,SUM(COALESCE(EIC.").append(FatEspelhoItemContaHosp.Fields.VALOR_ANESTESISTA.toString()).append(",0)) AS ").append(FatEspelhoItemContaHospVO.Fields.VALOR_ANESTESISTA.toString())
		   .append(" ,SUM(COALESCE(EIC.").append(FatEspelhoItemContaHosp.Fields.PONTOS_ANESTESISTA.toString()).append(",0)) AS ").append(FatEspelhoItemContaHospVO.Fields.PONTOS_ANESTESISTA.toString())
		   .append(" ,SUM(COALESCE(EIC.").append(FatEspelhoItemContaHosp.Fields.PONTOS_CIRURGIAO.toString()).append(",0)) AS ").append(FatEspelhoItemContaHospVO.Fields.PONTOS_CIRURGIAO.toString())
		   .append(" ,SUM(COALESCE(EIC.").append(FatEspelhoItemContaHosp.Fields.PONTOS_SADT.toString()).append(",0)) AS ").append(FatEspelhoItemContaHospVO.Fields.PONTOS_SADT.toString())
		   
		   .append(" FROM ").append(FatEspelhoItemContaHosp.class.getSimpleName()).append(" AS EIC ")
		   .append("  JOIN EIC.").append(FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString()).append(" AS IPH ")
		
		   .append(" WHERE 1=1 ")
		   .append("  AND EIC.").append(FatEspelhoItemContaHosp.Fields.IND_TIPO_ITEM.toString()).append(" = :situacaoTipoItem ")
		   .append("  AND EIC.").append(FatEspelhoItemContaHosp.Fields.IND_CONSISTENTE.toString()).append(" = :situacaoConsistente ")		
		   .append("  AND EIC.").append(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString()).append(" = :cthSeq ");
		
		if(Boolean.TRUE.equals(pPrevia)){
			hql.append("  AND EIC.").append(FatEspelhoItemContaHosp.Fields.DATA_PREVIA.toString()).append(" is not null ");
			
		} else {
			hql.append("  AND EIC.").append(FatEspelhoItemContaHosp.Fields.DATA_PREVIA.toString()).append(" is null ");
		}
		
		hql.append(" AND EXISTS (SELECT 1 FROM ").append(FatEspelhoAih.class.getSimpleName()).append(" AS EAI_IN ")
						.append(" WHERE 1=1 ")
						.append("  AND EAI_IN.").append(FatEspelhoAih.Fields.CTH_SEQ.toString()).append(" = :cthSeq ");

				if(Boolean.TRUE.equals(pPrevia)){
					hql.append("  and EAI_IN.").append(FatEspelhoAih.Fields.DATA_PREVIA.toString()).append(" is not null ");
					
				} else {
					hql.append("  and EAI_IN.").append(FatEspelhoAih.Fields.DATA_PREVIA.toString()).append(" is null ");
				}
		
		hql.append("           )");

		hql.append(" GROUP BY EIC.").append(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString())
		   .append("  , EIC.").append(FatEspelhoItemContaHosp.Fields.TIV_SEQ.toString())
		   .append("  , EIC.").append(FatEspelhoItemContaHosp.Fields.TAO_SEQ.toString())
		   .append("  , EIC.").append(FatEspelhoItemContaHosp.Fields.IPH_PHO_SEQ.toString())
		   .append("  , EIC.").append(FatEspelhoItemContaHosp.Fields.IPH_SEQ.toString());
		
		hql.append(" ORDER BY EIC.").append(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString());
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", pCth);
		query.setParameter("situacaoConsistente", true);
		query.setParameter("situacaoTipoItem", pTipo);
		
		return query.setResultTransformer(Transformers.aliasToBean(FatEspelhoItemContaHospVO.class)).list();
	}
	
	
	public List<FatEspelhoItemContaHospVO> cItemAgrupPorProcComp(Integer pCth, DominioTipoItemEspelhoContaHospitalar pTipo, Boolean pPrevia,
			final Long procHospSus, final Byte tivSeq, final Byte taoSeq, final Short iphPhoSeq, final Integer iphSeq){
		
		DetachedCriteria criteria = this.getCItemCriteriaPorProcedimentoCompetencia(pCth, pTipo, pPrevia, procHospSus, tivSeq, taoSeq, iphPhoSeq, iphSeq);
		
		criteria.setProjection(Projections.projectionList()				
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString()), FatEspelhoItemContaHospVO.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.TIV_SEQ.toString()), FatEspelhoItemContaHospVO.Fields.TIV_SEQ.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.TAO_SEQ.toString()), FatEspelhoItemContaHospVO.Fields.TAO_SEQ.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IPH_PHO_SEQ.toString()), FatEspelhoItemContaHospVO.Fields.IPH_PHO_SEQ.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IPH_SEQ.toString()), FatEspelhoItemContaHospVO.Fields.IPH_SEQ.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IND_MODO_COBRANCA.toString()), FatEspelhoItemContaHospVO.Fields.IND_MODO_COBRANCA.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IND_LOCAL_SOMA.toString()), FatEspelhoItemContaHospVO.Fields.IND_LOCAL_SOMA.toString()))				
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString() + "." + FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString()), FatEspelhoItemContaHospVO.Fields.IPH_INTERNACAO.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IND_TIPO_ITEM.toString()), FatEspelhoItemContaHospVO.Fields.IND_TIPO_ITEM.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.CGC.toString()), FatEspelhoItemContaHospVO.Fields.CGC.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.COMPETENCIA_UTI.toString()), FatEspelhoItemContaHospVO.Fields.COMPETENCIA_UTI.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.IND_EQUIPE.toString()), FatEspelhoItemContaHospVO.Fields.IND_EQUIPE.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.CBO.toString()), FatEspelhoItemContaHospVO.Fields.CBO.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.CPF_CNS.toString()), FatEspelhoItemContaHospVO.Fields.CPF_CNS.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.SERIE_OPM.toString()), FatEspelhoItemContaHospVO.Fields.SERIE_OPM.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.LOTE_OPM.toString()), FatEspelhoItemContaHospVO.Fields.LOTE_OPM.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.REG_ANVISA_OPM.toString()), FatEspelhoItemContaHospVO.Fields.REG_ANVISA_OPM.toString()))
				.add(Projections.alias(Projections.groupProperty(FatEspelhoItemContaHosp.Fields.CNPJ_REG_ANVISA.toString()), FatEspelhoItemContaHospVO.Fields.CNPJ_REG_ANVISA.toString()))
				
				.add(Projections.alias(Projections.min(FatEspelhoItemContaHosp.Fields.NOTA_FISCAL.toString()), FatEspelhoItemContaHospVO.Fields.NOTA_FISCAL.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.QUANTIDADE.toString()), FatEspelhoItemContaHospVO.Fields.QUANTIDADE.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_SERV_HOSP.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_SERV_HOSP.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_SERV_PROF.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_SERV_PROF.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_SADT.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_SADT.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_PROCEDIMENTO.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_PROCEDIMENTO.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.VALOR_ANESTESISTA.toString()), FatEspelhoItemContaHospVO.Fields.VALOR_ANESTESISTA.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.PONTOS_ANESTESISTA.toString()), FatEspelhoItemContaHospVO.Fields.PONTOS_ANESTESISTA.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.PONTOS_CIRURGIAO.toString()), FatEspelhoItemContaHospVO.Fields.PONTOS_CIRURGIAO.toString()))
				.add(Projections.alias(Projections.sum(FatEspelhoItemContaHosp.Fields.PONTOS_SADT.toString()), FatEspelhoItemContaHospVO.Fields.PONTOS_SADT.toString()))
				);
							
		criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.IPH_INTERNACAO.toString()));
		
		if(DominioTipoItemEspelhoContaHospitalar.O.equals(pTipo)){
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString()));
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_HOSP.toString()));			
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_PROF.toString()));
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.VALOR_SADT.toString()));
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.VALOR_PROCEDIMENTO.toString()));
			criteria.addOrder(Order.asc(FatEspelhoItemContaHospVO.Fields.VALOR_ANESTESISTA.toString()));
		} else if(DominioTipoItemEspelhoContaHospitalar.D.equals(pTipo)){
			//FIXME: CORRIGIR ESSA ORDENAÇÃO, "DECODES" 
			criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_HOSP.toString()));			
			criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_SERV_PROF.toString()));
			criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_SADT.toString()));
			criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_PROCEDIMENTO.toString()));
			criteria.addOrder(Order.desc(FatEspelhoItemContaHospVO.Fields.VALOR_ANESTESISTA.toString()));
		}
		
	
		criteria.setResultTransformer(Transformers.aliasToBean(FatEspelhoItemContaHospVO.class));
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Cursor <code>c_iec</code>
	 * 
	 * @param cthSeq
	 * @return
	 */
	public List<FatEspelhoItemContaHosp> listarEspelhoItemContaHospPorCthSeq(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class, "IEC");
		
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), cthSeq));

		criteria.createAlias(FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), FatEspelhoItemContaHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString());
		
		criteria.addOrder(Order.asc("IEC." + FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString()));
		criteria.addOrder(Order.asc("IEC." + FatEspelhoItemContaHosp.Fields.TIV_SEQ.toString()));
		criteria.addOrder(Order.asc("IEC." + FatEspelhoItemContaHosp.Fields.TAO_SEQ.toString()));
		
		return executeCriteria(criteria);
	}


	public FatEspelhoItemContaHosp obterFatEspelhoItemContaHospPorCthSeqSeqpTivTaoCodSus(final Integer cthSeq, final Short seqp, final Long pCodSus, 
			final Byte pTivSeq, final Byte pTaoSeq) {
		return (FatEspelhoItemContaHosp) executeCriteriaUniqueResult(obterCriteriaPorCthSeqSeqpTivTaoCodSus(cthSeq, seqp, pCodSus, 
				pTivSeq, pTaoSeq));
	}
	
	public List<FatEspelhoItemContaHosp> buscarPorCthSeqSeqpTivTaoCodSus(final Integer cthSeq, final Long pCodSus, 
			final Byte pTivSeq, final Byte pTaoSeq) {
		return executeCriteria(obterCriteriaPorCthSeqTivTaoCodSus(cthSeq, pCodSus, 
				pTivSeq, pTaoSeq));
	}

	public DetachedCriteria obterCriteriaPorCthSeqTivTaoCodSus(final Integer cthSeq, final Long pCodSus, final Byte pTivSeq, final Byte pTaoSeq) {
		/*
		 * where 
		 * ich_cth_seq = p_cth_seq 
		 * and procedimento_hospitalar_sus = p_cod_sus 
		 * and tiv_seq = p_tiv_seq 
		 * and tao_seq = p_tao_seq
		 */
		DetachedCriteria criteria = obterCriteriaPorIchCthSeqProcedHospSusTaoSeq(cthSeq, pCodSus, pTaoSeq);
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.TIV_SEQ.toString(), pTivSeq));
		return criteria;
	}

	public DetachedCriteria obterCriteriaPorCthSeqSeqpTivTaoCodSus(final Integer cthSeq, final Short seqp, final Long pCodSus, final Byte pTivSeq,
			final Byte pTaoSeq) {
		/*
		 * where 
		 * ich_cth_seq = p_cth_seq 
		 * and procedimento_hospitalar_sus = p_cod_sus 
		 * and tiv_seq = p_tiv_seq 
		 * and tao_seq = p_tao_seq 
		 * and seqp = tab_iec(ind_iec).seqp;
		 */
		
		DetachedCriteria criteria = obterCriteriaPorIchCthSeqProcedHospSusTaoSeq(cthSeq, pCodSus, pTaoSeq);
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.TIV_SEQ.toString(), pTivSeq));
		
		return criteria;
	}

	private DetachedCriteria obterCriteriaPorIchCthSeqProcedHospSusTaoSeq(final Integer pCth, final Long procedimentoHospitalarSus, final Byte taoSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoItemContaHosp.class);
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.ICH_CTH_SEQ.toString(), pCth));
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.PROCEDIMENTO_HOSPITALAR_SUS.toString(), procedimentoHospitalarSus));
		criteria.add(Restrictions.eq(FatEspelhoItemContaHosp.Fields.TAO_SEQ.toString(), taoSeq));
		
		return criteria;
	}
	
	public Short obterCursorBuscaNovaQtde (final Integer pCth, final Long pProcedimentoHospitalarSus, final Byte pTaoSeq){
		
		DetachedCriteria criteria = obterCriteriaPorIchCthSeqProcedHospSusTaoSeq(pCth, pProcedimentoHospitalarSus, pTaoSeq);
		String[] columnAliases = {"qtde"}; 
		Type[] types = {ShortType.INSTANCE};
		String sql =  " COALESCE ( this_." + FatEspelhoItemContaHosp.Fields.QUANTIDADE.toString() + ", 0) qtde ";
		
		criteria.setProjection(Projections.sqlProjection(sql, columnAliases, types));
		
		return (Short) executeCriteriaUniqueResult(criteria);
	}
}
