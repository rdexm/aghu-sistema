package br.gov.mec.aghu.controleinfeccao.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.vo.ProcedRealizadoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoTopografiasVO;
import br.gov.mec.aghu.controleinfeccao.vo.OrigemInfeccoesVO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaProcedimentoVO;
import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioMotivoEncerramentoNotificacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;


public class NotificacaoTopografiaController extends ActionController {

	private static final long serialVersionUID = -2983266063986567711L;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private String voltarPara;
	private Integer codigoPaciente;
	private String prontuarioFormatado;
	private String localizacao;
	private AipPacientes paciente;
	private Integer idade;
	private Date instalacao;
	private Date encerramento;
	private DominioConfirmacaoCCI dominioConfirmacaoCCI;
	private DominioMotivoEncerramentoNotificacao dominioMotivoEncerramentoNotificacao;
	private boolean modoEdicao;
	private TopografiaProcedimentoVO topografiaProcedimentoVO;
	private List<AghAtendimentos> listaAtendimentos;
	private AghAtendimentos atendimento;
	private OrigemInfeccoesVO origemInfeccoesVO;
	private ProcedRealizadoVO procedimento;
	private DominioIndContaminacao potencialContaminacao;
	private List<NotificacaoTopografiasVO> listNotificacoes;
	private NotificacaoTopografiasVO notificacaoTopografiaSelecionada;
	
	private enum NotificacaoTopografiaControllerONExceptionCode implements BusinessExceptionCode {
		MSG_NOTIF_TOPO_ATENDIMENTO_OBRIGATORIO;
	}

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
		recarregarTela();
	}
	
	
	private void recarregarTela(){
		limpar();
		dominioConfirmacaoCCI = DominioConfirmacaoCCI.N;
		obterInformacoesDoPaciente();
		obterListaAtendimentos();
		obterNotificacoesTopografia();
	}

	private void obterListaAtendimentos() {
		listaAtendimentos = aghuFacade.obterAghAtendimentoPorDadosPaciente(codigoPaciente, paciente.getProntuario());
	}

	private void obterNotificacoesTopografia() {
		try {
			listNotificacoes = controleInfeccaoFacade.obterNotificacoesTopografia(paciente.getCodigo());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void obterInformacoesDoPaciente() {
		paciente = obterPaciente(null);
		if(paciente != null){
			obterProntuarioFormatado();
			obterIdade();
		}
	}

	private AipPacientes obterPaciente(Integer codigoPaciente) {
		if(codigoPaciente != null){
			return pacienteFacade.obterPaciente(codigoPaciente);
		} else {
			return pacienteFacade.obterPaciente(this.codigoPaciente);
		}
	}

	private void obterIdade() {
		idade = DateUtil.getIdade(paciente.getDtNascimento());
	}

	private void obterProntuarioFormatado() {
		prontuarioFormatado = CoreUtil.formataProntuario(paciente.getProntuario());
	}
	
	public void selecionarAtendimento() {
		localizacao = obterLocalizacao(atendimento);
	}

	private String obterLocalizacao(AghAtendimentos atendimento) {
		try {
			 return prescricaoMedicaFacade.buscarResumoLocalPaciente(atendimento);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String voltar(){
		limpar();
		return getVoltarPara();
	}
	
	private OrigemInfeccoesVO obterTopografiaOrigemInfeccoes(String codigoEtiologiaInfeccao) {
		try {
			return origemInfeccoesVO = controleInfeccaoFacade.obterTopografiaOrigemInfeccoes(codigoEtiologiaInfeccao);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	private TopografiaProcedimentoVO obterTopografiaProcedimento(Short seqTopografiaProcedimento){ 
		try {
			return topografiaProcedimentoVO = controleInfeccaoFacade.obterTopografiaProcedimento(seqTopografiaProcedimento);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return topografiaProcedimentoVO;
	}
	
	public void adicionarNotificacao(){
		notificar();
	}

	public void alterarNotificacao(){
		notificar();
	}
	
	private void notificar(){
		try {
			NotificacaoTopografiasVO vo = obterNotificacaoTopografiasVO();
			controleInfeccaoFacade.persistirNotificacaoTopografia(vo);
			apresentarMsgNegocio(Severity.INFO, "MSG_NOTIF_TOPO_SUCESSO_GRAVACAO", vo);
			recarregarTela();
			
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void apresentarMsgNegocio(Severity severity, String msgKey, NotificacaoTopografiasVO vo ){
		apresentarMsgNegocio(severity, msgKey, vo.getSeqAtendimento().toString(),
				DateUtil.obterDataFormatada(vo.getDataAtendimento() != null ? vo.getDataAtendimento() : vo.getDataHoraInicioAtendimento(),
				DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO), vo.getCodigoEtiologiaInfeccao(), vo.getDataInicio());
	}
	
	private NotificacaoTopografiasVO obterNotificacaoTopografiasVO() throws ApplicationBusinessException {
		
		NotificacaoTopografiasVO vo = null;
		
		if(modoEdicao){
			vo = notificacaoTopografiaSelecionada;
		} else {
			validarAtendimento();
			vo =  new NotificacaoTopografiasVO();
		}

		vo.setCodigoPaciente(paciente.getCodigo());
		
		if(atendimento != null){
			vo.setSeqAtendimento(atendimento.getSeq());
			vo.setLocalizacao(localizacao);
			vo.setSeqAtendimento(atendimento.getSeq());
			vo.setDataAtendimento(atendimento.getDthrInicio());
		} else {
			vo.setSeqAtendimento(vo.getSeqAtendimento());
			atendimento = controleInfeccaoFacade.obterAghAtendimentoObterPorChavePrimaria(vo.getSeqAtendimento());
			vo.setLocalizacao(obterLocalizacao(atendimento));
			vo.setSeqAtendimento(atendimento.getSeq());
			vo.setDataAtendimento(atendimento.getDthrInicio());
		}
		
		vo.setSeqTopografiaProcedimento(topografiaProcedimentoVO.getSeq());
		vo.setDescricaoTopografiaProcedimento(topografiaProcedimentoVO.getDescricao());
		vo.setDataInicio(instalacao);
		vo.setDataFim(encerramento);
		vo.setMotivoEncerramento(dominioMotivoEncerramentoNotificacao);
		vo.setConfirmacaoCci(dominioConfirmacaoCCI);
		vo.setProcedimento(this.procedimento);
		vo.setPotencialContaminacao(this.potencialContaminacao);
		vo.setProcedimentoCirurgico(this.topografiaProcedimentoVO.getProcedimentoCirurgico());
		
		vo.setCodigoEtiologiaInfeccao(origemInfeccoesVO.getCodigoOrigem());

		return vo;
	}
	
	private void validarAtendimento() throws ApplicationBusinessException {
		if (atendimento == null) {
			throw new ApplicationBusinessException(NotificacaoTopografiaControllerONExceptionCode.MSG_NOTIF_TOPO_ATENDIMENTO_OBRIGATORIO) ;
		}
	}

	public void cancelarEdicao(){
		recarregarTela();
	}
	
	public void limpar(){
		modoEdicao = Boolean.FALSE;
		idade = null;
		paciente =  null;
		atendimento = null;
		localizacao = null;
		topografiaProcedimentoVO = null;
		origemInfeccoesVO = null;
		instalacao = null;
		encerramento = null;
		dominioMotivoEncerramentoNotificacao = null;
		dominioConfirmacaoCCI = null;
		this.procedimento = null;
		this.potencialContaminacao = null;
	}
	
	public void editar(NotificacaoTopografiasVO item) {
		notificacaoTopografiaSelecionada = item;
		modoEdicao = Boolean.TRUE;
		
		atendimento = controleInfeccaoFacade.obterAghAtendimentoObterPorChavePrimaria(item.getSeqAtendimento());
		localizacao = obterLocalizacao(atendimento);
		
		topografiaProcedimentoVO = obterTopografiaProcedimento(notificacaoTopografiaSelecionada.getSeqTopografiaProcedimento());
		origemInfeccoesVO = obterTopografiaOrigemInfeccoes(notificacaoTopografiaSelecionada.getCodigoEtiologiaInfeccao());
		instalacao = notificacaoTopografiaSelecionada.getDataInicio();
		encerramento = notificacaoTopografiaSelecionada.getDataFim();
		dominioMotivoEncerramentoNotificacao = notificacaoTopografiaSelecionada.getMotivoEncerramento();
		dominioConfirmacaoCCI = notificacaoTopografiaSelecionada.getConfirmacaoCci();
		if (notificacaoTopografiaSelecionada.getPodDcgCrgSeq() != null) {
			Integer dcgCrgSeq = notificacaoTopografiaSelecionada.getPodDcgCrgSeq();
			Short dcgSeqp = notificacaoTopografiaSelecionada.getPodDcgSeqp();
			Integer seqp = notificacaoTopografiaSelecionada.getPodSeqp();
			this.procedimento = this.controleInfeccaoFacade.obterProcedimentoVOPorId(dcgCrgSeq, dcgSeqp, seqp);
		}
		this.potencialContaminacao = this.notificacaoTopografiaSelecionada.getPotencialContaminacao();
	}

	public void excluir(){
		try {
			controleInfeccaoFacade.excluirNotificacaoTopografia(notificacaoTopografiaSelecionada);
			apresentarMsgNegocio(Severity.INFO, "MSG_NOTIF_TOPO_SUCESSO_EXCLUSAO", notificacaoTopografiaSelecionada);
			recarregarTela();
			
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<TopografiaProcedimentoVO> suggestionBoxTopografiaProcedimento(String strPesquisa) {
		return controleInfeccaoFacade.suggestionBoxTopografiaProcedimentoPorSeqOuDescricao((String) strPesquisa);
	}
	
	public List<OrigemInfeccoesVO> suggestionBoxTopografiaOrigemInfeccoes(String strPesquisa) {
		return controleInfeccaoFacade.suggestionBoxTopografiaOrigemInfeccoes((String) strPesquisa);
	}
	
	public List<ProcedRealizadoVO> obterProcedimentosPorPaciente(String strPesquisa) {
		try {
			return this.controleInfeccaoFacade.obterProcedimentosPorPaciente(this.paciente.getCodigo(), strPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public void selecionarPotencialContaminacao() {
		if (this.notificacaoTopografiaSelecionada == null) {
			this.potencialContaminacao = this.procedimento.getContaminacao();
		}
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
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

	public DominioConfirmacaoCCI getDominioConfirmacaoCCI() {
		return dominioConfirmacaoCCI;
	}

	public void setDominioConfirmacaoCCI(DominioConfirmacaoCCI dominioConfirmacaoCCI) {
		this.dominioConfirmacaoCCI = dominioConfirmacaoCCI;
	}

	public DominioMotivoEncerramentoNotificacao getDominioMotivoEncerramentoNotificacao() {
		return dominioMotivoEncerramentoNotificacao;
	}

	public void setDominioMotivoEncerramentoNotificacao(DominioMotivoEncerramentoNotificacao dominioMotivoEncerramentoNotificacao) {
		this.dominioMotivoEncerramentoNotificacao = dominioMotivoEncerramentoNotificacao;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public TopografiaProcedimentoVO getTopografiaProcedimentoVO() {
		return topografiaProcedimentoVO;
	}

	public void setTopografiaProcedimentoVO(
			TopografiaProcedimentoVO topografiaProcedimentoVO) {
		this.topografiaProcedimentoVO = topografiaProcedimentoVO;
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

	public OrigemInfeccoesVO getOrigemInfeccoesVO() {
		return origemInfeccoesVO;
	}

	public void setOrigemInfeccoesVO(OrigemInfeccoesVO origemInfeccoesVO) {
		this.origemInfeccoesVO = origemInfeccoesVO;
	}

	public ProcedRealizadoVO getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(ProcedRealizadoVO procedimento) {
		this.procedimento = procedimento;
	}

	public DominioIndContaminacao getPotencialContaminacao() {
		return potencialContaminacao;
	}

	public void setPotencialContaminacao(
			DominioIndContaminacao potencialContaminacao) {
		this.potencialContaminacao = potencialContaminacao;
	}

	public List<NotificacaoTopografiasVO> getListNotificacoes() {
		return listNotificacoes;
	}

	public void setListNotificacoes(List<NotificacaoTopografiasVO> listNotificacoes) {
		this.listNotificacoes = listNotificacoes;
	}

	public NotificacaoTopografiasVO getNotificacaoTopografiaSelecionada() {
		return notificacaoTopografiaSelecionada;
	}

	public void setNotificacaoTopografiaSelecionada(NotificacaoTopografiasVO notificacaoTopografiaSelecionada) {
		this.notificacaoTopografiaSelecionada = notificacaoTopografiaSelecionada;
	}
	
}
