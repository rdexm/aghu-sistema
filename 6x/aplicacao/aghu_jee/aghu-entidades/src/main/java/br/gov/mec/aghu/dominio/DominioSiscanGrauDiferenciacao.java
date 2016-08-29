package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio contendo os códigos dos campos relacionados 
 * a Exame Histopatológico de Colo- SISCAN. 
 * 
 * @author ghernandez
 *
 */
public enum DominioSiscanGrauDiferenciacao implements Dominio{
	
	NAO_SE_APLICA(1),
	BEM_DIFERENCIADO(2),
	MODERADAMENTE_DIFERENCIADO(3),
	POUCO_DIFERENCIADO(4),
	INDIFERENCIADO(5);
	
	private int codigo;

	private DominioSiscanGrauDiferenciacao(int codigo) {
		this.codigo = codigo;
	}
	

	@Override
	public int getCodigo() {
		return codigo;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NAO_SE_APLICA:
			return "Não se aplica";
		case BEM_DIFERENCIADO:
			return "Bem diferenciado (Grau I)";
		case MODERADAMENTE_DIFERENCIADO:
			return "Moderadamente diferenciado (Grau II)";
		case POUCO_DIFERENCIADO:
			return "Pouco Diferenciado (Grau III)";
		case INDIFERENCIADO:
			return "Indiferenciado (Grau IV)";
		default:
			return "";
		}
	}
	
	public static DominioSiscanGrauDiferenciacao getInstance(int codigo){
		switch (codigo) {
		case 1:
			return NAO_SE_APLICA;
		case 2:
			return BEM_DIFERENCIADO;
		case 3:
			return MODERADAMENTE_DIFERENCIADO;
		case 4:
			return POUCO_DIFERENCIADO;
		case 5:
			return INDIFERENCIADO;
		default:
			return null;
		}
	}

}
