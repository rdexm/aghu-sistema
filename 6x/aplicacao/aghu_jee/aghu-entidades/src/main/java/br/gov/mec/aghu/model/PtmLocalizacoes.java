package br.gov.mec.aghu.model;

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

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "PTM_LOCALIZACOES", schema = "AGH")
@SequenceGenerator(name="ptmLocalizacaoSeq", sequenceName="AGH.PTM_LOC_SQ1", allocationSize = 1)
public class PtmLocalizacoes extends BaseEntitySeq<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3768814699953776146L;

	private Long seq;
	private String nome;
	private String descricao;
	private PtmEdificacao edificacao;
	private DominioSituacao indSituacao;
	private FccCentroCustos centroCusto;
	private RapServidores servidor;
	private Date dataCriacao;
	private Date dataAlteradoEm;
	private RapServidores servidorAlteradoPor;
	private Integer version;
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmLocalizacaoSeq")
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name = "NOME", length = 50, nullable = false)
	@Length(max = 50)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "DESCRICAO", length = 250, nullable = false)
	@Length(max = 250)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EDI_SEQ", referencedColumnName = "SEQ")
	public PtmEdificacao getEdificacao() {
		return edificacao;
	}

	public void setEdificacao(PtmEdificacao edificacao) {
		this.edificacao = edificacao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_SEQ", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_CRIACAO", nullable = true)
	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ALTERADO_EM", nullable = true)
	public Date getDataAlteradoEm() {
		return dataAlteradoEm;
	}

	public void setDataAlteradoEm(Date dataAlteradoEm) {
		this.dataAlteradoEm = dataAlteradoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ALTERADO_POR", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO_POR", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlteradoPor() {
		return servidorAlteradoPor;
	}

	public void setServidorAlteradoPor(RapServidores servidorAlteradoPor) {
		this.servidorAlteradoPor = servidorAlteradoPor;
	}
	
	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}
	

	public enum Fields {

		SEQ("seq"),
		NOME("nome"),
		DESCRICAO("descricao"),
		EDIFICACAO("edificacao"),
		EDIFICACAO_SEQ("edificacao.seq"),
		IND_SITUACAO("indSituacao"),
		CENTRO_CUSTO("centroCusto");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
