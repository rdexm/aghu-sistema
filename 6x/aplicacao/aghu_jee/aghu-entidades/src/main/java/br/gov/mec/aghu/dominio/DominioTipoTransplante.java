package br.gov.mec.aghu.dominio;

import java.util.List;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoTransplante implements Dominio {
	M,
	O,
	X;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Medula Óssea";
		case O:
			return "Órgãos";
		case X:
			return "Medula Óssea / Órgãos";
		default:
			return "";
		}
	}
	
	public static DominioTipoTransplante getInstance(List<String> listaDomTipoTransplanteSelecionado){
		if(contains(listaDomTipoTransplanteSelecionado, DominioTipoTransplante.O) && contains(listaDomTipoTransplanteSelecionado, DominioTipoTransplante.M)){
			return X;
		}else if(contains(listaDomTipoTransplanteSelecionado, DominioTipoTransplante.O)){
			return O;
		}else if(contains(listaDomTipoTransplanteSelecionado, DominioTipoTransplante.M)){
			return M;
		}else{
			return null;
		}
	}
	
	public static boolean contains(List<String> listaDomTipoTransplanteSelecionado, DominioTipoTransplante value){
		for (int x = 0; x < listaDomTipoTransplanteSelecionado.size(); x++){
			if(listaDomTipoTransplanteSelecionado.get(x).equals(value.getDescricao())){
				return true;
			}
		}
		return false;
	}
}
