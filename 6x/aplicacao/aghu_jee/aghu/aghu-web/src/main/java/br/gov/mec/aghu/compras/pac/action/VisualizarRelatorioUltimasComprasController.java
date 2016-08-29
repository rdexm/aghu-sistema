package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class VisualizarRelatorioUltimasComprasController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	private static final String CONSULTAR_ULTIMAS_COMPRAS_MATERIAL_LIST = "consultarUltimasComprasMaterialList";

	private static final long serialVersionUID = 6311943154533755902L;
	
	
	private List<ScoUltimasComprasMaterialVO> lista = new ArrayList<ScoUltimasComprasMaterialVO>();
	
	@EJB
	protected IParametroFacade parametroFacade;
	
	private String material;
	
	private String unidade;
	
	private DocumentoJasper documento;
	
	private String voltarPara;

	public void inicio(){
		//
		// TODO  MOSTRAR PREVIEW DO REPORT NA TELA
		//
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void directPrint(){
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/relatorioUltimasCompras.jasper";
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String hospital = null;
		
		try {
			AghParametros hospitalLocal = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			hospital = hospitalLocal.getVlrTexto();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("relatorio", "Relatorio Ultimas Compras");
		
		// params variaveis
		params.put("material", this.material);
		params.put("unidade", this.unidade);

		return params;
	}

	public String voltar(){
		return CONSULTAR_ULTIMAS_COMPRAS_MATERIAL_LIST;
	}
	
	@Override
	public Collection<ScoUltimasComprasMaterialVO> recuperarColecao() {
		return this.lista;
	}

	public List<ScoUltimasComprasMaterialVO> getLista() {
		return lista;
	}

	public void setLista(List<ScoUltimasComprasMaterialVO> lista) {
		this.lista = lista;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public DocumentoJasper getDocumento() {
		return documento;
	}

	public void setDocumento(DocumentoJasper documento) {
		this.documento = documento;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}
