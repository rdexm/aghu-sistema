package br.gov.mec.aghu.model;

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
import javax.persistence.Transient;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "MPM_MOD_BASIC_CUIDADOS", schema = "AGH")
public class MpmModeloBasicoCuidado extends BaseEntityId<MpmModeloBasicoCuidadoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8175624186996131734L;
	private MpmModeloBasicoCuidadoId id;
	private MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento;
	private MpmCuidadoUsual cuidadoUsual;
	private MpmModeloBasicoPrescricao modeloBasicoPrescricao;
	private RapServidores servidor;
	private Integer frequencia;
	private String descricao;
	private Integer version;
	
	public MpmModeloBasicoCuidado() {
	}

	public MpmModeloBasicoCuidado(MpmModeloBasicoCuidadoId id,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			MpmCuidadoUsual cuidadoUsual,
			MpmModeloBasicoPrescricao modBasicPrescricao, RapServidores servidor) {
		this.id = id;
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
		this.cuidadoUsual = cuidadoUsual;
		this.modeloBasicoPrescricao = modBasicPrescricao;
		this.servidor = servidor;
	}

	public MpmModeloBasicoCuidado(MpmModeloBasicoCuidadoId id,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			MpmCuidadoUsual cuidadoUsual,
			MpmModeloBasicoPrescricao modBasicPrescricao,
			RapServidores servidor, Integer frequencia, String descricao) {
		this.id = id;
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
		this.cuidadoUsual = cuidadoUsual;
		this.modeloBasicoPrescricao = modBasicPrescricao;
		this.servidor = servidor;
		this.frequencia = frequencia;
		this.descricao = descricao;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "modeloBasicoPrescricaoSeq", column = @Column(name = "MDB_SEQ", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false, precision = 8, scale = 0)) })
	public MpmModeloBasicoCuidadoId getId() {
		return this.id;
	}

	public void setId(MpmModeloBasicoCuidadoId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ", nullable = false)
	public MpmTipoFrequenciaAprazamento getTipoFrequenciaAprazamento() {
		return tipoFrequenciaAprazamento;
	}

	public void setTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDU_SEQ", nullable = false)
	public MpmCuidadoUsual getCuidadoUsual() {
		return this.cuidadoUsual;
	}

	public void setCuidadoUsual(MpmCuidadoUsual cuidadoUsual) {
		this.cuidadoUsual = cuidadoUsual;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDB_SEQ", nullable = false, insertable = false, updatable = false)
	public MpmModeloBasicoPrescricao getModeloBasicoPrescricao() {
		return this.modeloBasicoPrescricao;
	}

	public void setModeloBasicoPrescricao(
			MpmModeloBasicoPrescricao modBasicPrescricao) {
		this.modeloBasicoPrescricao = modBasicPrescricao;
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

	@Column(name = "FREQUENCIA", precision = 5, scale = 0)
	public Integer getFrequencia() {
		return this.frequencia;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}

	@Column(name = "DESCRICAO", length = 120)
	@Length(max = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// outros
	@Transient
	/*
	 * Utilizar o método obterDescricaoEditadaModeloBasicoCuidado(...):String em ManterCuidadosModeloBasicoON
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.core.persistence.BaseEntityId#hashCode()
	 */
//	public String getDescricaoEditada() {
//		StringBuffer sintaxeDieta = new StringBuffer();
//		String descTfq = null;
//		if (this.getTipoFrequenciaAprazamento() != null) {
//			if (this.getTipoFrequenciaAprazamento().getSintaxe() != null) { 
//
//				if (this.getFrequencia() != null ) {
//					descTfq = this.getTipoFrequenciaAprazamento().getSintaxe()
//							.replace("#", this.getFrequencia().toString());
//				} else {
//					descTfq = this.getTipoFrequenciaAprazamento().getSintaxe();
//				}
//				
//				
//			} else if (this.getTipoFrequenciaAprazamento().getDescricao() != null) {
//				descTfq = this.getTipoFrequenciaAprazamento().getDescricao();
//			}
//		}
//		if (this.getCuidadoUsual() != null
//				&& this.getCuidadoUsual().getDescricao() != null) {
//
//			if (StringUtils.isNotBlank(this.getDescricao())) {
//				sintaxeDieta.append(this.getCuidadoUsual().getDescricao());
//				sintaxeDieta.append(" - ");
//				sintaxeDieta.append(this.getDescricao());
//			} else {
//				sintaxeDieta.append(this.getCuidadoUsual().getDescricao());
//			}
//		}
//
//		if (descTfq != null) {
//			sintaxeDieta.append(", ");
//			sintaxeDieta.append(descTfq);
//		}
//		sintaxeDieta.append(" ; ");
//
//		return sintaxeDieta.toString();
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		MpmModeloBasicoCuidado other = (MpmModeloBasicoCuidado) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public enum Fields {
		MODELO_BASICO_PRESCRICAO_SEQ("id.modeloBasicoPrescricaoSeq"), MODELO_BASICO_CUIDADO_SEQ(
				"id.seq"), DESCRICAO("descricao"), CDU("cuidadoUsual"), TIPO_FREQUENCIA_APRAZAMENTO("tipoFrequenciaAprazamento");

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