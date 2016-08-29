package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author Rafael Nascimento
 * 
 */
public enum DominioCalculoDose implements Dominio {

	/** FIXO (Padrão) */
	F,
	/** MOSTELLER */
	M,
	/** DUBOIS */
	D,
	/** HAYCOCK */
	H,
	/** PESO */
	G,
	/** CALVERT */
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "Fixo";
		case M:
			return "Mosteller";
		case D:
			return "Dubois";
		case H:
			return "Haycock";
		case G:
			return "Peso";
		case C:
			return "Calvert";
		default:
			return "";
		}
	}

}
