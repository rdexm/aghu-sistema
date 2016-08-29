package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoPareceresMateriais;

public class ScoPareceresMateriaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoPareceresMateriais> {	

	private static final long serialVersionUID = -419052006821575536L;
	
	public List<ScoPareceresMateriais> pesquisarParecesMateriaisPorMaterialMarca(Integer codigoMaterial, Integer codigoMarcaComercial){
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoPareceresMateriais.class, "SCOPMT");
		
		criteria.add(Restrictions.eq("SCOPMT."+ScoPareceresMateriais.Fields.MAT_CODIGO.toString(), codigoMaterial));	
		criteria.add(Restrictions.eq("SCOPMT."+ScoPareceresMateriais.Fields.IND_EXCLUIDO.toString(), false));	
		
		if (codigoMarcaComercial != null){
			criteria.add(Restrictions.eq("SCOPMT."+ScoPareceresMateriais.Fields.PTC_MCM_CODIGO.toString(), codigoMarcaComercial));
		}
		return this.executeCriteria(criteria);
			 
	}
	
	
}
