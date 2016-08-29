package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmCidUnidFuncional;
import br.gov.mec.aghu.model.RapServidores;
	
	public class MpmCidUnidFuncionalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmCidUnidFuncional> {
		
		private static final long serialVersionUID = 5407864112260372101L;

		public List<MpmCidUnidFuncional> listaCidUnidadeFuncional
		(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AghUnidadesFuncionais aghUnidadesFuncionais) {
			
			DetachedCriteria criteria = DetachedCriteria.forClass(MpmCidUnidFuncional.class);		
			
			criteria.createAlias(MpmCidUnidFuncional.Fields.AGH_CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(MpmCidUnidFuncional.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);

			if(aghUnidadesFuncionais != null ){
				criteria.add(Restrictions.eq(MpmCidUnidFuncional.Fields.UNF_SEQ.toString(), aghUnidadesFuncionais.getSeq()));
			}	
			return executeCriteria(criteria, firstResult, maxResult, "unidadeFuncional"  , true);
		}
		
		public Long listaCidUnidadeFuncionalCount(AghUnidadesFuncionais aghUnidadesFuncionais) {
			
			DetachedCriteria criteria = DetachedCriteria.forClass(MpmCidUnidFuncional.class);		

			if(aghUnidadesFuncionais != null ){
				criteria.add(Restrictions.eq(MpmCidUnidFuncional.Fields.UNF_SEQ.toString(), aghUnidadesFuncionais.getSeq()));
			}	
			return executeCriteriaCount(criteria);
		}
		
		public Boolean verificaCidUnidadeFuncional(MpmCidUnidFuncional mpmCidUnidFuncional){
			List<MpmCidUnidFuncional> result = null;

			DetachedCriteria criteria = DetachedCriteria.forClass(MpmCidUnidFuncional.class);
			criteria.add(Restrictions.eq(MpmCidUnidFuncional.Fields.CID_SEQ.toString(), mpmCidUnidFuncional.getId().getCidSeq()));
			criteria.add(Restrictions.eq(MpmCidUnidFuncional.Fields.UNF_SEQ.toString(), mpmCidUnidFuncional.getId().getUnfSeq()));
			
			result = executeCriteria(criteria);
			if(result.size()>0){
				return true;
			}else{
				return false;
			}

		}
		
}
