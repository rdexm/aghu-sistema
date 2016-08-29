package br.gov.mec.aghu.model;

import java.io.Serializable;
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigAvprSq1", sequenceName = "SIG_AVPR_SQ1", allocationSize = 1)
@Table(name = "SIG_ATIV_PESSOA_RESTRICOES", schema = "agh")
public class SigAtividadePessoaRestricoes extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4423351486062929177L;

	private Integer seq;
	private SigAtividadePessoas sigAtividadePessoas;
	private AacPagador pagador;
	private SigCategoriaRecurso categoriaRecurso;
	private BigDecimal percentual;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;
	
	public SigAtividadePessoaRestricoes() {
	}
	
	public SigAtividadePessoaRestricoes(Integer seq) {
		this.seq = seq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigAvprSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PGD_SEQ", nullable = false)
	public AacPagador getPagador() {
		return this.pagador;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATR_SEQ", referencedColumnName = "SEQ", nullable = false)
	public SigCategoriaRecurso getCategoriaRecurso() {
		return categoriaRecurso;
	}

	public void setCategoriaRecurso(SigCategoriaRecurso categoriaRecurso) {
		this.categoriaRecurso = categoriaRecurso;
	}
	
	@Column(name = "PERCENTUAL", nullable = false, precision = 5, scale = 2)
	public BigDecimal getPercentual() {
		return percentual;
	}

	public void setPercentual(BigDecimal percentual) {
		this.percentual = percentual;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "avp_seq", referencedColumnName = "seq")
	public SigAtividadePessoas getSigAtividadePessoas() {
		return this.sigAtividadePessoas;
	}

	public void setSigAtividadePessoas(SigAtividadePessoas sigAtividadePessoas) {
		this.sigAtividadePessoas = sigAtividadePessoas;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		ATIVIDADE_PESSOA("sigAtividadePessoas"),
		CATEGORIA_RECURSO("categoriaRecurso"),
		PAGADOR("pagador");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(seq).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SigAtividadePessoaRestricoes)) {
			return false;
		}
		SigAtividadePessoaRestricoes other = (SigAtividadePessoaRestricoes) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}
}
