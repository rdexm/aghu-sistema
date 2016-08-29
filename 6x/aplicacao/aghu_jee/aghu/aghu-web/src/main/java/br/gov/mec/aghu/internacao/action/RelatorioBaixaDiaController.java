package br.gov.mec.aghu.internacao.action;

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

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.BaixasDiaPorEtniaVO;
import br.gov.mec.aghu.internacao.vo.BaixasDiaVO;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AipEtnia;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * @author tfelini
 */
public class RelatorioBaixaDiaController extends ActionReport {

	private static final long serialVersionUID = 7516633104301370819L;

	private static final Log LOG = LogFactory.getLog(RelatorioBaixaDiaController.class);

	private static final String RELATORIO_BAIXAS_DIA_PDF = "relatorioBaixasDiaPdf";
	private static final String RELATORIO_BAIXAS_DIA = "relatorioBaixasDia";

	private Date dataDeReferencia = new Date();

	private DominioGrupoConvenio grupoConvenio;

	private AghOrigemEventos origemEvento;
	
	private AipEtnia etniaPaciente;
	
	private boolean exibeEtnia;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@PostConstruct
	public void inicio() {
		begin(conversation);
	}

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<BaixasDiaVO> colecao = new ArrayList<BaixasDiaVO>(0);
	
	private List<BaixasDiaPorEtniaVO> colecaoPorEtnia = new ArrayList<BaixasDiaPorEtniaVO>(0); 

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print() throws ApplicationBusinessException, JRException, SystemException, IOException {
		return "relatorio";
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
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public String imprimirRelatorio() {
		try {
			carregarColecao();
			directPrint();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		
		return null;
	}

	public String visualizarRelatorio() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
			
		try {
			carregarColecao();
			
			return RELATORIO_BAIXAS_DIA_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}	
	}

	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		if(exibeEtnia){
			return colecaoPorEtnia;
		} else {
			return colecao;	
		}
	}

	@Override
	public String recuperarArquivoRelatorio() {
		if(exibeEtnia){
			return "br/gov/mec/aghu/internacao/report/relatorioBaixasDiaPorEtnia.jasper";
		} else {
			return "br/gov/mec/aghu/internacao/report/relatorioBaixasDia.jasper";
		}
	}
	
	public void carregarColecao() throws ApplicationBusinessException {
		
		if(exibeEtnia){
			colecaoPorEtnia = this.internacaoFacade.pesquisaBaixasDiaPorEtnia(this.dataDeReferencia, this.grupoConvenio, this.origemEvento,	this.etniaPaciente, true);
		} else {
			colecao = this.internacaoFacade.pesquisaBaixasDia(this.dataDeReferencia, this.grupoConvenio, this.origemEvento,	this.etniaPaciente, false);
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
	 * @throws MECBaseException
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/internacao/report/");
		
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		// params.put("nomeHospital",
		// "Hospital de Clínicas de Porto Alegre 121");
		params.put("dtInicial", "Relação de Baixas do Dia " + data.format(dataDeReferencia));
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "AINR_BAIXAS_DIA");

		return params;
	}

	public List<AghOrigemEventos> pesquisarUnidadeFuncionalPorCodigoEDescricao(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarOrigemEventoPorCodigoEDescricao(param);
	}
	
	public List<AipEtnia> pesquisarEtnias(String etnia){
		return this.pacienteFacade.pesquisarEtniaPorIDouDescricao(etnia);
	}

	public String voltar() {
		return RELATORIO_BAIXAS_DIA;
	}
	
	public void limparPesquisa(){
		this.setDataDeReferencia(new Date());
		this.setOrigemEvento(null);
		this.setGrupoConvenio(null);
		this.setExibeEtnia(false);
	}

	// GETTERS e SETTERS
	public Date getDataDeReferencia() {
		return dataDeReferencia;
	}

	public void setDataDeReferencia(Date dataDeReferencia) {
		this.dataDeReferencia = dataDeReferencia;
	}

	public DominioGrupoConvenio getGrupoConvenio() {
		return grupoConvenio;
	}

	public void setGrupoConvenio(DominioGrupoConvenio grupoConvenio) {
		this.grupoConvenio = grupoConvenio;
	}

	public AghOrigemEventos getOrigemEvento() {
		return origemEvento;
	}

	public void setOrigemEvento(AghOrigemEventos origemEvento) {
		this.origemEvento = origemEvento;
	}

	public List<BaixasDiaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<BaixasDiaVO> colecao) {
		this.colecao = colecao;
	}

	public List<BaixasDiaPorEtniaVO> getColecaoPorEtnia() {
		return colecaoPorEtnia;
	}

	public void setColecaoPorEtnia(List<BaixasDiaPorEtniaVO> colecaoPorEtnia) {
		this.colecaoPorEtnia = colecaoPorEtnia;
	}
	
	public void setEtniaPaciente(AipEtnia etniaPaciente) {
		this.etniaPaciente = etniaPaciente;
	}

	public AipEtnia getEtniaPaciente() {
		return etniaPaciente;
	}

	public void setExibeEtnia(boolean exibeEtnia) {
		this.exibeEtnia = exibeEtnia;
	}

	public boolean isExibeEtnia() {
		return exibeEtnia;
	}

}