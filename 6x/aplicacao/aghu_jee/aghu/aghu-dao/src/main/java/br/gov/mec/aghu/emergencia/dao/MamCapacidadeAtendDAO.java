package br.gov.mec.aghu.emergencia.dao;



import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamCapacidadeAtend;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamCapacidadeAtendDAO extends BaseDao<MamCapacidadeAtend> {

	private static final long serialVersionUID = -4032668150964974992L;
	
	private static final String MAM_CAPACIDADE_ATEND = "MamCapacidadeAtend.";

	public Short obterCapacidadeAtend(Short espSeq, Short qtdeAtendimento){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamCapacidadeAtend.class,"MamCapacidadeAtend");
		
		criteria.add(Restrictions.eq(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.le(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.QTDE_INICIAL_PAC.toString(), qtdeAtendimento));
		criteria.add(Restrictions.ge(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.QTDE_FINAL_PAC.toString(), qtdeAtendimento));
		
		criteria.setProjection(Projections.property(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.CAPACIDADE_ATEND.toString()));
		
		return (Short) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<MamCapacidadeAtend> pesquisarCapacidadesAtends(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short espSeq, DominioSituacao indSituacao) {
		final DetachedCriteria criteria = obterCriteriaPesquisarCapacidadesAtends(
				espSeq, indSituacao);
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarCapacidadesAtendsCount(Short espSeq, DominioSituacao indSituacao) {
		final DetachedCriteria criteria = obterCriteriaPesquisarCapacidadesAtends(
				espSeq, indSituacao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaPesquisarCapacidadesAtends(
			Short espSeq, DominioSituacao indSituacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamCapacidadeAtend.class,"CAT");
		criteria.createAlias("CAT." + MamCapacidadeAtend.Fields.EMG_ESPECIALIDADE.toString(), "EEP");
		if (espSeq != null) {
			criteria.add(Restrictions.eq("CAT." + MamCapacidadeAtend.Fields.ESP_SEQ.toString(), espSeq));
		}
		if (indSituacao != null) {
			criteria.add(Restrictions.eq("CAT." + MamCapacidadeAtend.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		return criteria;
	}
	
	public Boolean verificaExisteColisaoQtdPacientes(Short espSeq, Integer capacidadeSeq, Short qtdeInicialPac, Short qtdeFinalPac) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamCapacidadeAtend.class, "MamCapacidadeAtend");
		
		criteria.add(Restrictions.eq(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.ESP_SEQ.toString(), espSeq));
		//qtde_inicial_pac <= campo 14 and qtde_final_pac >= campo 14
		Criterion restricao1 = Restrictions.le(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.QTDE_INICIAL_PAC.toString(), qtdeInicialPac);
		Criterion restricao11 = Restrictions.ge(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.QTDE_FINAL_PAC.toString(), qtdeInicialPac);
		//qtde_inicial_pac <= campo 15 and qtde_final_pac >= campo 15  		
		Criterion restricao2 = Restrictions.le(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.QTDE_INICIAL_PAC.toString(), qtdeFinalPac);
		Criterion restricao22 = Restrictions.ge(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.QTDE_FINAL_PAC.toString(), qtdeFinalPac);
		//campo 14 <= qtde_inicial_pac and campo 15 >= qtde_inicial_pac
		Criterion restricao3 = Restrictions.ge(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.QTDE_INICIAL_PAC.toString(), qtdeInicialPac);
		Criterion restricao33 = Restrictions.le(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.QTDE_INICIAL_PAC.toString(), qtdeFinalPac);
		//campo 14 <= qtde_final_pac and campo 15 >= qtde_final_pac	
		Criterion restricao4 = Restrictions.ge(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.QTDE_FINAL_PAC.toString(), qtdeInicialPac);
		Criterion restricao44 = Restrictions.le(MAM_CAPACIDADE_ATEND + MamCapacidadeAtend.Fields.QTDE_FINAL_PAC.toString(), qtdeFinalPac);
	
		criteria.add(Restrictions.or(Restrictions.and(restricao1,restricao11),
				Restrictions.or(Restrictions.and(restricao2,restricao22), 
				Restrictions.or(Restrictions.and(restricao3,restricao33),
				Restrictions.and(restricao4,restricao44)))));
		
		if (capacidadeSeq != null) {
			criteria.add(Restrictions.ne(MamCapacidadeAtend.Fields.SEQ.toString(), capacidadeSeq));
		}
		return executeCriteriaCount(criteria) > 0;
	}
}