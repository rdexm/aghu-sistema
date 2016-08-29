package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.MamTrgMedicacoes;

public class MamTrgMedicacaoDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTrgMedicacoes> {

	private static final long serialVersionUID = 227895534373313759L;

	public Short obterMaxSeqPTrgMedicacao(Long trgSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgMedicacoes.class, "MamTrgMedicacoes");

		criteria.add(Restrictions.eq("MamTrgMedicacoes." + MamTrgMedicacoes.Fields.TRG_SEQ.toString(), trgSeq));

		criteria.setProjection(Projections.max("MamTrgMedicacoes." + MamTrgMedicacoes.Fields.SEQP.toString()));

		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return Short.valueOf(String.valueOf(maxSeqP + 1));
		}
		return 1;
	}
	
	public List<MamTrgMedicacoes> listarMamTrgMedicacoesPorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgMedicacoes.class, "MamTrgMedicacoes");
		criteria.createAlias("MamTrgMedicacoes." + MamTrgMedicacoes.Fields.ITEM_MEDICACAO.toString(), "MamItemMedicacao");
		
		criteria.add(Restrictions.eq("MamTrgMedicacoes." + MamTrgMedicacoes.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.addOrder(Order.asc("MamTrgMedicacoes." + MamTrgMedicacoes.Fields.MDM_SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<MamTrgMedicacoes> listarMamTrgMedicacoesPorTriagemEItemMedicacao(Long trgSeq, Integer mdmSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgMedicacoes.class, "MamTrgMedicacoes");
		criteria.createAlias("MamTrgMedicacoes." + MamTrgMedicacoes.Fields.ITEM_MEDICACAO.toString(), "MamItemMedicacao");
		
		criteria.add(Restrictions.eq("MamTrgMedicacoes." + MamTrgMedicacoes.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.add(Restrictions.eq("MamTrgMedicacoes." + MamTrgMedicacoes.Fields.MDM_SEQ.toString(), mdmSeq));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta utilizada pra buscar quais itens já estão inseridos no grid de “Medicações”
	 * 
	 * C9 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param trgSeq
	 * @return
	 */
	public List<MamTrgMedicacoes> pesquisarMamTrgMedicacoesPorTriagem(Long trgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgMedicacoes.class);
		criteria.add(Restrictions.eq(MamTrgMedicacoes.Fields.TRG_SEQ.toString(), trgSeq));
		
		criteria.createAlias(MamTrgMedicacoes.Fields.ITEM_MEDICACAO.toString(), "ITEM_MEDICACAO");
		criteria.addOrder(Order.asc("ITEM_MEDICACAO." + MamItemMedicacao.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta utilizada para obter um seqp para inserção de registro em MAM_TRG_MEDICACOES
	 * 
	 * C12 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param trgSeq
	 * @return
	 */
	public Short obterProximoSeqPorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgMedicacoes.class);
		criteria.add(Restrictions.eq(MamTrgMedicacoes.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.setProjection(Projections.max(MamTrgMedicacoes.Fields.SEQP.toString()));
		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return ++maxSeqP;
		}
		return 1;
	}
}
