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
@SequenceGenerator(name="aelLuwSq1", sequenceName="AGH.AEL_LUW_SQ1", allocationSize = 1)
@Table(name = "AEL_IMAGEM_APS", schema = "AGH")
public class AelImagemAp extends BaseEntitySeq<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7657295763585088598L;
	private Long seq;
	private Integer version;
	private AelExameAp aelExameAp;
	private RapServidores rapServidores;
	private String legenda;
	private String selecionada;
	private byte[] imagem;
	private Date criadoEm;
	private String indFigura;

	public AelImagemAp() {
	}

	public AelImagemAp(Long seq, AelExameAp aelExameAp, RapServidores rapServidores, String selecionada, Date criadoEm,
			String indFigura) {
		this.seq = seq;
		this.aelExameAp = aelExameAp;
		this.rapServidores = rapServidores;
		this.selecionada = selecionada;
		this.criadoEm = criadoEm;
		this.indFigura = indFigura;
	}

	public AelImagemAp(Long seq, AelExameAp aelExameAp, RapServidores rapServidores, String legenda, String selecionada,
			byte[] imagem, Date criadoEm, String indFigura) {
		this.seq = seq;
		this.aelExameAp = aelExameAp;
		this.rapServidores = rapServidores;
		this.legenda = legenda;
		this.selecionada = selecionada;
		this.imagem = imagem;
		this.criadoEm = criadoEm;
		this.indFigura = indFigura;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLuwSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
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
	@JoinColumn(name = "LUX_SEQ", nullable = false)
	public AelExameAp getAelExameAp() {
		return this.aelExameAp;
	}

	public void setAelExameAp(AelExameAp aelExameAp) {
		this.aelExameAp = aelExameAp;
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

	@Column(name = "LEGENDA", length = 4000)
	@Length(max = 4000)
	public String getLegenda() {
		return this.legenda;
	}

	public void setLegenda(String legenda) {
		this.legenda = legenda;
	}

	@Column(name = "SELECIONADA", nullable = false, length = 1)
	@Length(max = 1)
	public String getSelecionada() {
		return this.selecionada;
	}

	public void setSelecionada(String selecionada) {
		this.selecionada = selecionada;
	}

	@Column(name = "IMAGEM")
	public byte[] getImagem() {
		return this.imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_FIGURA", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndFigura() {
		return this.indFigura;
	}

	public void setIndFigura(String indFigura) {
		this.indFigura = indFigura;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		AEL_EXAME_APS("aelExameAp"),
		RAP_SERVIDORES("rapServidores"),
		LEGENDA("legenda"),
		SELECIONADA("selecionada"),
		IMAGEM("imagem"),
		CRIADO_EM("criadoEm"),
		IND_FIGURA("indFigura");

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
		if (!(obj instanceof AelImagemAp)) {
			return false;
		}
		AelImagemAp other = (AelImagemAp) obj;
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
