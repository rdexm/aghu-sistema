package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;

/**
 * Retorno do cursor <code>BUSCA_CONTA</code> de <code>FATF_ARQ_TXT_INT</code> implementado em GeracaoArquivoTextoInternacaoRN
 * @author gandriotti
 *
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class BuscaContaVO {

	private Integer cthSeq = null; // diferente	
	private String nome = null;
	private Integer prontuario = null;
	private DominioSituacaoConta indSituacao = null;
	private String descricao = null;
	private String leitoID = null;
	private Long nroAih = null;
	private Byte mdsSeq = null;
	private Date dataInternacaoAdministrativa = null;
	private Date dtAltaAdministrativa = null;
	private Date dtEnvioSms = null;
	private String indAutorizadoSms = null;
	private Integer phiSeq = null; // diferente
	private Short cspCnvCodigo = null; //diferente
	private Byte cspSeq = null; // diferente
	private Integer phiRealizSeq = null; // diferente
	private String indEnviadoSms = null;
	private String codigo = null;
	private BigDecimal valorSh = null;
	private BigDecimal valorUti = null;
	private BigDecimal valorUtie = null;
	private BigDecimal valorSp = null;
	private BigDecimal valorAcomp = null;
	private BigDecimal valorRn = null;
	private BigDecimal valorSadt = null;
	private BigDecimal valorHemat = null;
	private BigDecimal valorTransp = null;
	private BigDecimal valorOpm = null;
	private BigDecimal valorAnestesista = null;
	private BigDecimal valorProcedimento = null;
	private Integer tciSeq = null;
	//derivadas
	private BigDecimal valorTotal;
	private Boolean isCobraAih;
	private Boolean isEspCir;
	private Boolean isForaEstado;
	private Boolean isDesdobrada;
	private String fcfDesc;
	private String statusSms;
	private String iphSsmSol;
	private String iphSsmReal;
	private String caracterInternacao;
	private String msgErro;
	private Integer cmce = null;
	private String realizadoSolicitado;

	public BuscaContaVO() {

		super();
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	public BuscaContaVO(final Integer cthSeq, final String nome, final Integer prontuario, final DominioSituacaoConta indSituacao, final String descricao,
			final String leitoID, final Integer tciSeq, final Long nroAih,
			final Byte mdsSeq, final Date dataInternacaoAdministrativa, final Date dtAltaAdministrativa, final Date dtEnvioSms, final String indAutorizadoSms,
			final Integer phiSeq,
			final Short cspCnvCodigo, final Byte cspSeq, final Integer phiRealizSeq, final String indEnviadoSms, final String codigo, final BigDecimal valorSh,
			final BigDecimal valorUti,
			final BigDecimal valorUtie, final BigDecimal valorSp, final BigDecimal valorAcomp, final BigDecimal valorRn, final BigDecimal valorSadt,
			final BigDecimal valorHemat,
			final BigDecimal valorTransp, final BigDecimal valorOpm, final BigDecimal valorAnestesista, final BigDecimal valorProcedimento) {

		super();
		this.cthSeq = cthSeq;
		this.nome = nome;
		this.prontuario = prontuario;
		this.indSituacao = indSituacao;
		this.descricao = descricao;
		this.leitoID = leitoID;
		this.tciSeq = tciSeq;
		this.nroAih = nroAih;
		this.mdsSeq = mdsSeq;
		this.dataInternacaoAdministrativa = dataInternacaoAdministrativa;
		this.dtAltaAdministrativa = dtAltaAdministrativa;
		this.dtEnvioSms = dtEnvioSms;
		this.indAutorizadoSms = indAutorizadoSms;
		this.phiSeq = phiSeq;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
		this.phiRealizSeq = phiRealizSeq;
		this.indEnviadoSms = indEnviadoSms;
		this.codigo = codigo;
		this.valorSh = valorSh;
		this.valorUti = valorUti;
		this.valorUtie = valorUtie;
		this.valorSp = valorSp;
		this.valorAcomp = valorAcomp;
		this.valorRn = valorRn;
		this.valorSadt = valorSadt;
		this.valorHemat = valorHemat;
		this.valorTransp = valorTransp;
		this.valorOpm = valorOpm;
		this.valorAnestesista = valorAnestesista;
		this.valorProcedimento = valorProcedimento;
	}

	public Integer getCthSeq() {

		return this.cthSeq;
	}

	public void setCthSeq(final Integer cthSeq) {

		this.cthSeq = cthSeq;
	}

	public String getNome() {

		return this.nome;
	}

	public void setNome(final String nome) {

		this.nome = nome;
	}

	public Integer getProntuario() {

		return this.prontuario;
	}

	public void setProntuario(final Integer prontuario) {

		this.prontuario = prontuario;
	}

	public DominioSituacaoConta getIndSituacao() {

		return this.indSituacao;
	}

	public void setIndSituacao(final DominioSituacaoConta indSituacao) {

		this.indSituacao = indSituacao;
	}

	public String getDescricao() {

		return this.descricao;
	}

	public void setDescricao(final String descricao) {

		this.descricao = descricao;
	}

	public String getLeitoID() {

		return this.leitoID;
	}

	public void setLeitoID(final String leitoID) {

		this.leitoID = leitoID;
	}

	public Long getNroAih() {

		return this.nroAih;
	}

	public void setNroAih(final Long nroAih) {

		this.nroAih = nroAih;
	}

	public Byte getMdsSeq() {

		return this.mdsSeq;
	}

	public void setMdsSeq(final Byte mdsSeq) {

		this.mdsSeq = mdsSeq;
	}

	public Date getDataInternacaoAdministrativa() {

		return this.dataInternacaoAdministrativa;
	}

	public void setDataInternacaoAdministrativa(final Date dataInternacaoAdministrativa) {

		this.dataInternacaoAdministrativa = dataInternacaoAdministrativa;
	}

	public Date getDtAltaAdministrativa() {

		return this.dtAltaAdministrativa;
	}

	public void setDtAltaAdministrativa(final Date dtAltaAdministrativa) {

		this.dtAltaAdministrativa = dtAltaAdministrativa;
	}

	public Date getDtEnvioSms() {

		return this.dtEnvioSms;
	}

	public void setDtEnvioSms(final Date dtEnvioSms) {

		this.dtEnvioSms = dtEnvioSms;
	}

	public String getIndAutorizadoSms() {

		return this.indAutorizadoSms;
	}

	public void setIndAutorizadoSms(final String indAutorizadoSms) {

		this.indAutorizadoSms = indAutorizadoSms;
	}

	public Integer getPhiSeq() {

		return this.phiSeq;
	}

	public void setPhiSeq(final Integer phiSeq) {

		this.phiSeq = phiSeq;
	}

	public Short getCspCnvCodigo() {

		return this.cspCnvCodigo;
	}

	public void setCspCnvCodigo(final Short cspCnvCodigo) {

		this.cspCnvCodigo = cspCnvCodigo;
	}

	public Byte getCspSeq() {

		return this.cspSeq;
	}

	public void setCspSeq(final Byte cspSeq) {

		this.cspSeq = cspSeq;
	}

	public Integer getPhiRealizSeq() {

		return this.phiRealizSeq;
	}

	public void setPhiRealizSeq(final Integer phiRealizSeq) {

		this.phiRealizSeq = phiRealizSeq;
	}

	public String getIndEnviadoSms() {

		return this.indEnviadoSms;
	}

	public void setIndEnviadoSms(final String indEnviadoSms) {

		this.indEnviadoSms = indEnviadoSms;
	}

	public String getCodigo() {

		return this.codigo;
	}

	public void setCodigo(final String codigo) {

		this.codigo = codigo;
	}

	public BigDecimal getValorSh() {

		return this.valorSh;
	}

	public void setValorSh(final BigDecimal valorSh) {

		this.valorSh = valorSh;
	}

	public BigDecimal getValorUti() {

		return this.valorUti;
	}

	public void setValorUti(final BigDecimal valorUti) {

		this.valorUti = valorUti;
	}

	public BigDecimal getValorUtie() {

		return this.valorUtie;
	}

	public void setValorUtie(final BigDecimal valorUtie) {

		this.valorUtie = valorUtie;
	}

	public BigDecimal getValorSp() {

		return this.valorSp;
	}

	public void setValorSp(final BigDecimal valorSp) {

		this.valorSp = valorSp;
	}

	public BigDecimal getValorAcomp() {

		return this.valorAcomp;
	}

	public void setValorAcomp(final BigDecimal valorAcomp) {

		this.valorAcomp = valorAcomp;
	}

	public BigDecimal getValorRn() {

		return this.valorRn;
	}

	public void setValorRn(final BigDecimal valorRn) {

		this.valorRn = valorRn;
	}

	public BigDecimal getValorSadt() {

		return this.valorSadt;
	}

	public void setValorSadt(final BigDecimal valorSadt) {

		this.valorSadt = valorSadt;
	}

	public BigDecimal getValorHemat() {

		return this.valorHemat;
	}

	public void setValorHemat(final BigDecimal valorHemat) {

		this.valorHemat = valorHemat;
	}

	public BigDecimal getValorTransp() {

		return this.valorTransp;
	}

	public void setValorTransp(final BigDecimal valorTransp) {

		this.valorTransp = valorTransp;
	}

	public BigDecimal getValorOpm() {

		return this.valorOpm;
	}

	public void setValorOpm(final BigDecimal valorOpm) {

		this.valorOpm = valorOpm;
	}

	public BigDecimal getValorAnestesista() {

		return this.valorAnestesista;
	}

	public void setValorAnestesista(final BigDecimal valorAnestesista) {

		this.valorAnestesista = valorAnestesista;
	}

	public BigDecimal getValorProcedimento() {

		return this.valorProcedimento;
	}

	public void setValorProcedimento(final BigDecimal valorProcedimento) {

		this.valorProcedimento = valorProcedimento;
	}

	public BigDecimal getValorTotal() {

		return this.valorTotal;
	}

	public void setValorTotal(final BigDecimal valorTotal) {

		this.valorTotal = valorTotal;
	}

	public Boolean getIsCobraAih() {

		return this.isCobraAih;
	}

	public void setIsCobraAih(final Boolean isCobraAih) {

		this.isCobraAih = isCobraAih;
	}

	public Boolean getIsEspCir() {

		return this.isEspCir;
	}

	public void setIsEspCir(final Boolean isEspCir) {

		this.isEspCir = isEspCir;
	}

	public Boolean getIsForaEstado() {

		return this.isForaEstado;
	}

	public void setIsForaEstado(final Boolean isForaEstado) {

		this.isForaEstado = isForaEstado;
	}

	public Boolean getIsDesdobrada() {

		return this.isDesdobrada;
	}

	public void setIsDesdobrada(final Boolean isDesdobrada) {

		this.isDesdobrada = isDesdobrada;
	}

	public String getFcfDesc() {

		return this.fcfDesc;
	}

	public void setFcfDesc(final String fcfDesc) {

		this.fcfDesc = fcfDesc;
	}

	public String getStatusSms() {

		return this.statusSms;
	}

	public void setStatusSms(final String statusSms) {

		this.statusSms = statusSms;
	}

	public String getIphSsmSol() {

		return this.iphSsmSol;
	}

	public void setIphSsmSol(final String iphSsmSol) {

		this.iphSsmSol = iphSsmSol;
	}

	public String getIphSsmReal() {

		return this.iphSsmReal;
	}

	public void setIphSsmReal(final String iphSsmReal) {

		this.iphSsmReal = iphSsmReal;
	}

	public Integer getTciSeq() {
		return tciSeq;
	}

	public void setTciSeq(Integer tciSeq) {
		this.tciSeq = tciSeq;
	}

	public String getCaracterInternacao() {
		return caracterInternacao;
	}

	public void setCaracterInternacao(String caracterInternacao) {
		this.caracterInternacao = caracterInternacao;
	}

	public String getMsgErro() {
		return msgErro;
	}

	public void setMsgErro(String msgErro) {
		this.msgErro = msgErro;
	}

	public Integer getCmce() {
		return cmce;
	}

	public void setCmce(Integer cmce) {
		this.cmce = cmce;
	}

	public String getRealizadoSolicitado() {
		return realizadoSolicitado;
	}

	public void setRealizadoSolicitado(String realizadoSolicitado) {
		this.realizadoSolicitado = realizadoSolicitado;
	}
	
	@Override
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.codigo == null)
				? 0
				: this.codigo.hashCode());
		result = prime * result + ((this.cspCnvCodigo == null)
				? 0
				: this.cspCnvCodigo.hashCode());
		result = prime * result + ((this.cspSeq == null)
				? 0
				: this.cspSeq.hashCode());
		result = prime * result + ((this.cthSeq == null)
				? 0
				: this.cthSeq.hashCode());
		result = prime * result + ((this.dataInternacaoAdministrativa == null)
				? 0
				: this.dataInternacaoAdministrativa.hashCode());
		result = prime * result + ((this.descricao == null)
				? 0
				: this.descricao.hashCode());
		result = prime * result + ((this.dtAltaAdministrativa == null)
				? 0
				: this.dtAltaAdministrativa.hashCode());
		result = prime * result + ((this.dtEnvioSms == null)
				? 0
				: this.dtEnvioSms.hashCode());
		result = prime * result + ((this.fcfDesc == null)
				? 0
				: this.fcfDesc.hashCode());
		result = prime * result + ((this.indAutorizadoSms == null)
				? 0
				: this.indAutorizadoSms.hashCode());
		result = prime * result + ((this.indEnviadoSms == null)
				? 0
				: this.indEnviadoSms.hashCode());
		result = prime * result + ((this.indSituacao == null)
				? 0
				: this.indSituacao.hashCode());
		result = prime * result + ((this.iphSsmReal == null)
				? 0
				: this.iphSsmReal.hashCode());
		result = prime * result + ((this.iphSsmSol == null)
				? 0
				: this.iphSsmSol.hashCode());
		result = prime * result + ((this.isCobraAih == null)
				? 0
				: this.isCobraAih.hashCode());
		result = prime * result + ((this.isDesdobrada == null)
				? 0
				: this.isDesdobrada.hashCode());
		result = prime * result + ((this.isEspCir == null)
				? 0
				: this.isEspCir.hashCode());
		result = prime * result + ((this.isForaEstado == null)
				? 0
				: this.isForaEstado.hashCode());
		result = prime * result + ((this.leitoID == null)
				? 0
				: this.leitoID.hashCode());
		result = prime * result + ((this.tciSeq == null)
				? 0
				: this.tciSeq.hashCode());
		result = prime * result + ((this.mdsSeq == null)
				? 0
				: this.mdsSeq.hashCode());
		result = prime * result + ((this.nome == null)
				? 0
				: this.nome.hashCode());
		result = prime * result + ((this.nroAih == null)
				? 0
				: this.nroAih.hashCode());
		result = prime * result + ((this.phiRealizSeq == null)
				? 0
				: this.phiRealizSeq.hashCode());
		result = prime * result + ((this.phiSeq == null)
				? 0
				: this.phiSeq.hashCode());
		result = prime * result + ((this.prontuario == null)
				? 0
				: this.prontuario.hashCode());
		result = prime * result + ((this.statusSms == null)
				? 0
				: this.statusSms.hashCode());
		result = prime * result + ((this.valorAcomp == null)
				? 0
				: this.valorAcomp.hashCode());
		result = prime * result + ((this.valorAnestesista == null)
				? 0
				: this.valorAnestesista.hashCode());
		result = prime * result + ((this.valorHemat == null)
				? 0
				: this.valorHemat.hashCode());
		result = prime * result + ((this.valorOpm == null)
				? 0
				: this.valorOpm.hashCode());
		result = prime * result + ((this.valorProcedimento == null)
				? 0
				: this.valorProcedimento.hashCode());
		result = prime * result + ((this.valorRn == null)
				? 0
				: this.valorRn.hashCode());
		result = prime * result + ((this.valorSadt == null)
				? 0
				: this.valorSadt.hashCode());
		result = prime * result + ((this.valorSh == null)
				? 0
				: this.valorSh.hashCode());
		result = prime * result + ((this.valorSp == null)
				? 0
				: this.valorSp.hashCode());
		result = prime * result + ((this.valorTotal == null)
				? 0
				: this.valorTotal.hashCode());
		result = prime * result + ((this.valorTransp == null)
				? 0
				: this.valorTransp.hashCode());
		result = prime * result + ((this.valorUti == null)
				? 0
				: this.valorUti.hashCode());
		result = prime * result + ((this.valorUtie == null)
				? 0
				: this.valorUtie.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NcssMethodCount"})
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BuscaContaVO)) {
			return false;
		}
		BuscaContaVO other = (BuscaContaVO) obj;
		if (this.codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!this.codigo.equals(other.codigo)) {
			return false;
		}
		if (this.cspCnvCodigo == null) {
			if (other.cspCnvCodigo != null) {
				return false;
			}
		} else if (!this.cspCnvCodigo.equals(other.cspCnvCodigo)) {
			return false;
		}
		if (this.cspSeq == null) {
			if (other.cspSeq != null) {
				return false;
			}
		} else if (!this.cspSeq.equals(other.cspSeq)) {
			return false;
		}
		if (this.cthSeq == null) {
			if (other.cthSeq != null) {
				return false;
			}
		} else if (!this.cthSeq.equals(other.cthSeq)) {
			return false;
		}
		if (this.dataInternacaoAdministrativa == null) {
			if (other.dataInternacaoAdministrativa != null) {
				return false;
			}
		} else if (!this.dataInternacaoAdministrativa.equals(other.dataInternacaoAdministrativa)) {
			return false;
		}
		if (this.descricao == null) {
			if (other.descricao != null) {
				return false;
			}
		} else if (!this.descricao.equals(other.descricao)) {
			return false;
		}
		if (this.dtAltaAdministrativa == null) {
			if (other.dtAltaAdministrativa != null) {
				return false;
			}
		} else if (!this.dtAltaAdministrativa.equals(other.dtAltaAdministrativa)) {
			return false;
		}
		if (this.dtEnvioSms == null) {
			if (other.dtEnvioSms != null) {
				return false;
			}
		} else if (!this.dtEnvioSms.equals(other.dtEnvioSms)) {
			return false;
		}
		if (this.fcfDesc == null) {
			if (other.fcfDesc != null) {
				return false;
			}
		} else if (!this.fcfDesc.equals(other.fcfDesc)) {
			return false;
		}
		if (this.indAutorizadoSms == null) {
			if (other.indAutorizadoSms != null) {
				return false;
			}
		} else if (!this.indAutorizadoSms.equals(other.indAutorizadoSms)) {
			return false;
		}
		if (this.indEnviadoSms == null) {
			if (other.indEnviadoSms != null) {
				return false;
			}
		} else if (!this.indEnviadoSms.equals(other.indEnviadoSms)) {
			return false;
		}
		if (this.indSituacao != other.indSituacao) {
			return false;
		}
		if (this.iphSsmReal == null) {
			if (other.iphSsmReal != null) {
				return false;
			}
		} else if (!this.iphSsmReal.equals(other.iphSsmReal)) {
			return false;
		}
		if (this.tciSeq == null) {
			if (other.tciSeq != null) {
				return false;
			}
		} else if (!this.tciSeq.equals(other.tciSeq)) {
			return false;
		}
		if (this.iphSsmSol == null) {
			if (other.iphSsmSol != null) {
				return false;
			}
		} else if (!this.iphSsmSol.equals(other.iphSsmSol)) {
			return false;
		}
		if (this.isCobraAih == null) {
			if (other.isCobraAih != null) {
				return false;
			}
		} else if (!this.isCobraAih.equals(other.isCobraAih)) {
			return false;
		}
		if (this.isDesdobrada == null) {
			if (other.isDesdobrada != null) {
				return false;
			}
		} else if (!this.isDesdobrada.equals(other.isDesdobrada)) {
			return false;
		}
		if (this.isEspCir == null) {
			if (other.isEspCir != null) {
				return false;
			}
		} else if (!this.isEspCir.equals(other.isEspCir)) {
			return false;
		}
		if (this.isForaEstado == null) {
			if (other.isForaEstado != null) {
				return false;
			}
		} else if (!this.isForaEstado.equals(other.isForaEstado)) {
			return false;
		}
		if (this.leitoID == null) {
			if (other.leitoID != null) {
				return false;
			}
		} else if (!this.leitoID.equals(other.leitoID)) {
			return false;
		}
		if (this.mdsSeq == null) {
			if (other.mdsSeq != null) {
				return false;
			}
		} else if (!this.mdsSeq.equals(other.mdsSeq)) {
			return false;
		}
		if (this.nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!this.nome.equals(other.nome)) {
			return false;
		}
		if (this.nroAih == null) {
			if (other.nroAih != null) {
				return false;
			}
		} else if (!this.nroAih.equals(other.nroAih)) {
			return false;
		}
		if (this.phiRealizSeq == null) {
			if (other.phiRealizSeq != null) {
				return false;
			}
		} else if (!this.phiRealizSeq.equals(other.phiRealizSeq)) {
			return false;
		}
		if (this.phiSeq == null) {
			if (other.phiSeq != null) {
				return false;
			}
		} else if (!this.phiSeq.equals(other.phiSeq)) {
			return false;
		}
		if (this.prontuario == null) {
			if (other.prontuario != null) {
				return false;
			}
		} else if (!this.prontuario.equals(other.prontuario)) {
			return false;
		}
		if (this.statusSms == null) {
			if (other.statusSms != null) {
				return false;
			}
		} else if (!this.statusSms.equals(other.statusSms)) {
			return false;
		}
		if (this.valorAcomp == null) {
			if (other.valorAcomp != null) {
				return false;
			}
		} else if (!this.valorAcomp.equals(other.valorAcomp)) {
			return false;
		}
		if (this.valorAnestesista == null) {
			if (other.valorAnestesista != null) {
				return false;
			}
		} else if (!this.valorAnestesista.equals(other.valorAnestesista)) {
			return false;
		}
		if (this.valorHemat == null) {
			if (other.valorHemat != null) {
				return false;
			}
		} else if (!this.valorHemat.equals(other.valorHemat)) {
			return false;
		}
		if (this.valorOpm == null) {
			if (other.valorOpm != null) {
				return false;
			}
		} else if (!this.valorOpm.equals(other.valorOpm)) {
			return false;
		}
		if (this.valorProcedimento == null) {
			if (other.valorProcedimento != null) {
				return false;
			}
		} else if (!this.valorProcedimento.equals(other.valorProcedimento)) {
			return false;
		}
		if (this.valorRn == null) {
			if (other.valorRn != null) {
				return false;
			}
		} else if (!this.valorRn.equals(other.valorRn)) {
			return false;
		}
		if (this.valorSadt == null) {
			if (other.valorSadt != null) {
				return false;
			}
		} else if (!this.valorSadt.equals(other.valorSadt)) {
			return false;
		}
		if (this.valorSh == null) {
			if (other.valorSh != null) {
				return false;
			}
		} else if (!this.valorSh.equals(other.valorSh)) {
			return false;
		}
		if (this.valorSp == null) {
			if (other.valorSp != null) {
				return false;
			}
		} else if (!this.valorSp.equals(other.valorSp)) {
			return false;
		}
		if (this.valorTotal == null) {
			if (other.valorTotal != null) {
				return false;
			}
		} else if (!this.valorTotal.equals(other.valorTotal)) {
			return false;
		}
		if (this.valorTransp == null) {
			if (other.valorTransp != null) {
				return false;
			}
		} else if (!this.valorTransp.equals(other.valorTransp)) {
			return false;
		}
		if (this.valorUti == null) {
			if (other.valorUti != null) {
				return false;
			}
		} else if (!this.valorUti.equals(other.valorUti)) {
			return false;
		}
		if (this.valorUtie == null) {
			if (other.valorUtie != null) {
				return false;
			}
		} else if (!this.valorUtie.equals(other.valorUtie)) {
			return false;
		}
		return true;
	}
}
