package br.gov.mec.aghu.paciente.prontuario.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;
import br.gov.mec.aghu.paciente.vo.DesarquivamentoProntuarioVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioMonitorDesarquivamentoProntuarioController extends ActionReport {

	private static final long serialVersionUID = 3624873464068215974L;
	private static final Log LOG = LogFactory.getLog(RelatorioMonitorDesarquivamentoProntuarioController.class);
	private static final String REDIRECT_MONITOR_DESARQUIVAMENTO_PRONTUARIO = "monitorDesarquivamentoProntuario";
	private static final String REDIRECT_RELATORIO_AMBULATORIO = "relatorioMonitorDesarquivamentoProntuario";

	@Inject
	private MonitoramentoDesarquivamentoProntuarioController monitoramentoDesarquivamentoProntuarioController;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private Collection<AipMovimentacaoProntuarios> listaMovimentacoes;

	private AipSolicitacaoProntuarios solicitacao;

	private Integer abaSelecionada;
	
	public String print(){
		return REDIRECT_RELATORIO_AMBULATORIO;
	}
	
	public String voltar(){
		monitoramentoDesarquivamentoProntuarioController.setAbaSelecionada(abaSelecionada);
		LOG.info("voltar"+REDIRECT_MONITOR_DESARQUIVAMENTO_PRONTUARIO);
		return REDIRECT_MONITOR_DESARQUIVAMENTO_PRONTUARIO;
	}
	
	@Override
	public Collection<DesarquivamentoProntuarioVO> recuperarColecao()
			throws ApplicationBusinessException {

		List<DesarquivamentoProntuarioVO> listaDesarquivamentoVO = new ArrayList<DesarquivamentoProntuarioVO>();

		DesarquivamentoProntuarioVO desarquivamentoVO;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		for (AipMovimentacaoProntuarios movimentacao : this.listaMovimentacoes) {
			desarquivamentoVO = new DesarquivamentoProntuarioVO();
			desarquivamentoVO.setDataMvto(sdf.format(movimentacao.getDataMovimento()));
			desarquivamentoVO.setLocal(movimentacao.getLocal());
			desarquivamentoVO.setNome(movimentacao.getPaciente().getNome());
			desarquivamentoVO.setObservacao(movimentacao.getObservacoes());
			desarquivamentoVO.setProntuario(movimentacao.getPaciente().getProntuario().toString());
			desarquivamentoVO.setVolume(movimentacao.getVolumes().toString());

			listaDesarquivamentoVO.add(desarquivamentoVO);
		}

		// Collections.sort(listaDesarquivamentoVO,
		// new DesarquivamentoProntuarioComparator());
		return listaDesarquivamentoVO;

	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException,
			ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/report/relatorioMonitorDesarquivamentoProntuario.jasper";

	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "AIPR_DESARQ_PRNT");
		
		String nomeHospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		if(nomeHospital.isEmpty()) {
			try {
				nomeHospital = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL).getVlrTexto();
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getLocalizedMessage());
			}
		}
		
		params.put("nomeHospital", nomeHospital);
		params.put("codigoSolicitacao", this.solicitacao.getCodigo().toString());

		return params;
	}

	// GETs AND SETs

	/**
	 * @return the listaMovimentacoes
	 */
	public Collection<AipMovimentacaoProntuarios> getListaMovimentacoes() {
		return listaMovimentacoes;
	}

	/**
	 * @param listaMovimentacoes
	 *            the listaMovimentacoes to set
	 */
	public void setListaMovimentacoes(Collection<AipMovimentacaoProntuarios> listaMovimentacoes) {
		this.listaMovimentacoes = listaMovimentacoes;
	}

	/**
	 * @return the solicitacao
	 */
	public AipSolicitacaoProntuarios getSolicitacao() {
		return solicitacao;
	}

	/**
	 * @param solicitacao
	 *            the solicitacao to set
	 */
	public void setSolicitacao(AipSolicitacaoProntuarios solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}


}
