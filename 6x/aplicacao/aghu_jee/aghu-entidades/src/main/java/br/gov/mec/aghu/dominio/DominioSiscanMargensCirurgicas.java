package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio contendo os códigos dos campos relacionados a Exame Histopatológico
 * de Colo- SISCAN.
 * 
 * @author ghernandez
 * 
 */
public enum DominioSiscanMargensCirurgicas implements Dominio {
	
	LIVRES(1), COMPROMETIDAS(2), IMPOSSIVEL(3);

	private int codigo;

	private DominioSiscanMargensCirurgicas(int codigo) {
		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {
		return codigo;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case LIVRES:
			return "Livres";
		case COMPROMETIDAS:
			return "Comprometidas";
		case IMPOSSIVEL:
			return "Impossível de serem avaliadas";
		default:
			return "";
		}

	}
	
	public static DominioSiscanMargensCirurgicas getInstance(int codigo){
		switch (codigo) {
		case 1:
			return LIVRES;
		case 2:
			return COMPROMETIDAS;
		case 3:
			return IMPOSSIVEL;
		default:
			return null;
		}
	}

}
