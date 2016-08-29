package br.gov.mec.aghu.model;

import java.math.BigDecimal;
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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioIndDigitaCompl;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDadoParametro;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "MAM_ITEM_SINAL_VITAIS", schema = "AGH")
@SequenceGenerator(name = "mamSviSq1", sequenceName = "AGH.MAM_SVI_SQ1", allocationSize = 1)
public class MamItemSinalVital extends BaseEntitySeq<Integer> implements java.io.Serializable {
	private static final long serialVersionUID = -7023888228112187438L;
	
	private Integer seq;
	private Integer version;
	private RapServidores rapServidores;
	private String descricao;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private DominioIndDigitaCompl indDigitaCompl;
	private Integer ordem;
	private DominioTipoDadoParametro tipoDado;
	private BigDecimal valorMinimo;
	private BigDecimal valorMaximo;
	private Set<MamUnidXSinalVital> mamUnidXSinalVitales = new HashSet<MamUnidXSinalVital>(0);
	public MamItemSinalVital() {
	}

	public MamItemSinalVital(Integer seq, RapServidores rapServidores, String descricao, Date criadoEm, DominioSituacao indSituacao,
			DominioIndDigitaCompl indDigitaCompl, Integer ordem, DominioTipoDadoParametro tipoDado) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.indDigitaCompl = indDigitaCompl;
		this.ordem = ordem;
		this.tipoDado = tipoDado;
	}

	public MamItemSinalVital(Integer seq, RapServidores rapServidores, String descricao, Date criadoEm, DominioSituacao indSituacao,
			DominioIndDigitaCompl indDigitaCompl, Integer ordem, DominioTipoDadoParametro tipoDado, BigDecimal valorMinimo, BigDecimal valorMaximo,
			Set<MamUnidXSinalVital> mamUnidXSinalVitales) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.indDigitaCompl = indDigitaCompl;
		this.ordem = ordem;
		this.tipoDado = tipoDado;
		this.valorMinimo = valorMinimo;
		this.valorMaximo = valorMaximo;
		this.mamUnidXSinalVitales = mamUnidXSinalVitales;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamSviSq1")
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

	@Column(name = "DESCRICAO", nullable = false, length = 240)
	@NotNull
	@Length(max = 240)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_DIGITA_COMPL", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndDigitaCompl getIndDigitaCompl() {
		return indDigitaCompl;
	}

	public void setIndDigitaCompl(DominioIndDigitaCompl indDigitaCompl) {
		this.indDigitaCompl = indDigitaCompl;
	}

	@Column(name = "ORDEM", nullable = false)
	@NotNull
	public Integer getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	@Column(name = "TIPO_DADO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoDadoParametro getTipoDado() {
		return tipoDado;
	}

	public void setTipoDado(DominioTipoDadoParametro tipoDado) {
		this.tipoDado = tipoDado;
	}

	@Column(name = "VALOR_MINIMO", precision = 10, scale = 4, nullable = false)
	@NotNull
	public BigDecimal getValorMinimo() {
		return valorMinimo;
	}

	public void setValorMinimo(BigDecimal valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	@Column(name = "VALOR_MAXIMO", precision = 10, scale = 4, nullable = false)
	@NotNull
	public BigDecimal getValorMaximo() {
		return valorMaximo;
	}

	public void setValorMaximo(BigDecimal valorMaximo) {
		this.valorMaximo = valorMaximo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamItemSinalVital")
	public Set<MamUnidXSinalVital> getMamUnidXSinalVitales() {
		return this.mamUnidXSinalVitales;
	}

	public void setMamUnidXSinalVitales(Set<MamUnidXSinalVital> mamUnidXSinalVitales) {
		this.mamUnidXSinalVitales = mamUnidXSinalVitales;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		ORDEM("ordem"),
		TIPO_DADO("tipoDado"),
		VALOR_MINIMO("valorMinimo"),
		VALOR_MAXIMO("valorMaximo"),
		MAM_UNID_X_SINAL_VITALES("mamUnidXSinalVitales"),
		IND_DIGITA_COMPL("indDigitaCompl");

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
		if (!(obj instanceof MamItemSinalVital)) {
			return false;
		}
		MamItemSinalVital other = (MamItemSinalVital) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}

}
