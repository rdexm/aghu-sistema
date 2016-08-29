package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ReimpressaoLaudosProcedimentosVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;

public class ReImpressaoLaudosProcedimentosController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3597957390722419651L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	private Integer cthSeq;

	private String from;

	private VFatContaHospitalarPac contaHospitalar;

	private Integer pacProntuario;

	private Integer pacCodigo;

	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;

	private AipPacientes paciente;

	@Inject
	private Instance<ReImpressaoLaudosProcedimentosRelatorioPdfController> reportController;

	@Inject @Paginator
	private DynamicDataModel<ReimpressaoLaudosProcedimentosVO> dataModel;

	@PostConstruct
	protected void init() {
		begin(conversation);
	}

	public String inicio() {
	 

		trataRetornoPesquisaFonetica();

		if (cthSeq != null) { // redirecionado com parametro
			contaHospitalar = this.faturamentoFacade.obterContaHospitalarPaciente(this.cthSeq);

			if (contaHospitalar == null) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_CARREGAR_CONTA_HOSPITALAR", this.cthSeq);
				return from;
			}
			dataModel.reiniciarPaginator();
		}
		return null;
	
	}

	private void trataRetornoPesquisaFonetica() {
		if (paciente == null || paciente.getCodigo() == null) {
			CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
			if (codPac != null && codPac.getCodigo() > 0) { 
				paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac.getCodigo());
				if (paciente != null) {
					pacProntuario = paciente.getProntuario();
					pacCodigo = paciente.getCodigo();
					dataModel.setPesquisaAtiva(false);
				}
			}
		}
	}

	public void imprimirRelatorio() {
		inicializaReportController(true);
	}

	public String visualizarRelatorio() {
		inicializaReportController(false);
		return "reimpressaoLaudosProcedimentosRelatorioPdf";

	}

	private void inicializaReportController(boolean isDirectPrint) {
		reportController.get().setDirectPrint(isDirectPrint);
		reportController.get().inicio();
	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {

		if (this.pacProntuario == null && this.pacCodigo == null) {
			apresentarMsgNegocio(Severity.ERROR, "FILTRO_OBRIGATORIO_PESQUISA_CONTA");
			return;
		}

		this.paciente = this.faturamentoFacade.pesquisarAbertaFechadaEncerradaPaciente(this.pacProntuario, null, this.pacCodigo, null);

		if (paciente != null) {
			pacProntuario = paciente.getProntuario();
			pacCodigo = paciente.getCodigo();
		}
		dataModel.reiniciarPaginator();
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.pacProntuario = null;
		this.pacCodigo = null;
		this.cthSeq = null;
		this.paciente = new AipPacientes();
		dataModel.limparPesquisa();
	}

	public void obterPacientePorCodigo() {
		final Integer filtro = pacCodigo;
		limparPesquisa();

		if (filtro != null) {
			paciente = pacienteFacade.obterNomePacientePorCodigo(filtro);
			if (paciente != null) {
				pacProntuario = paciente.getProntuario();
				pacCodigo = paciente.getCodigo();

			} else {
				apresentarMsgNegocio("FAT_00731");
			}
		}
	}

	public void obterPacientePorProntuario() {
		final Integer filtro = pacProntuario;
		limparPesquisa();

		if (filtro != null) {
			paciente = pacienteFacade.obterPacientePorProntuario(filtro);

			if (paciente != null) {
				pacProntuario = paciente.getProntuario();
				pacCodigo = paciente.getCodigo();
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PACIENTE_PRONTUARIO_NAO_ENCONTRADO");
			}
		}
	}

	public String redirecionarPesquisaFonetica() {
		paciente = new AipPacientes();
		return "paciente-pesquisaPacienteComponente";
	}

	@Override
	public Long recuperarCount() {
		if (contaHospitalar != null) {
			return aghuFacade.listarReimpressaoLaudosProcedimentosVOCount(contaHospitalar.getPacCodigo(),
					contaHospitalar.getPacProntuario());
		} else {
			return aghuFacade.listarReimpressaoLaudosProcedimentosVOCount(pacCodigo, pacProntuario);
		}
	}

	@Override
	public List<ReimpressaoLaudosProcedimentosVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		if (contaHospitalar != null) {
			return aghuFacade.listarReimpressaoLaudosProcedimentosVO(firstResult, maxResult, orderProperty, asc,
					contaHospitalar.getPacCodigo(), contaHospitalar.getPacProntuario());
		} else {
			return aghuFacade.listarReimpressaoLaudosProcedimentosVO(firstResult, maxResult, orderProperty, asc, pacCodigo, pacProntuario);
		}
	}

	public String voltar() {
		return this.from != null ? from : "encerramentoPreviaConta";
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public VFatContaHospitalarPac getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(VFatContaHospitalarPac contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public DynamicDataModel<ReimpressaoLaudosProcedimentosVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ReimpressaoLaudosProcedimentosVO> dataModel) {
		this.dataModel = dataModel;
	}



}
