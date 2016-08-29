package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedorMarca;
import br.gov.mec.aghu.model.ScoMarcaComercial;

/**
 * 
 * @modulo compras
 *
 */
public class ScoFornecedorMarcaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoFornecedorMarca> {

	
	
	
	private static final long serialVersionUID = 5960362016494506224L;

	public List<ScoFornecedorMarca> pesquisarFornecedorMarca(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, ScoFornecedor fornecedor, String descricaoMarca) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedorMarca.class);
		criteria.createAlias(ScoFornecedorMarca.Fields.MARCA_COMERCIAL.toString(), "marc");

		if(fornecedor != null ){
			criteria.add(Restrictions.eq(ScoFornecedorMarca.Fields.FORNECEDOR.toString(), fornecedor));
		}	
		if(descricaoMarca != null && !descricaoMarca.equals("")){
			criteria.add(Restrictions.ilike("marc."+ScoMarcaComercial.Fields.DESCRICAO.toString(), descricaoMarca,MatchMode.ANYWHERE));
		}
		return executeCriteria(criteria, firstResult, maxResult, null  , true);
	}
	
	public Long fornecedorMarcaCount(ScoFornecedor scoFornecedor){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedorMarca.class);		

		if(scoFornecedor != null ){
			criteria.add(Restrictions.eq(ScoFornecedorMarca.Fields.FORNECEDOR.toString(), scoFornecedor));
		}				
		return executeCriteriaCount(criteria);
	}

	public Boolean verificaFornecedorMarca(ScoFornecedorMarca scoFornecedorMarca){
		List<ScoFornecedorMarca> result = null;

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFornecedorMarca.class);
		criteria.add(Restrictions.eq(ScoFornecedorMarca.Fields.FRNNUMERO.toString(), scoFornecedorMarca.getId().getFrnNumero()));
		criteria.add(Restrictions.eq(ScoFornecedorMarca.Fields.MCMCODIGO.toString(), scoFornecedorMarca.getId().getMcmCodigo()));
		
		result = executeCriteria(criteria);
		if(result.size()>0){
			return true;
		}else{
			return false;
		}

	}


}

