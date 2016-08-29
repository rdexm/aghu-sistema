package br.gov.mec.aghu.ambulatorio.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioImprimirTicketRetornoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class ImprimeTicketRetornoController extends ActionReport{


	private static final long serialVersionUID = 8487543208111903447L;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	private String nomeHospital;
	private String descricao;
	private String identificacao;
	private String nomeMedico;
	
	private StreamedContent media;

	private List<RelatorioImprimirTicketRetornoVO> colecao = new ArrayList<RelatorioImprimirTicketRetornoVO>();
	private Boolean mensagem = false;
	
	@Override
	protected Collection<RelatorioImprimirTicketRetornoVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	@Override
	protected void directPrint(){
		
		RelatorioImprimirTicketRetornoVO retornoVO = new RelatorioImprimirTicketRetornoVO();
		retornoVO.setNomeHospital(nomeHospital);
		retornoVO.setDescricao(descricao);
		retornoVO.setIdentificacao(identificacao);
		retornoVO.setNomeMedico(nomeMedico);
		this.colecao.add(retornoVO);

		try {
			DocumentoJasper documento = gerarDocumento();
//			this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(true)));
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			if(!mensagem){
				mensagem = true;
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Solicitação Retorno");
				this.colecao.clear();
			}
		} catch (SistemaImpressaoException e) {
			this.colecao.clear();
			this.apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.colecao.clear();
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		} 
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioImprimirTicketsRetorno.jasper";
	}
	
	@Override
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {

		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public String getNomeHospital() {
		return nomeHospital;
	}

	public void setNomeHospital(String nomeHospital) {
		this.nomeHospital = nomeHospital;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getIdentificacao() {
		return identificacao;
	}

	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}

	public String getNomeMedico() {
		return nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	public Boolean getMensagem() {
		return mensagem;
	}

	public void setMensagem(Boolean mensagem) {
		this.mensagem = mensagem;
	}
}
