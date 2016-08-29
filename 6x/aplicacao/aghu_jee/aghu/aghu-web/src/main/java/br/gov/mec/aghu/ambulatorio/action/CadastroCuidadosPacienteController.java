package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.MamRecCuidPreferidoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamItemReceitCuidado;
import br.gov.mec.aghu.model.MamReceituarioCuidado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VMamDiferCuidServidores;
import br.gov.mec.aghu.model.VMamPessoaServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

public class CadastroCuidadosPacienteController extends ActionController {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1143563130059520243L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private List<MamItemReceitCuidado> list;
	
	private AacConsultas consultaSelecionada;

	private MamReceituarioCuidado receituarioAnterior;
	
	private MamReceituarioCuidado receituarioAtual = new MamReceituarioCuidado();
	
	private MamItemReceitCuidado itemReceitCuidado = new MamItemReceitCuidado();
	
	private MamItemReceitCuidado itemReceitCuidadoSelecionado = new MamItemReceitCuidado();//utilizado na edicao e exclusao
	
	private MamItemReceitCuidado itemNovo = new MamItemReceitCuidado();
	
	private boolean frist = true;
	
	private boolean emEdicao = false;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private RapServidores servidorlogado;
	
	private VMamDiferCuidServidores servidorOrigem;//sbb
	

	private List<MamRecCuidPreferidoVO> listaCuidadosFavoritos;
	
	private String copiarPara;
	
	private VMamDiferCuidServidores usuarioCopiarPara;
	
	private VMamPessoaServidores usuarioCopiarDe;
	
	@PostConstruct
	protected void inicializar() {		
		this.begin(conversation);
	}
	
	public String voltar(){
		return null;
	}
	
	public void iniciar() {
		receituarioAnterior = ambulatorioFacade.mamReceituarioCuidadoPorNumeroConsulta(consultaSelecionada.getNumero(), DominioIndPendenteAmbulatorio.V);
				ambulatorioFacade.verificaRequisitosReceituarioCuidado(consultaSelecionada);
				setarParamentrosUsuarioOrigem();
			receituarioAtual = ambulatorioFacade.obterUltimoMamReceituarioCuidadoPorNumeroConsulta(consultaSelecionada.getNumero());
			if(receituarioAtual==null){
				receituarioAtual= new MamReceituarioCuidado();
				receituarioAtual.setNroVias(Byte.valueOf("1"));
			}
			pesquisar();
	}

	private void setarParamentrosUsuarioOrigem() {
		servidorlogado=servidorLogadoFacade.obterServidorLogado();
		usuarioCopiarDe =ambulatorioFacade.obterVMamPessoaServidores(servidorlogado);
		if(usuarioCopiarDe != null){
			copiarPara = usuarioCopiarDe.getMatricula()+" - "+usuarioCopiarDe.getVinCodigo()+" - "+usuarioCopiarDe.getPesNome();
		}
	}
	
	public void pesquisar(){
		list = ambulatorioFacade.listarMamItensReceituarioCuidadoPorNumeroConsulta(consultaSelecionada.getNumero()); 
	}
	
	public void adicionar(){
		ambulatorioFacade.adicionarEditarMamItemReceitCuidado(receituarioAtual, receituarioAnterior, itemNovo,consultaSelecionada);
		itemNovo = new MamItemReceitCuidado();
		itemReceitCuidadoSelecionado = new MamItemReceitCuidado();
		receituarioAtual = ambulatorioFacade.obterUltimoMamReceituarioCuidadoPorNumeroConsulta(consultaSelecionada.getNumero());
		pesquisar();
	}
	
	public boolean emEdicao(MamItemReceitCuidado item){
		if(item.getId().equals(itemNovo.getId())){
			return true;
		}
		return false;
	}
	
	public void checkItem(int indice){
		
		if(listaCuidadosFavoritos.get(indice).isSelecionado()){
			listaCuidadosFavoritos.get(indice).setSelecionado(false);
		}
		else{
			listaCuidadosFavoritos.get(indice).setSelecionado(true);
		}
	}
	
	public void setarItemEdicao(MamItemReceitCuidado item){
		setItemNovo(item);
		setItemReceitCuidadoSelecionado(item);
		emEdicao=true;
	}
	
	public void setarItemEdicao(){
		setItemNovo(itemReceitCuidadoSelecionado);
		emEdicao=true;
	}
	
	public MamItemReceitCuidado getItemNovo() {
		return itemNovo;
	}

	public void setItemNovo(MamItemReceitCuidado itemNovo) {
		this.itemNovo = itemNovo;
	}

	public void limpar(){
		emEdicao=false;
		itemNovo=new MamItemReceitCuidado();
		itemReceitCuidadoSelecionado = new MamItemReceitCuidado();
	}
	
	
	
	public void editar(){
		ambulatorioFacade.adicionarEditarMamItemReceitCuidado(receituarioAtual, receituarioAnterior, itemNovo,consultaSelecionada);
		itemReceitCuidadoSelecionado = new MamItemReceitCuidado();
		itemNovo = new MamItemReceitCuidado();
		pesquisar();
		emEdicao=false;
	}
	
	public void cancelarEdicao(){
		emEdicao=false;
		itemNovo=new MamItemReceitCuidado();
		itemReceitCuidadoSelecionado = new MamItemReceitCuidado();
	}
	
	public void excluir(MamItemReceitCuidado itemReceitCuidado){
		ambulatorioFacade.excluirmamItemReceitCuidado(itemReceitCuidado);
		pesquisar();
	}
	
	
	public void carregarCuidadosPreferidos(){
		listaCuidadosFavoritos = ambulatorioFacade.listarCuidadosPreferidos(servidorlogado, false);
	}
	
	public void copiarDosCuidadosPreferidos(){
		ambulatorioFacade.selecionarCuidadosEntrePreferidosUsuario(listaCuidadosFavoritos, receituarioAtual, consultaSelecionada);
		pesquisar();
	}
	
	public void copiarCuidadosFavoridosOutroUsuario(){
		ambulatorioFacade.copiaCuidadoPreferidosOutroUsuario(servidorOrigem,servidorlogado);
		listaCuidadosFavoritos = ambulatorioFacade.listarCuidadosPreferidos(servidorlogado, false);//116811
		servidorOrigem = null;
	}
	
	public void cancelarCopia(){
		servidorOrigem = null;
	}
	
	public boolean desabilitaBotaoOk(){
		if(servidorOrigem==null){
			return true;
		}
		return false;
	}
	
	/**
	 * suggestion 52356
	 * @param pesquisa
	 * @return
	 */
	public List<VMamDiferCuidServidores> pesquisarVMamDiferCuidServidores(String pesquisa) {
		return this.returnSGWithCount(this.ambulatorioFacade.pesquisarVMamDiferCuidServidores(servidorlogado,pesquisa),this.ambulatorioFacade.countVMamDiferCuidServidores(servidorlogado,pesquisa));
	}

	public List<MamItemReceitCuidado> getList() {
		return list;
	}

	public void setList(List<MamItemReceitCuidado> list) {
		this.list = list;
	}

	public MamItemReceitCuidado getItemReceitCuidado() {
		return itemReceitCuidado;
	}

	public void setItemReceitCuidado(MamItemReceitCuidado itemReceitCuidado) {
		this.itemReceitCuidado = itemReceitCuidado;
	}

	public MamReceituarioCuidado getReceituarioAnterior() {
		return receituarioAnterior;
	}

	public void setReceituarioAnterior(MamReceituarioCuidado receituarioAnterior) {
		this.receituarioAnterior = receituarioAnterior;
	}

	public MamReceituarioCuidado getReceituarioAtual() {
		return receituarioAtual;
	}

	public void setReceituarioAtual(MamReceituarioCuidado receituarioAtual) {
		this.receituarioAtual = receituarioAtual;
	}

	public boolean isFrist() {
		return frist;
	}

	public void setFrist(boolean frist) {
		this.frist = frist;
	}

	public MamItemReceitCuidado getItemReceitCuidadoSelecionado() {
		return itemReceitCuidadoSelecionado;
	}

	public void setItemReceitCuidadoSelecionado(
			MamItemReceitCuidado itemReceitCuidadoSelecionado) {
		this.itemReceitCuidadoSelecionado = itemReceitCuidadoSelecionado;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public List<MamRecCuidPreferidoVO> getListaCuidadosFavoritos() {
		return listaCuidadosFavoritos;
	}

	public void setListaCuidadosFavoritos(List<MamRecCuidPreferidoVO> listaCuidadosFavoritos) {
		this.listaCuidadosFavoritos = listaCuidadosFavoritos;
	}

	public String getCopiarPara() {
		return copiarPara;
	}

	public void setCopiarPara(String copiarPara) {
		this.copiarPara = copiarPara;
	}

	public VMamDiferCuidServidores getUsuarioCopiarPara() {
		return usuarioCopiarPara;
	}

	public void setUsuarioCopiarPara(VMamDiferCuidServidores usuarioCopiarPara) {
		this.usuarioCopiarPara = usuarioCopiarPara;
	}

	public VMamPessoaServidores getUsuarioCopiarDe() {
		return usuarioCopiarDe;
	}

	public void setUsuarioCopiarDe(VMamPessoaServidores usuarioCopiarDe) {
		this.usuarioCopiarDe = usuarioCopiarDe;
	}

	
	public VMamDiferCuidServidores getServidorOrigem() {
		return servidorOrigem;
	}

	public void setServidorOrigem(VMamDiferCuidServidores servidorOrigem) {
		this.servidorOrigem = servidorOrigem;
	}	
	
}