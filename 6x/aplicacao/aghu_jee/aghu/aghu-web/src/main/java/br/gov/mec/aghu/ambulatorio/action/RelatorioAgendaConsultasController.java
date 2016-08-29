package br.gov.mec.aghu.ambulatorio.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
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
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.CabecalhoRelatorioAgendaConsultasVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioAgendaConsultasController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioAgendaConsultasController.class);
	
	private static final long serialVersionUID = 2632400007058708506L;
		
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private List<CabecalhoRelatorioAgendaConsultasVO> relatorioVO;
	private static final String URL_DOCUMENTO_JASPER = "br/gov/mec/aghu/ambulatorio/report/relatorioAgendaConsultas.jasper";
	private static final String URL_SUBREPORT_JASPER = "br/gov/mec/aghu/ambulatorio/report/";
	private static final String URL_TELA_PESQUISA = "relatorioAgendaConsultas";
	private static final String URL_TELA_PDF = "ambulatorio-visualizarRelatorioAgendaConsultas";

	private static final String PAGE_LISTAR_CONSULTAS_GRADE = "ambulatorio-listarConsultasPorGrade";
	
	private StreamedContent media;
	
	private String fileName;

	private Boolean gerouArquivo = Boolean.FALSE;
	
	// FILTRO
	private Integer seqGrade;
	private AghEspecialidades especialidade;
	private Short seqEspecialidade;
	private AghUnidadesFuncionais unidadeFuncional;
	private Short seqUnidadeFuncional;
	private DominioTurno turno;
	private Date dtInicio;
	private Date dtFim;	
	
	private String labelSetor;
	private String titleSetor;
	
	private boolean origemSumario;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		this.carregarParametros();
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<CabecalhoRelatorioAgendaConsultasVO> recuperarColecao() throws ApplicationBusinessException {
		return this.relatorioVO;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return URL_DOCUMENTO_JASPER;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {	
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		try {				
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			AghParametros parametroLabelSetor = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA);
			AghParametros parametroEncaminhamento = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_EXIBIR_RETORNOS_AGENDA_CONSULTAS);


			parametros.put("SUBREPORT_DIR", URL_SUBREPORT_JASPER);
			parametros.put("nomeRelatorio", DominioNomeRelatorio.AACR_AGENDA_DT_REF_TITLE.getDescricao());
			parametros.put("nomeHospital", parametroRazaoSocial.getVlrTexto());
			parametros.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
			parametros.put("labelSetor", parametroLabelSetor.getVlrTexto());
			parametros.put("mostraRetorno", parametroEncaminhamento.getVlrTexto());
			parametros.put("retornos", ambulatorioFacade.obterTodosRetornosRelatorioAgenda(9));
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return parametros;
	}
	
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {

		if (getEspecialidade() != null){
			seqEspecialidade = getEspecialidade().getSeq();
		}
		if (getUnidadeFuncional() != null){
			seqUnidadeFuncional = getUnidadeFuncional().getSeq();
		}
		
		try {
		this.relatorioVO = ambulatorioFacade.carregarRelatorioAgendaConsultas(dtInicio, dtFim, getSeqGrade(), seqEspecialidade, seqUnidadeFuncional, getTurno());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return URL_TELA_PESQUISA;
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
			return URL_TELA_PESQUISA;
		} catch (Exception e) {
			if (e instanceof EJBTransactionRolledbackException){
				apresentarMsgNegocio(Severity.INFO, "MSG_RESTRICAO_REGISTROS_MUITO_GRANDE");				
				return URL_TELA_PESQUISA;
			}
		}
		DocumentoJasper documento = gerarDocumento();
		this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
        
        return URL_TELA_PDF;
    }	

	public void directPrint() throws ApplicationBusinessException {
		try {
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
	
	public void gerarCSV(){
		if (getEspecialidade() != null){
			seqEspecialidade = getEspecialidade().getSeq();
		}
		if (getUnidadeFuncional() != null){
			seqUnidadeFuncional = getUnidadeFuncional().getSeq();
		}
		try {
			this.relatorioVO = this.ambulatorioFacade.carregarRelatorioAgendaConsultas(this.dtInicio, this.dtFim, this.getSeqGrade(), seqEspecialidade, seqUnidadeFuncional, this.getTurno());
			fileName = this.ambulatorioFacade.gerarCSVAgendaConsultas(this.relatorioVO);
			setGerouArquivo(Boolean.TRUE);
			dispararDownload();
		} catch (ApplicationBusinessException e) {
			LOG.error("Excecao capturada: ",e);
			setGerouArquivo(Boolean.FALSE);
			apresentarExcecaoNegocio(e);
		} catch (IOException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			setGerouArquivo(Boolean.FALSE);
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
			setGerouArquivo(Boolean.FALSE);
		}catch (Exception e) {
			if (e instanceof EJBTransactionRolledbackException){
				apresentarMsgNegocio(Severity.INFO, "MSG_RESTRICAO_REGISTROS_MUITO_GRANDE");				
				setGerouArquivo(Boolean.FALSE);
			}
		}
	}
	
	/**
	 * Dispara o download para o arquivo CSV do relatório.
	 */
	public void dispararDownload() {
		if (fileName != null) {
			try {
				this.download(
						fileName,
						DominioNomeRelatorio.AACR_AGENDA_DT_REF_TITLE
								.toString() + DominioNomeRelatorio.EXTENSAO_CSV,
						DominioMimeType.CSV.getContentType());
				download(fileName);
				setGerouArquivo(Boolean.FALSE);
				fileName = null;
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,
						e, e.getLocalizedMessage()));
			}
		}

	}

	private void carregarParametros(){
		try {
			this.labelSetor = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			if (this.labelSetor==null){
				this.labelSetor="Setor";
			}
			this.titleSetor = WebUtil.initLocalizedMessage("TITLE_ZONA_GRADE_AGENDAMENTO", null, this.labelSetor);
			this.dtInicio = null;
			this.dtFim = null;
			this.turno  = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Ação do botão Limpar
	 */
	public void limparPesquisa() {

		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.limparFiltro();
	}

	/**
	 * Percorre o formulário resetando os valores digitados nos campos
	 * (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * @param object {@link Object}
	 */
	private void limparValoresSubmetidos(Object object) {
		if (object == null || object instanceof UIComponent == false) {
			return;
		}

		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}

		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	public void limparFiltro(){
		this.seqGrade = null;
		this.turno = null;
		this.dtInicio = null;
		this.dtFim = null;
		this.relatorioVO = null;
		this.limparEspecialidade();
		this.limparSetor();
		setGerouArquivo(Boolean.FALSE);
	}
	
	public String voltar(){
		if (origemSumario){
			origemSumario = false;
			return PAGE_LISTAR_CONSULTAS_GRADE;
		}else {
			return URL_TELA_PESQUISA;
		}
	}
	
	/**
	 * Método para carregar sugestionBox de Especialidade
	 * @param parametro O que foi digitado no sugestionBox que será utilizado para consulta.
	 * @return Lista de especialidades de acordo com valor digitado
	 */
	public List<AghEspecialidades> obterEspecialidade(String parametro) {
		return this.aghuFacade.obterListaEspSiglaOuNomeOrdSigla((String) parametro);
	}
	/**
	 * Método para limpar sugestionBox de Especialidade
	 */
	public void limparEspecialidade(){
		this.especialidade = null;
		this.seqEspecialidade =  null;
	}
	
	/**
	 * Método para carregar sugestionBox de Setor
	 * @param pesquisa O que foi digitado no sugestionBox que será utilizado para consulta
	 * @return Lista de setores de acordo com valor digitado
	 */
	public List<AghUnidadesFuncionais> obterSetor(String pesquisa){
		return this.ambulatorioFacade.obterSetorPorSiglaDescricaoECaracteristica(pesquisa);
	}
	
	/**
	 * Método para limpar sugestionBox de Setor
	 */
	public void limparSetor(){
		this.unidadeFuncional = null;
		this.seqUnidadeFuncional = null;
	}
	
	//Getters and Setters
	public List<CabecalhoRelatorioAgendaConsultasVO> getRelatorioVO() {
		return relatorioVO;
	}

	public void setRelatorioVO(List<CabecalhoRelatorioAgendaConsultasVO> relatorioVO) {
		this.relatorioVO = relatorioVO;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public Integer getSeqGrade() {
		return seqGrade;
	}

	public void setSeqGrade(Integer seqGrade) {
		this.seqGrade = seqGrade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public DominioTurno getTurno() {
		return turno;
	}

	public void setTurno(DominioTurno turno) {
		this.turno = turno;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public String getLabelSetor() {
		return labelSetor;
	}

	public void setLabelSetor(String labelSetor) {
		this.labelSetor = labelSetor;
	}

	public String getTitleSetor() {
		return titleSetor;
	}

	public void setTitleSetor(String titleSetor) {
		this.titleSetor = titleSetor;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	public boolean isOrigemSumario() {
		return origemSumario;
	}

	public void setOrigemSumario(boolean origemSumario) {
		this.origemSumario = origemSumario;
	}
	
}
