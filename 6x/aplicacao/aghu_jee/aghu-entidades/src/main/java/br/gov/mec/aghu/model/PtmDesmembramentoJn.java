package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;
/**
 * @author rafael.nascimento
 */

@Entity
@Immutable
@Table(name="PTM_DESMEMBRAMENTO_JN", schema="AGH")
@SequenceGenerator(name="ptmDesmembramentoJnSeq", sequenceName="AGH.PTM_DES_JN_SEQ", allocationSize = 1)
public class PtmDesmembramentoJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3892042220515235174L;
	
	private Integer seq;
	private PtmAvaliacaoTecnica avaliacaoTecnica;
	private String descricao;
	private Short percentual;
	private BigDecimal valor;
	private RapServidores servidor;
	private Date dataCriacao;
	private Integer version;
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmDesmembramentoJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AVT_SEQ", nullable = false)
	public PtmAvaliacaoTecnica getAvaliacaoTecnica() {
		return avaliacaoTecnica;
	}

	public void setAvaliacaoTecnica(PtmAvaliacaoTecnica avaliacaoTecnica) {
		this.avaliacaoTecnica = avaliacaoTecnica;
	}
	
	@Column(name = "DESCRICAO", length = 500)
	@Length(max = 500)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "PERCENTUAL", precision = 3, scale = 0)
	public Short getPercentual() {
		return percentual;
	}

	public void setPercentual(Short percentual) {
		this.percentual = percentual;
	}
	
	@Column(name = "VALOR", precision = 9, scale = 2)
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_CRIACAO", nullable = false)
	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	@Version
	@Column(name = "VERSION", length = 9, nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	
	public enum Fields {
		
		SEQ("seq"),
		DATA_CRIACAO("dataCriacao"),
		SERVIDOR("servidor"),
		MATRICULA_SERVIDOR("servidor.id.matricula"),
		VIN_CODIGO_SERVIDOR("servidor.id.vinCodigo"),
		AVALIACAO_TECNICA("avaliacaoTecnica"),
		AVT_SEQ("avaliacaoTecnica.seq"),
		DESCRICAO("descricao"),
		PERCENTUAL("percentual"),
		VALOR("valor"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERACAO("jnOperacao"),
		JN_USUARIO("jnUsuario"),
		JN_SEQ("jnSeq")
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
}
