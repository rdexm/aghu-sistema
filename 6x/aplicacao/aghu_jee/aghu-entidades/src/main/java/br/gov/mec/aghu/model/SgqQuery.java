package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "SGQ_QUERIES", schema = "AGH")
public class SgqQuery extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3053648036962504744L;
	private Integer seq;
	private Integer version;
	private RapPessoasFisicas rapPessoasFisicas;
	private String titulo;
	private String descricao;
	private Date dthrSolicitacao;
	private String situacao;
	private String dadosARecuperar;
	private String condicoesAConsiderar;
	private String objetivo;
	private Date previsaoEntrega;
	private String complexidade;
	private Set<SgqOrigem> sgqOrigemes = new HashSet<SgqOrigem>(0);
	private Set<SgqProfAtuante> sgqProfAtuantees = new HashSet<SgqProfAtuante>(0);
	private Set<SgqTabelaUtilizada> sgqTabelaUtilizadaes = new HashSet<SgqTabelaUtilizada>(0);

	public SgqQuery() {
	}

	public SgqQuery(Integer seq, RapPessoasFisicas rapPessoasFisicas, String titulo, String descricao, Date dthrSolicitacao,
			String dadosARecuperar, String condicoesAConsiderar, String objetivo) {
		this.seq = seq;
		this.rapPessoasFisicas = rapPessoasFisicas;
		this.titulo = titulo;
		this.descricao = descricao;
		this.dthrSolicitacao = dthrSolicitacao;
		this.dadosARecuperar = dadosARecuperar;
		this.condicoesAConsiderar = condicoesAConsiderar;
		this.objetivo = objetivo;
	}

	public SgqQuery(Integer seq, RapPessoasFisicas rapPessoasFisicas, String titulo, String descricao, Date dthrSolicitacao,
			String situacao, String dadosARecuperar, String condicoesAConsiderar, String objetivo, Date previsaoEntrega,
			String complexidade, Set<SgqOrigem> sgqOrigemes, Set<SgqProfAtuante> sgqProfAtuantees,
			Set<SgqTabelaUtilizada> sgqTabelaUtilizadaes) {
		this.seq = seq;
		this.rapPessoasFisicas = rapPessoasFisicas;
		this.titulo = titulo;
		this.descricao = descricao;
		this.dthrSolicitacao = dthrSolicitacao;
		this.situacao = situacao;
		this.dadosARecuperar = dadosARecuperar;
		this.condicoesAConsiderar = condicoesAConsiderar;
		this.objetivo = objetivo;
		this.previsaoEntrega = previsaoEntrega;
		this.complexidade = complexidade;
		this.sgqOrigemes = sgqOrigemes;
		this.sgqProfAtuantees = sgqProfAtuantees;
		this.sgqTabelaUtilizadaes = sgqTabelaUtilizadaes;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PES_CODIGO", nullable = false)
	public RapPessoasFisicas getRapPessoasFisicas() {
		return this.rapPessoasFisicas;
	}

	public void setRapPessoasFisicas(RapPessoasFisicas rapPessoasFisicas) {
		this.rapPessoasFisicas = rapPessoasFisicas;
	}

	@Column(name = "TITULO", nullable = false, length = 200)
	@Length(max = 200)
	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 4000)
	@Length(max = 4000)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_SOLICITACAO", nullable = false, length = 29)
	public Date getDthrSolicitacao() {
		return this.dthrSolicitacao;
	}

	public void setDthrSolicitacao(Date dthrSolicitacao) {
		this.dthrSolicitacao = dthrSolicitacao;
	}

	@Column(name = "SITUACAO", length = 1)
	@Length(max = 1)
	public String getSituacao() {
		return this.situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	@Column(name = "DADOS_A_RECUPERAR", nullable = false, length = 4000)
	@Length(max = 4000)
	public String getDadosARecuperar() {
		return this.dadosARecuperar;
	}

	public void setDadosARecuperar(String dadosARecuperar) {
		this.dadosARecuperar = dadosARecuperar;
	}

	@Column(name = "CONDICOES_A_CONSIDERAR", nullable = false, length = 4000)
	@Length(max = 4000)
	public String getCondicoesAConsiderar() {
		return this.condicoesAConsiderar;
	}

	public void setCondicoesAConsiderar(String condicoesAConsiderar) {
		this.condicoesAConsiderar = condicoesAConsiderar;
	}

	@Column(name = "OBJETIVO", nullable = false, length = 4000)
	@Length(max = 4000)
	public String getObjetivo() {
		return this.objetivo;
	}

	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PREVISAO_ENTREGA", length = 29)
	public Date getPrevisaoEntrega() {
		return this.previsaoEntrega;
	}

	public void setPrevisaoEntrega(Date previsaoEntrega) {
		this.previsaoEntrega = previsaoEntrega;
	}

	@Column(name = "COMPLEXIDADE", length = 1)
	@Length(max = 1)
	public String getComplexidade() {
		return this.complexidade;
	}

	public void setComplexidade(String complexidade) {
		this.complexidade = complexidade;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sgqQuery")
	public Set<SgqOrigem> getSgqOrigemes() {
		return this.sgqOrigemes;
	}

	public void setSgqOrigemes(Set<SgqOrigem> sgqOrigemes) {
		this.sgqOrigemes = sgqOrigemes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sgqQuery")
	public Set<SgqProfAtuante> getSgqProfAtuantees() {
		return this.sgqProfAtuantees;
	}

	public void setSgqProfAtuantees(Set<SgqProfAtuante> sgqProfAtuantees) {
		this.sgqProfAtuantees = sgqProfAtuantees;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sgqQuery")
	public Set<SgqTabelaUtilizada> getSgqTabelaUtilizadaes() {
		return this.sgqTabelaUtilizadaes;
	}

	public void setSgqTabelaUtilizadaes(Set<SgqTabelaUtilizada> sgqTabelaUtilizadaes) {
		this.sgqTabelaUtilizadaes = sgqTabelaUtilizadaes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_PESSOAS_FISICAS("rapPessoasFisicas"),
		TITULO("titulo"),
		DESCRICAO("descricao"),
		DTHR_SOLICITACAO("dthrSolicitacao"),
		SITUACAO("situacao"),
		DADOS_A_RECUPERAR("dadosARecuperar"),
		CONDICOES_A_CONSIDERAR("condicoesAConsiderar"),
		OBJETIVO("objetivo"),
		PREVISAO_ENTREGA("previsaoEntrega"),
		COMPLEXIDADE("complexidade"),
		SGQ_ORIGEMES("sgqOrigemes"),
		SGQ_PROF_ATUANTEES("sgqProfAtuantees"),
		SGQ_TABELA_UTILIZADAES("sgqTabelaUtilizadaes");

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
		if (!(obj instanceof SgqQuery)) {
			return false;
		}
		SgqQuery other = (SgqQuery) obj;
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
