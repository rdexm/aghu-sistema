package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "RAP_AFASTAMENTOS", schema = "AGH")
public class RapAfastamento extends BaseEntityId<RapAfastamentoId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -352795916539251965L;

	private RapAfastamentoId rapAfastamentoId;

	private Date dtInicio;
	private Date dtDigitacao;
	private Date dtFim;
	private Date alteradoEm;
	private Date dtDigitaFim;

	private RapServidores servidor;
	private RapServidores servidorResponsavel;
	private RapServidores servidorAlterado;
	private RapTipoAfastamento rapTipoAfastamento;
	private AacConsultas consulta;
	private AghCid cid;

	// getters & setters

	@EmbeddedId()
	@AttributeOverrides( {
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false, length = 7)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, length = 3)),
			@AttributeOverride(name = "sequencia", column = @Column(name = "SEQUENCIA", nullable = false, length = 3)) })
	public RapAfastamentoId getRapAfastamentoId() {
		return this.rapAfastamentoId;
	}

	public void setRapAfastamentoId(RapAfastamentoId id) {
		this.rapAfastamentoId = id;
	}

	@Column(name = "DT_INICIO", nullable = false)
	public Date getDtInicio() {
		return this.dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	@Column(name = "DT_DIGITACAO", nullable = false)
	public Date getDtDigitacao() {
		return this.dtDigitacao;
	}

	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}

	@Column(name = "DT_FIM")
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "DT_DIGITA_FIM")
	public Date getDtDigitaFim() {
		return this.dtDigitaFim;
	}

	public void setDtDigitaFim(Date dtDigitaFim) {
		this.dtDigitaFim = dtDigitaFim;
	}

	@ManyToOne
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false, insertable = false, updatable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_RESPONSAVEL", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_RESPONSAVEL", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidorResponsavel() {
		return servidorResponsavel;
	}

	public void setServidorResponsavel(RapServidores servidorResponsavel) {
		this.servidorResponsavel = servidorResponsavel;
	}

	@ManyToOne
	@JoinColumn(name = "TAF_CODIGO", referencedColumnName = "CODIGO")
	public RapTipoAfastamento getRapTipoAfastamento() {
		return rapTipoAfastamento;
	}

	public void setRapTipoAfastamento(RapTipoAfastamento rapTipoAfastamento) {
		this.rapTipoAfastamento = rapTipoAfastamento;
	}

	@ManyToOne
	@JoinColumn(name = "CON_NUMERO", referencedColumnName = "NUMERO")
	public AacConsultas getConsulta() {
		return consulta;
	}

	public void setConsulta(AacConsultas consulta) {
		this.consulta = consulta;
	}

	@ManyToOne
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidorAlterado() {
		return servidorAlterado;
	}

	public void setServidorAlterado(RapServidores servidorAlterado) {
		this.servidorAlterado = servidorAlterado;
	}

	@ManyToOne
	@JoinColumn(name = "CID_SEQ", referencedColumnName = "SEQ")
	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("rapAfastamentoId",
				this.rapAfastamentoId).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof RapAfastamento)) {
			return false;
		}
		RapAfastamento castOther = (RapAfastamento) other;
		return new EqualsBuilder().append(this.rapAfastamentoId,
				castOther.getRapAfastamentoId()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.rapAfastamentoId).toHashCode();
	}

	public enum Fields {
		SEQUENCIA("rapAfastamentoId.sequencia"), DT_INICIO("dtInicio"), DT_DIGITACAO(
				"dtDigitacao"), DT_FIM("dtFim"), ALTERADO_EM("alteradoEm"), DT_DIGITA_FIM(
				"dtDigitaFim"), SERVIDOR("servidor"), SERVIDOR_RESPONSAVEL(
				"servidorResponsavel"), SERVIDOR_ALTERADO("servidorAlterado"), TIPO_AFASTAMENTO(
				"rapTipoAfastamento"), CONSULTA("consulta"), CID("cid"), CODIGO_VINCULO_SERVIDOR("servidor.id.vinCodigo"),
				MATRICULA_SERVIDOR("servidor.id.matricula");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
 
 @Transient public RapAfastamentoId getId(){ return this.getRapAfastamentoId();} 
 public void setId(RapAfastamentoId id){ this.setRapAfastamentoId(id);}
}