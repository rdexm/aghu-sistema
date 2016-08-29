	package br.gov.mec.aghu.model;

import java.io.Serializable;
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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="anuTiuSq1", sequenceName="AGH.ANU_TIU_SQ1", allocationSize = 1)
@Table(name = "ANU_TIPO_ITEM_DIETA_UNFS", schema = "AGH")
public class AnuTipoItemDietaUnfs extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2460650980223320297L;
	private Integer seq;
	private AnuTipoItemDieta tipoItemDieta;
	private AghUnidadesFuncionais unidadeFuncional;
	private RapServidores servidor;
	private Date criadoEm;

	// construtores

	public AnuTipoItemDietaUnfs() {
	}

	public AnuTipoItemDietaUnfs(Integer seq) {
		this.seq = seq;
	}

	// getters & setters
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "anuTiuSq1")
	@Column(name = "SEQ", length = 5, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne
	@JoinColumn(name = "TID_SEQ")
	public AnuTipoItemDieta getTipoItemDieta() {
		return tipoItemDieta;
	}

	public void setTipoItemDieta(AnuTipoItemDieta tipoItemDieta) {
		this.tipoItemDieta = tipoItemDieta;
	}

	@ManyToOne
	@JoinColumn(name = "UNF_SEQ")
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AnuTipoItemDietaUnfs)) {
			return false;
		}
		AnuTipoItemDietaUnfs castOther = (AnuTipoItemDietaUnfs) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), //
		TIPO_ITEM_DIETA("tipoItemDieta"), // 
		UNIDADE_FUNCIONAL("unidadeFuncional"),
		CRIADO_EM("criadoEm");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}