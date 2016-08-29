package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioTipoValorDesmembramento;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * @author rafael.nascimento
 */

@Entity
@Table(name="PTM_DESMEMBRAMENTO", schema="AGH")
@SequenceGenerator(name="ptmDesmembramentoSeq", sequenceName="AGH.PTM_DES_SQ1", allocationSize = 1)
public class PtmDesmembramento extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3903459062468239640L;
	
	private Integer seq;
	private PtmAvaliacaoTecnica avaliacaoTecnica;
	private String descricao;
	private Short percentual;
	private BigDecimal valor;
	private DominioTipoValorDesmembramento tipoValor;
	private RapServidores servidor;
	private Date dataCriacao;
	private Integer version;
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmDesmembramentoSeq")
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
	
	@Column(name = "TIPO_VALOR", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoValorDesmembramento getTipoValor() {
		return tipoValor;
	}

	public void setTipoValor(DominioTipoValorDesmembramento tipoValor) {
		this.tipoValor = tipoValor;
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
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Override
	public int hashCode() {

		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeq());
		umHashCodeBuilder.append(this.getDescricao());
		umHashCodeBuilder.append(this.getPercentual());
		umHashCodeBuilder.append(this.getValor());
		umHashCodeBuilder.append(this.getTipoValor());
		umHashCodeBuilder.append(this.getDataCriacao());
		umHashCodeBuilder.append(this.getVersion());
		
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof PtmDesmembramento)) {
			return false;
		}

		PtmDesmembramento other = (PtmDesmembramento) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
		umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
		umEqualsBuilder.append(this.getPercentual(), other.getPercentual());
		umEqualsBuilder.append(this.getValor(), other.getValor());
		umEqualsBuilder.append(this.getTipoValor(), other.getTipoValor());
		umEqualsBuilder.append(this.getDataCriacao(), other.getDataCriacao());
		umEqualsBuilder.append(this.getVersion(), other.getVersion());

		return umEqualsBuilder.isEquals();
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
		TIPO_VALOR("tipoValor")
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