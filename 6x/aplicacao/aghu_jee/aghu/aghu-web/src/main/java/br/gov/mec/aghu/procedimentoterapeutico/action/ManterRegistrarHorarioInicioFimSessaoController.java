package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioAdministracao;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RegistrarControlePacienteVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterRegistrarHorarioInicioFimSessaoController extends ActionController {


	private static final String PAGE_REGISTRAR_HORARIO_INICIO_FIM_SESSAO = "procedimentoterapeutico-registrarHorarioInicioFimSessao";

	private static final String PAGE_REGISTRAR_INTERCORRENCIA_CRUD = "procedimentoterapeutico-registrarIntercorrenciaCrud";

	private static final long serialVersionUID = 3790164088181712617L;
	
	private static final String MENSAGEM_SUCESSO_GRAVAR = "MENSAGEM_SUCESSO_GRAVAR";
	private static final String TRACO = " - ";
	private static final String T = "T";
	private static final String P = "P";
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@Inject
	private RegistrarIntercorrenciaCrudController registrarIntercorrenciaCrudController;
	
	private Integer seqSessao;
	
	private MptSessao mptSessao;
	
	private Integer prontuario;
	
	private String paciente;
	
	private String prontuarioPaciente;

	private String administracao;

	private String valorParcial = P;  

	private String valorTotal = T;
	
	private String cameFrom;

	private boolean habilitardesabilitarCampos = true;
	
	private Boolean abaEmAtendimento = Boolean.FALSE;
	
	//#41771
	private List <RegistrarControlePacienteVO> listaIntercorrencia;
	private RegistrarControlePacienteVO selecionado;
	private Integer pacCodigo;
	
	private Date dataInicio;
	
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	
	
	public void iniciar(){
		mptSessao = new MptSessao();
		administracao = null;
		if(prontuario != null && (paciente != null && !paciente.isEmpty())){
			prontuarioPaciente = String.valueOf(prontuario).concat(TRACO).concat(paciente);
		}
		obterListaIntercorrencia();
		if (!habilitardesabilitarCampos){			
			dataInicio = procedimentoTerapeuticoFacade.pesquisarHoraInicio(seqSessao);
			mptSessao.setDthrInicio(dataInicio);
			mptSessao.setDthrFim(new Date());
		}else{
			mptSessao.setDthrInicio(null);
			mptSessao.setDthrFim(null);
		}
	}
	
	/**
	 * Atualiza a tabela MPT_SESSAO.
	 * @throws ApplicationBusinessException 
	 */
	public String gravar() {
		
		try {
			procedimentoTerapeuticoFacade.validarHorario(mptSessao);
			
			if (administracao.equals(P)){
				mptSessao.setTipoAdministracao(DominioAdministracao.P);			
			}else{
				mptSessao.setTipoAdministracao(DominioAdministracao.T);
			}	
			mptSessao.setSeq(seqSessao);
			procedimentoTerapeuticoFacade.inserir(mptSessao, dataInicio);
			procedimentoTerapeuticoFacade.concluirAtendimento(seqSessao);
			
			this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_GRAVAR);
			
			if(abaEmAtendimento){
				return this.voltar();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}	
		return null;
	}

	public String voltar() {
		limpar();
		return cameFrom;
	}
	
	public void obterListaIntercorrencia(){
		if(pacCodigo != null && seqSessao != null){
			listaIntercorrencia = procedimentoTerapeuticoFacade.carregarRegistrosIntercorrencia(pacCodigo, seqSessao);
		}
	}
	
	/**
	 * Acessar Controles do Paciente.
	 */
	public void controlesPaciente() {
		//TODO Implementação futura.
	}

	/**
	 * Acessar Intercorrências.
	 */
	public String redirecionarIntercorrencia(){
		registrarIntercorrenciaCrudController.setSessao(seqSessao);
		registrarIntercorrenciaCrudController.setTelaAnterior(PAGE_REGISTRAR_HORARIO_INICIO_FIM_SESSAO);
		return PAGE_REGISTRAR_INTERCORRENCIA_CRUD;
	}
	
	public void limpar(){
		mptSessao = new MptSessao();
		administracao = null;
		prontuario = null;
		seqSessao = null;
		pacCodigo = null;
		listaIntercorrencia = null;
		selecionado = null;
		paciente = null;
		prontuarioPaciente = null;
		habilitardesabilitarCampos = true;
		
	}

	public MptSessao getMptSessao() {
		return mptSessao;
	}


	public void setMptSessao(MptSessao mptSessao) {
		this.mptSessao = mptSessao;
	}


	public String getValorParcial() {
		return valorParcial;
	}


	public void setValorParcial(String valorParcial) {
		this.valorParcial = valorParcial;
	}


	public String getValorTotal() {
		return valorTotal;
	}


	public void setValorTotal(String valorTotal) {
		this.valorTotal = valorTotal;
	}


	public IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}


	public void setProcedimentoTerapeuticoFacade(
			IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade) {
		this.procedimentoTerapeuticoFacade = procedimentoTerapeuticoFacade;
	}


	public String getAdministracao() {
		return administracao;
	}


	public void setAdministracao(String administracao) {
		this.administracao = administracao;
	}


	public String getPaciente() {
		return paciente;
	}


	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}


	public Integer getSeqSessao() {
		return seqSessao;
	}


	public void setSeqSessao(Integer seqSessao) {
		this.seqSessao = seqSessao;
	}


	public String getCameFrom() {
		return cameFrom;
	}


	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
	
	public boolean isHabilitardesabilitarCampos() {
		return habilitardesabilitarCampos;
	}


	public void setHabilitardesabilitarCampos(boolean habilitardesabilitarCampos) {
		this.habilitardesabilitarCampos = habilitardesabilitarCampos;
	}


	public Integer getProntuario() {
		return prontuario;
	}


	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public String getProntuarioPaciente() {
		return prontuarioPaciente;
	}


	public void setProntuarioPaciente(String prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}


	public List<RegistrarControlePacienteVO> getListaIntercorrencia() {
		return listaIntercorrencia;
	}


	public void setListaIntercorrencia(List<RegistrarControlePacienteVO> listaIntercorrencia) {
		this.listaIntercorrencia = listaIntercorrencia;
	}


	public RegistrarControlePacienteVO getSelecionado() {
		return selecionado;
	}


	public void setSelecionado(RegistrarControlePacienteVO selecionado) {
		this.selecionado = selecionado;
	}


	public Integer getPacCodigo() {
		return pacCodigo;
	}


	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}


	public Boolean getAbaEmAtendimento() {
		return abaEmAtendimento;
	}


	public void setAbaEmAtendimento(Boolean abaEmAtendimento) {
		this.abaEmAtendimento = abaEmAtendimento;
	}
}
