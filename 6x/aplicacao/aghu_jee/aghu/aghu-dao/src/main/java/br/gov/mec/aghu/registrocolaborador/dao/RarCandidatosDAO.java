package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.RarCandidatos;

public class RarCandidatosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RarCandidatos>{
	
	private static final long serialVersionUID = 393343672840235615L;

	protected RarCandidatosDAO() {
	}
	
	/**
	 * Retorna os candidatos de uma certa nacionalidade
	 * @param nacionalidade do candidato
	 * @return Total encontrado
	 */
	public Long pesquisarCandidatosPorNacionalidadeCount(AipNacionalidades nacionalidade){
		DetachedCriteria criteria = DetachedCriteria.forClass(RarCandidatos.class);
		criteria.add(Restrictions.eq(RarCandidatos.Fields.NACIONALIDADE.toString(), nacionalidade));
		return executeCriteriaCount(criteria);
	}
}
