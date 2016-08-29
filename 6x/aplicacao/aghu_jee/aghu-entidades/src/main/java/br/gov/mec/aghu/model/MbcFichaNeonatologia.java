package br.gov.mec.aghu.model;

// Generated 28/03/2012 15:17:44 by Hibernate Tools 3.4.0.CR1

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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mbcFneSq1", sequenceName="AGH.MBC_FNE_SQ1", allocationSize = 1)
@Table(name = "MBC_FICHA_NEONATOLOGIAS", schema = "AGH")
public class MbcFichaNeonatologia extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -6841385233823545053L;
	private Integer seq;
	private Integer version;
	private MbcFichaAnestesias mbcFichaAnestesias;
	private Float racaoHidrica;
	private Float txInfusaoGlicose;
	private Float volumeSoro;
	private Float volumeSangue;
	private Float volumePlasma;
	private Float diurese;
	private Boolean acessoCentralPic;
	private Boolean puncaoUmbilical;
	private RapServidores servidor;
	private Date criadoEm;
	private Short igSemanas;

	public MbcFichaNeonatologia() {
	}

	public MbcFichaNeonatologia(Integer seq, Integer version,
			MbcFichaAnestesias mbcFichaAnestesias, Float racaoHidrica,
			Float txInfusaoGlicose, Float volumeSoro, Float volumeSangue,
			Float volumePlasma, Float diurese, Boolean acessoCentralPic,
			Boolean puncaoUmbilical, RapServidores servidor, Date criadoEm,
			Short igSemanas) {
		super();
		this.seq = seq;
		this.version = version;
		this.mbcFichaAnestesias = mbcFichaAnestesias;
		this.racaoHidrica = racaoHidrica;
		this.txInfusaoGlicose = txInfusaoGlicose;
		this.volumeSoro = volumeSoro;
		this.volumeSangue = volumeSangue;
		this.volumePlasma = volumePlasma;
		this.diurese = diurese;
		this.acessoCentralPic = acessoCentralPic;
		this.puncaoUmbilical = puncaoUmbilical;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
		this.igSemanas = igSemanas;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcFneSq1")
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
	@JoinColumn(name = "FIC_SEQ", nullable = false)
	public MbcFichaAnestesias getMbcFichaAnestesias() {
		return this.mbcFichaAnestesias;
	}

	public void setMbcFichaAnestesias(MbcFichaAnestesias mbcFichaAnestesias) {
		this.mbcFichaAnestesias = mbcFichaAnestesias;
	}

	@Column(name = "RACAO_HIDRICA", precision = 8, scale = 8)
	public Float getRacaoHidrica() {
		return this.racaoHidrica;
	}

	public void setRacaoHidrica(Float racaoHidrica) {
		this.racaoHidrica = racaoHidrica;
	}

	@Column(name = "TX_INFUSAO_GLICOSE", precision = 8, scale = 8)
	public Float getTxInfusaoGlicose() {
		return this.txInfusaoGlicose;
	}

	public void setTxInfusaoGlicose(Float txInfusaoGlicose) {
		this.txInfusaoGlicose = txInfusaoGlicose;
	}

	@Column(name = "VOLUME_SORO", precision = 8, scale = 8)
	public Float getVolumeSoro() {
		return this.volumeSoro;
	}

	public void setVolumeSoro(Float volumeSoro) {
		this.volumeSoro = volumeSoro;
	}

	@Column(name = "VOLUME_SANGUE", precision = 8, scale = 8)
	public Float getVolumeSangue() {
		return this.volumeSangue;
	}

	public void setVolumeSangue(Float volumeSangue) {
		this.volumeSangue = volumeSangue;
	}

	@Column(name = "VOLUME_PLASMA", precision = 8, scale = 8)
	public Float getVolumePlasma() {
		return this.volumePlasma;
	}

	public void setVolumePlasma(Float volumePlasma) {
		this.volumePlasma = volumePlasma;
	}

	@Column(name = "DIURESE", precision = 8, scale = 8)
	public Float getDiurese() {
		return this.diurese;
	}

	public void setDiurese(Float diurese) {
		this.diurese = diurese;
	}

	@Column(name = "ACESSO_CENTRAL_PIC", nullable = false, length = 1)
	@Length(max = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getAcessoCentralPic() {
		return this.acessoCentralPic;
	}

	public void setAcessoCentralPic(Boolean acessoCentralPic) {
		this.acessoCentralPic = acessoCentralPic;
	}

	@Column(name = "PUNCAO_UMBILICAL", nullable = false, length = 1)
	@Length(max = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPuncaoUmbilical() {
		return this.puncaoUmbilical;
	}

	public void setPuncaoUmbilical(Boolean puncaoUmbilical) {
		this.puncaoUmbilical = puncaoUmbilical;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IG_SEMANAS")
	public Short getIgSemanas() {
		return this.igSemanas;
	}

	public void setIgSemanas(Short igSemanas) {
		this.igSemanas = igSemanas;
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
	
	public enum Fields {

		SEQ("seq"),
		RACAO_HIDRICA("racaoHidrica"),
		TX_INFUSAO_GLICOSE("txInfusaoGlicose"),
		VOLUME_SORO("volumeSoro"),
		VOLUME_SANGUE("volumeSangue"),
		VOLUME_PLASMA("volumePlasma"),
		DIURESE("diurese"),
		ACESSO_CENTRAL_PIC("acessoCentralPic"),
		PUNCAO_UMBILICAL("puncaoUmbilical"),
		CRIADO_EM("criadoEm"),
		IG_SEMANAS("igSemanas"),
		FICHA_ANESTESIA("mbcFichaAnestesias"),
		SERVIDOR("servidor"),
		FICHA_ANESTESIA_SEQ("mbcFichaAnestesias.seq"),;
		
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
		if (!(obj instanceof MbcFichaNeonatologia)) {
			return false;
		}
		MbcFichaNeonatologia other = (MbcFichaNeonatologia) obj;
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
