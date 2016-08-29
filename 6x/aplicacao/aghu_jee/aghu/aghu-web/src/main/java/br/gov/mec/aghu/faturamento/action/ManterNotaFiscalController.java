package br.gov.mec.aghu.faturamento.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoFornecedor;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceItemRmpsId;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

public class ManterNotaFiscalController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterNotaFiscalController.class);

	private static final long serialVersionUID = 6132559299807341569L;

	public static final String NOTA_FISCAL = "notaFiscal";

	public static final String NUMERO_FORNECEDOR = "numeroFornecedor";

	public static final String RMR_SEQ = "sceRmrPaciente";

	public enum ManterNotaFiscalControllerExceptionCode implements BusinessExceptionCode {
		SCE_00677, ERRO_CNPJ_DUPLICADO, ERRO_CPF_DUPLICADO
	}

	private static final Integer SUGGESTION_LIMIT = 100;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;	

	private FatItemContaHospitalar itemContaHospitalar;

	private Integer cthSeq;

	private Short seq;

	private ScoFornecedor scoFornecedorAnvisa;

	private Integer nrFornecedor;

	private String propriedade;

	private boolean indSituacao;

	private boolean indNacional;

	private SceItemRmps oldItem;

	private SceItemRmps item;

	private SceRmrPaciente oldPaciente;

	private FatItemContaHospitalar oldItemContaHospitalar;

	private ScoFornecedor oldFornecedor = null;

	private ScoFornecedor oldFornecedorAnvisa = null;

	private ScoFornecedor tmpFornecedor;

	private boolean mostrarModal;

	private boolean fornecedorNovo;

	private boolean fornecedorAnvisaNovo;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	// public enum PropriedadesFornecedor {
	// FORNECEDOR, CNPJ_ANVISA;
	// }

	@SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "PMD.NPathComplexity" })
	public void inicio() {
	 

		if (getCthSeq() == null || getSeq() == null) {
			// mensagem de parâmetro inválidos
			this.apresentarMsgNegocio(Severity.FATAL, "MSG_PARAMETROS_INVALIDOS_MANTER_NOTA_FISCAL");
		} else {
			try {
				FatItemContaHospitalar itemCTH = getItemContaHospitalar();
				if (itemCTH == null) {
					itemCTH = this.faturamentoFacade.obterItemContaHospitalarLazyPorId(new FatItemContaHospitalarId(getCthSeq(), getSeq()));
					setItemContaHospitalar(itemCTH);
				}
				if (itemCTH == null) {
					// mensagem de item não encontrado
					this.apresentarMsgNegocio(Severity.FATAL, "MSG_ITEM_CONTA_HOSPITALAR_MANTER_NOTA_FISCAL");
				} else {
					fornecedorNovo = false;
					fornecedorAnvisaNovo = false;
					oldItemContaHospitalar = this.faturamentoFacade.clonarItemContaHospitalar(itemCTH);
					SceItemRmps itemRmps = itemCTH.getItemRmps();
					if (itemRmps == null) {
						itemRmps = new SceItemRmps();
						itemRmps.setId(new SceItemRmpsId());

						final Map<String, Integer> dadosIniciais = faturamentoFacade.buscarDadosInicias(getCthSeq());
						if (dadosIniciais != null && !dadosIniciais.isEmpty()) {
							itemRmps.setNotaFiscal(dadosIniciais.get(NOTA_FISCAL));

							final SceRmrPaciente paciente = estoqueFacade.obterSceRmrPacientePorChavePrimaria(dadosIniciais.get(RMR_SEQ));
							itemRmps.setSceRmrPaciente(paciente);

							// informa o fornecedor
							final Integer nrForn = dadosIniciais.get(NUMERO_FORNECEDOR);
							if (nrForn != null) {
								itemRmps.getSceRmrPaciente().setScoFornecedor(comprasFacade.obterFornecedorPorNumero(nrForn));
							}
							// informa internacao
							setarInternacao(itemRmps.getSceRmrPaciente(), itemCTH);

							this.oldPaciente = comprasFacade.cloneSceRmrPaciente(paciente);
						}
					} else {
						this.oldItem = comprasFacade.cloneSceItemRmps(itemRmps);
					}

					setItem(itemRmps);

					if (itemRmps.getSceRmrPaciente() == null) {
						itemRmps.setSceRmrPaciente(new SceRmrPaciente());
						// informa a internacao
						setarInternacao(itemRmps.getSceRmrPaciente(), itemCTH);
					} else if (this.oldPaciente == null) {
						this.oldPaciente = comprasFacade.cloneSceRmrPaciente(itemRmps.getSceRmrPaciente());
					}

					if (itemRmps.getCnpjRegistroAnvisa() != null) {
						final List<ScoFornecedor> fornecedors = comprasFacade.listarFornecedoresAtivos(itemRmps.getCnpjRegistroAnvisa()
								.toString());
						if (fornecedors != null && !fornecedors.isEmpty()) {
							setScoFornecedorAnvisa(fornecedors.get(0));
							oldFornecedorAnvisa = comprasFacade.clonarFornecedor(getScoFornecedorAnvisa());
						}
					}
					if (itemRmps.getSceRmrPaciente().getScoFornecedor() != null) {
						oldFornecedor = comprasFacade.clonarFornecedor(itemRmps.getSceRmrPaciente().getScoFornecedor());
					}
					itemRmps.setQuantidade(itemCTH.getQuantidadeRealizada() == null ? 0 : itemCTH.getQuantidadeRealizada().intValue());
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				LOG.error("Exceção capturada: ",e);
				this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			}
		}
	
	}

	private void setarInternacao(final SceRmrPaciente paciente, final FatItemContaHospitalar item) {
		if (paciente.getInternacao() == null) {
			final List<FatContasInternacao> contasInternacao = this.faturamentoFacade.listarContasInternacao(item.getContaHospitalar().getSeq());
			if (contasInternacao == null || contasInternacao.isEmpty()) {
				paciente.setProcedimentoHospitalarInterno(item.getProcedimentoHospitalarInterno());
			} else {
				for (final FatContasInternacao fatContaInternacao : contasInternacao) {
					//final AinInternacao internacao = fatContaInternacao.getInternacao();
					final AinInternacao internacao = faturamentoFacade.buscaContaInternacao(fatContaInternacao.getSeq()).getInternacao();
					if (internacao != null) {
						final Date inicioInt = internacao.getDthrInternacao();
						Date fimInt = item.getContaHospitalar().getDtAltaAdministrativa();
						if (fimInt == null) {
							fimInt = new Date();
						}

						if (item.getDthrRealizado().after(inicioInt) && item.getDthrRealizado().before(fimInt)) {
							paciente.setInternacao(internacao);
						}
					} else {
						paciente.setProcedimentoHospitalarInterno(item.getProcedimentoHospitalarInterno());
					}
				}
			}
		}
	}

	public String cadastrarFornecedor(final String propriedade) {
		setPropriedade(propriedade);
		return "manterFornecedor";
	}
	
	private IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}	

	public String gravar() {
		try {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			SceRmrPaciente sceRmrPaciente = null;
			if (this.oldFornecedor == null
					|| (getItem().getSceRmrPaciente().getScoFornecedor().getNumero().equals(this.oldFornecedor.getNumero())) == false
					&& getItem().getId().getNumero() == null) {
				// novo item sendo cadastrado com fornecedor diferente do
				// anterior
				sceRmrPaciente = new SceRmrPaciente();
				sceRmrPaciente.setScoFornecedor(getItem().getSceRmrPaciente().getScoFornecedor());
				sceRmrPaciente.setInternacao(getItem().getSceRmrPaciente().getInternacao());
				sceRmrPaciente.setCirurgia(getItem().getSceRmrPaciente().getCirurgia());
				if (sceRmrPaciente.getInternacao() == null && sceRmrPaciente.getCirurgia() == null) {
					sceRmrPaciente.setProcedimentoHospitalarInterno(getItem().getSceRmrPaciente().getProcedimentoHospitalarInterno());
				}
				sceRmrPaciente = this.comprasFacade.persistirSceRmrPaciente(sceRmrPaciente, this.oldPaciente, true);
			} else {
				sceRmrPaciente = this.comprasFacade.persistirSceRmrPaciente(getItem().getSceRmrPaciente(), this.oldPaciente, true);
			}
			getItem().setSceRmrPaciente(sceRmrPaciente);

			getItem().getId().setRmpSeq(getItem().getSceRmrPaciente().getSeq());
			getItem().getItensContasHospitalar().add(getItemContaHospitalar());

			final SceItemRmps itemRmps = this.comprasFacade.persistirItemRmps(getItem(), this.oldItem, true);
			itemContaHospitalar = this.faturamentoFacade.obterItemContaHospitalar(new FatItemContaHospitalarId(getCthSeq(), getSeq()));
			itemContaHospitalar.setItemRmps(itemRmps);
			faturamentoFacade.persistirItemContaHospitalar(itemContaHospitalar, oldItemContaHospitalar, servidorLogado, new Date());
			apresentarMsgNegocio(Severity.INFO, "VALORES_NOTA_FISCAL_ALTERADA_SUCESSO");
			return cancelar();
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (final RuntimeException rte) {
			apresentarMsgNegocio(Severity.FATAL, rte.getMessage());
		}
		return null;
	}

	public String limpar() {
		final SceRmrPaciente sceRmrPaciente = new SceRmrPaciente();
		setarInternacao(sceRmrPaciente, getItemContaHospitalar());
		if (this.oldItem == null) {
			setItem(new SceItemRmps());
			getItem().setId(new SceItemRmpsId());
			getItem().setSceRmrPaciente(sceRmrPaciente);
			getItem().getItensContasHospitalar().add(getItemContaHospitalar());
			this.oldItem = null;
			this.oldPaciente = null;
		} else {
			getItem().setCnpjRegistroAnvisa(null);
			getItem().setLote(null);
			getItem().setQuantidade(null);
			getItem().setSerie(null);
			if (this.oldPaciente == null) {
				getItem().setSceRmrPaciente(sceRmrPaciente);
			} else {
				getItem().getSceRmrPaciente().setScoFornecedor(null);
			}
		}
		setScoFornecedorAnvisa(null);
		setNrFornecedor(null);
		setPropriedade(null);
		return "";
	}

	public void iniciaGravacaoFornecedorAnvisa() {
		tmpFornecedor = null;
		setScoFornecedorAnvisa(iniciarScoFornecedor());
		fornecedorAnvisaNovo = true;
		setMostrarModal(true);
	}

	public void iniciaEdicaoFornecedor() {
		if (getItem().getSceRmrPaciente().getScoFornecedor() != null) {
			try {
				tmpFornecedor = comprasFacade.clonarFornecedor(getItem().getSceRmrPaciente().getScoFornecedor());
			} catch (final BaseException e) {
				apresentarExcecaoNegocio(e);
			}
			popularCheckBox(getItem().getSceRmrPaciente().getScoFornecedor());
		}
		setMostrarModal(true);
	}

	public void iniciaEdicaoFornecedorAnvisa() {
		if (getScoFornecedorAnvisa() != null) {
			try {
				tmpFornecedor = comprasFacade.clonarFornecedor(getScoFornecedorAnvisa());
			} catch (final BaseException e) {
				apresentarExcecaoNegocio(e);
			}
			popularCheckBox(getScoFornecedorAnvisa());
		}
		setMostrarModal(true);
	}

	private void popularCheckBox(final ScoFornecedor fornecedor) {
		setIndNacional(DominioTipoFornecedor.FNA.equals(fornecedor.getTipoFornecedor()));
		setIndSituacao(DominioSituacao.A.equals(fornecedor.getSituacao()));
	}

	private ScoFornecedor iniciarScoFornecedor() {
		final ScoFornecedor fornecedor = new ScoFornecedor();
		fornecedor.setDtCadastramento(new Date());
		// fornecedor.setDtAlteracao(new Date());
		setarIndInicial();
		return fornecedor;
	}

	private void setarIndInicial() {
		setIndSituacao(true);
		setIndNacional(true);
	}

	private ScoFornecedor gravarScoFornecedor(final ScoFornecedor fornecedor, final ScoFornecedor oldFornecedor) {
		if (isIndNacional() && fornecedor.getCpfCnpj() == null) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(ManterNotaFiscalControllerExceptionCode.SCE_00677));
		} else {
			try {
				fornecedor.setSituacao(isIndSituacao() ? DominioSituacao.A : DominioSituacao.I);
				if (isIndNacional()) {
					fornecedor.setTipoFornecedor(DominioTipoFornecedor.FNA);
				} else {
					fornecedor.setTipoFornecedor(DominioTipoFornecedor.FNE);
					// INFO setado null para na inscrição estadual para
					// Estrangeiros pois em "validarConstraints" do
					// "ScoFornecedor"
					// é verificado e esse campo não consta na tela
					fornecedor.setInscricaoEstadual(null);
					fornecedor.setClassificacaoEconomica(null);
				}
				fornecedor.setDtAlteracao(new Date());
				final ScoFornecedor retorno;
				if (fornecedor.getNumero() == null) {
					// fornecedor.setDtCadastramento(new Date());
					retorno = comprasFacade.inserirScoFornecedor(fornecedor, true);
				} else {
					retorno = comprasFacade.atualizarScoFornecedor(fornecedor, oldFornecedor, true);
				}
				setMostrarModal(false);
				return retorno;
			} catch (final BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (final RuntimeException re) {
				this.apresentarMsgNegocio(Severity.ERROR, re.getLocalizedMessage());
				LOG.error("Exceção capturada: ",re);
			}
		}
		return fornecedor;
	}

	public void iniciaGravacaoFornecedor() {
		tmpFornecedor = null;
		getItem().getSceRmrPaciente().setScoFornecedor(iniciarScoFornecedor());
		fornecedorNovo = true;
		setMostrarModal(true);
	}

	public void gravarFornecedor() {
		final ScoFornecedor fornecedorAnvisa = getScoFornecedorAnvisa();
		final ScoFornecedor fornecedor = getItem().getSceRmrPaciente().getScoFornecedor();
		boolean valido = true;
		if (fornecedorAnvisa != null && fornecedorAnvisaNovo && DominioTipoFornecedor.FNA.equals(fornecedorAnvisa.getTipoFornecedor())
				&& isIndNacional()) {
			if (fornecedor.getCgc() != null && fornecedor.getCgc().equals(fornecedorAnvisa.getCgc())) {
				apresentarExcecaoNegocio(new ApplicationBusinessException(ManterNotaFiscalControllerExceptionCode.ERRO_CNPJ_DUPLICADO));
				valido = false;
			} else if (fornecedor.getCpf() != null && fornecedor.getCpf().equals(fornecedorAnvisa.getCpf())) {
				apresentarExcecaoNegocio(new ApplicationBusinessException(ManterNotaFiscalControllerExceptionCode.ERRO_CPF_DUPLICADO));
				valido = false;
			}
		}
		if (valido) {
			getItem().getSceRmrPaciente().setScoFornecedor(gravarScoFornecedor(fornecedor, oldFornecedor));
		}
	}

	public void cancelarFornecedorAnvisa() {
		setScoFornecedorAnvisa(tmpFornecedor);
		setMostrarModal(false);
	}

	public void cancelarFornecedor() {
		getItem().getSceRmrPaciente().setScoFornecedor(tmpFornecedor);
		setMostrarModal(false);
		setMostrarModal(false);
	}

	public void gravarFornecedorAnvisa() {
		final ScoFornecedor fornecedorAnvisa = getScoFornecedorAnvisa();
		final ScoFornecedor fornecedor = getItem().getSceRmrPaciente().getScoFornecedor();
		boolean valido = true;
		if (fornecedor != null && fornecedorNovo && isIndNacional() && DominioTipoFornecedor.FNA.equals(fornecedor.getTipoFornecedor())) {
			if (fornecedorAnvisa.getCgc() != null && fornecedorAnvisa.getCgc().equals(fornecedor.getCgc())) {
				apresentarExcecaoNegocio(new ApplicationBusinessException(ManterNotaFiscalControllerExceptionCode.ERRO_CNPJ_DUPLICADO));
				valido = false;
			} else if (fornecedorAnvisa.getCpf() != null && fornecedorAnvisa.getCpf().equals(fornecedor.getCpf())) {
				apresentarExcecaoNegocio(new ApplicationBusinessException(ManterNotaFiscalControllerExceptionCode.ERRO_CPF_DUPLICADO));
				valido = false;
			}
		}
		if (valido) {
			setScoFornecedorAnvisa(gravarScoFornecedor(fornecedorAnvisa, oldFornecedorAnvisa));
		}
	}

	public List<ScoFornecedor> listarFornecedor(final String strPesquisa) {
		return this.returnSGWithCount(comprasFacade.listarFornecedoresAtivos(strPesquisa, 0, SUGGESTION_LIMIT, null, false), 
				comprasFacade.listarFornecedoresAtivosCount(strPesquisa));
	}

	public String cancelar() {
		return "lancarItensContaHospitalarList";
	}

	public void setItemContaHospitalar(final FatItemContaHospitalar itemContaHospitalar) {
		this.itemContaHospitalar = itemContaHospitalar;
	}

	public FatItemContaHospitalar getItemContaHospitalar() {
		return itemContaHospitalar;
	}

	public void setCthSeq(final Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setSeq(final Short seq) {
		this.seq = seq;
	}

	public Short getSeq() {
		return seq;
	}

	public void setScoFornecedorAnvisa(final ScoFornecedor scoFornecedorAnvisa) {
		this.scoFornecedorAnvisa = scoFornecedorAnvisa;
		if (getItemContaHospitalar() != null && getItem() != null) {
			if (scoFornecedorAnvisa == null) {
				getItem().setCnpjRegistroAnvisa(null);
			} else {
				getItem().setCnpjRegistroAnvisa(scoFornecedorAnvisa.getCpfCnpj());
			}
		}
	}

	public ScoFornecedor getScoFornecedorAnvisa() {
		return scoFornecedorAnvisa;
	}

	public void setNrFornecedor(final Integer nrFornecedor) {
		this.nrFornecedor = nrFornecedor;
	}

	public Integer getNrFornecedor() {
		return nrFornecedor;
	}

	public void setPropriedade(final String propriedade) {
		this.propriedade = propriedade;
	}

	public String getPropriedade() {
		return propriedade;
	}

	public void setIndSituacao(final boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public boolean isIndSituacao() {
		return indSituacao;
	}

	public void setIndNacional(final boolean indNacional) {
		this.indNacional = indNacional;
	}

	public boolean isIndNacional() {
		return indNacional;
	}

	public void setItem(final SceItemRmps item) {
		this.item = item;
	}

	public SceItemRmps getItem() {
		return item;
	}

	public void setMostrarModal(final boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}

	public boolean isMostrarModal() {
		return mostrarModal;
	}

	public IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	public void setPesquisaInternacaoFacade(IPesquisaInternacaoFacade pesquisaInternacaoFacade) {
		this.pesquisaInternacaoFacade = pesquisaInternacaoFacade;
	}

}
