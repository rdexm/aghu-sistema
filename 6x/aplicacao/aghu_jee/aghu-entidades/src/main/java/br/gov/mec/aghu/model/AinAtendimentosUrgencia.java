package br.gov.mec.aghu.model;

// Generated 04/02/2010 10:55:42 by Hibernate Tools 3.2.5.Beta

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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;


import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * AinAtendimentosUrgencia generated by hbm2java
 */
@Entity
@SequenceGenerator(name="ainAtuSq1", sequenceName="AGH.AIN_ATU_SQ1", allocationSize = 1)
@Table(name = "AIN_ATENDIMENTOS_URGENCIA", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = "CON_NUMERO"))

public class AinAtendimentosUrgencia extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5188703032194185202L;

	private Integer seq;

	private AghCid cid;

	private RapServidores servidor;
	
	private FatConvenioSaude convenioSaude;
	
	private Byte cspSeq;
	
	//private Byte cspSeq;
	private FatConvenioSaudePlano convenioSaudePlano;
	
	private Date dtAtendimento;
	
	private AipPacientes paciente;

	private Boolean indInternacao;
	
	private Boolean indPacienteEmAtendimento;

	private DominioLocalPaciente indLocalPaciente;

	private AghEspecialidades especialidade;

	private AinLeitos leito;
	
	private AinQuartos quarto;

	private AghUnidadesFuncionais unidadeFuncional;

	private AghClinicas clinica;

	private AinTiposAltaMedica tipoAltaMedica;

	private Date dtAltaAtendimento;
	
	private String envProntUnidInt;
	
	private AghAtendimentos atendimento;
	
	private AacConsultas consulta;
	
	private Date dataAvisoSamis;
	
	private enum AtendimentosUrgenciaExceptionCode implements
			BusinessExceptionCode {
		AIN_ATU_CK4, AIN_ATU_CK6
	}

	public AinAtendimentosUrgencia() {
	}

	public AinAtendimentosUrgencia(Integer seq, AghCid cidSeq,
			RapServidores servidor, FatConvenioSaude convenioSaude,
			FatConvenioSaudePlano convenioSaudePlano, Date dtAtendimento, AipPacientes paciente,
			Boolean indInternacao,
			Boolean indPacienteEmAtendimento,
			DominioLocalPaciente indLocalPaciente, AacConsultas consulta,
			String envProntUnidInt) {
		this.seq = seq;
		this.cid = cidSeq;
		this.servidor = servidor;
		this.convenioSaude = convenioSaude;
		this.convenioSaudePlano = convenioSaudePlano;
		this.dtAtendimento = dtAtendimento;
		this.paciente = paciente;
		this.indInternacao = indInternacao;
		this.indPacienteEmAtendimento = indPacienteEmAtendimento;
		this.indLocalPaciente = indLocalPaciente;
		this.consulta = consulta;
		this.envProntUnidInt = envProntUnidInt;
	}

	public AinAtendimentosUrgencia(Integer seq, AghCid cidSeq,
			RapServidores servidor, FatConvenioSaude convenioSaude,
			FatConvenioSaudePlano convenioSaudePlano, Date dtAtendimento, AipPacientes paciente,
			Boolean indInternacao,
			Boolean indPacienteEmAtendimento,
			DominioLocalPaciente indLocalPaciente, AacConsultas consulta,
			AghEspecialidades espSeq, AinLeitos leito, AinQuartos quarto,
			AghUnidadesFuncionais unfSeq, AghClinicas clcCodigo,
			AinTiposAltaMedica tamCodigo, Date dtAltaAtendimento,
			String envProntUnidInt) {
		this.seq = seq;
		this.cid = cidSeq;
		this.servidor = servidor;
		this.convenioSaude = convenioSaude;
		this.convenioSaudePlano = convenioSaudePlano;
		this.dtAtendimento = dtAtendimento;
		this.paciente = paciente;
		this.indInternacao = indInternacao;
		this.indPacienteEmAtendimento = indPacienteEmAtendimento;
		this.indLocalPaciente = indLocalPaciente;
		this.consulta = consulta;
		this.especialidade = espSeq;
		this.leito = leito;
		this.quarto = quarto;
		this.unidadeFuncional = unfSeq;
		this.clinica = clcCodigo;
		this.tipoAltaMedica = tamCodigo;
		this.dtAltaAtendimento = dtAltaAtendimento;
		this.envProntUnidInt = envProntUnidInt;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ainAtuSq1")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	/**
	 * @param servidor
	 *            the servidor to set
	 */
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.MERGE,
			org.hibernate.annotations.CascadeType.REFRESH})
	@JoinColumn(name = "CSP_CNV_CODIGO", nullable = false)
	public FatConvenioSaude getConvenioSaude() {
		return this.convenioSaude;
	}

	public void setConvenioSaude(FatConvenioSaude convenioSaude) {
		this.convenioSaude = convenioSaude;
	}
	
	@Column(name = "CSP_SEQ", nullable = false, precision = 2, scale = 0)
	public Byte getCspSeq() {
		return this.cspSeq;
	}

	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "CSP_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CSP_CNV_CODIGO", referencedColumnName = "CNV_CODIGO", nullable = false, insertable = false, updatable = false) })
	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return this.convenioSaudePlano;
	}

	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ATENDIMENTO", nullable = false, length = 7)
	public Date getDtAtendimento() {
		return this.dtAtendimento;
	}

	public void setDtAtendimento(Date dtAtendimento) {
		this.dtAtendimento = dtAtendimento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", nullable = false)
	public AipPacientes getPaciente() {
		return this.paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@Column(name = "IND_INTERNACAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInternacao() {
		return this.indInternacao;
	}

	public void setIndInternacao(Boolean indInternacao) {
		this.indInternacao = indInternacao;
	}

	
	
	@Column(name = "IND_PACIENTE_EM_ATENDIMENTO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPacienteEmAtendimento() {
		return this.indPacienteEmAtendimento;
	}

	public void setIndPacienteEmAtendimento(
			Boolean indPacienteEmAtendimento) {
		this.indPacienteEmAtendimento = indPacienteEmAtendimento;
	}

	@Column(name = "IND_LOCAL_PACIENTE", nullable = false, length=1)
	@Enumerated(EnumType.STRING)
	public DominioLocalPaciente getIndLocalPaciente() {
		return this.indLocalPaciente;
	}

	public void setIndLocalPaciente(DominioLocalPaciente indLocalPaciente) {
		this.indLocalPaciente = indLocalPaciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CON_NUMERO", nullable = false)
	public AacConsultas getConsulta() {
		return consulta;
	}

	public void setConsulta(AacConsultas consulta) {
		this.consulta = consulta;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.MERGE})
	@JoinColumn(name = "LTO_LTO_ID")
	public AinLeitos getLeito() {
		return this.leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.MERGE})
	@JoinColumn(name = "QRT_NUMERO")
	public AinQuartos getQuarto() {
		return this.quarto;
	}

	public void setQuarto(AinQuartos ainQuarto) {
		this.quarto = ainQuarto;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ALTA_ATENDIMENTO", length = 7)
	public Date getDtAltaAtendimento() {
		return this.dtAltaAtendimento;
	}

	public void setDtAltaAtendimento(Date dtAltaAtendimento) {
		this.dtAltaAtendimento = dtAltaAtendimento;
	}

	@Column(name = "ENV_PRONT_UNID_INT", nullable = false, length = 1)
	@Length(max = 1)
	public String getEnvProntUnidInt() {
		return this.envProntUnidInt;
	}

	public void setEnvProntUnidInt(String envProntUnidInt) {
		this.envProntUnidInt = envProntUnidInt;
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validarDados() {
		/*
		 * VALORES PADRÃO
		 */
		if (this.dtAtendimento == null) {
			this.dtAtendimento = new Date();
		}
		if (this.indInternacao == null) {
			this.indInternacao = true;
		}
		if (this.indPacienteEmAtendimento == null) {
			this.indPacienteEmAtendimento = true;
		}

		/*
		 * RESTRICOES (CONSTRAINTS)
		 */
		// AIN_ATU_CK4
		if (!(this.dtAltaAtendimento == null || this.dtAltaAtendimento
				.compareTo(this.dtAtendimento) >= 0)) {
			throw new BaseRuntimeException(
					AtendimentosUrgenciaExceptionCode.AIN_ATU_CK4);
		}
		// AIN_ATU_CK6
		if (!((this.tipoAltaMedica == null && this.dtAltaAtendimento == null) || (this.tipoAltaMedica != null && this.dtAltaAtendimento != null))) {
			throw new BaseRuntimeException(
					AtendimentosUrgenciaExceptionCode.AIN_ATU_CK6);
		}
	}

	public enum Fields {
		COD_PACIENTE("paciente.codigo"), IND_PACIENTE_EM_ATENDIMENTO(
				"indPacienteEmAtendimento"), CLINICA("clinica"), ESPECIALIDADE(
				"especialidade"), UNIDADE_FUNCIONAL("unidadeFuncional"), UNIDADE_FUNCIONAL_SEQ(
				"unidadeFuncional.seq"), DT_ATENDIMENTO("dtAtendimento"), SEQ(
				"seq"), PACIENTE("paciente"), PACIENTE_CODIGO("paciente.codigo"), QUARTO(
				"quarto"), QUARTO_NUMERO("quarto.numero"), LEITO("leito"), LEITO_ID(
				"leito.leitoID"), IND_LOCAL_PACIENTE("indLocalPaciente"), DATA_ALTA_ATENDIMENTO(
				"dtAltaAtendimento"), TIPO_ALTA_MEDICA_CODIGO(
				"tipoAltaMedica.codigo"), ESP_SEQ("especialidade.seq"), CSP_SEQ(
				"convenioSaudePlano.id.seq"),CSP_CNV_CODIGO(
				"convenioSaudePlano.id.cnvCodigo"),	CONSULTA_NUMERO(
				"consulta.numero"),CONSULTA("consulta"),CONVENIO_SAUDE("convenioSaude"),SERVIDOR(
				"servidor"),PESSOA_FISICA("servidor.pessoaFisica"), DTHR_AVISO_SAMIS("dataAvisoSamis");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "atendimentoUrgencia")
	@Cascade({org.hibernate.annotations.CascadeType.MERGE})
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.MERGE})
	@JoinColumn(name = "UNF_SEQ")
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.MERGE})
	@JoinColumn(name = "ESP_SEQ")
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLC_CODIGO")
	public AghClinicas getClinica() {
		return clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	/**
	 * @param cid
	 *            the cid to set
	 */
	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	/**
	 * @return the cid
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.MERGE})
	@JoinColumn(name = "CID_SEQ")
	public AghCid getCid() {
		return cid;
	}

	/**
	 * @param tipoAltaMedica
	 *            the tipoAltaMedica to set
	 */
	public void setTipoAltaMedica(AinTiposAltaMedica tipoAltaMedica) {
		this.tipoAltaMedica = tipoAltaMedica;
	}

	/**
	 * @return the tipoAltaMedica
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TAM_CODIGO")
	public AinTiposAltaMedica getTipoAltaMedica() {
		return tipoAltaMedica;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (!(obj instanceof AinAtendimentosUrgencia)) {
			return false;
		}
		AinAtendimentosUrgencia other = (AinAtendimentosUrgencia) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_AVISO_SAMIS", nullable = false, length = 7)
	public Date getDataAvisoSamis() {
		return dataAvisoSamis;
	}

	public void setDataAvisoSamis(Date dataAvisoSamis) {
		this.dataAvisoSamis = dataAvisoSamis;
	}
	
}