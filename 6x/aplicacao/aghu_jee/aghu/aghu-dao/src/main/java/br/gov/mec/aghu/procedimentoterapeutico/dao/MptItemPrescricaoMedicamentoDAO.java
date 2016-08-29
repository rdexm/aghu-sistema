package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MptItemPrescricaoMedicamento;

public class MptItemPrescricaoMedicamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptItemPrescricaoMedicamento> {



	private static final long serialVersionUID = -3314498931827878843L;

	/**
	 * Metodo para listar itens de Prescricoes Medicamento, filtrando pelo atdSeq, pteSeq e pmoSeq.
	 * 
	 * @param atdSeq
	 * @param pteSeq
	 * @param pmoSeq
	 * @return
	 */
	public List<MptItemPrescricaoMedicamento> listarItensPrescricaoMedicamento(Integer atdSeq, Integer pteSeq, Integer pmoSeq){
		List<MptItemPrescricaoMedicamento> lista = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MptItemPrescricaoMedicamento.class, "IMO");
		
		criteria.add(Restrictions.eq(MptItemPrescricaoMedicamento.Fields.PMO_PTE_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MptItemPrescricaoMedicamento.Fields.PMO_PTE_SEQ.toString(), pteSeq));
		criteria.add(Restrictions.eq(MptItemPrescricaoMedicamento.Fields.PMO_SEQ.toString(), pmoSeq));
		
		lista = this.executeCriteria(criteria);
		
		return lista;

	}
	/**
	 * Metodo para listar itens de Prescricoes Medicamento com Join 
	 * 
	 * @param atdSeq
	 * @param pteSeqCriteria.LEFT_JOIN
	 * @param pmoSeq
	 * @return 
	 */
	public List<MptItemPrescricaoMedicamento> listarItensPrescricaoMdtoJoin(Integer atdSeq, Integer pteSeq, Integer pmoSeq){		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptItemPrescricaoMedicamento.class, "imo");
		
		criteria.add(Restrictions.eq(MptItemPrescricaoMedicamento.Fields.PMO_PTE_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MptItemPrescricaoMedicamento.Fields.PMO_PTE_SEQ.toString(), pteSeq));
		criteria.add(Restrictions.eq(MptItemPrescricaoMedicamento.Fields.PMO_SEQ.toString(), pmoSeq));
		//criteria.add(Restrictions.isNotNull(MptItemPrescricaoMedicamento.Fields.DOSE.toString()));
		
		criteria.createAlias(MptItemPrescricaoMedicamento.Fields.MEDICAMENTO.toString(),"med");		
		// where umm.seq(+) = med.umm_seq
		criteria.createAlias("med." + AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "umm", JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias("med." + AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), "tum");
		
		criteria.createAlias("imo." + MptItemPrescricaoMedicamento.Fields.FORMA_DOSAGEM.toString(), "fds");
		criteria.createAlias("fds." + AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString(), "umm2", JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria);
	}
}
