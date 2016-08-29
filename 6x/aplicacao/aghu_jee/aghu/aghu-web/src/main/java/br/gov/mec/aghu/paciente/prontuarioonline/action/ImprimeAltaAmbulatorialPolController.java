package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolImpressaoVO;
import net.sf.jasperreports.engine.JRException;


public class ImprimeAltaAmbulatorialPolController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(ImprimeAltaAmbulatorialPolController.class);


	private static final long serialVersionUID = -7059135769608077352L;


	private static final String ALTA_AMBULATORIAL_POL_PDF = "pol-altaAmbulatorialPolPdf";

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject
	private SecurityController securityController;	

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private List<AltaAmbulatorialPolImpressaoVO> colecao = new ArrayList<AltaAmbulatorialPolImpressaoVO>(0);

	private Long seqMamAltaSumario;

	private String voltarPara;
	
	private Boolean permiteImprimirAltaAmbulatorialPOL;

	public void impressaoDireta(Long seqMamAltaSumario)	throws BaseException, JRException, SystemException, IOException {
		this.seqMamAltaSumario = seqMamAltaSumario;
		directPrint();
	}

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		permiteImprimirAltaAmbulatorialPOL = securityController.usuarioTemPermissao("permiteImprimirAltaAmbulatorialPOL", "imprimir");
	}
	
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() {

		try {
			colecao = this.prontuarioOnlineFacade.recuperarAltaAmbuPolImpressaoVO(seqMamAltaSumario);

			// if (colecao.isEmpty()) {
			// this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			// return;
			// }

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Collection<AltaAmbulatorialPolImpressaoVO> recuperarColecao() throws ApplicationBusinessException  {
		colecao = this.prontuarioOnlineFacade.recuperarAltaAmbuPolImpressaoVO(seqMamAltaSumario);
		return colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/altaAmbulatorialPol.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		try {
			Map<String, Object> params = new HashMap<String, Object>();

			String enderecoCompleto = prontuarioOnlineFacade
					.getEnderecoCompleto();
			params.put("enderecoHospitalCompleto", enderecoCompleto);
			
			try {
				params.put("logo", recuperarCaminhoLogo());
			} catch (BaseException e) {
				LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
			}
			params.put("dataAtual", DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()));

			params.put("funcionalidade", "Alta Ambulatorial");

			String postoSaude = prontuarioOnlineFacade.getTextoPostoSaude();
			params.put("posto", postoSaude);

			String subRelatorio1 = "br/gov/mec/aghu/paciente/prontuarioonline/report/subAltaAmbPolDiag.jasper";
			String subRelatorio2 = "br/gov/mec/aghu/paciente/prontuarioonline/report/subAltaAmbPolEvol.jasper";
			String subRelatorio3 = "br/gov/mec/aghu/paciente/prontuarioonline/report/subAltaAmbPolPres.jasper";

			params.put("subReport1", Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(subRelatorio1));
			params.put("subReport2", Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(subRelatorio2));
			params.put("subReport3", Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(subRelatorio3));

			return params;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * @throws JRException 
	 * @throws IOException 
	 * 
	 * @throws DocumentException
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException  {
		colecao.clear();
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false)); // Protegido? = TRUE
	}

	public String exibeRelatorioAltasAmbulatorias(Long seqMamAltaSumario)
			throws ApplicationBusinessException {
		try{
			getProntuarioOnlineFacade().validarRelatorioAltasAmbulatorias(seqMamAltaSumario);
			
			this.seqMamAltaSumario = seqMamAltaSumario;
			if (seqMamAltaSumario != null) {
				return ALTA_AMBULATORIAL_POL_PDF;
			}
			
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	
	public String voltar(){
		return voltarPara;
	}
	
	
	// Getters e Setters

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public List<AltaAmbulatorialPolImpressaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<AltaAmbulatorialPolImpressaoVO> colecao) {
		this.colecao = colecao;
	}

	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public Long getSeqMamAltaSumario() {
		return seqMamAltaSumario;
	}

	public void setSeqMamAltaSumario(Long seqMamAltaSumario) {
		this.seqMamAltaSumario = seqMamAltaSumario;
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

	public Boolean getPermiteImprimirAltaAmbulatorialPOL() {
		return permiteImprimirAltaAmbulatorialPOL;
	}

	public void setPermiteImprimirAltaAmbulatorialPOL(
			Boolean permiteImprimirAltaAmbulatorialPOL) {
		this.permiteImprimirAltaAmbulatorialPOL = permiteImprimirAltaAmbulatorialPOL;
	}

}
