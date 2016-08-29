package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import javax.persistence.Query;

import br.gov.mec.aghu.model.AipPosicaoFonemasPacientes;

public class AipPosicaoFonemasPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPosicaoFonemasPacientes> {

	private static final long serialVersionUID = 6119600698026332866L;

	@SuppressWarnings("unchecked")
	public List<AipPosicaoFonemasPacientes> listarPosicoesFonemaPaciente(Integer codigoPaciente) {
		Query query = this
				.createQuery("select pf from AipPosicaoFonemasPacientes pf where id.seq in (select seq from AipFonemaPacientes where aipPaciente.codigo = :codigo)");
		query.setParameter("codigo", codigoPaciente);
		
		return query
				.getResultList();
	}
	
}
