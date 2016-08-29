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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "RAP_PESSOA_TIPO_INFORMACOES", schema = "AGH")
public class RapPessoaTipoInformacoes extends BaseEntityId<RapPessoaTipoInformacoesId> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5093433791027346302L;
	/**
	 * Chave primária composta da base de dados.
	 */
	private RapPessoaTipoInformacoesId id;
	private String valor; // conteúdo
	private Date criadoEm;
	private Date alteradoEm;
	private Date dtInicio;
	private Date dtFim;
	
	private RapPessoasFisicas pessoaFisica;
	private RapTipoInformacoes tipoInformacao;
	private RapServidores alteradoPor;
	private RapServidores criadoPor;
	private Integer version;
	
	private FatCbos fatCbos;
	
	// getters & setters

	@EmbeddedId()
	@AttributeOverrides({
			@AttributeOverride(name = "PES_CODIGO", column = @Column(name = "PES_CODIGO", nullable = false, length = 9)),
			@AttributeOverride(name = "TII_SEQ",    column = @Column(name = "TII_SEQ",    nullable = false, length = 4)),
			@AttributeOverride(name = "SEQ",        column = @Column(name = "SEQ",        nullable = false, length = 38))})
	public RapPessoaTipoInformacoesId getId() {
		return this.id;
	}

	public void setId(RapPessoaTipoInformacoesId rapPessoaTipoInformacoesId) {
		this.id = rapPessoaTipoInformacoesId;
	}

	@Column(name = "VALOR", length = 300, nullable = false)
	@Length(max = 300)
	public String getValor() {
		return this.valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@ManyToOne
	@JoinColumn(name = "PES_CODIGO", referencedColumnName = "CODIGO", insertable = false, updatable = false)
	public RapPessoasFisicas getPessoaFisica() {
		return this.pessoaFisica;
	}

	public void setPessoaFisica(RapPessoasFisicas pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}

	@ManyToOne
	@JoinColumn(name = "TII_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false)
	public RapTipoInformacoes getTipoInformacao() {
		return this.tipoInformacao;
	}

	public void setTipoInformacao(RapTipoInformacoes tipoInformacao) {
		this.tipoInformacao = tipoInformacao;
	}

	@ManyToOne
	@JoinColumns(value = {
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getAlteradoPor() {
		return this.alteradoPor;
	}

	public void setAlteradoPor(RapServidores alteradoPor) {
		this.alteradoPor = alteradoPor;
	}

	@ManyToOne
	@JoinColumns(value = {
			@JoinColumn(name = "SER_MATRICULA_CRIADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CRIADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getCriadoPor() {
		return this.criadoPor;
	}

	public void setCriadoPor(RapServidores criadoPor) {
		this.criadoPor = criadoPor;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INICIO")
	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM")
	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	@ManyToOne
	@JoinColumn(name = "VALOR", referencedColumnName = "CODIGO", insertable = false, updatable = false)
	public FatCbos getFatCbos() {
		return this.fatCbos;
	}
	
	public void setFatCbos(FatCbos fatCbos) {
		this.fatCbos = fatCbos;
	}
	
	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("RapPessoaTipoInformacoesId",
				this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof RapPessoaTipoInformacoes)) {
			return false;
		}
		RapPessoaTipoInformacoes castOther = (RapPessoaTipoInformacoes) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		VALOR("valor"), CRIADO_EM("criadoEm"), ALTERADO_EM("alteradoEm"), PESSOAS_FISICA(
				"pessoaFisica"), TIPO_INFORMACAO("tipoInformacao"), ALTERADO_POR(
				"alteradoPor"), CRIADO_POR("criadoPor"), PES_CODIGO(
				"id.pesCodigo"), TII_SEQ("id.tiiSeq"),
				DT_FIM("dtFim"), DT_INICIO("dtInicio"), 
				FAT_COBS("fatCbos");

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