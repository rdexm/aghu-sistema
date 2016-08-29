package br.gov.mec.aghu.farmacia.action;

import java.io.ByteArrayInputStream;
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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.vo.PrescricaoUnidadeVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import net.sf.jasperreports.engine.JRException;

public class RelatorioPrescricaoPorUnidadeController extends ActionReport{

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 5795420459171554799L;
	
	private static final String PAGE_RELATORIO_PRESCRICAO_POR_UNIDADE= "relatorioPrescricaoPorUnidade";
	private static final String PAGE_RELATORIO_PRESCRICAO_POR_UNIDADE_PDF= "relatorioPrescricaoPorUnidadePdf";
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private AghUnidadesFuncionais unidadeFuncional;
	private Date dataDeReferencia;
	private String validade;
	private Boolean indPmNaoEletronica;

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	/**
	 * Dados que serão impressos
	 */
	private List<PrescricaoUnidadeVO> colecao = new ArrayList<PrescricaoUnidadeVO>(
			0);

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(
			String parametro) {
		return this.returnSGWithCount(farmaciaFacade
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
						parametro),pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(parametro));
	}
	
	public Long pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(String parametro) {
		return farmaciaFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(parametro);
	}

	public void atribuirValidade() {
		if (dataDeReferencia != null && unidadeFuncional != null) {
			validade = farmaciaFacade.atribuirValidadeDaPrescricaoMedica(dataDeReferencia, unidadeFuncional);
		} else {
			validade = "";
		}
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 */
	public void directPrint()throws SistemaImpressaoException, ApplicationBusinessException{

		try {
			colecao = this.farmaciaFacade
					.obterConteudoRelatorioPrescricaoPorUnidade(
							unidadeFuncional, dataDeReferencia, validade, indPmNaoEletronica);
			if (colecao.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * Apresenta PDF na tela do navegador.
	 * 
	 */
	public String print()throws JRException, IOException, DocumentException {
		try {
			colecao = this.farmaciaFacade.obterConteudoRelatorioPrescricaoPorUnidade(
							unidadeFuncional, dataDeReferencia, validade, indPmNaoEletronica);
			if (colecao.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return null;
			}
//			limpaCampos();
			return PAGE_RELATORIO_PRESCRICAO_POR_UNIDADE_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String voltar(){
		return PAGE_RELATORIO_PRESCRICAO_POR_UNIDADE;
	}

	@Override
	public Collection<PrescricaoUnidadeVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/farmacia/report/PrescricaoUnidade.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		// String hospital = "Hospital de Clínicas da UFPR";
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()));
		params.put("funcionalidade", "Prescrições por Unidade");
		params.put("nomeRelatorio", "AFAR_CONF_PRCR_UNID");

		return params;
	}

	public void limpaCampos() {
		unidadeFuncional = null;
		dataDeReferencia = null;
		validade = null;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 */
	public StreamedContent getRenderPdf() throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	// Getters e Setters

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Date getDataDeReferencia() {
		return dataDeReferencia;
	}

	public void setDataDeReferencia(Date dataDeReferencia) {
		this.dataDeReferencia = dataDeReferencia;
	}

	public String getValidade() {
		return validade;
	}

	public void setValidade(String validade) {
		this.validade = validade;
	}

	public List<PrescricaoUnidadeVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<PrescricaoUnidadeVO> colecao) {
		this.colecao = colecao;
	}
	public Boolean getIndPmNaoEletronica() {
		return indPmNaoEletronica;
	}

	public void setIndPmNaoEletronica(Boolean indPmNaoEletronica) {
		this.indPmNaoEletronica = indPmNaoEletronica;
	}
}