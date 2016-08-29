package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ProcedimentosEspeciaisPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -1720545574709576225L;

	private static final String PAGE_PROCEDIMENTOS_ESPECIAIS_CRUD = "procedimentosEspeciaisCrud";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	// Campos filtro
	private Integer codigo;
	private String descricao;
	private DominioSituacao indSituacao;

	@Inject @Paginator
	private DynamicDataModel<MpmProcedEspecialDiversos> dataModel;

	private MpmProcedEspecialDiversos parametroSelecionado;
	//Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;

	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoNovo = true;
		
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		// Limpa filtro
		this.codigo = null;
		this.descricao = null;
		this.indSituacao = null;
		this.getDataModel().limparPesquisa();
		this.exibirBotaoNovo = false;
	}

	@Override
	public List<MpmProcedEspecialDiversos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		// Cria objeto com os parâmetros para busca
		MpmProcedEspecialDiversos elemento = new MpmProcedEspecialDiversos(this.codigo == null ? null : this.codigo.shortValue(), this.descricao, this.indSituacao);
		return this.cadastrosBasicosPrescricaoMedicaFacade.pesquisarProcedimentoEspecial(firstResult, maxResult, MpmProcedEspecialDiversos.Fields.DESCRICAO.toString(), true,
				elemento);
	}

	@Override
	public Long recuperarCount() {
		// Cria objeto com os parâmetros para busca
		MpmProcedEspecialDiversos elemento = new MpmProcedEspecialDiversos(this.codigo == null ? null : this.codigo.shortValue(), this.descricao, this.indSituacao);
		return this.cadastrosBasicosPrescricaoMedicaFacade.pesquisarCount(elemento);
	}

	public void excluir() {
		try {
			MpmProcedEspecialDiversos procedimento = this.cadastrosBasicosPrescricaoMedicaFacade.obterProcedimentoEspecialPeloId(this.parametroSelecionado.getSeq());
			// Valida se o procedimento não está sendo usado
			boolean procedimentoEmUsoPrescricao = this.prescricaoMedicaFacade.verificaProcedimentosEspeciasPrescritos(procedimento.getSeq());
			boolean procedimentoEmUsoModeloBasico = this.prescricaoMedicaFacade.verificaProcedimentoEspecialEmModeloBasico(procedimento);
			boolean procedimentoEmUso = procedimentoEmUsoPrescricao || procedimentoEmUsoModeloBasico;
			if (procedimentoEmUso) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_REMOCAO_PROCEDIMENTO_ESPECIAL_EM_USO", procedimento.getDescricao());
			} else {
				this.cadastrosBasicosPrescricaoMedicaFacade.removerProcedimentoEspecial(procedimento.getSeq());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_PROCEDIMENTO_ESPECIAL", procedimento.getDescricao());
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public String inserirEditar() {
		return PAGE_PROCEDIMENTOS_ESPECIAIS_CRUD;
	}

	/*
	 * Getters and Setters
	 */

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DynamicDataModel<MpmProcedEspecialDiversos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmProcedEspecialDiversos> dataModel) {
		this.dataModel = dataModel;
	}

	public MpmProcedEspecialDiversos getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MpmProcedEspecialDiversos parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
	
	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
}