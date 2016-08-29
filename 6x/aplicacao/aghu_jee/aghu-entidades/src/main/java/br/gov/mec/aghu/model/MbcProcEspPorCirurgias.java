package br.gov.mec.aghu.model;

// Generated 19/03/2010 17:25:07 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_PROC_ESP_POR_CIRURGIAS", schema = "AGH")
public class MbcProcEspPorCirurgias extends BaseEntityId<MbcProcEspPorCirurgiasId> implements java.io.Serializable {

	private static final long serialVersionUID = -5371387730158210217L;
	private MbcProcEspPorCirurgiasId id;
	private MbcCirurgias cirurgia;
	private MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs;
	private DominioIndRespProc indRespProc;
	private RapServidores servidor;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Byte qtd;
	private Boolean indPrincipal;
	private FatProcedHospInternos procedHospInterno;
	private AghCid cid;
	private String pacOruAccNumber;
	private MbcProcedimentoCirurgicos procedimentoCirurgico; 
	

	public MbcProcEspPorCirurgias() {
	}

	public MbcProcEspPorCirurgias(MbcProcEspPorCirurgiasId id) {
		this.id = id;
	}

	public MbcProcEspPorCirurgias(MbcProcEspPorCirurgiasId id,
			RapServidores servidor, DominioSituacao situacao,
			Date criadoEm, Byte qtd, Boolean indPrincipal) {
		this.id = id;
		this.servidor = servidor;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.qtd = qtd;
		this.indPrincipal = indPrincipal;
	}

	public MbcProcEspPorCirurgias(MbcProcEspPorCirurgiasId id,
			RapServidores servidor, DominioSituacao situacao,
			Date criadoEm, Byte qtd, Boolean indPrincipal, FatProcedHospInternos procedHospInterno,
			AghCid cid, String pacOruAccNumber) {
		this.id = id;
		this.servidor = servidor;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.qtd = qtd;
		this.indPrincipal = indPrincipal;
		this.procedHospInterno = procedHospInterno;
		this.cid = cid;
		this.pacOruAccNumber = pacOruAccNumber;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "crgSeq", column = @Column(name = "CRG_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "eprPciSeq", column = @Column(name = "EPR_PCI_SEQ", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "eprEspSeq", column = @Column(name = "EPR_ESP_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "indRespProc", column = @Column(name = "IND_RESP_PROC", nullable = false, length = 4)) })
	public MbcProcEspPorCirurgiasId getId() {
		return this.id;
	}

	public void setId(MbcProcEspPorCirurgiasId id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="CRG_SEQ", referencedColumnName="SEQ", updatable = false, insertable = false)	
	public MbcCirurgias getCirurgia() {
		return this.cirurgia;
	}
	
	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}
	
	@Column(name = "IND_RESP_PROC", nullable = false, length = 4, updatable = false, insertable = false)
	@Enumerated(EnumType.STRING)
	public DominioIndRespProc getIndRespProc() {
		return this.indRespProc;
	}

	public void setIndRespProc(DominioIndRespProc indRespProc) {
		this.indRespProc = indRespProc;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "QTD", nullable = false, precision = 2, scale = 0)
	@Min(value = 1)
	public Byte getQtd() {
		return this.qtd;
	}

	public void setQtd(Byte qtd) {
		this.qtd = qtd;
	}

	@Column(name = "PAC_ORU_ACC_NUMBER", length = 50)
	@Length(max = 50)
	public String getPacOruAccNumber() {
		return this.pacOruAccNumber;
	}

	public void setPacOruAccNumber(String pacOruAccNumber) {
		this.pacOruAccNumber = pacOruAccNumber;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EPR_PCI_SEQ", insertable=false, updatable=false,referencedColumnName="SEQ")
	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return this.procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(
			MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( { 
		@JoinColumn(name = "EPR_ESP_SEQ", insertable = false, updatable = false, referencedColumnName="ESP_SEQ"),
		@JoinColumn(name = "EPR_PCI_SEQ", insertable = false, updatable = false, referencedColumnName="PCI_SEQ") })
	public MbcEspecialidadeProcCirgs getMbcEspecialidadeProcCirgs() {
		return this.mbcEspecialidadeProcCirgs;
	}

	public void setMbcEspecialidadeProcCirgs(
			MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs) {
		this.mbcEspecialidadeProcCirgs = mbcEspecialidadeProcCirgs;
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
		if (!(obj instanceof MbcProcEspPorCirurgias)) {
			return false;
		}
		MbcProcEspPorCirurgias other = (MbcProcEspPorCirurgias) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())){
			return false;
		}
		return true;
	}



	public enum Fields {
		ID_CRG_SEQ("cirurgia.seq"),//
		ID_EPR_PCI_SEQ("id.eprPciSeq"),//
		EPR_PCI_SEQ("id.eprPciSeq"),//
		ID_EPR_ESP_SEQ("id.eprEspSeq"),//
		IND_RESP_PROC("id.indRespProc"),//
		CIRURGIA("cirurgia"), //
		SITUACAO("situacao"), //
		ESPECIALIDADE("mbcEspecialidadeProcCirgs"),// 
		PAC_ORU_ACC_NUMMER("pacOruAccNumber"),//
		PROCEDIMENTO("procedimentoCirurgico"), //
		PROCEDIMENTO_SEQ("procedimentoCirurgico.seq"), //
		CIRURGIA2("cirurgia"), //
		IND_PRINCIPAL("indPrincipal"),//
		QTD("qtd"),//
		CRG_SEQ("cirurgia.seq"), 
		PROCEDIMENTO_HOSP_INTERNO("procedHospInterno"),//
		PHI_SEQ("procedHospInterno.seq");//
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ")
	public FatProcedHospInternos getProcedHospInterno() {
		return procedHospInterno;
	}

	public void setProcedHospInterno(FatProcedHospInternos procedHospInterno) {
		this.procedHospInterno = procedHospInterno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CID_SEQ")
	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	@Column(name = "IND_PRINCIPAL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPrincipal() {
		return indPrincipal;
	}

	public void setIndPrincipal(Boolean indPrincipal) {
		this.indPrincipal = indPrincipal;
	}
	
}