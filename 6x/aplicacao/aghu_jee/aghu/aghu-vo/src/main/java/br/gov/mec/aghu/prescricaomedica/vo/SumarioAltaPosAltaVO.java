package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.MpmAltaSumarioId;

public class SumarioAltaPosAltaVO implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -121673763866157258L;
	// Atributos para identificar o registro no banco
	private MpmAltaSumarioId id;
	private Short seqp;
	
	// Atributos para controlar o comportamento em tela
	private String descricao;
	private Boolean emEdicao;
	private Boolean origemListaCombo;
	private Integer indiceCombo;
	
	/**
	 * Construtor padrão.
	 */
	public SumarioAltaPosAltaVO(MpmAltaSumarioId id) {
		setId(id);
		setEmEdicao(Boolean.FALSE);
		setOrigemListaCombo(Boolean.FALSE);		
	}
	
	public MpmAltaSumarioId getId() {
		return this.id;
	}

	public void setId(MpmAltaSumarioId id) {
		this.id = id;
	}

	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getEmEdicao() {
		return this.emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public Boolean getOrigemListaCombo() {
		return this.origemListaCombo;
	}

	public void setOrigemListaCombo(Boolean origemListaCombo) {
		this.origemListaCombo = origemListaCombo;
	}

	public void setIndiceCombo(Integer indiceCombo) {
		this.indiceCombo = indiceCombo;
	}

	public Integer getIndiceCombo() {
		return this.indiceCombo;
	}
	
}