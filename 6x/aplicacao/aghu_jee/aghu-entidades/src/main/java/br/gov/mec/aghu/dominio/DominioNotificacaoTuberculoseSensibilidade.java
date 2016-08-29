package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseSensibilidade implements Dominio {

	RESISTENTE_SOMENTE_A_ISONIAZIDA(1), // Resistente somente à isoniazida
	RESISTENTE_SOMENTE_A_RIFAMPICINA(2), // Resistente somente à rifampicina
	RESISTENTE_A_ISONIAZIDA_E_RIFAMPICINA(3), // Resistente à isoniazida e rifampicina
	RESISTENTE_A_OUTRAS_DROGAS_DE_1A_LINHA(4), // Resistente a outras drogas de 1a linha
	SENSIVEL(5), // Sensível
	EM_ANDAMENTO(6), // Em andamento
	NAO_REALIZADO(7); // Não realizado

	private int value;

	private DominioNotificacaoTuberculoseSensibilidade(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case RESISTENTE_SOMENTE_A_ISONIAZIDA:
			return "Resistente somente à isoniazida";
		case RESISTENTE_SOMENTE_A_RIFAMPICINA:
			return "Resistente somente à rifampicina";
		case RESISTENTE_A_ISONIAZIDA_E_RIFAMPICINA:
			return "Resistente à isoniazida e rifampicina";
		case RESISTENTE_A_OUTRAS_DROGAS_DE_1A_LINHA:
			return "Resistente a outras drogas de 1a linha";
		case SENSIVEL:
			return "Sensível";
		case EM_ANDAMENTO:
			return "Em andamento";
		case NAO_REALIZADO:
			return "Não realizado";
		default:
			return "";
		}
	}

}