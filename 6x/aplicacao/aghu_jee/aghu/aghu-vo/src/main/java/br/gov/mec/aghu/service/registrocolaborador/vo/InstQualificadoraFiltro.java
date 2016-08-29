package br.gov.mec.aghu.service.registrocolaborador.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import br.gov.mec.aghu.service.paginacao.PaginationInfo;

//import com.wordnik.swagger.annotations.ApiModel;
//import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Filtro para busca de Instituições Qualificadoras via web service
 * 
 * @author agerling
 * 
 */
@ApiModel(value = "InstQualificadoraFiltro", description = "Filtro para listagem de instituições qualificadoras" )
public class InstQualificadoraFiltro implements Serializable {

	private static final long serialVersionUID = -3042478342956249629L;

	@ApiModelProperty(value = "Código da Instituição Qualificadora")
	private Integer codigo;
	
	@ApiModelProperty(value = "Descrição da Instituição Qualificadora(Pesquisa será feita com LIKE)")
	private String descricao;
	
	@ApiModelProperty(value = "Instituição Interna")
	private boolean interno;
	
	@ApiModelProperty(value = "Usado pelo GPPG")
	private boolean usoGppg;
	
	@ApiModelProperty(value = "Atributos de Controle da Paginação")
	private PaginationInfo pagination;

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

	public boolean isUsoGppg() {
		return usoGppg;
	}

	public void setUsoGppg(boolean usoGppg) {
		this.usoGppg = usoGppg;
	}

	public PaginationInfo getPagination() {
		return pagination;
	}

	public void setPagination(PaginationInfo pagination) {
		this.pagination = pagination;
	}

}
