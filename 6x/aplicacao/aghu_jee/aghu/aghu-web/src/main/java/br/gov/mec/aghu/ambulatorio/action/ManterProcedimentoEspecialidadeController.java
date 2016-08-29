package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacProcedHospEspecialidades;
import br.gov.mec.aghu.model.AacProcedHospEspecialidadesId;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterProcedimentoEspecialidadeController extends ActionController{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1705940570456428530L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	

	private AghEspecialidades especialidade;
	private FatProcedHospInternos procedimento;
	private Boolean consulta = false;
	
	private List<AacProcedHospEspecialidades> listaProcedimentos = new ArrayList<AacProcedHospEspecialidades>(0);
	private AacProcedHospEspecialidades procedimentoEspecialidade = new AacProcedHospEspecialidades();
	private DominioOperacoesJournal operacao = DominioOperacoesJournal.INS;
	private Boolean edicao = false;
	private Boolean validarAgendamento = true;
	private Boolean exibirModalAgendamento = false;

	public enum ManterProcedimentoEspecialidadeControllerExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_PROC_ESP, PROC_ESP_ATUALIZADA_SUCESSO, PROC_ESP_INSERIDA_SUCESSO, PROC_ESP_EXCLUIDA_SUCESSO,
		VALIDAR_AGENDAMENTO
		;
	}

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void pesquisar() {
		listaProcedimentos = ambulatorioFacade.listarProcedimentosEspecialidadesPorEspecialidade(especialidade.getSeq());
	}
	
	public void limpar() {
		especialidade = null;
		procedimento = null;
		consulta = false;
		edicao = false;
		validarAgendamento = true;
		exibirModalAgendamento = false;
		operacao = DominioOperacoesJournal.INS;
		procedimentoEspecialidade = new AacProcedHospEspecialidades();
		listaProcedimentos = new ArrayList<AacProcedHospEspecialidades>(0);
	}
	
	public void editar(AacProcedHospEspecialidades procedEsp) {
		this.procedimentoEspecialidade = procedEsp;
		procedimento = procedimentoEspecialidade.getProcedHospInterno();
		consulta = procedimentoEspecialidade.getConsulta();
		operacao = DominioOperacoesJournal.UPD;
		edicao = true;
	}
	
	public void cancelarEdicao() {
		edicao = false;
		consulta = false;
		procedimento = null;
		exibirModalAgendamento = false;
		validarAgendamento = true;
		procedimentoEspecialidade = new AacProcedHospEspecialidades();
		operacao = DominioOperacoesJournal.INS;
	}
	
	public void excluir(AacProcedHospEspecialidades procedEsp) {
		ambulatorioFacade.excluirProcedimentoEspecialidade(procedEsp.getId().getEspSeq(), procedEsp.getId().getPhiSeq());		
		apresentarMsgNegocio(Severity.INFO,  ManterProcedimentoEspecialidadeControllerExceptionCode.PROC_ESP_EXCLUIDA_SUCESSO.toString());
		listaProcedimentos = ambulatorioFacade.listarProcedimentosEspecialidadesPorEspecialidade(especialidade.getSeq());
		this.procedimentoEspecialidade = new AacProcedHospEspecialidades(); 
	}
	
	public void gravarComConfirmacao() {
		validarAgendamento = false;
		gravar();
	}
	
	public void gravar() {
		try {
			if(procedimentoEspecialidade.getId() == null || procedimentoEspecialidade.getId().getPhiSeq() == null) {
				procedimentoEspecialidade.setId(new AacProcedHospEspecialidadesId(especialidade.getSeq(), procedimento.getSeq()));
				procedimentoEspecialidade.setProcedHospInterno(procedimento);
				procedimentoEspecialidade.setEspecialidade(especialidade);
			}
			procedimentoEspecialidade.setConsulta(consulta);

			
			if(validarAgendamento && DominioOperacoesJournal.UPD.equals(operacao) && ambulatorioFacade.validaGradeAgendamento(procedimentoEspecialidade)) {
				throw new ApplicationBusinessException(ManterProcedimentoEspecialidadeControllerExceptionCode.VALIDAR_AGENDAMENTO);
			}
			ambulatorioFacade.persistirProcedimentoEspecialidade(procedimentoEspecialidade, operacao);
			
			
			if(edicao) {
				apresentarMsgNegocio(Severity.INFO,  ManterProcedimentoEspecialidadeControllerExceptionCode.PROC_ESP_ATUALIZADA_SUCESSO.toString());
			} else {
				apresentarMsgNegocio(Severity.INFO,  ManterProcedimentoEspecialidadeControllerExceptionCode.PROC_ESP_INSERIDA_SUCESSO.toString());
			}			
			this.cancelarEdicao();
		} catch (ApplicationBusinessException e) {
			if(e.getCode().equals(ManterProcedimentoEspecialidadeControllerExceptionCode.VALIDAR_AGENDAMENTO)) {
				exibirModalAgendamento = true;
			} else {
				apresentarExcecaoNegocio(e);
				if (!edicao) {
					procedimentoEspecialidade = new AacProcedHospEspecialidades();	
				}
			}
			
		} catch (Exception e) {			
			apresentarMsgNegocio(Severity.ERROR,  ManterProcedimentoEspecialidadeControllerExceptionCode.ERRO_PERSISTIR_PROC_ESP.toString());
			if (!edicao) {
				procedimentoEspecialidade = new AacProcedHospEspecialidades();	
			}			
			
		} finally{
			listaProcedimentos = ambulatorioFacade.listarProcedimentosEspecialidadesPorEspecialidade(especialidade.getSeq());
		}
		
	}
	
	public String isSimNao(Boolean valor) {
		return DominioSimNao.getInstance(valor).getDescricao();
	}
	
	public List<AghEspecialidades> listarEspecialidades(String objPesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarEspecialidadePorNomeOuSigla(objPesquisa!=null?objPesquisa:null), listarEspecialidadesCount(objPesquisa));
	}
	
	public Long listarEspecialidadesCount(String objPesquisa){
		return aghuFacade.pesquisarEspecialidadePorSiglaNomeCount(objPesquisa);
	}
	
	public List<FatProcedHospInternos> listarProcedimentos(String objPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.obterProcedimentoLimitadoPeloMaterial(objPesquisa), this.listarProcedimentosCount(objPesquisa)) ;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatProcedHospInternos>();
		}
	}
	
	public Long listarProcedimentosCount(String objPesquisa) {
		try {
			return faturamentoFacade.obterProcedimentoLimitadoPeloMaterialCount(objPesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public FatProcedHospInternos getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(FatProcedHospInternos procedimento) {
		this.procedimento = procedimento;
	}

	public Boolean getConsulta() {
		return consulta;
	}

	public void setConsulta(Boolean consulta) {
		this.consulta = consulta;
	}

	public List<AacProcedHospEspecialidades> getListaProcedimentos() {
		return listaProcedimentos;
	}

	public void setListaProcedimentos(
			List<AacProcedHospEspecialidades> listaProcedimentos) {
		this.listaProcedimentos = listaProcedimentos;
	}

	public AacProcedHospEspecialidades getProcedimentoEspecialidade() {
		return procedimentoEspecialidade;
	}

	public void setProcedimentoEspecialidade(
			AacProcedHospEspecialidades procedimentoEspecialidade) {
		this.procedimentoEspecialidade = procedimentoEspecialidade;
	}

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Boolean getValidarAgendamento() {
		return validarAgendamento;
	}

	public void setValidarAgendamento(Boolean validarAgendamento) {
		this.validarAgendamento = validarAgendamento;
	}

	public Boolean getExibirModalAgendamento() {
		return exibirModalAgendamento;
	}

	public void setExibirModalAgendamento(Boolean exibirModalAgendamento) {
		this.exibirModalAgendamento = exibirModalAgendamento;
	}
}
