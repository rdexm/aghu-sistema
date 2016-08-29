package br.gov.mec.aghu.internacao.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAinMovimentosExtrato;

public class VAinMovimentosExtratoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAinMovimentosExtrato> {

	private static final long serialVersionUID = -4544600917956997639L;

	private DetachedCriteria createPesquisaCriteria(Integer codigoInternacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAinMovimentosExtrato.class);
		if (codigoInternacao != null) {
			criteria.add(Restrictions.eq(VAinMovimentosExtrato.Fields.INT_SEQ.toString(), codigoInternacao));
		}
		criteria.setProjection(Projections.projectionList().add(// 0
				Projections.property(VAinMovimentosExtrato.Fields.DTHR_LANCAMENTO.toString())).add(// 1
				Projections.property(VAinMovimentosExtrato.Fields.TMI_SEQ.toString())).add(// 2
				Projections.property(VAinMovimentosExtrato.Fields.ESP_SEQ.toString())).add(// 3
				Projections.property(VAinMovimentosExtrato.Fields.LTO_LTO_ID.toString())).add(// 4
				Projections.property(VAinMovimentosExtrato.Fields.QRT_DESCRICAO.toString())).add(// 5
				Projections.property(VAinMovimentosExtrato.Fields.UNF_SEQ.toString())).add(// 6
				Projections.property(VAinMovimentosExtrato.Fields.SER_VIN_CODIGO_GERADO.toString())).add(// 7
				Projections.property(VAinMovimentosExtrato.Fields.SER_MATRICULA_GERADO.toString())).add(// 8
				Projections.property(VAinMovimentosExtrato.Fields.SER_MATRICULA.toString())).add(// 9
				Projections.property(VAinMovimentosExtrato.Fields.SER_VIN_CODIGO.toString())).add(// 10
				Projections.property(VAinMovimentosExtrato.Fields.CRIADO_EM.toString())));
		return criteria;
	}

	public List<Object[]> pesquisarExtratoPaciente(Integer firstResult, Integer maxResult, Integer codigoInternacao,
			Date dataInternacao) {
		DetachedCriteria criteria = this.createPesquisaCriteria(codigoInternacao);
		criteria.addOrder(Order.desc(VAinMovimentosExtrato.Fields.DTHR_LANCAMENTO.toString()));
		return executeCriteria(criteria, firstResult, maxResult, null, true);
	}

	public Long pesquisarExtratoPacienteCount(Integer codigoInternacao, Date dataInternacao) {
		DetachedCriteria criteria = this.createPesquisaCriteria(codigoInternacao);
		return executeCriteriaCount(criteria);
	}
}
