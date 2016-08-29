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
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoProcedimentoRiscoVO;
import br.gov.mec.aghu.dominio.DominioSimNaoCCIH;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MciMvtoProcedimentoRiscos;
import br.gov.mec.aghu.model.MciProcedimentoRisco;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateConstants;

public class NotificarProcedimentosRiscoController extends ActionController {

	private static final String _HIFEN_ = " - ";

	/**
	 * 
	 */
	private static final long serialVersionUID = 3720935490362728206L;
	
	//private static final String REDIRECIONA_BUSCA_ATIVA_PACIENTES = "buscaAtivaPacientes";

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private Integer pacCodigo;
	private String prontuarioFormatado;
	private String localizacao;
	private Date instalacao;
	private Date encerramento;
	private DominioSimNaoCCIH confirmadoCCIH;
	
	private List<NotificacaoProcedimentoRiscoVO> listaProcedimentosRisco = new ArrayList<NotificacaoProcedimentoRiscoVO>();
	
	private AipPacientes paciente;
	private List<AghAtendimentos> listaAtendimentos;
	private AghAtendimentos atendimento;
	private AghAtendimentos atendimentoSelecionado;
	
	private boolean modoEdicao;
	private String voltarPara;
	private MciProcedimentoRisco procedimentoRisco;
	private NotificacaoProcedimentoRiscoVO procedimentoRiscoExclusao;
	private Integer seqEdicao;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	public void inicio() {
		//this.pacCodigo = 2789279;
		this.confirmadoCCIH = DominioSimNaoCCIH.N;
		this.modoEdicao = false;
		this.paciente = this.pacienteFacade.obterPacientePorCodigo(this.pacCodigo);
		this.prontuarioFormatado = CoreUtil.formataProntuario(this.paciente.getProntuario());
		this.atendimento = null;
		this.procedimentoRisco = null;
		this.instalacao = null;
		this.encerramento = null;
		
		this.setListaAtendimentos(this.aghuFacade.obterAghAtendimentoPorDadosPaciente(this.pacCodigo, this.paciente.getProntuario()));
		this.setListaProcedimentosRisco(new ArrayList<NotificacaoProcedimentoRiscoVO>());
		this.listaProcedimentosRisco = this.controleInfeccaoFacade.listarNotificacoesProcedientoRiscoPorPaciente(this.pacCodigo);
	
	}
	
	public void adicionarNotificacao() {
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_LOCAL_ORIGEM");
		this.cancelarEdicao();
	}
	
	public void gravar(){
		if(this.atendimento == null){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ATENDIMENTO_OBRIGATORIO");
		}else{
			MciMvtoProcedimentoRiscos entity = new MciMvtoProcedimentoRiscos();
			entity.setCriadoEm(new Date());
			entity.setMciProcedimentoRisco(this.procedimentoRisco);
			entity.setPaciente(this.paciente);
			entity.setAtendimento(this.atendimento);
			entity.setDtInicio(this.instalacao);
			entity.setDtFim(this.encerramento);
			entity.setConfirmacaoCci(this.confirmadoCCIH);
			RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			entity.setSerMatricula(servidor.getId().getMatricula());
			entity.setSerVinCodigo(servidor.getId().getVinCodigo());
			if(this.encerramento != null){
				entity.setSerMatriculaEncerrado(servidor.getId().getMatricula());
				entity.setSerVinCodigoEncerrado(servidor.getId().getVinCodigo());
			}		
			try {
				controleInfeccaoFacade.inserirNotificacaoProcedimentoRisco(entity);
				StringBuffer descricao = new StringBuffer();
				if(existeConsultaPara(atendimento)){
					descricao.append(this.atendimento.getConsulta().getNumero().toString());	
				}
				if(this.atendimento.getDthrFim() != null){
					Format formatter = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY);
					String dataFormatada = formatter.format(this.atendimento.getDthrFim());
					descricao.append(_HIFEN_);
					descricao.append(dataFormatada);
				}
				this.listaProcedimentosRisco = controleInfeccaoFacade.listarNotificacoesProcedientoRiscoPorPaciente(this.pacCodigo);
				limparForm();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_NPR", descricao);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
    private boolean existeConsultaPara(AghAtendimentos atendimento) {
        return atendimento.getConsulta() != null && atendimento.getConsulta().getNumero() != null;
    }
	
	public void alterarNotificacao() {
		if(this.atendimento == null){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ATENDIMENTO_OBRIGATORIO");
		}else{
			MciMvtoProcedimentoRiscos entity = new MciMvtoProcedimentoRiscos();
			entity.setMciProcedimentoRisco(this.procedimentoRisco);
			entity.setAtendimento(this.atendimento);
			entity.setDtInicio(this.instalacao);
			entity.setDtFim(this.encerramento);
			entity.setConfirmacaoCci(this.confirmadoCCIH);
			try {
				controleInfeccaoFacade.atualizarProcedimentoRisco(entity,seqEdicao);
				StringBuffer descricao = new StringBuffer();
				
				if(this.atendimento.getConsulta() != null){
				if(this.atendimento.getConsulta().getNumero() != null){
					descricao.append(this.atendimento.getConsulta().getNumero().toString());	
				}
				}
				
				if(this.atendimento.getDthrFim() != null){
					Format formatter = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY);
					String dataFormatada = formatter.format(this.atendimento.getDthrFim());
					descricao.append(_HIFEN_);
					descricao.append(dataFormatada);
				}
				this.listaProcedimentosRisco = controleInfeccaoFacade.listarNotificacoesProcedientoRiscoPorPaciente(this.pacCodigo);
				this.modoEdicao = false;
				limparForm();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_NPR", descricao);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public void excluir(){
		try {
			if(procedimentoRiscoExclusao != null){
				NotificacaoProcedimentoRiscoVO vo = controleInfeccaoFacade.listarNotificacoesProcedimentoRiscoPorSeq(procedimentoRiscoExclusao.getSeq());
				StringBuffer descricao = new StringBuffer();
				if(vo != null){
					if(vo.getNumero() != null){
						descricao.append(vo.getNumero().toString());	
					}
					if(vo.getDthrFim() != null){
						Format formatter = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY);
						String dataFormatada = formatter.format(vo.getDthrFim());
						descricao.append(_HIFEN_);
						descricao.append(dataFormatada);
					}
					controleInfeccaoFacade.removerNotificacaoProcedimentoRisco(procedimentoRiscoExclusao.getSeq());
				}else{
					if(procedimentoRiscoExclusao.getNumero() != null){
						descricao.append(procedimentoRiscoExclusao.getNumero().toString());	
					}
					if(procedimentoRiscoExclusao.getDthrFim() != null){
						Format formatter = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY);
						String dataFormatada = formatter.format(procedimentoRiscoExclusao.getDthrFim());
						descricao.append(_HIFEN_);
						descricao.append(dataFormatada);
					}
				}
				this.listaProcedimentosRisco = controleInfeccaoFacade.listarNotificacoesProcedientoRiscoPorPaciente(this.pacCodigo);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_NPR", descricao.toString());
			}else{
				this.listaProcedimentosRisco = controleInfeccaoFacade.listarNotificacoesProcedientoRiscoPorPaciente(this.pacCodigo);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_NPR", " ");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void editar(NotificacaoProcedimentoRiscoVO item) {
		this.modoEdicao = true;
		this.seqEdicao = item.getSeq();
		this.instalacao = item.getDtInicio();
		this.encerramento = item.getDtFim();
		this.atendimento = aghuFacade.obterAghAtendimentoPorSeq(item.getAtdSeq());
		this.procedimentoRisco = controleInfeccaoFacade.obterProcedimentoRiscoPorSeq(item.getPorSeq());
		this.confirmadoCCIH = item.getConfirmadoCCIH();
		try {
			this.localizacao = this.prescricaoMedicaFacade.buscarResumoLocalPaciente(this.atendimento);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void selecionarAtendimento() {
		this.setAtendimento(this.atendimentoSelecionado);
		try {
			this.localizacao = this.prescricaoMedicaFacade.buscarResumoLocalPaciente(this.atendimento);
			this.closeDialog("modalAtendimentosWG");
			this.atendimentoSelecionado = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<MciProcedimentoRisco> pesquisarProcedimentoRisco(String param){
		String strPesquisa = (String) param;
		return returnSGWithCount(controleInfeccaoFacade.pesquisarProcedimentoRisco(strPesquisa), pesquisarProcedimentoRiscoCount(strPesquisa));
	}
	
	public Long pesquisarProcedimentoRiscoCount(String param) {
		return controleInfeccaoFacade.pesquisarProcedimentoRiscoCount(param);
	}
	
	public String voltar() {
		limparForm();
		this.paciente = null;
		this.prontuarioFormatado = null;
		this.listaProcedimentosRisco = new ArrayList<NotificacaoProcedimentoRiscoVO>();
		return voltarPara;
	}
	
	public void cancelarEdicao() {
		this.modoEdicao = false;
		limparForm();
	}
	
	public void limparForm(){
		this.confirmadoCCIH = DominioSimNaoCCIH.N;
		this.atendimento = null;
		this.localizacao = null;
		this.procedimentoRisco = null;
		this.instalacao = null;
		this.encerramento = null;
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
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

	public Date getInstalacao() {
		return instalacao;
	}

	public void setInstalacao(Date instalacao) {
		this.instalacao = instalacao;
	}

	public Date getEncerramento() {
		return encerramento;
	}

	public void setEncerramento(Date encerramento) {
		this.encerramento = encerramento;
	}

	public DominioSimNaoCCIH getConfirmadoCCIH() {
		return confirmadoCCIH;
	}

	public void setConfirmadoCCIH(DominioSimNaoCCIH confirmadoCCIH) {
		this.confirmadoCCIH = confirmadoCCIH;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public List<AghAtendimentos> getListaAtendimentos() {
		return listaAtendimentos;
	}

	public void setListaAtendimentos(List<AghAtendimentos> listaAtendimentos) {
		this.listaAtendimentos = listaAtendimentos;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public AghAtendimentos getAtendimentoSelecionado() {
		return atendimentoSelecionado;
	}

	public void setAtendimentoSelecionado(AghAtendimentos atendimentoSelecionado) {
		this.atendimentoSelecionado = atendimentoSelecionado;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public List<NotificacaoProcedimentoRiscoVO> getListaProcedimentosRisco() {
		return listaProcedimentosRisco;
	}

	public void setListaProcedimentosRisco(List<NotificacaoProcedimentoRiscoVO> listaProcedimentosRisco) {
		this.listaProcedimentosRisco = listaProcedimentosRisco;
	}

	public MciProcedimentoRisco getProcedimentoRisco() {
		return procedimentoRisco;
	}

	public void setProcedimentoRisco(MciProcedimentoRisco procedimentoRisco) {
		this.procedimentoRisco = procedimentoRisco;
	}

	public NotificacaoProcedimentoRiscoVO getProcedimentoRiscoExclusao() {
		return procedimentoRiscoExclusao;
	}

	public void setProcedimentoRiscoExclusao(
			NotificacaoProcedimentoRiscoVO procedimentoRiscoExclusao) {
		this.procedimentoRiscoExclusao = procedimentoRiscoExclusao;
	}

	public Integer getSeqEdicao() {
		return seqEdicao;
	}

	public void setSeqEdicao(Integer seqEdicao) {
		this.seqEdicao = seqEdicao;
	}
	
}
