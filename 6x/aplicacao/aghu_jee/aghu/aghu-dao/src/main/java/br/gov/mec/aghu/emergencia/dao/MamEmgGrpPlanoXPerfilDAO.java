package br.gov.mec.aghu.emergencia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.model.MamEmgGrpPlanoXPerfil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamEmgGrpPlanoXPerfil
 * 
 * @author luismoura
 * 
 */
public class MamEmgGrpPlanoXPerfilDAO extends BaseDao<MamEmgGrpPlanoXPerfil> {
	private static final long serialVersionUID = 6585139836427020852L;

	/**
	 * Executa o exists da pesquisa de grupo de plano por perfil de emergência
	 * 
	 * C4 de #12173 - Manter cadastro de especialidades
	 * 
	 * @param eepEspSeq
	 * @return
	 */
	public Boolean pesquisarGrupoPlanoPerfilEmergenciaExists(Short eepEspSeq) {

		final DetachedCriteria criteria = this.montarPesquisaGrupoPlanoPerfilEmergencia(eepEspSeq);

		return this.executeCriteriaExists(criteria);
	}

	/**
	 * Monta a pesquisa de grupo de plano por perfil de especialidades de emergência
	 * 
	 * @param eepEspSeq
	 * @return
	 */
	private DetachedCriteria montarPesquisaGrupoPlanoPerfilEmergencia(Short eepEspSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgGrpPlanoXPerfil.class, "MamEmgGrpPlanoXPerfil");

		if (eepEspSeq != null) {
			criteria.createAlias("MamEmgGrpPlanoXPerfil." + MamEmgGrpPlanoXPerfil.Fields.MAM_EMG_ESPECIALIDADES.toString(), "MamEmgEspecialidades");
			criteria.add(Restrictions.eq("MamEmgEspecialidades." + MamEmgEspecialidades.Fields.ESP_SEQ.toString(), eepEspSeq));
		}

		return criteria;
	}
}
