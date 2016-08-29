package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioGrupoProcedimento;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ProcedimentoNaoFaturadoVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;

public class RelatorioProcedimentoNaoFaturadoController extends ActionReport {

	private static final String GRUPO = "grupo";

	/**
	 * 
	 */
	private static final long serialVersionUID = 9016453759637759118L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioProcedimentoNaoFaturadoController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private FatCompetencia competencia;

	private String iniciaisPaciente;

	private Boolean reapresentada;

	private DominioGrupoProcedimento grupoProcedimento;

	private AghEspecialidades especialidade;

	private FatItensProcedHospitalar procedimento;

	private FatItensProcedHospitalar itemProcedimento;

	private Boolean gerouArquivo;

	private String fileName;

	public enum RelatorioProcedimentosNaoFaturadosExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_PEND_ENC_INVALIDAS;
	}

	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}

	@Override
	public Collection<ProcedimentoNaoFaturadoVO> recuperarColecao() {
		try {
			return faturamentoFacade.pesquisarEspelhoAihProcedimentosNaoFaturados(
					(itemProcedimento != null) ? itemProcedimento.getCodTabela() : null,
					(procedimento != null) ? procedimento.getCodTabela() : null, (especialidade != null) ? especialidade.getSeq() : null,
					competencia.getId().getMes(), competencia.getId().getAno(), competencia.getId().getDtHrInicio(), grupoProcedimento,
					iniciaisPaciente, reapresentada);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioProcedimentoNaoFaturado.jasper";
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 */
	public String visualizarRelatorio() {
		try {
			if (!CoreUtil.validaIniciaisPaciente(iniciaisPaciente)) {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioProcedimentosNaoFaturadosExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
				return null;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return "relatorioProcedimentoNaoFaturadoPdf";
	}
	
	public String voltar(){
		return "relatorioProcedimentoNaoFaturado";
	}

	public void directPrint() {
		try {
			if (CoreUtil.validaIniciaisPaciente(iniciaisPaciente)) {
				super.directPrint();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioProcedimentosNaoFaturadosExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public void gerarCSV() {
		// try {

		// fileName = faturamentoFacade.gerarCSVRelatorioAihsFaturadas(cthSeq,
		// prontuario, competencia.getId().getMes(), competencia.getId()
		// .getAno(), competencia.getId().getDtHrInicio(),
		// procedimentoInicial.getCodTabela(),
		// (procedimentoFinal != null) ? procedimentoFinal.getCodTabela()
		// : null, iniciais, reapresentada);
		gerouArquivo = true;

		// } catch (IOException e) {

		// apresentarExcecaoNegocio(new
		// BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,
		// e, e.getLocalizedMessage()));
		//
		// } catch (BaseException e) {
		// apresentarExcecaoNegocio(e);
		// }
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
		competencia = null;
		especialidade = null;
		grupoProcedimento = null;
		procedimento = null;
		itemProcedimento = null;
		iniciaisPaciente = null;
		reapresentada = false;
		gerouArquivo = false;
		fileName = null;
	}

	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ano", competencia.getId().getAno());
		params.put("mes",
				(DateFormatSymbols.getInstance(new Locale("pt", "BR")).getMonths()[competencia.getId().getMes() - 1]).toUpperCase());
		params.put("situacao", (reapresentada) ? "'CONTAS REAPRESENTADAS" : "CONTAS ENCERRADAS NO PERÍODO");
		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		if (grupoProcedimento != null) {
			if (DominioGrupoProcedimento.M.equals(grupoProcedimento)) {
				params.put(GRUPO, "MATERIAIS");
			} else if (DominioGrupoProcedimento.C.equals(grupoProcedimento)) {
				params.put(GRUPO, "PROCEDIMENTOS CIRURGICOS");
			} else if (DominioGrupoProcedimento.D.equals(grupoProcedimento)) {
				params.put(GRUPO, "PROCEDIMENTOS ESPECIAIS DIVERSOS");
			} else if (DominioGrupoProcedimento.S.equals(grupoProcedimento)) {
				params.put(GRUPO, "COMPONENTES SANGUINEOS");
			} else if (DominioGrupoProcedimento.H.equals(grupoProcedimento)) {
				params.put(GRUPO, "PROCEDIMENTOS HEMOTERAPICOS");
			} else if (DominioGrupoProcedimento.E.equals(grupoProcedimento)) {
				params.put(GRUPO, "EXAMES");
			}
		}
		return params;
	}

	public List<FatCompetencia> pesquisarCompetencias(String objPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(faturamentoFacade.getCompetenciaId(objPesquisa)),pesquisarCompetenciasCount(objPesquisa));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<FatCompetencia>();
	}

	public Long pesquisarCompetenciasCount(String objPesquisa) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(faturamentoFacade.getCompetenciaId(objPesquisa));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return 0L;
	}

	public List<AghEspecialidades> listarEspecialidades(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadesPorNomeOuSigla((String) strPesquisa),listarEspecialidadesCount(strPesquisa));
	}

	public Integer listarEspecialidadesCount(final String strPesquisa) {
		return this.aghuFacade.pesquisarEspecialidadesPorNomeOuSigla((String) strPesquisa).size();
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

	public List<FatItensProcedHospitalar> pesquisarItensProcInt(final String obj) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarItensProcedHospTabPadraoPlanoInt(obj),pesquisarItensProcIntCount(obj));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatItensProcedHospitalar>(0);
		}
	}

	public Long pesquisarItensProcIntCount(final String obj) {
		try {
			return faturamentoFacade.listarItensProcedHospTabPadraoPlanoIntCount(obj);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public String getIniciaisPaciente() {
		return iniciaisPaciente;
	}

	public void setIniciaisPaciente(String iniciaisPaciente) {
		this.iniciaisPaciente = iniciaisPaciente;
	}

	public Boolean getReapresentada() {
		return reapresentada;
	}

	public void setReapresentada(Boolean reapresentada) {
		this.reapresentada = reapresentada;
	}

	public DominioGrupoProcedimento getGrupoProcedimento() {
		return grupoProcedimento;
	}

	public void setGrupoProcedimento(DominioGrupoProcedimento grupoProcedimento) {
		this.grupoProcedimento = grupoProcedimento;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public FatItensProcedHospitalar getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(FatItensProcedHospitalar procedimento) {
		this.procedimento = procedimento;
	}

	public FatItensProcedHospitalar getItemProcedimento() {
		return itemProcedimento;
	}

	public void setItemProcedimento(FatItensProcedHospitalar itemProcedimento) {
		this.itemProcedimento = itemProcedimento;
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
}
