package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioValorDataItemSumario implements DominioString {

	POSITIVO_UM("1"),
	POSITIVO_DOIS("2"),
	S("S"),
	P("P");
	
	private String value;
	
	private DominioValorDataItemSumario(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		return value;
	}
	
	public static DominioValorDataItemSumario getDominioByValor(String valor){
		if(POSITIVO_UM.getDescricao().equals(valor)){
			return POSITIVO_UM;
		}else if(POSITIVO_DOIS.getDescricao().equals(valor)){
			return POSITIVO_DOIS;
		}else if(S.getDescricao().equals(valor)){
			return S;
		}else if(P.getDescricao().equals(valor)){
			return P;
		} 
		
		return null;
	}

}