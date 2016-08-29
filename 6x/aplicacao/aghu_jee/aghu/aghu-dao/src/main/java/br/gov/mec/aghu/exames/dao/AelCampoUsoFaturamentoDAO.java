package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelCampoUsoFaturamento;

public class AelCampoUsoFaturamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelCampoUsoFaturamento> {

	private static final long serialVersionUID = -495742016671799528L;

	public List<AelCampoUsoFaturamento> pesquisarCampoUsoFaturamentoPorExameMaterial(String exaSigla, Integer manSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoUsoFaturamento.class, "CPF");
		
		criteria.createAlias(AelCampoUsoFaturamento.Fields.CAMPO_LAUDO.toString(), "CAL");

		criteria.add(Restrictions.eq("CPF." + AelCampoUsoFaturamento.Fields.EMA_EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq("CPF." + AelCampoUsoFaturamento.Fields.EMA_MAN_SEQ.toString(), manSeq));

		return this.executeCriteria(criteria);
	}

}
