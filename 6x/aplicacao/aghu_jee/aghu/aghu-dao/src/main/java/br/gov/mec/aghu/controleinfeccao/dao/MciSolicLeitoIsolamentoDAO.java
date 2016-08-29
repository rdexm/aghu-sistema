package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MciSolicLeitoIsolamento;

public class MciSolicLeitoIsolamentoDAO extends
br.gov.mec.aghu.core.persistence.dao.BaseDao<MciSolicLeitoIsolamento> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5248791985730444840L;


	//#1297 C6
	public List<MciSolicLeitoIsolamento> pesquisarMciSolicLeitoIsolamentoPorSeqNotificacao(Integer seqNotificacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciSolicLeitoIsolamento.class, "SLI");
		criteria.createAlias("SLI." + MciSolicLeitoIsolamento.Fields.AGH_ATENDIMENTOS.toString(), "ATD");
		if (seqNotificacao != null) {
			criteria.add(Restrictions.eq("SLI."	+ MciSolicLeitoIsolamento.Fields.MMP_SEQ.toString(), seqNotificacao));
		}
		return executeCriteria(criteria);
	}
	
}
