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

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasIndicacaoExamesVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class RelatorioCirurgiasIndicacaoExamesController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}



	private static final long serialVersionUID = -2455280241079228998L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IAghuFacade aghuFacade;	
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	//filtros
	private AghUnidadesFuncionais unidadeCirurgica;	
	private Date dataCirurgia;
	private AghUnidadesFuncionais unidadeExecutora;
	private Boolean cirgComSolicitacao = Boolean.FALSE;
	
	private List<RelatorioCirurgiasIndicacaoExamesVO> colecao;
	
	private String nomeMicrocomputador;

    private static final Log LOG = LogFactory.getLog(RelatorioCirurgiasIndicacaoExamesController.class);
	
	
	private static final String RELATORIO_CIR_INDI_EXAMES_PDF = "relatorioCirurgiasIndicacaoExamesPdf";
	private static final String RELATORIO_CIR_INDI_EXAMES = "relatorioCirurgiasIndicacaoExames";
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
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
	
	public String print()throws JRException, IOException, DocumentException{
		try {
			carregarColecao();
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));			
			
		return RELATORIO_CIR_INDI_EXAMES_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}		
	}
	
	public void directPrint(){
		
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
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();			
		
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
		params.put("unidadeCirurgica", unidadeCirurgica.getDescricao());
		params.put("unidadeExecutora", unidadeExecutora.getDescricao());
		
		String escala = this.blocoCirurgicoFacade.cfESCALAFormula(unidadeCirurgica.getSeq(), dataCirurgia);
		params.put("escala", escala);
		
		params.put("SUBREPORT_DIR","br/gov/mec/aghu/blococirurgico/report/");  
		
		try {			
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("hospitalLocal", parametroRazaoSocial.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
		return params;
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioCirurgiasIndicacaoExamesVO> recuperarColecao() throws ApplicationBusinessException {		
		return colecao; 
	}
	
	public void carregarColecao() throws ApplicationBusinessException{		
			colecao = blocoCirurgicoFacade.recuperarRelatorioCirurgiasIndicacaoExames(unidadeCirurgica.getSeq(), unidadeExecutora.getSeq(), dataCirurgia, cirgComSolicitacao);			
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));			
	}
	
	public void limparCampos(){
		unidadeCirurgica = null;		
		dataCirurgia = null;
		cirgComSolicitacao = Boolean.FALSE;
		unidadeExecutora = null;
	}
	
	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		return RELATORIO_CIR_INDI_EXAMES;
	}
	
	/**
	 * Obtem unidade funcional ativa executora de cirurgias
	 */
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false);
	}
	
	/**
	 * Obtem unidade funcional ativa executora de exames
	 */
	public List<AghUnidadesFuncionais> obterUnidadeExecutora(String filtro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadeFuncionalPorSeqDescricao((String)filtro, false),obterUnidadeExecutoraCount(filtro));
	}
	
	public Long obterUnidadeExecutoraCount(String filtro) {
        return this.aghuFacade.pesquisarUnidadeFuncionalPorSeqDescricaoCount((String)filtro, false);
    }
		
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioCirurgiasIndicacaoExames.jasper";
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

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public List<RelatorioCirurgiasIndicacaoExamesVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioCirurgiasIndicacaoExamesVO> colecao) {
		this.colecao = colecao;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public Boolean getCirgComSolicitacao() {
		return cirgComSolicitacao;
	}

	public void setCirgComSolicitacao(Boolean cirgComSolicitacao) {
		this.cirgComSolicitacao = cirgComSolicitacao;
	}
		
}
