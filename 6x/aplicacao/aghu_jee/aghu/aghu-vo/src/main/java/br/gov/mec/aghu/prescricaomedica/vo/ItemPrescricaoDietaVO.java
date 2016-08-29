package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;

/**
 * 
 * @author cvagheti
 * 
 */

public class ItemPrescricaoDietaVO implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 902372707604123071L;
	// chave composta de MpmItemPrescricaoDieta
	private Integer pdtAtdSeq;
	private Long pdtSeq;
	private Integer tidSeq;

	private AnuTipoItemDieta tipoItem;
	private BigDecimal quantidade;
	private String unidade;
	private Short frequencia;
	private MpmTipoFrequenciaAprazamento tipoAprazamento;
	
	private Byte numVezes;

	private MpmItemPrescricaoDieta itemPrescricaoDieta;

	private boolean edicao;
	private boolean persistido;

	public ItemPrescricaoDietaVO() {

	}

	public ItemPrescricaoDietaVO(MpmItemPrescricaoDieta item) {
		this.pdtAtdSeq = item.getId().getPdtAtdSeq();
		this.pdtSeq = item.getId().getPdtSeq();
		this.tidSeq = item.getId().getTidSeq();
		//
		this.tipoItem = item.getTipoItemDieta();
		this.quantidade = item.getQuantidade();
		if (item.getTipoItemDieta() != null
				&& item.getTipoItemDieta().getUnidadeMedidaMedica() != null) {
			this.unidade = item.getTipoItemDieta().getUnidadeMedidaMedica()
					.getDescricao();

		}
		this.frequencia = item.getFrequencia();
		this.tipoAprazamento = item.getTipoFreqAprazamento();
		this.numVezes = item.getNumVezes();
		this.itemPrescricaoDieta = item;
	}

	public Integer getPdtAtdSeq() {
		return pdtAtdSeq;
	}

	public void setPdtAtdSeq(Integer pdtAtdSeq) {
		this.pdtAtdSeq = pdtAtdSeq;
	}

	public Long getPdtSeq() {
		return pdtSeq;
	}

	public void setPdtSeq(Long pdtSeq) {
		this.pdtSeq = pdtSeq;
	}

	public Integer getTidSeq() {
		return tidSeq;
	}

	public void setTidSeq(Integer tidSeq) {
		this.tidSeq = tidSeq;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public Byte getNumVezes() {
		return numVezes;
	}

	public void setNumVezes(Byte numVezes) {
		this.numVezes = numVezes;
	}

	public AnuTipoItemDieta getTipoItem() {
		return tipoItem;
	}

	public void setTipoItem(AnuTipoItemDieta tipoItem) {
		this.tipoItem = tipoItem;
	}

	public MpmItemPrescricaoDieta getItemPrescricaoDieta() {
		return itemPrescricaoDieta;
	}

	public void setItemPrescricaoDieta(
			MpmItemPrescricaoDieta itemPrescricaoDieta) {
		this.itemPrescricaoDieta = itemPrescricaoDieta;
	}

	/**
	 * Retorna a descrição editada do item de prescrição.
	 * 
	 * @return
	 */
	public String getDescricao() {
		if (this.itemPrescricaoDieta != null) {
			return this.itemPrescricaoDieta.getDescricaoFormatada();
		}
		return null;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public boolean isPersistido() {
		return persistido;
	}

	public void setPersistido(boolean persistido) {
		this.persistido = persistido;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.pdtAtdSeq).append(this.pdtSeq)
				.append(this.tidSeq).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ItemPrescricaoDietaVO)) {
			return false;
		}
		ItemPrescricaoDietaVO castOther = (ItemPrescricaoDietaVO) other;
		return new EqualsBuilder().append(this.pdtAtdSeq,
				castOther.getPdtAtdSeq()).append(this.pdtSeq,
				castOther.getPdtSeq()).append(this.tidSeq,
				castOther.getTidSeq()).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("pdtAtdSeq", this.pdtAtdSeq)
				.append("pdtSeq", this.pdtSeq).append("tidSeq", this.tidSeq)
				.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
