package br.gov.mec.aghu.compras.pac.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import java.util.Objects;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Controller de Listagem do Andamento do PAC
 * 
 * @author mlcruz
 */

public class AndamentoPacPaginatorController extends ActionController implements ActionPaginator {

	private static final String ACOES_ANDAMENTO_PAC = "acoesAndamentoPac";

	@Inject @Paginator
	private DynamicDataModel<ScoAndamentoProcessoCompra> dataModel;

	private static final long serialVersionUID = -5774965552335019985L;

	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private SecurityController securityController;
	
	@Inject
	private AndamentoPacController andamentoPacController;

	/** Licitação/Filtro */
	private ScoLicitacao licitacao;

	/** ID do andamento do PAC a ser excluído. */
	private Integer andamentoAExcluir;

	/** Andamento do PAC */
	private ScoAndamentoProcessoCompra andamento;

	/** Flag de Update */
	private Boolean isUpdate;

	/** Andamento do PAC a ser marcado. */
	private ScoAndamentoProcessoCompra andamentoASerMarcado;
	
	private ScoAndamentoProcessoCompra itemSelecionado;

	/** Parâmetro AGHU */
	private Short maxDiasPerm;

	/** ID da Licitação */
	private Integer licitacaoId;

	/** Primeiro Item do PAC */
	private ScoAndamentoProcessoCompra firstAndamento;

	/** Permissão para Exclusão do Primeiro Item do PAC */
	private Boolean hasPermissionToExcluir;

	/** Permissão para Edição do Primeiro Item do PAC */
	private Boolean hasPermissionToEditar;

	/** Permissão para Marcação do Primeiro Item do PAC */
	private Boolean hasPermissionToMarcar;
	
	private Boolean permiteCadNovoLocal;

	private String voltarPara;
	
	private RapServidores servidorLogado;
	
	private ScoModalidadeLicitacao modalidadeLicitacao;	
	private Integer numEdital;
	private Integer anoComplemento;
	
	public enum AndamentoPacPaginatorControllerExceptionCode {
		MENSAGEM_FILTROS_PESQUISA_ANDAMENTO;
	}	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void inicio(){
		servidorLogado = servidorLogadoFacade.obterServidorLogado(); 
		
		if(licitacaoId != null){
			pesquisa();
		}
	}
	

	/**
	 * Limpa.
	 */
	public void limpar() {
		licitacao = null;
		setLicitacaoId(null);
		this.setModalidadeLicitacao(null);
		this.setNumEdital(null);
		this.setAnoComplemento(null);
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		hasPermissionToExcluir = null;
		hasPermissionToMarcar = null;
		hasPermissionToEditar = null;
	}

	/**
	 * Inclui andamento do PAC.
	 */
	public void incluir() {
		andamento = new ScoAndamentoProcessoCompra();
		andamento.setLicitacao(licitacao);
		andamento.setServidor(servidorLogadoFacade.obterServidorLogado());
		andamentoPacController.setAndamento(andamento);
		isUpdate = false;
	}

	public void pesquisa() {
		try {
			maxDiasPerm = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_MAX_DIAS_PERMANENCIA).getVlrNumerico()
					.shortValue();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		permiteCadNovoLocal = permitirNovoLocal();
		
		if (getLicitacaoId() == null &&
			(modalidadeLicitacao == null ||
			numEdital == null ||
			anoComplemento == null)){
			
			dataModel.setPesquisaAtiva(Boolean.FALSE);
			
			apresentarMsgNegocio(
					Severity.ERROR,
					AndamentoPacPaginatorControllerExceptionCode.MENSAGEM_FILTROS_PESQUISA_ANDAMENTO
							.toString());
			
		}
		
		if (getLicitacaoId()!= null){			
		    licitacao = pacFacade.obterLicitacao(getLicitacaoId());
		}
		else {
			licitacao = pacFacade.obterLicitacaoPorModalidadeEditalAno(modalidadeLicitacao, numEdital, anoComplemento);
		}
		
		if (licitacao != null){
			setLicitacaoId(licitacao.getNumero());
			this.setModalidadeLicitacao(licitacao.getModalidadeLicitacao());
			this.setNumEdital(licitacao.getNumEdital());
			this.setAnoComplemento(licitacao.getAnoComplemento());
		    dataModel.reiniciarPaginator();
		}
	}

	/**
	 * Trunca descrição do servidor.
	 * 
	 * @param serv
	 *            Servidor.
	 * @return Descrição truncada.
	 */
	public String truncaServidor(RapServidores serv) {
		return truncaServidor(serv, false);
	}

	/**
	 * Trunca descrição do servidor.
	 * 
	 * @param serv
	 *            Servidor.
	 * @return Descrição truncada.
	 */
	public String truncaServidor(RapServidores serv, Boolean full) {
		if (serv == null) {
			return null;
		}

		StringBuffer desc = new StringBuffer();
		String nome = serv.getPessoaFisica().getNome();
		final Integer MAX_LENGTH = 16;
		final String RET = "...";

		if (!full && nome.length() > MAX_LENGTH) {
			desc.append(nome.substring(0, MAX_LENGTH)).append(RET);
		} else {
			desc.append(nome);
		}

		if (full) {
			desc.append(" (").append(serv.getId().getMatricula()).append('/').append(serv.getId().getVinCodigo()).append(')');
		}

		return desc.toString();
	}

	@Override
	public Long recuperarCount() {
		return pacFacade.contarPacLicitacao(getLicitacaoId());
	}

	@Override
	public List<ScoAndamentoProcessoCompra> recuperarListaPaginada(Integer first, Integer max, String order, boolean asc) {
		List<ScoAndamentoProcessoCompra> result = pacFacade.pesquisarPacLicitacao(licitacaoId, first, max, order, asc);

		firstAndamento = null;
		hasPermissionToExcluir = null;
		hasPermissionToEditar = null;
		hasPermissionToMarcar = null;

		if (!result.isEmpty() && first.equals(0)) {
			firstAndamento = result.get(0);
			hasPermissionToExcluir = hasPermissionToExcluir(firstAndamento);
			hasPermissionToEditar = hasPermissionToEditar(firstAndamento);
			hasPermissionToMarcar = hasPermissionToMarcar(firstAndamento);
		}

		return result;
	}
	
	public Boolean verificarCaracteristicaProtocoloPac() {
		return this.comprasCadastrosBasicosFacade.verificarProtocoloPac(servidorLogado);
	}

	public Boolean verificarResponsavelLocalAtual() {
		return this.firstAndamento != null && this.firstAndamento.getLocalizacaoProcesso() != null && Objects.equals(this.firstAndamento.getLocalizacaoProcesso().getServidorResponsavel(), servidorLogado);
	}
	
	public Boolean permitirNovoLocal(){	
		Boolean permiteNovoLocal = verificarCaracteristicaProtocoloPac();		
		
		if(!permiteNovoLocal){	
			List<ScoAndamentoProcessoCompra> result = pacFacade.pesquisarPacLicitacao(licitacaoId);
			firstAndamento = null;
			if (!result.isEmpty()) {
				firstAndamento = result.get(0);
				
				permiteNovoLocal = verificarResponsavelLocalAtual();
			}
		}		
				
		return permiteNovoLocal;
	}

	/**
	 * Exclui andamento do PAC.
	 */
	public void excluir() {
		try {
			pacFacade.excluir(itemSelecionado.getSeq());
			
			apresentarMsgNegocio(Severity.INFO, "MESSAGE_EXCLUSAO_ANDAMENTO_PAC", andamento.getSeq().toString(), andamento
					.getLocalizacaoProcesso().getDescricao());

			this.pesquisa();
			//dataModel.reiniciarPaginator();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Verifica se é permitido excluir andamento do PAC.
	 * 
	 * @param andamento
	 *            Andamento do PAC.
	 * @return Flag.
	 */
	public Boolean hasPermissionToExcluir(ScoAndamentoProcessoCompra andamento) {
		if (andamento.equals(firstAndamento)) {
			if (hasPermissionToExcluir != null) {
				return hasPermissionToExcluir;
			}
		} else {
			return false;
		}

		if (hasPermission(andamento)) {
			if (andamento.getDtRecebido() == null) {
				Date entrada = DateUtil.truncaData(andamento.getDtEntrada());
				Date hoje = DateUtil.truncaData(new Date());
				return entrada.equals(hoje);
			}
		}

		return false;
	}

	/**
	 * Edita andamento do PAC.
	 * 
	 * @param andamento
	 *            Andamento do PAC.
	 */
	public void editar(ScoAndamentoProcessoCompra andamento) {
		this.andamento = andamento;
		isUpdate = true;
		andamentoPacController.setAndamento(andamento);
		andamentoPacController.setIsUpdate(isUpdate);
	}

	/**
	 * Verifica se é permitido editar andamento do PAC.
	 * 
	 * @param andamento
	 *            Andamento do PAC.
	 * @return Flag.
	 */
	public Boolean hasPermissionToEditar(ScoAndamentoProcessoCompra andamento) {
		if (andamento.equals(firstAndamento)) {
			if (hasPermissionToEditar != null) {
				return hasPermissionToEditar;
			}
		} else {
			return false;
		}

		return hasPermission(andamento);
	}

	/**
	 * Registra recebimento.
	 * 
	 * @param andamento
	 *            Andamento do PAC.
	 */
	public void marcar(ScoAndamentoProcessoCompra andamento) {
		andamentoASerMarcado = andamento;
		andamentoPacController.setAndamento(andamentoASerMarcado);
	}

	/**
	 * Confirma recebimento do andamento do PAC.
	 */
	public void confirmaMarcar() {
		andamentoASerMarcado.setDtRecebido(new Date());
		pacFacade.alterar(andamentoASerMarcado);

		apresentarMsgNegocio(Severity.INFO, "MESSAGE_ALTERACAO_ANDAMENTO_PAC", andamentoASerMarcado.getSeq().toString(),
				andamentoASerMarcado.getLocalizacaoProcesso().getDescricao());

		dataModel.reiniciarPaginator();
		andamentoASerMarcado = null;
	}

	/**
	 * Cancela marcação para recebimento de andamento do PAC.
	 */
	public void cancelaMarcar() {
		andamentoASerMarcado = null;
	}

	/**
	 * Verifica se é permitido registrar recebimento para andamento do PAC.
	 * 
	 * @param andamento
	 *            Andamento do PAC.
	 * @return Flag.
	 */
	public Boolean hasPermissionToMarcar(ScoAndamentoProcessoCompra andamento) {
		if (andamento.equals(firstAndamento)) {
			if (hasPermissionToMarcar != null) {
				return hasPermissionToMarcar;
			}
		} else {
			return false;
		}

		if (andamento.getDtRecebido() == null) {
			if (andamento.getDtSaida() == null) {
				if (securityController.usuarioTemPermissao("efetuarAndamentoProcessoCompras", "gravar")) {
					return servidorLogadoFacade.obterServidorLogado().equals(
							andamento.getLocalizacaoProcesso().getServidorResponsavel());
				}
			}
		}

		return false;
	}

	private Boolean hasPermission(ScoAndamentoProcessoCompra andamento) {
		if (andamento.getDtSaida() == null) {
			if (securityController.usuarioTemPermissao("efetuarAndamentoProcessoCompras", "gravar")) {
				return servidorLogadoFacade.obterServidorLogado().equals(andamento.getServidor());
			}
		}

		return false;
	}
	
	
	public Boolean verificarAcoesPontoParadaPac(ScoAndamentoProcessoCompra andamento) {
		return this.pacFacade.verificarAcoesPontoParadaPac(andamento.getSeq());
	}
	
	public Boolean validarEdicaoAcoes(ScoAndamentoProcessoCompra andamento) {
		return this.pacFacade.validarEdicaoAcoes(andamento, firstAndamento);
	}

	/**
	 * Obtem estilo CSS de célula da grade.
	 * 
	 * @param andamento
	 *            Andamento do PAC (linha da grid).
	 * @return Estilo CSS.
	 * @throws ApplicationBusinessException
	 */
	public String styleCell(ScoAndamentoProcessoCompra andamento) {
		if (andamento == null){
			return "";
		}
		Short diasPerm = pacFacade.obterMaxDiasPermAndamentoPac(andamento.getLicitacao().getModalidadeLicitacao().getCodigo(),
				andamento.getLocalizacaoProcesso().getCodigo());

		if (diasPerm == null) {
			diasPerm = maxDiasPerm;
		}

		if (diasPerm != null && andamento.getDiasPerm() > diasPerm) {
			return "background-color: #FFFF99";
		} else {
			return "";
		}
	}
	
	/**
	 * SuggestionBox Modalidade
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacao(String modalidade) {
		return this.comprasCadastrosBasicosFacade.pesquisarModalidadeLicitacaoPorCodigoDescricao(modalidade);
	}

	
	public String redirecionarAcoesAndamentoPac(){
		return ACOES_ANDAMENTO_PAC;
	}

	// Getters/Setters

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public Integer getAndamentoAExcluir() {
		return andamentoAExcluir;
	}

	public void setAndamentoAExcluir(Integer andamentoAExcluir) {
		this.andamentoAExcluir = andamentoAExcluir;
	}

	public ScoAndamentoProcessoCompra getAndamento() {
		return andamento;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public ScoAndamentoProcessoCompra getAndamentoASerMarcado() {
		return andamentoASerMarcado;
	}

	public Integer getLicitacaoId() {
		return licitacaoId;
	}

	public void setLicitacaoId(Integer licitacaoId) {
		this.licitacaoId = licitacaoId;
	}

	public Boolean getHasPermissionToExcluir() {
		return hasPermissionToExcluir;
	}

	public Boolean getHasPermissionToEditar() {
		return hasPermissionToEditar;
	}

	public Boolean getHasPermissionToMarcar() {
		return hasPermissionToMarcar;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String voltar() {
		return voltarPara;
	}

	public ScoAndamentoProcessoCompra getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ScoAndamentoProcessoCompra itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public DynamicDataModel<ScoAndamentoProcessoCompra> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoAndamentoProcessoCompra> dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean getPermiteCadNovoLocal() {
		return permiteCadNovoLocal;
	}

	public void setPermiteCadNovoLocal(Boolean permiteCadNovoLocal) {
		this.permiteCadNovoLocal = permiteCadNovoLocal;
	}

	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}

	public Integer getNumEdital() {
		return numEdital;
	}

	public void setNumEdital(Integer numEdital) {
		this.numEdital = numEdital;
	}

	public Integer getAnoComplemento() {
		return anoComplemento;
	}

	public void setAnoComplemento(Integer anoComplemento) {
		this.anoComplemento = anoComplemento;
	}
}
