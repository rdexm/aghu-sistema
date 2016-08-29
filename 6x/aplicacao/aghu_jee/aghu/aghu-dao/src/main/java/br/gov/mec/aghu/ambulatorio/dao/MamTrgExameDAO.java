package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamTrgExames;

public class MamTrgExameDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTrgExames> {

	private static final long serialVersionUID = 227895534373313759L;

	/**
	 * CURSOR c_exames
	 * @param trgSeq
	 * @return List<MamTrgExames>
	*/
	public List<MamTrgExames> pesquisarMamTrgExamesComItem(Long trgSeq) {
		DetachedCriteria dc = DetachedCriteria.forClass(MamTrgExames.class, "EMS");

		dc.add(Restrictions.isNotNull("EMS.".concat(MamTrgExames.Fields.EMS_SEQ.toString())));
		dc.add(Restrictions.eq("EMS.".concat(MamTrgExames.Fields.TRG_SEQ.toString()), trgSeq));
		dc.add(Restrictions.eq("EMS.".concat(MamTrgExames.Fields.IND_USO.toString()), Boolean.TRUE));
		
		return executeCriteria(dc);
	}
	
	public Short obterMaxSeqPTrgExame(Long trgSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgExames.class, "MamTrgExames");

		criteria.add(Restrictions.eq("MamTrgExames." + MamTrgExames.Fields.TRG_SEQ.toString(), trgSeq));

		criteria.setProjection(Projections.max("MamTrgExames." + MamTrgExames.Fields.SEQP.toString()));

		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return Short.valueOf(String.valueOf(maxSeqP + 1));
		}
		return 1;
	}
	
	public List<MamTrgExames> listarMamTrgExamesPorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgExames.class, "MamTrgExames");
		criteria.createAlias("MamTrgExames." + MamTrgExames.Fields.MAM_ITEM_EXAME.toString(), "MamItemExame");
		
		criteria.add(Restrictions.eq("MamTrgExames." + MamTrgExames.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.addOrder(Order.asc("MamTrgExames." + MamTrgExames.Fields.EMS_SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<MamTrgExames> listarMamTrgExamesPorTriagemEItemExame(Long trgSeq, Integer emsSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgExames.class, "MamTrgExames");
		criteria.createAlias("MamTrgExames." + MamTrgExames.Fields.MAM_ITEM_EXAME.toString(), "MamItemExame");
		
		criteria.add(Restrictions.eq("MamTrgExames." + MamTrgExames.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.add(Restrictions.eq("MamTrgExames." + MamTrgExames.Fields.EMS_SEQ.toString(), emsSeq));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta utilizada pra buscar quais itens já estão inseridos no grid de “Exames”
	 * 
	 * C8 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param trgSeq
	 * @return
	 */
	public List<MamTrgExames> pesquisarMamTrgExamesPorTriagem(Long trgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgExames.class);
		criteria.add(Restrictions.eq(MamTrgExames.Fields.TRG_SEQ.toString(), trgSeq));

		criteria.createAlias(MamTrgExames.Fields.MAM_ITEM_EXAME.toString(), "MAM_ITEM_EXAME");
		criteria.addOrder(Order.asc("MAM_ITEM_EXAME." + MamItemExame.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta utilizada para obter um seqp para inserção de registro em MAM_TRG_EXAMES
	 * 
	 * C11 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param trgSeq
	 * @return
	 */
	public Short obterProximoSeqPorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgExames.class);
		criteria.add(Restrictions.eq(MamTrgExames.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.setProjection(Projections.max(MamTrgExames.Fields.SEQP.toString()));
		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return ++maxSeqP;
		}
		return 1;
	}
	
}
