package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmSumarioAlta;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaVO;

public class MpmSumarioAltaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmSumarioAlta> {

	private static final long serialVersionUID = -3919211396478261552L;

	/**
	 * Obtém um lista de sumários de alta pelo seu atd_seq (atendimento).
	 * 
	 * @param {Integer} atdSeq
	 * 
	 * @return List<MpmSumarioAlta>
	 */
	public MpmSumarioAlta obterSumarioAltaSemMotivoAltaPeloAtendimento(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmSumarioAlta.class);
		criteria.add(Restrictions.eq(
				MpmSumarioAlta.Fields.SEQ.toString(), atdSeq));		
		criteria.add(Restrictions.isNull(
				MpmSumarioAlta.Fields.MOTIVO_ALTA_MEDICA.toString()));

		return (MpmSumarioAlta)this.executeCriteriaUniqueResult(criteria);
	}
	
	public SumarioAltaVO obtemSumarioAltaVO(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSumarioAlta.class);

		criteria
		.setProjection(Projections
						.projectionList()
						.add(Projections.property(MpmSumarioAlta.Fields.SEQ.toString()),SumarioAltaVO.Fields.SEQ.toString())
						.add(Projections.property(MpmSumarioAlta.Fields.MOTIVO_ALTA_MEDICA_SEQ.toString()),SumarioAltaVO.Fields.MAM_SEQ.toString())
						.add(Projections.property(MpmSumarioAlta.Fields.PLA_SEQ.toString()),SumarioAltaVO.Fields.PLA_SEQ.toString())
						.add(Projections.property(MpmSumarioAlta.Fields.DATA_ALTA.toString()),SumarioAltaVO.Fields.DATA_ALTA.toString())
						.add(Projections.property(MpmSumarioAlta.Fields.DATA_ELB_ALTA.toString()),SumarioAltaVO.Fields.DATA_ELB_ALTA.toString())
						.add(Projections.property(MpmSumarioAlta.Fields.IND_NECROPSIA.toString()),SumarioAltaVO.Fields.IND_NECROPSIA.toString())
						.add(Projections.property(MpmSumarioAlta.Fields.MATRICULA_SERVIDOR_VLD.toString()),SumarioAltaVO.Fields.MATRICULA_SER_VALIDA.toString())
						.add(Projections.property(MpmSumarioAlta.Fields.CODIGO_SERVIDOR_VLD.toString()),SumarioAltaVO.Fields.CODIGO_SER_VALIDA.toString())
						.add(Projections.property(MpmSumarioAlta.Fields.COMPL_PLN_POS_ALTA.toString()),SumarioAltaVO.Fields.COMPL_PLN_POS_ALTA.toString())
						.add(Projections.property(MpmSumarioAlta.Fields.COMPL_MTV_ALTA.toString()),SumarioAltaVO.Fields.COMPL_MTV_ALTA.toString())
						.add(Projections.property(MpmSumarioAlta.Fields.ESTD_PAC_ALTA.toString()),SumarioAltaVO.Fields.ESTD_PAC_ALTA.toString())
		);

		criteria.add(Restrictions.eq(MpmSumarioAlta.Fields.SEQ.toString(), atdSeq));

		criteria.setResultTransformer(Transformers.aliasToBean(SumarioAltaVO.class));
		
		return (SumarioAltaVO)executeCriteriaUniqueResult(criteria);
	}

	public Date pesquisarDataAltaInternacao(Integer internacaoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSumarioAlta.class);
		criteria.createAlias(MpmSumarioAlta.Fields.ATENDIMENTO.toString(), MpmSumarioAlta.Fields.ATENDIMENTO.toString());
		criteria.add(Restrictions.eq(MpmSumarioAlta.Fields.ATENDIMENTO.toString() + "." + AghAtendimentos.Fields.INT_SEQ.toString(),
				internacaoSeq));
		criteria.setProjection(Projections.property(MpmSumarioAlta.Fields.DATA_ALTA.toString()));
		List<Date> dates = executeCriteria(criteria, 0, 1, null, true);
		if (dates != null && !dates.isEmpty()) {
			return dates.get(0);
		}
		return null;
	}
	
	public Date obterDataAltaInternacaoComMotivoAlta(Integer intSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSumarioAlta.class);
		criteria.setProjection(Projections.property(MpmSumarioAlta.Fields.DATA_ALTA.toString()));
		criteria.createAlias(MpmSumarioAlta.Fields.ATENDIMENTO.toString(), "ATD");

		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.INT_SEQ.toString(), intSeq));
		criteria.add(Restrictions.isNotNull(MpmSumarioAlta.Fields.MOTIVO_ALTA_MEDICA.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	public Long obterQuantidadeSumarioAltaComMotivoAlta(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSumarioAlta.class);

		criteria.add(Restrictions.eq(MpmSumarioAlta.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNotNull(MpmSumarioAlta.Fields.MOTIVO_ALTA_MEDICA.toString()));
		return executeCriteriaCount(criteria);
	}
}
