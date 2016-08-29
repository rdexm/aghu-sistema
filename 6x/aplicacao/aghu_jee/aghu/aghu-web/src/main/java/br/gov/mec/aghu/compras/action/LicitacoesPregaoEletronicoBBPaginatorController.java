package br.gov.mec.aghu.compras.action;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.action.EncaminhaLicitacoesLiberarController;
import br.gov.mec.aghu.compras.pac.action.ProcessoAdmComprasController;
import br.gov.mec.aghu.compras.pac.action.ProcessoAdmComprasPaginatorController;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class LicitacoesPregaoEletronicoBBPaginatorController extends ActionController implements ActionPaginator  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 13211L;
	
	private static final String IMPRIMIR_HISTORICO_RESUMIDO_LICITACAO = "imprimirHistoricoResumidoLicitacao";
	
	private static final String IMPRIMIR_HISTORICO_DETALHADO_LICITACAO = "imprimirHistoricoDetalhadoLicitacao";
	
	private static final String IMPRIMIR_HISTORICO_CADASTRADA_LICITACAO = "imprimirHistoricoLicitacaoCadastrada";	
	
	private static final String PAGE_CONSULTA_PROPOSTA_COMPRA = "compras-encaminharLicitacoesLiberar";
	
	private static final String PAGE_LICITACOES_PREGAO = "compras-licitacoesPregaoEletronicoBBList";

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB 
	private IPacFacade pacFacade;
	
	@EJB
	protected IParametroFacade parametroFacade;
	
	@Inject @Paginator
	private DynamicDataModel<ScoLicitacaoVO> dataModel;
	
	@Inject 
	private ProcessoAdmComprasPaginatorController processoAdmComprasPaginatorController;
	
	@Inject
	private ProcessoAdmComprasController processoAdmComprasController;

	@Inject
	private ImprimirSolicitacaoPregaoEletronicoBBController imprimirSolicitacaoPregaoEletronicoBBController;
		
	@Inject
	private EncaminhaLicitacoesLiberarController encaminhaLicitacoesLiberarController;
		
	private Date dtInicioGeracaoPAC;
	private Date dtFimGeracaoPAC;
	private Date dtInicioGeracaoArqRem;
	private Date dtFimGeracaoArqRem;
	private Boolean pacPendenteEnvio;
	private Boolean pacPendenteRetorno;
	private List<ScoLicitacaoVO> listaScoLicitacaoVO;
	private List<Integer> licitacoesSelecionadas;
	private RapServidoresVO rapServidoresVO;
	private ScoLicitacaoVO scoLicitacaoVOFiltro;
	private ScoLicitacaoVO scoLicitacaoMouseOver;
	private ScoModalidadeLicitacao modalidade;
	private ScoLicitacao scolicitacaoSuggestion;
	private boolean allChecked;
	private List<ScoLicitacaoVO> licitacaoSelecionada;

	private Integer numPacSelecionado;
	
	private Boolean mostrarModal = Boolean.FALSE;
	private Boolean isModalLote = Boolean.TRUE;
	private String mensagemModal = StringUtils.EMPTY;
	private AghParametros sitePregao = null;
	private static final String URL_TELA_LOTE = "gerarLotesDoPAC";
	private List<Long> nrosPac;
	private StreamedContent media;
	
	private UploadedFile arquivoCarregado;
	private byte[] arquivo;

	private Boolean habilitaConsultarProposta = Boolean.FALSE;
	
	private Integer nroPAC;
	private String voltarParaUrl;
		
	@PostConstruct
	public void inicializar() {
		begin(conversation);
		listaScoLicitacaoVO = new ArrayList<ScoLicitacaoVO>();
		scoLicitacaoVOFiltro = new ScoLicitacaoVO();
		modalidade = null;
		allChecked = false;
		licitacoesSelecionadas = new ArrayList<Integer>();
		licitacaoSelecionada = new ArrayList<ScoLicitacaoVO>();
		scolicitacaoSuggestion = null;
		dtInicioGeracaoPAC = null;
		dtFimGeracaoPAC = null;
		dtInicioGeracaoArqRem = null;
		pacPendenteEnvio = false;
		pacPendenteRetorno = false;
		dtFimGeracaoArqRem = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		habilitaConsultarProposta = Boolean.FALSE;
	}

	
	public void iniciar(){
		if (nroPAC != null){
		   this.scolicitacaoSuggestion = this.comprasFacade.buscarLicitacaoPorNumero(nroPAC);
		   this.pesquisar();
		}
	}
	
	// INICIO - checkbox grid
	
	//MARCA DESMARCA TODOS
	public void checkAll(){
		licitacoesSelecionadas = new ArrayList<Integer>();
		if(allChecked){
			for(ScoLicitacaoVO vo: listaScoLicitacaoVO){
				licitacoesSelecionadas.add(vo.getNumeroPAC());
				licitacaoSelecionada.add(vo);
			}
		}
	}
	//MARCA DESMARCA 1
	public void checkItem(ScoLicitacaoVO item){
		if(licitacoesSelecionadas.contains(item.getNumeroPAC())){
			licitacoesSelecionadas.remove(item.getNumeroPAC());
			licitacaoSelecionada.remove(item);
		}else{
			licitacoesSelecionadas.add(item.getNumeroPAC());
			//licitacaoSelecionada = new ArrayList<ScoLicitacaoVO>();
			licitacaoSelecionada.add(item);
		}
		alterarSelecaoNaListaVO();
	}
	
	//ALTERA A SELECAO NA LISTA
	private void alterarSelecaoNaListaVO(){
		for(ScoLicitacaoVO vo:listaScoLicitacaoVO){
			if(licitacoesSelecionadas.contains(vo.getNumeroPAC())){
				vo.setSelecionado(true);
			}else{
				vo.setSelecionado(false);
			}
		}
		if (licitacoesSelecionadas != null && !licitacoesSelecionadas.isEmpty() && licitacoesSelecionadas.size() == 1) {
			habilitaConsultarProposta  = Boolean.TRUE;
		} else {
			habilitaConsultarProposta = Boolean.FALSE;
		}
		
	}
	
	//RETORNA TODAS LINHAS SELECIONADOS
	public List<ScoLicitacaoVO> obterTodasLicitacoesSelecionadas(){
		List<ScoLicitacaoVO> lista = new ArrayList<ScoLicitacaoVO>();
		for(ScoLicitacaoVO vo:listaScoLicitacaoVO){
			if(licitacoesSelecionadas.contains(vo.getNumeroPAC())){
				lista.add(vo);
			}
		}
		return lista;
	}
	
	// FIM - checkbox grid
	
	@Override
	public Long recuperarCount() {
		setarDatas();
		return 	 comprasFacade.pesquisarLicitacoesVOCount(scoLicitacaoVOFiltro, dtInicioGeracaoPAC, dtFimGeracaoPAC, dtInicioGeracaoArqRem, dtFimGeracaoArqRem, pacPendenteEnvio, pacPendenteRetorno);
	}
	
	
	//setar Data com hora final caso não seja nulo
	
	public void setarDatas(){
		if(dtFimGeracaoPAC!=null){
			dtFimGeracaoPAC=DateUtil.obterDataComHoraFinal(dtFimGeracaoPAC);
		}
		if(dtFimGeracaoArqRem!=null){
			dtFimGeracaoArqRem=DateUtil.obterDataComHoraFinal(dtFimGeracaoArqRem);
		}
	}

	@Override
	public List<ScoLicitacaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		setarDatas();
		listaScoLicitacaoVO = comprasFacade.pesquisarLicitacoesVO(firstResult, maxResult, orderProperty, asc, scoLicitacaoVOFiltro, dtInicioGeracaoPAC, dtFimGeracaoPAC, dtInicioGeracaoArqRem, dtFimGeracaoArqRem, pacPendenteEnvio, pacPendenteRetorno);
		alterarSelecaoNaListaVO();
		return listaScoLicitacaoVO;
	}
	
	public String manterPAC(){
		ScoLicitacao licitacaoPac = null;

		processoAdmComprasController.setNumeroPac(numPacSelecionado);
		
		licitacaoPac = pacFacade.obterLicitacao(numPacSelecionado);
		processoAdmComprasPaginatorController.setLicitacaoSelecionado(licitacaoPac);
		
		processoAdmComprasController.setVoltarParaUrl("compras-licitacoesPregaoEletronicoBBList");

		return processoAdmComprasPaginatorController.editar();
	}
	
	public void setarVinculo(){
		scoLicitacaoVOFiltro.setVinculoGestor(rapServidoresVO.getVinculo()); 
	}
	
	public void limparVinculo(){
		scoLicitacaoVOFiltro.setVinculoGestor(null);
		scoLicitacaoVOFiltro.setMatriculaGestor(null);
	}
	
	//C5   SB2
	public List<ScoModalidadeLicitacao> pesquisarModalidadesProcessoAdministrativo(String parametro){
		return returnSGWithCount(comprasFacade.pesquisarModalidadesProcessoAdministrativo(parametro), comprasFacade.pesquisarModalidadesProcessoAdministrativoCount(parametro));
	}
	
	//C3   SB3
	public List<RapServidoresVO> pesquisarGestores(String parametro){
		return  returnSGWithCount(comprasFacade.pesquisarGestorPorNomeOuMatricula(parametro), comprasFacade.pesquisarGestorPorNomeOuMatriculaCount(parametro));
	}
	
	//C2   SB1
	public List<ScoLicitacao> pesquisarLicitacoesPorNumero(String parametro){
		return returnSGWithCount(comprasFacade.pesquisarLicitacoesPorNumero(parametro), comprasFacade.pesquisarLicitacoesPorNumeroCount(parametro));
	}
	
	public void limparPesquisa() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		dtInicioGeracaoPAC = null;
		dtFimGeracaoPAC = null;
		dtInicioGeracaoArqRem = null;
		dtFimGeracaoArqRem = null;
		pacPendenteEnvio = false;
		pacPendenteRetorno = false;
		allChecked = false;
		licitacoesSelecionadas.clear();
		licitacaoSelecionada.clear();
		rapServidoresVO = null;
		scolicitacaoSuggestion = null;
		modalidade = null;
		this.scoLicitacaoVOFiltro = new ScoLicitacaoVO();
		this.dataModel.limparPesquisa();
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		habilitaConsultarProposta = Boolean.FALSE;
	}
	
	public void limparNumeroPac(){
		scolicitacaoSuggestion = null;
		scoLicitacaoVOFiltro.setNumeroPAC(null);
	}
	
	public void limparModalidade(){
		modalidade = null;
		scoLicitacaoVOFiltro.setModalidadeLicitacao(null);
	}
	
	public void pesquisar() {
		try {
			setarDatas();
			comprasFacade.validarDataPregaoEletronicoBB(dtInicioGeracaoPAC, dtFimGeracaoPAC);
			comprasFacade.validarDataPregaoEletronicoBB(dtInicioGeracaoArqRem, dtFimGeracaoArqRem);
			if(modalidade != null){
				scoLicitacaoVOFiltro.setModalidadeLicitacao(modalidade.getCodigo());
			}
			if(scolicitacaoSuggestion != null){
				scoLicitacaoVOFiltro.setNumeroPAC(scolicitacaoSuggestion.getNumero());
			}
			if(rapServidoresVO != null){
				scoLicitacaoVOFiltro.setMatriculaGestor(rapServidoresVO.getMatricula());
			}
			
			allChecked = false;
			licitacoesSelecionadas.clear();
			licitacaoSelecionada.clear();
			this.reiniciarPaginator();
			dataModel.setPesquisaAtiva(Boolean.TRUE);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Truncar os itens e adiciona o símbolo de reticências (...)
	 * 
	 * @param item
	 * @return
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		String itemCapitalizado = WordUtils.capitalizeFully(item);
		if (itemCapitalizado.length() > tamanhoMaximo) {
			itemCapitalizado = StringUtils.abbreviate(itemCapitalizado, tamanhoMaximo);
		}
			
		return itemCapitalizado;
	}
	
	public String obterHintModalidade(String itemModalidade){
		List<ScoModalidadeLicitacao> lista = comprasFacade.pesquisarModalidadesProcessoAdministrativo("");
		StringBuffer hint = new StringBuffer();
		for (ScoModalidadeLicitacao mod : lista) {
			if(itemModalidade.equals(mod.getCodigo())){
				hint.append(mod.getDescricao());
				break;
			}
		}
		return hint.toString();
	}
	
	public String obterHintSituacao(ScoLicitacaoVO item){
		if(item != null){
			return item.getSituacao().getDescricaoParaHint();
		}
		return null;
	}
				  
	public String obterHintNomeArqRetorno(ScoLicitacaoVO item){
		StringBuffer hint = new StringBuffer("PAC's constantes nesse arquivo:");
		if(item != null && item.getNomeArqRetorno() != null && !item.getNomeArqRetorno().equals("")){
			List<ScoLicitacao> lista = comprasFacade.pesquisarLicitacoesPorNomeArquivoRetorno(item.getNomeArqRetorno());
			for (ScoLicitacao lic : lista) {
				String s = StringUtils.LF+lic.getNumero();
				hint.append(s);
			}
		}
		return hint.toString();
	}
	
	/**
	 * 
	 * Percorre o formulÃ¡rio resetando os valores digitados nos campos
	 * (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * 
	 * 
	 * @param object
	 *            {@link Object}
	 */

	private void limparValoresSubmetidos(Object object) {
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		Iterator<UIComponent> uiComponent = ((UIComponent) object)
				.getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	public String acaoBotaoSimLote(){
		return URL_TELA_LOTE;
	}
		
	public void gerarTxt(){
		try {
			AghParametros ParamDir = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PG_BB_DIR_REM);
			String msgRetorno = comprasFacade.montarArqPregaoBB(licitacoesSelecionadas, ParamDir);
			if(msgRetorno.equals(getBundle().getString("MS06_MERCADORIA_BB_SEM_CONTEUDO")) || msgRetorno.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "MS01_ARQUIVO_GERADO", ParamDir.getVlrTexto());
				sitePregao = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PG_BB_SITE);
				if(sitePregao != null){
					mensagemModal = getBundle().getString("MS07_DESEJA_ENVIAR_LICITACOES");
					isModalLote = Boolean.FALSE;
				}
				if(msgRetorno.equals(getBundle().getString("MS06_MERCADORIA_BB_SEM_CONTEUDO"))){
					apresentarMsgNegocio(Severity.WARN, "MS06_MERCADORIA_BB_SEM_CONTEUDO");
				}
				
			} else {
				mensagemModal = msgRetorno;
				isModalLote = Boolean.TRUE;
			}
			openDialog("panelModalEnviarOuLotesWG");
			mostrarModal = Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			mostrarModal = Boolean.FALSE;
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gerarArquivoRem(){
		try {
			AghParametros  P_PG_BB_ParamDir = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PG_BB_DIR_REM);
			String msgRetorno = comprasFacade.montarSolCodBB(licitacoesSelecionadas,  P_PG_BB_ParamDir);
			if(msgRetorno.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "ARQUIVO_GERADO");
			}else{
				apresentarMsgNegocio(Severity.WARN, "PERMISSAO_DIRETORIO", P_PG_BB_ParamDir.getVlrTexto());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void listener(FileUploadEvent event) throws IOException {
		if(event != null){
			this.arquivoCarregado = event.getFile();
			arquivo = IOUtils.toByteArray(event.getFile().getInputstream());
		}
	}
	
	public void dispararDownload() {
		if (this.arquivoCarregado != null) {
			executarDownload();
		}
	}

	private void executarDownload() {
		
		final FacesContext fc = FacesContext.getCurrentInstance();
		final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
		
		response.setContentType(arquivoCarregado.getContentType());
		response.setHeader("Content-Disposition", "attachment;filename=" + arquivoCarregado.getFileName());
		
		ServletOutputStream out = null;
		try {
			
			out = response.getOutputStream();
			response.flushBuffer();
			
			out.write(arquivo);
			out.flush();
			out.close();
			
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			apresentarMsgNegocio(e.getMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public String imprimirHistoricoProcessoAdmComprasLicitacaoDetalhada() throws DocumentException, IOException, JRException  {
		imprimirSolicitacaoPregaoEletronicoBBController.setCameFrom(IMPRIMIR_HISTORICO_DETALHADO_LICITACAO);
		return imprimirHistoricoProcessoAdmComprasLicitacaoHomologada();
	}
	
	public String imprimirHistoricoProcessoAdmComprasLicitacaoResumida() throws DocumentException, IOException, JRException  {
		imprimirSolicitacaoPregaoEletronicoBBController.setCameFrom(IMPRIMIR_HISTORICO_RESUMIDO_LICITACAO);
		return imprimirHistoricoProcessoAdmComprasLicitacaoHomologada();
	}
	
	public String imprimirHistoricoProcessoAdmComprasLicitacaoCadastrada() throws DocumentException, IOException, JRException, ApplicationBusinessException  {
		imprimirSolicitacaoPregaoEletronicoBBController.setCameFrom(IMPRIMIR_HISTORICO_CADASTRADA_LICITACAO);
		return imprimirConsultaLicitacaoCadastrada();
	}
	
	/**
	 * Imprime os relatórios Resumido e Detalhado. 
	 * 
	 * @param nomeRelorio
	 * @return String
	 * @throws ApplicationBusinessException
	 * @throws JRException
	 * @throws IOException
	 * @throws DocumentException
	 * @throws ParseException
	 */
	private String imprimirHistoricoProcessoAdmComprasLicitacaoHomologada() throws DocumentException, IOException, JRException  {
		try {
			comprasFacade.validarSolicitacaoPregaoEletronico(licitacaoSelecionada);
			return imprimirSolicitacaoPregaoEletronicoBBController.iniciar(
					licitacaoSelecionada.get(0).getNumeroPAC(),
					licitacaoSelecionada.get(0).getNomeArqRetorno());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	/**
	 * Imprime o relatório de licitação cadastrada 
	 * 
	 * @param nomeRelorio
	 * @return String
	 * @throws ApplicationBusinessException
	 * @throws JRException
	 * @throws IOException
	 * @throws DocumentException
	 * @throws ParseException
	 */
	private String imprimirConsultaLicitacaoCadastrada() throws DocumentException, IOException, JRException  {
		try {
			licitacaoSelecionada = obterTodasLicitacoesSelecionadas();
			String arqCad = comprasFacade.validarNomeArqCad(licitacaoSelecionada);
			return imprimirSolicitacaoPregaoEletronicoBBController.iniciarLicitacaoCadastrada(licitacaoSelecionada.get(0).getNumeroPAC(), arqCad);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String gerarProposta(){
		try {
			pacFacade.validarSelecaoPregaoEletronico(licitacaoSelecionada);
			pacFacade.gerarPropostaPregaoBB(licitacaoSelecionada.get(0).getNumeroPAC(),
					licitacaoSelecionada.get(0).getNomeArqRetorno());
			ScoLicitacaoVO licitacaoGrid = licitacaoSelecionada.get(0);
			ScoLicitacao licitacaoPesquisa = new ScoLicitacao();
			licitacaoPesquisa.setNumero(licitacaoGrid.getNumeroPAC());
			licitacaoPesquisa.setDescricao(licitacaoGrid.getDescricaoPAC());
			if(licitacaoGrid.getModalidadeLicitacao() != null){
				List<ScoModalidadeLicitacao> modalidadeList = processoAdmComprasPaginatorController
						.pesquisarModalidadeLicitacao(licitacaoGrid.getModalidadeLicitacao().toString());
				if (modalidadeList != null && !modalidadeList.isEmpty()){
					licitacaoPesquisa.setModalidadeLicitacao(modalidadeList.get(0));
				}
			}
			if(licitacaoGrid.getNomeGestor() != null){
				List<RapServidores> servidorGestor = processoAdmComprasPaginatorController.pesquisarGestorPac(licitacaoGrid.getNomeGestor());
				if(servidorGestor != null && !servidorGestor.isEmpty()){
					licitacaoPesquisa.setServidorGestor(servidorGestor.get(0));
				}
			}
			licitacaoPesquisa.setSituacao(licitacaoGrid.getSituacao());
			
			processoAdmComprasPaginatorController.setLicitacao(licitacaoPesquisa);
			
			return encaminharLiberacoes(licitacaoPesquisa.getNumero());
					
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	private String encaminharLiberacoes(Integer numeroLicitacao) {
		encaminhaLicitacoesLiberarController.setNumeroPac(numeroLicitacao);
		encaminhaLicitacoesLiberarController.setVoltarPara(PAGE_LICITACOES_PREGAO);
		return PAGE_CONSULTA_PROPOSTA_COMPRA;
	}

	public String consultarEncaminharLiberacoes() {
		return encaminharLiberacoes(licitacaoSelecionada.get(0).getNumeroPAC());
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public Date getDtInicioGeracaoPAC() {
		return dtInicioGeracaoPAC;
	}

	public void setDtInicioGeracaoPAC(Date dtInicioGeracaoPAC) {
		this.dtInicioGeracaoPAC = dtInicioGeracaoPAC;
	}

	public Date getDtFimGeracaoPAC() {
		return dtFimGeracaoPAC;
	}

	public void setDtFimGeracaoPAC(Date dtFimGeracaoPAC) {
		this.dtFimGeracaoPAC = dtFimGeracaoPAC;
	}

	public Date getDtInicioGeracaoArqRem() {
		return dtInicioGeracaoArqRem;
	}

	public void setDtInicioGeracaoArqRem(Date dtInicioGeracaoArqRem) {
		this.dtInicioGeracaoArqRem = dtInicioGeracaoArqRem;
	}

	public Date getDtFimGeracaoArqRem() {
		return dtFimGeracaoArqRem;
	}

	public void setDtFimGeracaoArqRem(Date dtFimGeracaoArqRem) {
		this.dtFimGeracaoArqRem = dtFimGeracaoArqRem;
	}

	public DynamicDataModel<ScoLicitacaoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoLicitacaoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public List<ScoLicitacaoVO> getListaScoLicitacaoVO() {
		return listaScoLicitacaoVO;
	}

	public void setListaScoLicitacaoVO(List<ScoLicitacaoVO> listaScoLicitacaoVO) {
		this.listaScoLicitacaoVO = listaScoLicitacaoVO;
	}

	public RapServidoresVO getRapServidoresVO() {
		return rapServidoresVO;
	}

	public void setRapServidoresVO(RapServidoresVO rapServidoresVO) {
		this.rapServidoresVO = rapServidoresVO;
	}

	public ScoLicitacaoVO getScoLicitacaoVOFiltro() {
		return scoLicitacaoVOFiltro;
	}

	public void setScoLicitacaoVOFiltro(ScoLicitacaoVO scoLicitacaoVOFiltro) {
		this.scoLicitacaoVOFiltro = scoLicitacaoVOFiltro;
	}

	public boolean isAllChecked() {
		return allChecked;
	}

	public void setAllChecked(boolean allChecked) {
		this.allChecked = allChecked;
	}

	public List<Integer> getLicitacoesSelecionadas() {
		return licitacoesSelecionadas;
	}

	public void setLicitacoesSelecionadas(List<Integer> licitacoesSelecionadas) {
		this.licitacoesSelecionadas = licitacoesSelecionadas;
	}

	public ScoModalidadeLicitacao getModalidade() {
		return modalidade;
	}
	public void setModalidade(ScoModalidadeLicitacao modalidade) {
		this.modalidade = modalidade;
	}

	public ProcessoAdmComprasPaginatorController getProcessoAdmComprasPaginatorController() {
		return processoAdmComprasPaginatorController;
	}
	public void setProcessoAdmComprasPaginatorController(
			ProcessoAdmComprasPaginatorController processoAdmComprasPaginatorController) {
		this.processoAdmComprasPaginatorController = processoAdmComprasPaginatorController;
	}

	public ScoLicitacao getScolicitacaoSuggestion() {
		return scolicitacaoSuggestion;
	}

	public void setScolicitacaoSuggestion(ScoLicitacao scolicitacaoSuggestion) {
		this.scolicitacaoSuggestion = scolicitacaoSuggestion;
	}

	public Integer getNumPacSelecionado() {
		return numPacSelecionado;
	}

	public void setNumPacSelecionado(Integer numPacSelecionado) {
		this.numPacSelecionado = numPacSelecionado;
	}
	
	public ScoLicitacaoVO getScoLicitacaoMouseOver() {
		return scoLicitacaoMouseOver;
	}

	public void setScoLicitacaoMouseOver(ScoLicitacaoVO scoLicitacaoMouseOver) {
		this.scoLicitacaoMouseOver = scoLicitacaoMouseOver;
	}
	
	public Boolean getMostrarModal() {
		return mostrarModal;
	}

	public void setMostrarModal(Boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}
	
	public Boolean getIsModalLote() {
		return isModalLote;
	}

	public void setIsModalLote(Boolean isModalLote) {
		this.isModalLote = isModalLote;
	}

	public List<Long> getNrosPac() {
		return nrosPac;
	}

	public void setNrosPac(List<Long> nrosPac) {
		this.nrosPac = nrosPac;
	}
	
	public String getMensagemModal() {
		return mensagemModal;
	}

	public void setMensagemModal(String mensagemModal) {
		this.mensagemModal = mensagemModal;
	}

	public AghParametros getSitePregao() {
		return sitePregao;
	}

	public void setSitePregao(AghParametros sitePregao) {
		this.sitePregao = sitePregao;
	}

	public StreamedContent getMedia() {
		return media;
	}


	public void setMedia(StreamedContent media) {
		this.media = media;
	}


	public List<ScoLicitacaoVO> getLicitacaoSelecionada() {
		return licitacaoSelecionada;
	}


	public void setLicitacaoSelecionada(List<ScoLicitacaoVO> licitacaoSelecionada) {
		this.licitacaoSelecionada = licitacaoSelecionada;
	}
	
	public UploadedFile getArquivoCarregado() {
		return arquivoCarregado;
	}

	public void setUploadedFile(UploadedFile arquivoCarregado) {
		this.arquivoCarregado = arquivoCarregado;
	}
	
	public byte[] getArquivo() {
		return arquivo;
	}

	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}

	public Boolean getPacPendenteEnvio() {
		return pacPendenteEnvio;
	}

	public void setPacPendenteEnvio(Boolean pacPendenteEnvio) {
		this.pacPendenteEnvio = pacPendenteEnvio;
	}

	public Boolean getPacPendenteRetorno() {
		return pacPendenteRetorno;
	}

	public void setPacPendenteRetorno(Boolean pacPendenteRetorno) {
		this.pacPendenteRetorno = pacPendenteRetorno;
	}

	public Boolean getHabilitaConsultarProposta() {
		return habilitaConsultarProposta;
	}

	public void setHabilitaConsultarProposta(Boolean habilitaConsultarProposta) {
		this.habilitaConsultarProposta = habilitaConsultarProposta;
	}


	public Integer getNroPAC() {
		return nroPAC;
	}


	public void setNroPAC(Integer nroPAC) {
		this.nroPAC = nroPAC;
	}


	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}


	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
	
}
