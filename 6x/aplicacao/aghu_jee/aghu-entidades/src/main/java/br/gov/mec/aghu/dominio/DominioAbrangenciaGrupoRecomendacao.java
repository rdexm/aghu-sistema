package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domino para o campo abrangencia da classe GrupoRecomendacao.
 * 
 * @author rcorvalao
 *
 */
public enum DominioAbrangenciaGrupoRecomendacao implements Dominio {
	  A // Ambulatorio 
	, I // Internacao
	, S // Ambos
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case A:
				return "Ambulatório";
			case I:
				return "Internação";
			case S:
				return "Ambos";
			default:
				return "descrição não definida";
		}
	}

}
