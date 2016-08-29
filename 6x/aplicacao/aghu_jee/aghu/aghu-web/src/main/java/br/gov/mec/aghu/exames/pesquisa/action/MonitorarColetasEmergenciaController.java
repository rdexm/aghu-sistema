package br.gov.mec.aghu.exames.pesquisa.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.IdentificarUnidadeExecutoraController;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.SolicitacaoColetarVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

import com.itextpdf.text.DocumentException;



public class MonitorarColetasEmergenciaController  extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 9130455978081098619L;

	private static final Log LOG = LogFactory.getLog(MonitorarColetasEmergenciaController.class);

	private static final String MONITORAR_COLETAS_EMERGENCIA_PESQUISA = "exames-monitorarColetasEmergenciaPesquisa";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@Inject
	private IdentificarUnidadeExecutoraController unidadeExecutoraController; 
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	private StreamedContent streamedContent;
	
	// Referencias especificas
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private AghUnidadesFuncionais unidadeExecutora;
	private List<VAelSolicAtendsVO> listaSolicitacoesColeta;
	private VAelSolicAtendsVO solicitacaoColetaSelecionada;
	private List<AelItemSolicitacaoExames>  listaItensProgramadosUrgentes;
	private Integer itemSolicitacaoColetaSelecionadoId;
	private boolean ativarPesquisaAutomatica;
	
	private List<SolicitacaoColetarVO> colecao = new ArrayList<SolicitacaoColetarVO>();
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 


		// Reseta instancias
		this.listaSolicitacoesColeta = null;
		this.listaItensProgramadosUrgentes = null;
		this.itemSolicitacaoColetaSelecionadoId = null;
		
		// Obtem a unidade executora atraves do usuario logado
		this.obterIdentificacaoUnidadeExecutora();
		
		// Caso a unidade executora associada ao usuario exista: realiza pesquisa automatica de solicitacoes de coleta
		if(this.unidadeExecutora != null){
			this.pesquisarSolicitacoesColeta();
		}
	}
	
	private void processarImpressao() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		streamedContent  = this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		
		// Imprime as etiquetas da amostra quando imprimir solicitações a coletar
		if(this.colecao != null && !this.colecao.isEmpty()){
			try {
				String nomeMicrocomputador = getEnderecoRedeHostRemoto();
				
				for (SolicitacaoColetarVO vo : this.colecao) {
					Integer soeSeq = Integer.valueOf(vo.getSoeSeq());
					Short seqp = Short.valueOf(vo.getSeqp());
					AelAmostras amostra = this.examesFacade.buscarAmostrasPorId(soeSeq, seqp);
					examesFacade.imprimirEtiquetaAmostra(amostra, this.unidadeExecutora, nomeMicrocomputador);
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (UnknownHostException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	
	/**
	 * Pesquisa AUTOMATICA de soliciticacoes de soleta.
	 * Chamado pelo compomente responsavel por realizar o REFRESH da pesquisa. 
	 */
	public void pesquisaAutomatica(){
		if(this.unidadeExecutora != null && this.ativarPesquisaAutomatica){
			this.pesquisarSolicitacoesColeta();
		}
	}
	
	/**
	 * Pesquisa Soliciticacoes de Coleta. Chamado automaticamente na acao "posSelectionAction" da Sugestion Box de unidade executora
	 */
	public void pesquisarSolicitacoesColeta() {

		try {

			// Desativa pesquisa/refresh automatico durante a pesquisa
			this.ativarPesquisaAutomatica = false;

			// Persiste identificacao da unidade executora atraves do usuario logado
			this.persistirIdentificacaoUnidadeExecutora();

			// Limpa identificador de item selecionado a lista de solicitacoes de coleta
			this.itemSolicitacaoColetaSelecionadoId = null;

			// Limpa lista inferior de itens programados e urgentes
			this.listaItensProgramadosUrgentes = null;

			// Pesquisa solicitacoes de coleta
			try {
				this.listaSolicitacoesColeta = this.pesquisaExamesFacade.pesquisaMonitoramentoColetasEmergencia(this.unidadeExecutora);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}

			// Realiza a pesquisa automatica dos itens programados do primeiro registro da lista de solicitacao de coletas
			if (this.listaSolicitacoesColeta != null && !this.listaSolicitacoesColeta.isEmpty()) {
				this.pesquisarSolicitacoesColetaItensProgramados(listaSolicitacoesColeta.get(0));
			}

		} finally {
			// Ativa pesquisa/refresh automatica
			this.ativarPesquisaAutomatica = true;
		}

	}
	
	
	/**
	 * Obtem a unidade executora atraves do usuario logado
	 */
	public void obterIdentificacaoUnidadeExecutora(){
		
		// Obtem o USUARIO da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			usuarioUnidadeExecutora = null;
		}

		// Resgata a unidade executora associada ao usuario
		if(this.usuarioUnidadeExecutora != null){
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}
	}
	

	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora(){
		try {
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 *  Pesquisa itens programadados e urgentes de uma soliciticacao de coleta. 
	 *  Chamado ao clicar em uma linha da tabela de Soliciticacoes de Coleta.
	 */
	public void pesquisarSolicitacoesColetaItensProgramados(VAelSolicAtendsVO vo) {
		
		// Seta uma referencia do item selecionado da lista de solicitacoes de coleta
		this.itemSolicitacaoColetaSelecionadoId = vo.getNumero();
		
		// Pesquisa itens programados registro selecionado na lista de solicitacao de coletas
		try {
			this.listaItensProgramadosUrgentes = this.pesquisaExamesFacade.pesquisaMonitoramentoColetasEmergenciaItensProgramados(this.unidadeExecutora, vo);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	
	public void pesquisarSolicitacoesColetaItensProgramadosaAtualizar() {
		
		// Seta uma referencia do item selecionado da lista de solicitacoes de coleta
		this.itemSolicitacaoColetaSelecionadoId = solicitacaoColetaSelecionada.getNumero();
		
		// Pesquisa itens programados registro selecionado na lista de solicitacao de coletas
		try {
			this.listaItensProgramadosUrgentes = this.pesquisaExamesFacade.pesquisaMonitoramentoColetasEmergenciaItensProgramados(this.unidadeExecutora, solicitacaoColetaSelecionada);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/*
	 * Metodos para Suggestion Box...
	 */
	
	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa){
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}	
	
	/**
	 * Detalha solicitacao de coleta
	 * @param soeSeq
	 * @return
	 */
	public String detalharSolicitacaoColeta(Integer soeSeq){
		setItemSolicitacaoColetaSelecionadoId(soeSeq);
		return "exames-pesquisaDetalhesSolicitacaoExame";
	}
	
	/**
	 * Imprime solicitacao de coleta
	 * @param soeSeq
	 * @throws DocumentException 
	 */
	public String imprimirSolicitacaoColeta(Integer soeSeq) throws BaseException, JRException, SystemException, IOException, DocumentException {
		 List<Integer> listaSoeSeq = new ArrayList<Integer>();
		 listaSoeSeq.add(soeSeq);
		 
		 String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
		 
		 try{
			 this.colecao = solicitacaoExameFacade.imprimirSolicitacoesColetar(listaSoeSeq,unidadeExecutora, nomeMicrocomputador);
		 
			// #9418 - Contigencia Consular Últimos Arquivos Impressos
			if (this.colecao != null && !this.colecao.isEmpty()){
				this.imprimirRelatorioCopiaSeguranca(true);
				processarImpressao();
				return "exames-relatorioListarSolicitacoesColeta";
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
			
		} catch (DocumentException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_IMPRIMIR_SOLICITACAO_COLETA");
		}
		 
		this.apresentarMsgNegocio(Severity.WARN,"AEL_00895");
		
		return null;
	}
	
	/**
	 * Visualiza impressao de todos os itens de solicitacao de coleta
	 * @param soeSeq
	 */
	public String visualizarImpressaoTodosItens() throws BaseException, JRException, SystemException, IOException {
		List<Integer> listaSoeSeq = new ArrayList<Integer>();
		for(VAelSolicAtendsVO vo :listaSolicitacoesColeta){
			listaSoeSeq.add(vo.getNumero());
		}
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		
		try{
			colecao = solicitacaoExameFacade.imprimirSolicitacoesColetar(listaSoeSeq,unidadeExecutora, nomeMicrocomputador);
			
			// #9418 - Contigencia Consular Últimos Arquivos Impressos
			if (this.colecao != null && !this.colecao.isEmpty()){
				processarImpressao();
				this.imprimirRelatorioCopiaSeguranca(true);
				return "exames-relatorioListarSolicitacoesColeta";
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (DocumentException e) {
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
		this.apresentarMsgNegocio(Severity.WARN,"AEL_00895");
		return null;
	}
	
	@Override
	public Collection<SolicitacaoColetarVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_MAT_A_COLETAR");
		params.put("tituloRelatorio", "Materiais a coletar pelo coletador da "+unidadeExecutora.getDescricao());
		if(colecao != null && !colecao.isEmpty()) {
			params.put("totalRegistros", colecao.size()-1);
		}
		params
		.put(
				"subRelatorio",
				Thread
						.currentThread()
						.getContextClassLoader().getResource("br/gov/mec/aghu/exames/report/relatorioListarSolicitacoesColetar_subreport1.jasper"));

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioListarSolicitacoesColetar.jasper";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() {
		return streamedContent;
	}
	
	/**
	 * Metodo para limpeza da suggestion box de unidade executora
	 */
	public void limparAghUnidadesFuncionaisExecutoras(){
		this.unidadeExecutora = null;
		this.listaSolicitacoesColeta = null;
		this.listaItensProgramadosUrgentes = null;
		this.itemSolicitacaoColetaSelecionadoId = null;
	}

	public String voltar(){
		return MONITORAR_COLETAS_EMERGENCIA_PESQUISA;
	}
	
	
	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(
			AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public IdentificarUnidadeExecutoraController getUnidadeExecutoraController() {
		return unidadeExecutoraController;
	}

	public void setUnidadeExecutoraController(
			IdentificarUnidadeExecutoraController unidadeExecutoraController) {
		this.unidadeExecutoraController = unidadeExecutoraController;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public List<VAelSolicAtendsVO> getListaSolicitacoesColeta() {
		return listaSolicitacoesColeta;
	}

	public void setListaSolicitacoesColeta(
			List<VAelSolicAtendsVO> listaSolicitacoesColeta) {
		this.listaSolicitacoesColeta = listaSolicitacoesColeta;
	}

	public List<AelItemSolicitacaoExames> getListaItensProgramadosUrgentes() {
		return listaItensProgramadosUrgentes;
	}

	public void setListaItensProgramadosUrgentes(
			List<AelItemSolicitacaoExames> listaItensProgramadosUrgentes) {
		this.listaItensProgramadosUrgentes = listaItensProgramadosUrgentes;
	}

	public Integer getItemSolicitacaoColetaSelecionadoId() {
		return itemSolicitacaoColetaSelecionadoId;
	}

	public void setItemSolicitacaoColetaSelecionadoId(
			Integer itemSolicitacaoColetaSelecionadoId) {
		this.itemSolicitacaoColetaSelecionadoId = itemSolicitacaoColetaSelecionadoId;
	}

	public boolean isAtivarPesquisaAutomatica() {
		return ativarPesquisaAutomatica;
	}

	public void setAtivarPesquisaAutomatica(boolean ativarPesquisaAutomatica) {
		this.ativarPesquisaAutomatica = ativarPesquisaAutomatica;
	}

	public VAelSolicAtendsVO getSolicitacaoColetaSelecionada() {
		return solicitacaoColetaSelecionada;
	}

	public void setSolicitacaoColetaSelecionada(
			VAelSolicAtendsVO solicitacaoColetaSelecionada) {
		this.solicitacaoColetaSelecionada = solicitacaoColetaSelecionada;
	}
}