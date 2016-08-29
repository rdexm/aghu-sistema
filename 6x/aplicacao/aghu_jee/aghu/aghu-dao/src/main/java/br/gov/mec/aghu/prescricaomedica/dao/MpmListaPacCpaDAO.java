package br.gov.mec.aghu.prescricaomedica.dao;

import br.gov.mec.aghu.model.MpmListaPacCpa;
import br.gov.mec.aghu.model.MpmListaPacCpaId;
import br.gov.mec.aghu.model.RapServidores;

/**
 * @see MpmListaPacCpa
 * 
 * @author cvagheti
 * 
 */
public class MpmListaPacCpaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmListaPacCpa> {

	private static final long serialVersionUID = -8808551964574650972L;

	/**
	 * Retorna pelo profissional fornecido.
	 * 
	 * @param servidor
	 *            configurado para
	 * @return
	 */
	public MpmListaPacCpa busca(final RapServidores servidor) {
		if (servidor == null || servidor.getId() == null) {
			throw new IllegalArgumentException(
					"O argumento e seu id são obrigatórios");
		}
		MpmListaPacCpaId id = new MpmListaPacCpaId(servidor.getId()
				.getMatricula(), servidor.getId().getVinCodigo());
		return busca(id);
	}

	/**
	 * Retorna pelo id.
	 * 
	 * @param id
	 * @return
	 */
	public MpmListaPacCpa busca(final MpmListaPacCpaId id) {
		if (id == null) {
			throw new IllegalArgumentException("Argumento obrigatório");
		}
		return this.findByPK(MpmListaPacCpa.class, id);
	}

	/**
	 * Retorna se pacientes em cuidados pós anestésicos devem ser mostrados na
	 * lista de pacientes do profissional fornecido.
	 * 
	 * @param servidor
	 *            configurado para
	 * @return se não houver registro retorna false.
	 */
	public boolean mostrarPacientesCpas(final RapServidores servidor) {
		MpmListaPacCpa cpas = this.busca(servidor);
		if (cpas == null) {
			return false;
		}
		return cpas.getIndPacCpa();
	}

	
}