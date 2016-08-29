package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Range;

import br.gov.mec.aghu.dominio.DominioFatorRh;
import br.gov.mec.aghu.dominio.DominioGrupoSanguineo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name = "AIP_PACIENTE_DADO_CLINICOS", schema = "AGH")
public class AipPacienteDadoClinicos extends BaseEntityCodigo<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5309737375152680495L;
	private Integer pacCodigo;
	private AipPacientes aipPaciente;
	private DominioFatorRh fatorRh;
	private DominioGrupoSanguineo tipagemSanguinea;
	private Byte mesesGestacao;
	private Byte apgar1;
	private Byte apgar5;
	private Date dthrNascimento;
	private BigDecimal temperatura;
	private Byte igSemanas;
	private Date criadoEm;
	private Date alteradoEm;
	private DominioSimNao indExterno;
	private Integer version;

	public AipPacienteDadoClinicos() {
	}

	public AipPacienteDadoClinicos(Integer pacCodigo, AipPacientes aipPaciente) {
		this.pacCodigo = pacCodigo;
		this.aipPaciente = aipPaciente;
	}

	public AipPacienteDadoClinicos(Integer pacCodigo, AipPacientes aipPaciente,
			DominioFatorRh fatorRh, DominioGrupoSanguineo tipagemSanguinea,
			Byte mesesGestacao, Byte apgar1, Byte apgar5, Date dthrNascimento,
			BigDecimal temperatura, Byte igSemanas, Date criadoEm,
			Date alteradoEm, DominioSimNao indExterno) {
		this.pacCodigo = pacCodigo;
		this.aipPaciente = aipPaciente;
		this.fatorRh = fatorRh;
		this.tipagemSanguinea = tipagemSanguinea;
		this.mesesGestacao = mesesGestacao;
		this.apgar1 = apgar1;
		this.apgar5 = apgar5;
		this.dthrNascimento = dthrNascimento;
		this.temperatura = temperatura;
		this.igSemanas = igSemanas;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.indExterno = indExterno;
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "aipPaciente"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "FATOR_RH", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioFatorRh getFatorRh() {
		return this.fatorRh;
	}

	public void setFatorRh(DominioFatorRh fatorRh) {
		this.fatorRh = fatorRh;
	}

	@Column(name = "TIPAGEM_SANGUINEA", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioGrupoSanguineo getTipagemSanguinea() {
		return this.tipagemSanguinea;
	}

	public void setTipagemSanguinea(DominioGrupoSanguineo tipagemSanguinea) {
		this.tipagemSanguinea = tipagemSanguinea;
	}

	@Column(name = "MESES_GESTACAO", precision = 2, scale = 0)
	@Range(min = 0, max = 9, message = "Meses de gestação deve estar entre 0 e 9.")
	public Byte getMesesGestacao() {
		return this.mesesGestacao;
	}

	public void setMesesGestacao(Byte mesesGestacao) {
		this.mesesGestacao = mesesGestacao;
	}

	@Column(name = "APGAR1", precision = 2, scale = 0)
	@Range(min = 0, max = 10, message = "Apgar1 deve estar entre 0 e 10.")
	public Byte getApgar1() {
		return this.apgar1;
	}

	public void setApgar1(Byte apgar1) {
		this.apgar1 = apgar1;
	}

	@Column(name = "APGAR5", precision = 2, scale = 0)
	@Range(min = 0, max = 10, message = "Apgar5 deve estar entre 0 e 10.")
	public Byte getApgar5() {
		return this.apgar5;
	}

	public void setApgar5(Byte apgar5) {
		this.apgar5 = apgar5;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_NASCIMENTO", length = 7)
	public Date getDthrNascimento() {
		return this.dthrNascimento;
	}

	public void setDthrNascimento(Date dthrNascimento) {
		this.dthrNascimento = dthrNascimento;
	}

	@Column(name = "TEMPERATURA", precision = 3, scale = 1)
	@Range(min = 33, max = 43, message = "Temperatura deve estar entre 33 e 43 graus.")
	public BigDecimal getTemperatura() {
		return this.temperatura;
	}

	public void setTemperatura(BigDecimal temperatura) {
		this.temperatura = temperatura;
	}

	@Column(name = "IG_SEMANAS", precision = 2, scale = 0)
	@Range(min = 1, max = 52, message = "Ig Semanas deve estar entre 1 e 52.")
	public Byte getIgSemanas() {
		return this.igSemanas;
	}

	public void setIgSemanas(Byte igSemanas) {
		this.igSemanas = igSemanas;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "IND_EXTERNO", length=1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndExterno() {
		return this.indExterno;
	}

	public void setIndExterno(DominioSimNao indExterno) {
		this.indExterno = indExterno;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public AipPacientes getAipPaciente() {
		return aipPaciente;
	}

	public void setAipPaciente(AipPacientes aipPaciente) {
		this.aipPaciente = aipPaciente;
	}

	public enum Fields {
		PAC_CODIGO("pacCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getPacCodigo() == null) ? 0 : getPacCodigo().hashCode());
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
		if (!(obj instanceof AipPacienteDadoClinicos)) {
			return false;
		}
		AipPacienteDadoClinicos other = (AipPacienteDadoClinicos) obj;
		if (getPacCodigo() == null) {
			if (other.getPacCodigo() != null) {
				return false;
			}
		} else if (!getPacCodigo().equals(other.getPacCodigo())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####
 
 @Transient public Integer getCodigo(){ return this.getPacCodigo();} 
 public void setCodigo(Integer codigo){ this.setPacCodigo(codigo);}
}
