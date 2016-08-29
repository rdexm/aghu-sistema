package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

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
@SequenceGenerator(name="mamFopSq1", sequenceName="AGH.MAM_FOP_SQ1", allocationSize = 1)
@Table(name = "MAM_FORMULARIO_PADROES", schema = "AGH")
public class MamFormularioPadrao extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7710137687686717459L;
	private Integer seq;
	private Integer version;
	private RapServidores rapServidores;
	private String caminho;
	private String titulo;
	private String observacao;
	private String indSituacao;
	private Date criadoEm;
	private Set<MamFormularioCategoria> mamFormularioCategoriaes = new HashSet<MamFormularioCategoria>(0);

	public MamFormularioPadrao() {
	}

	public MamFormularioPadrao(Integer seq, RapServidores rapServidores, String caminho, String titulo, String indSituacao, Date criadoEm) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.caminho = caminho;
		this.titulo = titulo;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public MamFormularioPadrao(Integer seq, RapServidores rapServidores, String caminho, String titulo, String observacao,
			String indSituacao, Date criadoEm, Set<MamFormularioCategoria> mamFormularioCategoriaes) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.caminho = caminho;
		this.titulo = titulo;
		this.observacao = observacao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.mamFormularioCategoriaes = mamFormularioCategoriaes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamFopSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "CAMINHO", nullable = false, length = 60)
	@Length(max = 60)
	public String getCaminho() {
		return this.caminho;
	}

	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}

	@Column(name = "TITULO", nullable = false, length = 60)
	@Length(max = 60)
	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "OBSERVACAO", length = 240)
	@Length(max = 240)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamFormularioPadrao")
	public Set<MamFormularioCategoria> getMamFormularioCategoriaes() {
		return this.mamFormularioCategoriaes;
	}

	public void setMamFormularioCategoriaes(Set<MamFormularioCategoria> mamFormularioCategoriaes) {
		this.mamFormularioCategoriaes = mamFormularioCategoriaes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		CAMINHO("caminho"),
		TITULO("titulo"),
		OBSERVACAO("observacao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		MAM_FORMULARIO_CATEGORIAES("mamFormularioCategoriaes");

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof MamFormularioPadrao)) {
			return false;
		}
		MamFormularioPadrao other = (MamFormularioPadrao) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
