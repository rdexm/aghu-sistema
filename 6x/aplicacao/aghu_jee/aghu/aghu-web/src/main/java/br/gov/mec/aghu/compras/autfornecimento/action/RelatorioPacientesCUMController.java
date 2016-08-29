package br.gov.mec.aghu.compras.autfornecimento.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.view.VScoPacientesCUM;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



public class RelatorioPacientesCUMController extends ActionReport {

	

	private static final Log LOG = LogFactory.getLog(RelatorioPacientesCUMController.class);
	
	private static final long serialVersionUID = 4916702392530524924L;	
	
		
	public enum EnumTargetRelatorioPacientesCUM{
		LABEL_MENSAGEM_RELATORIO_PACIENTES_CUM,
		LABEL_TITULO_RELATORIO_PACIENTES_CUM,
		LABEL_MENSAGEM_ENTRE_CONTATO_RELATORIO_PACIENTES_CUM;
	}
	
	private Integer afeAfnNumero;
	private Integer afeNumero;
	
	//armazena dados do relatório
	private List<VScoPacientesCUM> dados = null;

	

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAutFornecimentoFacade autorizacaoFornecimentoFacade;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);	
		this.setAfeAfnNumero(138617);
		this.setAfeNumero(80);
	}
	
	/**
	 * Recupera arquivo compilado do Jasper
	 */
	@Override
	public String recuperarArquivoRelatorio(){
		return "br/gov/mec/aghu/compras/report/relatorioPacientesCUM.jasper";
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {		
		return getDados();
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		
		
		Map<String, Object> params = new HashMap<String, Object>();
		/*Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy");*/
		String hospital;
		try {
			
			hospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL).getVlrTexto();
			params.put("nomeHospital", hospital);
		    params.put("nomeRelatorio", "SCOR_PROGR_ENTG_X_CUM");
		    params.put("tituloRelatorio",  super.getBundle().getString(EnumTargetRelatorioPacientesCUM.LABEL_TITULO_RELATORIO_PACIENTES_CUM.toString()));
		    String mensagemRelatorioPacientesCUM = super.getBundle().getString(EnumTargetRelatorioPacientesCUM.LABEL_MENSAGEM_RELATORIO_PACIENTES_CUM.toString());		
		    String mensagemEntreContratoRelatorioPacientesCUM = super.getBundle().getString(EnumTargetRelatorioPacientesCUM.LABEL_MENSAGEM_ENTRE_CONTATO_RELATORIO_PACIENTES_CUM.toString());		
			   
		    params.put("mensagem", mensagemRelatorioPacientesCUM);
		    params.put("mensagemEntreContato", mensagemEntreContratoRelatorioPacientesCUM);
		    
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		
		return params;
	}

	/**
	 * Método que carrega a lista de VO's para ser usado no relatório.
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JRException 
	 */
	
	public void gerarArquivoPdf() throws ApplicationBusinessException, JRException, IOException, DocumentException{	
		
		if(this.getAfeAfnNumero() !=null &&
		   this.getAfeNumero() !=null){
			
		   setDados(this.autorizacaoFornecimentoFacade.pesquisarPacientesCUMPorAFeAFP(this.getAfeAfnNumero(), this.getAfeNumero()));
						
		  DocumentoJasper documento = gerarDocumento();
		  Date dataAtual = new Date();
		  SimpleDateFormat sdf_1 = new SimpleDateFormat("ddMMyyHHmm");
		
	      File file = new java.io.File("/opt/aghu/afp/PAC_AFP_" + this.getAfeAfnNumero() + "_" + this.getAfeNumero() + "_" +  sdf_1.format(dataAtual) + ".pdf");  
		  FileOutputStream in = new FileOutputStream(file) ;    
		  in.write(documento.getPdfByteArray(false));  
		  in.close();		 
		}
			
	}
	
   public DocumentoJasper gerarDocumentoEmail() throws ApplicationBusinessException, JRException, IOException, DocumentException{	
		
		if(this.getAfeAfnNumero() !=null &&
		   this.getAfeNumero() !=null){
			
		   setDados(this.autorizacaoFornecimentoFacade.pesquisarPacientesCUMPorAFeAFP(this.getAfeAfnNumero(), this.getAfeNumero()));
						
		  DocumentoJasper documento = gerarDocumento();
		  return documento;
		  /*Date dataAtual = new Date();
		  SimpleDateFormat sdf_1 = new SimpleDateFormat("ddMMyyHHmm");
		
	      File file = new java.io.File("/opt/aghu/afp/PAC_AFP_" + this.getAfeAfnNumero() + "_" + this.getAfeNumero() + "_" +  sdf_1.format(dataAtual) + ".PDF");  
		  FileOutputStream in = new FileOutputStream(file) ;    
		  in.write(documento.getPdfByteArray(false));  
		  in.close();	*/	 
		}
		return null;	
	}

	public Integer getAfeAfnNumero() {
		return afeAfnNumero;
	}

	public void setAfeAfnNumero(Integer afeAfnNumero) {
		this.afeAfnNumero = afeAfnNumero;
	}

	public Integer getAfeNumero() {
		return afeNumero;
	}

	public void setAfeNumero(Integer afeNumero) {
		this.afeNumero = afeNumero;
	}

	public List<VScoPacientesCUM> getDados() {
		return dados;
	}

	public void setDados(List<VScoPacientesCUM> dados) {
		this.dados = dados;
	}
	
	
}