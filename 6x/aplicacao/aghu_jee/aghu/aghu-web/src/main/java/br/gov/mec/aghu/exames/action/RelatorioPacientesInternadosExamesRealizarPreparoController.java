package br.gov.mec.aghu.exames.action;

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

import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.exames.vo.RelatorioPacientesInternadosExamesRealizarDadosVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import net.sf.jasperreports.engine.JRException;

public class RelatorioPacientesInternadosExamesRealizarPreparoController  extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	private Boolean impressao;

	private static final long serialVersionUID = 2848170418132768250L;
	private final static String ARQUIVO_JASPER = "br/gov/mec/aghu/exames/report/relatorioPacientesInternadosExamesRealizarPreparo.jasper";
	private final static String TITULO_RELATORIO = "Relação de Pacientes Internados com exames a realizar (Preparo)";

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	// Dados que serao impressos em PDF
	private List<RelatorioPacientesInternadosExamesRealizarDadosVO> colecao = new ArrayList<RelatorioPacientesInternadosExamesRealizarDadosVO>();
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public Collection<RelatorioPacientesInternadosExamesRealizarDadosVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		// Instancia mapa de parametros do relatorio
		Map<String, Object> params = new HashMap<String, Object>();

		// Popula parametro da data atual
		Date dataAtual = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		// Popula parametro com o nome da instituicao hospitalar
		String hospital = this.cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		// Seta parametros
		params.put("dataAtual", simpleDateFormat.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", DominioNomeRelatorio.AELR_EXM_A_REAL_PREP.toString());
		params.put("tituloRelatorio", TITULO_RELATORIO);
		if(this.colecao != null){
			params.put("totalRegistros", this.colecao.size()-1);
		}

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return ARQUIVO_JASPER;
	}

	@Override
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {
		try {
			if(impressao && this.colecao.isEmpty()){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ARQUIVO_IMPRESSAO_EM_BRANCO");
				return;
			}
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public List<RelatorioPacientesInternadosExamesRealizarDadosVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioPacientesInternadosExamesRealizarDadosVO> colecao) {
		this.colecao = colecao;
	}

	public Boolean getImpressao() {
		return impressao;
	}

	public void setImpressao(Boolean impressao) {
		this.impressao = impressao;
	}
}