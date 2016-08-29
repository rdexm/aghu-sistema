package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ManterFavoritosUsuarioVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterFavoritosUsuarioController extends ActionController {

	private static final long serialVersionUID = 5292879512940178461L;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private List<MptSalas> listaSalas = new ArrayList<MptSalas>();
	private MptSalas salaCombo;
	private List<MptTipoSessao> listaTipoSessao = new ArrayList<MptTipoSessao>();
	private MptTipoSessao tipoSessaoCombo;
	private ManterFavoritosUsuarioVO favorito;
	private String nomeServidor;
	private RapServidores servidor;
	private MptFavoritoServidor favoritoServidor;
	private Integer favoritoSeq;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);		
	}
	
	public void iniciar() throws ApplicationBusinessException{
		favorito = new ManterFavoritosUsuarioVO();
		favoritoSeq = null;
		listaSalas = null;
		listaTipoSessao = null;
		nomeServidor = obterUsuariologado().getPessoaFisica().getNome();
		carregarCombos();
		obterFavorito();
	}
	
	public void carregarCombos(){
		carregarTipoSessao();
		carregarSalas();
		
	}
	
	public void carregarTipoSessao(){
		listaTipoSessao = procedimentoTerapeuticoFacade.obterTipoSessaoDescricao();
	}
	
	public void carregarSalas(){
		if (tipoSessaoCombo != null && tipoSessaoCombo.getSeq() != null){
			listaSalas = procedimentoTerapeuticoFacade.obterSalaPorTipoSessao(tipoSessaoCombo.getSeq());
		}else{
			listaSalas = procedimentoTerapeuticoFacade.obterSalaPorTipoSessao(null);
		}
	}
	
	public void obterFavorito(){
		favorito = procedimentoTerapeuticoFacade.obterFavoritoPorPesCodigo(servidor);
		if(favorito != null && favorito.getSeq() != null){
			favoritoSeq = favorito.getFavoritosSeq();
		}
	}
	
	public RapServidores obterUsuariologado() throws ApplicationBusinessException{
		servidor = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(obterLoginUsuarioLogado());
		return servidor;
	}
	
	public void adicionar(){
		try{
			favorito = procedimentoTerapeuticoFacade.adicionarFavorito(salaCombo, tipoSessaoCombo, favorito);
			carregarCombos();
			tipoSessaoCombo = null;
			salaCombo = null;
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravar(){
		try{
			if (favoritoSeq != null) {
				procedimentoTerapeuticoFacade.removerMptFavoritoServidor(favoritoSeq);
				this.apresentarMsgNegocio(Severity.INFO, "MSG_REMOCAO_FAV_SUCESSO");
			}else{
				procedimentoTerapeuticoFacade.persistirMptFavoritoServidor(favorito, servidor);
				apresentarMsgNegocio(Severity.INFO, "MSG_CADASTRO_FAV_SUCESSO");
			}
			iniciar();
			tipoSessaoCombo = null;
			salaCombo = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}	
	}
	
	public void excluir(){
		
		if (favorito != null) {
			favorito = null;
		}else{
			this.apresentarMsgNegocio(Severity.INFO, "MSG_ERRO_REMOCAO_FAV");
		}
	}
	
	
	//Getters and Setters
	public List<MptSalas> getListaSalas() {
		return listaSalas;
	}

	public void setListaSalas(List<MptSalas> listaSalas) {
		this.listaSalas = listaSalas;
	}

	public MptSalas getSalaCombo() {
		return salaCombo;
	}

	public void setSalaCombo(MptSalas salaCombo) {
		this.salaCombo = salaCombo;
	}

	public List<MptTipoSessao> getListaTipoSessao() {
		return listaTipoSessao;
	}

	public void setListaTipoSessao(List<MptTipoSessao> listaTipoSessao) {
		this.listaTipoSessao = listaTipoSessao;
	}

	public MptTipoSessao getTipoSessaoCombo() {
		return tipoSessaoCombo;
	}

	public void setTipoSessaoCombo(MptTipoSessao tipoSessaoCombo) {
		this.tipoSessaoCombo = tipoSessaoCombo;
	}

	public ManterFavoritosUsuarioVO getFavorito() {
		return favorito;
	}

	public void setFavorito(ManterFavoritosUsuarioVO favorito) {
		this.favorito = favorito;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public MptFavoritoServidor getFavoritoServidor() {
		return favoritoServidor;
	}

	public void setFavoritoServidor(MptFavoritoServidor favoritoServidor) {
		this.favoritoServidor = favoritoServidor;
	}

}
