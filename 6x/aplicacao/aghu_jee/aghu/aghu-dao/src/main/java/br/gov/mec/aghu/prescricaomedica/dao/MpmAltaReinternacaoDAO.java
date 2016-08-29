package br.gov.mec.aghu.prescricaomedica.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaReinternacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MpmAltaReinternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaReinternacao> {

	private static final long serialVersionUID = -3452704552885510707L;


	@Override
	protected void obterValorSequencialId(MpmAltaReinternacao elemento) {

		if (elemento.getAltaSumario() == null) {

			throw new IllegalArgumentException("MpmAltaReinternacao nao esta associado corretamente a MpmAltaSumario.");

		}

		elemento.setId(elemento.getAltaSumario().getId());
	}

	/**
	 * Retorna MpmAltaReinternacao do sumário ativo.
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmAltaReinternacao obterMpmAltaReinternacao(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaReinternacao.class);
		criteria.add(Restrictions.eq(MpmAltaReinternacao.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaReinternacao.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaReinternacao.Fields.ASU_SEQP.toString(), altanAsuSeqp));
				
		return (MpmAltaReinternacao) executeCriteriaUniqueResult(criteria);
	}
	
	
	public String obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaReinternacao.class);
		
		criteria.add(Restrictions.eq(MpmAltaReinternacao.Fields.ASU_APA_ATD_SEQ.toString(), asuApaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaReinternacao.Fields.ASU_APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmAltaReinternacao.Fields.ASU_SEQP.toString(), asuSeqp));

		MpmAltaReinternacao ar = (MpmAltaReinternacao) executeCriteriaUniqueResult(criteria);
		if(ar==null){
			return null;
		}
		
		String observacao = "";
		
		if (ar.getObservacao() != null){
			observacao = ar.getObservacao();
		}
		
		StringBuffer result = new StringBuffer("Reinternação programada para o dia ").append(DateUtil.obterDataFormatada(ar.getData(), "dd/MM/yyyy"));
		result.append(", ").append(ar.getDescEspecialidade()).append(", pelo motivo ");
		if (ar.getMotivoReinternacao().getIndOutros()){
			result.append(observacao).append('.');
		}else{
			result.append(ar.getMotivoReinternacao().getDescricao()).append(' ').append(observacao).append('.');
		}	
		return result.toString();		
	}		
	
}
