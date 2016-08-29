package br.gov.mec.aghu.compras.solicitacaocompras.action;


import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.vo.SolCompraVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class SolicitacaoCompraPaginatorController extends ActionController implements ActionPaginator {

	private static final String CONSULTA_SCSS_LIST = "compras-consultaSCSSList";

	private static final String SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";

	private static final String SOLICITACAO_COMPRA_CRUD = "solicitacaoCompraCRUD";

	@Inject @Paginator
	private DynamicDataModel<ScoSolicitacaoDeCompra> dataModel;

	private static final long serialVersionUID = 9041577601091390580L;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	private List<FccCentroCustos> listaCentroCustos;
	private ScoPontoParadaSolicitacao pontoParadaAtual;
	private RapServidores servidorCompra;
	private FccCentroCustos centroCusto;
	private FccCentroCustos centroCustoAplicada;
	private ScoMaterial material;
	private ScoSolicitacaoDeCompra solicitacaoDeCompra = new ScoSolicitacaoDeCompra();
	
	private DominioSimNao pendente;
	private DominioSimNao urgente;
	private DominioSimNao matExclusivo;
	private DominioSimNao exclusao;
	private DominioSimNao devolucao;
	private DominioSimNao efetivada;
	private DominioSimNao prioridade;
	private Date dtSolInicio;
	private Date dtSolFim;
	private Boolean exibirNovo = false;
	private Boolean primeiraVez = true;
	private Short ppsAutorizacao;
	private List<SolCompraVO> listaSolicitacaoCompra;
	
	private Boolean temPermissaoComprador;
	private Boolean temPermissaoGeral;
	private Boolean temPermissaoPlanejamento;
	private Boolean temPermissaoEncaminhar;
	private List<FccCentroCustos> listaCentroCustosUsuario;
	private String  voltarParaUrl;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void pesquisar() throws ApplicationBusinessException {
		exibirNovo = true;	
		if(primeiraVez){
			exclusao = DominioSimNao.N;
			if (!this.temPermissaoComprador) {					
				pendente = DominioSimNao.S;
			}
		}
		this.setaVariaveisComboBox();
		this.reiniciarPaginator();
	}
	
	public void obterParametrosUsuario() throws ApplicationBusinessException {
		ppsAutorizacao = solicitacaoComprasFacade.getPpsAutorizacao().getCodigo().shortValue();
		this.temPermissaoComprador = this.solicitacaoComprasFacade.verificaPemissaoUsuario("cadastrarSCComprador", this.obterLoginUsuarioLogado());
		this.temPermissaoGeral = this.solicitacaoComprasFacade.verificaPemissaoUsuario("cadastrarSCGeral", this.obterLoginUsuarioLogado());
		this.temPermissaoPlanejamento = this.solicitacaoComprasFacade.verificaPemissaoUsuario("cadastrarSCPlanejamento", this.obterLoginUsuarioLogado());
		this.temPermissaoEncaminhar = this.solicitacaoComprasFacade.verificaPemissaoUsuario("encaminharSolicitacaoCompras", this.obterLoginUsuarioLogado());
	}
	
	public void inicio() throws ApplicationBusinessException {
		
		setEfetivada(DominioSimNao.N);
		
		if(! this.isAtivo()){
			this.dataModel.setPageRotate(true);
			this.obterParametrosUsuario();
			if (listaCentroCustos == null || listaCentroCustos.isEmpty()) {
				this.listaCentroCustos = centroCustoFacade.pesquisarCentroCustoUsuarioGerarSCSuggestion(null);
			}
			if (listaCentroCustosUsuario == null || listaCentroCustosUsuario.isEmpty()) {
				this.listaCentroCustosUsuario = this.centroCustoFacade.pesquisarCentroCustoUsuarioGerarSC();
			}
			if(!this.isPerfilGeral()){
				this.pesquisar();				
			}
		}
	}
	
	public void setaVariaveisComboBox(){
		this.verificarCheckboxUrgente();
		this.verificarCheckboxMaterialExclusivo();
		this.verificarCheckboxExclusao();
		this.verificarCheckboxDevolucao();
		this.verificarCheckboxEfetivada();
		this.verificarCheckboxPrioridade();
	}
	
	private void verificarCheckboxUrgente(){
		if (urgente!=null){
			this.solicitacaoDeCompra.setUrgente((urgente== DominioSimNao.S ?true:false));
		}
		else {
			this.solicitacaoDeCompra.setUrgente(null);
		}
	}
	
	private void verificarCheckboxMaterialExclusivo(){
		if (matExclusivo!=null) {
			this.solicitacaoDeCompra.setMatExclusivo((matExclusivo== DominioSimNao.S ?true:false));
		} 
		else {
			this.solicitacaoDeCompra.setMatExclusivo(null);
		}
	}
	
	private void verificarCheckboxExclusao(){
		if (exclusao!=null) {
			this.solicitacaoDeCompra.setExclusao((exclusao== DominioSimNao.S ?true:false));
		}	
		else {
			this.solicitacaoDeCompra.setExclusao(null);
		}
	}
	
	private void verificarCheckboxDevolucao(){
		if (devolucao!=null) {
			this.solicitacaoDeCompra.setDevolucao((devolucao== DominioSimNao.S ?true:false));
		}
		else {
			this.solicitacaoDeCompra.setDevolucao(null);
		}
	}
	
	private void verificarCheckboxEfetivada(){
		if (efetivada!=null) {
			this.solicitacaoDeCompra.setEfetivada((efetivada== DominioSimNao.S ?true:false));
		}
		else {
			this.solicitacaoDeCompra.setEfetivada(null);
		}
	}
	
	private void verificarCheckboxPrioridade(){
		if (prioridade!=null){
			this.solicitacaoDeCompra.setPrioridade((prioridade== DominioSimNao.S ?true:false));
		}
		else {
			this.solicitacaoDeCompra.setPrioridade(null);
		}
	}

	
	public void limpar() {
		listaSolicitacaoCompra = null;
		solicitacaoDeCompra = new ScoSolicitacaoDeCompra();
		centroCusto = null;
		centroCustoAplicada = null;
		material= null;
		exibirNovo = false;
		//primeiraVez = true;
		dtSolInicio = null;
		dtSolFim = null;
		pendente = null;
		solicitacaoDeCompra.setExclusao(false);
		urgente = null;
		matExclusivo = null;
		exclusao = null;
		devolucao = null;
		efetivada = null;
		prioridade = null;
		pontoParadaAtual = null;
		servidorCompra = null;
		exclusao = DominioSimNao.N;
		pendente = DominioSimNao.S;
		efetivada = DominioSimNao.N;
	}
	
	 
	
	@Override
	public Long recuperarCount() {
		if (temPermissaoComprador == null) {
			try {
				this.obterParametrosUsuario();
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			}
		}
		
		if (primeiraVez){
			
			try {
				exclusao = DominioSimNao.N;
				solicitacaoDeCompra.setExclusao(false);
				
				if (this.temPermissaoComprador) {
					return this.solicitacaoComprasFacade.listarSolicitacoesDeComprasCompradorCount(null, centroCusto, centroCustoAplicada, material,
							dtSolInicio, dtSolFim, solicitacaoDeCompra, pontoParadaAtual, servidorCompra, false);
				} else if(this.temPermissaoGeral) {
					return this.solicitacaoComprasFacade.listarSolicitacoesDeComprasCompradorCount(null, centroCusto, centroCustoAplicada, material,
							dtSolInicio, dtSolFim, solicitacaoDeCompra, pontoParadaAtual, servidorCompra, true);
				}
				else {
					pendente = DominioSimNao.S;
					return this.solicitacaoComprasFacade.listarSolicitacoesDeComprasCountSemParametros(listaCentroCustos);
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		else {
			this.setaVariaveisComboBox();
			
			if (this.temPermissaoComprador){
				try {
				
						if (servidorCompra!=null && servidorCompra.getId()!=null) {
							servidorCompra = registroColaboradorFacade.obterRapServidor(servidorCompra.getId());
						}	
						return this.solicitacaoComprasFacade.listarSolicitacoesDeComprasCompradorCount(null, centroCusto, centroCustoAplicada, material, 
								dtSolInicio, dtSolFim, solicitacaoDeCompra, pontoParadaAtual, servidorCompra, false);
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
			} else if (this.temPermissaoGeral){
					try {
						if (servidorCompra!=null && servidorCompra.getId()!=null) {
							servidorCompra = registroColaboradorFacade.obterRapServidor(servidorCompra.getId());
						}	
						return this.solicitacaoComprasFacade.listarSolicitacoesDeComprasCompradorCount(null, centroCusto, centroCustoAplicada, material, 
								dtSolInicio, dtSolFim, solicitacaoDeCompra, pontoParadaAtual, servidorCompra, true);
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				}
				else {
					if (centroCusto!=null){
						try {
							return this.solicitacaoComprasFacade.listarSolicitacoesDeComprasCount(null, centroCusto, centroCustoAplicada, material, dtSolInicio,
									dtSolFim, pendente, solicitacaoDeCompra, pontoParadaAtual);
						} catch (ApplicationBusinessException e) {
							apresentarExcecaoNegocio(e);
						}
					}
					else {
						try {
							return this.solicitacaoComprasFacade.listarSolicitacoesDeComprasCount(listaCentroCustos,
									centroCusto, centroCustoAplicada, material, dtSolInicio,dtSolFim, pendente, solicitacaoDeCompra, pontoParadaAtual);
						} catch (ApplicationBusinessException e) {
							apresentarExcecaoNegocio(e);
						} 
					}
				}

			}
		return null;		
		
	}

	@Override
	public List<SolCompraVO> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String order, boolean asc) {
		
		if (primeiraVez){
		
			try {
				primeiraVez = false;
				if (this.temPermissaoComprador) {
					listaSolicitacaoCompra = this.solicitacaoComprasFacade.listarSolicitacoesDeComprasComprador(firstResult, maxResult, order, asc, null, centroCusto, centroCustoAplicada, material, 
							dtSolInicio, dtSolFim, solicitacaoDeCompra, pontoParadaAtual, servidorCompra, false);
				} else if(this.temPermissaoGeral){
					listaSolicitacaoCompra = this.solicitacaoComprasFacade.listarSolicitacoesDeComprasComprador(firstResult, maxResult, order, asc, null, centroCusto, centroCustoAplicada, material, 
							dtSolInicio, dtSolFim, solicitacaoDeCompra, pontoParadaAtual, servidorCompra, true);
				}
				else {
					listaSolicitacaoCompra = this.solicitacaoComprasFacade.listarSolicitacoesDeComprasSemParametros(firstResult, maxResult, order, asc, listaCentroCustos);
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		else {
			
			if (this.temPermissaoComprador) {
				try {						
					listaSolicitacaoCompra = this.solicitacaoComprasFacade.listarSolicitacoesDeComprasComprador(firstResult, maxResult, order, asc, null, centroCusto, centroCustoAplicada, material, 
							dtSolInicio, dtSolFim, solicitacaoDeCompra, pontoParadaAtual, servidorCompra, false);
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			} else if (this.temPermissaoGeral) {
				try {
					return  this.solicitacaoComprasFacade.listarSolicitacoesDeComprasComprador(firstResult, maxResult, order, asc, null, centroCusto, centroCustoAplicada, material, 
							dtSolInicio, dtSolFim, solicitacaoDeCompra, pontoParadaAtual, servidorCompra, true);
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
			else {
				if (centroCusto!=null){
					try {
						listaSolicitacaoCompra = this.solicitacaoComprasFacade.listarSolicitacoesDeCompras(firstResult, maxResult, order, asc, null, centroCusto, centroCustoAplicada, material, 
								dtSolInicio, dtSolFim, pendente, solicitacaoDeCompra, pontoParadaAtual);
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				}
				else {
					try {
						return  this.solicitacaoComprasFacade.listarSolicitacoesDeCompras(firstResult, maxResult, order, asc, 
								listaCentroCustos, 
								centroCusto, centroCustoAplicada, material, dtSolInicio, dtSolFim, pendente, solicitacaoDeCompra, pontoParadaAtual);
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
					
				}
			}
		}
		
		return listaSolicitacaoCompra;
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	
	
		
	public void liberarParaChefia(SolCompraVO slc){
		ScoSolicitacaoDeCompra solicitacaoDeCompraOld = null;
		ScoSolicitacaoDeCompra solicitacaoDeCompra = this.solicitacaoComprasFacade.obterSolicitacaoDeCompra(slc.getNumero());
		try{
			solicitacaoDeCompraOld = this.solicitacaoComprasFacade.clonarSolicitacaoDeCompra(solicitacaoDeCompra);
			solicitacaoDeCompra.setPontoParada(solicitacaoDeCompra.getPontoParadaProxima());
			solicitacaoDeCompra.setPontoParadaProxima(this.solicitacaoComprasFacade.getPpsAutorizacao());
			this.solicitacaoComprasFacade.atualizarScoSolicitacaoDeCompra(solicitacaoDeCompra, solicitacaoDeCompraOld);
			slc.setPontoParadaAtual(solicitacaoDeCompra.getPontoParadaProxima().getCodigo());
			slc.setDescricaoPontoParada(solicitacaoDeCompra.getPontoParadaProxima().getDescricao());			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SC_LIBERADO_PARA_CHEFIA_COM_SUCESSO",solicitacaoDeCompra.getNumero());
		} catch(BaseException e){
			if(solicitacaoDeCompraOld!=null){
				solicitacaoDeCompra.setPontoParada(solicitacaoDeCompraOld.getPontoParada());
				solicitacaoDeCompra.setPontoParadaProxima(solicitacaoDeCompraOld.getPontoParadaProxima());	
			}
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean habilitarLiberarParaChefia(SolCompraVO slc){
		ScoSolicitacaoDeCompra solicitacaoCompra = this.solicitacaoComprasFacade.obterSolicitacaoDeCompra(slc.getNumero());
		return this.solicitacaoComprasFacade.habilitarEncaminharSC(solicitacaoCompra,
				temPermissaoComprador, temPermissaoPlanejamento, temPermissaoEncaminhar,
				listaCentroCustosUsuario);
	}
	
	public Boolean isPerfilGeral() throws ApplicationBusinessException{
		return this.solicitacaoComprasFacade.verificaPemissaoUsuario("cadastrarSCGeral", this.obterLoginUsuarioLogado());
	}

	public String truncarTexto(String texto) {
		return StringUtils.abbreviate(texto, 20);
	}
	
	public String criarNovaSolicitacao(){
		return SOLICITACAO_COMPRA_CRUD;
	}
	
	public String editar(){
		return SOLICITACAO_COMPRA_CRUD;
	}
	
	public String anexar(){
		return SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	}
	
	public String redirecionarPesquisaGeral(){
		return CONSULTA_SCSS_LIST;
	}
		
	/**
	 * SuggestionBox CentroCusto Solicitante
	 * @param paramPesquisa
	 * @param servidor
	 * @return
	 */
    public List<FccCentroCustos> pesquisarCentroCustoUsuarioGerarSCSuggestion(String paramPesquisa){
    	return centroCustoFacade.pesquisarCentroCustoUsuarioGerarSCSuggestion(paramPesquisa);	
    }

    public Long pesquisarCentroCustoUsuarioGerarSCSuggestionCount(String paramPesquisa){
       	return centroCustoFacade.pesquisarCentroCustoUsuarioGerarSCSuggestionCount(paramPesquisa);	
    }
    
    public List<FccCentroCustos> listarCentroCustosAplic(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustos(objPesquisa);
	}

	public Long pesquisarCentroCustosAplicCount(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosCount(objPesquisa);
	}
	
	public List<ScoMaterial> listarMateriais(String param) {
		return this.comprasFacade.listarMateriaisAtivos(param, null);
	}

	public Long listarMateriaisCount(String param)	{
		return this.comprasFacade.listarMateriaisAtivosCount(param, null);
	}

	//Suggestion Ponto Parada Atual
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaDeCompraAtivos(String pontoParadaSolic) {
		return this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivos((String)pontoParadaSolic, true);
	}
	
	//Suggestion Servidor Comprador
	public List<RapServidores> pesquisarCompradorAtivoPorMatriculaNome(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidoresCompradorAtivoPorMatriculaNome(parametro);
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

	public Boolean getExibirNovo() {
		return exibirNovo;
	}

	public void setExibirNovo(Boolean exibirNovo) {
		this.exibirNovo = exibirNovo;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoDeCompra() {
		return solicitacaoDeCompra;
	}

	public void setSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra) {
		this.solicitacaoDeCompra = solicitacaoDeCompra;
	}

	public Date getDtSolInicio() {
		return dtSolInicio;
	}

	public void setDtSolInicio(Date dtSolInicio) {
		this.dtSolInicio = dtSolInicio;
	}

	public Date getDtSolFim() {
		return dtSolFim;
	}

	public void setDtSolFim(Date dtSolFim) {
		this.dtSolFim = dtSolFim;
	}

	public DominioSimNao getPendente() {
		return pendente;
	}

	public void setPendente(DominioSimNao pendente) {
		this.pendente = pendente;
	}

	public Short getPpsAutorizacao() {
		return ppsAutorizacao;
	}

	public void setPpsAutorizacao(Short ppsAutorizacao) {
		this.ppsAutorizacao = ppsAutorizacao;
	}

	public DominioSimNao getUrgente() {
		return urgente;
	}

	public DominioSimNao getMatExclusivo() {
		return matExclusivo;
	}

	public DominioSimNao getExclusao() {
		return exclusao;
	}

	public DominioSimNao getDevolucao() {
		return devolucao;
	}

	public DominioSimNao getEfetivada() {
		return efetivada;
	}

	public Boolean getPrimeiraVez() {
		return primeiraVez;
	}

	public void setUrgente(DominioSimNao urgente) {
		this.urgente = urgente;
	}

	public void setMatExclusivo(DominioSimNao matExclusivo) {
		this.matExclusivo = matExclusivo;
	}

	public void setExclusao(DominioSimNao exclusao) {
		this.exclusao = exclusao;
	}

	public void setDevolucao(DominioSimNao devolucao) {
		this.devolucao = devolucao;
	}

	public void setEfetivada(DominioSimNao efetivada) {
		this.efetivada = efetivada;
	}

	public void setPrimeiraVez(Boolean primeiraVez) {
		this.primeiraVez = primeiraVez;
	}

	public DominioSimNao getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(DominioSimNao prioridade) {
		this.prioridade = prioridade;
	}

	public ScoPontoParadaSolicitacao getPontoParadaAtual() {
		return pontoParadaAtual;
	}

	public void setPontoParadaAtual(ScoPontoParadaSolicitacao pontoParadaAtual) {
		this.pontoParadaAtual = pontoParadaAtual;
	}
	
	public RapServidores getServidorCompra() {
		return servidorCompra;
	}

	public void setServidorCompra(RapServidores servidorCompra) {
		this.servidorCompra = servidorCompra;
	}
	
	public List<SolCompraVO> getListaSolicitacaoCompra() {
		return listaSolicitacaoCompra;
	}

	public void setListaSolicitacaoCompra(
			List<SolCompraVO> listaSolicitacaoCompra) {
		this.listaSolicitacaoCompra = listaSolicitacaoCompra;
	}
	
	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
	
	public DynamicDataModel<ScoSolicitacaoDeCompra> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoSolicitacaoDeCompra> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
	
	public List<FccCentroCustos> getListaCentroCustos() {
		return listaCentroCustos;
	}

	public void setListaCentroCustos(List<FccCentroCustos> listaCentroCustos) {
		this.listaCentroCustos = listaCentroCustos;
	}

	public Boolean getTemPermissaoComprador() {
		return temPermissaoComprador;
	}

	public void setTemPermissaoComprador(Boolean temPermissaoComprador) {
		this.temPermissaoComprador = temPermissaoComprador;
	}

	public Boolean getTemPermissaoGeral() {
		return temPermissaoGeral;
	}

	public void setTemPermissaoGeral(Boolean temPermissaoGeral) {
		this.temPermissaoGeral = temPermissaoGeral;
	}

	public Boolean getTemPermissaoPlanejamento() {
		return temPermissaoPlanejamento;
	}

	public void setTemPermissaoPlanejamento(Boolean temPermissaoPlanejamento) {
		this.temPermissaoPlanejamento = temPermissaoPlanejamento;
	}

	public Boolean getTemPermissaoEncaminhar() {
		return temPermissaoEncaminhar;
	}

	public void setTemPermissaoEncaminhar(Boolean temPermissaoEncaminhar) {
		this.temPermissaoEncaminhar = temPermissaoEncaminhar;
	}

	public List<FccCentroCustos> getListaCentroCustosUsuario() {
		return listaCentroCustosUsuario;
	}

	public void setListaCentroCustosUsuario(List<FccCentroCustos> listaCentroCustosUsuario) {
		this.listaCentroCustosUsuario = listaCentroCustosUsuario;
	}
}
