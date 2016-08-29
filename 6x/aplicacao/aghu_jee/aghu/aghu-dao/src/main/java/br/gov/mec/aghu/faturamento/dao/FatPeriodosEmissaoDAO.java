/**
 * 
 */
package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatPeriodosEmissao;

/**
 * @author marcelofilho
 *
 */
public class FatPeriodosEmissaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatPeriodosEmissao> {

	private static final long serialVersionUID = 1039872723748804908L;

	/**
	 * @param convenioSaudePlano
	 * @return
	 */
	public List<FatPeriodosEmissao> pesquisarPeriodosEmissaoConvenioSaudePlano(
			FatConvenioSaudePlano convenioSaudePlano) {

		if (convenioSaudePlano == null || convenioSaudePlano.getId() == null) {
			// Nunca deveria ser nulo.
			throw new IllegalArgumentException("Convênio Saúde Plano não informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(FatPeriodosEmissao.class);

		criteria.add(Restrictions.eq(
				FatPeriodosEmissao.Fields.CONVENIO_SAUDE_PLANO.toString(),
				convenioSaudePlano));

		return executeCriteria(criteria);
	}

}
