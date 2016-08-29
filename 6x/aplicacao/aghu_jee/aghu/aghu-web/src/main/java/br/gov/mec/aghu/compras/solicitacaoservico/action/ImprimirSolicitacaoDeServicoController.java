package br.gov.mec.aghu.compras.solicitacaoservico.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.compras.vo.SolicitacaoServicoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class ImprimirSolicitacaoDeServicoController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	
	

	private static final Log LOG = LogFactory.getLog(ImprimirSolicitacaoDeServicoController.class);
	
	private static final String PAGE_IMPRIMIR_SOLICITACAO_SERVICO = "compras-imprimirSolicitacaoDeServicoPdfCadastro";

	private static final long serialVersionUID = -5005769024181822854L;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;

	private List<SolicitacaoServicoVO> colecao = new ArrayList<SolicitacaoServicoVO>();

	private List<Integer> numSolicServicos;

	// indica para onde o botao voltar deve redirecionar
	private String voltarParaUrl;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String voltar() {
		return voltarParaUrl;
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 */
	public String print(Integer numSolicitacaoServico) throws BaseException, JRException, SystemException, IOException {

		if (numSolicServicos == null) {
			numSolicServicos = new ArrayList<Integer>();
		}

		try {
			if (numSolicitacaoServico != null) {
				getNumSolicServicos().clear();
				getNumSolicServicos().add(numSolicitacaoServico);
			}

			colecao = this.solicitacaoServicoFacade.obterRelatorioSolicitacaoServico(getNumSolicServicos());

			if (colecao.isEmpty()) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			return null;
			}

			return PAGE_IMPRIMIR_SOLICITACAO_SERVICO;

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() {

		try {
			colecao = this.solicitacaoServicoFacade.obterRelatorioSolicitacaoServico(getNumSolicServicos());

			if (colecao.isEmpty()) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("titulo", "Solicitação de Serviço");
		params.put("nomeHospital", hospital);
		params.put("dataAtual", sdf_1.format(dataAtual).toString());
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		params.put("logoAnexo", servletContext.getRealPath("/images/anexo-impressao.png"));

		return params;

	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/SolicitacaoServico.jasper";
	}

	@Override
	public Collection<SolicitacaoServicoVO> recuperarColecao() {
		return colecao;

	}

	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException {

		try {
			DocumentoJasper documento = gerarDocumento();
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public List<SolicitacaoServicoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<SolicitacaoServicoVO> colecao) {
		this.colecao = colecao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public List<Integer> getNumSolicServicos() {
		return numSolicServicos;
	}

	public void setNumSolicServicos(List<Integer> numSolicServicos) {
		this.numSolicServicos = numSolicServicos;
	}

}