package br.gov.mec.aghu.protocolos.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MpaCadIntervaloTempo;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;



public class MpaCadIntervaloTempoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpaCadIntervaloTempo> {

	private static final long serialVersionUID = 469755185446429926L;

	public List<CadIntervaloTempoVO> listarIntervalosTempo(Short vpaSeqp, Short vpaPtaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpaCadIntervaloTempo.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MpaCadIntervaloTempo.Fields.VPA_SEQP.toString())
						, CadIntervaloTempoVO.Fields.VPA_SEQP.toString())
				.add(Projections.property(MpaCadIntervaloTempo.Fields.VPA_PTA_SEQ.toString())
						, CadIntervaloTempoVO.Fields.VPA_PTA_SEQ.toString())
				.add(Projections.property(MpaCadIntervaloTempo.Fields.SEQP.toString())
						, CadIntervaloTempoVO.Fields.SEQP.toString())
				.add(Projections.property(MpaCadIntervaloTempo.Fields.DIA_REFERENCIA.toString())
						, CadIntervaloTempoVO.Fields.DIA_REFERENCIA.toString())
				.add(Projections.property(MpaCadIntervaloTempo.Fields.HORA_INICIO_REFERENCIA.toString())
						, CadIntervaloTempoVO.Fields.HR_INICIO_REFERENCIA.toString())
				.add(Projections.property(MpaCadIntervaloTempo.Fields.HORA_FIM_REFERENCIA.toString())
						, CadIntervaloTempoVO.Fields.HR_FIM_REFERENCIA.toString()));
		
		criteria.add(Restrictions.eq(MpaCadIntervaloTempo.Fields.VPA_SEQP.toString(), vpaSeqp));
		criteria.add(Restrictions.eq(MpaCadIntervaloTempo.Fields.VPA_PTA_SEQ.toString(), vpaPtaSeq));
		criteria.addOrder(Order.asc(MpaCadIntervaloTempo.Fields.DIA_REFERENCIA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(CadIntervaloTempoVO.class));
		
		return executeCriteria(criteria);
	}
}
	
		
	