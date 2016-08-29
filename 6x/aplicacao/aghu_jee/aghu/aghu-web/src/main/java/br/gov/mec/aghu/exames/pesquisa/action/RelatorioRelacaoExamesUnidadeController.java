package br.gov.mec.aghu.exames.pesquisa.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioRelacaoExamesUnidadeController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	private static final long serialVersionUID = -6384845357208890049L;

	private static final String PESQUISA_CADASTRO_EXAMES_UNIDADE = "exames-pesquisaCadastroExamesUnidade";
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB	
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	private Short seq;
	private String siglaPesquisa;
	private String material;
	private String nomeUsual;
	private DominioSituacao indSituacao;
	private String descricaoUnidadeExecutora;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void imprimirRelatorio() throws BaseException, JRException, SystemException, IOException {
		
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<VAelUnfExecutaExames> recuperarColecao() throws ApplicationBusinessException {
		
		String situacao;
		
		if (indSituacao != null ){
			situacao =  indSituacao.toString();
		}else{
			situacao =  "";
		}
		
		return examesFacade.pesquisarExamePorSeqUnidadeExecutora(seq, siglaPesquisa, material, nomeUsual, situacao);
	}

	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
		
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("tituloRelatorio", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());
		params.put("textoCabecalho", "Cadastro de Exames da " + getDescricaoUnidadeExecutora());
		params.put("nomeRelatorio", "AELR_PESQ_CAD_EXME");
		params.put("tituloExame", "Exame:");
		params.put("tituloMaterial", "Material:");
		params.put("tituloMotDesativacao", "Mot. Desativação:");
		params.put("tituloSituacao", "Situação:");
		
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioExamesUnidadeExecutora.jasper";
	}
	
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public String voltar(){
		return PESQUISA_CADASTRO_EXAMES_UNIDADE;
	}
	
	
	public String getSiglaPesquisa() {
		return siglaPesquisa;
	}

	public void setSiglaPesquisa(String siglaPesquisa) {
		this.siglaPesquisa = siglaPesquisa;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getNomeUsual() {
		return nomeUsual;
	}

	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public Short getSeq() {
		return seq;
	}

	public void setDescricaoUnidadeExecutora(String descricaoUnidadeExecutora) {
		this.descricaoUnidadeExecutora = descricaoUnidadeExecutora;
	}

	public String getDescricaoUnidadeExecutora() {
		return descricaoUnidadeExecutora;
	}
}