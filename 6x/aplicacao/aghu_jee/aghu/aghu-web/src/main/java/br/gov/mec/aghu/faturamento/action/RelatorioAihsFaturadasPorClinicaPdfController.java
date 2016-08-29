package br.gov.mec.aghu.faturamento.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.AihsFaturadasPorClinicaVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioAihsFaturadasPorClinicaPdfController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3648181537634894817L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioAihsFaturadasPorClinicaPdfController.class);

	@EJB
    private IParametroFacade parametroFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private String iniciaisPaciente;

	private String origem;

	private Boolean isDirectPrint;

	private Integer ano;

	private Integer mes;

	private Date dtHrInicio;
	
	private List<AihsFaturadasPorClinicaVO> colecao = new ArrayList<AihsFaturadasPorClinicaVO>();
	
	private final static String NOME_ARQUIVO= "FATR_INT_AIH_CLINICA";
	
	private File arquivoGerado;
	
	public enum RelatorioRelacaoDeOPMNaoFaturadaExceptionCode implements BusinessExceptionCode {
		NENHUM_REGISTRO_ORTESES_PROTESES;
	}
	
	@PostConstruct
	protected void init(){
		begin(conversation);
	}

	public String inicio() throws ApplicationBusinessException {
		colecao = new ArrayList<AihsFaturadasPorClinicaVO>();
		if (iniciaisPaciente != null && iniciaisPaciente.isEmpty()) {
			iniciaisPaciente = null;
		}
		colecao = carregarColecao();
		
		if(colecao == null || colecao.isEmpty()){
			throw new ApplicationBusinessException(RelatorioRelacaoDeOPMNaoFaturadaExceptionCode.NENHUM_REGISTRO_ORTESES_PROTESES);
		}
		if (isDirectPrint) {
			this.directPrint();
		}
		return null;
	}

	@Override
	public Collection<AihsFaturadasPorClinicaVO> recuperarColecao() {
		return colecao;
	}
	
	public List<AihsFaturadasPorClinicaVO> carregarColecao() throws ApplicationBusinessException{
		Integer maximoLinhas = 34;
		String dcih = null;
		colecao = new ArrayList<AihsFaturadasPorClinicaVO>();
		List<AihsFaturadasPorClinicaVO> colecaoFake = faturamentoFacade.obterAihsFaturadasPorClinica(ano, mes, dtHrInicio, iniciaisPaciente);
		for (AihsFaturadasPorClinicaVO item : colecaoFake) {
			if(item.getDcihlabel().equals(dcih)){
				if(!(maximoLinhas == 0)){
					item.setDcihlabel(null);
				}else{
					 dcih = item.getDcihlabel();
					 maximoLinhas = 34;
				}
			}else{
				 dcih = item.getDcihlabel();
				 maximoLinhas = 34;
			}
			
			maximoLinhas--;
			
			if(item.getProntuario() != null){
				item.setProntuarioformatado(CoreUtil.formataProntuario(item.getProntuario()));
			}else{
				item.setProntuarioformatado(" ");
			}
			item.setClinica(item.getCodcli().toString()+" - "+item.getDesccli());
			colecao.add(item);
		}
		return colecao;
	}

	public String voltar() {
		return origem;
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}
	
	public void directPrint()  {
		try {
			carregarColecao();
		} catch (ApplicationBusinessException e1) {
			apresentarExcecaoNegocio(e1);
		}
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		}else{
			try {
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
				
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		}
	}
	
	public void dispararDownloadPDF() {
		// Gera o PDF
		try {
			DocumentoJasper documento = gerarDocumento();
					
			final File file = File.createTempFile(NOME_ARQUIVO +  DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_MM) +   DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY), ".pdf");
			final FileOutputStream out = new FileOutputStream(file);

			out.write(documento.getPdfByteArray(false));
			out.flush();
			out.close();
			
			arquivoGerado = file;

			if(arquivoGerado != null) {
				download(arquivoGerado, DominioMimeType.PDF.getContentType());
			}
			arquivoGerado = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
					
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public DocumentoJasper buscarDocumentoGerado() throws ApplicationBusinessException{
		return this.gerarDocumento();
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioAihsFaturadasPorClinica.jasper";
	}

	public Map<String, Object> recuperarParametros() {
		
		final Map<String, Object> params = new HashMap<String, Object>();
		String local = "";
		try {
			local = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		params.put("nomeHospital",  local);
		params.put("competencia",  dtHrInicio);
		
		return params;
	}

	public String getIniciaisPaciente() {
		return iniciaisPaciente;
	}

	public void setIniciaisPaciente(String iniciaisPaciente) {
		this.iniciaisPaciente = iniciaisPaciente;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Date getDtHrInicio() {
		return dtHrInicio;
	}

	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}

	public List<AihsFaturadasPorClinicaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<AihsFaturadasPorClinicaVO> colecao) {
		this.colecao = colecao;
	}

	public File getArquivoGerado() {
		return arquivoGerado;
	}

	public void setArquivoGerado(File arquivoGerado) {
		this.arquivoGerado = arquivoGerado;
	}
	
}
