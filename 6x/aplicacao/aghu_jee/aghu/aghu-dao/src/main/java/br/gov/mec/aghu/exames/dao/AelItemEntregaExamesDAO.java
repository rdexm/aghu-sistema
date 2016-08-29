package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelItemEntregaExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelProtocoloEntregaExames;


public class AelItemEntregaExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelItemEntregaExames> {

	private static final long serialVersionUID = 3928708266403538875L;
	
	
	public List<AelItemSolicitacaoExames> recuperarItensPorNumeroProtocoloAntigo(Long seqAntigoProtocolo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class);
		criteria.createAlias(AelItemSolicitacaoExames.Fields.ITEM_ENTREGA_EXAMES.toString(),"iee" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("iee."+AelItemEntregaExames.Fields.PROTOCOLO.toString(),"pee" ,JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("pee." + AelProtocoloEntregaExames.Fields.SEQ, seqAntigoProtocolo));
		
		return executeCriteria(criteria);
	}
	
	
	
	
	
	
	
	

}
