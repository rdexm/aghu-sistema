package br.gov.mec.aghu.model;

// Generated 28/03/2012 15:17:44 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioAsa;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteFicha;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mbcFhpSq1", sequenceName="AGH.MBC_FHP_SQ1", allocationSize = 1)
@Table(name = "MBC_FICHA_PACIENTES", schema = "AGH")
public class MbcFichaPaciente extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -8889058917885580480L;
	private Integer seq;
	private Integer version;
	private MbcFichaAnestesias mbcFichaAnestesias;
	private MbcCondPsicoSensorial mbcCondPsicoSensoriais;
	private Double peso;
	private Float altura;
	private Boolean jejumIndeterminado;
	private Short tempoJejum;
	private DominioAsa asa;
	private String justificativaAsa;
	private DominioOrigemPacienteFicha origemPaciente;
	private Date criadoEm;
	private RapServidores servidor;

	public MbcFichaPaciente() {
	}

	public MbcFichaPaciente(Integer seq, MbcFichaAnestesias mbcFichaAnestesias,
			Boolean jejumIndeterminado, Date criadoEm, RapServidores servidor) {
		this.seq = seq;
		this.mbcFichaAnestesias = mbcFichaAnestesias;
		this.jejumIndeterminado = jejumIndeterminado;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	public MbcFichaPaciente(Integer seq, MbcFichaAnestesias mbcFichaAnestesias,
			MbcCondPsicoSensorial mbcCondPsicoSensoriais, Double peso,
			Float altura, Boolean jejumIndeterminado, Short tempoJejum,
			DominioAsa asa, String justificativaAsa, DominioOrigemPacienteFicha origemPaciente,
			Date criadoEm, RapServidores servidor) {
		this.seq = seq;
		this.mbcFichaAnestesias = mbcFichaAnestesias;
		this.mbcCondPsicoSensoriais = mbcCondPsicoSensoriais;
		this.peso = peso;
		this.altura = altura;
		this.jejumIndeterminado = jejumIndeterminado;
		this.tempoJejum = tempoJejum;
		this.asa = asa;
		this.justificativaAsa = justificativaAsa;
		this.origemPaciente = origemPaciente;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcFhpSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIC_SEQ", nullable = false)
	public MbcFichaAnestesias getMbcFichaAnestesias() {
		return this.mbcFichaAnestesias;
	}

	public void setMbcFichaAnestesias(MbcFichaAnestesias mbcFichaAnestesias) {
		this.mbcFichaAnestesias = mbcFichaAnestesias;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CPS_SEQ")
	public MbcCondPsicoSensorial getMbcCondPsicoSensoriais() {
		return this.mbcCondPsicoSensoriais;
	}

	public void setMbcCondPsicoSensoriais(
			MbcCondPsicoSensorial mbcCondPsicoSensoriais) {
		this.mbcCondPsicoSensoriais = mbcCondPsicoSensoriais;
	}

	@Column(name = "PESO", precision = 17, scale = 17)
	public Double getPeso() {
		return this.peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}

	@Column(name = "ALTURA", precision = 8, scale = 8)
	public Float getAltura() {
		return this.altura;
	}

	public void setAltura(Float altura) {
		this.altura = altura;
	}

	@Column(name = "JEJUM_INDETERMINADO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getJejumIndeterminado() {
		return this.jejumIndeterminado;
	}

	public void setJejumIndeterminado(Boolean jejumIndeterminado) {
		this.jejumIndeterminado = jejumIndeterminado;
	}

	@Column(name = "TEMPO_JEJUM")
	public Short getTempoJejum() {
		return this.tempoJejum;
	}

	public void setTempoJejum(Short tempoJejum) {
		this.tempoJejum = tempoJejum;
	}

	@Column(name = "ASA")
	@Enumerated(EnumType.ORDINAL)
	public DominioAsa getAsa() {
		return this.asa;
	}

	public void setAsa(DominioAsa asa) {
		this.asa = asa;
	}

	@Column(name = "JUSTIFICATIVA_ASA", length = 120)
	@Length(max = 120)
	public String getJustificativaAsa() {
		return this.justificativaAsa;
	}

	public void setJustificativaAsa(String justificativaAsa) {
		this.justificativaAsa = justificativaAsa;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	public enum Fields {

		SEQ("seq"),
		MBC_COND_PSICO_SENSORIAIS("mbcCondPsicoSensoriais"),
		PESO("peso"),
		ALTURA("altura"),
		JEJUM_INDETERMINADO("jejumIndeterminado"),
		TEMPO_JEJUM("tempoJejum"),
		ASA("asa"),
		JUSTIFICATIVA_ASA("justificativaAsa"),
		ORIGEM_PACIENTE("origemPaciente"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		FICHA_ANESTESIA("mbcFichaAnestesias"),
		FICHA_ANESTESIA_SEQ("mbcFichaAnestesias.seq");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MbcFichaPaciente)) {
			return false;
		}
		MbcFichaPaciente other = (MbcFichaPaciente) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "ORIGEM_PACIENTE", length = 3)
	@Enumerated(EnumType.STRING)
	public DominioOrigemPacienteFicha getOrigemPaciente() {
		return origemPaciente;
	}

	public void setOrigemPaciente(DominioOrigemPacienteFicha origemPaciente) {
		this.origemPaciente = origemPaciente;
	}
	
}
