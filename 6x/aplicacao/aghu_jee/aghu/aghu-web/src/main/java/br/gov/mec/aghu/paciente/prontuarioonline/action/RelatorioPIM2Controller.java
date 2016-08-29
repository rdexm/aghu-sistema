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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.vo.ImpressaoPIM2VO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioPIM2Controller extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioPIM2Controller.class);

	@Inject
	private SistemaImpressao sistemaImpressao;

	private static final long serialVersionUID = 5349518595132043274L;
	
	private List<ImpressaoPIM2VO> dadosRelatorio = null;
	
	private String nomeHospitalRelatorio = null;
	
	private Long seqPim2;
	
	private String origem;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/relatorioPIM2.jasper";
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<ImpressaoPIM2VO> recuperarColecao() throws ApplicationBusinessException {
		return dadosRelatorio;
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		
		Map<String, Object> params = new HashMap<String, Object>();
				
		// informa todos os parametros necessarios
		params.put("P_HOSPITAL_RAZAO_SOCIAL", getNomeHospitalRelatorio());
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}		
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/paciente/prontuarioonline/report/");
		
		return params;
	}
	
	public void gerarDados() {
		AghParametros parametro = null;
		try {
			parametro = parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			
			dadosRelatorio = new ArrayList<ImpressaoPIM2VO>();
			dadosRelatorio.add(prontuarioOnlineFacade.gerarDadosImpressaoPIM2(getSeqPim2()));			
			setNomeHospitalRelatorio(parametro.getVlrTexto());			
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar() {
		limparDados();
		return getOrigem();
	}

	private void limparDados(){
		setNomeHospitalRelatorio(null);
		setSeqPim2(null);
		setDadosRelatorio(null);
	}
	
	/**
	 * Renderiza o PDF
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws JRException
	 * @throws DocumentException
	 * @throws BaseException
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE			
	}

	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento(Boolean.TRUE);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Long getSeqPim2() {
		return seqPim2;
	}

	public void setSeqPim2(Long seqPim2) {
		this.seqPim2 = seqPim2;
	}

	public List<ImpressaoPIM2VO> getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(List<ImpressaoPIM2VO> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public String getNomeHospitalRelatorio() {
		return nomeHospitalRelatorio;
	}

	public void setNomeHospitalRelatorio(String nomeHospitalRelatorio) {
		this.nomeHospitalRelatorio = nomeHospitalRelatorio;
	}
}