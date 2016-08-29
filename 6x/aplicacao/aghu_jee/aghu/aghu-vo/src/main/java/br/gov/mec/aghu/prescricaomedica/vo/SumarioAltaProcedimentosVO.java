package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.gov.mec.aghu.model.MpmAltaSumarioId;

public class SumarioAltaProcedimentosVO implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6011792138155197234L;
	// Atributos para identificar o registro no banco
	private MpmAltaSumarioId id;
	private Short seqp;
	
	// Atributos para controlar o comportamento em tela
	private Date data;	
	private String descricao;
	private Boolean emEdicao;
	private Boolean origemListaCombo;
	private Integer indiceCombo;
	
	/**
	 * Construtor padrão.
	 */
	public SumarioAltaProcedimentosVO(MpmAltaSumarioId id) {
		this.setId(id);
		this.setEmEdicao(Boolean.FALSE);
		this.setOrigemListaCombo(Boolean.FALSE);		
	}
	
	public String getDescricaoFormatada() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return (this.getData() != null ? df.format(this.getData()) + " - " : "") + this.getDescricao();
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

	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
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