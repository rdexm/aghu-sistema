package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Classe que popula a lista de registros da grid na tela de Solicitação de Análise Técnica de Bem Permanente.
 */
public class AnaliseTecnicaBemPermanenteVO implements Serializable {
	
	private static final long serialVersionUID = 1883006711578621596L;

	private Integer recebimento;

	private Integer itemRecebimento;

	private Integer af;

	private Short complemento;

	private Integer nroSolicCompras;

	private Integer nroMaterial;

	private String material;

	private Integer centroCustoAutTec;

	private Integer esl;

	private Integer seqArea;

	private String nomeArea;

	private Integer matriculaServidor;

	private Integer afnNumero;
	
	private Boolean selecionado;

	public enum Fields {

		RECEBIMENTO("recebimento"),
		ITEM_RECEBIMENTO("itemRecebimento"),
		AF("af"),
		COMPLEMENTO("complemento"),
		NRO_SOLIC_COMPRAS("nroSolicCompras"),
		NRO_MATERIAL("nroMaterial"),
		MATERIAL("material"),
		CENTRO_CUSTO_AUT_TEC("centroCustoAutTec"),
		ESL("esl"),
		SEQ_AREA("seqArea"),
		NOME_AREA("nomeArea"),
		MATRICULA_SERVIDOR("matriculaServidor"),
		AFN_NUMERO("afnNumero");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	public Integer getRecebimento() {
		return recebimento;
	}

	public void setRecebimento(Integer recebimento) {
		this.recebimento = recebimento;
	}

	public Integer getItemRecebimento() {
		return itemRecebimento;
	}

	public void setItemRecebimento(Integer itemRecebimento) {
		this.itemRecebimento = itemRecebimento;
	}

	public Integer getAf() {
		return af;
	}

	public void setAf(Integer af) {
		this.af = af;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public Integer getNroSolicCompras() {
		return nroSolicCompras;
	}

	public void setNroSolicCompras(Integer nroSolicCompras) {
		this.nroSolicCompras = nroSolicCompras;
	}

	public Integer getNroMaterial() {
		return nroMaterial;
	}

	public void setNroMaterial(Integer nroMaterial) {
		this.nroMaterial = nroMaterial;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public Integer getCentroCustoAutTec() {
		return centroCustoAutTec;
	}

	public void setCentroCustoAutTec(Integer centroCustoAutTec) {
		this.centroCustoAutTec = centroCustoAutTec;
	}

	public Integer getEsl() {
		return esl;
	}

	public void setEsl(Integer esl) {
		this.esl = esl;
	}

	public Integer getSeqArea() {
		return seqArea;
	}

	public void setSeqArea(Integer seqArea) {
		this.seqArea = seqArea;
	}

	public String getNomeArea() {
		return nomeArea;
	}

	public void setNomeArea(String nomeArea) {
		this.nomeArea = nomeArea;
	}

	public Integer getMatriculaServidor() {
		return matriculaServidor;
	}

	public void setMatriculaServidor(Integer matriculaServidor) {
		this.matriculaServidor = matriculaServidor;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public int hashCode() {

		HashCodeBuilder builder = new HashCodeBuilder();
		
		builder.append(itemRecebimento);
		builder.append(recebimento);
		
		return builder.build();
	}

	@Override
	public boolean equals(Object obj) {

		EqualsBuilder builder = new EqualsBuilder();

		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		AnaliseTecnicaBemPermanenteVO other = (AnaliseTecnicaBemPermanenteVO) obj;

		builder.append(itemRecebimento, other.getItemRecebimento());
		builder.append(recebimento, other.getRecebimento());

		return builder.build();
	}

}
