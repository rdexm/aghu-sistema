package br.gov.mec.aghu.dominio;

import java.util.List;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoMotivoAlteraSituacoes implements Dominio {

	M,
	O,
	T;
	
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
		case T:
			return "Todos";
		default:
			return "";
		}
	}
	
	public static DominioTipoMotivoAlteraSituacoes getInstance(List<String> listaTipoTransplanteSelecionado){
		
		if(contains(listaTipoTransplanteSelecionado, DominioTipoMotivoAlteraSituacoes.M) && contains(listaTipoTransplanteSelecionado, DominioTipoMotivoAlteraSituacoes.O)){
			return T;
		}else if(contains(listaTipoTransplanteSelecionado, DominioTipoMotivoAlteraSituacoes.M)){
			return M;
		}else if(contains(listaTipoTransplanteSelecionado, DominioTipoMotivoAlteraSituacoes.O)){
			return O;
		}
		return null;	
	}
	
	public static boolean contains(List<String> listaTipoTransplanteSelecionado, DominioTipoMotivoAlteraSituacoes tipo){
		
		for(String var : listaTipoTransplanteSelecionado){
			if(var.equals(tipo.getDescricao())){
				return true;
			}
				
		}
		return false;
		
	}

}
