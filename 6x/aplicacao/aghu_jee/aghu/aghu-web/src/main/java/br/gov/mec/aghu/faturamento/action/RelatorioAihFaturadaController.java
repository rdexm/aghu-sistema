package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.AihFaturadaVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioAihFaturadaController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3857647987105462139L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioAihFaturadaController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Integer cthSeq;

	private Integer prontuario;

	private FatCompetencia competencia;

	private FatItensProcedHospitalar procedimentoInicial;

	private FatItensProcedHospitalar procedimentoFinal;

	private String iniciais;

	private Boolean reapresentada;

	private Boolean gerouArquivo;

	private String fileName;

	private Boolean todosProcedimentos;

	private Long codTabIni;

	private Long codTabFim;
	
	private Boolean habilitaIntervalo;
	
	private Long ssmInicial;
	
	private Long ssmFinal;
	
	public enum RelatorioAihFaturadaControllerExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_PEND_ENC_INVALIDAS;
	}
	

	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}

	@Override
	public Collection<AihFaturadaVO> recuperarColecao() {
		obterProcedimentos();
		return faturamentoFacade.listarEspelhoAihFaturada(cthSeq, prontuario, competencia.getId().getMes(), competencia.getId().getAno(),
				competencia.getId().getDtHrInicio(), codTabIni, codTabFim, iniciais, reapresentada);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioAihFaturada.jasper";
	}

	public String imprimirRelatorio() {
		try {
			if (CoreUtil.validaIniciaisPaciente(iniciais)) {
				directPrint();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
				return "";
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioAihFaturadaControllerExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}

	public String visualizarRelatorio() {
		try {
			if (CoreUtil.validaIniciaisPaciente(iniciais)) {
				return "relatorioAihFaturadaPdf";
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioAihFaturadaControllerExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private void obterProcedimentos() {
		if(habilitaIntervalo) {
			codTabIni = (todosProcedimentos) ? 1 : ssmInicial;
		}
		else {
			codTabIni = (todosProcedimentos) ? 1 : procedimentoInicial.getCodTabela();
		}

		codTabFim = null;
		if(todosProcedimentos) {
			codTabFim = 999999999L;		
		} 
		else {
			if (habilitaIntervalo) {
				if(ssmFinal != null) {
					codTabFim = ssmFinal;
				}
			}
			else {
				if (procedimentoFinal != null) {
					codTabFim = procedimentoFinal.getCodTabela();
				}
			}
		}
	}

	public void clearProcedimentos() {
		procedimentoInicial = null;
		procedimentoFinal = null;
		ssmInicial = null;
		ssmFinal = null;
	}

	public void gerarCSV() {
		try {
			if (CoreUtil.validaIniciaisPaciente(iniciais)) {

				obterProcedimentos();

				fileName = faturamentoFacade.gerarCSVRelatorioAihsFaturadas(cthSeq, prontuario, competencia.getId().getMes(), competencia
						.getId().getAno(), competencia.getId().getDtHrInicio(), codTabIni, codTabFim, iniciais, reapresentada);
				gerouArquivo = true;
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioAihFaturadaControllerExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (IOException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
					e.getLocalizedMessage()));

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void dispararDownload() {
		if (fileName != null) {
			try {
				download(fileName);
				gerouArquivo = false;
				fileName = null;
			} catch (IOException e) {
				getLog().error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
						e.getLocalizedMessage()));
			}
		}
	}

	public void limpar() {
		cthSeq = null;
		prontuario = null;
		competencia = null;
		procedimentoInicial = null;
		procedimentoFinal = null;
		iniciais = null;
		reapresentada = false;
		gerouArquivo = false;
		fileName = null;
	}

	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));

		obterProcedimentos();

		params.put("codTabelaInicial", codTabIni);
		if (codTabFim != null) {
			params.put("codTabelaFinal", codTabFim);
		}
		params.put("competencia",
				(DateFormatSymbols.getInstance(new Locale("pt", "BR")).getMonths()[competencia.getId().getMes() - 1]).toUpperCase()
						+ " DE " + competencia.getId().getAno());
		params.put("situacao", (reapresentada) ? "'CONTAS REAPRESENTADAS" : "CONTAS ENCERRADAS NO PERÍODO");
		params.put("nomeRelatorio", "FATR_INT_AIH_POR_SSM");
		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);

		if (!recuperarColecao().isEmpty()) {
			params.put("procedimento",
					((AihFaturadaVO) (recuperarColecao().toArray()[recuperarColecao().size() - 1])).getDescricaoProcedimento());
		}

		return params;
	}

	public List<FatCompetencia> pesquisarCompetencias(String objPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(faturamentoFacade.getCompetenciaId(objPesquisa)),pesquisarCompetenciasCount(objPesquisa));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatCompetencia>(0);
		}
	}

	public Long pesquisarCompetenciasCount(String objPesquisa) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(faturamentoFacade.getCompetenciaId(objPesquisa));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	public List<FatItensProcedHospitalar> pesquisarItensProc(final String obj) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarFatItensProcedHospitalarTabFatPadrao(obj),pesquisarItensProcCount(obj));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatItensProcedHospitalar>(0);
		}
	}

	public Long pesquisarItensProcCount(final String obj) {
		try {
			return faturamentoFacade.listarFatItensProcedHospitalarTabFatPadraoCount(obj);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	public String voltar() {
		return "relatorioAihFaturada";
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public FatItensProcedHospitalar getProcedimentoInicial() {
		return procedimentoInicial;
	}

	public void setProcedimentoInicial(FatItensProcedHospitalar procedimentoInicial) {
		this.procedimentoInicial = procedimentoInicial;
	}

	public FatItensProcedHospitalar getProcedimentoFinal() {
		return procedimentoFinal;
	}

	public void setProcedimentoFinal(FatItensProcedHospitalar procedimentoFinal) {
		this.procedimentoFinal = procedimentoFinal;
	}

	public String getIniciais() {
		return iniciais;
	}

	public void setIniciais(String iniciais) {
		this.iniciais = iniciais;
	}

	public Boolean getReapresentada() {
		return reapresentada;
	}

	public void setReapresentada(Boolean reapresentada) {
		this.reapresentada = reapresentada;
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Boolean getTodosProcedimentos() {
		return todosProcedimentos;
	}

	public void setTodosProcedimentos(Boolean todosProcedimentos) {
		this.todosProcedimentos = todosProcedimentos;
	}

	public Long getSsmInicial() {
		return ssmInicial;
	}

	public void setSsmInicial(Long ssmInicial) {
		this.ssmInicial = ssmInicial;
	}

	public Long getSsmFinal() {
		return ssmFinal;
	}

	public void setSsmFinal(Long ssmFinal) {
		this.ssmFinal = ssmFinal;
	}

	public Boolean getHabilitaIntervalo() {
		return habilitaIntervalo;
	}

	public void setHabilitaIntervalo(Boolean habilitaIntervalo) {
		this.habilitaIntervalo = habilitaIntervalo;
	}
}
