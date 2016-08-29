package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoFornMarcaMateriais;

public class ScoFornMarcaMateriaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoFornMarcaMateriais> {

	
	
	
	private static final long serialVersionUID = -3946813716446419528L;

	public List<ScoFornMarcaMateriais> pesquisarFornecedorMarca(Integer frnNumero, Integer mcmCodigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornMarcaMateriais.class);		

		criteria.add(Restrictions.eq(ScoFornMarcaMateriais.Fields.FM_FRN_NUMERO.toString(), frnNumero));
		criteria.add(Restrictions.eq(ScoFornMarcaMateriais.Fields.FM_MCM_CODIGO.toString(), mcmCodigo));
		
		return executeCriteria(criteria);
	}


}

