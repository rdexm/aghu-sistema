package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.compras.action.ImprimirDescricaoTecnicaMaterialController;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.ScoAnexoDescricaoTecnica;
import br.gov.mec.aghu.model.ScoDescricaoTecnicaPadrao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialDescTecnica;


public class CadastroDescricaoTecnicaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(CadastroDescricaoTecnicaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 3212900217313393269L;

//		private enum DescricaoTecnicaExceptionCode implements BusinessExceptionCode{
//			MENSAGEM_DESCRICAO_NAO_PREENCHIDA, MENSAGEM_IMPRESSAO_SEM_DESCRICAO, MENSAGEM_TAMANHO_ARQUIVO_REJEITADO
//		}


	@EJB
	private IComprasFacade comprasFacade;


	@Inject
	private ImprimirDescricaoTecnicaMaterialController imprimirDescricaoTecnicaMaterialController;
	
	@Inject
	private CadastroDescricaoTecnicaPaginatorController cadastroDescricaoTecnicaPaginatorController;

	private ScoDescricaoTecnicaPadrao descricaoTecnica;
	private List<ScoMaterial> listaMateriais;
	private List<ScoAnexoDescricaoTecnica> listaAnexo;
	private String descricao;
	private ScoMaterial material = new ScoMaterial();
	private ScoMaterial materialSugestion = null;
	private ScoAnexoDescricaoTecnica anexo;
	private Integer codigo;
	private String descricaoAnexo;

	// Flag utilizada para a integração da tela de Criação de Material.
	private boolean retornoCriacaoMaterial = false;
	private String voltarPara;
	private boolean integracaoCriacao = false;

	private boolean importando;

	private boolean desabilitaUpload = false;

	private Integer tamanhoMaximoUpload;
	private Integer tamanhoArquivo;
	
	private Integer abaAberta;

	private static final String CADASTRO_DESCRICAO_TECNICA_LIST = "cadastroDescricaoTecnicaList";
	
	private static final String CADASTRO_MATERIAL_CRUD = "estoque-manterMaterialCRUD";
	
	private static final String IMPRIMIR_DESCRICAO_TECNICA= "imprimirDescricaoTecnicaMaterialPdf";
	
	private static final Enum[] fetchArgsLeftJoin = {ScoDescricaoTecnicaPadrao.Fields.LISTA_ANEXO, ScoDescricaoTecnicaPadrao.Fields.LISTA_MATERIAIS};
	private static final Enum[] fetchArgsInnerJoin = {ScoMaterialDescTecnica.Fields.MATERIAL};
	
	private UploadedFile UploadedFile;
	
	public String iniciar() {
	 

	 

		if (!retornoCriacaoMaterial) {
			this.listaAnexo = new ArrayList<ScoAnexoDescricaoTecnica>();
			this.listaMateriais = new ArrayList<ScoMaterial>();
			this.anexo = new ScoAnexoDescricaoTecnica();
			if(descricaoTecnica != null && descricaoTecnica.getCodigo() != null) {
				this.descricaoTecnica = this.comprasFacade.buscarScoDescricaoTecnicaPadraoByCodigo(this.descricaoTecnica.getCodigo(),null, fetchArgsLeftJoin);
				if(descricaoTecnica == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelar();
				}else {
					this.descricao = this.descricaoTecnica.getDescricao1() + this.descricaoTecnica.getDescricao2();
					for (ScoAnexoDescricaoTecnica anex : this.descricaoTecnica.getListaAnexo()) {
						this.listaAnexo.add(anex);
					}
					
					if(this.descricaoTecnica.getListaMateriais() != null){
						for (ScoMaterialDescTecnica matdesc : this.descricaoTecnica.getListaMateriais()) {
							matdesc = this.comprasFacade.obterScoDescricaoTecnicaPorCodigo(matdesc.getCodigo(), fetchArgsInnerJoin, null);
							if (!listaMateriais.contains(matdesc.getMaterial())) {
								this.listaMateriais.add(matdesc.getMaterial());
							}
						}
				}
			} 
			} else if (this.integracaoCriacao) {
				this.codigo = null;
				this.descricaoTecnica = new ScoDescricaoTecnicaPadrao();
				this.descricao = null;
				setRetornoCriacaoMaterial(false);
			} else {
				this.descricaoTecnica = new ScoDescricaoTecnicaPadrao();
			}
		}
		return null;
	
	}
	
	public String cancelar() {
		descricaoTecnica = null;
		return CADASTRO_DESCRICAO_TECNICA_LIST;
	}
	
	
	public String iniciarInclusao() {
		this.material = new ScoMaterial();
		this.anexo = new ScoAnexoDescricaoTecnica();
		this.codigo = null;
		this.descricaoTecnica = new ScoDescricaoTecnicaPadrao();
		this.listaMateriais = new ArrayList<ScoMaterial>();
		this.listaAnexo = new ArrayList<ScoAnexoDescricaoTecnica>();
		this.descricao = null;
		setRetornoCriacaoMaterial(false);
		return "iniciarInclusao";
	}

	public void adicionarMaterial() {
		if (this.materialSugestion != null && this.materialSugestion.getCodigo() != null) {
			this.listaMateriais.add(this.materialSugestion);
			setRetornoCriacaoMaterial(false);
			this.materialSugestion = null;
		}
	}

	public String criarMaterial() {
		return CADASTRO_MATERIAL_CRUD;
	}

	public void removerMaterial(Integer codigoMaterial) {

		for (ScoMaterial mat : this.listaMateriais) {
			if (mat.getCodigo().equals(codigoMaterial)) {
				this.listaMateriais.remove(mat);
				break;
			}
		}

	}

	public void adicionarDocumento() {
		if (this.anexo != null) {
			this.listaAnexo.add(this.anexo);
		}
	}

	public void removerDocumento(ScoAnexoDescricaoTecnica doc) {
		this.listaAnexo.remove(doc);
	}

	public String voltar() {
		return CADASTRO_DESCRICAO_TECNICA_LIST;
	}


	public String voltarIntegracao() {
		return this.voltarPara;
	}

	public void validarMateriaisAssociados() throws ApplicationBusinessException{
		if( this.listaMateriais !=null && this.listaMateriais.isEmpty()){
			throw new ApplicationBusinessException("Obrigatório material associados", null, null);
		}
	}
	
	public void gravar()  {

		try{
			validarMateriaisAssociados();
			String mensagem = this.comprasFacade.salvarDescricaoTecnica(this.descricaoTecnica, this.descricao, this.listaMateriais, this.listaAnexo);
			cadastroDescricaoTecnicaPaginatorController.getDataModel().reiniciarPaginator();
			setRetornoCriacaoMaterial(false);
			this.apresentarMsgNegocio(Severity.INFO, mensagem);
			this.iniciar();

		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} 

	}

	public String imprimir() {

		if (this.descricaoTecnica.getCodigo() == null || (this.descricaoTecnica.getCodigo() != null && StringUtils.isEmpty(descricaoTecnica.getDescricao1()) && StringUtils.isEmpty(descricaoTecnica.getDescricao2()))) {
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_IMPRESSAO_SEM_DESCRICAO");
			return null;
		}

		imprimirDescricaoTecnicaMaterialController.setCodigo(this.descricaoTecnica.getCodigo());
		
	//	getComprasFacade().refresh(getDescricaoTecnica());
		setDescricao(getDescricaoTecnica().getDescricao());

		try {
			imprimirDescricaoTecnicaMaterialController.print();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			LOG.error(e.getMessage(), e);
		} catch (SystemException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}

		return IMPRIMIR_DESCRICAO_TECNICA;
	}

	/*
	 * Metodo que realiza a inserção do arquivo adicionado para a lista de anexos.
	 */
	/*public void listener(final FileUploadEvent event)  throws ApplicationBusinessException {

		this.tamanhoMaximoUpload = this.examesFacade.obterTamanhoMaximoBytesUploadLaudo();
		final UploadedFile item = event.getFile();
		if(item!=null){
			//this.tamanhoArquivo = item.getSize();
			// Transforma o arquivo em uma array de bytes.
			//byte[] arquivo = CoreUtil.converterFileToArrayBytes(item.getFile());
			//this.anexo.setAnexo(arquivo);
			this.anexo.setArquivo(item.getFileName());
			this.anexo.setDescricao(this.descricaoAnexo);
		}
	}*/
	
	/**
	 * Listener de upload para o arquivo
	 * 
	 * @param event
	 * @throws IOException 
	 */
	public void listener(FileUploadEvent event) throws IOException {
		if(descricaoAnexo == null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Erro", "Para adicionar um arquivo é obrigatório informar sua descrição."));
		} else {
			this.UploadedFile = event.getFile();
			anexo.setAnexo(IOUtils.toByteArray(UploadedFile.getInputstream()));
			anexo.setArquivo(UploadedFile.getFileName());
			anexo.setDescricao(this.descricaoAnexo);
			this.adicionarDocumento();
			this.limparDescricaoAnexo();
		}
	}

	public void limparDescricaoAnexo() {
		this.descricaoAnexo = null;
	}

	public void limparMaterial() {
		this.material = new ScoMaterial();
		this.materialSugestion = null;
	}

	public void  validarDescricaoAnexo() {

		if (StringUtils.isBlank(this.descricaoAnexo)) {
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_DESCRICAO_NAO_PREENCHIDA");
			this.anexo = new ScoAnexoDescricaoTecnica();
		}else if(this.tamanhoArquivo!=null &&  this.tamanhoArquivo > tamanhoMaximoUpload){
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_TAMANHO_ARQUIVO_REJEITADO",this.descricaoAnexo,tamanhoMaximoUpload);
			this.anexo = new ScoAnexoDescricaoTecnica();
		}else{
			this.listaAnexo.add(this.anexo);
			this.anexo = new ScoAnexoDescricaoTecnica();
			this.descricaoAnexo = null;
		}
	}

	
	public String gravarIntegracao() {
		try{
			String mensagem = this.comprasFacade.salvarDescricaoTecnica(this.descricaoTecnica, this.descricao, this.listaMateriais, this.listaAnexo);
			this.codigo = descricaoTecnica.getCodigo().intValue();
			this.apresentarMsgNegocio(Severity.INFO, mensagem);

		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			
		}
		this.descricaoTecnica = new ScoDescricaoTecnicaPadrao();
		this.descricaoTecnica = null;
		return this.voltarPara;

	}
	
	public void collapseTogglePesquisa() {
		if (abaAberta == null || (abaAberta != null && -1 == abaAberta)) {
			abaAberta = 0;
		} else {
			abaAberta = -1;
		}
	}
	
	public void visualizarDocumento(ScoAnexoDescricaoTecnica doc) {
		FacesContext context = FacesContext.getCurrentInstance();
		byte[] arquivo = doc.getAnexo();

		// Instancia uma HTTP Response
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

		// Seta o tipo de MIME
		response.setContentType("application/octet-stream");

		// Obtém o nome do arquivo em anexo
		String nomeArquivo = doc.getArquivo();

		// Seta o arquivo no cabeçalho
		response.addHeader("Content-disposition", "attachment; filename=\""	+ nomeArquivo + "\"");

		// Escreve a resposta no HTTP RESPONSE
		ServletOutputStream os = null;
		try {
			os = response.getOutputStream();
			os.write(arquivo);
			os.flush();
			context.responseComplete();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(os);
		}
	}


	public List<ScoMaterial> pesquisarMaterialPorCodigoDescricao(String objPesquisa) {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriaisAtivosOrderByCodigo(objPesquisa),listarMateriaisCount(objPesquisa));
	}

	public Long listarMateriaisCount(String param)	{
		return this.comprasFacade.listarScoMatriaisAtivosCount(param);
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public ScoDescricaoTecnicaPadrao getDescricaoTecnica() {
		return descricaoTecnica;
	}

	public List<ScoMaterial> getListaMateriais() {
		return listaMateriais;
	}

	public List<ScoAnexoDescricaoTecnica> getListaAnexo() {
		return listaAnexo;
	}

	public String getDescricao() {
		return descricao;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public ScoAnexoDescricaoTecnica getAnexo() {
		return anexo;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public void setDescricaoTecnica(ScoDescricaoTecnicaPadrao descricaoTecnica) {
		this.descricaoTecnica = descricaoTecnica;
	}

	public void setListaMateriais(List<ScoMaterial> listaMateriais) {
		this.listaMateriais = listaMateriais;
	}

	public void setListaAnexo(List<ScoAnexoDescricaoTecnica> listaAnexo) {
		this.listaAnexo = listaAnexo;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public void setAnexo(ScoAnexoDescricaoTecnica anexo) {
		this.anexo = anexo;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public boolean isImportando() {
		return importando;
	}

	public void setImportando(boolean importando) {
		this.importando = importando;
	}

	public boolean isRetornoCriacaoMaterial() {
		return retornoCriacaoMaterial;
	}

	public void setRetornoCriacaoMaterial(boolean retornoCriacaoMaterial) {
		this.retornoCriacaoMaterial = retornoCriacaoMaterial;
	}

	public ImprimirDescricaoTecnicaMaterialController getImprimirDescricaoTecnicaMaterialController() {
		return imprimirDescricaoTecnicaMaterialController;
	}

	public void setImprimirDescricaoTecnicaMaterialController(
			ImprimirDescricaoTecnicaMaterialController imprimirDescricaoTecnicaMaterialController) {
		this.imprimirDescricaoTecnicaMaterialController = imprimirDescricaoTecnicaMaterialController;
	}

	public boolean isIntegracaoCriacao() {
		return integracaoCriacao;
	}

	public void setIntegracaoCriacao(boolean integracaoCriacao) {
		this.integracaoCriacao = integracaoCriacao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getDescricaoAnexo() {
		return descricaoAnexo;
	}

	public void setDescricaoAnexo(String descricaoAnexo) {
		this.descricaoAnexo = descricaoAnexo;
	}

	public boolean isDesabilitaUpload() {
		return desabilitaUpload;
	}

	public void setDesabilitaUpload(boolean desabilitaUpload) {
		this.desabilitaUpload = desabilitaUpload;
	}

	public Integer getTamanhoMaximoUpload() {
		return tamanhoMaximoUpload;
	}

	public void setTamanhoMaximoUpload(Integer tamanhoMaximoUpload) {
		this.tamanhoMaximoUpload = tamanhoMaximoUpload;
	}

	public Integer getTamanhoArquivo() {
		return tamanhoArquivo;
	}

	public void setTamanhoArquivo(Integer tamanhoArquivo) {
		this.tamanhoArquivo = tamanhoArquivo;
	}

	public ScoMaterial getMaterialSugestion() {
		return materialSugestion;
	}

	public void setMaterialSugestion(ScoMaterial materialSugestion) {
		this.materialSugestion = materialSugestion;
	}
	
	public Integer getAbaAberta() {
		return abaAberta;
	}

	public void setAbaAberta(Integer abaAberta) {
		this.abaAberta = abaAberta;
	}

	public UploadedFile getUploadedFile() {
		return UploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		UploadedFile = uploadedFile;
	}

}