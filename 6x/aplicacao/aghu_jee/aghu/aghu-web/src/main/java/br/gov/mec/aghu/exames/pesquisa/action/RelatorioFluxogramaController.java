package br.gov.mec.aghu.exames.pesquisa.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialDadosDataValorVO;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialDadosVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import net.sf.jasperreports.engine.JRException;

public class RelatorioFluxogramaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 7210844403149103307L;

	private static final Log LOG = LogFactory.getLog(RelatorioFluxogramaController.class);

	private static final String PESQUISA_FLUXOGRAMA  = "exames-pesquisaFluxograma";
	private static final String RELATORIO_FLUXOGRAMA = "exames-relatorioFluxogramaPdf";

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject
	private SecurityController securityController;	 

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	
	private List<FluxogramaLaborarorialDadosVO> colecao = new ArrayList<FluxogramaLaborarorialDadosVO>();
	private List<Date> datas = new ArrayList<Date>();
	
	private String nomePaciente;
	private Integer prontuario;
	private Integer consulta;
	private String leito;
	private String andar;
	private String ala;
	private Integer idade;
	
	private Boolean imprimirFluxogramaLaboratorial;
	private Boolean origemPol = Boolean.FALSE;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		imprimirFluxogramaLaboratorial = securityController.usuarioTemPermissao("imprimirFluxogramaLaboratorial", "pesquisar");
	}
	
	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
		defineValores();
	
		DocumentoJasper documento = gerarDocumento();

		Boolean permiteImpressao = !imprimirFluxogramaLaboratorial;
		if(getOrigemPol()){
			permiteImpressao = Boolean.TRUE;//Impressão via plugin sempre deve estar desabilitada #27354 
		}
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(permiteImpressao)));
		return RELATORIO_FLUXOGRAMA;
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			
			defineValores();
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}
	
	private void defineValores(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		for (FluxogramaLaborarorialDadosVO record : this.colecao) {
			
			for (int i = 0; i < this.datas.size(); i++) {
				if(record.getDatasValores().containsKey(sdf.format(this.datas.get(i)))){
					
					FluxogramaLaborarorialDadosDataValorVO dataValor = (FluxogramaLaborarorialDadosDataValorVO)record.getDatasValores().get(sdf.format(this.datas.get(i))); 
					String valor = "";
					if(!dataValor.isPossuiNotaAdicional()){
						valor = dataValor.getDataValor();
					}

					switch (i) {
						case 0:  record.setValor0(valor);  break;
						case 1:  record.setValor1(valor);  break;
						case 2:  record.setValor2(valor);  break;
						case 3:  record.setValor3(valor);  break;
						case 4:  record.setValor4(valor);  break;
						case 5:  record.setValor5(valor);  break;
						case 6:  record.setValor6(valor);  break;
						case 7:  record.setValor7(valor);  break;
						case 8:  record.setValor8(valor);  break;
						case 9:  record.setValor9(valor);  break;
						case 10: record.setValor10(valor); break;
						case 11: record.setValor11(valor); break;
						case 12: record.setValor12(valor); break;
					}
				}
			}
		}
	}
	
	@Override
	public Collection<FluxogramaLaborarorialDadosVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_FLUXOGRAMA");
		params.put("tituloRelatorio", "Fluxograma Laboratorial");
		params.put("totalRegistros", colecao.size()-1);
		
		/*Dados Paciente*/
		params.put("nomePaciente", this.nomePaciente);
		params.put("prontuario", this.prontuario);
		params.put("leito", this.leito);
		params.put("andar", this.andar);
		params.put("ala", this.ala);
		params.put("consulta", this.consulta);
		params.put("idade", this.idade);

		/*	Parametros datas	*/

		for (int i = 0; i < datas.size(); i++) {
			params.put("data"+i, datas.get(i));
		}

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioFluxograma.jasper";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();

		Boolean permiteImpressao = !imprimirFluxogramaLaboratorial;
		if(getOrigemPol()){
			permiteImpressao = Boolean.TRUE;//Impressão via plugin sempre deve estar desabilitada #27354 
		}
		return this.criarStreamedContentPdf(documento.getPdfByteArray(permiteImpressao));
	}
	
	public String voltar(){
		return PESQUISA_FLUXOGRAMA;
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public List<FluxogramaLaborarorialDadosVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<FluxogramaLaborarorialDadosVO> colecao) {
		this.colecao = colecao;
	}

	public List<Date> getDatas() {
		return datas;
	}

	public void setDatas(List<Date> datas) {
		this.datas = datas;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getAndar() {
		return andar;
	}

	public void setAndar(String andar) {
		this.andar = andar;
	}

	public String getAla() {
		return ala;
	}

	public void setAla(String ala) {
		this.ala = ala;
	}

	public Integer getConsulta() {
		return consulta;
	}

	public void setConsulta(Integer consulta) {
		this.consulta = consulta;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public Boolean getOrigemPol() {
		return origemPol;
	}

	public void setOrigemPol(Boolean origemPol) {
		this.origemPol = origemPol;
	}
}