package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoAtividade implements Dominio {

	//Centros Cirúrgicos
	CC,
	//Consultorias
	CO,
	//Internação
	IN,
	//Ambulatórios
	AM;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CC:
			return "Centros Cirúrgicos";
		case CO:
			return "Consultorias";
		case IN:
			return "Internação";
		case AM:
			return "Ambulatórios";
		default:
			return "";
		}
	}

}
