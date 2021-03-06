package br.gov.mec.aghu.model;

// Generated 10/06/2011 10:57:51 by Hibernate Tools 3.4.0.CR1
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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioIndPendenteLaudoAih;
import br.gov.mec.aghu.dominio.DominioIndSituacaoLaudoAih;
import br.gov.mec.aghu.dominio.DominioOrigemLaudoAih;
import br.gov.mec.aghu.dominio.DominioParecerRevisaoMedicaLaudoAih;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * MamLaudoAihs generated by hbm2java
 */

@Entity
@SequenceGenerator(name="mamLaiSq1", sequenceName="AGH.MAM_LAI_SQ1", allocationSize = 1)
@Table(name = "MAM_LAUDO_AIHS", schema = "AGH")
public class MamLaudoAih extends BaseEntitySeq<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 452897347420955393L;
	private Long seq;
	private Integer version;
	private MamLaudoAih mamLaudoAihs;
	private String sinaisSintomas;
	private String condicoes;
	private String resultadosProvas;
	private Date dthrCriacao;
	private Date dthrValida;
	private Date dthrMvto;
	private Date dthrValidaMvto;
	private DominioIndPendenteLaudoAih indPendente;
	private Boolean indImpresso;
	private Integer conNumero;
	private Integer pacCodigo;
	private AghCid aghCid;
	private AghCid aghCidSecundario;
	private String materialSolicitado;
	private String descricaoProcedimento;
	private Long trgSeq;
	private Long rgtSeq;
	private Date dtProvavelInternacao;
	private Date dtProvavelCirurgia;
	private RapServidores servidor;
	private RapServidores servidorValida;
	private RapServidores servidorMvto;
	private RapServidores servidorValidaMvto;
	private RapServidores servidorRespInternacao;
	private Set<MamLaudoAih> mamLaudoAihses = new HashSet<MamLaudoAih>(0);
	
	private AghEspecialidades especialidade;
	private DominioIndSituacaoLaudoAih indSituacao;
	private String obsRevisaoMedica;
	private AipPacientes paciente;
	private FatItensProcedHospitalar fatItemProcedHospital;
	private Short prioridade;
	private DominioOrigemLaudoAih origem;
	private Integer codigoCentral;
	private DominioParecerRevisaoMedicaLaudoAih parecerRevisaoMedica;
	private Integer internacaoGsh;
	

	public MamLaudoAih() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamLaiSq1")
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
	@JoinColumn(name = "LAI_SEQ")
	public MamLaudoAih getMamLaudoAihs() {
		return this.mamLaudoAihs;
	}

	public void setMamLaudoAihs(MamLaudoAih mamLaudoAihs) {
		this.mamLaudoAihs = mamLaudoAihs;
	}

	@Column(name = "SINAIS_SINTOMAS", nullable = false, length = 1000)
	@Length(max = 1000)
	public String getSinaisSintomas() {
		return this.sinaisSintomas;
	}

	public void setSinaisSintomas(String sinaisSintomas) {
		this.sinaisSintomas = sinaisSintomas;
	}

	@Column(name = "CONDICOES", nullable = false, length = 500)
	@Length(max = 500)
	public String getCondicoes() {
		return this.condicoes;
	}

	public void setCondicoes(String condicoes) {
		this.condicoes = condicoes;
	}

	@Column(name = "RESULTADOS_PROVAS", nullable = false, length = 500)
	@Length(max = 500)
	public String getResultadosProvas() {
		return this.resultadosProvas;
	}

	public void setResultadosProvas(String resultadosProvas) {
		this.resultadosProvas = resultadosProvas;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CRIACAO", nullable = false, length = 29)
	public Date getDthrCriacao() {
		return this.dthrCriacao;
	}

	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_VALIDA", length = 29)
	public Date getDthrValida() {
		return this.dthrValida;
	}

	public void setDthrValida(Date dthrValida) {
		this.dthrValida = dthrValida;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_MVTO", length = 29)
	public Date getDthrMvto() {
		return this.dthrMvto;
	}

	public void setDthrMvto(Date dthrMvto) {
		this.dthrMvto = dthrMvto;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_VALIDA_MVTO", length = 29)
	public Date getDthrValidaMvto() {
		return this.dthrValidaMvto;
	}

	public void setDthrValidaMvto(Date dthrValidaMvto) {
		this.dthrValidaMvto = dthrValidaMvto;
	}

	@Column(name = "IND_PENDENTE", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndPendenteLaudoAih getIndPendente() {
		return this.indPendente;
	}

	public void setIndPendente(DominioIndPendenteLaudoAih indPendente) {
		this.indPendente = indPendente;
	}

	@Column(name = "IND_IMPRESSO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndImpresso() {
		return this.indImpresso;
	}

	public void setIndImpresso(Boolean indImpresso) {
		this.indImpresso = indImpresso;
	}

	@Column(name = "CON_NUMERO")
	public Integer getConNumero() {
		return this.conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	@Column(name = "PAC_CODIGO",  nullable = false, insertable=false, updatable=false)
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "MATERIAL_SOLICITADO", length = 2000)
	@Length(max = 2000)
	public String getMaterialSolicitado() {
		return this.materialSolicitado;
	}

	public void setMaterialSolicitado(String materialSolicitado) {
		this.materialSolicitado = materialSolicitado;
	}

	@Column(name = "DESCRICAO_PROCEDIMENTO", length = 500)
	@Length(max = 500)
	public String getDescricaoProcedimento() {
		return this.descricaoProcedimento;
	}

	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}

	@Column(name = "TRG_SEQ")
	public Long getTrgSeq() {
		return this.trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	@Column(name = "RGT_SEQ")
	public Long getRgtSeq() {
		return this.rgtSeq;
	}

	public void setRgtSeq(Long rgtSeq) {
		this.rgtSeq = rgtSeq;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamLaudoAihs")
	public Set<MamLaudoAih> getMamLaudoAihses() {
		return this.mamLaudoAihses;
	}

	public void setMamLaudoAihses(Set<MamLaudoAih> mamLaudoAihses) {
		this.mamLaudoAihses = mamLaudoAihses;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_PROVAVEL_CIRURGIA", length = 29)
	public Date getDtProvavelCirurgia() {
		return dtProvavelCirurgia;
	}

	public void setDtProvavelCirurgia(Date dtProvavelCirurgia) {
		this.dtProvavelCirurgia = dtProvavelCirurgia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_PROVAVEL_INTERNACAO", length = 29)
	public Date getDtProvavelInternacao() {
		return dtProvavelInternacao;
	}

	public void setDtProvavelInternacao(Date dtProvavelInternacao) {
		this.dtProvavelInternacao = dtProvavelInternacao;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA_VALIDA", referencedColumnName = "MATRICULA", nullable = true),
		@JoinColumn(name = "SER_VIN_CODIGO_VALIDA", referencedColumnName = "VIN_CODIGO", nullable = true) })
	public RapServidores getServidorValida() {
		return this.servidorValida;
	}
	
	public void setServidorValida(RapServidores servidorValida) {
		this.servidorValida = servidorValida;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA_MVTO", referencedColumnName = "MATRICULA", nullable = true),
		@JoinColumn(name = "SER_VIN_CODIGO_MVTO", referencedColumnName = "VIN_CODIGO", nullable = true) })
	public RapServidores getServidorMvto() {
		return this.servidorMvto;
	}
	
	public void setServidorMvto(RapServidores servidorMvto) {
		this.servidorMvto = servidorMvto;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA_VALIDA_MVTO", referencedColumnName = "MATRICULA", nullable = true),
		@JoinColumn(name = "SER_VIN_CODIGO_VALIDA_MVTO", referencedColumnName = "VIN_CODIGO", nullable = true) })
	public RapServidores getServidorValidaMvto() {
		return this.servidorValidaMvto;
	}
	
	public void setServidorValidaMvto(RapServidores servidorValidaMvto) {
		this.servidorValidaMvto = servidorValidaMvto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA_RESP_INTERNACAO", referencedColumnName = "MATRICULA", nullable = true),
		@JoinColumn(name = "SER_VIN_CODIGO_RESP_INTERNACAO", referencedColumnName = "VIN_CODIGO", nullable = true) })
	public RapServidores getServidorRespInternacao() {
		return this.servidorRespInternacao;
	}
	
	public void setServidorRespInternacao(RapServidores servidorRespInternacao) {
		this.servidorRespInternacao = servidorRespInternacao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndSituacaoLaudoAih getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioIndSituacaoLaudoAih indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ESP_SEQ",  referencedColumnName = "SEQ")
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	
	public void setObsRevisaoMedica(String obsRevisaoMedica) {
		this.obsRevisaoMedica = obsRevisaoMedica;
	}

	@Column(name = "OBSERVACAO_REVISAO_MEDICA", length = 2000)
	@Length(max = 2000)
	public String getObsRevisaoMedica() {
		return obsRevisaoMedica;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO")
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
	@JoinColumns( {
        @JoinColumn(name = "IPH_PHO_SEQ", referencedColumnName = "PHO_SEQ", nullable = false),
        @JoinColumn(name = "IPH_SEQ", referencedColumnName = "SEQ", nullable = false) })
        @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public FatItensProcedHospitalar getFatItemProcedHospital() {
        return fatItemProcedHospital;
    }

    public void setFatItemProcedHospital(FatItensProcedHospitalar fatItemProcedHospital) {
        this.fatItemProcedHospital = fatItemProcedHospital;
    }

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CID_SEQ", nullable = false)
    public AghCid getAghCid() {
        return aghCid;
    }

    public void setAghCid(AghCid aghCid) {
        this.aghCid = aghCid;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CID_SEQ_SECUNDARIO", nullable = true)
    public AghCid getAghCidSecundario() {
        return aghCidSecundario;
    }

    public void setAghCidSecundario(AghCid aghCidSecundario) {
        this.aghCidSecundario = aghCidSecundario;
    }

	public Short getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Short prioridade) {
		this.prioridade = prioridade;
	}

	@Column(name = "CODIGO_CENTRAL")
	public Integer getCodigoCentral() {
		return codigoCentral;
	}
	
	public void setCodigoCentral(Integer codigoCentral) {
		this.codigoCentral = codigoCentral;
	}

	@Column(name = "INTERNACAO_GSH")
	public Integer getInternacaoGsh() {
		return internacaoGsh;
	}

	public void setInternacaoGsh(Integer internacaoGsh) {
		this.internacaoGsh = internacaoGsh;
	}

	@Column(name = "ORIGEM", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioOrigemLaudoAih getOrigem() {
		return origem;
	}

	public void setOrigem(DominioOrigemLaudoAih origem) {
		this.origem = origem;
	}
	
	@Column(name = "PARECER_REVISAO_MEDICA", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioParecerRevisaoMedicaLaudoAih getParecerRevisaoMedica() {
		return parecerRevisaoMedica;
	}
	
	public void setParecerRevisaoMedica(DominioParecerRevisaoMedicaLaudoAih parecerRevisaoMedica) {
		this.parecerRevisaoMedica = parecerRevisaoMedica;
	}

	public enum Fields {
		TRG_SEQ("trgSeq"),
		RGT_SEQ("rgtSeq"),
		IND_PENDENTE("indPendente"),
		DTHR_VALIDA_MVTO("dthrValidaMvto"),
		SEQ("seq"),
		SER_MATRICULA_RESP_INTERNACAO("servidorRespInternacao.id.matricula"),
		SER_VIN_CODIGO_RESP_INTERNACAO("servidorRespInternacao.id.vinCodigo"),
		SERVIDOR_RESP_INTERNACAO("servidorRespInternacao"),
		DTHR_CRIACAO ("dthrCriacao"),
		ESPECIALIDADE ("especialidade"),
		DT_PROVAVEL_INTERNACAO ("dtProvavelInternacao"),
		MAM_LAUDO_AIHS ("mamLaudoAihs"),
		IND_SITUACAO ("indSituacao"),
		OBS_REVISAO_MEDICA ("obsRevisaoMedica"),
		PAC_CODIGO ("pacCodigo"),
		CON_NUMERO ("conNumero"),
		DTHR_MVTO ("dthrMvto"),
		PACIENTE ("paciente"),
		SINAIS_SINTOMAS ("sinaisSintomas"),
		CONDICOES ("condicoes"),
		RESULTADOS_PROVAS ("resultadosProvas"),
		DESCRICAO_PROCEDIMENTO ("descricaoProcedimento"),
		AGH_CID("aghCid"),
		AGH_CID_SECUNDARIO ("aghCidSecundario"),
		FAT_ITENS_PROCED_HOSPITALAR ("fatItemProcedHospital"),
		PRIORIDADE ("prioridade");
		
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
		if (!(obj instanceof MamLaudoAih)) {
			return false;
		}
		MamLaudoAih other = (MamLaudoAih) obj;
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
