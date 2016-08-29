package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioCorApgar;
import br.gov.mec.aghu.dominio.DominioEsforcoResp;
import br.gov.mec.aghu.dominio.DominioFreqCardicaca;
import br.gov.mec.aghu.dominio.DominioIrritabilidade;
import br.gov.mec.aghu.dominio.DominioTonoMuscular;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MCO_APGARS", schema = "AGH")
public class McoApgars extends BaseEntityId<McoApgarsId> implements java.io.Serializable {

	private static final long serialVersionUID = 2243483440795621043L;
	
	private McoApgarsId id;
	private McoRecemNascidos mcoRecemNascidos;
	private DominioFreqCardicaca freqCardiaca1;
	private DominioEsforcoResp esforcoResp1;
	private DominioTonoMuscular tonoMuscular1;
	private DominioIrritabilidade irritabilidade1;
	private DominioCorApgar cor1;
	private Byte apgar1;
	private DominioFreqCardicaca freqCardiaca5;
	private DominioEsforcoResp esforcoResp5;
	private DominioTonoMuscular tonoMuscular5;
	private DominioIrritabilidade irritabilidade5;
	private DominioCorApgar cor5;
	private Byte apgar5;
	private DominioFreqCardicaca freqCardiaca10;
	private DominioEsforcoResp esforcoResp10;
	private DominioTonoMuscular tonoMuscular10;
	private DominioIrritabilidade irritabilidade10;
	private DominioCorApgar cor10;
	private Byte apgar10;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer pacCodigo;

	public McoApgars() {
	}

	public McoApgars(McoApgarsId id, McoRecemNascidos mcoRecemNascidos,
			Date criadoEm, Integer pacCodigo) {
		this.id = id;
		this.mcoRecemNascidos = mcoRecemNascidos;
		this.criadoEm = criadoEm;
		this.pacCodigo = pacCodigo;
	}

	public McoApgars(McoApgarsId id, McoRecemNascidos mcoRecemNascidos,
			DominioFreqCardicaca freqCardiaca1, DominioEsforcoResp esforcoResp1, DominioTonoMuscular tonoMuscular1,
			DominioIrritabilidade irritabilidade1, DominioCorApgar cor1, Byte apgar1,
			DominioFreqCardicaca freqCardiaca5, DominioEsforcoResp esforcoResp5, DominioTonoMuscular tonoMuscular5,
			DominioIrritabilidade irritabilidade5, DominioCorApgar cor5, Byte apgar5,
			DominioFreqCardicaca freqCardiaca10, DominioEsforcoResp esforcoResp10, DominioTonoMuscular tonoMuscular10,
			DominioIrritabilidade irritabilidade10, DominioCorApgar cor10, Byte apgar10, Date criadoEm,
			Integer pacCodigo) {
		this.id = id;
		this.mcoRecemNascidos = mcoRecemNascidos;
		this.freqCardiaca1 = freqCardiaca1;
		this.esforcoResp1 = esforcoResp1;
		this.tonoMuscular1 = tonoMuscular1;
		this.irritabilidade1 = irritabilidade1;
		this.cor1 = cor1;
		this.apgar1 = apgar1;
		this.freqCardiaca5 = freqCardiaca5;
		this.esforcoResp5 = esforcoResp5;
		this.tonoMuscular5 = tonoMuscular5;
		this.irritabilidade5 = irritabilidade5;
		this.cor5 = cor5;
		this.apgar5 = apgar5;
		this.freqCardiaca10 = freqCardiaca10;
		this.esforcoResp10 = esforcoResp10;
		this.tonoMuscular10 = tonoMuscular10;
		this.irritabilidade10 = irritabilidade10;
		this.cor10 = cor10;
		this.apgar10 = apgar10;
		this.criadoEm = criadoEm;
		this.pacCodigo = pacCodigo;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "rnaGsoPacCodigo", column = @Column(name = "RNA_GSO_PAC_CODIGO", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "rnaGsoSeqp", column = @Column(name = "RNA_GSO_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "rnaSeqp", column = @Column(name = "RNA_SEQP", nullable = false, precision = 2, scale = 0)),
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)) })
	@NotNull
	public McoApgarsId getId() {
		return this.id;
	}

	public void setId(McoApgarsId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "RNA_GSO_PAC_CODIGO", referencedColumnName = "GSO_PAC_CODIGO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "RNA_GSO_SEQP", referencedColumnName = "GSO_SEQP", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "RNA_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public McoRecemNascidos getMcoRecemNascidos() {
		return this.mcoRecemNascidos;
	}

	public void setMcoRecemNascidos(McoRecemNascidos mcoRecemNascidos) {
		this.mcoRecemNascidos = mcoRecemNascidos;
	}

	@Column(name = "FREQ_CARDIACA_1", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioFreqCardicaca") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioFreqCardicaca getFreqCardiaca1() {
		return this.freqCardiaca1;
	}

	public void setFreqCardiaca1(DominioFreqCardicaca freqCardiaca1) {
		this.freqCardiaca1 = freqCardiaca1;
	}

	@Column(name = "ESFORCO_RESP_1", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioEsforcoResp") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioEsforcoResp getEsforcoResp1() {
		return this.esforcoResp1;
	}

	public void setEsforcoResp1(DominioEsforcoResp esforcoResp1) {
		this.esforcoResp1 = esforcoResp1;
	}

	@Column(name = "TONO_MUSCULAR_1", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioTonoMuscular") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioTonoMuscular getTonoMuscular1() {
		return this.tonoMuscular1;
	}

	public void setTonoMuscular1(DominioTonoMuscular tonoMuscular1) {
		this.tonoMuscular1 = tonoMuscular1;
	}

	@Column(name = "IRRITABILIDADE_1", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioIrritabilidade") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioIrritabilidade getIrritabilidade1() {
		return this.irritabilidade1;
	}

	public void setIrritabilidade1(DominioIrritabilidade irritabilidade1) {
		this.irritabilidade1 = irritabilidade1;
	}

	@Column(name = "COR_1", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioCorApgar") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioCorApgar getCor1() {
		return this.cor1;
	}

	public void setCor1(DominioCorApgar cor1) {
		this.cor1 = cor1;
	}

	@Column(name = "APGAR_1", precision = 2, scale = 0)
	public Byte getApgar1() {
		return this.apgar1;
	}

	public void setApgar1(Byte apgar1) {
		this.apgar1 = apgar1;
	}

	@Column(name = "FREQ_CARDIACA_5", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioFreqCardicaca") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioFreqCardicaca getFreqCardiaca5() {
		return this.freqCardiaca5;
	}

	public void setFreqCardiaca5(DominioFreqCardicaca freqCardiaca5) {
		this.freqCardiaca5 = freqCardiaca5;
	}

	@Column(name = "ESFORCO_RESP_5", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioEsforcoResp") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioEsforcoResp getEsforcoResp5() {
		return this.esforcoResp5;
	}

	public void setEsforcoResp5(DominioEsforcoResp esforcoResp5) {
		this.esforcoResp5 = esforcoResp5;
	}

	@Column(name = "TONO_MUSCULAR_5", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioTonoMuscular") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioTonoMuscular getTonoMuscular5() {
		return this.tonoMuscular5;
	}

	public void setTonoMuscular5(DominioTonoMuscular tonoMuscular5) {
		this.tonoMuscular5 = tonoMuscular5;
	}

	@Column(name = "IRRITABILIDADE_5", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioIrritabilidade") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioIrritabilidade getIrritabilidade5() {
		return this.irritabilidade5;
	}

	public void setIrritabilidade5(DominioIrritabilidade irritabilidade5) {
		this.irritabilidade5 = irritabilidade5;
	}

	@Column(name = "COR_5", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioCorApgar") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioCorApgar getCor5() {
		return this.cor5;
	}

	public void setCor5(DominioCorApgar cor5) {
		this.cor5 = cor5;
	}

	@Column(name = "APGAR_5", precision = 2, scale = 0)
	public Byte getApgar5() {
		return this.apgar5;
	}

	public void setApgar5(Byte apgar5) {
		this.apgar5 = apgar5;
	}

	@Column(name = "FREQ_CARDIACA_10", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioFreqCardicaca") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioFreqCardicaca getFreqCardiaca10() {
		return this.freqCardiaca10;
	}

	public void setFreqCardiaca10(DominioFreqCardicaca freqCardiaca10) {
		this.freqCardiaca10 = freqCardiaca10;
	}

	@Column(name = "ESFORCO_RESP_10", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioEsforcoResp") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioEsforcoResp getEsforcoResp10() {
		return this.esforcoResp10;
	}

	public void setEsforcoResp10(DominioEsforcoResp esforcoResp10) {
		this.esforcoResp10 = esforcoResp10;
	}

	@Column(name = "TONO_MUSCULAR_10", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioTonoMuscular") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioTonoMuscular getTonoMuscular10() {
		return this.tonoMuscular10;
	}

	public void setTonoMuscular10(DominioTonoMuscular tonoMuscular10) {
		this.tonoMuscular10 = tonoMuscular10;
	}

	@Column(name = "IRRITABILIDADE_10", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioIrritabilidade") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioIrritabilidade getIrritabilidade10() {
		return this.irritabilidade10;
	}

	public void setIrritabilidade10(DominioIrritabilidade irritabilidade10) {
		this.irritabilidade10 = irritabilidade10;
	}

	@Column(name = "COR_10", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioCorApgar") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")
	public DominioCorApgar getCor10() {
		return this.cor10;
	}

	public void setCor10(DominioCorApgar cor10) {
		this.cor10 = cor10;
	}

	@Column(name = "APGAR_10", precision = 2, scale = 0)
	public Byte getApgar10() {
		return this.apgar10;
	}

	public void setApgar10(Byte apgar10) {
		this.apgar10 = apgar10;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", nullable = false, length = 7)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	@Column(name = "PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	@NotNull
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	public enum Fields {
		CODIGO_PACIENTE("pacCodigo"),
		RECEM_NASCIDO_CODIGO_PACIENTE("id.rnaGsoPacCodigo"), 
		RECEM_NASCIDO_SEQUENCE("id.rnaGsoSeqp"),
		SER_MATRICULA("id.serMatricula"),
		SER_VIN_CODIGO("id.serVinCodigo"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),		
		RNA_GSO_PAC_CODIGO("id.rnaGsoPacCodigo"),
		RNA_GSO_SEQP("id.rnaGsoSeqp"),
		RNA_SEQP("id.rnaSeqp");
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
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof McoApgars)) {
			return false;
		}
		McoApgars other = (McoApgars) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

}
