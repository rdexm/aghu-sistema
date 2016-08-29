package br.gov.mec.aghu.emergencia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamEmgDestino;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamEmgDestino
 * 
 * @author luismoura
 * 
 */
public class MamEmgDestinoDAO extends BaseDao<MamEmgDestino> {
	private static final long serialVersionUID = -840024104684360665L;

	/**
	 * Executa o count da pesquisa de EmgDestino da situação de emergência pelo código da situação
	 * 
	 * C5 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param codigoSit
	 * @return
	 */
	public Long pesquisarEmgDestinoSituacaoEmergenciaCount(Short codigoSit) {

		final DetachedCriteria criteria = this.montarPesquisaEmgDestinoSituacaoEmergencia(codigoSit);

		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Monta a pesquisa de EmgDestino da situação de emergência pelo código da situação
	 * 
	 * C5 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param codigoSit
	 * @return
	 */
	private DetachedCriteria montarPesquisaEmgDestinoSituacaoEmergencia(Short codigoSit) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgDestino.class, "MamEmgDestino");

		if (codigoSit != null) {
			criteria.createAlias("MamEmgDestino." + MamEmgDestino.Fields.MAM_SITUACAO_EMERGENCIAS.toString(), "MamSituacaoEmergencia");
			criteria.add(Restrictions.eq("MamSituacaoEmergencia." + MamSituacaoEmergencia.Fields.SEQ.toString(), codigoSit));
		}

		return criteria;
	}

}