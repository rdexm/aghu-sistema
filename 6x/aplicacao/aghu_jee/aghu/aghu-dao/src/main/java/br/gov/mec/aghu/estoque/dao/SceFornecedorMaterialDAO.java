package br.gov.mec.aghu.estoque.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceFornecedorMaterial;

public class SceFornecedorMaterialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceFornecedorMaterial> {

	private static final long serialVersionUID = 3893083858452787461L;

	/** Pesquisa FornecedorMaterial pelo Numero do Fornecedor e Codigo do material
	 * 
	 * @param frnNumero
	 * @param matCodigo
	 * @return
	 */
	public SceFornecedorMaterial obterFornecedorMaterialPorFornecedorNumeroEMaterialCodigo(
			Integer frnNumero, Integer matCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceFornecedorMaterial.class);
		
		if (frnNumero != null){
			criteria.add(Restrictions.eq(SceFornecedorMaterial.Fields.FRN_NUMERO.toString(), frnNumero));
		}
		if (matCodigo != null){
			criteria.add(Restrictions.eq(SceFornecedorMaterial.Fields.MAT_CODIGO.toString(), matCodigo));
		}
		return (SceFornecedorMaterial) executeCriteriaUniqueResult(criteria);
	}
}

