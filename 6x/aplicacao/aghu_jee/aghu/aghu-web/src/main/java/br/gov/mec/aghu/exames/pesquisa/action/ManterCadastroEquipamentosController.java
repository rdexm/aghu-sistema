package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;



public class ManterCadastroEquipamentosController  extends ActionController {

	private static final long serialVersionUID = 9130455978081098619L;

	@EJB
	private IAghuFacade aghuFacade;

	private AghUnidadesFuncionais unidadeExecutora;

	private List<AelEquipamentos>  listaEquipamentos;

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private Short seq;
	
	private Short seqExclusao;

	private AelEquipamentos aelEquipamentos;

	private Boolean exibirCadastro = false;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void inicio() {
	 


		if(unidadeExecutora ==null){
			// Reseta instâncias
			this.exibirCadastro = false;

		}else{
			pesquisarListaEquipamentos();
		}
		this.aelEquipamentos = new AelEquipamentos();
		this.setValoresDefault();
	
	}

	private void setValoresDefault() {
		this.aelEquipamentos.setCargaAutomatica(Boolean.TRUE);
		this.aelEquipamentos.setPossuiInterface(Boolean.TRUE);
		this.aelEquipamentos.setSituacao(DominioSituacao.A);
	}

	/**
	 * Pesquisa lista de equipamentos. 
	 * Chamado automaticamente na acao "posSelectionAction" da Sugestion Box de unidade executora
	 */
	public void pesquisarListaEquipamentos() {
		try {
			this.exibirCadastro = true;
			this.limpar();
			this.listaEquipamentos = this.pesquisaExamesFacade.pesquisaListaEquipamentos(this.unidadeExecutora);

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa){
		return this.aghuFacade.pesquisarUnidExecPorCodDescCaractExames(objPesquisa);
	}	

	/**
	 * Metodo para limpeza da suggestion box de unidade executora
	 */
	public void limparAghUnidadesFuncionaisExecutoras(){
		this.unidadeExecutora = null;
		this.listaEquipamentos = null;
		this.exibirCadastro = false;

	}


	/**
	 * Edita o registro
	 * @param AelEquipamentos
	 */
	public void editar(final Short seq) {
		this.seq = seq;
		Enum[] fetchArgsInnerJoin = {AelEquipamentos.Fields.SERVIDOR};
		Enum[] fetchArgsLeftJoin = null;
		aelEquipamentos = cadastrosApoioExamesFacade.obterAelEquipamentosId(seq, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	public void limpar() {
		this.seqExclusao = null;
		this.seq = null;
		this.aelEquipamentos = new AelEquipamentos();
		this.setValoresDefault();
	}

	/**
	 * Confirma operação
	 */
	public void confirmar() {

		try {
			
			this.aelEquipamentos.setUnidadeFuncional(unidadeExecutora);
			
			if (this.aelEquipamentos.getSeq() == null) {
				this.cadastrosApoioExamesFacade.inserirEquipamentos(aelEquipamentos);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_EQUIPAMENTOS", aelEquipamentos.getDescricao());

			} else {
				this.cadastrosApoioExamesFacade.atualizarEquipamentos(aelEquipamentos);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_EQUIPAMENTOS", aelEquipamentos.getDescricao());

			} 
			
			this.limpar();
			this.pesquisarListaEquipamentos();

		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicao() {
		this.limpar();
	}

	public void excluir() {
	
		try {
			if(aelEquipamentos == null){
				apresentarMsgNegocio(Severity.ERROR, "REGISTRO_NULO_EXCLUSAO");
				return;
			}
			
			String descricaoEquip = aelEquipamentos.getDescricao(); 
			cadastrosApoioExamesFacade.removerEquipamento(aelEquipamentos);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_EQUIPAMENTOS", descricaoEquip);

		}catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);

		}finally{
			this.seqExclusao = null;
		}
		this.pesquisarListaEquipamentos();
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public List<AelEquipamentos> getListaEquipamentos() {
		return listaEquipamentos;
	}

	public void setListaEquipamentos(List<AelEquipamentos> listaEquipamentos) {
		this.listaEquipamentos = listaEquipamentos;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public AelEquipamentos getAelEquipamentos() {
		return aelEquipamentos;
	}

	public void setAelEquipamentos(AelEquipamentos aelEquipamentos) {
		this.aelEquipamentos = aelEquipamentos;
	}

	public Boolean getExibirCadastro() {
		return exibirCadastro;
	}

	public void setExibirCadastro(Boolean exibirCadastro) {
		this.exibirCadastro = exibirCadastro;
	}

	public Short getSeqExclusao() {
		return seqExclusao;
	}

	public void setSeqExclusao(Short seqExclusao) {
		this.seqExclusao = seqExclusao;
	}
}