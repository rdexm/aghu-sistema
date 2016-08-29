package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.primefaces.event.SelectEvent;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaCompoGrupoComponente;
import br.gov.mec.aghu.model.AfaCompoGrupoComponenteId;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.AfaCompoGrupoComponenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaGrupoComponenteNptVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarGruposComponentesNPTController extends ActionController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4994432858036477786L;

	private List<DominioSituacao> situacoes;

	private DominioSituacao situacao;
	private Short codigo;
	private String descricao;

	private boolean pesquisaAtiva;

	private List<AfaGrupoComponenteNptVO> lista = new ArrayList<AfaGrupoComponenteNptVO>();
	
	private List<AfaCompoGrupoComponenteVO> lista2 = new ArrayList<AfaCompoGrupoComponenteVO>();

	private AfaGrupoComponenteNptVO item = new AfaGrupoComponenteNptVO();
	
	private AfaGrupoComponenteNptVO itemSelecionado = new AfaGrupoComponenteNptVO();
	
	private AfaCompoGrupoComponenteVO item2Selecionado = new AfaCompoGrupoComponenteVO();
	
	private AfaTipoComposicoes tipo = null;

	private AfaCompoGrupoComponenteVO item2 = new AfaCompoGrupoComponenteVO();
	
	
	private boolean situacaoManter;
	
	private boolean situacaoManter2;

	private Boolean permissao;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IPermissionService permissionService;

	private Boolean modoEdicao;
	
	private Boolean modoEdicao2;
	
	private static final String REDIRECIONA_HISTORICO= "pesquisaHistoricoGrupoComponentesNPT";

	@PostConstruct
	public void inicio() {
		begin(conversation, true);
		limparPesquisa();
	}

	public void init() {
		situacoes = new ArrayList<DominioSituacao>();
		situacoes.add(DominioSituacao.A);
		situacoes.add(DominioSituacao.I);

		permissao = permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "manterCadastrosNPT", "manter");
		
	}
	
	public String historico(){
		return REDIRECIONA_HISTORICO;
	}
	
	public void pesquisa2(){
		lista2 = prescricaoMedicaFacade.obterListaCompoGrupo(itemSelecionado.getSeq());
		for (AfaCompoGrupoComponenteVO afaCompoGrupoComponenteVO : lista2) {
			afaCompoGrupoComponenteVO.setTooltip("Criado por:"+afaCompoGrupoComponenteVO.getUsuario());
			if(afaCompoGrupoComponenteVO.getUsuario2() != null){
				afaCompoGrupoComponenteVO.setTooltip2("Alterado por:"+afaCompoGrupoComponenteVO.getUsuario2());
			}else{
				afaCompoGrupoComponenteVO.setTooltip2("Alterado por:");
			}
		}
		situacaoManter2 = true;
		modoEdicao2 = false;
	}
	
	public List<AfaTipoComposicoes> obterSuggestion(String strPesquisa){
		return prescricaoMedicaFacade.obterListaSuggestionTipoComposicoes(strPesquisa,lista2);
	}

	public void pesquisar() {
		lista = prescricaoMedicaFacade.obterListaGrupoComponentes(codigo, situacao, descricao);
		for (AfaGrupoComponenteNptVO afaGrupoComponenteNptVO : lista) {
			String dataFormatada = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(afaGrupoComponenteNptVO.getCriadoEm());
			afaGrupoComponenteNptVO.setTooltipCodigo("Criado em:"+dataFormatada);
			afaGrupoComponenteNptVO.setTooltipCodigo2("Responsável:"+afaGrupoComponenteNptVO.getUsuario());
		}
		item = new AfaGrupoComponenteNptVO();
		situacaoManter = true;
		situacaoManter2 = true;
		this.setPesquisaAtiva(true);
		if(lista.size() > 0){
			itemSelecionado = lista.get(0);
			pesquisa2();
		}
		modoEdicao = false;
		modoEdicao2 = false;
	}

	public void limparPesquisa() {
		// form pesquisa 1
		codigo = null;
		descricao = null;
		situacao = null;

		// form crud 1
		item = new AfaGrupoComponenteNptVO();
		situacaoManter = true;

		// listagem 1
		lista = new ArrayList<AfaGrupoComponenteNptVO>();
		this.setPesquisaAtiva(false);
		itemSelecionado = new AfaGrupoComponenteNptVO();

		// form crud 2
		tipo = null;
		item2Selecionado = new AfaCompoGrupoComponenteVO();
		situacaoManter2 = true;
		
		// listagem 2
		lista2 = new ArrayList<AfaCompoGrupoComponenteVO>();
		item2 = new AfaCompoGrupoComponenteVO();
		
		
		modoEdicao = false;
		modoEdicao2 = false;
		
	}

	public boolean validaForm() {

		this.apresentarMsgNegocio(Severity.ERROR,
				"MENSAGEM_PELO_MENOS_UM_FILTRO");

		return true;
	}

	public void adicionarGrupo() {
		item.setIndSituacao(DominioSituacao.I);
		if (situacaoManter == true) {
			item.setIndSituacao(DominioSituacao.A);
		}
		if (item.getSeq() == null) {
			prescricaoMedicaFacade.inserirGrupoComponentes(item);
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_INCLUIR_GRUPO");
		} else {
			prescricaoMedicaFacade.inserirGrupoComponentes(item);
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_ALTERAR_GRUPO");
		}
		modoEdicao = false;
		modoEdicao2 = false;
		pesquisar();
	}
	
	public void adicionarCompo() {
		AfaCompoGrupoComponente compo = new AfaCompoGrupoComponente();
		AfaCompoGrupoComponenteId id = new AfaCompoGrupoComponenteId();
		
		id.setGcnSeq(itemSelecionado.getSeq());
		id.setTicSeq(tipo.getSeq());
		compo.setId(id);
		//compo.setAfaTipoComposicoes(tipo);
		//compo.setAfaGrupoComponenteNpt(itemSelecionado);
		compo.setCriadoEm(new Date());
		
		compo.setIndSituacao(DominioSituacao.I);
		if(situacaoManter2){
			compo.setIndSituacao(DominioSituacao.A);
		}
		
		try {
			prescricaoMedicaFacade.inserirCompoGrupoComponentes(compo,tipo,itemSelecionado);
			//
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_INCLUIR_COMPO");
			tipo = null;
			pesquisa2();
			situacaoManter2 = true;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		modoEdicao = false;
		modoEdicao2 = false;
	}
	
	public void alterarCompo() {
		item2.setAlteradoEm(new Date());
		item2.setIndSituacao(DominioSituacao.I);
		if(situacaoManter2){
			item2.setIndSituacao(DominioSituacao.A);
		}
		
		try {
			prescricaoMedicaFacade.alterarCompoGrupoComponentes(item2,tipo,itemSelecionado);
			//
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_ALTERAR_COMPO");
			tipo = null;
			pesquisa2();
			situacaoManter2 = true;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		modoEdicao = false;
		modoEdicao2 = false;
	}

	public void editarGrupo(AfaGrupoComponenteNptVO selecionado) {
		modoEdicao = true;
		item = selecionado;
		itemSelecionado = selecionado;
		situacaoManter = false;
		if(DominioSituacao.A.equals(selecionado.getIndSituacao())){
			situacaoManter = true;
		}
	}
	
	public void editarCompo(AfaCompoGrupoComponenteVO selecionado) {
		modoEdicao2 = true;
		tipo = prescricaoMedicaFacade.obterTipoPorSeq(selecionado.getTicSeq());
		item2 = selecionado;
		situacaoManter2 = false;
		if(DominioSituacao.A.equals(selecionado.getIndSituacao())){
			situacaoManter2 = true;
		}
	}
	

	public void cancelarEditarGrupo() {
		modoEdicao = false;
		item = new AfaGrupoComponenteNptVO();
		item.setDescricao(null);
	}
	
	public void cancelarEditarCompo() {
		modoEdicao = false;
		modoEdicao2 = false;
		item2 = new AfaCompoGrupoComponenteVO();
		tipo = null;
		situacaoManter2 = true;
	}

	public void excluirGrupo() {
		try {
			prescricaoMedicaFacade.removerGrupoComponentes(itemSelecionado);
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_EXCLUIR_GRUPO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
	}
	
	
	public void excluirCompo() {
		try {
			prescricaoMedicaFacade.removerCompo(item2Selecionado);
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_EXCLUIR_COMPO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisa2();
		// executar pesquisa da lista 2
	}
	
	public void onRowSelect(SelectEvent event) {
		modoEdicao2 = false;
		tipo = null;
		pesquisa2();
    }

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public Boolean getPermissao() {
		return permissao;
	}

	public void setPermissao(Boolean permissao) {
		this.permissao = permissao;
	}

	public List<DominioSituacao> getSituacoes() {
		return situacoes;
	}

	public void setSituacoes(List<DominioSituacao> situacoes) {
		this.situacoes = situacoes;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<AfaGrupoComponenteNptVO> getLista() {
		return lista;
	}

	public void setLista(List<AfaGrupoComponenteNptVO> lista) {
		this.lista = lista;
	}

	public AfaGrupoComponenteNptVO getItem() {
		return item;
	}

	public void setItem(AfaGrupoComponenteNptVO item) {
		this.item = item;
	}

	public boolean isSituacaoManter() {
		return situacaoManter;
	}

	public void setSituacaoManter(boolean situacaoManter) {
		this.situacaoManter = situacaoManter;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public AfaGrupoComponenteNptVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(AfaGrupoComponenteNptVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public List<AfaCompoGrupoComponenteVO> getLista2() {
		return lista2;
	}

	public void setLista2(List<AfaCompoGrupoComponenteVO> lista2) {
		this.lista2 = lista2;
	}

	public AfaTipoComposicoes getTipo() {
		return tipo;
	}

	public void setTipo(AfaTipoComposicoes tipo) {
		this.tipo = tipo;
	}

	public boolean isSituacaoManter2() {
		return situacaoManter2;
	}

	public void setSituacaoManter2(boolean situacaoManter2) {
		this.situacaoManter2 = situacaoManter2;
	}

	public AfaCompoGrupoComponenteVO getItem2() {
		return item2;
	}

	public void setItem2(AfaCompoGrupoComponenteVO item2) {
		this.item2 = item2;
	}

	public Boolean getModoEdicao2() {
		return modoEdicao2;
	}

	public void setModoEdicao2(Boolean modoEdicao2) {
		this.modoEdicao2 = modoEdicao2;
	}

	public AfaCompoGrupoComponenteVO getItem2Selecionado() {
		return item2Selecionado;
	}

	public void setItem2Selecionado(AfaCompoGrupoComponenteVO item2Selecionado) {
		this.item2Selecionado = item2Selecionado;
	}
	
}
