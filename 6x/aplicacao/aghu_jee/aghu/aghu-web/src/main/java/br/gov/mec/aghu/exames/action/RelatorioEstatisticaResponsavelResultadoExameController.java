package br.gov.mec.aghu.exames.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.AelExtratoItemSolicitacaoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import net.sf.jasperreports.engine.JRException;

public class RelatorioEstatisticaResponsavelResultadoExameController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 6911149934269375291L;

	//exemplo de caminho onde está o relatorio
	public final static String ARQUIVO_JASPER = "br/gov/mec/aghu/exames/report/relatorioEstatRespLib.jasper";

	private static final String RELATORIO_ESTATISTICA_RESPONSAVEL_RESULTADO_EXAME = "relatorioEstatisticaResponsavelResultadoExame";
	private static final String RELATORIO_ESTATISTICA_RESPONSAVEL_RESULTADO_EXAME_PDF = "relatorioEstatisticaResponsavelResultadoExamePdf";
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	
	private List<AelExtratoItemSolicitacaoVO> colecao = new LinkedList<AelExtratoItemSolicitacaoVO>();
	
	//filtros de pesquisa
	private Date dataInicial;
	private Date dataFinal;
	private AghUnidadesFuncionais unidadeExecutora;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		limpar();
	
	}
	
	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}
	
	
	@Override
	public Collection<AelExtratoItemSolicitacaoVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		// Instancia mapa de parametros do relatorio
		Map<String, Object> params = new HashMap<String, Object>();
		
		// Popula parametro da data atual
		Date dataAtual = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		// Popula parametro com o nome da instituicao hospitalar
		String hospital = this.cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		
		// Seta parametros
		params.put("dataAtual", simpleDateFormat.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("dataInicial", DateUtil.obterDataFormatada(this.dataInicial, "dd/MM/yyyy"));
		params.put("dataFinal", DateUtil.obterDataFormatada(this.dataFinal, "dd/MM/yyyy"));
		params.put("exame", this.unidadeExecutora.getDescricao());
		params.put("nomeRelatorio", DominioNomeRelatorio.AELR_ESTAT_RESP_LIB.toString());//criar o nome do relatorio nessa classe dominio
		//params.put("descricaoRelatorio", DominioNomeRelatorio.AELR_ESTAT_RESP_LIB.getDescricao());

		//criar no properties
		params.put("tituloRelatorio", DominioNomeRelatorio.AELR_ESTAT_RESP_LIB.getDescricao());
	
		return params;
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return ARQUIVO_JASPER;
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		try {

			if(DateUtil.calcularDiasEntreDatas(this.dataInicial, this.dataFinal)>90){
				apresentarMsgNegocio(Severity.ERROR, "ERRO_DIFERENCA_DATAS", 90, 
									DateUtil.dataToString(dataInicial, DateConstants.DATE_PATTERN_DDMMYYYY), 
									DateUtil.dataToString(dataFinal, DateConstants.DATE_PATTERN_DDMMYYYY), 
									DateUtil.calcularDiasEntreDatas(dataInicial, dataFinal));
			}else{

				this.examesFacade.setTimeout(300);//5 min
				DateUtil.difMeses(this.dataInicial, this.dataFinal);
				this.colecao = this.examesFacade.listarExtatisticaPorResultadoExame(this.dataInicial, this.dataFinal, this.unidadeExecutora.getSeq());
				
				DocumentoJasper documento = gerarDocumento();
	
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
	
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			}
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}
	
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
		
		if(DateUtil.calcularDiasEntreDatas(this.dataInicial, this.dataFinal)>90){
			apresentarMsgNegocio(Severity.ERROR, "ERRO_DIFERENCA_DATAS", 90, 
								DateUtil.dataToString(dataInicial, DateConstants.DATE_PATTERN_DDMMYYYY), 
								DateUtil.dataToString(dataFinal, DateConstants.DATE_PATTERN_DDMMYYYY), 
								DateUtil.calcularDiasEntreDatas(dataInicial, dataFinal));
		
	
		return null;
		}
		
		this.examesFacade.setTimeout(300);//5 min
		this.colecao = this.examesFacade.listarExtatisticaPorResultadoExame(this.dataInicial, this.dataFinal, this.unidadeExecutora.getSeq());
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_ESTATISTICA_RESPONSAVEL_RESULTADO_EXAME_PDF;

	}

	public String voltar(){
		return RELATORIO_ESTATISTICA_RESPONSAVEL_RESULTADO_EXAME;
	}
	
	public void limpar() {
		this.unidadeExecutora = null;
		this.dataInicial = new Date();
		this.dataFinal = new Date();
	}
	
	public List<AelExtratoItemSolicitacaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<AelExtratoItemSolicitacaoVO> colecao) {
		this.colecao = colecao;
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

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

}
