package br.gov.mec.aghu.compras.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.vo.RelatorioSolicitacaoCompraEstocavelVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class ImprimirSolicitacaoDeEstocaveisController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final String REPORT = "compras-imprimirSolicitacaoMatEstocavel";
	
	private static final long serialVersionUID = -1504907992661982992L;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade paramFacade;
	
	private String voltarParaUrl;

	/* Dados que serão impressos em PDF. */
	private List<RelatorioSolicitacaoCompraEstocavelVO> colecao;

	// Filtros do relatório de solicitações de estocáveis
	private Date dataInicioGeracao;
	private Date dataFimGeracao;
	private Integer numSCEstocavel;
	private Date dataCompetencia;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/SolicitacaoCompraMaterialEstocavel.jasper";
	}

	public String print() throws BaseException, JRException,
			SystemException, IOException, DocumentException {

		try {
			colecao = this.solicitacaoComprasFacade
					.pesquisarSolicitacaoMaterialEstocavel(dataInicioGeracao,
							dataFimGeracao, numSCEstocavel, getDataCompetencia());
			
			limpar();

		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return REPORT;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	/**
	 * Limpa campos.
	 */
	public void limpar() {
		dataInicioGeracao = null;
		dataFimGeracao = null;
		numSCEstocavel = null;
	}

	public StreamedContent getRenderPdf() throws IOException,
			JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("nomeHospital", hospital);
		params.put("nomeRelatorio", "SCOR_SC_POR_NRO");
		params.put("tituloRelatorio", "Solicitação de Compra");
		params.put("destinoRelatorio", "SERV. PLANEJAMENTO");
		params.put("SUBREPORT_DIR","br/gov/mec/aghu/compras/report/");
		
		params.put(JRParameter.REPORT_RESOURCE_BUNDLE,
				this.getBundle());
		
		params.put("NOME_INSTITUICAO_HOSPITALAR", aghuFacade
				.recuperarInstituicaoHospitalarLocal().getNome());
		
		return params;
	}
	
	public String voltar() {
		return voltarParaUrl;
	}
	
	/**
	 * Obtem data competência, podendo esta estar parametrizada ou ser a data
	 * corrente.
	 * 
	 * @return Data
	 */
	public Date getDataCompetencia() {
		if (dataCompetencia == null) {
			boolean noParam = false;

			try {
				AghParametros param = paramFacade
						.buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);

				if (param.getVlrData() == null) {
					noParam = true;
				} else {
					dataCompetencia = param.getVlrData();
				}
			} catch (ApplicationBusinessException e) {
				noParam = true;
			}

			if (noParam) {
				dataCompetencia = new Date();
			}
		}

		return dataCompetencia;
	}
	
	@Override
	public Collection<RelatorioSolicitacaoCompraEstocavelVO> recuperarColecao() {		
		return colecao;		
	}

	public List<RelatorioSolicitacaoCompraEstocavelVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioSolicitacaoCompraEstocavelVO> colecao) {
		this.colecao = colecao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Date getDataInicioGeracao() {
		return dataInicioGeracao;
	}

	public void setDataInicioGeracao(Date dataInicioGeracao) {
		this.dataInicioGeracao = dataInicioGeracao;
	}

	public Date getDataFimGeracao() {
		return dataFimGeracao;
	}

	public void setDataFimGeracao(Date dataFimGeracao) {
		this.dataFimGeracao = dataFimGeracao;
	}

	public Integer getNumSCEstocavel() {
		return numSCEstocavel;
	}

	public void setNumSCEstocavel(Integer numSCEstocavel) {
		this.numSCEstocavel = numSCEstocavel;
	}

	public String getDataFormatada() {
		return DateUtil.dataToString(getDataCompetencia(), "MM/yyyy");
	}
}