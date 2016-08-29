package br.gov.mec.aghu.compras.action;

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
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoProgrCodAcessoForn;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class ImprimirAcessoFornecedorController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ImprimirAcessoFornecedorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1006442888234676013L;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	private Integer codigo;
	private String voltarPara;

	private List<ScoProgrCodAcessoForn> colecao = new ArrayList<ScoProgrCodAcessoForn>();

	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/imprimirSenhaAcessoFornecedor.jasper";
	}

	public void print()throws  BaseException, JRException, SystemException, IOException {
		try {
			recuperarColecao();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Collection<ScoProgrCodAcessoForn> recuperarColecao() throws ApplicationBusinessException {
		colecao = recuperaRelatorio();
		return colecao;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		
		ScoProgrCodAcessoForn acessoFornecedor = this.comprasFacade.obterScoProgrCodAcessoFornPorChavePrimaria(getCodigo());
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		AghParametros parametroLogo = null;
		AghParametros parametroUniversidade = null;
		AghParametros parametroHU = null;
		AghParametros parametroSite = null;
		AghParametros parametroDetalhe = null;
		
		try {
			parametroLogo = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_RELATIVO_BW_JEE7);
			parametroUniversidade = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LOGO_UNIVERSIDADE);
			parametroHU = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PARAMETRO_HU);
			parametroSite = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_SITE);
			parametroDetalhe = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_DETALHES_ACESSO_PROG_ENTREGA_MAT);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		if (parametroLogo != null) {
			String caminhoLogo = null; //TODO MIGRAÇÃO Imagem não existe na localização informada pelo parâmetro  //parametroLogo.getVlrTexto();
			params.put("caminhoLogo", caminhoLogo);
		}
		
		if (parametroUniversidade != null) {
			String caminhoLogoUniversidade = null;//TODO MIGRAÇÃO Imagem não existe na localização informada pelo parâmetro //parametroUniversidade.getVlrTexto();
			params.put("caminhoLogoUniversidade", caminhoLogoUniversidade);
		}
		
		params.put("nomeFornecedor", acessoFornecedor.getScoFornecedor().getRazaoSocial());
		params.put("nomeHU", parametroHU.getVlrTexto());
		params.put("site", parametroSite.getVlrTexto());
		params.put("detalhe", parametroDetalhe.getVlrTexto());
		params.put("usuario", acessoFornecedor.getScoFornecedor().getCgc());
		params.put("senha", acessoFornecedor.getCodAcesso());

		return params;
	}
	
	public DocumentoJasper buscarDocumentoGerado() throws ApplicationBusinessException{
		return this.gerarDocumento();
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}

	private List<ScoProgrCodAcessoForn> recuperaRelatorio() throws ApplicationBusinessException {
		List<ScoProgrCodAcessoForn> lista = new ArrayList<ScoProgrCodAcessoForn>();
		lista.add(this.comprasFacade.obterScoProgrCodAcessoFornPorChavePrimaria(getCodigo()));
		return lista;
	}

	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public String voltar() {
		return this.voltarPara;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
}
