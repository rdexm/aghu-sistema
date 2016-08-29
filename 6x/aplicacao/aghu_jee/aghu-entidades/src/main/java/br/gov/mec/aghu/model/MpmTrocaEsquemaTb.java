package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Parameter;
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
@Table(name = "MPM_TROCA_ESQUEMA_TBS", schema = "AGH")
public class MpmTrocaEsquemaTb extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5626373443386988513L;
	private Integer jumSeq;
	private MpmTipoEsquemaTb mpmTipoEsquemaTbs;
	private MpmJustificativaUsoMdto mpmJustificativaUsoMdtos;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date criadoEm;
	private String descricao;
	private Set<MpmJustifTrocaEsquemaTb> mpmJustifTrocaEsquemaTbses = new HashSet<MpmJustifTrocaEsquemaTb>(
			0);

	public MpmTrocaEsquemaTb() {
	}

	public MpmTrocaEsquemaTb(MpmTipoEsquemaTb mpmTipoEsquemaTbs,
			MpmJustificativaUsoMdto mpmJustificativaUsoMdtos,
			Integer serMatricula, Short serVinCodigo, Date criadoEm) {
		this.mpmTipoEsquemaTbs = mpmTipoEsquemaTbs;
		this.mpmJustificativaUsoMdtos = mpmJustificativaUsoMdtos;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.criadoEm = criadoEm;
	}

	public MpmTrocaEsquemaTb(MpmTipoEsquemaTb mpmTipoEsquemaTbs,
			MpmJustificativaUsoMdto mpmJustificativaUsoMdtos,
			Integer serMatricula, Short serVinCodigo, Date criadoEm,
			String descricao,
			Set<MpmJustifTrocaEsquemaTb> mpmJustifTrocaEsquemaTbses) {
		this.mpmTipoEsquemaTbs = mpmTipoEsquemaTbs;
		this.mpmJustificativaUsoMdtos = mpmJustificativaUsoMdtos;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.criadoEm = criadoEm;
		this.descricao = descricao;
		this.mpmJustifTrocaEsquemaTbses = mpmJustifTrocaEsquemaTbses;
	}

	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "mpmJustificativaUsoMdtos"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "JUM_SEQ", unique = true, nullable = false, precision = 8, scale = 0)
	public Integer getJumSeq() {
		return this.jumSeq;
	}

	public void setJumSeq(Integer jumSeq) {
		this.jumSeq = jumSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TET_SEQ", nullable = false)
	public MpmTipoEsquemaTb getMpmTipoEsquemaTbs() {
		return this.mpmTipoEsquemaTbs;
	}

	public void setMpmTipoEsquemaTbs(MpmTipoEsquemaTb mpmTipoEsquemaTbs) {
		this.mpmTipoEsquemaTbs = mpmTipoEsquemaTbs;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public MpmJustificativaUsoMdto getMpmJustificativaUsoMdtos() {
		return this.mpmJustificativaUsoMdtos;
	}

	public void setMpmJustificativaUsoMdtos(
			MpmJustificativaUsoMdto mpmJustificativaUsoMdtos) {
		this.mpmJustificativaUsoMdtos = mpmJustificativaUsoMdtos;
	}

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "DESCRICAO", length = 500)
	@Length(max = 500)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "trocaEsquemaTb")
	public Set<MpmJustifTrocaEsquemaTb> getMpmJustifTrocaEsquemaTbses() {
		return this.mpmJustifTrocaEsquemaTbses;
	}

	public void setMpmJustifTrocaEsquemaTbses(
			Set<MpmJustifTrocaEsquemaTb> mpmJustifTrocaEsquemaTbses) {
		this.mpmJustifTrocaEsquemaTbses = mpmJustifTrocaEsquemaTbses;
	}

	public enum Fields {

		JUM_SEQ("jumSeq"),
		MPM_TIPO_ESQUEMA_TBS("mpmTipoEsquemaTbs"),
		MPM_JUSTIFICATIVA_USO_MDTOS("mpmJustificativaUsoMdtos"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		CRIADO_EM("criadoEm"),
		DESCRICAO("descricao"),
		MPM_JUSTIF_TROCA_ESQUEMA_TBSES("mpmJustifTrocaEsquemaTbses");

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
		result = prime * result + ((getJumSeq() == null) ? 0 : getJumSeq().hashCode());
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
		if (!(obj instanceof MpmTrocaEsquemaTb)) {
			return false;
		}
		MpmTrocaEsquemaTb other = (MpmTrocaEsquemaTb) obj;
		if (getJumSeq() == null) {
			if (other.getJumSeq() != null) {
				return false;
			}
		} else if (!getJumSeq().equals(other.getJumSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####
 
 @Transient public Integer getSeq(){ return this.getJumSeq();} 
 public void setSeq(Integer seq){ this.setJumSeq(seq);}
}
