package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a ordenação do relatório Procedência Pacientes Internados
 * 
 * @author lalegre
 * 
 */
public enum DominioOrdenacaoProcedenciaPacientes implements Dominio {
	
	/**
	 * Cidade
	 **/
	C,
	/**
	 * Equipe
	 **/
	E,
	/**
	 * Unidade
	 **/
	U;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Cidade";

		case E:
			return "Equipe";
			
		case U:
			return "Unidade";
		
		default:
			return "";
		}
	}

}
