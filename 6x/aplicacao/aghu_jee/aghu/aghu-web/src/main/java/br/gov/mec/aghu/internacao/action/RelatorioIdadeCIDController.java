package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.PacienteIdadeCIDVO;
import br.gov.mec.aghu.model.AghCid;
import net.sf.jasperreports.engine.JRException;

public class RelatorioIdadeCIDController  extends ActionReport {


	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8460100088417630811L;

	private static final Log LOG = LogFactory.getLog(RelatorioIdadeCIDController.class);

	private static final String RELATORIO_IDADE_CID_PDF = "relatorioIdadeCIDPdf";
	private static final String RELATORIO_IDADE_CID = "relatorioIdadeCID";
	
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private List<PacienteIdadeCIDVO> listaPacienteIdadeDID = new ArrayList<PacienteIdadeCIDVO>();
	
	@PostConstruct
	public void inicio() {
		begin(conversation);
	}

	private AghCid cid;
	
	private Integer idadeInicial;
	
	private Integer idadeFinal;
	
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public String imprimirRelatorio() {
		try {
			directPrint();
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			LOG.error("Exceção capturada: ", e);
		}
		return null;
	}
	
	public String visualizarRelatorio() throws IOException, JRException, ApplicationBusinessException, SystemException, DocumentException {
		try {
			listaPacienteIdadeDID = internacaoFacade.pesquisarPacientesPorIdadeECID(idadeInicial, idadeFinal, this.cid);
			return RELATORIO_IDADE_CID_PDF;		
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_RELATORIO_VAZIO");
			LOG.error("Exceção capturada: ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			return null;
		}
	}
	
	@Override
	protected List<PacienteIdadeCIDVO> recuperarColecao() throws ApplicationBusinessException {
		return listaPacienteIdadeDID;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "Relatório idade por CID");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);

		return params;
	}
	
	public List<AghCid> pesquisarCidsConsulta(String param) {
		return aghuFacade.obterCidPorNomeCodigoAtivaPaginado(param);
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioIdadeCID.jasper";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws MECBaseException
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	public String voltar() {
		return RELATORIO_IDADE_CID;
	}

	// GETs e SETs
	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public Integer getIdadeInicial() {
		return idadeInicial;
	}

	public void setIdadeInicial(Integer idadeInicial) {
		this.idadeInicial = idadeInicial;
	}

	public Integer getIdadeFinal() {
		return idadeFinal;
	}

	public void setIdadeFinal(Integer idadeFinal) {
		this.idadeFinal = idadeFinal;
	}
}
