package br.gov.mec.aghu.paciente.dao;

import javax.persistence.Query;

import br.gov.mec.aghu.model.temp.AipPosicaoFonemasMaePacienteRotinaFonema;

public class AipPosicaoFonemasMaePacienteRotinaFonemaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPosicaoFonemasMaePacienteRotinaFonema> {

	private static final long serialVersionUID = -8471751001795966406L;

	public void removerPosicaoFonemasMaePacienteRotinaFonema(Integer codigoPaciente) {
		Query _query = this
				.createQuery(
						"delete AipPosicaoFonemasMaePacienteRotinaFonema where id.seq in (select seq from AipFonemasMaePacienteRotinaFonema where aipPaciente.codigo = :codigo)");
		_query.setParameter("codigo", codigoPaciente);
		_query.executeUpdate();
	}

}
