package br.gov.mec.aghu.service.centrocusto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

//import com.wordnik.swagger.annotations.ApiModel;
//import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Filtro para busca de centros de custo através do web service.
 * 
 */

@ApiModel(value = "CentroCustoFiltro", description = "Filtro para pesquisas com centro de custo" )
public class CentroCustoFiltro implements Serializable {

	private static final long serialVersionUID = 1538215153012951637L;
	
	@ApiModelProperty(value = "Código do centro de custo", required=false)
	private Integer codigo;
	
	@ApiModelProperty(value = "Descrição do centro de custo", required=false)
	private String descricao;
	
	@ApiModelProperty(value = "Situação do centro de custo", required=false)
	private String situacao;

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

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

}
