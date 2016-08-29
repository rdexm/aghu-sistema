package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptPidSq1", sequenceName="AGH.MPT_PID_SQ1", allocationSize = 1)
@Table(name = "MPT_PROTOCOLO_ITEM_MDTOS_DIA", schema = "AGH")
public class MptProtocoloItemMedicamentosDia extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 357742012934013642L;

	private Integer seq;
	
	private MptProtocoloMedicamentosDia protocoloMedicamentosDia;
	
	private AfaMedicamento medicamento;
	
	private BigDecimal dose;
	
	private Integer fdsSeq;
	
	private String observacao;
	
	private RapServidores servidor;
	
	private Date criadoEm;
	
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptPidSq1")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PTD_SEQ")
	@NotNull
	public MptProtocoloMedicamentosDia getProtocoloMedicamentosDia() {
		return protocoloMedicamentosDia;
	}

	public void setProtocoloMedicamentosDia(
			MptProtocoloMedicamentosDia protocoloMedicamentosDia) {
		this.protocoloMedicamentosDia = protocoloMedicamentosDia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MED_MAT_CODIGO")
	@NotNull
	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	@Column(name = "DOSE", precision = 14, scale = 4)
	public BigDecimal getDose() {
		return dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}

	@Column(name = "FDS_SEQ", precision = 6, scale = 0)
	public Integer getFdsSeq() {
		return fdsSeq;
	}

	public void setFdsSeq(Integer fdsSeq) {
		this.fdsSeq = fdsSeq;
	}

	@Column(name = "OBSERVACAO")
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
	@NotNull
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "CRIADO_EM", nullable = false)
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

}
