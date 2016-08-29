package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmControlPrevAltas;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;



public class MpmControlPrevAltasDAO extends BaseDao<MpmControlPrevAltas> {

    private static final long serialVersionUID = -7811426933931013918L;

	/**
     * Obtem último registro de MpmControlPrevAltas por atendimento
     * @param aghAtendimentos
     * @return
     */
	public MpmControlPrevAltas obterUltimoControlePrevisaoAltasPorAtendimento(
			AghAtendimentos aghAtendimentos) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmControlPrevAltas.class);
		criteria.add(Restrictions.eq(
				MpmControlPrevAltas.Fields.ATENDIMENTO.toString(),
				aghAtendimentos));
		criteria.addOrder(Order.desc(MpmControlPrevAltas.Fields.SEQ.toString()));
		List<MpmControlPrevAltas> controlPrevAltas = executeCriteria(criteria);
		MpmControlPrevAltas controlPrevAltasRetorno = null;
		if (!controlPrevAltas.isEmpty()) {
			controlPrevAltasRetorno = controlPrevAltas.get(0);
		}
		return controlPrevAltasRetorno;
	}
	
}
