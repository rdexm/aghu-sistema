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
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigOccSq1", sequenceName = "sig_occ_sq1", allocationSize = 1)
@Table(name = "sig_objeto_custo_clientes", schema = "agh")
public class SigObjetoCustoClientes extends BaseEntitySeq<Integer> implements Serializable, Cloneable {

	private static final long serialVersionUID = -6296598956365500872L;
	
	private Integer seq;
	private SigObjetoCustoVersoes objetoCustoVersoes;
	private FccCentroCustos centroCusto;
	private SigCentroProducao centroProducao;
	private Boolean indTodosCct;
	private SigDirecionadores direcionadores;
	private BigDecimal valor;
	private Date criadoEm;
	private RapServidores servidor;
	private DominioSituacao situacao;
	private Integer version;

	//transient
	private Boolean emEdicao = Boolean.FALSE;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigOccSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCV_SEQ", referencedColumnName = "SEQ", nullable = false)
	public SigObjetoCustoVersoes getObjetoCustoVersoes() {
		return objetoCustoVersoes;
	}

	public void setObjetoCustoVersoes(SigObjetoCustoVersoes objetoCustoVersoes) {
		this.objetoCustoVersoes = objetoCustoVersoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "codigo")
	public FccCentroCustos getCentroCusto() {
		return this.centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CTO_SEQ", referencedColumnName = "SEQ")
	public SigCentroProducao getCentroProducao() {
		return centroProducao;
	}

	public void setCentroProducao(SigCentroProducao centroProducao) {
		this.centroProducao = centroProducao;
	}

	@Column(name = "IND_TODOS_CCT")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTodosCct() {
		return indTodosCct;
	}

	public void setIndTodosCct(Boolean indTodosCct) {
		this.indTodosCct = indTodosCct;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIR_SEQ", referencedColumnName = "SEQ")
	public SigDirecionadores getDirecionadores() {
		return direcionadores;
	}

	public void setDirecionadores(SigDirecionadores direcionadores) {
		this.direcionadores = direcionadores;
	}

	@Column(name = "VALOR ", precision = 14, scale = 4)
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "VERSION", nullable = false)
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
		OBJETO_CUSTO_VERSAO("objetoCustoVersoes"),
		OBJETO_CUSTO_VERSAO_SEQ("objetoCustoVersoes.seq"),
		CENTRO_CUSTO("centroCusto"),
		CENTRO_PRODUCAO("centroProducao"),
		IND_TODOS_CCT("indTodosCct"),
		DIRECIONADORES("direcionadores"),
		VALOR("valor"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		SITUACAO("situacao");

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
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigObjetoCustoClientes)) {
			return false;
		}
		SigObjetoCustoClientes castOther = (SigObjetoCustoClientes) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
}
