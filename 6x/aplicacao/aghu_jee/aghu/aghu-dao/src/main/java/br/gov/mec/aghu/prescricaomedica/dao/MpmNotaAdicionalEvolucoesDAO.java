package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.model.MpmNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.RapServidores;

public class MpmNotaAdicionalEvolucoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmNotaAdicionalEvolucoes> {

	private static final long serialVersionUID = 123323456576L;
	
	public List<MpmNotaAdicionalEvolucoes> listarNotasAdicionaisEvolucoesPorSeqEvolucao(Long seqEvolucoes) {
		DetachedCriteria criteria = criarCriteriaListarNotaAdicionalPorSeqEvolucao(seqEvolucoes);
		criteria.addOrder(Order.desc(MpmNotaAdicionalEvolucoes.Fields.DTHR_CRIACAO.toString()));
		return executeCriteria(criteria);
	}
	
	public boolean possuiNotaAdicional(Long seqEvolucoes) {
		DetachedCriteria criteria = criarCriteriaListarNotaAdicionalPorSeqEvolucao(seqEvolucoes);
		return executeCriteriaExists(criteria);
	}
	
	private DetachedCriteria criarCriteriaListarNotaAdicionalPorSeqEvolucao(Long seqEvolucoes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmNotaAdicionalEvolucoes.class, "MNAE");
		criteria.createAlias("MNAE." + MpmEvolucoes.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.add(Restrictions.eq(MpmNotaAdicionalEvolucoes.Fields.EVOLUCOES_SEQ.toString(), seqEvolucoes));
		return criteria;
	}
	
	public List<MpmNotaAdicionalEvolucoes> listarNotasAdicionais(Long seqEvolucao) {
		DetachedCriteria criteria = criarCriteriaListarNotaAdicionalPorSeqEvolucao(seqEvolucao);
		criteria.addOrder(Order.desc(MpmNotaAdicionalEvolucoes.Fields.DTHR_CRIACAO.toString()));
		return executeCriteria(criteria);
	}

}
