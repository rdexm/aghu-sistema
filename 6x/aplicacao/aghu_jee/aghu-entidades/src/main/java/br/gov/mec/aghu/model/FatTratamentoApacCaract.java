package br.gov.mec.aghu.model;

// Generated 15/03/2011 13:46:58 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


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
@Table(name = "FAT_TRATAMENTO_APAC_CARACTS", schema = "AGH")
public class FatTratamentoApacCaract extends BaseEntityId<FatTratamentoApacCaractId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1464739529920952545L;
	private FatTratamentoApacCaractId id;
	private Integer valorNumerico;
	private String valorChar;
	private Date valorData;

	public FatTratamentoApacCaract() {
	}

	public FatTratamentoApacCaract(FatTratamentoApacCaractId id) {
		this.id = id;
	}

	public FatTratamentoApacCaract(FatTratamentoApacCaractId id,
			Integer valorNumerico, String valorChar, Date valorData) {
		this.id = id;
		this.valorNumerico = valorNumerico;
		this.valorChar = valorChar;
		this.valorData = valorData;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "iphPhoSeq", column = @Column(name = "IPH_PHO_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "iphSeq", column = @Column(name = "IPH_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "cihIphPhoSeq", column = @Column(name = "CIH_IPH_PHO_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "cihIphSeq", column = @Column(name = "CIH_IPH_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "cihTctSeq", column = @Column(name = "CIH_TCT_SEQ", nullable = false, precision = 22, scale = 0)) })
	public FatTratamentoApacCaractId getId() {
		return this.id;
	}

	public void setId(FatTratamentoApacCaractId id) {
		this.id = id;
	}

	@Column(name = "VALOR_NUMERICO", precision = 22, scale = 0)
	public Integer getValorNumerico() {
		return this.valorNumerico;
	}

	public void setValorNumerico(Integer valorNumerico) {
		this.valorNumerico = valorNumerico;
	}

	@Column(name = "VALOR_CHAR", length = 240)
	@Length(max = 240)
	public String getValorChar() {
		return this.valorChar;
	}

	public void setValorChar(String valorChar) {
		this.valorChar = valorChar;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "VALOR_DATA", length = 7)
	public Date getValorData() {
		return this.valorData;
	}

	public void setValorData(Date valorData) {
		this.valorData = valorData;
	}

	public enum Fields {

		ID("id"),
		VALOR_NUMERICO("valorNumerico"),
		VALOR_CHAR("valorChar"),
		VALOR_DATA("valorData");

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
		if (!(obj instanceof FatTratamentoApacCaract)) {
			return false;
		}
		FatTratamentoApacCaract other = (FatTratamentoApacCaract) obj;
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
