package br.gov.mec.aghu.compras.pac.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;

import br.gov.mec.aghu.model.RapServidores;
import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.compras.action.CadastroContatoFornecedorController;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.VisualizarExtratoJulgamentoLicitacaoVO;
import br.gov.mec.aghu.dominio.DominioVisaoExtratoJulgamento;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class VisualizarExtratoJulgamentoLicitacaoController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
		visao = DominioVisaoExtratoJulgamento.TODOS;
		licitacao = new ScoLicitacao();
		list = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
		listExtrato = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
		numeroPac = null;
		pesquisaFeita = false;
		listExtratoFornecedoresFiltrados = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(VisualizarExtratoJulgamentoLicitacaoController.class);

	private static final long serialVersionUID = 6311943154533755902L;

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private Integer numeroPac;

	private DominioVisaoExtratoJulgamento visao;

	private List<DominioVisaoExtratoJulgamento> visaoItens;

	private List<VisualizarExtratoJulgamentoLicitacaoVO> colecao;

	ScoLicitacao licitacao;

	List<VisualizarExtratoJulgamentoLicitacaoVO> list;
	List<VisualizarExtratoJulgamentoLicitacaoVO> listExtrato;

	private String fornecedor; 

	private String pac;

	private String modalidade;

	private String licitacaoJasper;

	private String edital;

	private String descricao;

	private static final int BUFFER_SIZE_EQ_1M = 1024 * 1024;

	private static final String MAGIC_MIME_TYPE_EQ_APPLICATION_ZIP = "application/zip";

	private Integer numeroFornecedor;
	
	private boolean pesquisaFeita;
	
	private static final String CADASTRO_CONTATO_FORNECEDOR = "compras-cadastrarContatoFornecedor";
	
	@Inject
	private AtaRegistroPrecoController ataRegistroPrecoController;
	
	@Inject
	private CadastroContatoFornecedorController cadastroContatoFornecedorController;
	
	List<VisualizarExtratoJulgamentoLicitacaoVO> listExtratoFornecedoresFiltrados;

	public void limpar(){
		numeroPac = null;
		licitacao = new ScoLicitacao();
		list = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
		listExtrato = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
		pesquisaFeita = false;
		listExtratoFornecedoresFiltrados = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
	}

	public void pesquisar(){
		licitacao = this.pacFacade.buscarLicitacaoPorNumero(numeroPac);
		list = this.pacFacade.buscarPropostasFornecedor(numeroPac);
		pesquisaFeita = true;
	}

	public void imprimir() throws IOException, BaseException, JRException, SystemException, DocumentException{
		this.getRenderPdf();
	}

	public String contatos(){
		setNumeroFornecedor(listExtrato.get(0).getPfrFrnNumero());
		cadastroContatoFornecedorController.setNumeroFrn(getNumeroFornecedor());
		return CADASTRO_CONTATO_FORNECEDOR;
	}

	public String obterCampoTruncado(String valor, int tamanhoMaximo, boolean isTruncate){
		if(valor == null || valor.isEmpty()){
			return "";
		}else{
			return truncaCampo(valor, tamanhoMaximo, isTruncate);
		}
	} 

	private String truncaCampo(String valor, int tamanhoMaximo, boolean isTruncate) {
		if(isTruncate && valor.length() > tamanhoMaximo){
			return valor.substring(0, tamanhoMaximo) + "...";
		}
		return valor;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public List<VisualizarExtratoJulgamentoLicitacaoVO> getList() {
		return list;
	}

	public void setList(List<VisualizarExtratoJulgamentoLicitacaoVO> list) {
		this.list = list;
	}

	public DominioVisaoExtratoJulgamento getVisao() {
		return visao;
	}

	public void setVisao(DominioVisaoExtratoJulgamento visao) {
		this.visao = visao;
	}

	public List<DominioVisaoExtratoJulgamento> getVisaoItens() {
		return visaoItens;
	}

	public void setVisaoItens(List<DominioVisaoExtratoJulgamento> visaoItens) {
		this.visaoItens = visaoItens;
	}

	public List<VisualizarExtratoJulgamentoLicitacaoVO> getListExtrato() {
		return listExtrato;
	}

	public void setListExtrato(List<VisualizarExtratoJulgamentoLicitacaoVO> listExtrato) {
		this.listExtrato = listExtrato;
		listExtratoFornecedoresFiltrados = removerFornecedoresRepetidos();
	}

	public List<VisualizarExtratoJulgamentoLicitacaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<VisualizarExtratoJulgamentoLicitacaoVO> colecao) {
		this.colecao = colecao;
	}

	/*public void selecionaItem(VisualizarExtratoJulgamentoLicitacaoVO item){
		if(listExtrato.contains(item)){
			listExtrato.remove(item);
		}else{
			listExtrato.add(item);
		}
		listExtratoFornecedoresFiltrados = removerFornecedoresRepetidos();
	}*/

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getPac() {
		return pac;
	}

	public void setPac(String pac) {
		this.pac = pac;
	}

	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public String getLicitacaoJasper() {
		return licitacaoJasper;
	}

	public void setLicitacaoJasper(String licitacaoJasper) {
		this.licitacaoJasper = licitacaoJasper;
	}

	public String getEdital() {
		return edital;
	}

	public void setEdital(String edital) {
		this.edital = edital;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public void enviarEmail() throws IOException, JRException, SystemException, DocumentException {
		try {
			List<VisualizarExtratoJulgamentoLicitacaoVO> listExtratoAux = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
			for (VisualizarExtratoJulgamentoLicitacaoVO item : listExtrato) {
				listExtratoAux.add(item);
			}
	
			List<VisualizarExtratoJulgamentoLicitacaoVO> listExtratoFornecedores = removerFornecedoresRepetidos();
			
			pacFacade.validarContatosFornecedores(listExtratoFornecedores);
			
			
			for (VisualizarExtratoJulgamentoLicitacaoVO item : listExtratoFornecedores) {
				List<VisualizarExtratoJulgamentoLicitacaoVO> listaMateriaisFornecedor =  pacFacade.verificaVisaoExtratoJulgamento(visao,list,listExtratoAux,item);
				limpaParametros();
				this.fornecedor = listaMateriaisFornecedor.get(0).getCgc().toString()+" - "+listaMateriaisFornecedor.get(0).getRazaoSocial().toString();
				this.pac = this.numeroPac.toString();
				this.modalidade = this.licitacao.getModalidadeLicitacao().getDescricao();
				this.licitacaoJasper = this.licitacao.getModalidadeLicitacao().getNumDocLicit().toString();
				this.edital = this.licitacao.getModalidadeLicitacao().getNumEdital().toString();
				this.descricao = this.licitacao.getDescricao();
				this.colecao = listaMateriaisFornecedor;
				DocumentoJasper documento = gerarDocumento();
				pacFacade.enviarEmail(numeroPac, item, documento.getPdfByteArray(false), registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date()),null);
				
				ataRegistroPrecoController.setNroPac(this.numeroPac);
				ataRegistroPrecoController.setFornecedor(listaMateriaisFornecedor.get(0).getPfrFrnNumero());
				
				String nomeArquivoPDF = "ATA_REGISTRO_PREÇOS_"+numeroPac;
				DocumentoJasper documentoAta = ataRegistroPrecoController.gerarDoc();
                 byte[] byteArray =  documentoAta.getPdfByteArray(false);
                RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
				pacFacade.enviarEmail(numeroPac, item,byteArray, servidor, nomeArquivoPDF);
			}
			this.apresentarMsgNegocio(Severity.INFO, "EMAIL_EX_JULG_ENVIADO_SUCESSO");
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {	
		
		StreamedContent streamedContent = null;
		List<VisualizarExtratoJulgamentoLicitacaoVO> listExtratoAux = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
		for (VisualizarExtratoJulgamentoLicitacaoVO item : listExtrato) {
			listExtratoAux.add(item);
		}

		List<VisualizarExtratoJulgamentoLicitacaoVO> listExtratoFornecedores = removerFornecedoresRepetidos();

		File zipFile = null;
		ZipOutputStream zip = null;
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date dataAtual = new Date();

		String data = sdf_1.format(dataAtual).toString();
		data = data.replace("/", "_");
		data = data.replace(" ", "_");
		data = data.replace(":", "_");

		zipFile = new File("ExtratosJulgamentoProcessoAdministrativoCompras_"+data+".zip");
		zip = new ZipOutputStream(new FileOutputStream(zipFile));

		for (VisualizarExtratoJulgamentoLicitacaoVO item : listExtratoFornecedores) {
			List<VisualizarExtratoJulgamentoLicitacaoVO> listaMateriaisFornecedor =  pacFacade.verificaVisaoExtratoJulgamento(visao,list,listExtratoAux,item);

			// limpa e POPULA PARAMETROS
			limpaParametros();
			this.fornecedor = listaMateriaisFornecedor.get(0).getCgc().toString()+" - "+listaMateriaisFornecedor.get(0).getRazaoSocial().toString();
			this.pac = this.numeroPac.toString();
			this.modalidade = this.licitacao.getModalidadeLicitacao().getDescricao();
			this.licitacaoJasper = this.licitacao.getModalidadeLicitacao().getNumDocLicit().toString();
			this.edital = this.licitacao.getModalidadeLicitacao().getNumEdital().toString();
			this.descricao = this.licitacao.getDescricao();

			this.colecao = listaMateriaisFornecedor;
			DocumentoJasper documento = gerarDocumento();

			streamedContent = this.criarStreamedContentPdf(documento.getPdfByteArray(false));
			
			ataRegistroPrecoController.setNroPac(this.numeroPac);
			ataRegistroPrecoController.setFornecedor(listaMateriaisFornecedor.get(0).getPfrFrnNumero());
			
			ZipEntry entry = new ZipEntry("PAC_"+this.pac+"_Fornecedor_"+listaMateriaisFornecedor.get(0).getPfrFrnNumero()+".pdf");
			entry.setSize(documento.getPdfByteArray(false).length);
			
			zip.putNextEntry(entry);
			zip.write(documento.getPdfByteArray(false));
			
			DocumentoJasper documentoAta = ataRegistroPrecoController.gerarDoc();
			if(ataRegistroPrecoController.getExisteDados()){
				ZipEntry ataEntry = new ZipEntry("ATA_REGISTRO_PRECOS_"+this.numeroPac+"_"+Calendar.getInstance().getTimeInMillis()+".pdf");
				ataEntry.setSize(documentoAta.getPdfByteArray(false).length);
				
				zip.putNextEntry(ataEntry);
				zip.write(documentoAta.getPdfByteArray(false));
			}
			zip.closeEntry();
		}
		zip.close();
		internDispararDownload(zipFile.toURI(), zipFile.getName(), MAGIC_MIME_TYPE_EQ_APPLICATION_ZIP);
		return streamedContent;
	}

	protected void internDispararDownload(final URI arquivo, final String nomeArq, final String mimeType) throws IOException {

		FacesContext facesContext = null;
		HttpServletResponse response = null;
		ServletOutputStream out = null;
		FileInputStream in = null;
		byte[] buffer = null;
		int len = 0;

		facesContext = FacesContext.getCurrentInstance();
		response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		response.reset();
		response.setContentType(mimeType);
		response.setContentLength((int)(new File(arquivo)).length());
		response.addHeader("Content-disposition", "attachment; filename=\"" + nomeArq + "\"");
		response.addHeader("Cache-Control", "no-cache");             
		buffer = new byte[BUFFER_SIZE_EQ_1M];
		// committing status and headers
		response.flushBuffer();
		out = response.getOutputStream();
		in = new FileInputStream(new File(arquivo));
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);            	
		}
		out.flush();
		out.close();
		in.close();
		facesContext.responseComplete();		
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/extratoJulgamento.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("relatorio", "EXTRATO DO JULGAMENTO DO PROCESSO ADMINISTRATIVO DE COMPRAS");
		// params variaveis
		params.put("fornecedor", this.fornecedor);
		params.put("pac", this.pac);
		params.put("modalidade", this.modalidade);
		params.put("licitacao", this.licitacaoJasper);
		params.put("edital", this.edital);
		params.put("descricao", this.descricao);

		return params;
	}

	@Override
	public Collection<VisualizarExtratoJulgamentoLicitacaoVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	private void limpaParametros() {
		this.fornecedor = null;
		this.pac = null;
		this.modalidade = null;
		this.licitacaoJasper = null;
		this.edital = null;
		this.descricao = null;
	}

	private List<VisualizarExtratoJulgamentoLicitacaoVO> removerFornecedoresRepetidos() {
		List<Long> listaCgcs = new ArrayList<Long>();
		List<VisualizarExtratoJulgamentoLicitacaoVO> listExtratoFornecedores = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
		for (VisualizarExtratoJulgamentoLicitacaoVO item : listExtrato) {
			if(!listaCgcs.contains(item.getCgc())){
				listExtratoFornecedores.add(item);
				listaCgcs.add(item.getCgc());
			}
		}
		return listExtratoFornecedores;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setPesquisaFeita(boolean pesquisaFeita) {
		this.pesquisaFeita = pesquisaFeita;
	}

	public boolean isPesquisaFeita() {
		return pesquisaFeita;
	}

	public List<VisualizarExtratoJulgamentoLicitacaoVO> getListExtratoFornecedoresFiltrados() {
		return listExtratoFornecedoresFiltrados;
	}

	public void setListExtratoFornecedoresFiltrados(List<VisualizarExtratoJulgamentoLicitacaoVO> listExtratoFornecedoresFiltrados) {
		this.listExtratoFornecedoresFiltrados = listExtratoFornecedoresFiltrados;
	}

	protected AtaRegistroPrecoController getAtaRegistroPrecoController() {
		return ataRegistroPrecoController;
	}

	protected void setAtaRegistroPrecoController(AtaRegistroPrecoController ataRegistroPrecoController) {
		this.ataRegistroPrecoController = ataRegistroPrecoController;
	}

	protected static Log getLog() {
		return LOG;
	}
	
	
}
