/**
 * 
 */
package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.FatConvPlanoAcomodacoes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;

/**
 * @author marcelofilho
 *
 */
public class FatConvPlanoAcomodacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatConvPlanoAcomodacoes> {

	private static final long serialVersionUID = 7525640773820407678L;

	public List<FatConvPlanoAcomodacoes> pesquisarConvPlanoAcomodocaoConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		if (convenioSaudePlano == null || convenioSaudePlano.getId() == null) {
			// Nunca deveria ser nulo.
			throw new IllegalArgumentException("Convênio Saúde Plano não informado.");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatConvPlanoAcomodacoes.class);

		criteria.createAlias(FatConvPlanoAcomodacoes.Fields.AIN_ACOMODACAO.toString(), "AAC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(
				FatConvPlanoAcomodacoes.Fields.CONVENIO_SAUDE_PLANO.toString(),
				convenioSaudePlano));

		return executeCriteria(criteria);
	}

}
