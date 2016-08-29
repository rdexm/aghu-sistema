package br.gov.mec.aghu.ambulatorio.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioProgramacaoGradeCabecalhoVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioProgramacaoGradeVO;
import br.gov.mec.aghu.ambulatorio.vo.VRapServidorConselhoVO;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.model.VRapServidorConselhoId;

import com.itextpdf.text.DocumentException;

public class RelatorioGradeProgramacaoController extends ActionReport{

	/**
	 * 
	 */
	private static final long serialVersionUID = -84759321L;
	private static final Log LOG = LogFactory.getLog(RelatorioGradeProgramacaoController.class);
	private static final String URL_DOCUMENTO_JASPER = "br/gov/mec/aghu/ambulatorio/report/relatorioProgramacaoGrade.jasper";
	private static final String URL_SUBREPORT_JASPER = "br/gov/mec/aghu/ambulatorio/report/";
	private static final String URL_TELA_PDF = "ambulatorio-visualizarRelatorioProgramacaoGrade";
	private static final String URL_VOLTAR = "ambulatorio-relatorioGradeProgramacao";
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private AghEspecialidades especialidade;
	private AghEquipes equipe;
	private VRapServidorConselhoVO conselhoVO;
	private VRapServidorConselho conselho;
	private FccCentroCustos servico;
	private Integer numeroGrade;
	private Date mesAno;
	private StreamedContent streamedContent;
	private Boolean gerouArquivo = Boolean.FALSE;
	private List<RelatorioProgramacaoGradeVO> listaRelatorioVO;
	private List<RelatorioProgramacaoGradeCabecalhoVO> listaCabecalhoVO;
	private String sigla;
	private Integer seqEquipe;
	private Integer cctCodigo;
	private StreamedContent media;
	
	@PostConstruct
	public void init(){
		this.begin(conversation);
		limpar();
	}
	
	public void gerarArquivo() {
		
		try {
			if(especialidade != null){
				sigla = especialidade.getSigla();
			}
			if(equipe != null){
				seqEquipe = equipe.getSeq();
			}
			if(servico != null){
				cctCodigo = servico.getCodigo();
			}
			String fileName = this.ambulatorioFacade.obterCSVProgramacaoGrade(DateUtil.truncaData(mesAno), 
					DateUtil.obterDataFimCompetencia(mesAno), numeroGrade, sigla, seqEquipe, cctCodigo, conselho);
			if (fileName == null) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUGGESTION_NENHUM_REGISTRO_ENCONTRADO");
			} else {
				String header = DominioNomeRelatorio.RELATORIO_PROGRAMACAO_GRADE.getDescricao() + DominioNomeRelatorio.EXTENSAO_CSV;
				download(fileName, header);
			}
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String print() throws ApplicationBusinessException, JRException, IOException, DocumentException {
	
		carregarListaRelatorioProgramacaoGrade();
		DocumentoJasper documento = gerarDocumento();
		this.media = this.criarStreamedContentPdf(documento.getPdfByteArray(true));
		
        return URL_TELA_PDF;
    }
	
	public void imprimirVisualizar() {
		try {
			listaCabecalhoVO = new ArrayList<RelatorioProgramacaoGradeCabecalhoVO>();
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public void directPrint() {
		try {
			carregarListaRelatorioProgramacaoGrade();
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	private void carregarListaRelatorioProgramacaoGrade() {
		listaCabecalhoVO = new ArrayList<RelatorioProgramacaoGradeCabecalhoVO>();
		
		if(especialidade != null){
			sigla = especialidade.getSigla();
		}
		if(equipe != null){
			seqEquipe = equipe.getSeq();
		}
		if(servico != null){
			cctCodigo = servico.getCodigo();
		}
		this.listaRelatorioVO = this.ambulatorioFacade.obterRelatorioProgramacaoGrade(DateUtil.truncaData(mesAno), 
				DateUtil.obterDataFimCompetencia(mesAno), numeroGrade, sigla, seqEquipe, cctCodigo, conselho);
	}
	
	public void limparConselho(){
		conselho = null;
	}
	
	public void obterConselhoOriginal(){
		VRapServidorConselhoId idConselho = new VRapServidorConselhoId(conselhoVO.getMatricula(), conselhoVO.getVinCodigo());
		conselho = ambulatorioFacade.obterRapServidorConselhoOriginal(idConselho);
	}
	
	public void limpar(){
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		
		especialidade = null;
		equipe = null;
		conselho = null;
		conselhoVO = null;
		servico = null;
		numeroGrade = null;
		mesAno = null;
		sigla = null;
		seqEquipe = null;
		cctCodigo = null;
	}
	
	public void limparSigla(){
		sigla = null;
	}
	
	public void limparSeqEquipe(){
		seqEquipe = null;
	}
	
	public void limparCctCodigo(){
		cctCodigo = null;
	}
	
	
	public String voltar(){
		return URL_VOLTAR;
	}
	
	//SB1
	public List<AghEspecialidades> obterEspecialidades(String parametro){
		return returnSGWithCount(ambulatorioFacade.obterEspecialidadesPorSiglaOUNomeEspecialidade(parametro),
				ambulatorioFacade.obterEspecialidadesPorSiglaOUNomeEspecialidadeCount(parametro));
	}
	
	//SB2
	public List<AghEquipes> obterEquipes(String parametro){
		return returnSGWithCount(ambulatorioFacade.obterEquipesPorSeqOuNome(parametro), ambulatorioFacade.obterEquipesPorSeqOuNomeCount(parametro));
	}
	
	//SB3
	public List<VRapServidorConselho> obterConselho(String parametro){
		return returnSGWithCount(ambulatorioFacade.obterVRapServidorConselhoPorNumConselhoOuNome(parametro), 
				ambulatorioFacade.obterVRapServidorConselhoPorNumConselhoOuNomeCount(parametro));
	}
	
	//SB4
	public List<FccCentroCustos> obterServicos(String parametro){
		return returnSGWithCount(ambulatorioFacade.obterCentroCustoPorCodigoDescricao(parametro), 
				ambulatorioFacade.obterCentroCustoPorCodigoDescricaoCount(parametro));
	}

	@Override
	protected Collection<RelatorioProgramacaoGradeCabecalhoVO> recuperarColecao() throws ApplicationBusinessException {	
		if(listaRelatorioVO != null && !listaRelatorioVO.isEmpty()){
			RelatorioProgramacaoGradeCabecalhoVO cabecalhoVO =  new RelatorioProgramacaoGradeCabecalhoVO();
			cabecalhoVO.setGrades(listaRelatorioVO);
			listaCabecalhoVO.add(cabecalhoVO);
		}
		return this.listaCabecalhoVO;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return URL_DOCUMENTO_JASPER;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {	
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		try {				
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			AghParametros parametroLabelSetor = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA);

			parametros.put("SUBREPORT_DIR", URL_SUBREPORT_JASPER);
			parametros.put("NOME_HOSPITAL", parametroRazaoSocial.getVlrTexto());
			parametros.put("LABEL_SETOR", parametroLabelSetor.getVlrTexto());
			parametros.put("MES_ANO", mesAno);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return parametros;
	}
	
	/**
	 * 
	 * Percorre o formulÃ¡rio resetando os valores digitados nos campos
	 * (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * 
	 * 
	 * @param object
	 *            {@link Object}
	 */

	private void limparValoresSubmetidos(Object object) {
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		Iterator<UIComponent> uiComponent = ((UIComponent) object)
				.getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	//getters and setters
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghEquipes getEquipe() {
		return equipe;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	public VRapServidorConselho getConselho() {
		return conselho;
	}

	public void setConselho(VRapServidorConselho conselho) {
		this.conselho = conselho;
	}

	public FccCentroCustos getServico() {
		return servico;
	}

	public void setServico(FccCentroCustos servico) {
		this.servico = servico;
	}

	public Integer getNumeroGrade() {
		return numeroGrade;
	}

	public void setNumeroGrade(Integer numeroGrade) {
		this.numeroGrade = numeroGrade;
	}

	public Date getMesAno() {
		return mesAno;
	}

	public void setMesAno(Date mesAno) {
		this.mesAno = mesAno;
	}

	public StreamedContent getStreamedContent() {
		return streamedContent;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}
	
	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public List<RelatorioProgramacaoGradeVO> getListaRelatorioVO() {
		return listaRelatorioVO;
	}

	public void setListaRelatorioVO(
			List<RelatorioProgramacaoGradeVO> listaRelatorioVO) {
		this.listaRelatorioVO = listaRelatorioVO;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getSeqEquipe() {
		return seqEquipe;
	}

	public void setSeqEquipe(Integer seqEquipe) {
		this.seqEquipe = seqEquipe;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public List<RelatorioProgramacaoGradeCabecalhoVO> getListaCabecalhoVO() {
		return listaCabecalhoVO;
	}

	public void setListaCabecalhoVO(
			List<RelatorioProgramacaoGradeCabecalhoVO> listaCabecalhoVO) {
		this.listaCabecalhoVO = listaCabecalhoVO;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	public VRapServidorConselhoVO getConselhoVO() {
		return conselhoVO;
	}

	public void setConselhoVO(VRapServidorConselhoVO conselhoVO) {
		this.conselhoVO = conselhoVO;
	}

}
