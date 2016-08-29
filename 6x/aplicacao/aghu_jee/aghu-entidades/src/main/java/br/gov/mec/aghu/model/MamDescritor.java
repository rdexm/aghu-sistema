package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "MAM_DESCRITORES", schema = "AGH")
@SequenceGenerator(name = "mamDctSeq", sequenceName = "AGH.MAM_DCT_SQ1", allocationSize = 1)
public class MamDescritor extends BaseEntitySeq<Integer> implements Serializable {
	private static final long serialVersionUID = -8435078245263487770L;

	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Integer ordem;

	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	private MamFluxograma fluxograma;
	private MamGravidade gravidade;

	private Boolean indDtQueixaObgt;
	private Boolean indHrQueixaObgt;
	
	private Set<MamObrigatoriedade> mamObrigatoriedades = new HashSet<MamObrigatoriedade>(0);
	private Set<MamTrgGravidade> mamTrgGravidade = new HashSet<MamTrgGravidade>(0);

	private Integer version;

	public MamDescritor() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamDctSeq")
	@Column(name = "SEQ", nullable = false)
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

	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 200)
	@NotNull
	@Length(max = 200)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ORDEM", nullable = false)
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FLX_SEQ", nullable = false)
	public MamFluxograma getFluxograma() {
		return fluxograma;
	}

	public void setFluxograma(MamFluxograma fluxograma) {
		this.fluxograma = fluxograma;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "GRV_SEQ", nullable = false)
	public MamGravidade getGravidade() {
		return gravidade;
	}

	public void setGravidade(MamGravidade gravidade) {
		this.gravidade = gravidade;
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

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_DT_QUEIXA_OBGT", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	@NotNull
	public Boolean getIndDtQueixaObgt() {
		return indDtQueixaObgt;
	}

	public void setIndDtQueixaObgt(Boolean indDtQueixaObgt) {
		this.indDtQueixaObgt = indDtQueixaObgt;
	}

	@Column(name = "IND_HR_QUEIXA_OBGT", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	@NotNull
	public Boolean getIndHrQueixaObgt() {
		return indHrQueixaObgt;
	}

	public void setIndHrQueixaObgt(Boolean indHrQueixaObgt) {
		this.indHrQueixaObgt = indHrQueixaObgt;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamDescritor")
	public Set<MamObrigatoriedade> getMamObrigatoriedades() {
		return mamObrigatoriedades;
	}

	public void setMamObrigatoriedades(Set<MamObrigatoriedade> mamObrigatoriedades) {
		this.mamObrigatoriedades = mamObrigatoriedades;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamDescritor")
	public Set<MamTrgGravidade> getMamTrgGravidade() {
		return mamTrgGravidade;
	}

	public void setMamTrgGravidade(Set<MamTrgGravidade> mamTrgGravidade) {
		this.mamTrgGravidade = mamTrgGravidade;
	}

	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		ORDEM("ordem"),
		FLUXOGRAMA("fluxograma"),
		GRAVIDADE("gravidade"),
		IND_DT_QUEIXA_OBGT("indDtQueixaObgt"),
		IND_HR_QUEIXA_OBGT("indHrQueixaObgt"),
		MAM_OBRIGATORIEDADE("mamObrigatoriedades"),
		MAM_TRG_GRAVIDADE("mamTrgGravidade"),
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
}
