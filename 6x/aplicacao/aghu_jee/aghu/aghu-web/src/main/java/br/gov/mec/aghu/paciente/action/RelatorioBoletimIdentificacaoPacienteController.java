package br.gov.mec.aghu.paciente.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.RelatorioPacienteVO;
import net.sf.jasperreports.engine.JRException;


public class RelatorioBoletimIdentificacaoPacienteController extends ActionReport {

	private static final long serialVersionUID = -5970033406313746118L;
	private static final Log LOG = LogFactory.getLog(RelatorioBoletimIdentificacaoPacienteController.class);
	private static final String PACIENTE_CADASTRO_PACIENTE = "paciente-cadastroPaciente";
	private static final String PACIENTE_RELATORIO_BOLETIM_IDENTIFICACAO_PACIENTE = "paciente-relatorioBoletimIdentificacaoPaciente";
	private static final String PACIENTE_RELATORIO_BOLETIM_IDENTIFICACAO_PACIENTE_PDF = "paciente-relatorioBoletimIdentificacaoPacientePdf";
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	private AghInstituicoesHospitalares aghInstituicoesHospitalares;

	private DocumentoJasper documento;

	/**
	 * Prontuário
	 */
	private Integer prontuario;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioPacienteVO> colecao = new ArrayList<RelatorioPacienteVO>(1);
	
	/**
	 * Método invocado na criação do componente.
	 */
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void directPrint(Integer prontuario) throws SistemaImpressaoException, ApplicationBusinessException, JRException {
		setProntuario(prontuario);
		directPrint();
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {
		try {
			documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * Apresenta PDF na tela do navegador.
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print() {
		return PACIENTE_RELATORIO_BOLETIM_IDENTIFICACAO_PACIENTE_PDF;
	}
	
	public String inicializar(){
		try {
			documento = gerarDocumento();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return PACIENTE_RELATORIO_BOLETIM_IDENTIFICACAO_PACIENTE;
		}
		return null;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws MECBaseException
	 * @throws DocumentException 
	 */
	
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		try {
			DocumentoJasper documento = gerarDocumento();
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	@Override
	public List<RelatorioPacienteVO> recuperarColecao() throws ApplicationBusinessException {
		RelatorioPacienteVO pacienteVO = pacienteFacade.obterPacienteComEnderecoPadrao(getProntuario(), null, null);
		this.colecao.clear();
		this.colecao.add(pacienteVO);
		return this.colecao;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/report/boletimIdentificacaoPaciente.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		aghInstituicoesHospitalares = aghuFacade.recuperarInstituicaoHospitalarLocal();
		if (aghInstituicoesHospitalares != null) {
			String cidade = "";
			if (aghInstituicoesHospitalares.getCddCodigo() != null) {
				cidade = aghInstituicoesHospitalares.getCddCodigo().getNome();
			} else if (aghInstituicoesHospitalares.getCidade() != null) {
				cidade = aghInstituicoesHospitalares.getCidade();
			}
			params.put("nomeHospital", capitalizarNomeInstituicao(aghInstituicoesHospitalares.getNome()));
			params.put(
					"cidade",
					WordUtils.capitalizeFully(cidade)
							.replaceAll(" Da ", " da ")
							.replaceAll(" De ", " de ")
							.replaceAll(" Do ", " do "));
		} else {
			params.put("nomeHospital", "");
			params.put("cidade", "");
		}

		params.put("nomeRelatorio", "AIPR_BOLETIM_IDEN");
		params.put("caminhoLogo", parametroFacade.recuperarCaminhoLogo2Relativo());
		
		return params;
	}

	/**
	 * Capitaliza o nome da instituição hospitalar
	 * @param nome
	 * @return
	 */
	private String capitalizarNomeInstituicao(String nome){
		
		if (nome != null) {
			return WordUtils.capitalizeFully(nome).replaceAll(" Da ", " da ")
					.replaceAll(" De ", " de ").replaceAll(" Do ", " do ");
		} else {
			return "";
		}
	}
	

	/**
	 * Metodo que verifica se o usuario tem permissao de visualizar Modal onde
	 * opta por relatorio em tela ou a impressao direta em impressora.
	 * 
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws SistemaImpressaoException 
	 */
	public void checkModal() throws SistemaImpressaoException, ApplicationBusinessException, JRException {
		directPrint();
	}

	public String voltar(){
		return PACIENTE_RELATORIO_BOLETIM_IDENTIFICACAO_PACIENTE;
	}
	
	public String voltarCadastroPaciente(){
		return PACIENTE_CADASTRO_PACIENTE;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AghInstituicoesHospitalares getAghInstituicoesHospitalares() {
		return aghInstituicoesHospitalares;
	}

	public void setAghInstituicoesHospitalares(
			AghInstituicoesHospitalares aghInstituicoesHospitalares) {
		this.aghInstituicoesHospitalares = aghInstituicoesHospitalares;
	}
}
