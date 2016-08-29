package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ParecerPendenteVO implements Serializable {

	private static final long serialVersionUID = -1429840472621177235L;
	
	private BigDecimal seq;

	private String medicamento;

	private BigDecimal dose;

	private String aprazamento;

	private String viaAdministracao;

	private String descricao;

	public String getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}

	public BigDecimal getDose() {
		return dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}

	public String getAprazamento() {
		return aprazamento;
	}

	public void setAprazamento(String aprazamento) {
		this.aprazamento = aprazamento;
	}

	public String getViaAdministracao() {
		return viaAdministracao;
	}

	public void setViaAdministracao(String viaAdministracao) {
		this.viaAdministracao = viaAdministracao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public enum Fields {

		SEQ("seq"),
		MEDICAMENTO("medicamento"),
		DOSE("dose"),
		APRAZAMENTO("aprazamento"),
		VIA_ADMINISTRACAO("viaAdministracao"),
		DESCRICAO("descricao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@Override
	public int hashCode() {

		HashCodeBuilder hc = new HashCodeBuilder();

		hc.append(aprazamento);
		hc.append(descricao);
		hc.append(dose);
		hc.append(medicamento);
		hc.append(viaAdministracao);

		return hc.build();
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		ParecerPendenteVO other = (ParecerPendenteVO) obj;
		EqualsBuilder eq = new EqualsBuilder();

		eq.append(aprazamento, other.aprazamento);
		eq.append(descricao, other.descricao);
		eq.append(dose, other.dose);
		eq.append(medicamento, other.medicamento);
		eq.append(viaAdministracao, other.viaAdministracao);

		return eq.build();
	}

	public BigDecimal getSeq() {
		return seq;
	}

	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}

}
