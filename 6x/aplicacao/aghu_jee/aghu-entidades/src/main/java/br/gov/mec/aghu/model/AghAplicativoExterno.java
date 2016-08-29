package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
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
@Table(name = "AGH_APLICATIVOS_EXTERNOS", schema = "AGH")
public class AghAplicativoExterno extends BaseEntityCodigo<String> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3067497805194591207L;
	private String codigo;
	private Integer version;
	private String descricao;
	private Date alteradoEm;
	private Short versao;
	private byte[] aplicativo;

	public AghAplicativoExterno() {
	}

	public AghAplicativoExterno(String codigo, String descricao, Date alteradoEm, Short versao, byte[] aplicativo) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.alteradoEm = alteradoEm;
		this.versao = versao;
		this.aplicativo = aplicativo;
	}

	@Id
	@Column(name = "CODIGO", unique = true, nullable = false, length = 20)
	@Length(max = 20)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
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

	@Column(name = "DESCRICAO", nullable = false, length = 100)
	@Length(max = 100)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", nullable = false, length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "VERSAO", nullable = false)
	public Short getVersao() {
		return this.versao;
	}

	public void setVersao(Short versao) {
		this.versao = versao;
	}

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "APLICATIVO", nullable = false)
	public byte[] getAplicativo() {
		return this.aplicativo;
	}

	public void setAplicativo(byte[] aplicativo) {
		this.aplicativo = aplicativo;
	}

	public enum Fields {

		CODIGO("codigo"),
		VERSION("version"),
		DESCRICAO("descricao"),
		ALTERADO_EM("alteradoEm"),
		VERSAO("versao"),
		APLICATIVO("aplicativo");

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
		if (!(obj instanceof AghAplicativoExterno)) {
			return false;
		}
		AghAplicativoExterno other = (AghAplicativoExterno) obj;
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
