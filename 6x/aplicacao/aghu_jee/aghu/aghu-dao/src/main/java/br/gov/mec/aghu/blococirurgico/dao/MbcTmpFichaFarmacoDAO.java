package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MbcTmpFichaFarmaco;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MbcTmpFichaFarmacoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcTmpFichaFarmaco> {

	private static final long serialVersionUID = -6615615897525895151L;

	private void setProjectionAllAtributes(DetachedCriteria criteria) {
		Projection prof = Projections.projectionList()
									.add(Projections.property(MbcTmpFichaFarmaco.Fields.CELULA.toString())				, MbcTmpFichaFarmaco.Fields.CELULA.toString())
									.add(Projections.property(MbcTmpFichaFarmaco.Fields.CRIADO_EM.toString())			, MbcTmpFichaFarmaco.Fields.CRIADO_EM.toString())
									.add(Projections.property(MbcTmpFichaFarmaco.Fields.DESC_MEDICAMENTO.toString())	, MbcTmpFichaFarmaco.Fields.DESC_MEDICAMENTO.toString())
									.add(Projections.property(MbcTmpFichaFarmaco.Fields.DOSE_TOTAL.toString())			, MbcTmpFichaFarmaco.Fields.DOSE_TOTAL.toString())
									.add(Projections.property(MbcTmpFichaFarmaco.Fields.DTHR_OCORRENCIA.toString())		, MbcTmpFichaFarmaco.Fields.DTHR_OCORRENCIA.toString())
									.add(Projections.property(MbcTmpFichaFarmaco.Fields.FFA_SEQ.toString())				, MbcTmpFichaFarmaco.Fields.FFA_SEQ.toString())
									.add(Projections.property(MbcTmpFichaFarmaco.Fields.FIC_SEQ.toString())				, MbcTmpFichaFarmaco.Fields.FIC_SEQ.toString())
									.add(Projections.property(MbcTmpFichaFarmaco.Fields.ORDEM.toString())				, MbcTmpFichaFarmaco.Fields.ORDEM.toString())
									.add(Projections.property(MbcTmpFichaFarmaco.Fields.TEMPO_DECORRIDO.toString())		, MbcTmpFichaFarmaco.Fields.TEMPO_DECORRIDO.toString())
									.add(Projections.property(MbcTmpFichaFarmaco.Fields.ID_IDSESSAO.toString())			, MbcTmpFichaFarmaco.Fields.IDSESSAO.toString())
				;
		criteria.setProjection(prof);
		criteria.setResultTransformer(Transformers.aliasToBean(MbcTmpFichaFarmaco.class));
	}

	public List<MbcTmpFichaFarmaco> pesquisarMbcTmpFichaFarmacoByFichaAnestesiaESessao(
			Integer seqMbcFichaAnestesia, String vSessao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTmpFichaFarmaco.class, "mff");
		criteria.add(Restrictions.eq("mff." + MbcTmpFichaFarmaco.Fields.ID_IDSESSAO.toString(), vSessao));
		criteria.add(Restrictions.eq("mff." + MbcTmpFichaFarmaco.Fields.FIC_SEQ.toString(), seqMbcFichaAnestesia));
		setProjectionAllAtributes(criteria);
		criteria.addOrder(Order.asc(MbcTmpFichaFarmaco.Fields.ORDEM.toString()));

		return executeCriteria(criteria);
	}

	public void removerMbcTmpFichaFarmacoAnteriores(Integer qtdeDias, Boolean flush) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MbcTmpFichaFarmaco.class, "mff");
		setProjectionAllAtributes(criteria);
		Date sysdate = DateUtil.adicionaDias(new GregorianCalendar().getTime(),	qtdeDias * -1);
		criteria.add(Restrictions.ge("mff." + MbcTmpFichaFarmaco.Fields.CRIADO_EM.toString(), sysdate));

		List<MbcTmpFichaFarmaco> result = executeCriteria(criteria);
		for(MbcTmpFichaFarmaco f : result){
			remover(f);
		}
		if(flush){
			flush();
		}

	}

}
