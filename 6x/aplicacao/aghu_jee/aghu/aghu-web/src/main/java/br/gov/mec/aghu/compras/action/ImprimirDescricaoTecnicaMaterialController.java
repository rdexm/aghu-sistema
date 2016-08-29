package br.gov.mec.aghu.compras.action;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
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
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.ScoDescricaoTecnicaPadrao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class ImprimirDescricaoTecnicaMaterialController extends
		ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(ImprimirDescricaoTecnicaMaterialController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1006442888234676013L;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	private Short codigo;

	private List<ScoDescricaoTecnicaPadrao> colecao;
	
	private static final String CADASTRO_DESCRICAO_TECNICA = "cadastroDescricaoTecnicaCRUD";
	
	@PostConstruct
	public void inicializar() {
		begin(conversation);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/imprimirDescricaoTecnicaMaterial.jasper";
	}

	@Override
	public Collection<ScoDescricaoTecnicaPadrao> recuperarColecao()
			throws ApplicationBusinessException {
		colecao = recuperaRelatorio();
		return colecao;
	}

	public void print()throws  BaseException, JRException, SystemException,
			IOException {
		try {
			recuperarColecao();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("hospitalLocal", cadastrosBasicosInternacaoFacade
				.recuperarNomeInstituicaoHospitalarLocal());
		params.put("dataAtual",
				DateUtil.obterDataFormatada(new Date(), "dd/MM/yy HH:mm"));
		params.put("nomeRelatorio", "SCOR_DESCR_TEC_MAT");

		return params;
	}

	public StreamedContent getRenderPdf() throws IOException,
			JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}

	private List<ScoDescricaoTecnicaPadrao> recuperaRelatorio()
			throws ApplicationBusinessException {
		return comprasFacade.obterRelatorioDescricaoTecnica(this.codigo);

	}

	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}

	public String voltar() {
		return CADASTRO_DESCRICAO_TECNICA;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

}
