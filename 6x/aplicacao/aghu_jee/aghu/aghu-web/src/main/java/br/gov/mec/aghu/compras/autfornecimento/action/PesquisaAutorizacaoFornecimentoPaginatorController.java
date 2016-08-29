package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaAutorizacaoFornecimentoVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioAndamentoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFiltroAutorizacaoFornecimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaAutorizacaoFornecimentoPaginatorController  extends ActionController implements ActionPaginator {

	private static final String PESQUISAR_ITEM_AUT_FORNECIMENTO = "pesquisarItemAutFornecimento";

	private static final String AUTORIZACAO_FORNECIMENTO_CRUD = "autorizacaoFornecimentoCRUD";

	@Inject @Paginator
	private DynamicDataModel<ScoAutorizacaoForn> dataModel;

	private static final long serialVersionUID = -729445125588120797L;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IPacFacade pacFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	// filtros
	private Integer numeroAf;
	private Short numeroComplemento;
	private DominioSituacaoAutorizacaoFornecimento situacaoAf;
	private DominioAndamentoAutorizacaoFornecimento andamentoAf;
	private ScoFornecedor fornecedor;
	private ScoModalidadeLicitacao modalidadeCompra;
	private RapServidores servidorGestor;
	private DominioTipoFiltroAutorizacaoFornecimento tipoFiltroAf;
	private Integer codigoFiltroAf;
	private Integer numeroContrato;
	private Date dataInicioContrato;
	private Date dataFimContrato;
	private ScoMaterial material;
	private ScoServico servico;
	private Map<String,DominioAndamentoAutorizacaoFornecimento> mapAndamento;
	private DominioSimNao pendente;
	private DominioSimNao vencida;
	
	
	// URL para botão voltar
	private String voltarParaUrl;
	
	// controles de tela
	private Boolean mostrarBotaoVoltar;
	private Boolean mostraFiltroSimples;
	private Boolean mostraSuggestionMaterial;
	private Boolean mostraSuggestionServico;
	private Boolean refazerPesquisa;
	private Boolean retornoEdicao = Boolean.FALSE;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		if(retornoEdicao){
			this.pesquisar();
			retornoEdicao = Boolean.FALSE;
			return;
		}
		if (this.numeroAf != null) {
			this.limpar(false);
			this.mostrarBotaoVoltar = Boolean.TRUE;
			this.pesquisar();
		} else {
			if (refazerPesquisa == null){
			    this.refazerPesquisa = Boolean.FALSE;
			}
		}
		
		if(refazerPesquisa){
			this.pesquisar();
		}
	}
	
	
	public void pesquisar() {		
		if (this.numeroAf == null && this.numeroComplemento != null) {
			apresentarMsgNegocio(
					Severity.INFO, "MENSAGEM_PESQUISAR_COMPLEMENTO_SEM_AF");	
		} else {		
			this.dataModel.reiniciarPaginator();		
			this.refazerPesquisa = Boolean.FALSE;
		}
	}

	public void limpar(Boolean tudo) {
		if (tudo) {
			this.numeroAf = null;
			this.numeroComplemento = null;	
			this.situacaoAf = null;
		}
		this.andamentoAf = null;
		this.fornecedor = null;
		this.modalidadeCompra = null;
		this.servidorGestor = null;
		this.tipoFiltroAf = null;
		this.codigoFiltroAf = null;
		this.numeroContrato = null;
		this.material = null;
		this.servico = null;
		this.dataInicioContrato = null;
		this.dataFimContrato = null;
		this.mostraFiltroSimples = Boolean.FALSE;
		this.mostraSuggestionMaterial = Boolean.FALSE; 
		this.mostraSuggestionServico = Boolean.FALSE;
		this.refazerPesquisa = Boolean.FALSE;
		this.mapAndamento = new HashMap<String, DominioAndamentoAutorizacaoFornecimento>();
		this.setAtivo(false);
	}
	

	// suggestions
	public List<ScoModalidadeLicitacao> pesquisarModalidades(String filter) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(filter);
	}
	
	public List<ScoFornecedor> pesquisarFornecedores(String param) {
		return this.comprasFacade.listarFornecedoresAtivos(param,0,100,null,false);
	}
	
	public List<RapServidores> listarServidores(String objPesquisa) {
		if (objPesquisa!=null && !"".equalsIgnoreCase((String) objPesquisa)){
			return this.registroColaboradorFacade.pesquisarServidor(objPesquisa);
		}else {return this.registroColaboradorFacade.pesquisarRapServidores();}
	}
	
	public List<ScoMaterial> listarMateriais(String param) throws BaseException {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(param, null, true),listarMateriaisCount(param));
	}
	
	public Long listarMateriaisCount(String param)	{
		return this.solicitacaoComprasFacade.listarMateriaisAtivosCount(param, this.obterLoginUsuarioLogado());
	}
	
	public List<ScoServico> listarServicosAtivos(String param) {
		return this.returnSGWithCount(this.solicitacaoServicoFacade.listarServicosAtivos(param),listarServicosAtivosCount(param));		
	}

	public Long listarServicosAtivosCount(String param)	{
		return this.solicitacaoServicoFacade.listarServicosAtivosCount(param);
	}

	
	// metodos auxilio
	public void verificarFiltro() {
		if (this.tipoFiltroAf == DominioTipoFiltroAutorizacaoFornecimento.MATERIAL) {
			this.mostraSuggestionMaterial = Boolean.TRUE;
			this.mostraSuggestionServico = Boolean.FALSE;
			this.mostraFiltroSimples = Boolean.FALSE;
			this.material = null;
			this.servico = null;
		} else if (this.tipoFiltroAf == DominioTipoFiltroAutorizacaoFornecimento.SERVICO) {
			this.mostraSuggestionMaterial = Boolean.FALSE;
			this.mostraSuggestionServico = Boolean.TRUE;
			this.mostraFiltroSimples = Boolean.FALSE;
			this.material = null;
			this.servico = null;
		} else if (this.tipoFiltroAf == null) {
			this.mostraSuggestionMaterial = Boolean.FALSE;
			this.mostraSuggestionServico = Boolean.FALSE;
			this.mostraFiltroSimples = Boolean.FALSE;
			this.material = null;
			this.servico = null;
		} else  {
			this.mostraSuggestionMaterial = Boolean.FALSE;
			this.mostraSuggestionServico = Boolean.FALSE;
			this.mostraFiltroSimples = Boolean.TRUE;
		}
	}
	
	public DominioAndamentoAutorizacaoFornecimento obterAndamento(ScoAutorizacaoForn item) {
		return this.mapAndamento.get(item.getPropostaFornecedor().getId().getLctNumero().toString().concat("_").concat(item.getNroComplemento().toString()));
	}
	
	public String obterNomeImagem(ScoAutorizacaoForn item) {
		return this.autFornecimentoFacade.obterNomeImagemAutorizacaoFornecimento(obterAndamento(item));
	}
	
	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}
	
	public String obterDocFormatado(ScoFornecedor fornecedor) {
		return this.comprasFacade.obterCnpjCpfFornecedorFormatado(fornecedor);
	}
	
	public String voltar(){
		return voltarParaUrl;
	}
	
	public String redirecionarAutorizacaoFornecimentoCrud(){
		return AUTORIZACAO_FORNECIMENTO_CRUD;
	}
	
	public String redirecionarPesquisarItemAutFornecimento(){
		return PESQUISAR_ITEM_AUT_FORNECIMENTO;
	}
	
	private void preencherFiltrosDiretos(PesquisaAutorizacaoFornecimentoVO filtro) {
		if (this.numeroAf != null) {
			filtro.setLctNumero(this.numeroAf);
		}
		if (this.numeroComplemento != null) { 			
			filtro.setNumeroComplemento(this.numeroComplemento);
		}
		if (this.situacaoAf != null) {
			filtro.setSituacaoAf(this.situacaoAf);
		}
		if (this.andamentoAf != null) {		
			filtro.setAndamentoAf(this.andamentoAf);
		}
		if (this.servidorGestor != null) {
			filtro.setServidorGestor(this.servidorGestor);
		}
		if (this.numeroContrato != null) {
			filtro.setNumeroContrato(this.numeroContrato);
		}
		if (this.dataInicioContrato != null) {
			filtro.setDataInicioContrato(this.dataInicioContrato);
		}
		if (this.dataFimContrato != null) {
			filtro.setDataFimContrato(this.dataFimContrato);
		}
	}
	
	private void preencherFiltrosIndiretos(PesquisaAutorizacaoFornecimentoVO filtro) {
		if (this.fornecedor != null) {
			filtro.setFornecedor(this.fornecedor);
		}
		if (this.modalidadeCompra != null) {
			filtro.setModalidadeCompra(this.modalidadeCompra);
		}
		if (this.tipoFiltroAf != null) {
			filtro.setTipoFiltroAf(this.tipoFiltroAf);
		}
		if (this.codigoFiltroAf != null) {
			filtro.setCodigoFiltroAf(this.codigoFiltroAf);
		}
		if (this.servico != null) {
			filtro.setCodigoFiltroAf(this.servico.getCodigo());
		}
		if (this.material != null) {
			filtro.setCodigoFiltroAf(this.material.getCodigo());
		}
		if (this.vencida != null) {
			filtro.setVencida(this.vencida);
		}
		if (this.pendente != null) {
			filtro.setPendente(this.pendente);
		}			
	}
	
	private PesquisaAutorizacaoFornecimentoVO montarFiltro() {
		PesquisaAutorizacaoFornecimentoVO filtro = new PesquisaAutorizacaoFornecimentoVO();
		
		this.preencherFiltrosDiretos(filtro);
		this.preencherFiltrosIndiretos(filtro);
		
		return filtro;
	}
	
	public void atualizarCombos(Integer w) {
		this.situacaoAf = null;
		switch(w) {
			case 0:
				this.vencida = null;
				break;
			case 1:
				this.pendente = null;
				break;	
		}
	}
	
	// paginator

	@Override
	public Long recuperarCount() {
		return this.autFornecimentoFacade.contarAutorizacaoFornecimento(this.montarFiltro());
	}

	@Override
	public List<ScoAutorizacaoForn> recuperarListaPaginada(Integer first, Integer max,
			String order, boolean asc) {	
		
		this.mapAndamento = new HashMap<String, DominioAndamentoAutorizacaoFornecimento>();
		List<ScoAutorizacaoForn> lista = this.autFornecimentoFacade.pesquisarAutorizacaoFornecimento(first, max, order, asc, this.montarFiltro()); 
		
		for (ScoAutorizacaoForn item : lista) {			
			this.mapAndamento.put(item.getPropostaFornecedor().getId().getLctNumero().toString().concat("_").concat(item.getNroComplemento().toString()), 
					this.autFornecimentoFacade.obterAndamentoAutorizacaoFornecimento(item.getPropostaFornecedor().getId().getLctNumero(), item.getNroComplemento()));
		}
		
		return lista;
	}

	public String descricaoSplited(String descricao){
		return descricao.replaceAll("<br />", "\n");
	}
	
	// Getters/Setters
	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setComprasCadastrosBasicosFacade(
			IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public IPacFacade getPacFacade() {
		return pacFacade;
	}

	public void setPacFacade(IPacFacade pacFacade) {
		this.pacFacade = pacFacade;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	public DominioSituacaoAutorizacaoFornecimento getSituacaoAf() {
		return situacaoAf;
	}

	public void setSituacaoAf(DominioSituacaoAutorizacaoFornecimento situacaoAf) {
		this.situacaoAf = situacaoAf;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public ScoModalidadeLicitacao getModalidadeCompra() {
		return modalidadeCompra;
	}

	public void setModalidadeCompra(ScoModalidadeLicitacao modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}

	public Integer getCodigoFiltroAf() {
		return codigoFiltroAf;
	}

	public void setCodigoFiltroAf(Integer codigoFiltroAf) {
		this.codigoFiltroAf = codigoFiltroAf;
	}

	public Integer getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(Integer numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public RapServidores getServidorGestor() {
		return servidorGestor;
	}

	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}

	public Date getDataInicioContrato() {
		return dataInicioContrato;
	}

	public void setDataInicioContrato(Date dataInicioContrato) {
		this.dataInicioContrato = dataInicioContrato;
	}

	public Date getDataFimContrato() {
		return dataFimContrato;
	}

	public void setDataFimContrato(Date dataFimContrato) {
		this.dataFimContrato = dataFimContrato;
	}

	public DominioAndamentoAutorizacaoFornecimento getAndamentoAf() {
		return andamentoAf;
	}

	public void setAndamentoAf(DominioAndamentoAutorizacaoFornecimento andamentoAf) {
		this.andamentoAf = andamentoAf;
	}

	public DominioTipoFiltroAutorizacaoFornecimento getTipoFiltroAf() {
		return tipoFiltroAf;
	}

	public void setTipoFiltroAf(DominioTipoFiltroAutorizacaoFornecimento tipoFiltroAf) {
		this.tipoFiltroAf = tipoFiltroAf;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Boolean getMostrarBotaoVoltar() {
		return mostrarBotaoVoltar;
	}

	public void setMostrarBotaoVoltar(Boolean mostrarBotaoVoltar) {
		this.mostrarBotaoVoltar = mostrarBotaoVoltar;
	}

	public Boolean getMostraFiltroSimples() {
		return mostraFiltroSimples;
	}

	public void setMostraFiltroSimples(Boolean mostraFiltroSimples) {
		this.mostraFiltroSimples = mostraFiltroSimples;
	}

	public Boolean getMostraSuggestionMaterial() {
		return mostraSuggestionMaterial;
	}

	public void setMostraSuggestionMaterial(Boolean mostraSuggestionMaterial) {
		this.mostraSuggestionMaterial = mostraSuggestionMaterial;
	}

	public Boolean getMostraSuggestionServico() {
		return mostraSuggestionServico;
	}

	public void setMostraSuggestionServico(Boolean mostraSuggestionServico) {
		this.mostraSuggestionServico = mostraSuggestionServico;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public Boolean getRefazerPesquisa() {
		return refazerPesquisa;
	}

	public void setRefazerPesquisa(Boolean refazerPesquisa) {
		this.refazerPesquisa = refazerPesquisa;
	} 


	public DynamicDataModel<ScoAutorizacaoForn> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoAutorizacaoForn> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
	
	public DominioSimNao getPendente() {
		return pendente;
	}

	public void setPendente(DominioSimNao pendente) {
		this.pendente = pendente;
	}

	public DominioSimNao getVencida() {
		return vencida;
	}

	public void setVencida(DominioSimNao vencida) {
		this.vencida = vencida;
	}

	public Boolean getRetornoEdicao() {
		return retornoEdicao;
	}

	public void setRetornoEdicao(Boolean retornoEdicao) {
		this.retornoEdicao = retornoEdicao;
	}	
	
	
}