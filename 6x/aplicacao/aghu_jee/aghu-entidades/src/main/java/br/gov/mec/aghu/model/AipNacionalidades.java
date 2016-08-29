package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@Table(name = "AIP_NACIONALIDADES", schema = "AGH")
@SequenceGenerator(name="AIP_NACIONALIDADES_SEQ", sequenceName="AGH.AIP_NAC_SQ1", allocationSize = 1)
public class AipNacionalidades extends BaseEntityCodigo<Integer> implements java.io.Serializable {
	
	private static final long serialVersionUID = 7371014623708945373L;
	
	/**
	 * Chave primária na base de dados.
	 */
	private Integer codigo;
	/**
	 * Sigla da nacionalidade.
	 */
	private String sigla;
	/**
	 * Descrição da nacionalidade.
	 */
	private String descricao;
	/**
	 * 
	 */
	private DominioSituacao indAtivo;

	private Integer version;

	public AipNacionalidades() {
	}

	public AipNacionalidades(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public AipNacionalidades(Integer codigo, String sigla, String descricao,
			DominioSituacao indAtivo) {
		this.codigo = codigo;
		this.sigla = sigla;
		this.descricao = descricao;
		this.indAtivo = indAtivo;
	}

	@Id
	@Column(name = "CODIGO", nullable = false, precision = 3, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AIP_NACIONALIDADES_SEQ")
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "SIGLA", unique = true, length = 3)
	@Length(max = 3)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "DESCRICAO", unique = true, nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_ATIVO", length=1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndAtivo() {
		return this.indAtivo;
	}

	public void setIndAtivo(DominioSituacao indAtivo) {
		this.indAtivo = indAtivo;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * Campo sintético criado para mapear diretamente este dominio booleano em
	 * um componente selectOneCheckBox
	 * 
	 * @author jvaranda
	 * @return
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

	@Transient
	public String getDescricaoAtivo() {
		return isAtivo() ? "Sim" : "Não";
	}

	public enum Fields {
		CODIGO("codigo"), SIGLA("sigla"), DESCRICAO("descricao"), IND_ATIVO(
				"indAtivo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

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
		if (!(obj instanceof AipNacionalidades) ){
			return false;
		}
		AipNacionalidades other = (AipNacionalidades) obj;
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
