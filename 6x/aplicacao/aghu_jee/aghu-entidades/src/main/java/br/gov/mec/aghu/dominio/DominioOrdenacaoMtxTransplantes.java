package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrdenacaoMtxTransplantes implements Dominio {
	
	PERMANENCIA,
	NOME,
	DATA_INGRESSO;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
	switch(this){
		case PERMANENCIA:
			return "PERMANCENCIA";
		case NOME:
			return "NOME";
		case DATA_INGRESSO:
			return "DATA_INGRESSO";
		
	}
			
		return null;
	}
	
	

}
