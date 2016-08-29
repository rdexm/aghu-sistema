package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamExtratoRegistro;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MamRegistroDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamRegistro> {

	private static final long serialVersionUID = 2037202897237222484L;
	
	
	/**
	 * #44179 - Atualizar ind_situacao na funcao MAMP_EMG_ATLZ_REG
	 * @author marcelo.deus
	 */
	public void atualizarSituacao(Long pRgtSeq, DominioSituacaoRegistro situacao) throws ApplicationBusinessException{
		MamRegistro registro = obterPorChavePrimaria(pRgtSeq);
		
		if(registro != null){
			registro.setIndSituacao(situacao);
			this.atualizar(registro);
		} 
	}
	
    /**
     * #44179 - CURSOR cur_rgt_pend
     * @author thiago.cortes
     * @return SEQ
     */
    public Long obterSeqPorTrgSeq(Long trgSeq){
                   DetachedCriteria criteria = DetachedCriteria.forClass(MamRegistro.class,"RGT");          
                   criteria.setProjection(Projections.property("RGT."+MamRegistro.Fields.SEQ.toString()));
                    criteria.add(Restrictions.eq("RGT."+MamRegistro.Fields.IND_SITUACAO.toString(),DominioSituacaoRegistro.PE));
                   
                   DetachedCriteria subCriteria = DetachedCriteria.forClass(MamRegistro.class,"RGTX");
                   subCriteria.setProjection(Projections.max("RGTX."+MamRegistro.Fields.SEQ.toString()));
                   subCriteria.add(Restrictions.eqProperty("RGTX." +MamRegistro.Fields.TRIAGEM_SEQ.toString(),
                                                   "RGT." +MamRegistro.Fields.TRIAGEM_SEQ.toString()));
                   criteria.add(Subqueries.propertiesIn(new String[]{"RGT."+MamRegistro.Fields.SEQ.toString()},subCriteria));
                   return (Long) executeCriteriaUniqueResult(criteria);
    }
    
     /**
	* #44179 - F03 - CURSOR cur_exr
	* @ORADB - MAMP_EMG_ATLZ_REG
	* @author marcelo.deus
	*/
	public Long obterValorMaximoExtratoRegistro(Long cRgtSeq){
	
		DetachedCriteria criteria = DetachedCriteria.forClass(MamExtratoRegistro.class, "MER");
		criteria.add(Restrictions.eq("MER." + MamExtratoRegistro.Fields.ID_RGT_SEQ.toString(), cRgtSeq));
		criteria.setProjection(Projections.max("MER." + MamExtratoRegistro.Fields.ID_SEQP.toString()));
	
		Long result = (Long) executeCriteriaUniqueResult(criteria);
		if (result == null) {
			result = (long) 0;
		}
		result++;
		return result;
	}
	
	/**
	 * Consulta C1 da estória #50937
	 * 
	 * @param atendimentoSeq
	 * @param criadoEm
	 * @return
	 */
	public List<MamRegistro> obterRegistroAnamnesePorAtendSeqCriadoEm(Integer atendimentoSeq, Date criadoEm){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRegistro.class,"RGT");
		List<DominioSituacaoRegistro> listInSituacaoRegistro = Arrays.asList(DominioSituacaoRegistro.PE, DominioSituacaoRegistro.VA);
		List<DominioIndPendenteAmbulatorio> listInPendente = Arrays.asList(DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V);
		
		criteria.add(Restrictions.eq("RGT." + MamRegistro.Fields.ATD_SEQ.toString(), atendimentoSeq));
		
		if (criadoEm != null){
			criteria.add(Restrictions.le("RGT." + MamRegistro.Fields.CRIADO_EM.toString(), criadoEm));
		}
		
		criteria.add(Restrictions.in("RGT." + MamRegistro.Fields.IND_SITUACAO.toString(), listInSituacaoRegistro));
		
		DetachedCriteria subquery = DetachedCriteria.forClass(MamAnamneses.class, "ANA");
		subquery.setProjection(Projections.property("ANA." + MamAnamneses.Fields.RGT_SEQ.toString()));
		subquery.add(Restrictions.eqProperty("ANA." + MamAnamneses.Fields.RGT_SEQ.toString(), "RGT." + MamRegistro.Fields.SEQ.toString()));
		subquery.add(Restrictions.in("ANA." + MamAnamneses.Fields.IND_PENDENTE.toString(), listInPendente));
		subquery.add(Restrictions.isNull("ANA." + MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()));
		
		criteria.add(Subqueries.propertyIn("RGT." + MamRegistro.Fields.SEQ.toString(), subquery));
		
		criteria.addOrder(Order.desc("RGT." + MamRegistro.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta C2 da estória #50937
	 * 
	 * @param atendimentoSeq
	 * @param criadoEm
	 * @return
	 */
	public List<MamRegistro> obterRegistroEvolucoesPorAtendSeqCriadoEm(Integer atendimentoSeq, Date criadoEm){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRegistro.class,"RGT");
		List<DominioIndPendenteAmbulatorio> listInPendente = Arrays.asList(DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V);
		
		criteria.add(Restrictions.eq("RGT." + MamRegistro.Fields.ATD_SEQ.toString(), atendimentoSeq));
		criteria.add(Restrictions.le("RGT." + MamRegistro.Fields.CRIADO_EM.toString(), criadoEm));
		
		criteria.add(Restrictions.eq("RGT." + MamRegistro.Fields.IND_SITUACAO.toString(), DominioSituacaoRegistro.VA));
		
		DetachedCriteria subquery = DetachedCriteria.forClass(MamEvolucoes.class, "EVO");
		subquery.setProjection(Projections.property("EVO." + MamEvolucoes.Fields.RGT_SEQ.toString()));
		subquery.add(Restrictions.eqProperty("EVO." + MamEvolucoes.Fields.RGT_SEQ.toString(), "RGT." + MamRegistro.Fields.SEQ.toString()));
		subquery.add(Restrictions.in("EVO." + MamEvolucoes.Fields.IND_PENDENTE.toString(), listInPendente));
		subquery.add(Restrictions.isNull("EVO." + MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));
		
		criteria.add(Subqueries.propertyIn("RGT." + MamRegistro.Fields.SEQ.toString(), subquery));
		
		criteria.addOrder(Order.desc("RGT." + MamRegistro.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}

}
