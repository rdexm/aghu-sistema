package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoIddGestBallards;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class McoIddGestBallardDAO extends BaseDao<McoIddGestBallards> {
	private static final long serialVersionUID = 875525466722641212L;

	/**
	 * #27482 - C6
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param seqp
	 * @return
	 */
	public Byte obterIdadeGestacionalPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteria =  DetachedCriteria.forClass(McoIddGestBallards.class);
		criteria.add(Restrictions.eq(McoIddGestBallards.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
	   	criteria.addOrder(Order.asc(McoIddGestBallards.Fields.CRIADO_EM.toString()));
	   	List<McoIddGestBallards> lista = executeCriteria(criteria);
	   	if (lista != null) {
			return lista.get(0).getIgSemanas();
		}
		return null;
	}

}
