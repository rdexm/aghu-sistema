package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.net.UnknownHostException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.HostRemotoCache;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.dominio.DominioTipoCups;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ImpressaoTicketAgendamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ReservasVO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;

public class ImprimirTicketAgendamentoController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5435847980612126280L;
	
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	private static final String PAGE_SUGESTAO_AGENDA = "sugestaoAgenda";
	
	@Inject
	private HostRemotoCache hostRemoto;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private AipPacientes paciente;
	
	private Boolean reimpressao = true;
	
	private List<ImpressaoTicketAgendamentoVO> listaCiclo;

	private List<ReservasVO> listaCicloReservas;
	
	private List<Integer> listCloSeq;

	private List<Integer> listCicloSeq;
	
	private Integer ciclo;

	private Short seqAgendamento;

	private Integer valorRadio;
	
	private Integer valorAgendado = 1;

	private Integer valorReservado = 2;
	
	private List<ImpressaoTicketAgendamentoVO> listRegistroHorariosSessao;

	List<ReservasVO> listConsultaReservas;
	
	private List<CadIntervaloTempoVO> listaHorarios;
	
	private Boolean cicloCombo = Boolean.FALSE;

	private Boolean cicloReservasCombo = Boolean.FALSE;
	
    private ImpressaoTicketAgendamentoVO selecionado;

    private ReservasVO selecionadoreservas;
    
    private String textoAgendamento;

    private String textoReservado;

    private boolean cameFrom = false;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void limpar() {
		this.listRegistroHorariosSessao = null;
		listConsultaReservas = null;
		this.listaCiclo = new ArrayList<ImpressaoTicketAgendamentoVO>();
		this.listaCicloReservas = new ArrayList<ReservasVO>();
		this.listCloSeq = new ArrayList<Integer>();
		this.listCicloSeq = new ArrayList<Integer>();
		this.ciclo = null;
		this.cicloCombo = Boolean.FALSE;
		this.cicloReservasCombo = Boolean.FALSE;

	}
	
	
	public void inicio() {
		if (!cameFrom){
			valorRadio = valorAgendado;			
		}
		this.carregarListas();
		if (listaCiclo != null && !listaCiclo.isEmpty()){
			for (ImpressaoTicketAgendamentoVO concat : listaCiclo) {
				textoAgendamento = concat.getCloSeq() + concat.getPtcDescricao();
			}			
		}
	}

	
	public boolean naoPossuiPaciente(){
		if (this.paciente != null && this.paciente.getCodigo() != null && this.listRegistroHorariosSessao != null && !this.listRegistroHorariosSessao.isEmpty()) {
			return false;
		}
		else{
			return true;
		}
	}

	public void pesquisarRadio(){
		if (paciente != null){
			listRegistroHorariosSessao = null;
			listConsultaReservas = null;
			this.listaCiclo = new ArrayList<ImpressaoTicketAgendamentoVO>();
			this.listaCicloReservas = new ArrayList<ReservasVO>(); 
			this.carregarListas();			
		}
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			this.paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			this.carregarListas();
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}

	private void carregarListas() {
		if (this.paciente != null && this.paciente.getCodigo() != null) {
			if (valorRadio.equals(1)){
				this.listRegistroHorariosSessao = this.procedimentoTerapeuticoFacade.obterListaRegistroHorariosSessao(this.paciente.getCodigo(), null, 1);
				if (this.listRegistroHorariosSessao != null && this.listRegistroHorariosSessao.isEmpty()) {
					this.apresentarMsgNegocio(Severity.INFO, "IMPRISSAO_TICKET_AGENDAMENTO_MS01");
				}else{
					if (this.reimpressao) {
						this.listCloSeq = new ArrayList<Integer>();
						if (this.listRegistroHorariosSessao != null && !this.listRegistroHorariosSessao.isEmpty()) {
							for (ImpressaoTicketAgendamentoVO elementList : this.listRegistroHorariosSessao) {
								this.listCloSeq.add(elementList.getSesCloSeq());							
							}
						}
						this.listaCiclo = this.procedimentoTerapeuticoFacade.obterListaCiclo(this.paciente.getCodigo(), this.listCloSeq, 2);
					}
					else{
						if (this.listaHorarios != null && !this.listaHorarios.isEmpty()) {
							for (CadIntervaloTempoVO element : listaHorarios) {
								this.ciclo = element.getCiclo() != null ? element.getCiclo().intValue() : null;
							}
						}
					}
				}
				
			}else{
				
				listConsultaReservas = this.procedimentoTerapeuticoFacade.pesquisarConsultasReservas(this.paciente.getCodigo(), 9);
				if (this.listConsultaReservas != null && this.listConsultaReservas.isEmpty()) {
					this.apresentarMsgNegocio(Severity.INFO, "IMPRISSAO_TICKET_RESERVADOS_MS01");
				}else{
					if (this.reimpressao) {
						this.listCicloSeq = new ArrayList<Integer>();
						if (this.listConsultaReservas != null && !this.listConsultaReservas.isEmpty()) {
							for (ReservasVO elementList : this.listConsultaReservas) {
								if (elementList.getCiclo() != null){
									this.listCicloSeq.add(Integer.valueOf(elementList.getCiclo()));
								}
							}
						}
						this.listaCicloReservas = this.procedimentoTerapeuticoFacade.obterListaCicloReservas(this.paciente.getCodigo(), this.listCicloSeq);
					}
					else{
						if (this.listaHorarios != null && !this.listaHorarios.isEmpty()) {
							for (CadIntervaloTempoVO element : listaHorarios) {
								this.ciclo = element.getCiclo() != null ? element.getCiclo().intValue() : null;
							}
						}
					}
				}				
			}
		}else{
			limpar();
		}
	}
	
	
	public void carregaRegistroHorariosfiltradosPorCiclo(){
		if (this.reimpressao && this.ciclo != null) {
			List<Integer> listCloSeqProtocolo = new ArrayList<Integer>();
			listCloSeqProtocolo.add(ciclo);
			this.listRegistroHorariosSessao = this.procedimentoTerapeuticoFacade.obterListaRegistroHorariosSessao(this.paciente.getCodigo(), listCloSeqProtocolo, 4);
			this.cicloCombo = Boolean.TRUE;
			
		}else{
			this.carregarListas();
			this.cicloCombo = Boolean.FALSE;
		}
	}

	public void carregaRegistroHorariosfiltradosPorCicloReservas(){
		if (this.reimpressao && this.seqAgendamento != null) {					
			this.listConsultaReservas = this.procedimentoTerapeuticoFacade.pesquisarConsultarDiasReservas(seqAgendamento);
			this.cicloReservasCombo = Boolean.TRUE;
			
		}else{
			this.carregarListas();
			this.cicloReservasCombo = Boolean.FALSE;
		}
	}
	
	public String redirecionarPesquisaFonetica() {
		return PESQUISA_FONETICA;
	}
	
	public void processarBuscaPacientePorCodigo(Integer codigoPaciente){
		if(codigoPaciente != null){
			this.setPaciente(this.pacienteFacade.buscaPaciente(codigoPaciente));
		}else{
			this.paciente = null;
		}
	}
	
	/**
	 * Obtem p protocolo da grid
	 * 
	 * @param cloSeq
	 * @return
	 */
	public String obterProtocolo(Integer cloSeq){
		List<Integer> listCloSeqProtocolo = new ArrayList<Integer>();
		if (cloSeq != null) {
			listCloSeqProtocolo.add(cloSeq);
		}
		return this.procedimentoTerapeuticoFacade.obterProtocolos(listCloSeqProtocolo);
	}
	
	public String cancelar() {
		limpar();
		return PAGE_SUGESTAO_AGENDA;
	}

	/**
	 * Retorna impressora matricial do host remoto se houver.
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	private String getMatricialHost() throws UnknownHostException {
		String remoteHost = hostRemoto.getEnderecoRedeHostRemoto();
		ImpComputador computador = cadastrosBasicosCupsFacade
				.obterComputador(remoteHost);
		ImpComputadorImpressora impressora = null;
		if (computador == null){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_COMPUTADOR_NAO_CADASTRADO", new Object[] 
					{remoteHost});
		}
		else {
			impressora = cadastrosBasicosCupsFacade.obterImpressora(
					computador.getSeq(), DominioTipoCups.RAW);
			if(impressora == null){
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_IMPRESSORA_NAO_CADASTRADA_SEM_PARAMETRO", new Object[] 
						{remoteHost});
			}
		}

		return impressora == null ? null : impressora.getImpImpressora()
				.getFilaImpressora();
	}
	
	public void imprimirTicket() {

		try {
			if (valorRadio.equals(1)){
				this.listCloSeq = new ArrayList<Integer>();
				if (this.listRegistroHorariosSessao != null
						&& !this.listRegistroHorariosSessao.isEmpty()) {
					for (ImpressaoTicketAgendamentoVO elementList : this.listRegistroHorariosSessao) {
						if (elementList.getSesCloSeq() != null){
							this.listCloSeq.add(elementList.getSesCloSeq());							
						}
					}
				}
				
				String matricial = this.getMatricialHost();
				if (StringUtils.isNotBlank(matricial)) {
					String textoMatricial = procedimentoTerapeuticoFacade
							.obterTextoTicket(this.paciente, this.listCloSeq);
					textoMatricial = Normalizer.normalize(textoMatricial,
							Normalizer.Form.NFD);
					textoMatricial = textoMatricial.replaceAll("[^\\p{ASCII}]", "");
					// Necessário adicionar algumas linhas no final para ficar no
					// ponto de corte correto da impressora.
					textoMatricial = textoMatricial.concat("\n\n\n\n\n\n\n\n\n\n");
					
					this.sistemaImpressao.imprimir(textoMatricial, matricial);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_TICKET");
				}				
			}else{
				this.listCicloSeq = new ArrayList<Integer>();
				if (this.listConsultaReservas != null
						&& !this.listConsultaReservas.isEmpty()) {
					for (ReservasVO elementList : this.listConsultaReservas) {
						if (elementList.getCiclo() != null){
							this.listCicloSeq.add(Integer.valueOf(elementList.getCiclo()));							
						}
					}
				}
				
				String matricial = this.getMatricialHost();
				if (StringUtils.isNotBlank(matricial)) {
					String textoMatricial = procedimentoTerapeuticoFacade
							.obterTextoTicketReservadas(this.paciente, this.listCicloSeq, this.seqAgendamento);
					textoMatricial = Normalizer.normalize(textoMatricial,
							Normalizer.Form.NFD);
					textoMatricial = textoMatricial.replaceAll("[^\\p{ASCII}]", "");
					// Necessário adicionar algumas linhas no final para ficar no
					// ponto de corte correto da impressora.
					textoMatricial = textoMatricial.concat("\n\n\n\n\n\n\n\n\n\n");
					
					this.sistemaImpressao.imprimir(textoMatricial, matricial);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_TICKET");
				}				
			}							
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);

		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public AipPacientes getPaciente() {
		return paciente;
	}
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	public Boolean getReimpressao() {
		return reimpressao;
	}
	public void setReimpressao(Boolean reimpressao) {
		this.reimpressao = reimpressao;
	}
	public List<ImpressaoTicketAgendamentoVO> getListaCiclo() {
		return listaCiclo;
	}
	public void setListaCiclo(List<ImpressaoTicketAgendamentoVO> listaCiclo) {
		this.listaCiclo = listaCiclo;
	}
	public List<ImpressaoTicketAgendamentoVO> getListRegistroHorariosSessao() {
		return listRegistroHorariosSessao;
	}
	public void setListRegistroHorariosSessao(
			List<ImpressaoTicketAgendamentoVO> listRegistroHorariosSessao) {
		this.listRegistroHorariosSessao = listRegistroHorariosSessao;
	}
	public Integer getCiclo() {
		return ciclo;
	}
	public void setCiclo(Integer ciclo) {
		this.ciclo = ciclo;
	}
	public List<CadIntervaloTempoVO> getListaHorarios() {
		return listaHorarios;
	}
	public void setListaHorarios(List<CadIntervaloTempoVO> listaHorarios) {
		this.listaHorarios = listaHorarios;
	}
	public Boolean getCicloCombo() {
		return cicloCombo;
	}
	public void setCicloCombo(Boolean cicloCombo) {
		this.cicloCombo = cicloCombo;
	}
	public ImpressaoTicketAgendamentoVO getSelecionado() {
		return selecionado;
	}
	public void setSelecionado(ImpressaoTicketAgendamentoVO selecionado) {
		this.selecionado = selecionado;
	}

	public Integer getValorRadio() {
		return valorRadio;
	}

	public void setValorRadio(Integer valorRadio) {
		this.valorRadio = valorRadio;
	}

	public Integer getValorAgendado() {
		return valorAgendado;
	}

	public void setValorAgendado(Integer valorAgendado) {
		this.valorAgendado = valorAgendado;
	}

	public Integer getValorReservado() {
		return valorReservado;
	}

	public void setValorReservado(Integer valorReservado) {
		this.valorReservado = valorReservado;
	}

	public String getTextoReservado() {
		return textoReservado;
	}

	public void setTextoReservado(String textoReservado) {
		this.textoReservado = textoReservado;
	}

	public String getTextoAgendamento() {
		return textoAgendamento;
	}

	public void setTextoAgendamento(String textoAgendamento) {
		this.textoAgendamento = textoAgendamento;
	}

	public ReservasVO getSelecionadoreservas() {
		return selecionadoreservas;
	}

	public void setSelecionadoreservas(ReservasVO selecionadoreservas) {
		this.selecionadoreservas = selecionadoreservas;
	}

	public List<ReservasVO> getListConsultaReservas() {
		return listConsultaReservas;
	}

	public void setListConsultaReservas(List<ReservasVO> listConsultaReservas) {
		this.listConsultaReservas = listConsultaReservas;
	}

	public List<Integer> getListCicloSeq() {
		return listCicloSeq;
	}

	public void setListCicloSeq(List<Integer> listCicloSeq) {
		this.listCicloSeq = listCicloSeq;
	}

	public List<ReservasVO> getListaCicloReservas() {
		return listaCicloReservas;
	}

	public void setListaCicloReservas(List<ReservasVO> listaCicloReservas) {
		this.listaCicloReservas = listaCicloReservas;
	}

	public Boolean getCicloReservasCombo() {
		return cicloReservasCombo;
	}

	public void setCicloReservasCombo(Boolean cicloReservasCombo) {
		this.cicloReservasCombo = cicloReservasCombo;
	}

	public boolean isCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(boolean cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Short getSeqAgendamento() {
		return seqAgendamento;
	}

	public void setSeqAgendamento(Short seqAgendamento) {
		this.seqAgendamento = seqAgendamento;
	}
}
