package br.gov.mec.aghu.model;

import java.util.Date;
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


import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
// @ORADB Function AELC_GET_AEL_CGU_SQ1_NEXTVAL
@SequenceGenerator(name="aelCguSq1", sequenceName="AGH.AEL_CGU_SQ1", allocationSize = 1)
@Table(name = "AEL_CAD_GUICHES", schema = "AGH")
public class AelCadGuiche extends BaseEntitySeq<Short> implements java.io.Serializable {

	private static final long serialVersionUID = 8085236377635670129L;
	
	private Short seq;
	private Integer version;
	private String descricao;
	private DominioSimNao ocupado;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private AghUnidadesFuncionais unidadeFuncional;
	private String machine;
	private String usuario;
	private Set<AelMovimentoGuiche> aelMovimentoGuiches; 

	public AelCadGuiche() {
	}

	public AelCadGuiche(final Short seq, final String descricao, final DominioSimNao ocupado, final DominioSituacao indSituacao, final Date criadoEm,
			final RapServidores servidor) {
		this.seq = seq;
		this.descricao = descricao;
		this.ocupado = ocupado;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	public AelCadGuiche(final Short seq, final String descricao, final DominioSimNao ocupado, final DominioSituacao indSituacao, final Date criadoEm,
			final RapServidores servidor, final AghUnidadesFuncionais unidadeFuncional, final String machine, final String usuario) {
		this.seq = seq;
		this.descricao = descricao;
		this.ocupado = ocupado;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.unidadeFuncional = unidadeFuncional;
		this.machine = machine;
		this.usuario = usuario;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelCguSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(final Short seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 9, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(final Integer version) {
		this.version = version;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(final String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "OCUPADO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getOcupado() {
		return this.ocupado;
	}

	public void setOcupado(final DominioSimNao ocupado) {
		this.ocupado = ocupado;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(final DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(final Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	/**
	 * responsável.
	 * 
	 * @return
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", nullable = true)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return this.unidadeFuncional;
	}

	public void setUnidadeFuncional(final AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@Column(name = "MACHINE", length = 64)
	public String getMachine() {
		return this.machine;
	}

	public void setMachine(final String machine) {
		this.machine = machine;
	}

	@Column(name = "USUARIO", length = 30)
	public String getUsuario() {
		return this.usuario;
	}

	public void setUsuario(final String usuario) {
		this.usuario = usuario;
	}

	public void setAelMovimentoGuiches(final Set<AelMovimentoGuiche> aelMovimentoGuiches) {
		this.aelMovimentoGuiches = aelMovimentoGuiches;
	}

	@OneToMany(mappedBy="aelCadGuiche")
	public Set<AelMovimentoGuiche> getAelMovimentoGuiches() {
		return aelMovimentoGuiches;
	}

	public void setServidor(final RapServidores servidor) {
		this.servidor = servidor;
	}

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		OCUPADO("ocupado"),
		UNIDADE_FUNCIONAL("unidadeFuncional"),
		UNIDADE_FUNCIONAL_SEQ("unidadeFuncional.seq"),
		MOVIMENTO_GUICHES("aelMovimentoGuiches"),
		USUARIO("usuario")
		;

		private String fields;

		private Fields(final String fields) {
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
		if (!(obj instanceof AelCadGuiche)) {
			return false;
		}
		AelCadGuiche other = (AelCadGuiche) obj;
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
