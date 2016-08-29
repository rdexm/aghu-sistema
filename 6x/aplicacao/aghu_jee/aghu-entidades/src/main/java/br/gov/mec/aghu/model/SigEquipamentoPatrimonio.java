package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigEcpSq1", sequenceName = "SIG_ECP_SQ1", allocationSize = 1)
@Table(name = "SIG_EQUIPAMENTO_PATRIMONIOS", schema = "AGH")
public class SigEquipamentoPatrimonio extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 6401464981495435793L;

	private Integer seq;
	private Date criadoEm;
	private String codPatrimonio;
	private RapServidores servidorResp;
	private Integer version;
	private MbcEquipamentoCirurgico mbcEquipamentoCirurgico;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigEcpSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "COD_PATRIMONIO", nullable = false, length = 60)
	public String getCodPatrimonio() {
		return this.codPatrimonio;
	}

	public void setCodPatrimonio(String codPatrimonio) {
		this.codPatrimonio = codPatrimonio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorResp() {
		return this.servidorResp;
	}

	public void setServidorResp(RapServidores servidorResp) {
		this.servidorResp = servidorResp;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EUU_SEQ", referencedColumnName = "SEQ")
	public MbcEquipamentoCirurgico getMbcEquipamentoCirurgico() {
		return mbcEquipamentoCirurgico;
	}

	public void setMbcEquipamentoCirurgico(MbcEquipamentoCirurgico mbcEquipamentoCirurgico) {
		this.mbcEquipamentoCirurgico = mbcEquipamentoCirurgico;
	}

	public enum Fields {

		SEQ("seq"),
		CODIGO_PATRIMONIO("codPatrimonio"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("servidorResp"),
		EQUIPAMENTO_CIRURGICO("mbcEquipamentoCirurgico");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigEquipamentoPatrimonio)) {
			return false;
		}
		SigEquipamentoPatrimonio castOther = (SigEquipamentoPatrimonio) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
