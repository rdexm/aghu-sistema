package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;


import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.TipoComposicaoComponenteVMpmDosagemVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class FormulaNPTCrudController extends ActionController{
	
	
	private AfaFormulaNptPadrao afaFormulaNptPadrao = new AfaFormulaNptPadrao();
	private AfaTipoVelocAdministracoes afaTipoVelocAdministracoes;
	private TipoComposicaoComponenteVMpmDosagemVO tipoComposicaoComponenteVMpmDosagemVO;
	private VMpmDosagem vMpmDosagem;
	
	/**
	 * Temporarrio para testes do suggestionBoxes da tela principal
	 */
	private AfaTipoComposicoes afaTipoComposicoes = new AfaTipoComposicoes();
	private short seqAfaTipoComposicoes;//colocar um valor para testar
	
	public short getSeqAfaTipoComposicoes() {
		return seqAfaTipoComposicoes;
	}
	public void setSeqAfaTipoComposicoes(short seqAfaTipoComposicoes) {
		this.seqAfaTipoComposicoes = seqAfaTipoComposicoes;
	}
	
	public AfaTipoComposicoes getAfaTipoComposicoes() {
		return afaTipoComposicoes;
	}
	public void setAfaTipoComposicoes(AfaTipoComposicoes afaTipoComposicoes) {
		this.afaTipoComposicoes = afaTipoComposicoes;
	}
	/**
	 * fim 
	 */
	
	private RapServidores rapServidores = new RapServidores();
	private Boolean indSituacao;
	private Boolean indPediatria;
	private Boolean indPadrao;
	
	
	private Short seq;
	
	//utlizar para testar
//	private Short seq = 2;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@EJB
	private ICascaFacade cascaFacade;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	
	private static final String PESQUISAR_FORMULA_NTP = "formulaNPTList";
	
	private static final long serialVersionUID = 7306150627300243244L;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	public void iniciar(){
		if(seq!=null){
			afaFormulaNptPadrao = prescricaoMedicaFacade.obterFormulaNptPadraoPorPk(seq);
			setValoresBoolean();			
		}
		setRapServidores(servidorLogadoFacade.obterServidorLogado());
	}
	
	public void gravarSalvar(){
		if(afaFormulaNptPadrao.getSeq()==null){
			ajustaFormula();
			prescricaoMedicaFacade.persistirFormulaNptPadrao(afaFormulaNptPadrao,rapServidores);
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_INCLUIR_FORMULA");
		}
		else{
			ajustaFormula();
			prescricaoMedicaFacade.atualizarFormulaNptPadrao(afaFormulaNptPadrao,rapServidores);
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_ALTERAR_FORMULA");
		}
		seq=null;
		afaFormulaNptPadrao = new AfaFormulaNptPadrao();
		indSituacao = null;
		indPediatria = null;
		indPadrao = null;
	}
	
	public String voltar(){
		seq=null;
		afaFormulaNptPadrao = new AfaFormulaNptPadrao();
		indSituacao = null;
		indPediatria = null;
		indPadrao = null;
		return PESQUISAR_FORMULA_NTP;
	}
	
	public AfaFormulaNptPadrao getAfaFormulaNptPadrao(){
		return this.afaFormulaNptPadrao;
	}
	
	public AfaFormulaNptPadrao setAfaFormulaNptPadrao(){
		return this.afaFormulaNptPadrao;
	}
	
	public boolean verificaPermissao(){
		String loginUsuario = obterLoginUsuarioLogado();
		return cascaFacade.usuarioTemPermissao(loginUsuario, "manterCadastrosNPT");
	}
	public RapServidores getRapServidores() {
		return rapServidores;
	}
	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}
	
	private void ajustaFormula(){
		afaFormulaNptPadrao.setIndFormulaPediatrica(ajustaSimNao(indPediatria));
		afaFormulaNptPadrao.setIndSituacao(ajustaIndSituacao(indSituacao));
		afaFormulaNptPadrao.setIndPadrao(ajustaSimNao(indPadrao));
	}
	
	private String ajustaIndSituacao(Boolean valor){
		if(valor != null && valor.equals(Boolean.TRUE)){
			return "A";
		}else if(valor != null && valor.equals(Boolean.FALSE)){
			return "I";
		}
		return null;
	}
	private String ajustaSimNao(Boolean valor){
		if(valor != null && valor.equals(Boolean.TRUE)){
			return "S";
		}else if(valor != null && valor.equals(Boolean.FALSE)){
			return "N";
		}
		return null;
	}
	
	private void setValoresBoolean() {
		if(afaFormulaNptPadrao.getIndFormulaPediatrica()!=null && afaFormulaNptPadrao.getIndFormulaPediatrica().equals("S")){
			indPediatria = Boolean.TRUE; 
		}
		else if(afaFormulaNptPadrao.getIndFormulaPediatrica()!=null && afaFormulaNptPadrao.getIndFormulaPediatrica().equals("N")){
			indPediatria = Boolean.FALSE;
		}
		if(afaFormulaNptPadrao.getIndSituacao()!=null && afaFormulaNptPadrao.getIndSituacao().equals("A")){
			indSituacao = Boolean.TRUE;
		}
		else if(afaFormulaNptPadrao.getIndSituacao()!=null && afaFormulaNptPadrao.getIndSituacao().equals("I")){
			indSituacao = Boolean.FALSE;
		}
		if(afaFormulaNptPadrao.getIndPadrao()!=null && afaFormulaNptPadrao.getIndPadrao().equals("S")){
			indPadrao = Boolean.TRUE;
		}else if(afaFormulaNptPadrao.getIndPadrao()!=null && afaFormulaNptPadrao.getIndPadrao().equals("N")){
			indPadrao = Boolean.FALSE;
		}
		
	}
	
	public Short getSeq() {
		return seq;
	}
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	public Boolean getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}
	public Boolean getIndPediatria() {
		return indPediatria;
	}
	public void setIndPediatria(Boolean indPediatria) {
		this.indPediatria = indPediatria;
	}
	public Boolean getIndPadrao() {
		return indPadrao;
	}
	public void setIndPadrao(Boolean indPadrao) {
		this.indPadrao = indPadrao;
	}
	public AfaTipoVelocAdministracoes getAfaTipoVelocAdministracoes() {
		return afaTipoVelocAdministracoes;
	}
	public void setAfaTipoVelocAdministracoes(
			AfaTipoVelocAdministracoes afaTipoVelocAdministracoes) {
		this.afaTipoVelocAdministracoes = afaTipoVelocAdministracoes;
	}
	public TipoComposicaoComponenteVMpmDosagemVO getComposicaoComponenteVMpmDosagemVO() {
		return tipoComposicaoComponenteVMpmDosagemVO;
	}
	public void setTipoComposicaoComponenteVMpmDosagemVO(
			TipoComposicaoComponenteVMpmDosagemVO tipoComposicaoComponenteVMpmDosagemVO) {
		this.tipoComposicaoComponenteVMpmDosagemVO = tipoComposicaoComponenteVMpmDosagemVO;
	}
	public VMpmDosagem getvMpmDosagem() {
		return vMpmDosagem;
	}
	public void setvMpmDosagem(VMpmDosagem vMpmDosagem) {
		this.vMpmDosagem = vMpmDosagem;
	}
	

}
