package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCondicaoPaciente implements Dominio{
	CO,
	SI,
	SN,
	TP,
	VI,
	VN,
	VG;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case CO:
			return "Coma";
		case SI:
			return "Sono Induzido";
		case SN:
			return "Sono Natural";
		case TP:
			return "Torpor";
		case VI:
			return "Vig. e sono induzido";
		case VN:
			return "Vig. e sono natural";
		case VG:
			return "Vigília";	
		default:
			return "";
		}
	}
	
	

}
