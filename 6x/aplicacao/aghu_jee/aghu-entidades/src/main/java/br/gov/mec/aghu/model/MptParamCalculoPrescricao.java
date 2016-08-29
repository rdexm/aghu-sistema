package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity

@Table(name = "MPT_PARAM_CALCULO_PRESCRICOES", schema = "AGH")
public class MptParamCalculoPrescricao extends BaseEntityId<MptParamCalculoPrescricaoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8951108497275061027L;
	private MptParamCalculoPrescricaoId id;
	private BigDecimal auc;
	private Date criadoEm;
	private BigDecimal sc;
	private String indSituacao;
	private BigDecimal creatininaSerica;
	private BigDecimal dce;
	private AipAlturaPacientes aipAlturaPaciente;
	private AipPesoPacientes aipPesoPaciente;
	private Integer serMatricula;
	private Short serVinCodigo;
	private BigDecimal scCalculada;
	private BigDecimal dceCalculada;
	private String formulaSc;
	private String sexo;
	private Short idade;
	private Integer idadeEmMeses;

	public MptParamCalculoPrescricao() {
	}

	public MptParamCalculoPrescricao(MptParamCalculoPrescricaoId id,
			String indSituacao, Integer serMatricula, Short serVinCodigo) {
		this.id = id;
		this.indSituacao = indSituacao;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}



	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 4, scale = 0)) })
	public MptParamCalculoPrescricaoId getId() {
		return this.id;
	}

	public void setId(MptParamCalculoPrescricaoId id) {
		this.id = id;
	}

	@Column(name = "AUC", precision = 5)
	public BigDecimal getAuc() {
		return this.auc;
	}

	public void setAuc(BigDecimal auc) {
		this.auc = auc;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SC", precision = 5)
	public BigDecimal getSc() {
		return this.sc;
	}

	public void setSc(BigDecimal sc) {
		this.sc = sc;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "CREATININA_SERICA", precision = 4, scale = 1)
	public BigDecimal getCreatininaSerica() {
		return this.creatininaSerica;
	}

	public void setCreatininaSerica(BigDecimal creatininaSerica) {
		this.creatininaSerica = creatininaSerica;
	}

	@Column(name = "DCE", precision = 4, scale = 1)
	public BigDecimal getDce() {
		return this.dce;
	}

	public void setDce(BigDecimal dce) {
		this.dce = dce;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns(value = {
		@JoinColumn(name = "ATP_PAC_CODIGO", referencedColumnName = "PAC_CODIGO"),
		@JoinColumn(name = "ATP_CRIADO_EM", referencedColumnName = "CRIADO_EM")
	})
	public AipAlturaPacientes getAipAlturaPaciente() {
		return this.aipAlturaPaciente;
	}

	public void setAipAlturaPaciente(AipAlturaPacientes aipAlturaPaciente) {
		this.aipAlturaPaciente = aipAlturaPaciente;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns(value = {
		@JoinColumn(name = "PEP_PAC_CODIGO", referencedColumnName = "PAC_CODIGO"),
		@JoinColumn(name = "PEP_CRIADO_EM", referencedColumnName = "CRIADO_EM")
	})
	public AipPesoPacientes getAipPesoPaciente() {
		return this.aipPesoPaciente;
	}

	public void setAipPesoPaciente(AipPesoPacientes aipPesoPaciente) {
		this.aipPesoPaciente = aipPesoPaciente;
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

	@Column(name = "SC_CALCULADA", precision = 5)
	public BigDecimal getScCalculada() {
		return this.scCalculada;
	}

	public void setScCalculada(BigDecimal scCalculada) {
		this.scCalculada = scCalculada;
	}

	@Column(name = "DCE_CALCULADA", precision = 4, scale = 1)
	public BigDecimal getDceCalculada() {
		return this.dceCalculada;
	}

	public void setDceCalculada(BigDecimal dceCalculada) {
		this.dceCalculada = dceCalculada;
	}

	@Column(name = "FORMULA_SC", length = 1)
	@Length(max = 1)
	public String getFormulaSc() {
		return this.formulaSc;
	}

	public void setFormulaSc(String formulaSc) {
		this.formulaSc = formulaSc;
	}

	@Column(name = "SEXO", length = 1)
	@Length(max = 1)
	public String getSexo() {
		return this.sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	@Column(name = "IDADE", precision = 3, scale = 0)
	public Short getIdade() {
		return this.idade;
	}

	public void setIdade(Short idade) {
		this.idade = idade;
	}

	@Column(name = "IDADE_EM_MESES", precision = 6, scale = 0)
	public Integer getIdadeEmMeses() {
		return this.idadeEmMeses;
	}

	public void setIdadeEmMeses(Integer idadeEmMeses) {
		this.idadeEmMeses = idadeEmMeses;
	}
	
	public enum Fields {
		PEP_PAC_CODIGO("aipPesoPaciente.id.pacCodigo"), PEP_CRIADO_EM("aipPesoPaciente.id.criadoEm"),
		ATP_PAC_CODIGO("aipAlturaPaciente.id.pacCodigo"), ATP_CRIADO_EM("aipAlturaPaciente.id.criadoEm");

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
		if (!(obj instanceof MptParamCalculoPrescricao)) {
			return false;
		}
		MptParamCalculoPrescricao other = (MptParamCalculoPrescricao) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
