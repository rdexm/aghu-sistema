package br.gov.mec.aghu.exames.coleta.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.business.IColetaExamesFacade;
import br.gov.mec.aghu.exames.coleta.vo.GrupoExameVO;
import br.gov.mec.aghu.exames.coleta.vo.OrigemUnidadeVO;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


/**
 * Controller Transferencia Agendamento Controller
 * 
 * @author dansantos
 *
 */


public class TransferenciaAgendamentoController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(TransferenciaAgendamentoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 7499296478682738384L;


	@EJB
	private IColetaExamesFacade coletaExamesFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private List<ItemHorarioAgendadoVO> listaExamesAgendados;
	
	private Boolean todosExamesSelecionados = false;
	
	private Short gaeUnfSeq;
	
	private Integer gaeSeqp;
	
	private Date dthrAgenda;
	
	private Date data;
	
	private Date hora;
	
	private Integer grade;
	
	private AelGrupoExames grupoExame;
	
	private AelSalasExecutorasExames salaExecutoraExame;
	
	private RapServidores servidor;

	private AghUnidadesFuncionais unidadeExecutora;
	
	private List<VAelHrGradeDispVO> listaHorarios;
	
	private Integer idSelecionado = null;
	
	private VAelHrGradeDispVO vAelHrGradeDispVO;
	
	private List<ItemHorarioAgendadoVO> examesSelecionados;
	
	private Boolean primeiraPesquisa = false;
	
	public VAelHrGradeDispVO getvAelHrGradeDispVO() {
		return vAelHrGradeDispVO;
	}

	public void setvAelHrGradeDispVO(VAelHrGradeDispVO vAelHrGradeDispVO) {
		this.vAelHrGradeDispVO = vAelHrGradeDispVO;
	}

	public AelGrupoExames getGrupoExame() {
		return grupoExame;
	}

	public void setGrupoExame(AelGrupoExames grupoExame) {
		this.grupoExame = grupoExame;
	}

	public void inicio() {
	 

		this.unidadeExecutora = this.aghuFacade
		.obterAghUnidadesFuncionaisPorChavePrimaria(
				this.getGaeUnfSeq());
		this.carregarDados();
	
	}
	
	public void carregarDados() {
		this.listaExamesAgendados = this.coletaExamesFacade.pesquisarExamesParaTransferencia(this.getGaeUnfSeq(), this.getGaeSeqp(), this.getDthrAgenda());
	}
	

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		OrigemUnidadeVO origemUnidadeVO = this.coletaExamesFacade.obterOrigemUnidadeSolicitante(this.getGaeUnfSeq(), this.getGaeSeqp(), this.getDthrAgenda());
		Short tipo1 = null;
		Short tipo2 = null;
		AghParametros parametroTipoMarcacao = null;
		try{
			if(origemUnidadeVO!=null){
				tipo1 = this.agendamentoExamesFacade.obterTipoMarcacao(origemUnidadeVO.getOrigem(), origemUnidadeVO.getUnfSeq());	
			}
			parametroTipoMarcacao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_MARC_DIV);
			if(parametroTipoMarcacao!=null){
				tipo2 = parametroTipoMarcacao.getVlrNumerico().shortValue();
			}
		} catch(ApplicationBusinessException e){
			this.apresentarExcecaoNegocio(e);
		}
		examesSelecionados = this.coletaExamesFacade.obterExamesSelecionados(this.listaExamesAgendados);
		if(examesSelecionados.size()==0){
			this.apresentarMsgNegocio(Severity.ERROR,
					"MENSAGEM_NENHUM_EXAME_SELECIONADO_PARA_TRANSFERENCIA");
		} else if(examesSelecionados.size()==1){
			ItemHorarioAgendadoVO itemSelecionado = examesSelecionados.get(0);
			Date dataHoraReativacao = this.coletaExamesFacade.obterDataReativacaoUnfExecutaExameAtiva(itemSelecionado.getSigla(), itemSelecionado.getSeqMaterialAnalise(), itemSelecionado.getSeqUnidade());
			listaHorarios = this.coletaExamesFacade.pesquisarHorariosParaExameSelecionado(dataHoraReativacao, tipo1, tipo2, itemSelecionado, this.data, this.hora, this.grade, this.grupoExame, this.salaExecutoraExame, this.servidor);
			if(listaHorarios!=null && listaHorarios.size()>0){
				this.vAelHrGradeDispVO = listaHorarios.get(0);
				idSelecionado = vAelHrGradeDispVO.getId();
			}
		} else if(examesSelecionados.size()>1){
			List<GrupoExameVO> listaGrupoExame = this.coletaExamesFacade.pesquisarGrupoExameTransferenciaAgendamento(examesSelecionados);
			Date dataHoraReativacao = this.coletaExamesFacade.obterMaiorDataReativacao(listaGrupoExame);
			listaHorarios = this.coletaExamesFacade.pesquisarHorariosParaGrupoExameSelecionado(dataHoraReativacao, tipo1, tipo2, listaGrupoExame, this.data, this.hora, this.grade, this.grupoExame, this.salaExecutoraExame, this.servidor);
			if(listaHorarios!=null && listaHorarios.size()>0){
				this.vAelHrGradeDispVO = listaHorarios.get(0);
				idSelecionado = vAelHrGradeDispVO.getId();
			}
		}
		this.primeiraPesquisa =  true;
	}
	
	public void atribuirHorario(){
		for(VAelHrGradeDispVO vAelHrGradeDispVO: listaHorarios){
			if(vAelHrGradeDispVO.getId().equals(idSelecionado)){
				this.vAelHrGradeDispVO = vAelHrGradeDispVO;
				break;
			}
		}
	}
	
	public void limparHorarios(){
		this.listaHorarios = null;
		this.primeiraPesquisa = false;
	}
	
	public void limparPesquisa(){
		this.data = null;
		this.hora = null;
		this.grade = null;
		this.grupoExame = null;
		this.salaExecutoraExame = null;
		this.servidor = null;
		this.listaHorarios = null;
		this.primeiraPesquisa = false;
	}
	
	public List<ItemHorarioAgendadoVO> getExamesSelecionados() {
		return examesSelecionados;
	}

	public void setExamesSelecionados(List<ItemHorarioAgendadoVO> examesSelecionados) {
		this.examesSelecionados = examesSelecionados;
	}

	public Boolean getPrimeiraPesquisa() {
		return primeiraPesquisa;
	}

	public void setPrimeiraPesquisa(Boolean primeiraPesquisa) {
		this.primeiraPesquisa = primeiraPesquisa;
	}

	/**
	 * Seleciona todos os Exames.
	 */
	public void marcarTodosExames() {
		if(listaExamesAgendados!=null) {
			for(ItemHorarioAgendadoVO item : listaExamesAgendados) {
				item.setSelecionado(this.todosExamesSelecionados);
			}
		}
		this.listaHorarios = null;
		this.primeiraPesquisa = false;
	}
	
	public List<AelGrupoExames> pesquisarGrupoExame(String parametro) {
		return this.examesFacade.pesquisarGrupoExamePorCodigoOuDescricaoEUnidade(parametro, unidadeExecutora);
	}
	
	public List<AelSalasExecutorasExames> pesquisarSala(String parametro) {
		return this.examesFacade.pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidade(parametro, unidadeExecutora);
	}
	
	public List<RapServidores> pesquisarServidor(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidores(parametro);
	}
	
	public void transferir() {
		for(ItemHorarioAgendadoVO item: examesSelecionados){
			AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
			AelItemHorarioAgendadoId itemHorarioAgendadoId = new AelItemHorarioAgendadoId();
			itemHorarioAgendadoId.setHedDthrAgenda(vAelHrGradeDispVO.getDthrAgenda());
			itemHorarioAgendadoId.setHedGaeUnfSeq(vAelHrGradeDispVO.getGrade());
			itemHorarioAgendadoId.setHedGaeSeqp(vAelHrGradeDispVO.getSeqGrade());
			itemHorarioAgendadoId.setIseSeqp(item.getSeqp());
			itemHorarioAgendadoId.setIseSoeSeq(item.getSoeSeq());
			itemHorarioAgendado.setId(itemHorarioAgendadoId);
			AelUnfExecutaExamesId id = new AelUnfExecutaExamesId();
			AghUnidadesFuncionais unidadeFuncional = null;
			AelUnfExecutaExames unfExecutaExame = null;
			String nomeMicrocomputador = null;			
			try {
				itemHorarioAgendado.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				id.setEmaExaSigla(item.getSigla());
				id.setEmaManSeq(item.getSeqMaterialAnalise());
				unidadeFuncional = this.aghuFacade.obterUnidadeFuncional(item.getSeqUnidade());
				id.setUnfSeq(unidadeFuncional);
				unfExecutaExame = this.agendamentoExamesFacade.obterAelUnfExecutaExamesDAOPorId(id);				
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			} catch (ApplicationBusinessException e) {
				LOG.error("Não encontrou o servidor logado!", e);
			}
			try {
				this.coletaExamesFacade.transferirHorarioAgendado(itemHorarioAgendado, vAelHrGradeDispVO.getHrExtra(), unfExecutaExame, nomeMicrocomputador);
			
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(),e);
			}
		}
		
		this.limparPesquisa();
		this.listaExamesAgendados = this.coletaExamesFacade.pesquisarExamesParaTransferencia(this.getGaeUnfSeq(), this.getGaeSeqp(), this.getDthrAgenda());
		this.examesSelecionados = null;
		this.idSelecionado = null;
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_TRANSFERENCIA_AGENDAMENTO");
	}
	
	public String voltar() {
		this.todosExamesSelecionados = false;
		this.limparPesquisa();
		return "pesquisaAgendaExamesHorarios";
	}

	public IColetaExamesFacade getColetaExamesFacade() {
		return coletaExamesFacade;
	}

	public void setColetaExamesFacade(IColetaExamesFacade coletaExamesFacade) {
		this.coletaExamesFacade = coletaExamesFacade;
	}

	public List<ItemHorarioAgendadoVO> getListaExamesAgendados() {
		return listaExamesAgendados;
	}

	public void setListaExamesAgendados(
			List<ItemHorarioAgendadoVO> listaExamesAgendados) {
		this.listaExamesAgendados = listaExamesAgendados;
	}

	public Boolean getTodosExamesSelecionados() {
		return todosExamesSelecionados;
	}

	public void setTodosExamesSelecionados(Boolean todosExamesSelecionados) {
		this.todosExamesSelecionados = todosExamesSelecionados;
	}

	public Short getGaeUnfSeq() {
		return gaeUnfSeq;
	}

	public void setGaeUnfSeq(Short gaeUnfSeq) {
		this.gaeUnfSeq = gaeUnfSeq;
	}

	public Integer getGaeSeqp() {
		return gaeSeqp;
	}

	public void setGaeSeqp(Integer gaeSeqp) {
		this.gaeSeqp = gaeSeqp;
	}



	public Date getDthrAgenda() {
		return dthrAgenda;
	}

	public void setDthrAgenda(Date dthrAgenda) {
		this.dthrAgenda = dthrAgenda;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public AelSalasExecutorasExames getSalaExecutoraExame() {
		return salaExecutoraExame;
	}

	public void setSalaExecutoraExame(AelSalasExecutorasExames salaExecutoraExame) {
		this.salaExecutoraExame = salaExecutoraExame;
	}

	public IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	public void setExamesFacade(IExamesFacade examesFacade) {
		this.examesFacade = examesFacade;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IAgendamentoExamesFacade getAgendamentoExamesFacade() {
		return agendamentoExamesFacade;
	}

	public void setAgendamentoExamesFacade(
			IAgendamentoExamesFacade agendamentoExamesFacade) {
		this.agendamentoExamesFacade = agendamentoExamesFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public List<VAelHrGradeDispVO> getListaHorarios() {
		return listaHorarios;
	}

	public void setListaHorarios(List<VAelHrGradeDispVO> listaHorarios) {
		this.listaHorarios = listaHorarios;
	}

	public Integer getIdSelecionado() {
		return idSelecionado;
	}

	public void setIdSelecionado(Integer idSelecionado) {
		this.idSelecionado = idSelecionado;
	}
	
	
}


