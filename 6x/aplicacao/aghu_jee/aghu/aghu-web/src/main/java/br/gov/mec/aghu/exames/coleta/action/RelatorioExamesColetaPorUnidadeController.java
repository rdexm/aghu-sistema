package br.gov.mec.aghu.exames.coleta.action;

import java.io.IOException;
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

import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.business.IColetaExamesFacade;
import br.gov.mec.aghu.exames.coleta.vo.RelatorioExameColetaPorUnidadeVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import net.sf.jasperreports.engine.JRException;


public class RelatorioExamesColetaPorUnidadeController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -6034758665975842999L;

	private static final String RELATORIO_EXAMES_COLETA_POR_UNIDADE = "relatorioExamesColetaPorUnidade";
	private static final String RELATORIO_EXAMES_COLETA_POR_UNIDADE_PDF = "relatorioExamesColetaPorUnidadePdf";

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IColetaExamesFacade coletaExamesFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	private AelUnidExecUsuario usuarioUnidadeExecutora;

	private AghUnidadesFuncionais unidadeExecutora;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if(unidadeExecutora==null) {
			// Obtem o usuario da unidade executora
			try {
				this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
			} catch (ApplicationBusinessException e) {
				usuarioUnidadeExecutora=null;
			}
	
			// Resgata a unidade executora associada ao usuario
			if (this.usuarioUnidadeExecutora != null) {
				this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
			}
		}
	
	}
	
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioExameColetaPorUnidadeVO> colecao = new ArrayList<RelatorioExameColetaPorUnidadeVO>();
	
	/**
	 * Método responsável pela visualização do relatório.
	 */
	public String visualizarImpressao() throws BaseException, JRException, SystemException, IOException {
		try {
			this.colecao = coletaExamesFacade.obterExamesColetaPorUnidade(unidadeExecutora);
		} catch (Exception e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
			return null;
		}
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_EXAME_COLETA_UNIDADE_VAZIA");
			return null;
		}
		return RELATORIO_EXAMES_COLETA_POR_UNIDADE_PDF;
	}

	public String voltar(){
		return RELATORIO_EXAMES_COLETA_POR_UNIDADE;
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			this.colecao = coletaExamesFacade.obterExamesColetaPorUnidade(unidadeExecutora);

		} catch (Exception e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			return;
		}
		
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_EXAME_COLETA_UNIDADE_VAZIA");
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Exames em Coleta Por Unidade");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<RelatorioExameColetaPorUnidadeVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	public List<RelatorioExameColetaPorUnidadeVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioExameColetaPorUnidadeVO> colecao) {
		this.colecao = colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_EM_COLETA");
		params.put("qtdRegistros", colecao.size());
		params.put("tituloRelatorio", "Exames em coleta pela unid."+unidadeExecutora.getSeq()+" - "+ unidadeExecutora.getDescricao() 
				+ " - " + unidadeExecutora.getAndar() + " " + unidadeExecutora.getIndAla());
		params.put("SUBREPORT_DIR","/br/gov/mec/aghu/exames/coleta/report/");

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/coleta/report/relatorioExamesColetaPorUnidade.jasper";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	public void limparPesquisa(){
		this.unidadeExecutora = null;
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String parametro) {
		return this.aghuFacade.obterUnidadesFuncionais(parametro);
	}
	
	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}
			
}
