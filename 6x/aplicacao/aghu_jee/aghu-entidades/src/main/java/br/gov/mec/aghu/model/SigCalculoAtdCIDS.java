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
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCciSq1", sequenceName = "SIG_CCI_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_atd_cids", schema = "agh")
public class SigCalculoAtdCIDS extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4423351486063029177L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;
	private SigCalculoAtdPaciente calculoAtdPaciente;
	private AghCid cid;
	private Boolean principal;

	public SigCalculoAtdCIDS() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCciSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "version", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "IND_PRINCIPAL", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CID_SEQ", nullable = true)
	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAC_SEQ", nullable = false)
	public SigCalculoAtdPaciente getCalculoAtdPaciente() {
		return calculoAtdPaciente;
	}

	public void setCalculoAtdPaciente(SigCalculoAtdPaciente calculoAtdPaciente) {
		this.calculoAtdPaciente = calculoAtdPaciente;
	}

	public enum Fields {

		SEQ("seq"), CRIADO_EM("criadoEm"), SERVIDOR("rapServidores"), PRINCIPAL("principal"), CID("cid"), CALCULO_ATD_PACIENTE("calculoAtdPaciente");

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
	public int hashCode() {
		return new HashCodeBuilder().append(seq).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SigCalculoAtdCIDS)) {
			return false;
		}
		SigCalculoAtdCIDS other = (SigCalculoAtdCIDS) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}

}
