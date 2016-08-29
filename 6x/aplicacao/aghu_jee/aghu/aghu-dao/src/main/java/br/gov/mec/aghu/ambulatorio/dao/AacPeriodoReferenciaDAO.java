/**
 * 
 */
package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.model.AacPeriodoReferencia;

public class AacPeriodoReferenciaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacPeriodoReferencia> {
	
	private static final long serialVersionUID = 4540281544886940690L;

	public List<AacPeriodoReferencia> pesquisarPeriodoReferencia() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacPeriodoReferencia.class);
		return executeCriteria(criteria);
	}
}
