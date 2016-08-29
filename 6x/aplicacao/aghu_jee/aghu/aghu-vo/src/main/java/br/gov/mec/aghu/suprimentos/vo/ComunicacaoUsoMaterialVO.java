package br.gov.mec.aghu.suprimentos.vo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ComunicacaoUsoMaterialVO implements BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4315993867315532420L;
	private Integer numero;
	private Integer item;
	private Integer quantidade;
	private String paciente;
	private String nomePAC1;
	private String nomePAC2;
	private String nomePAC3;
	private Integer peaSeq;
	private Integer peaParcela;
	private boolean selecionado = false;
	
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public String getPaciente() {
		StringBuilder nomePaciente = new StringBuilder();
		if(getNomePAC1()!=null){
			nomePaciente.append(getNomePAC1()+StringUtils.EMPTY);
		}
		if(getNomePAC2() != null){
			nomePaciente.append(getNomePAC2()+StringUtils.EMPTY);
		}
		if(getNomePAC3() != null){
			nomePaciente.append(getNomePAC3());
		}
		paciente = nomePaciente.toString();
		return paciente;
	}
	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}
	public String getNomePAC1() {
		return nomePAC1;
	}
	public void setNomePAC1(String nomePAC1) {
		this.nomePAC1 = nomePAC1;
	}
	public String getNomePAC2() {
		return nomePAC2;
	}
	public void setNomePAC2(String nomePAC2) {
		this.nomePAC2 = nomePAC2;
	}
	public String getNomePAC3() {
		return nomePAC3;
	}
	public void setNomePAC3(String nomePAC3) {
		this.nomePAC3 = nomePAC3;
	}
	public Integer getPeaSeq() {
		return peaSeq;
	}
	public void setPeaSeq(Integer peaSeq) {
		this.peaSeq = peaSeq;
	}
	public Integer getPeaParcela() {
		return peaParcela;
	}
	public void setPeaParcela(Integer peaParcela) {
		this.peaParcela = peaParcela;
	}
	
	public enum Fields {
		NUMERO("numero"),
		QUANTIDADE("quantidade"),
		NOME_PAC1("nomePAC1"),
		NOME_PAC2("nomePAC2"),
		NOME_PAC3("nomePAC3"),
		PEA_SEQ("peaSeq"),
		PEA_PARCELA("peaParcela");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public boolean isSelecionado() {
		return selecionado;
	}
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	public Integer getItem() {
		return item;
	}
	public void setItem(Integer item) {
		this.item = item;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getNumero());
		umHashCodeBuilder.append(this.getQuantidade());
		umHashCodeBuilder.append(this.getPaciente());
        return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ComunicacaoUsoMaterialVO other = (ComunicacaoUsoMaterialVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getNumero(), other.getNumero());
        umEqualsBuilder.append(this.getQuantidade(), other.getQuantidade());
        umEqualsBuilder.append(this.getPaciente(), other.getPaciente());
        return umEqualsBuilder.isEquals();
	}
}
