package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name="PTM_EDIFICACOES", schema = "AGH")
@SequenceGenerator(name="ptmEdificacaoSeq", sequenceName="AGH.PTM_EDI_SQ1", allocationSize = 1)
public class PtmEdificacao extends BaseEntitySeq<Integer> implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7925295328744195387L;

	private Integer seq;
	private DominioSituacao situacao;
	private String nome;
	private String descricao;
	private Date dtCriacao;
	private Date dtAlteradoEm;
	private RapServidores servidor;
	private RapServidores servidorAlterado;
	private AipLogradouros aipLogradouros;
	private PtmBemPermanentes ptmBemPermanentes;
	private Double longitude;
	private Double latitude;
	private Integer numero;
	private String complemento;
	
	private Integer version;

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmEdificacaoSeq")
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable=false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	
	@Column(name="NOME", length= 20, nullable=false)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "DESCRICAO", length = 50, nullable=false)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "DT_CRIACAO", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtCriacao() {
		return dtCriacao;
	}

	public void setDtCriacao(Date dtCriacao) {
		this.dtCriacao = dtCriacao;
	}

	@Column(name = "DT_ALTERADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAlteradoEm() {
		return dtAlteradoEm;
	}

	public void setDtAlteradoEm(Date dtAlteradoEm) {
		this.dtAlteradoEm = dtAlteradoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="BPE_SEQ", nullable=false)
	public PtmBemPermanentes getPtmBemPermanentes() {
		return ptmBemPermanentes;
	}

	public void setPtmBemPermanentes(PtmBemPermanentes ptmBemPermanentes) {
		this.ptmBemPermanentes = ptmBemPermanentes;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="LGR_SEQ", nullable=false)
	public AipLogradouros getAipLogradouros() {
		return aipLogradouros;
	}

	public void setAipLogradouros(AipLogradouros aipLogradouros) {
		this.aipLogradouros = aipLogradouros;
	}
	
	@Column(name = "NUMERO", nullable = false)
	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column(name = "COMPLEMENTO", nullable = false)
	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(name="LONGITUDE", nullable = false)
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Column(name="LATITUDE", nullable = false)
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA_ALTERADO_POR", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO_POR", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlterado() {
		return servidorAlterado;
	}

	public void setServidorAlterado(RapServidores servidorAlterado) {
		this.servidorAlterado = servidorAlterado;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {

		SEQ("seq"),
		SITUACAO("situacao"),
		MENSAGEM("mensagem"),
		SERVIDOR("servidor"),
		LGR_SEQ("aipLogradouros"),
		BPE_SEQ("ptmBemPermanentes"),
		VERSION("version"),
		NOME("nome"),
		DESCRICAO("descricao"),
		LATITUDE("latitude"),
		LONGITUDE("longitude"),
		NUMERO("numero"),
		COMPLEMENTO("complemento"),
		BPESEQ("ptmBemPermanentes.seq"),
		BPE_NUM_BEM("ptmBemPermanentes.numeroBem")
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
//	// ##### GeradorEqualsHashCodeMain #####
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
		if (!(obj instanceof PtmEdificacao)) {
			return false;
		}
		PtmEdificacao other = (PtmEdificacao) obj;
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
