package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioFixoDemanda;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioReceitaDespesa;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioTipoAditivo;
import br.gov.mec.aghu.dominio.DominioTipoGarantia;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name = "SCO_CONTRATOS", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = "NR_CONTRATO"))
@SequenceGenerator(name = "scoContSq1", sequenceName = "AGH.SCO_CONT_SQ1", allocationSize = 1)
public class ScoContrato extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7030223179844407333L;
	private Integer seq;
	private Long nrContrato;
	private Integer uasgSubrog;
	private String codInternoUasg;
	private Integer uasgLicit;
	private String inciso;
	private Date dtPublicacao;
	private String objetoContrato;
	private String fundamentoLegal;
	private Date dtInicioVigencia;
	private Date dtFimVigencia;
	private Date dtAssinatura;
	private BigDecimal valorTotal;
	private DominioOrigemContrato indOrigem;
	private DominioSituacaoEnvioContrato situacao;
	private DominioReceitaDespesa indRecDep;
	private DominioSimNao indAditivar;
	private DominioSimNao indLicitar;
	private DominioTipoGarantia indTipoGarantia;
	private BigDecimal valorGarantia;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer version;
	private ScoCriterioReajusteContrato criterioReajusteContrato;
	private ScoTipoContratoSicon tipoContratoSicon;
	private ScoFornecedor fornecedor;
	private ScoModalidadeLicitacao modalidadeLicitacao;
	private ScoLicitacao licitacao;
	private RapServidores servidorGestor;
	private RapServidores servidorFiscal;
	private RapServidores servidor;
	private DominioFixoDemanda indFixoDemanda;
	private String observacao;
	private List<ScoAfContrato> scoAfContratos;
	private List<ScoAditContrato> aditivos;
	private List<ScoItensContrato> itensContrato;
	private ScoResContrato rescicao;
	protected Date dataIniVigComAditivos;
	protected Date dataFimVigComAditivos;

	// construtores

	public ScoContrato() {
	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoContSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NR_CONTRATO", length = 5)
	public Long getNrContrato() {
		return this.nrContrato;
	}

	public void setNrContrato(Long nrContrato) {
		this.nrContrato = nrContrato;
	}

	@Column(name = "UASG_SUBROG", length = 7)
	public Integer getUasgSubrog() {
		return this.uasgSubrog;
	}

	public void setUasgSubrog(Integer uasgSubrog) {
		this.uasgSubrog = uasgSubrog;
	}

	@Column(name = "COD_INTERNO_UASG", length = 10)
	public String getCodInternoUasg() {
		return this.codInternoUasg;
	}

	public void setCodInternoUasg(String codInternoUasg) {
		this.codInternoUasg = codInternoUasg;
	}

	@Column(name = "UASG_LICIT", length = 7)
	public Integer getUasgLicit() {
		return this.uasgLicit;
	}

	public void setUasgLicit(Integer uasgLicit) {
		this.uasgLicit = uasgLicit;
	}

	@Column(name = "INCISO", length = 2)
	public String getInciso() {
		return this.inciso;
	}

	public void setInciso(String inciso) {
		this.inciso = inciso;
	}

	@Column(name = "DT_PUBLICACAO")
	@Temporal(TemporalType.DATE)
	public Date getDtPublicacao() {
		return this.dtPublicacao;
	}

	public void setDtPublicacao(Date dtPublicacao) {
		this.dtPublicacao = dtPublicacao;
	}

	@Column(name = "OBJETO_CONTRATO", length = 509, nullable = false)
	public String getObjetoContrato() {
		return this.objetoContrato;
	}

	public void setObjetoContrato(String objetoContrato) {
		this.objetoContrato = objetoContrato;
	}

	@Column(name = "FUNDAMENTO_LEGAL", length = 141)
	public String getFundamentoLegal() {
		return this.fundamentoLegal;
	}

	public void setFundamentoLegal(String fundamentoLegal) {
		this.fundamentoLegal = fundamentoLegal;
	}

	@Column(name = "DT_INICIO_VIGENCIA", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getDtInicioVigencia() {
		return this.dtInicioVigencia;
	}

	public void setDtInicioVigencia(Date dtInicioVigencia) {
		this.dtInicioVigencia = dtInicioVigencia;
	}

	@Column(name = "DT_FIM_VIGENCIA", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getDtFimVigencia() {
		return this.dtFimVigencia;
	}

	public void setDtFimVigencia(Date dtFimVigencia) {
		this.dtFimVigencia = dtFimVigencia;
	}

	@Column(name = "DT_ASSINATURA")
	@Temporal(TemporalType.DATE)
	public Date getDtAssinatura() {
		return this.dtAssinatura;
	}

	public void setDtAssinatura(Date dtAssinatura) {
		this.dtAssinatura = dtAssinatura;
	}

	@Column(name = "VALOR_TOTAL", precision = 15, scale = 2)
	public BigDecimal getValorTotal() {
		return this.valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	@Column(name = "IND_ORIGEM", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioOrigemContrato getIndOrigem() {
		return this.indOrigem;
	}

	public void setIndOrigem(DominioOrigemContrato indOrigem) {
		this.indOrigem = indOrigem;
	}

	@Column(name = "IND_SITUACAO", length = 2, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoEnvioContrato getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoEnvioContrato situacao) {
		this.situacao = situacao;
	}

	@Column(name = "IND_REC_DEP", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioReceitaDespesa getIndRecDep() {
		return this.indRecDep;
	}

	public void setIndRecDep(DominioReceitaDespesa indRecDep) {
		this.indRecDep = indRecDep;
	}

	@Column(name = "IND_ADITIVAR", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndAditivar() {
		return this.indAditivar;
	}

	public void setIndAditivar(DominioSimNao indAditivar) {
		this.indAditivar = indAditivar;
	}

	@Column(name = "IND_LICITAR", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndLicitar() {
		return this.indLicitar;
	}

	public void setIndLicitar(DominioSimNao indLicitar) {
		this.indLicitar = indLicitar;
	}

	@Column(name = "IND_TIPO_GARANTIA", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioTipoGarantia getIndTipoGarantia() {
		return this.indTipoGarantia;
	}

	public void setIndTipoGarantia(DominioTipoGarantia indTipoGarantia) {
		this.indTipoGarantia = indTipoGarantia;
	}

	@Column(name = "VALOR_GARANTIA", precision = 15, scale = 2)
	public BigDecimal getValorGarantia() {
		return this.valorGarantia;
	}

	public void setValorGarantia(BigDecimal valorGarantia) {
		this.valorGarantia = valorGarantia;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ALTERADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "VERSION", length = 7)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RCON_SEQ", referencedColumnName = "SEQ")
	public ScoCriterioReajusteContrato getCriterioReajusteContrato() {
		return criterioReajusteContrato;
	}

	public void setCriterioReajusteContrato(
			ScoCriterioReajusteContrato criterioReajusteContrato) {
		this.criterioReajusteContrato = criterioReajusteContrato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TCON_SEQ", referencedColumnName = "SEQ")
	public ScoTipoContratoSicon getTipoContratoSicon() {
		return tipoContratoSicon;
	}

	public void setTipoContratoSicon(ScoTipoContratoSicon tipoContratoSicon) {
		this.tipoContratoSicon = tipoContratoSicon;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO", referencedColumnName = "NUMERO")
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MLC_CODIGO", referencedColumnName = "CODIGO")
	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(
			ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LCT_NUMERO", referencedColumnName = "NUMERO")
	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_GESTOR", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_GESTOR", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorGestor() {
		return servidorGestor;
	}

	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_FISCAL", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_FISCAL", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorFiscal() {
		return servidorFiscal;
	}

	public void setServidorFiscal(RapServidores servidorFiscal) {
		this.servidorFiscal = servidorFiscal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "IND_FIXO_DEMANDA", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioFixoDemanda getIndFixoDemanda() {
		return indFixoDemanda;
	}

	public void setIndFixoDemanda(DominioFixoDemanda indFixoDemanda) {
		this.indFixoDemanda = indFixoDemanda;
	}

	@Column(name = "OBSERVACAO", length = 80)
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	// outros

	// bi-directional many-to-one association to ScoAfContrato
	@OneToMany(mappedBy = "scoContrato", fetch = FetchType.LAZY)
	public List<ScoAfContrato> getScoAfContratos() {
		return scoAfContratos;
	}

	public void setScoAfContratos(List<ScoAfContrato> scoAfContratos) {
		this.scoAfContratos = scoAfContratos;
	}

	@OneToMany(mappedBy = "cont", fetch = FetchType.LAZY)
	public List<ScoAditContrato> getAditivos() {
		return aditivos;
	}

	public void setAditivos(List<ScoAditContrato> aditivos) {
		this.aditivos = aditivos;
	}

	@OneToOne(mappedBy = "contrato", fetch = FetchType.LAZY)
	public ScoResContrato getRescicao() {
		return rescicao;
	}

	public void setRescicao(ScoResContrato rescicao) {
		this.rescicao = rescicao;
	}

	@OneToMany(mappedBy = "contrato", fetch = FetchType.LAZY)
	public List<ScoItensContrato> getItensContrato() {
		return itensContrato;
	}

	public void setItensContrato(List<ScoItensContrato> itensContrato) {
		this.itensContrato = itensContrato;
	}

	@Transient
	public boolean isLicitar() {
		if (indLicitar != null && indLicitar.isSim()) {
			return true;
		} else {
			return false;
		}
	}

	public void setLicitar(boolean input) {
		if (input) {
			setIndLicitar(DominioSimNao.S);
		} else {
			setIndLicitar(DominioSimNao.N);
		}
	}

	@Transient
	public boolean isAditivar() {
		if (indAditivar != null && indAditivar.isSim()) {
			return true;
		} else {
			return false;
		}
	}

	@Transient
	public BigDecimal getValTotComAdit() {
		if (this.aditivos == null) {
			return this.valorTotal;
		} else {
			if (this.aditivos.size() == 0) {
				return this.valorTotal;
			} else {
				BigDecimal val = new BigDecimal(this.valorTotal.longValue());
				for (ScoAditContrato aditcont : this.aditivos) {
					if (aditcont.getDataRescicao() == null) {
						if (aditcont.getVlAditivado() != null) {
							if (aditcont.getIndTipoAditivo() == DominioTipoAditivo.A) {
								val = val.add(aditcont.getVlAditivado());
							} else {
								val = val.subtract(aditcont.getVlAditivado());
							}
						}
					}
				}
				return val;
			}
		}
	}

	@Transient
	public BigDecimal getValEfetAfs() {
		if (this.scoAfContratos != null) {
			if (this.scoAfContratos.size() > 0) {

				BigDecimal val = BigDecimal.ZERO;

				for (ScoAfContrato afcon : this.scoAfContratos) {

					if (afcon.getScoAutorizacoesForn().getSituacao()
							.equals(DominioSituacaoAutorizacaoFornecedor.EX)) {
						continue;
					}

					val = val
							.add(afcon.getScoAutorizacoesForn().getValEfetAF());
				}

				return val;
			}
		}
		return null;
	}

	@Transient
	public boolean hasAditivosAguardandoEnvio() {
		for (ScoAditContrato ad : this.aditivos) {
			if (ad.getSituacao() == DominioSituacaoEnvioContrato.A
					|| ad.getSituacao() == DominioSituacaoEnvioContrato.AR) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public boolean hasAditivosComErroEnvio() {
		for (ScoAditContrato ad : this.aditivos) {
			if (ad.getSituacao() == DominioSituacaoEnvioContrato.EE) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public boolean isTodosAditivosEnviados() {
		for (ScoAditContrato ad : this.aditivos) {
			if (ad.getSituacao() != DominioSituacaoEnvioContrato.E) {
				return false;
			} else {
				continue;
			}
		}
		return true;
	}

	public void setAditivar(boolean input) {
		if (input) {
			setIndAditivar(DominioSimNao.S);
		} else {
			setIndAditivar(DominioSimNao.N);
		}
	}

	@Transient
	public Date getDataIniVigComAditivos() {
		if (this.aditivos == null) {
			return getDtInicioVigencia();
		} else {
			if (this.aditivos.size() == 0) {
				return getDtInicioVigencia();
			} else {
				Date dataInicio = getDtInicioVigencia();

				for (ScoAditContrato aditivo : getAditivos()) {
					if (aditivo.getDataRescicao() == null) {
						if (aditivo.getDtInicioVigencia() != null) {
							if (aditivo.getDtInicioVigencia()
									.before(dataInicio)) {
								dataInicio = aditivo.getDtInicioVigencia();
							}
						}
					}
				}

				return dataInicio;
			}
		}

	}

	public void setDataIniVigComAditivos(Date d) {
		this.dataIniVigComAditivos = d;
	}

	@Transient
	public Date getDataFimVigComAditivos() {
		if (this.aditivos == null) {
			return getDtFimVigencia();
		} else {
			if (this.aditivos.size() == 0) {
				return getDtFimVigencia();
			} else {
				Date dataFim = getDtFimVigencia();

				for (ScoAditContrato aditivo : getAditivos()) {
					if (aditivo.getDataRescicao() == null) {
						if (aditivo.getDtFimVigencia() != null) {
							if (aditivo.getDtFimVigencia().after(dataFim)) {
								dataFim = aditivo.getDtFimVigencia();
							}
						}
					}
				}

				return dataFim;
			}
		}
	}

	public void setDataFimVigComAditivos(Date d) {
		this.dataFimVigComAditivos = d;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	
	public enum Fields {
		SEQ("seq"), NR_CONTRATO("nrContrato"), UASG_SUBROG("uasgSubrog"), COD_INTERNO_UASG(
				"codInternoUasg"), UASG_LICIT("uasgLicit"), INCISO("inciso"), DT_PUBLICACAO(
				"dtPublicacao"), OBJETO_CONTRATO("objetoContrato"), FUNDAMENTO_LEGAL(
				"fundamentoLegal"), DT_INICIO_VIGENCIA("dtInicioVigencia"), DT_FIM_VIGENCIA(
				"dtFimVigencia"), DT_ASSINATURA("dtAssinatura"), VALOR_TOTAL(
				"valorTotal"), IND_ORIGEM("indOrigem"), SITUACAO("situacao"), IND_REC_DEP(
				"indRecDep"), IND_ADITIVAR("indAditivar"), IND_LICITAR(
				"indLicitar"), IND_TIPO_GARANTIA("indTipoGarantia"), VALOR_GARANTIA(
				"valorGarantia"), CRIADO_EM("criadoEm"), ALTERADO_EM(
				"alteradoEm"), VERSION("version"), CRITERIO_REAJUSTE_CONTRATO(
				"criterioReajusteContrato"), TIPO_CONTRATO_SICON(
				"tipoContratoSicon"), FORNECEDOR("fornecedor"), MODALIDADE_LICITACAO(
				"modalidadeLicitacao"), LICITACAO("licitacao"), SERVIDOR_GESTOR(
				"servidorGestor"), SERVIDOR_FISCAL("servidorFiscal"), SERVIDOR(
				"servidor"), RESCICAO("rescicao"), ADITIVOS("aditivos"), AFS(
				"scoAfContratos"), ITENS_CONT("itensContrato"), OBSERVACAO(
				"observacao");

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