package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;

public class MpmItemModeloBasicoMedicamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmItemModeloBasicoMedicamento> {

	private static final long serialVersionUID = 6568855390140611823L;

	@Override
	protected void obterValorSequencialId(MpmItemModeloBasicoMedicamento elemento) {
		if (elemento == null || elemento.getModeloBasicoMedicamento() == null || elemento.getMedicamento() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!");
		}
		if (elemento == null || elemento.getModeloBasicoMedicamento() == null || elemento.getMedicamento() == null
				|| elemento.getModeloBasicoMedicamento().getId() == null 
				|| elemento.getModeloBasicoMedicamento().getId().getModeloBasicoPrescricaoSeq() == null 
				|| elemento.getModeloBasicoMedicamento().getId().getSeq() == null
				|| elemento.getMedicamento().getMatCodigo() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		// Nao tem sequence para esta tabela
		MpmItemModeloBasicoMedicamentoId id = new MpmItemModeloBasicoMedicamentoId();
		id.setModeloBasicoPrescricaoSeq(elemento.getModeloBasicoMedicamento().getId().getModeloBasicoPrescricaoSeq());
		id.setModeloBasicoMedicamentoSeq(elemento.getModeloBasicoMedicamento().getId().getSeq());
		id.setMedicamentoMaterialCodigo(elemento.getMedicamento().getMatCodigo());
		
		Integer seqItem = this.buscaMaxSeqItemModeloBasicoMedicamento(elemento.getModeloBasicoMedicamento(), elemento.getMedicamento());
		seqItem = seqItem + 1;
		id.setSeqp(seqItem);
		
		elemento.setId(id);
	}
	
	private Integer buscaMaxSeqItemModeloBasicoMedicamento(MpmModeloBasicoMedicamento modeloBasicoMedicamento, AfaMedicamento medicamento) {
		if (modeloBasicoMedicamento == null || modeloBasicoMedicamento.getId() == null 
				|| modeloBasicoMedicamento.getId().getModeloBasicoPrescricaoSeq() == null 
				|| modeloBasicoMedicamento.getId().getSeq() == null
				|| medicamento == null
				|| medicamento.getMatCodigo() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		Integer returnValue = null;

		StringBuilder sql = new StringBuilder(200);
		sql.append("select max(o.id.seqp) as maxseq ");
		sql.append("from ").append(MpmItemModeloBasicoMedicamento.class.getName()).append(" o ");
		sql.append("where o.").append(MpmItemModeloBasicoMedicamento.Fields.MBM_MDB_SEQ.toString()).append(" = :modeloBasicoPrescricaoSeq ");
		sql.append("and o.").append(MpmItemModeloBasicoMedicamento.Fields.MBM_SEQ.toString()).append(" = :modeloBasicoMedicamentoSeq ");
		sql.append("and o.").append(MpmItemModeloBasicoMedicamento.Fields.MED_MAT_CODIGO.toString()).append(" = :medicamentoMaterialCodigo ");
		Query query = createQuery(sql.toString());
		query.setParameter("modeloBasicoPrescricaoSeq", modeloBasicoMedicamento.getId().getModeloBasicoPrescricaoSeq());
		query.setParameter("modeloBasicoMedicamentoSeq", modeloBasicoMedicamento.getId().getSeq());			
		query.setParameter("medicamentoMaterialCodigo", medicamento.getMatCodigo());			
			
		Object maxSeq = query.getSingleResult();
		if (maxSeq == null) {
			returnValue = Integer.valueOf(0);
		} else {
			returnValue = (Integer) maxSeq; 
		}
		
		return returnValue;
	}

	private DetachedCriteria montarCriteriaItensMedicamento(Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoMedicamentoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemModeloBasicoMedicamento.class);
		criteria.add(Restrictions.eq(MpmItemModeloBasicoMedicamento.Fields.MBM_MDB_SEQ.toString(), modeloBasicoPrescricaoSeq));
		criteria.add(Restrictions.eq(MpmItemModeloBasicoMedicamento.Fields.MBM_SEQ.toString(), modeloBasicoMedicamentoSeq));
		return criteria;
	}
	
	/**
	 * Retorna a quantidade de itens do medicamento
	 * @param modeloBasicoPrescricaoSeq
	 * @param modeloBasicoMedicamentoSeq
	 * @return
	 */
	public Long obterQuantidadeItensMedicamento(
			Integer modeloBasicoPrescricaoSeq,
			Integer modeloBasicoMedicamentoSeq){
		
		DetachedCriteria criteria = montarCriteriaItensMedicamento(modeloBasicoPrescricaoSeq, modeloBasicoMedicamentoSeq);
		return executeCriteriaCount(criteria);
	}
	
	public List<MpmItemModeloBasicoMedicamento> obterItensMedicamento(Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoMedicamentoSeq) {
		DetachedCriteria criteria = montarCriteriaItensMedicamento(modeloBasicoPrescricaoSeq, modeloBasicoMedicamentoSeq);
		return executeCriteria(criteria);
	}
}
