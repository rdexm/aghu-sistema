package br.gov.mec.aghu.model;

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
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;
import br.gov.mec.aghu.dominio.DominioSituacao;


@Entity
@SequenceGenerator(name = "aghSamisSq1", sequenceName = "AGH.AGH_SAMIS_SQ1", allocationSize = 1)
@Table(name = "AGH_SAMIS", schema = "AGH")
public class AghSamis extends BaseEntityCodigo<Short> implements java.io.Serializable {

	private static final long serialVersionUID = 4343268590269924702L;

	private Short codigo;
	private String descricao;
	private DominioSituacao indAtivo;
	private RapServidores servidor;
	private Integer version;
	private Date criadoEm;

	public AghSamis() {
	}

	public AghSamis(Short codigo, String descricao, DominioSituacao indAtivo) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.indAtivo = indAtivo;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghSamisSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@NotNull
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(DominioSituacao indAtivo) {
		this.indAtivo = indAtivo;
	}
	

	/**
	 * Campo sintético criado para mapear diretamente este dominio booleano em
	 * um componente selectOneCheckBox
	 * 
	 */
	@Transient
	public boolean isAtivo() {
		if (getIndAtivo() != null) {
			return getIndAtivo() == DominioSituacao.A;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setAtivo(boolean valor) {
		setIndAtivo(DominioSituacao.getInstance(valor));
	}

	@JoinColumns ( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@NotNull
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores sevidor) {
		this.servidor = sevidor;
	}

	@Transient
	public String getDescricaoAtivo() {
		return isAtivo() ? "Ativo" : "Inativo";
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		CODIGO("codigo"), DESCRICAO("descricao"), IND_ATIVO("indAtivo"), CRIADO_EM(
				"criadoEm"), SERVIDOR("servidor");

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
		result = prime * result
				+ ((getCodigo() == null) ? 0 : getCodigo().hashCode());
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
		if (!(obj instanceof AghSamis)) {
			return false;
		}
		AghSamis other = (AghSamis) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}

}