package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MptCidTratTerapeutico;

public class MptCidTratTerapeuticoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptCidTratTerapeutico> {

	private static final long serialVersionUID = -7709941258489971555L;

	// #17309 - Listar sessões terapeuticas Quimio POL - C2 (detalhe CID) 
	public List<MptCidTratTerapeutico> pesquisarMptCidTratTerapeutico (Integer seqMptTratamentoTerapeutico){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptCidTratTerapeutico.class);
		criteria.createAlias(MptCidTratTerapeutico.Fields.AGH_CID.toString(), "aghCid");
		criteria.add(Restrictions.eq(MptCidTratTerapeutico.Fields.TRP_SEQ.toString(), seqMptTratamentoTerapeutico));
		return executeCriteria(criteria);
	}
}
