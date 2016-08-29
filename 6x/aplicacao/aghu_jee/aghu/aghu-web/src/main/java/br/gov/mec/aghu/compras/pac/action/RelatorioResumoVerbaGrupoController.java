package br.gov.mec.aghu.compras.pac.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.RelatorioResumoVerbaGrupoVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



public class RelatorioResumoVerbaGrupoController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}


	private static final Log LOG = LogFactory.getLog(RelatorioResumoVerbaGrupoController.class);
	
	private static final long serialVersionUID = -7636003387294048548L;

	private static final String RELATORIO_RESUMO_VERBA_GRUPO = "compras-relatorioResumoVerbaGrupo";

	private static final String RELATORIO_RESUMO_VERBA_GRUPO_PDF = "relatorioResumoVerbaGrupoPdf";
	
	private List<RelatorioResumoVerbaGrupoVO> dadosRelatorio = new ArrayList<RelatorioResumoVerbaGrupoVO>();

	private enum RelatorioResumoVerbasGruposControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_DADOS, MENSAGEM_SUCESSO_IMPRESSAO;
	}
	
	public enum EnumTargetRelatorioEspelhoLicitacao{
		LABEL_RELATORIO_RESUMO_VERBAS_GRUPOS,
		LABEL_MENSAGEM_RELATORIO_RELATORIO_RESUMO_VERBAS_GRUPOS;
	}
	
	
	// Variaveis da pesquisa
	private Integer numLicitacao;	
	
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public String recuperarArquivoRelatorio(){
		return "br/gov/mec/aghu/compras/report/relatorioResumoVerbasGrupos.jasper";
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioResumoVerbaGrupoVO> recuperarColecao() throws ApplicationBusinessException {
		return getDadosRelatorio();
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String subRelatorioCompras = "br/gov/mec/aghu/compras/report/subResumoVerbasGruposCompras.jasper";
		String subRelatorioServicos = "br/gov/mec/aghu/compras/report/subResumoVerbasGruposServicos.jasper";
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal().toUpperCase();
		String relatorio = super.getBundle().getString(EnumTargetRelatorioEspelhoLicitacao.LABEL_RELATORIO_RESUMO_VERBAS_GRUPOS.toString());
		String cidade = aghuFacade.getCidade();
		String razaoSocial = aghuFacade.getRazaoSocial();
		String endereco = aghuFacade.getEndereco();
		
		String mensagemRelatorioResumoVerba = super.getBundle().getString(EnumTargetRelatorioEspelhoLicitacao.LABEL_MENSAGEM_RELATORIO_RELATORIO_RESUMO_VERBAS_GRUPOS.toString());
		
		mensagemRelatorioResumoVerba = mensagemRelatorioResumoVerba.replace("{0}", this.getNumLicitacao().toString());
		
		params.put("nomeInstituicao", hospital);
		params.put("nomeRelatorio", relatorio);
		params.put("razaoSocial", razaoSocial);
		params.put("endereco", endereco);
		params.put("mensagem", mensagemRelatorioResumoVerba);
		params.put("cidade", cidade);
		params.put("subRelatorioCompras", Thread.currentThread().getContextClassLoader().getResourceAsStream(subRelatorioCompras));
		params.put("subRelatorioServicos", Thread.currentThread().getContextClassLoader().getResourceAsStream(subRelatorioServicos));
		
		try {
			params.put("cep", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_CEP));
			params.put("caminhoLogo", super.recuperarCaminhoLogo());
			params.put("cidadeUf", cidade+"-"+parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_UF_SEDE_HU));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return params;
	}

	/**
	 * Método que carrega a lista de VO's para ser usado no relatório.
	 * 
	 */
	public String print()throws JRException, IOException, DocumentException, ApplicationBusinessException{
		String retorno = null,
			   mensagem = null;
		
		if(getNumLicitacao()!=null){
			setDadosRelatorio(new ArrayList<RelatorioResumoVerbaGrupoVO>());
			RelatorioResumoVerbaGrupoVO vo = pacFacade.obterDadosRelatorioVerbaGrupo(numLicitacao);
			if(vo.getNumero() != null){
				dadosRelatorio.add(vo);
			}
			
			if(getDadosRelatorio() == null || getDadosRelatorio().isEmpty()){
				mensagem = super.getBundle().getString((RelatorioResumoVerbasGruposControllerExceptionCode.MENSAGEM_PESQUISA_SEM_DADOS.toString()));
				apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
			} else{
			
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_RESUMO_VERBA_GRUPO_PDF;
			}
		}
		return retorno;
	}
	
	/**
	 * Realiza a impressão do relatório
	 */
	public void impressaoDireta(){
		try {
			if(print() != null) {
				DocumentoJasper documento = gerarDocumento();
				getSistemaImpressao().imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				this.apresentarMsgNegocio(Severity.INFO, RelatorioResumoVerbasGruposControllerExceptionCode.MENSAGEM_SUCESSO_IMPRESSAO.toString());
			}				
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			
		}catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
		}
	}

	public String voltar(){
		return RELATORIO_RESUMO_VERBA_GRUPO;
	}
	
	/**
	 * Renderiza o PDF.
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public void limparCampos() {
		setNumLicitacao(null);
	}

	public Integer getNumLicitacao() {
		return numLicitacao;
	}

	public void setNumLicitacao(Integer numLicitacao) {
		this.numLicitacao = numLicitacao;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public List<RelatorioResumoVerbaGrupoVO> getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(
			List<RelatorioResumoVerbaGrupoVO> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}
}