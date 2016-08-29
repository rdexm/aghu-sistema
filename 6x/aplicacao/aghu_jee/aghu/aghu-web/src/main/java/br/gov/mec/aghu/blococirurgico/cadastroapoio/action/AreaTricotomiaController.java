package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcAreaTricotomia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para tela de pesquisa/cadastro de Areas de Triconomia.
 * 
 * @author dpacheco
 *
 */

public class AreaTricotomiaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3762669677142340391L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	// Filtros da pesquisa
	private Short filtroSeq;
	private String filtroDescricao;
	private DominioSituacao filtroSituacao;
	
	// Campos para inserção
	private String descricao;
	
	private Boolean realizouPesquisa = Boolean.FALSE;
	
	private List<MbcAreaTricotomia> listaAreaTricotomia;
	
	
	public void iniciar() {
		limpar();
	}
	
	public void limpar() {
		filtroSeq = null;
		filtroDescricao = null;
		filtroSituacao = null;
		descricao = null;
		realizouPesquisa = Boolean.FALSE;
	}
	
	public String pesquisar() {
		listaAreaTricotomia = blocoCirurgicoCadastroApoioFacade
				.pesquisarAreaTricotomia(filtroSeq, filtroDescricao, filtroSituacao);
		realizouPesquisa = Boolean.TRUE;
		return null;
	}
	
	public void gravar() {
		try {
			blocoCirurgicoCadastroApoioFacade.validarPreenchimentoDescricao(descricao);
			
			MbcAreaTricotomia areaTricotomia = blocoCirurgicoCadastroApoioFacade.obterNovaAreaTricotomia(descricao);
			blocoCirurgicoCadastroApoioFacade.inserirAreaTricotomia(areaTricotomia);
			
			this.apresentarMsgNegocio(Severity.INFO, 
					"MENSAGEM_SUCESSO_INSERIR_AREA_TRICOTOMIA", areaTricotomia.getDescricao());
			
			pesquisar();
			descricao = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}
	
	public void ativarInativar(MbcAreaTricotomia areaTricotomia) {
		Boolean novaIndSituacao = !areaTricotomia.getSituacao().isAtivo();
		areaTricotomia.setSituacao(obterSituacaoSelecionada(novaIndSituacao));
		
		try {
			blocoCirurgicoCadastroApoioFacade.atualizarAreaTricotomia(areaTricotomia);
			
			String keyMensagem = "";
			if (areaTricotomia.getSituacao().isAtivo()) {
				keyMensagem = "MENSAGEM_SUCESSO_ATIVAR_AREA_TRICOTOMIA";
			} else {
				keyMensagem = "MENSAGEM_SUCESSO_DESATIVAR_AREA_TRICOTOMIA";
			}
			
			this.apresentarMsgNegocio(Severity.INFO, 
					keyMensagem, areaTricotomia.getDescricao());
		} catch (ApplicationBusinessException  e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private DominioSituacao obterSituacaoSelecionada(Boolean booleanIndSituacao) {
		return DominioSituacao.getInstance(booleanIndSituacao);	
	}	

	public Short getFiltroSeq() {
		return filtroSeq;
	}

	public void setFiltroSeq(Short filtroSeq) {
		this.filtroSeq = filtroSeq;
	}

	public String getFiltroDescricao() {
		return filtroDescricao;
	}

	public void setFiltroDescricao(String filtroDescricao) {
		this.filtroDescricao = filtroDescricao;
	}

	public DominioSituacao getFiltroSituacao() {
		return filtroSituacao;
	}

	public void setFiltroSituacao(DominioSituacao filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<MbcAreaTricotomia> getListaAreaTricotomia() {
		return listaAreaTricotomia;
	}

	public void setListaAreaTricotomia(List<MbcAreaTricotomia> listaAreaTricotomia) {
		this.listaAreaTricotomia = listaAreaTricotomia;
	}

	public Boolean getRealizouPesquisa() {
		return realizouPesquisa;
	}

	public void setRealizouPesquisa(Boolean realizouPesquisa) {
		this.realizouPesquisa = realizouPesquisa;
	}

}
