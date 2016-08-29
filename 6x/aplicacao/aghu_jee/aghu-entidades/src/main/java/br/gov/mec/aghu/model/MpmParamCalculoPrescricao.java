package br.gov.mec.aghu.model;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MPM_PARAM_CALCULO_PRESCRICOES", schema = "AGH")
public class MpmParamCalculoPrescricao extends BaseEntityId<MpmParamCalculoPrescricaoId> implements java.io.Serializable {

	private static final long serialVersionUID = -884582035277405532L;

	private MpmParamCalculoPrescricaoId id;
	private RapServidores servidor;
	private AipPesoPacientes aipPesoPaciente;
	private AipAlturaPacientes aipAlturaPaciente;
	private Integer pepPacCodigo;
	private Integer atpPacCodigo;
	private BigDecimal sc;
	private BigDecimal scCalculada;
	private Short idadeEmAnos;
	private Integer idadeEmMeses;
	private Integer idadeEmDias;
	private DominioSituacao indSituacao;

	public MpmParamCalculoPrescricao() {
	}

	public MpmParamCalculoPrescricao(final MpmParamCalculoPrescricaoId id, final RapServidores servidor,
			final Short idadeEmAnos, final Integer idadeEmMeses, final Integer idadeEmDias, final DominioSituacao indSituacao) {
		this.id = id;
		this.servidor = servidor;
		this.idadeEmAnos = idadeEmAnos;
		this.idadeEmMeses = idadeEmMeses;
		this.idadeEmDias = idadeEmDias;
		this.indSituacao = indSituacao;
	}

	public MpmParamCalculoPrescricao(final MpmParamCalculoPrescricaoId id, final RapServidores servidor,
			final AipPesoPacientes aipPesoPaciente, final AipAlturaPacientes aipAlturaPaciente, final BigDecimal sc,
			final BigDecimal scCalculada, final Short idadeEmAnos, final Integer idadeEmMeses, final Integer idadeEmDias,
			final DominioSituacao indSituacao) {
		this.id = id;
		this.servidor = servidor;
		this.aipPesoPaciente = aipPesoPaciente;
		this.aipAlturaPaciente = aipAlturaPaciente;
		this.sc = sc;
		this.scCalculada = scCalculada;
		this.idadeEmAnos = idadeEmAnos;
		this.idadeEmMeses = idadeEmMeses;
		this.idadeEmDias = idadeEmDias;
		this.indSituacao = indSituacao;
	}

	@EmbeddedId
	@AttributeOverrides( { @AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ", nullable = false, precision = 7, scale = 0)),
	@AttributeOverride(name = "criadoEm", column = @Column(name = "CRIADO_EM", nullable = false, length = 7)) })
	public MpmParamCalculoPrescricaoId getId() {
		return this.id;
	}

	public void setId(final MpmParamCalculoPrescricaoId id) {
		this.id = id;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns(value = { @JoinColumn(name = "PEP_PAC_CODIGO", referencedColumnName = "PAC_CODIGO"),
	@JoinColumn(name = "PEP_CRIADO_EM", referencedColumnName = "CRIADO_EM") })
	public AipPesoPacientes getAipPesoPaciente() {
		return this.aipPesoPaciente;
	}

	public void setAipPesoPaciente(final AipPesoPacientes aipPesoPaciente) {
		this.aipPesoPaciente = aipPesoPaciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns(value = { @JoinColumn(name = "ATP_PAC_CODIGO", referencedColumnName = "PAC_CODIGO"),
			@JoinColumn(name = "ATP_CRIADO_EM", referencedColumnName = "CRIADO_EM") })
	public AipAlturaPacientes getAipAlturaPaciente() {
		return this.aipAlturaPaciente;
	}

	public void setAipAlturaPaciente(final AipAlturaPacientes aipAlturaPaciente) {
		this.aipAlturaPaciente = aipAlturaPaciente;
	}

	@Column(name = "SC", precision = 5)
	public BigDecimal getSc() {
		return this.sc;
	}

	public void setSc(final BigDecimal sc) {
		this.sc = sc;
	}

	@Column(name = "SC_CALCULADA", precision = 5)
	public BigDecimal getScCalculada() {
		return this.scCalculada;
	}

	public void setScCalculada(final BigDecimal scCalculada) {
		this.scCalculada = scCalculada;
	}

	@Column(name = "IDADE_EM_ANOS", nullable = false, precision = 3, scale = 0)
	public Short getIdadeEmAnos() {
		return this.idadeEmAnos;
	}

	public void setIdadeEmAnos(final Short idadeEmAnos) {
		this.idadeEmAnos = idadeEmAnos;
	}

	@Column(name = "IDADE_EM_MESES", nullable = false, precision = 6, scale = 0)
	public Integer getIdadeEmMeses() {
		return this.idadeEmMeses;
	}

	public void setIdadeEmMeses(final Integer idadeEmMeses) {
		this.idadeEmMeses = idadeEmMeses;
	}

	@Column(name = "IDADE_EM_DIAS", nullable = false, precision = 6, scale = 0)
	public Integer getIdadeEmDias() {
		return this.idadeEmDias;
	}

	public void setIdadeEmDias(final Integer idadeEmDias) {
		this.idadeEmDias = idadeEmDias;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(final DominioSituacao i) {
		this.indSituacao = i;
	}

	@Column(name = "PEP_PAC_CODIGO", insertable = false, updatable = false)
	public Integer getPepPacCodigo() {
		return pepPacCodigo;
	}

	public void setPepPacCodigo(Integer pepPacCodigo) {
		this.pepPacCodigo = pepPacCodigo;
	}

	@Column(name = "ATP_PAC_CODIGO", insertable = false, updatable = false)
	public Integer getAtpPacCodigo() {
		return atpPacCodigo;
	}

	public void setAtpPacCodigo(Integer atpPacCodigo) {
		this.atpPacCodigo = atpPacCodigo;
	}



	public enum Fields {
		SITUACAO("indSituacao"), ATD_SEQ("id.atdSeq"), CRIADOEM("id.criadoEm"), PESO_PACIENTE("aipPesoPaciente"), PESO_PACIENTE_CODIGO("aipPesoPaciente.id.pacCodigo"), 
		ALTURA_PACIENTE("aipAlturaPaciente"), ALTURA_PACIENTE_CODIGO("aipAlturaPaciente.id.pacCodigo"), PESO_CRIADO_EM(
		"aipPesoPaciente.id.criadoEm"), ALTURA_CRIADO_EM("aipAlturaPaciente.id.criadoEm"), SC("sc");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		MpmParamCalculoPrescricao other = (MpmParamCalculoPrescricao) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

}
