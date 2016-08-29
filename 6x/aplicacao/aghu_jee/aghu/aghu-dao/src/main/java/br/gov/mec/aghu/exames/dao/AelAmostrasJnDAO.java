package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.exames.solicitacao.vo.HistoricoNumeroUnicoVO;
import br.gov.mec.aghu.model.AelAmostrasJn;
import br.gov.mec.aghu.core.model.BaseJournal;


public class AelAmostrasJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAmostrasJn> {
	
	
	
	private static final long serialVersionUID = -2097927365663592864L;

	public List<HistoricoNumeroUnicoVO> listarAmostrasJnPorSoeSeqESeqp(Integer soeSeq, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostrasJn.class, "aaj");

		criteria.add(Restrictions.eq("aaj." + AelAmostrasJn.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("aaj." + AelAmostrasJn.Fields.SEQP.toString(), seqp));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(BaseJournal.Fields.DATA_ALTERACAO.toString()), HistoricoNumeroUnicoVO.Fields.DATA_ALTERACAO.toString())
				.add(Projections.property(BaseJournal.Fields.NOME_USUARIO.toString()), HistoricoNumeroUnicoVO.Fields.JN_USER.toString())
				.add(Projections.property(AelAmostrasJn.Fields.SOE_SEQ.toString()), HistoricoNumeroUnicoVO.Fields.SOE_SEQ.toString())
				.add(Projections.property(AelAmostrasJn.Fields.SEQP.toString()), HistoricoNumeroUnicoVO.Fields.SEQP.toString())
				.add(Projections.property(AelAmostrasJn.Fields.NRO_UNICO.toString()), HistoricoNumeroUnicoVO.Fields.NRO_UNICO.toString())
				.add(Projections.property(AelAmostrasJn.Fields.DT_NUMERO_UNICO.toString()), HistoricoNumeroUnicoVO.Fields.DT_NUMERO_UNICO.toString())
		);
		
		criteria.addOrder(Order.asc(BaseJournal.Fields.DATA_ALTERACAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(HistoricoNumeroUnicoVO.class));
		
		return executeCriteria(criteria);
	}
}
