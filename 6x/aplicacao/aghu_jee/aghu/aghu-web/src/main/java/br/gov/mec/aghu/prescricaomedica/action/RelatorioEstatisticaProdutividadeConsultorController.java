package br.gov.mec.aghu.prescricaomedica.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ItemRelatorioEstatisticaProdutividadeConsultorVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioEstatisticaProdutividadeConsultorVO;
import net.sf.jasperreports.engine.JRException;

/**
 * #3855 Prescrição: Emitir relatório de estatísica da produtividade do
 * consultor
 * 
 * @author aghu
 *
 */
public class RelatorioEstatisticaProdutividadeConsultorController extends ActionReport {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 1382634579812691302L;

	private StreamedContent media;

	private static final String NOME_RELATORIO = "Estatística da Produtividade do Consultor";
	private static final String MPMR_ESTAT_PROD_CNST = "MPMR_ESTAT_PROD_CNST";

	private static final String RELATORIO = "relatorioEstatisticaProdutividadeConsultor";
	private static final String RELATORIO_PDF = "relatorioEstatisticaProdutividadeConsultorPdf";

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	// Dados que serão impressos em PDF
	private RelatorioEstatisticaProdutividadeConsultorVO relatorioVO = new RelatorioEstatisticaProdutividadeConsultorVO();

	/*
	 * Filtros
	 */
	private AghEspecialidades especialidade;
	private Date dataInicial;
	private Date dataFinal;

	@Override
	protected Collection<ItemRelatorioEstatisticaProdutividadeConsultorVO> recuperarColecao() throws ApplicationBusinessException {
		return this.relatorioVO.getResultados();
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaomedica/report/relatorioEstatisticaProdutividadeConsultor.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("hospitalLocal", this.cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());
		params.put("nomeRelatorio", NOME_RELATORIO);
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("especialidade", StringUtils.upperCase(this.especialidade.getNomeEspecialidade()));
		params.put("dataInicial", DateUtil.obterDataFormatada(this.dataInicial, DateConstants.DATE_PATTERN_DDMMYYYY));
		params.put("dataFinal", DateUtil.obterDataFormatada(this.dataFinal, DateConstants.DATE_PATTERN_DDMMYYYY));
		params.put("nomeRelatorioRodape", MPMR_ESTAT_PROD_CNST);

		params.put("tempoMedioGeralConhecimento", this.relatorioVO.getTempoMedioGeralConhecimento());
		params.put("tempoMedioGeralResposta", this.relatorioVO.getTempoMedioGeralResposta());

		return params;
	}

	/**
	 * Pesquisa principal do relatório
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void pesquisar() throws ApplicationBusinessException {
		try {
			this.relatorioVO = this.prescricaoMedicaFacade.pesquisarRelatorioEstatisticaProdutividadeConsultor(this.especialidade.getSeq(), this.dataInicial, this.dataFinal);
		} catch (ApplicationBusinessException e) {
			this.relatorioVO = null;
			throw e;
		}
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 */
	public String print() throws JRException, IOException, DocumentException {
		try {
			this.pesquisar();

			DocumentoJasper documento = gerarDocumento();
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
			return RELATORIO_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException {
		try {

			this.pesquisar();

			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public void renderPdf(OutputStream out, Object data) throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		out.write(documento.getPdfByteArray(false));
	}

	/**
	 * Pesquisa SuggestionBox de especialidades
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghEspecialidades> pesquisarSelecaoEspecialidades(String parametro) {
		return super.returnSGWithCount(this.aghuFacade.pesquisarSelecaoEspecialidades(parametro), this.aghuFacade.pesquisarSelecaoEspecialidadesCount(parametro));
	}

	/**
	 * Voltar da tela de visualização
	 * 
	 * @return
	 */
	public String voltar() {
		this.relatorioVO = new RelatorioEstatisticaProdutividadeConsultorVO();
		return RELATORIO;
	}

	/**
	 * Limpar tela
	 */
	public void limpar() {
		this.relatorioVO = new RelatorioEstatisticaProdutividadeConsultorVO();
		this.especialidade = null;
		this.dataInicial = null;
		this.dataFinal = null;
	}

	/*
	 * Getters and Setters
	 */

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public RelatorioEstatisticaProdutividadeConsultorVO getRelatorioVO() {
		return relatorioVO;
	}

	public void setRelatorioVO(RelatorioEstatisticaProdutividadeConsultorVO relatorioVO) {
		this.relatorioVO = relatorioVO;
	}

}