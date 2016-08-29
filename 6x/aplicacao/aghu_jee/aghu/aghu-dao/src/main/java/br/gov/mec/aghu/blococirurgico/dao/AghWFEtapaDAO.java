package br.gov.mec.aghu.blococirurgico.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.ShortType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFFluxo;

public class AghWFEtapaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghWFEtapa> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6326681845952938987L;
	

	public Short obterProxSequenciaParaEtapaFluxo(Integer seqFluxo) {
		Short proxSequencia = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(AghWFEtapa.class,"WET");
		criteria.createAlias("WET."+AghWFEtapa.Fields.WFL_SEQ.toString(), "WFL");
		criteria.add(Restrictions.eq("WFL."+AghWFFluxo.Fields.SEQ.toString(), seqFluxo));

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sqlProjection("COALESCE(MAX(SEQUENCIA),0) + 1 as proxSequencia", new String[]{"proxSequencia"}, new Type[] { ShortType.INSTANCE }), "proxSequencia");		
		criteria.setProjection(projection);
		proxSequencia = (Short) executeCriteriaUniqueResult(criteria);
		return proxSequencia;
	}
	
}
