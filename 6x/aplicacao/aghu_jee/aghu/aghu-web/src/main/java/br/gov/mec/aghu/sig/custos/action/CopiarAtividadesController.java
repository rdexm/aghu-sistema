package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class CopiarAtividadesController extends ActionController {

	private static final String MANTER_ATIVIDADES = "manterAtividades";

	private static final String PESQUISAR_ATIVIDADES = "pesquisarAtividades";
	
	@Inject
	private ManterAtividadesController manterAtividadesController;

	public enum CopiarAtividadeExceptionCode implements BusinessExceptionCode {
		MSG_COPIA_NENHUM_ITEM_SELECIONADO,
		MENSAGEM_JA_EXISTE_CENTRO_CUSTO
	}

	private static final long serialVersionUID = -6849617106040072872L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	private SigAtividades atividadesSuggestion;

	private FccCentroCustos fccCentroCustos;

	private String nome;

	private Integer seqFccCentroCustos;
	
	private SigAtividades atividadeCopia;
	
	private boolean pessoal;
	private boolean insumos;
	private boolean equipamentos;
	private boolean servicos;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		this.pessoal = this.insumos = this.equipamentos = this.servicos = false;
		this.nome = "";
		this.fccCentroCustos = this.centroCustoFacade.obterCentroCustoPorChavePrimaria(this.seqFccCentroCustos);
	
	}

	public List<SigAtividades> pesquisarComposicao(String paramPesquisa) throws BaseException {
		return this.custosSigFacade.listAtividadesAtivas(fccCentroCustos, paramPesquisa);
	}

	public String copiarAtividade() {
		try {
			if (!this.pessoal && !this.insumos && !this.equipamentos && !this.servicos) {
				throw new ApplicationBusinessException(CopiarAtividadeExceptionCode.MSG_COPIA_NENHUM_ITEM_SELECIONADO);
			}
			
			if (!verificaSePodeGravar(this.nome, this.fccCentroCustos)) {
				throw new ApplicationBusinessException(CopiarAtividadeExceptionCode.MENSAGEM_JA_EXISTE_CENTRO_CUSTO, this.fccCentroCustos.getDescricao());
				
			}
			this.atividadeCopia = this.custosSigFacade.copiaAtividade(this.atividadesSuggestion, this.nome, this.pessoal, this.insumos, this.equipamentos,
					this.servicos);

			this.apresentarMsgNegocio(Severity.INFO, "MSG_COPIA_ATIVIDADE_SUCESSO",
					this.atividadesSuggestion.getNome(), this.nome);
			this.atividadesSuggestion = null;
			this.manterAtividadesController.setSeqAtividade(atividadeCopia.getSeq());
			return MANTER_ATIVIDADES;
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public boolean verificaSePodeGravar(String nome, FccCentroCustos fccCentroCustos) {
		boolean retorno = true;
		List<SigAtividades> listaAtividades = custosSigFacade.pesquisarAtividades(fccCentroCustos);
		if (listaAtividades == null || listaAtividades.size() == 0) {
			retorno = true;
			return retorno;
		}
		for (SigAtividades sigAtividades : listaAtividades) {
			if (nome.equalsIgnoreCase(sigAtividades.getNome())) {
				retorno = false;
				break;
			}
		}
		return retorno;
	}

	public String voltar() {
		this.atividadesSuggestion = null;
		return PESQUISAR_ATIVIDADES;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public SigAtividades getAtividadesSuggestion() {
		return atividadesSuggestion;
	}

	public void setAtividadesSuggestion(SigAtividades atividadesSuggestion) {
		this.atividadesSuggestion = atividadesSuggestion;
	}

	public Integer getSeqFccCentroCustos() {
		return seqFccCentroCustos;
	}

	public void setSeqFccCentroCustos(Integer seqFccCentroCustos) {
		this.seqFccCentroCustos = seqFccCentroCustos;
	}

	public boolean isServicos() {
		return servicos;
	}

	public void setServicos(boolean servicos) {
		this.servicos = servicos;
	}

	public boolean isEquipamentos() {
		return equipamentos;
	}

	public void setEquipamentos(boolean equipamentos) {
		this.equipamentos = equipamentos;
	}

	public boolean isInsumos() {
		return insumos;
	}

	public void setInsumos(boolean insumos) {
		this.insumos = insumos;
	}

	public boolean isPessoal() {
		return pessoal;
	}

	public void setPessoal(boolean pessoal) {
		this.pessoal = pessoal;
	}

	public SigAtividades getAtividadeCopia() {
		return atividadeCopia;
	}

	public void setAtividadeCopia(SigAtividades atividadeCopia) {
		this.atividadeCopia = atividadeCopia;
	}
}
