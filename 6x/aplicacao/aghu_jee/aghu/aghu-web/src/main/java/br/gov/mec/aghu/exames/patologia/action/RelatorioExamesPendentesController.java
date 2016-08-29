package br.gov.mec.aghu.exames.patologia.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPendentesVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class RelatorioExamesPendentesController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {			
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final String RELATORIO_EXAMES_PENDENTES_PDF = "relatorioExamesPendentesPdf";

	private static final String RELATORIO_EXAMES_PENDENTES = "relatorioExamesPendentes";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	private static final long serialVersionUID = -4292035220406165224L;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private Date dataInicial;
	private Date dataFinal;
	private AelSitItemSolicitacoes situacao;
	private AelPatologista patologista;
	private AelPatologista residenteResp;

	private DominioSituacaoExamePatologia situacaoExmAnd;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioExamesPendentesVO> colecao = new ArrayList<RelatorioExamesPendentesVO>();
	
	public List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesParaExamesPendentes(final String strPesquisa) {
		List<AelSitItemSolicitacoes>  lista = this.examesFacade.buscarListaAelSitItemSolicitacoesParaExamesPendentes((String) strPesquisa);
		lista.add(new AelSitItemSolicitacoes("EA", "EM ANDAMENTO", DominioSituacao.A));
		return lista;
	}

	public Long buscarListaAelSitItemSolicitacoesParaExamesPendentesCount(final String strPesquisa) {
		return this.examesFacade.buscarListaAelSitItemSolicitacoesParaExamesPendentesCount((String) strPesquisa);
	}
	
	public List<AelPatologista> listarPatologistaPorSeqNome(final String strPesquisa) {
		return this.returnSGWithCount(this.examesFacade.listarPatologistaPorSeqNome(strPesquisa),listarPatologistaPorSeqNomeCount(strPesquisa));
	}

	public Long listarPatologistaPorSeqNomeCount(final String strPesquisa) {
		return this.examesFacade.listarPatologistaPorSeqNomeCount(strPesquisa);
	}
	
	public List<AelPatologista> pesquisarResidenteResponsavel(final String strPesquisa){
		return this.returnSGWithCount(this.examesPatologiaFacade.listarPatologistasPorCodigoNomeFuncao(strPesquisa, DominioFuncaoPatologista.R),listarPatologistaPorSeqNomeCount(strPesquisa));
	}

	public Long pesquisarResidenteResponsavelCount(Object filtro){
		return examesPatologiaFacade.listarPatologistasPorCodigoNomeFuncaoCount((String) filtro, DominioFuncaoPatologista.R);
	}

	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioExamesPendentes.jasper";
		
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
		try {			
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("hospitalLocal", parametroRazaoSocial.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		params.put("SUBREPORT_DIR","/br/gov/mec/aghu/blococirurgico/report/");
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yy HH:mm"));
		params.put("tituloRelatorio", "Exames da PATOLOGIA CIRÚRGICA na situação " + this.situacao.getDescricao());
		params.put("dataReferenciaIni", DateUtil.obterDataFormatada(this.dataInicial, "dd/MM/yyyy"));
		params.put("dataReferenciaFim", DateUtil.obterDataFormatada(this.dataFinal, "dd/MM/yyyy"));
		
		return params;
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioExamesPendentesVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao; 
	}
	
	public String visualizarImpressao(){
		//#41868 - Validar se data Final é menor que data inicial
		if(validaDataInicialMaiorDataFinal(dataFinal, dataInicial)){
			return null;
		}

		Date dataFinalAjustada = this.dataFinal;
		if (this.dataFinal != null){
			Calendar calendarDtFinalRef = Calendar.getInstance();  
			calendarDtFinalRef.setTime(this.dataFinal); 
			calendarDtFinalRef.set(Calendar.HOUR_OF_DAY, 23);  
			calendarDtFinalRef.set(Calendar.MINUTE, 59);  
			calendarDtFinalRef.set(Calendar.SECOND, 59);  	
			dataFinalAjustada = calendarDtFinalRef.getTime();
		}

		List<Integer> paramPatologistas = setPatologistasParam();
		
		try {
			this.colecao = examesFacade.obterListaExamesPendentes(this.dataInicial, dataFinalAjustada, this.situacao.getCodigo(),
					paramPatologistas != null ? paramPatologistas.toArray(new Integer[paramPatologistas.size()]) : null, this.situacaoExmAnd);
		} catch (BaseException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
			return null;
		} catch (Exception e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			return null;
		}
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_EXAMES_PENDENTES_VAZIO");
			return null;
		}
		return RELATORIO_EXAMES_PENDENTES_PDF;
	}
	
	private boolean validaDataInicialMaiorDataFinal(Date dtFinal, Date  dtInicial) {
		if (dtFinal != null && dtInicial != null && DateUtil.validaDataMenor(dtFinal, dtInicial)){
			apresentarMsgNegocio(Severity.ERROR,"AEL_02794");
			return true;
		}
		return false;
	}	

	public void directPrint() {
		
		//#41868 - Validar se data Final é menor que data inicial
		if(validaDataInicialMaiorDataFinal(dataFinal, dataInicial)){
			return;
		}		
		
		Date dataFinalAjustada = this.dataFinal;
		if (this.dataFinal != null){
			Calendar calendarDtFinalRef = Calendar.getInstance();  
			calendarDtFinalRef.setTime(this.dataFinal); 
			calendarDtFinalRef.set(Calendar.HOUR_OF_DAY, 23);  
			calendarDtFinalRef.set(Calendar.MINUTE, 59);  
			calendarDtFinalRef.set(Calendar.SECOND, 59);  	
			dataFinalAjustada = calendarDtFinalRef.getTime();
		}
		
		List<Integer> paramPatologistas = setPatologistasParam();
		
		try {
			this.colecao = examesFacade
				.obterListaExamesPendentes(this.dataInicial, dataFinalAjustada, this.situacao.getCodigo(),
					this.patologista != null ? paramPatologistas.toArray(new Integer[paramPatologistas.size()]) : null, this.situacaoExmAnd);
		}catch (BaseException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
			return; 
		} catch (Exception e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			return;
		}
		
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_EXAMES_PENDENTES_VAZIO");
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Exames Pendentes");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}
	
	private List<Integer> setPatologistasParam() {
		List<Integer> paramPatologistas = new ArrayList<Integer>();
		
		if (patologista != null) {
			paramPatologistas.add(patologista.getSeq());
		}
		
		if (residenteResp != null) {
			paramPatologistas.add(residenteResp.getSeq());
		}
		if (paramPatologistas.isEmpty()) {
			paramPatologistas = null;
		}
		
		return paramPatologistas;
	}	
	
	public StreamedContent getRenderPdf() throws JRException, ApplicationBusinessException, IOException, DocumentException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));			
	}
	
	public void limparPesquisa() {
		this.dataInicial = null;
		this.dataFinal = null;
		this.situacao = null;
		this.residenteResp = null;
		this.patologista = null;
	}
	
	public String voltar(){
		return RELATORIO_EXAMES_PENDENTES;
	}

	//Getters and Setters
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

	public AelSitItemSolicitacoes getSituacao() {
		return situacao;
	}

	public void setSituacao(AelSitItemSolicitacoes situacao) {
		this.situacao = situacao;
	}

	public AelPatologista getPatologista() {
		return patologista;
	}

	public void setPatologista(AelPatologista patologista) {
		this.patologista = patologista;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public List<RelatorioExamesPendentesVO> getColecao() {
		return colecao;
	}

	public void setColecao(
			List<RelatorioExamesPendentesVO> colecao) {
		this.colecao = colecao;
	}
	
	public void setSituacaoExmAnd(DominioSituacaoExamePatologia situacaoExmAnd) {
		this.situacaoExmAnd = situacaoExmAnd;
	}

	public DominioSituacaoExamePatologia getSituacaoExmAnd() {
		return situacaoExmAnd;
	}

	public AelPatologista getResidenteResp() {
		return residenteResp;
	}

	public void setResidenteResp(AelPatologista residenteResp) {
		this.residenteResp = residenteResp;
	}	
}
