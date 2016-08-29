package br.gov.mec.aghu.exames.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.PendenciaExecucaoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelGrupoExameTecnicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

import com.itextpdf.text.DocumentException;


/**
 * Controller para geração do relatório de pendencias de execução de exames.
 */
public class RelatorioPendenciasExecucaoExamesController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 1517584827583691072L;

	private static final String RELATORIO_PENDENCIAS_EXECUCAO_EXAMES = "relatorioPendenciasExecucaoExames";
	private static final String RELATORIO_PENDENCIAS_EXECUCAO_EXAMES_PDF = "relatorioPendenciasExecucaoExamesPdf";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<PendenciaExecucaoVO> colecao = new ArrayList<PendenciaExecucaoVO>();
	
	// Campos de filtro para pesquisa
	private AghUnidadesFuncionais unidadeExecutora;
	private AelGrupoExameTecnicas grupoExameTecnicas;
	private Date dtInicial;
	private Date dtFinal;
	private Integer numUnicoInicial;
	private Integer numUnicoFinal;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String voltar(){
		return RELATORIO_PENDENCIAS_EXECUCAO_EXAMES;
	}
	
	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
		try{
			this.colecao = examesFacade.pesquisaExamesPendentesExecucao(unidadeExecutora.getSeq(), 
																		grupoExameTecnicas.getSeq(), 
																		dtInicial, dtFinal, 
																		numUnicoInicial, numUnicoFinal);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_PENDENCIAS_EXECUCAO_VAZIO");
			return null;
		}
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_PENDENCIAS_EXECUCAO_EXAMES_PDF;
	}

	public void directPrint() {
		try{
			this.colecao = examesFacade.pesquisaExamesPendentesExecucao(unidadeExecutora.getSeq(), 
																		grupoExameTecnicas.getSeq(), 
																		dtInicial, dtFinal, 
																		numUnicoInicial, numUnicoFinal);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_PENDENCIAS_EXECUCAO_VAZIO");
			return;
		}
		try {
			DocumentoJasper documento = gerarDocumento();
	
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());
	
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Pendências de Execução");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.unidadeExecutora = null;
		this.grupoExameTecnicas = null;
		this.dtInicial = null;
		this.dtFinal = null;
		this.numUnicoInicial = null;
		this.numUnicoFinal = null;		
	}
	
	@Override
	public Collection<PendenciaExecucaoVO> recuperarColecao() throws ApplicationBusinessException{
		return this.colecao;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_PENDENCIAS");
		params.put("tituloRelatorio", "Relatório de Pendências");
		params.put("totalRegistros", colecao.size()-1);

		params.put("grupo", grupoExameTecnicas.getDescricao());
		params.put("unidade", unidadeExecutora.getDescricao());

		if(dtInicial!=null){
			params.put("dataReferenciaIni", DateFormatUtil.obterDataFormatada(dtInicial, "dd/MM/yy HH:mm"));
		}else{
			params.put("dataReferenciaIni", "");
		}
		
		if(dtFinal!=null){
			params.put("dataReferenciaFim",  DateFormatUtil.obterDataFormatada(dtFinal, "dd/MM/yy HH:mm"));
		}else{
			params.put("dataReferenciaFim", "");
		}
		
		if(numUnicoInicial != null) {
			params.put("numUnicoInicial", numUnicoInicial);
		}else {
			params.put("numUnicoInicial", "");
		}

		if(numUnicoFinal != null) {
			params.put("numUnicoFinal", numUnicoFinal);
		}else {
			params.put("numUnicoFinal", "");
		}

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioPendenciasExecucao.jasper";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	// Metódo para Suggestion Box Unidade executora
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesOrdenadaDescricao(param);
	}
	
	// Metódo para Suggestion Box de Grupos
	public List<AelGrupoExameTecnicas> pesquisarGrupos(String objPesquisa){
		return this.examesFacade.obterGrupoExameTecnicasPorDescricao(objPesquisa);
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public Date getDtInicial() {
		return dtInicial;
	}

	public void setDtInicial(Date dtInicial) {
		this.dtInicial = dtInicial;
	}

	public Date getDtFinal() {
		return dtFinal;
	}

	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}

	public Integer getNumUnicoInicial() {
		return numUnicoInicial;
	}

	public void setNumUnicoInicial(Integer numUnicoInicial) {
		this.numUnicoInicial = numUnicoInicial;
	}

	public Integer getNumUnicoFinal() {
		return numUnicoFinal;
	}

	public void setNumUnicoFinal(Integer numUnicoFinal) {
		this.numUnicoFinal = numUnicoFinal;
	}

	public AelGrupoExameTecnicas getGrupoExameTecnicas() {
		return grupoExameTecnicas;
	}

	public void setGrupoExameTecnicas(AelGrupoExameTecnicas grupoExameTecnicas) {
		this.grupoExameTecnicas = grupoExameTecnicas;
	}

}
