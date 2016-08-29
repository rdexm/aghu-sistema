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
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigPhcSq1", sequenceName = "SIG_PHC_SQ1", allocationSize = 1)
@Table(name = "SIG_OBJETO_CUSTO_PHIS", schema = "AGH")
public class SigObjetoCustoPhis extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 6984545006114363770L;
	private Integer seq;
	private SigObjetoCustoVersoes sigObjetoCustoVersoes;
	private Date criadoEm;
	private FatProcedHospInternos fatProcedHospInternos;
	private RapServidores rapServidores;
	private DominioSituacao dominioSituacao;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigPhcSq1")
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ", referencedColumnName = "SEQ")
	public FatProcedHospInternos getFatProcedHospInternos() {
		return fatProcedHospInternos;
	}

	public void setFatProcedHospInternos(FatProcedHospInternos fatProcedHospInternos) {
		this.fatProcedHospInternos = fatProcedHospInternos;
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
	public DominioSituacao getDominioSituacao() {
		return dominioSituacao;
	}

	public void setDominioSituacao(DominioSituacao dominioSituacao) {
		this.dominioSituacao = dominioSituacao;
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
		OBJETO_CUSTO_VERSAO("sigObjetoCustoVersoes"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		SITUACAO("dominioSituacao"),
		OBJETO_CUSTO_VERSAO_SEQ("sigObjetoCustoVersoes.seq"),
		FAT_PHI("fatProcedHospInternos"),
		FAT_PHI_SEQ("fatProcedHospInternos.seq");

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
		if (!(other instanceof SigObjetoCustoPhis)) {
			return false;
		}
		SigObjetoCustoPhis castOther = (SigObjetoCustoPhis) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
