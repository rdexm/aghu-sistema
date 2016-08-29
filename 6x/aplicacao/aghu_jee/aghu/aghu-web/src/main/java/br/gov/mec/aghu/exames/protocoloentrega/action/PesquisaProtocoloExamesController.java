package br.gov.mec.aghu.exames.protocoloentrega.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.ItensProtocoloVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.exames.pesquisa.vo.ResultadoPesquisaProtocoloVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;

public class PesquisaProtocoloExamesController extends ActionController {

	private static final long serialVersionUID = -4809445928872398498L;

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	
	private List<PesquisaExamesPacientesVO> listaPacientes;
	private PesquisaExamesPacientesVO pacienteSelecionado;
	private List<PesquisaExamesPacientesResultsVO> listaResultadoPesquisa;
	private List<ResultadoPesquisaProtocoloVO> listaResultadoPesquisaProtocolo;
	private List<ItensProtocoloVO> listaItensProtocolo = new ArrayList<ItensProtocoloVO>();
	private PesquisaExamesFiltroVO filtro;
	private Boolean selecionado;
	private Integer codigoPaciente;
	private Integer codigoConsulta;
	private Integer prontuario;
	private Long numeroProtocolo;
	private ResultadoPesquisaProtocoloVO resultadoPesquisaProtocoloVO;
	
	private Boolean isHist = Boolean.FALSE;
	private static final String EXAMES_GERAR_PROTOCOLO = "exames-gerarProtocoloEntregaExames";

	private Comparator<PesquisaExamesPacientesResultsVO> currentComparator;
	
	public void selecionarPaciente(PesquisaExamesPacientesVO pesquisaExamesPacientesVO, Integer codigoConsulta) throws ApplicationBusinessException, ApplicationBusinessException{
	
			
			if (listaPacientes == null){
				listaPacientes = new ArrayList<>();
			}
			listaPacientes.add(pesquisaExamesPacientesVO);
			
			setCodigoPaciente(pesquisaExamesPacientesVO.getCodigo());
			setCodigoConsulta(codigoConsulta);
			setProntuario(pesquisaExamesPacientesVO.getProntuario());
			
			AipPacientes pac = pacienteFacade.buscaPaciente(codigoPaciente);
			if(isHist){
				
				setListaResultadoPesquisa(this.pesquisaExamesFacade.buscaExamesSolicitadosPorPacienteHist(pac));
			}else{
				setListaResultadoPesquisa(this.pesquisaExamesFacade.buscaExamesSolicitadosPorPaciente(codigoPaciente, codigoConsulta, filtro));
			}
			
			// mantem a ordem corrente
			if (this.currentComparator != null) {
				Collections.sort(this.listaResultadoPesquisa, this.currentComparator);
			}
		}
	
	public void selecionarPaciente() throws ApplicationBusinessException, ApplicationBusinessException {
		this.selecionarPaciente(pacienteSelecionado.getCodigo(), pacienteSelecionado.getConsulta(), pacienteSelecionado.getProntuario());
	}
	
	public void selecionarPaciente(Integer codigoPaciente, Integer codigoConsulta, Integer prontuario) throws ApplicationBusinessException, ApplicationBusinessException{
	
		setCodigoPaciente(codigoPaciente);
		setCodigoConsulta(codigoConsulta);
		setProntuario(prontuario);
	
		if(isHist){
			AipPacientes pac = pacienteFacade.buscaPaciente(codigoPaciente);
			setListaResultadoPesquisa(this.pesquisaExamesFacade.buscaExamesSolicitadosPorPacienteHist(pac));
		}else{
			setListaResultadoPesquisa(this.pesquisaExamesFacade.buscaExamesSolicitadosPorPaciente(codigoPaciente, codigoConsulta, filtro));
		}
		
		// mantem a ordem corrente
		if (this.currentComparator != null) {
			Collections.sort(this.listaResultadoPesquisa, this.currentComparator);
		}
	}
	
	public void selecionaPaciente() {
		
		if (resultadoPesquisaProtocoloVO != null) {
			this.numeroProtocolo = resultadoPesquisaProtocoloVO.getProtocolo();
			this.listaItensProtocolo = pesquisaExamesFacade.buscarItensProtocolo(resultadoPesquisaProtocoloVO.getProtocolo());
			this.selecionado = Boolean.TRUE;
		} else {
			setListaItensProtocolo(null);
		}
	}
	
	public void selecionarPacientePorProtocolo(Long protocolo) {
		List<ResultadoPesquisaProtocoloVO> lista = new ArrayList<ResultadoPesquisaProtocoloVO>();
		lista = pesquisaExamesFacade.buscarProtocolo(protocolo);
		setListaResultadoPesquisaProtocolo(lista);
	}
	
	public void selecionarPacientePorProntuario(Integer prontuario) {
		List<ResultadoPesquisaProtocoloVO> lista = new ArrayList<ResultadoPesquisaProtocoloVO>();
		lista = pesquisaExamesFacade.buscarProtocoloPorProntuario(prontuario);
		setListaResultadoPesquisaProtocolo(lista);
	}

	public void selecionarPacientePorSolicitacao(Integer solicitacao) {
		List<ResultadoPesquisaProtocoloVO> lista = new ArrayList<ResultadoPesquisaProtocoloVO>();
		lista = pesquisaExamesFacade.buscarProtocoloPorSolicitacao(solicitacao);
		setListaResultadoPesquisaProtocolo(lista);
	}
	
	public String gerarNovoProtocolo() throws ApplicationBusinessException {
		String opcao = null;
		if (this.numeroProtocolo != null) {
			opcao = EXAMES_GERAR_PROTOCOLO;
		} else {
			this.apresentarMsgNegocio(Severity.ERROR,"NENHUM_ITEM_SELECIONADO");
		}
		return opcao;
	}
	
	public boolean isPacienteEmEdicao(Integer codigoPaciente, Integer codigoConsulta){
		return this.codigoPaciente != null && codigoPaciente != null && this.codigoPaciente.equals(codigoPaciente);
	}

	public List<PesquisaExamesPacientesVO> getListaPacientes() {
		return listaPacientes;
	}

	public void setListaPacientes(List<PesquisaExamesPacientesVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}

	public PesquisaExamesPacientesVO getPacienteSelecionado() {
		return pacienteSelecionado;
	}

	public void setPacienteSelecionado(PesquisaExamesPacientesVO pacienteSelecionado) {
		this.pacienteSelecionado = pacienteSelecionado;
	}

	public List<PesquisaExamesPacientesResultsVO> getListaResultadoPesquisa() {
		return listaResultadoPesquisa;
	}

	public void setListaResultadoPesquisa(
			List<PesquisaExamesPacientesResultsVO> listaResultadoPesquisa) {
		this.listaResultadoPesquisa = listaResultadoPesquisa;
	}

	public PesquisaExamesFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaExamesFiltroVO filtro) {
		this.filtro = filtro;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getCodigoConsulta() {
		return codigoConsulta;
	}

	public void setCodigoConsulta(Integer codigoConsulta) {
		this.codigoConsulta = codigoConsulta;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Comparator<PesquisaExamesPacientesResultsVO> getCurrentComparator() {
		return currentComparator;
	}

	public void setCurrentComparator(
			Comparator<PesquisaExamesPacientesResultsVO> currentComparator) {
		this.currentComparator = currentComparator;
	}

	public List<ResultadoPesquisaProtocoloVO> getListaResultadoPesquisaProtocolo() {
		return listaResultadoPesquisaProtocolo;
	}

	public void setListaResultadoPesquisaProtocolo(
			List<ResultadoPesquisaProtocoloVO> listaResultadoPesquisaProtocolo) {
		this.listaResultadoPesquisaProtocolo = listaResultadoPesquisaProtocolo;
	}

	public List<ItensProtocoloVO> getListaItensProtocolo() {
		return listaItensProtocolo;
	}

	public void setListaItensProtocolo(List<ItensProtocoloVO> listaItensProtocolo) {
		this.listaItensProtocolo = listaItensProtocolo;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Long getNumeroProtocolo() {
		return numeroProtocolo;
	}

	public void setNumeroProtocolo(Long numeroProtocolo) {
		this.numeroProtocolo = numeroProtocolo;
	}

	public ResultadoPesquisaProtocoloVO getResultadoPesquisaProtocoloVO() {
		return resultadoPesquisaProtocoloVO;
	}

	public void setResultadoPesquisaProtocoloVO(
			ResultadoPesquisaProtocoloVO resultadoPesquisaProtocoloVO) {
		this.resultadoPesquisaProtocoloVO = resultadoPesquisaProtocoloVO;
	}



	
	
	

}
