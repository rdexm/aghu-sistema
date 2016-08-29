package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domino para o campo responsavel da classe GrupoRecomendacao.
 * 
 * @author rcorvalao
 *
 */
public enum DominioResponsavelGrupoRecomendacao implements Dominio {
	 S // Solicitante
	, C // Coletador
	, P // Paciente
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case C:
				return "Coletador";
			case P:
				return "Paciente";
			case S:
				return "Solicitante";
			default:
				return "Descrição não definida";
		}
	}

}
