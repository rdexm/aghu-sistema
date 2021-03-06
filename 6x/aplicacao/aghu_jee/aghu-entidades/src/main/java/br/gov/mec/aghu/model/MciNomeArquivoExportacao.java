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
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * MciNomeArquivoExportacao generated by hbm2java
 */
@Entity
@SequenceGenerator(name="mciNaeSq1", sequenceName="AGH.MCI_NAE_SQ1", allocationSize = 1)
@Table(name = "MCI_NOME_ARQUIVO_EXPORTACOES", schema = "AGH")
public class MciNomeArquivoExportacao extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8808785178011900243L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidores;
	private String nome;
	private Date criadoEm;
	private String indSituacao;
	private Set<MciLinhaExportacaoDado> mciLinhaExportacaoDadoes = new HashSet<MciLinhaExportacaoDado>(0);

	public MciNomeArquivoExportacao() {
	}

	public MciNomeArquivoExportacao(Short seq, RapServidores rapServidores, String nome, Date criadoEm, String indSituacao) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.nome = nome;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	public MciNomeArquivoExportacao(Short seq, RapServidores rapServidores, String nome, Date criadoEm, String indSituacao,
			Set<MciLinhaExportacaoDado> mciLinhaExportacaoDadoes) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.nome = nome;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.mciLinhaExportacaoDadoes = mciLinhaExportacaoDadoes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mciNaeSq1")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "NOME", nullable = false, length = 60)
	@Length(max = 60)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mciNomeArquivoExportacao")
	public Set<MciLinhaExportacaoDado> getMciLinhaExportacaoDadoes() {
		return this.mciLinhaExportacaoDadoes;
	}

	public void setMciLinhaExportacaoDadoes(Set<MciLinhaExportacaoDado> mciLinhaExportacaoDadoes) {
		this.mciLinhaExportacaoDadoes = mciLinhaExportacaoDadoes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		NOME("nome"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		MCI_LINHA_EXPORTACAO_DADOES("mciLinhaExportacaoDadoes");

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
		if (!(obj instanceof MciNomeArquivoExportacao)) {
			return false;
		}
		MciNomeArquivoExportacao other = (MciNomeArquivoExportacao) obj;
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
