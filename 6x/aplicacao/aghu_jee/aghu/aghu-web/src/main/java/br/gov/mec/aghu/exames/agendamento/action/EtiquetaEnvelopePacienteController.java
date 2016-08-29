package br.gov.mec.aghu.exames.agendamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.EtiquetaEnvelopePacienteVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class EtiquetaEnvelopePacienteController extends ActionController {

	private static final long serialVersionUID = -6181598735126731378L;

	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	private Integer codigoSolicitacao;
	
	private AelSolicitacaoExames solicitacaoExame;
	
	private ImpImpressora impressora;
	
	private String nomePaciente;
	
	private Integer prontuario;
	
	private Short unfSeq;
	
	private String voltarPara;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

		if(this.getCodigoSolicitacao()!=null){
			this.solicitacaoExame = this.examesFacade.obterAelSolicitacaoExamesPeloId(codigoSolicitacao);
			AghAtendimentos atendimento = this.getSolicitacaoExame().getAtendimento();
			if(atendimento!=null){
				AipPacientes paciente = atendimento.getPaciente();
				this.nomePaciente = paciente.getNome();
				this.prontuario = paciente.getProntuario();
			}
		}
	
	}
	
	// Metodo para Suggestion Box de impressoras
	public List<ImpImpressora> pesquisarImpressora(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarImpressora(paramPesquisa, true);
	}
	
	// Metodo para Suggestion Box de Solicitações
	public List<AelSolicitacaoExames> buscarAelSolicitacaoExames(String valor){
		return examesFacade.buscarAelSolicitacaoExames((String)valor);
	}
	
	public void selecionarPaciente(){
		if(this.getSolicitacaoExame()!=null){
			this.solicitacaoExame = this.examesFacade.obterAelSolicitacaoExamesPeloId(this.getSolicitacaoExame().getSeq());
			AghAtendimentos atendimento = this.getSolicitacaoExame().getAtendimento();
			if(atendimento!=null){
				AipPacientes paciente = atendimento.getPaciente();
				this.nomePaciente = paciente.getNome();
				this.prontuario = paciente.getProntuario();
			}
			this.codigoSolicitacao = this.getSolicitacaoExame().getSeq();
		}
	}
	
	public void limpar(){
		this.impressora = null;
		this.nomePaciente = null;
		this.prontuario = null;
		this.solicitacaoExame = null;
		this.codigoSolicitacao = null;
	}
	
	public void limparCampos(){
		this.nomePaciente = null;
		this.prontuario = null;
		this.solicitacaoExame = null;
	}
	
	public String voltar() {
		String retorno = voltarPara;
		this.codigoSolicitacao = null;
		this.solicitacaoExame = null;
		this.impressora = null;
		this.nomePaciente = null;
		this.prontuario = null;
		this.unfSeq = null;
		this.voltarPara = null;
		return retorno;
	}
	
	public void imprimirEnvelopePaciente() throws BaseException {
		List<EtiquetaEnvelopePacienteVO> colecao = agendamentoExamesFacade.pesquisarEtiquetaEnvelopePaciente(this.codigoSolicitacao, this.unfSeq);
		if (colecao != null && !colecao.isEmpty()) {
			for (EtiquetaEnvelopePacienteVO etiqueta : colecao) {
				String zpl = this.solicitacaoExameFacade.gerarEtiquetaEnvelopePaciente(etiqueta.getNomePaciente(), etiqueta.getSoeSeq(),
						etiqueta.getNomeUnidadeFuncional(), etiqueta.getDataAgenda(), etiqueta.getProntuario());
				
				this.sistemaImpressao.imprimir(zpl, impressora.getFilaImpressora());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_ENVELOPE_PACIENTE");
			}
		}else{
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_INSUCESSO_IMPRESSAO_ENVELOPE_PACIENTE");
		}
		this.unfSeq = null;
	}
	
	public void imprimirEnvelopePaciente(ImpImpressora impressoraEtiqueta, Integer codigoSolicitacaoExame, AghUnidadesFuncionais unidadeFuncional) throws BaseException {
		this.impressora = impressoraEtiqueta;
		this.codigoSolicitacao = codigoSolicitacaoExame;
		if(unidadeFuncional != null) {
			this.unfSeq = unidadeFuncional.getSeq();
		}
		this.imprimirEnvelopePaciente();
	}
	
	
	public Integer getCodigoSolicitacao() {
		return codigoSolicitacao;
	}

	public void setCodigoSolicitacao(Integer codigoSolicitacao) {
		this.codigoSolicitacao = codigoSolicitacao;
	}

	public AelSolicitacaoExames getSolicitacaoExame() {
		return solicitacaoExame;
	}

	public void setSolicitacaoExame(AelSolicitacaoExames solicitacaoExame) {
		this.solicitacaoExame = solicitacaoExame;
	}

	public ImpImpressora getImpressora() {
		return impressora;
	}

	public void setImpressora(ImpImpressora impressora) {
		this.impressora = impressora;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	
}
