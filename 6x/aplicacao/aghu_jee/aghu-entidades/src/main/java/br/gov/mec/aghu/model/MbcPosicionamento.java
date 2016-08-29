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

import br.gov.mec.aghu.dominio.DominioGrupoMbcPosicionamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "MBC_POSICIONAMENTOS", schema = "AGH")
public class MbcPosicionamento extends BaseEntitySeq<Short> implements java.io.Serializable {

	private static final long serialVersionUID = 8194715233015434739L;
	private Short seq;
	private Integer version;
	private String descricao;
	private DominioSituacao situacao;
	private DominioGrupoMbcPosicionamento grupo;
	private Boolean informarInclinacao;
	private Boolean permiteBloqueio;
	private Short ordem;
	private Boolean especificar;
	private Date criadoEm;
	private RapServidores servidor;
	private Set<MbcNeuroeixoNvlPuncionados> mbcNeuroeixoNvlPuncionadoses = new HashSet<MbcNeuroeixoNvlPuncionados>(0);
	private Set<MbcFichaPosicionamento> mbcFichaPosicionamentoses = new HashSet<MbcFichaPosicionamento>(0);

	public MbcPosicionamento() {
	}

	public MbcPosicionamento(Short seq, Integer version, String descricao,
			DominioSituacao situacao, DominioGrupoMbcPosicionamento grupo,
			Boolean informarInclinacao, Boolean permiteBloqueio, Short ordem,
			Boolean especificar, Date criadoEm, RapServidores servidor,
			Set<MbcNeuroeixoNvlPuncionados> mbcNeuroeixoNvlPuncionadoses,
			Set<MbcFichaPosicionamento> mbcFichaPosicionamentoses) {
		super();
		this.seq = seq;
		this.version = version;
		this.descricao = descricao;
		this.situacao = situacao;
		this.grupo = grupo;
		this.informarInclinacao = informarInclinacao;
		this.permiteBloqueio = permiteBloqueio;
		this.ordem = ordem;
		this.especificar = especificar;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.mbcNeuroeixoNvlPuncionadoses = mbcNeuroeixoNvlPuncionadoses;
		this.mbcFichaPosicionamentoses = mbcFichaPosicionamentoses;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
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

	@Column(name = "DESCRICAO", nullable = false, length = 120)
	@Length(max = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "GRUPO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioGrupoMbcPosicionamento getGrupo() {
		return this.grupo;
	}

	public void setGrupo(DominioGrupoMbcPosicionamento grupo) {
		this.grupo = grupo;
	}

	@Column(name = "INFORMAR_INCLINACAO", nullable = false, length = 1)
	@Length(max = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getInformarInclinacao() {
		return this.informarInclinacao;
	}

	public void setInformarInclinacao(Boolean informarInclinacao) {
		this.informarInclinacao = informarInclinacao;
	}

	@Column(name = "PERMITE_BLOQUEIO", nullable = false, length = 1)
	@Length(max = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPermiteBloqueio() {
		return this.permiteBloqueio;
	}

	public void setPermiteBloqueio(Boolean permiteBloqueio) {
		this.permiteBloqueio = permiteBloqueio;
	}

	@Column(name = "ORDEM", nullable = false)
	public Short getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	@Column(name = "ESPECIFICAR", nullable = false, length = 1)
	@Length(max = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEspecificar() {
		return this.especificar;
	}

	public void setEspecificar(Boolean especificar) {
		this.especificar = especificar;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcPosicionamentos")
	public Set<MbcNeuroeixoNvlPuncionados> getMbcNeuroeixoNvlPuncionadoses() {
		return this.mbcNeuroeixoNvlPuncionadoses;
	}

	public void setMbcNeuroeixoNvlPuncionadoses(
			Set<MbcNeuroeixoNvlPuncionados> mbcNeuroeixoNvlPuncionadoses) {
		this.mbcNeuroeixoNvlPuncionadoses = mbcNeuroeixoNvlPuncionadoses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcPosicionamentos")
	public Set<MbcFichaPosicionamento> getMbcFichaPosicionamentoses() {
		return this.mbcFichaPosicionamentoses;
	}

	public void setMbcFichaPosicionamentoses(
			Set<MbcFichaPosicionamento> mbcFichaPosicionamentoses) {
		this.mbcFichaPosicionamentoses = mbcFichaPosicionamentoses;
	}

	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		SITUACAO("situacao"),
		GRUPO("grupo"),
		INFORMAR_INCLINACAO("informarInclinacao"),
		PERMITE_BLOQUEIO("permiteBloqueio"),
		ORDEM("ordem"),
		ESPECIFICAR("especificar"),
		CRIADO_EM("criadoEm"),
		MBC_NEUROEIXO_NVL_PUNCIONADOSES("mbcNeuroeixoNvlPuncionadoses"),
		MBC_FICHA_POSICIONAMENTOSES("mbcFichaPosicionamentoses");

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
		if (!(obj instanceof MbcPosicionamento)) {
			return false;
		}
		MbcPosicionamento other = (MbcPosicionamento) obj;
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
