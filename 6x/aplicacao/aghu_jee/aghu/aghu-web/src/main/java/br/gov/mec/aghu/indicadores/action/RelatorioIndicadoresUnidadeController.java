package br.gov.mec.aghu.indicadores.action;

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
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioTipoUnidade;
import br.gov.mec.aghu.indicadores.business.IIndicadoresFacade;
import br.gov.mec.aghu.indicadores.vo.UnidadeIndicadoresVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioIndicadoresUnidadeController extends ActionReport {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8578958894122758161L;

	@EJB
	private IIndicadoresFacade indicadoresFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Date mes;
	private DominioTipoUnidade tipoUnidade = DominioTipoUnidade.U;

	private List<UnidadeIndicadoresVO> indicadoresUnidadeList = new ArrayList<UnidadeIndicadoresVO>();

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print() throws BaseException, JRException, SystemException, IOException {

		Integer numeroOcorrencias = indicadoresFacade.obterNumeroOcorrenciasIndicadoresUnidade(tipoUnidade, mes);

		if (numeroOcorrencias > 0) {
			this.indicadoresUnidadeList = indicadoresFacade.pesquisarIndicadoresUnidade(tipoUnidade, mes);

			return "relatorioIndicadoresHospitalaresUnidadePdf";
		} else {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_MES_INFORMADO_NAO_APURADO");

			return null;
		}
	}

	@Override
	public Collection<UnidadeIndicadoresVO> recuperarColecao() {

		return this.indicadoresUnidadeList;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/indicadores/report/relatorioIndicadoresHospitalaresUnidade.jasper";
	}

	@Override
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "AINR_INDICADOR_HOSP");

		return params;
	}

	public void limpar() {
		this.mes = null;
		this.tipoUnidade = DominioTipoUnidade.U;
	}

	public Date getMes() {
		return mes;
	}

	public void setMes(Date mes) {
		this.mes = mes;
	}

	public DominioTipoUnidade getTipoUnidade() {
		return tipoUnidade;
	}

	public void setTipoUnidade(DominioTipoUnidade tipoUnidade) {
		this.tipoUnidade = tipoUnidade;
	}

	public List<UnidadeIndicadoresVO> getIndicadoresUnidadeList() {
		return indicadoresUnidadeList;
	}

	public void setIndicadoresUnidadeList(List<UnidadeIndicadoresVO> indicadoresUnidadeList) {
		this.indicadoresUnidadeList = indicadoresUnidadeList;
	}

}
