package br.gov.mec.aghu.patrimonio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.PtmSituacaoMotivoMovimento;

public class PtmSituacaoMotivoMovimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PtmSituacaoMotivoMovimento>{

	private static final long serialVersionUID = 3269302140027094400L;
	
	public List<PtmSituacaoMotivoMovimento> obterTodasSituacoesMotivoMovimento(){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmSituacaoMotivoMovimento.class);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(PtmSituacaoMotivoMovimento.Fields.SEQ.toString()).as(PtmSituacaoMotivoMovimento.Fields.SEQ.toString()))
				.add(Projections.property(PtmSituacaoMotivoMovimento.Fields.SITUACAO.toString()).as(PtmSituacaoMotivoMovimento.Fields.SITUACAO.toString()))
		);
		
		criteria.addOrder(Order.asc(PtmSituacaoMotivoMovimento.Fields.SITUACAO.toString()));			
		criteria.setResultTransformer(Transformers.aliasToBean(PtmSituacaoMotivoMovimento.class));
		
		return executeCriteria(criteria);
	}
}
