package br.gov.mec.aghu.prescricaomedica.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmTrfDestino;

/**
 * 
 * @author heliz
 *
 */
public class MpmTrfDestinoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmTrfDestino> {

	
	private static final long serialVersionUID = -7389831632986002394L;

	/**
	 * 
	 * @param asuApaAtdSeq
	 * @param asuApaSeq
	 * @param asuSeqp
	 * @return
	 */
	public MpmTrfDestino obterEquipeDestino(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTrfDestino.class);
		criteria.add(Restrictions.eq(MpmTrfDestino.Fields.ASU_APA_ATD_SEQ.toString(), asuApaAtdSeq));
		criteria.add(Restrictions.eq(MpmTrfDestino.Fields.ASU_APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmTrfDestino.Fields.ASU_SEQP.toString(), asuSeqp));
		return (MpmTrfDestino) executeCriteriaUniqueResult(criteria);
//		MpmTrfDestinoId id = new MpmTrfDestinoId(asuApaAtdSeq, asuApaSeq, asuSeqp);
//		return obterPorChavePrimaria(id);
	}
//	public List<MpmTrfDestino> obterTrfDestinoComAltaSumarioEPaciente(Integer apaAtdSeq){
//		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTrfDestino.class);
//		criteria.createAlias(MpmTrfDestino.Fields.ALTA_SUMARIO.toString(), "ASU");
//		criteria.createAlias("ASU."+MpmAltaSumario.Fields.PACIENTE.toString(), "PAC");
//		
//	//	if(apaAtdSeq != null){
//	//		criteria.add(Restrictions.eq(MpmTrfDestino.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));		
//	//	}
//		criteria.add(Restrictions.eq(MpmTrfDestino.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
//		criteria.add(Restrictions.eq("ASU."+MpmAltaSumario.Fields.IND_TIPO.toString(), DominioIndTipoAltaSumarios.TRF));
//		criteria.add(Restrictions.eq("ASU."+MpmAltaSumario.Fields.TRANSFCONCLUIDA.toString(), Boolean.TRUE));
//		
//		return executeCriteria(criteria);
//	}
}
