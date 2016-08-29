package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
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
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.vo.RelatorioCaracteristicasResultadosPorExameVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import net.sf.jasperreports.engine.JRException;


public class RelatorioCaracteristicasResultadosPorExameController extends ActionReport {

	private static final String END_NL = "> \n";

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	private static final long serialVersionUID = 1350008746440561272L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioCaracteristicasResultadosPorExameController.class);

	private static final String AGRUPAR_CARACTERISTICA_EXAME = "exames-agruparCaracteristicaExame";

	private static final String RELATORIO_CARACTERISTICAS_RESULTADOS_PDF = "exames-relatorioCaracteristicasResultadosPdf";

	//Parâmetros de consulta
	private String siglaExame;
	private Integer manSeq;
	
	private String origem;
	
	private List<RelatorioCaracteristicasResultadosPorExameVO> colecao = new ArrayList<RelatorioCaracteristicasResultadosPorExameVO>(0);

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_CARACTERISTICAS_RESULTADOS_PDF;
	}
	
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Collection<RelatorioCaracteristicasResultadosPorExameVO> recuperarColecao() throws ApplicationBusinessException {
		if(siglaExame!=null && manSeq!=null){
			colecao = this.pesquisaExamesFacade.pesquisarRelatorioCaracteristicasResultadosExame(siglaExame,manSeq);
		}
		return this.colecao;
	}
	
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("hospitalLocal", hospital);
		params.put("tituloRelatorio", "Características de Resultado por Exame");  
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("nomeRelatorio", "AELR_CARACTERIST");
		params.put("totalRegistros", colecao.size()-1);

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioCaracteristicasResutados.jasper";
	}

	public String voltar(){
		return AGRUPAR_CARACTERISTICA_EXAME;
	}
	
	/**
	 * UTILIZADO PARA TESTES NO IREPORT
	 * @param source
	 */
	public void converteParaXml(List<? extends Object> source) {

		try {               
			StringBuffer xml = new StringBuffer("\n <?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <aghu> \n")
			.append(montarXml(source))
			.append("</aghu>");
			LOG.info(xml);
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		}

	}

	private String montarXml(List<? extends Object> source) throws IllegalAccessException {
		StringBuffer xml = new StringBuffer();
		if(source != null && !source.isEmpty()){   
			Field[] fields = source.get(0).getClass().getDeclaredFields();
			for(Object vo : source){
				xml.append("    <").append(vo.getClass().getSimpleName()).append(END_NL);
				for(Field field : fields){
					field.setAccessible(true);
					String nomeField = field.getName();


					Class<?> typeField = field.getType();
					if(typeField.getName().contains("List")){
						List<Object> list = (List<Object>)field.get(vo);

						xml.append("        <").append(nomeField).append(END_NL);
						xml.append(montarXml(list));
						xml.append("\n        </").append(nomeField).append(END_NL);

					}else{
						Object valor = field.get(vo);
						xml.append("        <").append(nomeField).append('>');
						xml.append(valor);
						xml.append("</").append(nomeField).append(END_NL);
					}


				}
				xml.append("    </").append(vo.getClass().getSimpleName()).append(END_NL);
			}
		}
		return xml.toString();
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}
	
	public String getSiglaExame() {
		return siglaExame;
	}

	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public List<RelatorioCaracteristicasResultadosPorExameVO> getColecao() {
		return colecao;
	}

	public void setColecao(
			List<RelatorioCaracteristicasResultadosPorExameVO> colecao) {
		this.colecao = colecao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
}
