package br.gov.mec.aghu.exames.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.RelatorioEstatisticaTipoTransporteVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import net.sf.jasperreports.engine.JRException;

public class RelatorioEstatisticaTipoTransporteController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 1766159320348219247L;

	private static final String RELATORIO_ESTATISTICA_TIPO_TRANSPORTE = "relatorioEstatisticaTipoTransporte";
	private static final String RELATORIO_ESTATISTICA_TIPO_TRANSPORTE_PDF = "relatorioEstatisticaTipoTransportePdf";

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	// Dados que serão impressos em PDF.
	private List<RelatorioEstatisticaTipoTransporteVO> colecao = new ArrayList<RelatorioEstatisticaTipoTransporteVO>();

	// Filtros
	private AghUnidadesFuncionais unidadeExecutora;
	private DominioOrigemAtendimento origem;
	private Date dataInicial;
	private Date dataFinal;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Pesquisa do relatório de consumo sintético de material considerando a classificação de materiais
	 */
	private void pesquisarRelatorioEstatisticaTipoTransporte()throws BaseException {
		this.colecao = this.examesFacade.pesquisarRelatorioEstatisticaTipoTransporte(this.unidadeExecutora, this.origem, this.dataInicial, this.dataFinal);

	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 */
	public String print()throws JRException, IOException, DocumentException  {

		try{
			// Verifica se a data final é menor que a data inicial
			if (DateUtil.validaDataMenor(this.dataFinal, this.dataInicial)) {
				this.apresentarMsgNegocio(Severity.ERROR, "AEL_01904");
			
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return null;
			}

			this.pesquisarRelatorioEstatisticaTipoTransporte();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return RELATORIO_ESTATISTICA_TIPO_TRANSPORTE_PDF;
	}
	
	public String voltar(){
		return RELATORIO_ESTATISTICA_TIPO_TRANSPORTE;
	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {
		// Verifica se a data final é menor que a data inicial
		if (DateUtil.validaDataMenor(this.dataFinal, this.dataInicial)) {
			this.apresentarMsgNegocio(Severity.ERROR, "AEL_01904");
			return;
		}

		// Verifica se a data final é menor que a data inicial
		if (DateUtil.validaDataMenor(this.dataFinal, this.dataInicial)) {
			this.apresentarMsgNegocio(Severity.ERROR, "AEL_01904");
			return;
		}

		try {
			this.pesquisarRelatorioEstatisticaTipoTransporte();

			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			
		}catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Collection<RelatorioEstatisticaTipoTransporteVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(dataAtual, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_ESTAT_TIP_TRANS");

		params.put("tituloRelatorio", "Estatística de Pacientes por Tipo de Transporte");

		// Seta unidade executora
		params.put("unidadeExecutora", this.unidadeExecutora.getDescricao().toUpperCase());

		// Seta origem quando informada
		if(this.origem != null){
			params.put("origem", this.origem.getDescricao());
		}

		params.put("periodoInicial", DateFormatUtil.obterDataFormatada(dataInicial, DateConstants.DATE_PATTERN_DDMMYYYY));
		params.put("periodoFinal", DateFormatUtil.obterDataFormatada(dataFinal, DateConstants.DATE_PATTERN_DDMMYYYY));

		Map<Integer, Integer> mapaTotaisTurno = this.examesFacade.obterTotaisTurno(this.colecao);
		Map<Integer, BigDecimal> mapaPercentuaisTurno = this.examesFacade.obterPercentuaisTurno(this.colecao);

		// Total e percentual turno 1
		params.put("totalTurno1", mapaTotaisTurno.get(1));
		params.put("percentualTurno1", mapaPercentuaisTurno.get(1));

		// Total e percentual turno 2
		params.put("totalTurno2", mapaTotaisTurno.get(2));
		params.put("percentualTurno2", mapaPercentuaisTurno.get(2));

		// Total e percentual turno 3
		params.put("totalTurno3", mapaTotaisTurno.get(3));
		params.put("percentualTurno3", mapaPercentuaisTurno.get(3));

		// Total e percentual turno 4
		params.put("totalTurno4", mapaTotaisTurno.get(4));
		params.put("percentualTurno4", mapaPercentuaisTurno.get(4));

		// Total e percentual Geral
		Integer totalGeral = this.examesFacade.obterTotalGeralTurnos(this.colecao);
		params.put("totalGeral", totalGeral);

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioEstatisticaTipoTransporte.jasper";
	}

	/**
	 * Limpa filtros da pesquisa
	 */
	public void limparPesquisa() {
		this.unidadeExecutora = null;
		this.origem = null;
		this.dataInicial = null;
		this.dataFinal = null;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}


	/**
	 * Metodo para pesquisa na suggestion box da unidade executora de exames
	 */
	public List<AghUnidadesFuncionais> obterUnidadeExecutoraExames(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasExecutoraColeta(objPesquisa);
	}

	public List<RelatorioEstatisticaTipoTransporteVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioEstatisticaTipoTransporteVO> colecao) {
		this.colecao = colecao;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}

	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

}