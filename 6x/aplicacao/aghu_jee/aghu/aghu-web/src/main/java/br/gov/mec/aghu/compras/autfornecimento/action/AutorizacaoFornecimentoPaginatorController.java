package br.gov.mec.aghu.compras.autfornecimento.action;

import java.text.MessageFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.GeracaoAfVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class AutorizacaoFornecimentoPaginatorController extends	ActionController implements ActionPaginator {

	private static final String PROCESSO_ADM_COMPRA_CRUD = "compras-processoAdmCompraCRUD";

	private static final String CONSULTAR_PROPOSTAS_VENCEDORAS = "consultarPropostasVencedoras";

	private static final String GER_AUT_FORNECIMENTO_ITENS_PAC = "gerAutFornecimentoItensPac";

	private static final String PESQUISA_AUTORIZACAO_FORNECIMENTO_LIST = "pesquisaAutorizacaoFornecimentoList";

	@Inject @Paginator
	private DynamicDataModel<GeracaoAfVO> dataModel;

	private static final long serialVersionUID = -2253382642451464107L;

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	

	private ScoLicitacao pac = new ScoLicitacao();
	private ScoGrupoMaterial grupoMaterial;
	private Boolean indProcNaoAptoGerAutForn = false;
	private Integer numeroPacSelecionado;
	private DominioModalidadeEmpenho modalidadeEmpenhoSelecionada;
	
	
	// URL para botão voltar
	private String voltarPara;

	private Integer numeroPac;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
		if (numeroPac != null){
			this.pac.setNumero(numeroPac);
			this.pesquisar();
		}
	}
	
	
	public void pesquisar() {
		this.reiniciarPaginator();
	}
	
	public void limpar() {
		pac = new ScoLicitacao();
		this.grupoMaterial = null;
		this.setIndProcNaoAptoGerAutForn(false);
		this.setAtivo(false);
	}
	
	@Override
	public Long recuperarCount() {
		try{
			return Long.valueOf(this.autFornecimentoFacade.listarPacsParaAFCount(pac, grupoMaterial, indProcNaoAptoGerAutForn));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}

	@Override
	public List<GeracaoAfVO> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String order, boolean asc) {
		
		try {
			List<GeracaoAfVO> lista = this.autFornecimentoFacade.listarPacsParaAF(firstResult, maxResult, order, asc, 
					pac, grupoMaterial, indProcNaoAptoGerAutForn);
			return lista;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}	
		
		return null;
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	
	
	
	public String visualizarItensPac(){
		return GER_AUT_FORNECIMENTO_ITENS_PAC;
	}
	
	public String pesquisarAutorizacaoFornecimento(){
		return PESQUISA_AUTORIZACAO_FORNECIMENTO_LIST;
	}

	public String processarAdmCompra(){
		return PROCESSO_ADM_COMPRA_CRUD;
	}
	
	public String redirecionarConsultarPropostasVencedoras(){
		return CONSULTAR_PROPOSTAS_VENCEDORAS;
	}
	
	//Métodos para carregar suggestion 
	//Modalidade Licitação - Ativas
	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacao(String modalidade) {
		return this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(modalidade);
	}

	//Suggestion Servidor Gestor
	public List<RapServidores> pesquisarGestor(String parametro) {
		return this.returnSGWithCount(this.registroColaboradorFacade.pesquisarResponsaveis(parametro),pesquisarGestorCount(parametro));
	}
	
	public Long pesquisarGestorCount(String parametro){
		return this.registroColaboradorFacade.pesquisarResponsaveisCount(parametro);
	}

	//Suggestion Grupo Material
	public List<ScoGrupoMaterial> pesquisarGrupoMaterial(String parametro){
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoMaterialPorFiltro(parametro),pesquisarGrupoMaterialCount(parametro));
	}
	
	public Long pesquisarGrupoMaterialCount(String parametro){
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroCount(parametro);
	}
	
	public String gerarAf(){
		try{
			this.autFornecimentoFacade.gerarAf(this.getNumeroPacSelecionado(), this.getModalidadeEmpenhoSelecionada());	
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GERACAO_AF");
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}		
		return PESQUISA_AUTORIZACAO_FORNECIMENTO_LIST;
	}
	
	public String montarMensagemsModal() {
		try {
			String msg = this.getBundle().getString("GERAR_AF_PAC_01");
			
			ScoLicitacao licitacao = this.pacFacade.obterLicitacao(this.numeroPacSelecionado);

			if(licitacao!=null && licitacao.getModalidadeEmpenho()!=null){
				// Faz a interpolacao de parametros na mensagem
				msg = MessageFormat.format(msg, licitacao.getModalidadeEmpenho().getDescricao());	
			} 
			return msg;
		} catch (Exception e) {
			return "GERAR_AF_PAC_01";
		}
	}
	
	public Boolean verificarItemProposta(ScoLicitacao item) {
		if (item != null) { 
			//A verificação do item será feita somente se a opção de Ver Processos nao aptos, estiver DESMARCADA
			if (!this.indProcNaoAptoGerAutForn) {
				return this.pacFacade.verificarItemProposta(item);
			}
			else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public ScoLicitacao getPac() {
		return pac;
	}

	public void setPac(ScoLicitacao pac) {
		this.pac = pac;
	}
	
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}
	
	public Boolean getIndProcNaoAptoGerAutForn() {
		return indProcNaoAptoGerAutForn;
	}

	public void setIndProcNaoAptoGerAutForn(Boolean indProcNaoAptoGerAutForn) {
		this.indProcNaoAptoGerAutForn = indProcNaoAptoGerAutForn;
	}

	public IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	public void setAutFornecimentoFacade(
			IAutFornecimentoFacade autFornecimentoFacade) {
		this.autFornecimentoFacade = autFornecimentoFacade;
	}

	public Integer getNumeroPacSelecionado() {
		return numeroPacSelecionado;
	}

	public void setNumeroPacSelecionado(Integer numeroPacSelecionado) {
		this.numeroPacSelecionado = numeroPacSelecionado;
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

	public DominioModalidadeEmpenho getModalidadeEmpenhoSelecionada() {
		return modalidadeEmpenhoSelecionada;
	}

	public void setModalidadeEmpenhoSelecionada(
			DominioModalidadeEmpenho modalidadeEmpenhoSelecionada) {
		this.modalidadeEmpenhoSelecionada = modalidadeEmpenhoSelecionada;
	}

	public DynamicDataModel<GeracaoAfVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<GeracaoAfVO> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}
	
}