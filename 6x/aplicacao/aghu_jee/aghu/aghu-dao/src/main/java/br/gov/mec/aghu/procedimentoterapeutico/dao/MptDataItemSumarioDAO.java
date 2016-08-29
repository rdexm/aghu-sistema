package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MptDataItemSumario;
import br.gov.mec.aghu.model.MptItemPrescricaoSumario;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioQuimioItensPOLVO;



public class MptDataItemSumarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptDataItemSumario> {
	private static final long serialVersionUID = 3867211750081771630L;

	/**
	 * C3 da #18505 Sumário prescrição Quimio POL
	 * @param dtInicial
	 * @param dtFinal
	 * @param idAtendimentoPaciente
	 * @return
	 */
	public List<SumarioQuimioItensPOLVO> listarDadosPeriodosSumarioPrescricaoQuimio(Date dtInicial, Date dtFinal, Integer apaAtdSeq, Integer apaSeq){
		 DetachedCriteria criteria = DetachedCriteria.forClass(MptDataItemSumario.class, "diq");
		 
		 // where diq.apa_atd_seq = diq1.apa_atd_seq
		 // and   diq.apa_seq     = diq1.apa_seq
		 // and   diq.itq_seq     = diq1.itq_seq
		 // and   diq.seqp        = diq1.seqp
		 criteria.createAlias("diq." + MptDataItemSumario.Fields.MPT_DATA_ITEM_SUMARIO.toString(), "diq1");
		 
		 // where diq.itq_seq = itq.seq
		 criteria.createAlias("diq." + MptDataItemSumario.Fields.MPT_ITEM_PRESCRICAO_SUMARIOS.toString(), "itq");
		 
		 // where diq1.data BETWEEN :dtInicial and :dtFinal
		 criteria.add(Restrictions.between("diq1." + MptDataItemSumario.Fields.DATA.toString(), dtInicial, dtFinal));
		  
		 // where diq.apa_atd_seq = :apaAtdSeq
		 criteria.add(Restrictions.eq("diq." + MptDataItemSumario.Fields.ID_APA_ATD_SEQ.toString(), apaAtdSeq));
		 criteria.add(Restrictions.eq("diq." + MptDataItemSumario.Fields.ID_APA_SEQ.toString(), apaSeq));
		  
		 criteria.setProjection
			(Projections.projectionList()
			.add(Projections.property("diq." + MptDataItemSumario.Fields.DATA.toString()), 
					SumarioQuimioItensPOLVO.Fields.DIQ_DATA.toString())
			.add(Projections.property("itq." + MptItemPrescricaoSumario.Fields.TIPO.toString()), 
					SumarioQuimioItensPOLVO.Fields.ITQ_TIPO_INTEGER.toString())
			.add(Projections.property("itq." + MptItemPrescricaoSumario.Fields.SEQ.toString()), 
					SumarioQuimioItensPOLVO.Fields.ITQ_SEQ.toString())
			.add(Projections.property("itq." + MptItemPrescricaoSumario.Fields.DESCRICAO.toString()), 
					SumarioQuimioItensPOLVO.Fields.ITQ_DESCRICAO.toString())
			.add(Projections.property("diq1." + MptDataItemSumario.Fields.VALOR.toString()), 
					SumarioQuimioItensPOLVO.Fields.DIQ_VALOR.toString())
			.add(Projections.property("diq." + MptDataItemSumario.Fields.ID_APA_ATD_SEQ.toString()), 
					SumarioQuimioItensPOLVO.Fields.DIQ_APA_ATD_SEQ.toString())
			.add(Projections.property("diq." + MptDataItemSumario.Fields.ID_APA_SEQ.toString()), 
					SumarioQuimioItensPOLVO.Fields.DIQ_APA_SEQ.toString())	
			.add(Projections.property("diq." + MptDataItemSumario.Fields.ID_SEQP.toString()), 
					SumarioQuimioItensPOLVO.Fields.DIQ_SEQP.toString())
			);
		 
		 criteria.setResultTransformer(Transformers.aliasToBean(SumarioQuimioItensPOLVO.class));
					 
		return executeCriteria(criteria);
	 }

	public List<MptDataItemSumario> pesquisarDataItemSumario(Integer intAtdSeq, Integer apaSeq, Integer itqSeq) {
		
		 DetachedCriteria criteria = DetachedCriteria.forClass(MptDataItemSumario.class);
		 
		 criteria.add(Restrictions.eq(MptDataItemSumario.Fields.ID_APA_ATD_SEQ.toString(), intAtdSeq));
		 criteria.add(Restrictions.eq(MptDataItemSumario.Fields.ID_APA_SEQ.toString(), apaSeq));
		 criteria.add(Restrictions.eq(MptDataItemSumario.Fields.ID_ITQ_SEQ.toString(), itqSeq));
		 criteria.addOrder(Order.asc(MptDataItemSumario.Fields.ID_SEQP.toString()));	
		 
		 return executeCriteria(criteria);
	}

}
