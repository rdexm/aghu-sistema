package br.gov.mec.aghu.sicon.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;
import br.gov.mec.aghu.sicon.vo.ContratoGridVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe para o Controller do Gerenciamento de Integração para os Contratos
 * Sicon
 * 
 * @author agerling
 * 
 */
public class GerenciarIntegracaoSiconController extends ActionController {

	private static final long serialVersionUID = -4141293964812675117L;
	
	private static final String PAGE_ENVIO_CONTRATO_SIASG_SICON = "envioContratoSiasgSicon";
	private static final String PAGE_ENVIO_RESCISAO_SICON = "envioRescisaoSicon";
	private static final String PAGE_ENVIO_ADITIVOS = "envioAditivoSicon";
	private static final String PAGE_RETORNO_INTEGRACAO = "retornoIntegracao";

	private enum GerenciarContratosControllerExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_NROCP_OBRIGATORIO, MENSAGEM_SELECIONE_ALGUM_FILTRO;
	}

	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	@EJB
	private IComprasFacade comprasFacade;

	private ContratoFiltroVO filtroContratoIntegracao;

	private Integer tabSelecionada;

	@Inject
	private ContratoIntegracaoSiconController contratoController;

	@Inject
	private AditivoIntegracaoSiconController aditivoController;

	@Inject
	private RescisaoIntegracaoSiconController rescisaoController;
	
	@Inject
	private RetornoIntegracaoController retornoIntegracaoController;
	
	@Inject
	private EnvioContratoSiasgSiconController envioContratoSiasgSiconController;
	
	@Inject 
	private EnvioRescisaoSiconController envioRescisaoSiconController;
	
	@Inject
	private EnvioAditivoSiconController envioAditivoSiconController;

	// Utilizados na modal de integração e parâmetros para Envio XML
	private String valorAutentificacao;

	private Boolean solicitarAutenticacao;

	// Envio de Contrato
	private Integer seqContrato;
	private boolean indAfCont;

	// Envio de Aditivo
	private Integer seqAditivo;
	private Integer aditivoContSeq;
	
	private ContratoGridVO aditSelecionado;
	private ContratoGridVO rescSelecionada;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	public void iniciar() {
	 


		setSolicitarAutenticacao(false);

		if (filtroContratoIntegracao == null) {
			filtroContratoIntegracao = returnFiltroDefault();
		}

		try {
			pesquisaAbas();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	
	}

	public void pesquisaAbas() throws BaseException {
		try {
			if (!existeFiltro(filtroContratoIntegracao)) {
				throw new ApplicationBusinessException(
						GerenciarContratosControllerExceptionCode.MENSAGEM_SELECIONE_ALGUM_FILTRO);
			} else {
				if ((filtroContratoIntegracao.getAf().getNumero() != null && 
						filtroContratoIntegracao.getAf().getNroComplemento() == null)
				 || (filtroContratoIntegracao.getAf().getNumero() == null && 
				       filtroContratoIntegracao.getAf().getNroComplemento() != null)) {
					throw new ApplicationBusinessException(
							GerenciarContratosControllerExceptionCode.MENSAGEM_NROCP_OBRIGATORIO);
				}
			}

			limparAbas();

			contratoController.pesquisarContratos(filtroContratoIntegracao);

			aditivoController.pesquisarAditivos(filtroContratoIntegracao);

			rescisaoController.pesquisarRescisao(filtroContratoIntegracao);

			setTabSelecionada(0);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// Métodos utilizados nos campos de filtro da tela//
	public List<ScoLicitacao> pesquisarLicitacaoContrato(String param) {
		try {
			return siconFacade.listarLicitacoesAtivas(param);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	private ContratoFiltroVO returnFiltroDefault() {

		ContratoFiltroVO filtro = new ContratoFiltroVO(new ScoContrato(),
				new ScoAutorizacaoForn());

		filtro.getContrato().setSituacao(DominioSituacaoEnvioContrato.A);
		filtro.setSitEnvAditivo(DominioSituacaoEnvioContrato.A);
		filtro.setSitEnvResc(DominioSituacaoEnvioContrato.A);

		return filtro;
	}

	public List<ScoTipoContratoSicon> listarTodosTiposContratoAtivos(
			final String pesquisa) {
		final List<ScoTipoContratoSicon> tiposContrato = cadastrosBasicosSiconFacade
				.listarTodosTiposContratoAtivos(pesquisa);
		return tiposContrato;
	}

	public List<ScoFornecedor> listarFornecedoresAtivos(final String pesquisa) {
		final List<ScoFornecedor> fornecedores = comprasFacade
				.listarFornecedoresAtivos(pesquisa, 0, 100, null, false);
		return fornecedores;
	}

	public String getDescricaoForn() {

		if (this.filtroContratoIntegracao != null
				&& this.filtroContratoIntegracao.getContrato() != null
				&& this.filtroContratoIntegracao.getContrato().getFornecedor() != null) {

			if (this.filtroContratoIntegracao.getContrato().getFornecedor()
					.getCgc() != null
					&& this.filtroContratoIntegracao.getContrato()
							.getFornecedor().getCgc().intValue() != 0) {

				return CoreUtil.formatarCNPJ(this.filtroContratoIntegracao
						.getContrato().getFornecedor().getCgc())
						+ " - "
						+ this.filtroContratoIntegracao.getContrato()
								.getFornecedor().getRazaoSocial();

			} else if (this.filtroContratoIntegracao.getContrato()
					.getFornecedor().getCpf() != null
					&& this.filtroContratoIntegracao.getContrato()
							.getFornecedor().getCpf().intValue() != 0) {

				return CoreUtil.formataCPF(this.filtroContratoIntegracao
						.getContrato().getFornecedor().getCpf())
						+ " - "
						+ this.filtroContratoIntegracao.getContrato()
								.getFornecedor().getRazaoSocial();
			}
		}

		return null;
	}

	public List<RapServidores> pesquisarServidorAtivoGestor(
			final String paramPesquisa) {
		try {
			return siconFacade
					.pesquisarServidorAtivoGestorEFiscalContrato(paramPesquisa);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public List<RapServidores> pesquisarServidorAtivoFiscal(
			final String paramPesquisa) {
		try {
			return siconFacade
					.pesquisarServidorAtivoGestorEFiscalContrato(paramPesquisa);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private boolean existeFiltro(ContratoFiltroVO filtro) {

		if (filtro.getContrato().getNrContrato() != null
				|| filtro.getContrato().getTipoContratoSicon() != null
				|| filtro.getContrato().getDtInicioVigencia() != null
				|| filtro.getContrato().getDtFimVigencia() != null
				|| filtro.getContrato().getFornecedor() != null
				|| filtro.getContrato().getLicitacao() != null
				|| filtro.getAf().getNumero() != null
				|| filtro.getAf().getNroComplemento() != null
				|| filtro.getContrato().getModalidadeLicitacao() != null
				|| filtro.getContrato().getIndAditivar() != null
				|| filtro.getSitEnvAditivo() != null
				|| filtro.getSitEnvResc() != null
				|| filtro.getContrato().getSituacao() != null
				|| filtro.getContrato().getServidorGestor() != null
				|| filtro.getContrato().getServidorFiscal() != null) {

			return true;

		} else {
			return false;
		}
	}

	public void limparAbas() {
		contratoController.limpar();
		aditivoController.limpar();
		rescisaoController.limpar();
	}

	public void limparConsulta() {

		filtroContratoIntegracao = new ContratoFiltroVO(new ScoContrato(),
				new ScoAutorizacaoForn());
		seqContrato = null;

		limparAbas();
	}

	public String autenticarOperacoesSicon() {

		if (StringUtils.isBlank(valorAutentificacao)) {
			setSolicitarAutenticacao(true);
		} else {
			setSolicitarAutenticacao(false);

			if (contratoController.getContSelecionado() != null) {
				return enviarContrato();
			}

			if (aditivoController.getAditSelecionado() != null) {
				return enviarAditivo();
			}

			if (rescisaoController.getRescSelecionada() != null) {
				return enviarRescisao();
			}
		}

		return null;

	}

	// INTEGRAÇÃO --> CONTRATOS

	public String enviarContrato() {

		if (contratoController.getContSelecionado() != null
				&& contratoController.getContSelecionado().getContrato() != null
				&& contratoController.getContSelecionado().getContrato()
						.getSeq() != null) {

			if (StringUtils.isBlank(valorAutentificacao)) {

				setSolicitarAutenticacao(true);

				aditivoController.setAditSelecionado(null);

				rescisaoController.setRescSelecionada(null);

				return null;

			} else {
				// Parâmetros para o processo de Envio de Contrato
				seqContrato = contratoController.getContSelecionado()
						.getContrato().getSeq();

				if (contratoController.getContSelecionado().getFlagType() == 2) {
					indAfCont = true;
				} else {
					indAfCont = false;
				}
			}
			
			envioContratoSiasgSiconController.setContratoSeq(seqContrato);
			envioContratoSiasgSiconController.setIndAf(indAfCont);
			envioContratoSiasgSiconController.setAutenticacaoSicon(valorAutentificacao);

		} else {
			this.apresentarMsgNegocio(Severity.ERROR,
					"MSG_SELECIONAR_CONTRATO");
			return null;
		}

		return PAGE_ENVIO_CONTRATO_SIASG_SICON;
	}

	public String detalharContrato() {
		return null;
	}

	public String retornoIntegracaoContrato() {

		if (contratoController.getContSelecionado() != null
				&& contratoController.getContSelecionado().getContrato() != null
				&& contratoController.getContSelecionado().getContrato()
						.getSeq() != null) {

			seqContrato = contratoController.getContSelecionado().getContrato()
					.getSeq();
			retornoIntegracaoController.setContSeq(seqContrato);

		} else {
			this.apresentarMsgNegocio(Severity.ERROR,
					"MSG_SELECIONAR_CONTRATO_INTEGRACAO");
			return null;

		}

		return PAGE_RETORNO_INTEGRACAO;
	}

	// INTEGRAÇÃO --> ADITIVOS

	public String enviarAditivo() {

		if (aditivoController.getAditSelecionado() != null
				&& aditivoController.getAditSelecionado().getAditContrato() != null
				&& aditivoController.getAditSelecionado().getAditContrato()
						.getId() != null) {

			if (StringUtils.isBlank(valorAutentificacao)) {

				setSolicitarAutenticacao(true);

				contratoController.setContSelecionado(null);

				rescisaoController.setRescSelecionada(null);

				return null;

			} else {
				// Parâmetros para o processo de Envio de Aditivo
				seqAditivo = aditivoController.getAditSelecionado()
						.getAditContrato().getId().getSeq();
				aditivoContSeq = aditivoController.getAditSelecionado()
						.getAditContrato().getId().getContSeq();

				envioAditivoSiconController.setSeqContrato(aditivoContSeq);
				envioAditivoSiconController.setSeqAditivo(seqAditivo);
				envioAditivoSiconController.setAutenticacao(this.valorAutentificacao);
			}
			
			
			
			
			envioContratoSiasgSiconController.setContratoSeq(seqContrato);
			envioContratoSiasgSiconController.setIndAf(indAfCont);

		} else {
			this.apresentarMsgNegocio(Severity.ERROR,
					"MSG_SELECIONAR_ADITIVO");
			return null;
		}

		return PAGE_ENVIO_ADITIVOS;
	}

	public String detalharAditivo() {
		return null;
	}

	public String retornoIntegracaoAditivo() {

		if (aditivoController.getAditSelecionado() != null
				&& aditivoController.getAditSelecionado().getAditContrato() != null
				&& aditivoController.getAditSelecionado().getAditContrato()
						.getId() != null) {

			seqContrato = aditivoController.getAditSelecionado()
					.getAditContrato().getId().getContSeq();
			
			retornoIntegracaoController.setContSeq(seqContrato);

		} else {
			this.apresentarMsgNegocio(Severity.ERROR,
					"MSG_SELECIONAR_ADITIVO_INTEGRACAO");
			return null;
		}

		return PAGE_RETORNO_INTEGRACAO;
	}

	// INTEGRAÇÃO --> RESCISÕES

	public String enviarRescisao() {
		if (getRescSelecionada() != null
				&& getRescSelecionada().getContrato() != null
				&& getRescSelecionada().getContrato()
						.getSeq() != null) {

			if (StringUtils.isBlank(valorAutentificacao)) {

				setSolicitarAutenticacao(true);

				contratoController.setContSelecionado(null);

				aditivoController.setAditSelecionado(null);

				return null;

			} else {
				// Parâmetros para o processo de Envio de Contrato
				seqContrato = getRescSelecionada()
						.getContrato().getSeq();
				envioRescisaoSiconController.setContratoSeq(seqContrato);
				envioRescisaoSiconController.setAutenticacaoSicon(valorAutentificacao);
			}
			
//			envioContratoSiasgSiconController.setContratoSeq(seqContrato);
//			envioContratoSiasgSiconController.setIndAf(indAfCont);

		} else {
			this.apresentarMsgNegocio(Severity.ERROR,
					"MSG_SELECIONAR_RESCISAO");
			return null;
		}

		return PAGE_ENVIO_RESCISAO_SICON;
	}

	public String detalharRescisao() {
		return null;
	}

	public String retornoIntegracaoRescisao() {

		if (getRescSelecionada() != null
				&& getRescSelecionada().getContrato() != null
				&& getRescSelecionada().getContrato()
						.getSeq() != null) {

			seqContrato =getRescSelecionada().getContrato()
					.getSeq();
			
			retornoIntegracaoController.setContSeq(seqContrato);

		} else {
			this.apresentarMsgNegocio(Severity.ERROR,
					"MSG_SELECIONAR_RESCISAO_INTEGRACAO");
			return null;
		}

		return PAGE_RETORNO_INTEGRACAO;
	}

	// Getters and Setters
	public ContratoFiltroVO getFiltroContratoIntegracao() {
		return filtroContratoIntegracao;
	}

	public void setFiltroContratoIntegracao(
			ContratoFiltroVO filtroContratoIntegracao) {
		this.filtroContratoIntegracao = filtroContratoIntegracao;
	}

	public Integer getTabSelecionada() {
		return tabSelecionada;
	}

	public void setTabSelecionada(Integer tabSelecionada) {
		this.tabSelecionada = tabSelecionada;
	}

	public ContratoIntegracaoSiconController getContratoController() {
		return contratoController;
	}

	public void setContratoController(
			ContratoIntegracaoSiconController contratoController) {
		this.contratoController = contratoController;
	}

	public AditivoIntegracaoSiconController getAditivoController() {
		return aditivoController;
	}

	public void setAditivoController(
			AditivoIntegracaoSiconController aditivoController) {
		this.aditivoController = aditivoController;
	}

	public RescisaoIntegracaoSiconController getRescisaoController() {
		return rescisaoController;
	}

	public void setRescisaoController(
			RescisaoIntegracaoSiconController rescisaoController) {
		this.rescisaoController = rescisaoController;
	}

	public String getValorAutentificacao() {
		return valorAutentificacao;
	}

	public void setValorAutentificacao(String valorAutentificacao) {
		this.valorAutentificacao = valorAutentificacao;
	}

	public Boolean getSolicitarAutenticacao() {
		return solicitarAutenticacao;
	}

	public void setSolicitarAutenticacao(Boolean solicitarAutenticacao) {
		this.solicitarAutenticacao = solicitarAutenticacao;
	}

	public Integer getSeqContrato() {
		return seqContrato;
	}

	public void setSeqContrato(Integer seqContrato) {
		this.seqContrato = seqContrato;
	}

	public boolean isIndAfCont() {
		return indAfCont;
	}

	public void setIndAfCont(boolean indAfCont) {
		this.indAfCont = indAfCont;
	}

	public Integer getSeqAditivo() {
		return seqAditivo;
	}

	public void setSeqAditivo(Integer seqAditivo) {
		this.seqAditivo = seqAditivo;
	}

	public Integer getAditivoContSeq() {
		return aditivoContSeq;
	}

	public void setAditivoContSeq(Integer aditivoContSeq) {
		this.aditivoContSeq = aditivoContSeq;
	}
	
	public Integer getSeqAditSelecionado() {
		return aditivoController.getSeqAditSelecionado();
	}

	public void setSeqAditSelecionado(Integer seqAditSelecionado) {
		aditivoController.setSeqAditSelecionado(seqAditSelecionado);
		aditivoController.setAditSelecionado(aditSelecionado);
	}
	
	public String getContSelecionadoNro() {
		return contratoController.getContSelecionadoNro();
	}

	public void setContSelecionadoNro(String contSelecionadoNro) {
		this.contratoController.setContSelecionadoNro(contSelecionadoNro);		
	}
		

	public ContratoGridVO getAditSelecionado() {
		return aditSelecionado;
	}

	public void setAditSelecionado(ContratoGridVO aditSelecionado) {
		this.aditSelecionado = aditSelecionado;		
		aditivoController.setAditSelecionado(aditSelecionado);
		
	}

	public ContratoGridVO getRescSelecionada() {
		return rescSelecionada;
	}

	public void setRescSelecionada(ContratoGridVO rescSelecionada) {
		this.rescSelecionada = rescSelecionada;
		rescisaoController.setRescSelecionada(rescSelecionada);
	}

}
