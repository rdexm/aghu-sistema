package br.gov.mec.aghu.paciente.action;

import java.io.IOException;
import java.text.ParseException;
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

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioStatusRelatorio;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.EscalaCirurgiasVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

/**
 * Controller de geração do relatório Escala Cirurgias
 * 
 * @author lalegre
 */
public class RelatorioEscalaCirurgiasController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 498438170712057663L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioEscalaCirurgiasController.class);
	private static final String RELATORIO_ESCALA_CIRURGIAS = "relatorioEscalaCirurgias";
	private static final String RELATORIO_ESCALA_CIRURGIAS_PDF = "relatorioEscalaCirurgiasPdf";



	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	
	private AghUnidadesFuncionais unidadesFuncionais;
	private AipPacientes paciente;
	private AghParametros aghParametro;
	private DominioStatusRelatorio status;
	private Date dataCirurgia;
	private String titulo;
	private List<EscalaCirurgiasVO> colecao;
	
	
	@PostConstruct
	protected void init(){
		begin(conversation);
		colecao = new ArrayList<EscalaCirurgiasVO>(0);
		this.setStatus(DominioStatusRelatorio.P);
	}
	
	/**
	 * Impressao direta via CUPS.
	 */
	public void impressaoDireta() throws ApplicationBusinessException, JRException, SystemException,
			IOException, ParseException {

		if(this.print() == null) {
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	
	}

	/**
	 * Metodo responsavel pela geracao e visualizacao do relatorio.
	 */
	public String print() throws ApplicationBusinessException, JRException, SystemException, IOException {
		
		try {
			
			this.colecao = blocoCirurgicoFacade.pesquisaEscalaCirurgia(unidadesFuncionais, dataCirurgia, status);
			
			if (colecao.size() > 0) {
				titulo = colecao.get(colecao.size() - 1).getTitulo();
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		return RELATORIO_ESCALA_CIRURGIAS_PDF;
	}

	@Override
	public Collection<EscalaCirurgiasVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/report/relatorioEscalaCirurgias.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "MBCR_ESCALA_SIMPLES");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("unidade", unidadesFuncionais.getDescricao());
		params.put("escala", titulo);
		return params;
	}

	/**
	 * Método que alimenta a suggestion de Unidade Funcionais (Unidade).
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(String strPesquisa) {
		return pacienteFacade.pesquisarUnidadesFuncionaisPorCodigoDescricao(strPesquisa);
	}


	/**
	 * Método invocado ao usuário cancelar impressão.
	 */
	public void actionCancel() {
		try {
			cadastrosBasicosInternacaoFacade.cancelarImpressao();
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.WARN, e.getLocalizedMessage());
		}
	}
	
	/**
	 * Método Limpar pesquisa.
	 */
	public void limparPesquisa(){
		this.setUnidadesFuncionais(null);
		this.setStatus(null);
		this.setDataCirurgia(null);
	}
	
	/**
	 * Método voltar da tela pdf.
	 */
	public String voltar(){
		return RELATORIO_ESCALA_CIRURGIAS;
	}
	
	// GETs AND SETs

	public AghUnidadesFuncionais getUnidadesFuncionais() {
		return unidadesFuncionais;
	}

	public void setUnidadesFuncionais(AghUnidadesFuncionais unidadesFuncionais) {
		this.unidadesFuncionais = unidadesFuncionais;
	}

	public boolean isMostrarLinkExcluirUnidades() {
		return this.getUnidadesFuncionais() != null;
	}

	public void limparUnidades() {
		this.unidadesFuncionais = null;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public DominioStatusRelatorio getStatus() {
		return status;
	}

	public void setStatus(DominioStatusRelatorio status) {
		this.status = status;
	}
	
	public AghParametros getAghParametro() {
		return aghParametro;
	}

	public void setAghParametro(AghParametros aghParametro) {
		this.aghParametro = aghParametro;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
}
