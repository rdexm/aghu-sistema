package br.gov.mec.aghu.service.paginacao;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "PaginatedResult", description = "Resultado paginado com links para página anterior e próxima página" )
public class PaginatedResult<T> implements Serializable {

	private static final long serialVersionUID = 7568215153012951637L;

	@ApiModelProperty(value = "Resultado da pesquisa", required=true)	
	private List<T> listaItens;
	
	@ApiModelProperty(value = "Atributos da página atual", required=true)
	private PaginationInfo paginationInfo;
	
	@ApiModelProperty(value = "Link para a próxima página", required=true)
	private String nextPageLink;
	
	@ApiModelProperty(value = "Link para a página anterior", required=true)
	private String previousPageLink;
	
	@ApiModelProperty(value = "Link para a primeira página", required=true)
	private String firstPageLink;
	
	@ApiModelProperty(value = "Link para a última página", required=true)
	private String lastPageLink;

	public PaginatedResult() {
		super();
	}
	
	public PaginatedResult(PaginationInfo paginationInfo) {
		super();
		this.paginationInfo = paginationInfo;
	}
	
	public List<T> getListaItens() {
		return listaItens;
	}

	public void setListaItens(List<T> listaItens) {
		this.listaItens = listaItens;
	}

	public PaginationInfo getPaginationInfo() {
		return paginationInfo;
	}

	public void setPaginationInfo(PaginationInfo paginationInfo) {
		this.paginationInfo = paginationInfo;
	}

	public String getNextPageLink() {
		return nextPageLink;
	}

	public void setNextPageLink(String nextPageLink) {
		this.nextPageLink = nextPageLink;
	}

	public String getPreviousPageLink() {
		return previousPageLink;
	}

	public void setPreviousPageLink(String previousPageLink) {
		this.previousPageLink = previousPageLink;
	}

	public String getFirstPageLink() {
		return firstPageLink;
	}

	public void setFirstPageLink(String firstPageLink) {
		this.firstPageLink = firstPageLink;
	}

	public String getLastPageLink() {
		return lastPageLink;
	}

	public void setLastPageLink(String lastPageLink) {
		this.lastPageLink = lastPageLink;
	}
}