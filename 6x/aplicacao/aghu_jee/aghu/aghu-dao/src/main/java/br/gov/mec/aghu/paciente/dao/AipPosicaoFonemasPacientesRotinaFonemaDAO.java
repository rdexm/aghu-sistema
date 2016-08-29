package br.gov.mec.aghu.paciente.dao;

import javax.persistence.Query;

import br.gov.mec.aghu.model.temp.AipPosicaoFonemasPacientesRotinaFonema;

public class AipPosicaoFonemasPacientesRotinaFonemaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPosicaoFonemasPacientesRotinaFonema> {

	private static final long serialVersionUID = -6502936435133408443L;

	public void removerPosicoesFonemasPacientesRotinaFonema(Integer codigoPaciente) {
		Query _query = this
				.createQuery(
						"delete AipPosicaoFonemasPacientesRotinaFonema where id.seq in (select seq from AipFonemaPacientesRotinaFonema where aipPaciente.codigo = :codigo)");
		_query.setParameter("codigo", codigoPaciente);
		_query.executeUpdate();
	}

}
