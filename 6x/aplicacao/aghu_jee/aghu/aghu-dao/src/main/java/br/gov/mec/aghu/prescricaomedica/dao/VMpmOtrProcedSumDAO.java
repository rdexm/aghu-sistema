package br.gov.mec.aghu.prescricaomedica.dao;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.VMpmOtrProcedSum;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class VMpmOtrProcedSumDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMpmOtrProcedSum> {

       
    private static final long serialVersionUID = 5676518652755497098L;

	public VMpmOtrProcedSum obterVMpmOtrProcedSum(Integer matCodigo, Integer pciSeq, Short pedSeq) {
    	DetachedCriteria criteria = DetachedCriteria.forClass(VMpmOtrProcedSum.class);
    	criteria.add(Restrictions.eq(VMpmOtrProcedSum.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
    	
    	Map<VMpmOtrProcedSum.Fields, BigDecimal> map = new HashMap<VMpmOtrProcedSum.Fields, BigDecimal>();
    	map.put(VMpmOtrProcedSum.Fields.MAT_CODIGO, matCodigo == null ? null : BigDecimal.valueOf(matCodigo));
    	map.put(VMpmOtrProcedSum.Fields.PCI_SEQ, pciSeq == null ? null : BigDecimal.valueOf(pciSeq));
    	map.put(VMpmOtrProcedSum.Fields.PED_SEQ, pedSeq == null ? null : BigDecimal.valueOf(pedSeq));
    	
    	for (Entry<VMpmOtrProcedSum.Fields, BigDecimal> entry : map.entrySet()) {
    		VMpmOtrProcedSum.Fields key = entry.getKey();
			BigDecimal value = entry.getValue();
			
			if (value == null) {
				criteria.add(Restrictions.isNull(key.toString()));	
			} else {
				criteria.add(Restrictions.eq(key.toString(), value));	
			}
		}
    	       	
    	return (VMpmOtrProcedSum) this.executeCriteriaUniqueResult(criteria);
    }
    
    @SuppressWarnings("unchecked")
    public List<VMpmOtrProcedSum> pesquisarVMpmOtrProcedSum(String srtPesquisa){
	
		StringBuilder sb = new StringBuilder(100);
		montaQueryVMpmOtrProcedSum(srtPesquisa, sb);
		sb.append(" order by descricao asc");
		Query q =   this.createQuery(sb.toString());
		return q.getResultList();
    }

	private void montaQueryVMpmOtrProcedSum(String srtPesquisa, StringBuilder sb) {
		sb.append("from " + VMpmOtrProcedSum.class.getName() + " where " + VMpmOtrProcedSum.Fields.IND_SITUACAO.toString() + " = '");
		sb.append(DominioSituacao.A).append('\'');
		
		if(StringUtils.isNotEmpty(srtPesquisa)){
		    srtPesquisa = CoreUtil.retirarCaracteresInvalidos(srtPesquisa);
		    srtPesquisa = srtPesquisa.toUpperCase();	    
		    sb.append(" and ( UPPER (" + VMpmOtrProcedSum.Fields.DESCRICAO.toString() + ") like '%").append(srtPesquisa).append("%'");
		    sb.append(" or UPPER(" + VMpmOtrProcedSum.Fields.CODIGO.toString() + ") like '%").append(srtPesquisa).append("%')");
		}
	}
    
    @SuppressWarnings("unchecked")
    public Long pesquisarVMpmOtrProcedSumCount(String srtPesquisa){
	
		StringBuilder sb = new StringBuilder(100);
		sb.append("select count(*) ");
		montaQueryVMpmOtrProcedSum(srtPesquisa, sb);
		Query q =   this.createQuery(sb.toString());
		return (Long) q.getSingleResult();
    }

}
