package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.model.MamEmgServEspecialidade;
import br.gov.mec.aghu.model.MamEmgServidor;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamEmgServEspecialidade
 * 
 * @author luismoura
 * 
 */
public class MamEmgServEspecialidadeDAO extends BaseDao<MamEmgServEspecialidade> {
	
	private static final long serialVersionUID = 6585139836427020852L;

	public List<MamEmgServEspecialidade> pesquisarServidorEspecialidadePorRapServidor(RapServidores servidor) {
		
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "SEE");
		
		dc.createAlias("SEE.".concat(MamEmgServEspecialidade.Fields.MAM_EMG_SERVIDORES.toString()), "ESE");		
		dc.add(Restrictions.eq("ESE.".concat(MamEmgServidor.Fields.RAP_SERVIDORES_BY_MAM_ESE_SER_FK1.toString()), servidor));
		
		return executeCriteria(dc);
	}	
	
	/**
	 * Executa o exists da pesquisa de serv de especialidade de emergência
	 * 
	 * C5 de #12173 - Manter cadastro de especialidades
	 * 
	 * @param eepEspSeq
	 * @return
	 */
	public Boolean pesquisarServEspecialidadeEmergenciaExists(Short eepEspSeq) {

		final DetachedCriteria criteria = this.montarPesquisaServEspecialidadeEmergencia(eepEspSeq, null);

		return this.executeCriteriaExists(criteria);
	}

	/**
	 * Executa o exists da pesquisa de serv de especialidade de emergência
	 * 
	 * C10 de #12174 - Manter cadastro de servidores
	 * 
	 * @param eepEspSeq
	 * @return
	 */
	public Boolean pesquisarServEspecialidadeEmergenciaExists(Integer eseSeq) {

		final DetachedCriteria criteria = this.montarPesquisaServEspecialidadeEmergencia(null, eseSeq);

		return this.executeCriteriaExists(criteria);
	}
	
	/**
	 * Executa a pesquisa de serv de especialidade de emergência
	 * 
	 * C4 de #12174 - Manter cadastro de servidores
	 * 
	 * @param eseSeq
	 * @return
	 */
	public List<MamEmgServEspecialidade> pesquisarServEspecialidadeEmergencia(Integer eseSeq) {

		final DetachedCriteria criteria = this.montarPesquisaServEspecialidadeEmergencia(null, eseSeq);

		return this.executeCriteria(criteria);
	}

	/**
	 * Monta a pesquisa de serv de especialidade de emergência
	 * 
	 * @param eepEspSeq
	 * @param eseSeq
	 * @return
	 */
	private DetachedCriteria montarPesquisaServEspecialidadeEmergencia(Short eepEspSeq, Integer eseSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgServEspecialidade.class, "MamEmgServEspecialidade");

		if (eepEspSeq != null) {
			criteria.createAlias("MamEmgServEspecialidade." + MamEmgServEspecialidade.Fields.MAM_EMG_ESPECIALIDADES.toString(), "MamEmgEspecialidades");
			criteria.add(Restrictions.eq("MamEmgEspecialidades." + MamEmgEspecialidades.Fields.ESP_SEQ.toString(), eepEspSeq));
		}

		if (eseSeq != null) {
			criteria.createAlias("MamEmgServEspecialidade." + MamEmgServEspecialidade.Fields.MAM_EMG_SERVIDORES.toString(), "MamEmgServidor");
			criteria.add(Restrictions.eq("MamEmgServidor." + MamEmgServidor.Fields.SEQ.toString(), eseSeq));
		}

		return criteria;
	}
}
