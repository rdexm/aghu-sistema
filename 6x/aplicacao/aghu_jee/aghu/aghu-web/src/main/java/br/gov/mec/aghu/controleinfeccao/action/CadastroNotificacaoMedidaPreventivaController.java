package br.gov.mec.aghu.controleinfeccao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.DoencaInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoMedidasPreventivasVO;
import br.gov.mec.aghu.controleinfeccao.vo.OrigemInfeccoesVO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaProcedimentoVO;
import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class CadastroNotificacaoMedidaPreventivaController  extends ActionController {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -9215200679615820914L;
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
	
	private Boolean modoEdicao;
	private AipPacientes paciente;
	private Integer codigoPaciente;
	private NotificacaoMedidasPreventivasVO notificacao;
	private NotificacaoMedidasPreventivasVO notificacaoSelecionado;
	private AghAtendimentos atendimentoSelecionado;
	private AghAtendimentos atendimento;
	private DoencaInfeccaoVO doencaInfeccaoVOSelecionado;
	private TopografiaProcedimentoVO topografiaProcedimentoVOSelecionado;
	private Integer idadePaciente;
	private String prontuarioFormatado;
	private String localizacaoPaciente;
	
	private List<NotificacaoMedidasPreventivasVO> notificacoes = new ArrayList<NotificacaoMedidasPreventivasVO>();
	private List<AghAtendimentos> atendimentos = new ArrayList<AghAtendimentos>();

	private OrigemInfeccoesVO origem;
	private Boolean mostrarModalExclusao;
	private String voltarPara;
	

		
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	private void limparParametros() {
		this.codigoPaciente = null;
		this.paciente = null;
		this.notificacoes = null;
		this.prontuarioFormatado = null;
		this.idadePaciente = null;
		this.notificacao = null;
		this.notificacaoSelecionado = null;
		this.modoEdicao = false;
		this.atendimento = null;
		this.atendimentoSelecionado = null;
		this.doencaInfeccaoVOSelecionado = null;
		this.topografiaProcedimentoVOSelecionado = null;
		this.localizacaoPaciente = null;
		this.atendimentos = null;
		this.origem = null;
		this.mostrarModalExclusao = null;
	}
	
	public void iniciar() {
		this.modoEdicao = false;
		this.paciente = pacienteFacade.obterPaciente(this.codigoPaciente);
		this.mostrarModalExclusao = false;
		carregarNotificacoes();	
		
		if(this.paciente != null) {
			this.setProntuarioFormatado(CoreUtil.formataProntuario(this.paciente.getProntuario()));
			this.idadePaciente = DateUtil.getIdade(paciente.getDtNascimento());
			pesquisarAtendimentos();
		}
		notificacao = new NotificacaoMedidasPreventivasVO();
		notificacao.setIndConfirmacaoCCI(DominioConfirmacaoCCI.N);
	}
	

	private void carregarNotificacoes() {
		
		try {
			this.notificacoes = controleInfeccaoFacade.pesquisarNotificacoesMedidaPreventiva(this.codigoPaciente);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			}		
	}	

	
	public void editar() {
		try {
			this.modoEdicao = true;
			this.notificacao = notificacaoSelecionado;
			this.atendimento = pacienteFacade.obterAtendimento(notificacao.getAtdSeq());
			this.localizacaoPaciente = prescricaoMedicaFacade.buscarResumoLocalPaciente(atendimento);
			criarDoencaInfeccaoVO();
			criarTopografiaProcedimentoVO();
			criarOrigemInfeccaoVO();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void prepararExclusao() {
		this.mostrarModalExclusao= true;
		this.notificacao = this.notificacaoSelecionado;
		
	}

	private void criarOrigemInfeccaoVO() {
		if(StringUtils.isNotBlank(notificacao.getEinTipo())){
			this.origem = new OrigemInfeccoesVO();
			this.origem.setCodigoOrigem(notificacao.getEinTipo());
			this.origem.setDescricao(notificacao.getEinDescricao());
		} else {
			this.origem = null;
		}
	}

	private void criarTopografiaProcedimentoVO() {
		if(notificacao.getTopSeq() != null) {
			this.topografiaProcedimentoVOSelecionado = new TopografiaProcedimentoVO();
			this.topografiaProcedimentoVOSelecionado.setSeq(notificacao.getTopSeq());
			this.topografiaProcedimentoVOSelecionado.setDescricao(notificacao.getTopDescricao());
		} else {
			this.topografiaProcedimentoVOSelecionado = null;
		}
	}

	private void criarDoencaInfeccaoVO() {
		if(notificacao.getPaiSeq() != null) {
			this.doencaInfeccaoVOSelecionado = new DoencaInfeccaoVO();
			this.doencaInfeccaoVOSelecionado.setSeqPai(notificacao.getPaiSeq());
			this.doencaInfeccaoVOSelecionado.setPalavraChavePatologia(notificacao.getDescricaoPatologia());
		} else {
			this.doencaInfeccaoVOSelecionado = null;
		}
	}
	
	private void pesquisarAtendimentos() {
		this.atendimentos = aghuFacade.obterAghAtendimentoPorDadosPaciente(codigoPaciente, paciente.getProntuario());
	}
	
	public void selecionarAtendimento() {
		try {
			this.atendimento = this.atendimentoSelecionado;
			this.localizacaoPaciente = prescricaoMedicaFacade.buscarResumoLocalPaciente(this.atendimento);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<DoencaInfeccaoVO> listarDoencaInfeccaoVO(String param) {
		String strPesquisa = (String) param;
		return returnSGWithCount(controleInfeccaoFacade.listarDoencaInfeccaoVO(strPesquisa), controleInfeccaoFacade.listarDoencaInfeccaoVOCount(strPesquisa));
	}
	
	public List<TopografiaProcedimentoVO> listarTopografiasInfeccao(String param) {
		String strPesquisa = (String) param;
		return returnSGWithCount(controleInfeccaoFacade.listarTopografiasAtivas(strPesquisa), controleInfeccaoFacade.listarTopografiasAtivasCount(strPesquisa));
	}
	
	public List<OrigemInfeccoesVO> listarOrigens(String param) {
		String strPesquisa = (String) param;
		return returnSGWithCount(controleInfeccaoFacade.listarOrigemInfeccoes(strPesquisa), controleInfeccaoFacade.listarOrigemInfeccoesCount(strPesquisa)) ;
	}
	
	public void alterar() {
		try {
			if(validarCamposObrigatorios()) {
				prepararDadosNotificacao();
				controleInfeccaoFacade.validarCadastroEdicaoNotificacao(notificacao);
				controleInfeccaoFacade.alterarNotificacaoMedidaPreventiva(notificacao);
				apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_GRAVACAO_NMP", notificacao.getDescricao());
				cancelarEdicao();
				carregarNotificacoes();
			}
		}  catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private String getDescricaoNotificacao() {
		for (NotificacaoMedidasPreventivasVO item : notificacoes) {
			 if(notificacao.equals(item)) {
				 return item.getDescricao();
			 }
			
		}
		return "";
	}
	
	public void gravar() {
		try {
			if(validarCamposObrigatorios()){
				prepararDadosNotificacao();
				controleInfeccaoFacade.validarCadastroEdicaoNotificacao(notificacao);
				controleInfeccaoFacade.persistirNotificacaoMedidaPreventiva(notificacao);
				carregarNotificacoes();
				apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_GRAVACAO_NMP", getDescricaoNotificacao());
				cancelarEdicao();
			}
		}  catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
	}
	
	private Boolean validarCamposObrigatorios() {
		if (atendimento == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ATD_OBRIGATORIO_NMP");
			return false;
		} else {
			this.atendimento = pacienteFacade.obterAtendimento(atendimento.getSeq());
		}
		if(doencaInfeccaoVOSelecionado == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PAT_OBRIGATORIO_NMP");
			return false;
		}		
		if (notificacao.getDtInicio() == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_DT_OBRIGATORIO_NMP");
			return false;
		}		
		return true;
	}

	private void prepararDadosNotificacao() {	
		notificacao.setAtdSeq(atendimento.getSeq());
		if(atendimento.getConsulta() == null) {
			notificacao.setConsulta(null);
		} else {
			notificacao.setConsulta(atendimento.getConsulta().getNumero());
		}
		notificacao.setPacCodigo(codigoPaciente);
		notificacao.setLocalizacao(localizacaoPaciente);
		if(origem == null) {
			notificacao.setEinTipo(null);
			notificacao.setEinDescricao(null);
		} else {
			notificacao.setEinTipo(origem.getCodigoOrigem());
			notificacao.setEinDescricao(origem.getDescricao());
		}
		if (topografiaProcedimentoVOSelecionado == null) {
			notificacao.setTopSeq(null);
		}  else {
			notificacao.setTopSeq(topografiaProcedimentoVOSelecionado.getSeq());
		}
		if(doencaInfeccaoVOSelecionado!=null){
			notificacao.setPaiSeq(doencaInfeccaoVOSelecionado.getSeqPai());
		}
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		notificacao.setSerMatricula(servidor.getId().getMatricula());
		notificacao.setSerVinCodigo(servidor.getId().getVinCodigo());
		if(notificacao.getDtFim() != null) {
			notificacao.setSerMatriculaEncerrado(servidor.getId().getMatricula());
			notificacao.setSerVinCodigoEncerrado(servidor.getId().getVinCodigo());
		}
		if(notificacao.getIndConfirmacaoCCI() != null) {
			notificacao.setSerMatriculaConfirmado(servidor.getId().getMatricula());
			notificacao.setSerVinCodigoConfirmado(servidor.getId().getVinCodigo());
		}		
		
	}
	
	public void deletar() {		
		try {
			controleInfeccaoFacade.deletarNotificacaoMedidaPreventiva(notificacaoSelecionado);
			apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_EXCLUSAO_NMP", notificacaoSelecionado.getDescricao());
			carregarNotificacoes();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			cancelarExclusao();
			cancelarEdicao();
		}
	}
	
	public void cancelarExclusao() {
		mostrarModalExclusao = false;
		notificacaoSelecionado = null;
	}
	
	public String voltar() {
		this.limparParametros();
		return voltarPara;
	}
	
	public void cancelarEdicao() {
		this.notificacaoSelecionado = null;
		this.modoEdicao = false;
		this.atendimento = null;
		this.atendimentoSelecionado = null;
		this.doencaInfeccaoVOSelecionado = null;
		this.topografiaProcedimentoVOSelecionado = null;
		this.localizacaoPaciente = null;		
		this.origem = null;
		this.mostrarModalExclusao = null;
		notificacao = new NotificacaoMedidasPreventivasVO();
		notificacao.setIndConfirmacaoCCI(DominioConfirmacaoCCI.N);
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public List<NotificacaoMedidasPreventivasVO> getNotificacoes() {
		return notificacoes;
	}

	public List<AghAtendimentos> getAtendimentos() {
		return atendimentos;
	}


	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public void setNotificacoes(List<NotificacaoMedidasPreventivasVO> notificacoes) {
		this.notificacoes = notificacoes;
	}

	public void setAtendimentos(List<AghAtendimentos> atendimentos) {
		this.atendimentos = atendimentos;
	}


	public NotificacaoMedidasPreventivasVO getNotificacao() {
		return notificacao;
	}

	public void setNotificacao(NotificacaoMedidasPreventivasVO notificacao) {
		this.notificacao = notificacao;
	}

	public AghAtendimentos getAtendimentoSelecionado() {
		return atendimentoSelecionado;
	}

	public void setAtendimentoSelecionado(AghAtendimentos atendimentoSelecionado) {
		this.atendimentoSelecionado = atendimentoSelecionado;
	}

	public DoencaInfeccaoVO getDoencaInfeccaoVOSelecionado() {
		return doencaInfeccaoVOSelecionado;
	}

	public void setDoencaInfeccaoVOSelecionado(
			DoencaInfeccaoVO doencaInfeccaoVOSelecionado) {
		this.doencaInfeccaoVOSelecionado = doencaInfeccaoVOSelecionado;
	}
	
	public TopografiaProcedimentoVO getTopografiaProcedimentoVOSelecionado() {
		return topografiaProcedimentoVOSelecionado;
	}

	public void setTopografiaProcedimentoVOSelecionado(
			TopografiaProcedimentoVO topografiaProcedimentoVOSelecionado) {
		this.topografiaProcedimentoVOSelecionado = topografiaProcedimentoVOSelecionado;
	}

	public Integer getIdadePaciente() {
		return idadePaciente;
	}

	public void setIdadePaciente(Integer idadePaciente) {
		this.idadePaciente = idadePaciente;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public OrigemInfeccoesVO getOrigem() {
		return origem;
	}

	public void setOrigem(OrigemInfeccoesVO origem) {
		this.origem = origem;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public String getLocalizacaoPaciente() {
		return localizacaoPaciente;
	}

	public void setLocalizacaoPaciente(String localizacaoPaciente) {
		this.localizacaoPaciente = localizacaoPaciente;
	}

	public NotificacaoMedidasPreventivasVO getNotificacaoSelecionado() {
		return notificacaoSelecionado;
	}

	public void setNotificacaoSelecionado(NotificacaoMedidasPreventivasVO notificacaoSelecionado) {
		this.notificacaoSelecionado = notificacaoSelecionado;
	}

	public Boolean getMostrarModalExclusao() {
		return mostrarModalExclusao;
	}

	public void setMostrarModalExclusao(Boolean mostrarModalExclusao) {
		this.mostrarModalExclusao = mostrarModalExclusao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

}
