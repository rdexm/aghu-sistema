package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import br.gov.mec.aghu.dominio.DominioCategoriaProfissionalEquipeCrg;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * Migrado durante o desenvolvimento do módulo Faturamento</br> 
 * TODO: <b>REVISAR CAMPOS (RELACIONAMENTOS)</b></br> 
 * TODO: <b>Implementar Triggers</b></br>
 * TODO: <b>Implementar Constraints</b></br>
 */

@Entity
@Table(name = "MBC_PROF_DESCRICOES", schema = "AGH")
public class MbcProfDescricoes extends BaseEntityId<MbcProfDescricoesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -604997315779418963L;

	private MbcProfDescricoesId id;
	private DominioTipoAtuacao tipoAtuacao;
	private String nome;
	private DominioCategoriaProfissionalEquipeCrg categoria;
	private Date criadoEm;
	private RapServidores servidor;
	private RapServidores servidorProf;
	private MbcDescricaoCirurgica mbcDescricaoCirurgica;
	
	public MbcProfDescricoes() {
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "dcgCrgSeq", column = @Column(name = "DCG_CRG_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "dcgSeqp", column = @Column(name = "DCG_SEQP", nullable = false, precision = 2, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false))})
	public MbcProfDescricoesId getId() {
		return this.id;
	}

	public void setId(MbcProfDescricoesId id) {
		this.id = id;
	}

	@Column(name = "TIPO_ATUACAO", nullable = false, length = 4)
	@Enumerated(EnumType.STRING)
	public DominioTipoAtuacao getTipoAtuacao() {
		return tipoAtuacao;
	}

	@Column(name = "NOME", nullable = true, length = 100)
	public String getNome() {
		return nome;
	}

	@Column(name = "CATEGORIA", nullable = true, length = 4)
	@Enumerated(EnumType.STRING)
	public DominioCategoriaProfissionalEquipeCrg getCategoria() {
		return categoria;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public void setTipoAtuacao(DominioTipoAtuacao tipoAtuacao) {
		this.tipoAtuacao = tipoAtuacao;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setCategoria(DominioCategoriaProfissionalEquipeCrg categoria) {
		this.categoria = categoria;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_PROF", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_PROF", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorProf() {
		return servidorProf;
	}

	public void setServidorProf(RapServidores servidorProf) {
		this.servidorProf = servidorProf;
	}	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "DCG_CRG_SEQ", referencedColumnName = "CRG_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "DCG_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public MbcDescricaoCirurgica getMbcDescricaoCirurgica() {
		return this.mbcDescricaoCirurgica;
	}

	public void setMbcDescricaoCirurgica(MbcDescricaoCirurgica mbcDescricaoCirurgica) {
		this.mbcDescricaoCirurgica = mbcDescricaoCirurgica;
	}


	public enum Fields {
		DCG_CRG_SEQ("id.dcgCrgSeq"),
		DCG_SEQP("id.dcgSeqp"),
		SEQP("id.seqp"),
		TIPO_ATUACAO("tipoAtuacao"),
		SERVIDOR("servidor"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		SERVIDOR_PROF("servidorProf"), 
		SER_MATRICULA_PROF("servidorProf.id.matricula"), 
		SER_VIN_CODIGO_PROF("servidorProf.id.vinCodigo"),
		MBC_DESCRICAO_CIRURGICA("mbcDescricaoCirurgica");

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
		if (!(obj instanceof MbcProfDescricoes)) {
			return false;
		}
		MbcProfDescricoes other = (MbcProfDescricoes) obj;
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
