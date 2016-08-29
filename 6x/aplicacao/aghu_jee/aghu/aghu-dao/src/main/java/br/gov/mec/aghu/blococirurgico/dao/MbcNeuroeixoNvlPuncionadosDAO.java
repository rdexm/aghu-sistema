package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcNeuroeixoNvlPuncionados;

public class MbcNeuroeixoNvlPuncionadosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcNeuroeixoNvlPuncionados> {

	private static final long serialVersionUID = -8907962674923730321L;

	public List<MbcNeuroeixoNvlPuncionados> pesquisarMbcNeuroNvlPuncionadosByMbcAnestRegionalNeuroeixos(
			Integer seqMbcNeuroEixoNvlPuncionados) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcNeuroeixoNvlPuncionados.class);
		criteria.createAlias(MbcNeuroeixoNvlPuncionados.Fields.MBC_NIVEL_PUNCIONADOS.toString(), "pun", Criteria.LEFT_JOIN);
		criteria.createAlias(MbcNeuroeixoNvlPuncionados.Fields.MBC_POSICIONAMENTOS.toString(), "poi", Criteria.LEFT_JOIN);
		criteria.createAlias(MbcNeuroeixoNvlPuncionados.Fields.MBC_ANEST_REGIONAL_NEUROEIXOS.toString(), "neu");
		
		criteria.add(Restrictions.eq("neu." + MbcNeuroeixoNvlPuncionados.Fields.SEQ.toString(), seqMbcNeuroEixoNvlPuncionados));
		
		criteria.addOrder(Order.asc(MbcNeuroeixoNvlPuncionados.Fields.BLOQUEIO.toString()));
		
		return executeCriteria(criteria);
	}




}
