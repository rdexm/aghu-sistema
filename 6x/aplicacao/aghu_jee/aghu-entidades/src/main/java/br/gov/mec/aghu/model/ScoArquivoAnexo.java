package br.gov.mec.aghu.model;

import java.math.BigInteger;
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
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name = "scoAqvSq1", sequenceName = "AGH.SCO_AQV_SQ1", allocationSize = 1)
@Table(name = "SCO_ARQUIVOS_ANEXOS", schema = "AGH")
public class ScoArquivoAnexo extends BaseEntitySeq<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2709018877114077590L;

	
	/**
	 * Chave primária da base de dados, obtida via sequence.
	 */
	private Long seq;

	
	/**
	 * Tipo do processo onde será anexado o arquivo
	 */
	private DominioOrigemSolicitacaoSuprimento tpOrigem;

	
	/**
	 * Número da solicitação de compra
	 */
	private BigInteger numero;
	
	/**
	 * Descrição do arquivo
	 */
	private String descricao;
	
	/**
	 * Nome do arquivo
	 */
	private String arquivo;
	
	
	/**
	 * Arquivo anexado à solicitação de compra
	 */
	private byte[] anexo;	
	
	/**
	 * Data da inclusão do arquivo
	 */	
	private Date dtInclusao;
		
	/**
	 * Usuário que incluiu o arquivo na base de dados
	 */
	private RapServidores usuario;
	
	/**
	 * Controle de concorrência otimista JPA
	 */
	private Integer version;

	/**
	 * Campo Transient, usado para sinalizar item sendo editado na grade da tela
	 */	
	private Boolean emEdicao = Boolean.FALSE;

	public ScoArquivoAnexo(){		
	}
	
	@Id	
	@Column(name = "SEQ", unique = true, nullable = false)	
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoAqvSq1")
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
	@Column(name = "TP_ORIGEM", nullable = false, length = 25)
	@Enumerated(EnumType.STRING)
	public DominioOrigemSolicitacaoSuprimento getTpOrigem() {
		return tpOrigem;
	}

	public void setTpOrigem(DominioOrigemSolicitacaoSuprimento tpOrigem) {
		this.tpOrigem = tpOrigem;
	}
	
	@Column(name = "NUMERO", nullable = false)
	public BigInteger getNumero() {
		return numero;
	}

	public void setNumero(BigInteger numero) {
		this.numero = numero;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 50)
	@Length(max = 50, message="Descrição não pode ter mais que 50 caracteres.")	
	public String getDescricao(){
		return this.descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	@Column(name = "ARQUIVO", nullable = false, length = 100)
	@Length(max = 100, message="Nome do arquivo não pode ter mais que 100 caracteres")		
	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "ANEXO", nullable = false)
	public byte[] getAnexo() {
		return anexo;
	}

	public void setAnexo(byte[] anexo) {
		this.anexo = anexo;
	}
	
	@Column(name = "DT_INCLUSAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtInclusao() {
		return dtInclusao;
	}

	public void setDtInclusao(Date dtInclusao) {
		this.dtInclusao = dtInclusao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			       @JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getUsuario() {
		return usuario;
	}

	public void setUsuario(RapServidores usuario) {
		this.usuario = usuario;
	}

	@Column(name = "VERSION")
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Transient
	public Boolean getEmEdicao() {
		return emEdicao;
	}
	
	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}	

	public enum Fields {
		SEQ("seq"),
		TPORIGEM("tpOrigem"), 
		NUMERO("numero"), 
		NOME("nome"), 
		DESCRICAO("descricao"), 
		ARQUIVO("arquivo"),
		ANEXO("anexo"), 
		DT_INCLUSAO("dtInclusao"), 
		USUARIO("usuario"), 
		VERSION("version");

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
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ScoArquivoAnexo)) {
			return false;
		}
		ScoArquivoAnexo other = (ScoArquivoAnexo) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
	
	
}







