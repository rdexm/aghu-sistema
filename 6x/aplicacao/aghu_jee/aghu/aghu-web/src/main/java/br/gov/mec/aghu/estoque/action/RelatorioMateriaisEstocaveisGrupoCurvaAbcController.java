package br.gov.mec.aghu.estoque.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.action.RelatorioMateriaisValidadeVencidaController.EnumRelatorioMateriaisValidadeVencidaControllerMessageCode;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.RelatorioMateriaisEstocaveisGrupoCurvaAbcVO;
import br.gov.mec.aghu.faturamento.action.RelatorioResumoAIHEmLotePdfController.RelatorioResumoAIHEmLotePdfExceptionCode;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioMateriaisEstocaveisGrupoCurvaAbcController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioMateriaisEstocaveisGrupoCurvaAbcController.class);
	
	private static final long serialVersionUID = 3885346716364239685L;

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private boolean gerouPDF;
	private boolean considerarCompras = true;

	private List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> dados = null;

	private File arquivoGerado;
	
	private enum RelatorioMateriaisEstocaveisGrupoCurvaAbcControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_DADOS;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void inicio() {

		try {
			final AghParametros parametro = this.parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_COMPRAS_ATIVO);

			this.considerarCompras = BigDecimal.ONE.equals(parametro
					.getVlrNumerico());

		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void dispararDownload(){
		if(arquivoGerado != null){
			try {
				download(arquivoGerado, DominioMimeType.PDF.getContentType());
				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_PDF, e, e.getLocalizedMessage()));
			}
		}
		arquivoGerado = null;
		gerouPDF = false;
	}

	/**
	 * Recupera arquivo compilado do Jasper
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		if(this.considerarCompras){
			return "br/gov/mec/aghu/estoque/report/relatorioMateriaisEstocaveisGrupoCurvaAbc.jasper";
		}else{
			return "br/gov/mec/aghu/estoque/report/relatorioMateriaisEstocaveisGrupoSemComprasCurvaAbc.jasper";
		}
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> recuperarColecao() throws ApplicationBusinessException {
		List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> vv = estoqueFacade.buscarRelatorioMaterialEstocaveisCurvaAbc(considerarCompras);
		dados = vv;
		return vv;
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("nomeInstituicao", hospital);
		params.put("nomeRelatorioRodape", "SCER_MAT_EST_GRP_ABC");
		params.put("nomeRelatorio", "Materiais Estocáveis por Grupo e Curva ABC");
		
		return params;
	}

	/**
	 * Renderiza o PDF.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, 
																JRException, SystemException,	DocumentException {
		if (dados == null || dados.size() == 0) { 
			String mensagem = super.getBundle().getString(RelatorioMateriaisEstocaveisGrupoCurvaAbcControllerExceptionCode.MENSAGEM_PESQUISA_SEM_DADOS.toString());
			apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
		} else {
			DocumentoJasper documento = gerarDocumento();
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		}
		return null;
	}
	
	public void impressaoDireta(){ 
		try {
			if (dados != null && dados.size() > 0) {
				DocumentoJasper documento = gerarDocumento();
				getSistemaImpressao().imprimir(documento.getJasperPrint(), getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio(Severity.INFO, EnumRelatorioMateriaisValidadeVencidaControllerMessageCode.MENSAGEM_SUCESSO_IMPRESSAO.toString());
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			
		}catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
		}
	}
	
	public String imprimirRelatorioPdfFile(){		
		
		// Gera o PDF
		try {
			final AghParametros parametro =  this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_COMPRAS_ATIVO);
			this.considerarCompras = BigDecimal.ONE.equals(parametro.getVlrNumerico());

			DocumentoJasper documento = gerarDocumento();
			
			final File file = File.createTempFile(DominioNomeRelatorio.SCER_MAT_EST_GRP_ABC.toString(),".pdf");
			final FileOutputStream out = new FileOutputStream(file);

			out.write(documento.getPdfByteArray(false));
			out.flush();
			out.close();

			arquivoGerado = file;
			gerouPDF = true;
			dispararDownload();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(RelatorioResumoAIHEmLotePdfExceptionCode.ERRO_AO_GERAR_PDF_RELATORIO_RESUMO_AIH_EM_LOTE));
		}	
		
		return null;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public boolean isGerouPDF() {
		return gerouPDF;
	}

	public void setGerouPDF(boolean gerouPDF) {
		this.gerouPDF = gerouPDF;
	}

	public File getArquivoGerado() {
		return arquivoGerado;
	}

	public void setArquivoGerado(File arquivoGerado) {
		this.arquivoGerado = arquivoGerado;
	}

}
