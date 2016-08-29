package br.gov.mec.aghu.model;

// Generated 28/03/2012 15:17:44 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioGrupoMbcVentilacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "MBC_VENTILACOES", schema = "AGH")
public class MbcVentilacao extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = 5927134969817618100L;
	private Integer seq;
	private Integer version;
	private String descricao;
	private String nomeReduzido;
	private DominioSituacao situacao;
	private Short ordem;
	private Boolean especificarSistema;
	private DominioGrupoMbcVentilacao grupo;
	private Date criadoEm;
	private RapServidores servidor;
	private Set<MbcFichaVentilacao> mbcFichaVentilacoeses = new HashSet<MbcFichaVentilacao>(0);

	public MbcVentilacao() {
	}

	public MbcVentilacao(Integer seq, Integer version, String descricao,
			String nomeReduzido, DominioSituacao situacao, Short ordem,
			Boolean especificarSistema, DominioGrupoMbcVentilacao grupo,
			Date criadoEm, RapServidores servidor,
			Set<MbcFichaVentilacao> mbcFichaVentilacoeses) {
		super();
		this.seq = seq;
		this.version = version;
		this.descricao = descricao;
		this.nomeReduzido = nomeReduzido;
		this.situacao = situacao;
		this.ordem = ordem;
		this.especificarSistema = especificarSistema;
		this.grupo = grupo;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.mbcFichaVentilacoeses = mbcFichaVentilacoeses;
	}


	@Id
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

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "NOME_REDUZIDO", nullable = false, length = 25)
	@Length(max = 25)
	public String getNomeReduzido() {
		return this.nomeReduzido;
	}

	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "ORDEM", nullable = false)
	public Short getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	@Column(name = "ESPECIFICAR_SISTEMA", nullable = false, length = 1)
	@Length(max = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEspecificarSistema() {
		return this.especificarSistema;
	}

	public void setEspecificarSistema(Boolean especificarSistema) {
		this.especificarSistema = especificarSistema;
	}

	@Column(name = "GRUPO", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioGrupoMbcVentilacao getGrupo() {
		return this.grupo;
	}

	public void setGrupo(DominioGrupoMbcVentilacao grupo) {
		this.grupo = grupo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcVentilacoes")
	public Set<MbcFichaVentilacao> getMbcFichaVentilacoeses() {
		return this.mbcFichaVentilacoeses;
	}

	public void setMbcFichaVentilacoeses(
			Set<MbcFichaVentilacao> mbcFichaVentilacoeses) {
		this.mbcFichaVentilacoeses = mbcFichaVentilacoeses;
	}

	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		NOME_REDUZIDO("nomeReduzido"),
		SITUACAO("situacao"),
		ORDEM("ordem"),
		ESPECIFICAR_SISTEMA("especificarSistema"),
		GRUPO("grupo"),
		CRIADO_EM("criadoEm"),
		MBC_FICHA_VENTILACOESES("mbcFichaVentilacoeses");

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
		if (!(obj instanceof MbcVentilacao)) {
			return false;
		}
		MbcVentilacao other = (MbcVentilacao) obj;
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
