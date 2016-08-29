package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.vo.CursorAtosMedicosVO;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatCompetenciaCompatibilid;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;

public class FatCompatExclusItemDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCompatExclusItem> {

	private static final long serialVersionUID = 6550858794085853073L;
	
	public List<CursorAtosMedicosVO> obterListaPorIphEEaiCTH(Short iphPhoSeq, Integer iphSeq, Integer cthSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class,"FCE");		
		criteria.createAlias("FCE."+FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString(), "ITEM");
		criteria.createAlias("ITEM."+FatItensProcedHospitalar.Fields.ATOS_MEDICOS_AIH.toString(),"AAM");
		
		criteria.add(Restrictions.eq("FCE."+FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("FCE."+FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.toString(), iphSeq));
		criteria.add(Restrictions.eq("AAM."+FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.isNull("AAM."+FatAtoMedicoAih.Fields.SEQ_COMPATIBILIDADE.toString()));
		
		criteria.addOrder(Order.asc("AAM."+FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.toString()));
		
		criteria.setProjection(Projections.projectionList()
											.add(Projections.property("AAM."+FatAtoMedicoAih.Fields.EAI_SEQ.toString()),CursorAtosMedicosVO.Fields.EAI_SEQ.toString())
											.add(Projections.property("AAM."+FatAtoMedicoAih.Fields.EAI_CTH_SEQ.toString()),CursorAtosMedicosVO.Fields.EAI_CTH_SEQ.toString())
											.add(Projections.property("AAM."+FatAtoMedicoAih.Fields.SEQP.toString()),CursorAtosMedicosVO.Fields.SEQ_P.toString())
											.add(Projections.property("AAM."+FatAtoMedicoAih.Fields.SEQ_ARQ_SUS.toString()),CursorAtosMedicosVO.Fields.SEQ_ARQ_SUS.toString())
											.add(Projections.property("AAM."+FatAtoMedicoAih.Fields.SEQ_COMPATIBILIDADE.toString()),CursorAtosMedicosVO.Fields.SEQ_COMPATIBILIDADE.toString())
											.add(Projections.property("ITEM."+FatItensProcedHospitalar.Fields.PHO_SEQ.toString()),CursorAtosMedicosVO.Fields.IPH_PHO_SEQ.toString())
											.add(Projections.property("ITEM."+FatItensProcedHospitalar.Fields.SEQ.toString()),CursorAtosMedicosVO.Fields.IPH_SEQ.toString())
								);
		
		criteria.setResultTransformer(Transformers.aliasToBean(CursorAtosMedicosVO.class));
		
		return executeCriteria(criteria);
	}	

	protected DetachedCriteria obterCriteriaPorIphOrdenadaPorQtdDesc(Short iphPhoSeq,			
            Integer iphSeq, DominioIndCompatExclus... indCompatExclus) {
		
		DetachedCriteria result = null;
		
		result = DetachedCriteria.forClass(FatCompatExclusItem.class);		
		result.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.toString(), iphPhoSeq));
		result.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.toString(), iphSeq));
		result.add(Restrictions.in(FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString(), indCompatExclus));
		result.addOrder(Order.desc(FatCompatExclusItem.Fields.QUANTIDADE_MAXIMA.toString()));
		
		return result;
	}

	public List<FatCompatExclusItem> obterListaPorIphOrdenadaPorQtdDesc(Short iphPhoSeq,			
            Integer iphSeq, DominioIndCompatExclus... indCompatExclus) {
		
		List<FatCompatExclusItem>  result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaPorIphOrdenadaPorQtdDesc(iphPhoSeq, iphSeq, indCompatExclus);
		result = this.executeCriteria(criteria);
		
		return result;				
	}
	
	private static final String aliasCpx = "cpx";
	private static final String ponto = ".";	
	private static final String aliasCpxSql = "cpx2_";
	
	/** ORADB: CURSOR  FATK_ICT_RN_UN.C_TIPO_RESTRICAO_REALIZ */
	public List<DominioIndCompatExclus> buscaTiposRestricoesExistentesParaRealizado(Short iphPhoSeq, Integer iphSeq,
			DominioIndComparacao indComparacao, Boolean indInternacao,
			Date pCompetencia,
			DominioIndCompatExclus ...compatExclus) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class);

		criteria.createAlias(FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString(), FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString());
		
		criteria.createAlias(FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString()+"."+FatItensProcedHospitalar.Fields.FAT_COMPETENCIA_COMPATIBILIDS.toString(), aliasCpx);//-- Marina 22/07/2013
		
		criteria.setProjection(Projections.distinct(Projections.property(FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString())));		

		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.in(FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString(), compatExclus));
		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_COMPARACAO.toString(), indComparacao));
		
		final Object[] values = {"N", Boolean.TRUE.equals(indInternacao) ? "S" :"N"};
		final Type[] types = {StringType.INSTANCE, StringType.INSTANCE}; // ETB 01/01/2008
		criteria.add(Restrictions.sqlRestriction(" COALESCE({alias}."+FatCompatExclusItem.Fields.IND_INTERNACAO.name()+",?) = ?",values, types ));

		if(pCompetencia != null){
			//-- Marina 22/07/2013(Migrado do Oracle)
			if (isOracle()) {
				criteria.add(Restrictions.sqlRestriction(" TRUNC (?) BETWEEN TRUNC(" 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name() + " ) AND TRUNC(NVL(" 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name()+ ", SYSDATE)) ",
						(Object) pCompetencia, TimestampType.INSTANCE));
			} else {
				criteria.add(Restrictions.sqlRestriction("DATE_TRUNC ( 'day',cast( ? as TIMESTAMP)) BETWEEN DATE_TRUNC('day', " 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name() +  ") AND DATE_TRUNC('day', (COALESCE(" 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name() + ", now()))) ",
						(Object) pCompetencia, TimestampType.INSTANCE));
			}
			
		}
		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		return executeCriteria(criteria, true);
	}

	/** ORADB: CURSOR  FATK_ICT_RN_UN.C_TIPO_RESTRICAO_ITEM */
	public List<DominioIndCompatExclus> buscaTiposRestricoesExistentesParaItem(Short iphPhoSeq, Integer iphSeq,
			DominioIndComparacao indComparacao, Boolean indInternacao, Date pCompetencia, DominioIndCompatExclus ...compatExclus) {//-- Marina 24/07/2013
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class);
		criteria.createAlias(FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString(), "IPH");
		criteria.createAlias("IPH."+FatItensProcedHospitalar.Fields.FAT_COMPETENCIA_COMPATIBILIDS.toString(), "CPX");

		criteria.setProjection(Projections.distinct(Projections.property(FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString())));
		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.toString(), iphSeq));
		criteria.add(Restrictions.in(FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString(), compatExclus));
		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_COMPARACAO.toString(), indComparacao));
		
		final Object[] values = {"N", Boolean.TRUE.equals(indInternacao) ? "S" :"N"};
		final Type[] types = {StringType.INSTANCE, StringType.INSTANCE}; // ETB 01/01/2008
		criteria.add(Restrictions.sqlRestriction(" COALESCE({alias}."+FatCompatExclusItem.Fields.IND_INTERNACAO.name()+",?) = ?",values, types ));
		
		if(pCompetencia != null){
			if (isOracle()) {
				criteria.add(Restrictions.sqlRestriction(" TRUNC (?) BETWEEN TRUNC(cpx2_." 
						+ FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name() + " ) AND TRUNC(NVL(cpx2_." 
						+ FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name()+ ", SYSDATE)) ",
						(Object) pCompetencia, TimestampType.INSTANCE));
			} else {
				criteria.add(Restrictions.sqlRestriction("  DATE_TRUNC ( 'day',cast( ? as TIMESTAMP)) BETWEEN DATE_TRUNC('day', cpx2_." 
						+ FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name() +  ") AND DATE_TRUNC('day', (COALESCE(cpx2_." 
						+ FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name() + ", now()))) ",
						(Object) pCompetencia, TimestampType.INSTANCE));
			}
		}

		return executeCriteria(criteria); 
	}

	/**
	 * Busca todos os FatCompatExclusItem com filtro nos campos:<br>
	 * comparacao, internacao e compatibilzaExclusao<br>
	 * 
	 * @param compatExclus
	 * @param indComparacao
	 * @param indInternacao
	 * @return List of FatCompatExclusItem
	 */
	public List<FatCompatExclusItem> buscarFatCompatExclusItemCountILoad(DominioIndCompatExclus[] compatExclus
			, DominioIndComparacao indComparacao,
			Boolean indInternacao,Date pCompetencia) {
		
		String aliasCpx = "cpx";
		String ponto = ".";	
		String aliasCpxSql = "cpx3_";		
				
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class);

		criteria.createAlias(FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString(), FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString()); //item		
		
		criteria.createAlias(FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString(), FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString());
		
		criteria.createAlias(FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString()+"."+FatItensProcedHospitalar.Fields.FAT_COMPETENCIA_COMPATIBILIDS.toString(), aliasCpx);//-- Marina 22/07/2013		
		
 		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_COMPARACAO.toString(), indComparacao));
		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_INTERNACAO.toString(), indInternacao));
		criteria.add(Restrictions.in(FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString(), compatExclus));
		
		if(pCompetencia != null){
			//-- Marina 22/07/2013(Migrado do Oracle)
			if (isOracle()) {
				criteria.add(Restrictions.sqlRestriction(" TRUNC (?) BETWEEN TRUNC(" 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name() + " ) AND TRUNC(NVL(" 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name()+ ", SYSDATE)) ",
						(Object) pCompetencia, TimestampType.INSTANCE));
			} else {
				criteria.add(Restrictions.sqlRestriction("  DATE_TRUNC ( 'day',cast( ? as TIMESTAMP)) BETWEEN DATE_TRUNC('day', " 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name() +  ") AND DATE_TRUNC('day', (COALESCE(" 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name() + ", now()))) ",
						(Object) pCompetencia, TimestampType.INSTANCE));
			}
		}
		//--		
		
		List<FatCompatExclusItem> result = executeCriteria(criteria, true);
		return result;
	}

	/** ORADB: CURSOR  FATK_ICT_RN_UN.C_RESTRICAO_PCI_R */
	public FatCompatExclusItem buscarFatCompatExclusItemCountR(Short iphPhoSeq, Integer iphSeq, Short iphPhoSeqCompatibiliza,
			Integer iphSeqCompatibiliza, DominioIndComparacao indComparacao, Boolean indInternacao, String tipoTrans,
			Date pCompetencia, DominioIndCompatExclus ...compatExclus) {//-- Marina 22/07/2013
		
		String aliasCpx = "cpx";
		String ponto = ".";	
		String aliasCpxSql = "cpx4_";			
				
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class);

		criteria.createAlias(FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString(), FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString());
		criteria.createAlias(FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString(), FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString());
		criteria.createAlias(FatCompatExclusItem.Fields.TIPO_TRANSPLANTE.toString(), FatCompatExclusItem.Fields.TIPO_TRANSPLANTE.toString(), Criteria.LEFT_JOIN);
		criteria.createAlias(FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString()+"."+FatItensProcedHospitalar.Fields.FAT_COMPETENCIA_COMPATIBILIDS.toString(), aliasCpx);//-- Marina 22/07/2013		
		
		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.toString(), iphPhoSeqCompatibiliza));
		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.toString(), iphSeqCompatibiliza));
		criteria.add(Restrictions.in(FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString(), compatExclus));
 		criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_COMPARACAO.toString(), indComparacao));	
 		
 		final Object[] values = {"N", Boolean.TRUE.equals(indInternacao) ? "S" :"N"};
 		final Type[] types = {StringType.INSTANCE, StringType.INSTANCE}; // ETB 01/01/2008
 		criteria.add(Restrictions.sqlRestriction(" COALESCE({alias}."+FatCompatExclusItem.Fields.IND_INTERNACAO.name()+",?) = ?",values, types ));
 		
		if (tipoTrans != null) {
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.TTR_CODIGO.toString(), tipoTrans));
		}
		
		if(pCompetencia != null){
			//-- Marina 22/07/2013
			if (isOracle()) {
				criteria.add(Restrictions.sqlRestriction(" TRUNC (?) BETWEEN TRUNC(" 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name() + " ) AND TRUNC(NVL(" 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name()+ ", SYSDATE)) ",
						(Object) pCompetencia, TimestampType.INSTANCE));
			} else {
				criteria.add(Restrictions.sqlRestriction(" DATE_TRUNC ( 'day',cast( ? as TIMESTAMP)) BETWEEN DATE_TRUNC('day', " 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_INICIO_VALIDADE.name() +  ") AND DATE_TRUNC('day', (COALESCE(" 
						+ aliasCpxSql + ponto + FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE.name() + ", now()))) ",
						(Object) pCompetencia, TimestampType.INSTANCE));
			}
		}

		List<FatCompatExclusItem> result = executeCriteria(criteria, true);
		if(result != null && !result.isEmpty()){
			return result.get(0);
			
		} else {
			return null;
		}
	}

	public List<FatCompatExclusItem> listarFatCompatExclusItem(Short phoSeq, Integer seq, DominioSituacao indSituacao) {
		DetachedCriteria criteria = obterCriteriaFatCompatExclusItem(phoSeq, seq, indSituacao);
		criteria.addOrder(Order.asc(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.toString()));
		return executeCriteria(criteria);
	}

	protected DetachedCriteria obterCriteriaFatCompatExclusItem (Short phoSeq, Integer seq, 
			DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class);
		
		criteria.createAlias(FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString(), FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString());
		
		criteria.createAlias(FatCompatExclusItem.Fields.TIPO_TRANSPLANTE.toString(), FatCompatExclusItem.Fields.TIPO_TRANSPLANTE.toString(), JoinType.LEFT_OUTER_JOIN);
		
		if (phoSeq != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_PHO_SEQ.toString(), phoSeq));
		}
		if (seq != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_SEQ.toString(), seq));
		}
		if (indSituacao != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_IND_SITUACAO.toString(), indSituacao));
		}
		return criteria;
	}

	/**
	 * 
	 * @param iphPhoSeqCompatibiliza
	 * @param iphSeqCompatibiliza
	 * @param indComparacao
	 * @param iphIndSituacao
	 * @param colunaOrder
	 * @param order (true == asc)
	 * @return
	 */
	public List<FatCompatExclusItem> listarFatCompatExclusItemPorIphCompatibilizaEIndSituacaoEIndComparacao(Short iphPhoSeqCompatibiliza, Integer iphSeqCompatibiliza, DominioIndComparacao indComparacao, DominioSituacao iphIndSituacao, String colunaOrder, Boolean order){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class);
		
		criteria.createAlias(FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString(), FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString());
		criteria.createAlias(FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString(), FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString());
		
		if(iphPhoSeqCompatibiliza != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString() + "." + FatItensProcedHospitalar.Fields.PROCEDIMENTO_HOSPITALAR.toString() +"."+ FatProcedimentosHospitalares.Fields.SEQ.toString(), iphPhoSeqCompatibiliza));
		}
		
		if(iphSeqCompatibiliza != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString()+ "." + FatItensProcedHospitalar.Fields.SEQ_ORDER.toString(), iphSeqCompatibiliza));
		}

		if(indComparacao != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_COMPARACAO.toString(), indComparacao));
		}

		if(iphIndSituacao != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_SITUACAO_IPH.toString(), iphIndSituacao));
		}

		if(StringUtils.isNotBlank(colunaOrder)){
			if(order){
				criteria.addOrder(Order.asc(colunaOrder));
			}else{
				criteria.addOrder(Order.desc(colunaOrder));
			}
		}
		return executeCriteria(criteria);
	}
	
	public List<FatCompatExclusItem> listaCompatExclusItem(Short iphPhoSeq, Integer iphSeq, DominioIndComparacao indComparacao,
			DominioIndCompatExclus indCompatExclus, DominioSituacao indSituacao, String order) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class);
		criteria.createAlias(FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString(), FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString());
		criteria.createAlias(FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString(), FatCompatExclusItem.Fields.IPH_COMPATIBILIZA.toString());
		criteria.createAlias(FatCompatExclusItem.Fields.TIPO_TRANSPLANTE.toString(), FatCompatExclusItem.Fields.TIPO_TRANSPLANTE.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatCompatExclusItem.Fields.COMPETENCIA_COMPATIBILIDADE.toString(), "CCOMP");
		
		if (iphPhoSeq != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		}
		if (iphSeq != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_SEQ.toString(), iphSeq));
		}
		if (indComparacao != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_COMPARACAO.toString(), indComparacao));
		}
		if (indCompatExclus != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString(), indCompatExclus));
		}
		if (indSituacao != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_SITUACAO_IPH.toString(), indSituacao));
		}
		
		criteria.add(Restrictions.isNull("CCOMP."+FatCompetenciaCompatibilid.Fields.DT_FIM_VALIDADE));
		
		
		if (StringUtils.isNotBlank(order)) {
			criteria.addOrder(Order.asc(order));
		}
		
		return executeCriteria(criteria);
		
	}
	
	/** Executa cursor COMPAT_R
	 * 
	 * @param phoSeqCont
	 * @param iphSeqCont
	 * @param phoSeqComp
	 * @param iphSeqComp
	 * @param indComparacao
	 * @param indCompatExclus
	 * @param indSituacao
	 * @return
	 */
	public List<FatCompatExclusItem> executarCursorCompatR(Short phoSeqCont, Integer iphSeqCont, Short phoSeqComp, Integer iphSeqComp,
			DominioIndComparacao indComparacao,	DominioIndCompatExclus indCompatExclus, DominioSituacao indSituacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class);
		criteria.createAlias(FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString(), FatCompatExclusItem.Fields.ITEM_PROCED_HOSP.toString());
		if (phoSeqCont != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_PHO_SEQ.toString(), phoSeqCont));
		}
		if (iphSeqCont != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_SEQ.toString(), iphSeqCont));
		}
		if (indComparacao != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_COMPARACAO.toString(), indComparacao));
		}
		if (indCompatExclus != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_COMPAT_EXCLUS.toString(), indCompatExclus));
		}
		if (indSituacao != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_SITUACAO_IPH.toString(), indSituacao));
		}
		return executeCriteria(criteria);
	}
	
	/** Executa cursor COMPAT_I
	 * 
	 * @param phoSeqCont
	 * @param iphSeqCont
	 * @param phoSeqComp
	 * @param iphSeqComp
	 * @param indComparacao
	 * @param indCompatExclus
	 * @param indSituacao
	 * @return
	 */	
	public FatCompatExclusItem executarCursorCompatI(Short phoComp, Integer iphComp, Short pho, Integer iph,
			DominioIndComparacao indComparacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class);
		if (phoComp != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_PHO_SEQ.toString(), phoComp));
		}
		if (iphComp != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_SEQ.toString(), iphComp));
		}
		if (indComparacao != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_COMPARACAO.toString(), indComparacao));
		}
		if (pho != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.toString(), pho));
		}
		if (iph != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.toString(), iph));
		}
		List<FatCompatExclusItem> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}	
	
	/** Pesquisa Excludência
	 * 
	 * @param phoComp
	 * @param iphComp
	 * @param indComparacao
	 * @param indSituacao
	 * @return
	 */
	public List<FatCompatExclusItem> pesquisarExcludencia(Short phoComp, Integer iphComp, DominioIndComparacao indComparacao, DominioSituacao indSituacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatCompatExclusItem.class, "cei");
		
		if (phoComp != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_PHO_SEQ.toString(), phoComp));
		}
		if (iphComp != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IPH_SEQ.toString(), iphComp));
		}
		if (indComparacao != null){
			criteria.add(Restrictions.eq(FatCompatExclusItem.Fields.IND_COMPARACAO.toString(), indComparacao));
		}
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatItensProcedHospitalar.class, "iph");
		subCriteria.setProjection(Projections.property(FatItensProcedHospitalar.Fields.PHO_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("cei." + FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.toString(), "iph." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("cei." + FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.toString(), "iph." + FatItensProcedHospitalar.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq(FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), indSituacao));
		
		criteria.add(Subqueries.exists(subCriteria));
		
		criteria.addOrder(Order.asc(FatCompatExclusItem.Fields.IPH_PHO_SEQ_COMPATIBILIZA.toString()));
		criteria.addOrder(Order.asc(FatCompatExclusItem.Fields.IPH_SEQ_COMPATIBILIZA.toString()));
		
		return executeCriteria(criteria);
	}
}
