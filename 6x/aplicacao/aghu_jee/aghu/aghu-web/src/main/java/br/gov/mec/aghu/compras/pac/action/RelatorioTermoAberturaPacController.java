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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.ScoLicitacao;
import net.sf.jasperreports.engine.JRException;


public class RelatorioTermoAberturaPacController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}


	private static final long serialVersionUID = -5005769024181822853L;
	
	private static final String PAGE_IMPRIMIR_TERMO_ABERTURA_PAC = "compras-imprimirTermoAberturaPac";

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;
		
	@EJB
	private IPacFacade pacFacade;
	
	//armazena dados do relatório
	private List<ScoLicitacao> dados = null;	
	
	private List<ScoLicitacao> listaLicitacoes  = new ArrayList<ScoLicitacao>();
	
	private ScoLicitacao pac;
		
	// indica para onde o botao voltar deve redirecionar
	private String voltarParaUrl;

	private String remetente;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String voltar() {
		listaLicitacoes.clear();
		return voltarParaUrl;
	}

	
	
	
	
	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print(Integer numPac) throws BaseException, JRException, SystemException, IOException, DocumentException {
		
		pac = this.pacFacade.obterLicitacao(numPac);
		listaLicitacoes.add(pac);
		
		setDados(listaLicitacoes);

		if (getDados() == null || getDados().isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return null;
		}
	
		return PAGE_IMPRIMIR_TERMO_ABERTURA_PAC;
	}	
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint()  throws SistemaImpressaoException, ApplicationBusinessException {

		listaLicitacoes.add(pac);
		setDados(listaLicitacoes);

		if (dados.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			return;
		}

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
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		try {
			params.put("endereco", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_COMPLETO));
			params.put("cnpj", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_CGC));
			params.put("telefone", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_FONE));
			params.put("email", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_EMAIL));
			params.put("site", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_SITE));			
			params.put("caminhoLogo", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_RELATIVO_BW_JEE7));
			params.put("remetente", remetente);
			
			if (pac!=null && pac.getNumero()!=null){
				
				params.put("modalidadeTipoDescricao", (pac.getModalidadeLicitacao()!=null?pac.getModalidadeLicitacao().getDescricao():"")
														+" :   "+ (pac.getTipo()!=null?pac.getTipo().getDescricao():""));
				
				if (pac.getModalidadeEmpenho()!=null && pac.getModalidadeEmpenho().equals(DominioModalidadeEmpenho.ORDINARIO)){
					params.put("modalidadeEmpenho", "Compra Única");
				}
				
				if (pac.getPeriodoFornecimento()!=null){
					if (pac.getModalidadeEmpenho()!=null && (pac.getModalidadeEmpenho().equals(DominioModalidadeEmpenho.ESTIMATIVA)
							|| pac.getModalidadeEmpenho().equals(DominioModalidadeEmpenho.CONTRATO))){
						params.put("modalidadeEmpenho", pac.getPeriodoFornecimento());
					}
					
				} else {
					if (pac.getModalidadeEmpenho()!=null && (pac.getModalidadeEmpenho().equals(DominioModalidadeEmpenho.ESTIMATIVA)
							|| pac.getModalidadeEmpenho().equals(DominioModalidadeEmpenho.CONTRATO))){
						params.put("modalidadeEmpenho", "");
					}
				}
			}
		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/TermoAberturaPac.jasper";
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return dados;
	}

	public Collection<ScoLicitacao> getDados() {
		return dados;
	}

	public void setDados(List<ScoLicitacao> dados) {
		this.dados = dados;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public List<ScoLicitacao> getListaLicitacoes() {
		return listaLicitacoes;
	}

	public void setListaLicitacoes(List<ScoLicitacao> listaLicitacoes) {
		this.listaLicitacoes = listaLicitacoes;
	}

	public ScoLicitacao getPac() {
		return pac;
	}

	public void setPac(ScoLicitacao pac) {
		this.pac = pac;
	}

	public String getRemetente() {
		return remetente;
	}

	public void setRemetente(String remetente) {
		this.remetente = remetente;
	}
}