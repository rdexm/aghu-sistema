package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.VFatAssocProcCids;

public class VFatAssocProcCidsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VFatAssocProcCids> {

	private static final long serialVersionUID = -471832429741214076L;
	
	private DetachedCriteria obterCriteria(){
		return DetachedCriteria.forClass(VFatAssocProcCids.class);
	}

	/**
	 * Método para listar AghCid de VFatAssocProcCids filtrando por ItemProcedimentoHospitalar e Convenio.
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param cpgCphCspCnvCodigo
	 * @param order
	 * @return List<AghCid>
	 */
	public List<AghCid> listarPorItemProcedimentoHospitalarEConvenio(Short iphPhoSeq, Integer iphSeq, Short cpgCphCspCnvCodigo, String order){
		DetachedCriteria criteria = obterCriteria();
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(VFatAssocProcCids.Fields.CODIGO_CID.toString())), AghCid.Fields.CODIGO.toString())
				.add((Projections.property(VFatAssocProcCids.Fields.DESCRICAO_CID.toString())), AghCid.Fields.DESCRICAO.toString())
		);
		
		criteria.createAlias(VFatAssocProcCids.Fields.CID.toString(), VFatAssocProcCids.Fields.CID.toString());
		
		if(iphPhoSeq != null){
			criteria.add(Restrictions.eq(VFatAssocProcCids.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		}
		if(iphSeq != null){
			criteria.add(Restrictions.eq(VFatAssocProcCids.Fields.IPH_SEQ.toString(), iphSeq));
		}
		if(cpgCphCspCnvCodigo != null){
			criteria.add(Restrictions.eq(VFatAssocProcCids.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));
		}
		if(order != null){
			criteria.addOrder(Order.asc(order));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(AghCid.class));
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Método para listar FatItensProcedHospitalar de VFatAssocProcCids filtrando por cidCodigo e Convenio.
	 * 
	 * @param cidCodigo
	 * @param cpgCphCspCnvCodigo
	 * @param campoOrder
	 * @param order
	 * @return List<FatItensProcedHospitalar>
	 */
	public List<FatItensProcedHospitalar> listarPorCidEConvenio(String cidCodigo, Short cpgCphCspCnvCodigo, String campoOrder, Boolean order){
		DetachedCriteria criteria = obterCriteria();
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(VFatAssocProcCids.Fields.ITEM_PROCED_HOSPITALAR.toString() +"."+ FatItensProcedHospitalar.Fields.SEQ.toString())), FatItensProcedHospitalar.Fields.SEQ_ORDER.toString())
				.add((Projections.property(VFatAssocProcCids.Fields.ITEM_PROCED_HOSPITALAR.toString() +"."+ FatItensProcedHospitalar.Fields.DESCRICAO.toString())), FatItensProcedHospitalar.Fields.DESCRICAO.toString())
				.add((Projections.property(VFatAssocProcCids.Fields.ITEM_PROCED_HOSPITALAR.toString() +"."+ FatItensProcedHospitalar.Fields.COD_TABELA.toString())), FatItensProcedHospitalar.Fields.COD_TABELA.toString())
		);
		
		criteria.createAlias(VFatAssocProcCids.Fields.ITEM_PROCED_HOSPITALAR.toString(), VFatAssocProcCids.Fields.ITEM_PROCED_HOSPITALAR.toString());
		
		
		if(StringUtils.isNotBlank(cidCodigo)){
			criteria.add(Restrictions.ilike(VFatAssocProcCids.Fields.CID_CODIGO.toString(), cidCodigo, MatchMode.EXACT));
		}
		if(cpgCphCspCnvCodigo != null){
			criteria.add(Restrictions.eq(VFatAssocProcCids.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));
		}
		if(StringUtils.isNotBlank(campoOrder)){
			if(order == null || order){
				criteria.addOrder(Order.asc(campoOrder));
			}else{
				criteria.addOrder(Order.desc(campoOrder));
			}
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(FatItensProcedHospitalar.class));
		
		return executeCriteria(criteria);
	}
}
