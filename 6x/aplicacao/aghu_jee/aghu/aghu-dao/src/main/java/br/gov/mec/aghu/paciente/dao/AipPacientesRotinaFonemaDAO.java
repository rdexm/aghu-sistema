package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import javax.persistence.Query;

import br.gov.mec.aghu.model.temp.AipPacientesRotinaFonema;

public class AipPacientesRotinaFonemaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPacientesRotinaFonema> {

	private static final long serialVersionUID = 5691635359436762398L;

	public Long countPacientesRotinaFonema(Boolean somentePacientesSemFonemas) {
		StringBuilder hqlTotal = new StringBuilder(100);
		hqlTotal.append(" select count(pac) from AipPacientesRotinaFonema pac ");
		if (somentePacientesSemFonemas) {
			hqlTotal.append(" where pac.fonemas is empty ");
		}
		return (Long) this.createQuery(hqlTotal.toString()).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<AipPacientesRotinaFonema> listaPacientesRotinaFonema(Integer firstResult, Integer maxResults,
			Boolean somentePacientesSemFonemas) {
		StringBuilder hql = new StringBuilder(100);
		
		hql.append(" select distinct pac from AipPacientesRotinaFonema pac ");
		if (!somentePacientesSemFonemas) {
			hql.append(" where pac.codigo > :start and pac.codigo <= :end ");
		} else {
			hql.append(" where pac.fonemas is empty ");
		}

		Query query = this.createQuery(hql.toString());

		if (!somentePacientesSemFonemas) {
			query.setParameter("start", firstResult);
			query.setParameter("end", firstResult + maxResults);
		}

		return query.getResultList();
	}
	
	public Integer buscaProximoCodigoPacientesRotinaFonema(Integer firstResult) {
		Query _query = this.createQuery(
				"select min(p.codigo) from AipPacientesRotinaFonema p where p.codigo > :codigo");
		_query.setParameter("codigo", firstResult);

		return (Integer) _query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<AipPacientesRotinaFonema> listaPacientesRotinaFonema(Integer firstResult, Integer maxResults) {
		StringBuilder hql = new StringBuilder(200);
		hql.append(" select distinct pac from AipPacientesRotinaFonema pac ")
		.append(" 	join fetch pac.fonemas as f ")
		.append(" 	join fetch pac.fonemasMae as fm ")
		.append(" where pac.codigo > :start and pac.codigo <= :end ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("start", firstResult);
		query.setParameter("end", firstResult + maxResults);

		return query.getResultList();
	}
	
}
