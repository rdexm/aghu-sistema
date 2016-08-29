package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.McoProcReanimacao;
import br.gov.mec.aghu.model.McoReanimacaoRns;
import br.gov.mec.aghu.perinatologia.vo.MedicamentoRecemNascidoVO;

public class McoReanimacaoRnsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoReanimacaoRns> {

	private static final long serialVersionUID = 783176808012764781L;

	public List<McoReanimacaoRns> listarReanimacoesRns(Integer pacCodigo, Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoReanimacaoRns.class);
		
		criteria.add(Restrictions.eq(McoReanimacaoRns.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoReanimacaoRns.Fields.SEQUENCE.toString(), seq));

		return executeCriteria(criteria);
	}

	public List<McoReanimacaoRns> listarReanimacoesRnsPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoReanimacaoRns.class);
		
		criteria.add(Restrictions.eq(McoReanimacaoRns.Fields.CODIGO_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public Long quantidadeReanimacoesRns(Integer pacCodigo, Short seq, Byte rnaSeqp) {
		DetachedCriteria criteria = criarCriteriaMcoReanimacaoRns(pacCodigo,seq, rnaSeqp);
		return executeCriteriaCount(criteria);
	}



	private DetachedCriteria criarCriteriaMcoReanimacaoRns(Integer pacCodigo,
			Short seq, Byte rnaSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoReanimacaoRns.class, "MRN");
		adicionarFiltrosMcoReanimacaoRns(pacCodigo, seq, rnaSeqp, criteria);
		return criteria;
	}
	
	/**
	 * #41973 - C5 
	 */
	public boolean isReanimacaoCadastradoBD(Integer rnaGsoPacCodigo, Short rnaGsoSeqp, Byte rnaSeqp, Integer pniSeq) {
		DetachedCriteria criteria = criarCriteriaMcoReanimacaoRns(rnaGsoPacCodigo, rnaGsoSeqp, rnaSeqp);
		criteria.add(Restrictions.eq("MRN." + McoReanimacaoRns.Fields.PNI_SEQ, pniSeq));
		return executeCriteriaExists(criteria);
	}

	/**
	 * #41973 - C6 
	 */
	public List<MedicamentoRecemNascidoVO> buscarMedicamentosPorRecemNascido(Integer rnaGsoPacCodigo, Short rnaGsoSeqp, Byte rnaSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoReanimacaoRns.class, "MRN");
		criteria.createAlias(McoReanimacaoRns.Fields.PNI.toString(), "PNI");
		adicionarFiltrosMcoReanimacaoRns(rnaGsoPacCodigo, rnaGsoSeqp, rnaSeqp,criteria);
		
    	criteria.setProjection(Projections.projectionList()
    			.add(Projections.property("MRN." + McoReanimacaoRns.Fields.PNI_SEQ) ,MedicamentoRecemNascidoVO.Fields.SEQ_PNI.toString())
    			.add(Projections.property("PNI." + McoProcReanimacao.Fields.DESCRICAO) ,MedicamentoRecemNascidoVO.Fields.DESCRICAO_PNI.toString())
    			.add(Projections.property("MRN." + McoReanimacaoRns.Fields.DOSE) ,MedicamentoRecemNascidoVO.Fields.DOSE.toString())
    			.add(Projections.property("MRN." + McoReanimacaoRns.Fields.UNIDADE) ,MedicamentoRecemNascidoVO.Fields.UNIDADE.toString())
    			.add(Projections.property("MRN." + McoReanimacaoRns.Fields.VAD_SIGLA) ,MedicamentoRecemNascidoVO.Fields.VAD_SIGLA.toString())
    			);
    	criteria.addOrder(Order.asc("MRN." + McoReanimacaoRns.Fields.PNI_SEQ.toString()));
    	criteria.setResultTransformer(Transformers.aliasToBean(MedicamentoRecemNascidoVO.class));
    	return executeCriteria(criteria);
	}


	private void adicionarFiltrosMcoReanimacaoRns(Integer rnaGsoPacCodigo,
			Short rnaGsoSeqp, Byte rnaSeqp, DetachedCriteria criteria) {
		criteria.add(Restrictions.eq("MRN." + McoReanimacaoRns.Fields.CODIGO_PACIENTE, rnaGsoPacCodigo));
		criteria.add(Restrictions.eq("MRN." + McoReanimacaoRns.Fields.SEQUENCE, rnaGsoSeqp));
		criteria.add(Restrictions.eq("MRN." + McoReanimacaoRns.Fields.RNA_SEQP, rnaSeqp));
	}
	
}
