package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
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
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioPacientesEmSalaRecuperacaoPorUnidadeController extends
		ActionReport	 {

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
	private static final long serialVersionUID = 4150483598287427933L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IAghuFacade aghuFacade;

	private AghUnidadesFuncionais unidadeCirurgica;
	private String ordemListagem;
	private Collection<RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO> colecao;
	
	
	private String nomeMicrocomputador;
	
	private static final Log LOG = LogFactory.getLog(RelatorioPacientesEmSalaRecuperacaoPorUnidadeController.class);

	
	
	private static final String RELATORIO_PAC_SALA_RECUP_UNIDADE_PDF = "relatorioPacientesEmSalaRecuperacaoPorUnidadePdf";
	private static final String RELATORIO_PAC_SALA_RECUP_UNIDADE = "relatorioPacientesEmSalaRecuperacaoPorUnidade";

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
	

	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(
			final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(
			final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioPacientesEmSalaRecuperacaoPorUnidade.jasper";
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade
				.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		return params;
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO> recuperarColecao()
			throws ApplicationBusinessException {
		return colecao;
	}
	
	public void carregarColecao() throws ApplicationBusinessException{
		Short seqUnidadeCirurgica = null;
		if(unidadeCirurgica != null){
			seqUnidadeCirurgica = unidadeCirurgica.getSeq();
		}
		colecao = blocoCirurgicoFacade.listarPacientesEmSalaRecuperacaoPorUnidade(seqUnidadeCirurgica, ordemListagem);
	}

	public String print()throws JRException, IOException, DocumentException , ApplicationBusinessException {
		carregarColecao();
		String retorno = null;
		if (colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		} else {
			retorno = RELATORIO_PAC_SALA_RECUP_UNIDADE_PDF;
		}
	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));
		return retorno;
	}

	public void directPrint() throws ApplicationBusinessException {
		carregarColecao();
		if (colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		} else {
			try {
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(),
						super.getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_IMPRESSAO");

			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				apresentarMsgNegocio(Severity.ERROR,
						"ERRO_GERAR_RELATORIO");
			}
		}
	}

	public StreamedContent getRenderPdf() throws IOException,
			JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}

	public void limparCampos() {
		unidadeCirurgica = null;
		ordemListagem = null;
	}

	
	public String voltar(){    	
	    return RELATORIO_PAC_SALA_RECUP_UNIDADE;
	    	
	}

	// Getters and Setters
	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setBlocoCirurgicoFacade(
			IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setOrdemListagem(String ordemListagem) {
		this.ordemListagem = ordemListagem;
	}

	public String getOrdemListagem() {
		return ordemListagem;
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

	public Collection<RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO> getColecao() {
		return colecao;
	}

	public void setColecao(
			Collection<RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO> colecao) {
		this.colecao = colecao;
	}
}
