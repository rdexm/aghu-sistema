package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.action.RelatorioEspelhoPACController.EnumTargetRelatorioEspelhoLicitacao;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.ItensPACVO;
import br.gov.mec.aghu.dominio.DominioAgrupadorItemFornecedorMarca;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVO;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVOPai;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioUltimasComprasPACController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(RelatorioUltimasComprasPACController.class);
	
	private static final long serialVersionUID = -2023025218038942848L;

	private static final String RELATORIO_ULTIMAS_COMPRAS_PAC 	  = "relatorioUltimasComprasPAC";
	private static final String RELATORIO_ULTIMAS_COMPRAS_PAC_PDF = "relatorioUltimasComprasPACPDF";


	private enum RelatorioUltimasComprasPACControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_DADOS;
	}
		
	@EJB
	protected IPacFacade pacFacade;
	
	@EJB
	protected IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private List<RelUltimasComprasPACVOPai> dados =null;
	private List<RelUltimasComprasPACVO> lista;
	private Integer numeroPAC;
	private Integer qtdeCompras;
	private String fileName;
	private DominioAgrupadorItemFornecedorMarca tipoAgrupamento;
	private Boolean gerouArquivo;
		
	private Integer[] listaItensPAC;
	private Map<String,Object> listaItensPACValue  = new LinkedHashMap<String,Object>();
	
	private String[] listaModalidade;
	private Map<String,Object> listaModalidadesValue  = new LinkedHashMap<String,Object>();
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar(){
	 

	 

		carregaListaModalidade();
		setaParametroQtdeUltimasCompras();
		tipoAgrupamento = DominioAgrupadorItemFornecedorMarca.ITEM;
	
	}
	
	
	@Override
	public String recuperarArquivoRelatorio(){
		if(DominioAgrupadorItemFornecedorMarca.MARCA == tipoAgrupamento){
			return "br/gov/mec/aghu/compras/report/RelatorioUltimasComprasPACMarca.jasper";
		} else if(DominioAgrupadorItemFornecedorMarca.FORNECEDOR == tipoAgrupamento){
			return "br/gov/mec/aghu/compras/report/RelatorioUltimasComprasPACFornecedor.jasper";
		} else {
			return "br/gov/mec/aghu/compras/report/RelatorioUltimasComprasPAC.jasper";
		}	
	}
	
	
	public void print(OutputStream out, Object data) throws BaseException, JRException, SystemException, IOException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}	

	public void setaParametroQtdeUltimasCompras(){
		try {
			
			this.setQtdeCompras(parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_QTD_ULTIMAS_COMPRAS_MAT));
			
		} catch (ApplicationBusinessException e) {				
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Envia o relatório para impressora.
	 * 
	 * @author bruno.mourao
	 * @since 06/06/2011
	 */
	public void imprimir(){
		//veja RelatorioMovimentacaoController#imprimirRelatorioMovimentacao 
	}
	
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		if(DominioAgrupadorItemFornecedorMarca.ITEM == tipoAgrupamento){
			return this.dados;
		} else {
			return this.lista;
		}	
	}

	public String visualizarImpressao(){
		recuperarDados();
		
		if(DominioAgrupadorItemFornecedorMarca.ITEM == tipoAgrupamento){
			if(this.dados == null || this.dados.isEmpty()){
				String mensagem = super.getBundle().getString(RelatorioUltimasComprasPACControllerExceptionCode
						.MENSAGEM_PESQUISA_SEM_DADOS.toString());
				apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
				return null;
			}
			else{
				return RELATORIO_ULTIMAS_COMPRAS_PAC_PDF;
			}		
		} else {
			if(this.lista == null || this.lista.isEmpty()){
				String mensagem = super.getBundle().getString(RelatorioUltimasComprasPACControllerExceptionCode
						.MENSAGEM_PESQUISA_SEM_DADOS.toString());
				apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
				return null;
			}
			else{
				return RELATORIO_ULTIMAS_COMPRAS_PAC_PDF;
			}
		}	
		
	}
	
	/**
	 * Realiza a impressão do relatório
	 */
	public void impressaoDireta(){
		try {
			if(visualizarImpressao() != null){
				DocumentoJasper documento = gerarDocumento();
				getSistemaImpressao().imprimir(documento.getJasperPrint(), getEnderecoIPv4HostRemoto());
				this.apresentarMsgNegocio(Severity.INFO, EnumTargetRelatorioEspelhoLicitacao.MENSAGEM_SUCESSO_IMPRESSAO.toString());
			}				
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			
		}catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
		}
	}

	private void recuperarDados() {
		List<Integer> listaItens = new ArrayList<Integer>();
		List<String> listaCodigosModalidades = new ArrayList<String>();
		Integer qtdeUltimasCompras = this.getQtdeCompras();
		
		if (this.listaItensPAC!=null){
			listaItens =  Arrays.asList(this.listaItensPAC);
		}
		if (this.listaModalidade!=null){
			listaCodigosModalidades = Arrays.asList(this.listaModalidade);
		}
		
		if (listaItens.size() <= 0){
			listaItens = null;
		}
		
		if (listaCodigosModalidades.size() <= 0){
			listaCodigosModalidades = null;
		}
		
		if (qtdeUltimasCompras == null ||
			qtdeUltimasCompras == 0	){
			
			this.setaParametroQtdeUltimasCompras();
			qtdeUltimasCompras = this.getQtdeCompras();			
		}
		
		if(this.getNumeroPAC() != null){
			if(DominioAgrupadorItemFornecedorMarca.ITEM == tipoAgrupamento){
				this.dados = pacFacade.gerarRelatorioUltimasComprasPAC(this.getNumeroPAC(), listaItens, listaCodigosModalidades, qtdeUltimasCompras, this.tipoAgrupamento);
			} else {
				this.lista = pacFacade.buscarUltimasComprasPAC(this.getNumeroPAC(), listaItens, listaCodigosModalidades, qtdeUltimasCompras, this.tipoAgrupamento);
			}	
		}
	}
	

	public String imprimirRelatorio(){
		recuperarDados();
		if(DominioAgrupadorItemFornecedorMarca.ITEM == tipoAgrupamento){
			if(this.dados == null || this.dados.isEmpty()){
					String mensagem = super.getBundle().getString(RelatorioUltimasComprasPACControllerExceptionCode.
							MENSAGEM_PESQUISA_SEM_DADOS.toString());
					apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
					return null;
				}
				else{
					return RELATORIO_ULTIMAS_COMPRAS_PAC_PDF;
			}
		} else {
			if(this.lista == null || this.lista.isEmpty()){
				String mensagem = super.getBundle().getString(RelatorioUltimasComprasPACControllerExceptionCode.
						MENSAGEM_PESQUISA_SEM_DADOS.toString());
				apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
				return null;
			}
			else{
				return RELATORIO_ULTIMAS_COMPRAS_PAC_PDF;
			}
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		
        //Obtem parametros do sistema
        AghParametros nomeInstituicao = null;
        String cidade = "";
		try {
			cidade = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_CIDADE_PADRAO);
			nomeInstituicao = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			cidade  = "";
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		String msg = super.getBundle().getString("LABEL_NOME_INSTITUICAO");
		if(DominioAgrupadorItemFornecedorMarca.FORNECEDOR == tipoAgrupamento || DominioAgrupadorItemFornecedorMarca.FORNECEDOR == tipoAgrupamento){
			params.put("nomeInstituicao", nomeInstituicao.getVlrTexto());
		} else {
			params.put("nomeInstituicao", msg + cidade);
		}	
		
		msg = super.getBundle().getString("TITLE_RELATORIO_ULTIMAS_COMPRAS");
		msg = msg.replace("{0}", this.getQtdeCompras().toString());
		
		params.put("nomeRelatorio", msg);
		params.put("cidade", cidade);
		params.put("numLicitacao", getNumeroPAC()); 
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/compras/report/");
		
		//obtem a licitacao
		ScoLicitacao licitacao = pacFacade.obterLicitacao(getNumeroPAC());
		
		if(licitacao != null && licitacao.getModalidadeLicitacao() != null){
			params.put("descrTipoLicitacao", licitacao.getModalidadeLicitacao().getDescricao());
		}
		
		//27911
		params.put("qtdCompras", this.getQtdeCompras() != null ? this.getQtdeCompras().toString() : "");
		
		return params;
	}
 
  
 	public Map<String,Object> getlistaItensPACValue() {
 		return listaItensPACValue;
 	}
   
   public void carregaListaItensPAC(){
	   listaItensPACValue = new LinkedHashMap<String,Object>();
		try {
			for (ItensPACVO itemPACVO: this.pacFacade.pesquisarRelatorioItensPAC(this.getNumeroPAC(), true)){
				String nomeMaterialServico = (StringUtils.isNotBlank(itemPACVO.getNomeMaterial())) ? StringUtils.abbreviate(itemPACVO.getNomeMaterial(), 50) : "";
				listaItensPACValue.put(itemPACVO.getNumeroItem() + "-" + nomeMaterialServico , itemPACVO.getNumeroItem()); //label, value 		
			}
		} catch (ApplicationBusinessException e) {
			listaItensPACValue = new LinkedHashMap<String,Object>();			
		}
   }
   
   public void carregaListaModalidade(){
	   listaModalidadesValue = new LinkedHashMap<String,Object>();
	   for (ScoModalidadeLicitacao modalidade: this.comprasCadastrosBasicosFacade.listarModalidadeLicitacaoAtivas(null) ){
			String descricao = (StringUtils.isNotBlank(modalidade.getDescricao())) ? StringUtils.abbreviate(modalidade.getDescricao(), 50) : "";
			 listaModalidadesValue.put(descricao , modalidade.getCodigo()); //label, value 		
	   }	
   }
   
   public void limpar(){
		setNumeroPAC(null);
		setListaItensPAC(null);
		setListaItensPACValue(new LinkedHashMap<String, Object>());
		this.iniciar();
	}
   
   public String voltar(){
	   return RELATORIO_ULTIMAS_COMPRAS_PAC;
   }
   
	public void gerarArquivoCSV(){
		if(DominioAgrupadorItemFornecedorMarca.ITEM != this.tipoAgrupamento){
			this.recuperarDados();
			try {
				setFileName(this.pacFacade.geraArquivoCSV(this.lista, this.getNumeroPAC()));
				setGerouArquivo(Boolean.TRUE);
				this.download(fileName);
			} catch(IOException e) {
				setGerouArquivo(Boolean.FALSE);
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		} else {
			this.gerarArquivoCSVItem();
		}	
	}

   public void gerarArquivoCSVItem(){
		this.recuperarDados();
		try {
			setFileName(this.pacFacade.geraArquivoCSVItem(this.dados, this.getNumeroPAC()));
			setGerouArquivo(Boolean.TRUE);
			this.download(fileName);
		} catch(IOException e) {
			setGerouArquivo(Boolean.FALSE);
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
	}
	
	public void executarDownload() {
		if (fileName != null) {
			try {
				this.pacFacade.downloaded(fileName);
				fileName = null;
				gerouArquivo = Boolean.FALSE;
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}
   
	public Integer getNumeroPAC() {
		return numeroPAC;
	}

	public void setNumeroPAC(Integer numeroPAC) {
		this.numeroPAC = numeroPAC;
	}

	public Integer getQtdeCompras() {
		return qtdeCompras;
	}

	public void setQtdeCompras(Integer qtdeCompras) {
		this.qtdeCompras = qtdeCompras;
	}

	public Integer[] getListaItensPAC() {
		return listaItensPAC;
	}

	public void setListaItensPAC(Integer[] listaItensPAC) {
		this.listaItensPAC = listaItensPAC;
	}

	public String[] getListaModalidade() {
		return listaModalidade;
	}

	public void setListaModalidade(String[] listaModalidade) {
		this.listaModalidade = listaModalidade;
	}

	public Map<String, Object> getListaModalidadesValue() {
		return listaModalidadesValue;
	}

	public void setListaModalidadesValue(
			Map<String, Object> listaModalidadesValue) {
		this.listaModalidadesValue = listaModalidadesValue;
	}

	public Map<String, Object> getListaItensPACValue() {
		return listaItensPACValue;
	}

	public void setListaItensPACValue(Map<String, Object> listaItensPACValue) {
		this.listaItensPACValue = listaItensPACValue;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public List<RelUltimasComprasPACVO> getLista() {
		return lista;
	}

	public void setLista(List<RelUltimasComprasPACVO> lista) {
		this.lista = lista;
	}
	
	public DominioAgrupadorItemFornecedorMarca getTipoAgrupamento() {
		return tipoAgrupamento;
	}

	public void setTipoAgrupamento(
			DominioAgrupadorItemFornecedorMarca tipoAgrupamento) {
		this.tipoAgrupamento = tipoAgrupamento;
	}
	
}