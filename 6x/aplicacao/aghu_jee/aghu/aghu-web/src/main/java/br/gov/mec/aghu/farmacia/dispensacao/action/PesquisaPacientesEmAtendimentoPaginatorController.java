package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.farmacia.vo.MedicoResponsavelVO;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.vo.PacientesEmAtendimentoVO;


public class PesquisaPacientesEmAtendimentoPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -8913134700455418041L;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB 
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private DispensacaoDePrescricaoNaoEletronicaController dispensacaoDePrescricaoNaoEletronicaController;
	
	@Inject
	private SecurityController securityController;
	
	private Integer pacCodigo;
	private Integer prontuario;
	private AipPacientes paciente;
	private String leito;
	private AghUnidadesFuncionais unidade;
	private MedicoResponsavelVO responsavel;
	private DominioOrigemAtendimento origem;
	private AghEspecialidades especialidade;
	
	private List<AfaPrescricaoMedicamento> prescricoesMedicamentos;
	private PacientesEmAtendimentoVO pacienteAtendimentoSelecionado;
	private Integer parametroNumeroDiasPrescricao;
	private Boolean possuiPrescricoesMedicamentos;
	private Long countListaPaginada = null;
	
	private Boolean inserirPrescricaoMedicamentos;/* =	securityController.usuarioTemPermissao("dispensacaoMedicamentosNaoEletronica", "inserirPrescricaoMedicamentos", false);*/
	
	@Inject @Paginator
	private DynamicDataModel<PacientesEmAtendimentoVO> dataModel;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void iniciarPagina(){
		countListaPaginada = null;
		
		if (pacCodigo != null) {
			paciente = pacienteFacade.obterPaciente(pacCodigo);
			prontuario = paciente.getProntuario();
		}
		prescricoesMedicamentos = null;
		pacienteAtendimentoSelecionado = null;
		try {
			parametroNumeroDiasPrescricao = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_NRO_DIAS_PRESCRICAO_MDTOS).getVlrNumerico().intValue();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		possuiPrescricoesMedicamentos = Boolean.FALSE;
		if(inserirPrescricaoMedicamentos == null){
			inserirPrescricaoMedicamentos = securityController.usuarioTemPermissao("dispensacaoMedicamentosNaoEletronica", "inserirPrescricaoMedicamentos", false);
		}
	
	}
	
	@Override
	public Long recuperarCount() {
		if (countListaPaginada == null) {
			Short unfSeq = null;
			Integer matriculaResp = null;
			Short vinCodigoResp = null;
			Short espSeq = null;
			if(unidade != null){
				unfSeq = unidade.getSeq();
			}
			if(responsavel != null){
				matriculaResp = responsavel.getMatricula();
				vinCodigoResp = responsavel.getVinCodigo();
			}
			if(especialidade != null){
				espSeq = especialidade.getSeq();
			}
			try {
				countListaPaginada = farmaciaFacade.listarPacientesEmAtendimentoCount(pacCodigo, leito, unfSeq, matriculaResp, vinCodigoResp, origem, espSeq);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return countListaPaginada;
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public List<PacientesEmAtendimentoVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		Short unfSeq = null;
		Integer matriculaResp = null;
		Short vinCodigoResp = null;
		Short espSeq = null;
		if(unidade != null){
			unfSeq = unidade.getSeq();
		}
		if(responsavel != null){
			matriculaResp = responsavel.getMatricula();
			vinCodigoResp = responsavel.getVinCodigo();
		}
		if(especialidade != null){
			espSeq = especialidade.getSeq();
		}
		try {
			return farmaciaFacade.listarPacientesEmAtendimento(firstResult, maxResult, orderProperty, asc, pacCodigo, leito, unfSeq, matriculaResp, vinCodigoResp, origem, espSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<PacientesEmAtendimentoVO>();
		}
	
	}
	
	public String pesquisarPrescricoesMedicamentos(PacientesEmAtendimentoVO pacienteAtendimento) {
		obterPrescricoesMedicamentos(pacienteAtendimento);
		String retorno;
		if (prescricoesMedicamentos.isEmpty()) {
			if(!inserirPrescricaoMedicamentos){
				apresentarExcecaoNegocio(new ApplicationBusinessException("USUARIO_SEM_PERMISSAO_DE_CRIAR_PRESCRICAO_NAO_ELETRONICA", Severity.ERROR));
				return null;
			}
			retorno = "dispensacaoDePrescricaoNaoEletronicaList";
		} else {
			retorno = null;
		}
		dispensacaoDePrescricaoNaoEletronicaController.setSeqAfaPrescricaoMedicamento(null);
		dispensacaoDePrescricaoNaoEletronicaController.setPacienteEmAtendimentoSelecionado(pacienteAtendimento);
		return retorno; 
	}
	
	public Boolean possuiPrescricoes(PacientesEmAtendimentoVO pacienteAtendimento) {
		obterPrescricoesMedicamentos(pacienteAtendimento);
		if (prescricoesMedicamentos.isEmpty()) {
			possuiPrescricoesMedicamentos = false;
		} else {
			possuiPrescricoesMedicamentos = true;
		}
		 
		return possuiPrescricoesMedicamentos;
	}

	private void obterPrescricoesMedicamentos(PacientesEmAtendimentoVO pacienteAtendimento) {
		pacienteAtendimentoSelecionado = pacienteAtendimento;
		Integer prontuario = pacienteAtendimento.getPacProntuario();
		prescricoesMedicamentos = farmaciaDispensacaoFacade.pesquisarPrescricaoMedicamentos(prontuario, getDtReferenciaMinima());
	}

	private Date getDtReferenciaMinima() {
		Integer dias = parametroNumeroDiasPrescricao;
		Date dtReferenciaMinima = DateUtil.adicionaDias(DateUtil.truncaData(new Date()),-dias);
		return dtReferenciaMinima;
	}
	
	public String selecionaPrescricao(AfaPrescricaoMedicamento prescricaoMedicamento) {
		dispensacaoDePrescricaoNaoEletronicaController.setSeqAfaPrescricaoMedicamento(prescricaoMedicamento.getSeq());
		return "dispensacaoDePrescricaoNaoEletronicaList";
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidades(String strPesquisa){
		return this.returnSGWithCount(farmaciaFacade.pesquisarPorDescricaoCodigoAtivaAssociada(strPesquisa),pesquisarUnidadesCount(strPesquisa));
	}
	
	public Long pesquisarUnidadesCount(String strPesquisa){
		return farmaciaFacade.pesquisarPorDescricaoCodigoAtivaAssociadaCount(strPesquisa);
	}
	
	public List<MedicoResponsavelVO> pesquisarMedicoResponsavel(String strPesquisa){
		return this.returnSGWithCount(farmaciaFacade.pesquisarMedicoResponsavel(strPesquisa, null, null),pesquisarMedicoResponsavelCount(strPesquisa));
	}
	
	public Long pesquisarMedicoResponsavelCount(String strPesquisa){
		return aghuFacade.obterProfessoresInternacaoTodosCount(strPesquisa, null, null);
	}
	
	public List<AghEspecialidades> pesquisarEspecialidades(String strPesquisa){
		return this.returnSGWithCount(aghuFacade.pesquisarEspecialidades(strPesquisa),pesquisarEspecialidadesCount(strPesquisa));
	}
	
	public Long pesquisarEspecialidadesCount(String strPesquisa){
		return aghuFacade.pesquisarEspecialidadesCount(strPesquisa);
	}
	
	public String redirecionarPesquisaFonetica() {
		return "paciente-pesquisaPacienteComponente";
	}
	
	public String pesquisar() {
		countListaPaginada = null;
		possuiPrescricoesMedicamentos = Boolean.FALSE;
		if(prontuario != null){
			paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
		}else{ 
			if(pacCodigo != null){
				paciente = pacienteFacade.obterPacientePorCodigo(pacCodigo);
			}
		}
		pacCodigo = paciente != null ? paciente.getCodigo() : null;
		prontuario = paciente != null ? paciente.getProntuario() : null;	
		
		return pesquisarPacientesEmAtendimento();
	}
	
	private String pesquisarPacientesEmAtendimento() {
		//Mudar para que seja forçado chamar recuperarListaPaginada;
		dataModel.reiniciarPaginator();
		
		if(recuperarCount() == 1 && (paciente != null || (leito != null && !"".equals(leito)))){
			List<PacientesEmAtendimentoVO> pacientes = recuperarListaPaginada(0, 1, null, true);
			PacientesEmAtendimentoVO resultadoUnico = pacientes.get(0); 
			prescricoesMedicamentos = farmaciaDispensacaoFacade.
				pesquisarPrescricaoMedicamentos(
						resultadoUnico.getPacProntuario(), getDtReferenciaMinima());
			dispensacaoDePrescricaoNaoEletronicaController.setPacienteEmAtendimentoSelecionado(resultadoUnico);
			if(prescricoesMedicamentos.isEmpty()){
				if(!inserirPrescricaoMedicamentos){
					possuiPrescricoesMedicamentos = Boolean.FALSE;
					apresentarExcecaoNegocio(new ApplicationBusinessException("USUARIO_SEM_PERMISSAO_DE_CRIAR_PRESCRICAO_NAO_ELETRONICA", Severity.ERROR));
					return null;
				}
				dispensacaoDePrescricaoNaoEletronicaController.setSeqAfaPrescricaoMedicamento(null);
				return "dispensacaoDePrescricaoNaoEletronicaList";
			}else{
				pacienteAtendimentoSelecionado = resultadoUnico;
				possuiPrescricoesMedicamentos = Boolean.TRUE;
			}
			
		}
		return null;
		
	}

	public void limpar(){
		pacCodigo = null;
		prontuario = null;
		paciente = null;
		origem = null;
		leito = null;
		unidade = null;
		responsavel = null;
		especialidade = null;
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}
	
	//Getters and Setters

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public void setOrigem(DominioOrigemAtendimento origem) {
		this.origem = origem;
	}
	
	public DominioOrigemAtendimento[] origensAtendimento() {
		return (DominioOrigemAtendimento[]) Arrays.asList(
				DominioOrigemAtendimento.I, DominioOrigemAtendimento.N, DominioOrigemAtendimento.U, DominioOrigemAtendimento.A
				).toArray();
	}

	public DominioOrigemAtendimento getOrigem() {
		return origem;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getLeito() {
		return leito;
	}

	public void setUnidade(AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}

	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}

	public void setResponsavel(MedicoResponsavelVO responsavel) {
		this.responsavel = responsavel;
	}

	public MedicoResponsavelVO getResponsavel() {
		return responsavel;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public List<AfaPrescricaoMedicamento> getPrescricoesMedicamentos() {
		return prescricoesMedicamentos;
	}

	public PacientesEmAtendimentoVO getPacienteAtendimentoSelecionado() {
		return pacienteAtendimentoSelecionado;
	}

	public Integer getParametroNumeroDiasPrescricao() {
		return parametroNumeroDiasPrescricao;
	}

	public Boolean getPossuiPrescricoesMedicamentos() {
		return possuiPrescricoesMedicamentos;
	}

	public void setPrescricoesMedicamentos(
			List<AfaPrescricaoMedicamento> prescricoesMedicamentos) {
		this.prescricoesMedicamentos = prescricoesMedicamentos;
	}

	public void setPacienteAtendimentoSelecionado(
			PacientesEmAtendimentoVO pacienteAtendimentoSelecionado) {
		this.pacienteAtendimentoSelecionado = pacienteAtendimentoSelecionado;
	}

	public void setParametroNumeroDiasPrescricao(
			Integer parametroNumeroDiasPrescricao) {
		this.parametroNumeroDiasPrescricao = parametroNumeroDiasPrescricao;
	}

	public void setPossuiPrescricoesMedicamentos(
			Boolean possuiPrescricoesMedicamentos) {
		this.possuiPrescricoesMedicamentos = possuiPrescricoesMedicamentos;
	}

	public DynamicDataModel<PacientesEmAtendimentoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PacientesEmAtendimentoVO> dataModel) {
		this.dataModel = dataModel;
	}
}