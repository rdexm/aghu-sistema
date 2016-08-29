package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelSinonimoCampoLaudo;
import br.gov.mec.aghu.model.AelSinonimoCampoLaudoId;

public class AelSinonimoCampoLaudoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSinonimoCampoLaudo>{
	
	private static final long serialVersionUID = 6720342216577958236L;

	@Override
	protected void obterValorSequencialId(AelSinonimoCampoLaudo elemento) {
		
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio não informado!!!");
		}
		if (elemento.getCampoLaudo() == null || elemento.getCampoLaudo().getSeq() == null) {
			throw new IllegalArgumentException("Associacao com AelCampoLaudo não está corretamente informada!!!");
		}

		final short seqp = this.obterSeqpPorAelCampoLaudo(elemento.getCampoLaudo());

		AelSinonimoCampoLaudoId id = new AelSinonimoCampoLaudoId();
		id.setCalSeq(elemento.getCampoLaudo().getSeq());
		id.setSeqp(seqp);
		
		elemento.setId(id);
	}
	
	/**
	 * Obtém o seqp através de AelCampoLaudo
	 * @param campoLaudo
	 * @return
	 */
	private Short obterSeqpPorAelCampoLaudo(AelCampoLaudo campoLaudo) {
		
		if (campoLaudo == null || campoLaudo.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio não informado!!");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSinonimoCampoLaudo.class);
		criteria.setProjection(Projections.max(AelSinonimoCampoLaudo.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(AelSinonimoCampoLaudo.Fields.CAL_SEQ.toString(), campoLaudo.getSeq()));
		
		Short seqp = 0;
		Object maxSeqp = this.executeCriteriaUniqueResult(criteria);
		if (maxSeqp != null) {
			seqp = (Short) maxSeqp;
		}
		
		return ++seqp;
	}
	
	/**
	 * Pesquisa AelSinonimoCampoLaudo Laudo por AelCampoLaudo
	 * @param campoLaudo
	 * @return
	 */
	public List<AelSinonimoCampoLaudo> pesquisarSinonimoCampoLaudoPorCampoLaudo(AelCampoLaudo campoLaudo){
		
		if (campoLaudo == null || campoLaudo.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio não informado!");
		}
		
		return this.pesquisarSinonimoCampoLaudoPorSeqCampoLaudo(campoLaudo.getSeq());
	}
	
	/**
	 * Pesquisa AelSinonimoCampoLaudo por seq/id de AelCampoLaudo
	 * @param seqCampoLaudo
	 * @return
	 */
	public List<AelSinonimoCampoLaudo> pesquisarSinonimoCampoLaudoPorSeqCampoLaudo(Integer seqCampoLaudo){

		if (seqCampoLaudo == null) {
			throw new IllegalArgumentException("Parametro obrigatorio não informado!");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSinonimoCampoLaudo.class);
		criteria.add(Restrictions.eq(AelSinonimoCampoLaudo.Fields.CAL_SEQ.toString(), seqCampoLaudo));
		
		criteria.addOrder(Order.asc(AelSinonimoCampoLaudo.Fields.CAL_SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	
	
}
