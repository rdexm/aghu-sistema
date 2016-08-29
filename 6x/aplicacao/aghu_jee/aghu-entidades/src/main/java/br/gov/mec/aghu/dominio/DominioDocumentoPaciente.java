package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de documento do paciente
 */
public enum DominioDocumentoPaciente implements Dominio {

	/**
	 * Com prontuário
	 */
	CP,
	/**
	 * Sem prontuário
	 */
	SP,
	/**
	 * Com Raio X
	 */
	RX,
	/**
	 * Com prontuário e Raio X
	 **/
	PX;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	public String getDescricaoBanco() {
		switch (this) {
		case CP:
			return "Com prontuário";
		case SP:
			return "Sem prontuário";
		case RX:
			return "Com Raio X";
		case PX:
			return "Com prontuário e Raio X";
		default:
			return "";
		}
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CP:
			return "Com prontuário";
		case SP:
			return "Sem prontuário";
		case RX:
			return "Com Raio X";
		case PX:
			return "Com prontuário e Raio X";
		default:
			return "";
		}
	}

}
