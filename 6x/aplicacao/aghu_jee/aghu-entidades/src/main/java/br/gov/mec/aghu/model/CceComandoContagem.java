package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

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
@Table(name = "CCE_COMANDOS_CONTAGEM", schema = "AGH")
public class CceComandoContagem extends BaseEntityId<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1182049942226091713L;
	private Integer id;
	private Integer version;
	private Short ord;
	private String cab;
	private String data;
	private String term;

	public CceComandoContagem() {
	}

	public CceComandoContagem(Integer id, Short ord, String cab, String data, String term) {
		this.id = id;
		this.ord = ord;
		this.cab = cab;
		this.data = data;
		this.term = term;
	}

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "ORD", nullable = false)
	public Short getOrd() {
		return this.ord;
	}

	public void setOrd(Short ord) {
		this.ord = ord;
	}

	@Column(name = "CAB", nullable = false, length = 2000)
	@Length(max = 2000)
	public String getCab() {
		return this.cab;
	}

	public void setCab(String cab) {
		this.cab = cab;
	}

	@Column(name = "DATA", nullable = false, length = 2000)
	@Length(max = 2000)
	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Column(name = "TERM", nullable = false, length = 1)
	@Length(max = 1)
	public String getTerm() {
		return this.term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		ORD("ord"),
		CAB("cab"),
		DATA("data"),
		TERM("term");

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
		if (!(obj instanceof CceComandoContagem)) {
			return false;
		}
		CceComandoContagem other = (CceComandoContagem) obj;
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
