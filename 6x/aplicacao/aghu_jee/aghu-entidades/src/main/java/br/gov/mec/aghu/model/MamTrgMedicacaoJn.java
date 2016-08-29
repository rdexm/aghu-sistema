package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mamTdcjSeq", sequenceName="AGH.MAM_TDC_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_TRG_MEDICACOES_JN", schema = "AGH")
@Immutable
public class MamTrgMedicacaoJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8870060540711102039L;
	private Long trgSeq;
	private Short seqp;
	private MamTriagens mamTriagens;
	private String complemento;
	private String micNome;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private MamItemMedicacao mamItemMedicacao;
	private Boolean indUso;
	private Boolean indConsistenciaOk;
	private Date dtHrInformada;
	private Date dtHrConsistenciaOk;

	public MamTrgMedicacaoJn() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamTdcjSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "TRG_SEQ")
	public Long getTrgSeq() {
		return this.trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	@Column(name = "SEQP")
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRG_SEQ", insertable = false, updatable = false)
	public MamTriagens getMamTriagens() {
		return this.mamTriagens;
	}

	public void setMamTriagens(MamTriagens mamTriagens) {
		this.mamTriagens = mamTriagens;
	}

	@Column(name = "COMPLEMENTO", length = 2000)
	@Length(max = 2000)
	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(name = "MIC_NOME", length = 50)
	@Length(max = 50)
	public String getMicNome() {
		return this.micNome;
	}

	public void setMicNome(String micNome) {
		this.micNome = micNome;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDM_SEQ", referencedColumnName="SEQ")
	public MamItemMedicacao getMamItemMedicacao() {
		return mamItemMedicacao;
	}

	public void setMamItemMedicacao(MamItemMedicacao mamItemMedicacao) {
		this.mamItemMedicacao = mamItemMedicacao;
	}

	@Column(name = "IND_USO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUso() {
		return indUso;
	}

	public void setIndUso(Boolean indUso) {
		this.indUso = indUso;
	}

	@Column(name = "IND_CONSISTENCIA_OK", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConsistenciaOk() {
		return indConsistenciaOk;
	}

	public void setIndConsistenciaOk(Boolean indConsistenciaOk) {
		this.indConsistenciaOk = indConsistenciaOk;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INFORMADA", length = 7)
	public Date getDtHrInformada() {
		return dtHrInformada;
	}

	public void setDtHrInformada(Date dtHrInformada) {
		this.dtHrInformada = dtHrInformada;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CONSISTENCIA_OK", length = 7)
	public Date getDtHrConsistenciaOk() {
		return dtHrConsistenciaOk;
	}

	public void setDtHrConsistenciaOk(Date dtHrConsistenciaOk) {
		this.dtHrConsistenciaOk = dtHrConsistenciaOk;
	}

	public enum Fields {

		TRG_SEQ("trgSeq"),
		SEQP("seqp"),
		MIC_NOME("micNome"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
