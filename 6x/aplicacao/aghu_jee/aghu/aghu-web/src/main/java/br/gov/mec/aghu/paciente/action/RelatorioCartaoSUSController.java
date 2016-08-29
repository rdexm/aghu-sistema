package br.gov.mec.aghu.paciente.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * 
 * @author MurilloMarinho
 * 
 */

public class RelatorioCartaoSUSController extends ActionReport {
	
	private static final long serialVersionUID = -6540882227024385594L;

	private static final Log LOG = LogFactory.getLog(RelatorioCartaoSUSController.class);

	private static final String PACIENTE_CADASTRO_PACIENTE = "paciente-cadastroPaciente";

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	private AghInstituicoesHospitalares aghInstituicoesHospitalares;

	/**
	 * Código do Paciente
	 */
	private Integer codigoPaciente;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<Object> colecao = new ArrayList<Object>(0);
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/*
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @param aipPacientesCodigo
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print(Integer aipPacientesCodigo) throws BaseException, JRException, SystemException, IOException {
		this.codigoPaciente = aipPacientesCodigo;
		return "relatorioCartaoSUS";
	}

	// @Observer("imprimirCartaoSus")
	public void directPrint(Integer aipPacientesCodigo) throws BaseException, JRException, SystemException, IOException {
		this.setCodigoPaciente(aipPacientesCodigo);
		DocumentoJasper documento = gerarDocumento();
		try {
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
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
	@Override
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Collection<Object> recuperarColecao() throws ApplicationBusinessException {
		this.colecao.clear();
		try {
			this.colecao.add(pacienteFacade.obterPacienteComEnderecoPadrao(null, null, getCodigoPaciente()));
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarExcecaoNegocio(e);
		}
		return colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/report/relatorioCartaoSUS.jasper";
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

			params.put("nomeEAS", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());
			if (aghInstituicoesHospitalares.getCnes() != null) {
				params.put("CNES", aghInstituicoesHospitalares.getCnes().toString());
			} else {
				params.put("CNES", "");
			}

			if (aghInstituicoesHospitalares.getCodIbge() != null) {
				params.put("codigoPOA", aghInstituicoesHospitalares.getCodIbge().toString() + " - " + cidade);
			} else {
				params.put("codigoPOA", "");
			}

		} else {
			params.put("nomeEAS", "");
			params.put("CNES", "");
			params.put("codigoPOA", "");
		}

		params.put("nomeRelatorio", "AIP_CAD_SUS_EAS");
		params.put("caminhoLogo", parametroFacade.recuperarCaminhoLogoRelativo());
		return params;
	}

	public String voltarCadastroPaciente() {
		return PACIENTE_CADASTRO_PACIENTE;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public List<Object> getColecao() {
		return colecao;
	}

	public void setColecao(List<Object> colecao) {
		this.colecao = colecao;
	}

	public AghInstituicoesHospitalares getAghInstituicoesHospitalares() {
		return aghInstituicoesHospitalares;
	}

	public void setAghInstituicoesHospitalares(AghInstituicoesHospitalares aghInstituicoesHospitalares) {
		this.aghInstituicoesHospitalares = aghInstituicoesHospitalares;
	}

}
