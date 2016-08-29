package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoFnRamoComerClas;
import br.gov.mec.aghu.core.exception.BaseException;

public class ScoFnRamoComerClasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoFnRamoComerClas> {

	private static final long serialVersionUID = 2378611067680831118L;

	public ScoFnRamoComerClas pesquisarScoFornRamoComerciailClasPorForneCodigo(Integer numForn, Short rcmCodigo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoFnRamoComerClas.class);
		criteria.add(Restrictions.eq(ScoFnRamoComerClas.Fields.SCO_NUM_FORN.toString(),numForn));
		criteria.add(Restrictions.eq(ScoFnRamoComerClas.Fields.SCO_RCM_CODIGO.toString(),rcmCodigo));
		
		return (ScoFnRamoComerClas) executeCriteriaUniqueResult(criteria);
	}
	
	public void removerScoFnRamoComerClas(Object chavePrimaria) throws BaseException{
		ScoFnRamoComerClas scoFnRamoComerClas = super.obterPorChavePrimaria(chavePrimaria);
		super.remover(scoFnRamoComerClas);
	}
}