package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.UniqueConstraint;


import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * FcuEntrDadoLuz generated by hbm2java
 */
@Entity
@SequenceGenerator(name="fcuEluSq1", sequenceName="AGH.FCU_ELU_SQ1", allocationSize = 1)
@Table(name = "FCU_ENTR_DADOS_LUZ", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = { "gcc_seq",
		"dt_inicio_validade" }))
public class FcuEntrDadoLuz extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4176899607324208816L;
	private Short seq;
	//private Integer version; //Comentado porque não existe no BD.
	private FcuGrupoCentroCustos fcuGrupoCentroCustos;
	private RapServidores rapServidores;
	private Date dtInicioValidade;
	private Date dtFimValidade;
	private Double contribuicaoKwh;
	private Date criadoEm;

	public FcuEntrDadoLuz() {
	}

	public FcuEntrDadoLuz(Short seq, FcuGrupoCentroCustos fcuGrupoCentroCustos, RapServidores rapServidores, Date dtInicioValidade,
			Double contribuicaoKwh) {
		this.seq = seq;
		this.fcuGrupoCentroCustos = fcuGrupoCentroCustos;
		this.rapServidores = rapServidores;
		this.dtInicioValidade = dtInicioValidade;
		this.contribuicaoKwh = contribuicaoKwh;
	}

	public FcuEntrDadoLuz(Short seq, FcuGrupoCentroCustos fcuGrupoCentroCustos, RapServidores rapServidores, Date dtInicioValidade,
			Date dtFimValidade, Double contribuicaoKwh, Date criadoEm) {
		this.seq = seq;
		this.fcuGrupoCentroCustos = fcuGrupoCentroCustos;
		this.rapServidores = rapServidores;
		this.dtInicioValidade = dtInicioValidade;
		this.dtFimValidade = dtFimValidade;
		this.contribuicaoKwh = contribuicaoKwh;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fcuEluSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	/*@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}*/

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GCC_SEQ", nullable = false)
	public FcuGrupoCentroCustos getFcuGrupoCentroCustos() {
		return this.fcuGrupoCentroCustos;
	}

	public void setFcuGrupoCentroCustos(FcuGrupoCentroCustos fcuGrupoCentroCustos) {
		this.fcuGrupoCentroCustos = fcuGrupoCentroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INICIO_VALIDADE", nullable = false, length = 29)
	public Date getDtInicioValidade() {
		return this.dtInicioValidade;
	}

	public void setDtInicioValidade(Date dtInicioValidade) {
		this.dtInicioValidade = dtInicioValidade;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM_VALIDADE", length = 29)
	public Date getDtFimValidade() {
		return this.dtFimValidade;
	}

	public void setDtFimValidade(Date dtFimValidade) {
		this.dtFimValidade = dtFimValidade;
	}

	@Column(name = "CONTRIBUICAO_KWH", nullable = false, precision = 17, scale = 17)
	public Double getContribuicaoKwh() {
		return this.contribuicaoKwh;
	}

	public void setContribuicaoKwh(Double contribuicaoKwh) {
		this.contribuicaoKwh = contribuicaoKwh;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		FCU_GRUPO_CENTRO_CUSTOS("fcuGrupoCentroCustos"),
		RAP_SERVIDORES("rapServidores"),
		DT_INICIO_VALIDADE("dtInicioValidade"),
		DT_FIM_VALIDADE("dtFimValidade"),
		CONTRIBUICAO_KWH("contribuicaoKwh"),
		CRIADO_EM("criadoEm");

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
		if (!(obj instanceof FcuEntrDadoLuz)) {
			return false;
		}
		FcuEntrDadoLuz other = (FcuEntrDadoLuz) obj;
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