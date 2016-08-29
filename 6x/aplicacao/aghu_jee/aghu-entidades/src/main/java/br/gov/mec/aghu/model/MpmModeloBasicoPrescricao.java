package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="mpmMdbSq1", sequenceName="AGH.MPM_MDB_SQ1", allocationSize = 1)
@Table(name = "MPM_MOD_BASIC_PRESCRICOES", schema = "AGH")
public class MpmModeloBasicoPrescricao extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8705890206218654957L;

	private Integer seq;

	@Deprecated
	private Short tipo;

	private RapServidores servidor;
	private RapServidores servidorDigitado;
	private String descricao;
	private Date criadoEm;
	private Boolean indPublico;

	private Set<MpmModeloBasicoDieta> modeloDietas = new HashSet<MpmModeloBasicoDieta>(
			0);
	private Set<MpmModeloBasicoMedicamento> modeloMedicamentos = new HashSet<MpmModeloBasicoMedicamento>(
			0);
	private Set<MpmModeloBasicoProcedimento> modeloProcedimentos = new HashSet<MpmModeloBasicoProcedimento>(
			0);

	private Set<MpmModeloBasicoCuidado> modeloCuidados = new HashSet<MpmModeloBasicoCuidado>(
			0);

	private Integer version;
	
	// construtores
	
	public MpmModeloBasicoPrescricao() {
	}

	public MpmModeloBasicoPrescricao(Integer seq) {
		this.seq = seq;
	}

	// getters & setters

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmMdbSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	/**
	 * Retorna o tipo deste modelo básico.
	 * 
	 * @deprecated apesar da coluna estar definida na tabela, não está mais
	 *             sendo utilizada.
	 * @return
	 */
	@Deprecated
	@Column(name = "TMB_SEQ", nullable = true)
	public Short getTipo() {
		return tipo;
	}

	@Deprecated
	public void setTipo(Short tipo) {
		this.tipo = tipo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_DIGITADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_DIGITADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorDigitado() {
		return servidorDigitado;
	}

	public void setServidorDigitado(RapServidores servidorDigitado) {
		this.servidorDigitado = servidorDigitado;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modeloBasicoPrescricao")
	public Set<MpmModeloBasicoDieta> getModeloDietas() {
		return modeloDietas;
	}

	public void setModeloDietas(Set<MpmModeloBasicoDieta> modeloDietas) {
		this.modeloDietas = modeloDietas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modeloBasicoPrescricao")
	public Set<MpmModeloBasicoMedicamento> getModeloMedicamentos() {
		return modeloMedicamentos;
	}

	public void setModeloMedicamentos(
			Set<MpmModeloBasicoMedicamento> modeloMedicamentos) {
		this.modeloMedicamentos = modeloMedicamentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modeloBasicoPrescricao")
	public Set<MpmModeloBasicoProcedimento> getModeloProcedimentos() {
		return modeloProcedimentos;
	}

	public void setModeloProcedimentos(
			Set<MpmModeloBasicoProcedimento> modeloProcedimentos) {
		this.modeloProcedimentos = modeloProcedimentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modeloBasicoPrescricao")
	public Set<MpmModeloBasicoCuidado> getModeloCuidados() {
		return modeloCuidados;
	}

	public void setModeloCuidados(Set<MpmModeloBasicoCuidado> modeloCuidados) {
		this.modeloCuidados = modeloCuidados;
	}
	
	@Column(name = "IND_PUBLICO", nullable = false, length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPublico() {
		return indPublico;
	}

	public void setIndPublico(Boolean indPublico) {
		this.indPublico = indPublico;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	
	// outros
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmModeloBasicoPrescricao)) {
			return false;
		}
		MpmModeloBasicoPrescricao castOther = (MpmModeloBasicoPrescricao) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
	
	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validarModeloPulico() {
		
		if (this.indPublico == null) {
			
			this.indPublico = false;
			
		}

	}

	public enum Fields {
		SEQ("seq"), DESCRICAO("descricao"), // 
		CRIADO_EM("criadoEm"), TIPO("tipo"), // 
		SERVIDOR("servidor"), SERVIDOR_DIGITADO("servidorDigitado"), IND_PUBLICO("indPublico");

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