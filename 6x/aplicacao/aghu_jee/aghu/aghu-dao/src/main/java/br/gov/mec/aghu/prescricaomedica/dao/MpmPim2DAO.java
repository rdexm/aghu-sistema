package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoPim2;
import br.gov.mec.aghu.model.MpmPim2;
import br.gov.mec.aghu.model.RapServidores;

public class MpmPim2DAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPim2> {

	private static final long serialVersionUID = -8563979196758808514L;

	/**
	 * Método para pesquisa de entidades MmpPim2 por atendimento.
	 * @param seqAtendimento
	 * @return
	 */
	public List<MpmPim2> pesquisarPim2PorAtendimento(Integer seqAtendimento, DominioSituacaoPim2 situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPim2.class);
		criteria.createAlias(MpmPim2.Fields.SERVIDOR.toString(), "RAP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RAP."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmPim2.Fields.ATD_SEQ.toString(),seqAtendimento));
		if (situacao != null) {
			criteria.add(Restrictions.eq(MpmPim2.Fields.SITUACAO.toString(), DominioSituacaoPim2.P));
		}

		criteria.addOrder(Order.desc(MpmPim2.Fields.CRIADO_EM.toString()));
		
		return this.executeCriteria(criteria);
	}

	/**
	 * Método para pesquisa de entidades MmpPim2 por situacao e atendimento.
	 * 
	 * @param seqAtendimento
	 * @param situacao
	 * @return
	 */
	public List<MpmPim2> pesquisarPim2PorAtendimentoSituacao(
			Integer seqAtendimento, DominioSituacaoPim2 situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPim2.class);
		criteria.add(Restrictions.eq(MpmPim2.Fields.ATD_SEQ.toString(),
				seqAtendimento));
		criteria.add(Restrictions.eq(MpmPim2.Fields.SITUACAO.toString(),
				situacao));
		criteria.addOrder(Order.desc(MpmPim2.Fields.CRIADO_EM.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Método para pesquisa de entidades MmpPim2 por situações e atendimento.
	 * 
	 * @param seqAtendimento
	 * @param situacao
	 * @return
	 * @author bruno.mourao
	 * @since 24/10/2012
	 */
	public List<MpmPim2> pesquisarPim2PorAtendimentoSituacao(Integer seqAtendimento, List<DominioSituacaoPim2> situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmPim2.class);
		criteria.add(Restrictions.eq(MpmPim2.Fields.ATD_SEQ.toString(),
				seqAtendimento));
		criteria.add(Restrictions.in(MpmPim2.Fields.SITUACAO.toString(),situacao));
		
		criteria.addOrder(Order.desc(MpmPim2.Fields.DTHR_INGRESSO_UNIDADE.toString()));
		
		return this.executeCriteria(criteria);
	}
}
