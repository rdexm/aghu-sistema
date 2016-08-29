package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioDistribuicaoMicrocalcificacaoMamografia implements Dominio {
	
	/*1 – Agrupada
	2 – Segmento Mamário
	3 – Trajeto Ductal*/
	
	AGRUPADA(1),
	SEGMENTO_MAMARIO(2),
	TRAJETO_DUCTAL(3);
	
	private final int valor;
	
	/**
	 * 
	 * @param valor
	 */
	private DominioDistribuicaoMicrocalcificacaoMamografia(final int valor) {
		this.valor = valor;
	}

	@Override
	public int getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case AGRUPADA:
				return "Agrupada";	
			case SEGMENTO_MAMARIO:
				return "Segmento Mamário";
			case TRAJETO_DUCTAL:
				return "Trajeto Ductal";					
			default:
				return "";
		}
	}
	
	public static DominioDistribuicaoMicrocalcificacaoMamografia getDominioPorCodigo(int codigo){
		for (DominioDistribuicaoMicrocalcificacaoMamografia dominio : DominioDistribuicaoMicrocalcificacaoMamografia.values()) {
			if (codigo == dominio.getCodigo()){
				return dominio;
			}
		}
		return null;
	}	
}
