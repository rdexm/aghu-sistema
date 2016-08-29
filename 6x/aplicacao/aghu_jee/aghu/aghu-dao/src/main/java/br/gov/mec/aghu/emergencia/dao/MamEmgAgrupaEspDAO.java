package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamEmgAgrupaEsp;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamEmgAgrupaEsp
 * 
 * @author luismoura
 * 
 */
public class MamEmgAgrupaEspDAO extends BaseDao<MamEmgAgrupaEsp> {
	private static final long serialVersionUID = 4098422379379254781L;

	/**
	 * Executa o exists da pesquisa de agrupamento de especialidades de emergência
	 * 
	 * C3 de #12173 - Manter cadastro de especialidades
	 * 
	 * @param eepEspSeq
	 * @return
	 */
	public Boolean pesquisarAgrupaEspEmergenciaExists(Short eepEspSeq) {

		final DetachedCriteria criteria = this.montarPesquisaAgrupaEspEmergencia(eepEspSeq);

		return this.executeCriteriaExists(criteria);
	}

	/**
	 * Monta a pesquisa de agrupamento de especialidades de emergência
	 * 
	 * @param eepEspSeq
	 * @return
	 */
	private DetachedCriteria montarPesquisaAgrupaEspEmergencia(Short eepEspSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgAgrupaEsp.class, "MamEmgAgrupaEsp");

		if (eepEspSeq != null) {
			criteria.add(Restrictions.eq(MamEmgAgrupaEsp.Fields.EEP_ESP_SEQ.toString(), eepEspSeq));
		}

		return criteria;
	}

	/**
	 * Executa a pesquisa de situações de emergência
	 * 
	 * @param codigo
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<MamEmgAgrupaEsp> pesquisarAgrupamentoEspecialidadeEmergencia(Short codigo, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		final DetachedCriteria criteria = this.montarPesquisaAgrupamentoEspecialidadeEmergencia(codigo);

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Executa a pesquisa de situações de emergência
	 * 
	 * @param codigo
	 * @return
	 */
	public List<MamEmgAgrupaEsp> pesquisarAgrupamentoEspecialidadeEmergencia(Short codigo) {

		final DetachedCriteria criteria = this.montarPesquisaAgrupamentoEspecialidadeEmergencia(codigo);

		return this.executeCriteria(criteria);
	}

	public List<Short> pesquisarSeqsAgrupamentoEspecialidadeEmergencia(Short codigo) {
		final DetachedCriteria criteria = this.montarPesquisaAgrupamentoEspecialidadeEmergencia(codigo);
		criteria.setProjection(Projections.property(MamEmgAgrupaEsp.Fields.ESP_SEQ.toString()));
		return this.executeCriteria(criteria);
	}

	/**
	 * Monta a pesquisa de especialidades de emergência
	 * 
	 * @param codigo
	 * @return
	 */
	private DetachedCriteria montarPesquisaAgrupamentoEspecialidadeEmergencia(Short codigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgAgrupaEsp.class, "MamEmgAgrupaEsp");
		if (codigo != null) {
			criteria.add(Restrictions.eq(MamEmgAgrupaEsp.Fields.EEP_ESP_SEQ.toString(), codigo));
		}
		return criteria;
	}
}
