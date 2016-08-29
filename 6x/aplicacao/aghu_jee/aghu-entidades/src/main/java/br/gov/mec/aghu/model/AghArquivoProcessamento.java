package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Range;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "aghSequenceArquivoProcessamento", sequenceName = "AGH.AGH_ARQ_SQ1", allocationSize = 1)
@Table(name = "AGH_ARQUIVOS_PROCESSAMENTO", schema = "AGH")

public class AghArquivoProcessamento extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7344368396044245083L;

	/**
	 * Chave primária da base de dados, obtida via sequence.
	 */
	private Integer seq;

	/**
	 * controlhe de concorrência otimista JPA
	 */
	private Integer version;

	/**
	 * Nome do arquivo
	 */
	private String nome;

	/**
	 * Usuário que incluiu o arquivo na base de dados
	 */
	private RapServidores usuario;

	/**
	 * Arquivo a ser processado.
	 */
	private byte[] arquivo;

	/**
	 * Percentual do arquivo que já foi processado. na inclusão este campo deve
	 * ter valor zero.
	 */
	private Integer percentualProcessado;

	/**
	 * Data e hora de criação do registro.
	 */
	private Date dthrCriadoEm;

	/**
	 * Data e hora início do processamento do arquivo.
	 */
	private Date dthrInicioProcessamento;

	/**
	 * Data e hora do fim do processamento do arquivo.
	 */
	private Date dthrFimProcessamento;

	/**
	 * Data e hora da última vez que o arquivo esteve em processamento.
	 */
	private Date dthrUltimoProcessamento;

	/**
	 * Ordem de processamento do arquivo dentro do processo do qual faz parte.
	 */
	private Integer ordemProcessamento;

	/**
	 * sistema ao qual o arquivo está ligado.
	 */
	private AghSistemas sistema;
	
	

	@Column(name = "ARQUIVO", nullable = false)
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	public byte[] getArquivo() {
		return arquivo;
	}

	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}

	@Column(name = "PERCENTUAL_PROCESSADO", nullable = false)
	@Range(min = 0, max = 100)
	public Integer getPercentualProcessado() {
		return percentualProcessado;
	}

	public void setPercentualProcessado(Integer percentualProcessado) {
		this.percentualProcessado = percentualProcessado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CRIADO_EM")
	public Date getDthrCriadoEm() {
		return dthrCriadoEm;
	}

	public void setDthrCriadoEm(Date dthrInclusao) {
		this.dthrCriadoEm = dthrInclusao;
	}

	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghSequenceArquivoProcessamento")
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer sequencial) {
		this.seq = sequencial;
	}

	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "NOME", nullable = false)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO_PROCESSAMENTO")
	public Date getDthrInicioProcessamento() {
		return dthrInicioProcessamento;
	}

	public void setDthrInicioProcessamento(Date dthrInicioProcessamento) {
		this.dthrInicioProcessamento = dthrInicioProcessamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM_PROCESSAMENTO")
	public Date getDthrFimProcessamento() {
		return dthrFimProcessamento;
	}

	public void setDthrFimProcessamento(Date dthrFimProcessamento) {
		this.dthrFimProcessamento = dthrFimProcessamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ULTIMO_PROCESSAMENTO")
	public Date getDthrUltimoProcessamento() {
		return dthrUltimoProcessamento;
	}

	public void setDthrUltimoProcessamento(Date dthrUltimoProcessamento) {
		this.dthrUltimoProcessamento = dthrUltimoProcessamento;
	}

	@Column(name = "ORDEM_PROCESSAMENTO")
	public Integer getOrdemProcessamento() {
		return ordemProcessamento;
	}

	public void setOrdemProcessamento(Integer ordemProcessamento) {
		this.ordemProcessamento = ordemProcessamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SIS_SIGLA")
	public AghSistemas getSistema() {
		return sistema;
	}

	public void setSistema(AghSistemas sistema) {
		this.sistema = sistema;
	}


	public enum Fields {
		ID("seq"),SISTEMA("sistema"), PERCENTUAL_PROCESSADO("percentualProcessado"), NOME("nome"), DT_CRIACAO("dthrCriadoEm"), DT_INICIO_PROCESSAMENTO(
				"dthrInicioProcessamento"), DT_FIM_PROCESSAMENTO("dthrFimProcessamento"), DT_ULTIMO_PROCESSAMENTO("dthrUltimoProcessamento"), ORDEM("ordemProcessamento") ;

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
		if (!(obj instanceof AghArquivoProcessamento)) {
			return false;
		}
		AghArquivoProcessamento other = (AghArquivoProcessamento) obj;
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