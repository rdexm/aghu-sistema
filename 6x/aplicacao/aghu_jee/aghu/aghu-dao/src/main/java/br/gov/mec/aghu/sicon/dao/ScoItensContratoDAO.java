package br.gov.mec.aghu.sicon.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
/**
 * @modulo sicon
 * @author cvagheti
 *
 */
public class ScoItensContratoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoItensContrato> {

	private static final long serialVersionUID = 2697486371566738746L;

	public List<ScoItensContrato> getItensContratoByContrato(ScoContrato input) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItensContrato.class);
		
		criteria.createAlias(ScoItensContrato.Fields.SERVICO.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoItensContrato.Fields.MATERIAL.toString(), "mat", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoItensContrato.Fields.MARCA_COMERCIAL.toString(), "marcaComercial", JoinType.LEFT_OUTER_JOIN);
				
		criteria.createAlias("serv." + ScoServico.Fields.SERVICO_SICON.toString(), "serv_sicon", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("mat." + ScoMaterial.Fields.MATERIAL_SICON.toString(), "mat_sicon", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(
				ScoItensContrato.Fields.CONT_SEQ.toString(), input.getSeq()));
		return executeCriteria(criteria);
	}

	public boolean possuiItensContrato(ScoContrato input) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoItensContrato.class);
		criteria.add(Restrictions.eq(
				ScoItensContrato.Fields.CONT_SEQ.toString(), input.getSeq()));

		List<ScoItensContrato> listaItens = executeCriteria(criteria);

		if (listaItens.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public List<ScoItensContrato> getItensContratoByContratoManual(ScoContrato input) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItensContrato.class);
		criteria.add(Restrictions.eq(ScoItensContrato.Fields.CONT_SEQ.toString(), input.getSeq()));
		criteria.add(Restrictions.isNotNull(ScoItensContrato.Fields.SERVICO.toString()));
		return executeCriteria(criteria);
	}
}
