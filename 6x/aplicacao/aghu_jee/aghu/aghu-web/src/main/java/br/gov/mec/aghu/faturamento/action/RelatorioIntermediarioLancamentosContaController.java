package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class RelatorioIntermediarioLancamentosContaController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4211632037668571709L;

	private static final Log LOG = LogFactory.getLog(RelatorioIntermediarioLancamentosContaController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@Inject
	private RelatorioIntermediarioLancamentosContaPdfController reportController;

	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;

	// FILTROS
	private Integer prontuario;

	private Integer codigo;

	private Integer contaHospitalar;

	private String codigoDcih;

	// DISPLAY
	private Integer cthSeq;

	private String nomePaciente;

	private Integer pacCodigoFonetica;

	private Boolean gerouArquivo;

	private String fileName;

	private boolean voltouRelatorio;

	@Inject @Paginator
	private DynamicDataModel<VFatContaHospitalarPac> dataModel;

	public enum RelatorioIntermediarioLancamentosContaControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_INFORME_PELO_MENOS_UM_CAMPO_PESQUISA
	}

	@PostConstruct
	protected void init() {
		begin(conversation);
	}

	public void inicio() {
	 

		CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
		if (codPac != null && codPac.getCodigo() > 0) {
			AipPacientes paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac.getCodigo());
			if (paciente != null) {
				limpar();
				prontuario = paciente.getProntuario();
				codigo = paciente.getCodigo();
				nomePaciente = paciente.getNome();
			}

		} else if (prontuario != null) {
			obterPacientePorProntuario();
			dataModel.reiniciarPaginator();
		} else if (codigo != null) {
			obterPacientePorCodigo();
			dataModel.reiniciarPaginator();
		}
	
	}

	public String redirecionarPesquisaFonetica() {
		limpar();
		return "paciente-pesquisaPacienteComponente";
	}

	public void obterPacientePorCodigo() {
		final Integer filtro = codigo;
		limpar();
		if (filtro != null) {
			AipPacientes paciente = pacienteFacade.obterPacientePorCodigo(filtro);
			if (paciente != null) {
				nomePaciente = paciente.getNome();
				prontuario = paciente.getProntuario();
				codigo = paciente.getCodigo();
			} else {
				apresentarMsgNegocio(Severity.ERROR, "FAT_00731");
			}
		}
	}

	public void obterPacientePorProntuario() {
		final Integer filtro = prontuario;
		limpar();
		AipPacientes paciente = pacienteFacade.obterPacientePorProntuario(filtro);
		if (paciente != null) {
			nomePaciente = paciente.getNome();
			codigo = paciente.getCodigo();
			prontuario = paciente.getProntuario();
		} else {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PACIENTE_PRONTUARIO_NAO_ENCONTRADO");
		}
	}

	public void pesquisar() {
		if (prontuario != null || contaHospitalar != null || (codigoDcih != null && !codigoDcih.trim().equals("")) || codigo != null) {
			if (codigo != null && prontuario == null) {
				obterPacientePorCodigo();
			} else if (prontuario != null && codigo == null) {
				obterPacientePorProntuario();
			}
			if (contaHospitalar != null && nomePaciente == null) {
				final VFatContaHospitalarPac vPaciente = faturamentoFacade.obterVFatContaHospitalarPac(contaHospitalar);
				if (vPaciente != null) {
					prontuario = vPaciente.getPacProntuario();
					codigo = vPaciente.getPacCodigo();
					nomePaciente = vPaciente.getPacNome();
				}
			}
			// para evitar que a consulta traga todos os registros
			if (this.prontuario != null && this.codigo != null && this.nomePaciente != null) {
				dataModel.reiniciarPaginator();
			}
		} else {
			apresentarMsgNegocio(Severity.ERROR,
					RelatorioIntermediarioLancamentosContaControllerExceptionCode.MENSAGEM_INFORME_PELO_MENOS_UM_CAMPO_PESQUISA.toString());
		}
	}

	public void limpar() {
		prontuario = null;

		if (!voltouRelatorio) {
			contaHospitalar = null;
		}

		voltouRelatorio = false;
		codigoDcih = null;
		nomePaciente = null;
		codigo = null;
		dataModel.setPesquisaAtiva(false);
	}

	@Override
	public Long recuperarCount() {
		return this.faturamentoFacade.pesquisarContaHospitalarCount(prontuario, contaHospitalar, codigoDcih, null, null, codigo,
				new DominioSituacaoConta[] { DominioSituacaoConta.A, DominioSituacaoConta.F });
	}

	@Override
	public List<VFatContaHospitalarPac> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		final List<VFatContaHospitalarPac> lista = this.faturamentoFacade.pesquisarContaHospitalar(firstResult, maxResult, orderProperty,
				asc, prontuario, contaHospitalar, codigoDcih, null, null, codigo, new DominioSituacaoConta[] { DominioSituacaoConta.A,
						DominioSituacaoConta.F });

		for (VFatContaHospitalarPac aux : lista) {
			if (aux.getConvenioSaudePlano() != null) {
				aux.setSsmRealizado(faturamentoFacade.buscaSSM(aux.getCthSeq(), aux.getConvenioSaudePlano().getId().getCnvCodigo(), aux
						.getConvenioSaudePlano().getId().getSeq(), DominioSituacaoSSM.R));

				aux.setSsmSolicitado(faturamentoFacade.buscaSSM(aux.getCthSeq(), aux.getConvenioSaudePlano().getId().getCnvCodigo(), aux
						.getConvenioSaudePlano().getId().getSeq(), DominioSituacaoSSM.S));

				if (aux.getProcedimentoHospitalarInterno() != null && aux.getProcedimentoHospitalarInternoRealizado() != null) {
					aux.setFinanciamentoRealizado(faturamentoFacade.buscaSsmComplexidade(aux.getCthSeq(), aux.getConvenioSaudePlano()
							.getId().getCnvCodigo(), aux.getConvenioSaudePlano().getId().getSeq(), aux.getProcedimentoHospitalarInterno()
							.getSeq(), aux.getProcedimentoHospitalarInternoRealizado().getSeq(), DominioSituacaoSSM.R));

					aux.setFinanciamentoSolicitado(faturamentoFacade.buscaSsmComplexidade(aux.getCthSeq(), aux.getConvenioSaudePlano()
							.getId().getCnvCodigo(), aux.getConvenioSaudePlano().getId().getSeq(), aux.getProcedimentoHospitalarInterno()
							.getSeq(), aux.getProcedimentoHospitalarInternoRealizado().getSeq(), DominioSituacaoSSM.S));
				}

				if (aux.getDciCpeAno() != null && aux.getDciCpeMes() != null) {
					aux.setCompetencia(DateUtil.obterData(aux.getDciCpeAno(), aux.getDciCpeMes() - 1, 1));
				}
			}
			aux.setSituacaoSms(faturamentoFacade.buscaSitSms(aux.getCthSeq()));
		}
		return lista;
	}

	public void gerarCSV() {
		try {
			fileName = faturamentoFacade.geraCSVRelatorioIntermediarioLancamentosConta(cthSeq);
			cthSeq = null;
			gerouArquivo = true;
		} catch (IOException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
					e.getLocalizedMessage()));

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void dispararDownload() {
		if (fileName != null) {
			try {
				download(fileName, DominioNomeRelatorio.RELATORIO_INTERMEDIARIO_LANCAMENTOS_CONTA.toString() + ".csv");
				gerouArquivo = false;
				fileName = null;

			} catch (IOException e) {
				getLog().error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
						e.getLocalizedMessage()));
			}
		}
	}

	public String imprimirRelatorio() throws BaseException, JRException, SystemException, IOException {
		inicializaReportController(true);
		return "";
	}

	private void inicializaReportController(boolean isDirectPrint) {
		reportController.setIsDirectPrint(isDirectPrint);

		reportController.setCodPaciente(codigo);
		reportController.setProntuario(prontuario);
		reportController.setCthSeq(cthSeq);
		reportController.setContaHospitalarFiltro(contaHospitalar);
		reportController.setNomePaciente(nomePaciente);

		reportController.inicio();
	}

	public String visualizarRelatorio() {
		inicializaReportController(false);
		return "relatorioIntermediarioLancamentosContaPdf";
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(Integer contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public String getCodigoDcih() {
		return codigoDcih;
	}

	public void setCodigoDcih(String codigoDcih) {
		this.codigoDcih = codigoDcih;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
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

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public boolean isVoltouRelatorio() {
		return voltouRelatorio;
	}

	public void setVoltouRelatorio(boolean voltouRelatorio) {
		this.voltouRelatorio = voltouRelatorio;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public DynamicDataModel<VFatContaHospitalarPac> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<VFatContaHospitalarPac> dataModel) {
		this.dataModel = dataModel;
	}
}