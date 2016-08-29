package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;
/*
 * #41792-Filtro
 */
public enum DominioOrdenacaoPesquisaSobrevidaTransplante implements Dominio {
	NOME,
	SOBREVIDA,
	DATA_TRANSPLANTE;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NOME:
			return "Nome";
		case SOBREVIDA:
			return "Sobrevida";
		case DATA_TRANSPLANTE:
			return "Data Transplante";
		default:
			return "";
		}
	}
}
