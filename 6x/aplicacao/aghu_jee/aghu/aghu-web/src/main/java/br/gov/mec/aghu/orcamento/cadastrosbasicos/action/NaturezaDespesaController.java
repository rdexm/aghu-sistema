package br.gov.mec.aghu.orcamento.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class NaturezaDespesaController extends ActionController {

	private static final long serialVersionUID = 6311945254533755902L;

	private static final String NATUREZA_DESPESA_LIST = "naturezaDespesaList";

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	
	private FsoNaturezaDespesa naturezaDespesa;

	
	// parâmetro para consulta (somente visualização)
	private boolean visualizacaoRegistro;
	private boolean alteracaoRegistro;

	// listas de associacao
	private List<ScoSolicitacaoDeCompra> listaSolCompras = new ArrayList<ScoSolicitacaoDeCompra>();;
	private List<ScoSolicitacaoServico> listaSolServico = new ArrayList<ScoSolicitacaoServico>();
	
	// controle das modais
	private Boolean existeSolCompras;
	private Boolean existeSolServico;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if (naturezaDespesa != null && naturezaDespesa.getId() != null && naturezaDespesa.getId().getGndCodigo() != null) {
			naturezaDespesa = cadastrosBasicosOrcamentoFacade.obterNaturezaDespesa(naturezaDespesa.getId());

			if(naturezaDespesa == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			this.listaSolCompras = this.solicitacaoComprasFacade.buscarSolicitacaoCompraAssociadaNaturezaDespesa(naturezaDespesa.getId(), false);
			this.listaSolServico = this.solicitacaoServicoFacade.buscarSolicitacaoServicoAssociadaNaturezaDespesa(naturezaDespesa.getId(), false);
			
		} else {
			limpar();
		}
		
		return null;
	
	}
	
	public String gravar() {
		try {
			if (this.naturezaDespesa != null) {
				
				if (!this.isAlteracaoRegistro()) {
					naturezaDespesa.setContaPatrimonial(null);
					cadastrosBasicosOrcamentoFacade.inserirNaturezaDespesa(this.naturezaDespesa);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NATUREZA_DESPESA_M01", naturezaDespesa.getDescricao());
                    return NATUREZA_DESPESA_LIST;
					
				} else {
					cadastrosBasicosOrcamentoFacade.alterarNaturezaDespesa(this.naturezaDespesa);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NATUREZA_DESPESA_M02", naturezaDespesa.getDescricao());
				}
			}
			
			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	
	public String confirmaGravacao() {
		if (this.listaSolCompras.size() > 0) {
			this.setExisteSolCompras(true);
		}
		if (this.listaSolServico.size() > 0) {
			this.setExisteSolServico(true);
		}
		if (this.listaSolCompras.isEmpty() && this.listaSolServico.isEmpty()) {
			return this.gravar();
		}
		
		return null;
	}
	
	public String cancelar() {
		this.limpar();
		return NATUREZA_DESPESA_LIST;
	}

	public List<FsoGrupoNaturezaDespesa> pesquisarGrupoNaturezaPorCodigoEDescricao(final String parametro) throws BaseException {
		return cadastrosBasicosOrcamentoFacade.pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(parametro);
	}
	
	public void atualizarCodNat(){
		naturezaDespesa.getId().setCodigo(null);
		
		if(naturezaDespesa.getGrupoNaturezaDespesa() != null){
			naturezaDespesa.getId().setGndCodigo(naturezaDespesa.getGrupoNaturezaDespesa().getCodigo());
		}
	}

	public void validarCodigoJaUtilizado() {
		if (!this.isAlteracaoRegistro() && naturezaDespesa.getId().getCodigo() != null) {
			FsoNaturezaDespesa natDespesa = cadastrosBasicosOrcamentoFacade.obterNaturezaDespesa(
						new FsoNaturezaDespesaId(naturezaDespesa.getGrupoNaturezaDespesa().getCodigo(), naturezaDespesa.getId().getCodigo()));
			
			if (natDespesa != null) {
				this.apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_NATUREZA_DESPESA_M11",
						natDespesa.getId().getCodigo(), natDespesa.getDescricao(), natDespesa.getIndSituacao().getDescricao());
			}
		}
	}
	
	private void limpar() {
		naturezaDespesa = new FsoNaturezaDespesa();
		naturezaDespesa.setId(new FsoNaturezaDespesaId());
		naturezaDespesa.setIndSituacao(DominioSituacao.A);
		this.alteracaoRegistro = false;
		this.visualizacaoRegistro = false;
		this.existeSolCompras = Boolean.FALSE;
		this.existeSolServico = Boolean.FALSE;
        this.listaSolCompras = new ArrayList<ScoSolicitacaoDeCompra>();
        this.listaSolServico = new ArrayList<ScoSolicitacaoServico>();
	}
	
	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	public boolean isVisualizacaoRegistro() {
		return visualizacaoRegistro;
	}

	public void setVisualizacaoRegistro(boolean visualizacaoRegistro) {
		this.visualizacaoRegistro = visualizacaoRegistro;
	}

	public boolean isAlteracaoRegistro() {
		return alteracaoRegistro;
	}

	public void setAlteracaoRegistro(boolean alteracaoRegistro) {
		this.alteracaoRegistro = alteracaoRegistro;
	}

	public Boolean getExisteSolCompras() {
		return existeSolCompras;
	}

	public void setExisteSolCompras(Boolean existeSolCompras) {
		this.existeSolCompras = existeSolCompras;
	}

	public Boolean getExisteSolServico() {
		return existeSolServico;
	}

	public void setExisteSolServico(Boolean existeSolServico) {
		this.existeSolServico = existeSolServico;
	}

	public ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	} 

	public void setSolicitacaoServicoFacade(ISolicitacaoServicoFacade solicitacaoServicoFacade) {
		this.solicitacaoServicoFacade = solicitacaoServicoFacade;
	}

	public List<ScoSolicitacaoDeCompra> getListaSolCompras() {
		return listaSolCompras;
	}

	public void setListaSolCompras(List<ScoSolicitacaoDeCompra> listaSolCompras) {
		this.listaSolCompras = listaSolCompras;
	}

	public List<ScoSolicitacaoServico> getListaSolServico() {
		return listaSolServico;
	}

	public void setListaSolServico(List<ScoSolicitacaoServico> listaSolServico) {
		this.listaSolServico = listaSolServico;
	}
}