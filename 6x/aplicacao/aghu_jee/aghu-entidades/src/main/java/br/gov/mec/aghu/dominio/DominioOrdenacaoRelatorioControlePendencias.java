package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrdenacaoRelatorioControlePendencias implements Dominio {
	/*
	 * Nome do profissional
	 */
	NP,
	/*
	 * Matricula
	 */
	M,
	/*
	 * Vinculo
	 */
	V,
	/*
	 * Serviço
	 */
	S,
	/*
	 * Total de Documentos Pendentes
	 */
	TDP;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NP:
			return "Nome do Profissional";
		case M:
			return "Matrícula";
		case V:
			return "Vínculo";
		case S:
			return "Serviço";
		case TDP:
			return "Total de Documentos Pendentes";
		default:
			return "";
		}	
	}

}
