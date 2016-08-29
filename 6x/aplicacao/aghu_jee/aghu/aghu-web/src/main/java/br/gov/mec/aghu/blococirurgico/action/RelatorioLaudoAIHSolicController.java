package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioLaudoAIHSolicVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioLaudoAIHSolicController extends ActionReport{

	private static final long serialVersionUID = 8931947265003299969L; 
		
	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	private static final Log LOG = LogFactory.getLog(RelatorioLaudoAIHSolicController.class);
	
	private Integer codigoPac;
	
	private Integer matricula;

	private Short vinCodigo;
	
	private String materialSolicitado;
	
	@EJB
	IBlocoCirurgicoFacade blocoCirurgicoFacade;
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioLaudoAIHSolicVO> colecao;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@Override
	protected Collection<RelatorioLaudoAIHSolicVO> recuperarColecao()
			throws ApplicationBusinessException {
		RelatorioLaudoAIHSolicVO vo = blocoCirurgicoFacade.pesquisarLaudoAIHSolic(this.materialSolicitado, this.codigoPac, this.matricula, this.vinCodigo);
		this.colecao = Arrays.asList(vo);
		return this.colecao;		
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioLaudoAIHSolicitacoes.jasper";
	}
	
	public StreamedContent getRenderPdf() throws ApplicationBusinessException, IOException, JRException, DocumentException {

		DocumentoJasper documento = gerarDocumento(Boolean.TRUE);

		return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
	}
	
	public void directPrint(Boolean bloquearGeracaoPendencia) {
		try {
			DocumentoJasper documento = gerarDocumento(bloquearGeracaoPendencia);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			params.put("logoSusPath", recuperarCaminhoLogo2());
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao tentar recuperar logotipo SUS para o relatório", e);
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}

		return params;
	}

	public Integer getCodigoPac() {
		return codigoPac;
	}

	public void setCodigoPac(Integer codigoPac) {
		this.codigoPac = codigoPac;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getMaterialSolicitado() {
		return materialSolicitado;
	}

	public void setMaterialSolicitado(String materialSolicitado) {
		this.materialSolicitado = materialSolicitado;
	}

	public List<RelatorioLaudoAIHSolicVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioLaudoAIHSolicVO> colecao) {
		this.colecao = colecao;
	}
	
}
