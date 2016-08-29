package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioEtiquetasIdentificacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.VAghUnidFuncionalVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoEtiqueta;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import net.sf.jasperreports.engine.JRException;

/**
 * #27200 - Impressão das etiquetas de identificação relacionada ao paciente
 * 
 * @author jback
 * 
 */

public class RelatorioEtiquetasIdentificacaoController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}


	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioEtiquetasIdentificacaoController.class);

	private static final long serialVersionUID = 433052708282290129L;
	private final static ConstanteAghCaractUnidFuncionais[] CARACTERISTICAS_UNIDADE_FUNCIONAL = {ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS};

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	private AghUnidadesFuncionais unidade;
	private VAghUnidFuncionalVO unidadeMae;
	private Boolean previsaoAlta = true;
	private AipPacientes paciente;
	private Integer prontuario;
	private Integer pacCodigoFonetica;
	private String nomePaciente;
	private Date dataCirurgia;
	private String voltarPara;
	private List<RelatorioEtiquetasIdentificacaoVO> colecao;
	private RelatorioEtiquetasIdentificacaoVO relatorioEtiquetasIdentificacaoVO;
	private String exibirCamposFixos;
	
	private final String PAGE_RELATORIO_ETIQUETAS_IDENTIFICACAO = "relatorioEtiquetasIdentificacao";
	private final String PAGE_RELATORIO_ETIQUETAS_MISTAS_PDF = "relatorioEtiquetasMistasPdf";
	private final String PAGE_RELATORIO_ETIQUETAS_IDENTIFICACAO_PDF = "relatorioEtiquetasIdentificacaoPdf";
	private final String PAGE_RELATORIO_ETIQUETAS_ITEM_PACIENTE_PDF = "relatorioEtiquetasItensPacientePdf";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";

	public void inicio() {
	 

	 

		if (this.pacCodigoFonetica != null) {
			this.setPaciente(this.pacienteFacade.obterPaciente(this.pacCodigoFonetica));
			this.prontuario = this.paciente.getProntuario();
		}
	
	}
	

	public List<AghUnidadesFuncionais> listarUnidadesFuncionais(final String param) {
		return this.returnSGWithCount(aghuFacade.pesquisarUnidadeFuncionalPorSeqAndarAlaDescricao((String) param),listarUnidadesFuncionaisCount(param));
	}

	public Integer listarUnidadesFuncionaisCount(final String param) {
		return aghuFacade.pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoCount((String) param);
	}

	public List<VAghUnidFuncionalVO> listarUnidadesFuncionaisMae(final String param) {
		return aghuFacade.listarUnidadesFuncionaisMae((String) param);
	}

	public String redirecionarPesquisaFonetica() {
		return PESQUISA_FONETICA;
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			if (paciente != null){
				this.pacCodigoFonetica = paciente.getCodigo();
			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void directPrint() throws ApplicationBusinessException, SistemaImpressaoException {

		DocumentoJasper documento = gerarDocumento();
		//OpcoesImpressao noMargins = new OpcoesImpressao(true, false);
		try {
			sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		} catch (JRException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

	}
	
	private boolean verificaSeUnidadePossuiCarateristicaCirurgica(){
		if(unidade != null){
			return aghuFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristica( unidade.getSeq().toString()
					, CARACTERISTICAS_UNIDADE_FUNCIONAL, DominioSituacao.A, Boolean.FALSE).size() > 0 ;
		}
		return false;
	}

	// Etiqueta Folha A4 para medicamento
	public String printMedicamento() throws ApplicationBusinessException, JRException, IOException, DocumentException{
		
		try {
			boolean retorno = this.verificaSeUnidadePossuiCarateristicaCirurgica();
			
			blocoCirurgicoFacade.preImprimirValidarUnidadeFuncionalProntuarioUnidadeFuncionalMae(unidade, pacCodigoFonetica, unidadeMae, dataCirurgia, retorno);
			
			setExibirCamposFixos(DominioTipoEtiqueta.E.name());
			
			this.carregarColecao();
			
			if (colecao == null || colecao.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return null;
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream((documento.getPdfByteArray(Boolean.FALSE))));
		
		return PAGE_RELATORIO_ETIQUETAS_IDENTIFICACAO_PDF;

	}
	
	public String directPrinEtiquetasMista() {
		try {
			directPrint();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		} 
		return null;
	}
	
	public String printEtiquetasMistas() {

		try{ 
			boolean retorno = this.verificaSeUnidadePossuiCarateristicaCirurgica();
			
			blocoCirurgicoFacade.preImprimirValidarUnidadeFuncionalProntuarioUnidadeFuncionalMae(unidade, pacCodigoFonetica, unidadeMae, dataCirurgia, retorno);

			setExibirCamposFixos(DominioTipoEtiqueta.M.name()); // Etiqueta Folha A4 mista
			
			carregarColecao();
			
			if (colecao == null || colecao.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return null;
			} 
		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		return PAGE_RELATORIO_ETIQUETAS_MISTAS_PDF;
	}
	
	public String directPrintMedicamento() {
		try {
			directPrint();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		} 

		return null;
	}

	// Etiqueta Folha A4 identificação itens do paciente
	public String printItensPaciente(){
	
		try{	
			boolean retorno = this.verificaSeUnidadePossuiCarateristicaCirurgica();
			
			blocoCirurgicoFacade.preImprimirValidarUnidadeFuncionalProntuarioUnidadeFuncionalMae(unidade, pacCodigoFonetica, unidadeMae, dataCirurgia, retorno);
			
			setExibirCamposFixos(DominioTipoEtiqueta.I.name());
			
			this.carregarColecao();
			if (colecao == null || colecao.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return null;
			}
		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		return PAGE_RELATORIO_ETIQUETAS_ITEM_PACIENTE_PDF;
		
	}
	
	public String directPrintItensPaciente() {
		try {
			directPrint();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		} 

		return null;
	}

	
	public String voltar(){
		return PAGE_RELATORIO_ETIQUETAS_IDENTIFICACAO;
	}

	public String voltarCensoDiario(){
		if (voltarPara != null && !voltarPara.isEmpty()){
			return voltarPara;
		} else {
			return PAGE_RELATORIO_ETIQUETAS_IDENTIFICACAO;
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("P_EXIBIR_CAMPOS_FIXOS", exibirCamposFixos);

		return params;
	}

	@Override
	public Collection<RelatorioEtiquetasIdentificacaoVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}

	public void carregarColecao() throws ApplicationBusinessException {
		Short unfSeq = (unidade != null) ? unidade.getSeq() : null;
		Boolean pacientesQueNaoPossuemPrevisaoAlta = previsaoAlta;
		Short unfSeqMae = (unidadeMae != null) ? unidadeMae.getSeq() : null;
		colecao = blocoCirurgicoFacade.pesquisarRelatorioEtiquetasIdentificacao(unfSeq, unfSeqMae, pacientesQueNaoPossuemPrevisaoAlta, pacCodigoFonetica, dataCirurgia);
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.FALSE));
	}

	public void limparCampos() {
		this.unidade = null;
		this.unidadeMae = null;
		this.previsaoAlta = true;
		this.paciente = null;
		this.prontuario = null;
		this.pacCodigoFonetica = null;
		this.dataCirurgia = null;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioEtiquetasIdentificacao.jasper";
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}
	
	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}

	public void setUnidade(AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}

	public VAghUnidFuncionalVO getUnidadeMae() {
		return unidadeMae;
	}

	public void setUnidadeMae(VAghUnidFuncionalVO unidadeMae) {
		this.unidadeMae = unidadeMae;
	}

	public Boolean getPrevisaoAlta() {
		return previsaoAlta;
	}

	public void setPrevisaoAlta(Boolean previsaoAlta) {
		this.previsaoAlta = previsaoAlta;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
		if (paciente != null && StringUtils.isNotBlank(paciente.getNome())) {
			this.nomePaciente = paciente.getNome();
			this.pacCodigoFonetica = paciente.getCodigo();
			this.prontuario = paciente.getProntuario();
		} else {
			this.nomePaciente = null;
			this.pacCodigoFonetica = null;
			this.prontuario = null;
		}
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public List<RelatorioEtiquetasIdentificacaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioEtiquetasIdentificacaoVO> colecao) {
		this.colecao = colecao;
	}

	public RelatorioEtiquetasIdentificacaoVO getRelatorioEtiquetasIdentificacaoVO() {
		return relatorioEtiquetasIdentificacaoVO;
	}

	public void setRelatorioEtiquetasIdentificacaoVO(RelatorioEtiquetasIdentificacaoVO relatorioEtiquetasIdentificacaoVO) {
		this.relatorioEtiquetasIdentificacaoVO = relatorioEtiquetasIdentificacaoVO;
	}

	public String getExibirCamposFixos() {
		return exibirCamposFixos;
	}

	public void setExibirCamposFixos(String exibirCamposFixos) {
		this.exibirCamposFixos = exibirCamposFixos;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

}
