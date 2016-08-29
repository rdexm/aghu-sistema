package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialDescTecnica;

public class ScoMaterialDescTecnicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoMaterialDescTecnica>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4747902286350581074L;
	
	
	/*
	 * Consultar todas as vinculacoes de descricoes tecnicas de um determinado material.
	 */
	public List<ScoMaterialDescTecnica> buscarListaDescricoesByCodigoMaterial(final ScoMaterial material) {
			DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterialDescTecnica.class);
			criteria.createAlias(ScoMaterialDescTecnica.Fields.DESCRICAO.toString(), "dt");
			criteria.add(Restrictions.eq(ScoMaterialDescTecnica.Fields.MATERIAL.toString(), material));
			
			return executeCriteria(criteria);
	}
	
}
