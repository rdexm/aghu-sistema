package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.faturamento.vo.CursorCAutorizadoCMSVO;
import br.gov.mec.aghu.model.FatAutorizadoCma;

public class FatAutorizadoCmaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatAutorizadoCma> {

	private static final long serialVersionUID = -4795919849605783392L;

	/**
	 * Primeira FatAutorizadoCma pelo CTH_SEQ e COD_SUS_CMA
	 * 
	 * @param cthSeq
	 * @param codSusCma
	 * @return
	 */
	public FatAutorizadoCma buscarPrimeiraPorCthCodSus(Integer cthSeq, Long codSusCma){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatAutorizadoCma.class);
		criteria.add(Restrictions.eq(FatAutorizadoCma.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatAutorizadoCma.Fields.COD_SUS_CMA.toString(), codSusCma));
		List<FatAutorizadoCma> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * ORADB: FATK_CTHN_RN_UN.RN_CTHC_ATU_INS_AAM.C_AUTORIZADO_SMS
	 * Primeira FatAutorizadoCma pelo CTH_SEQ e COD_SUS_CMA
	 * 
	 * DAOTest: FatAutorizadoCmaDAOTest.buscarPrimeiraPorCthCodSusComSomatorio (Retestar qd alterado) eSchweigert 17/09/2012
	 * 
	 * @param cthSeq
	 * @param codSusCma
	 * @return
	 */
	public CursorCAutorizadoCMSVO buscarPrimeiraPorCthCodSusComSomatorio(Integer cthSeq, Long codSusCma){
		/*
		    select cod_sus_cma, sum(qtd_proc)
		    from FAT_AUTORIZADOS_CMA
		    where cth_seq       = p_cth
		    and   cod_sus_cma   = p_sus_cma
		    group by cod_sus_cma;
		 */
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatAutorizadoCma.class);
		
		criteria.add(Restrictions.eq(FatAutorizadoCma.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatAutorizadoCma.Fields.COD_SUS_CMA.toString(), codSusCma));

		criteria.setProjection(Projections.projectionList()
										.add(Projections.groupProperty(FatAutorizadoCma.Fields.COD_SUS_CMA.toString()),CursorCAutorizadoCMSVO.Fields.COD_SUS_CMA.toString())
										.add(Projections.sum(FatAutorizadoCma.Fields.QTD_PROC.toString()), CursorCAutorizadoCMSVO.Fields.QTDE_PROC_CMA.toString())
							   );
		
		criteria.setResultTransformer(Transformers.aliasToBean(CursorCAutorizadoCMSVO.class));
		
		List<CursorCAutorizadoCMSVO> list = executeCriteria(criteria);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
}
