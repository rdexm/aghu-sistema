package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MciMaterialInfecPatologia;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MciMaterialInfecPatologiaDAO extends BaseDao<MciMaterialInfecPatologia> {

	private static final long serialVersionUID = -8275995300286112636L;
	
	public List<MciMaterialInfecPatologia> pesquisarMciMicroorganismoPatologiaPorPatologia(final Integer codigoPatologia){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMaterialInfecPatologia.class);
		criteria.createAlias(MciMaterialInfecPatologia.Fields.MCI_PATOLOGIA_INFECCAO.toString(), "MPI", JoinType.INNER_JOIN);
		criteria.createAlias(MciMaterialInfecPatologia.Fields.MCI_MATERIAL_INFECTANTES.toString(), "MIN", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MPI." + MciPatologiaInfeccao.Fields.SEQ.toString(), codigoPatologia));
		return executeCriteria(criteria);
	}
	
	
}
