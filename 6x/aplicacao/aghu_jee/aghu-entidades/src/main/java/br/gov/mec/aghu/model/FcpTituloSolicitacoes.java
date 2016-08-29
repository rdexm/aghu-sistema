package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "fcpTxsSq1", sequenceName = "AGH.FCP_TXS_SQ1", allocationSize = 1)
@Table(name = "FCP_TTL_X_SOLICITACOES", schema = "AGH")
public class FcpTituloSolicitacoes extends BaseEntitySeq<Integer> implements Serializable{

	private static final long serialVersionUID = -2557943074862024152L;

	private Integer seq;
	private Double valor;
	private Integer slsNumero;
	private Integer slcNumero;
	private Integer ttlSeq;
	private Integer version;
	
	private ScoSolicitacaoDeCompra solicitacaoDeCompra;
	
	private ScoSolicitacaoServico solicitacaoServico;
	
	private FcpTitulo titulo; 
	
	@Id
	@Column(name = "SEQ", length = 10, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fcpTxsSq1")
	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Column(name = "VALOR", nullable = true, precision = 12, scale = 2)
	public Double getValor() {
		return valor;
	}
	
	public void setValor(Double valor) {
		this.valor = valor;
	}
		
	@Version
	@Column(name = "VERSION", length = 9, nullable = false)
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "SLS_NUMERO", nullable = true)
	public Integer getSlsNumero() {
		return slsNumero;
	}

	public void setSlsNumero(Integer slsNumero) {
		this.slsNumero = slsNumero;
	}

	@Column(name = "SLC_NUMERO", nullable = true)
	public Integer getSlcNumero() {
		return slcNumero;
	}

	public void setSlcNumero(Integer slcNumero) {
		this.slcNumero = slcNumero;
	}

	@Column(name = "TTL_SEQ", nullable = true)
	public Integer getTtlSeq() {
		return ttlSeq;
	}

	public void setTtlSeq(Integer ttlSeq) {
		this.ttlSeq = ttlSeq;
	}
	
	public enum Fields {

		SEQ("seq"),
		MODALIDADE_EMPENHO("modalidadeEmpenho"),
		VALOR("valor"),
		SLS_NUMERO("slsNumero"),
		SLC_NUMERO("slcNumero"),
		SOLICITACAO_COMPRA("solicitacaoDeCompra"),
		SOLICITACAO_SERVICO("solicitacaoServico"),
		TTL_SEQ("ttlSeq"),
		TITULO("titulo"),		
		VERSION("version"),
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
			if (!(obj instanceof FcpTituloSolicitacoes)) {
				return false;
			}
			FcpTituloSolicitacoes other = (FcpTituloSolicitacoes) obj;
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

		
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "TTL_SEQ",insertable=false, updatable=false)
		public FcpTitulo getTitulo() {
			return titulo;
		}

		public void setTitulo(FcpTitulo titulo) {
			this.titulo = titulo;
		}

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "SLC_NUMERO",insertable=false, updatable=false)
		public ScoSolicitacaoDeCompra getSolicitacaoDeCompra() {
			return solicitacaoDeCompra;
		}

		public void setSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra) {
			this.solicitacaoDeCompra = solicitacaoDeCompra;
		}

		
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "SLS_NUMERO",insertable=false, updatable=false)
		public ScoSolicitacaoServico getSolicitacaoServico() {
			return solicitacaoServico;
		}

		public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
			this.solicitacaoServico = solicitacaoServico;
		}
}
