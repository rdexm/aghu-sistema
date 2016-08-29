package br.gov.mec.aghu.compras.solicitacaoservico.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.compras.vo.SolServicoVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class SolicitacaoServicoPaginatorController extends ActionController implements ActionPaginator {

	private static final String COMPRAS_CONSULTA_SCSS_LIST = "compras-consultaSCSSList";

	private static final long serialVersionUID = 9041577601091390580L;

	private static final String PAGE_SOLICITACAO_SERVICO_CRUD = "solicitacaoServicoCRUD";
	private static final String PAGE_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@Inject @Paginator
	private DynamicDataModel<ScoSolicitacaoServico> dataModel;

	private FccCentroCustos centroCusto;
	private FccCentroCustos centroCustoAplicada;
	private ScoServico servico;
	private ScoSolicitacaoServico solicitacaoDeServico = new ScoSolicitacaoServico();
	private ScoSolicitacaoServico solicitacaoServicoSelecionado;
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
	private ScoPontoParadaSolicitacao pontoParada;
	private RapServidores servidorCompra;
	private ScoModalidadeLicitacao modalidadeLicitacao;
	private Boolean temPermissaoComprador;
	private Boolean temPermissaoEngenharia;
	private Boolean temPermissaoPlanejamento;
	private Boolean temPermissaoEncaminhar;
	private List<FccCentroCustos> listaCentroCustos;
	private List<FccCentroCustos> listaCentroCustosUsuario;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

//	public void init() {
//		dataModel.reiniciarPaginator();
//		dataModel.setPesquisaAtiva(Boolean.TRUE);
//		exclusao = DominioSimNao.N;
//	}

	public void pesquisar() {
		exibirNovo = true;
		this.setaVariaveisComboBox();
		dataModel.reiniciarPaginator();
	}

	public void setaVariaveisComboBox() {
		solicitacaoDeServico.setIndUrgente(null);
		solicitacaoDeServico.setIndExclusivo(null);
		solicitacaoDeServico.setIndExclusao(null);
		solicitacaoDeServico.setIndDevolucao(null);
		solicitacaoDeServico.setIndEfetivada(null);
		solicitacaoDeServico.setIndPrioridade(null);
		if (urgente!=null){
			this.solicitacaoDeServico.setIndUrgente(urgente.isSim());
		}
		if (matExclusivo!=null) {
			this.solicitacaoDeServico.setIndExclusivo(matExclusivo.isSim());
		}
		if (exclusao!=null) {
			this.solicitacaoDeServico.setIndExclusao(exclusao.isSim());
		}
		if (devolucao!=null) {
			this.solicitacaoDeServico.setIndDevolucao(devolucao.isSim());
		}
		if (efetivada!=null) {
			this.solicitacaoDeServico.setIndEfetivada(efetivada.isSim());
		}
		if (prioridade!=null){
			this.solicitacaoDeServico.setIndPrioridade(prioridade.isSim());
		}
	}
	
	public void iniciar() throws ApplicationBusinessException {
	 

	 

		
		if(!this.dataModel.getPesquisaAtiva()){
			this.dataModel.setPageRotate(true);
			ppsAutorizacao = solicitacaoServicoFacade.getPpsAutorizacao().getCodigo().shortValue();
			this.temPermissaoComprador = this.solicitacaoServicoFacade.verificaPemissaoUsuario("cadastrarSSComprador", this.obterLoginUsuarioLogado());
			this.temPermissaoEngenharia = this.solicitacaoServicoFacade.verificaPemissaoUsuario("cadastrarSSEngenharia", this.obterLoginUsuarioLogado());
			this.temPermissaoPlanejamento = this.solicitacaoServicoFacade.verificaPemissaoUsuario("cadastrarSSPlanejamento", this.obterLoginUsuarioLogado()); 
			this.temPermissaoEncaminhar = this.solicitacaoServicoFacade.verificaPemissaoUsuario("encaminharSolicitacaoServico", this.obterLoginUsuarioLogado()); 
			this.listaCentroCustos = centroCustoFacade.pesquisarCentroCustoUsuarioGerarSSSuggestion(null);
			this.listaCentroCustosUsuario = this.centroCustoFacade.pesquisarCentroCustoUsuarioGerarSs();
			
			exclusao = DominioSimNao.N;
			pendente = DominioSimNao.S;
			this.solicitacaoDeServico.setIndExclusao(exclusao.isSim());
			
			this.pesquisar();
		}
	
	}
	

	public void limpar() {
		solicitacaoDeServico = new ScoSolicitacaoServico();
		centroCusto = null;
		centroCustoAplicada = null;
		servico = null;
		exibirNovo = false;
		primeiraVez = true;
		dtSolInicio = null;
		dtSolFim = null;
		solicitacaoDeServico.setIndExclusao(false);
		urgente = null;
		matExclusivo = null;
		devolucao = null;
		efetivada = null;
		prioridade = null;
		servidorCompra = null;
		modalidadeLicitacao = null;
		pontoParada = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		exclusao = DominioSimNao.N;
		pendente = DominioSimNao.S;
	}

	public String novo() {
		return PAGE_SOLICITACAO_SERVICO_CRUD;
	}

	public String editar() {
		return PAGE_SOLICITACAO_SERVICO_CRUD;
	}

	public String anexar() {
		return PAGE_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	}
	
	public String redirecionarPesquisaGeral(){
		return COMPRAS_CONSULTA_SCSS_LIST;
	}

	@Override
	public Long recuperarCount() {

		if (this.temPermissaoComprador) {
				if (servidorCompra != null && servidorCompra.getId() != null) {
					servidorCompra = registroColaboradorFacade.obterRapServidor(servidorCompra.getId());
				}
				try {
					return this.solicitacaoServicoFacade.listarSolicitacoesDeServicoCompradoreEngenhariaCount(null, centroCusto,
							centroCustoAplicada, servico, dtSolInicio, dtSolFim, solicitacaoDeServico, pontoParada, servidorCompra,
							modalidadeLicitacao);
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}

		}else if (this.temPermissaoEngenharia)	{
				try {
					return this.solicitacaoServicoFacade.listarSolicitacoesDeServicoCompradoreEngenhariaCount(
							this.listaCentroCustos, centroCusto, centroCustoAplicada, servico, 
							dtSolInicio, dtSolFim, solicitacaoDeServico, pontoParada, servidorCompra, modalidadeLicitacao);
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
		} else {

				if (primeiraVez) {
					try {
						return this.solicitacaoServicoFacade.listarSolicitacoesDeServicosCountSemParametros(this.listaCentroCustos);
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				} else {
					this.setaVariaveisComboBox();

					if (centroCusto != null) {
						try {
							return this.solicitacaoServicoFacade.listarSolicitacoesDeServicosCount(null, centroCusto, centroCustoAplicada,
									servico, dtSolInicio, dtSolFim, pendente, solicitacaoDeServico, this.pontoParada);
						} catch (ApplicationBusinessException e) {
							apresentarExcecaoNegocio(e);
						}
					} else {

						try {
							return this.solicitacaoServicoFacade.listarSolicitacoesDeServicosCount(
									this.listaCentroCustos, centroCusto, centroCustoAplicada,
									servico, dtSolInicio, dtSolFim, pendente, solicitacaoDeServico, this.pontoParada);
						} catch (ApplicationBusinessException e) {
							apresentarExcecaoNegocio(e);
						}
					}
				}
		}
		return null;
	}

	@Override
	public List<SolServicoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String order, boolean asc) {

		try {

			if (temPermissaoComprador){
				return this.solicitacaoServicoFacade.listarSolicitacoesDeServicoCompradoreEngenharia(firstResult, maxResult, order, asc,
						null, centroCusto, centroCustoAplicada, servico, dtSolInicio, dtSolFim, solicitacaoDeServico, pontoParada,
						servidorCompra, modalidadeLicitacao);
			}
			else if (temPermissaoEngenharia){
				try {
					return this.solicitacaoServicoFacade.listarSolicitacoesDeServicoCompradoreEngenharia(firstResult, maxResult, order,
							asc, this.listaCentroCustos, centroCusto, centroCustoAplicada,
							servico, dtSolInicio, dtSolFim, solicitacaoDeServico, pontoParada, servidorCompra, modalidadeLicitacao);
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}

			} else {
				if (primeiraVez) {
					primeiraVez = false;
					try {
						return this.solicitacaoServicoFacade.listarSolicitacoesDeServicosSemParametros(firstResult, maxResult, order, asc,
								this.listaCentroCustos);
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				} else {
					if (centroCusto != null) {
						try {
							return this.solicitacaoServicoFacade.listarSolicitacoesDeServicos(firstResult, maxResult, order, asc, null,
									centroCusto, centroCustoAplicada, servico, dtSolInicio, dtSolFim, pendente, solicitacaoDeServico,
									this.pontoParada);
						} catch (ApplicationBusinessException e) {
							apresentarExcecaoNegocio(e);
						}
					} else {
						try {
							return this.solicitacaoServicoFacade.listarSolicitacoesDeServicos(firstResult, maxResult, order, asc,
									listaCentroCustos, centroCusto, centroCustoAplicada,
									servico, dtSolInicio, dtSolFim, pendente, solicitacaoDeServico, this.pontoParada);
						} catch (ApplicationBusinessException e) {
							apresentarExcecaoNegocio(e);
						}

					}
				}

			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String truncarTexto(String texto) {
		return StringUtils.abbreviate(texto, 20);
	}

	/**
	 * SuggestionBox CentroCusto Solicitante
	 * 
	 * @param paramPesquisa
	 * @param servidor
	 * @return
	 */

	public List<FccCentroCustos> listarCentroCustosSolic(String objPesquisa) throws ApplicationBusinessException {
			if (this.temPermissaoComprador){
				return this.centroCustoFacade.pesquisarCentroCustos(objPesquisa);
			} else {
				return centroCustoFacade.pesquisarCentroCustoUsuarioGerarSSSuggestion(objPesquisa);
			}
	}

	public List<FccCentroCustos> listarCentroCustosAplic(String objPesquisa) {
		return this.returnSGWithCount(this.centroCustoFacade.pesquisarCentroCustos(objPesquisa),pesquisarCentroCustosAplicCount(objPesquisa));
	}

	public Long pesquisarCentroCustosAplicCount(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosCount(objPesquisa);
	}

	public List<ScoServico> listarServicos(String param) {

		if (this.temPermissaoEngenharia){
				return this.returnSGWithCount(this.solicitacaoServicoFacade.listarServicosEngenharia(param, "S"),listarServicosCount(param));
		}
		else{
			return this.solicitacaoServicoFacade.listarServicosEngenharia(param, "N");
		}
	}

	public Long listarServicosCount(String param) {

		if (this.temPermissaoEngenharia){
				return this.solicitacaoServicoFacade.listarServicosEngenhariaCount(param, "S");
		} else {
			return this.solicitacaoServicoFacade.listarServicosEngenhariaCount(param, "N");
		}

	}

	// Suggestion Ponto Parada Atual
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaDeCompra(String parametro) {
		/*if (this.temPermissaoEngenharia){
			return this.comprasCadastrosBasicosFacade.listarPontoParadaSolicitacao(pontoParadaSolic);
		}
		else{
			return this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoCompradorPorCodigoOuDescricaoAtivos((String)pontoParadaSolic,false);
		}*/
		String filtro = "";
		if(parametro != null){
			filtro = parametro;
		}		
		
		try {
			return this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPermitidosPorCodigoOuDescricao(filtro, false);
		} catch (BaseException e) {
			return null;
		}

	}

	public void liberarParaChefia(SolServicoVO sls) {
		ScoSolicitacaoServico solicitacaoServico = this.solicitacaoServicoFacade.obterSolicitacaoServico(sls.getNumero());
		ScoSolicitacaoServico solicitacaoServicoOld = null;
		try {
			solicitacaoServicoOld = this.solicitacaoServicoFacade.clonarSolicitacaoServico(solicitacaoServico);
			solicitacaoServico.setPontoParadaLocAtual(solicitacaoServico.getPontoParada());
			solicitacaoServico.setPontoParada(this.solicitacaoServicoFacade.getPpsAutorizacao());

			this.solicitacaoServicoFacade.atualizarSolicitacaoServico(solicitacaoServico, solicitacaoServicoOld);

			sls.setPontoParadaAtual(solicitacaoServico.getPontoParada().getCodigo());
			sls.setDescricaoPontoParada(solicitacaoServico.getPontoParada().getDescricao());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SS_LIBERADO_PARA_CHEFIA_COM_SUCESSO",
					solicitacaoServico.getNumero());
		} catch (BaseException e) {
			if (solicitacaoServico != null) {
				solicitacaoServico.setPontoParadaLocAtual(solicitacaoServicoOld.getPontoParadaLocAtual());
				solicitacaoServico.setPontoParada(solicitacaoServicoOld.getPontoParada());
			}
			this.apresentarExcecaoNegocio(e);
		}
	}

	public Boolean habilitarLiberarParaChefia(SolServicoVO sls){
		ScoSolicitacaoServico solicitacaoServico = this.solicitacaoServicoFacade.obterSolicitacaoServico(sls.getNumero());
		return this.solicitacaoServicoFacade.habilitarEncaminharSS(solicitacaoServico,
				temPermissaoComprador, temPermissaoPlanejamento, temPermissaoEncaminhar,
				listaCentroCustosUsuario);
	}

	// Suggestion Servidor Comprador
	public List<RapServidores> pesquisarCompradorPorMatriculaNome(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidoresCompradorPorMatriculaNome(parametro);
	}

	// Suggestion Forma Contratacao
	public List<ScoModalidadeLicitacao> listarFormasContratacao(String formaCont) {
		return this.comprasCadastrosBasicosFacade.pesquisarModalidadeLicitacaoPorCodigoDescricao(formaCont);
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

	public ScoSolicitacaoServico getSolicitacaoDeServico() {
		return solicitacaoDeServico;
	}

	public void setSolicitacaoDeServico(ScoSolicitacaoServico solicitacaoDeServico) {
		this.solicitacaoDeServico = solicitacaoDeServico;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}

	public RapServidores getServidorCompra() {
		return servidorCompra;
	}

	public void setServidorCompra(RapServidores servidorCompra) {
		this.servidorCompra = servidorCompra;
	}

	public ScoPontoParadaSolicitacao getPontoParada() {
		return pontoParada;
	}

	public void setPontoParada(ScoPontoParadaSolicitacao pontoParada) {
		this.pontoParada = pontoParada;
	}

	public DynamicDataModel<ScoSolicitacaoServico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoSolicitacaoServico> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoSolicitacaoServico getSolicitacaoServicoSelecionado() {
		return solicitacaoServicoSelecionado;
	}

	public void setSolicitacaoServicoSelecionado(ScoSolicitacaoServico solicitacaoServicoSelecionado) {
		this.solicitacaoServicoSelecionado = solicitacaoServicoSelecionado;
	}

	public Boolean getTemPermissaoComprador() {
		return temPermissaoComprador;
	}

	public void setTemPermissaoComprador(Boolean temPermissaoComprador) {
		this.temPermissaoComprador = temPermissaoComprador;
	}

	public Boolean getTemPermissaoEngenharia() {
		return temPermissaoEngenharia;
	}

	public void setTemPermissaoEngenharia(Boolean temPermissaoEngenharia) {
		this.temPermissaoEngenharia = temPermissaoEngenharia;
	}

	public Boolean getTemPermissaoPlanejamento() {
		return temPermissaoPlanejamento;
	}

	public void setTemPermissaoPlanejamento(Boolean temPermissaoPlanejamento) {
		this.temPermissaoPlanejamento = temPermissaoPlanejamento;
	}

	public List<FccCentroCustos> getListaCentroCustosUsuario() {
		return listaCentroCustosUsuario;
	}

	public void setListaCentroCustosUsuario(List<FccCentroCustos> listaCentroCustosUsuario) {
		this.listaCentroCustosUsuario = listaCentroCustosUsuario;
	}
}
