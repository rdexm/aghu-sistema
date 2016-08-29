package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO de #37928 - Lista de Germes Multirresistentes do Paciente
 * 
 * @author aghu
 *
 */

public class GMRPacienteVO implements BaseBean {

	private static final long serialVersionUID = -3371045066987917986L;

	private Integer seq; // NGM.SEQ
	private boolean ativo; // NGM.IND_NOTIFICACAO_ATIVA
	private String materialAnalise; // MAN.DESCRICAO
	private Date dataIdentificacao; // NGM.CRIADO_EM
	private Integer solicitacao; // SOE.SEQ
	private String bacteria; // BMR.DESCRICAO
	private String medicamento; // AMB.DESCRICAO

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getMaterialAnalise() {
		return materialAnalise;
	}

	public void setMaterialAnalise(String materialAnalise) {
		this.materialAnalise = materialAnalise;
	}

	public Date getDataIdentificacao() {
		return dataIdentificacao;
	}

	public void setDataIdentificacao(Date dataIdentificacao) {
		this.dataIdentificacao = dataIdentificacao;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getBacteria() {
		return bacteria;
	}

	public void setBacteria(String bacteria) {
		this.bacteria = bacteria;
	}

	public String getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeq());
		return umHashCodeBuilder.toHashCode();
	}

	public enum Fields {

		SEQ("seq"), ATIVO("ativo"), MATERIAL_ANALISE("materialAnalise"), 
		DATA_IDENTIFICACAO("dataIdentificacao"), SOLICITACAO("solicitacao"),
		BACTERIA("bacteria"), MEDICAMENTO("medicamento");

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
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GMRPacienteVO)) {
			return false;
		}
		GMRPacienteVO other = (GMRPacienteVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
		return umEqualsBuilder.isEquals();
	}

}
