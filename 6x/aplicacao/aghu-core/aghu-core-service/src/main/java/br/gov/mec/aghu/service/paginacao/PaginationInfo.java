package br.gov.mec.aghu.service.paginacao;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "PaginationInfo", description = "Atributos de Controle da Paginação" )
public class PaginationInfo implements Serializable {

	private static final long serialVersionUID = 7568215153012951637L;
	
	@ApiModelProperty(value = "Número do Registro do primeiro resultado da página", required=true)
	private Integer firstResult;

	@ApiModelProperty(value = "Quantidade máxima de registros por página", required=true)
	private Integer maxResults;

	@ApiModelProperty(value = "Propriedade para ordenar a consulta")
	private String orderProperty;
	
	@ApiModelProperty(value = "Ordem (asc ou desc) da ordenção da consulta", notes="o default é true")
	private boolean asc;
	
	@ApiModelProperty(value="Total de registros da pesquisa")
	private Long totalCount;
	
	public PaginationInfo() {
		this.asc = true;
	}

	public PaginationInfo(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Long totalCount) {
		this.firstResult = firstResult;
		this.maxResults = maxResults;
		this.orderProperty = orderProperty;
		this.asc = asc;
		this.totalCount = totalCount;
	}
	
	public Integer getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	public String getOrderProperty() {
		return orderProperty;
	}

	public void setOrderProperty(String orderProperty) {
		this.orderProperty = orderProperty;
	}

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
}