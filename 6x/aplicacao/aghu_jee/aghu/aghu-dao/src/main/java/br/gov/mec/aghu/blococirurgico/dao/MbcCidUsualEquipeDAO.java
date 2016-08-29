package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcCidUsualEquipe;
import br.gov.mec.aghu.model.MbcCidUsualEquipeId;

public class MbcCidUsualEquipeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcCidUsualEquipe> {

	
	@Override
	protected void obterValorSequencialId(MbcCidUsualEquipe elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		MbcCidUsualEquipeId id = new MbcCidUsualEquipeId();
		id.setCidSeq(elemento.getAghCid().getSeq());
		id.setEqpSeq(elemento.getAghEquipes().getSeq().shortValue());
		elemento.setId(id);
	}
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7505775797012235190L;

	public List<MbcCidUsualEquipe> pesquisarCidUsualEquipe(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, Short eqpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				MbcCidUsualEquipe.class);
		
		this.montarCriterioParaConsulta(criteria, eqpSeq);		
	
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	
	public Long pesquisarCidUsualEquipeCount(Short eqpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
					MbcCidUsualEquipe.class);

		this.montarCriterioParaConsulta(criteria, eqpSeq);
			
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriterioParaConsulta(DetachedCriteria criteria, Short eqpSeq) {
		// Where
		
		criteria.createAlias(MbcCidUsualEquipe.Fields.AGH_CID.toString(), "CID");
		criteria.createAlias(MbcCidUsualEquipe.Fields.RAP_SERVIDORES.toString(), "RAP");
		
		criteria.add(Restrictions.eq(
				MbcCidUsualEquipe.Fields.EQP_SEQ.toString(), eqpSeq));
		return criteria;		
	}
	
}
