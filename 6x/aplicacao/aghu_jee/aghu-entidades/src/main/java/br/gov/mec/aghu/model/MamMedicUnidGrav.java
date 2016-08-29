package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
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
@SequenceGenerator(name="mamMugSq1", sequenceName="AGH.MAM_MUG_SQ1", allocationSize = 1)
@Table(name = "MAM_MEDIC_UNID_GRAVS", schema = "AGH")
public class MamMedicUnidGrav extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6347509882791728146L;
	private Integer seq;
	private Integer version;
	private MamUnidXMedicacao mamUnidXMedicacao;
	private RapServidores rapServidores;
	private MamGravidade mamGravidade;
	private Double valor;
	private Short tipoComparacao;
	private String indSituacao;
	private Date criadoEm;
	private String micNome;

	public MamMedicUnidGrav() {
	}

	public MamMedicUnidGrav(Integer seq, MamUnidXMedicacao mamUnidXMedicacao, RapServidores rapServidores, MamGravidade mamGravidade,
			Double valor, Short tipoComparacao, String indSituacao, Date criadoEm) {
		this.seq = seq;
		this.mamUnidXMedicacao = mamUnidXMedicacao;
		this.rapServidores = rapServidores;
		this.mamGravidade = mamGravidade;
		this.valor = valor;
		this.tipoComparacao = tipoComparacao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	public MamMedicUnidGrav(Integer seq, MamUnidXMedicacao mamUnidXMedicacao, RapServidores rapServidores, MamGravidade mamGravidade,
			Double valor, Short tipoComparacao, String indSituacao, Date criadoEm, String micNome) {
		this.seq = seq;
		this.mamUnidXMedicacao = mamUnidXMedicacao;
		this.rapServidores = rapServidores;
		this.mamGravidade = mamGravidade;
		this.valor = valor;
		this.tipoComparacao = tipoComparacao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.micNome = micNome;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamMugSq1")
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
	@JoinColumns({ @JoinColumn(name = "UXM_MDM_SEQ", referencedColumnName = "MDM_SEQ", nullable = false),
			@JoinColumn(name = "UXM_UAN_UNF_SEQ", referencedColumnName = "UAN_UNF_SEQ", nullable = false) })
	public MamUnidXMedicacao getMamUnidXMedicacao() {
		return this.mamUnidXMedicacao;
	}

	public void setMamUnidXMedicacao(MamUnidXMedicacao mamUnidXMedicacao) {
		this.mamUnidXMedicacao = mamUnidXMedicacao;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GRV_SEQ", nullable = false)
	public MamGravidade getMamGravidade() {
		return this.mamGravidade;
	}

	public void setMamGravidade(MamGravidade mamGravidade) {
		this.mamGravidade = mamGravidade;
	}

	@Column(name = "VALOR", nullable = false, precision = 17, scale = 17)
	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	@Column(name = "TIPO_COMPARACAO", nullable = false)
	public Short getTipoComparacao() {
		return this.tipoComparacao;
	}

	public void setTipoComparacao(Short tipoComparacao) {
		this.tipoComparacao = tipoComparacao;
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

	@Column(name = "MIC_NOME", length = 50)
	@Length(max = 50)
	public String getMicNome() {
		return this.micNome;
	}

	public void setMicNome(String micNome) {
		this.micNome = micNome;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		MAM_UNID_X_MEDICACOES("mamUnidXMedicacao"),
		RAP_SERVIDORES("rapServidores"),
		MAM_GRAVIDADES("mamGravidade"),
		VALOR("valor"),
		TIPO_COMPARACAO("tipoComparacao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		MIC_NOME("micNome");

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
		if (!(obj instanceof MamMedicUnidGrav)) {
			return false;
		}
		MamMedicUnidGrav other = (MamMedicUnidGrav) obj;
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
