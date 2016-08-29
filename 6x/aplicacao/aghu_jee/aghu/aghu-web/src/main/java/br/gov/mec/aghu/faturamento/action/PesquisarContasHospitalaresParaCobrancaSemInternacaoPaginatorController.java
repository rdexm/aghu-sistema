package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FatDadosContaSemIntVO;
import br.gov.mec.aghu.internacao.vo.PacienteComponenteVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarContasHospitalaresParaCobrancaSemInternacaoPaginatorController extends ActionController implements ActionPaginator {

	public enum PesquisarContasHospitalaresParaCobrancaSemInternacaoPaginatorControllerExceptionCode implements BusinessExceptionCode {
		FAT_00731

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6196688100380918636L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	// FILTROS
	private Integer cthSeq;

	private Integer prontuarioControle;

	private Integer codigoControle;

	private Boolean editando = false;

	private Integer pacCodigoFonetica;

	private PacienteComponenteVO paciente;

	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;

	@Inject @Paginator
	private DynamicDataModel<FatDadosContaSemIntVO> dataModel;

	@Inject
	private ContasHospitalaresParaCobrancaSemInternacaoController contasHospitalaresParaCobrancaSemInternacaoController;
	
	@PostConstruct
	protected void init() { 
		begin(conversation);
	}

	private void populaControles() {
		if (paciente != null) {
			prontuarioControle = paciente.getProntuario();
			codigoControle = paciente.getCodigo();
		}
	}

	public void inicio() {
	 

		if (editando) {
			return;
		}

		if (paciente == null || paciente.getCodigo() == null) {
			CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
			if (codPac != null && codPac.getCodigo() > 0) {
				final AipPacientes pac = pacienteFacade.obterNomePacientePorCodigo(codPac.getCodigo());

				if (pac != null) {
					limparPesquisa();
					paciente = new PacienteComponenteVO(pac.getCodigo(), pac.getProntuario(), pac.getNome());
					populaControles();
				}
			} else {
				paciente = new PacienteComponenteVO();
			}
		}

	
	}

	@Override
	public Long recuperarCount() {
		return faturamentoFacade.pesquisarContaHospitalarParaCobrancaSemInternacaoCount(paciente.getProntuario(), paciente.getCodigo(),
				this.cthSeq);
	}

	@Override
	public List<FatDadosContaSemIntVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return faturamentoFacade.pesquisarContaHospitalarParaCobrancaSemInternacao(paciente.getProntuario(), paciente.getCodigo(), cthSeq,
				firstResult, maxResult, orderProperty, asc);

	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		cthSeq = null;
		paciente = new PacienteComponenteVO();
		dataModel.setPesquisaAtiva(false);
		populaControles();
	}

	public void pesquisar() {
		if (paciente.getCodigo() == null && paciente.getProntuario() == null && cthSeq == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_PESQUISA_FAT_DADOS_CONTA_SEM_INTERNACAO");

		} else {
			if (cthSeq != null) {
				final VFatContaHospitalarPac vPaciente = faturamentoFacade.obterVFatContaHospitalarPac(cthSeq);

				if (vPaciente != null) {
					final AipPacientes pac = vPaciente.getPaciente();
					paciente = new PacienteComponenteVO(pac.getCodigo(), pac.getProntuario(), pac.getNome());
					populaControles();
				} else {
					paciente = new PacienteComponenteVO();
					populaControles();
				}

			} else {
				final Integer codigoPac = paciente.getCodigo();
				final Integer prontuario = paciente.getProntuario();

				if (!CoreUtil.igual(codigoPac, codigoControle)) {
					obterPacientePorCodigo();

				} else if (!CoreUtil.igual(prontuario, prontuarioControle)) {
					obterPacientePorProntuario();
				}
			}

			if (paciente.getNome() != null || cthSeq != null) {
				dataModel.reiniciarPaginator();
			}
		}
	}

	public String redirecionarPesquisaFonetica() {
		paciente = new PacienteComponenteVO();
		return "paciente-pesquisaPacienteComponente";
	}
	
	public String redirectContasHospitalaresParaCobrancaSemInternacao(){
		return "contasHospitalaresParaCobrancaSemInternacao";
	}
	
	public String redirectContasHospitalaresParaCobrancaSemInternacaoNovo(){
		this.contasHospitalaresParaCobrancaSemInternacaoController.limparCampos();
		return redirectContasHospitalaresParaCobrancaSemInternacao();
	}

	public void obterPacientePorCodigo() {
		final Integer filtro = paciente.getCodigo();

		if (!CoreUtil.igual(codigoControle, filtro)) {
			limparPesquisa();

			if (filtro != null) {
				AipPacientes pac = pacienteFacade.obterNomePacientePorCodigo(filtro);

				if (pac == null) {
					apresentarMsgNegocio(Severity.ERROR,
							PesquisarContasHospitalaresParaCobrancaSemInternacaoPaginatorControllerExceptionCode.FAT_00731.toString());
					limparPesquisa();
					paciente.setCodigo(filtro);
				} else {
					paciente = new PacienteComponenteVO(pac.getCodigo(), pac.getProntuario(), pac.getNome());
					populaControles();
				}
			} else {
				limparPesquisa();
				paciente.setCodigo(filtro);
			}
		}
	}

	public void obterPacientePorProntuario() {
		if (!CoreUtil.igual(paciente.getProntuario(), prontuarioControle)) {
			final Integer filtro = paciente.getProntuario();
			limparPesquisa();

			if (filtro != null) {

				AipPacientes pac = pacienteFacade.obterPacientePorProntuario(filtro);

				if (pac == null) {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PACIENTE_PRONTUARIO_NAO_ENCONTRADO");
					limparPesquisa();
					paciente.setProntuario(filtro);

				} else {
					paciente = new PacienteComponenteVO(pac.getCodigo(), pac.getProntuario(), pac.getNome());
					populaControles();
				}
			} else {
				limparPesquisa();
			}
		}
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}

	public PacienteComponenteVO getPaciente() {
		return paciente;
	}

	public void setPaciente(PacienteComponenteVO paciente) {
		this.paciente = paciente;
	}

	public boolean isShowNew() {
		return (paciente != null && paciente.getNome() != null && dataModel.getPesquisaAtiva());
	}

	
	public DynamicDataModel<FatDadosContaSemIntVO> getDataModel() {
		return dataModel;
	}

	
	public void setDataModel(DynamicDataModel<FatDadosContaSemIntVO> dataModel) {
		this.dataModel = dataModel;
	}

	public ContasHospitalaresParaCobrancaSemInternacaoController getContasHospitalaresParaCobrancaSemInternacaoController() {
		return contasHospitalaresParaCobrancaSemInternacaoController;
	}

	public void setContasHospitalaresParaCobrancaSemInternacaoController(
			ContasHospitalaresParaCobrancaSemInternacaoController contasHospitalaresParaCobrancaSemInternacaoController) {
		this.contasHospitalaresParaCobrancaSemInternacaoController = contasHospitalaresParaCobrancaSemInternacaoController;
	}
}