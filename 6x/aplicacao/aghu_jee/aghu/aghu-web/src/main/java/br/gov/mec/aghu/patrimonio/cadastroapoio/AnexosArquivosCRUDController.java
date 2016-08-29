package br.gov.mec.aghu.patrimonio.cadastroapoio;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoPatrimonio;
import br.gov.mec.aghu.dominio.DominioTipoProcessoPatrimonio;
import br.gov.mec.aghu.model.PtmArquivosAnexos;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmNotificacaoTecnica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.ArquivosAnexosPesquisaFiltroVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller da estoria 44713 - Sistema de Anexação de arquivos para o patrimônio (imagem 1)'.
 */
public class AnexosArquivosCRUDController extends ActionController{
	/**
	 * 
	 */
	private static final Log LOG = LogFactory.getLog(AnexosArquivosCRUDController.class);
	private static final long serialVersionUID = -2140442940971790652L;
	private static final String ANEXOS_ARQUIVOS_LIST = "patrimonio-anexosArquivosList";
	private static final String CRUD_ANEXOS = "patrimonio-anexosArquivosCRUD";

	@EJB
	private IPatrimonioFacade patrimonioFacade;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	//Armazenamento de filtros para consutas C5,12,13
	private ArquivosAnexosPesquisaFiltroVO arquivosAnexosPesquisaFiltroVO = new ArquivosAnexosPesquisaFiltroVO();
	private PtmArquivosAnexos anexoForm = new PtmArquivosAnexos();
	private PtmArquivosAnexos anexoSelecionado = new PtmArquivosAnexos();
	private PtmArquivosAnexos selectionGrid = new PtmArquivosAnexos();
	private ArquivosAnexosPesquisaFiltroVO anexoSelecionadoGrid = new ArquivosAnexosPesquisaFiltroVO();
	
	private Long aaSeq;
	private RapServidores servidorLogado;
	private Boolean flagStatusOutros = false;
	private Boolean flagEditar = false;
	private Boolean flagTipoProcesso = false;
	
	private List<PtmArquivosAnexos> listaGridAnexosVo = new ArrayList<PtmArquivosAnexos>();
	
	
	//Campos da Tela de Cadastro
	private PtmBemPermanentes patrimonio = new PtmBemPermanentes();
	private PtmItemRecebProvisorios recebimentoItem = new PtmItemRecebProvisorios();
	private PtmNotificacaoTecnica notificacaoTecnica = new PtmNotificacaoTecnica();
	private DominioTipoDocumentoPatrimonio tipoDocumento;
	private DominioTipoProcessoPatrimonio tipoProcesso;
	private Boolean gravando = false;
	
	private String descricaoArquivoAnexoPesquisa;
	private DominioTipoDocumentoPatrimonio tipoDocumentoPesquisa;
	private DominioTipoProcessoPatrimonio tipoProcessoPesquisa;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		
	}
	
	public void iniciar(){
		
		anexoForm = new PtmArquivosAnexos();
		
		anexoForm.setDescricao(descricaoArquivoAnexoPesquisa);
		anexoForm.setTipoDocumento(tipoDocumentoPesquisa);
		anexoForm.setTipoProcesso(tipoProcessoPesquisa);
		
		servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		if(gravando == true){
			anexoForm.setTipoProcesso(DominioTipoProcessoPatrimonio.ACEITE_TECNICO);
		}
		
		if(aaSeq != null){
			
			setFlagEditar(true);
			
			//Resultado da C7
			anexoSelecionado = patrimonioFacade.obterVisualizacaoDetalhesAnexo(aaSeq);
			listaGridAnexosVo.clear();
			
			anexoForm.setDescricao(anexoSelecionado.getDescricao());
			anexoForm.setTipoDocumento(anexoSelecionado.getTipoDocumento());
			anexoForm.setTipoProcesso(anexoSelecionado.getTipoProcesso());
			
			if(anexoSelecionado.getAceiteTecnico() != null){
				arquivosAnexosPesquisaFiltroVO.setAceiteTecnico(anexoSelecionado.getAceiteTecnico()); 
			}
			
			if(anexoForm.getTipoDocumento()!= null && anexoForm.getTipoDocumento().getDescricao().equals(DominioTipoDocumentoPatrimonio.OUTROS.getDescricao())){
				setFlagStatusOutros(true);
			}
			
			anexoForm.setTipoDocumentoOutros(anexoSelecionado.getTipoDocumentoOutros());
			anexoForm.setArquivo(anexoSelecionado.getArquivo());
			anexoForm.setAnexo(anexoSelecionado.getAnexo());
			
			this.recebimentoItem = anexoSelecionado.getPtmItemRecebProvisorios();
			this.patrimonio = anexoSelecionado.getPtmBemPermanentes();
			this.notificacaoTecnica = anexoSelecionado.getPtmNotificacaoTecnica();			
			listaGridAnexosVo.add(this.anexoForm);
		
		} else if(aaSeq == null){
			
				if(recebimentoItem != null && DominioTipoProcessoPatrimonio.RECEBIMENTO.equals(arquivosAnexosPesquisaFiltroVO.getTipoProcesso())){ 
					anexoForm.setTipoProcesso(DominioTipoProcessoPatrimonio.RECEBIMENTO);
					flagTipoProcesso = true;
				}
				setFlagEditar(false);
				setFlagStatusOutros(false);
			}
	}
	
	public String download(){
		PtmArquivosAnexos doc = this.patrimonioFacade.obterDocumentoAnexado(Long.valueOf(this.aaSeq));
		return this.downloadViaHttpServletResponse(doc);
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
	
	//SB Patrimonio
		public List<PtmBemPermanentes>  obterPatrimonioSB(String param){
			return this.returnSGWithCount(patrimonioFacade.listarSugestionPatrimonio(param),patrimonioFacade.listarSugestionPatrimonioCount(param));
		}
			
		//NOTFICACAO TEC SB
		public List<PtmNotificacaoTecnica> obterNotificacaoTecnicaSB(String param) {
			return this.returnSGWithCount(patrimonioFacade.listarSugestionNotificacaoTecnica(param),patrimonioFacade.listarSugestionNotificacaoTecnicaCount(param));
		}
		
		//RECEBIMENTO ITEM SB
		public List<PtmItemRecebProvisorios> obterRecebimentoItemSB(String param) {
			return this.returnSGWithCount(patrimonioFacade.listarConsultaRecebimentosSugestion(param),patrimonioFacade.listarConsultaRecebimentosSugestionCount(param));
		}
		

	public void setArquivosAnexosPesquisaFiltroVO(ArquivosAnexosPesquisaFiltroVO arquivosAnexosPesquisaFiltroVO) {
		this.arquivosAnexosPesquisaFiltroVO = arquivosAnexosPesquisaFiltroVO;
	}

	public void listener(final FileUploadEvent event) {
		
		final UploadedFile item = event.getFile();

		InputStream is;
		try {
			is = item.getInputstream();
			byte[] bytes = IOUtils.toByteArray(is);
			if (anexoForm.getDescricao() != null && 
					(!StringUtils.isEmpty(anexoForm.getDescricao().trim()) && 
					!StringUtils.isBlank(anexoForm.getDescricao().trim())) && 
					(anexoForm.getDescricao().length() < 500) && 
					(anexoForm.getTipoDocumento() != null) && 
					(anexoForm.getTipoProcesso() != null))
					{					
						if (this.flagStatusOutros){							
								if (!StringUtils.isEmpty(anexoForm.getTipoDocumentoOutros().trim()) && 
										!StringUtils.isBlank(anexoForm.getTipoDocumentoOutros().trim())){				
									anexoForm.setAnexo(bytes);
									anexoForm.setArquivo(item.getFileName());
								}
						} else {
							anexoForm.setAnexo(bytes);
							anexoForm.setArquivo(new String(item.getFileName().getBytes("ISO-8859-1"), "UTF-8"));
						}
					}
						

		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
		
	}
	
	public void adicionarGrid(){
		
		try {
			anexoSelecionado = new PtmArquivosAnexos(); 
			
			if(!StringUtils.isEmpty(anexoForm.getDescricao().trim()) && !StringUtils.isBlank(anexoForm.getDescricao().trim())){
				anexoSelecionado.setDescricao(anexoForm.getDescricao().trim());
			}else{
				apresentarMsgNegocio(Severity.ERROR, "Descrição: Campo obrigatório, digite um valor.");
				anexoForm.setDescricao(anexoForm.getDescricao().trim());
				anexoForm.setAnexo(null);
				return;
			} 
			
			if (anexoForm.getDescricao().length() > 500) {
				apresentarMsgNegocio(Severity.ERROR, "Número máximo de caracteres excedido.");
				anexoForm.setDescricao("");
				anexoForm.setAnexo(null);
				return ;
			}
			
			anexoSelecionado.setTipoDocumento(anexoForm.getTipoDocumento());
			anexoSelecionado.setTipoProcesso(anexoForm.getTipoProcesso());
			anexoSelecionado.setTipoDocumentoOutros(anexoForm.getTipoDocumentoOutros());
			anexoSelecionado.setAnexo(anexoForm.getAnexo());
			anexoSelecionado.setArquivo(anexoForm.getArquivo());
			anexoSelecionado.setPtmBemPermanentes(this.patrimonio);
			anexoSelecionado.setPtmItemRecebProvisorios(this.recebimentoItem);
			anexoSelecionado.setPtmNotificacaoTecnica(this.notificacaoTecnica);
			
			patrimonioFacade.validarArquivoAnexo(anexoForm);
			
			listaGridAnexosVo.add(this.anexoSelecionado);
			
			limpar();

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	public String getObterTamanho(Integer ordem) {
		PtmArquivosAnexos item = listaGridAnexosVo.get(ordem);
		String[] result ;
		if(item.getAnexo()!=null){
			long fileSize = item.getAnexo().length;
		    if((double)fileSize/(1024*1024) >= 1){
		    	result = String.valueOf((double)fileSize/(1024*1024)).split("\\.");
		    	return result[0] +"."+result[1].substring(0, 2) + "MB"; 
		    }else{
		    	result = String.valueOf((double)fileSize/(1024)).split("\\.");
		    	if(result[1].length()>2){
		    		return result[0] +"."+ result[1].substring(0, 2) + "KB";
		    	}
		    	else{
		    		return result[0] +"."+ result[1] + "KB";
		    	}
		    }
		}
		return "";
	}
	
	public String gravar(){
		
		Long irpSeq = null;
		
		try {
			if(!listaGridAnexosVo.isEmpty() && listaGridAnexosVo != null){
				
				 for (PtmArquivosAnexos ptmArquivosAnexos : listaGridAnexosVo) {
					
					 this.anexoForm = new PtmArquivosAnexos();
						anexoForm.setTipoProcesso(ptmArquivosAnexos.getTipoProcesso());
						anexoForm.setTipoDocumento(ptmArquivosAnexos.getTipoDocumento());
						anexoForm.setDescricao(ptmArquivosAnexos.getDescricao());
						anexoForm.setAnexo(ptmArquivosAnexos.getAnexo());
						anexoForm.setPtmNotificacaoTecnica(notificacaoTecnica);
						anexoForm.setPtmItemRecebProvisorios(recebimentoItem);
						anexoForm.setPtmBemPermanentes(patrimonio);
						anexoForm.setArquivo(ptmArquivosAnexos.getArquivo());
						
						if (ptmArquivosAnexos.getTipoDocumentoOutros() != null){
							anexoForm.setTipoDocumentoOutros(ptmArquivosAnexos.getTipoDocumentoOutros());
						}
						
						if(arquivosAnexosPesquisaFiltroVO.getAceiteTecnico() != null){
							anexoForm.setAceiteTecnico(arquivosAnexosPesquisaFiltroVO.getAceiteTecnico());
						}
						
						anexoForm.setCriadoEm(new Date());
						anexoForm.setServidor(servidorLogado);												
						
						patrimonioFacade.inserirArquivoAnexos(anexoForm, aaSeq, notificacaoTecnica, irpSeq, flagEditar);
						
						anexoForm = new PtmArquivosAnexos();
						listaGridAnexosVo = new ArrayList<PtmArquivosAnexos>();
				 
				}if(flagEditar){
					apresentarMsgNegocio(Severity.INFO, "ANEXO_ATUALIZADO");
				 }else{
					 apresentarMsgNegocio(Severity.INFO, "ANEXO_CADASTRADO");
				 }
				 cancelar();
				 return ANEXOS_ARQUIVOS_LIST;
				 //return null;
				
			}else{
				apresentarMsgNegocio(Severity.FATAL, "ERRO_AO_ANEXAR_ARQUIVO");
			}
			
		} catch (Exception e){
			apresentarMsgNegocio(Severity.FATAL, "ERRO_AO_ANEXAR", new Object[] {e.getMessage()});
		
		}
		return CRUD_ANEXOS;		
	}
	
	public void verificarStatusOutros(){
		
		if (anexoForm.getTipoDocumento() != null && anexoForm.getTipoDocumento().getDescricao().equals(DominioTipoDocumentoPatrimonio.OUTROS.getDescricao())){
			setFlagStatusOutros(true);
		}else{
			setFlagStatusOutros(false);
		}
		anexoForm.setTipoDocumentoOutros("");
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {

		if (item.length() > tamanhoMaximo) {
                item = StringUtils.abbreviate(item, tamanhoMaximo);
        }
        return item;
	}
	
	public String cancelar(){
		
		setAaSeq(null);
		anexoForm = new PtmArquivosAnexos();
		patrimonio = new PtmBemPermanentes();
		recebimentoItem = new PtmItemRecebProvisorios();
		notificacaoTecnica = new PtmNotificacaoTecnica();
		listaGridAnexosVo = new ArrayList<PtmArquivosAnexos>();
		setFlagEditar(false);
		setFlagStatusOutros(false);
		setFlagTipoProcesso(false);
		
		return ANEXOS_ARQUIVOS_LIST;
	}
	
	public void limpar(){
		
		anexoForm.setDescricao("");
		anexoForm.setTipoDocumento(null);
		anexoForm.setTipoProcesso(null);
		anexoForm.setTipoDocumentoOutros("");
		anexoForm.setAnexo(null);
		setFlagStatusOutros(false);
		setFlagTipoProcesso(false);
	}
	
	public void limparSugestionsObrigatorios(){
		
		this.patrimonio = null;
		this.recebimentoItem = null;
		this.notificacaoTecnica = null;
	}
	
	public boolean validarGrid(){
		if(!flagEditar && !listaGridAnexosVo.isEmpty() && listaGridAnexosVo != null){
			return true;
		}else if(flagEditar){
			return false;
		}else{
			return false;
		}
	}
	
	public void excluirArquivosAnexos(PtmArquivosAnexos anexoSelecionado){
		listaGridAnexosVo.remove(anexoSelecionado);
	}
	
	public ArquivosAnexosPesquisaFiltroVO getArquivosAnexosPesquisaFiltroVO() {
		return arquivosAnexosPesquisaFiltroVO;
	}

	public Long getAaSeq() {
		return aaSeq;
	}
	public void setAaSeq(Long aaSeq) {
		this.aaSeq = aaSeq;
	}

	public List<PtmArquivosAnexos> getListaGridAnexosVo() {
		return listaGridAnexosVo;
	}

	public void setListaGridAnexosVo(List<PtmArquivosAnexos> listaGridAnexosVo) {
		this.listaGridAnexosVo = listaGridAnexosVo;
	}

	public PtmBemPermanentes getPatrimonio() {
		return patrimonio;
	}
	
	public void setPatrimonio(PtmBemPermanentes patrimonio) {
		this.patrimonio = patrimonio;
	}

	public PtmItemRecebProvisorios getRecebimentoItem() {
		return recebimentoItem;
	}

	public void setRecebimentoItem(PtmItemRecebProvisorios recebimentoItem) {
		this.recebimentoItem = recebimentoItem;
	}

	public PtmNotificacaoTecnica getNotificacaoTecnica() {
		return notificacaoTecnica;
	}

	public void setNotificacaoTecnica(PtmNotificacaoTecnica notificacaoTecnica) {
		this.notificacaoTecnica = notificacaoTecnica;
	}

	public PtmArquivosAnexos getAnexoForm() {
		return anexoForm;
	}

	public void setAnexoForm(PtmArquivosAnexos anexoForm) {
		this.anexoForm = anexoForm;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public ArquivosAnexosPesquisaFiltroVO getAnexoSelecionadoGrid() {
		return anexoSelecionadoGrid;
	}

	public void setAnexoSelecionadoGrid(ArquivosAnexosPesquisaFiltroVO anexoSelecionadoGrid) {
		this.anexoSelecionadoGrid = anexoSelecionadoGrid;
	}

	public DominioTipoDocumentoPatrimonio getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(DominioTipoDocumentoPatrimonio tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public DominioTipoProcessoPatrimonio getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(DominioTipoProcessoPatrimonio tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

	public Boolean getFlagStatusOutros() {
		return flagStatusOutros;
	}

	public void setFlagStatusOutros(Boolean flagStatusOutros) {
		this.flagStatusOutros = flagStatusOutros;
	}

	public PtmArquivosAnexos getAnexoSelecionado() {
		return anexoSelecionado;
	}

	public void setAnexoSelecionado(PtmArquivosAnexos anexoSelecionado) {
		this.anexoSelecionado = anexoSelecionado;
	}

	public Boolean getFlagEditar() {
		return flagEditar;
	}

	public void setFlagEditar(Boolean flagEditar) {
		this.flagEditar = flagEditar;
	}

	public PtmArquivosAnexos getSelectionGrid() {
		return selectionGrid;
	}

	public void setSelectionGrid(PtmArquivosAnexos selectionGrid) {
		this.selectionGrid = selectionGrid;
	}

	public Boolean getGravando() {
		return gravando;
	}

	public void setGravando(Boolean gravando) {
		this.gravando = gravando;
	}

	public Boolean getFlagTipoProcesso() {
		return flagTipoProcesso;
	}

	public void setFlagTipoProcesso(Boolean flagTipoProcesso) {
		this.flagTipoProcesso = flagTipoProcesso;
	}

	public String getDescricaoArquivoAnexoPesquisa() {
		return descricaoArquivoAnexoPesquisa;
	}

	public void setDescricaoArquivoAnexoPesquisa(
			String descricaoArquivoAnexoPesquisa) {
		this.descricaoArquivoAnexoPesquisa = descricaoArquivoAnexoPesquisa;
	}

	public DominioTipoDocumentoPatrimonio getTipoDocumentoPesquisa() {
		return tipoDocumentoPesquisa;
	}

	public void setTipoDocumentoPesquisa(
			DominioTipoDocumentoPatrimonio tipoDocumentoPesquisa) {
		this.tipoDocumentoPesquisa = tipoDocumentoPesquisa;
	}

	public DominioTipoProcessoPatrimonio getTipoProcessoPesquisa() {
		return tipoProcessoPesquisa;
	}

	public void setTipoProcessoPesquisa(
			DominioTipoProcessoPatrimonio tipoProcessoPesquisa) {
		this.tipoProcessoPesquisa = tipoProcessoPesquisa;
	}

}
