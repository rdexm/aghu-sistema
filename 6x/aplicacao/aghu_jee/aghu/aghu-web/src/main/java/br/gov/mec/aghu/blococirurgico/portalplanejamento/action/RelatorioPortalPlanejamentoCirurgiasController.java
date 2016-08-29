package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RelatorioPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioPortalPlanejamentoCirurgiasController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioPortalPlanejamentoCirurgiasController.class);

	private static final long serialVersionUID = 1393686588124506641L;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	
	private Short pUnfSeq;
	private Short pEspSeq;
	private String pEquipe;
	private Date pDtIni;
	private Date pDtFim;
	private Integer pPucSerMatricula;
	private Short pPucSerVinCodigo;
	private Short pPucUnfSeq;
	private String pPucIndFuncaoProf;
	private String voltarPara;
	
	private final String VISUALIZAR_AGENDAMENTO = "relatorioPortalPlanejamentoCirurgiasPdf";
	
	private Boolean apresentaMsgModal = Boolean.FALSE;
	private String msgModal;	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/portalplanejamento/report/relatorioPlanejamentoDeCirurgias.jasper";
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioPortalPlanejamentoCirurgiasVO> recuperarColecao(){
		try {
			return blocoCirurgicoPortalPlanejamentoFacade
					.listarEquipePlanejamentoCirurgias(pDtIni, pDtFim,
							pPucSerMatricula, pPucSerVinCodigo, pPucUnfSeq,
							DominioFuncaoProfissional.getInstance(pPucIndFuncaoProf), pEspSeq, pUnfSeq, pEquipe);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public Boolean relatorioPossuiRegistros(){
		try {
			return blocoCirurgicoPortalPlanejamentoFacade
					.listarEquipePlanejamentoCirurgiasPossuiRegistro(pDtIni, pDtFim,
							pPucSerMatricula, pPucSerVinCodigo, pPucUnfSeq,
							DominioFuncaoProfissional.getInstance(pPucIndFuncaoProf), pEspSeq, pUnfSeq, pEquipe);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));			
	}
	
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
		try {
			params.put("dataAtual", sdf.format(dataAtual));
			params.put("caminhoLogo", recuperarCaminhoLogo2());
			
			
			String hospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL).getVlrTexto();
			params.put("hospitalLocal", hospital);
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}		
		return params;
	}
	
	public String imprimirRelatorioAgenda() throws ApplicationBusinessException{	
		
		detalhamentoPortalAgendamentoController.buscarDetalhamento();		
				
		if(validarDadosRelatorioPortalPlanej()){	
			try {
				directPrint();					
			} catch (Exception e) {
				apresentarExcecaoNegocio((BaseException) e);
			}
		}
		
		
		return null;
	}	

	public String visualizarRelatorioAgenda() throws ApplicationBusinessException{	

		if(validarDadosRelatorioPortalPlanej()){
			return VISUALIZAR_AGENDAMENTO;
		}
		return null;
	}

	private Boolean validarDadosRelatorioPortalPlanej() {
		this.msgModal = null;
		if(apresentarMsgModalDatasObrigatorias()){
			return Boolean.FALSE;
		}else{
			if(apresentarMsgModalDataFimMenor()){
				return Boolean.FALSE;
			}else{
				if (apresentarMsgModalAusenciaDadosRelatorio()){	
					return Boolean.FALSE;
				}
			}
		}
		return Boolean.TRUE;
	}
	
	public String voltar() {
		return voltarPara;
	}
	
	private Boolean apresentarMsgModalDatasObrigatorias() {
		Boolean erro = Boolean.FALSE;
		if(pDtIni == null){
			apresentarMsgNegocio("dtInicio", Severity.ERROR, "CAMPO_OBRIGATORIO", "Início");
			apresentaMsgModal = Boolean.TRUE;
			erro = Boolean.TRUE;
		}
		if(pDtFim == null){
			apresentarMsgNegocio("dtFim", Severity.ERROR, "CAMPO_OBRIGATORIO", "Fim");
			apresentaMsgModal = Boolean.TRUE;
			erro = Boolean.TRUE;
		}
		return erro;
	}	
	
	private Boolean apresentarMsgModalDataFimMenor() {				
		if (pDtIni.after(pDtFim)){
			this.msgModal =  getBundle().getString("MENSAGEM_DETALHAMENTO_AGENDA_DATA_INICIAL_MAIOR_QUE_FINAL");
			apresentaMsgModal = Boolean.TRUE;
		}else{
			apresentaMsgModal = Boolean.FALSE;
		}
		return apresentaMsgModal;
	}
	
	private boolean apresentarMsgModalAusenciaDadosRelatorio() {
		try {
			if (!blocoCirurgicoPortalPlanejamentoFacade
					.listarEquipePlanejamentoCirurgiasPossuiRegistro(pDtIni,
							pDtFim, pPucSerMatricula, pPucSerVinCodigo,
							pPucUnfSeq, 
							DominioFuncaoProfissional.getInstance(pPucIndFuncaoProf), pEspSeq,
							pUnfSeq, pEquipe)) {

				this.msgModal =  getBundle().getString("NAO_EXISTE_ESCALA_DE_SALA_PARA_PERIODO");
				apresentaMsgModal = Boolean.TRUE;
			}else{
				apresentaMsgModal = Boolean.FALSE;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}		
		return apresentaMsgModal;
	}
	
	
	//Getters and Setters
	public Short getpUnfSeq() {
		return pUnfSeq;
	}

	public void setpUnfSeq(Short pUnfSeq) {
		this.pUnfSeq = pUnfSeq;
	}

	public Short getpEspSeq() {
		return pEspSeq;
	}

	public void setpEspSeq(Short pEspSeq) {
		this.pEspSeq = pEspSeq;
	}

	public String getpEquipe() {
		return pEquipe;
	}

	public void setpEquipe(String pEquipe) {
		this.pEquipe = pEquipe;
	}

	public Integer getpPucSerMatricula() {
		return pPucSerMatricula;
	}

	public void setpPucSerMatricula(Integer pPucSerMatricula) {
		this.pPucSerMatricula = pPucSerMatricula;
	}

	public Short getpPucSerVinCodigo() {
		return pPucSerVinCodigo;
	}

	public void setpPucSerVinCodigo(Short pPucSerVinCodigo) {
		this.pPucSerVinCodigo = pPucSerVinCodigo;
	}

	public Short getpPucUnfSeq() {
		return pPucUnfSeq;
	}

	public void setpPucUnfSeq(Short pPucUnfSeq) {
		this.pPucUnfSeq = pPucUnfSeq;
	}

	public String getpPucIndFuncaoProf() {
		return pPucIndFuncaoProf;
	}

	public void setpPucIndFuncaoProf(String pPucIndFuncaoProf) {
		this.pPucIndFuncaoProf = pPucIndFuncaoProf;
	}

	public Date getpDtIni() {
		return pDtIni;
	}

	public Date getpDtFim() {
		return pDtFim;
	}

	public void setpDtIni(Date pDtIni) {
		this.pDtIni = pDtIni;
	}

	public void setpDtFim(Date pDtFim) {
		this.pDtFim = pDtFim;
	}

	public Boolean getApresentaMsgModal() {
		return apresentaMsgModal;
	}

	public String getMsgModal() {
		return msgModal;
	}

	public void setApresentaMsgModal(Boolean apresentaMsgModal) {
		this.apresentaMsgModal = apresentaMsgModal;
	}

	public void setMsgModal(String msgModal) {
		this.msgModal = msgModal;
	}

	public void setarDatasInicioEFim(Date dataInicio) {
		pDtIni= dataInicio;
		pDtFim = dataInicio;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
}