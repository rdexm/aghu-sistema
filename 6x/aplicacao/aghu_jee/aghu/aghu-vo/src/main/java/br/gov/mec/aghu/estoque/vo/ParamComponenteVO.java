package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.model.AfaParamComponenteNpt;

public class ParamComponenteVO  {

	private AfaParamComponenteNpt param;
	private String ummDescricao;
	
	
	
	
	public AfaParamComponenteNpt getParam() {
		return param;
	}

	public void setParam(AfaParamComponenteNpt param) {
		this.param = param;
	}

	public String getUmmDescricao() {
		return ummDescricao;
	}
	
	public void setUmmDescricao(String ummDescricao) {
		this.ummDescricao = ummDescricao;
	}
	
}
