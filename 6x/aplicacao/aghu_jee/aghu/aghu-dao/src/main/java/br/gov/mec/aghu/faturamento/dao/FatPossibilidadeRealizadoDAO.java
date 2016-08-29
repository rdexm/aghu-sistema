package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatPossibilidadeRealizado;

public class FatPossibilidadeRealizadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatPossibilidadeRealizado> {

	private static final long serialVersionUID = 4759158403690941174L;

	public List<Integer> listarPossibilidadesDistinct(Short iphPhoSeq, Integer iphSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPossibilidadeRealizado.class);

		criteria.add(Restrictions.eq(FatPossibilidadeRealizado.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));

		criteria.add(Restrictions.eq(FatPossibilidadeRealizado.Fields.IPH_SEQ.toString(), iphSeq));

		criteria.setProjection(Projections.distinct(Projections.property(FatPossibilidadeRealizado.Fields.POSSIBILIDADE.toString())));

		return executeCriteria(criteria);
	}

	public List<FatPossibilidadeRealizado> listarPossibilidadesRealizados(Short iphPhoSeq, Integer iphSeq, Integer possibilidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPossibilidadeRealizado.class);

		criteria.add(Restrictions.eq(FatPossibilidadeRealizado.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));

		criteria.add(Restrictions.eq(FatPossibilidadeRealizado.Fields.IPH_SEQ.toString(), iphSeq));

		criteria.add(Restrictions.eq(FatPossibilidadeRealizado.Fields.POSSIBILIDADE.toString(), possibilidade));

		return executeCriteria(criteria);
	}
	
	/**
	 * Método que lista Possibilidade que possui Procedimentos associados de
	 * acordo com o filtro.
	 * @param itemProcedimentoHospitalar
	 * @return
	 */
	public List<FatPossibilidadeRealizado> listarProcedimentosPossibilidadesAssociados(
			FatItensProcedHospitalar itemProcedimentoHospitalar) {
		DetachedCriteria criteria = obterProcedimentosPossibilidadesAssociadosCriteria(itemProcedimentoHospitalar);

		criteria.addOrder(Order.asc("POS."
				+ FatPossibilidadeRealizado.Fields.POSSIBILIDADE.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * 
	 * @param possibilidadeRealizadoId
	 * @return
	 */
	public DetachedCriteria obterProcedimentosPossibilidadesAssociadosCriteria(
			FatItensProcedHospitalar itemProcedimentoHospitalar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				FatPossibilidadeRealizado.class, "POS");

		criteria.createAlias(
				"POS."
						+ FatPossibilidadeRealizado.Fields.PROCEDIMENTOS_HOSPITALAR
								.toString(),
				FatPossibilidadeRealizado.Fields.PROCEDIMENTOS_HOSPITALAR
						.toString(), JoinType.INNER_JOIN);

		if (itemProcedimentoHospitalar.getProcedimentoHospitalar().getSeq() != null) {
			criteria.add(Restrictions.eq("POS."
					+ FatPossibilidadeRealizado.Fields.IPH_PHO_SEQ.toString(),
					itemProcedimentoHospitalar.getId().getPhoSeq()));
		}

		if (itemProcedimentoHospitalar.getSeq() != null) {
			criteria.add(Restrictions.eq("POS."
					+ FatPossibilidadeRealizado.Fields.IPH_SEQ.toString(),
					itemProcedimentoHospitalar.getId().getSeq()));
		}

		return criteria;
	}

}
