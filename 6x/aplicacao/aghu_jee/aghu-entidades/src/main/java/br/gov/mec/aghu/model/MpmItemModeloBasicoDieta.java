package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MPM_ITEM_MOD_BASICO_DIETAS", schema = "AGH")

public class MpmItemModeloBasicoDieta extends BaseEntityId<MpmItemModeloBasicoDietaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5382349005403111042L;
	private MpmItemModeloBasicoDietaId id;
	private MpmModeloBasicoDieta modeloBasicoDieta;
	private AnuTipoItemDieta tipoItemDieta;

	private MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento;
	private RapServidores servidor;
	private Date criadoEm;
	private BigDecimal quantidade;
	private Short frequencia;
	private Byte numeroVezes;

	// construtores
	public MpmItemModeloBasicoDieta() {
	}

	public MpmItemModeloBasicoDieta(MpmItemModeloBasicoDietaId id) {
		this.id = id;
	}

	public MpmItemModeloBasicoDieta(MpmItemModeloBasicoDietaId id,
			MpmModeloBasicoDieta modBasicDieta, RapServidores servidor,
			Date criadoEm) {
		this.id = id;
		this.modeloBasicoDieta = modBasicDieta;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
	}

	public MpmItemModeloBasicoDieta(MpmItemModeloBasicoDietaId id,
			MpmModeloBasicoDieta modBasicDieta,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			RapServidores servidor, Date criadoEm, BigDecimal qtde,
			Short frequencia, Byte numVezes) {
		this.id = id;
		this.modeloBasicoDieta = modBasicDieta;
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
		this.quantidade = qtde;
		this.frequencia = frequencia;
		this.numeroVezes = numVezes;
	}

	// getters and setters

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "mbdMdbSeq", column = @Column(name = "MBD_MDB_SEQ", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "mbdSeq", column = @Column(name = "MBD_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "tidSeq", column = @Column(name = "TID_SEQ", nullable = false, precision = 5, scale = 0)) })
	public MpmItemModeloBasicoDietaId getId() {
		return this.id;
	}

	public void setId(MpmItemModeloBasicoDietaId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ", referencedColumnName = "SEQ", nullable = true)
	public MpmTipoFrequenciaAprazamento getTipoFrequenciaAprazamento() {
		return this.tipoFrequenciaAprazamento;
	}

	public void setTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "FREQUENCIA", precision = 3, scale = 0)
	public Short getFrequencia() {
		return this.frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "MBD_MDB_SEQ", referencedColumnName = "MDB_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "MBD_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public MpmModeloBasicoDieta getModeloBasicoDieta() {
		return modeloBasicoDieta;
	}

	public void setModeloBasicoDieta(MpmModeloBasicoDieta modeloBasicoDieta) {
		this.modeloBasicoDieta = modeloBasicoDieta;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TID_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false)
	public AnuTipoItemDieta getTipoItemDieta() {
		return tipoItemDieta;
	}

	public void setTipoItemDieta(AnuTipoItemDieta tipoItemDieta) {
		this.tipoItemDieta = tipoItemDieta;
	}

	@Column(name = "QTDE", precision = 7)
	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	@Column(name = "NUM_VEZES", precision = 2, scale = 0)
	public Byte getNumeroVezes() {
		return numeroVezes;
	}

	public void setNumeroVezes(Byte numeroVezes) {
		this.numeroVezes = numeroVezes;
	}

	// outros
	@Transient
	public String getDescricaoEditada() {
		StringBuffer sintaxeDieta = new StringBuffer(30);
		String descTfq;

		sintaxeDieta.append(this.tipoItemDieta.getDescricao());
		if (this.quantidade != null) {
			// Formatar quantidade 'fm999990D99'
			NumberFormat formatter = new DecimalFormat("######0.##");
			String qtdF = null; //

			if ((this.getTipoItemDieta() != null)
					&& (this.getTipoItemDieta().getUnidadeMedidaMedica() != null)
					&& (this.getTipoItemDieta().getUnidadeMedidaMedica()
							.getDescricao() != null)) {
				qtdF = formatter.format(this.quantidade)
						+ " "
						+ this.getTipoItemDieta().getUnidadeMedidaMedica()
								.getDescricao() ;
			} else {
				qtdF = formatter.format(this.quantidade);
			}

			sintaxeDieta.append(" " + qtdF + " ");
			
		} else {
			sintaxeDieta.append(' ');
		}

		// if (this.getTipoItemDieta() != null) {
		if (this.getTipoFrequenciaAprazamento() != null) {
			if (this.getTipoFrequenciaAprazamento().getSintaxe() != null) {

				descTfq = this.getTipoFrequenciaAprazamento().getSintaxe()
						.replace("#", this.getFrequencia().toString());
				sintaxeDieta.append(descTfq);

			} else if (this.getTipoFrequenciaAprazamento().getDescricao() != null) {
				descTfq = this.getTipoFrequenciaAprazamento().getDescricao();
				sintaxeDieta.append(descTfq);
			}
		}
		// }

		if (this.getNumeroVezes() != null) {
			sintaxeDieta.append(", número de vezes: ");
			sintaxeDieta.append(this.getNumeroVezes());

		}

		sintaxeDieta.append(" ; ");

		return sintaxeDieta.toString();

	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmItemModeloBasicoDieta)) {
			return false;
		}
		MpmItemModeloBasicoDieta castOther = (MpmItemModeloBasicoDieta) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		ID("id"), TIPO_FREQ_APRAZAMENTO("tipoFrequenciaAprazamento"), SERVIDOR(
				"servidor"), CRIADO_EM("criadoEm"), QUANTIDADE("quantidade"), FREQUENCIA(
				"frequencia"), NUMERO_VEZES("numeroVezes"), MODELO_BASICO_PRESCRICAO_SEQ(
				"id.modeloBasicoPrescricaoSeq"), MODELO_BASICO_DIETA_SEQ(
				"id.modeloBasicoDietaSeq"),TIPO_ITEM_DIETA("tipoItemDieta"), TIPO_ITEM_DIETA_SEQ("id.tipoItemDietaSeq"),
				MODELO_BASICO_DIETA("modeloBasicoDieta");

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
