package br.gov.mec.aghu.exames.agendamento.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelExaAgendPacVO;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExaAgendPac;
import br.gov.mec.aghu.model.VAelExaAgendPacId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para tela de visualização dos exames agendados do paciente.
 * 
 * @author g.zapalaglio
 *
 */

public class VisualizarExamesAgendadosPacienteController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(VisualizarExamesAgendadosPacienteController.class);
	

	private static final long serialVersionUID = 3226363583840985985L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	
	private List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO;
	
	private List<ItemHorarioAgendadoVO> listaAgendamentoEmGrupo;
	private ItemHorarioAgendadoVO itemSelecionado;
	private Short unfExecutora;
	private List<VAelExaAgendPacVO> examesAgendados;
	private List<VAelExaAgendPac> itemExameAgendado;
	private VAelExaAgendPacVO exameSelecionado;
	private Integer idSelecionado;
    private String cameFrom = VISUALIZAR_EXAMES_AGENDAMENTO_SELECAO;
    private String origem;
    private static final String LISTAR_EXAMES_AGENDAMENTO_SELECAO = "listarExamesAgendamentoSelecao";
    private static final String CONSULTA_HORARIOS_LIVRES_EXAMES = "consultaHorariosLivresExame";
    private static final String VISUALIZAR_EXAMES_AGENDAMENTO_SELECAO = "visualizarExamesAgendadosPaciente";

    public void iniciar() {
	 

		this.limpar();
//		for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO){
//			if (itemHorarioAgendadoVO.getSelecionado()){
//				this.itemSelecionado = itemHorarioAgendadoVO;
//			}
//		}
        this.itemSelecionado = listaItemHorarioAgendadoVO.get(0);
		this.examesAgendados = this.agendamentoExamesFacade.obterExamesAgendadosDoPaciente(itemSelecionado.getPacCodigo(), 
				itemSelecionado.getSigla(), itemSelecionado.getSeqMaterialAnalise(), itemSelecionado.getSeqUnidade());

        if (this.getOrigem() == null) {
            setOrigem(LISTAR_EXAMES_AGENDAMENTO_SELECAO);
        }
    }
	
	public void obterExameSelecionado() throws ApplicationBusinessException {
		VAelExaAgendPacId exameAgendadoPacienteId = null;
		for(VAelExaAgendPacVO exameAgendadoPacienteVO : examesAgendados){
			if(exameAgendadoPacienteVO.getId().equals(idSelecionado)){
				exameAgendadoPacienteId = exameAgendadoPacienteVO.getExameAgendadoPaciente().getId();
				
				this.itemExameAgendado = this.agendamentoExamesFacade.obterInformacoesExamesAgendadosPaciente(itemSelecionado.getPacCodigo(), 
						itemSelecionado.getSigla(), itemSelecionado.getSeqMaterialAnalise(), itemSelecionado.getSeqUnidade(),
						exameAgendadoPacienteId.getHedGaeUnfSeq(), exameAgendadoPacienteId.getHedGaeSeqp(), 
						exameAgendadoPacienteId.getHedDthrAgenda());
				this.exameSelecionado = exameAgendadoPacienteVO;
				break;
			}
		}
		listaAgendamentoEmGrupo = this.agendamentoExamesFacade.obterListaExamesParaAgendamentoEmGrupo(itemSelecionado.getSoeSeq(), listaItemHorarioAgendadoVO, 
				exameAgendadoPacienteId.getHedGaeUnfSeq(), exameAgendadoPacienteId.getHedGaeSeqp(), itemSelecionado.getSeqp());
	}
	
	public String gravar(){
		if(exameSelecionado==null) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_NENHUM_HORARIO_SELECIONADO");
			return null;
		}
		else {
			listaAgendamentoEmGrupo.add(itemSelecionado);
			VAelExaAgendPacId exameAgendadoPacienteId = exameSelecionado.getExameAgendadoPaciente().getId();
		
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			try {
				AelHorarioExameDisp horarioExameDisp = this.agendamentoExamesFacade.obterHorarioExameDisponivel(exameAgendadoPacienteId.getHedDthrAgenda(),
                        exameAgendadoPacienteId.getHedGaeUnfSeq(), exameAgendadoPacienteId.getHedGaeSeqp());
				for(ItemHorarioAgendadoVO item : listaAgendamentoEmGrupo) {
					AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
					AelItemHorarioAgendadoId itemHorarioAgendadoId = new AelItemHorarioAgendadoId();
					itemHorarioAgendadoId.setIseSeqp(item.getSeqp());
					itemHorarioAgendadoId.setIseSoeSeq(item.getSoeSeq());
					itemHorarioAgendadoId.setHedDthrAgenda(exameAgendadoPacienteId.getHedDthrAgenda());
					itemHorarioAgendadoId.setHedGaeUnfSeq(exameAgendadoPacienteId.getHedGaeUnfSeq());
					itemHorarioAgendadoId.setHedGaeSeqp(exameAgendadoPacienteId.getHedGaeSeqp());
					itemHorarioAgendado.setId(itemHorarioAgendadoId);
					RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
					itemHorarioAgendado.setServidor(servidorLogado);
					
					AelItemSolicitacaoExames itemSolicitacaoExameOriginal = agendamentoExamesFacade.obterItemSolicitacaoExameOriginal(item.getSoeSeq(), item.getSeqp());
					
					this.agendamentoExamesFacade.inserirItemHorarioAgendado(itemHorarioAgendado, itemSolicitacaoExameOriginal, horarioExameDisp.getIndHorarioExtra(), 
							Boolean.TRUE, Boolean.TRUE, item.getSigla(), item.getSeqMaterialAnalise(), item.getSeqUnidade(), nomeMicrocomputador);
				}
				//this.agendamentoExamesFacade.flush();
//				List<AelItemHorarioAgendado> listaHorarioAgendado = agendamentoExamesFacade
//					.pesquisarItemHorarioAgendadoPorGradeESoeSeq(horarioExameDisp.getId().getGaeUnfSeq(), 
//							horarioExameDisp.getId().getGaeSeqp(), itemSelecionado.getSoeSeq());
				this.listaItemHorarioAgendadoVO = this.agendamentoExamesFacade.atualizarListaItemHorarioAgendadoVO( listaItemHorarioAgendadoVO, horarioExameDisp.getId().getGaeUnfSeq(), 
						horarioExameDisp.getId().getGaeSeqp(), horarioExameDisp.getGradeAgendaExame().getSalaExecutoraExames().getNumero(),itemSelecionado.getSoeSeq());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVACAO_HORARIO_EXAME");

            } catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
				LOG.error("Erro",e);
				return null;
			}
		}
        return getOrigem();
	}
	
	public void limpar() {
		examesAgendados = null;
		itemExameAgendado = null;
		exameSelecionado = null;
		idSelecionado = null;
	}

	public String cancelar(){
//        if (getCameFrom() != null){
//            return getCameFrom();
//        } else {
            return CONSULTA_HORARIOS_LIVRES_EXAMES;
//        }
    }

	public void setIdSelecionado(Integer idSelecionado) {
		this.idSelecionado = idSelecionado;
	}

	public Integer getIdSelecionado() {
		return idSelecionado;
	}

	public void setUnfExecutora(Short unfExecutora) {
		this.unfExecutora = unfExecutora;
	}

	public Short getUnfExecutora() {
		return unfExecutora;
	}

	public void setItemExameAgendado(List<VAelExaAgendPac> itemExameAgendado) {
		this.itemExameAgendado = itemExameAgendado;
	}

	public List<VAelExaAgendPac> getItemExameAgendado() {
		return itemExameAgendado;
	}
	
	public List<VAelExaAgendPacVO> getExamesAgendados() {
		return examesAgendados;
	}

	public void setExamesAgendados(List<VAelExaAgendPacVO> examesAgendados) {
		this.examesAgendados = examesAgendados;
	}

	public VAelExaAgendPacVO getExameSelecionado() {
		return exameSelecionado;
	}

	public void setExameSelecionado(VAelExaAgendPacVO exameSelecionado) {
		this.exameSelecionado = exameSelecionado;
	}

	public IAgendamentoExamesFacade getAgendamentoExamesFacade() {
		return agendamentoExamesFacade;
	}

	public void setAgendamentoExamesFacade(
			IAgendamentoExamesFacade agendamentoExamesFacade) {
		this.agendamentoExamesFacade = agendamentoExamesFacade;
	}

	public void setListaItemHorarioAgendadoVO(
			List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO) {
		this.listaItemHorarioAgendadoVO = listaItemHorarioAgendadoVO;
	}

	public List<ItemHorarioAgendadoVO> getListaItemHorarioAgendadoVO() {
		return listaItemHorarioAgendadoVO;
	}

	public void setItemSelecionado(ItemHorarioAgendadoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public ItemHorarioAgendadoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setListaAgendamentoEmGrupo(List<ItemHorarioAgendadoVO> listaAgendamentoEmGrupo) {
		this.listaAgendamentoEmGrupo = listaAgendamentoEmGrupo;
	}

	public List<ItemHorarioAgendadoVO> getListaAgendamentoEmGrupo() {
		return listaAgendamentoEmGrupo;
	}

    public String getCameFrom() {
        return cameFrom;
    }

    public void setCameFrom(String cameFrom) {
        this.cameFrom = cameFrom;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }
}
