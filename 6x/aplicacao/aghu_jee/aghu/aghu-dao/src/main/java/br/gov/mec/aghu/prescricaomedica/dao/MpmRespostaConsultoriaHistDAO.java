package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmRespostaConsultoriaHist;
import br.gov.mec.aghu.model.MpmTipoRespostaConsultoria;

public class MpmRespostaConsultoriaHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmRespostaConsultoriaHist> {

	private static final long serialVersionUID = 2096890911972162074L;

	private DetachedCriteria obterCriteriaRespostaSolicitacaoConsultoria(Integer idAtendimento, Integer seqConsultoria, Integer ordem) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmRespostaConsultoriaHist.class);

		criteria.createAlias(MpmRespostaConsultoriaHist.Fields.TIPO_RESPOSTA_CONSULTORIA.toString(), "TRC");

		criteria.add(Restrictions.eq(MpmRespostaConsultoriaHist.Fields.SCN_ATD_SEQ.toString(), idAtendimento));
		criteria.add(Restrictions.eq(MpmRespostaConsultoriaHist.Fields.SCN_SEQ.toString(), seqConsultoria));

		if (ordem != null && ordem.equals(1)) {
			criteria.addOrder(Order.desc(MpmRespostaConsultoriaHist.Fields.CRIADO_EM.toString()));
			criteria.addOrder(Order.asc("TRC." + MpmTipoRespostaConsultoria.Fields.ORDEM_VISUALIZACAO.toString()));
		} else if (ordem != null && ordem.equals(1)) {
			criteria.addOrder(Order.asc(MpmRespostaConsultoriaHist.Fields.CRIADO_EM.toString()));
			criteria.addOrder(Order.asc("TRC." + MpmTipoRespostaConsultoria.Fields.ORDEM_VISUALIZACAO.toString()));
		}

		return criteria;
	}

	public List<MpmRespostaConsultoriaHist> obterRespostasConsultoria(Integer idAtendimento, Integer seqConsultoria, Integer ordem) {
		DetachedCriteria criteria = obterCriteriaRespostaSolicitacaoConsultoria(idAtendimento, seqConsultoria, ordem);
		return executeCriteria(criteria);
	}
}
