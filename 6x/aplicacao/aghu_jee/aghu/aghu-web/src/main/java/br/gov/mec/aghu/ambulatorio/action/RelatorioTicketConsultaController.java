package br.gov.mec.aghu.ambulatorio.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.Normalizer;
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

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAgendamentoConsultaVO;
import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.dominio.DominioTipoCups;
import br.gov.mec.aghu.dominio.DominioTipoRelatorio;
import br.gov.mec.aghu.impressao.ISistemaImpressaoCUPS;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioTicketConsultaController extends ActionReport {


	

	private static final Log LOG = LogFactory.getLog(RelatorioTicketConsultaController.class);

	
	private static final String RELATORIO_TICKET_CONSULTA     = "ambulatorio-relatorio-relatorioTicketConsulta";
	private static final String RELATORIO_TICKET_CONSULTA_PDF = "ambulatorio-relatorio-relatorioTicketConsultaPdf";

	public enum EnumRelatorioTicketConsultaControllerExceptionCode {
		LABEL_CAMPO_NUMERO_UNIDADE,
		MESSAGE_CAMPO_NUMERO_UNIDADE_OBRIGATORIO,
		MESSAGE_CAMPO_TIPO_RELATORIO_OBRIGATORIO,
		MESSAGE_CAMPO_NUMERO_CONSULTA_OBRIGATORIO,
		LABEL_PESQUISA_SEM_RESULTADOS;
	}
	// --tela
	private Integer numeroConsulta;
	private DominioTipoRelatorio tipoRelatorio;
	private Short unidadeNumero;
	//tela
	//fachadas
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@Inject
	private ISistemaImpressaoCUPS sistemaImpressao;
	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	//fachadas
	
	//relatorio
		private RelatorioAgendamentoConsultaVO itemColecao;
		private String labelZona;
		private String labelSala;
	//fim-relatorio
	

	private AacConsultas aacConsultas;
	private List<RelatorioAgendamentoConsultaVO> dados; 
	
	private StreamedContent media;
	
	//Controla Exibição do botão visualizarImpressão
	private boolean preImpressao;
	
	
	@Inject
	private SistemaImpressao sistemaimpre;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioAgendamentoConsulta.jasper";
	}
	
	@Override
	public List<RelatorioAgendamentoConsultaVO> recuperarColecao() throws ApplicationBusinessException {
		
		List<RelatorioAgendamentoConsultaVO> lista = new ArrayList<RelatorioAgendamentoConsultaVO>();
		lista.add(itemColecao);
		return lista;
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		obterParametros();
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		params.put("dataAtual", sdf.format(dataAtual));
		params.put("labelZona", this.labelZona + ":");
		params.put("labelSala", this.labelSala + ":");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoLocal();
		params.put("hospitalLocal", hospital);
		if (isGradeUBS()){
			String tituloUbs = null;
			try{
				tituloUbs = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_TITULO_UBS);
			}catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
			if (StringUtils.isNotBlank(tituloUbs)){
				params.put("hospitalLocal", tituloUbs);
			}
		}
		return params;
	}
	

	
	
	public void  pesquisa(){

			aacConsultas = ambulatorioFacade.obterAgendamentoConsultaPorFiltros(numeroConsulta,unidadeNumero); 
	}
	
	/**
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JRException 
	 * @throws BaseException 
	 */
	public String prepararImpressao() throws JRException, IOException, DocumentException, BaseException {
		String mensagem = null;
			if(aacConsultas!=null){
				itemColecao = ambulatorioFacade.popularTicketAgendamentoConsulta(aacConsultas);
				dados = recuperarColecao();
			}
			else{
				dados = null;
			}
			if (dados != null && !dados.isEmpty()) {
				DocumentoJasper documento = gerarDocumento();
				media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(true)));
				return RELATORIO_TICKET_CONSULTA_PDF;
			} else{
				mensagem = super.getBundle().getString(EnumRelatorioTicketConsultaControllerExceptionCode.LABEL_PESQUISA_SEM_RESULTADOS.toString());
				apresentarMsgNegocio(Severity.WARN, mensagem,new Object[0]);
				return RELATORIO_TICKET_CONSULTA;
			}
	}
	
	/**
	 * Renderiza o PDF.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, 
																JRException, SystemException,	DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
	}
	
	public String visualizaImpressao() throws JRException, IOException, DocumentException, BaseException{
		if(validarCampos()){
			pesquisa();
			prepararImpressao();
			if(aacConsultas!=null){
				return RELATORIO_TICKET_CONSULTA_PDF;
			}
		}
		return RELATORIO_TICKET_CONSULTA;
	}
	public void enviaImpressaoImpressoraSelecionada() throws JRException, IOException, DocumentException, BaseException{
		
		if(validarCampos()){
			pesquisa();
			prepararImpressao();
			if(aacConsultas!=null){
				directPrint();
				return;
			}
		}
			
	}
	
	public boolean validarCampos(){
		boolean realizarConsulta=true;
		
		if(tipoRelatorio==null){
			apresentarMsgNegocio(
					Severity.ERROR,
					EnumRelatorioTicketConsultaControllerExceptionCode.MESSAGE_CAMPO_TIPO_RELATORIO_OBRIGATORIO
							.toString());
			realizarConsulta = false;
		}
		
		if(numeroConsulta==null){
			apresentarMsgNegocio(
					Severity.ERROR,
					EnumRelatorioTicketConsultaControllerExceptionCode.MESSAGE_CAMPO_NUMERO_CONSULTA_OBRIGATORIO
							.toString());
			realizarConsulta = false;
		}
		if(tipoRelatorio!=null && tipoRelatorio.equals(DominioTipoRelatorio.F)){
			unidadeNumero=null;
		}
		
		return realizarConsulta;
		
	}

	
	private boolean isGradeUBS() {
		List<AacGradeAgendamenConsultas> listaGrade = ambulatorioFacade.listarGradeUnidFuncionalECaracteristicas(numeroConsulta);
		AacGradeAgendamenConsultas grade = listaGrade.get(0);
		
		return grade.getUnidadeFuncional().possuiCaracteristica(ConstanteAghCaractUnidFuncionais.UBS);
	}
	
	/**
	 * Metodo responsavel por imprimir via CUPS.
	 * Qualquer alteração neste método, deverá ser alterado também na classe ListarConsultasPorGradeController
	 */
	public void directPrint()  {
		
			try {
				// prepara dados para geração do relatório.
								
//				String remoteAddress = String.valueOf(super.getEnderecoIPv4HostRemoto()); //25304
				InetAddress remoteHost = super.getEnderecoIPv4HostRemoto();
				String remoteAddress = remoteHost.getHostAddress();
				
				ImpComputador computador = cadastrosBasicosCupsFacade.obterComputador(remoteAddress);
				ImpComputadorImpressora impressora = null;
				if (computador != null) {
					impressora = cadastrosBasicosCupsFacade.obterImpressora(computador.getSeq(), DominioTipoCups.RAW);
				}
				
				if (tipoRelatorio.equals(DominioTipoRelatorio.F) && impressora!=null && impressora.getImpImpressora()!= null && impressora.getImpImpressora().getId() != null) {
					String textoMatricial = obterTextoAgendamentoConsulta();
					textoMatricial = Normalizer.normalize(textoMatricial, Normalizer.Form.NFD);  
					textoMatricial = textoMatricial.replaceAll("[^\\p{ASCII}]", "");
					//Necessário adicionar algumas linhas no final para ficar no ponto de corte correto da impressora.
					textoMatricial = textoMatricial.concat("\n\n\n\n\n\n");
					try {
						this.sistemaImpressao.imprimir(textoMatricial, super.getEnderecoIPv4HostRemoto());
			
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_TICKET");
			
					} catch (Exception e) {
						LOG.error("Exceção capturada: ", e);
						apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
					}
					
				} else {
					
					DocumentoJasper documento = gerarDocumento();
					getSistemaimpre().imprimir(documento.getJasperPrint(),
							super.getEnderecoIPv4HostRemoto());
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_TICKET");
				}
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				
			} catch (Exception e) {
				apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
			}	
	}
	
	public String voltar(){
		return RELATORIO_TICKET_CONSULTA;
	}
	public String obterTextoAgendamentoConsulta() throws ApplicationBusinessException {
		String hospitalLocal = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoLocal();
		obterParametros();
		return ambulatorioFacade.obterTextoAgendamentoConsulta(hospitalLocal, this.labelZona, this.labelSala, aacConsultas,true);
	}

	
	/**
	 * Obtem os parametros zona e sala que serao exibidos no relatorio. 
	 */
	private void obterParametros() {
		try {
			this.labelZona = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LABEL_ZONA);
			this.labelSala = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LABEL_SALA);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}		
	}
	
	public boolean imprimeNaoFiscal(){
		if(tipoRelatorio!=null && tipoRelatorio.equals(DominioTipoRelatorio.N)){
			preImpressao=true;
			return true;
		}
		if(tipoRelatorio!=null && tipoRelatorio.equals(DominioTipoRelatorio.F)){
			preImpressao=false;
			return false;
		}
		
		preImpressao=true;
		return false;
	}

	public Short getUnidadeNumero() {
		return unidadeNumero;
	}

	public void setUnidadeNumero(Short unidadeNumero) {
		this.unidadeNumero = unidadeNumero;
	}
	
	public void zerarNumeroUnidade(){
		if(tipoRelatorio==null || (tipoRelatorio!=null && tipoRelatorio.equals(DominioTipoRelatorio.F))){
			this.unidadeNumero=null;
		}
	}

	public DominioTipoRelatorio getTipoRelatorio() {
		return tipoRelatorio;
	}


	public void setTipoRelatorio(DominioTipoRelatorio tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}


	public RelatorioAgendamentoConsultaVO getItemColecao() {
		return itemColecao;
	}


	public void setItemColecao(RelatorioAgendamentoConsultaVO itemColecao) {
		this.itemColecao = itemColecao;
	}


	public AacConsultas getAacConsultas() {
		return aacConsultas;
	}


	public void setAacConsultas(AacConsultas aacConsultas) {
		this.aacConsultas = aacConsultas;
	}


	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}


	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public List<RelatorioAgendamentoConsultaVO> getDados() {
		return dados;
	}

	public void setDados(List<RelatorioAgendamentoConsultaVO> dados) {
		this.dados = dados;
	}

	public SistemaImpressao getSistemaimpre() {
		return sistemaimpre;
	}

	public void setSistemaimpre(SistemaImpressao sistemaimpre) {
		this.sistemaimpre = sistemaimpre;
	}

	public boolean isPreImpressao() {
		return preImpressao;
	}

	public void setPreImpressao(boolean preImpressao) {
		this.preImpressao = preImpressao;
	}
}
