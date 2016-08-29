package br.gov.mec.aghu.transplante.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.AelExamesXAelParametroCamposLaudoVO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.MtxExameUltResults;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastraExamesTransplantesController extends ActionController {
	
	private static final long serialVersionUID = 4674544361339848406L;
	
	private static final String PAGE_PESQUISA_EXAMES_TRANSPLANTES = "transplante-pesquisaExamesTransplantes";
	private MtxExameUltResults selecionado;
	private AelExames aelExames;
	private AelExamesXAelParametroCamposLaudoVO campoLaudo;
	private Boolean ativo;
	private Boolean edicaoAtiva = Boolean.FALSE;
	private Boolean exibicaoAtivo = Boolean.FALSE;
	
	@EJB
	private ITransplanteFacade transplantes;
	
	@EJB
	private IExamesFacade exames;
	
	@PostConstruct 
	public void init(){
		begin(conversation, true);
	}
	
	public void iniciar(){
		if(edicaoAtiva || exibicaoAtivo){
			ativo = selecionado.getSituacao().isAtivo();
			aelExames = selecionado.getAelExames();
			campoLaudo = getCampoLaudoVo();
		}else{
			limpar();
		}
	}
	
	public String gravar() {
		
		try{
			if(edicaoAtiva){
				selecionado.setSituacao(DominioSituacao.getInstance(ativo));
				selecionado.setAelExames(aelExames);
				selecionado.setCampoLaudo(voTOentity());
				transplantes.atualizarMtxExameUltResults(selecionado);
				apresentarMsgNegocio(Severity.INFO, "MSG_EXAMES_TRANSPLANTES_EDICAO_SUCESSO", "");
			}else{
				selecionado = new MtxExameUltResults();
				selecionado.setAelExames(aelExames);
				selecionado.setCampoLaudo(voTOentity());
				selecionado.setSituacao(DominioSituacao.getInstance(ativo));
				transplantes.inserirMtxExameUltResults(selecionado);
				apresentarMsgNegocio(Severity.INFO, "MSG_EXAMES_TRANSPLANTES_CADASTRO_SUCESSO", "");
			}
		}catch(ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_PESQUISA_EXAMES_TRANSPLANTES;
	}
	
	public void limpar(){
		selecionado = new MtxExameUltResults();
		aelExames = null;
		campoLaudo = null;
		ativo = Boolean.TRUE;
	}
	
	public String cancelar(){
		limpar();
		return PAGE_PESQUISA_EXAMES_TRANSPLANTES;
	}
	
	public List<AelExames> listarExames(String _filtro){
		return this.returnSGWithCount(exames.obterAelExamesPorSiglaDescricao(_filtro), 
									  exames.obterAelExamesPorSiglaDescricaoCount(_filtro)); 
	}
	
	public List<AelExamesXAelParametroCamposLaudoVO> listarCamposLaudo(String nome){
		return this.returnSGWithCount(exames.obterAelCampoLaudoPorNome(nome, (aelExames != null)? aelExames.getSigla() : ""), 
									  exames.obterAelCampoLaudoPorNomeCount(nome, (aelExames != null)? aelExames.getSigla() : ""));
	}
	
	public void limparCampoLaudo(){
		campoLaudo = null;
	}
	
	private AelCampoLaudo voTOentity(){
		AelCampoLaudo aelCampoLaudo = new AelCampoLaudo();
		aelCampoLaudo.setSeq(campoLaudo.getSeq());
		return aelCampoLaudo;
	}
	
	private AelExamesXAelParametroCamposLaudoVO getCampoLaudoVo(){
		AelExamesXAelParametroCamposLaudoVO vo = new AelExamesXAelParametroCamposLaudoVO();
		vo.setDescricao(aelExames.getDescricao());
		vo.setNome(selecionado.getCampoLaudo().getNome());
		vo.setSeq(selecionado.getCampoLaudo().getSeq());
		vo.setSigla(aelExames.getSigla());
		return vo; 
	}
	
	public Boolean getEdicaoAtiva() {
		return edicaoAtiva;
	}

	public void setEdicaoAtiva(Boolean edicaoAtiva) {
		this.edicaoAtiva = edicaoAtiva;
	}

	public MtxExameUltResults getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(MtxExameUltResults selecionado) {
		this.selecionado = selecionado;
	}

	public AelExames getAelExames() {
		return aelExames;
	}

	public void setAelExames(AelExames aelExames) {
		this.aelExames = aelExames;
	}

	public AelExamesXAelParametroCamposLaudoVO getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelExamesXAelParametroCamposLaudoVO campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	public Boolean getExibicaoAtivo() {
		return exibicaoAtivo;
	}

	public void setExibicaoAtivo(Boolean exibicaoAtivo) {
		this.exibicaoAtivo = exibicaoAtivo;
	}	
}
