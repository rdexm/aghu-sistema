package br.gov.mec.aghu.ambulatorio.action;

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
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.RelatoriosInterconsultasVO;
import br.gov.mec.aghu.dominio.DominioOrdenacaoInterconsultas;
import br.gov.mec.aghu.dominio.DominioSituacaoInterconsultasPesquisa;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatoriosInterconsultasController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1084444245806769479L;
	
	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";
	private static final String RELATORIOS_INTERCONSULTAS = "relatoriosInterconsultas";
	private static final String RELATORIOS_INTERCONSULTAS_PDF = "relatoriosInterconsultasPDF";
	private static final String CAMINHO_RELATORIO_INTERCONSULTAS = "br/gov/mec/aghu/ambulatorio/report/relatorioInterconsulta.jasper";
	private static final String CAMINHO_RELATORIO_PACIENTES_INTERCONSULTAS = "br/gov/mec/aghu/ambulatorio/report/relatorioPacienteInterconsultas.jasper";
	private static final Integer UM = 1;
	private static final Integer DOIS = 2;
	
	private static final Log LOG = LogFactory.getLog(RelatoriosInterconsultasController.class);
	
	private Log getLog() {
		return LOG;
	}	
	
	private enum RelatoriosInterconsultasExceptionCode implements BusinessExceptionCode {
		CAMPO_DATA_FINAL_MAIOR_QUE_DATA_INICIAL;
	}
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private String labelSetor;
	private Integer tipoRelatorio = UM;
	
	//Filtro
	private String ordenar = DominioOrdenacaoInterconsultas.D.toString();
	private String situacao = DominioSituacaoInterconsultasPesquisa.P.toString();
	
	private DominioSituacaoInterconsultasPesquisa situacaoFiltro;
	
	private Date dataInicial;
	private Date dataFinal;
	private AghEspecialidades agenda;
	
	private List<SelectItem> listaOrdenacao = new ArrayList<SelectItem>();
	private List<SelectItem> listaSituacao = new ArrayList<SelectItem>();
	private List<SelectItem> listaTipoRelatorio = new ArrayList<SelectItem>();
	
	private StreamedContent media;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		carregarParametros();
	}
	
	@Override
	protected Collection<RelatoriosInterconsultasVO> recuperarColecao() throws ApplicationBusinessException {
		
		if (this.situacao != null) {
			this.setSituacaoFiltro(this.obterSituacaoInterconsultasPesquisa(this.situacao));
		}else{
			this.setSituacaoFiltro(null);
		}
		if (this.dataFinal == null) {
			this.dataFinal = new Date();
		}
		if (this.tipoRelatorio.equals(UM)) {
			return ambulatorioFacade.carregarRelatorioInterconsultas(dataInicial, dataFinal, situacaoFiltro, ordenar, agenda);
		}
		else if (this.tipoRelatorio.equals(DOIS)) {
			return ambulatorioFacade.carregarRelatorioPacientesInterconsultas(dataInicial, dataFinal, agenda);
		}
		return null;
		
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		if (this.tipoRelatorio.equals(UM)) {
			return CAMINHO_RELATORIO_INTERCONSULTAS;
		}
		else if (this.tipoRelatorio.equals(DOIS)) {
			return CAMINHO_RELATORIO_PACIENTES_INTERCONSULTAS;
		}
		return null;
	}
	
	/**
	 * Pesquisas SuggestionBox e Pesquisas SuggestionBox Count..
	 * 
	 * @param parametro
	 * @return List<AghEspecialidades>
	 */
	public List<AghEspecialidades> pesquisarPorSiglaOuNomeEspecialidade(String parametro) {
		return this.returnSGWithCount(this.ambulatorioFacade.pesquisarPorSiglaOuNomeEspecialidade(parametro),this.ambulatorioFacade.pesquisarPorSiglaOuNomeEspecialidadeCount(parametro));
	}
	
	/**
	 * Retorna uma lista de Tipo Relatorio
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> obterListaTipoRelatorio() {
		
		listaTipoRelatorio.clear();
		
		SelectItem item1 = new SelectItem();
		item1.setLabel("Relatório das Interconsultas");
		item1.setValue(1);
		
		SelectItem item2 = new SelectItem();
		item2.setLabel("Pacientes com Interconsultas Pendentes");
		item2.setValue(2);
		
		listaTipoRelatorio.add(item1);				
		listaTipoRelatorio.add(item2);				
		
		return listaTipoRelatorio;
	}
	
	/**
	 * Retorna uma lista de Domínio Ordenação.
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> obterListaOrdenacao() {
		
		listaOrdenacao.clear();
		for (DominioOrdenacaoInterconsultas dominioOrdenacao :  DominioOrdenacaoInterconsultas.values()) {
			SelectItem item = new SelectItem();
			item.setLabel(dominioOrdenacao.getDescricao());
			item.setValue(dominioOrdenacao.toString());
			listaOrdenacao.add(item);
		}
		 
		SelectItem setor = new SelectItem();
		setor.setLabel(this.labelSetor);
		setor.setValue("S");
		listaOrdenacao.add(setor);
		
		return listaOrdenacao;
	}
	
	/**
	 * Retorna uma lista de Domínio Situação.
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> obterListaSituacao() {
		
		listaSituacao.clear();
		for (DominioSituacaoInterconsultasPesquisa dominioSituacao :  DominioSituacaoInterconsultasPesquisa.values()) {
			SelectItem item = new SelectItem();
			item.setLabel(dominioSituacao.getDescricao());
			item.setValue(dominioSituacao.toString());
			if (!dominioSituacao.equals(DominioSituacaoInterconsultasPesquisa.O)){
				listaSituacao.add(item);				
			}
		}
		
		return listaSituacao;
	}
	
	/**
	 * Carrega o parâmetro para setar o label Setor.
	 */
	private void carregarParametros(){
		try {
			this.labelSetor = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			if (this.labelSetor==null){
				this.labelSetor="Setor";
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Obtem Dominio Situacao Interconsultas Pesquisa 
	 * 
	 * @param codigo {@link String}
	 * @return {@link DominioSituacaoInterconsultasPesquisa}
	 */
	private DominioSituacaoInterconsultasPesquisa obterSituacaoInterconsultasPesquisa (String codigo) {
		switch (codigo) {
		case "P":
			return DominioSituacaoInterconsultasPesquisa.P;
		case "M":
			return DominioSituacaoInterconsultasPesquisa.M;
		case "A":
			return DominioSituacaoInterconsultasPesquisa.A;
		case "N":
			return DominioSituacaoInterconsultasPesquisa.N;
		default:
			return null;
		}
	}
	
	public String visualizarRelatorio() {
		
		try {
			validaRegras(dataInicial, dataFinal);
			DocumentoJasper documento = gerarDocumento();
			this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));
			return RELATORIOS_INTERCONSULTAS_PDF;
				
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException | IOException | DocumentException e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}
	
	public String imprimirRelatorio() {
		try {
			validaRegras(dataInicial, dataFinal);
			directPrint();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			return null;
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}
	
	private void validaRegras(Date dataInicial, Date dataFinal ) throws ApplicationBusinessException {
		
		if(dataInicial != null && dataFinal != null){
			if (DateUtil.validaDataMaior(dataInicial, dataFinal)) {
				throw new ApplicationBusinessException(RelatoriosInterconsultasExceptionCode.CAMPO_DATA_FINAL_MAIOR_QUE_DATA_INICIAL);
			}
		}	
	}
	
	
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		
		if (this.dataInicial != null) {
			params.put("dataInicial", this.dataInicial);
		}
		
		if (this.dataFinal != null) {
			params.put("dataFinal", this.dataFinal);
		}else{
			params.put("dataFinal", new Date());
		}
		
		params.put("logo", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images/logoClinicas2.jpg"));
		
		try {		
			params.put("nomeHospital", this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL).getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return params;
	}
	
	/**
	 * Limpar tela e campos
	 */
	public void limpar() {
		
		this.dataInicial = null;
		this.dataFinal = null;
		this.situacao = DominioSituacaoInterconsultasPesquisa.P.toString();
		this.ordenar = DominioOrdenacaoInterconsultas.D.toString();
		this.setSituacaoFiltro(null);
		this.tipoRelatorio = UM;
		this.agenda = null;
	}
	
	public String voltar() {
		return RELATORIOS_INTERCONSULTAS;
	}

	// Gets e Sets
	public Integer getTipoRelatorio() {
		return tipoRelatorio;
	}
	public void setTipoRelatorio(Integer tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}
	public String getLabelSetor() {
		return labelSetor;
	}
	public void setLabelSetor(String labelSetor) {
		this.labelSetor = labelSetor;
	}
	public String getOrdenar() {
		return ordenar;
	}
	public void setOrdenar(String ordenar) {
		this.ordenar = ordenar;
	}
	public List<SelectItem> getListaOrdenacao() {
		return listaOrdenacao;
	}
	public void setListaOrdenacao(List<SelectItem> listaOrdenacao) {
		this.listaOrdenacao = listaOrdenacao;
	}
	public List<SelectItem> getListaSituacao() {
		return listaSituacao;
	}
	public void setListaSituacao(List<SelectItem> listaSituacao) {
		this.listaSituacao = listaSituacao;
	}
	public AghEspecialidades getAgenda() {
		return agenda;
	}
	public void setAgenda(AghEspecialidades agenda) {
		this.agenda = agenda;
	}
	public StreamedContent getMedia() {
		return media;
	}
	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public DominioSituacaoInterconsultasPesquisa getSituacaoFiltro() {
		return situacaoFiltro;
	}
	public void setSituacaoFiltro(DominioSituacaoInterconsultasPesquisa situacaoFiltro) {
		this.situacaoFiltro = situacaoFiltro;
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
