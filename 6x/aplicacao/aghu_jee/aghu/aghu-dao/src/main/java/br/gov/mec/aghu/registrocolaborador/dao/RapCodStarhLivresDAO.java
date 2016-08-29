package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;

import br.gov.mec.aghu.model.RapCodStarhLivres;
/**
 * 
 * @modulo registrocolaborador
 *
 */
public class RapCodStarhLivresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapCodStarhLivres> {

	private static final long serialVersionUID = -4823697516001377256L;

	/**
	 * ORADB: Function RAPC_BUSCA_COD_STARH
	 * 
	 * Versão java da Function RAPC_BUSCA_COD_STARH
	 * 
	 * @param 
	 * @return codStarh
	 */
	public Integer obterProximoCodStarhLivre() {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapCodStarhLivres.class);

		criteria.setProjection(Projections
				.min(RapCodStarhLivres.Fields.COD_STARH.toString()));

		Integer proximoCod = (Integer) executeCriteriaUniqueResult(criteria);

		return proximoCod;
	}
}
