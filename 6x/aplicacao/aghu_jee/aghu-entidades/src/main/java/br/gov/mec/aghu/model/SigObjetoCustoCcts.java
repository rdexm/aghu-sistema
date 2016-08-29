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
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCustoCcts;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigObcSq1", sequenceName = "SIG_OBC_SQ1", allocationSize = 1)
@Table(name = "SIG_OBJETO_CUSTO_CCTS", schema = "AGH")
public class SigObjetoCustoCcts extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 4667197005920588129L;
	
	private Integer seq;
	private SigObjetoCustoVersoes sigObjetoCustoVersoes;
	private FccCentroCustos fccCentroCustos;
	private Date criadoEm;
	private RapServidores rapServidores;
	private DominioSituacao indSituacao;
	private DominioTipoObjetoCustoCcts indTipo;
	private Integer version;
	
	private Boolean controleCCComplementar = Boolean.FALSE;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigObcSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCV_SEQ", referencedColumnName = "SEQ")
	public SigObjetoCustoVersoes getSigObjetoCustoVersoes() {
		return sigObjetoCustoVersoes;
	}

	public void setSigObjetoCustoVersoes(SigObjetoCustoVersoes sigObjetoCustoVersoes) {
		this.sigObjetoCustoVersoes = sigObjetoCustoVersoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO")
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
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
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_TIPO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoObjetoCustoCcts getIndTipo() {
		return indTipo;
	}

	public void setIndTipo(DominioTipoObjetoCustoCcts indTipo) {
		this.indTipo = indTipo;
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
	public Boolean getControleCCComplementar() {
		return controleCCComplementar;
	}

	public void setControleCCComplementar(Boolean controleCCComplementar) {
		this.controleCCComplementar = controleCCComplementar;
	}


	public enum Fields {

		SEQ("seq"), 
		OBJETO_CUSTO_VERSAO("sigObjetoCustoVersoes"),
		OBJETO_CUSTO_VERSAO_SEQ("sigObjetoCustoVersoes.seq"),
		CENTRO_CUSTO("fccCentroCustos"), 
		CRIADO_EM("criadoEm"), 
		SERVIDOR_RESPONSAVEL("rapServidores"), 
		IND_SITUACAO("indSituacao"), 
		IND_TIPO("indTipo");

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
	public boolean equals(Object other) {
		if (!(other instanceof SigObjetoCustoCcts)) {
			return false;
		}
		SigObjetoCustoCcts castOther = (SigObjetoCustoCcts) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
