package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
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
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.FichaPreOperatoriaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class FichaPreOperatoriaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8063424345080116858L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private AghUnidadesFuncionais unidadeCirurgica;
	private LinhaReportVO unidadeInternacao;
	private Date dataCirurgia;
	private Integer prontuario;
	private Integer qtdeCopia = 1;
	private Collection<FichaPreOperatoriaVO> colecao;
	
	private String nomeMicrocomputador;
	
	private static final Log LOG = LogFactory.getLog(FichaPreOperatoriaController.class);
	
	private static final String FICHA = "fichaPreOperatoria";
	private static final String FICHA_PDF = "fichaPreOperatoriaPdf";
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio(){
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
		qtdCopiasValorPadrao();
	}

	public void qtdCopiasValorPadrao() {
		qtdeCopia = 1;
	}
	
	public String voltar() {
		return FICHA;
	}

	
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa,ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false);
	}
	
	public List<LinhaReportVO> listarUnidadesFuncionaisPorUnidInternacao(final String strPesquisa) {
		return this.returnSGWithCount(this.blocoCirurgicoFacade.listarUnidadesFuncionaisPorUnidInternacao((String) strPesquisa, Boolean.TRUE),listarUnidadesFuncionaisPorUnidInternacaoCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidInternacaoCount(final String strPesquisa) {
		return this.blocoCirurgicoFacade.listarUnidadesFuncionaisPorUnidInternacaoCount((String) strPesquisa, Boolean.TRUE);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/fichaPreOperatoria.jasper";
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("dataAtual", new Date());
		params.put("SUBREPORT_DIR","br/gov/mec/aghu/blococirurgico/report/");
		
		try {
			String caminhoLogo = recuperarCaminhoLogo();
			params.put("caminhoLogo", caminhoLogo);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório", e);
		}
			
		return params;
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<FichaPreOperatoriaVO> recuperarColecao() throws ApplicationBusinessException {
		if(qtdeCopia != null){//entao copias em branco
			return Arrays.asList(new FichaPreOperatoriaVO(unidadeCirurgica.getDescricao()));
		}
		return colecao; 
	}
	
	public void carregarColecao() throws ApplicationBusinessException {
		Short seqUnidadeInternacao = null;
		if(unidadeInternacao != null){
			seqUnidadeInternacao = unidadeInternacao.getNumero4();
		}
		colecao = blocoCirurgicoFacade.listarProcedimentoPorCirurgia(unidadeCirurgica.getSeq(), unidadeCirurgica.getDescricao(), seqUnidadeInternacao, dataCirurgia, prontuario);
		qtdeCopia = null;
	}

	public String print()throws JRException, IOException, DocumentException{
		try {
			carregarColecao();
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));			
		return FICHA_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public void directPrint() {
		
		try {
			carregarColecao();
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
	}
	
	public void imprimirFichaEmBranco() throws ApplicationBusinessException, JRException, SystemException, IOException {
			
		try {
				DocumentoJasper documento = gerarDocumento();
				JasperPrint impressao = documento.getJasperPrint();
				for(int i =0; i<qtdeCopia;i++){
					this.sistemaImpressao.imprimir(impressao,super.getEnderecoIPv4HostRemoto());
				}
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
				qtdeCopia = null;
				
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
			}		
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));			
	}
	
	public void limparCampos(){
		unidadeCirurgica = null;
		unidadeInternacao = null;
		dataCirurgia = null;
		prontuario = null;
		qtdeCopia = null;
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

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getProntuario() {
		return prontuario;
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

	public Collection<FichaPreOperatoriaVO> getColecao() {
		return colecao;
	}

	public void setColecao(
			Collection<FichaPreOperatoriaVO> colecao) {
		this.colecao = colecao;
	}

	public Integer getQtdeCopia() {
		return qtdeCopia;
	}

	public void setQtdeCopia(Integer qtdeCopia) {
		this.qtdeCopia = qtdeCopia;
	}
}
