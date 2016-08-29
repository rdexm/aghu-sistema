package br.gov.mec.aghu.transplante.action;

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
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioOrdenacRelatorioSitPacOrgao;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.RelatorioTransplanteOrgaosSituacaoVO;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

public class RelatorioTransplanteOrgaosSituacaoController extends ActionReport {

	private static final long serialVersionUID = -8822297486606862956L;
	
	private static final String PAGE_RELATORIO_TRANSPLANTE_ORGAOS_SITUACAO = "transplante-relatorioTransplanteOrgaosSituacao";
	private static final String PAGE_RELATORIO_TRANSPLANTE_ORGAOS_SITUACAO_PDF = "transplante-relatorioTransplanteOrgaosSituacaoPdf";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	private static final String NOME_RELATORIO = "Relatório_de_Pacientes_de_Transplante_de_Órgãos_por_Situação";
	private static final Log LOG = LogFactory.getLog(RelatorioTransplanteOrgaosSituacaoController.class);
	
	@Inject
	private PesquisaPacienteController pesquisaPacienteController;
	private String fileName;
	private DominioTipoOrgao dominioTipoOrgao;
	private AipPacientes aipPacientes;
	private DominioSexo dominioSexo;
	private DominioOrdenacRelatorioSitPacOrgao ordenacao;
	private List<DominioSexo> listaDominioSexo;
	private List<DominioSituacaoTransplante> listaDominioSituacaoTransplanteSelecionados;
	private List<DominioSituacaoTransplante> listaDominioSituacaoTransplante;
	private List<RelatorioTransplanteOrgaosSituacaoVO> listaRelatorioTransplanteOrgaosSituacaoVO;
	private final String tipoParametro = "codigo";
	private Date dataInicial;
	private Date dataFinal;
	private StreamedContent media;
	private AghParametros hospital;
	@EJB
	private ITransplanteFacade transplanteFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	@EJB
	private IParametroFacade parametroFacade;

	@PostConstruct
	public void inicializar(){
		begin(conversation,true);
	}
	
	public void iniciar(){
		carregarListaSexo();
		carregarListaSituacao();
		carregarDadosPaciente();
		carregarNomeHospital();
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		aipPacientes = buscarPaciente(event.getNewValue(), event.getComponent().getId());
	}
	
	public void pesquisarDadosRelatorio() throws ApplicationBusinessException{
		transplanteFacade.validarData(dataInicial, dataFinal);
		Integer prontuario = aipPacientes != null ? aipPacientes.getProntuario() : null;
		List<DominioSituacaoTransplante> lista = listaDominioSituacaoTransplanteSelecionados;
		if(listaDominioSituacaoTransplanteSelecionados == null || listaDominioSituacaoTransplanteSelecionados.isEmpty()){
			lista = listaDominioSituacaoTransplante;
		}
    	listaRelatorioTransplanteOrgaosSituacaoVO = transplanteFacade.pesquisarTransplante(dominioTipoOrgao, prontuario, 
    			dataInicial, dataFinal, lista, ordenacao, dominioSexo);
    	transplanteFacade.validarListaRelatorioTransplanteOrgaosSituacaoVO(listaRelatorioTransplanteOrgaosSituacaoVO);
	}
	
    public String visualizarImpressao(){
    	try {
			pesquisarDadosRelatorio();
			return PAGE_RELATORIO_TRANSPLANTE_ORGAOS_SITUACAO_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
    	return null;
    }
    
    public void carregarNomeHospital(){
    	try {
			hospital = parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
    }
    
    public void carregarDadosPaciente(){
    	if(pesquisaPacienteController != null && PAGE_RELATORIO_TRANSPLANTE_ORGAOS_SITUACAO.equals(pesquisaPacienteController.getCameFrom())){
    		aipPacientes = buscarPaciente(pesquisaPacienteController.getCodigoPaciente(), tipoParametro);
    	}
    	pesquisaPacienteController.setCameFrom(null);
    }
    
    public AipPacientes buscarPaciente(Object valor, String tipoParametro){
    	try {
			return pacienteFacade.pesquisarPacienteComponente(valor, tipoParametro);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
    }
    
    public void gerarCSV(){
    	try {
			pesquisarDadosRelatorio();
			fileName = transplanteFacade.gerarCSVRelatorioTransplanteOrgaosSituacao(hospital.getVlrTexto(), listaRelatorioTransplanteOrgaosSituacaoVO); 
			dispararDownload();
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
    }
    
	public String redirecionarPesquisaFonetica() {
		return PESQUISA_FONETICA;
	}

	public void processarBuscaPacientePorCodigo(Integer codigoPaciente){
        if(codigoPaciente != null){
        	aipPacientes = pacienteFacade.buscaPaciente(codigoPaciente);
        }else{
        	aipPacientes = null;
        }
	}
	
	public void carregarListaSexo(){
		listaDominioSexo = new ArrayList<DominioSexo>();
		listaDominioSexo.add(DominioSexo.M);
		listaDominioSexo.add(DominioSexo.F);
	}
	
	public void carregarListaSituacao(){
		listaDominioSituacaoTransplante = new ArrayList<DominioSituacaoTransplante>();
		listaDominioSituacaoTransplante.add(DominioSituacaoTransplante.E);
		listaDominioSituacaoTransplante.add(DominioSituacaoTransplante.T);
		listaDominioSituacaoTransplante.add(DominioSituacaoTransplante.I);
		listaDominioSituacaoTransplante.add(DominioSituacaoTransplante.R);
	}
	
	@Override
	protected Collection<?> recuperarColecao() {
		return listaRelatorioTransplanteOrgaosSituacaoVO;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/transplante/report/relatorioTransplanteOrgaosSituacao.jasper";	
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nomeInstituicao", hospital.getValor());
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		return params;
	}
	
	public void limpar() {
		dominioSexo = null;
		dominioTipoOrgao = null;
		aipPacientes = null;
		dataFinal = null;
		dataInicial = null;
		listaDominioSituacaoTransplanteSelecionados.clear();
		ordenacao = null;
	}
	
	/**
	 * Dispara o download para o arquivo CSV do relatório.
	 **/

	public void dispararDownload() {
		if (fileName != null) {
			try {
				download(fileName, NOME_RELATORIO + DominioNomeRelatorio.EXTENSAO_CSV, DominioMimeType.CSV.getContentType());
				fileName = null;
			} catch (IOException e) {
				fileName = null;
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,e, e.getLocalizedMessage()));
			}
		} else {
			apresentarMsgNegocio(Severity.ERROR,AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV.toString());
		}

	}
	
	public String print(){
		try {
			DocumentoJasper documento = gerarDocumento();
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));
			return PAGE_RELATORIO_TRANSPLANTE_ORGAOS_SITUACAO_PDF;
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}catch(Exception e){
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}
	
	//Gerar pdf bloqueado para impressao
	public StreamedContent getRenderPdf(){
		try {
			DocumentoJasper documento = gerarDocumento();
			media =  criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
			return media;
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}catch(Exception e){
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}

	
	public void directPrint() {
		try {
			pesquisarDadosRelatorio();
			DocumentoJasper documento = gerarDocumento();
			sistemaImpressao.imprimir(documento.getJasperPrint(),
			super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		}catch (ApplicationBusinessException e1) {
			apresentarExcecaoNegocio(e1);
		}catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO"); 
		}
	}
	
	public String voltar() {
		
		return PAGE_RELATORIO_TRANSPLANTE_ORGAOS_SITUACAO;
	}
	
	public DominioTipoOrgao getDominioTipoOrgao() {
		return dominioTipoOrgao;
	}

	public void setDominioTipoOrgao(DominioTipoOrgao dominioTipoOrgao) {
		this.dominioTipoOrgao = dominioTipoOrgao;
	}

	public AipPacientes getAipPacientes() {
		return aipPacientes;
	}

	public void setAipPacientes(AipPacientes aipPacientes) {
		this.aipPacientes = aipPacientes;
	}

	public DominioSexo getDominioSexo() {
		return dominioSexo;
	}

	public void setDominioSexo(DominioSexo dominioSexo) {
		this.dominioSexo = dominioSexo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
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

	public List<DominioSexo> getListaDominioSexo() {
		return listaDominioSexo;
	}

	public void setListaDominioSexo(List<DominioSexo> listaDominioSexo) {
		this.listaDominioSexo = listaDominioSexo;
	}

	public List<DominioSituacaoTransplante> getListaDominioSituacaoTransplanteSelecionados() {
		return listaDominioSituacaoTransplanteSelecionados;
	}

	public void setListaDominioSituacaoTransplanteSelecionados(List<DominioSituacaoTransplante> listaDominioSituacaoTransplanteSelecionados) {
		this.listaDominioSituacaoTransplanteSelecionados = listaDominioSituacaoTransplanteSelecionados;
	}

	public List<DominioSituacaoTransplante> getListaDominioSituacaoTransplante() {
		return listaDominioSituacaoTransplante;
	}

	public void setListaDominioSituacaoTransplante(List<DominioSituacaoTransplante> listaDominioSituacaoTransplante) {
		this.listaDominioSituacaoTransplante = listaDominioSituacaoTransplante;
	}

	public DominioOrdenacRelatorioSitPacOrgao getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(DominioOrdenacRelatorioSitPacOrgao ordenacao) {
		this.ordenacao = ordenacao;
	}
}
