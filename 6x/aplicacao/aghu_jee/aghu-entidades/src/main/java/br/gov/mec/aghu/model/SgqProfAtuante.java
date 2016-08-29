package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "SGQ_PROF_ATUANTES", schema = "AGH")
public class SgqProfAtuante extends BaseEntityId<SgqProfAtuanteId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4266713369809326788L;
	private SgqProfAtuanteId id;
	private Integer version;
	private SgqQuery sgqQuery;
	private RapPessoasFisicas rapPessoasFisicas;
	private String atuacao;
	private Short tempo;
	private String indTempo;
	private String dtInicioAtuacao;

	public SgqProfAtuante() {
	}

	public SgqProfAtuante(SgqProfAtuanteId id, SgqQuery sgqQuery, RapPessoasFisicas rapPessoasFisicas, String atuacao,
			Short tempo, String indTempo, String dtInicioAtuacao) {
		this.id = id;
		this.sgqQuery = sgqQuery;
		this.rapPessoasFisicas = rapPessoasFisicas;
		this.atuacao = atuacao;
		this.tempo = tempo;
		this.indTempo = indTempo;
		this.dtInicioAtuacao = dtInicioAtuacao;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "pesCodigo", column = @Column(name = "PES_CODIGO", nullable = false)),
			@AttributeOverride(name = "qrySeq", column = @Column(name = "QRY_SEQ", nullable = false)) })
	public SgqProfAtuanteId getId() {
		return this.id;
	}

	public void setId(SgqProfAtuanteId id) {
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QRY_SEQ", nullable = false, insertable = false, updatable = false)
	public SgqQuery getSgqQuery() {
		return this.sgqQuery;
	}

	public void setSgqQuery(SgqQuery sgqQuery) {
		this.sgqQuery = sgqQuery;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PES_CODIGO", nullable = false, insertable = false, updatable = false)
	public RapPessoasFisicas getRapPessoasFisicas() {
		return this.rapPessoasFisicas;
	}

	public void setRapPessoasFisicas(RapPessoasFisicas rapPessoasFisicas) {
		this.rapPessoasFisicas = rapPessoasFisicas;
	}

	@Column(name = "ATUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getAtuacao() {
		return this.atuacao;
	}

	public void setAtuacao(String atuacao) {
		this.atuacao = atuacao;
	}

	@Column(name = "TEMPO", nullable = false)
	public Short getTempo() {
		return this.tempo;
	}

	public void setTempo(Short tempo) {
		this.tempo = tempo;
	}

	@Column(name = "IND_TEMPO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndTempo() {
		return this.indTempo;
	}

	public void setIndTempo(String indTempo) {
		this.indTempo = indTempo;
	}

	@Column(name = "DT_INICIO_ATUACAO", nullable = false, length = 240)
	@Length(max = 240)
	public String getDtInicioAtuacao() {
		return this.dtInicioAtuacao;
	}

	public void setDtInicioAtuacao(String dtInicioAtuacao) {
		this.dtInicioAtuacao = dtInicioAtuacao;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		SGQ_QUERIES("sgqQuery"),
		RAP_PESSOAS_FISICAS("rapPessoasFisicas"),
		ATUACAO("atuacao"),
		TEMPO("tempo"),
		IND_TEMPO("indTempo"),
		DT_INICIO_ATUACAO("dtInicioAtuacao");

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
		if (!(obj instanceof SgqProfAtuante)) {
			return false;
		}
		SgqProfAtuante other = (SgqProfAtuante) obj;
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
