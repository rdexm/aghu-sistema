package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioReservadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PrescricaoPacienteVO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

public class ConfirmarCancelarReservaHorarioController extends ActionController {

	private static final long serialVersionUID = -2765002898537869609L;

	private static final Log LOG = LogFactory.getLog(ConfirmarCancelarReservaHorarioController.class);

	private static final String PAGE_AGENDAMENTO_SESSAO = "procedimentoterapeutico-agendamentoSessao";

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;

	@Inject
	private AgendamentoSessaoController agendamentoSessaoController;
	
	private List<CadIntervaloTempoVO> listaPrescricoes;
	private List<HorarioReservadoVO> horariosReservados;
	private PrescricaoPacienteVO prescricaoSelecionada;
	private CadIntervaloTempoVO intervaloSelecionado;
	private HorarioReservadoVO reservaSelecionada;
	private boolean desabilitaCancelar;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void inicio() {

		prescricaoSelecionada = agendamentoSessaoController.getPrescricaoSelecionada();
		horariosReservados = agendamentoSessaoController.getHorariosReservados();

		listaPrescricoes = new ArrayList<CadIntervaloTempoVO>();
		for (CadIntervaloTempoVO item : agendamentoSessaoController.getListaHorarios()) {
			if (item.isAgendar()) {
				listaPrescricoes.add(item);
			}
		}
		
		desabilitaCancelar = true;
		for (HorarioReservadoVO horario : horariosReservados) {
			if (!horario.isConforme()) {
				desabilitaCancelar = false;
				break;
			}
		}
	}

	public String cancelarReserva() {

		procedimentoTerapeuticoFacade.cancelarReservas(horariosReservados);

		return PAGE_AGENDAMENTO_SESSAO;
	}
	
	public String confirmarReserva() {

			try {
				procedimentoTerapeuticoFacade.confirmarReservas(horariosReservados, agendamentoSessaoController.getTipoSessao().getSeq(),
						agendamentoSessaoController.getSala().getSeq(), agendamentoSessaoController.getSala().getEspecialidade().getSeq(),
						agendamentoSessaoController.getPrescricao(), getEnderecoRedeHostRemoto(), listaPrescricoes);
				
				apresentarMsgNegocio("MENSAGEM_RESERVA_CONFIRMADA");
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);

				return null;
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				
				return null;
			}

		return PAGE_AGENDAMENTO_SESSAO;
	}
	
	public String voltar() {
		return PAGE_AGENDAMENTO_SESSAO;
	}

	// getters & setters
	public List<CadIntervaloTempoVO> getListaPrescricoes() {
		return listaPrescricoes;
	}

	public void setListaPrescricoes(List<CadIntervaloTempoVO> listaPrescricoes) {
		this.listaPrescricoes = listaPrescricoes;
	}

	public List<HorarioReservadoVO> getHorariosReservados() {
		return horariosReservados;
	}

	public void setHorariosReservados(List<HorarioReservadoVO> horariosReservados) {
		this.horariosReservados = horariosReservados;
	}

	public PrescricaoPacienteVO getPrescricaoSelecionada() {
		return prescricaoSelecionada;
	}

	public void setPrescricaoSelecionada(PrescricaoPacienteVO prescricaoSelecionada) {
		this.prescricaoSelecionada = prescricaoSelecionada;
	}

	public boolean isDesabilitaCancelar() {
		return desabilitaCancelar;
	}

	public void setDesabilitaCancelar(boolean desabilitaCancelar) {
		this.desabilitaCancelar = desabilitaCancelar;
	}

	public HorarioReservadoVO getReservaSelecionada() {
		return reservaSelecionada;
	}

	public void setReservaSelecionada(HorarioReservadoVO reservaSelecionada) {
		this.reservaSelecionada = reservaSelecionada;
	}

	public CadIntervaloTempoVO getIntervaloSelecionado() {
		return intervaloSelecionado;
	}

	public void setIntervaloSelecionado(CadIntervaloTempoVO intervaloSelecionado) {
		this.intervaloSelecionado = intervaloSelecionado;
	}

}
