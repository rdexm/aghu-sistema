package br.gov.mec.aghu.model;

// Generated 26/02/2010 17:37:25 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioTipoParto;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MCO_TRAB_PARTOS", schema = "AGH")
public class McoTrabPartos extends BaseEntityId<McoGestacoesId> implements java.io.Serializable {

	private static final long serialVersionUID = 2208328111179841757L;
	
	private McoGestacoesId id;
	private McoGestacoes mcoGestacoes;
	private Date dthriniCtg;
	private String indicacoesCtg;
	private DominioTipoParto tipoParto;
	private String observacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private McoIndicacaoNascimento indicacaoNascimento;
	private String justificativa;
	private Integer serMatriculaIndicado;
	private Short serVinCodigoIndicado;
	private Integer serMatriculaIndicado2;
	private Short serVinCodigoIndicado2;
	private Boolean indicadorPartoInduzido;
	private Integer version;

	public McoTrabPartos() {
	}

	public McoTrabPartos(McoGestacoes mcoGestacoes, Date criadoEm,
			Integer serMatricula, Short serVinCodigo) {
		this.mcoGestacoes = mcoGestacoes;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	public McoTrabPartos(McoGestacoes mcoGestacoes, Date dthriniCtg,
			String indicacoesCtg, DominioTipoParto tipoParto, String observacao,
			Date criadoEm, Integer serMatricula, Short serVinCodigo,
			McoIndicacaoNascimento indicacaoNascimento, String justificativa, Integer serMatriculaIndicado,
			Short serVinCodigoIndicado, Integer serMatriculaIndicado2,
			Short serVinCodigoIndicado2) {
		this.mcoGestacoes = mcoGestacoes;
		this.dthriniCtg = dthriniCtg;
		this.indicacoesCtg = indicacoesCtg;
		this.tipoParto = tipoParto;
		this.observacao = observacao;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.indicacaoNascimento = indicacaoNascimento;
		this.justificativa = justificativa;
		this.serMatriculaIndicado = serMatriculaIndicado;
		this.serVinCodigoIndicado = serVinCodigoIndicado;
		this.serMatriculaIndicado2 = serMatriculaIndicado2;
		this.serVinCodigoIndicado2 = serVinCodigoIndicado2;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "GSO_PAC_CODIGO", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "GSO_SEQP", nullable = false, precision = 3, scale = 0)) })
	public McoGestacoesId getId() {
		return this.id;
	}

	public void setId(McoGestacoesId id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public McoGestacoes getMcoGestacoes() {
		return this.mcoGestacoes;
	}

	public void setMcoGestacoes(McoGestacoes mcoGestacoes) {
		this.mcoGestacoes = mcoGestacoes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHRINI_CTG")
	public Date getDthriniCtg() {
		return this.dthriniCtg;
	}

	public void setDthriniCtg(Date dthriniCtg) {
		this.dthriniCtg = dthriniCtg;
	}

	@Column(name = "INDICACOES_CTG", length = 120)
	@Length(max = 120)
	public String getIndicacoesCtg() {
		return this.indicacoesCtg;
	}

	public void setIndicacoesCtg(String indicacoesCtg) {
		this.indicacoesCtg = indicacoesCtg;
	}

	@Column(name = "TIPO_PARTO")
	@Enumerated(EnumType.STRING)
	public DominioTipoParto getTipoParto() {
		return this.tipoParto;
	}

	public void setTipoParto(DominioTipoParto tipoParto) {
		this.tipoParto = tipoParto;
	}

	@Column(name = "OBSERVACAO", length = 240)
	@Length(max = 240)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@OneToOne
	@JoinColumn(name = "INA_SEQ",nullable=true)
	public McoIndicacaoNascimento getIndicacaoNascimento() {
		return indicacaoNascimento;
	}

	public void setIndicacaoNascimento(McoIndicacaoNascimento indicacaoNascimento) {
		this.indicacaoNascimento = indicacaoNascimento;
	}

	@Column(name = "JUSTIFICATIVA", length = 250)
	@Length(max = 250)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Column(name = "SER_MATRICULA_INDICADO", precision = 7, scale = 0)
	public Integer getSerMatriculaIndicado() {
		return this.serMatriculaIndicado;
	}

	public void setSerMatriculaIndicado(Integer serMatriculaIndicado) {
		this.serMatriculaIndicado = serMatriculaIndicado;
	}

	@Column(name = "SER_VIN_CODIGO_INDICADO", precision = 3, scale = 0)
	public Short getSerVinCodigoIndicado() {
		return this.serVinCodigoIndicado;
	}

	public void setSerVinCodigoIndicado(Short serVinCodigoIndicado) {
		this.serVinCodigoIndicado = serVinCodigoIndicado;
	}

	@Column(name = "SER_MATRICULA_INDICADO2", precision = 7, scale = 0)
	public Integer getSerMatriculaIndicado2() {
		return this.serMatriculaIndicado2;
	}

	public void setSerMatriculaIndicado2(Integer serMatriculaIndicado2) {
		this.serMatriculaIndicado2 = serMatriculaIndicado2;
	}
	
	@Column(name = "IND_PARTO_INDUZIDO", nullable = true, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndicadorPartoInduzido() {
		return indicadorPartoInduzido;
	}

	public void setIndicadorPartoInduzido(Boolean indicadorPartoInduzido) {
		this.indicadorPartoInduzido = indicadorPartoInduzido;
	}

	@Column(name = "SER_VIN_CODIGO_INDICADO2", precision = 3, scale = 0)
	public Short getSerVinCodigoIndicado2() {
		return this.serVinCodigoIndicado2;
	}

	public void setSerVinCodigoIndicado2(Short serVinCodigoIndicado2) {
		this.serVinCodigoIndicado2 = serVinCodigoIndicado2;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields  {

		ID("id"),
		MCO_GESTACOES("mcoGestacoes"),
		DTHRINI_CTG("dthriniCtg"),
		INDICACOES_CTG("indicacoesCtg"),
		TIPO_PARTO("tipoParto"),
		OBSERVACAO("observacao"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		INA_SEQ("inaSeq"),
		JUSTIFICATIVA("justificativa"),
		SER_MATRICULA_INDICADO("serMatriculaIndicado"),
		SER_VIN_CODIGO_INDICADO("serVinCodigoIndicado"),
		SER_MATRICULA_INDICADO2("serMatriculaIndicado2"),
		SER_VIN_CODIGO_INDICADO2("serVinCodigoIndicado2"),
		
		GSO_PAC_CODIGO("id.pacCodigo"), 
		GSO_SEQP("id.seqp");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof McoTrabPartos)) {
			return false;
		}
		McoTrabPartos other = (McoTrabPartos) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

}
