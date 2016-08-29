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
import br.gov.mec.aghu.indicadores.business.IIndicadoresFacade;
import br.gov.mec.aghu.indicadores.vo.IndHospClinicaEspVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioIndicadoresClinicaController extends ActionReport {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5869187047210442792L;

	@EJB
	private IIndicadoresFacade indicadoresFacade;

	private Date mes;

	private List<IndHospClinicaEspVO> listIndicadores = new ArrayList<IndHospClinicaEspVO>();

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	public String print() {
		Integer numeroOcorrencias = indicadoresFacade.obterNumeroOcorrenciasIndicadoresGerais(mes);

		if (numeroOcorrencias > 0) {
			listIndicadores = indicadoresFacade.gerarRelatorioClinicaEspecialidade(mes);
			return "relatorioIndicadoresHospitalaresClinicaPdf";

		} else {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_MES_INFORMADO_NAO_APURADO");
			return null;
		}
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/indicadores/report/relatorioIndicadoresHospitalaresClinica.jasper";
	}

	@Override
	public Collection<IndHospClinicaEspVO> recuperarColecao() {

		return this.listIndicadores;
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
		params.put("nomeRelatorio", "AINR_INDICADOR_ESP");

		return params;
	}

	public Date getMes() {
		return mes;
	}

	public void setMes(Date mes) {
		this.mes = mes;
	}

	public List<IndHospClinicaEspVO> getListIndicadores() {
		return listIndicadores;
	}

	public void setListIndicadores(List<IndHospClinicaEspVO> listIndicadores) {
		this.listIndicadores = listIndicadores;
	}

	public void limpar() {
		this.mes = null;
	}
}
