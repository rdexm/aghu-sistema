package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelItemSolicConsultado;
import br.gov.mec.aghu.model.RapServidores;

public class AelItemSolicConsultadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelItemSolicConsultado> {
	
	private static final long serialVersionUID = 8627278153678609110L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelItemSolicConsultado.class);
    }

	public List<AelItemSolicConsultado> pesquisarAelItemSolicConsultadosResultadosExames(Integer iseSoeSeq, Short iseSeqp) {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicConsultado.class);
		criteria.add(Restrictions.eq(AelItemSolicConsultado.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelItemSolicConsultado.Fields.ISE_SEQP.toString(), iseSeqp));
		// Ordena a lista
		criteria.addOrder(Order.desc(AelItemSolicConsultado.Fields.CRIADO_EM.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public Date obterMaxCriadoEm(Integer iseSoeSeq, Short iseSeqp, RapServidores servidor){
		DetachedCriteria criteria = obterCriteria();
		
		Integer matricula = servidor.getId().getMatricula().intValue();
		Short vinculo = servidor.getId().getVinCodigo().shortValue();
		
		
		criteria.setProjection(Projections.max(AelItemSolicConsultado.Fields.CRIADO_EM.toString()));
		criteria.add(Restrictions.eq(AelItemSolicConsultado.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelItemSolicConsultado.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelItemSolicConsultado.Fields.SERVIDOR_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(AelItemSolicConsultado.Fields.SERVIDOR_CODIGO_VINCULO.toString(), vinculo));
		
		Date dataConsulta = (Date) executeCriteriaUniqueResult(criteria);
	
		return dataConsulta;
	}
	

	public List<Object[]> pesquisarServidorAelItemSolicConsultadosResultadosExames(Integer iseSoeSeq, Short iseSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicConsultado.class);
		criteria.add(Restrictions.eq(AelItemSolicConsultado.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelItemSolicConsultado.Fields.ISE_SEQP.toString(), iseSeqp));
	
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(AelItemSolicConsultado.Fields.SERVIDOR_MATRICULA.toString())))
				.add(Projections.property(AelItemSolicConsultado.Fields.SERVIDOR_CODIGO_VINCULO.toString())));

		return executeCriteria(criteria);
	}
	
}
