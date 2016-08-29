package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "MPM_CONTROL_PREV_ALTAS", schema = "AGH")
@SequenceGenerator(name = "mpmCvaSq1", sequenceName = "AGH.MPM_CVA_SQ1", allocationSize = 1)
public class MpmControlPrevAltas extends BaseEntitySeq<Long> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8405425915605731560L;
	/**
	 * 
	 */

	private Long seq;
	private Date criadoEm;
	private String resposta;
	private Date dtInicio;
	private Date dtFim;

	private AghAtendimentos atendimento;
	private RapServidores servidor;
	private Integer version;


	// construtores
	public MpmControlPrevAltas() {

	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 14, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmCvaSq1")
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	
	@Column(name = "RESPOSTA", length = 1)
	public String getResposta() {
		return this.resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	@Column(name = "DT_INICIO", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtInicio() {
		return this.dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}
	

	@Column(name = "DT_FIM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}
	

	@ManyToOne
	@JoinColumn(name = "ATD_SEQ", referencedColumnName = "SEQ")
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
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

	
	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	// outros
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmControlPrevAltas)) {
			return false;
		}
		MpmControlPrevAltas castOther = (MpmControlPrevAltas) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"),  CRIADO_EM("criadoEm"), ATENDIMENTO("atendimento"),SERVIDOR("servidor"),RESPOSTA("resposta"), DT_FIM("dtFim");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}