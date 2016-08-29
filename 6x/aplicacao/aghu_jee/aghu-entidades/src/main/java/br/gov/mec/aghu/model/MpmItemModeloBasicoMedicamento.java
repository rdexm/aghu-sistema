package br.gov.mec.aghu.model;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityId;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;

@Entity
@Table(name = "MPM_ITEM_MOD_BASICO_MDTOS", schema = "AGH")

public class MpmItemModeloBasicoMedicamento extends BaseEntityId<MpmItemModeloBasicoMedicamentoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2476591105779189491L;

	private enum ItemModBasicoMdtoExceptionCode implements BusinessExceptionCode {
		DOSE_MENOR_IGUAL_ZERO;
	}

	private MpmItemModeloBasicoMedicamentoId id;
	private BigDecimal dose;
	private String observacao;
	private MpmModeloBasicoMedicamento modeloBasicoMedicamento;
	private AfaMedicamento medicamento;
	private RapServidores servidor;
	private AfaFormaDosagem formaDosagem;

	// construtores
	
	public MpmItemModeloBasicoMedicamento() {
	}

	public MpmItemModeloBasicoMedicamento(MpmItemModeloBasicoMedicamentoId id) {
		this.id = id;
	}

	// getters & setters
	
	@EmbeddedId()
	@AttributeOverrides( {
			@AttributeOverride(name = "MBM_MDB_SEQ", column = @Column(name = "MBM_MDB_SEQ", nullable = false, length = 5)),
			@AttributeOverride(name = "MBM_SEQ", column = @Column(name = "MBM_SEQ", nullable = false, length = 8)),
			@AttributeOverride(name = "MED_MAT_CODIGO", column = @Column(name = "MED_MAT_CODIGO", nullable = false, length = 6)),
			@AttributeOverride(name = "SEQP", column = @Column(name = "SEQP", nullable = false, length = 5))})
	public MpmItemModeloBasicoMedicamentoId getId() {
		return this.id;
	}

	public void setId(MpmItemModeloBasicoMedicamentoId id) {
		this.id = id;
	}

	@Column(name = "DOSE", nullable = false, precision = 14, scale = 4)
	@Max(value = 10000000000L, message = "O maior valor permitido para este campo é 9999999999.9999")	
	public BigDecimal getDose() {
		return this.dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}

	@Column(name = "OBSERVACAO", length = 120)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "MBM_MDB_SEQ", referencedColumnName = "MDB_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "MBM_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public MpmModeloBasicoMedicamento getModeloBasicoMedicamento() {
		return modeloBasicoMedicamento;
	}

	public void setModeloBasicoMedicamento(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento) {
		this.modeloBasicoMedicamento = modeloBasicoMedicamento;
	}

	@ManyToOne
	@JoinColumn(name = "MED_MAT_CODIGO", referencedColumnName = "MAT_CODIGO", insertable = false, updatable = false)
	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne
	@JoinColumn(name = "FDS_SEQ", referencedColumnName = "SEQ", nullable = false)
	public AfaFormaDosagem getFormaDosagem() {
		return formaDosagem;
	}

	public void setFormaDosagem(AfaFormaDosagem formaDosagem) {
		this.formaDosagem = formaDosagem;
	}
	
	/**
	 * Formata a dose conforme a anotacao Column do atributo.
	 * Formato: (14,4) #.###.###.###,####
	 * 
	 * @return
	 */
	@Transient
	public String getDoseFormatada() {
		String numFormated = null;
		if (this.getDose() != null) {
			numFormated = AghuNumberFormat.formatarValor(this.getDose(), this.getClass(), "dose");
		}
		return numFormated;
	}

	// outros
	/*
	 * (non-Javadoc)
	 * 
	 * @seebr.gov.mec.aghu.model.interfaces.PrescricaoMedicaDescricao#
	 * getDescricaoFormatada()
	 */
	@Transient
	public String getDescricaoEditada() {
		StringBuilder strBuilder = new StringBuilder();
		
		// Por regra do SQL this.getMedicamento() nao deveria ser nulo.
		strBuilder.append(this.getMedicamento().getDescricao());
		
		if (this.getMedicamento().getConcentracao() != null) {
			// Formatar concentracao (14,4) #.###.###.###,####
			//NumberFormat formatter = new DecimalFormat("#,###,###,###.####");
			//String concentracao = formatter.format(this.getMedicamento().getConcentracao());
			strBuilder.append(' ').append(this.getMedicamento().getConcentracaoFormatada());
		}
		
		if (this.getMedicamento().getMpmUnidadeMedidaMedicas() != null && StringUtils.isNotBlank(this.getMedicamento().getMpmUnidadeMedidaMedicas().getDescricao())) {
			strBuilder.append(' ').append(this.getMedicamento().getMpmUnidadeMedidaMedicas().getDescricao());
		}
		
		if (StringUtils.isNotBlank(this.getObservacao())) {
			strBuilder.append(" : ").append(this.getObservacao());
		}
		strBuilder.append(';')
		
		// Formatar concentracao (14,4) #.###.###.###,####
		//NumberFormat formatter = new DecimalFormat("#,###,###,###.####");
		//String dose = formatter.format(this.getDose());
		.append(" DS= ").append(this.getDoseFormatada()).append(' ');
		
		if (this.getFormaDosagem() != null && StringUtils.isNotBlank(this.getFormaDosagem().getDescricaoUnidadeMedidaMedica())) {
			strBuilder.append(this.getFormaDosagem().getDescricaoUnidadeMedidaMedica()).append(';');
		} else if(this.getMedicamento().getTipoApresentacaoMedicamento() != null) {
			strBuilder.append(this.getMedicamento().getTipoApresentacaoMedicamento().getSigla()).append(';');
		}
		
		return strBuilder.toString();
	}


	
	
	
	
	
	
	
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmItemModeloBasicoMedicamento)) {
			return false;
		}
		MpmItemModeloBasicoMedicamento castOther = (MpmItemModeloBasicoMedicamento) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		ID("id")
		, MEDICAMENTO("medicamento") /* ACESSA MATERIAL PELA FK */
		, MODELO_BASICO_MEDICAMENTO("modeloBasicoMedicamento") /* ACESSA MODELO PELA FK */
		, DOSE("dose")
		, OBSERVACAO("observacao")
		, SERVIDOR("servidor")
		, FORMA_DOSAGEM("formaDosagem")
		, MBM_MDB_SEQ("id.modeloBasicoPrescricaoSeq")
		, MBM_SEQ("id.modeloBasicoMedicamentoSeq")
		, MED_MAT_CODIGO("id.medicamentoMaterialCodigo")
		;
		
		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}

	@PrePersist
	@PreUpdate
	protected void validacoes() {
		// a dose deve ser maior que zero
		if (this.getDose().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BaseRuntimeException(
					ItemModBasicoMdtoExceptionCode.DOSE_MENOR_IGUAL_ZERO);
		}
	}
}
