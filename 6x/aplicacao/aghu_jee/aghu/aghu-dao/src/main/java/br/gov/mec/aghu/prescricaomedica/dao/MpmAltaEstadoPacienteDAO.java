package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MpmAltaEstadoPaciente;
import br.gov.mec.aghu.model.MpmAltaSumarioId;

/**
 * 
 * @author bsoliveira
 *
 */
public class MpmAltaEstadoPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaEstadoPaciente> {

	private static final long serialVersionUID = -4524389056581454075L;

	@Override
	protected void obterValorSequencialId(MpmAltaEstadoPaciente elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro invalido!!!");
		}
		if (elemento.getAltaSumario() == null) {
			throw new IllegalArgumentException("MpmAltaEvolucao nao esta associado corretamente a MpmAltaSumario.");
		}

		elemento.setId(elemento.getAltaSumario().getId());

	}

	/**
	 * Busca MpmAltaEstadoPaciente do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmAltaEstadoPaciente obterMpmAltaEstadoPaciente(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaEstadoPaciente.class, "AEP");
		criteria.createAlias("AEP." + MpmAltaEstadoPaciente.Fields.ESTADO_PACIENTE.toString(), "EST", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("AEP." + MpmAltaEstadoPaciente.Fields.APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq("AEP." + MpmAltaEstadoPaciente.Fields.APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq("AEP." + MpmAltaEstadoPaciente.Fields.SEQP.toString(), altanAsuSeqp));
		return (MpmAltaEstadoPaciente) executeCriteriaUniqueResult(criteria);
		
	}

	
	public String obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaEstadoPaciente.class);
		
		criteria.add(Restrictions.eq(MpmAltaEstadoPaciente.Fields.APA_ATD_SEQ.toString(), asuApaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaEstadoPaciente.Fields.APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmAltaEstadoPaciente.Fields.SEQP.toString(), asuSeqp));

		MpmAltaEstadoPaciente aep = (MpmAltaEstadoPaciente) executeCriteriaUniqueResult(criteria);
		if(aep==null){
			return null;
		}		

		return this.obterDescricaoFormatadaCodigoSUS(aep);
	}
	
	public String obterDescricaoFormatadaCodigoSUS(MpmAltaEstadoPaciente aep){
		return aep.getEstadoPaciente() != null ? aep.getEstadoPaciente().getDescricaoEditada()+" ("+aep.getEstadoPaciente().getMotivoSaidaPaciente().getCodigoSus()+aep.getEstadoPaciente().getCodigoSus()+")" : (aep.getDominioEstadoPaciente() != null ? aep.getDominioEstadoPaciente().getDescricao() : null);
	}
	
	/**
	 * Método que verifica a validação
	 * do estado clínico do paciente. Deve
	 * pelo menos ter um registro associado 
	 * ao sumário do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 */
	public List<Long> listAltaEstadoPaciente(MpmAltaSumarioId altaSumarioId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaEstadoPaciente.class);
		
		criteria.setProjection(Projections.rowCount())
		.add(Restrictions.idEq(altaSumarioId));
		
		return executeCriteria(criteria);
	}
	
}
