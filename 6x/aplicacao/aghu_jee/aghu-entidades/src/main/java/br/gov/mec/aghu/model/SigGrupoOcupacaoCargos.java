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
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigGcaSq1", sequenceName = "SIG_GCA_SQ1", allocationSize = 1)
@Table(name = "SIG_GRUPO_OCUPACAO_CARGOS", schema = "AGH")
public class SigGrupoOcupacaoCargos extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -2426038376140045799L;

	private Integer seq;
	private RapServidores rapServidores;
	private SigGrupoOcupacoes sigGrupoOcupacoes;
	private RapOcupacaoCargo rapOcupacaoCargo;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private Integer version;
	
	//transient
	private Boolean selected = Boolean.FALSE;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigGcaSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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
	@JoinColumn(name = "GOC_SEQ", referencedColumnName = "SEQ")
	public SigGrupoOcupacoes getSigGrupoOcupacoes() {
		return sigGrupoOcupacoes;
	}

	public void setSigGrupoOcupacoes(SigGrupoOcupacoes sigGrupoOcupacoes) {
		this.sigGrupoOcupacoes = sigGrupoOcupacoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "OCA_CAR_CODIGO", referencedColumnName = "CAR_CODIGO"), @JoinColumn(name = "OCA_CODIGO", referencedColumnName = "CODIGO") })
	public RapOcupacaoCargo getRapOcupacaoCargo() {
		return rapOcupacaoCargo;
	}

	public void setRapOcupacaoCargo(RapOcupacaoCargo rapOcupacaoCargo) {
		this.rapOcupacaoCargo = rapOcupacaoCargo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
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
	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}


	public enum Fields {

		SEQ("seq"),
		RAP_SERVIDORES("rapServidores"),
		SIG_GRUPO_OCUPACOES("sigGrupoOcupacoes"),
		SIG_GRUPO_OCUPACOES_SEQ("sigGrupoOcupacoes.seq"),
		RAP_OCUPACAO_CARGO("rapOcupacaoCargo"),
		RAP_OCUPACAO_CARGO_ID("rapOcupacaoCargo.id.cargoCodigo"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao");

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
	public boolean equals(Object other) {
		if (!(other instanceof SigGrupoOcupacaoCargos)) {
			return false;
		}
		SigGrupoOcupacaoCargos castOther = (SigGrupoOcupacaoCargos) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
