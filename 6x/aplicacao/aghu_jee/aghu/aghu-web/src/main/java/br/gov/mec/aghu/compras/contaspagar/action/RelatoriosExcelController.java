package br.gov.mec.aghu.compras.contaspagar.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.business.IContasPagarFacade;
import br.gov.mec.aghu.compras.vo.FiltroRelatoriosExcelVO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável por controlar as ações da tela de historico de paciente
 * 
 */
public class RelatoriosExcelController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3552849422505061611L;
	
	//private static final Log LOG = LogFactory.getLog(EncerramentoCompetenciaInternacaoController.class);
	
	@EJB
	private IContasPagarFacade contasPagarFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	private boolean titulosBloqueadosSelecionado;
	

	public boolean isTitulosBloqueadosSelecionado() {
		return titulosBloqueadosSelecionado;
	}

	private final String PAGE_DIVIDA_HOSPITAL = "relatoriosExcel";

	private FiltroRelatoriosExcelVO filtroRelatoriosExcelVO;
	
	private static final String MAGIC_MIME_TYPE_EQ_APPLICATION_ZIP = "application/zip";
	private static final int BUFFER_SIZE_EQ_1M = 1024 * 1024;
	
	private boolean gerouArquivo;
	
	

	private ArquivoURINomeQtdVO arqVo;

	//Classe referente ao fieldset de filtros de
	//data de pagamento
	public class Filtro {
		public boolean dataEnabled = false;
		public boolean fornecedorEnabled = false;
		// quando false exibe o label "Periodo Pagamento", quando true exibe label "Periodo da Divida"
		public boolean labelPeriodo = false;

		public void setAllEnabled(boolean enabled){
			this.dataEnabled = enabled;
			this.fornecedorEnabled = enabled;
			this.labelPeriodo = enabled;
		}
		
		public boolean isAnyEnabled(){
			return this.dataEnabled || this.fornecedorEnabled || this.labelPeriodo;
		}
		
		public boolean isDataEnabled() {
			return dataEnabled;
		}

		public void setDataEnabled(boolean dataEnabled) {
			this.dataEnabled = dataEnabled;
		}

		public boolean isFornecedorEnabled() {
			return fornecedorEnabled;
		}

		public void setFornecedorEnabled(boolean fornecedorEnabled) {
			this.fornecedorEnabled = fornecedorEnabled;
		}

		public boolean isLabelPeriodo() {
			return labelPeriodo;
		}

		public void setLabelPeriodo(boolean labelPeriodo) {
			this.labelPeriodo = labelPeriodo;
		}
	}
	
	private Filtro filtro;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		this.setFiltro(new Filtro());
		this.setFiltroRelatoriosExcelVO(new FiltroRelatoriosExcelVO());
	}

	public enum DividaHospitalMessages implements BusinessExceptionCode {
		EXCECAO_NENHUM_CAMPO_INFORMADO, 
		MENSAGEM_NENHUM_DADO_ENCONTRADO,
		MENSAGEM_ERRO_GERAR_ARQUIVO,
		MENSAGEM_RELATORIO_NAO_SELECIONADO;
	}
		
	
	/*
	 * Métodos para SuggestionBox
	 */

	public List<ScoFornecedor> pesquisarFornecedores(final String pesquisa) {
		return  this.returnSGWithCount(comprasFacade.listarFornecedoresAtivos(pesquisa, 0, 100, null, true),pesquisarFornecedoresCount(pesquisa));
	}

	public Long pesquisarFornecedoresCount(final String strPesquisa) {
		return comprasFacade.listarFornecedoresAtivosCount(strPesquisa);
	}
	
	/**
	 * CNPJ/CPF formatado
	 * 
	 * @param item
	 * @return
	 */
	public static String getCpfCnpjFormatado(ScoFornecedor item) {
		if (item.getCpf() == null) {
			if (item.getCgc() == null) {
				return StringUtils.EMPTY;
			}
			return CoreUtil.formatarCNPJ(item.getCgc());
		}
		return CoreUtil.formataCPF(item.getCpf());
	}

	/**
	 * Método para limpar campos e deixar tela como no inicio.
	 */
	public String limparCampos() {
		this.filtroRelatoriosExcelVO = new FiltroRelatoriosExcelVO();
		this.titulosBloqueadosSelecionado = false;
		filtro.setAllEnabled(false);
		
		return PAGE_DIVIDA_HOSPITAL;
	}

	
	public void habilitarDesabilitarFiltros() {
		Filtro filtro = this.getFiltro();

		switch (filtroRelatoriosExcelVO.getDominioDividaHospitalar()) {
			case DN:
				this.limparDataPagamentoRealizados();
				this.filtroRelatoriosExcelVO.setFornecedor(null);
				filtro.setAllEnabled(true);
				break;
			case TB:
				filtro.setAllEnabled(false);
				break;
			case PP:
				this.definirDataDefaultPagamentoRealizados();
				filtro.setDataEnabled(true);
				filtro.setFornecedorEnabled(false);
				filtro.setLabelPeriodo(false);
				break;
			default :
				filtro.setAllEnabled(false);
				break;
		}
	}
	/**
	 * Método responsável por gerar o período default para gerar o CSV de pagamentos realizados no período
	 * */
	private void definirDataDefaultPagamentoRealizados() {
		Date data = new Date();
		data = DateUtil.adicionaDias(data, -1);
		while (DateUtil.isFinalSemana(data)) {
			data = DateUtil.adicionaDias(data, -1);
		}
		filtroRelatoriosExcelVO.setDataInicioDivida(data);
		filtroRelatoriosExcelVO.setDataFimDivida(data);
	}
	
	private void limparDataPagamentoRealizados() {
		filtroRelatoriosExcelVO.setDataInicioDivida(null);
		filtroRelatoriosExcelVO.setDataFimDivida(null);
	}	

	public void gerarRelatorio() throws ApplicationBusinessException{
		if (filtroRelatoriosExcelVO.getDominioDividaHospitalar() == null) {
			apresentarMsgNegocio(Severity.ERROR, DividaHospitalMessages.MENSAGEM_RELATORIO_NAO_SELECIONADO.toString());
		} else {
			
			switch (filtroRelatoriosExcelVO.getDominioDividaHospitalar()) {
			case DN:
				gerarArquivoParcial();
				if (gerouArquivo) {
					dispararDownloadArquivo();
				}
				break;
			
			case TB:
				gerarArquivoParcialTitulosBloquados();
				if(gerouArquivo) {
					dispararDownloadArquivo();
				}
				break;
				
			case PP:
			   gerarArquivoParcialPagamentosRealizados();
			   if(gerouArquivo) {
				   dispararDownloadArquivo();
			   }
				break;
				
			default:
				break;
			}
		}
		
	}
	
	/**
	 * Método responsável por criar o arquivo CSV de títulos bloqueados.
	 */
	private void gerarArquivoParcialTitulosBloquados() 
			throws ApplicationBusinessException {
		try {
			arqVo = comprasFacade.gerarArquivoTextoTitulosBloqueados();
			if(arqVo != null) {
				this.gerouArquivo = Boolean.TRUE;
			} else {
				this.gerouArquivo = Boolean.FALSE;
				apresentarMsgNegocio(Severity.ERROR, DividaHospitalMessages.MENSAGEM_NENHUM_DADO_ENCONTRADO.toString());
			}	
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());

		} catch ( Exception e ){
			throw new ApplicationBusinessException(e.getMessage(),Severity.ERROR);
		}
		
	}

	public void dispararDownloadArquivo() throws ApplicationBusinessException {

		if (this.arqVo != null) {
			try {
				this.internDispararDownload(this.arqVo.getUri(), this.arqVo.getNome(), MAGIC_MIME_TYPE_EQ_APPLICATION_ZIP);
				this.gerouArquivo = Boolean.FALSE;

			} catch (IOException e) {
				throw new ApplicationBusinessException(DividaHospitalMessages.MENSAGEM_ERRO_GERAR_ARQUIVO.toString(), Severity.ERROR);
			}
		}
	}
	
	public void gerarArquivoParcial() throws ApplicationBusinessException {
		try {
			arqVo = comprasFacade.gerarArquivoTextoContasPeriodo(filtroRelatoriosExcelVO);
			if(arqVo != null) {
				this.gerouArquivo = Boolean.TRUE;
			} else {
				this.gerouArquivo = Boolean.FALSE;
				apresentarMsgNegocio(Severity.ERROR, DividaHospitalMessages.MENSAGEM_NENHUM_DADO_ENCONTRADO.toString());
			}	
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());

		} catch ( Exception e ){
			throw new ApplicationBusinessException(e.getMessage(),Severity.ERROR);
		}
	}
	
	public void gerarArquivoParcialPagamentosRealizados() throws ApplicationBusinessException {
		try {
			arqVo = comprasFacade.gerarArquivoTextoPagamentosRealizadosPeriodo(filtroRelatoriosExcelVO);
			if(arqVo != null) {
				this.gerouArquivo = Boolean.TRUE;
			} else {
				this.gerouArquivo = Boolean.FALSE;
				apresentarMsgNegocio(Severity.ERROR, DividaHospitalMessages.MENSAGEM_NENHUM_DADO_ENCONTRADO.toString());
			}	
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());

		} catch ( Exception e ){
			throw new ApplicationBusinessException(e.getMessage(),Severity.ERROR);
		}
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
		response.setContentLength((int) (new File(arquivo)).length());
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
	
	// ### GETs e SETs ###

	public String getPAGEDIVIDAHOSPITAL() {
		return PAGE_DIVIDA_HOSPITAL;
	}

	public IContasPagarFacade getContasPagarFacade() {
		return contasPagarFacade;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public Filtro getFiltro() {
		return filtro;
	}

	public void setFiltro(Filtro filtro) {
		this.filtro = filtro;
	}

	public void setTitulosBloqueadosSelecionado(boolean titulosBloqueadosSelecionado) {
		this.titulosBloqueadosSelecionado = titulosBloqueadosSelecionado;
	}

	public FiltroRelatoriosExcelVO getFiltroRelatoriosExcelVO() {
		return filtroRelatoriosExcelVO;
	}

	public void setFiltroRelatoriosExcelVO(FiltroRelatoriosExcelVO filtroRelatoriosExcelVO) {
		this.filtroRelatoriosExcelVO = filtroRelatoriosExcelVO;
	}
	
	public boolean isGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}
}


