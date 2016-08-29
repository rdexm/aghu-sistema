package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamEmgServEspCoop;
import br.gov.mec.aghu.model.MamEmgServEspecialidade;
import br.gov.mec.aghu.model.MamTipoCooperacao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamEmgServEspCoop
 * 
 * @author luismoura
 * 
 */
public class MamEmgServEspCoopDAO extends BaseDao<MamEmgServEspCoop> {
	private static final long serialVersionUID = -2352214897833557000L;

	public List<MamEmgServEspCoop> pesquisarPorMamEmgServEspecialidade(Integer eseSeq, Short eepEspSeq) {
		final DetachedCriteria criteria = this.montarPesquisaPorMamEmgServEspecialidade(eseSeq, eepEspSeq);
		return executeCriteria(criteria);
	}

	public Long pesquisarPorMamEmgServEspecialidadeCount(Integer eseSeq, Short eepEspSeq) {
		final DetachedCriteria criteria = this.montarPesquisaPorMamEmgServEspecialidade(eseSeq, eepEspSeq);
		return executeCriteriaCount(criteria);
	}

	public Boolean pesquisarPorMamEmgServEspecialidadeMamTipoCooperacaoExists(Integer eseSeq, Short eepEspSeq, Short tcoSeq) {
		final DetachedCriteria criteria = this.montarPesquisaPorMamEmgServEspecialidade(eseSeq, eepEspSeq);
		criteria.add(Restrictions.eq("MamTipoCooperacao." + MamTipoCooperacao.Fields.SEQ.toString(), tcoSeq));
		return executeCriteriaExists(criteria);
	}

	private DetachedCriteria montarPesquisaPorMamEmgServEspecialidade(Integer eseSeq, Short eepEspSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgServEspCoop.class, "MamEmgServEspCoop");
		criteria.createAlias(MamEmgServEspCoop.Fields.MAM_EMG_SERV_ESPECIALIDADES.toString(), "MamEmgServEspecialidade");
		criteria.createAlias(MamEmgServEspCoop.Fields.MAM_TIPO_COOPERACOES.toString(), "MamTipoCooperacao");
		criteria.add(Restrictions.eq("MamEmgServEspecialidade." + MamEmgServEspecialidade.Fields.ID_ESE_SEQ.toString(), eseSeq));
		criteria.add(Restrictions.eq("MamEmgServEspecialidade." + MamEmgServEspecialidade.Fields.ID_EEP_ESP_SEQ.toString(), eepEspSeq));
		return criteria;
	}
}
