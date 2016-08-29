package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import javax.persistence.Query;

import br.gov.mec.aghu.model.AipPosicaoFonemasMaePaciente;

public class AipPosicaoFonemasMaePacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPosicaoFonemasMaePaciente> {

	private static final long serialVersionUID = -7307818359716747134L;

	@SuppressWarnings("unchecked")
	public List<AipPosicaoFonemasMaePaciente> listarPosicoesFonemaMaePaciente(Integer codigo) {
		Query query = this
				.createQuery(
						"select pf from AipPosicaoFonemasMaePaciente pf where id.seq in (select seq from AipFonemasMaePaciente where aipPaciente.codigo = :codigo)");
		query.setParameter("codigo", codigo);

		return query.getResultList();
	}

}
