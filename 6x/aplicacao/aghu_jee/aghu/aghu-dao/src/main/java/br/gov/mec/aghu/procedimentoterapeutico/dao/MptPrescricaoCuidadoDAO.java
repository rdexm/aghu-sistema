package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptPrescricaoCuidado;
import br.gov.mec.aghu.model.MptPrescricaoCuidadoId;

public class MptPrescricaoCuidadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptPrescricaoCuidado> {



	private static final long serialVersionUID = -6390967559183062388L;


	@Override
	protected void obterValorSequencialId(MptPrescricaoCuidado elemento) {
		if (elemento.getId() == null) {
			elemento.setId(new MptPrescricaoCuidadoId());
		}
		elemento.getId().setSeq(this.getNextVal(SequenceID.MPT_PCO_SQ1).intValue());
	}

	public List<MptPrescricaoCuidado> pesquisarPrescricoesCuidado(Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoCuidado.class);
		
		criteria.add(Restrictions.eq(MptPrescricaoCuidado.Fields.PTE_ATD_SEQ.toString(), pteAtdSeq));
		criteria.add(Restrictions.eq(MptPrescricaoCuidado.Fields.PTE_SEQ.toString(), pteSeq));
				
		criteria.add(Restrictions.or(
				Restrictions.isNull(MptPrescricaoCuidado.Fields.ALTERADO_EM.toString()), 
				Restrictions.eq(MptPrescricaoCuidado.Fields.ALTERADO_EM.toString(), agpDtAgenda)));
		
		DominioSituacaoItemPrescricaoMedicamento[] dominioSituacaoItem = {DominioSituacaoItemPrescricaoMedicamento.V, DominioSituacaoItemPrescricaoMedicamento.A, DominioSituacaoItemPrescricaoMedicamento.E};
		criteria.add(Restrictions.in(MptPrescricaoCuidado.Fields.SITUACAO_ITEM.toString(), dominioSituacaoItem));
		
		criteria.add(Restrictions.isNotNull(MptPrescricaoCuidado.Fields.SERVIDOR_INC_VALIDA.toString()));
		
		criteria.addOrder(Order.asc(MptPrescricaoCuidado.Fields.PRESCRICAO_CUIDADO.toString()));		
				
		return executeCriteria(criteria);
	}


	public MptPrescricaoCuidado obterMptPrescricaoCuidadoComJoin(Integer pteAtdSeq, Integer pteSeq, Integer pcoSeq, Boolean join1, Boolean join2) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoCuidado.class);
		
		if(join1){
			criteria.createAlias(MptPrescricaoCuidado.Fields.CDU.toString(), "cuidadoUsual");
		}
		if(join2){
			criteria.createAlias(MptPrescricaoCuidado.Fields.TIPO_FREQ_APRAZAMENTO.toString(), "tipoFreqAprazamento");
		}
		criteria.add(Restrictions.eq(MptPrescricaoCuidado.Fields.PTE_ATD_SEQ.toString(), pteAtdSeq));
		criteria.add(Restrictions.eq(MptPrescricaoCuidado.Fields.PTE_SEQ.toString(), pteSeq));
		criteria.add(Restrictions.eq(MptPrescricaoCuidado.Fields.SEQ.toString(), pcoSeq));		
		
		return (MptPrescricaoCuidado) executeCriteriaUniqueResult(criteria);
	}


	public List<MptPrescricaoCuidado> listarPrescricaoCuidadoHierarquia(Integer pteAtdSeq, Integer pteSeq, Integer seq, Date agpDtAgenda) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptPrescricaoCuidado.class);

		criteria.add(Restrictions.eq(MptPrescricaoCuidado.Fields.PRESCRICAO_CUIDADO_ORIGINAL_PTE_ATD_SEQ.toString(), pteAtdSeq));
		criteria.add(Restrictions.eq(MptPrescricaoCuidado.Fields.PRESCRICAO_CUIDADO_ORIGINAL_PTE_SEQ.toString(), pteSeq));
		criteria.add(Restrictions.eq(MptPrescricaoCuidado.Fields.PRESCRICAO_CUIDADO_ORIGINAL_SEQ.toString(), seq));

		criteria.add(Restrictions.or(
				Restrictions.isNull(MptPrescricaoCuidado.Fields.ALTERADO_EM.toString()), 
				Restrictions.eq(MptPrescricaoCuidado.Fields.ALTERADO_EM.toString(), agpDtAgenda)));

		DominioSituacaoItemPrescricaoMedicamento[] dominioSituacaoItem = {DominioSituacaoItemPrescricaoMedicamento.V, DominioSituacaoItemPrescricaoMedicamento.A, DominioSituacaoItemPrescricaoMedicamento.E};
		criteria.add(Restrictions.in(MptPrescricaoCuidado.Fields.SITUACAO_ITEM.toString(), dominioSituacaoItem));

		criteria.add(Restrictions.isNotNull(MptPrescricaoCuidado.Fields.SERVIDOR_INC_VALIDA.toString()));

		criteria.addOrder(Order.asc(MptPrescricaoCuidado.Fields.PRESCRICAO_CUIDADO.toString()));
		
		return executeCriteria(criteria);
	}
	
	
}
