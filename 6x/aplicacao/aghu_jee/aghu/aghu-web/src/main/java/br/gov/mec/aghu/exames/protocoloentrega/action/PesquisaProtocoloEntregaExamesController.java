package br.gov.mec.aghu.exames.protocoloentrega.action;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.PesquisaProtocoloExceptionCode;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;

public class PesquisaProtocoloEntregaExamesController extends ActionController {
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	private static final long serialVersionUID = 3230649794413207115L;
	
	@EJB 
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private PesquisaProtocoloExamesController pesquisaProtocoloExamesController;
	
	private PesquisaExamesFiltroVO filtro = new PesquisaExamesFiltroVO();
	private List<PesquisaExamesPacientesResultsVO> listaResultadoPesquisa;
	private Comparator<PesquisaExamesPacientesResultsVO> currentComparator;
	private PesquisaExamesPacientesVO pacienteSelecionado;
	private Boolean isHist = Boolean.FALSE;
	private boolean filtroAberto = true;
	private String voltarPara;
	private AipPacientes paciente;
	private Integer codigoPaciente;
	private Integer codigoConsulta;
	private Integer prontuario;
	private Long protocolo;
	private Integer pacCodigoFonetica;
	private TipoPesquisa tipoPesquisa = null;
	private boolean exibirBotaoVoltar = false;
	private boolean voltaPesquisa;
	
	private List<PesquisaExamesPacientesVO> listaPacientes = new ArrayList<PesquisaExamesPacientesVO>();
	
	private static final String PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";
	private static final String VOLTAR_PARA_PESQUISA_PROTOCOLO_ENTREGA_EXAMES = "exames-pesquisaProtocoloEntregaExames";

	public enum TipoPesquisa { 
		PACIENTE, SOLICITANTE; 
	}
	
	public void inicio() throws ApplicationBusinessException {
		
		if (voltaPesquisa) {
			pacCodigoFonetica = null;
		}
		
		if (getProntuario() != null) {
			paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
			filtro.setProntuarioPac(paciente.getProntuario());
			filtro.setNomePacientePac(paciente.getNome());	
		}
		if (pacCodigoFonetica != null){
			AipPacientes pac = pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			paciente = pac;
			filtro.setProntuarioPac(pac.getProntuario());
			filtro.setNomePacientePac(pac.getNome());
			return;
		}
		if (StringUtils.isNotBlank(this.voltarPara) && !this.voltarPara.equals(VOLTAR_PARA_PESQUISA_PROTOCOLO_ENTREGA_EXAMES)) {
			exibirBotaoVoltar = true;
		} else {
			setVoltarPara(VOLTAR_PARA_PESQUISA_PROTOCOLO_ENTREGA_EXAMES);
		}
		
		limparSelecao();
	}
	
	private void limparSelecao() {
		setFiltro(new PesquisaExamesFiltroVO());
		tipoPesquisa = null;
		paciente=null;
		pacCodigoFonetica = null;
		prontuario=null;
		protocolo=null;
		setVoltaPesquisa(false);
		setFiltroAberto(true);
		this.pesquisaProtocoloExamesController.setListaItensProtocolo(null);
        this.pesquisaProtocoloExamesController.setListaPacientes(null);
        this.pesquisaProtocoloExamesController.setCodigoConsulta(null);
        this.pesquisaProtocoloExamesController.setCodigoPaciente(null);
        this.pesquisaProtocoloExamesController.setFiltro(getFiltro());
        this.pesquisaProtocoloExamesController.setListaResultadoPesquisa(null);
		
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean isPacienteEmEdicao(Integer codigoPaciente, Integer codigoConsulta){
		return this.codigoPaciente != null && codigoPaciente != null && this.codigoPaciente.equals(codigoPaciente);
	}
	
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String param) {
		return this.returnSGWithCount(this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExamesOrdenadaDescricao(param),pesquisarUnidadeExecutoraCount(param));
	}
	
	public Integer pesquisarUnidadeExecutoraCount(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(param);
	}

	public void pesquisarProtocoloEntregaExames() throws ApplicationBusinessException {
		
		this.aghuFacade.limparEntityManager();
		try {
			atualizarFiltro();
			
			if (verificaExistenciaCriterioPesquisa()) {
				throw new ApplicationBusinessException(PesquisaProtocoloExceptionCode.PREENCHER_CRITERIOS_PARA_SELECAO);
			}
			if (((this.getFiltro().getServidorPac() != null && this.getFiltro().getServidorPac().getId()==null))) {
				throw new ApplicationBusinessException(PesquisaProtocoloExceptionCode.INFORMAR_DADOS_SERVIDOR);
			}
			if ((this.getFiltro().getConselhoSolic() == null && this.getFiltro().getNumeroConselhoSolic() != null) || (this.getFiltro().getConselhoSolic() != null && this.getFiltro().getNumeroConselhoSolic() == null)){
				throw new ApplicationBusinessException(PesquisaProtocoloExceptionCode.INFORMAR_CONSELHO_NUMERO_REGISTRO);
			}
			pesquisarNumeroProtocoloPaciente();
			renderPesquisa();
			} catch (ApplicationBusinessException e) {
				this.getFiltro().setConsultaPac(null);
				tipoPesquisa = null;
				setFiltroAberto(true);
				this.pesquisaProtocoloExamesController.setListaResultadoPesquisa(null);
				this.pesquisaProtocoloExamesController.setListaResultadoPesquisa(null);
				apresentarExcecaoNegocio(e);
			}
	}

	private void pesquisarNumeroProtocoloPaciente() throws ApplicationBusinessException {
		if (this.getFiltro().getNumeroProtocoloEntregaExames() != null) {
			listaPacientes = this.pesquisaExamesFacade.buscarAipPacientesPorNumProtocolo(this.getFiltro().getNumeroProtocoloEntregaExames());
			if (listaPacientes == null || listaPacientes.size()==0) {
				this.getFiltro().setConsultaPac(null);
				this.getFiltro().setProntuarioPac(null);
				this.getFiltro().setNomePacientePac(null);
				this.getFiltro().setNumeroProtocoloEntregaExames(null);
				this.pesquisaProtocoloExamesController.setListaResultadoPesquisa(null);
				throw new ApplicationBusinessException(PesquisaProtocoloExceptionCode.PROTOCOLO_DIGITADO_NAO_EXISTE);
			} else {
				tipoPesquisa = TipoPesquisa.PACIENTE;
				this.getFiltro().setConsultaPac(listaPacientes.get(0).getConsulta());
				this.getFiltro().setProntuarioPac(listaPacientes.get(0).getProntuario());
				this.getFiltro().setNomePacientePac(listaPacientes.get(0).getNomePaciente());

				this.pesquisaProtocoloExamesController.setFiltro(filtro);
				this.pesquisaProtocoloExamesController.selecionarPacientePorProtocolo(this.getFiltro().getNumeroProtocoloEntregaExames());
			}
		} else {
			pesquisarNumeroSolicitacaoPaciente();
		}
	}

	private void pesquisarNumeroSolicitacaoPaciente() throws ApplicationBusinessException {
		if (this.getFiltro().getNumeroSolicitacaoInfo() != null) {
			listaPacientes = this.pesquisaExamesFacade.buscarAipPacientesPorSolicExa(this.getFiltro().getNumeroSolicitacaoInfo());
			if (listaPacientes == null || listaPacientes.size()==0) {
				this.getFiltro().setConsultaPac(null);
				this.getFiltro().setProntuarioPac(null);
				this.getFiltro().setNomePacientePac(null);
				this.getFiltro().setNumeroProtocoloEntregaExames(null);
				this.pesquisaProtocoloExamesController.setListaResultadoPesquisa(null);
				throw new ApplicationBusinessException(PesquisaProtocoloExceptionCode.SOLICITACAO_DIGITADA_NAO_EXISTE);
			} else {
				tipoPesquisa = TipoPesquisa.PACIENTE;
				this.getFiltro().setConsultaPac(listaPacientes.get(0).getConsulta());
				this.getFiltro().setProntuarioPac(listaPacientes.get(0).getProntuario());
				this.getFiltro().setNomePacientePac(listaPacientes.get(0).getNomePaciente());
				this.getFiltro().setNumeroProtocoloEntregaExames(listaPacientes.get(0).getProtocolo());

				this.pesquisaProtocoloExamesController.setFiltro(filtro);
				this.pesquisaProtocoloExamesController.selecionarPacientePorSolicitacao(this.getFiltro().getNumeroSolicitacaoInfo());
			}

		} else {
			pesquisarProntuarioPaciente();
		}
	}

	private void pesquisarProntuarioPaciente() throws ApplicationBusinessException {
		if (this.getFiltro().getProntuarioPac() != null || this.getFiltro().getLeitoPac() != null
			|| this.getFiltro().getAelUnffuncionalPac() != null || (this.getFiltro().getNomePacientePac()!=null && !this.getFiltro().getNomePacientePac().trim().equals(""))) {
			
			listaPacientes = this.pesquisaExamesFacade.buscarAipPacientesPorParametros(this.getFiltro().getProntuarioPac(), this.getFiltro().getNomePacientePac(), this.getFiltro().getLeitoPac(), this.getFiltro().getAelUnffuncionalPac());
			if (listaPacientes == null || listaPacientes.size()==0) {
				throw new ApplicationBusinessException(PesquisaProtocoloExceptionCode.PRONTUARIO_DIGITADO_NAO_EXISTE);
			} else {
				tipoPesquisa = TipoPesquisa.PACIENTE;
				this.getFiltro().setConsultaPac(null);
				this.pesquisaProtocoloExamesController.setFiltro(filtro);
				this.pesquisaProtocoloExamesController.selecionarPacientePorProntuario(listaPacientes.get(0).getProntuario());
			}
		}
	}
	
	private boolean verificaExistenciaCriterioPesquisa(){
		if (this.getFiltro().getNumeroProtocoloEntregaExames() == null && this.getFiltro().getProntuarioPac() == null && this.getFiltro().getLeitoPac() == null&& this.getFiltro().getAelUnffuncionalPac() == null
				&& this.getFiltro().getServidorPac() == null&& this.getFiltro().getConsultaPac() == null&& this.getFiltro().getConselhoSolic() == null
				&& this.getFiltro().getNumeroConselhoSolic() == null&& this.getFiltro().getServidorSolic() == null&& this.getFiltro().getNumeroSolicitacaoInfo() == null
				&& (this.getFiltro().getNumeroAp() == null || this.getFiltro().getConfigExame() == null)&& (this.getFiltro().getNomePacientePac()==null || this.getFiltro().getNomePacientePac().trim().equals(""))	){
			return true;
		} else {
			return false;
		}
	}

	public void limpar() {
		setFiltro(new PesquisaExamesFiltroVO());
		tipoPesquisa = null;
		pacCodigoFonetica = null;
		paciente=null;
		prontuario=null;
		protocolo=null;
		setFiltroAberto(true);
		this.pesquisaProtocoloExamesController.setListaItensProtocolo(null);
        this.pesquisaProtocoloExamesController.setListaPacientes(null);
        this.pesquisaProtocoloExamesController.setCodigoConsulta(null);
        this.pesquisaProtocoloExamesController.setCodigoPaciente(null);
        this.pesquisaProtocoloExamesController.setFiltro(getFiltro());
        this.pesquisaProtocoloExamesController.setListaResultadoPesquisa(null);
		
	}
	
	public String redirecionarPesquisaFonetica(){
		return PESQUISA_PACIENTE_COMPONENTE;
	}
	
    public int controlaAccordion(){
    	int retorno = 0;
    	if(!filtroAberto){
    		retorno = -1;
    	}
        return retorno;
    }
    
    private void atualizarFiltro(){
   
    	buscarPorProtocolo();
    	
        if (this.paciente != null && protocolo == null){
        	buscarPorProntuario();
            buscarPorCodigoPac();
            buscarPorNomePaciente();
           
        } else if (this.paciente == null && protocolo == null) {
            limpaFiltroPaciente();
        }
    }

	private void limpaFiltroPaciente() {
		filtro.setProntuarioPac(null);
		filtro.setCodigoPac(null);
		filtro.setNomePacientePac(null);
		filtro.setNumeroProtocoloEntregaExames(null);
	}

	private void buscarPorProtocolo() {
		if (protocolo != null) {
			filtro.setNumeroProtocoloEntregaExames(protocolo);
		}
	}

	private void buscarPorNomePaciente() {
		if (paciente.getNome() != null && StringUtils.isNotBlank(paciente.getNome())){
		    filtro.setNomePacientePac(paciente.getNome());
		}
	}

	private void buscarPorCodigoPac() {
		if (paciente.getCodigo() != null){
		    filtro.setCodigoPac(paciente.getCodigo());
		}
	}

	private void buscarPorProntuario() {
		if (paciente.getProntuario() != null){
		    filtro.setProntuarioPac(paciente.getProntuario());
		}
	}
    
	private void renderPesquisa() {
		setFiltroAberto(false);
		if (TipoPesquisa.PACIENTE.equals(this.tipoPesquisa)) {
			this.pesquisaProtocoloExamesController.setListaPacientes(this.listaPacientes);
		} 
	}

	public boolean isPesquisaPaciente(){
		return TipoPesquisa.PACIENTE.equals(this.tipoPesquisa);
	}
	
	
	
    public boolean isExibirBotaoVoltar() {
		return exibirBotaoVoltar;
	}

	public void setExibirBotaoVoltar(boolean exibirBotaoVoltar) {
		this.exibirBotaoVoltar = exibirBotaoVoltar;
	}

	public Comparator<PesquisaExamesPacientesResultsVO> getCurrentComparator() {
		return currentComparator;
	}

	public void setCurrentComparator(
			Comparator<PesquisaExamesPacientesResultsVO> currentComparator) {
		this.currentComparator = currentComparator;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public PesquisaExamesFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaExamesFiltroVO filtro) {
		this.filtro = filtro;
	}

	public boolean isFiltroAberto() {
		return filtroAberto;
	}

	public void setFiltroAberto(boolean filtroAberto) {
		this.filtroAberto = filtroAberto;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Long getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(Long protocolo) {
		this.protocolo = protocolo;
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

	public Boolean getIsHist() {
		return isHist;
	}

	public void setIsHist(Boolean isHist) {
		this.isHist = isHist;
	}

	public List<PesquisaExamesPacientesResultsVO> getListaResultadoPesquisa() {
		return listaResultadoPesquisa;
	}

	public void setListaResultadoPesquisa(
			List<PesquisaExamesPacientesResultsVO> listaResultadoPesquisa) {
		this.listaResultadoPesquisa = listaResultadoPesquisa;
	}

	public TipoPesquisa getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(TipoPesquisa tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public boolean isVoltaPesquisa() {
		return voltaPesquisa;
	}

	public void setVoltaPesquisa(boolean voltaPesquisa) {
		this.voltaPesquisa = voltaPesquisa;
	}





	
	

}
