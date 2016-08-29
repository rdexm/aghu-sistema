package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AvaliacaoPreSedacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.DescricaoCirurgiaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



public class RelatorioDescricaoCirurgiaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 conversationContext.setConcurrentAccessTimeout(900000000000l);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioDescricaoCirurgiaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -3949012343792385390L;

	@EJB
	IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject @Http 
	private ConversationContext conversationContext;


	private Boolean print;
	private Boolean previa;
	private Integer crgSeq;
	private Short seqpMbcDescCrg;
	private Collection<DescricaoCirurgiaVO> colecao;
	
	private Collection<DescricaoCirurgiaVO> descricaoCirurgica;
	private String voltarPara;
	
	private MbcCirurgias cirurgia;
	private Boolean certificar;
	
	private boolean chamadoPorRegistroCirurgiaRealizada = false;
	private AvaliacaoPreSedacaoVO avaliacaoPreSedacaoVO;
	
	public void inicio(){
		this.carregarParametrosRequest();
		definirVoltarPara();
		descricaoCirurgica = recuperarColecao();
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
	
	public String voltar(){
		return voltarPara;
	}

	
	@Override
	protected BaseEntity getEntidadePai() {
		if(this.crgSeq != null) {
			return this.cirurgia = blocoCirurgicoFacade.obterCirurgiaPorSeq(crgSeq);
		}
		
		return null;
	}
	
	@Override
	public Collection<DescricaoCirurgiaVO> recuperarColecao() {
		String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		try {
			colecao =  prontuarioOnlineFacade.pesquisarDescricaoCirurgicaPol(crgSeq, seqpMbcDescCrg, previa, contextPath);
			
			if(avaliacaoPreSedacaoVO != null){
				for(DescricaoCirurgiaVO c : colecao){
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
		
		} catch (BaseException  e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void directPrint() {
		try {
					
			DocumentoJasper documento;
			
			if(Boolean.TRUE.equals(certificar)) {
				 documento = gerarDocumento();
			}
			else {
				 documento = gerarDocumento(DominioTipoDocumento.DC, true);
			}

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}

	/**
	 * Apresenta PDF na tela do navegador.
	 * 
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public void print()throws JRException, IOException, DocumentException, ApplicationBusinessException {
		colecao = recuperarColecao();
		
		DocumentoJasper documento;

		if(Boolean.TRUE.equals(certificar)) {
			 documento = gerarDocumento();
		}
		else {
			 documento = gerarDocumento(DominioTipoDocumento.DC, true);
		}

		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));
			
		
	}

	@Override
	public String recuperarArquivoRelatorio() {	
		
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/relatorioDescricaoCirurgia.jasper";
		
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}		
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/paciente/prontuarioonline/report/");
		params.put("previaUrlImagem", recuperarCaminhoImgBackground());

		return params;
	}
	
	private String recuperarCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/imag/report_previa.png");
		return path;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws BaseException
	 * @throws DocumentException 
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento;

		if(Boolean.TRUE.equals(certificar)) {
			 documento = gerarDocumento();
		}
		else {
			 documento = gerarDocumento(DominioTipoDocumento.DC, true);
		}

		//if(getPermiteImprimirCirurgiaInternacaoPOL()) {
		//	return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.FALSE));
		//} else {
			return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
		//}
	}

	public void geraPendenciasCertificacaoDigital() throws BaseException {
		this.gerarDocumento(DominioTipoDocumento.DC);		
	}
	
	// Getters e Setters

	public Collection<DescricaoCirurgiaVO> getDescricaoCirurgica() {
		if (descricaoCirurgica == null || descricaoCirurgica.isEmpty()){
			descricaoCirurgica = recuperarColecao();
		}
		return descricaoCirurgica;
	}
	
	public Collection<DescricaoCirurgiaVO> getColecao() {
		return colecao;
	}

	public void setColecao(Collection<DescricaoCirurgiaVO> colecao) {
		this.colecao = colecao;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public void setPrint(Boolean print) {
		this.print = print;
	}

	public Boolean getPrint() {
		return print;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public Short getSeqpMbcDescCrg() {
		return seqpMbcDescCrg;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public void setSeqpMbcDescCrg(Short seqpMbcDescCrg) {
		this.seqpMbcDescCrg = seqpMbcDescCrg;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	public void setPrevia(Boolean previa) {
		this.previa = previa;
	}

	public Boolean getPrevia() {
		return previa;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public Boolean getCertificar() {
		return certificar;
	}

	public void setCertificar(Boolean certificar) {
		this.certificar = certificar;
	}
	
	public boolean isChamadoPorRegistroCirurgiaRealizada() {
		return chamadoPorRegistroCirurgiaRealizada;
	}
	
	public void setChamadoPorRegistroCirurgiaRealizada(
			boolean chamadoPorRegistroCirurgiaRealizada) {
		this.chamadoPorRegistroCirurgiaRealizada = chamadoPorRegistroCirurgiaRealizada;
	}

	public AvaliacaoPreSedacaoVO getAvaliacaoPreSedacaoVO() {
		return avaliacaoPreSedacaoVO;
	}

	public void setAvaliacaoPreSedacaoVO(AvaliacaoPreSedacaoVO avaliacaoPreSedacaoVO) {
		this.avaliacaoPreSedacaoVO = avaliacaoPreSedacaoVO;
	}
		
}