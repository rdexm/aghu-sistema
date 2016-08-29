package br.gov.mec.aghu.patrimonio.cadastroapoio;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoPatrimonio;
import br.gov.mec.aghu.dominio.DominioTipoProcessoPatrimonio;
import br.gov.mec.aghu.model.PtmArquivosAnexos;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmNotificacaoTecnica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.ArquivosAnexosPesquisaFiltroVO;
import br.gov.mec.aghu.patrimonio.vo.ArquivosAnexosPesquisaGridVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Controller da estoria 44713 - Sistema de Anexação de arquivos para o patrimônio (imagem 2)'.
 */
public class AnexosArquivosListPaginatorController extends ActionController implements ActionPaginator {
	/**
	 * 
	 */
	private static final Log LOG = LogFactory.getLog(AnexosArquivosListPaginatorController.class);
	private static final long serialVersionUID = -2140442940971790652L;
	private static final String DETALHAMENTO_ANEXOS = "patrimonio-anexosArquivosDetalhes";
	private static final String CRUD_ANEXOS = "patrimonio-anexosArquivosCRUD";

	@EJB
	private IPatrimonioFacade patrimonioFacade;
	@Inject	@Paginator
	private DynamicDataModel<ArquivosAnexosPesquisaGridVO> dataModel;
	@Inject
	private AnexosArquivosCRUDController anexosArquivosCRUDController;
	@Inject
	private AnexosArquivosDetalhesController anexosArquivosDetalhesController;
	
	
	//Armazenamento de filtros para consutas C5,12,13
	private ArquivosAnexosPesquisaFiltroVO arquivosAnexosPesquisaFiltroVO = new ArquivosAnexosPesquisaFiltroVO();
	private ArquivosAnexosPesquisaFiltroVO anexoSelcionado = new ArquivosAnexosPesquisaFiltroVO();
	private boolean exibirBotaoNovo=false;
	private PtmItemRecebProvisorios recebimentoItem;
	
	private DominioTipoProcessoPatrimonio tipoProcesso; 
	
	private Boolean registroAceiteTecnico = false;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		
	}
	
	public void iniciar(){
				
		
		if (recebimentoItem != null){
			PtmItemRecebProvisorios ptm = this.patrimonioFacade.listarConsultaRecebimentosSugestionGrid(recebimentoItem);
			if (ptm != null){
				arquivosAnexosPesquisaFiltroVO.setRecebimentoItem(ptm);
				arquivosAnexosPesquisaFiltroVO.setTipoProcesso(DominioTipoProcessoPatrimonio.RECEBIMENTO);
				pesquisar();
			}
		}
		if(this.registroAceiteTecnico == true){
			this.registroAceiteTecnico = false;
			pesquisar();
		}
	}
	public void pesquisar(){
	
		if(validaPesquisa()){
			this.dataModel.reiniciarPaginator();
			this.setExibirBotaoNovo(true);
		} 
		
	}


	private boolean validaPesquisa() {
		if(arquivosAnexosPesquisaFiltroVO.getDtIniInclusao() != null && arquivosAnexosPesquisaFiltroVO.getDtFimInclusao()!=null && DateUtil.validaDataMaior(arquivosAnexosPesquisaFiltroVO.getDtIniInclusao(), arquivosAnexosPesquisaFiltroVO.getDtFimInclusao())){
			apresentarMsgNegocio(Severity.WARN, "MSG_DATA_INICIAL_MAIOR_DATA_FIM_INC");
			return false;
		}
		if(arquivosAnexosPesquisaFiltroVO.getDtIniUltAlt() != null && arquivosAnexosPesquisaFiltroVO.getDtFimUltAlt()!=null && DateUtil.validaDataMaior(arquivosAnexosPesquisaFiltroVO.getDtIniUltAlt(), arquivosAnexosPesquisaFiltroVO.getDtFimUltAlt())){
			apresentarMsgNegocio(Severity.WARN, "MSG_DATA_INICIAL_MAIOR_DATA_FIM_ALT");
			return false;
		}
		if(arquivosAnexosPesquisaFiltroVO.getNotificacaoTecnica()==null && arquivosAnexosPesquisaFiltroVO.getPatrimonio()== null && arquivosAnexosPesquisaFiltroVO.getRecebimentoItem()==null){
			apresentarMsgNegocio(Severity.WARN, "MSG_OBRIGATORIEDADE_PESQUISA_ARQUIVOS_ANEXOS");
			return false;
		}
		return true;
	}
	
	/**
     * Ação do botão Limpar
     */
    public void limpar() {       
    		limparSugestionBox();
            Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
            while (componentes.hasNext()) {                 
                    limparValoresSubmetidos(componentes.next());
            }
            this.dataModel.limparPesquisa();
            this.exibirBotaoNovo=false;
            iniciar();        
    }

	private void limparSugestionBox() {
		arquivosAnexosPesquisaFiltroVO.setUsuarioInclusao(null);
		arquivosAnexosPesquisaFiltroVO.setUsuarioUltimaAlteracao(null);
		arquivosAnexosPesquisaFiltroVO.setNotificacaoTecnica(null);
		arquivosAnexosPesquisaFiltroVO.setPatrimonio(null);
		arquivosAnexosPesquisaFiltroVO.setMaterial(null);
		arquivosAnexosPesquisaFiltroVO.setRecebimentoItem(null);
		arquivosAnexosPesquisaFiltroVO.setDtFimInclusao(null);
		arquivosAnexosPesquisaFiltroVO.setDtFimUltAlt(null);
		arquivosAnexosPesquisaFiltroVO.setDtIniInclusao(null);
		arquivosAnexosPesquisaFiltroVO.setDtIniUltAlt(null);
		arquivosAnexosPesquisaFiltroVO.setDescricaoArquivoAnexo(null);
		arquivosAnexosPesquisaFiltroVO.setDescricaoTipoDocumento(null);
		arquivosAnexosPesquisaFiltroVO.setArquivo(null);
		arquivosAnexosPesquisaFiltroVO.setNroAf(null);
		arquivosAnexosPesquisaFiltroVO.setSc(null);
		arquivosAnexosPesquisaFiltroVO.setNotaFiscal(null);
		arquivosAnexosPesquisaFiltroVO.setEsl(null);
		arquivosAnexosPesquisaFiltroVO.setTipoDocumento(null);
		arquivosAnexosPesquisaFiltroVO.setTipoProcesso(null);
		arquivosAnexosPesquisaFiltroVO.setAceiteTecnico(null);
	}

	public String obterTipoProcesso(String i){
		return DominioTipoProcessoPatrimonio.getDescricao(Integer.parseInt(i));
	}
	public String obterTipoDocumento(String i){
		if(!i.isEmpty()){
			return DominioTipoDocumentoPatrimonio.getDescricao(Integer.parseInt(i));
		}
		else{ 
			return null;
		}
	}
	
    public void limparValoresSubmetidos(Object object){
        if (object == null || object instanceof UIComponent == false) {
               return;
        }
        Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
        while (uiComponent.hasNext()) {
               limparValoresSubmetidos(uiComponent.next());
        }
        if (object instanceof UIInput) {
               ((UIInput) object).resetValue();
        }
	}


	public String novo(){
		this.anexosArquivosCRUDController.setArquivosAnexosPesquisaFiltroVO(this.arquivosAnexosPesquisaFiltroVO);
		anexosArquivosCRUDController.setDescricaoArquivoAnexoPesquisa(arquivosAnexosPesquisaFiltroVO.getDescricaoArquivoAnexo());
		anexosArquivosCRUDController.setTipoDocumentoPesquisa(arquivosAnexosPesquisaFiltroVO.getTipoDocumento());
		anexosArquivosCRUDController.setTipoProcessoPesquisa(arquivosAnexosPesquisaFiltroVO.getTipoProcesso());
		if(this.arquivosAnexosPesquisaFiltroVO.getAceiteTecnico() != null){
			this.anexosArquivosCRUDController.setGravando(true);
		}
		return CRUD_ANEXOS;
	}
	public String editar(Object seq){
		this.anexosArquivosCRUDController.setAaSeq(Long.valueOf(seq.toString()));
		return CRUD_ANEXOS;
	}
	public String detalhar(Object seq){
		String tipo;
		if(this.arquivosAnexosPesquisaFiltroVO.getNotificacaoTecnica()!=null){
			tipo = "NOT";
		}else if(this.arquivosAnexosPesquisaFiltroVO.getRecebimentoItem()!=null){
			tipo ="RECB";
		}else if (this.arquivosAnexosPesquisaFiltroVO.getPatrimonio()!=null){
			tipo = "PAT";
		}else{
			//OBS PARA OUTROS TIPOS CONTINUAR LOGICA OU CONSULTA FALHA 
			tipo ="";
		}
		this.anexosArquivosDetalhesController.setTipo(tipo);
		this.anexosArquivosDetalhesController.setAaSeq(Long.valueOf(seq.toString()));
		return DETALHAMENTO_ANEXOS;
	}
	
	public String download(Object seq){
		PtmArquivosAnexos doc = this.patrimonioFacade.obterDocumentoAnexado(Long.valueOf(seq.toString()));
		return this.downloadViaHttpServletResponse(doc);
	}

	public String obterDataFormatada(Object objeto,String tipo){
		ArquivosAnexosPesquisaGridVO linha  = (ArquivosAnexosPesquisaGridVO) objeto;
		String data = "";
		switch (tipo){
		case "A":{
			//ORACLE NAO OBTEM DATA COM HORA NO SQL NATIVO
			if(linha.getHoraAlt()!=null){
				data = "Data: ".concat(linha.getHoraAlt());
			}else{
				data = DateUtil.obterDataFormatada(linha.getDtUltAltera(), "dd/MM/yyyy HH:mm:ss");
			}
			
			break;
		}
		case "C":{
			if(linha.getHoraInc()!=null){
				data = "Data: ".concat(linha.getHoraInc());
			}else{
				data = DateUtil.obterDataFormatada(linha.getDtCriadoEm(), "dd/MM/yyyy HH:mm:ss");
			}
			break;
		}
		case "AS" :
		{
			if(linha.getDtUltAltera()!=null){
				data = DateUtil.obterDataFormatada(linha.getDtUltAltera(), "dd/MM/yyyy").concat("...");
			}
			break;
		}
		case "CS":{
			if(linha.getDtCriadoEm()!=null){
				data = DateUtil.obterDataFormatada(linha.getDtCriadoEm(), "dd/MM/yyyy").concat("...");	
			}
			break;
		}
		default: 
			data="";
			break;
		}
		return data;
	}
	
	/**
	 * Download via uma Http Response
	 * 
	 * @param dados
	 *            array de bytes (stream) do arquivo de download
	 * @return
	 */
	private String downloadViaHttpServletResponse(PtmArquivosAnexos arquivosAnexos) {

		// Instancia uma HTTP Response
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

		// Seta o tipo de MIME
		response.setContentType("application/octet-stream");

		// Seta o arquivo no cabeçalho
		response.addHeader("Content-disposition", "attachment; filename=\"" + arquivosAnexos.getArquivo() + "\"");

		// Escreve a resposta no HTTP RESPONSE
		ServletOutputStream os = null;
		try {
			os = response.getOutputStream();
			os.write(arquivosAnexos.getAnexo()); // Escrevemos o STREAM de resposta/RESPONSE
			os.flush();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(os);
		}
		return null;
	}

	public void setArquivosAnexosPesquisaFiltroVO(ArquivosAnexosPesquisaFiltroVO arquivosAnexosPesquisaFiltroVO) {
		this.arquivosAnexosPesquisaFiltroVO = arquivosAnexosPesquisaFiltroVO;
	}

	
	public void limparSugestionsObrigatorios(){
		this.arquivosAnexosPesquisaFiltroVO.setPatrimonio(null);
		this.arquivosAnexosPesquisaFiltroVO.setRecebimentoItem(null);
		this.arquivosAnexosPesquisaFiltroVO.setNotificacaoTecnica(null);
	}
	
	public String obterHintUsuarioInc(ArquivosAnexosPesquisaGridVO item) {
		StringBuilder str = new StringBuilder(100);
		if (StringUtils.isNotBlank(item.getNome())) {
			str.append("Nome: ").append(item.getNome());
			str.append("\nMatrícula: ").append(item.getMatricula());
			str.append("\nVínculo: ").append(item.getVinCodigo());
		}
		return str.toString();
	}
	public String obterHintUsuarioAlt(ArquivosAnexosPesquisaGridVO item) {
		StringBuilder str = new StringBuilder(100);
		if (StringUtils.isNotBlank(item.getNomeAlteracao())) {
			str.append("Nome: ").append(item.getNomeAlteracao());
			str.append("\nMatrícula: ").append(item.getMatriculaAlteracao());
			str.append("\nVínculo: ").append(item.getVinCodigoAlteracao());
		}
		return str.toString();
	}
	
	
	public String obterHintTipoDocumento(ArquivosAnexosPesquisaGridVO item) {
		StringBuilder str = new StringBuilder(100);
		if (item.getTipoDocumento()!=null) {
			str.append("Tipo Documento: ").append(DominioTipoDocumentoPatrimonio.getDescricao(item.getTipoDocumento()));
		}
		return str.toString();
	}
	public String obterHintTipoProcesso(ArquivosAnexosPesquisaGridVO item) {
		StringBuilder str = new StringBuilder(100);
		if (item.getTipoProcesso()!=null) {
			str.append("Tipo Processo: ").append(DominioTipoProcessoPatrimonio.getDescricao(item.getTipoProcesso()));
		}
		return str.toString();
	}
	
	public String obterHintArquivo(ArquivosAnexosPesquisaGridVO item) {
		StringBuilder str = new StringBuilder(100);
		if(item!=null){
			if (StringUtils.isNotBlank(item.getArquivo())) {
				str.append("Arquivo:").append(item.getArquivo());
			}
			if (StringUtils.isNotBlank(item.getDescricao())) {
				str.append("<br/>Descrição:").append(item.getDescricao());	
			}
		}
		return str.toString();
	}
		
	//SB Patrimonio
	public List<PtmBemPermanentes>  obterPatrimonioSB(String param){
		return this.returnSGWithCount(patrimonioFacade.listarSugestionPatrimonio(param),patrimonioFacade.listarSugestionPatrimonioCount(param));
	}
		
	//NOTFICACAO TEC SB
	public List<PtmNotificacaoTecnica> obterNotificacaoTecnicaSB(String param) {
		return this.returnSGWithCount(patrimonioFacade.listarSugestionNotificacaoTecnica(param),patrimonioFacade.listarSugestionNotificacaoTecnicaCount(param));
	}
	//MATERIAL SB
	public List<ScoMaterial> obterMaterialSB(String param) {
		return this.returnSGWithCount(patrimonioFacade.listarScoMateriaisSugestion(param),patrimonioFacade.listarScoMateriaisSugestionCount(param));
	}
	//RECEBIMENTO ITEM SB
	public List<PtmItemRecebProvisorios> obterRecebimentoItemSB(String param) {
		return this.returnSGWithCount(patrimonioFacade.listarConsultaRecebimentosSugestion(param),patrimonioFacade.listarConsultaRecebimentosSugestionCount(param));
	}
	
	//USU SB
	public List<RapServidores> obterUsuariosSugestion(String param) {
		return this.returnSGWithCount(patrimonioFacade.consultarUsuariosSugestion(param),patrimonioFacade.consultarUsuariosSugestionCount(param));
	}

	@Override
	public Long recuperarCount() {
		if(arquivosAnexosPesquisaFiltroVO.getRecebimentoItem()!= null){
			PtmItemRecebProvisorios ptmItemRecebProvisorios = patrimonioFacade.pesquisarItemRecebSeq(arquivosAnexosPesquisaFiltroVO.getRecebimentoItem().getNrpSeq(),arquivosAnexosPesquisaFiltroVO.getRecebimentoItem().getNroItem());
			return this.patrimonioFacade.pesquisarArquivoAnexosPorRecebimentoCount(arquivosAnexosPesquisaFiltroVO,ptmItemRecebProvisorios.getSeq());
		}else if(arquivosAnexosPesquisaFiltroVO.getPatrimonio()!=null){
			return this.patrimonioFacade.pesquisarArquivoAnexosPorPatrimonioCount(arquivosAnexosPesquisaFiltroVO);
		}else if(arquivosAnexosPesquisaFiltroVO.getNotificacaoTecnica()!=null){
			return this.patrimonioFacade.pesquisarArquivoAnexosPorNotificacaoTecnicaCount(arquivosAnexosPesquisaFiltroVO);
		}
		return null;
	}

	@Override
	public List<ArquivosAnexosPesquisaGridVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if(arquivosAnexosPesquisaFiltroVO.getRecebimentoItem()!= null){
			PtmItemRecebProvisorios ptmItemRecebProvisorios = patrimonioFacade.pesquisarItemRecebSeq(arquivosAnexosPesquisaFiltroVO.getRecebimentoItem().getNrpSeq(),arquivosAnexosPesquisaFiltroVO.getRecebimentoItem().getNroItem());
			return this.patrimonioFacade.pesquisarArquivoAnexosPorRecebimento(arquivosAnexosPesquisaFiltroVO,ptmItemRecebProvisorios.getSeq(), firstResult,maxResult, null, false);
		}else if(arquivosAnexosPesquisaFiltroVO.getPatrimonio()!=null){
			return this.patrimonioFacade.pesquisarArquivoAnexosPorPatrimonio(arquivosAnexosPesquisaFiltroVO, firstResult,maxResult);
		}else if(arquivosAnexosPesquisaFiltroVO.getNotificacaoTecnica()!=null){
			return this.patrimonioFacade.pesquisarArquivoAnexosPorNotificacaoTecnica(arquivosAnexosPesquisaFiltroVO, firstResult,maxResult);
		}
		return null;
	}

	public ArquivosAnexosPesquisaFiltroVO getArquivosAnexosPesquisaFiltroVO() {
		return arquivosAnexosPesquisaFiltroVO;
	}

	public DynamicDataModel<ArquivosAnexosPesquisaGridVO> getDataModel() {
		return dataModel;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public ArquivosAnexosPesquisaFiltroVO getAnexoSelcionado() {
		return anexoSelcionado;
	}

	public void setAnexoSelcionado(ArquivosAnexosPesquisaFiltroVO anexoSelcionado) {
		this.anexoSelcionado = anexoSelcionado;
	}

	public DominioTipoProcessoPatrimonio getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(DominioTipoProcessoPatrimonio tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

	public PtmItemRecebProvisorios getRecebimentoItem() {
		return recebimentoItem;
	}

	public void setRecebimentoItem(PtmItemRecebProvisorios recebimentoItem) {
		this.recebimentoItem = recebimentoItem;
	}

	public Boolean getRegistroAceiteTecnico() {
		return registroAceiteTecnico;
	}

	public void setRegistroAceiteTecnico(Boolean registroAceiteTecnico) {
		this.registroAceiteTecnico = registroAceiteTecnico;
	}
}
