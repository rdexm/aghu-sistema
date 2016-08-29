package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ByteType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;

import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.faturamento.vo.CursorMaximasAtosMedicoAihTempVO;
import br.gov.mec.aghu.model.FatAtoMedicoAihTemp;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatCompetenciaCompatibilid;
import br.gov.mec.aghu.core.utils.DateUtil;

public class FatAtoMedicoAihTempDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatAtoMedicoAihTemp> {

	private static final long serialVersionUID = 1798311692102491559L;
	
	/** Remove registro do FatArqEspelhoProcedAmb
	 * 
	 * Usado HQL para melhorar desempenho é uma tabela temporária
	 * que não possui triggers
	 * 
	 * @param cpeDtHrInicio
	 * @param ano
	 * @param mes
	 * @param modulo
	 * @param tipoFormulario
	 */
	public void removeAtoMedicoAihTemp(Integer pCth) {
		StringBuilder hql = new StringBuilder(27);
		
		hql.append("delete from ");
		hql.append(FatAtoMedicoAihTemp.class.getName());
		hql.append(" where ");
		
		hql.append(FatAtoMedicoAihTemp.Fields.EAI_CTH_SEQ.toString()).append(" = :pCth");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("pCth", pCth);		
		query.executeUpdate();
	}
	
	public List<FatAtoMedicoAihTemp> buscarFatAtoMedicoAihTempPorEaiCthSeqSeqArqSus(final Integer eaiCthSeq, final Short seqArqSus){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAihTemp.class);
		criteria.add(Restrictions.eq(FatAtoMedicoAihTemp.Fields.EAI_CTH_SEQ.toString(), eaiCthSeq));
		criteria.add(Restrictions.eq(FatAtoMedicoAihTemp.Fields.SEQ_ARQ_SUS.toString(), seqArqSus));
		return executeCriteria(criteria);
	}
	
	/**
	 * ORADB: cursor: FATP_SEPARA_ITENS_POR_COMP.RN_QUANTIDADES_MAXIMAS.C_MAXIMAS
	 * DAOTest: FatAtoMedicoAihTempDAOTest.obterQtMaximasAtosMedicoAihTemp (Retestar qd alterado) eSchweigert 17/09/2012
	 */
	public List<CursorMaximasAtosMedicoAihTempVO> obterQtMaximasAtosMedicoAihTemp( final Short phoSeq, final Integer iphSeq, final Integer eaiCthSeq, 
																				   final Byte taoSeq, final Short seqArqSus, final Date competencia){
		/*
			select 
			  ama.iph_cod_sus,AMA.SEQP,
			  (ict.QUANTIDADE_MAXIMA) QUANTIDADE_MAXIMA    
			from 
			FAT_ATOS_MEDICOS_AIH_temp AMA,
			fat_compat_exclus_itens ict,
			FAT_COMPETENCIAS_COMPATIBILID cpx
			where   ict.IPH_PHO_SEQ_COMPATIBILIZA = c_iph_pho_seq
			and     ict.IPH_SEQ_COMPATIBILIZA = c_iph_seq
			and     ict.IND_COMPAT_EXCLUS in ('ICP','PCI') 
			--
			and   cpx.iph_pho_seq  = ict.iph_pho_seq
			and   cpx.iph_seq = ict.iph_seq 
			and   trunc(c_competencia) between trunc(cpx.dt_inicio_validade) and trunc(nvl(cpx.dt_fim_validade,sysdate)) 
			and cpx.dt_fim_validade is null
			and   cpx.seq = ict.cpx_seq
			--
			and ama.eai_cth_seq = p_cth_seq
			and    ama.iph_pho_seq = ict.iph_pho_seq
			and ama.iph_seq = ict.iph_seq 
			and ama.tao_SEQ = 1
			and AMA.COMPETENCIA_UTI = to_char(c_competencia,'yyyymm')
			and ama.seq_arq_sus = 999  
			order by ict.QUANTIDADE_MAXIMA desc    
		 */
		
		final StringBuffer sql = new StringBuffer(1000);

		sql.append("SELECT ")
		   .append("    AMA.IPH_COD_SUS AS ").append(CursorMaximasAtosMedicoAihTempVO.Fields.IPH_COD_SUS.toString())
		   .append("  , AMA.SEQP AS ").append(CursorMaximasAtosMedicoAihTempVO.Fields.SEQP.toString())
		   .append("  , (ICT.QUANTIDADE_MAXIMA) AS ").append(CursorMaximasAtosMedicoAihTempVO.Fields.QUANTIDADE_MAXIMA.toString())
		   .append(" FROM ")
   		   .append("     AGH.").append(FatAtoMedicoAihTemp.class.getAnnotation(Table.class).name()).append(" AMA, ")
   		   .append("     AGH.").append(FatCompatExclusItem.class.getAnnotation(Table.class).name()).append(" ICT, ")
   		   .append("     AGH.").append(FatCompetenciaCompatibilid.class.getAnnotation(Table.class).name()).append(" CPX ")
		   .append(" WHERE 1=1 ")
		   .append("     AND ICT.IPH_PHO_SEQ_COMPATIBILIZA = :PRM_IPH_PHO_SEQ ")
		   .append("     AND ICT.IPH_SEQ_COMPATIBILIZA     = :PRM_IPH_SEQ ")
		   .append("     AND ICT.IND_COMPAT_EXCLUS IN (:IND_COMPAT_EXCLUS) ")
		   .append("     AND CPX.IPH_PHO_SEQ  = ICT.IPH_PHO_SEQ ")
		   .append("     AND CPX.IPH_SEQ = ICT.IPH_SEQ ")
		   .append("     AND CPX.DT_FIM_VALIDADE IS NULL ")
		   .append("     AND CPX.SEQ = ICT.CPX_SEQ ")
		   .append("     AND AMA.EAI_CTH_SEQ = :PRM_CTH_SEQ ")
		   .append("     AND AMA.IPH_PHO_SEQ = ICT.IPH_PHO_SEQ ")
		   .append("     AND AMA.IPH_SEQ = ICT.IPH_SEQ ")
		   .append("     AND AMA.TAO_SEQ = :PRM_TAO_SEQ")
		   .append("     AND AMA.COMPETENCIA_UTI = :PRM_COMPETENCIA_ANO_MES")
		   .append("     AND AMA.SEQ_ARQ_SUS = :PRM_SEQ_ARQ_SUS ");
		
		if(isOracle()){
			// AND TRUNC(:PRM_COMPETENCIA) BETWEEN TRUNC(CPX.DT_INICIO_VALIDADE) AND TRUNC(NVL(CPX.DT_FIM_VALIDADE,SYSDATE))
			sql.append(" AND :PRM_COMPETENCIA BETWEEN TRUNC(CPX.DT_INICIO_VALIDADE) AND TRUNC(NVL(CPX.DT_FIM_VALIDADE,SYSDATE)) ");
		} else {
			sql.append(" AND :PRM_COMPETENCIA BETWEEN DATE_TRUNC('day', CPX.DT_INICIO_VALIDADE) AND DATE_TRUNC('day', (COALESCE(CPX.DT_FIM_VALIDADE,now()))) ");
		}
		
		sql.append(" ORDER BY ICT.QUANTIDADE_MAXIMA DESC ");
	   
		final SQLQuery query = createSQLQuery(sql.toString());
		query.setShort("PRM_IPH_PHO_SEQ", phoSeq);
		query.setInteger("PRM_IPH_SEQ", iphSeq);
		query.setInteger("PRM_CTH_SEQ", eaiCthSeq);
		query.setByte("PRM_TAO_SEQ", taoSeq);
		query.setShort("PRM_SEQ_ARQ_SUS", seqArqSus);
		query.setDate("PRM_COMPETENCIA", DateUtil.truncaData(competencia));
		query.setString("PRM_COMPETENCIA_ANO_MES", DateUtil.obterDataFormatada(competencia,"yyyyMM"));
		query.setParameterList("IND_COMPAT_EXCLUS", new String[] {DominioIndCompatExclus.PCI.toString(),DominioIndCompatExclus.ICP.toString() });
	   
		@SuppressWarnings("unchecked")
		final List<CursorMaximasAtosMedicoAihTempVO> result =  query.addScalar(CursorMaximasAtosMedicoAihTempVO.Fields.IPH_COD_SUS.toString(), LongType.INSTANCE)
																    .addScalar(CursorMaximasAtosMedicoAihTempVO.Fields.SEQP.toString(),ByteType.INSTANCE)
																    .addScalar(CursorMaximasAtosMedicoAihTempVO.Fields.QUANTIDADE_MAXIMA.toString(),ShortType.INSTANCE)
																    .setResultTransformer(Transformers.aliasToBean(CursorMaximasAtosMedicoAihTempVO.class))
																    .list();

	   return result;
	}
	
	/**
	 * Cursor: c_ato
	 */
	public List<FatAtoMedicoAihTemp> cAto(Integer pCthSeq) {
		DetachedCriteria criteria = this.obterCriteriaCursoresCAtoCVin(pCthSeq, null, Byte.valueOf((byte) 1));
		return executeCriteria(criteria);
	}
	
	public List<FatAtoMedicoAihTemp> cVin(Integer pCthSeq, Long codSus){
		DetachedCriteria criteria = this.obterCriteriaCursoresCAtoCVin(pCthSeq, codSus, Byte.valueOf((byte) 1));
		return executeCriteria(criteria);
	}

	public void updateSeqp(Integer pCthSeq, Long codSus, Byte vSeqpVin) {
		DetachedCriteria criteria = this.obterCriteriaCursoresCAtoCVin(pCthSeq,	codSus, Byte.valueOf((byte) 6));
		List<FatAtoMedicoAihTemp> atosMed =  this.executeCriteria(criteria);
		if (atosMed != null && !atosMed.isEmpty()) {
			for (FatAtoMedicoAihTemp atoMed : atosMed) {
				atoMed.setSeqpVinculado(Short.valueOf(vSeqpVin));
				super.atualizar(atoMed);
			}
		}
	}

	private DetachedCriteria obterCriteriaCursoresCAtoCVin(Integer pCthSeq,	Long codSus, Byte taoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatAtoMedicoAihTemp.class);
		criteria.add(Restrictions.eq(FatAtoMedicoAihTemp.Fields.EAI_CTH_SEQ.toString(), pCthSeq));
		criteria.add(Restrictions.eq(FatAtoMedicoAihTemp.Fields.TAO_SEQ.toString(), taoSeq));
		criteria.add(Restrictions.le(FatAtoMedicoAihTemp.Fields.EAI_SEQ.toString(), Integer.valueOf(50)));
		if (codSus != null) {
			criteria.add(Restrictions.eq(FatAtoMedicoAihTemp.Fields.IPH_COD_SUS.toString(), codSus));
		}
		return criteria;
	}
}
