package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioBoletimOcorrencias implements Dominio {
	
	C,
	E,
	G,
	H,
	L,
	P,
	S,
	X;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Devolução Confirmada";
		case E:
			return "Devolução Encerrada";
		case G:
			return "Devolução Pendente";
		case H:
			return "Devolução Cancelada";
		case L:
			return "L";
		case P:
			return "P";
		case S:
			return "S";	
		case X:
			return "X";			
		default:
			return "";
		}
	}
	
	/**
	 * Descrições para títulos bloqueados
	 * @return
	 */
	public String getDescricaoTituloBloqueado() {
		switch (this) {
		case G:
			return "Gerado";
		case C:
			return "Confirmado";
		case P:
			return "Pendente";
		case L:
			return "Liberado";
		case E:
			return "Encerrado";
		case X:
			return "Excluído";			
		case H:
			return "Cancelado";
		case S:
			return "Estornado";	
		default:
			return "";
		}
	}
	
	public static DominioBoletimOcorrencias getInstance(String valor){
		if (valor != null && valor.equalsIgnoreCase("C")){
			return DominioBoletimOcorrencias.C;
		}else{
			if (valor != null && valor.equalsIgnoreCase("E")){
				return DominioBoletimOcorrencias.E;
			}else{
				if (valor != null && valor.equalsIgnoreCase("G")){
					return DominioBoletimOcorrencias.G;
				}else{
					if (valor != null && valor.equalsIgnoreCase("H")){
						return DominioBoletimOcorrencias.H;
					}else{
						if (valor != null && valor.equalsIgnoreCase("L")){
							return DominioBoletimOcorrencias.L;
						}else{
							if (valor != null && valor.equalsIgnoreCase("P")){
								return DominioBoletimOcorrencias.P;
							}else{
								if (valor != null && valor.equalsIgnoreCase("S")){
									return DominioBoletimOcorrencias.S;
								}else{
									if (valor != null && valor.equalsIgnoreCase("X")){
										return DominioBoletimOcorrencias.X;
									}
								}
							}
						}
					}
				}
			}
		}
		
		return null;
		
	}


}
