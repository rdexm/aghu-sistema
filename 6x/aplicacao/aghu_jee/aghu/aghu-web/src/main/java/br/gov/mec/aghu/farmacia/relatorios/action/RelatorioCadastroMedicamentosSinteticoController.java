package br.gov.mec.aghu.farmacia.relatorios.action;

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
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.vo.MedicamentoSinteticoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import net.sf.jasperreports.engine.JRException;

//Estória #5697
public class RelatorioCadastroMedicamentosSinteticoController extends ActionReport{
	
	private static final Log LOG = LogFactory.getLog(RelatorioCadastroMedicamentosSinteticoController.class);

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}



	private static final long serialVersionUID = 7899712934418059623L;
	
	private static final String PAGE_CADASTRO_MEDICAMENTO_SINTETICO= "cadastroMedicamentosSintetico";
	private static final String PAGE_CADASTRO_MEDICAMENTO_SINTETICO_PDF= "cadastroMedicamentosSinteticoPdf";
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Dados que serão impressos 
	 */
	private List<MedicamentoSinteticoVO> colecao = new ArrayList<MedicamentoSinteticoVO>();

	@PostConstruct
	public void init() {
		try {
			print();
		} catch (JRException | IOException | DocumentException e) {
			LOG.error("Erro no @PostConstruct RelatorioCadastroMedicamentosSinteticoController", e);
		}
	}

	/**	 * Impressão direta usando o CUPS.
	 * 
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		try {			

			colecao = this.farmaciaFacade.obterListaTodosMedicamentos();

			if(colecao.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Collection<MedicamentoSinteticoVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}

	/**
	 * Apresenta PDF na tela do navegador.
	 * 
	 */
	public String print()throws  JRException, IOException, DocumentException{
		try {

			colecao = this.farmaciaFacade.obterListaTodosMedicamentos();

			if(colecao.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			
		
	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return null;
			}
			return PAGE_CADASTRO_MEDICAMENTO_SINTETICO_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String voltar(){
		return PAGE_CADASTRO_MEDICAMENTO_SINTETICO;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String subRelatorio1 = "br/gov/mec/aghu/farmacia/report/subMedicamentoSinteticoMedicamento.jasper";
		String subRelatorio2 = "br/gov/mec/aghu/farmacia/report/subMedicamentoSinteticoTipoUsoMedicamento.jasper";
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()));
		params.put("funcionalidade", "Medicamentos");
		params.put("nomeRelatorio", "AFAR_CAD_MDTOS_SINT");		
		
		params.put("subReport1", Thread.currentThread().getContextClassLoader().getResourceAsStream(subRelatorio1));
		params.put("subReport2", Thread.currentThread().getContextClassLoader().getResourceAsStream(subRelatorio2));

		return params;			
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 */
	public StreamedContent getRenderPdf() throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}


	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/farmacia/report/medicamentoSintetico.jasper";
	}
	//Getters & Setters

	public List<MedicamentoSinteticoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<MedicamentoSinteticoVO> colecao) {
		this.colecao = colecao;
	}

}