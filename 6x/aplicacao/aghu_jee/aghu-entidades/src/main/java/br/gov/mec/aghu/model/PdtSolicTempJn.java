package br.gov.mec.aghu.model;

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


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "PDT_SOLIC_TEMPS_JN", schema = "AGH")
@SequenceGenerator(name = "pdtSptJnSeq", sequenceName = "AGH.PDT_SPT_JN_SEQ", allocationSize = 1)
@Immutable
public class PdtSolicTempJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8915628310514404287L;

	private Integer ddtSeq;
	private RapServidores servidor;
	private String solicitadoPor;
	private String motivo;
	private String medicamentos;
	private String observacoes;
	private Date criadoEm;

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "pdtSptJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "ddt_seq", nullable = false)
	public Integer getDdtSeq() {
		return this.ddtSeq;
	}

	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}	

	@Column(name = "motivo", length = 2000)
	@Length(max = 2000)
	public String getMotivo() {
		return this.motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	@Column(name = "medicamentos", length = 500)
	@Length(max = 500)
	public String getMedicamentos() {
		return this.medicamentos;
	}

	public void setMedicamentos(String medicamentos) {
		this.medicamentos = medicamentos;
	}

	@Column(name = "observacoes", length = 500)
	@Length(max = 500)
	public String getObservacoes() {
		return this.observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	@Column(name = "solicitado_por", length = 60)
	@Length(max = 60)
	public String getSolicitadoPor() {
		return solicitadoPor;
	}

	public void setSolicitadoPor(String solicitadoPor) {
		this.solicitadoPor = solicitadoPor;
	}

	@Column(name = "criado_em", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
}
