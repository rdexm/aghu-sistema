package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

public class RequisicaoMaterialItensVO implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6848506069154713437L;
	private Integer item;
	private Integer materialCod;
	private String nomeMaterial;
	private String unidade;
	private String endereco;
	private Integer quantSolic;
	private Integer quantEntr;
	private Integer ordemTela;
	private String mediaSemestre;
	private String mediaTrintaDias;
	private Double valorMaterial;
	private String observacao;
	private Boolean indCorrosivo;
	private Boolean indInflamavel;
	private Boolean indRadioativo;
	private Boolean indReativo;
	private Boolean indToxico;

	public Integer getItem() {
		return item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	public Integer getMaterialCod() {
		return materialCod;
	}

	public void setMaterialCod(Integer materialCod) {
		this.materialCod = materialCod;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public Integer getQuantSolic() {
		return quantSolic;
	}

	public void setQuantSolic(Integer quantSolic) {
		this.quantSolic = quantSolic;
	}

	public Integer getQuantEntr() {
		return quantEntr;
	}

	public void setQuantEntr(Integer quantEntr) {
		this.quantEntr = quantEntr;
	}

	public Integer getOrdemTela() {
		return ordemTela;
	}

	public void setOrdemTela(Integer ordemTela) {
		this.ordemTela = ordemTela;
	}

	public RequisicaoMaterialItensVO copiar() {
		try {
			return (RequisicaoMaterialItensVO) this.clone();
		} catch (CloneNotSupportedException e) {
			// engolir exceção nunca vai acontecer pois o bojeto é clonnable.
			return null;
		}
	}

	public String getMediaSemestre() {
		return mediaSemestre;
	}

	public void setMediaSemestre(String mediaSemestre) {
		this.mediaSemestre = mediaSemestre;
	}

	public String getMediaTrintaDias() {
		return mediaTrintaDias;
	}

	public void setMediaTrintaDias(String mediaTrintaDias) {
		this.mediaTrintaDias = mediaTrintaDias;
	}

	public Double getValorMaterial() {
		return valorMaterial;
	}

	public void setValorMaterial(Double valorMaterial) {
		this.valorMaterial = valorMaterial;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Boolean getIndCorrosivo() {
		return indCorrosivo;
	}

	public void setIndCorrosivo(Boolean indCorrosivo) {
		this.indCorrosivo = indCorrosivo;
	}

	public Boolean getIndRadioativo() {
		return indRadioativo;
	}

	public void setIndRadioativo(Boolean indRadioativo) {
		this.indRadioativo = indRadioativo;
	}

	public Boolean getIndReativo() {
		return indReativo;
	}

	public void setIndReativo(Boolean indReativo) {
		this.indReativo = indReativo;
	}

	public Boolean getIndToxico() {
		return indToxico;
	}

	public void setIndToxico(Boolean indToxico) {
		this.indToxico = indToxico;
	}

	public Boolean getIndInflamavel() {
		return indInflamavel;
	}

	public void setIndInflamavel(Boolean indInflamavel) {
		this.indInflamavel = indInflamavel;
	}
}