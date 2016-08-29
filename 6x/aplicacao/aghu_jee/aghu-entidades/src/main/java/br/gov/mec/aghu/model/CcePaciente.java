package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "CCE_PACIENTES", schema = "AGH")
public class CcePaciente extends BaseEntityCodigo<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2051754201352849226L;
	private Long codigo;
	private Integer version;
	private String nome;
	private String sexo;
	private Date dataNascimento;
	private Set<CceResultadoExame> cceResultadoExamees = new HashSet<CceResultadoExame>(0);
	private Set<CceAmostraContagem> cceAmostraContagems = new HashSet<CceAmostraContagem>(0);

	public CcePaciente() {
	}

	public CcePaciente(Long codigo, String nome) {
		this.codigo = codigo;
		this.nome = nome;
	}

	public CcePaciente(Long codigo, String nome, String sexo, Date dataNascimento, Set<CceResultadoExame> cceResultadoExamees,
			Set<CceAmostraContagem> cceAmostraContagems) {
		this.codigo = codigo;
		this.nome = nome;
		this.sexo = sexo;
		this.dataNascimento = dataNascimento;
		this.cceResultadoExamees = cceResultadoExamees;
		this.cceAmostraContagems = cceAmostraContagems;
	}

	@Id
	@Column(name = "CODIGO", unique = true, nullable = false)
	public Long getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "NOME", nullable = false, length = 100)
	@Length(max = 100)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "SEXO", length = 1)
	@Length(max = 1)
	public String getSexo() {
		return this.sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_NASCIMENTO", length = 29)
	public Date getDataNascimento() {
		return this.dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ccePaciente")
	public Set<CceResultadoExame> getCceResultadoExamees() {
		return this.cceResultadoExamees;
	}

	public void setCceResultadoExamees(Set<CceResultadoExame> cceResultadoExamees) {
		this.cceResultadoExamees = cceResultadoExamees;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ccePaciente")
	public Set<CceAmostraContagem> getCceAmostraContagems() {
		return this.cceAmostraContagems;
	}

	public void setCceAmostraContagems(Set<CceAmostraContagem> cceAmostraContagems) {
		this.cceAmostraContagems = cceAmostraContagems;
	}

	public enum Fields {

		CODIGO("codigo"),
		VERSION("version"),
		NOME("nome"),
		SEXO("sexo"),
		DATA_NASCIMENTO("dataNascimento"),
		CCE_RESULTADO_EXAMEES("cceResultadoExamees"),
		CCE_AMOSTRA_CONTAGEMS("cceAmostraContagems");

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
		if (!(obj instanceof CcePaciente)) {
			return false;
		}
		CcePaciente other = (CcePaciente) obj;
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
