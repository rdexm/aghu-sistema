package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * Classe que armazena o log gerado durante o processamento assíncrono de um
 * arquivo. Como é imutável, os métodos setters foram retirados. O estado é
 * passado como parâmetro ao construtor.
 * 
 * @author Geraldo Maciel
 * 
 */
@Entity
@org.hibernate.annotations.Entity(mutable = false)
@SequenceGenerator(name = "aghSequenceArquivoProcessamentoLog", sequenceName = "AGH.AGH_ARL_SQ1", allocationSize = 1)
@Table(name = "AGH_ARQUIVOS_PROCESSAMENTO_LOG", schema = "AGH")
public class AghArquivoProcessamentoLog extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4794796558736220724L;

	public AghArquivoProcessamentoLog() {

	}

	/**
	 * Constrututor com o estado inicial da instância.
	 * 
	 * @param arquivoProcessamento
	 * @param mensagem
	 */
	public AghArquivoProcessamentoLog(
			Integer seqArquivoProcessamento, String mensagem) {
		this.seqArquivoProcessamento = seqArquivoProcessamento;
		this.mensagem = mensagem ;
		this.dthrCriadoEm = new Date();

	}

	/**
	 * Chave primária da base de dados, obtida via sequence.
	 */
	private Integer seq;

	/**
	 * Mensagem do log.
	 */
	private String mensagem;

	/**
	 * Arquivo ao qual a entrada de log está associada.
	 */
	private Integer seqArquivoProcessamento;

	/**
	 * Data e hora de criação do registro.
	 */
	private Date dthrCriadoEm;

	
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghSequenceArquivoProcessamentoLog")
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	@Column(name = "MENSAGEM", nullable = false)
	@Lob
	@Type(type="text")	
	public String getMensagem() {
		return mensagem;
	}

	@Column(name = "ARQUIVO_PROCESSAMENTO_SEQ", nullable = false)
	public Integer getSeqArquivoProcessamento() {
		return seqArquivoProcessamento;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CRIADO_EM")
	public Date getDthrCriadoEm() {
		return dthrCriadoEm;
	}


	public enum Fields {
		SEQ("seq"), MENSAGEM("mensagem"), ID_ARQUIVO("seqArquivoProcessamento"), DTHR_CRIADO_EM("dthrCriadoEm");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	/**
	 * @param seq the seq to set
	 */
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	/**
	 * @param mensagem the mensagem to set
	 */
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	/**
	 * @param arquivoProcessamento the arquivoProcessamento to set
	 */
	public void setSeqArquivoProcessamento(Integer seqArquivoProcessamento) {
		this.seqArquivoProcessamento = seqArquivoProcessamento;
	}

	/**
	 * @param dthrCriadoEm the dthrCriadoEm to set
	 */
	public void setDthrCriadoEm(Date dthrCriadoEm) {
		this.dthrCriadoEm = dthrCriadoEm;
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
		if (!(obj instanceof AghArquivoProcessamentoLog)) {
			return false;
		}
		AghArquivoProcessamentoLog other = (AghArquivoProcessamentoLog) obj;
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