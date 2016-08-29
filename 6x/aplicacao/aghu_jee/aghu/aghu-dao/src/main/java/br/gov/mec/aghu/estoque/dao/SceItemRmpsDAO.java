package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceRmrPaciente;

/**
 * 
 * @modulo estoque
 *
 */
public class SceItemRmpsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemRmps> {

	private static final long serialVersionUID = -7963440768588350045L;

	public SceItemRmps buscarItemRmpsPorRmpSeq(final Integer rmpSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRmps.class);

		criteria.createAlias(SceItemRmps.Fields.SCE_RMR_PACIENTE.toString(), "rmp");

		criteria.createAlias("rmp." + SceRmrPaciente.Fields.SCO_FORNECEDOR.toString(), "sfo");

		criteria.add(Restrictions.eq("rmp." + SceRmrPaciente.Fields.SEQ.toString(), rmpSeq));

		criteria.add(Restrictions.isNotNull("rmp." + SceRmrPaciente.Fields.NF_GERAL.toString()));

		final List<SceItemRmps> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}


	public List<SceItemRmps> listarItensRmpsPorRmpSeqNfNaoNula(final Integer rmpSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRmps.class);

		criteria.createAlias(SceItemRmps.Fields.SCE_RMR_PACIENTE.toString(), "rmp");

		criteria.add(Restrictions.eq("rmp." + SceRmrPaciente.Fields.SEQ.toString(), rmpSeq));

		criteria.add(Restrictions.isNotNull(SceItemRmps.Fields.NOTA_FISCAL.toString()));

		return executeCriteria(criteria);
	}
	
	/** Migracao da seguinte consulta, que preenche parte do id de um novo SceItemRmps.
	 * 
	 *  SELECT NVL(MAX(NUMERO),0) + 1
 	 *	FROM   SCE_ITEM_RMPS
 	 *	WHERE  RMP_SEQ = NVL(P_RMP_SEQ, 1);
	 */

	public Short obterProximoNumero(Integer rmpSeq){
		
		if(rmpSeq == null){
			rmpSeq = 1;
		}
		
		Short numeroMax = null;
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRmps.class);
		
		criteria.createAlias(SceItemRmps.Fields.SCE_RMR_PACIENTE.toString(), "rmp");
		
		criteria.add(Restrictions.eq("rmp." + SceRmrPaciente.Fields.SEQ.toString(), rmpSeq));
		
		criteria.setProjection(Projections.max(SceItemRmps.Fields.NUMERO.toString()));
		
		numeroMax = (Short) executeCriteriaUniqueResult(criteria);
		
		if(numeroMax == null || numeroMax.equals(0)){
			numeroMax = 1;
		}else{
			numeroMax++;
		}
		
		return numeroMax;
	}

	public Long obterListaSceItemRmpsPorSceRmrPacienteCount(final Integer seqRmrPaciente) {
		return executeCriteriaCount(criarCreteriaObterListaSceItemRmpsPorSceRmrPaciente(seqRmrPaciente));
	}

	private DetachedCriteria criarCreteriaObterListaSceItemRmpsPorSceRmrPaciente(final Integer seqRmrPaciente){
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceItemRmps.class);
		criteria.createAlias(SceItemRmps.Fields.SCE_RMR_PACIENTE.toString(), "rmp");
		criteria.add(Restrictions.eq("rmp." + SceRmrPaciente.Fields.SEQ.toString(), seqRmrPaciente));
		return criteria;
	}

	public Long countSceRmrPaciente(Integer rmpSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(SceItemRmps.class);

		criteria.add(Restrictions.eq(SceItemRmps.Fields.RMP_SEQ.toString(),
				rmpSeq));

		return executeCriteriaCount(criteria);
	}

}
