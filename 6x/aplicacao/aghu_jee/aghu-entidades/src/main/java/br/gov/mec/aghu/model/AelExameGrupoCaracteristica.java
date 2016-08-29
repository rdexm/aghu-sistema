package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the ael_exame_grupo_caracts database table.
 * 
 */
@Entity
@Table(name="AEL_EXAME_GRUPO_CARACTS", schema = "AGH")
public class AelExameGrupoCaracteristica extends BaseEntityId<AelExameGrupoCaracteristicaId> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5919163414192095082L;
	private AelExameGrupoCaracteristicaId id;
	private Integer codigoFalante;
	private DominioSituacao indSituacao;
	private Integer ordemImpressao;
	private RapServidores servidor;
	
	private AelResultadoCaracteristica resultadoCaracteristica;	
	
	private AelGrupoResultadoCaracteristica grupoResultadoCaracteristica;
	
	private AelExamesMaterialAnalise exameMaterialAnalise;

	private AelCampoLaudo campoLaudo;
	
	private Integer version;
	
	
    public AelExameGrupoCaracteristica() {
    }


	@EmbeddedId
	public AelExameGrupoCaracteristicaId getId() {
		return this.id;
	}

	public void setId(AelExameGrupoCaracteristicaId id) {
		this.id = id;
	}
	

	@Column(name="CODIGO_FALANTE")
	public Integer getCodigoFalante() {
		return this.codigoFalante;
	}

	public void setCodigoFalante(Integer codigoFalante) {
		this.codigoFalante = codigoFalante;
	}


	@Column(name="IND_SITUACAO")
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}


	@Column(name="ORDEM_IMPRESSAO")
	public Integer getOrdemImpressao() {
		return this.ordemImpressao;
	}

	public void setOrdemImpressao(Integer ordemImpressao) {
		this.ordemImpressao = ordemImpressao;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAC_SEQ", insertable = false, updatable = false)
	public AelResultadoCaracteristica getResultadoCaracteristica() {
		return resultadoCaracteristica;
	}

	public void setResultadoCaracteristica(
			AelResultadoCaracteristica resultadoCaracteristica) {
		this.resultadoCaracteristica = resultadoCaracteristica;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GCA_SEQ", insertable = false, updatable = false)
	public AelGrupoResultadoCaracteristica getGrupoResultadoCaracteristica() {
		return grupoResultadoCaracteristica;
	}

	public void setGrupoResultadoCaracteristica(
			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica) {
		this.grupoResultadoCaracteristica = grupoResultadoCaracteristica;
	}



	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "EMA_EXA_SIGLA", referencedColumnName = "EXA_SIGLA", insertable = false, updatable = false),
			@JoinColumn(name = "EMA_MAN_SEQ", referencedColumnName = "MAN_SEQ", insertable = false, updatable = false) })
	public AelExamesMaterialAnalise getExameMaterialAnalise() {
		return exameMaterialAnalise;
	}


	public void setExameMaterialAnalise(
			AelExamesMaterialAnalise exameMaterialAnalise) {
		this.exameMaterialAnalise = exameMaterialAnalise;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAC_SEQ", insertable = false, updatable = false)
	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}


	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
	public enum Fields {

		RESULTADO_CARACTERISTICA("resultadoCaracteristica"), //
		EXAME_MATERIAL_ANALISE("exameMaterialAnalise"), //
		GRUPO_RESULTADO_CARACTERISTICA("grupoResultadoCaracteristica"),//
		CAMPO_LAUDO("campoLaudo"),//
		CAC_SEQ("resultadoCaracteristica.seq"),//
		EMA_EXA_SIGLA("exameMaterialAnalise.id.exaSigla"),//
		EMA_MAN_SEQ("exameMaterialAnalise.id.manSeq"),//
		GCA_SEQ("grupoResultadoCaracteristica.seq"),//
		CODIGO_FALANTE("codigoFalante"), //
		SITUACAO("indSituacao"), //
		;

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
		if (!(obj instanceof AelExameGrupoCaracteristica)) {
			return false;
		}
		AelExameGrupoCaracteristica other = (AelExameGrupoCaracteristica) obj;
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