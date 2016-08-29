package br.gov.mec.aghu.compras.action;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioRelatorioExcel;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import net.sf.jasperreports.engine.JRException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ImprimirRelatorioExcelController extends ActionReport {

	private static final long serialVersionUID = -7875846811874222891L;
	private static final String EXTENSAO_CSV = ".csv";
	private static final String CONTENT_TYPE_CSV = "text/csv";
	
	
	private Boolean gerouArquivo = Boolean.FALSE;
	private String nomeRelatorio;
	private String fileName;
	private String contentType;
	private String extensaoArquivo;
	private ScoGrupoMaterial grupoMaterial;
	private ScoMaterial material;
	private Integer numeroPac;
	private Integer anoEP;
	private DominioRelatorioExcel tipoRelatorio;
	private DominioRelatorioExcel relatorioSelecionado = null;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	protected IParametroFacade parametroFacade;

	private String voltarParaUrl;
	
	
	public String voltar() {
		return "voltar";
	}
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	private void inicio(){
		fileName = null;
		nomeRelatorio = null;
		gerouArquivo = Boolean.FALSE;
	}
	
	public void validaTipoRelatorio(){
		switch (tipoRelatorio) {
			case AF:
				resetParametersAF();
				relatorioSelecionado = DominioRelatorioExcel.AF;
				break;
			case AP:
				resetParametersAP();
				relatorioSelecionado = DominioRelatorioExcel.AP;
				break;	
			case EP:
				resetParametersEP();
				relatorioSelecionado = DominioRelatorioExcel.EP;
				break;
			case ES:
				relatorioSelecionado = DominioRelatorioExcel.ES;
				break;
			default:
				relatorioSelecionado = null;
				break;
			}
	}
	
	private void resetParametersEP() {
		this.anoEP = null;
	}

	private void resetParametersAP() {
		numeroPac = null;
	}

	private void resetParametersAF() {
		this.grupoMaterial = null;
		this.material = null;
	}
	
	public void gerarArquivo() throws ApplicationBusinessException {
		
		try {
			this.print();
			this.dispararDownload();
		} catch(IOException | BaseException | JRException | SystemException e ) {
			gerouArquivo = Boolean.FALSE;
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
	}
	
	public void print() throws BaseException, JRException, SystemException, IOException {
		inicio();
		extensaoArquivo = EXTENSAO_CSV;
		contentType = CONTENT_TYPE_CSV;
		if (tipoRelatorio == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_SELECIONE_RELATORIO");
		}
		if (DominioRelatorioExcel.AF.equals(tipoRelatorio)) {
			geraRelatorioAF();
		} else if(DominioRelatorioExcel.AP.equals(tipoRelatorio)){
			geraRelatorioAP();
		} else if(DominioRelatorioExcel.EP.equals(tipoRelatorio)){
			geraRelatorioEP();
		} else if(DominioRelatorioExcel.ES.equals(tipoRelatorio)){
			geraRelatorioES();
		} else{
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_RELATORIO_NAO_IMPLEMENTADO");
		}
	}
	
	private void geraRelatorioES(){
		try {
			Date data = new Date();
			String dataAtual = DateUtil.dataToString(data, "ddMMyy");
			Integer qtsLinhasRelatorio = comprasFacade.contadorLinhasRelatorioEntradaSemEmpenhoSemAssinaturaAF();
			nomeRelatorio = DominioNomeRelatorio.ARQUIVO_DADOS_SCO_ES.getDescricao()+dataAtual;
			fileName = this.comprasFacade.gerarRealatorioES();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GERADO_COM_SUCESSO_RELATORIO_ES", qtsLinhasRelatorio);
			gerouArquivo = Boolean.TRUE;
		} catch(ApplicationBusinessException ex) {
			this.apresentarExcecaoNegocio(ex);
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			gerouArquivo = Boolean.FALSE;
		}
	}

	private void geraRelatorioEP() {
		try {
			executaRelatorioEP();
		} catch(ApplicationBusinessException ex) {
			this.apresentarExcecaoNegocio(ex);
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			gerouArquivo = Boolean.FALSE;
		}
	}
	
	private void executaRelatorioEP() throws ApplicationBusinessException, IOException {
		if(this.anoEP > Calendar.getInstance().get(Calendar.YEAR)){
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RELATORIO_ANO_SUPERIOR_ATUAL");
		}else{
			fileName = this.comprasFacade.gerarRelatorioEP(this.anoEP);
			nomeRelatorio = DominioNomeRelatorio.ARQUIVO_DADOS_SCO_EP_.getDescricao()+this.anoEP;
			gerouArquivo = Boolean.TRUE;
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RELATORIO_EMITIDO_COM_SUCESSO");
		}
	}


	private void geraRelatorioAP() {
		try {
			fileName = this.comprasFacade.gerarRelatorioAP(this.numeroPac);
			nomeRelatorio = DominioNomeRelatorio.ANDAMENTO_PROCESSO_COMPRAS.getDescricao();
			gerouArquivo = Boolean.TRUE;
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RELATORIO_EMITIDO_COM_SUCESSO");
		} catch(ApplicationBusinessException ex) {
			this.apresentarExcecaoNegocio(ex);
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			gerouArquivo = Boolean.FALSE;
		}
	}

	private void geraRelatorioAF() {
		try {
			fileName = this.comprasFacade.gerarRelatorioCSVAFsPendentesComprador(this.grupoMaterial, this.material);
			nomeRelatorio = DominioNomeRelatorio.REL_AF_PENDENTE_COMPRADOR.getDescricao();
			gerouArquivo = Boolean.TRUE;
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RELATORIO_EMITIDO_COM_SUCESSO");
		} catch(ApplicationBusinessException ex) {
			this.apresentarExcecaoNegocio(ex);
			gerouArquivo = Boolean.FALSE;
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			gerouArquivo = Boolean.FALSE;
		}
	}
	
	
	
	
	public void dispararDownload(){
		if (fileName != null) {
		try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Calendar c1 = Calendar.getInstance(); // today
				this.download(fileName, "RELATORIO_"+sdf.format(c1.getTime())+EXTENSAO_CSV, CONTENT_TYPE_CSV);
				setGerouArquivo(Boolean.FALSE);
				fileName = null;				
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
	}
		resetaValoresRelatorio();
	}	
	
	/*private void dispararDownload(final String caminhoArquivo, final String nomeArq, final String mimeType) throws IOException {
		
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
		response.setContentLength((int) (new File(caminhoArquivo)).length());
		response.addHeader("Content-disposition", "attachment; filename=\"" + nomeArq + "\"");
		response.addHeader("Cache-Control", "no-cache");             
		buffer = new byte[BUFFER_SIZE_EQ_1M];
		response.flushBuffer();
		out = response.getOutputStream();
		in = new FileInputStream(new File(caminhoArquivo));
		
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);            	
		}
        
		out.flush();
		out.close();
		in.close();
		facesContext.responseComplete();
		resetaValoresRelatorio();
	}*/

	private void resetaValoresRelatorio() {
		inicio();
	}
	
	public List<ScoGrupoMaterial> pesquisarGruposMateriais(String filter) {

		return this.returnSGWithCount(comprasFacade.pesquisarGrupoMaterialPorCodigoDescricao(filter), pesquisarGruposMateriaisCount (filter));
	}

	public Long pesquisarGruposMateriaisCount(String filter) {

		return comprasFacade.pesquisarGrupoMaterialPorFiltroCount(filter);
	}

	public List<ScoMaterial> pesquisarMaterial(String filter) {

		return this.returnSGWithCount(comprasFacade.listarMateriaisAtivosPorGrupoMaterial(filter, this.grupoMaterial != null ? this.grupoMaterial.getCodigo() : null), listarMateriaisCount(filter, this.grupoMaterial != null ? this.grupoMaterial.getCodigo() : null));
	}
	
	public Long listarMateriaisCount(String filter, Integer gmtCodigo) {

		return comprasFacade.listarMaterialAtivoPorGrupoMaterialCount(filter, gmtCodigo);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return	null;
	}
	
	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public String getExtensaoArquivo() {
		return extensaoArquivo;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setExtensaoArquivo(String extensaoArquivo) {
		this.extensaoArquivo = extensaoArquivo;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public DominioRelatorioExcel getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(DominioRelatorioExcel tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setRelatorioSelecionado(DominioRelatorioExcel relatorioSelecionado) {
		this.relatorioSelecionado = relatorioSelecionado;
	}

	public DominioRelatorioExcel getRelatorioSelecionado() {
		return relatorioSelecionado;
	}

	public void setAnoEP(Integer anoEP) {
		this.anoEP = anoEP;
	}

	public Integer getAnoEP() {
		return anoEP;
	}

	public DominioRelatorioExcel[] listarTiposRelatorios() {
		 return new DominioRelatorioExcel[] {
				 DominioRelatorioExcel.AF,
				 DominioRelatorioExcel.CM,
				 DominioRelatorioExcel.IP,
				 DominioRelatorioExcel.IE,
				 DominioRelatorioExcel.CT,
				 DominioRelatorioExcel.NA,
				 DominioRelatorioExcel.SP,
				 DominioRelatorioExcel.VA,
				 DominioRelatorioExcel.AP,
				 DominioRelatorioExcel.ES,
				 DominioRelatorioExcel.VL,
				 DominioRelatorioExcel.EP,
				 DominioRelatorioExcel.LG,
				 DominioRelatorioExcel.LE,
				 DominioRelatorioExcel.DG,
				 DominioRelatorioExcel.DE,
				 DominioRelatorioExcel.DM,
				 DominioRelatorioExcel.TM,
				 DominioRelatorioExcel.IPC,
				 DominioRelatorioExcel.SPG,
				 DominioRelatorioExcel.CMM,
				 DominioRelatorioExcel.AEP,
				 DominioRelatorioExcel.SC
		 };
	}

	@Override
	protected Collection<?> recuperarColecao()
			throws ApplicationBusinessException {
		return null;
	}
}