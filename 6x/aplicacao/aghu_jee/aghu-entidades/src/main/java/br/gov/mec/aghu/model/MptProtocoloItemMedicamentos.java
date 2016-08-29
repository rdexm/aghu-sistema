package br.gov.mec.aghu.model;


import java.math.BigDecimal;
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
@SequenceGenerator(name="mptPimSq1", sequenceName="AGH.MPT_PIM_SQ1", allocationSize = 1)
@Table(name = "MPT_PROTOCOLO_ITEM_MDTOS", schema = "AGH")
public class MptProtocoloItemMedicamentos extends BaseEntitySeq<Long> implements java.io.Serializable {
	
	private static final long serialVersionUID = -3611981523970831263L;
	
	private Long seq;
	private MptProtocoloMedicamentos protocoloMedicamentos;
	private Integer medMatCodigo;
	private BigDecimal dose;	
	private Integer fdsSeq;
	private String observacao;
	private RapServidores servidor;
	private Date criadoEm;
	private Integer version;
	private AfaMedicamento afaMedicamento;	
	private AfaFormaDosagem afaFormaDosagem;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptPimSq1")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PTM_SEQ", nullable = false)
	public MptProtocoloMedicamentos getProtocoloMedicamentos() {
		return protocoloMedicamentos;
	}

	public void setProtocoloMedicamentos(MptProtocoloMedicamentos protocoloMedicamentos) {
		this.protocoloMedicamentos = protocoloMedicamentos;
	}
	
	@Column(name = "MED_MAT_CODIGO", nullable = false)
	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}
	
	@Column(name = "DOSE")
	public BigDecimal getDose() {
		return dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}
	
	@Column(name = "FDS_SEQ", insertable = false, updatable = false)
	public Integer getFdsSeq() {
		return fdsSeq;
	}

	public void setFdsSeq(Integer fdsSeq) {
		this.fdsSeq = fdsSeq;
	}
	
	@Column(name = "OBSERVACAO", length = 120)
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MED_MAT_CODIGO", insertable = false, updatable = false)
	public AfaMedicamento getAfaMedicamento() {
		return afaMedicamento;
	}
	public void setAfaMedicamento(AfaMedicamento afaMedicamento) {
		this.afaMedicamento = afaMedicamento;
	}
	
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FDS_SEQ")
	public AfaFormaDosagem getAfaFormaDosagem() {
		return afaFormaDosagem;
	}

	public void setAfaFormaDosagem(AfaFormaDosagem afaFormaDosagem) {
		this.afaFormaDosagem = afaFormaDosagem;
	}




	public enum Fields {		
		SEQ("seq"),
		MED_MAT_CODIGO("medMatCodigo"),
		DOSE("dose"),
		FDS_SEQ("fdsSeq"),
		OBSERVACAO("observacao"),
		SERVIDOR("servidor"),
		PROTOCOLO_MEDICAMENTOS("protocoloMedicamentos"),
		PROTOCOLO_MEDICAMENTOS_SEQ("protocoloMedicamentos.seq"),
		CRIADOEM("criadoEm"),
		AFA_MEDICAMENTO("afaMedicamento"),
		FORMA_DOSAGEM("afaFormaDosagem");
		
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
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(this.getSeq());
		return hashCodeBuilder.toHashCode();
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
		MptProtocoloItemMedicamentos other = (MptProtocoloItemMedicamentos) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(this.getSeq(), other.getSeq());
		return equalsBuilder.isEquals();
	}

}
