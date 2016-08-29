package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacNivelBusca;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;

public class AacNivelBuscaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacNivelBusca> {

	private static final long serialVersionUID = 6410197992676556795L;

	public List<AacNivelBusca> pesquisarNivelBuscaPorFormaAgendamento(AacFormaAgendamento formaAgendamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacNivelBusca.class);
		
		criteria.createAlias(AacNivelBusca.Fields.PAGADOR.toString(), "PAG", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacNivelBusca.Fields.CLINICA.toString(), "CLI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacNivelBusca.Fields.CONDICAO_ATENDIMENTO.toString(), "CAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacNivelBusca.Fields.TIPO_AGENDAMENTO.toString(), "TAG", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_CAA_SEQ.toString(), formaAgendamento.getId().getCaaSeq()));
		criteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_TAG_SEQ.toString(), formaAgendamento.getId().getTagSeq()));
		criteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_PGD_SEQ.toString(), formaAgendamento.getId().getPgdSeq()));

		return executeCriteria(criteria);
	}
	
	public Long obterQuantidadeAacNivelBuscaPorFormaAgendamento(AacFormaAgendamento formaAgendamento){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AacNivelBusca.class);
		criteria.add(Restrictions.eq(AacNivelBusca.Fields.FORMA_AGENDAMENTO.toString(), formaAgendamento));
		return executeCriteriaCount(criteria);
	}

	public Short buscaProximoSeqp(Short fagCaaSeq, Short fagTagSeq, Short fagPgdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacNivelBusca.class);
		criteria.setProjection(Projections.max(AacNivelBusca.Fields.SEQP.toString()));

		criteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_CAA_SEQ.toString(), fagCaaSeq));

		criteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_TAG_SEQ.toString(), fagTagSeq));

		criteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_PGD_SEQ.toString(), fagPgdSeq));

		Short result = (Short) executeCriteriaUniqueResult(criteria);
		if (result == null) {
			result = (short) 0;
		}
		result++;

		return result;
	}

	public Boolean existeNivelBuscaComTipoAgendamentoCount(
			AacTipoAgendamento tipoAgendamento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacNivelBusca.class);
		criteria.add(Restrictions.eq(
				AacNivelBusca.Fields.TIPO_AGENDAMENTO.toString(),
				tipoAgendamento));
		return executeCriteriaCount(criteria) > 0;
	}

	public Boolean existeNivelBuscaComPagadorCount(AacPagador pagador) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacNivelBusca.class);
		criteria.add(Restrictions.eq(AacNivelBusca.Fields.PAGADOR.toString(),
				pagador));
		return executeCriteriaCount(criteria) > 0;
	}
}
