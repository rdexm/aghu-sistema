package br.gov.mec.aghu.controleinfeccao.action;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoFatorPredisponenteVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MciFatorPredisponentes;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class CadastroNotificacaoFatorPredisponenteController extends ActionController {

    private static final long serialVersionUID = -1764123181648909556L;

    @EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private Integer codigoPaciente;
	
	private AipPacientes paciente;
	
	private Integer idade;
	
	private MciFatorPredisponentes fatorPredisponente;
	
	private List<NotificacaoFatorPredisponenteVO> notificacoes = new ArrayList<NotificacaoFatorPredisponenteVO>();
	
	private Date dtInicio;
	
	private Date dtFim;
	
	private Boolean modoEdicao;
	
	private Integer seqEdicao;
	
	private List<AghAtendimentos> listaAtendimentos;
	private AghAtendimentos atendimento;
	private AghAtendimentos atendimentoSelecionado;
	private String prontuarioFormatado;
	
	private String localizacao; 
	
	private String voltarPara;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public void inicio() {
		limparForm();
		
		this.paciente = null;
		this.idade = null;
		this.modoEdicao = false;
		this.prontuarioFormatado = null;
		this.setListaAtendimentos(new ArrayList<AghAtendimentos>());
		
		this.notificacoes = controleInfeccaoFacade.listarNotificacoesPorPaciente(this.codigoPaciente);
		this.paciente = pacienteFacade.obterPaciente(this.codigoPaciente);
		
		if(paciente != null){
			this.prontuarioFormatado = CoreUtil.formataProntuario(this.paciente.getProntuario());
			this.idade = DateUtil.getIdade(paciente.getDtNascimento());
			this.setListaAtendimentos(this.aghuFacade.obterAghAtendimentoPorDadosPaciente(this.codigoPaciente, this.paciente.getProntuario()));
		}
	}

	public List<MciFatorPredisponentes> obterSuggestionFatorPredisponentes(String param){
		String strPesquisa = (String) param;
		return returnSGWithCount(controleInfeccaoFacade.obterSuggestionFatorPredisponentes(strPesquisa), controleInfeccaoFacade.obterSuggestionFatorPredisponentesCount(strPesquisa));
	}
	
	public void editar(NotificacaoFatorPredisponenteVO item) {
		this.seqEdicao = item.getSeq();
		this.modoEdicao = true;
		this.dtInicio = item.getDtInicio();
		this.dtFim = item.getDtFim();
		this.atendimento = aghuFacade.obterAghAtendimentoPorSeq(item.getAtdSeq());
		this.fatorPredisponente = controleInfeccaoFacade.obterFatorPredisponentesPorSeq(item.getFpdSeq());
		try {
			this.localizacao = this.prescricaoMedicaFacade.buscarResumoLocalPaciente(this.atendimento);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravar(){
		if(this.atendimento == null){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ATENDIMENTO_OBRIGATORIO");
		}else{
			MciMvtoFatorPredisponentes entity = new MciMvtoFatorPredisponentes();
			entity.setCriadoEm(new Date());
			entity.setFatorPredisponente(this.fatorPredisponente);
			entity.setPaciente(this.paciente);
			entity.setAtendimento(this.atendimento);
			entity.setDataInicio(this.dtInicio);
			entity.setDataFim(this.dtFim);
			RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			entity.setSerMatricula(servidor.getId().getMatricula());
			entity.setSerVinCodigo(servidor.getId().getVinCodigo());
			if(this.dtFim != null){
				entity.setSerMatriculaEncerrado(servidor.getId().getMatricula());
				entity.setSerVinCodigoEncerrado(servidor.getId().getVinCodigo());
			}
			try {
				controleInfeccaoFacade.inserirNotificacaoFatorPredisponente(entity);
				StringBuffer descricao = new StringBuffer();
				if(atendimento.getConsulta() != null && atendimento.getConsulta().getNumero() != null){
					descricao.append(this.atendimento.getConsulta().getNumero().toString());	
				}
				if(this.atendimento.getDthrFim() != null){
					Format formatter = new SimpleDateFormat("dd/MM/yyyy");
					String dataFormatada = formatter.format(this.atendimento.getDthrFim());
					descricao.append(" - ");
					descricao.append(dataFormatada);
				}
				this.notificacoes = controleInfeccaoFacade.listarNotificacoesPorPaciente(this.codigoPaciente);
				limparForm();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_NFP", descricao);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void alterar(){
		if(this.atendimento == null){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ATENDIMENTO_OBRIGATORIO");
		}else{
			MciMvtoFatorPredisponentes entity = new MciMvtoFatorPredisponentes();
			entity.setFatorPredisponente(this.fatorPredisponente);
			entity.setAtendimento(this.atendimento);
			entity.setDataInicio(this.dtInicio);
			entity.setDataFim(this.dtFim);
			try {
				controleInfeccaoFacade.atualizarNotificacaoFatorPredisponente(entity,seqEdicao);
				StringBuffer descricao = new StringBuffer();
				
				if(this.atendimento.getConsulta() != null){
					if(this.atendimento.getConsulta().getNumero() != null){
						descricao.append(this.atendimento.getConsulta().getNumero().toString());	
					}
				}

				if(this.atendimento.getDthrFim() != null){
					Format formatter = new SimpleDateFormat("dd/MM/yyyy");
					String dataFormatada = formatter.format(this.atendimento.getDthrFim());
					descricao.append(" - ");
					descricao.append(dataFormatada);
				}
				this.notificacoes = controleInfeccaoFacade.listarNotificacoesPorPaciente(this.codigoPaciente);
				this.modoEdicao = false;
				limparForm();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_NFP", descricao);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void remover(){
		try {
			NotificacaoFatorPredisponenteVO vo = controleInfeccaoFacade.listarNotificacoesPorSeq(seqEdicao);
			StringBuffer descricao = new StringBuffer();
			if(vo != null){
				if(vo.getNumero() != null){
					descricao.append(vo.getNumero().toString());	
				}
				if(vo.getDthrFim() != null){
					Format formatter = new SimpleDateFormat("dd/MM/yyyy");
					String dataFormatada = formatter.format(vo.getDthrFim());
					descricao.append(" - ");
					descricao.append(dataFormatada);
				}
				controleInfeccaoFacade.removerNotificacaoFatorPredisponente(seqEdicao);
			}
			this.notificacoes = controleInfeccaoFacade.listarNotificacoesPorPaciente(this.codigoPaciente);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_NFP", descricao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void selecionarAtendimento() {
		this.setAtendimento(this.atendimentoSelecionado);
		try {
			this.localizacao = this.prescricaoMedicaFacade.buscarResumoLocalPaciente(this.atendimento);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicao(){
		this.fatorPredisponente = null;
		this.atendimento = null;
		this.dtInicio = null;
		this.dtFim = null;
        this.localizacao = null;
		this.modoEdicao = false;
	}
	
	public String voltar(){
		return voltarPara;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}


	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}


	public AipPacientes getPaciente() {
		return paciente;
	}


	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}


	public Integer getIdade() {
		return idade;
	}


	public void setIdade(Integer idade) {
		this.idade = idade;
	}


	public MciFatorPredisponentes getFatorPredisponente() {
		return fatorPredisponente;
	}


	public void setFatorPredisponente(MciFatorPredisponentes fatorPredisponente) {
		this.fatorPredisponente = fatorPredisponente;
	}

	public List<NotificacaoFatorPredisponenteVO> getNotificacoes() {
		return notificacoes;
	}

	public void setNotificacoes(List<NotificacaoFatorPredisponenteVO> notificacoes) {
		this.notificacoes = notificacoes;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public List<AghAtendimentos> getListaAtendimentos() {
		return listaAtendimentos;
	}

	public void setListaAtendimentos(List<AghAtendimentos> listaAtendimentos) {
		this.listaAtendimentos = listaAtendimentos;
	}

	public AghAtendimentos getAtendimentoSelecionado() {
		return atendimentoSelecionado;
	}

	public void setAtendimentoSelecionado(AghAtendimentos atendimentoSelecionado) {
		this.atendimentoSelecionado = atendimentoSelecionado;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public Integer getSeqEdicao() {
		return seqEdicao;
	}

	public void setSeqEdicao(Integer seqEdicao) {
		this.seqEdicao = seqEdicao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void limparForm(){
		this.atendimento = null;
		this.localizacao = null;
		this.fatorPredisponente = null;
		this.dtInicio = null;
		this.dtFim = null;
	}
	
}
