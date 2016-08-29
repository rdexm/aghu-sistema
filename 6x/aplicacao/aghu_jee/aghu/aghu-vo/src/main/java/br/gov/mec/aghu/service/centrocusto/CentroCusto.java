package br.gov.mec.aghu.service.centrocusto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//import com.wordnik.swagger.annotations.ApiModel;
//import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "CentroCusto", description = "Retorno da pesquisa com centro de custo" )
public class CentroCusto {

	@ApiModelProperty(value = "Código do centro de custo", required=true)
	private Integer codigo;
	
	@ApiModelProperty(value = "Descrição do centro de custo", required=true)
	private String descricao;
	
	@ApiModelProperty(value = "Nome reduzido do centro de custo", required=true)
	private String nomeReduzido;

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getNomeReduzido() {
		return nomeReduzido;
	}

	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}

}
