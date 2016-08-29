package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamItemGeral;
import br.gov.mec.aghu.model.MamTrgGerais;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamTrgGeralDAO extends BaseDao<MamTrgGerais> {

	private static final long serialVersionUID = 4060270788508740122L;
	
	private static final String MAM_TRG_GERAIS = "MamTrgGerais.";

	public Short obterMaxSeqPTrgGeral(Long trgSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGerais.class, "MamTrgGerais");

		criteria.add(Restrictions.eq(MAM_TRG_GERAIS + MamTrgGerais.Fields.TRG_SEQ.toString(), trgSeq));

		criteria.setProjection(Projections.max(MAM_TRG_GERAIS + MamTrgGerais.Fields.SEQP.toString()));

		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return Short.valueOf(String.valueOf(maxSeqP + 1));
		}
		return 1;
	}
	
	public List<MamTrgGerais> listarMamTrgGeralPorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGerais.class, "MamTrgGerais");
		criteria.createAlias(MAM_TRG_GERAIS + MamTrgGerais.Fields.MAM_ITEM_GERAL.toString(), "MamItemGeral");
		
		criteria.add(Restrictions.eq(MAM_TRG_GERAIS + MamTrgGerais.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.addOrder(Order.asc(MAM_TRG_GERAIS + MamTrgGerais.Fields.ITG_SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<MamTrgGerais> listarMamTrgGeralPorItemGeralETriagem(Long trgSeq, Integer itgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGerais.class, "MamTrgGerais");
		criteria.createAlias(MAM_TRG_GERAIS + MamTrgGerais.Fields.MAM_ITEM_GERAL.toString(), "MamItemGeral");
		
		criteria.add(Restrictions.eq(MAM_TRG_GERAIS + MamTrgGerais.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.add(Restrictions.eq(MAM_TRG_GERAIS + MamTrgGerais.Fields.ITG_SEQ.toString(), itgSeq));
		
		return executeCriteria(criteria);
	}

	/**
	 * Consulta utilizada pra buscar quais itens já estão inseridos no grid de “Medicações”
	 * 
	 * C10 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param trgSeq
	 * @return
	 */
	public List<MamTrgGerais> pesquisarMamTrgGeraisPorTriagem(Long trgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGerais.class);
		criteria.add(Restrictions.eq(MamTrgGerais.Fields.TRG_SEQ.toString(), trgSeq));
		
		criteria.createAlias(MamTrgGerais.Fields.MAM_ITEM_GERAL.toString(), "MAM_ITEM_GERAL");
		criteria.addOrder(Order.asc("MAM_ITEM_GERAL." + MamItemGeral.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta utilizada para obter um seqp para inserção de registro em MAM_TRG_GERAIS
	 * 
	 * C13 de #34193 - INFORMAR FLUXOGRAMA
	 * 
	 * @param trgSeq
	 * @return
	 */
	public Short obterProximoSeqPorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgGerais.class);
		criteria.add(Restrictions.eq(MamTrgGerais.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.setProjection(Projections.max(MamTrgGerais.Fields.SEQP.toString()));
		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return ++maxSeqP;
		}
		return 1;
	}
}