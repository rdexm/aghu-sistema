package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoMedicamentoId;

public class MptPrescricaoMedicamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptPrescricaoMedicamento> {



	private static final long serialVersionUID = -7371143722918405476L;


	@Override
	protected void obterValorSequencialId(MptPrescricaoMedicamento elemento) {
		if (elemento.getId() == null) {
			elemento.setId(new MptPrescricaoMedicamentoId());
		}
		elemento.getId().setSeq(this.getNextVal(SequenceID.MPT_PMO_SQ1).intValue());
	}

	public List<MptPrescricaoMedicamento> listarPrescricoesMedicamento(Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda, Boolean solucao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoMedicamento.class);
		
		criteria.add(Restrictions.eq(MptPrescricaoMedicamento.Fields.PTE_ATD_SEQ.toString(), pteAtdSeq));
		criteria.add(Restrictions.eq(MptPrescricaoMedicamento.Fields.PTE_SEQ.toString(), pteSeq));
				
		criteria.add(Restrictions.or(
				Restrictions.isNull(MptPrescricaoMedicamento.Fields.ALTERADO_EM.toString()), 
				Restrictions.eq(MptPrescricaoMedicamento.Fields.ALTERADO_EM.toString(), agpDtAgenda)));
		
		DominioSituacaoItemPrescricaoMedicamento[] dominioSituacaoItem = {DominioSituacaoItemPrescricaoMedicamento.V, DominioSituacaoItemPrescricaoMedicamento.A, DominioSituacaoItemPrescricaoMedicamento.E};
		criteria.add(Restrictions.in(MptPrescricaoMedicamento.Fields.SITUACAO_ITEM.toString(), dominioSituacaoItem));
		
		criteria.add(Restrictions.eq(MptPrescricaoMedicamento.Fields.SOLUCAO.toString(), solucao));
		criteria.add(Restrictions.isNotNull(MptPrescricaoMedicamento.Fields.SERVIDOR_INC_VALIDA.toString()));
		
		criteria.addOrder(Order.asc(MptPrescricaoMedicamento.Fields.PRESCRICAO_MEDICAMENTO.toString()));
						
		return executeCriteria(criteria);
	}
	
	public MptPrescricaoMedicamento obterMptPrescricaoMedicamentoComJoin(Integer pteAtdSeq, Integer pteSeq, Integer pmoSeq, Boolean solucao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoMedicamento.class, "pmo");		
		
		criteria.createAlias(MptPrescricaoMedicamento.Fields.TIPO_FREQ_APRAZAMENTO.toString(), "tipoFreqAprazamento");
		
		criteria.createAlias(MptPrescricaoMedicamento.Fields.VIA_ADMINISTRACAO.toString(), "viaAdministracao");	
						
		// where tva.seq(+) = pmo.tva_seq
		criteria.createAlias(MptPrescricaoMedicamento.Fields.TIPO_VELOCIDADE_ADMINISTRACAO.toString(), "tipoVelocidadeAdministracao", Criteria.LEFT_JOIN);
				
		criteria.add(Restrictions.eq(MptPrescricaoMedicamento.Fields.PTE_ATD_SEQ.toString(), pteAtdSeq));
		criteria.add(Restrictions.eq(MptPrescricaoMedicamento.Fields.PTE_SEQ.toString(), pteSeq));
		criteria.add(Restrictions.eq(MptPrescricaoMedicamento.Fields.SEQ.toString(), pmoSeq));			
		criteria.add(Restrictions.eq(MptPrescricaoMedicamento.Fields.SOLUCAO.toString(), solucao));
		
		return (MptPrescricaoMedicamento) executeCriteriaUniqueResult(criteria); 
	}


	public List<MptPrescricaoMedicamento> listarPrescricaoMedicamentoHierarquia(Integer pteAtdSeq, Integer pteSeq, Integer seq, Date agpDtAgenda, Boolean solucao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoMedicamento.class);
		
		criteria.add(Restrictions.eq(MptPrescricaoMedicamento.Fields.PRESCRICAO_MEDICAMENTO_ORIGINAL_PTE_ATD_SEQ.toString(), pteAtdSeq));
		criteria.add(Restrictions.eq(MptPrescricaoMedicamento.Fields.PRESCRICAO_MEDICAMENTO_ORIGINAL_PTE_SEQ.toString(), pteSeq));
		criteria.add(Restrictions.eq(MptPrescricaoMedicamento.Fields.PRESCRICAO_MEDICAMENTO_ORIGINAL_SEQ.toString(), seq));
				
		criteria.add(Restrictions.or(
				Restrictions.isNull(MptPrescricaoMedicamento.Fields.ALTERADO_EM.toString()), 
				Restrictions.eq(MptPrescricaoMedicamento.Fields.ALTERADO_EM.toString(), agpDtAgenda)));
		
		DominioSituacaoItemPrescricaoMedicamento[] dominioSituacaoItem = {DominioSituacaoItemPrescricaoMedicamento.V, DominioSituacaoItemPrescricaoMedicamento.A, DominioSituacaoItemPrescricaoMedicamento.E};
		criteria.add(Restrictions.in(MptPrescricaoMedicamento.Fields.SITUACAO_ITEM.toString(), dominioSituacaoItem));
		
		criteria.add(Restrictions.eq(MptPrescricaoMedicamento.Fields.SOLUCAO.toString(), solucao));
		criteria.add(Restrictions.isNotNull(MptPrescricaoMedicamento.Fields.SERVIDOR_INC_VALIDA.toString()));
		
		criteria.addOrder(Order.asc(MptPrescricaoMedicamento.Fields.PRESCRICAO_MEDICAMENTO.toString()));		
		
		return executeCriteria(criteria);
	}
}
