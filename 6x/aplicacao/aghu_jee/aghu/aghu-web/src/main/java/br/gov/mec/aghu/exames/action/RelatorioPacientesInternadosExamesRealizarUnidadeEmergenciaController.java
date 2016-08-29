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

public class RelatorioPacientesInternadosExamesRealizarUnidadeEmergenciaController  extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	private Boolean impressao;

	private static final long serialVersionUID = -6272055445644378427L;
	
	private final static String ARQUIVO_JASPER = "br/gov/mec/aghu/exames/report/relatorioPacientesInternadosExamesRealizarUnidadeEmergencia.jasper";
	private final static String TITULO_RELATORIO = "Relação de Pacientes Internados com exames a realizar (Emergência)";

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
		Map<String, Object> params = new HashMap<String, Object>();
		
		Date dataAtual = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		String hospital = this.cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		
		// Seta parametros
		params.put("dataAtual", simpleDateFormat.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", DominioNomeRelatorio.AELR_EXME_A_REAL_EME.toString());
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
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {
		try {
			if(impressao && this.colecao.isEmpty()){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ARQUIVO_IMPRESSAO_EM_BRANCO", "Pacientes de Unidade de Emergência");
				return;
			}

			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
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