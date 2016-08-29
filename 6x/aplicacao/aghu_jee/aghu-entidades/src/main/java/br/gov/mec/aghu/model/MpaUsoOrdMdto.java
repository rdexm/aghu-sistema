package br.gov.mec.aghu.model;


import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import br.gov.mec.aghu.dominio.DominioSituacaoOrdProtocolo;
import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the mpa_uso_ord_mdtos database table.
 * 
 */
@Entity
@SequenceGenerator(name="mpaUomSq1", sequenceName="AGH.MPA_UOM_SQ1", allocationSize = 1)
@Table(name="MPA_USO_ORD_MDTOS")
public class MpaUsoOrdMdto extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7044470430668215335L;

	private Integer seq;
	
	//TODO: Mapear para o POJO da tabela MPA_CAD_ORD_MDTOS quando este for criado
	private Integer comSeq;
	
	private Date criadoEm;
	private Integer frequencia;
	private Double gotejo;
	private Date horaInicioAdministracao;
	private Boolean indBombaInfusao;
	private Boolean indSeNecessario;
	private DominioSituacaoOrdProtocolo indSituacao;
	private Boolean indSolucao;

	private AfaMedicamento medicamento;
	
	private String observacao;
	
	private MpmPrescricaoMdto prescricaoMedicamento;
	private MpmPrescricaoMdto prescricaoMedicamentoAnt;
	
	private Integer qtdeHorasCorrer;
	
	private RapServidores servidor;
	
	private MpmTipoFrequenciaAprazamento tipoFreqAprazamento;
	private AfaTipoVelocAdministracoes tipoVelocAdministracao;
	
	/*------------------------------------------------------------------------
	* TODO: Substituir os atributos a seguir pelo pojo da tabela MPA_USO_MEDICACOES 
	* quanto este for criado
	* */
	private Integer umeCamCitSeqp;
	private Integer umeCamCitVpaPtaSeq;
	private Integer umeCamCitVpaSeqp;
	private Integer umeCamSeqp;
	private Integer umeUspApaAtdSeq;
	private Integer umeUspApaSeq;
	private Integer umeUspSeq;
	private Integer umeUspVpaPtaSeq;
	private Integer umeUspVpaSeqp;
	
	private DominioUnidadeHorasMinutos unidHorasCorrer;
	
	private AfaViaAdministracao viaAdministracao;
	
	private Double volumeDiluenteMl;
	private MpaUsoOrdMdto mpaUsoOrdMdto;
	
	//TODO: Criar no banco o campo version
	//private Integer version;

    public MpaUsoOrdMdto() {
    }


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpaUomSq1")
	@Column(name = "SEQ", nullable=false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	@Column(name="COM_SEQ")
	public Integer getComSeq() {
		return this.comSeq;
	}

	public void setComSeq(Integer comSeq) {
		this.comSeq = comSeq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	public Integer getFrequencia() {
		return this.frequencia;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}


	public Double getGotejo() {
		return this.gotejo;
	}

	public void setGotejo(Double gotejo) {
		this.gotejo = gotejo;
	}

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HORA_INICIO_ADMINISTRACAO")
	public Date getHoraInicioAdministracao() {
		return this.horaInicioAdministracao;
	}

	public void setHoraInicioAdministracao(Date horaInicioAdministracao) {
		this.horaInicioAdministracao = horaInicioAdministracao;
	}

	
	@Column(name = "IND_BOMBA_INFUSAO", length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndBombaInfusao() {
		return this.indBombaInfusao;
	}

	public void setIndBombaInfusao(Boolean indBombaInfusao) {
		this.indBombaInfusao = indBombaInfusao;
	}

	@Column(name = "IND_SE_NECESSARIO", nullable=false, length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSeNecessario() {
		return this.indSeNecessario;
	}

	public void setIndSeNecessario(Boolean indSeNecessario) {
		this.indSeNecessario = indSeNecessario;
	}
	
	@Column(name = "IND_SITUACAO", nullable=false, length=2)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoOrdProtocolo getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoOrdProtocolo indSituacao) {
		this.indSituacao = indSituacao;
	}


	@Column(name="IND_SOLUCAO", nullable=false, length=1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSolucao() {
		return this.indSolucao;
	}

	public void setIndSolucao(Boolean indSolucao) {
		this.indSolucao = indSolucao;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MED_MAT_CODIGO")
	public AfaMedicamento getMedicamento() {
		return this.medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}


	@Column(length=240)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PMD_ATD_SEQ_ANT", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "PMD_SEQ_ANT", referencedColumnName = "SEQ") })
	public MpmPrescricaoMdto getPrescricaoMedicamentoAnt() {
		return prescricaoMedicamentoAnt;
	}


	public void setPrescricaoMedicamentoAnt(
			MpmPrescricaoMdto prescricaoMedicamentoAnt) {
		this.prescricaoMedicamentoAnt = prescricaoMedicamentoAnt;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PMD_ATD_SEQ", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "PMD_SEQ", referencedColumnName = "SEQ") })
	public MpmPrescricaoMdto getPrescricaoMedicamento() {
		return prescricaoMedicamento;
	}


	public void setPrescricaoMedicamento(MpmPrescricaoMdto prescricaoMedicamento) {
		this.prescricaoMedicamento = prescricaoMedicamento;
	}


	@Column(name="QTDE_HORAS_CORRER")
	public Integer getQtdeHorasCorrer() {
		return this.qtdeHorasCorrer;
	}

	public void setQtdeHorasCorrer(Integer qtdeHorasCorrer) {
		this.qtdeHorasCorrer = qtdeHorasCorrer;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}


	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ")
	public MpmTipoFrequenciaAprazamento getTipoFreqAprazamento() {
		return tipoFreqAprazamento;
	}


	public void setTipoFreqAprazamento(
			MpmTipoFrequenciaAprazamento tipoFreqAprazamento) {
		this.tipoFreqAprazamento = tipoFreqAprazamento;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TVA_SEQ", referencedColumnName = "SEQ")
	public AfaTipoVelocAdministracoes getTipoVelocAdministracao() {
		return tipoVelocAdministracao;
	}


	public void setTipoVelocAdministracao(
			AfaTipoVelocAdministracoes tipoVelocAdministracao) {
		this.tipoVelocAdministracao = tipoVelocAdministracao;
	}


	@Column(name="UME_CAM_CIT_SEQP", nullable=false)
	public Integer getUmeCamCitSeqp() {
		return this.umeCamCitSeqp;
	}

	public void setUmeCamCitSeqp(Integer umeCamCitSeqp) {
		this.umeCamCitSeqp = umeCamCitSeqp;
	}


	@Column(name="UME_CAM_CIT_VPA_PTA_SEQ", nullable=false)
	public Integer getUmeCamCitVpaPtaSeq() {
		return this.umeCamCitVpaPtaSeq;
	}

	public void setUmeCamCitVpaPtaSeq(Integer umeCamCitVpaPtaSeq) {
		this.umeCamCitVpaPtaSeq = umeCamCitVpaPtaSeq;
	}


	@Column(name="UME_CAM_CIT_VPA_SEQP", nullable=false)
	public Integer getUmeCamCitVpaSeqp() {
		return this.umeCamCitVpaSeqp;
	}

	public void setUmeCamCitVpaSeqp(Integer umeCamCitVpaSeqp) {
		this.umeCamCitVpaSeqp = umeCamCitVpaSeqp;
	}


	@Column(name="UME_CAM_SEQP", nullable=false)
	public Integer getUmeCamSeqp() {
		return this.umeCamSeqp;
	}

	public void setUmeCamSeqp(Integer umeCamSeqp) {
		this.umeCamSeqp = umeCamSeqp;
	}


	@Column(name="UME_USP_APA_ATD_SEQ", nullable=false)
	public Integer getUmeUspApaAtdSeq() {
		return this.umeUspApaAtdSeq;
	}

	public void setUmeUspApaAtdSeq(Integer umeUspApaAtdSeq) {
		this.umeUspApaAtdSeq = umeUspApaAtdSeq;
	}


	@Column(name="UME_USP_APA_SEQ", nullable=false)
	public Integer getUmeUspApaSeq() {
		return this.umeUspApaSeq;
	}

	public void setUmeUspApaSeq(Integer umeUspApaSeq) {
		this.umeUspApaSeq = umeUspApaSeq;
	}


	@Column(name="UME_USP_SEQ", nullable=false)
	public Integer getUmeUspSeq() {
		return this.umeUspSeq;
	}

	public void setUmeUspSeq(Integer umeUspSeq) {
		this.umeUspSeq = umeUspSeq;
	}


	@Column(name="UME_USP_VPA_PTA_SEQ", nullable=false)
	public Integer getUmeUspVpaPtaSeq() {
		return this.umeUspVpaPtaSeq;
	}

	public void setUmeUspVpaPtaSeq(Integer umeUspVpaPtaSeq) {
		this.umeUspVpaPtaSeq = umeUspVpaPtaSeq;
	}


	@Column(name="UME_USP_VPA_SEQP", nullable=false)
	public Integer getUmeUspVpaSeqp() {
		return this.umeUspVpaSeqp;
	}

	public void setUmeUspVpaSeqp(Integer umeUspVpaSeqp) {
		this.umeUspVpaSeqp = umeUspVpaSeqp;
	}


	@Column(name="UNID_HORAS_CORRER", length=1)
	@Enumerated(EnumType.STRING)
	public DominioUnidadeHorasMinutos getUnidHorasCorrer() {
		return this.unidHorasCorrer;
	}

	public void setUnidHorasCorrer(DominioUnidadeHorasMinutos unidHorasCorrer) {
		this.unidHorasCorrer = unidHorasCorrer;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VAD_SIGLA", referencedColumnName = "SIGLA")
	public AfaViaAdministracao getViaAdministracao() {
		return viaAdministracao;
	}


	public void setViaAdministracao(AfaViaAdministracao viaAdministracao) {
		this.viaAdministracao = viaAdministracao;
	}


	@Column(name="VOLUME_DILUENTE_ML")
	public Double getVolumeDiluenteMl() {
		return this.volumeDiluenteMl;
	}

	public void setVolumeDiluenteMl(Double volumeDiluenteMl) {
		this.volumeDiluenteMl = volumeDiluenteMl;
	}


	//bi-directional many-to-one association to MpaUsoOrdMdto
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="UOM_SEQ")
	public MpaUsoOrdMdto getMpaUsoOrdMdto() {
		return this.mpaUsoOrdMdto;
	}

	public void setMpaUsoOrdMdto(MpaUsoOrdMdto mpaUsoOrdMdto) {
		this.mpaUsoOrdMdto = mpaUsoOrdMdto;
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		MpaUsoOrdMdto other = (MpaUsoOrdMdto) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	
	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validarUsoOrdMdto(){
		/*
		 * VALORES PADRÃO
		 */
		if (this.indBombaInfusao == null) {
			this.indBombaInfusao = false;
		}
	}
	
	public enum Fields {
		
		SEQ("seq"), PMD_ATD_SEQ("prescricaoMedicamento.id.atdSeq"), PMD_SEQ("prescricaoMedicamento.id.seq"),
		VIA_ADMINISTRACAO("viaAdministracao");	

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