package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

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
@SequenceGenerator(name="rarTslSq1", sequenceName="AGH.RAR_TSL_SQ1", allocationSize = 1)
@Table(name = "RAR_TIPOS_SELECAO", schema = "AGH")
public class RarTipoSelecao extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6601762410272328066L;
	private Short seq;
	private Integer version;
	private String descricao;
	private Float pesoProva;
	private Float pesoEntrevista;
	private Float pesoCurriculo;
	private String indEscore;
	private Short nroQuestoes;
	private Set<RarSelecao> rarSelecaoes = new HashSet<RarSelecao>(0);

	public RarTipoSelecao() {
	}

	public RarTipoSelecao(Short seq, String descricao, Float pesoProva) {
		this.seq = seq;
		this.descricao = descricao;
		this.pesoProva = pesoProva;
	}

	public RarTipoSelecao(Short seq, String descricao, Float pesoProva, Float pesoEntrevista, Float pesoCurriculo, String indEscore,
			Short nroQuestoes, Set<RarSelecao> rarSelecaoes) {
		this.seq = seq;
		this.descricao = descricao;
		this.pesoProva = pesoProva;
		this.pesoEntrevista = pesoEntrevista;
		this.pesoCurriculo = pesoCurriculo;
		this.indEscore = indEscore;
		this.nroQuestoes = nroQuestoes;
		this.rarSelecaoes = rarSelecaoes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rarTslSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 45)
	@Length(max = 45)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "PESO_PROVA", nullable = false, precision = 8, scale = 8)
	public Float getPesoProva() {
		return this.pesoProva;
	}

	public void setPesoProva(Float pesoProva) {
		this.pesoProva = pesoProva;
	}

	@Column(name = "PESO_ENTREVISTA", precision = 8, scale = 8)
	public Float getPesoEntrevista() {
		return this.pesoEntrevista;
	}

	public void setPesoEntrevista(Float pesoEntrevista) {
		this.pesoEntrevista = pesoEntrevista;
	}

	@Column(name = "PESO_CURRICULO", precision = 8, scale = 8)
	public Float getPesoCurriculo() {
		return this.pesoCurriculo;
	}

	public void setPesoCurriculo(Float pesoCurriculo) {
		this.pesoCurriculo = pesoCurriculo;
	}

	@Column(name = "IND_ESCORE", length = 1)
	@Length(max = 1)
	public String getIndEscore() {
		return this.indEscore;
	}

	public void setIndEscore(String indEscore) {
		this.indEscore = indEscore;
	}

	@Column(name = "NRO_QUESTOES")
	public Short getNroQuestoes() {
		return this.nroQuestoes;
	}

	public void setNroQuestoes(Short nroQuestoes) {
		this.nroQuestoes = nroQuestoes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarTipoSelecao")
	public Set<RarSelecao> getRarSelecaoes() {
		return this.rarSelecaoes;
	}

	public void setRarSelecaoes(Set<RarSelecao> rarSelecaoes) {
		this.rarSelecaoes = rarSelecaoes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		DESCRICAO("descricao"),
		PESO_PROVA("pesoProva"),
		PESO_ENTREVISTA("pesoEntrevista"),
		PESO_CURRICULO("pesoCurriculo"),
		IND_ESCORE("indEscore"),
		NRO_QUESTOES("nroQuestoes"),
		RAR_SELECAOES("rarSelecaoes");

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof RarTipoSelecao)) {
			return false;
		}
		RarTipoSelecao other = (RarTipoSelecao) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
