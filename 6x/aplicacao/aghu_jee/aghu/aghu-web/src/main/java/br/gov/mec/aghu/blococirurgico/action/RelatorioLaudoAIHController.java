package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioLaudoAIHVO;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioLaudoAIHController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioLaudoAIHController.class);

	private static final long serialVersionUID = 4071905645695966434L;

	private Integer seqAtendimento;

	private Integer codigoPac;

	private Long seq; // seq do LaudoAIH

	private Integer matricula;

	private Short vinCodigo;

	private Boolean imprimir = true;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	RelatorioLaudoAIHVO laudoAIH;

	private String voltarPara;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioLaudoAIHVO> colecao;

	public String voltar() {
		return voltarPara;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioLaudoAIH.jasper";
	}

	// @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection<RelatorioLaudoAIHVO> recuperarColecao() throws ApplicationBusinessException {
		
		RelatorioLaudoAIHVO vo = blocoCirurgicoFacade.pesquisarLaudoAIH(getSeqAtendimento(), getCodigoPac(), getSeq(), getMatricula(), getVinCodigo());
		this.colecao = Arrays.asList(vo);
		return this.colecao;
	}

	@Override
	protected BaseBean getEntidadePai() {
		if (this.colecao != null) {
			return this.colecao.get(0).getMamLaudoAih();
		}
		return null;
	}

	public void geraPendenciasCertificacaoDigital() throws BaseException {
		this.gerarDocumento(DominioTipoDocumento.AIH);
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws ApplicationBusinessException
	 * @throws IOException 
	 * @throws JRException
	 * @throws BaseException
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf() throws ApplicationBusinessException, IOException, JRException, DocumentException {

		DocumentoJasper documento = gerarDocumento(DominioTipoDocumento.AIH, Boolean.TRUE);

		return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
	}

	public void directPrint(Boolean bloquearGeracaoPendencia) {
		try {
			DocumentoJasper documento = gerarDocumento(bloquearGeracaoPendencia);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

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
			apresentarExcecaoNegocio(e);
		}

		return params;
	}

	protected void apresentarExcecaoNegocio(ApplicationBusinessException e) {
		// Apenas apresenta a mensagem de erro negocial para o cliente
		apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
	}

	// GETTERS e SETTERS

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public Integer getCodigoPac() {
		return codigoPac;
	}

	public void setCodigoPac(Integer codigoPac) {
		this.codigoPac = codigoPac;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public List<RelatorioLaudoAIHVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioLaudoAIHVO> colecao) {
		this.colecao = colecao;
	}

	public Boolean getImprimir() {
		return imprimir;
	}

	public void setImprimir(Boolean imprimir) {
		this.imprimir = imprimir;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

}
