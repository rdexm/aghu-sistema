package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "CCE_RESULTADO_EXAMES", schema = "AGH")
public class CceResultadoExame extends BaseEntityId<CceResultadoExameId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8572382415374136367L;
	private CceResultadoExameId id;
	private Integer version;
	private CceAmostraContagem cceAmostraContagem;
	private CcePaciente ccePaciente;
	private String idConfig;
	private String resultadoLws;
	private String resultadoContagem;
	private String indResultLiberado;
	private String parametros;

	public CceResultadoExame() {
	}

	public CceResultadoExame(CceResultadoExameId id, CceAmostraContagem cceAmostraContagem, CcePaciente ccePaciente,
			String idConfig) {
		this.id = id;
		this.cceAmostraContagem = cceAmostraContagem;
		this.ccePaciente = ccePaciente;
		this.idConfig = idConfig;
	}

	public CceResultadoExame(CceResultadoExameId id, CceAmostraContagem cceAmostraContagem, CcePaciente ccePaciente,
			String idConfig, String resultadoLws, String resultadoContagem, String indResultLiberado, String parametros) {
		this.id = id;
		this.cceAmostraContagem = cceAmostraContagem;
		this.ccePaciente = ccePaciente;
		this.idConfig = idConfig;
		this.resultadoLws = resultadoLws;
		this.resultadoContagem = resultadoContagem;
		this.indResultLiberado = indResultLiberado;
		this.parametros = parametros;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "camNumero", column = @Column(name = "CAM_NUMERO", nullable = false)),
			@AttributeOverride(name = "camSeqp", column = @Column(name = "CAM_SEQP", nullable = false)),
			@AttributeOverride(name = "idResultado", column = @Column(name = "ID_RESULTADO", nullable = false, length = 60)) })
	public CceResultadoExameId getId() {
		return this.id;
	}

	public void setId(CceResultadoExameId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "CAM_NUMERO", referencedColumnName = "NUMERO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CAM_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public CceAmostraContagem getCceAmostraContagem() {
		return this.cceAmostraContagem;
	}

	public void setCceAmostraContagem(CceAmostraContagem cceAmostraContagem) {
		this.cceAmostraContagem = cceAmostraContagem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CPA_CODIGO", nullable = false)
	public CcePaciente getCcePaciente() {
		return this.ccePaciente;
	}

	public void setCcePaciente(CcePaciente ccePaciente) {
		this.ccePaciente = ccePaciente;
	}

	@Column(name = "ID_CONFIG", nullable = false, length = 60)
	@Length(max = 60)
	public String getIdConfig() {
		return this.idConfig;
	}

	public void setIdConfig(String idConfig) {
		this.idConfig = idConfig;
	}

	@Column(name = "RESULTADO_LWS", length = 20)
	@Length(max = 20)
	public String getResultadoLws() {
		return this.resultadoLws;
	}

	public void setResultadoLws(String resultadoLws) {
		this.resultadoLws = resultadoLws;
	}

	@Column(name = "RESULTADO_CONTAGEM", length = 20)
	@Length(max = 20)
	public String getResultadoContagem() {
		return this.resultadoContagem;
	}

	public void setResultadoContagem(String resultadoContagem) {
		this.resultadoContagem = resultadoContagem;
	}

	@Column(name = "IND_RESULT_LIBERADO", length = 1)
	@Length(max = 1)
	public String getIndResultLiberado() {
		return this.indResultLiberado;
	}

	public void setIndResultLiberado(String indResultLiberado) {
		this.indResultLiberado = indResultLiberado;
	}

	@Column(name = "PARAMETROS", length = 10)
	@Length(max = 10)
	public String getParametros() {
		return this.parametros;
	}

	public void setParametros(String parametros) {
		this.parametros = parametros;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		CCE_AMOSTRAS_CONTAGEM("cceAmostraContagem"),
		CCE_PACIENTES("ccePaciente"),
		ID_CONFIG("idConfig"),
		RESULTADO_LWS("resultadoLws"),
		RESULTADO_CONTAGEM("resultadoContagem"),
		IND_RESULT_LIBERADO("indResultLiberado"),
		PARAMETROS("parametros");

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
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof CceResultadoExame)) {
			return false;
		}
		CceResultadoExame other = (CceResultadoExame) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
