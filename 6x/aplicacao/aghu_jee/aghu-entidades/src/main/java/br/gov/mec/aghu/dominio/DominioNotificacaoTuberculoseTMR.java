package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseTMR implements Dominio {

	DETECTAVEL_SENSIVEL_A_RIFAMPICINA(1), // Detectável sensível a rifampicina
	DETECTAVEL_RESISTENTE_A_RIFAMPICINA(2), // Detectável resistente a rifampicina
	NAO_DETECTAVEL(3), // Não detectável
	INCONCLUSIVO(4), // Inconclusivo
	NAO_REALIZADO(5); // Não realizado

	private int value;

	private DominioNotificacaoTuberculoseTMR(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case DETECTAVEL_SENSIVEL_A_RIFAMPICINA:
			return "Detectável sensível a rifampicina";
		case DETECTAVEL_RESISTENTE_A_RIFAMPICINA:
			return "Detectável resistente a rifampicina";
		case NAO_DETECTAVEL:
			return " Não detectável";
		case INCONCLUSIVO:
			return "Inconclusivo";
		case NAO_REALIZADO:
			return "Não realizado";
		default:
			return "";
		}
	}

}