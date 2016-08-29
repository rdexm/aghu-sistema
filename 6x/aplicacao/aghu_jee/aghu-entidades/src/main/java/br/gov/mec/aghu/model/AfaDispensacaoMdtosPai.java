package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioCoresSinaleiro;
import br.gov.mec.aghu.dominio.DominioCoresSituacaoItemPrescrito;
import br.gov.mec.aghu.dominio.DominioCtrlDispensario;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdtoImagens;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.core.utils.StringUtil;

@MappedSuperclass
public abstract class AfaDispensacaoMdtosPai extends BaseEntitySeq<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1493331497632141506L;
	
	protected Long seq;
	private MpmPrescricaoMedica prescricaoMedica;
	private MpmItemPrescricaoMdto itemPrescricaoMdto;
	private AfaMedicamento medicamento;
	private RapServidores servidor;
	private RapServidores servidorEntregue;
	private RapServidores servidorTicket;
	private Date criadoEm;
	private BigDecimal qtdeDispensada;
	private DominioSituacaoDispensacaoMdto indSituacao;
	private AfaTipoOcorDispensacao tipoOcorrenciaDispensacao;
	private AghUnidadesFuncionais unidadeFuncional;
	private Date dthrEntrega;
	private BigDecimal qtdeSolicitada;
	private Date dthrDispensacao;
	private Date dthrConferencia;
	private Date dthrTicket;
	private RapServidores servidorConferida;
	private AghUnidadesFuncionais unidadeFuncionalSolicitante;
	private DominioSituacaoItemPrescritoDispensacaoMdto indSitItemPrescrito;
	private Integer dsmPmeAtdSeq;
	private Integer dsmPmeSeq;
	private Integer dsmImePmdAtdSeq;
	private Integer dsmImePmdSeq;
	private Integer dsmImeMedMatCodigo;
	private Short dsmImeSeqp;
	private Long dsmSeq;
	private String nomeEstacaoDisp;
	private BigDecimal qtdeEstornada;
	private Date dthrEstorno;
	private RapServidores servidorEstornado;	
	//private Short todSeqEstornado;
	private AfaTipoOcorDispensacao tipoOcorrenciaDispensacaoEstornado;
	private Integer cpoItoPtoSeq;
	private Short cpoItoSeqp;
	private Short cpoSeqp;
	private Integer imoPmoPteAtdSeq;
	private Integer imoPmoPteSeq;
	private Integer imoPmoSeq;
	private Short imoSeqp;
	private AghAtendimentos atendimento;
	private Date dthrTriado;
	private RapServidores servidorTriadoPor;
	private Set<AfaDispMdtoCbSps> afaDispMdtoCbSpss;
	private DominioCtrlDispensario indCtrlDispensario;
	private Boolean indSitEspecial;
	
	//Transient
	private String descricaoMedicamentoPrescrito;
	private DominioCoresSituacaoItemPrescrito corSituacaoItemPrescrito;
	private Boolean indexItemSendoAtualizado;
	private DominioCoresSinaleiro corSinaleiro;
	private String localDeAtendimento;
	private Boolean itemDispensadoSemEtiqueta;
	private String localProntuarioMdto;
	private String mdtoNomeProntuario;
	private String apresNomeMdtoProntuario;
	private String ocorNomeProntuarioMdto;
	private String farmaciaNomeProntuarioMdto;
	private String seqAfaTipoOcorSelecionada;
	private String seqUnidadeFuncionalSelecionada;
	private DominioSituacaoDispensacaoMdto indSituacaoNova;
	private List<DominioSituacaoDispensacaoMdtoImagens> indSituacoes;
	private String matCodigoMdtoSelecionado;
	private Integer version;		
//	private Set<FatProcedHospInternos> procedimentosHospInterno = new HashSet<FatProcedHospInternos>();
	private Boolean indPmNaoEletronica;
	private AfaPrescricaoMedicamento prescricaoMedicamento;
	private String observacao;
	
	public AfaDispensacaoMdtosPai() {
	}

	public AfaDispensacaoMdtosPai(Long seq, AfaMedicamento medicamento, Date criadoEm,
			BigDecimal qtdeDispensada, DominioSituacaoDispensacaoMdto indSituacao, 
			AghUnidadesFuncionais unidadeFuncional, /*int atdSeq*/ AghAtendimentos atendimento) {
		this.seq = seq;
		this.medicamento = medicamento;
		this.criadoEm = criadoEm;
		this.qtdeDispensada = qtdeDispensada;
		this.indSituacao = indSituacao;
		this.unidadeFuncional = unidadeFuncional;
//		this.atdSeq = atdSeq;
		this.atendimento = atendimento;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public AfaDispensacaoMdtosPai(Long seq, MpmPrescricaoMedica prescricaoMedica, Integer pmeSeq,
			MpmItemPrescricaoMdto itemPrescricaoMdto, AfaMedicamento medicamento, 
			RapServidores servidor,
			RapServidores servidorEntregue, Date criadoEm,
			BigDecimal qtdeDispensada, DominioSituacaoDispensacaoMdto indSituacao, 
			AfaTipoOcorDispensacao tipoOcorrenciaDispensacao,
			AghUnidadesFuncionais unidadeFuncional, Date dthrEntrega, BigDecimal qtdeSolicitada,
			Date dthrDispensacao, Date dthrConferencia,
			RapServidores servidorConferida,
			AghUnidadesFuncionais unidadeFuncionalSolicitante, DominioSituacaoItemPrescritoDispensacaoMdto indSitItemPrescrito,
			Integer dsmPmeAtdSeq, Integer dsmPmeSeq, Integer dsmImePmdAtdSeq,
			Integer dsmImePmdSeq, Integer dsmImeMedMatCodigo, Short dsmImeSeqp,
			Long dsmSeq, String nomeEstacaoDisp, BigDecimal qtdeEstornada, Date dthrEstorno, 
			RapServidores servidorEstornado, Short todSeqEstornado,
			Integer cpoItoPtoSeq, Short cpoItoSeqp, Short cpoSeqp,
			Integer imoPmoPteAtdSeq, Integer imoPmoPteSeq, Integer imoPmoSeq,
			Short imoSeqp, AghAtendimentos atendimento, Date dthrTriado,
			RapServidores servidorTriadoPor, Set<AfaDispMdtoCbSps> afaDispMdtoCbSpss, 
			DominioCtrlDispensario indCtrlDispensario, Boolean indSitEspecial) {
		this.seq = seq;
		this.prescricaoMedica = prescricaoMedica;
		this.itemPrescricaoMdto = itemPrescricaoMdto;
		this.medicamento = medicamento;
		this.servidor = servidor;
		this.servidorEntregue = servidorEntregue;
		this.criadoEm = criadoEm;
		this.qtdeDispensada = qtdeDispensada;
		this.indSituacao = indSituacao;
		this.tipoOcorrenciaDispensacao = tipoOcorrenciaDispensacao;
		this.unidadeFuncional = unidadeFuncional;
		this.dthrEntrega = dthrEntrega;
		this.qtdeSolicitada = qtdeSolicitada;
		this.dthrDispensacao = dthrDispensacao;
		this.dthrConferencia = dthrConferencia;
		this.servidorConferida = servidorConferida;
		this.unidadeFuncionalSolicitante = unidadeFuncionalSolicitante;
		this.indSitItemPrescrito = indSitItemPrescrito;
		this.dsmPmeAtdSeq = dsmPmeAtdSeq;
		this.dsmPmeSeq = dsmPmeSeq;
		this.dsmImePmdAtdSeq = dsmImePmdAtdSeq;
		this.dsmImePmdSeq = dsmImePmdSeq;
		this.dsmImeMedMatCodigo = dsmImeMedMatCodigo;
		this.dsmImeSeqp = dsmImeSeqp;
		this.dsmSeq = dsmSeq;
		this.nomeEstacaoDisp = nomeEstacaoDisp;
		this.qtdeEstornada = qtdeEstornada;
		this.dthrEstorno = dthrEstorno;
		this.servidorEstornado = servidorEstornado;
		this.cpoItoPtoSeq = cpoItoPtoSeq;
		this.cpoItoSeqp = cpoItoSeqp;
		this.cpoSeqp = cpoSeqp;
		this.imoPmoPteAtdSeq = imoPmoPteAtdSeq;
		this.imoPmoPteSeq = imoPmoPteSeq;
		this.imoPmoSeq = imoPmoSeq;
		this.imoSeqp = imoSeqp;
//		this.atdSeq = atdSeq;
		this.atendimento = atendimento;
		this.dthrTriado = dthrTriado;
		this.servidorTriadoPor = servidorTriadoPor;
		this.afaDispMdtoCbSpss = afaDispMdtoCbSpss;
		this.indCtrlDispensario = indCtrlDispensario;
		this.indSitEspecial = indSitEspecial;
	}
	
	@SuppressWarnings("unused")
	@PrePersist
	@PreUpdate
	private void validarAfaDispensacaoMdtos() {

		if ((this.qtdeDispensada.compareTo(BigDecimal.ZERO) < 0)) {
			throw new BaseRuntimeException(
					AfaDispensacaoMdtosExceptionCode.AFA_DSM_CK2);
		}
		
		if ((this.qtdeEstornada == null && this.tipoOcorrenciaDispensacaoEstornado !=null)
				||
				(this.qtdeEstornada != null && this.tipoOcorrenciaDispensacaoEstornado == null)) {
			
			throw new BaseRuntimeException(
					AfaDispensacaoMdtosExceptionCode.AFA_DSM_CK5);
		}
		
		if (this.qtdeEstornada != null && (this.qtdeEstornada.compareTo(BigDecimal.ZERO) <= 0)) {
			throw new BaseRuntimeException(
					AfaDispensacaoMdtosExceptionCode.AFA_DSM_CK6);
		}
		
		if (this.qtdeEstornada != null && (this.qtdeEstornada.compareTo(this.qtdeDispensada) > 0)) {
			throw new BaseRuntimeException(
					AfaDispensacaoMdtosExceptionCode.AFA_DSM_CK7);
		}
				
		if(indPmNaoEletronica == null){
			indPmNaoEletronica = Boolean.FALSE;
	    }
	}
	
	private enum AfaDispensacaoMdtosExceptionCode implements BusinessExceptionCode {
		AFA_DSM_CK2, AFA_DSM_CK5, AFA_DSM_CK6, AFA_DSM_CK7
	}



	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PME_ATD_SEQ", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "PME_SEQ", referencedColumnName = "SEQ") })
	public MpmPrescricaoMedica getPrescricaoMedica() {
		return this.prescricaoMedica; 
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}	


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "IME_PMD_ATD_SEQ", referencedColumnName = "PMD_ATD_SEQ"),
			@JoinColumn(name = "IME_PMD_SEQ", referencedColumnName = "PMD_SEQ"),
			@JoinColumn(name = "IME_MED_MAT_CODIGO", referencedColumnName = "MED_MAT_CODIGO"),
			@JoinColumn(name = "IME_SEQP", referencedColumnName = "SEQP") })
	public MpmItemPrescricaoMdto getItemPrescricaoMdto() {
		return this.itemPrescricaoMdto;
	}

	public void setItemPrescricaoMdto(
			MpmItemPrescricaoMdto itemPrescricaoMdto) {
		this.itemPrescricaoMdto = itemPrescricaoMdto;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MED_MAT_CODIGO", referencedColumnName="MAT_CODIGO", nullable = false)
	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA_ENTREGUE", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO_ENTREGUE", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidorEntregue() {
		return servidorEntregue;
	}
	
	public void setServidorEntregue(RapServidores servidorEntregue) {
		this.servidorEntregue = servidorEntregue;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA_TICKET", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO_TICKET", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidorTicket() {
		return servidorTicket;
	}

	public void setServidorTicket(RapServidores servidorTicket) {
		this.servidorTicket = servidorTicket;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "QTDE_DISPENSADA", nullable = false, precision = 8, scale = 4)
	public BigDecimal getQtdeDispensada() {
		return this.qtdeDispensada;
	}

	public void setQtdeDispensada(BigDecimal qtdeDispensada) {
		this.qtdeDispensada = qtdeDispensada;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoDispensacaoMdto getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoDispensacaoMdto indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOD_SEQ", referencedColumnName="SEQ")
	public AfaTipoOcorDispensacao getTipoOcorrenciaDispensacao() {
		return this.tipoOcorrenciaDispensacao;
	}

	public void setTipoOcorrenciaDispensacao(AfaTipoOcorDispensacao tipoOcorrenciaDispensacao) {
		this.tipoOcorrenciaDispensacao = tipoOcorrenciaDispensacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", referencedColumnName = "SEQ", nullable = false)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}
	
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ENTREGA", length = 7)
	public Date getDthrEntrega() {
		return this.dthrEntrega;
	}

	public void setDthrEntrega(Date dthrEntrega) {
		this.dthrEntrega = dthrEntrega;
	}

	@Column(name = "QTDE_SOLICITADA", precision = 8, scale = 4)
	public BigDecimal getQtdeSolicitada() {
		return this.qtdeSolicitada;
	}

	public void setQtdeSolicitada(BigDecimal qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_DISPENSACAO", length = 7)
	public Date getDthrDispensacao() {
		return this.dthrDispensacao;
	}

	public void setDthrDispensacao(Date dthrDispensacao) {
		this.dthrDispensacao = dthrDispensacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CONFERENCIA", length = 7)
	public Date getDthrConferencia() {
		return this.dthrConferencia;
	}

	public void setDthrConferencia(Date dthrConferencia) {
		this.dthrConferencia = dthrConferencia;
	}
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_TICKET", length = 7)
	public Date getDthrTicket() {
		return this.dthrTicket;
	}

	public void setDthrTicket(Date dthrTicket) {
		this.dthrTicket = dthrTicket;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA_CONFERIDA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO_CONFERIDA", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidorConferida() {
		return servidorConferida;
	}
	
	public void setServidorConferida(RapServidores servidorConferida) {
		this.servidorConferida = servidorConferida;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ_SOLICITANTE", referencedColumnName = "SEQ", nullable = true)
	public AghUnidadesFuncionais getUnidadeFuncionalSolicitante() {
		return unidadeFuncionalSolicitante;
	}
	
	public void setUnidadeFuncionalSolicitante(
			AghUnidadesFuncionais unidadeFuncionalSolicitante) {
		this.unidadeFuncionalSolicitante = unidadeFuncionalSolicitante;
	}
	
	/**
	 * ORADB MPMP_POPULA_SITUACAO
	 */
	@Column(name = "IND_SIT_ITEM_PRESCRITO", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoItemPrescritoDispensacaoMdto getIndSitItemPrescrito() {
		return this.indSitItemPrescrito;
	}


	public void setIndSitItemPrescrito(DominioSituacaoItemPrescritoDispensacaoMdto indSitItemPrescrito) {
		this.indSitItemPrescrito = indSitItemPrescrito;
	}

	@Column(name = "DSM_PME_ATD_SEQ", precision = 7, scale = 0)
	public Integer getDsmPmeAtdSeq() {
		return this.dsmPmeAtdSeq;
	}

	public void setDsmPmeAtdSeq(Integer dsmPmeAtdSeq) {
		this.dsmPmeAtdSeq = dsmPmeAtdSeq;
	}

	@Column(name = "DSM_PME_SEQ", precision = 8, scale = 0)
	public Integer getDsmPmeSeq() {
		return this.dsmPmeSeq;
	}

	public void setDsmPmeSeq(Integer dsmPmeSeq) {
		this.dsmPmeSeq = dsmPmeSeq;
	}

	@Column(name = "DSM_IME_PMD_ATD_SEQ", precision = 7, scale = 0)
	public Integer getDsmImePmdAtdSeq() {
		return this.dsmImePmdAtdSeq;
	}

	public void setDsmImePmdAtdSeq(Integer dsmImePmdAtdSeq) {
		this.dsmImePmdAtdSeq = dsmImePmdAtdSeq;
	}

	@Column(name = "DSM_IME_PMD_SEQ", precision = 8, scale = 0)
	public Integer getDsmImePmdSeq() {
		return this.dsmImePmdSeq;
	}

	public void setDsmImePmdSeq(Integer dsmImePmdSeq) {
		this.dsmImePmdSeq = dsmImePmdSeq;
	}

	@Column(name = "DSM_IME_MED_MAT_CODIGO", precision = 6, scale = 0)
	public Integer getDsmImeMedMatCodigo() {
		return this.dsmImeMedMatCodigo;
	}

	public void setDsmImeMedMatCodigo(Integer dsmImeMedMatCodigo) {
		this.dsmImeMedMatCodigo = dsmImeMedMatCodigo;
	}

	@Column(name = "DSM_IME_SEQP", precision = 4, scale = 0)
	public Short getDsmImeSeqp() {
		return this.dsmImeSeqp;
	}

	public void setDsmImeSeqp(Short dsmImeSeqp) {
		this.dsmImeSeqp = dsmImeSeqp;
	}

	@Column(name = "DSM_SEQ", precision = 14, scale = 0)
	public Long getDsmSeq() {
		return this.dsmSeq;
	}

	public void setDsmSeq(Long dsmSeq) {
		this.dsmSeq = dsmSeq;
	}

	@Column(name = "NOME_ESTACAO_DISP", length = 64)
	@Length(max = 64)
	public String getNomeEstacaoDisp() {
		return this.nomeEstacaoDisp;
	}

	public void setNomeEstacaoDisp(String nomeEstacaoDisp) {
		this.nomeEstacaoDisp = nomeEstacaoDisp;
	}

	@Column(name = "QTDE_ESTORNADA", precision = 8, scale = 4)
	public BigDecimal getQtdeEstornada() {
		return this.qtdeEstornada;
	}

	public void setQtdeEstornada(BigDecimal qtdeEstornada) {
		this.qtdeEstornada = qtdeEstornada;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ESTORNO", length = 7)
	public Date getDthrEstorno() {
		return this.dthrEstorno;
	}

	public void setDthrEstorno(Date dthrEstorno) {
		this.dthrEstorno = dthrEstorno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA_ESTORNADO", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO_ESTORNADO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidorEstornado() {
		return servidorEstornado;
	}
	
	public void setServidorEstornado(RapServidores servidorEstornado) {
		this.servidorEstornado = servidorEstornado;
	}

	@Column(name = "CPO_ITO_PTO_SEQ", precision = 8, scale = 0)
	public Integer getCpoItoPtoSeq() {
		return this.cpoItoPtoSeq;
	}

	public void setCpoItoPtoSeq(Integer cpoItoPtoSeq) {
		this.cpoItoPtoSeq = cpoItoPtoSeq;
	}

	@Column(name = "CPO_ITO_SEQP", precision = 4, scale = 0)
	public Short getCpoItoSeqp() {
		return this.cpoItoSeqp;
	}

	public void setCpoItoSeqp(Short cpoItoSeqp) {
		this.cpoItoSeqp = cpoItoSeqp;
	}

	@Column(name = "CPO_SEQP", precision = 3, scale = 0)
	public Short getCpoSeqp() {
		return this.cpoSeqp;
	}

	public void setCpoSeqp(Short cpoSeqp) {
		this.cpoSeqp = cpoSeqp;
	}

	@Column(name = "IMO_PMO_PTE_ATD_SEQ", precision = 7, scale = 0)
	public Integer getImoPmoPteAtdSeq() {
		return this.imoPmoPteAtdSeq;
	}

	public void setImoPmoPteAtdSeq(Integer imoPmoPteAtdSeq) {
		this.imoPmoPteAtdSeq = imoPmoPteAtdSeq;
	}

	@Column(name = "IMO_PMO_PTE_SEQ", precision = 8, scale = 0)
	public Integer getImoPmoPteSeq() {
		return this.imoPmoPteSeq;
	}

	public void setImoPmoPteSeq(Integer imoPmoPteSeq) {
		this.imoPmoPteSeq = imoPmoPteSeq;
	}

	@Column(name = "IMO_PMO_SEQ", precision = 8, scale = 0)
	public Integer getImoPmoSeq() {
		return this.imoPmoSeq;
	}

	public void setImoPmoSeq(Integer imoPmoSeq) {
		this.imoPmoSeq = imoPmoSeq;
	}

	@Column(name = "IMO_SEQP", precision = 3, scale = 0)
	public Short getImoSeqp() {
		return this.imoSeqp;
	}

	public void setImoSeqp(Short imoSeqp) {
		this.imoSeqp = imoSeqp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ", referencedColumnName="SEQ")
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_TRIADO", length = 7)
	public Date getDthrTriado() {
		return this.dthrTriado;
	}

	public void setDthrTriado(Date dthrTriado) {
		this.dthrTriado = dthrTriado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA_TRIADO_POR", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO_TRIADO_POR", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidorTriadoPor() {
		return servidorTriadoPor;
	}
	
	public void setServidorTriadoPor(RapServidores servidorTriadoPor) {
		this.servidorTriadoPor = servidorTriadoPor;
	}
	
//	@OneToMany(fetch = FetchType.LAZY, mappedBy="dispensacaoMdto")
//	public Set<FatProcedHospInternos> getProcedimentosHospInterno() {
//		return procedimentosHospInterno;
//	}

//	public void setProcedimentosHospInterno(
//			Set<FatProcedHospInternos> procedimentosHospInterno) {
//		this.procedimentosHospInterno = procedimentosHospInterno;
//	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy="dispensacaoMdto")
	public Set<AfaDispMdtoCbSps> getAfaDispMdtoCbSpss() {
		return afaDispMdtoCbSpss;
	}

	public void setAfaDispMdtoCbSpss(
			Set<AfaDispMdtoCbSps> afaDispMdtoCbSpss) {
		this.afaDispMdtoCbSpss = afaDispMdtoCbSpss;
	}

	@Column(name = "IND_CTRL_DISPENSARIO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioCtrlDispensario getIndCtrlDispensario() {
		return indCtrlDispensario;
	}

	public void setIndCtrlDispensario(DominioCtrlDispensario indCtrlDispensario) {
		this.indCtrlDispensario = indCtrlDispensario;
	}

	@Column(name = "IND_SIT_ESPECIAL", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSitEspecial() {
		return indSitEspecial;
	}

	public void setIndSitEspecial(Boolean indSitEspecial) {
		this.indSitEspecial = indSitEspecial;
	}

	
	public enum Fields {		
		SEQ ("seq"),
		ITEM_PRESCRICAO_MEDICAMENTO("itemPrescricaoMdto"),
		QTDE_SOLICITADA("qtdeSolicitada"),
		MEDICAMENTO("medicamento"),
		MED_MAT_CODIGO("medicamento.matCodigo"),
		QTDE_DISPENSADA("qtdeDispensada"),
		CRIADO_EM("criadoEm"),
		PRESCRICAO_MEDICA("prescricaoMedica"),
		IND_SITUACAO("indSituacao"),
		IND_SIT_ITEM_PRESCRITO("indSitItemPrescrito"),
		TIPO_OCORRENCIA_DISPENSACAO("tipoOcorrenciaDispensacao"),
		TIPO_OCORRENCIA_DISPENSACAO_SEQ("tipoOcorrenciaDispensacao.seq"),
		SERVIDOR("servidor"),
		SERVIDOR_ENTREGUE("servidorEntregue"),
		SERVIDOR_CONFERIDA("servidorConferida"),
		SERVIDOR_ESTORNADO("servidorEstornado"),
		SERVIDOR_TRIADO_POR("servidorTriadoPor"),
		FARMACIA("unidadeFuncional"),
		UNID_SOLICITANTE("unidadeFuncionalSolicitante"),	
		UNID_SOLICITANTE_SEQ("unidadeFuncionalSolicitante.seq"),
		DSM_PME_ATQ_SEQ("dsmPmeAtdSeq"),
		FARMACIA_SEQ("unidadeFuncional.seq"),
		PRESCRICAO_MEDICA_SEQ("prescricaoMedica.id.seq"),
		PRESCRICAO_MEDICA_ATD_SEQ("prescricaoMedica.id.atdSeq"),
		IMO_PMO_PTE_ATD_SEQ("imoPmoPteAtdSeq"),
		IMO_PMO_PTE_SEQ("imoPmoPteSeq"),
		ATENDIMENTO ("atendimento"),
		DTHR_DISPENSACAO("dthrDispensacao"),
		DTHR_TRIADO("dthrTriado"),
		QTDE_ESTORNADA("qtdeEstornada"),		
		DATA_HORA_ENTREGA("dthrEntrega"),
		DTHR_ESTORNO ("dthrEstorno"),
		PRESCRICAO_MEDICA_DTHRINICIO("prescricaoMedica.dthrInicio"),
		ITEM_PRESCRICAO_MEDICAMENTO_PMD_ATD_SEQ("itemPrescricaoMdto.id.pmdAtdSeq"),
		ITEM_PRESCRICAO_MEDICAMENTO_PMD_SEQ("itemPrescricaoMdto.id.pmdSeq"),
		ITEM_PRESCRICAO_MEDICAMENTO_MED_MAT_CODIGO("itemPrescricaoMdto.id.medMatCodigo"),
		ITEM_PRESCRICAO_MEDICAMENTO_SEQP("itemPrescricaoMdto.id.seqp"),
		DSM_SEQ("dsmSeq"),
		ATENDIMENTO_SEQ ("atendimento.seq"),
		PRESCRICAO_MEDICA_DT_REFERENCIA("prescricaoMedica.dtReferencia"),
		TIPO_OCORRENCIA_DISPENSACAO_ESTORNADO("tipoOcorrenciaDispensacaoEstornado"),
		NOME_ESTACAO_DISP("nomeEstacaoDisp"),
		AFA_DISP_MDTO_CB_SPSS("afaDispMdtoCbSpss"),
		IND_CTRL_DISPENSARIO("indCtrlDispensario"),
		IND_SIT_ESPECIAL("indSitEspecial"),
		IMO_PMO_SEQ("imoPmoSeq"),
		IMO_SEQP("imoSeqp"),
		CPO_ITO_PTO_SEQ("cpoItoPtoSeq"),
		CPO_ITO_SEQP("cpoItoSeqp"),
		CPO_SEQP("cpoSeqp");

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
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		
		if (obj == null){
			return false;
		}
		
		if (getClass() != obj.getClass()){
			return false;
		}
		
		AfaDispensacaoMdtosPai other = (AfaDispensacaoMdtosPai) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
			
		} else if (!seq.equals(other.seq)){
			return false;
		}
		
		return true;
	}

	@Transient
	public String getDescricaoMedicamentoPrescrito() {
		return descricaoMedicamentoPrescrito;
	}
	
	public void setDescricaoMedicamentoPrescrito(
			String descricaoMedicamentoPrescrito) {
		this.descricaoMedicamentoPrescrito = descricaoMedicamentoPrescrito;
	}
	
	@Transient
	public String getDescricaoMedicamentoPrescritoTrunc(Long size) {
		return StringUtil.trunc(descricaoMedicamentoPrescrito, true, size);
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOD_SEQ_ESTORNADO", referencedColumnName="SEQ")
	public AfaTipoOcorDispensacao getTipoOcorrenciaDispensacaoEstornado() {
		return tipoOcorrenciaDispensacaoEstornado;
	}
	public void setTipoOcorrenciaDispensacaoEstornado(
			AfaTipoOcorDispensacao tipoOcorrenciaDispensacaoEstornado) {
		this.tipoOcorrenciaDispensacaoEstornado = tipoOcorrenciaDispensacaoEstornado;
	}
	
	@Transient
	public DominioCoresSituacaoItemPrescrito getCorSituacaoItemPrescrito() {
		return corSituacaoItemPrescrito;
	}

	public void setCorSituacaoItemPrescrito(
			DominioCoresSituacaoItemPrescrito corSituacaoItemPrescrito) {
		this.corSituacaoItemPrescrito = corSituacaoItemPrescrito;
	}
	
	@Transient
	public DominioCoresSinaleiro getCorSinaleiro() {
		return corSinaleiro;
	}

	public void setCorSinaleiro(DominioCoresSinaleiro corSinaleiro) {
		this.corSinaleiro = corSinaleiro;
	}
	
	@Transient
	public String getLocalDeAtendimento() {
		return localDeAtendimento;
	}

	public void setLocalDeAtendimento(String localDeAtendimento) {
		this.localDeAtendimento = localDeAtendimento;
	}
	
	@Transient
	public Boolean getItemDispensadoSemEtiqueta() {
		return itemDispensadoSemEtiqueta;
	}

	public void setItemDispensadoSemEtiqueta(Boolean itemDispensadoSemEtiqueta) {
		this.itemDispensadoSemEtiqueta = itemDispensadoSemEtiqueta;
	}
	
	
	@Transient
	public String getMensagem() {
		return (itemDispensadoSemEtiqueta ? "Dispensado" : "Dispensar sem uso de etiquetas");
	}
	
	
	@Transient
	public Boolean getIndexItemSendoAtualizado() {
		return indexItemSendoAtualizado;
	}

	public void setIndexItemSendoAtualizado(Boolean indexItemSendoAtualizado) {
		this.indexItemSendoAtualizado = indexItemSendoAtualizado;
	}

	@Transient
	public String getSeqAfaTipoOcorSelecionada() {
		return seqAfaTipoOcorSelecionada;
	}

	public void setSeqAfaTipoOcorSelecionada(String seqAfaTipoOcorSelecionada) {
		this.seqAfaTipoOcorSelecionada = seqAfaTipoOcorSelecionada;
	}
	
	@Transient
	public String getSeqUnidadeFuncionalSelecionada() {
		return seqUnidadeFuncionalSelecionada;
	}

	public void setSeqUnidadeFuncionalSelecionada(
			String seqUnidadeFuncionalSelecionada) {
		this.seqUnidadeFuncionalSelecionada = seqUnidadeFuncionalSelecionada;
	}
	
	@Transient
	public DominioSituacaoDispensacaoMdto getIndSituacaoNova() {
		return indSituacaoNova;
	}

	public void setIndSituacaoNova(DominioSituacaoDispensacaoMdto indSituacaoNova) {
		this.indSituacaoNova = indSituacaoNova;
	}

	@Transient
	public List<DominioSituacaoDispensacaoMdtoImagens> getIndSituacoes() {
		return indSituacoes;
	}

	public void setIndSituacoes(
			List<DominioSituacaoDispensacaoMdtoImagens> indSituacoes) {
		this.indSituacoes = indSituacoes;
	}
	
	@Transient
	public String getLocalProntuarioMdto() {
		return localProntuarioMdto;
	}

	public void setLocalProntuarioMdto(String localProntuarioMdto) {
		this.localProntuarioMdto = localProntuarioMdto;
	}
	
	@Transient
	public String getMdtoNomeProntuario() {
		return mdtoNomeProntuario;
	}

	public void setMdtoNomeProntuario(String mdtoNomeProntuario) {
		this.mdtoNomeProntuario = mdtoNomeProntuario;
	}
	
	@Transient
	public String getApresNomeMdtoProntuario() {
		return apresNomeMdtoProntuario;
	}

	public void setApresNomeMdtoProntuario(String apresNomeMdtoProntuario) {
		this.apresNomeMdtoProntuario = apresNomeMdtoProntuario;
	}

	@Transient
	public String getOcorNomeProntuarioMdto() {
		return ocorNomeProntuarioMdto;
	}

	public void setOcorNomeProntuarioMdto(String ocorNomeProntuarioMdto) {
		this.ocorNomeProntuarioMdto = ocorNomeProntuarioMdto;
	}

	@Transient
	public String getFarmaciaNomeProntuarioMdto() {
		return farmaciaNomeProntuarioMdto;
	}

	public void setFarmaciaNomeProntuarioMdto(String farmaciaNomeProntuarioMdto) {
		this.farmaciaNomeProntuarioMdto = farmaciaNomeProntuarioMdto;
	}

	@Transient
	public String getMatCodigoMdtoSelecionado() {
		return matCodigoMdtoSelecionado;
	}

	public void setMatCodigoMdtoSelecionado(String matCodigoMdtoSelecionado) {
		this.matCodigoMdtoSelecionado = matCodigoMdtoSelecionado;
	}
	
	@Column(name = "SEQ", insertable = false, updatable = false)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "IND_PM_NAO_ELETRONICA", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPmNaoEletronica() {
		return indPmNaoEletronica;
	}

	public void setIndPmNaoEletronica(Boolean indPmNaoEletronica) {
		this.indPmNaoEletronica = indPmNaoEletronica;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PMM_SEQ", referencedColumnName="SEQ")
	public AfaPrescricaoMedicamento getPrescricaoMedicamento() {
		return prescricaoMedicamento;
	}

	public void setPrescricaoMedicamento(
			AfaPrescricaoMedicamento prescricaoMedicamento) {
		this.prescricaoMedicamento = prescricaoMedicamento;
	}

	@Column(name = "OBSERVACAO", length = 250)
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}