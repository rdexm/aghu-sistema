package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelGrupoMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoXMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoXMaterialAnaliseId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class GrupoMaterialAnaliseController extends ActionController {

	private static final long serialVersionUID = 1437561463545536532L;

	private static final String GRUPO_MATERIAL_ANALISE_PESQUISA = "grupoMaterialAnalisePesquisa";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IExamesFacade examesFacade;

	private AelGrupoMaterialAnalise grupoMaterialAnalise;
	private AelMateriaisAnalises materialAnalise;
	
	// Flags que determinam comportamento da tela
	private boolean emEdicao;
	private List<AelGrupoXMaterialAnalise> listaGrupoXMaterialAnalise;
	private AelGrupoXMaterialAnalise grupoXMaterialAnaliseExclusao;
	
	private Integer codigo;
	private Integer codigoMaterial;
	private Boolean situacaoGrupo;
	private boolean habilitaBotaoGravar = false;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
		
		if (this.emEdicao) {
			this.grupoMaterialAnalise = this.cadastrosApoioExamesFacade.obterGrupoMaterialAnalisePorSeq(this.codigo); 

			if(grupoMaterialAnalise == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			this.listaGrupoXMaterialAnalise = this.cadastrosApoioExamesFacade.pesquisarGrupoXMaterialAnalisePorGrupo(this.codigo);
			
			if(grupoMaterialAnalise.getIndSituacao().equals(DominioSituacao.A)){
				this.situacaoGrupo = true;
			} else {
				this.situacaoGrupo = false;
			}
			
		} else {
			this.grupoMaterialAnalise = new AelGrupoMaterialAnalise();
			this.situacaoGrupo = true;
		}
		
		return null;
	}
	
	public String confirmar() {
		try{
			
			this.grupoMaterialAnalise = this.cadastrosApoioExamesFacade.validarCamposGrupoMaterialAnalise(grupoMaterialAnalise);
			
			final boolean isInclusao = this.grupoMaterialAnalise.getSeq() == null;
			if(this.getSituacaoGrupo()){
				this.getGrupoMaterialAnalise().setIndSituacao(DominioSituacao.A);
			} else {
				this.getGrupoMaterialAnalise().setIndSituacao(DominioSituacao.I);
			}
			
			if (isInclusao) {
				this.cadastrosApoioExamesFacade.persistirGrupoMaterialAnalise(this.grupoMaterialAnalise);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_GRUPO_MATERIAL_ANALISE");
				
			} else {
				this.cadastrosApoioExamesFacade.atualizarGrupoMaterialAnalise(this.grupoMaterialAnalise);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_GRUPO_MATERIAL_ANALISE");
			}
		
			if(isInclusao){
				this.codigo = this.grupoMaterialAnalise.getSeq();
				this.emEdicao = true;
				this.iniciar();
			}
			
		} catch(BaseException e){
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	
	public void confirmarGrupoXMaterialAnalise(){
		try{
			AelGrupoXMaterialAnalise grupoXMaterialAnalise = new AelGrupoXMaterialAnalise();
			AelGrupoXMaterialAnaliseId id = new AelGrupoXMaterialAnaliseId();
			id.setGmaSeq(this.getGrupoMaterialAnalise().getSeq());
			id.setManSeq(this.getMaterialAnalise().getSeq());
			grupoXMaterialAnalise.setId(id);
			grupoXMaterialAnalise.setGrpMatAnal(this.getGrupoMaterialAnalise());	
			grupoXMaterialAnalise.setMatAnal(this.getMaterialAnalise());
			grupoXMaterialAnalise.setIndSituacao(this.getGrupoMaterialAnalise().getIndSituacao());
				
			this.cadastrosApoioExamesFacade.persistirGrupoXMaterialAnalise(grupoXMaterialAnalise);
			this.listaGrupoXMaterialAnalise= this.cadastrosApoioExamesFacade.pesquisarGrupoXMaterialAnalisePorGrupo(this.codigo);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_GRUPO_X_MATERIAL_ANALISE");
			this.materialAnalise = null;
			this.habilitaBotaoGravar = false;
		} catch(BaseException e){
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void setGrupoXMaterialAnaliseExclusao(AelGrupoXMaterialAnalise grupoXMaterialAnalise) {
		this.grupoXMaterialAnaliseExclusao = grupoXMaterialAnalise;
	}
	
	public void excluirGrupoXMaterialAnalise(AelGrupoXMaterialAnalise entity)  {
		if (entity != null) {
			this.cadastrosApoioExamesFacade.removerGrupoXMaterialAnalise(entity.getId());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_GRUPO_X_MATERIAL_ANALISE");
			this.listaGrupoXMaterialAnalise = this.cadastrosApoioExamesFacade.pesquisarGrupoXMaterialAnalisePorGrupo(this.codigo);
		}
	}
	
	public void editarSituacao(AelGrupoXMaterialAnalise grupoXMaterialAnalise) {
		try{
			DominioSituacao situacao = grupoXMaterialAnalise.getIndSituacao() == DominioSituacao.A ? DominioSituacao.I : DominioSituacao.A;
			grupoXMaterialAnalise.setIndSituacao(situacao);
			this.cadastrosApoioExamesFacade.atualizarGrupoXMaterialAnalise(grupoXMaterialAnalise);
		} catch(BaseException e){
			DominioSituacao situacaoRetorno = grupoXMaterialAnalise.getIndSituacao() == DominioSituacao.A ? DominioSituacao.I : DominioSituacao.A;
			grupoXMaterialAnalise.setIndSituacao(situacaoRetorno);
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean verificarSituacao(AelGrupoXMaterialAnalise grupoXMaterialAnalise) {
		return (grupoXMaterialAnalise.getIndSituacao() == DominioSituacao.A);
	}

	public String cancelar() {
		this.grupoMaterialAnalise = null;
		this.codigo = null;
		this.emEdicao = false;
		return GRUPO_MATERIAL_ANALISE_PESQUISA;
	}
	
	public List<AelMateriaisAnalises> obterMaterialAnalise(String parametro){
		return this.examesFacade.listarAelMateriaisAnalises(parametro);
	}
	
	public void habilitarBotaoGravar(){
		this.setHabilitaBotaoGravar(true);
	}
	
	public void desabilitarBotaoGravar(){
		this.setHabilitaBotaoGravar(false);
	}
	
	public Integer getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	

	public boolean isEmEdicao() {
		return emEdicao;
	}
	
	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}
	
	public AelGrupoMaterialAnalise getGrupoMaterialAnalise() {
		return grupoMaterialAnalise;
	}

	public void setGrupoMaterialAnalise(AelGrupoMaterialAnalise grupoMaterialAnalise) {
		this.grupoMaterialAnalise = grupoMaterialAnalise;
	}



	public List<AelGrupoXMaterialAnalise> getListaGrupoXMaterialAnalise() {
		return listaGrupoXMaterialAnalise;
	}

	public void setListaGrupoXMaterialAnalise(
			List<AelGrupoXMaterialAnalise> listaGrupoXMaterialAnalise) {
		this.listaGrupoXMaterialAnalise = listaGrupoXMaterialAnalise;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public AelGrupoXMaterialAnalise getGrupoXMaterialAnaliseExclusao() {
		return grupoXMaterialAnaliseExclusao;
	}

	public AelMateriaisAnalises getMaterialAnalise() {
		return materialAnalise;
	}

	public void setMaterialAnalise(AelMateriaisAnalises materialAnalise) {
		this.materialAnalise = materialAnalise;
	}
	

	public Boolean getSituacaoGrupo() {
		return situacaoGrupo;
	}

	public void setSituacaoGrupo(Boolean situacaoGrupo) {
		this.situacaoGrupo = situacaoGrupo;
	}

	public Boolean getHabilitaBotaoGravar() {
		return habilitaBotaoGravar;
	}

	public void setHabilitaBotaoGravar(Boolean habilitaBotaoGravar) {
		this.habilitaBotaoGravar = habilitaBotaoGravar;
	}
	
	
}

