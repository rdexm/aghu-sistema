package br.gov.mec.aghu.ambulatorio.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.ambulatorio.vo.ImprimirRelatorioMedicoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class ImprimeRelatorioMedicoController extends ActionReport{

	private static final long serialVersionUID = 9159117143880347718L;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	ImprimirRelatorioMedicoVO imprimirRelatorioMedicoVO = new ImprimirRelatorioMedicoVO();
	
	private StreamedContent media;

	private List<ImprimirRelatorioMedicoVO> colecao = new ArrayList<ImprimirRelatorioMedicoVO>();
	
	@Override
	protected Collection<ImprimirRelatorioMedicoVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	@Override
	protected void directPrint(){

		this.colecao.add(imprimirRelatorioMedicoVO);

		try {
			DocumentoJasper documento = gerarDocumento();
//			this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(true)));
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Informação Complementar");
		} catch (SistemaImpressaoException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		} 
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioMedico.jasper";
	}
	
	@Override
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {

		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	public ImprimirRelatorioMedicoVO getImprimirRelatorioMedicoVO() {
		return imprimirRelatorioMedicoVO;
	}

	public void setImprimirRelatorioMedicoVO(ImprimirRelatorioMedicoVO imprimirRelatorioMedicoVO) {
		this.imprimirRelatorioMedicoVO = imprimirRelatorioMedicoVO;
	}

	@Override
	protected Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("caminhoLogo", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images/logoClinicas.jpg"));
		return params;
	}
}
