package br.gov.mec.aghu.compras.solicitacaoservico.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.orcamento.business.IOrcamentoFacade;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class SolicitacaoServicoController extends ActionController {

	private static final String SOLICITACAO_SERVICO_CRUD = "solicitacaoServicoCRUD";

	private static final long serialVersionUID = 9041577601091390580L;

	private static final String PAGE_FASES_SOLICITACAO_SERVICO_LIST = "fasesSolicitacaoServicoList";
	private static final String PAGE_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";
	private static final String PAGE_ASSOCIAR_SOLICITACAO_SERVICO_COMPRA = "associarSolicitacaoServicoCompra";
	private static final String PAGE_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";
	//private static final String PAGE_SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private IOrcamentoFacade orcamentoFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	protected IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

	private Integer numero; // numero da solicitacao

	private ScoSolicitacaoServico solicitacaoServico;
	private FccCentroCustos centroCusto;
	private FccCentroCustos centroCustoAplicada;
	private FsoGrupoNaturezaDespesa grupoNaturezaDespesa;
	private Boolean isVerbaGestaoReadonly;
	private Boolean isGrupoNaturezaReadonly;
	private Boolean isNaturezaReadonly;
	private boolean habilitaDesabilitaDtAtendimento;
	// private ScoPontoParadaSolicitacao pontoParadaDevolucao;
	
	private boolean chkCcSolic;
	private boolean chkCcAplic;
	private boolean chkCcOriginal;
	private Boolean exibeModalPreferencia;
	private ScoCaracteristicaUsuarioCentroCusto mantemCcOriginal;
	private ScoCaracteristicaUsuarioCentroCusto sugereCcSolic;
	private ScoCaracteristicaUsuarioCentroCusto sugereCcAplic;

	// indica para onde o botao voltar deve redirecionar
	private String voltarParaUrl;

	private String gravarVoltarUrl;

	private boolean isExibeModalEncaminhar = false;

	// modal de encaminhamento
	private ScoPontoParadaSolicitacao proximoPontoParada;
	private RapServidores funcionarioComprador;
	private Boolean desabilitaSuggestionComprador;
	private Boolean voltarPanel;
	private Boolean exibeModalDuplicar;

	// private Boolean isCentroCustoAplicadaUpdated;
	private ScoSolicitacaoServico solicitacaoServicoClone;

	/** Último serviço selecionado. */
	private ScoServico lastServico;

	private Boolean habEncaminharSS;
	private Boolean habAutorizarSS;
	private Boolean habBtAnexo;
	private Boolean edicaoArquivo;
	private Boolean rdEdicao;
	
	public enum ManterSolicitacaoServicoControllerExceptionCode implements BusinessExceptionCode {
		SOLICITACAO_SERVICO_INCLUIDA_COM_SUCESSO, SOLICITACAO_SERVICO_ALTERADA_COM_SUCESSO,
		SOLICITACAO_SERVICO_DUPLICADA_COM_SUCESSO, CCUSTO_APLIC_INST_GERAIS_APENAS_MAT_INST, CCUSTO_APLIC_APENAS_OBR_AND;
	}

	public static enum ControlaCheck {
		URGENTE, PRIORITARIO
	};

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() throws ApplicationBusinessException {	 


		Boolean isUpdate = numero != null;		

		this.sugereCcSolic = this.comprasCadastrosBasicosFacade.obterCaracteristica(DominioCaracteristicaCentroCusto.PREENCHER_CC_SOLIC_SS);
		this.sugereCcAplic = this.comprasCadastrosBasicosFacade.obterCaracteristica(DominioCaracteristicaCentroCusto.PREENCHER_CC_APLIC_SS);
		this.mantemCcOriginal = this.comprasCadastrosBasicosFacade.obterCaracteristica(DominioCaracteristicaCentroCusto.MANTER_CC_DUPLIC_SS);
		
		if (this.sugereCcSolic != null) {
			this.chkCcSolic = true;
		} else {
			this.chkCcSolic = false;
		}
		if (this.sugereCcAplic != null) {
			this.chkCcAplic = true;
		} else {
			this.chkCcAplic = false;
		}
		if (this.mantemCcOriginal != null) {
			this.chkCcOriginal = true;
		} else {
			this.chkCcOriginal = false;
		}
		
		if (isUpdate) {
			try {
				this.exibeModalPreferencia = false;
				solicitacaoServico = solicitacaoServicoFacade.obterSolicitacaoServico(numero);
				this.setSolicitacaoServicoClone(solicitacaoServicoFacade.clonarSolicitacaoServico(solicitacaoServico));
				
				
				centroCusto = solicitacaoServico.getCentroCusto();
				centroCustoAplicada = solicitacaoServico.getCentroCustoAplicada();

				if (solicitacaoServico.getNaturezaDespesa() != null) {
					grupoNaturezaDespesa = solicitacaoServico.getNaturezaDespesa().getGrupoNaturezaDespesa();
				} else {
					grupoNaturezaDespesa = null;
				}

				habEncaminharSS = this.habilitarEncaminharSS();
				habAutorizarSS = this.habilitarAutorizarSS();
				habBtAnexo = this.habilitarBtAnexo();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {

			/* centro de custo do usuario logado */
			centroCusto = centroCustoFacade.pesquisarCentroCustoAtuacaoLotacaoServidor();

			centroCustoAplicada = centroCusto;
			if (!chkCcSolic) {
				centroCusto = null;
			}
			if (!chkCcAplic) {
				centroCustoAplicada = null;
			}

			solicitacaoServico = new ScoSolicitacaoServico();
			solicitacaoServico.setIndPrioridade(false);
			solicitacaoServico.setIndExclusao(false);
			solicitacaoServico.setIndUrgente(false);
			solicitacaoServico.setIndDevolucao(false);
			solicitacaoServico.setIndEfetivada(false);
			solicitacaoServico.setDtSolicitacao(new Date());
			solicitacaoServico.setDtDigitacao(new Date());
		}
		
		this.rdEdicao = this.isReadonlyEdicao();
		refreshParametrosOrcamento(!isUpdate);
	
	}
	

	public String pesqFon() {
		return "pesquisaFonetica";
	}


	public boolean isReadonlyEdicao() {
		if(solicitacaoServico != null){
			return solicitacaoServicoFacade.isReadonlyEdicao(solicitacaoServico);
		}
		return false;
	}

	public void validaUrgente() {
		validaUrgentePrioritario(ControlaCheck.URGENTE.toString());
	}

	public void validaPrioritario() {

		validaUrgentePrioritario(ControlaCheck.PRIORITARIO.toString());
	}

	public void limpaDtMaxAtendimento() {

		if (this.isHabilitaDesabilitaDtAtendimento()) {
			solicitacaoServico.setDtMaxAtendimento(null);
		}

	}
	
	public void atualizarPreferenciaSugestaoCc(Boolean isAplic, Boolean isDuplic) {
		if (isDuplic) {
			if (this.mantemCcOriginal == null) {
				this.mantemCcOriginal = this.comprasCadastrosBasicosFacade.montarScoCaracUsuario(DominioCaracteristicaCentroCusto.MANTER_CC_DUPLIC_SS);
				try {
					this.comprasCadastrosBasicosFacade.inserirCaracteristicaUserCC(this.mantemCcOriginal);
				} catch (ApplicationBusinessException e) {
					this.mantemCcOriginal = null;
					this.chkCcOriginal = false;
					this.apresentarExcecaoNegocio(e);
				}
			} else {
				try {
					this.comprasCadastrosBasicosFacade.excluirCaracteristicaUserCC(this.mantemCcOriginal);
					this.mantemCcOriginal = null;
				} catch (ApplicationBusinessException e) {
					this.apresentarExcecaoNegocio(e);
					this.chkCcOriginal = true;
				}
			}
		} else {
			if (isAplic) {
				if (this.sugereCcAplic == null) {
					this.sugereCcAplic = this.comprasCadastrosBasicosFacade.montarScoCaracUsuario(DominioCaracteristicaCentroCusto.PREENCHER_CC_APLIC_SS);
					try {
						this.comprasCadastrosBasicosFacade.inserirCaracteristicaUserCC(this.sugereCcAplic);
					} catch (ApplicationBusinessException e) {
						this.sugereCcAplic = null;
						this.setChkCcAplic(false);
						this.apresentarExcecaoNegocio(e);
					}
				} else {
					try {
						this.comprasCadastrosBasicosFacade.excluirCaracteristicaUserCC(this.sugereCcAplic);
						this.sugereCcAplic = null;
					} catch (ApplicationBusinessException e) {
						this.apresentarExcecaoNegocio(e);
						this.setChkCcAplic(true);
					}
				}
			} else {
				if (this.sugereCcSolic == null) {
					this.sugereCcSolic = this.comprasCadastrosBasicosFacade.montarScoCaracUsuario(DominioCaracteristicaCentroCusto.PREENCHER_CC_SOLIC_SS);
					try {
						this.comprasCadastrosBasicosFacade.inserirCaracteristicaUserCC(this.sugereCcSolic);
					} catch (ApplicationBusinessException e) {
						this.sugereCcSolic = null;
						this.setChkCcSolic(false);
						this.apresentarExcecaoNegocio(e);
					}
				} else {
					try {
						this.comprasCadastrosBasicosFacade.excluirCaracteristicaUserCC(this.sugereCcSolic);
						this.sugereCcSolic = null;
					} catch (ApplicationBusinessException e) {
						this.apresentarExcecaoNegocio(e);
						this.setChkCcSolic(true);
					}
				}
			}
		}
	}

	public void validaUrgentePrioritario(String acaoEnum) {
		try {
			this.solicitacaoServicoFacade.validaUrgentePrioritario(solicitacaoServico);
		} catch (final BaseException e) {
			if (ControlaCheck.URGENTE.toString().equals(acaoEnum)) {
				solicitacaoServico.setIndUrgente(false);
			}
			if (ControlaCheck.PRIORITARIO.toString().equals(acaoEnum)) {
				solicitacaoServico.setIndPrioridade(false);
			}
			apresentarExcecaoNegocio(e);
		}
	}

	public void validaUrgentePrioritario(ScoSolicitacaoServico solicitacaoServico) throws ApplicationBusinessException {
		solicitacaoServicoFacade.validaUrgentePrioritario(solicitacaoServico);
	}

	public void validaExclusivo() {
		if (!solicitacaoServico.getIndExclusivo()) {
			solicitacaoServico.setJustificativaExclusividade(null);
		}
	}

	public void validaExcluidoDevolvido() {

		if (!solicitacaoServico.getIndDevolucao()) {
			solicitacaoServico.setJustificativaDevolucao(null);
		}
		if (!solicitacaoServico.getIndExclusao()) {
			solicitacaoServico.setMotivoExclusao(null);
		}
	}

	public Boolean isHabilitaDesabilitaDtAtendimento() {

		if (solicitacaoServico != null) {
			boolean flagUrgente = false;
			boolean flagPrioridade = false;
			boolean flagExclusivo = false;

			flagUrgente = (solicitacaoServico.getIndUrgente() != null ? solicitacaoServico.getIndUrgente() : false);
			flagPrioridade = (solicitacaoServico.getIndPrioridade() != null ? solicitacaoServico.getIndPrioridade() : false);
			flagExclusivo = (solicitacaoServico.getIndExclusivo() != null ? solicitacaoServico.getIndExclusivo() : false);

			this.habilitaDesabilitaDtAtendimento = !(flagUrgente || flagPrioridade || flagExclusivo);

			if (habilitaDesabilitaDtAtendimento) {
				solicitacaoServico.setDtMaxAtendimento(null);
			}
		}
		return this.habilitaDesabilitaDtAtendimento;

	}

	public void setHabilitaDesabilitaDtAtendimento(boolean habilitaDesabilitaDtAtendimento) {
		this.habilitaDesabilitaDtAtendimento = habilitaDesabilitaDtAtendimento;
	}

	public String gravar() {
		try {
			boolean edicao = false;
			if (solicitacaoServico.getNumero() != null) {
				edicao = true;
			}

			solicitacaoServico.setCentroCusto(centroCusto);
			solicitacaoServico.setCentroCustoAplicada(centroCustoAplicada);

			if (solicitacaoServico.getServidorComprador() != null && solicitacaoServico.getServidorComprador().getId() != null) {
				solicitacaoServico.setServidorComprador(solicitacaoServico.getServidorComprador());
			}

			solicitacaoServicoFacade.persistirSolicitacaoDeServico(solicitacaoServico, this.getSolicitacaoServicoClone());

			if (!edicao) {
				this.setNumero(solicitacaoServico.getNumero());
				this.apresentarMsgNegocio(Severity.INFO,
						ManterSolicitacaoServicoControllerExceptionCode.SOLICITACAO_SERVICO_INCLUIDA_COM_SUCESSO.toString(),
						solicitacaoServico.getNumero());
			} else {
				this.apresentarMsgNegocio(Severity.INFO,
						ManterSolicitacaoServicoControllerExceptionCode.SOLICITACAO_SERVICO_ALTERADA_COM_SUCESSO.toString(),
						solicitacaoServico.getNumero());
			}

			habBtAnexo = true;
			this.setSolicitacaoServicoClone(solicitacaoServicoFacade.clonarSolicitacaoServico(solicitacaoServico));

			if (StringUtils.isBlank(getGravarVoltarUrl())) {
				habEncaminharSS = this.habilitarEncaminharSS();
				return null;
			} else {
				return voltarParaUrl;
			}
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String voltar() {
		numero = null;
		return voltarParaUrl;
	}

	public List<FccCentroCustos> listarCentroCustosSolic(String objPesquisa) throws ApplicationBusinessException {
		if (this.solicitacaoServicoFacade.verificaPemissaoUsuario("cadastrarSSComprador", this.obterLoginUsuarioLogado())
				|| this.solicitacaoServicoFacade.verificaPemissaoUsuario("cadastrarSSPlanejamento", this.obterLoginUsuarioLogado())) {
			return this.centroCustoFacade.pesquisarCentroCustosAtivos(objPesquisa);
		} else {
			return centroCustoFacade.pesquisarCentroCustoUsuarioGerarSSSuggestion(objPesquisa);

		}
	}
	
	public void atualizarValorUnitPrevisto() throws ApplicationBusinessException {
		/*
		 * if (solicitacaoServico.getServico() != null) { Double ultimoValor =
		 * solicitacaoServicoFacade
		 * .getUltimoValorCompra(solicitacaoServico.getServico());
		 * 
		 * if (ultimoValor != null && ultimoValor > 0 ) {
		 * solicitacaoServico.setValorUnitPrevisto(new BigDecimal(
		 * ultimoValor)); }
		 * 
		 * atualizarValorTotal();
		 * 
		 * }
		 */
	}

	public Long listarCentroCustosSolicCount(Object objPesquisa) throws ApplicationBusinessException {
		return this.centroCustoFacade.pesquisarCentroCustosServidorCount(objPesquisa);
	}

	public Long listarCentroCustosAplicCount(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosCount(objPesquisa);
	}

	public List<ScoServico> listarServicosAtivos(String param) throws ApplicationBusinessException {

		return  this.returnSGWithCount(this.solicitacaoServicoFacade.listarServicosAtivos(param),listarServicosAtivosCount(param));
	}

	public Long listarServicosAtivosCount(String param) throws ApplicationBusinessException {
		if (this.solicitacaoServicoFacade.verificaPemissaoUsuario("cadastrarSSEngenharia", this.obterLoginUsuarioLogado())) {
			return this.solicitacaoServicoFacade.listarServicosAtivosEngenhariaCount(param, "S");
		}
		return this.solicitacaoServicoFacade.listarServicosAtivosEngenhariaCount(param, "N");
	}

	public List<FsoConveniosFinanceiro> listarConvenioFinanceiro(Object param) {
		return this.orcamentoFacade.listarConvenios(param);
	}

	public Long listarConvenioFinanceiroCount(Object param) {
		return this.orcamentoFacade.listarConveniosCount(param);
	}

	public List<FsoNaturezaDespesa> listarNaturezaDespesa(Object objPesquisa) {
		return this.orcamentoFacade.listarNaturezaDespesa(objPesquisa);
	}

	public Long listarNaturezaDespesaCount(Object objPesquisa) {
		return this.orcamentoFacade.listarNaturezaDespesaCount(objPesquisa);
	}

	/**
	 * SuggestionBox CentroCusto Solicitante
	 * 
	 * @param paramPesquisa
	 * @param servidor
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSCSuggestion(Object paramPesquisa) {

		return centroCustoFacade.pesquisarCentroCustoUsuarioGerarSCSuggestion(paramPesquisa);
	}

	/**
	 * Atualiza campos restringidos pelos parâmetros orçamentários.
	 */
	public void refreshParametrosOrcamento() {
		refreshParametrosOrcamento(true);
	}

	public void abrirNovo() throws ApplicationBusinessException {
		this.numero = null;
		this.centroCusto = null;
		this.inicio();
	}

	/**
	 * Atualiza campos restringidos pelos parâmetros orçamentários.
	 * 
	 * @param suggest Indica se deve encontrar parâmetro sugerido ou obrigatório.
	 */
	public void refreshParametrosOrcamento(Boolean suggest) {
		Boolean regLast = true;
		
		// Serviço foi informado.
		if (solicitacaoServico.getServico() != null) {
			// Serviço foi alterado.
			if (!solicitacaoServico.getServico().equals(lastServico)) {				
				// Grupo de Natureza
				isGrupoNaturezaReadonly = cadastrosBasicosOrcamentoFacade
						.hasUniqueRequiredGrupoNaturezaSsParam(solicitacaoServico.getServico());
				
				// Verba de gestão
				isVerbaGestaoReadonly = cadastrosBasicosOrcamentoFacade
						.hasUniqueRequiredVerbaGestaoSsParam(solicitacaoServico.getServico());
				
				if (Boolean.TRUE.equals(suggest)) {
					grupoNaturezaDespesa = cadastrosBasicosOrcamentoFacade
							.getGrupoNaturezaSsParam(solicitacaoServico.getServico());

					solicitacaoServico
							.setVerbaGestao(cadastrosBasicosOrcamentoFacade
									.getVerbaGestaoSsParam(solicitacaoServico.getServico()));
				}
				
				// Natureza
				refreshNaturezaDespesa(suggest);

				lastServico = solicitacaoServico.getServico();
				regLast = false;
			}
			// Os valores a serem preenchidos com os parâmetros orçamentários
			// são resetados.
		} else {			
			grupoNaturezaDespesa = null;
			isGrupoNaturezaReadonly = true; 
			
			solicitacaoServico.setNaturezaDespesa(null);
			isNaturezaReadonly = true;
			
			solicitacaoServico.setVerbaGestao(null);			
			isVerbaGestaoReadonly = true;
		}
		
		if (regLast) {
			lastServico = solicitacaoServico.getServico();
		}
	}

	/**
	 * Verifica se grupo de natureza possui um único parâmetro, sendo este
	 * obrigatório.
	 * 
	 * @return Flag para habilitar ou desabilitar o campo.
	 */
	public Boolean isGrupoNaturezaReadonly() {
		return isGrupoNaturezaReadonly;
	}

	/**
	 * Verifica se natureza possui um único parâmetro, sendo este obrigatório.
	 * 
	 * @return Flag para habilitar ou desabilitar o campo.
	 */
	public Boolean isNaturezaReadonly() {
		return isNaturezaReadonly;
	}

	/**
	 * Verifica se verba de gestão possui um único parâmetro, sendo este
	 * obrigatório.
	 * 
	 * @return Flag para habilitar ou desabilitar o campo.
	 */
	public Boolean isVerbaGestaoReadonly() {
		return isVerbaGestaoReadonly;
	}

	public List<FccCentroCustos> listarCentroCustosAplic(String objPesquisa) {
		return  this.returnSGWithCount(this.centroCustoFacade.pesquisarCentroCustosAtivos(objPesquisa),listarCentroCustosAplicCount(objPesquisa));
	}

	/**
	 * Obtem grupos de natureza disponí­veis.
	 * 
	 * @param filter
	 * @return Grupos de natureza disponí­veis.
	 */
	public List<FsoGrupoNaturezaDespesa> pesquisarGruposNaturezaDespesa(String filter) {
		return cadastrosBasicosOrcamentoFacade.listarGruposNaturezaSsParams(solicitacaoServico.getServico(), filter);
	}

	/**
	 * Obtem naturezas de despesa disponí­veis.
	 * 
	 * @param filter
	 * @return Naturezas de despesa disponí­veis.
	 */
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesa(String filter) {
		return cadastrosBasicosOrcamentoFacade.listarNaturezaSsParams(solicitacaoServico.getServico(), grupoNaturezaDespesa, filter);
	}

	/**
	 * Obtem verbas de gestão disponí­veis.
	 * 
	 * @param filter
	 *            Filtro.
	 * @return Verbas de gestão.
	 */
	public List<FsoVerbaGestao> pesquisarVerbasGestao(String filter) {
		return cadastrosBasicosOrcamentoFacade.listarVerbasGestaoSsParams(solicitacaoServico.getServico(), filter);
	}
	
	public void refreshNaturezaDespesa() {
		refreshNaturezaDespesa(true);
	}

	public void refreshNaturezaDespesa(Boolean suggest) {
		isNaturezaReadonly = grupoNaturezaDespesa == null
				|| cadastrosBasicosOrcamentoFacade
						.hasUniqueRequiredNaturezaSsParam(
								solicitacaoServico.getServico(),
								grupoNaturezaDespesa);

		if (Boolean.TRUE.equals(suggest)) {
			solicitacaoServico
					.setNaturezaDespesa(grupoNaturezaDespesa == null ? null
							: cadastrosBasicosOrcamentoFacade
									.getNaturezaSsParam(
											solicitacaoServico.getServico(),
											grupoNaturezaDespesa));
		}
	}
	
	public String obterPessoaNome(RapServidores servidores){		
		
		String nomePessoa = "";
		
		if (servidores != null){
			RapServidores serv = registroColaboradorFacade.obterRapServidor(servidores.getId());			
			
			if (serv != null && serv.getPessoaFisica() != null){
				try {
					RapPessoasFisicas pessoas =  registroColaboradorFacade.obterPessoaFisica(serv.getPessoaFisica().getCodigo());
					nomePessoa = pessoas.getNome();
				} catch (ApplicationBusinessException e) {
					nomePessoa = "";
				}
				
			}	
		}
		
		return nomePessoa;
	}
	
	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}

	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public FccCentroCustos getCentroCustoAplicada() {
		return centroCustoAplicada;
	}

	public void setCentroCustoAplicada(FccCentroCustos centroCustoAplicada) {
		this.centroCustoAplicada = centroCustoAplicada;
	}

	public FsoGrupoNaturezaDespesa getGrupoNaturezaDespesa() {
		return grupoNaturezaDespesa;
	}

	public void setGrupoNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		this.grupoNaturezaDespesa = grupoNaturezaDespesa;
	}

	public boolean isExibeModalEncaminhar() {
		return isExibeModalEncaminhar;
	}

	public void setExibeModalEncaminhar(boolean isExibeModalEncaminhar) {
		this.isExibeModalEncaminhar = isExibeModalEncaminhar;
	}

	public void exibirModalEncaminhar() {
		this.setExibeModalEncaminhar(true);
	}

	public void encaminharSSPontoParada() {
		try {
			List<ScoSolicitacaoServico> solicServico;
			solicServico = new ArrayList<ScoSolicitacaoServico>();
			solicServico.add(getSolicitacaoServico());

			solicitacaoServicoFacade.encaminharListaSolicitacaoServico(solicServico,
					comprasCadastrosBasicosFacade.obterPontoParadaAutorizacao(), this.getProximoPontoParada(),
					this.getFuncionarioComprador(), false);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SOL_UNICA_SS_ENCAMINHADA_COM_SUCESSO", getSolicitacaoServico().getNumero());
			this.closeDialog("modalEncaminharScoWG");
			this.limparModalEncaminhamento();
		

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.voltarPanel = true;
		}
	}

	public void limparModalEncaminhamento() {
		this.proximoPontoParada = null;
		this.funcionarioComprador = null;
		this.voltarPanel = false;
		this.setExibeModalEncaminhar(false);
		this.setDesabilitaSuggestionComprador(false);
	}

	public void mostrarModalEncaminhamento() {
		this.proximoPontoParada = null;
		this.voltarPanel = false;
		this.setExibeModalEncaminhar(true);
	}

	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(String parametro)
			throws BaseException {
		String filtro = "";
		if (parametro != null) {
			filtro = parametro;
		}
		return comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoCaminhoPorCodigoOuDescricao(filtro,
				((ScoPontoParadaSolicitacao) comprasCadastrosBasicosFacade.obterPontoParadaAutorizacao()).getCodigo());
	}

	public String redirecionaAndamentoSS() {
		if (this.numero == null) {
			this.numero = solicitacaoServico.getNumero();
		}
		return PAGE_FASES_SOLICITACAO_SERVICO_LIST;
	}

	public String redirecionaAnexos() {
		return PAGE_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	}

	public String redirecionaEstatistica() {
		return PAGE_ESTATISTICA_CONSUMO;
	}

	public String redirecionaAssociarSC() {
		return PAGE_ASSOCIAR_SOLICITACAO_SERVICO_COMPRA;
	}

	public List<RapServidores> pesquisarCompradorAtivoPorMatriculaNome(String parametro) {
		return registroColaboradorFacade.pesquisarServidoresCompradorAtivoPorMatriculaNome(parametro);
	}

	// Suggestion Forma Contratacao
	public List<ScoModalidadeLicitacao> listarFormasContratacaoAtivas(String formaCont) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(formaCont);
	}

	public void verificarPontoParadaComprador() throws ApplicationBusinessException {
		this.setDesabilitaSuggestionComprador(comprasCadastrosBasicosFacade.verificarPontoParadaComprador(this.proximoPontoParada));
	}

	private Boolean habilitarBtAnexo() {
		if (solicitacaoServico == null) {
			return false;
		}

		if (getEdicaoArquivo()) {
			return true;
		} else {
			// desabilitar o botão de anexo caso seja a inclusão de uma SS ou
			// esteja sendo editada uma SS
			// que se encontre na situação que não possa incluir novos arquivos
			// e ela ainda não possua nenhum a ser visualizado
			Boolean existeArquivo = false;

			existeArquivo = solicitacaoComprasFacade.verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento.SS,
					solicitacaoServico.getNumero());

			return existeArquivo;
		}
	}

	public Boolean habilitarEncaminharSS() throws ApplicationBusinessException{
		List<FccCentroCustos> listaCentroCustosUsuario = this.centroCustoFacade.pesquisarCentroCustoUsuarioGerarSC();
		Boolean temPermissaoComprador = this.solicitacaoServicoFacade.verificaPemissaoUsuario("cadastrarSSComprador", this.obterLoginUsuarioLogado());
		Boolean temPermissaoPlanejamento = this.solicitacaoServicoFacade.verificaPemissaoUsuario("cadastrarSSPlanejamento", this.obterLoginUsuarioLogado()); 
		Boolean temPermissaoEncaminhar = this.solicitacaoServicoFacade.verificaPemissaoUsuario("encaminharSolicitacaoServico", this.obterLoginUsuarioLogado()); 
		
		Boolean habilitaEncaminharSS = this.solicitacaoServicoFacade.habilitarEncaminharSS(this.solicitacaoServico,
				temPermissaoComprador, temPermissaoPlanejamento, temPermissaoEncaminhar,
				listaCentroCustosUsuario);
		
		// Será permitida edição de arquivos somente se SS estiver na mesma condição em que também possa ser encaminhada
		setEdicaoArquivo(habilitaEncaminharSS);
		
		return habilitaEncaminharSS;
	}
	
	public Boolean habilitarAutorizarSS() {
		try {

			return this.solicitacaoServicoFacade.habilitarAutorizarSS(this.solicitacaoServico);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

	}

	public String autorizarSS() {
		try {
//			List<Integer> nroSSos = new ArrayList<Integer>();
//			nroSSos.add(this.solicitacaoServico.getNumero());
			List<ScoSolicitacaoServico> listaSolicitacoes = new ArrayList<ScoSolicitacaoServico>();
			listaSolicitacoes.add(this.solicitacaoServico);
			
			this.solicitacaoServicoFacade.autorizarListaSolicitacaoServico(listaSolicitacoes);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AUTORIZACAO_SS_SOL_UNICA_COM_SUCESSO", this.solicitacaoServico.getNumero());

			return null;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String duplicarSS(){
		this.setExibeModalDuplicar(false);
		try {
			if (!this.cadastrosBasicosOrcamentoFacade.isNaturezaValidSsParam(solicitacaoServico.getServico(), 
					 solicitacaoServico.getNaturezaDespesa())) {
				refreshParametrosOrcamento(true);
			}
			
			this.solicitacaoServicoFacade.desatacharSolicitacaoServico(solicitacaoServico);
			this.setSolicitacaoServicoClone(solicitacaoServicoFacade.clonarSolicitacaoServico(solicitacaoServico));
			
			this.solicitacaoServicoFacade.duplicarSS(solicitacaoServicoClone, true, false, (this.mantemCcOriginal != null));
			this.setNumero(this.getSolicitacaoServicoClone().getNumero());
			this.apresentarMsgNegocio(Severity.INFO,
					ManterSolicitacaoServicoControllerExceptionCode.SOLICITACAO_SERVICO_DUPLICADA_COM_SUCESSO.toString(), this.getSolicitacaoServicoClone().getNumero());				
			return SOLICITACAO_SERVICO_CRUD;
		
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	

	public ScoPontoParadaSolicitacao getProximoPontoParada() {
		return proximoPontoParada;
	}

	public void setProximoPontoParada(ScoPontoParadaSolicitacao proximoPontoParada) {
		this.proximoPontoParada = proximoPontoParada;
	}

	public Boolean getVoltarPanel() {
		return voltarPanel;
	}

	public void setVoltarPanel(Boolean voltarPanel) {
		this.voltarPanel = voltarPanel;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public String getGravarVoltarUrl() {
		return gravarVoltarUrl;
	}

	public void setGravarVoltarUrl(String gravarVoltarUrl) {
		this.gravarVoltarUrl = gravarVoltarUrl;
	}

	public ScoSolicitacaoServico getSolicitacaoServicoClone() {
		return solicitacaoServicoClone;
	}

	public void setSolicitacaoServicoClone(ScoSolicitacaoServico solicitacaoServicoClone) {
		this.solicitacaoServicoClone = solicitacaoServicoClone;
	}

	public RapServidores getFuncionarioComprador() {
		return funcionarioComprador;
	}

	public void setFuncionarioComprador(RapServidores funcionarioComprador) {
		this.funcionarioComprador = funcionarioComprador;
	}

	public Boolean getDesabilitaSuggestionComprador() {
		return desabilitaSuggestionComprador;
	}

	public void setDesabilitaSuggestionComprador(Boolean desabilitaSuggestionComprador) {
		this.desabilitaSuggestionComprador = desabilitaSuggestionComprador;
	}

	public Boolean getHabEncaminharSS() {
		return habEncaminharSS;
	}

	public void setHabEncaminharSS(Boolean habEncaminharSS) {
		this.habEncaminharSS = habEncaminharSS;
	}

	public Boolean getHabAutorizarSS() {
		return habAutorizarSS;
	}

	public void setHabAutorizarSS(Boolean habAutorizarSS) {
		this.habAutorizarSS = habAutorizarSS;
	}

	public Boolean getHabBtAnexo() {
		return habBtAnexo;
	}

	public void setHabBtAnexo(Boolean habBtAnexo) {
		this.habBtAnexo = habBtAnexo;
	}

	public Boolean getEdicaoArquivo() {
		return edicaoArquivo;
	}

	public void setEdicaoArquivo(Boolean edicaoArquivo) {
		this.edicaoArquivo = edicaoArquivo;
	}

	public Boolean getRdEdicao() {
		return rdEdicao;
	}

	public void setRdEdicao(Boolean rdEdicao) {
		this.rdEdicao = rdEdicao;
	}

	public Boolean getExibeModalDuplicar() {
		return exibeModalDuplicar;
	}

	public void setExibeModalDuplicar(Boolean exibeModalDuplicar) {
		this.exibeModalDuplicar = exibeModalDuplicar;
	}
	public boolean isChkCcSolic() {
		return chkCcSolic;
	}
	public void setChkCcSolic(boolean chkCcSolic) {
		this.chkCcSolic = chkCcSolic;
	}
	public boolean isChkCcAplic() {
		return chkCcAplic;
	}
	public void setChkCcAplic(boolean chkCcAplic) {
		this.chkCcAplic = chkCcAplic;
	}
	public boolean isChkCcOriginal() {
		return chkCcOriginal;
	}
	public void setChkCcOriginal(boolean chkCcOriginal) {
		this.chkCcOriginal = chkCcOriginal;
	}
	public Boolean getExibeModalPreferencia() {
		return exibeModalPreferencia;
	}
	public void setExibeModalPreferencia(Boolean exibeModalPreferencia) {
		this.exibeModalPreferencia = exibeModalPreferencia;
	}
	public ScoCaracteristicaUsuarioCentroCusto getMantemCcOriginal() {
		return mantemCcOriginal;
	}
	public void setMantemCcOriginal(ScoCaracteristicaUsuarioCentroCusto mantemCcOriginal) {
		this.mantemCcOriginal = mantemCcOriginal;
	}
}