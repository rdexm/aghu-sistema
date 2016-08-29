package br.gov.mec.aghu.service.registrocolaborador.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

//import com.wordnik.swagger.annotations.ApiModel;
//import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Classe utilizada para o retorno dos registros de Instituições Qualificadoras
 * Obtida através de web service
 */
@ApiModel(value = "InstQualificadora", description = "Retorno dos registros de Instituições Qualificadoras" )
public class InstQualificadora implements Serializable {

	private static final long serialVersionUID = 7568215153012951637L;
	
	@ApiModelProperty(value = "Código da Instituição Qualificadora", required=true)
	private Integer codigo;

	@ApiModelProperty(value = "Descrição da Instituição Qualificadora", required=true)
	private String descricao;

	@ApiModelProperty(value = "Status se é exclusiva do HCPA", required=true)
	private boolean interno;

	public InstQualificadora() {
	}

	public InstQualificadora(Integer codigo, String descricao,
			boolean indInterno) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.interno = indInterno;
	}

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

	public boolean isInterno() {
		return interno;
	}

	public void setInterno(boolean interno) {
		this.interno = interno;
	}
}