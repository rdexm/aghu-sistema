package br.gov.mec.aghu.sicon.action;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioSortableSitEnvioContrato;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;
import br.gov.mec.aghu.sicon.vo.ContratoGridVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class GerenciarContratosController extends ActionController {
    
    	private static final String HABILITAR = "habilitar";

	private static final String GERENCIAR_CONTRATOS = "gerenciarContratos";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(GerenciarContratosController.class);

	private static final long serialVersionUID = -7170414301840612402L;

	private static final String PAGE_MANTER_CONTRATO_MANUAL = "manterContratoManual";
	private static final String PAGE_ASSOCIAR_AF_CONTRATO = "associarAfContrato";
	private static final String PAGE_MANTER_CONTRATO_AUTOMATICO = "manterContratoAutomatico";
	private static final String PAGE_MANTER_ADITIVO_CONTRATUAL = "manterAditivoContratual";
	private static final String PAGE_MANTER_RESCISAO_CONTRATO = "sicon-manterRescisaoContrato";
	private static final String PAGE_MANTER_MATERIAL_SICON = "sicon-manterMaterialSicon";
	private static final String PAGE_MANTER_SERVICO_SICON = "sicon-manterServicoSicon";
	private static final String PAGE_PESQUISAR_CONTRATOS_FUTUROS = "pesquisarContratosFuturos";

	private enum GerenciarContratosControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NROCP_OBRIGATORIO, MENSAGEM_SELECIONE_ALGUM_FILTRO;
	}

	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	@Inject
	private SecurityController securityController;

	private ContratoFiltroVO filtrocontrato;

	private String contSelecionadoNro;

	private final String htmlLB = "<br>";
	private final String leadingChar = " - ";
	private final String doubleDot = ": ";

	private List<ContratoGridVO> contratos;
	private ContratoGridVO contSelecionado;
	private boolean novaPesquisa = true;

	public void init() {
	 if(super.isValidInitMethod()){

		if (filtrocontrato == null) {
			filtrocontrato = returnFreshInstance();
		}

		if (isNovaPesquisa()) {
			pesquisar();
		}
	
	}}
	@SuppressWarnings({"PMD.CyclomaticComplexity","PMD.NPathComplexity"})
	public void pesquisar() {
		try {			
			setContSelecionado(null);
			if (hasAtLeastOneFilterSet(filtrocontrato)) {
				if ((filtrocontrato.getAf().getNumero() != null && filtrocontrato.getAf().getNroComplemento() == null)
						|| (filtrocontrato.getAf().getNumero() == null && filtrocontrato.getAf().getNroComplemento() != null)) {
					throw new ApplicationBusinessException(GerenciarContratosControllerExceptionCode.MENSAGEM_NROCP_OBRIGATORIO);
				}
			} else {
				throw new ApplicationBusinessException(GerenciarContratosControllerExceptionCode.MENSAGEM_SELECIONE_ALGUM_FILTRO);
			}

			List<ScoContrato> conts = siconFacade.obterContratoByFiltro(filtrocontrato);
			
			List<ContratoGridVO> grid = new ArrayList<ContratoGridVO>();
			AghParametros p = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PERC_CONTROLE_VLR_CTR);
			StringBuilder contTt = null;
			StringBuilder s = null;
			for (ScoContrato contrato : conts) {
				contTt = new StringBuilder();
				s = new StringBuilder();
				ContratoGridVO vo = new ContratoGridVO(contrato);

				BigDecimal v = new BigDecimal(StringUtils.replaceChars(p.getVlrTexto(), ",", "."));
				
				LOG.debug("++ BEGIN count materias pendentes de codigo sicon");
				Long count = this.siconFacade.siconPendentesCount(contrato);
				LOG.debug("++ END count materias pendentes de codigo sicon " + count);
				
				boolean hasPendenciasMateriaisSicon = count > 0 ? true : false;
				
				if (contrato.getIndOrigem() == DominioOrigemContrato.A) {
					vo.setFlagType(checkValEfetivado(contrato, v));
					// adicionando texto da tooltip para flags
					// 1 = flag laranja
					// 2 = flag vermelha
					if (vo.getFlagType() == 1) {

						StringBuilder valorFlag = new StringBuilder();
						valorFlag.append(getBundle().getString("MENSAGEM_TOOLTIP_FLAG_LARANJA1"))
						.append(' ');

						double percentual = contrato.getValEfetAfs().doubleValue() / contrato.getValorTotal().doubleValue();

						NumberFormat nf = NumberFormat.getPercentInstance();
						nf.setMaximumFractionDigits(3);
						nf.setMinimumFractionDigits(1);

						valorFlag.append(nf.format(percentual))
						.append(' ')
						.append(getBundle().getString("MENSAGEM_TOOLTIP_FLAG_LARANJA2"));

						vo.setFlagTooltip(valorFlag.toString());

					} else if (vo.getFlagType() == 2) {
						vo.setFlagTooltip(getBundle().getString("MENSAGEM_TOOLTIP_FLAG_VERMELHA"));
					}
				}
				if (contrato.getSituacao() == DominioSituacaoEnvioContrato.A || contrato.getSituacao() == DominioSituacaoEnvioContrato.AR) {
					// Contrato está aguardando envio ou reenvio (tabela
					// SCO_CONTRATOS, coluna IND_SITUACAO = ‘A’ ou IND_SITUACAO
					// = ‘AR’), não possui aditivos e não possui rescisão.
					// Contratos retornam da página Manter Contrato Automático
					// com getAditivos() = null
					if (contrato.getAditivos() == null && contrato.getRescicao() == null) {
						vo.setSitenvio(DominioSortableSitEnvioContrato.YELLOW);
						s.append(leadingChar + getBundle().getString("TT_CONTR_AGUARDANDO_ENVIO"));
						s.append(htmlLB);
						if (hasPendenciasMateriaisSicon || contrato.getDtAssinatura() == null) {
							if (hasPendenciasMateriaisSicon) {
								s.append(leadingChar + getBundle().getString("TT_NECESSARIO_RELACIONAR_M_S_COM_SICON"));
								s.append(htmlLB);
							}
							if (contrato.getDtAssinatura() == null) {
								s.append(leadingChar + getBundle().getString("TT_NECESSARIO_PREENCHER_DATA_ASS"));
							}
						} else {
							s.append(leadingChar + getBundle().getString("TT_SEM_PENDENCIAS_PARA_ENVIO"));
						}
					}
					if (contrato.getAditivos() != null) {
						if (contrato.getAditivos().size() == 0 && contrato.getRescicao() == null) {
							vo.setSitenvio(DominioSortableSitEnvioContrato.YELLOW);
							s.append(leadingChar + getBundle().getString("TT_CONTR_AGUARDANDO_ENVIO"));
							s.append(htmlLB);
							if (hasPendenciasMateriaisSicon || contrato.getDtAssinatura() == null) {
								if (hasPendenciasMateriaisSicon) {
									s.append(leadingChar + getBundle().getString("TT_NECESSARIO_RELACIONAR_M_S_COM_SICON"));
									s.append(htmlLB);
								}
								if (contrato.getDtAssinatura() == null) {
									s.append(leadingChar + getBundle().getString("TT_NECESSARIO_PREENCHER_DATA_ASS"));
								}
							} else {
								s.append(leadingChar + getBundle().getString("TT_SEM_PENDENCIAS_PARA_ENVIO"));
							}
						}
					}
				} else if (contrato.getSituacao() == DominioSituacaoEnvioContrato.E) {
					if (contrato.hasAditivosAguardandoEnvio() && contrato.getRescicao() == null) {
						vo.setSitenvio(DominioSortableSitEnvioContrato.YELLOW);
						s.append(leadingChar + getBundle().getString("TT_ADIT_AGUARDANDO_ENVIO"));
						s.append(htmlLB);
						if (hasPendenciasAditivos(contrato)) {
							s.append(leadingChar + getBundle().getString("TT_INFORMAR_DATA_ASS_ADIT"));
						} else {
							s.append(leadingChar + getBundle().getString("TT_ADIT_SEM_PENDENCIAS_PARA_ENVIO"));
						}
					} else if ((contrato.getAditivos().size() == 0 || contrato.isTodosAditivosEnviados()) && contrato.getRescicao() != null
							&& contrato.getRescicao().getIndSituacao() == DominioSituacaoEnvioContrato.A) {
						vo.setSitenvio(DominioSortableSitEnvioContrato.YELLOW);
						s.append(leadingChar + getBundle().getString("TT_RESCICAO_AGUARDANDO_ENVIO_SEM_PENDENCIAS"));
					} else if (contrato.getAditivos().size() == 0 && contrato.getRescicao() == null) {
						vo.setSitenvio(DominioSortableSitEnvioContrato.GREEN);
						s.append(leadingChar + getBundle().getString("TT_CONTRATO_ENVIADO_SEM_PENDENCIAS"));
					} else if (contrato.isTodosAditivosEnviados() && contrato.getRescicao() == null) {
						vo.setSitenvio(DominioSortableSitEnvioContrato.GREEN);
						s.append(leadingChar + getBundle().getString("TT_CONTRATO_E_ADITIVOS_ENVIADO_COM_SUCESSO"));
					} else if (contrato.isTodosAditivosEnviados() && contrato.getRescicao() != null) {
						if (DominioSituacaoEnvioContrato.E.equals(contrato.getRescicao().getIndSituacao())) {
							vo.setSitenvio(DominioSortableSitEnvioContrato.GREEN);
							s.append(leadingChar + getBundle().getString("TT_CONTRATO_E_ADITIVOS_E_RESC_ENVIADO_COM_SUCESSO"));
						} else if (DominioSituacaoEnvioContrato.EE.equals(contrato.getRescicao().getIndSituacao())) {
							vo.setSitenvio(DominioSortableSitEnvioContrato.RED);
							s.append(leadingChar + getBundle().getString("TT_RESC_ENVIADO_COM_ERRO"));
						} else {
							vo.setSitenvio(DominioSortableSitEnvioContrato.YELLOW);
							s.append(leadingChar + getBundle().getString("TT_RESCICAO_AGUARDANDO_ENVIO_SEM_PENDENCIAS"));
						}
					} else if (contrato.getAditivos().size() == 0 && contrato.getRescicao() != null
							&& contrato.getRescicao().getIndSituacao() == DominioSituacaoEnvioContrato.E) {
						vo.setSitenvio(DominioSortableSitEnvioContrato.GREEN);
						s.append(leadingChar + getBundle().getString("TT_CONTRATO_E_RESC_ENVIADO_COM_SUCESSO"));
					}
				} else if (contrato.getSituacao() == DominioSituacaoEnvioContrato.EE) {
					if (contrato.getAditivos().size() == 0 && contrato.getRescicao() == null) {
						vo.setSitenvio(DominioSortableSitEnvioContrato.RED);
						s.append(leadingChar + getBundle().getString("TT_CONTRATO_ENVIADO_COM_ERRO"));
					} else if (contrato.hasAditivosComErroEnvio() && contrato.getRescicao() == null) {
						vo.setSitenvio(DominioSortableSitEnvioContrato.RED);
						s.append(leadingChar + getBundle().getString("TT_ADIT_ENVIADO_COM_ERRO"));
					} else if ((contrato.isTodosAditivosEnviados() || contrato.getAditivos().size() == 0) && contrato.getRescicao() != null
							&& contrato.getRescicao().getIndSituacao() == DominioSituacaoEnvioContrato.EE) {
						vo.setSitenvio(DominioSortableSitEnvioContrato.RED);
						s.append(leadingChar + getBundle().getString("TT_RESC_ENVIADO_COM_ERRO"));
					}
				}

				else if (contrato.getAditivos().size() == 0 && isRescicaoCadastrado(contrato)
						&& contrato.getSituacao() == DominioSituacaoEnvioContrato.EE) {
					vo.setSitenvio(DominioSortableSitEnvioContrato.RED);
				}
				vo.setPendenciasTooltip(s.toString());
				contTt.append(getBundle().getString("LABEL_TIPO_CONTRATO"));
				contTt.append(doubleDot);
				contTt.append(contrato.getTipoContratoSicon().getDescricao());
				contTt.append(htmlLB);
				contTt.append(getBundle().getString("LABEL_ADITIVAR"));
				contTt.append(doubleDot);
				contTt.append(contrato.getIndAditivar().getDescricao());
				contTt.append(htmlLB);
				contTt.append(getBundle().getString("LABEL_ADIT"));
				contTt.append(doubleDot);
				if (contrato.getAditivos() != null) {
					contTt.append(String.valueOf(contrato.getAditivos().size()));
				} else {
					contTt.append(String.valueOf(0));
				}
				vo.setContratoTooltip(contTt.toString());
				grid.add(vo);
			}
			
			contratos = grid;

			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("filtrocontrato", filtrocontrato);
			// getSessionContext().set("filtrocontrato", filtrocontrato);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		setNovaPesquisa(false);
	}

	public void cancelarContrato() {
		try {
			siconFacade.deletarContrato(contSelecionado.getContrato());
			contratos.remove(contSelecionado);
			contSelecionado = null;
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CANCELAMENTO_CONTRATO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limpar() {
		filtrocontrato = returnFreshInstance();
		contratos.clear();
		contSelecionadoNro = null;
		contSelecionado = null;
	}

	/**
	 * Atribui valor para flag 0 - sem flag 1 - flag laranja 2 - flag vermelha
	 * 
	 * @param c
	 * @param vlrLim
	 * @return
	 */
	private int checkValEfetivado(ScoContrato c, BigDecimal vlrLim) {
		try {
			BigDecimal valEfet = c.getValEfetAfs();
			BigDecimal valTmp = c.getValorTotal().subtract(c.getValorTotal().multiply(vlrLim));
			if (valEfet.compareTo(c.getValorTotal()) == 1 || valEfet.compareTo(c.getValorTotal()) == 0) {
				return 2;
			}
			if (valEfet.compareTo(valTmp) >= 0 && valEfet.compareTo(c.getValorTotal()) < 0) {
				return 1;
			}
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			return 0;
		}
		return 0;
	}

	private boolean isRescicaoCadastrado(ScoContrato input) {
		boolean res = false;
		try {
			if (siconFacade.getRescicaoContrato(input) != null) {
				res = true;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return res;
	}

	private ContratoFiltroVO returnFreshInstance() {
		ContratoFiltroVO f = new ContratoFiltroVO(new ScoContrato(), new ScoAutorizacaoForn());
		f.setSitEnvAditivo(DominioSituacaoEnvioContrato.A);
		f.setSitEnvResc(DominioSituacaoEnvioContrato.A);
		f.getContrato().setSituacao(DominioSituacaoEnvioContrato.A);
		return f;
	}

	public List<ScoTipoContratoSicon> listarTiposContratoAtivos(final String pesquisa) {
		final List<ScoTipoContratoSicon> tiposContrato = cadastrosBasicosSiconFacade.listarTiposContrato(pesquisa);
		return tiposContrato;
	}

	public List<ScoModalidadeLicitacao> listarModalidadeLicitacaoAtivas(final String pesquisa) {
		final List<ScoModalidadeLicitacao> modalidades = comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(pesquisa);
		return modalidades;
	}

	public List<ScoFornecedor> listarFornecedoresAtivos(final String pesquisa) {
		final List<ScoFornecedor> fornecedores = comprasFacade.listarFornecedoresAtivos(pesquisa, 0, 100, null, false);
		return fornecedores;
	}

	public String getDescricaoForn() {
		if (this.filtrocontrato.getContrato() != null && this.filtrocontrato.getContrato().getFornecedor() != null) {
			if (this.filtrocontrato.getContrato().getFornecedor().getCgc() != null
					&& this.filtrocontrato.getContrato().getFornecedor().getCgc().intValue() != 0) {
				return CoreUtil.formatarCNPJ(this.filtrocontrato.getContrato().getFornecedor().getCgc()) + " - "
						+ this.filtrocontrato.getContrato().getFornecedor().getRazaoSocial();
			} else if (this.filtrocontrato.getContrato().getFornecedor().getCpf() != null
					&& this.filtrocontrato.getContrato().getFornecedor().getCpf().intValue() != 0) {
				return CoreUtil.formataCPF(this.filtrocontrato.getContrato().getFornecedor().getCpf()) + " - "
						+ this.filtrocontrato.getContrato().getFornecedor().getRazaoSocial();
			}
		}
		return "";
	}

	public List<ScoGrupoMaterial> pesquisarGrupoMateriais(String _input) {

		return this.comprasFacade.pesquisarGrupoMaterialPorFiltro(_input);
	}

	public List<ScoMaterial> pesquisarMateriais(String _input) {

		List<ScoMaterial> listaMaterial = null;
		try {
			listaMaterial = this.comprasFacade.listarScoMateriaisGrupoAtiva(_input, filtrocontrato.getGrupoMaterial(), false, false);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return listaMaterial;

	}

	public List<ScoServico> listarServicosAtivos(String pesquisa) {

		List<ScoServico> servicos = null;
		try {
			servicos = comprasFacade.listarServicosByNomeOrCodigoGrupoAtivo(pesquisa, filtrocontrato.getGrupoServico());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return servicos;
	}

	public List<ScoGrupoServico> listarGrupoServico(String pesquisa) {
		List<ScoGrupoServico> grupoServico = comprasFacade.listarGrupoServico(pesquisa);
		return grupoServico;
	}

	public List<ScoLicitacao> pesquisarLicitac(String param) {
		try {
			return siconFacade.listarLicitacoesAtivas(param);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public boolean isDisabledEditContBtn() {
		if (contSelecionado != null) {
			if (securityController.usuarioTemPermissao(GERENCIAR_CONTRATOS, HABILITAR)
					&& (contSelecionado.getContrato().getRescicao() == null || !DominioSituacaoEnvioContrato.E
							.equals(contSelecionado.getContrato().getRescicao().getIndSituacao()))) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public boolean isDisabledAditBtn() {
		if (contSelecionado != null) {
			if (securityController.usuarioTemPermissao(GERENCIAR_CONTRATOS, HABILITAR)
					&& DominioSituacaoEnvioContrato.E.equals(contSelecionado.getContrato().getSituacao())
					&& contSelecionado.getContrato().getRescicao() == null
					&& DominioSimNao.S.equals(contSelecionado.getContrato().getIndAditivar())) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public boolean isDisabledRescBtn() {
		if (contSelecionado != null) {
			if (securityController.usuarioTemPermissao(GERENCIAR_CONTRATOS, HABILITAR)
					&& DominioSituacaoEnvioContrato.E.equals(contSelecionado.getContrato().getSituacao()) && checkSitAditivos()) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public boolean isDisabledCancBtn() {
		if (contSelecionado != null) {
			if (securityController.usuarioTemPermissao(GERENCIAR_CONTRATOS, HABILITAR)
					&& DominioSituacaoEnvioContrato.A.equals(contSelecionado.getContrato().getSituacao())) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	private boolean checkSitAditivos() {
		if (contSelecionado.getContrato().getAditivos().size() > 0) {
			for (ScoAditContrato adit : contSelecionado.getContrato().getAditivos()) {
				if (DominioSituacaoEnvioContrato.E.equals(adit.getSituacao())) {
					continue;
				} else {
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
	}

	private void setContSelecionado() {
		for (ContratoGridVO vos : this.contratos) {
			if (vos.getContrato().getNrContrato().toString().equals(this.contSelecionadoNro)) {
				setContSelecionado(vos);
				break;
			} else {
				continue;
			}
		}
	}

	private boolean hasAtLeastOneFilterSet(ContratoFiltroVO input) {
		if (input.getContrato().getNrContrato() != null || input.getContrato().getTipoContratoSicon() != null
				|| input.getContrato().getDtInicioVigencia() != null || input.getContrato().getDtFimVigencia() != null
				|| input.getContrato().getFornecedor() != null || input.getContrato().getLicitacao() != null
				|| input.getAf().getNumero() != null || input.getAf().getNroComplemento() != null
				|| input.getContrato().getModalidadeLicitacao() != null || input.getContrato().getIndAditivar() != null
				|| input.getGrupoMaterial() != null || input.getMaterial() != null || input.getTipoItens() != null
				|| input.getGrupoServico() != null || input.getServico() != null || input.getEstocavel() != null
				|| input.getSitEnvAditivo() != null || input.getSitEnvResc() != null || input.getContrato().getSituacao() != null) {
			return true;
		} else {
			return false;
		}
	}

	private boolean hasPendenciasAditivos(ScoContrato input) {
		for (ScoAditContrato adit : input.getAditivos()) {
			if (adit.getDataAssinatura() == null && adit.getSituacao() == DominioSituacaoEnvioContrato.E) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	public String criarNovoContratoMan() {
		return PAGE_MANTER_CONTRATO_MANUAL;
	}

	public String criarNovoContratoAut() {
		return PAGE_ASSOCIAR_AF_CONTRATO;
	}

	public String editarContrato() {
		setNovaPesquisa(true);
		return PAGE_MANTER_CONTRATO_MANUAL;

	}

	public String editarContratoAutomatico() {
		setNovaPesquisa(true);
		return PAGE_MANTER_CONTRATO_AUTOMATICO;

	}

	public boolean isContratoManual() {
		if (this.contSelecionado != null) {
			if (DominioOrigemContrato.A.equals(this.contSelecionado.getContrato().getIndOrigem())) {
				return false;
			} else if (DominioOrigemContrato.M.equals(this.contSelecionado.getContrato().getIndOrigem())) {
				return true;
			}
		}
		return false;
	}

	public String aditivosContrato() {
		setNovaPesquisa(true);
		return PAGE_MANTER_ADITIVO_CONTRATUAL;
	}

	public String rescindirContrato() {
		return PAGE_MANTER_RESCISAO_CONTRATO;
	}

	public String relMat() {
		return PAGE_MANTER_MATERIAL_SICON;
	}

	public String relServ() {
		return PAGE_MANTER_SERVICO_SICON;
	}

	public String contratosFuturos() {
		return PAGE_PESQUISAR_CONTRATOS_FUTUROS;
	}

	public ContratoFiltroVO getFiltrocontrato() {
		return filtrocontrato;
	}

	public void setFiltrocontrato(ContratoFiltroVO filtrocontrato) {
		this.filtrocontrato = filtrocontrato;
	}

	public List<ContratoGridVO> getContratos() {
		return contratos;
	}

	public void setContratos(List<ContratoGridVO> contratos) {
		this.contratos = contratos;
	}

	public ContratoGridVO getContSelecionado() {
		return contSelecionado;
	}

	public void setContSelecionado(ContratoGridVO contSelecionado) {
		this.contSelecionado = contSelecionado;
	}

	public String getContSelecionadoNro() {
		return contSelecionadoNro;
	}

	public void setContSelecionadoNro(String contSelecionadoNro) {
		this.contSelecionadoNro = contSelecionadoNro;
		setContSelecionado();
	}

	public boolean isNovaPesquisa() {
		return novaPesquisa;
	}

	public void setNovaPesquisa(boolean novaPesquisa) {
		this.novaPesquisa = novaPesquisa;
	}

}
