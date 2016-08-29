package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioFormaMicrocalcificacaoMamografia implements Dominio {
	
	/*1 – Arredondada
	2 – Puntiformes
	3 – Irregulares
	4 – Ramificada*/
	
	ARREDONDADA(1),
	PUNTIFORMES(2),
	IRREGULARES(3),
	RAMIFICADA(4);
	
	private final int valor;
	
	/**
	 * 
	 * @param valor
	 */
	private DominioFormaMicrocalcificacaoMamografia(final int valor) {
		this.valor = valor;
	}

	@Override
	public int getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case ARREDONDADA:
				return "Arredondada";	
			case PUNTIFORMES:
				return "Puntiformes";
			case IRREGULARES:
				return "Irregulares";				
			case RAMIFICADA:
				return "Ramificada";	
			default:
				return "";
		}
	}
	
	public static DominioFormaMicrocalcificacaoMamografia getDominioPorCodigo(int codigo){
		for (DominioFormaMicrocalcificacaoMamografia dominio : DominioFormaMicrocalcificacaoMamografia.values()) {
			if (codigo == dominio.getCodigo()){
				return dominio;
			}
		}
		return null;
	}	
}
