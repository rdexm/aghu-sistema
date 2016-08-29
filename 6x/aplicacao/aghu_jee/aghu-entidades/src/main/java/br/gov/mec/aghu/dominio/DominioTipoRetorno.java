package br.gov.mec.aghu.dominio;

import java.util.List;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoRetorno implements Dominio{

	A,
	D,
	X;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Pré-Transplante";
		case D:
			return "Pós-Transplante";
		case X:
			return "Pré e Pós-Transplante";
		default:
			return "";
		}
	}
	
	public static DominioTipoRetorno getDominioSelecionado(List<String> lista){
		if(lista.contains(A.getDescricao()) && lista.contains(D.getDescricao())){
			return X;
		}else if(lista.contains(A.getDescricao())){
			return A;
		}else if(lista.contains(D.getDescricao())){
			return D;
		}
		return null;
	}
}
