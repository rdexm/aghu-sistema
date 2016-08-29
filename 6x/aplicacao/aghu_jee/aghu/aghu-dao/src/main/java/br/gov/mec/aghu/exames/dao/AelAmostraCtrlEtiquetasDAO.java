package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelAmostraCtrlEtiquetas;
import br.gov.mec.aghu.model.AelAmostraCtrlEtiquetasId;

public class AelAmostraCtrlEtiquetasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAmostraCtrlEtiquetas> {
	
	private static final long serialVersionUID = 6228651270382845525L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraCtrlEtiquetas.class);
		return criteria;
    }
	
	@Override
	public void obterValorSequencialId(AelAmostraCtrlEtiquetas elemento) {
		
		if (elemento == null || elemento.getAelAmostras() == null) {
			
			throw new IllegalArgumentException("AelAmostras nao foi informado corretamente.");
		
		}
	
		AelAmostraCtrlEtiquetasId id = new AelAmostraCtrlEtiquetasId();

		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraCtrlEtiquetas.class);
		
		criteria.add(Restrictions.eq(AelAmostraCtrlEtiquetas.Fields.AMO_SOE_SEQ.toString(), elemento.getAelAmostras().getId().getSoeSeq()));
		criteria.add(Restrictions.eq(AelAmostraCtrlEtiquetas.Fields.AMO_SEQP.toString(), elemento.getAelAmostras().getId().getSeqp()));
		
		criteria.setProjection(Projections.max(AelAmostraCtrlEtiquetas.Fields.SEQP.toString()));

		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		id.setAmoSoeSeq(elemento.getAelAmostras().getId().getSoeSeq());
		id.setAmoSeqp(elemento.getAelAmostras().getId().getSeqp());
		id.setSeqp(++seqp);

		elemento.setId(id);
	}

	public List<AelAmostraCtrlEtiquetas> obterAmostraCtrlEtiquetas(Integer amoSoeSeq, Short amoSeqp) {
		DetachedCriteria criteria = this.obterCriteria();

		criteria.add(Restrictions.eq(AelAmostraCtrlEtiquetas.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		criteria.add(Restrictions.eq(AelAmostraCtrlEtiquetas.Fields.AMO_SEQP.toString(),amoSeqp));
		
		criteria.addOrder(Order.desc(AelAmostraCtrlEtiquetas.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Long contarQuantidadeVezesEtiquetaImpressa(Integer amoSoeSeq,  Short amoSeqp, Integer nroUnico, Date dtNumeroUnico) {
		
		DetachedCriteria criteria = this.obterCriteria();

		criteria.add(Restrictions.eq(AelAmostraCtrlEtiquetas.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		criteria.add(Restrictions.eq(AelAmostraCtrlEtiquetas.Fields.AMO_SEQP.toString(), amoSeqp));
		criteria.add(Restrictions.eq(AelAmostraCtrlEtiquetas.Fields.NRO_UNICO.toString(), nroUnico));
		criteria.add(Restrictions.eq(AelAmostraCtrlEtiquetas.Fields.DT_NUMERO_UNICO.toString(), dtNumeroUnico));

		return executeCriteriaCount(criteria);
	}
	
	
}
