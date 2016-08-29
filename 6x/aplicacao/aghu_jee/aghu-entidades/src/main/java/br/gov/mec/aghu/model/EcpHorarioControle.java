package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "ECP_HORARIO_CONTROLES", schema = "AGH")
@SequenceGenerator(name = "ecpHctSq1", sequenceName = "AGH.ECP_HCT_SQ1", allocationSize = 1)
public class EcpHorarioControle extends BaseEntitySeq<Long> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8709662565343203306L;

	private Long seq;

	private Date dataHora;
	private String anotacoes;

	private AipPacientes paciente;
	private AghAtendimentos atendimento;
	private AghUnidadesFuncionais unidadeFuncional;
	private AinQuartos quarto;
	private AinLeitos leito;
	private Long trgSeq;

	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;

	private Set<EcpControlePaciente> controlePacientes = new HashSet<EcpControlePaciente>();

	// construtores
	public EcpHorarioControle() {

	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 18, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ecpHctSq1")
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name = "DATA_HORA", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataHora() {
		return this.dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	@Transient
	public String getHoraMinuto() {
		DateFormat sdf = DateFormat.getTimeInstance(DateFormat.SHORT, new Locale("pt","BR"));
		return sdf.format(getDataHora());
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ANOTACOES", length = 800)
	@Length(max = 800, message = "Anotação deve ter no máximo 800 caracteres.")
	public String getAnotacoes() {
		return this.anotacoes;
	}

	public void setAnotacoes(String anotacoes) {
		this.anotacoes = anotacoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", referencedColumnName = "CODIGO")
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ", referencedColumnName = "SEQ")
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", referencedColumnName = "SEQ")
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QRT_NUMERO", referencedColumnName = "NUMERO")
	public AinQuartos getQuarto() {
		return quarto;
	}

	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LTO_LTO_ID", referencedColumnName = "LTO_ID")
	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}
	
	@Column(name = "TRG_SEQ", length = 14)
	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
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

	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "horario")
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public Set<EcpControlePaciente> getControlePacientes() {
		return controlePacientes;
	}

	public void setControlePacientes(Set<EcpControlePaciente> controlePacientes) {
		this.controlePacientes = controlePacientes;
	}

	// outros
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EcpHorarioControle)) {
			return false;
		}
		EcpHorarioControle castOther = (EcpHorarioControle) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), DATA_HORA("dataHora"), CRIADO_EM("criadoEm"), ANOTACOES(
				"anotacoes"), PACIENTE("paciente"), ATENDIMENTO("atendimento"), UNIDADE_FUNCIONAL(
				"unidadeFuncional"), QUARTO("quarto"), LEITO("leito"), SERVIDOR(
						"servidor"), CONTROLE_PACIENTE("controlePacientes"),PACIENTE_CODIGO("paciente.codigo"), TRG_SEQ("trgSeq");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}