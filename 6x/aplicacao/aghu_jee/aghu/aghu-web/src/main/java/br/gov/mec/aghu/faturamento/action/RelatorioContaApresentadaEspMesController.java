package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * 
 * @author paulo.silveira
 *
 */
public class RelatorioContaApresentadaEspMesController extends ActionController {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4407345585244953091L;

	private static final Log LOG = LogFactory.getLog(RelatorioContaApresentadaEspMesController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private RelatorioContasApresentadasPorEspecialidadeController reportController;

	private FatCompetencia competencia;
	private AghEspecialidades especialidade;

	private Boolean gerouArquivo;

	private String fileName;

	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}
	
	public List<FatCompetencia> pesquisarCompetencias(String param) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioComHora(faturamentoFacade.getCompetenciaId(param)),pesquisarCompetenciasCount(param));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<FatCompetencia>();
	}
	
	public Long pesquisarCompetenciasCount(String param) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioComHoraCount(faturamentoFacade.getCompetenciaId(param));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return Long.valueOf(0);
	}
	
	public List<AghEspecialidades> pesquisarEspecialidades(String param) {
		return this.returnSGWithCount(aghuFacade.pesquisarPorNomeOuSiglaOrderByNomeReduzido((String) param),pesquisarEspecialidadesCount(param));
	}
	
	public Long pesquisarEspecialidadesCount(String param) {
		return aghuFacade.pesquisarPorNomeOuSiglaOrderByNomeReduzidoCount((String) param);
	}
	

	public void imprimirRelatorio() throws BaseException, JRException, SystemException, IOException {
		reportController.setCompetencia(competencia);
		reportController.setEspecialidade(especialidade);
		reportController.directPrint();
	}

	public String visualizarRelatorio() {
		reportController.setCompetencia(competencia);
		reportController.setEspecialidade(especialidade);
		return reportController.visualizarRelatorio();
	}

	public void gerarCSV() {
		try {
			Short seqEsp = especialidade == null ? null : especialidade.getSeq();
			fileName = faturamentoFacade.gerarCSVContaApresentadaEspMes(seqEsp, competencia);
			reportController.setCompetencia(competencia);
			reportController.setEspecialidade(especialidade);
			gerouArquivo = true;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void dispararDownload() {
		gerarCSV();
		if (fileName != null) {
			try {
				download(fileName, DominioNomeRelatorio.FATR_INT_ESPEC_MES.toString() + "_" + StringUtils.upperCase(reportController.mesAnoAtual()).replaceAll("/", "_").replaceAll(" ", "") + ".csv");
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
		fileName = null;
		gerouArquivo = false;
		especialidade = null;
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

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}
}