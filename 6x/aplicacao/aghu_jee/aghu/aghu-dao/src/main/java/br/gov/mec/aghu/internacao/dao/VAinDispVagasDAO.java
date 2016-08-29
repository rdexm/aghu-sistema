package br.gov.mec.aghu.internacao.dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.VAinDispVagas;

public class VAinDispVagasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAinDispVagas> {

	private static final long serialVersionUID = 2525713418634430361L;

	private DetachedCriteria createCriteriaVAinDispVagas(final AghClinicas clinica, final AghUnidadesFuncionais unidade) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(VAinDispVagas.class);
		
		if (clinica != null) {
			criteria.createAlias(VAinDispVagas.Fields.CLINICA.toString(), VAinDispVagas.Fields.CLINICA.toString());
			criteria.add(Restrictions.eq(VAinDispVagas.Fields.CLC_CODIGO.toString(), clinica.getCodigo()));
		}

		if (unidade != null) {
			criteria.add(Restrictions.eq(VAinDispVagas.Fields.UF_UNF_SEQ.toString(), unidade.getSeq()));
		}
		
		return criteria;
	}

	public List<VAinDispVagas> pesquisarVAinDispVagas(final Integer firstResult, final Integer maxResult, String orderProperty, final boolean asc, 
													  final AghClinicas clinica, final AghUnidadesFuncionais unidade) {
		final DetachedCriteria criteria = this.createCriteriaVAinDispVagas(clinica, unidade);

		
		boolean orderCampoCalculado = false;
		if(StringUtils.isBlank(orderProperty)){
			orderProperty = "ufAndar";
		} else if("pdrvVciCapacInternacao".equalsIgnoreCase(orderProperty)){
			orderProperty = null;
			orderCampoCalculado = true;
		}
		
		final List<VAinDispVagas> result;
		
		if(orderCampoCalculado){
			result = executeCriteria(criteria);
			
		} else {
			result = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		}
		
		if(orderCampoCalculado){
			if(asc){
				Collections.sort(result, new VAinDispVagasComparatorASC());
			} else {
				Collections.sort(result, new VAinDispVagasComparatorDESC());
			}
			
			if(firstResult != null){
				int limit = (result.size() > (firstResult+10)) ? (firstResult+10) : result.size();  
				return result.subList(firstResult, limit);
			}
		}
		
		return result;
	}
	
	class VAinDispVagasComparatorDESC implements Comparator<VAinDispVagas>{

		@Override
		public int compare(VAinDispVagas o1, VAinDispVagas o2) {
			if(o1 == null || o1.getPdrvVciCapacInternacao() == null){
				return 1;
				
			} else if (o2 == null || o2.getPdrvVciCapacInternacao() == null){
				return -1;
				
			} else {
				return o1.getPdrvVciCapacInternacao().compareTo(o2.getPdrvVciCapacInternacao());
			}
		}
	}
	
	class VAinDispVagasComparatorASC implements Comparator<VAinDispVagas>{

		@Override
		public int compare(VAinDispVagas o1, VAinDispVagas o2) {
			if(o1 == null || o1.getPdrvVciCapacInternacao() == null){
				return -1;
				
			} else if (o2 == null || o2.getPdrvVciCapacInternacao() == null){
				return 1;
				
			} else {
				return o2.getPdrvVciCapacInternacao().compareTo(o1.getPdrvVciCapacInternacao());
			}
		}
	}

	public Long pesquisarVAinDispVagasCount(final AghClinicas clinica, final AghUnidadesFuncionais unidade) {
		return executeCriteriaCount(createCriteriaVAinDispVagas(clinica, unidade));
	}

}
