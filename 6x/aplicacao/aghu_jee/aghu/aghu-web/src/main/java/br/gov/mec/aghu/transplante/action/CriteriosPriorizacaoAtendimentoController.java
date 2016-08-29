package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MtxCriterioPriorizacaoTmo;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.CriteriosPriorizacaoAtendVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class CriteriosPriorizacaoAtendimentoController extends ActionController {


	private static final long serialVersionUID = -8194798745495396555L;
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
    @EJB
    protected IRegistroColaboradorFacade registroColaboradorFacade;

    private static final String PAGE_PESQUISA_CID = "internacao-pesquisaCid";
    private static final String CID_PESQUISA = "criteriosPriorizacaoAtendimentoList";
	
	private String voltarPara;
	private boolean emEdicao = false;
	private Boolean situacao;
	private AghCid cid;
	private Integer coeficiente;
	private DominioSituacaoTmo tipoTmo;
	private String status;
	private MtxCriterioPriorizacaoTmo mtxCriterioPriorizacaoTmo = new MtxCriterioPriorizacaoTmo();
	private CriteriosPriorizacaoAtendVO filtro = new CriteriosPriorizacaoAtendVO();
	
	private List<Integer> listaGravidade;
	private List<Integer> listaCriticidade;
	 
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar(){
		preencherListasCombo();
		if(filtro.getSituacao() != null){
			if(emEdicao){
				if(filtro.getSituacao().isAtivo()){
					this.situacao = Boolean.TRUE;
				}else{
					this.situacao = Boolean.FALSE;
				}
			}
		}
		if(filtro.getTipoTmo() != null){
			if(emEdicao){
				this.tipoTmo = filtro.getTipoTmo();
			}
		}
		if(filtro.getStatus() != null){
			if(emEdicao){
				this.status = filtro.getStatus();
			}
		}
		calcularCoeficiente();
	}
	
	public void preencherListasCombo(){
		this.listaGravidade = new ArrayList<Integer>();
		this.listaCriticidade = new ArrayList<Integer>();
		
		listaCriticidade.add(0);
		listaCriticidade.add(5);
		listaCriticidade.add(10);
		listaCriticidade.add(15);
		listaCriticidade.add(20);
		listaCriticidade.add(25);
		listaCriticidade.add(30);
		listaCriticidade.add(35);
		listaCriticidade.add(40);
		listaCriticidade.add(45);
		listaCriticidade.add(50);
		listaCriticidade.add(55);
		listaCriticidade.add(60);
		listaCriticidade.add(65);
		listaCriticidade.add(70);
		listaCriticidade.add(75);
		listaCriticidade.add(80);
		listaCriticidade.add(85);
		listaCriticidade.add(90);
		listaCriticidade.add(95);
		listaCriticidade.add(100);
		
		listaGravidade.addAll(listaCriticidade);
	}

	/**Pesquisa Cid por seq, codigo ou descricao para preencher sbCid**/
	public List<AghCid> obterCid(String pesquisa){		
		return returnSGWithCount(this.transplanteFacade.pesquisarCidPorSeqCodDescricao(pesquisa), this.transplanteFacade.pesquisarCidPorSeqCodDescricaoCount(pesquisa));
	}
	
	/**Realiza a inserção ou update de um registro na tabela**/
	public String gravar() throws ApplicationBusinessException{
		try{
			transplanteFacade.validarCamposObrigatorios(filtro);
			
//			mtxCriterioPriorizacaoTmo.setCidSeq(cid);
			if(situacao){
				mtxCriterioPriorizacaoTmo.setIndSituacao(DominioSituacao.A);
			}else{
				mtxCriterioPriorizacaoTmo.setIndSituacao(DominioSituacao.I);
			}
			if(filtro.getCriticidade() != null){
				mtxCriterioPriorizacaoTmo.setCriticidade(filtro.getCriticidade());
			}
			if(filtro.getGravidade() != null){
				mtxCriterioPriorizacaoTmo.setGravidade(filtro.getGravidade());			
			}
			if(filtro.getTipoTmo() != null){
				mtxCriterioPriorizacaoTmo.setTipoTmo(filtro.getTipoTmo());
			}
			if(filtro.getStatus() != null){
				mtxCriterioPriorizacaoTmo.setStatus(filtro.getStatus());
			}
			
			if(emEdicao){
					mtxCriterioPriorizacaoTmo.setSeq(filtro.getSeq());
					transplanteFacade.atualizarMtxCriterioPriorizacaoTmo(mtxCriterioPriorizacaoTmo);
					apresentarMsgNegocio(Severity.INFO, "CID_ATUALIZADO");
			} else {// PRIMEIRO IF IRA SER RETIRADO 
//				if(transplanteFacade.verificarExistenciaRegistro(cid.getSeq())){
//					apresentarMsgNegocio(Severity.ERROR, "ERRO_JA_EXISTE_PRIOR_CID");
//					return null;
//				}else{
					transplanteFacade.gravarMtxCriterioPriorizacaoTmo(mtxCriterioPriorizacaoTmo);
					apresentarMsgNegocio(Severity.INFO, "CID_CADASTRADO");
//				}
			}

		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return voltar();
	}
	/**Retorna para a página anterior**/
	public String voltar() {
		this.mtxCriterioPriorizacaoTmo = new MtxCriterioPriorizacaoTmo();
		this.emEdicao = false;
		this.voltarPara = null;
		this.cid = null;
		return CID_PESQUISA;
	}
	/**Calcula o coeficiente para exibição na tela**/
	public void calcularCoeficiente(){
		
		if(filtro.getGravidade() != null && filtro.getCriticidade() != null)
		{
			setCoeficiente(filtro.getGravidade() + filtro.getCriticidade());
		}
		else
		{
			if(filtro.getGravidade() != null && filtro.getCriticidade() == null){
				this.setCoeficiente(filtro.getGravidade());
			}
			if(filtro.getCriticidade() != null && filtro.getGravidade() == null){
				this.setCoeficiente(filtro.getCriticidade());
			}
			if(filtro.getCriticidade() == null && filtro.getGravidade() == null){
				this.setCoeficiente(null);
			}
		}
	}
	
	public void obterCidSeq(){
		if(cid == null){
			cid = transplanteFacade.obterCidSeq(filtro.getCidSeq());
		}
	}
	/**Redireciona para a página pesquisaCid.xhtml*/
	public String cidPorCapitulo(){
		return PAGE_PESQUISA_CID;
	}
	
	//Getters e  Setters
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public MtxCriterioPriorizacaoTmo getMtxCriterioPriorizacaoTmo() {
		return mtxCriterioPriorizacaoTmo;
	}

	public void setMtxCriterioPriorizacaoTmo(
			MtxCriterioPriorizacaoTmo mtxCriterioPriorizacaoTmo) {
		this.mtxCriterioPriorizacaoTmo = mtxCriterioPriorizacaoTmo;
	}

	public CriteriosPriorizacaoAtendVO getFiltro() {
		return filtro;
	}

	public void setFiltro(CriteriosPriorizacaoAtendVO filtro) {
		this.filtro = filtro;
	}

	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public Integer getCoeficiente() {
		return coeficiente;
	}

	public void setCoeficiente(Integer coeficiente) {
		this.coeficiente = coeficiente;
	}

	public List<Integer> getListaGravidade() {
		return listaGravidade;
	}

	public void setListaGravidade(List<Integer> listaGravidade) {
		this.listaGravidade = listaGravidade;
	}

	public List<Integer> getListaCriticidade() {
		return listaCriticidade;
	}

	public void setListaCriticidade(List<Integer> listaCriticidade) {
		this.listaCriticidade = listaCriticidade;
	}

	public DominioSituacaoTmo getTipoTmo() {
		return tipoTmo;
	}

	public void setTipoTmo(DominioSituacaoTmo tipoTmo) {
		this.tipoTmo = tipoTmo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
