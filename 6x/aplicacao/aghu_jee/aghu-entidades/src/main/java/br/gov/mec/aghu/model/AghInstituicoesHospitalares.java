package br.gov.mec.aghu.model;

// Generated 13/05/2010 18:43:48 by Hibernate Tools 3.2.5.Beta

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "AGH_INSTITUICOES_HOSPITALARES", schema = "AGH")
@SequenceGenerator(name = "AghInstituicoesHospitalaresSeq", sequenceName = "AGH.AGH_IHO_SQ1", allocationSize = 1)
public class AghInstituicoesHospitalares extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 469374634269368110L;
	private Integer seq;
	private String nome;
	private String cidade;
	private AipCidades cddCodigo;
	private AipUfs ufSigla;
	private Integer cnes;
	private Integer codIbge;
	private Boolean indLocal;
	private Integer version;

	public AghInstituicoesHospitalares() {
	}

	public AghInstituicoesHospitalares(Integer seq, String nome) {
		this.seq = seq;
		this.nome = nome;
	}

	public AghInstituicoesHospitalares(Integer seq, String nome, String cidade,
			AipCidades cddCodigo, AipUfs ufSigla) {
		this.seq = seq;
		this.nome = nome;
		this.cidade = cidade;
		this.cddCodigo = cddCodigo;
		this.ufSigla = ufSigla;
	}

	public AghInstituicoesHospitalares(Integer seq, String nome, String cidade,
			AipCidades cddCodigo, AipUfs ufSigla, Integer cnes,
			Integer codIbge, Boolean indLocal) {
		super();
		this.seq = seq;
		this.nome = nome;
		this.cidade = cidade;
		this.cddCodigo = cddCodigo;
		this.ufSigla = ufSigla;
		this.cnes = cnes;
		this.codIbge = codIbge;
		this.indLocal = indLocal;
	}

	public enum Fields {
		SEQ("seq"), NOME("nome"), CIDADE("cidade"), CODCIDADE("cddCodigo"), SIGLA(
				"ufSigla"), CNES("cnes"), COD_IBGE("codIbge"), IND_LOCAL(
				"indLocal");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Id
	@Column(name = "SEQ", nullable = false, precision = 2, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AghInstituicoesHospitalaresSeq")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NOME", nullable = false, length = 60)
	@Length(max = 60)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "CIDADE", length = 60)
	@Length(max = 60)
	public String getCidade() {
		return this.cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDD_CODIGO", nullable = true)
	public AipCidades getCddCodigo() {
		return this.cddCodigo;
	}

	public void setCddCodigo(AipCidades cddCodigo) {
		this.cddCodigo = cddCodigo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UF_SIGLA", nullable = true)
	public AipUfs getUfSigla() {
		return this.ufSigla;
	}

	public void setUfSigla(AipUfs ufSigla) {
		this.ufSigla = ufSigla;
	}

	@Column(name = "CNES", precision = 7, scale = 0)
	public Integer getCnes() {
		return cnes;
	}

	public void setCnes(Integer cnes) {
		this.cnes = cnes;
	}

	@Column(name = "COD_IBGE", precision = 7, scale = 0)
	public Integer getCodIbge() {
		return codIbge;
	}

	public void setCodIbge(Integer codIbge) {
		this.codIbge = codIbge;
	}

	@Column(name = "IND_LOCAL", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndLocal() {
		return indLocal;
	}

	public void setIndLocal(Boolean indLocal) {
		this.indLocal = indLocal;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
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
		if (!(obj instanceof AghInstituicoesHospitalares)) {
			return false;
		}
		AghInstituicoesHospitalares other = (AghInstituicoesHospitalares) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

}
