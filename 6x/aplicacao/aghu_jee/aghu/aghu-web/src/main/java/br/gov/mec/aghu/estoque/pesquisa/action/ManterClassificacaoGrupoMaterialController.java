package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.ScoClassifMatNiv1;
import br.gov.mec.aghu.model.ScoClassifMatNiv2;
import br.gov.mec.aghu.model.ScoClassifMatNiv3;
import br.gov.mec.aghu.model.ScoClassifMatNiv4;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller responsável pela manutenção de classificação de grupos de materiais.
 * 
 * @author luismoura
 * 
 */

public class ManterClassificacaoGrupoMaterialController extends ActionController {
	
	private static final long serialVersionUID = 734773281742077563L;

	// --------------------------------- CONSTANTES
	private static final int NENHUM = 0;
	private static final int NIVEL1 = 1;
	private static final int NIVEL2 = 2;
	private static final int NIVEL3 = 3;
	private static final int NIVEL4 = 4;
	private static final int NIVEL5 = 5;

	// --------------------------------- MESSAGE CODES
	public enum EnumManterClassificacaoGrupoMaterialMessageCode {
		M1_CLASSIFICACAO_MATERIAL, //
		M2_CLASSIFICACAO_MATERIAL, //
		M3_CLASSIFICACAO_MATERIAL, //
		M4_CLASSIFICACAO_MATERIAL, //
		M5_CLASSIFICACAO_MATERIAL, //
		;
	}

	// --------------------------------- FACADES
	@EJB
	private IComprasFacade comprasFacade;

	// --------------------------------- CAMPOS DE TELA
	private ScoGrupoMaterial grupoMateriais;
	private List<ScoClassifMatNiv1> listaScoClassifMatNiv1; 
	

	private ScoClassifMatNiv1 scoClassifMatNiv1;
	private ScoClassifMatNiv2 scoClassifMatNiv2;
	private ScoClassifMatNiv3 scoClassifMatNiv3;
	private ScoClassifMatNiv4 scoClassifMatNiv4;
	private ScoClassifMatNiv5 scoClassifMatNiv5;

	private String descricaoNiv1;
	private String descricaoNiv2;
	private String descricaoNiv3;
	private String descricaoNiv4;
	private String descricaoNiv5;

	private boolean editandoNiv1;
	private boolean editandoNiv2;
	private boolean editandoNiv3;
	private boolean editandoNiv4;
	private boolean editandoNiv5;

	private int nivelExcluir;
	private Object ultimaSelecao;

	// ---------------------------------

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	

	/**
	 * Limpa os campos da tela
	 */
	public void limparCampos() {
		this.limparCamposNivel(NIVEL1, true, true);
	}
	
	/**
	 * Limpa os campos do nível
	 */
	public void limparCamposNivel(int nivel, boolean desmarca, boolean desmarcaProximos) {
		int selecao = (NIVEL5 - nivel) + NIVEL1;
		desmarca = this.limparCamposNiv1(desmarca, desmarcaProximos, selecao);
		desmarca = this.limparCamposNiv2(desmarca, desmarcaProximos, selecao);
		desmarca = this.limparCamposNiv3(desmarca, desmarcaProximos, selecao);
		desmarca = this.limparCamposNiv4(desmarca, desmarcaProximos, selecao);
		this.limparCamposNiv5(desmarca, selecao);
	}

	private void limparCamposNiv5(boolean desmarca, int selecao) {
		if (selecao >= NIVEL1) {
			if (desmarca) {
				this.scoClassifMatNiv5 = null;
			}
			this.descricaoNiv5 = null;
			this.editandoNiv5 = false;
		}
	}

	private boolean limparCamposNiv4(boolean desmarca, boolean desmarcaProximos, int selecao) {
		if (selecao >= NIVEL2) {
			if (desmarca) {
				this.scoClassifMatNiv4 = null;
			}
			this.descricaoNiv4 = null;
			this.editandoNiv4 = false;
			desmarca = (desmarcaProximos ? true : desmarca);
		}
		return desmarca;
	}

	private boolean limparCamposNiv3(boolean desmarca, boolean desmarcaProximos, int selecao) {
		if (selecao >= NIVEL3) {
			if (desmarca) {
				this.scoClassifMatNiv3 = null;
			}
			this.descricaoNiv3 = null;
			this.editandoNiv3 = false;
			desmarca = (desmarcaProximos ? true : desmarca);
		}
		return desmarca;
	}

	private boolean limparCamposNiv2(boolean desmarca, boolean desmarcaProximos, int selecao) {
		if (selecao >= NIVEL4) {
			if (desmarca) {
				this.scoClassifMatNiv2 = null;
			}
			this.descricaoNiv2 = null;
			this.editandoNiv2 = false;
			desmarca = (desmarcaProximos ? true : desmarca);
		}
		return desmarca;
	}

	private boolean limparCamposNiv1(boolean desmarca, boolean desmarcaProximos, int selecao) {
		if (selecao >= NIVEL5) {
			if (desmarca) {
				this.scoClassifMatNiv1 = null;
			}
			this.descricaoNiv1 = null;
			this.editandoNiv1 = false;
			desmarca = (desmarcaProximos ? true : desmarca);
		}
		return desmarca;
	}
	
	/**
	 * SUGGESTION Grupos - List
	 */
	public List<ScoGrupoMaterial> pesquisarGrupoMateriais(String objPesquisa) {
		return this.returnSGWithCount(this.comprasFacade.obterGrupoMaterialPorSeqDescricao(objPesquisa),pesquisarGrupoMateriaisCount(objPesquisa));
	}

	/**
	 * SUGGESTION Grupos - Count
	 */
	public Long pesquisarGrupoMateriaisCount(String objPesquisa) {
		return this.comprasFacade.obterGrupoMaterialPorSeqDescricaoCount(objPesquisa);
	}

	/**
	 * SUGGESTION Grupos - posSelectionAction
	 */
	public void posSelectionGrupoMateriais() {
		this.limparCampos();
		this.recarregarLista();
	}

	/**
	 * SUGGESTION Grupos - posDeleteAction
	 */
	public void posDeleteGrupoMateriais() {
		this.limparCampos();
		this.recarregarLista();
	}

	/**
	 * Ação dos botões "Excluir"
	 * 
	 * @param nivel
	 */
	public void excluir(int nivelExcluir) {
		this.nivelExcluir = nivelExcluir;
	}
	
	public boolean isEditando(){
		return editandoNiv1 || editandoNiv2 || editandoNiv3 || editandoNiv4 || editandoNiv5;
	}
	
	public boolean isEditandoOutro(int nivel){
		boolean editando = false;
		if(nivel != NIVEL1){
			editando = editando || editandoNiv1;
		}
		if(nivel != NIVEL2){
			editando = editando || editandoNiv2;
		}
		if(nivel != NIVEL3){
			editando = editando || editandoNiv3;
		}
		if(nivel != NIVEL4){
			editando = editando || editandoNiv4;
		}
		if(nivel != NIVEL5){
			editando = editando || editandoNiv5;
		}
		return editando;
	}

	/**
	 * Ação dos botões "Sim" de confirmação de exclusão
	 * 
	 */
	public void confirmarExclusao() {
		switch (nivelExcluir) {
		case NIVEL1:
			try {
				comprasFacade.removerScoClassifMatNiv1(scoClassifMatNiv1);
				this.pesquisar();
				this.limparCamposNivel(nivelExcluir, true, true);
				this.nivelExcluir = NENHUM;
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M4_CLASSIFICACAO_MATERIAL.toString());
			} catch (ApplicationBusinessException e) {
				super.apresentarExcecaoNegocio(e);
			}
			break;
		case NIVEL2:
			try {
				comprasFacade.removerScoClassifMatNiv2(scoClassifMatNiv2);
				
				this.pesquisar();
				this.limparCamposNivel(nivelExcluir, true, true);
				this.nivelExcluir = NENHUM;
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M4_CLASSIFICACAO_MATERIAL.toString());
			} catch (ApplicationBusinessException e) {
				super.apresentarExcecaoNegocio(e);
			}
			break;
		case NIVEL3:
			try {
				comprasFacade.removerScoClassifMatNiv3(scoClassifMatNiv3);
				
				this.pesquisar();
				this.limparCamposNivel(nivelExcluir, true, true);
				this.nivelExcluir = NENHUM;
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M4_CLASSIFICACAO_MATERIAL.toString());
			} catch (ApplicationBusinessException e) {
				super.apresentarExcecaoNegocio(e);
			}
			break;
		case NIVEL4:
			try {
				comprasFacade.removerScoClassifMatNiv4(scoClassifMatNiv4);
				
				this.pesquisar();
				this.limparCamposNivel(nivelExcluir, true, true);
				this.nivelExcluir = NENHUM;
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M4_CLASSIFICACAO_MATERIAL.toString());
			} catch (ApplicationBusinessException e) {
				super.apresentarExcecaoNegocio(e);
			}
			break;
		case NIVEL5:
			comprasFacade.removerScoClassifMatNiv5(scoClassifMatNiv5);
			
			this.pesquisar();
			this.limparCamposNivel(nivelExcluir, true, true);
			this.nivelExcluir = NENHUM;
			super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M4_CLASSIFICACAO_MATERIAL.toString());
			break;
		default:
			break;
		}
	}

	private void pesquisar() {
		if (grupoMateriais != null) {
			this.grupoMateriais = this.comprasFacade.obterGrupoMaterialPorId(this.grupoMateriais.getCodigo());
			this.recarregarLista();
		}
		if (scoClassifMatNiv1 != null) {
			this.scoClassifMatNiv1 = this.comprasFacade.obterscoClassifMatNiv1PorChavePrimaria(scoClassifMatNiv1.getId());
		}
		if (scoClassifMatNiv2 != null) {
			this.scoClassifMatNiv2 = this.comprasFacade.obterscoClassifMatNiv2PorChavePrimaria(scoClassifMatNiv2.getId());
		}
		if (scoClassifMatNiv3 != null) {
			this.scoClassifMatNiv3 = this.comprasFacade.obterscoClassifMatNiv3PorChavePrimaria(scoClassifMatNiv3.getId());
		}
		if (scoClassifMatNiv4 != null) {
			this.scoClassifMatNiv4 = this.comprasFacade.obterscoClassifMatNiv4PorChavePrimaria(scoClassifMatNiv4.getId());
		}
		if (scoClassifMatNiv5 != null) {	
			this.scoClassifMatNiv5 = this.comprasFacade.obterScoClassifMatNiv5PorSeq(scoClassifMatNiv5.getNumero());
		}
	}
	
	private void recarregarLista(){
		if(this.grupoMateriais != null){
			this.listaScoClassifMatNiv1 = this.comprasFacade.listarScoClassifMatNiv1PorGrupoMaterial(this.grupoMateriais.getCodigo());
		}
	}

	/**
	 * Botão selecionar
	 * 
	 * @param nivel
	 * @param selecionado
	 */
	public void selecionar(int nivel, Object selecionado) {
		switch (nivel) {
		case NIVEL1:
			this.scoClassifMatNiv1 = (ScoClassifMatNiv1) selecionado;
			this.scoClassifMatNiv1 = this.comprasFacade.obterscoClassifMatNiv1PorChavePrimaria(scoClassifMatNiv1.getId());
			break;
		case NIVEL2:
			this.scoClassifMatNiv2 = (ScoClassifMatNiv2) selecionado;
			this.scoClassifMatNiv2 = this.comprasFacade.obterscoClassifMatNiv2PorChavePrimaria(scoClassifMatNiv2.getId());
			break;
		case NIVEL3:
			this.scoClassifMatNiv3 = (ScoClassifMatNiv3) selecionado;
			this.scoClassifMatNiv3 = this.comprasFacade.obterscoClassifMatNiv3PorChavePrimaria(scoClassifMatNiv3.getId());
			break;
		case NIVEL4:
			this.scoClassifMatNiv4 = (ScoClassifMatNiv4) selecionado;
			this.scoClassifMatNiv4 = this.comprasFacade.obterscoClassifMatNiv4PorChavePrimaria(scoClassifMatNiv4.getId());
			break;
		case NIVEL5:
			this.scoClassifMatNiv5 = (ScoClassifMatNiv5) selecionado;
			this.scoClassifMatNiv5 = this.comprasFacade.obterScoClassifMatNiv5PorSeq(scoClassifMatNiv5.getNumero());
			break;
		default:
			break;
		}

		if (!selecionado.equals(ultimaSelecao)) {
			this.ultimaSelecao = selecionado;
			this.limparCamposNivel(nivel, false, true);
		}

	}

	/**
	 * Ação dos botões "Editar"
	 * 
	 * @param nivel
	 */
	public void editar(int nivel) {
		switch (nivel) {
		case NIVEL1:
			this.descricaoNiv1 = scoClassifMatNiv1.getDescricao();
			this.editandoNiv1 = true;
			break;
		case NIVEL2:
			this.descricaoNiv2 = scoClassifMatNiv2.getDescricao();
			this.editandoNiv2 = true;
			break;
		case NIVEL3:
			this.descricaoNiv3 = scoClassifMatNiv3.getDescricao();
			this.editandoNiv3 = true;
			break;
		case NIVEL4:
			this.descricaoNiv4 = scoClassifMatNiv4.getDescricao();
			this.editandoNiv4 = true;
			break;
		case NIVEL5:
			this.descricaoNiv5 = scoClassifMatNiv5.getDescricao();
			this.editandoNiv5 = true;
			break;
		default:
			break;
		}
	}

	/**
	 * Ação dos botões "Cancelar" Alteração/Inclusão
	 * 
	 */
	public void cancelar(int nivel) {
		this.limparCamposNivel(nivel, false, false);
	}

	/**
	 * Ação dos botões "Confirmar" Alteração/Inclusão
	 * 
	 */
	public void confirmar(int nivel) {
		switch (nivel) {
		case NIVEL1:
			if (editandoNiv1) {
				comprasFacade.atualizarScoClassifMatNiv1(scoClassifMatNiv1, descricaoNiv1);
				
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M2_CLASSIFICACAO_MATERIAL.toString());
			} else {
				comprasFacade.inserirScoClassifMatNiv1(grupoMateriais.getCodigo(), descricaoNiv1);
				
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M1_CLASSIFICACAO_MATERIAL.toString());
			}
			break;
		case NIVEL2:
			if (editandoNiv2) {
				comprasFacade.atualizarScoClassifMatNiv2(scoClassifMatNiv2, descricaoNiv2);
				
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M2_CLASSIFICACAO_MATERIAL.toString());
			} else {
				comprasFacade.inserirScoClassifMatNiv2(scoClassifMatNiv1.getId().getGmtCodigo(), scoClassifMatNiv1.getId().getCodigo(), descricaoNiv2);
				
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M1_CLASSIFICACAO_MATERIAL.toString());
			}
			break;
		case NIVEL3:
			if (editandoNiv3) {
				comprasFacade.atualizarScoClassifMatNiv3(scoClassifMatNiv3, descricaoNiv3);
				
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M2_CLASSIFICACAO_MATERIAL.toString());
			} else {
				comprasFacade.inserirScoClassifMatNiv3(scoClassifMatNiv2.getId().getCn1GmtCodigo(), scoClassifMatNiv2.getId().getCn1Codigo(), scoClassifMatNiv2
						.getId().getCodigo(), descricaoNiv3);
				
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M1_CLASSIFICACAO_MATERIAL.toString());
			}
			break;
		case NIVEL4:
			if (editandoNiv4) {
				comprasFacade.atualizarScoClassifMatNiv4(scoClassifMatNiv4, descricaoNiv4);
				
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M2_CLASSIFICACAO_MATERIAL.toString());
			} else {
				comprasFacade.inserirScoClassifMatNiv4(scoClassifMatNiv3.getId().getCn2Cn1GmtCodigo(), scoClassifMatNiv3.getId().getCn2Cn1Codigo(),
						scoClassifMatNiv3.getId().getCn2Codigo(), scoClassifMatNiv3.getId().getCodigo(), descricaoNiv4);
				
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M1_CLASSIFICACAO_MATERIAL.toString());
			}
			break;
		case NIVEL5:
			if (editandoNiv5) {
				comprasFacade.atualizarScoClassifMatNiv5(scoClassifMatNiv5, descricaoNiv5);
				
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M2_CLASSIFICACAO_MATERIAL.toString());
			} else {
				comprasFacade.inserirScoClassifMatNiv5(scoClassifMatNiv4, descricaoNiv5);
				
				super.apresentarMsgNegocio(Severity.INFO, EnumManterClassificacaoGrupoMaterialMessageCode.M1_CLASSIFICACAO_MATERIAL.toString());
			}
			break;
		default:
			break;
		}
		this.pesquisar();

		this.limparCamposNivel(nivel, false, false);
	}

	// --------------------------------- GETs e SETs

	public ScoGrupoMaterial getGrupoMateriais() {
		return grupoMateriais;
	}

	public void setGrupoMateriais(ScoGrupoMaterial grupoMateriais) {
		this.grupoMateriais = grupoMateriais;
	}

	public ScoClassifMatNiv1 getScoClassifMatNiv1() {
		return scoClassifMatNiv1;
	}

	public void setScoClassifMatNiv1(ScoClassifMatNiv1 scoClassifMatNiv1) {
		this.scoClassifMatNiv1 = scoClassifMatNiv1;
	}

	public ScoClassifMatNiv2 getScoClassifMatNiv2() {
		return scoClassifMatNiv2;
	}

	public void setScoClassifMatNiv2(ScoClassifMatNiv2 scoClassifMatNiv2) {
		this.scoClassifMatNiv2 = scoClassifMatNiv2;
	}

	public ScoClassifMatNiv3 getScoClassifMatNiv3() {
		return scoClassifMatNiv3;
	}

	public void setScoClassifMatNiv3(ScoClassifMatNiv3 scoClassifMatNiv3) {
		this.scoClassifMatNiv3 = scoClassifMatNiv3;
	}

	public ScoClassifMatNiv4 getScoClassifMatNiv4() {
		return scoClassifMatNiv4;
	}

	public void setScoClassifMatNiv4(ScoClassifMatNiv4 scoClassifMatNiv4) {
		this.scoClassifMatNiv4 = scoClassifMatNiv4;
	}

	public ScoClassifMatNiv5 getScoClassifMatNiv5() {
		return scoClassifMatNiv5;
	}

	public void setScoClassifMatNiv5(ScoClassifMatNiv5 scoClassifMatNiv5) {
		this.scoClassifMatNiv5 = scoClassifMatNiv5;
	}

	public String getDescricaoNiv1() {
		return descricaoNiv1;
	}

	public void setDescricaoNiv1(String descricaoNiv1) {
		this.descricaoNiv1 = descricaoNiv1;
	}

	public String getDescricaoNiv2() {
		return descricaoNiv2;
	}

	public void setDescricaoNiv2(String descricaoNiv2) {
		this.descricaoNiv2 = descricaoNiv2;
	}

	public String getDescricaoNiv3() {
		return descricaoNiv3;
	}

	public void setDescricaoNiv3(String descricaoNiv3) {
		this.descricaoNiv3 = descricaoNiv3;
	}

	public String getDescricaoNiv4() {
		return descricaoNiv4;
	}

	public void setDescricaoNiv4(String descricaoNiv4) {
		this.descricaoNiv4 = descricaoNiv4;
	}

	public String getDescricaoNiv5() {
		return descricaoNiv5;
	}

	public void setDescricaoNiv5(String descricaoNiv5) {
		this.descricaoNiv5 = descricaoNiv5;
	}

	public int getNivelExcluir() {
		return nivelExcluir;
	}

	public void setNivelExcluir(int nivelExcluir) {
		this.nivelExcluir = nivelExcluir;
	}

	public boolean isEditandoNiv1() {
		return editandoNiv1;
	}

	public void setEditandoNiv1(boolean editandoNiv1) {
		this.editandoNiv1 = editandoNiv1;
	}

	public boolean isEditandoNiv2() {
		return editandoNiv2;
	}

	public void setEditandoNiv2(boolean editandoNiv2) {
		this.editandoNiv2 = editandoNiv2;
	}

	public boolean isEditandoNiv3() {
		return editandoNiv3;
	}

	public void setEditandoNiv3(boolean editandoNiv3) {
		this.editandoNiv3 = editandoNiv3;
	}

	public boolean isEditandoNiv4() {
		return editandoNiv4;
	}

	public void setEditandoNiv4(boolean editandoNiv4) {
		this.editandoNiv4 = editandoNiv4;
	}

	public boolean isEditandoNiv5() {
		return editandoNiv5;
	}

	public void setEditandoNiv5(boolean editandoNiv5) {
		this.editandoNiv5 = editandoNiv5;
	}


	public List<ScoClassifMatNiv1> getListaScoClassifMatNiv1() {
		return listaScoClassifMatNiv1;
	}


	public void setListaScoClassifMatNiv1(List<ScoClassifMatNiv1> listaScoClassifMatNiv1) {
		this.listaScoClassifMatNiv1 = listaScoClassifMatNiv1;
	}
}