package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ProcessoAdmComprasPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6311943654533755944L;

	private static final String PAGE_PROCESSO_ADM_COMPRA_CRUD = "processoAdmCompraCRUD";
	private static final String PAGE_ANEXAR_DOC_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;


	@Inject
	private SecurityController securityController;

	@Inject @Paginator
	private DynamicDataModel<ScoLicitacao> dataModel;

	private ScoLicitacao licitacao = new ScoLicitacao();
	private ScoLicitacao licitacaoSelecionado;
	private DominioSimNao urgente;
	private DominioSimNao exclusao = DominioSimNao.N;
	private Boolean exibirNovo = false;
	private Boolean exibirLista = false;
	private Long count = Long.valueOf(0);
	
	//#27330
	private Boolean gerouArquivo = Boolean.FALSE;
	private String fileName;
	private List<ScoLicitacao> listaExcel;	
	private static final String EXTENSAO_CSV = ".csv";
	private static final String CONTENT_TYPE_CSV = "text/csv";	

	private Date dataInicioGer;
	private Date dataFimGer;

	public enum ProcessoAdmComprasPaginatorControllerExceptionCode implements BusinessExceptionCode {
		ERRO_PESQUISA_DATAS_PAC;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		dataModel.setUserEditPermission(securityController.usuarioTemPermissao("cadastrarPAC", "gravar"));
        this.dataModel.setPageRotate(true);
	}
	

	public void pesquisar() throws ApplicationBusinessException {
		exibirLista = true;
		
		this.setaVariaveisComboBox();
		if (this.verificarDatas()) {
			dataModel.reiniciarPaginator();
		}
		
		count = this.pacFacade.listarProcessosAdmCompraCount(licitacao, dataInicioGer, dataFimGer);
		
	}

	public String novo() {
		return PAGE_PROCESSO_ADM_COMPRA_CRUD;
	}

	public String editar() {
		return PAGE_PROCESSO_ADM_COMPRA_CRUD;
	}

	public String visualizar() {
		return PAGE_PROCESSO_ADM_COMPRA_CRUD;
	}

	public String anexar() {
		return PAGE_ANEXAR_DOC_SOLICITACAO_COMPRA;
	}

	public void setaVariaveisComboBox() {

		this.licitacao.setExclusao(exclusao == null ? null : exclusao.isSim());
		this.licitacao.setIndUrgente(urgente == null ? null : urgente.isSim());

		if (this.dataInicioGer != null && this.dataFimGer == null) {
			this.dataFimGer = new Date();
		}
		
		if (this.dataInicioGer!=null && this.dataFimGer==null){
			this.dataFimGer = new Date();
		}		
	}

	public void limpar() {
		licitacao = new ScoLicitacao();		
		exibirNovo = false;
		exibirLista = false;
		urgente = null;
		exclusao = null;
		this.dataFimGer = null;
		this.dataInicioGer = null;
		
		dataModel.limparPesquisa();
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	@Override
	public Long recuperarCount() {

		try {
			return this.pacFacade.listarProcessosAdmCompraCount(licitacao, dataInicioGer, dataFimGer);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}

	@Override
	public List<ScoLicitacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String order, boolean asc) {

		try {
			return this.pacFacade.listarProcessosAdmCompra(firstResult, maxResult, order, asc, licitacao, dataInicioGer,
					dataFimGer);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public boolean verificarDatas() {
		if (this.dataFimGer != null && this.dataInicioGer != null) {
			if (this.dataFimGer.before(this.dataInicioGer)) {
				this.apresentarMsgNegocio(Severity.INFO,
						ProcessoAdmComprasPaginatorControllerExceptionCode.ERRO_PESQUISA_DATAS_PAC.toString());
				return false;
			}
		}

		return true;

	}

	// Verifica se existem arquivos relacionados ao PAC ou as suas solicitações
	// de Compra ou Serviço ou aos seus materiais ou serviços
	public Boolean verificarExisteArquivoRelacionadoPAC(Integer numLicitacao) {
		Boolean existeArquivo = false;

		existeArquivo = solicitacaoComprasFacade.verificarExistenciaArquivosPorNumeroOrigem(
				DominioOrigemSolicitacaoSuprimento.PC, numLicitacao);

		return existeArquivo;
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

	// Suggestion Servidor Gestor
	public List<RapServidores> pesquisarGestorPac(String parametro) {
		return this.registroColaboradorFacade.pesquisarResponsaveis(parametro);
	}

	public Boolean getExibirNovo() {
		return exibirNovo;
	}

	public void setExibirNovo(Boolean exibirNovo) {
		this.exibirNovo = exibirNovo;
	}

	public DominioSimNao getUrgente() {
		return urgente;
	}

	public DominioSimNao getExclusao() {
		return exclusao;
	}

	public void setUrgente(DominioSimNao urgente) {
		this.urgente = urgente;
	}

	public void setExclusao(DominioSimNao exclusao) {
		this.exclusao = exclusao;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public ScoLicitacao getLicitacaoSelecionado() {
		return licitacaoSelecionado;
	}

	public void setLicitacaoSelecionado(ScoLicitacao licitacaoSelecionado) {
		this.licitacaoSelecionado = licitacaoSelecionado;
	}

	public DynamicDataModel<ScoLicitacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoLicitacao> dataModel) {
		this.dataModel = dataModel;
	}
	
	public Date getDataInicioGer() {
		return dataInicioGer;
	}

	public void setDataInicioGer(Date dataInicioGer) {
		this.dataInicioGer = dataInicioGer;
	}

	public Date getDataFimGer() {
		return dataFimGer;
	}

	public void setDataFimGer(Date dataFimGer) {
		this.dataFimGer = dataFimGer;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public List<ScoLicitacao> getListaExcel() {
		return listaExcel;
	}

	public void setListaExcel(List<ScoLicitacao> listaExcel) {
		this.listaExcel = listaExcel;
	}

	
	public void gerarArquivo() throws ApplicationBusinessException {
		listaExcel = this.pacFacade.listarProcessosAdmCompra(null, null, null, false, licitacao, dataInicioGer, dataFimGer);
		
		try {
			fileName = pacFacade.geraArquivoPAC(listaExcel);
			gerouArquivo = Boolean.TRUE;
			this.dispararDownload();			
			
		} catch(IOException e) {
			gerouArquivo = Boolean.FALSE;
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
	}
	
	/**
	 * Dispara o download para o arquivo CSV do relatório.
	 */
	public void dispararDownload(){
		if(fileName != null){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Calendar c1 = Calendar.getInstance(); // today
				this.download(fileName, "LISTA_PACS_"+sdf.format(c1.getTime())+EXTENSAO_CSV, CONTENT_TYPE_CSV);
				setGerouArquivo(Boolean.FALSE);
				fileName = null;				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}	
	

	public Boolean getExibirLista() {
		return exibirLista;
	}

	public void setExibirLista(Boolean exibirLista) {
		this.exibirLista = exibirLista;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
		
}
