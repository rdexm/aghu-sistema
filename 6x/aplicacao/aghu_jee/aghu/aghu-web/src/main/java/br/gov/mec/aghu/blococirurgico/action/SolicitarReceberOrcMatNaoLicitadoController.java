package br.gov.mec.aghu.blococirurgico.action;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.blococirurgico.vo.SolicitarReceberOrcMatNaoLicitadoVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoMaterialItem;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFHistoricoExecucao;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;


public class SolicitarReceberOrcMatNaoLicitadoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(SolicitarReceberOrcMatNaoLicitadoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -4646152782263453102L;
	
	private static final String VOLTAR_TELA_DETALHE_ACOMPANHAMENTO = "blococirurgico-acompanharProcessoAutorizacaoOPMs";

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private SolicitarReceberOrcMatNaoLicitadoVO matNaoLicitadoVO;
	private List<MbcItensRequisicaoOpmes> listaMatSolicitacaoOrcamento;
	private MbcItensRequisicaoOpmes itemDetalhado;
	private AghWFHistoricoExecucao esclarecimento;
	private ScoMaterial material;
	private List<ScoMaterial> listaMateriais;
	private Boolean desabilitarConcluirOrcamento;
	private Boolean arquivoAdicionadoMaiorTamanhoPermitido;
	
	// Anexo
	private UploadedFile uploadedFile;
	
	private String nomeUpload;
	
	// Parâmetros vindos da tela de detalhe de acompanhamento.
	private String voltarParaUrl;
	private Short seqRequisicaoOpme;
	private Integer seqEtapa;
	
	private String observacaoConcluir;
	
	public void inicio(){
		nomeUpload = null;
		this.setDesabilitarConcluirOrcamento(Boolean.TRUE);
		if(this.seqRequisicaoOpme != null){
			this.matNaoLicitadoVO = this.pesquisarMatNaoLicitado(this.seqRequisicaoOpme);
			this.listaMatSolicitacaoOrcamento = this.pesquisarMatSolicitacaoOrcamento(this.seqRequisicaoOpme);
			this.validarDesabilitarConcluirOrcamento();
		}
		
		if(this.itemDetalhado == null){
			itemDetalhado = new MbcItensRequisicaoOpmes();
		}
		
		esclarecimento = new AghWFHistoricoExecucao();
	
	}
		
	private void validarDesabilitarConcluirOrcamento(){
		if(this.listaMatSolicitacaoOrcamento != null && this.listaMatSolicitacaoOrcamento.size() > 0){
			for(MbcItensRequisicaoOpmes vo : listaMatSolicitacaoOrcamento){
				if("Pendente".equals(vo.getSituacao())){
					setDesabilitarConcluirOrcamento(Boolean.TRUE);
					return;
				}
			}
		}
		setDesabilitarConcluirOrcamento(Boolean.FALSE);
	}
	
	private SolicitarReceberOrcMatNaoLicitadoVO pesquisarMatNaoLicitado(Short seqRequisicaoOpme2){
		return this.blocoCirurgicoFacade.pesquisarMatNaoLicitado(seqRequisicaoOpme2);
	}
	
	private List<MbcItensRequisicaoOpmes> pesquisarMatSolicitacaoOrcamento(Short seqRequisicaoOpme2){
		return this.listaMatSolicitacaoOrcamento = this.blocoCirurgicoFacade.pesquisarMatSolicitacaoOrcamento(seqRequisicaoOpme2);
	}
	
	public void detalharMaterial(MbcItensRequisicaoOpmes item){
		this.nomeUpload = null;
		this.setItemDetalhado(item);
	}
	
	public String getDescricaoTrunc(String desc){
		if(desc != null){
			return StringUtil.trunc(desc, Boolean.TRUE, 60L);
		}
		return "";
	}
	
	public void gravarDetalheMaterial() throws ApplicationBusinessException, FileNotFoundException, IOException{
		MbcMateriaisItemOpmes novoMateriaisItens = null;
		if(this.itemDetalhado != null){
//			this.itemDetalhado = this.blocoCirurgicoFacade.buscarPorChavePrimaria(this.itemDetalhado.getSeq());
			if(itemDetalhado.getValorNovoMaterial() != null){
				itemDetalhado.setValorUnitarioIph(itemDetalhado.getValorNovoMaterial());
			}
			//if(this.material != null){
				novoMateriaisItens = criarMaterialOpmes();
				
				this.blocoCirurgicoFacade.gravarMateriaisItemOpmes(novoMateriaisItens);
			//}
//			if(uploadedFile != null){
//				this.itemDetalhado.setAnexoOrcamento(ziparArquivoParaSalvar().toByteArray());
//			}
			this.blocoCirurgicoFacade.atualizarDetalheMaterial(this.itemDetalhado);
			atualizaItemDetalhadoNaLista(itemDetalhado);
			
			//OPME-JOURNAL
			blocoCirurgicoOpmesFacade.insereMbcItensRequisicaoOpmesJn(this.itemDetalhado, DominioOperacoesJournal.UPD);
			this.validarDesabilitarConcluirOrcamento();
		}
		
		//OPME-JOURNAL
		if(novoMateriaisItens != null){
			this.blocoCirurgicoOpmesFacade.insereMbcMateriaisItemOpmesJn(novoMateriaisItens, DominioOperacoesJournal.INS);
		}
		this.listaMatSolicitacaoOrcamento = this.pesquisarMatSolicitacaoOrcamento(this.seqRequisicaoOpme);
		super.closeDialog("modalDetalharMaterialWG");
	}
	
	public String getUrlBase() {
	    //return FacesContext.getCurrentInstance().getExternalContext().getRealPath(System.getProperty("java.io.tmpdir"));
		return System.getProperty("java.io.tmpdir");
	}
	
	//private ByteArrayOutputStream ziparArquivoParaSalvar() throws IOException, FileNotFoundException {

//		String nomeDiretorioTemp = System.getProperty("java.io.tmpdir"); 
//	    String separator = System.getProperty("file.separator");  
//		
//		new File(getUrlBase() + separator);
//        File result = new File(getUrlBase() + separator + uploadedFile.getFileName());	
//		
//        FileOutputStream fileOutputStream = new FileOutputStream(result);
//	
//        int RETUbulk;
//        InputStream inputStream = uploadedFile.getInputstream();
//        while (true) {
//            bulk = inputStream.read(buffer);
//            if (bulk < 0) {
//                break;
//            }
//            fileOutputStream.write(buffer, 0, bulk);
//            fileOutputStream.flush();
//        }
//
//        fileOutputStream.close();
//        inputStream.close();
//
//		
//		File file = new File(getUrlBase() +separator+ uploadedFile.getFileName());
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ZipOutputStream zos = new ZipOutputStream(baos);
//		ZipEntry ze= new ZipEntry(this.uploadedFile.getFileName());
//		zos.putNextEntry(ze);
//		FileInputStream in = new FileInputStream(file);
//		int len;
//		while ((len = in.read(buffer)) > 0) {
//			zos.write(buffer, 0, len);
//		}
//		
//		in.close();
//		zos.closeEntry();
//		
//		//remember close it
//		zos.close();
//		return baos;
		
//	}
	
	
	public void atualizaItemDetalhadoNaLista(MbcItensRequisicaoOpmes item) {
		for(MbcItensRequisicaoOpmes itemMaterial: this.listaMatSolicitacaoOrcamento) {
			if(itemMaterial.getSeq() == item.getSeq()) {
				itemMaterial = item;
			}
		}
	}	

	private MbcMateriaisItemOpmes criarMaterialOpmes()
			throws ApplicationBusinessException {
		MbcMateriaisItemOpmes novoMateriaisItens = new MbcMateriaisItemOpmes();
		List<MbcMateriaisItemOpmes> listaMat = blocoCirurgicoFacade.buscarItemMaterialPorItemRequisicao(this.itemDetalhado);
		if(listaMat != null){
			if(listaMat.size() > 0){
				novoMateriaisItens = listaMat.get(0);
			}
		}
		if(novoMateriaisItens.getSeq() == null){
			novoMateriaisItens.setCriadoEm(new Date());
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			novoMateriaisItens.setRapServidores(servidorLogado);
		}
		if(this.material == null){
			Integer maxId = comprasFacade.obterScoMaterialMaxId();
			novoMateriaisItens.setMaterial(comprasFacade.obterScoMaterial(maxId));
		}else{
			novoMateriaisItens.setMaterial(this.material);
		}
		novoMateriaisItens.setItensRequisicaoOpmes(this.itemDetalhado);
		novoMateriaisItens.setSituacao(DominioSituacaoMaterialItem.A);
		novoMateriaisItens.setQuantidadeConsumida(1);
		novoMateriaisItens.setQuantidadeConsumida(0);
		novoMateriaisItens.setQuantidadeSolicitada(1);
		
		return novoMateriaisItens;
	}
	
	public String gravarSolitEsclarecimento(){
		
		try {
				if(this.esclarecimento != null){
					AghWFEtapa etapaAtual = this.getEtapaPorSeq(this.seqEtapa);
					
					this.blocoCirurgicoOpmesFacade.rejeitarEtapaFluxoAutorizacaoOPMEs(etapaAtual, this.esclarecimento.getObservacao());
					
					if(this.voltarParaUrl.equalsIgnoreCase(VOLTAR_TELA_DETALHE_ACOMPANHAMENTO)){
						return VOLTAR_TELA_DETALHE_ACOMPANHAMENTO;			
					}
				}
				
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
				
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			}
		
		return null;
	}
	
	public void listener(FileUploadEvent event) {
		this.nomeUpload = null;
		this.uploadedFile = event.getFile();
		this.nomeUpload = this.uploadedFile.getFileName();
		try {
			
			byte[] buffer = new byte[1024 * 1024];
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			ZipEntry ze= new ZipEntry(this.uploadedFile.getFileName());
			zos.putNextEntry(ze);
			InputStream in = this.uploadedFile.getInputstream();
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
	 
			in.close();
			zos.closeEntry();
	 
			//remember close it
			zos.close();
			
			itemDetalhado.setAnexoOrcamento(baos.toByteArray());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	public void download(MbcItensRequisicaoOpmes item){
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse resp = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	    resp.setContentType("application/zip, application/octet-stream");
	    resp.setHeader("Content-Disposition", "attachment;filename=" + "arquivo_"+item.getSeq().toString()+".zip");
	    try {
			resp.getOutputStream().write(item.getAnexoOrcamento());
			context.responseComplete();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public String voltar() {
		return VOLTAR_TELA_DETALHE_ACOMPANHAMENTO;
	}
	
	public String concluirOrcamento(){
		
		try {
				RapServidores servidor = this.getServidorLogado();
				AghWFEtapa etapaAtual = this.getEtapaPorSeq(this.seqEtapa);
				
				this.blocoCirurgicoOpmesFacade.executarEtapaFluxoAutorizacaoOPMEs(servidor, etapaAtual, observacaoConcluir);
				
				this.apresentarMsgNegocio(Severity.INFO, "MSG_CONCLUSAO_ORCAMENTO_SUCESSO");
				//if(this.voltarParaUrl.equalsIgnoreCase(VOLTAR_TELA_DETALHE_ACOMPANHAMENTO)){
				return VOLTAR_TELA_DETALHE_ACOMPANHAMENTO;			
				//}
				
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
			}
		
			return null;
	}
	
	public List<ScoMaterial> pesquisarMateriais(String material){
		
		try {
			AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);
			this.listaMateriais = this.comprasFacade.obterMateriaisOrteseseProtesesSB(parametro.getVlrNumerico(), material);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return this.listaMateriais;
	}
	
	private RapServidores getServidorLogado() throws ApplicationBusinessException{
		return this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
	}
	
	private AghWFEtapa getEtapaPorSeq(Integer etapaSeq){
		return this.blocoCirurgicoOpmesFacade.obterEtapaPorSeq(etapaSeq);
	}
	
	public SolicitarReceberOrcMatNaoLicitadoVO getMatNaoLicitadoVO() {
		return matNaoLicitadoVO;
	}

	public void setMatNaoLicitadoVO(SolicitarReceberOrcMatNaoLicitadoVO matNaoLicitadoVO) {
		this.matNaoLicitadoVO = matNaoLicitadoVO;
	}
	
	public List<MbcItensRequisicaoOpmes> getListaMatSolicitacaoOrcamento() {
		return listaMatSolicitacaoOrcamento;
	}

	public void setListaMatSolicitacaoOrcamento(List<MbcItensRequisicaoOpmes> listaMatSolicitacaoOrcamento) {
		this.listaMatSolicitacaoOrcamento = listaMatSolicitacaoOrcamento;
	}

	public Short getSeqRequisicaoOpme() {
		return seqRequisicaoOpme;
	}

	public void setSeqRequisicaoOpme(Short seqRequisicaoOpme) {
		this.seqRequisicaoOpme = seqRequisicaoOpme;
	}

	public Integer getSeqEtapa() {
		return seqEtapa;
	}

	public void setSeqEtapa(Integer seqEtapa) {
		this.seqEtapa = seqEtapa;
	}

	public MbcItensRequisicaoOpmes getItemDetalhado() {
		return itemDetalhado;
	}

	public void setItemDetalhado(MbcItensRequisicaoOpmes itemDetalhado) {
		this.itemDetalhado = itemDetalhado;
	}

	public AghWFHistoricoExecucao getEsclarecimento() {
		return esclarecimento;
	}

	public void setEsclarecimento(AghWFHistoricoExecucao esclarecimento) {
		this.esclarecimento = esclarecimento;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Boolean getDesabilitarConcluirOrcamento() {
		return desabilitarConcluirOrcamento;
	}

	public void setDesabilitarConcluirOrcamento(
			Boolean desabilitarConcluirOrcamento) {
		this.desabilitarConcluirOrcamento = desabilitarConcluirOrcamento;
	}

	public Boolean getArquivoAdicionadoMaiorTamanhoPermitido() {
		return arquivoAdicionadoMaiorTamanhoPermitido;
	}

	public void setArquivoAdicionadoMaiorTamanhoPermitido(
			Boolean arquivoAdicionadoMaiorTamanhoPermitido) {
		this.arquivoAdicionadoMaiorTamanhoPermitido = arquivoAdicionadoMaiorTamanhoPermitido;
	}

	public String getNomeUpload() {
		return nomeUpload;
	}

	public void setNomeUpload(String nomeUpload) {
		this.nomeUpload = nomeUpload;
	}

	public String getObservacaoConcluir() {
		return observacaoConcluir;
	}

	public void setObservacaoConcluir(String observacaoConcluir) {
		this.observacaoConcluir = observacaoConcluir;
	}
	
	
}
