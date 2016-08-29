package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioStatusNotificacaoTecnica implements Dominio {

	INS(1),	
	TRT(2),
	TRO(3),
	MON(4),
	ACE(5),
	ENT(6),
	AGE(7),
	OUT(8);
	
	private int value;
	
	DominioStatusNotificacaoTecnica(int codigo){
		this.value=codigo;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {		
		switch (this) {		
			case INS:
				return "Instalação";
			case TRT:
				return "Treinamento Técnico";
			case TRO:
				return "Treinamento Operacional";
			case MON:
				return "Montagem";
			case ACE:
				return "Acessórios";
			case ENT:
				return "Entrada Técnica";
			case AGE:
				return "Agendamento com Fornecedor ";
			case OUT:
				return "Outros";
			default:
				return "";
		}
	}
	
	public static String obterDominioStatusNotificacaoTecnica(Integer value) {
		switch (value) {
		case 1:
			return DominioStatusNotificacaoTecnica.INS.getDescricao();			
		case 2:
			return DominioStatusNotificacaoTecnica.TRT.getDescricao();			
		case 3:
			return DominioStatusNotificacaoTecnica.TRO.getDescricao();
		case 4:
			return DominioStatusNotificacaoTecnica.MON.getDescricao();
		case 5:
			return DominioStatusNotificacaoTecnica.ACE.getDescricao();
		case 6:
			return DominioStatusNotificacaoTecnica.ENT.getDescricao();
		case 7:
			return DominioStatusNotificacaoTecnica.AGE.getDescricao();
		case 8:
			return DominioStatusNotificacaoTecnica.OUT.getDescricao();
		default:
			return null;
		}
	}
	
	
}