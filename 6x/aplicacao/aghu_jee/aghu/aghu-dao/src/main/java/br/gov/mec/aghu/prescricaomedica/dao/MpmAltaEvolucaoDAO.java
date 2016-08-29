package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaEvolucao;
import br.gov.mec.aghu.model.MpmAltaSumarioId;

public class MpmAltaEvolucaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaEvolucao> {

	private static final long serialVersionUID = -5262518688650832061L;

	@Override
	protected void obterValorSequencialId(MpmAltaEvolucao elemento) {
		if (elemento.getAltaSumario() == null) {
			throw new IllegalArgumentException("MpmAltaEvolucao nao esta associado corretamente a MpmAltaSumario.");
		}
		elemento.setId(elemento.getAltaSumario().getId());
	}
	
	/**
	 * Busca MpmAltaEvolucao do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmAltaEvolucao obterMpmAltaEvolucao(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaEvolucao.class);
		criteria.add(Restrictions.eq(MpmAltaEvolucao.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaEvolucao.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaEvolucao.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		return (MpmAltaEvolucao) executeCriteriaUniqueResult(criteria);
		
	}
	
	public String obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaEvolucao.class);
		
		criteria.setProjection(Projections.property(MpmAltaEvolucao.Fields.DESCRICAO.toString()));
				
		criteria.add(Restrictions.eq(MpmAltaEvolucao.Fields.ASU_APA_ATD_SEQ.toString(), asuApaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaEvolucao.Fields.ASU_APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmAltaEvolucao.Fields.ASU_SEQP.toString(), asuSeqp));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Método que valida a verificação
	 * da evolução da alta do paciente. Deve
	 * ter pelo menos um registro associado ao
	 * sumário do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 */
	public List<Long> listAltaEvolucao(MpmAltaSumarioId altaSumarioId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaEvolucao.class);
		
		criteria.setProjection(Projections.rowCount())
		.add(Restrictions.idEq(altaSumarioId));
		
		return executeCriteria(criteria);
	}

}
