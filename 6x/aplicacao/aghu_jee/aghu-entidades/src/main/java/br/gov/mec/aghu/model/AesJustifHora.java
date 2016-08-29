package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(name = "AES_JUSTIF_HORAS", schema = "AGH")
public class AesJustifHora extends BaseEntityCodigo<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6243621152420192037L;
	private Short codigo;
	private Integer version;
	private String descricao;
	private Set<AesJustifHoraExced> aesJustifHoraExceds = new HashSet<AesJustifHoraExced>(0);

	public AesJustifHora() {
	}

	public AesJustifHora(Short codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public AesJustifHora(Short codigo, String descricao, Set<AesJustifHoraExced> aesJustifHoraExceds) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.aesJustifHoraExceds = aesJustifHoraExceds;
	}

	@Id
	@Column(name = "CODIGO", unique = true, nullable = false)
	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
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

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aesJustifHora")
	public Set<AesJustifHoraExced> getAesJustifHoraExceds() {
		return this.aesJustifHoraExceds;
	}

	public void setAesJustifHoraExceds(Set<AesJustifHoraExced> aesJustifHoraExceds) {
		this.aesJustifHoraExceds = aesJustifHoraExceds;
	}

	public enum Fields {

		CODIGO("codigo"),
		VERSION("version"),
		DESCRICAO("descricao"),
		AES_JUSTIF_HORA_EXCEDS("aesJustifHoraExceds");

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
		if (!(obj instanceof AesJustifHora)) {
			return false;
		}
		AesJustifHora other = (AesJustifHora) obj;
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
