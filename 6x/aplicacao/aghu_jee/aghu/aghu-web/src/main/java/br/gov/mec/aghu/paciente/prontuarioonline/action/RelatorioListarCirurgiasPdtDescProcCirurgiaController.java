package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghDocumentoCertificado;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AvaliacaoPreSedacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PdtDescricaoProcedimentoCirurgiaVO;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioListarCirurgiasPdtDescProcCirurgiaController extends	ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(RelatorioListarCirurgiasPdtDescProcCirurgiaController.class);
	private static final long serialVersionUID = -6265776185617240893L;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject
	private SecurityController securityController;	

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IParametroFacade parametroFacade;	

	private List<PdtDescricaoProcedimentoCirurgiaVO> colecao = new ArrayList<PdtDescricaoProcedimentoCirurgiaVO>(0);
	private Collection<PdtDescricaoProcedimentoCirurgiaVO> descricaoPDT;

	private Integer crgSeq;
	private String voltarPara;
	private Integer ddtSeq;
	private MbcCirurgias cirurgia;
	private Boolean previa;
	
	private Boolean permiteImprimirDescricaoProcedimentosPOL;
	
	protected AghDocumentoCertificado documentoCertificado;
	
	protected Object entidadePai;
	
	private boolean chamadoPorRegistroCirurgiaRealizada = false; 
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	private AvaliacaoPreSedacaoVO avaliacaoPreSedacaoVO;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		permiteImprimirDescricaoProcedimentosPOL = securityController.usuarioTemPermissao("permiteImprimirDescricaoProcedimentosPOL", "imprimir");
	}

	
	public void inicio(){
		try {
			this.carregarParametrosRequest();
			definirVoltarPara();
			descricaoPDT = recuperarColecao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}
	
	private void definirVoltarPara() {
		if(StringUtils.isBlank(voltarPara)){
			voltarPara = "blococirurgico-listarCirurgias";//default
		}
		if("registroCirurgiaRealizada".equals(voltarPara)) {
			setChamadoPorRegistroCirurgiaRealizada(true);
		}else {
			setChamadoPorRegistroCirurgiaRealizada(false); 
		}
	}
	
	private void carregarParametrosRequest() {
		String crgSeq = this.getRequestParameter("crgSeq");
		if(StringUtils.isNotBlank(crgSeq)) {
			this.setCrgSeq(Integer.valueOf(crgSeq));
		}
		String voltarPara = this.getRequestParameter("voltarPara");
		if(StringUtils.isNotBlank(voltarPara)) {
			this.setVoltarPara(voltarPara);
		}
	}
	
	@Override
	protected MbcCirurgias getEntidadePai() {
		if(this.crgSeq == null){
			return null;
		}
		return  blocoCirurgicoFacade.obterCirurgiaPorSeq(crgSeq);
	}
	
	public void gerarPendenciaCertificacao() throws BaseException {	
		this.gerarDocumento(DominioTipoDocumento.PDT);	
	}
	

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() {

		try {
		
			Boolean certificacaoDigital = false;
			String valorParametroCertificacaoDigital = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL).getVlrTexto(); 
			
			if (valorParametroCertificacaoDigital != null){
				certificacaoDigital =  DominioSimNao.valueOf(valorParametroCertificacaoDigital).isSim();
			}		
			colecao = this.prontuarioOnlineFacade.pesquisarRelatorioPdtDescricaoProcedimentoCirurgiaPorCrgSeq(this.crgSeq);
			if(avaliacaoPreSedacaoVO != null){
				
				for(PdtDescricaoProcedimentoCirurgiaVO c : colecao){
					c.setAsa(avaliacaoPreSedacaoVO.getAsa());
					c.setAvaliacaoClinica(avaliacaoPreSedacaoVO.getAvaliacaoClinica());
					c.setComorbidades(avaliacaoPreSedacaoVO.getComorbidades());
					c.setDescViaAereas(avaliacaoPreSedacaoVO.getDescViaAereas());
					c.setExameFisico(avaliacaoPreSedacaoVO.getExameFisico());
					c.setNome(avaliacaoPreSedacaoVO.getNome());
					c.setNroRegConselho(avaliacaoPreSedacaoVO.getNroRegConselho());
				}
			}		
			DocumentoJasper documento = gerarDocumento(!certificacaoDigital);

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),	super.getEnderecoIPv4HostRemoto());

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
	public Collection<PdtDescricaoProcedimentoCirurgiaVO> recuperarColecao() throws ApplicationBusinessException {
		if (this.ddtSeq!=null){
			colecao = this.prontuarioOnlineFacade.recuperarPdtDescricaoProcedimentoCirurgiaVO(this.ddtSeq);
			if(avaliacaoPreSedacaoVO != null){
				
				for(PdtDescricaoProcedimentoCirurgiaVO c : colecao){
					c.setAsa(avaliacaoPreSedacaoVO.getAsa());
					c.setAvaliacaoClinica(avaliacaoPreSedacaoVO.getAvaliacaoClinica());
					c.setComorbidades(avaliacaoPreSedacaoVO.getComorbidades());
					c.setDescViaAereas(avaliacaoPreSedacaoVO.getDescViaAereas());
					c.setExameFisico(avaliacaoPreSedacaoVO.getExameFisico());
					c.setNome(avaliacaoPreSedacaoVO.getNome());
					c.setNroRegConselho(avaliacaoPreSedacaoVO.getNroRegConselho());
				}
			}
			return colecao;
		
		}
		 colecao = this.prontuarioOnlineFacade.pesquisarRelatorioPdtDescricaoProcedimentoCirurgiaPorCrgSeq(this.crgSeq);
	
		if(avaliacaoPreSedacaoVO != null){
			
			for(PdtDescricaoProcedimentoCirurgiaVO c : colecao){
				c.setAsa(avaliacaoPreSedacaoVO.getAsa());
				c.setAvaliacaoClinica(avaliacaoPreSedacaoVO.getAvaliacaoClinica());
				c.setComorbidades(avaliacaoPreSedacaoVO.getComorbidades());
				c.setDescViaAereas(avaliacaoPreSedacaoVO.getDescViaAereas());
				c.setExameFisico(avaliacaoPreSedacaoVO.getExameFisico());
				c.setNome(avaliacaoPreSedacaoVO.getNome());
				c.setNroRegConselho(avaliacaoPreSedacaoVO.getNroRegConselho());
			}
		}
		return colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/relatorioPdtDescProcCirurgia.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		
			Map<String, Object> params = new HashMap<String, Object>();
			
			try {
				params.put("caminhoLogo", recuperarCaminhoLogo());
				params.put("previa", Boolean.TRUE.equals(previa));
			} catch (BaseException e) {
				LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
			}
			params.put("SUBREPORT_DIR", "br/gov/mec/aghu/paciente/prontuarioonline/report/");
					
			return params;		
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * @throws ApplicationBusinessException 
	 * @throws JRException 
	 * @throws IOException 
	 * 
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf() throws ApplicationBusinessException, IOException, JRException, DocumentException {
		//colecao.clear();
		DocumentoJasper documento = gerarDocumento();
		if (getPermiteImprimirDescricaoProcedimentosPOL()) {
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));			
		} else {
			return this.criarStreamedContentPdf(documento.getPdfByteArray(true));			
		}
	}	
	
	
	public String voltar(){
		return this.voltarPara;
	}

	// Getters e Setters

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public List<PdtDescricaoProcedimentoCirurgiaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<PdtDescricaoProcedimentoCirurgiaVO> colecao) {
		this.colecao = colecao;
	}

	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}	

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Boolean getPermiteImprimirDescricaoProcedimentosPOL() {
		return permiteImprimirDescricaoProcedimentosPOL;
	}

	public void setPermiteImprimirDescricaoProcedimentosPOL(
			Boolean permiteImprimirDescricaoProcedimentosPOL) {
		this.permiteImprimirDescricaoProcedimentosPOL = permiteImprimirDescricaoProcedimentosPOL;
	}

	public Boolean getPrevia() {
		return previa;
	}

	public void setPrevia(Boolean previa) {
		this.previa = previa;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public Collection<PdtDescricaoProcedimentoCirurgiaVO> getDescricaoPDT() {
		if(descricaoPDT == null || descricaoPDT.isEmpty()){
			try {
				descricaoPDT = recuperarColecao();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return descricaoPDT;
	}

	public void setDescricaoPDT(
			Collection<PdtDescricaoProcedimentoCirurgiaVO> descricaoPDT) {
		this.descricaoPDT = descricaoPDT;
	}
	
	public boolean isChamadoPorRegistroCirurgiaRealizada() {
		return chamadoPorRegistroCirurgiaRealizada;
	}

	public void setChamadoPorRegistroCirurgiaRealizada(
			boolean chamadoPorRegistroCirurgiaRealizada) {
		this.chamadoPorRegistroCirurgiaRealizada = chamadoPorRegistroCirurgiaRealizada;
	}
	
	public Integer getDdtSeq() {
		return ddtSeq;
	}

	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	public AvaliacaoPreSedacaoVO getAvaliacaoPreSedacaoVO() {
		return avaliacaoPreSedacaoVO;
	}

	public void setAvaliacaoPreSedacaoVO(AvaliacaoPreSedacaoVO avaliacaoPreSedacaoVO) {
		this.avaliacaoPreSedacaoVO = avaliacaoPreSedacaoVO;
	}
	
}