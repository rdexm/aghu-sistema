package br.gov.mec.aghu.exames.protocoloentrega.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.action.PesquisaExameController;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.model.AelProtocoloEntregaExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class ProtocoloEntregaExamesController extends ActionController {
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject
	private PesquisaExameController pesquisaExameController;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@Inject
	private RelatorioProtocoloEntregaExamesController relatorioProtocoloEntregaExamesController;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private static final long serialVersionUID = 6383483995962879996L;

	private Map<Integer, Vector<Short>> solicitacoesExames = new HashMap<Integer, Vector<Short>>();
	private AelProtocoloEntregaExames entregaExames = new AelProtocoloEntregaExames();
	private List<PesquisaExamesPacientesVO> listaPacientes = new ArrayList<PesquisaExamesPacientesVO>();
	private List<PesquisaExamesPacientesResultsVO> listaResultado = new ArrayList<PesquisaExamesPacientesResultsVO>();
	private RapServidores servidorLogado = new RapServidores();
	private String servidor;
	private Long numeroNovoProtocolo;
	private Boolean isPesquisa = Boolean.FALSE;
	private Boolean exibirModalNumeroProtocolo = Boolean.FALSE;
	
	
	private static final String VOLTAR_INCLUIR_PROTOCOLO = "exames-voltarPesquisa";
	private static final String VOLTAR_PESQUISA_PROTOCOLO = "exames-voltarPesquisa-protocolo";
	private static final String PAGINA_IMPRIMIR_PROTOCOLO = "exames-protocoloEntregaExames";
	
	public void inicio() throws ApplicationBusinessException {
		
		limparCampos();
		servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
		setServidor(servidorLogado.getServidor().getUsuario());
		
		if (this.numeroNovoProtocolo != null ) {
			this.isPesquisa = Boolean.TRUE;
		}	
	}

	public void gravar() throws ApplicationBusinessException {
		
		if(validaCPF()) {
			if(itensValidados()) {
				if (this.numeroNovoProtocolo == null ) {
					this.entregaExames.setServidor(servidorLogado);
					cadastrosApoioExamesFacade.persistirProtocolo(this.solicitacoesExames, entregaExames);
					visualizarRelatorio(this.solicitacoesExames, entregaExames, this.listaPacientes);
				} else {
					prepararDadosNovoProtocolo();
					cadastrosApoioExamesFacade.persistirNovoProtocolo(entregaExames);
					visualizarRelatorioPesquisa(entregaExames, this.listaPacientes);
				}
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_PERSISTIR");
			}
			this.pesquisaExameController.limpar();
		}
	}

	public Boolean validaCPF() {
		Boolean retorno = Boolean.TRUE;
		if(this.entregaExames.getCpf() != null){
			if (!CoreUtil.validarCPF(this.entregaExames.getCpf().toString())) {
				apresentarMsgNegocio(Severity.ERROR, "ERRO_CPF_INVALIDO");
				this.entregaExames.setCpf(null);
				retorno = Boolean.FALSE;
			}
		}
		return retorno;
	}
	
	public void limparCampos() {
		this.entregaExames = new AelProtocoloEntregaExames();
		setServidor(null);
	}
	
	public String cancelar() {
		
	String voltar;
		if (this.numeroNovoProtocolo == null ) {
			this.pesquisaExameController.limpar();
			voltar = VOLTAR_INCLUIR_PROTOCOLO;
		} else {
			voltar = VOLTAR_PESQUISA_PROTOCOLO;
		}
		return voltar;
	}

	private	boolean	itensValidados() {
		Boolean validados = Boolean.FALSE;
		if (this.numeroNovoProtocolo == null) {
			validados = this.solicitacoesExames != null && !this.solicitacoesExames.isEmpty() && this.solicitacoesExames.size() > 0 && entregaExames != null && entregaExames.getNomeResponsavelRetirada() != null 
				&& entregaExames.getGrauParentesco() != null;
		} else {
			validados = entregaExames.getNomeResponsavelRetirada() != null && entregaExames.getGrauParentesco() != null;
		}
		return validados;
	}
	
	public void visualizarRelatorio(Map<Integer, Vector<Short>> listaItens, AelProtocoloEntregaExames protocolo, List<PesquisaExamesPacientesVO> listaPacientes) {
		
		relatorioProtocoloEntregaExamesController.setListaItens(listaItens);
		relatorioProtocoloEntregaExamesController.setProtocolo(protocolo);
		relatorioProtocoloEntregaExamesController.setListaPacientes(listaPacientes); 
		relatorioProtocoloEntregaExamesController.setIsPesquisa(false);
		exibirModalNumeroProtocolo = true;
		openDialog("modalNumeroProtocoloPanelWG");
		
	}
	
	public void visualizarRelatorioPesquisa(AelProtocoloEntregaExames protocolo, List<PesquisaExamesPacientesVO> listaPacientes) {
		
		relatorioProtocoloEntregaExamesController.setProtocolo(protocolo);
		relatorioProtocoloEntregaExamesController.setListaPacientes(listaPacientes); 
		relatorioProtocoloEntregaExamesController.setIsPesquisa(true);
		exibirModalNumeroProtocolo = true;
		openDialog("modalNumeroProtocoloPanelWG");
		
	}
	
	private void prepararDadosNovoProtocolo() throws ApplicationBusinessException {
		AelProtocoloEntregaExames protocoloEntrega = new AelProtocoloEntregaExames();
		protocoloEntrega = pesquisaExamesFacade.recuperarNovoProtocolo(this.numeroNovoProtocolo);
		
		this.entregaExames.setSeq(protocoloEntrega.getSeq());
		this.entregaExames.setItemEntregaExames(protocoloEntrega.getItemEntregaExames());
		this.entregaExames.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date()));
		this.entregaExames.setCriadoEm(new Date());
		
	}
	
	public String redirecionarAposExibirNumeroProtocolo() {
		return PAGINA_IMPRIMIR_PROTOCOLO;
	}

	public AelProtocoloEntregaExames getEntregaExames() {
		return entregaExames;
	}

	public void setEntregaExames(AelProtocoloEntregaExames entregaExames) {
		this.entregaExames = entregaExames;
	}

	public Map<Integer, Vector<Short>> getSolicitacoesExames() {
		return solicitacoesExames;
	}

	public void setSolicitacoesExames(Map<Integer, Vector<Short>> solicitacoesExames) {
		this.solicitacoesExames = solicitacoesExames;
	}

	public List<PesquisaExamesPacientesVO> getListaPacientes() {
		return listaPacientes;
	}

	public void setListaPacientes(List<PesquisaExamesPacientesVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}

	public String getServidor() {
		return servidor;
	}

	public void setServidor(String servidor) {
		this.servidor = servidor;
	}

	public List<PesquisaExamesPacientesResultsVO> getListaResultado() {
		return listaResultado;
	}

	public void setListaResultado(
			List<PesquisaExamesPacientesResultsVO> listaResultado) {
		this.listaResultado = listaResultado;
	}

	public Long getNumeroNovoProtocolo() {
		return numeroNovoProtocolo;
	}

	public void setNumeroNovoProtocolo(Long numeroNovoProtocolo) {
		this.numeroNovoProtocolo = numeroNovoProtocolo;
	}

	public Boolean getIsPesquisa() {
		return isPesquisa;
	}

	public void setIsPesquisa(Boolean isPesquisa) {
		this.isPesquisa = isPesquisa;
	}

	public Boolean getExibirModalNumeroProtocolo() {
		return exibirModalNumeroProtocolo;
	}

	public void setExibirModalNumeroProtocolo(Boolean exibirModalNumeroProtocolo) {
		this.exibirModalNumeroProtocolo = exibirModalNumeroProtocolo;
	}

}
