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
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesComCirurgiaPorUnidadeVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import net.sf.jasperreports.engine.JRException;


public class RelatorioPacientesComCirurgiaPorUnidadeController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	private static final long serialVersionUID = -4292035220406165224L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private AghUnidadesFuncionais unidadeCirurgica;
	private LinhaReportVO unidadeInternacao;
	private Date dataCirurgia;
	private Collection<RelatorioPacientesComCirurgiaPorUnidadeVO> colecao;
	
	private String nomeMicrocomputador;
	
	private static final Log LOG = LogFactory.getLog(RelatorioPacientesComCirurgiaPorUnidadeController.class);
	

	private static final String RELATORIO_PAC_CIRURGIAS_UNIDADE_PDF = "relatorioPacientesComCirurgiaPorUnidadePdf";
	private static final String RELATORIO_PAC_CIRURGIAS_UNIDADE = "relatorioPacientesComCirurgiaPorUnidade";

		

	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}
	
	/**
	 * Método executado ao abrir a tela
	 */
	public void iniciar() {

		if (unidadeCirurgica == null) {
			try {
				
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error("Exceção capturada:", e);
				}
				
				unidadeCirurgica = blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);				
			}
		}
	}		
	
	
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false);
	}
	
	public List<LinhaReportVO> listarUnidadesFuncionaisPorUnidInternacao(final String strPesquisa) {
		return this.returnSGWithCount(this.blocoCirurgicoFacade.listarUnidadesFuncionaisPorUnidInternacao((String) strPesquisa,Boolean.TRUE),listarUnidadesFuncionaisPorUnidInternacaoCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidInternacaoCount(final String strPesquisa) {
		return this.blocoCirurgicoFacade.listarUnidadesFuncionaisPorUnidInternacaoCount((String) strPesquisa, Boolean.TRUE);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioPacientesComCirurgiaPorUnidade.jasper";
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = aghuFacade.getRazaoSocial();
		params.put("hospitalLocal", hospital);
		return params;
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioPacientesComCirurgiaPorUnidadeVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao; 
	}
	
	public void carregarColecao(){
		Short seqUnidadeInternacao = null;
		if(unidadeInternacao != null){
			seqUnidadeInternacao = unidadeInternacao.getNumero4();
		}
		colecao = blocoCirurgicoFacade.listarPacientesComCirurgia(unidadeCirurgica.getSeq(), seqUnidadeInternacao, dataCirurgia);
	}

	public String print()throws JRException, IOException, DocumentException, ApplicationBusinessException{
		carregarColecao();
		String retorno = null;
		if(colecao.isEmpty()){
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		}else{
			DocumentoJasper documento = gerarDocumento();
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));	
			retorno = RELATORIO_PAC_CIRURGIAS_UNIDADE_PDF;
		}			
		return retorno;
	}

	public void directPrint() throws ApplicationBusinessException {
		carregarColecao();
		if(colecao.isEmpty()){
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		}else{
			try {
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
				
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
			}
		}
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));			
	}
	
	public void limparCampos(){
		unidadeCirurgica = null;
		unidadeInternacao = null;
		dataCirurgia = null;
	}

	public String voltar(){
		return RELATORIO_PAC_CIRURGIAS_UNIDADE;
	}
	
	//Getters and Setters
	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setUnidadeInternacao(LinhaReportVO unidadeInternacao) {
		this.unidadeInternacao = unidadeInternacao;
	}

	public LinhaReportVO getUnidadeInternacao() {
		return unidadeInternacao;
	}

	public ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return cadastrosBasicosInternacaoFacade;
	}

	public void setCadastrosBasicosInternacaoFacade(
			ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade) {
		this.cadastrosBasicosInternacaoFacade = cadastrosBasicosInternacaoFacade;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public Collection<RelatorioPacientesComCirurgiaPorUnidadeVO> getColecao() {
		return colecao;
	}

	public void setColecao(
			Collection<RelatorioPacientesComCirurgiaPorUnidadeVO> colecao) {
		this.colecao = colecao;
	}
}
