package br.gov.mec.aghu.compras.autfornecimento.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.ParcelasAFVO;
import br.gov.mec.aghu.compras.vo.ProgrGeralEntregaAFVO;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class ImprimirPrevisaoProgramacaoController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(ImprimirPrevisaoProgramacaoController.class);

	private static final long serialVersionUID = -3744812806595136737L;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	protected IComprasFacade comprasFacade;

	@EJB
	protected IParametroFacade parametroFacade;

	private ProgrGeralEntregaAFVO progrGeralEntregaAFVO;
	
	private static final String PAGE_PROGRAMACAO_GERAL_ENTREGA_AF = "consultarProgramacaoGeralEntregaAF";
	private static final String PAGE_IMPRIMIR_PREVISAO_PROGRAMACAO_PDF = "imprimirPrevisaoProgramacaoPdf";
	
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<ParcelasAFVO> colecao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/ImprimirPrevisaoProgramacao.jasper";
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws JRException
	 */
	public void directPrint() {

		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio("MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}

	/**
	 * Apresenta PDF na tela do navegador.
	 * 
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
	

		final DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(true)));
		return PAGE_IMPRIMIR_PREVISAO_PROGRAMACAO_PDF;
	}

	@Override
	public List<ParcelasAFVO> recuperarColecao() throws ApplicationBusinessException {
		return comprasFacade.recuperaAFParcelas(progrGeralEntregaAFVO.getNroAF());
	}

	protected void apresentarExcecaoNegocio(ApplicationBusinessException e) {
		this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dataAtual", new Date());
		params.put("nroAf", progrGeralEntregaAFVO.getAf() + "/" + progrGeralEntregaAFVO.getCp());
		params.put("fornecedor", progrGeralEntregaAFVO.getRazaoSocialFornecedor());

		DominioTipoFaseSolicitacao tipoFaseSolicitacao = comprasFacade.consultaAFMaterialServico(progrGeralEntregaAFVO.getNroAF());
		params.put("tipoFaseSolicitacao", tipoFaseSolicitacao.getCodigo());

		if (progrGeralEntregaAFVO.getCgcFornecedor() != null) {
			params.put("cnpj", progrGeralEntregaAFVO.getCgcFornecedor());
		} else {
			params.put("cnpj", progrGeralEntregaAFVO.getCpfFornecedor());
		}

		try {
			AghParametros parametros = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("nomeHospital", parametros.getVlrTexto());

		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório", e);
		}
		return params;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws DocumentException
	 * @throws ApplicationBusinessException
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException,
			ApplicationBusinessException {

		final DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public ProgrGeralEntregaAFVO getProgrGeralEntregaAFVO() {
		return progrGeralEntregaAFVO;
	}

	public void setProgrGeralEntregaAFVO(ProgrGeralEntregaAFVO progrGeralEntregaAFVO) {
		this.progrGeralEntregaAFVO = progrGeralEntregaAFVO;
	}

	public String voltar() {
		return PAGE_PROGRAMACAO_GERAL_ENTREGA_AF;
	}
	
	public List<ParcelasAFVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<ParcelasAFVO> colecao) {
		this.colecao = colecao;
	}

}
