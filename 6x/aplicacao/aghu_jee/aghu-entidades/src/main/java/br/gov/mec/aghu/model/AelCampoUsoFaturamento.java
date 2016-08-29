package br.gov.mec.aghu.model;

// Generated 25/01/2012 16:25:37 by Hibernate Tools 3.4.0.CR1

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
@Table(name = "AEL_CAMPOS_USO_FATURAMENTO", schema = "AGH")
public class AelCampoUsoFaturamento extends BaseEntityId<AelCampoUsoFaturamentoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9191156131220787247L;
	private AelCampoUsoFaturamentoId id;
	private Integer version;
	private RapServidores servidor;
	private Date criadoEm;
	private String tabelaFat;
	private String atributoFat;	
	private AelExamesMaterialAnalise exameMaterialAnalise;
	private AelCampoLaudo campoLaudo;

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "emaExaSigla", column = @Column(name = "EMA_EXA_SIGLA", nullable = false, length = 5)),
			@AttributeOverride(name = "emaManSeq", column = @Column(name = "EMA_MAN_SEQ", nullable = false)),
			@AttributeOverride(name = "calSeq", column = @Column(name = "CAL_SEQ", nullable = false)) })
	public AelCampoUsoFaturamentoId getId() {
		return this.id;
	}

	public void setId(AelCampoUsoFaturamentoId id) {
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
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "TABELA_FAT", length = 60)
	@Length(max = 60)
	public String getTabelaFat() {
		return this.tabelaFat;
	}

	public void setTabelaFat(String tabelaFat) {
		this.tabelaFat = tabelaFat;
	}

	@Column(name = "ATRIBUTO_FAT", length = 45)
	@Length(max = 45)
	public String getAtributoFat() {
		return this.atributoFat;
	}

	public void setAtributoFat(String atributoFat) {
		this.atributoFat = atributoFat;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "EMA_EXA_SIGLA", referencedColumnName = "EXA_SIGLA",  nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "EMA_MAN_SEQ", referencedColumnName = "MAN_SEQ",  nullable = false, insertable = false, updatable = false) })
	public AelExamesMaterialAnalise getExameMaterialAnalise() {
		return exameMaterialAnalise;
	}
	
	public void setExameMaterialAnalise(
			AelExamesMaterialAnalise exameMaterialAnalise) {
		this.exameMaterialAnalise = exameMaterialAnalise;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CAL_SEQ", nullable = false, insertable=false, updatable=false)
	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}
	
	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}
	
	public enum Fields {
		ID("id"),
		EMA_EXA_SIGLA("id.emaExaSigla"),
		EMA_MAN_SEQ("id.emaManSeq"),
		CAL_SEQ("id.calSeq"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		TABELA_FAT("tabelaFat"),
		ATRIBUTO_FAT("atributoFat"),
		CAMPO_LAUDO("campoLaudo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof AelCampoUsoFaturamento)) {
			return false;
		}
		AelCampoUsoFaturamento other = (AelCampoUsoFaturamento) obj;
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
