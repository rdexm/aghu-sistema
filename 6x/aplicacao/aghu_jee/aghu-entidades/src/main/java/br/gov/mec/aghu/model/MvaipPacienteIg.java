package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * MvaipPacienteIg generated by hbm2java
 */
@Entity
@Table(name = "MVAIP_PACIENTES_IG", schema = "AGH")
public class MvaipPacienteIg extends BaseEntityCodigo<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5518833696300847638L;
	private Integer codigo;
	private String sexo;
	private Short grauInstrucao;
	private Integer ocpCodigo;
	private Date dtUltInternacao;
	private Date dtUltConsulta;
	private Date dtUltProcedimento;
	private Date dtNascimento;

	public MvaipPacienteIg() {
	}

	public MvaipPacienteIg(Integer codigo, Date dtNascimento) {
		this.codigo = codigo;
		this.dtNascimento = dtNascimento;
	}

	public MvaipPacienteIg(Integer codigo, String sexo, Short grauInstrucao, Integer ocpCodigo, Date dtUltInternacao, Date dtUltConsulta,
			Date dtUltProcedimento, Date dtNascimento) {
		this.codigo = codigo;
		this.sexo = sexo;
		this.grauInstrucao = grauInstrucao;
		this.ocpCodigo = ocpCodigo;
		this.dtUltInternacao = dtUltInternacao;
		this.dtUltConsulta = dtUltConsulta;
		this.dtUltProcedimento = dtUltProcedimento;
		this.dtNascimento = dtNascimento;
	}

	@Id
	@Column(name = "CODIGO", unique = true, nullable = false)
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "SEXO", length = 1)
	@Length(max = 1)
	public String getSexo() {
		return this.sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	@Column(name = "GRAU_INSTRUCAO")
	public Short getGrauInstrucao() {
		return this.grauInstrucao;
	}

	public void setGrauInstrucao(Short grauInstrucao) {
		this.grauInstrucao = grauInstrucao;
	}

	@Column(name = "OCP_CODIGO")
	public Integer getOcpCodigo() {
		return this.ocpCodigo;
	}

	public void setOcpCodigo(Integer ocpCodigo) {
		this.ocpCodigo = ocpCodigo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULT_INTERNACAO", length = 29)
	public Date getDtUltInternacao() {
		return this.dtUltInternacao;
	}

	public void setDtUltInternacao(Date dtUltInternacao) {
		this.dtUltInternacao = dtUltInternacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULT_CONSULTA", length = 29)
	public Date getDtUltConsulta() {
		return this.dtUltConsulta;
	}

	public void setDtUltConsulta(Date dtUltConsulta) {
		this.dtUltConsulta = dtUltConsulta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ULT_PROCEDIMENTO", length = 29)
	public Date getDtUltProcedimento() {
		return this.dtUltProcedimento;
	}

	public void setDtUltProcedimento(Date dtUltProcedimento) {
		this.dtUltProcedimento = dtUltProcedimento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_NASCIMENTO", nullable = false, length = 29)
	public Date getDtNascimento() {
		return this.dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public enum Fields {

		CODIGO("codigo"),
		SEXO("sexo"),
		GRAU_INSTRUCAO("grauInstrucao"),
		OCP_CODIGO("ocpCodigo"),
		DT_ULT_INTERNACAO("dtUltInternacao"),
		DT_ULT_CONSULTA("dtUltConsulta"),
		DT_ULT_PROCEDIMENTO("dtUltProcedimento"),
		DT_NASCIMENTO("dtNascimento");

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
		result = prime * result + ((getCodigo() == null) ? 0 : getCodigo().hashCode());
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
		if (!(obj instanceof MvaipPacienteIg)) {
			return false;
		}
		MvaipPacienteIg other = (MvaipPacienteIg) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
