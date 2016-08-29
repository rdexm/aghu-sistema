package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAelSolicPac;

public class VAelSolicPacDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelSolicPac>{

	private static final long serialVersionUID = 8854627853595730881L;

	public List<VAelSolicPac> buscarVAelSolicPacPorSeqSolicitacao(Integer solSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelSolicPac.class);
		criteria.add(Restrictions.eq(VAelSolicPac.Fields.SOLICITACAO.toString(), solSeq));
		return executeCriteria(criteria); 
	}
	
}
