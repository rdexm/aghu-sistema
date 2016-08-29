package br.gov.mec.aghu.estoque.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioTransferenciaMaterial;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.RelatorioTransferenciaMaterialVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import net.sf.jasperreports.engine.JRException;

public class RelatorioTransferenciaMaterialController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(RelatorioTransferenciaMaterialController.class);
	
	private static final long serialVersionUID = 3242495338325140110L;


	private static final String RELATORIO_TRANSFERENCIA_MATERIAL 	 = "estoque-relatorioTransferenciaMaterial";
	private static final String RELATORIO_TRANSFERENCIA_MATERIAL_PDF = "estoque-relatorioTransferenciaMaterialPdf";
	private static final String IMPRIMIR_TRANSFERENCIA_DE_MATERIAL_COD_ = "Imprimir_Transferencia_de_Material_COD_";
	private static final String EXTENSAO_CSV = ".csv";
	private static final String CONTENT_TYPE_CSV = "text/csv";
	

	
	private enum RelatorioTransferenciaMaterialControllerExceptionCode implements BusinessExceptionCode {
		SCE_00838;
	}
	
	// Variaveis da pesquisa
	private Integer numTransferenciaMaterial;
	private boolean indImprime2Vias;
	private DominioOrdenacaoRelatorioTransferenciaMaterial ordemImpressao;
	
	//indica se houve geração do arquivo do relatório
	private String fileName;
	private Boolean gerouArquivo;
	
	private Boolean transferenciaEfetivada;
	private Boolean transferenciaEstornada;
	
	private String origem;
	
	//Parametros do relatório
	private RelatorioTransferenciaMaterialVO grupoPrincipal;
	private List<RelatorioTransferenciaMaterialVO> dados = null;
	private String nomeAssinaturaServidor;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
//	@EJB
//	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/estoque/report/relatorioTransferenciaAlmoxarifado.jasper";
	}

	/**
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JRException 
	 * 
	 */
	public void inicio() throws JRException, IOException, DocumentException {
		if ("consultarTransferenciaAutomaticaNaoEfetivada".equals(origem)){
			try {
				print();
			} catch (ApplicationBusinessException e) {
				String mensagem = super.getBundle().getString(RelatorioTransferenciaMaterialControllerExceptionCode.SCE_00838.toString());
				apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
			}
		}
	}
	
	/**
	 * Recupera a coleção utilizada no relatóro
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		if(indImprime2Vias){
			List<RelatorioTransferenciaMaterialVO> colVOPai = new ArrayList<RelatorioTransferenciaMaterialVO>();
			prepararImprimirNovasVias(dados, colVOPai, Byte.valueOf("2"));
		}
		return dados;
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nomeInstituicao", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());		
		params.put("nomeRelatorio", DominioNomeRelatorio.RELATORIO_TRANSFERENCIA_MATERIAL.getDescricao());
		params.put("nomeRelatorioRodape", super.getBundle().getString("RELATORIO_TRANSFERENCIA_MATERIAL_RODAPE"));
		
		if(grupoPrincipal != null) {
			params.put("dataGerada", grupoPrincipal.getDtGeracao());
			params.put("dataEfetivada", grupoPrincipal.getDtEfetivacaoStr());
			params.put("dataEstornada", grupoPrincipal.getDtEstorno());
			params.put("seq", grupoPrincipal.getSeq());
			params.put("indTransferenciaStr", grupoPrincipal.getIndTransferenciaStr());
			params.put("indEfetivadaStr", grupoPrincipal.getIndEfetivadaStr());
			params.put("almSeq", grupoPrincipal.getAlmSeq().intValue());
			params.put("almSeqDescricao", grupoPrincipal.getAlmSeqDescricao());
			params.put("almSeqRecebe", grupoPrincipal.getAlmSeqRecebe().intValue());
			params.put("almSeqRecebeDescricao", grupoPrincipal.getAlmSeqRecebeDescricao());
			params.put("cnNumero", grupoPrincipal.getCn5() != null ? grupoPrincipal.getCn5().getNumero() : null);
			params.put("cnNumeroDescricao", grupoPrincipal.getRetornaDescricao());
		}
		params.put("indImprime2Vias", Boolean.valueOf(indImprime2Vias));
		params.put("nomeRequisitante", getNomeAssinaturaServidor());
		return params;
	}
/*	public String obterNomeAssinaturaTransf(){
		try{
    		if(matRapServidor == registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId().getMatricula()){
	    		return registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getPessoaFisica().getNomeUsual();
		    }
		    return null;
		}catch(ApplicationBusinessException e){
		   return null;
		}
	}*/

	/**
	 * Método que carrega a lista de VO's para ser usado no relatório
	 */
	public String print()throws JRException, IOException, DocumentException, ApplicationBusinessException {
		String retorno = null;
		String mensagem = null;
		if(origem == null){
			setOrigem(RELATORIO_TRANSFERENCIA_MATERIAL);
		}
		dados = estoqueFacade.gerarDadosRelatorioTransferenciaMaterialItens(getNumTransferenciaMaterial(), getOrdemImpressao());
		if(dados != null && !dados.isEmpty()){
			grupoPrincipal = dados.get(0);			
			retorno = RELATORIO_TRANSFERENCIA_MATERIAL_PDF;
		} else{
			mensagem = super.getBundle().getString(RelatorioTransferenciaMaterialControllerExceptionCode.SCE_00838.toString());
			apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
		}		
	
		try {
			DocumentoJasper documento = gerarDocumento();
			if(documento != null) {
				media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return retorno;
	}
	
	/**
	 * Prepara novas vias para serem impressas
	 */
	protected void prepararImprimirNovasVias(List<RelatorioTransferenciaMaterialVO> voPai, List<RelatorioTransferenciaMaterialVO> colVOPai, Byte nroViasPme) {
		
		for (RelatorioTransferenciaMaterialVO list : voPai) {
			list.setOrdemTela(1);
		}

		Integer ordemTela = 2;
		for (int i = 0; i < nroViasPme - 1; i++) {
			for (RelatorioTransferenciaMaterialVO transferenciaMaterialVO : voPai) {

				RelatorioTransferenciaMaterialVO copia = transferenciaMaterialVO.copiar();
				copia.setOrdemTela(ordemTela);

				colVOPai.add(copia);
			}
			ordemTela++;
		}
		voPai.addAll(colVOPai);
		indImprime2Vias = false;
	}
	
	
	/**
	 * Renderiza o PDF.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, 
																SystemException, DocumentException {
		try {
			DocumentoJasper documento = gerarDocumento();
			if(documento != null) {
				return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return null;
	}
	
	
	public void gerarArquivo() throws ApplicationBusinessException {
		dados = estoqueFacade.gerarDadosRelatorioTransferenciaMaterialItens(getNumTransferenciaMaterial(), getOrdemImpressao());
		
		try {
			if(dados != null && !dados.isEmpty()){
				grupoPrincipal = dados.get(0);			
				Map<String, Object> parametros = recuperarParametros();
				fileName = estoqueFacade.gerarCsvRelatorioTransferenciaMaterialItens(getNumTransferenciaMaterial(), dados, parametros);
				setGerouArquivo(Boolean.TRUE);
				this.dispararDownload();
			} else{
				String mensagem = super.getBundle().getString(RelatorioTransferenciaMaterialControllerExceptionCode.SCE_00838.toString());
				apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
			}		
			
		} catch(IOException e) {
			gerouArquivo = Boolean.FALSE;
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
	}
	
	/**
	 * Dispara o download para o arquivo CSV do relatório.
	 */
	public void dispararDownload(){
		if(fileName != null){
			try {
				this.download(fileName,IMPRIMIR_TRANSFERENCIA_DE_MATERIAL_COD_ + getNumTransferenciaMaterial() +EXTENSAO_CSV, CONTENT_TYPE_CSV);	
				setGerouArquivo(Boolean.FALSE);
				fileName = null;				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}	
	
	
	/**
	 * Dispara o download para o arquivo CSV do relatório.
	 */
	/*public void dispararDownload(){ 
		if(fileName != null){
			try {
				download(fileName, IMPRIMIR_TRANSFERENCIA_DE_MATERIAL_COD_ + getNumTransferenciaMaterial());
				setGerouArquivo(false);
				fileName = null;				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}*/
	
	public void impressaoDireta(){
		try {
			if(print() != null){				
				DocumentoJasper documento = gerarDocumento();
				getSistemaImpressao().imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
				setOrigem(RELATORIO_TRANSFERENCIA_MATERIAL);
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
		}
	}

	public String getOrigem() {
		return origem;
	}
	
	public void limparCampos(){
		this.numTransferenciaMaterial = null;
		setOrdemImpressao(null);
		setIndImprime2Vias(Boolean.FALSE);
		setGerouArquivo(Boolean.FALSE);
	}
	
	public String voltar(){
		if(origem != null){
			return origem;
		}
		
		return RELATORIO_TRANSFERENCIA_MATERIAL;
	}

	public Integer getNumTransferenciaMaterial() {
		return numTransferenciaMaterial;
	}
	
	public void setNumTransferenciaMaterial(Integer numTransferenciaMaterial) {
		this.numTransferenciaMaterial = numTransferenciaMaterial;
	}
	
	public boolean isIndImprime2Vias() {
		return indImprime2Vias;
	}
	
	public void setIndImprime2Vias(boolean indImprime2Vias) {
		this.indImprime2Vias = indImprime2Vias;
	}
	
	public RelatorioTransferenciaMaterialVO getGrupoPrincipal() {
		return grupoPrincipal;
	}
	
	public void setGrupoPrincipal(RelatorioTransferenciaMaterialVO grupoPrincipal) {
		this.grupoPrincipal = grupoPrincipal;
	}
	
	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public DominioOrdenacaoRelatorioTransferenciaMaterial getOrdemImpressao() {
		return ordemImpressao;
	}

	public void setOrdemImpressao(
			DominioOrdenacaoRelatorioTransferenciaMaterial ordemImpressao) {
		this.ordemImpressao = ordemImpressao;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public Boolean getTransferenciaEfetivada() {
		return transferenciaEfetivada;
	}

	public void setTransferenciaEfetivada(Boolean transferenciaEfetivada) {
		this.transferenciaEfetivada = transferenciaEfetivada;
	}

	public Boolean getTransferenciaEstornada() {
		return transferenciaEstornada;
	}

	public void setTransferenciaEstornada(Boolean transferenciaEstornada) {
		this.transferenciaEstornada = transferenciaEstornada;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getNomeAssinaturaServidor() {
		return nomeAssinaturaServidor;
	}

	public void setNomeAssinaturaServidor(String nomeAssinaturaServidor) {
		this.nomeAssinaturaServidor = nomeAssinaturaServidor;
	}
	
	
}