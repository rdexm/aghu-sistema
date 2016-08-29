package br.gov.mec.aghu.model;

import java.io.Serializable;

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

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name="PTM_SERV_AREA_TEC_AVALIACAO", schema = "AGH")
public class PtmServAreaTecAvaliacao extends BaseEntityId<PtmServAreaTecAvaliacaoId> implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8520765182964589486L;
	
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "seqAreaTecAvaliacao", column = @Column(name = "SEQ_AREA_TEC_AVALIACAO", nullable = false)),
			@AttributeOverride(name = "matRapTecnico", column = @Column(name = "MAT_RAP_TECNICO", nullable = false)),
			@AttributeOverride(name = "serVinCodigoTecnico", column = @Column(name = "SER_VIN_CODIGO_TECNICO", nullable = false)) })
	private PtmServAreaTecAvaliacaoId id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEQ_AREA_TEC_AVALIACAO", referencedColumnName = "SEQ", insertable=false, updatable=false)
	private PtmAreaTecAvaliacao ptmAreaTecAvaliacao;
	
	
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "MAT_RAP_TECNICO", referencedColumnName = "MATRICULA", insertable=false, updatable=false),
			@JoinColumn(name = "SER_VIN_CODIGO_TECNICO", referencedColumnName = "VIN_CODIGO", insertable=false, updatable=false) })
	private RapServidores servidor;
	
	@Column(name = "TECNICO_PADRAO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	private Boolean tecnicoPadrao;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	private RapServidores servidorCriacao;
	
	@Version
	@Column(name = "VERSION", nullable = false)
	private Integer version;
	
	public enum Fields {
		ID("id"),
		SEQ_AREA_TEC_AVALIACAO("id.seqAreaTecAvaliacao"),
		MAT_RAP_TECNICO("id.matRapTecnico"),
		SER_VIN_CODIGO_TECNICO("id.serVinCodigoTecnico"),
		TECNICO_PADARO("tecnicoPadrao"),
		MAT_RAP_CRIACAO("servidorCriacao.id.matricula"),
		SER_VIN_CODIGO_CRIACAO("servidorCriacao.id.vinCodigo"),
		SERVIDOR_CRIACAO("servidorCriacao"),
		SERVIDOR_TECNICO("servidor"),
		AREA_TEC_AVALIACAO("ptmAreaTecAvaliacao");
		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}

	}
	
	public PtmServAreaTecAvaliacaoId getId() {
		return id;
	}

	public void setId(PtmServAreaTecAvaliacaoId id) {
		this.id = id;
	}
	
	public Boolean getTecnicoPadrao() {
		return tecnicoPadrao;
	}

	public void setTecnicoPadrao(Boolean tecnicoPadrao) {
		this.tecnicoPadrao = tecnicoPadrao;
	}
	
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public RapServidores getServidorCriacao() {
		return servidorCriacao;
	}

	public void setServidorCriacao(RapServidores servidorCriacao) {
		this.servidorCriacao = servidorCriacao;
	}

	public PtmAreaTecAvaliacao getPtmAreaTecAvaliacao() {
		return ptmAreaTecAvaliacao;
	}

	public void setPtmAreaTecAvaliacao(PtmAreaTecAvaliacao ptmAreaTecAvaliacao) {
		this.ptmAreaTecAvaliacao = ptmAreaTecAvaliacao;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}


}
