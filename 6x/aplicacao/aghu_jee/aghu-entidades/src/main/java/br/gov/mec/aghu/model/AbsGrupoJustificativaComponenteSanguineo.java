package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "ABS_GRUPOS_JUSTIF_COMPS", schema = "AGH")
@SequenceGenerator(name = "absGjcSq1", sequenceName = "AGH.ABS_GJC_SQ1", allocationSize = 1)
public class AbsGrupoJustificativaComponenteSanguineo extends BaseEntitySeq<Short> implements java.io.Serializable {
	
	private static final long serialVersionUID = -4393021226574873385L;
	
	private Short seq;
	private String descricao;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Date alteradoEm;
	private String titulo;
	private RapServidores servidor;
	private RapServidores servidorAlterado;
	private Integer version;
	
	private Set<AbsJustificativaComponenteSanguineo> justificativasComponenteSanguineo= new HashSet<AbsJustificativaComponenteSanguineo>(0);
	
	@Id
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "absGjcSq1")
	public Short getSeq() {
		return seq;
	}
	
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, updatable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return alteradoEm;
	}
	
	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@Column(name = "TITULO", nullable = false, length = 15)
	@Length(max = 15)
	public String getTitulo() {
		return titulo;
	}
	
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlterado() {
		return servidorAlterado;
	}
	
	public void setServidorAlterado(RapServidores servidorAlterado) {
		this.servidorAlterado = servidorAlterado;
	}
	
	
	
	@OneToMany(mappedBy="grupoJustificativaComponenteSanguineo")
	public Set<AbsJustificativaComponenteSanguineo> getJustificativasComponenteSanguineo() {
		return justificativasComponenteSanguineo;
	}

	public void setJustificativasComponenteSanguineo(
			Set<AbsJustificativaComponenteSanguineo> justificativasComponenteSanguineo) {
		this.justificativasComponenteSanguineo = justificativasComponenteSanguineo;
	}



	public enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		TITULO("titulo"),
		SERVIDOR("servidor"),
		SERVIDOR_ALTERADO("servidorAlterado"),
		JUSTIFICATIVAS_COMPONENTE_SANGUINEO("justificativasComponenteSanguineo");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (!(obj instanceof AbsGrupoJustificativaComponenteSanguineo)) {
			return false;
		}
		AbsGrupoJustificativaComponenteSanguineo other = (AbsGrupoJustificativaComponenteSanguineo) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
