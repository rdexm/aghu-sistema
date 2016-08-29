package br.gov.mec.aghu.exames.pesquisa.action;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;



public class PesquisaExamesPorPacienteController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4170149834179271104L;

	/**
	 * Comparator null safe e locale-sensitive String.
	 */
	@SuppressWarnings("unchecked")
	private static final Comparator PT_BR_COMPARATOR = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {

			final Collator collator = Collator.getInstance(new Locale("pt",
					"BR"));
			collator.setStrength(Collator.PRIMARY);

			return ((Comparable) o1).compareTo(o2);
		}
	};
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	private List<PesquisaExamesPacientesVO> listaPacientes;
	private PesquisaExamesPacientesVO pacienteSelecionado;
	private List<PesquisaExamesPacientesResultsVO> listaResultadoPesquisa;
	private PesquisaExamesFiltroVO filtro;

	private Integer codigoPaciente;
	private Integer codigoConsulta;
	private Integer prontuario;
	
	private Comparator<PesquisaExamesPacientesResultsVO> currentComparator;
	private String currentSortProperty;
	
	private Boolean isHist = Boolean.FALSE;

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
	
	public void selecionarPaciente()throws ApplicationBusinessException, ApplicationBusinessException{
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
	
	public void selecionarPacienteEntregaExames(Integer codigoPaciente, Integer codigoConsulta, Integer prontuario) throws ApplicationBusinessException {

		setCodigoPaciente(codigoPaciente);
		setCodigoConsulta(codigoConsulta);
		setProntuario(prontuario);

		List<PesquisaExamesPacientesResultsVO> lista = new ArrayList<PesquisaExamesPacientesResultsVO>();
		buscarExamesSolicitados(codigoPaciente, codigoConsulta, lista);

		if (this.currentComparator != null) {
			Collections.sort(this.listaResultadoPesquisa, this.currentComparator);
		}
	}

	private void buscarExamesSolicitados(Integer codigoPaciente, Integer codigoConsulta, List<PesquisaExamesPacientesResultsVO> lista) throws ApplicationBusinessException	 {

		if(isHist) {
			AipPacientes pac = pacienteFacade.buscaPaciente(codigoPaciente);
			 for(PesquisaExamesPacientesResultsVO pesquisa : this.pesquisaExamesFacade.buscaExamesSolicitadosPorPacienteHist(pac)) {
				 if(pesquisa.getSituacaoItem().equalsIgnoreCase("LIBERADO")) {
					 lista.add(pesquisa);
				 }
			 }
			setListaResultadoPesquisa(lista);
		}else {
			 for(PesquisaExamesPacientesResultsVO pesquisa : this.pesquisaExamesFacade.buscaExamesSolicitadosPorPaciente(codigoPaciente, codigoConsulta, filtro)) {
				 if(pesquisa.getSituacaoItem().equalsIgnoreCase("LIBERADO") ) {
					 lista.add(pesquisa);
				 }
			 }
			setListaResultadoPesquisa(lista);
		}

	}
	
	@SuppressWarnings("unchecked")
	public void ordenar(String propriedade) {

		Comparator comparator = null;

		// se mesma propriedade, reverte a ordem
		if (this.currentComparator != null
				&& this.currentSortProperty.equals(propriedade)) {
			comparator = new ReverseComparator(this.currentComparator);
		} else {
			// cria novo comparator para a propriedade escolhida
			comparator = new BeanComparator(propriedade, new NullComparator(
					PT_BR_COMPARATOR, false));
		}

		Collections.sort(this.listaResultadoPesquisa, comparator);

		// guarda ordenação corrente
		this.currentComparator = comparator;
		this.currentSortProperty = propriedade;
	}
	
	public String obterAmostras(PesquisaExamesPacientesResultsVO entidade){
		return this.examesFacade.buscaAmoSeqParaUmaSolicitacao(entidade.getCodigoSoe(), entidade.getIseSeq(), entidade.getCodigoSoe());
	}

	public boolean isPacienteEmEdicao(Integer codigoPaciente, Integer codigoConsulta){
		return this.codigoPaciente != null && codigoPaciente != null && this.codigoPaciente.equals(codigoPaciente);
	}
	/** getters e setters **/
	public List<PesquisaExamesPacientesVO> getListaPacientes() {
		return listaPacientes;
	}

	public void setListaPacientes(List<PesquisaExamesPacientesVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	/**
	 * Serve para informar qual paciente e quais informações pegar
	 * @param codigoPaciente
	 */
	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getCodigoConsulta() {
		return codigoConsulta;
	}

	/**
	 * Serve para informar qual paciente e quais informações pegar
	 * @param codigoConsulta
	 */
	public void setCodigoConsulta(Integer codigoConsulta) {
		this.codigoConsulta = codigoConsulta;
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

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Boolean getIsHist() {
		return isHist;
	}

	public void setIsHist(Boolean isHist) {
		this.isHist = isHist;
	}


	public PesquisaExamesPacientesVO getPacienteSelecionado() {
		return pacienteSelecionado;
	}


	public void setPacienteSelecionado(PesquisaExamesPacientesVO pacienteSelecionado) {
		this.pacienteSelecionado = pacienteSelecionado;
	}

}