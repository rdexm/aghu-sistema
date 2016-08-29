package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaPedidoExame;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MpmAltaPedidoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaPedidoExame> {

	private static final long serialVersionUID = -405051390315886258L;

	@Override
	protected void obterValorSequencialId(MpmAltaPedidoExame elemento) {
		
		if (elemento.getAltaSumario() == null) {

			throw new IllegalArgumentException("MpmAltaPedidoExame nao esta associado corretamente a MpmAltaSumario.");

		}

		elemento.setId(elemento.getAltaSumario().getId());
		
	}
	
	/**
	 * Busca alta pedido exame do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmAltaPedidoExame obterMpmAltaPedidoExame(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPedidoExame.class);
		criteria.add(Restrictions.eq(MpmAltaPedidoExame.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaPedidoExame.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaPedidoExame.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		return (MpmAltaPedidoExame) executeCriteriaUniqueResult(criteria);
		
	}
	
	
	public String obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPedidoExame.class);
		
		criteria.add(Restrictions.eq(MpmAltaPedidoExame.Fields.ASU_APA_ATD_SEQ.toString(), asuApaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaPedidoExame.Fields.ASU_APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmAltaPedidoExame.Fields.ASU_SEQP.toString(), asuSeqp));

		MpmAltaPedidoExame ape = (MpmAltaPedidoExame) executeCriteriaUniqueResult(criteria);
		if(ape==null){
			return null;
		}
		StringBuffer result = new StringBuffer("Encaminhado para consulta no ambulatório de ").append(ape.getDescEspecialidade());
		if (ape.getIndAgenda() != null && ape.getIndAgenda()){
			result.append(" conforme agenda.");
		}else{
			result.append(" dia ").append(DateUtil.obterDataFormatada(ape.getDthrConsulta(), "dd/MM/yyyy"));
			result.append(" às ").append(DateUtil.obterDataFormatada(ape.getDthrConsulta(), "HH:mm")).append(" horas.");
		}

		return result.toString();		
	}
	
	public List<MpmAltaPedidoExame> pesquisarMpmAltaPedidoExamePorZonaESala(Short unfSeq, 
			Byte sala) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPedidoExame.class);
		criteria.add(Restrictions.eq(MpmAltaPedidoExame.Fields.USL_UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(MpmAltaPedidoExame.Fields.USL_SALA.toString(), sala));
		
		return executeCriteria(criteria);
	}
	
}
