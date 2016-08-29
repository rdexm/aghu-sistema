package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.validation.constraints.NotNull;

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
@SequenceGenerator(name="mamTcoSq1", sequenceName="AGH.MAM_TCO_SQ1", allocationSize = 1)
@Table(name = "MAM_TIPO_COOPERACOES", schema = "AGH")
public class MamTipoCooperacao extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4440885689262878528L;
	private Short seq;
	private RapServidores rapServidores;
	private String descricao;
	private String cor;
	private String indSituacao;
	private Date criadoEm;
	private Set<MamEmgServEspCoop> mamEmgServEspCoopes = new HashSet<MamEmgServEspCoop>(0);

	public MamTipoCooperacao() {
	}

	public MamTipoCooperacao(Short seq, RapServidores rapServidores, String descricao, String cor, String indSituacao, Date criadoEm) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.cor = cor;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public MamTipoCooperacao(Short seq, RapServidores rapServidores, String descricao, String cor, String indSituacao, Date criadoEm,
			Set<MamEmgServEspCoop> mamEmgServEspCoopes) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.cor = cor;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.mamEmgServEspCoopes = mamEmgServEspCoopes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamTcoSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 45)
	@NotNull
	@Length(max = 45)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "COR", nullable = false, length = 45)
	@NotNull
	@Length(max = 45)
	public String getCor() {
		return this.cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@NotNull
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamTipoCooperacao")
	public Set<MamEmgServEspCoop> getMamEmgServEspCoopes() {
		return this.mamEmgServEspCoopes;
	}

	public void setMamEmgServEspCoopes(Set<MamEmgServEspCoop> mamEmgServEspCoopes) {
		this.mamEmgServEspCoopes = mamEmgServEspCoopes;
	}

	public enum Fields {

		SEQ("seq"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		COR("cor"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		MAM_EMG_SERV_ESP_COOPES("mamEmgServEspCoopes");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

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
		if (!(obj instanceof MamTipoCooperacao)) {
			return false;
		}
		MamTipoCooperacao other = (MamTipoCooperacao) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}

}
