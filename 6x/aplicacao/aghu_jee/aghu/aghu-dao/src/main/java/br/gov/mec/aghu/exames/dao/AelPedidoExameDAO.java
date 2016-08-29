package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelPedidoExame;

public class AelPedidoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelPedidoExame> {


	private static final long serialVersionUID = -1002107368387502977L;

	public List<AelPedidoExame> buscarPedidosExamePeloAtendimento(Integer seqAtendimento) {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(AelPedidoExame.class);
		criteria.add(Restrictions.eq(AelPedidoExame.Fields.ATENDIMENTO_SEQ
				.toString(), seqAtendimento));

		return executeCriteria(criteria);
	}

	public List<AelPedidoExame> pesquisarAelPedidoExamePorZonaESala(Short unfSeq, 
			Byte sala) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPedidoExame.class);
		criteria.add(Restrictions.eq(AelPedidoExame.Fields.USL_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AelPedidoExame.Fields.USL_SALA.toString(), sala));
		
		return executeCriteria(criteria);
	}		

}
