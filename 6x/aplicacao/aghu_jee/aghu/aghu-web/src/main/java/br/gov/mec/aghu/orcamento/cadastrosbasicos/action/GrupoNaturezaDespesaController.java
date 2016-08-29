package br.gov.mec.aghu.orcamento.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoNaturezaDespesaController extends ActionController {

	private static final long serialVersionUID = -6550312158533877073L;

	private static final String GRUPO_NATUREZA_DESPESA_LIST = "grupoNaturezaDespesaList";

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

	private FsoGrupoNaturezaDespesa entidade;

	private Boolean isUpdate;
	private Boolean isReadonly;
	private Boolean isConfirmGravarRequired = false;
	private Boolean permiteAlteracaoDescricao;
	private boolean iniciouTela = false;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(!iniciouTela){
			if(entidade != null && entidade.getCodigo() != null){
				entidade = cadastrosBasicosOrcamentoFacade.obterGrupoNaturezaDespesa(entidade.getCodigo());
	
				if(entidade == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelar();
				}
				
				permiteAlteracaoDescricao = this.possuiNaturezasDespesaAssociadas();
				
			} else {
				entidade = new FsoGrupoNaturezaDespesa();
				entidade.setIndSituacao(DominioSituacao.A);
				isUpdate = false;
				isReadonly = false;
			}
			
			iniciouTela = true; 
		}
		return null;
	
	}
	
	public String verificarESalvar() {
		if (getIsUpdate() && possuiNaturezasDespesaAssociadas()) {
			setIsConfirmGravarRequired(true);
            openDialog("confirmGravarModalWG");
			return null;
		}
		
		return salvar();
	}

	/**
	 * Salva grupo de natureza de despesa.
	 */
	public String salvar() {
		setIsConfirmGravarRequired(false);
		
		if (entidade.getDescricao() != null) {
			entidade.setDescricao(StringUtils.trimToNull(entidade.getDescricao()));
		}

		try {
			if (getIsUpdate()) {
				cadastrosBasicosOrcamentoFacade.alterar(entidade);
				apresentarMsgNegocio(Severity.INFO, "GRUPO_NATUREZA_DESPESA_ALTERADO_SUCESSO", entidade.getDescricao());
			} else {
				cadastrosBasicosOrcamentoFacade.incluir(entidade);
				apresentarMsgNegocio(Severity.INFO,"GRUPO_NATUREZA_DESPESA_GRAVADO_SUCESSO", entidade.getDescricao());
			}

			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	/**
	 * Verifica se possui uma ou mais naturezas de despesa associadas.
	 * 
	 * @return Possui (true) ou não (false).
	 */
	private boolean possuiNaturezasDespesaAssociadas() {
		Long count = cadastrosBasicosOrcamentoFacade.countPesquisaListaNaturezaDespesa(entidade, null, null);
		return count.intValue() > 0;
	}
	
	public String cancelar() {
		iniciouTela = false;
		entidade = null;
		permiteAlteracaoDescricao = false;
		return GRUPO_NATUREZA_DESPESA_LIST;
	}

	public FsoGrupoNaturezaDespesa getEntidade() {
		return entidade;
	}

	public void setEntidade(FsoGrupoNaturezaDespesa entidade) {
		this.entidade = entidade;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public Boolean getIsReadonly() {
		return isReadonly;
	}

	public void setIsReadonly(Boolean isReadonly) {
		this.isReadonly = isReadonly;
	}

	public Boolean getIsConfirmGravarRequired() {
		return isConfirmGravarRequired;
	}

	public void setIsConfirmGravarRequired(Boolean isConfirmGravarRequired) {
		this.isConfirmGravarRequired = isConfirmGravarRequired;
	}

	public Boolean getPermiteAlteracaoDescricao() {
		return permiteAlteracaoDescricao;
	}

	public void setPermiteAlteracaoDescricao(Boolean permiteAlteracaoDescricao) {
		this.permiteAlteracaoDescricao = permiteAlteracaoDescricao;
	}
}