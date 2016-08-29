package br.gov.mec.aghu.orcamento.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ParametrosOrcamentoController extends ActionController {

	private static final long serialVersionUID = 2604379193337908273L;

	private static final String PARAMETROS_ORCAMENTO_LIST = "parametrosOrcamentoList";

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	
	private FsoParametrosOrcamento entidade;
	private Integer seqFsoParametrosOrcamento;

	private Boolean isUpdate;
	private Boolean isReadonly;
	private Boolean isCopia;
	private boolean iniciouTela;

	private static final Enum[] fetchArgsLeftJoin = {FsoParametrosOrcamento.Fields.GRUPO_MATERIAL, FsoParametrosOrcamento.Fields.MATERIAL, 
												 	 FsoParametrosOrcamento.Fields.GRUPO_SERVICO, FsoParametrosOrcamento.Fields.SERVICO,
												 	 FsoParametrosOrcamento.Fields.CENTRO_CUSTO, FsoParametrosOrcamento.Fields.GRUPO_NATUREZA_DESPESA,
												 	 FsoParametrosOrcamento.Fields.NATUREZA_DESPESA, FsoParametrosOrcamento.Fields.VERBA_GESTAO,
												 	 FsoParametrosOrcamento.Fields.CENTRO_CUSTO_REFERENCIA};
	
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar(){
	 

		if(!iniciouTela){
			if(seqFsoParametrosOrcamento != null){
				entidade = cadastrosBasicosOrcamentoFacade.obterParametrosOrcamento(seqFsoParametrosOrcamento, null, fetchArgsLeftJoin);

				if(entidade == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelar();
				}
				
				seqFsoParametrosOrcamento = null;
				
				
			} else {
				entidade = new FsoParametrosOrcamento();
				entidade.setTpProcesso(DominioTipoSolicitacao.SC);
				entidade.setIndSituacao(DominioSituacao.A);
			}
			iniciouTela = true;
		}
		return null;
	
	}
	
	
	/**
	 * Pesquisa grupos de materiais pelo código ou descrição da suggestion.
	 */
	public List<ScoGrupoMaterial> pesquisarGruposMateriais(String parametro) {
		return comprasFacade.pesquisarGrupoMaterialPorCodigoDescricao(parametro);
	}

	/**
	 * Pesquisa materiais pelo código ou descrição da suggestion.
	 */
	public List<ScoMaterial> pesquisarMateriais(String objPesquisa) {
		return comprasFacade.listarScoMateriais(objPesquisa, null);
	}

	/**
	 * Pesquisa centros de cuso pelo código ou descrição da suggestion.
	 */
	public List<FccCentroCustos> pesquisarCentrosCusto(String objPesquisa) {
		return centroCustoFacade.pesquisarCentroCustos(objPesquisa);
	}


	/**
	 * Obtem grupos de natureza de despesa.
	 */
	public List<FsoGrupoNaturezaDespesa> pesquisarGruposNaturezaDespesa(String filter) {
		return cadastrosBasicosOrcamentoFacade.pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(filter);
	}

	/**
	 * Limpa natureza de despesa quando grupo nulo.
	 */
	public void refreshFromGrupoNaturezaDespesa() {
		if (entidade.getGrupoNaturezaDespesa() == null) {
			entidade.setAcaoNtd(null);
			entidade.setNaturezaDespesa(null);
			entidade.setIsCadastradaGrupo(null);
		}
	}

	/**
	 * Obtem naturezas de despesa pelo grupo já selecionado.
	 */
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesa(String filter) {
		return cadastrosBasicosOrcamentoFacade.pesquisarNaturezasDespesa(entidade.getGrupoNaturezaDespesa(), filter);
	}

	/**
	 * Obtem verbas de gestão a partir do código ou descrição.
	 */
	public List<FsoVerbaGestao> pesquisarVerbasGestao(String filter) {
		return cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoPorSeqOuDescricao(filter);
	}

	/**
	 * Quando marcada natureza de despesa como cadastrada no grupo, então anula ação e natureza.
	 */
	public void refreshFromIsCadastradaGrupo() {
		if (Boolean.TRUE.equals(entidade.getIsCadastradaGrupo())) {
			entidade.setNaturezaDespesa(null);
			if (DominioTipoSolicitacao.SS.equals(entidade.getTpProcesso())){
				entidade.setGrupoNaturezaDespesa(null);				
			} 
		}
	}

	/**
	 * Zera valor se limite anulado.
	 */
	public void refreshFromTpLimite() {
		if (entidade.getTpLimite() == null) {
			entidade.setVlrLimitePatrimonio(null);
		}
	}

	/**
	 * Anula grupo de natureza se ação correspondente nula.
	 */
	public void refreshFromAcaoGnd() {
		if (entidade.getAcaoGnd() == null) {
			entidade.setGrupoNaturezaDespesa(null);
			entidade.setAcaoNtd(null);
			entidade.setNaturezaDespesa(null);
			entidade.setIsCadastradaGrupo(null);
		}
	}

	/**
	 * Anula natureza se ação correspondente nula.
	 */
	public void refreshFromAcaoNtd() {
		if (entidade.getAcaoNtd() == null) {
			entidade.setNaturezaDespesa(null);
		}
	}

	/**
	 * Anula verba de gestão se ação correspondente nula.
	 */
	public void refreshFromAcaoVbg() {
		if (entidade.getAcaoVbg() == null) {
			entidade.setVerbaGestao(null);
		}
	}
	
	/**
	 * Anula centro custo referência se centro custo definido.
	 */
	public void refreshFromCentroCusto() {
		if (entidade.getCentroCusto() != null) {
			entidade.setAcaoCct(null);
			entidade.setCentroCustoReferencia(null);
		}
	}
	
	/**
	 * Anula centro custo referência se ação nula.
	 */
	public void refreshFromAcaoCct() {
		if (entidade.getAcaoCct() == null) {
			entidade.setCentroCustoReferencia(null);
		}
	}
	
	/**
	 * Salva registro e retorna a lista.
	 */
	public String salvar() {
		
		try {
			if (getIsUpdate()) {
				cadastrosBasicosOrcamentoFacade.alterar(entidade);
				apresentarMsgNegocio(Severity.INFO,"PARAMETRO_ORCAMENTO_ALTERADO_SUCESSO", entidade.getSeq());

			} else {
				cadastrosBasicosOrcamentoFacade.incluir(entidade);
				apresentarMsgNegocio(Severity.INFO, "PARAMETRO_ORCAMENTO_GRAVADO_SUCESSO", entidade.getSeq());
			}

			if (getIsCopia()){
				// Quando esta sendo realizada a cópia de uma regra deve-se permanecer na página de edição/inclusão
				// Tela volta à condição de edição de Regra Orçamentária
				setIsUpdate(Boolean.TRUE);
				setIsCopia(Boolean.FALSE);
				return null;
			}
			
			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	/**
	 * Gera um novo Parametro Orcamentario (FsoParametrosOrcamento) copiando os dados do parametro original
	 * alterando apenas seus campos ServidorInclusao, com o usuário logado no sistema, e Seq = null
	 * A tela estava no estado de edição de um registro passa para o modo de inclusão.
	 */
	public void copiar(){
		if (getIsUpdate()){
			Integer numRegraOriginal = entidade.getSeq();
			String nomeRegraOriginal = entidade.getRegra();
			try {
				FsoParametrosOrcamento entidadeCopia = cadastrosBasicosOrcamentoFacade.clonarParametroOrcamento(entidade);

				entidade = entidadeCopia;
				setIsUpdate(Boolean.FALSE);
				setIsCopia(Boolean.TRUE);
				
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_COPIAR", numRegraOriginal, nomeRegraOriginal);			
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public String cancelar() {
		entidade = null;
		iniciouTela= false;
		seqFsoParametrosOrcamento = null;
		return PARAMETROS_ORCAMENTO_LIST;
	}

	/**
	 * Limpa valores de grupos de material e serviço, serviço e material
	 * conforme aplicação selecionada.
	 */
	public void refreshFromAplicacao() {
		entidade.setAcaoNtd(null);
		entidade.setAcaoGnd(null);
		entidade.setNaturezaDespesa(null);
		entidade.setGrupoNaturezaDespesa(null);
		entidade.setIsCadastradaGrupo(null);
		
		if (!DominioTipoSolicitacao.SC.equals(entidade.getTpProcesso())) {
			entidade.setIndGrupo(null);
			entidade.setGrupoMaterial(null);
			entidade.setMaterial(null);
		}

		if (!DominioTipoSolicitacao.SS.equals(entidade.getTpProcesso())) {
			entidade.setGrupoServico(null);
			entidade.setServico(null);
		}
	}
	
	/**
	 * Pesquisa grupos de serviço pelo código ou descrição.
	 * 
	 * @param filter Código ou descrição do grupo de serviço.
	 * @return Grupos de serviço encontrados.
	 */
	public List<ScoGrupoServico> pesquisarGruposServicos(String filter) {
		return comprasFacade.listarGrupoServico(filter);
	}
	
	/**
	 * Pesquisa serviços pelo código ou descrição da suggestion.
	 * 
	 * @param filter Código ou descrição do serviço.
	 * @return Serviços.
	 */
	public List<ScoServico> pesquisarServicos(String filter) {
		return comprasFacade.listarServicosAtivos(filter);
	}
	
	public String getCadastradaGrupoLabel() {
		switch (entidade.getTpProcesso()) {
			case SC: return super.getBundle().getString("LABEL_CADASTRADA_GRUPO_MATERIAL");
			case SS: return super.getBundle().getString("LABEL_CADASTRADA_SERVICO");
			default: return null;
		}
	}
	
	public String getCadastradaGrupoTitle() {
		switch (entidade.getTpProcesso()) {
			case SC: return super.getBundle().getString("TITLE_CADASTRADA_GRUPO_MATERIAL");
			case SS: return super.getBundle().getString("TITLE_CADASTRADA_SERVICO");
			default: return null;
		}
	}

	public FsoParametrosOrcamento getEntidade() {
		return entidade;
	}

	public void setEntidade(FsoParametrosOrcamento entidade) {
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

	public Boolean getIsCopia() {
		if (isCopia == null){
			setIsCopia(false);
		}
		return isCopia;
	}

	public void setIsCopia(Boolean isCopia) {
		this.isCopia = isCopia;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}

	public Integer getSeqFsoParametrosOrcamento() {
		return seqFsoParametrosOrcamento;
	}

	public void setSeqFsoParametrosOrcamento(Integer seqFsoParametrosOrcamento) {
		this.seqFsoParametrosOrcamento = seqFsoParametrosOrcamento;
	}
}