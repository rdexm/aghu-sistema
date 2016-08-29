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
@Table(name = "AAC_CONSULTA_PROFISSIONAIS", schema = "AGH")
public class AacConsultaProfissional extends BaseEntityId<AacConsultaProfissionalId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2206621378814950750L;
	private AacConsultaProfissionalId id;
	private Integer version;
	private RapServidores rapServidores;
	private AacConsultaEquipe aacConsultaEquipe;
	private Integer qtdeRealizadas;
	private Integer qtdeAbstProfissional;
	private Integer qtdeAbstPaciente;
	private Integer qtdeAcumAno;

	public AacConsultaProfissional() {
	}

	public AacConsultaProfissional(AacConsultaProfissionalId id, RapServidores rapServidores, AacConsultaEquipe aacConsultaEquipe) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.aacConsultaEquipe = aacConsultaEquipe;
	}

	public AacConsultaProfissional(AacConsultaProfissionalId id, RapServidores rapServidores, AacConsultaEquipe aacConsultaEquipe,
			Integer qtdeRealizadas, Integer qtdeAbstProfissional, Integer qtdeAbstPaciente, Integer qtdeAcumAno) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.aacConsultaEquipe = aacConsultaEquipe;
		this.qtdeRealizadas = qtdeRealizadas;
		this.qtdeAbstProfissional = qtdeAbstProfissional;
		this.qtdeAbstPaciente = qtdeAbstPaciente;
		this.qtdeAcumAno = qtdeAcumAno;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "cpqCpeCpfMesReferencia", column = @Column(name = "CPQ_CPE_CPF_MES_REFERENCIA", nullable = false, length = 29)),
			@AttributeOverride(name = "cpqCpeCpfEpcSeq", column = @Column(name = "CPQ_CPE_CPF_EPC_SEQ", nullable = false)),
			@AttributeOverride(name = "cpqCpeEspSeq", column = @Column(name = "CPQ_CPE_ESP_SEQ", nullable = false)),
			@AttributeOverride(name = "cpqEqpSeq", column = @Column(name = "CPQ_EQP_SEQ", nullable = false)),
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false)) })
	public AacConsultaProfissionalId getId() {
		return this.id;
	}

	public void setId(AacConsultaProfissionalId id) {
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
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false, insertable = false, updatable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "CPQ_CPE_CPF_MES_REFERENCIA", referencedColumnName = "CPE_CPF_MES_REFERENCIA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CPQ_CPE_CPF_EPC_SEQ", referencedColumnName = "CPE_CPF_EPC_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CPQ_CPE_ESP_SEQ", referencedColumnName = "CPE_ESP_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CPQ_EQP_SEQ", referencedColumnName = "EQP_SEQ", nullable = false, insertable = false, updatable = false) })
	public AacConsultaEquipe getAacConsultaEquipe() {
		return this.aacConsultaEquipe;
	}

	public void setAacConsultaEquipe(AacConsultaEquipe aacConsultaEquipe) {
		this.aacConsultaEquipe = aacConsultaEquipe;
	}

	@Column(name = "QTDE_REALIZADAS")
	public Integer getQtdeRealizadas() {
		return this.qtdeRealizadas;
	}

	public void setQtdeRealizadas(Integer qtdeRealizadas) {
		this.qtdeRealizadas = qtdeRealizadas;
	}

	@Column(name = "QTDE_ABST_PROFISSIONAL")
	public Integer getQtdeAbstProfissional() {
		return this.qtdeAbstProfissional;
	}

	public void setQtdeAbstProfissional(Integer qtdeAbstProfissional) {
		this.qtdeAbstProfissional = qtdeAbstProfissional;
	}

	@Column(name = "QTDE_ABST_PACIENTE")
	public Integer getQtdeAbstPaciente() {
		return this.qtdeAbstPaciente;
	}

	public void setQtdeAbstPaciente(Integer qtdeAbstPaciente) {
		this.qtdeAbstPaciente = qtdeAbstPaciente;
	}

	@Column(name = "QTDE_ACUM_ANO")
	public Integer getQtdeAcumAno() {
		return this.qtdeAcumAno;
	}

	public void setQtdeAcumAno(Integer qtdeAcumAno) {
		this.qtdeAcumAno = qtdeAcumAno;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		AAC_CONSULTA_EQUIPES("aacConsultaEquipe"),
		QTDE_REALIZADAS("qtdeRealizadas"),
		QTDE_ABST_PROFISSIONAL("qtdeAbstProfissional"),
		QTDE_ABST_PACIENTE("qtdeAbstPaciente"),
		QTDE_ACUM_ANO("qtdeAcumAno");

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
		if (!(obj instanceof AacConsultaProfissional)) {
			return false;
		}
		AacConsultaProfissional other = (AacConsultaProfissional) obj;
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
