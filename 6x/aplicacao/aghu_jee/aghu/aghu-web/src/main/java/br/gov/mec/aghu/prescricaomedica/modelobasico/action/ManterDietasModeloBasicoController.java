package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioRestricao;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.MpmItemModeloBasicoDietaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterDietasModeloBasicoController extends ActionController {

	private static final long serialVersionUID = 4086025678241245158L;
	
	private static final String PAGE_MANTER_ITENS_MODELO_BASICO = "manterItensModeloBasico";

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	// chave do modelo de dieta recebida via parametro
	private Integer modeloBasicoPrescricaoSeq;
	private Integer seq;
	// para completar a chave do item na alteração e exclusão
	private Integer tipoItemDietaSeq;

	private MpmModeloBasicoPrescricao modeloBasico;

	private MpmModeloBasicoDieta modeloBasicoDieta;
	private String observacao;

	private MpmItemModeloBasicoDieta itemDieta;

	private AnuTipoItemDieta tipoItemDieta; // Utilizado na Suggestion de busca
											// de tipos de item de dieta
	private Boolean altera = false;

	private BigDecimal quantidade;
	
	private String unidade;

	private List<MpmItemModeloBasicoDietaVO> listaItemModeloBasicoDieta = new ArrayList<MpmItemModeloBasicoDietaVO>();

	// usado na frequencia
	private Short frequencia;
	private MpmTipoFrequenciaAprazamento tipoAprazamento;
	
	private boolean frequenciaPadrao;

	// Utilizado para Exclusão
	private MpmItemModeloBasicoDietaId mpmItemModeloBasicoDietaId = new MpmItemModeloBasicoDietaId();

	private MpmItemModeloBasicoDieta itemDietaExcluir;
	
	//Campos para o controle de alteração no formulário do item
	private boolean campoAlteradoFormularioItem;
	private Integer tipoItemDietaSeqEdicao;

	private enum ManterDietasModeloBasicoControllerExceptionCode implements
		BusinessExceptionCode {
		MENSAGEM_MODELO_NAO_INFORMADO, MENSAGEM_ITEM_NAO_INFORMADO, MENSAGEM_ITEM_NAO_ENCONTRADO, MENSAGEM_ALTERACOES_PENDENTES, MENSAGEM_TIPO_NAO_EXISTE;
	}
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void iniciar() {
	 

		try {
			if (modeloBasicoPrescricaoSeq == null) {
				throw new ApplicationBusinessException(
						ManterDietasModeloBasicoControllerExceptionCode.MENSAGEM_MODELO_NAO_INFORMADO);
			}
			this.modeloBasico = this.modeloBasicoFacade
					.obterModeloBasico(modeloBasicoPrescricaoSeq);

			// verifica se é inclusão ou alteração
			if (seq == null) {
				this.modeloBasicoDieta = new MpmModeloBasicoDieta();
				this.modeloBasicoDieta.setModeloBasicoPrescricao(modeloBasico);
			} else {
				this.pesquisaModeloBasicoDieta();
				this.atualizaListaItensDieta();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e); // trata erro de parâmetro de sistema
		}

		// cria novo item para adicionar
		this.novoItem();
	
	}

	private void novoItem() {
		this.itemDieta = new MpmItemModeloBasicoDieta();
		this.itemDieta.setModeloBasicoDieta(modeloBasicoDieta);
	}

	private void pesquisaModeloBasicoDieta() {
		this.modeloBasicoDieta = modeloBasicoFacade.obterModeloBasicoDieta(
				modeloBasicoPrescricaoSeq, seq);
	}

	private void atualizaListaItensDieta() {
		this.listaItemModeloBasicoDieta = this.modeloBasicoFacade
				.obterListaItensDietaVO(modeloBasicoDieta
						.getModeloBasicoPrescricao().getSeq(),
						modeloBasicoDieta.getId().getSeq());
	}

	public void adicionar() {
		if (this.verificaRequiredFrequencia() && this.frequencia == null) {
			this.apresentarMsgNegocio(Severity.ERROR,
					"CAMPO_OBRIGATORIO", "Freq\u00FC\u00EAncia");
		} else {
			this.itemDieta.setQuantidade(quantidade);
			
			if (!this.altera) {
				incluirItem();
			} else {
				alterar();
			}
		}
	}

	private void incluirItem() {
		try {
			String descricao = itemDieta.getTipoItemDieta().getDescricao();
			this.validaAprazamento();
			this.modeloBasicoFacade.inserir(itemDieta);
			this.atualizaListaItensDieta();
			this.limpar();
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_ADICAO_ITEM_DIETA", descricao);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e); // trata erro de parametro de sistema
		}
	}

	private void alterar() {
		try {
			String descricao = itemDieta.getTipoItemDieta().getDescricao();
			this.validaAprazamento();
			this.modeloBasicoFacade.alterar(itemDieta);
			this.atualizaListaItensDieta();
			this.limpar();
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_ALTERACAO_ITEM_DIETA", descricao);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e); // trata erro de parametro de sistema
		}
	}

	public void excluir() {
		try {
			itemDietaExcluir = this.modeloBasicoFacade.obterItemDieta(
					mpmItemModeloBasicoDietaId.getModeloBasicoPrescricaoSeq(),
					mpmItemModeloBasicoDietaId.getModeloBasicoDietaSeq(),
					mpmItemModeloBasicoDietaId.getTipoItemDietaSeq());
			String desc = itemDietaExcluir.getTipoItemDieta().getDescricao();
			this.modeloBasicoFacade.excluir(itemDietaExcluir);

			this.atualizaListaItensDieta();
			this.limpar();

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_EXCLUSAO_ITEM_DIETA", desc);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e); // trata erro de parametro de sistema
		}
	}

	private void validaAprazamento() {
		itemDieta.setFrequencia(this.frequencia);
		if (this.tipoAprazamento != null) {
			itemDieta.setTipoFrequenciaAprazamento(this.tipoAprazamento);
		}
	}

	public void populaDependencias() {
		
		this.marcarAlteracaoCampoFormularioItem();
		
		this.unidade = null;
		this.quantidade = null;
		if (this.itemDieta != null && this.itemDieta.getTipoItemDieta() != null) {
			
			if(this.itemDieta.getQuantidade() != null){
				this.quantidade = this.itemDieta.getQuantidade();
			}
			
			if (this.itemDieta.getTipoItemDieta().getUnidadeMedidaMedica() != null) {
				this.unidade = this.itemDieta.getTipoItemDieta()
						.getUnidadeMedidaMedica().getDescricao();
			}
			if (this.itemDieta.getTipoItemDieta().getFrequencia() != null) {
				this.frequencia = this.itemDieta.getTipoItemDieta()
						.getFrequencia();
			}
			
			if (this.itemDieta.getTipoItemDieta()
					.getTipoFrequenciaAprazamento() != null) {
				this.tipoAprazamento = this.itemDieta.getTipoItemDieta().getTipoFrequenciaAprazamento();
				this.frequenciaPadrao = true;
			}
		}
	}

	public void gravar() {
		// altera o pai!
		try {
			this.modeloBasicoFacade.alterar(modeloBasicoDieta);
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_GRAVAR_DIETA");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e); // trata erro de parametro de sistema
		}
	}

	public void limpar() {
		this.itemDieta = null;
		this.frequencia = null;
		this.quantidade = null;
		this.unidade = null;
		this.altera = false;
		this.tipoAprazamento = null;
		this.novoItem();
		this.tipoItemDietaSeq = null;
		this.frequenciaPadrao = false;
		desmarcarAlteracaoCampoFormularioItem();
	}
	
	
	public void preparaCancelamentoEdicao() {
		this.limpar();
	}
	
	public void preparaAlterar(Integer modeloBasicoPrescricaoSeq,
			Integer modeloBasicoDietaSeq, Integer tipoItemDietaSeq) {
		
		//Armazena os dados do item alterado
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
		this.seq = modeloBasicoDietaSeq;
		this.tipoItemDietaSeqEdicao = tipoItemDietaSeq;
		
		//Verifica se existem campos alterados e não salvos
		if(!this.isCampoAlteradoFormularioItem()){
			this.editarItem();
		}
		else{
			this.openDialog("modalConfirmacaoEdicaoWG");
		}
	}
	
	public void editarItem() {
		
		//Limpa os campos do formulário de item
		this.limpar();
		
		Integer modeloBasicoPrescricaoSeq =  this.modeloBasicoPrescricaoSeq;
		Integer modeloBasicoDietaSeq =  this.seq;
		Integer tipoItemDietaSeq = this.tipoItemDietaSeqEdicao;
		
		this.tipoItemDietaSeq = tipoItemDietaSeq;
		
		//Somente busca o item quando o seq não for nulo
		if(this.tipoItemDietaSeq != null) {
			try {
				itemDieta = this.modeloBasicoFacade.obterItemDieta(
						modeloBasicoPrescricaoSeq, modeloBasicoDietaSeq,
						tipoItemDietaSeq);
				this.frequencia = this.itemDieta.getFrequencia();
				if (this.itemDieta.getTipoFrequenciaAprazamento() != null) {
					this.tipoAprazamento = this.itemDieta.getTipoFrequenciaAprazamento();
				}
				// popula dependencias
				if (this.itemDieta.getTipoItemDieta()
						.getTipoFrequenciaAprazamento() != null) {
					this.frequenciaPadrao = true;
				} else {
					this.frequenciaPadrao = false;
				}
				
				this.quantidade = null;
				if(this.itemDieta.getQuantidade() != null){
					this.quantidade = this.itemDieta.getQuantidade();
				}
				
				this.unidade = null;
				if (this.itemDieta.getTipoItemDieta().getUnidadeMedidaMedica() != null) {
					this.quantidade = this.itemDieta.getQuantidade();
					this.unidade = this.itemDieta.getTipoItemDieta()
							.getUnidadeMedidaMedica().getDescricao();
				}
				this.altera = true;
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e); // trata erro de parâmetro de sistema
			}
		}
	}

	public String verificaPendencias() {
		
		this.seq = null;
		this.listaItemModeloBasicoDieta = null;
		// se foi tem um item pendente ou foi alterada a observação
		if (this.modeloBasicoFacade.isAlterouDieta(this.modeloBasicoDieta)
				|| (this.itemDieta != null
						&& itemDieta.getTipoItemDieta() != null && itemDieta
						.getTipoItemDieta().getSeq() != null)) {
			this.openDialog("modalConfirmacaoPendenciaWG");
			return null;
		}
		return voltar();
	}

	public String voltar() {
		limpar();
		return PAGE_MANTER_ITENS_MODELO_BASICO;
	}

	public List<AnuTipoItemDieta> obterTiposItemDieta(String itemDietaPesquisa) {
		return this.returnSGWithCount(this.modeloBasicoFacade.obterTiposItemDieta(itemDietaPesquisa),this.modeloBasicoFacade.obterTiposItemDietaCount(itemDietaPesquisa));
	}

	public boolean verificaDisabledQuantidade() {
		// Se tem deita com digitação Opcional ou Obrigatória habilita
		if (itemDieta != null && itemDieta.getTipoItemDieta() != null
				&& itemDieta.getTipoItemDieta().getSeq() != null &&(itemDieta.getTipoItemDieta().getIndDigitaQuantidade() != null
				&& (itemDieta.getTipoItemDieta().getIndDigitaQuantidade()
						.equals(DominioRestricao.O) || itemDieta
						.getTipoItemDieta().getIndDigitaQuantidade()
						.equals(DominioRestricao.C)))) {
			return false;
		}
		return true; // e desabilita
	}

	public boolean verificaRequiredQuantidade() {
		// só se tem item e o indicador de digitação é Obrigatório
		if (itemDieta != null && itemDieta.getTipoItemDieta() != null
				&& itemDieta.getTipoItemDieta().getSeq() != null &&(itemDieta.getTipoItemDieta().getIndDigitaQuantidade() != null
				&& itemDieta.getTipoItemDieta().getIndDigitaQuantidade()
						.equals(DominioRestricao.O))) {
			return true;
		}
		return false; // nao obriga!
	}

	public boolean verificaDisabledAprazamento() {
		// se tem frequencia padrão desabilita
		if (this.frequenciaPadrao) {
			return true;
		}
		// Se tem item de dieta com digitação Opcional ou Obrigatória
		// habilita
		if (itemDieta != null && itemDieta.getTipoItemDieta() != null
				&& itemDieta.getTipoItemDieta().getSeq() != null &&(itemDieta.getTipoItemDieta().getIndDigitaAprazamento() != null
				&& (itemDieta.getTipoItemDieta().getIndDigitaAprazamento()
						.equals(DominioRestricao.O) || itemDieta
						.getTipoItemDieta().getIndDigitaAprazamento()
						.equals(DominioRestricao.C)))) {
			return false;
		}
		// limpa todos os campos de aprazamento
		if (itemDieta != null) {
			itemDieta.setTipoFrequenciaAprazamento(null);
			itemDieta.setFrequencia(null);
			itemDieta.setTipoFrequenciaAprazamento(null);
		}
		this.frequencia = null;
		this.tipoAprazamento = null;
		return true; // desabilita
	}

	public boolean verificaDisabledNumeroVezes() {
		// Se o tipo de item de dieta tem frequencia padrão habilita!
		if (itemDieta != null && itemDieta.getTipoItemDieta() != null
				&& itemDieta.getTipoItemDieta().getSeq() != null && (itemDieta.getTipoItemDieta().getTipoFrequenciaAprazamento() != null)) {
			return false;
		}
		if (itemDieta != null) {
			itemDieta.setNumeroVezes(null); // limpa antes de desabilitar
		}
		return true; // desabilita
	}

	public List<MpmTipoFrequenciaAprazamento> buscarTiposFrequenciaAprazamento(
			String strPesquisa) {
		return this.modeloBasicoFacade
				.obterListaTipoFrequenciaAprazamento((String) strPesquisa);
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(this.tipoAprazamento);
	}
	
	public String buscaDescricaoTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento
				.getDescricaoSintaxeFormatada(this.frequencia)
				: "";
	}
	
	public boolean verificaRequiredFrequencia() {
		if (this.tipoAprazamento != null){
			this.setTipoAprazamento(this.modeloBasicoFacade.obterTipoFrequenciaAprazamento(this.tipoAprazamento.getSeq()));
		}
		return this.tipoAprazamento != null
				&& this.tipoAprazamento.getIndDigitaFrequencia();
	}

	public void verificarFrequencia() {
		
		this.marcarAlteracaoCampoFormularioItem();
		
		if (!this.verificaRequiredFrequencia()) {
			this.frequencia = null;
		}
	}
	
	public void marcarAlteracaoCampoFormularioItem(){
		this.setCampoAlteradoFormularioItem(true);
	}
	
	public void desmarcarAlteracaoCampoFormularioItem(){
		this.setCampoAlteradoFormularioItem(false);
	}
	
	// getters and setters
	public MpmModeloBasicoDieta getModeloBasicoDieta() {
		return modeloBasicoDieta;
	}

	public void setModeloBasicoDieta(MpmModeloBasicoDieta modeloBasicoDieta) {
		this.modeloBasicoDieta = modeloBasicoDieta;
	}

	public IModeloBasicoFacade getModeloBasicoFacade() {
		return modeloBasicoFacade;
	}

	public void setModeloBasicoFacade(IModeloBasicoFacade modeloBasicoFacade) {
		this.modeloBasicoFacade = modeloBasicoFacade;
	}

	public Integer getModeloBasicoPrescricaoSeq() {
		return modeloBasicoPrescricaoSeq;
	}

	public void setModeloBasicoPrescricaoSeq(Integer modeloBasicoPrescricaoSeq) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public MpmItemModeloBasicoDieta getItemDieta() {
		return itemDieta;
	}

	public void setItemDieta(MpmItemModeloBasicoDieta itemDieta) {
		this.itemDieta = itemDieta;
	}

	public Boolean getAltera() {
		return altera;
	}

	public void setAltera(Boolean altera) {
		this.altera = altera;
	}

	public MpmModeloBasicoPrescricao getModeloBasico() {
		return modeloBasico;
	}

	public void setModeloBasico(MpmModeloBasicoPrescricao modeloBasico) {
		this.modeloBasico = modeloBasico;
	}

	public List<MpmItemModeloBasicoDietaVO> getListaItemModeloBasicoDieta() {
		return listaItemModeloBasicoDieta;
	}

	public void setListaItemModeloBasicoDieta(
			List<MpmItemModeloBasicoDietaVO> listaItemModeloBasicoDieta) {
		this.listaItemModeloBasicoDieta = listaItemModeloBasicoDieta;
	}

	public AnuTipoItemDieta getTipoItemDieta() {
		return tipoItemDieta;
	}

	public void setTipoItemDieta(AnuTipoItemDieta tipoItemDieta) {
		this.tipoItemDieta = tipoItemDieta;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public MpmItemModeloBasicoDietaId getMpmItemModeloBasicoDietaId() {
		return mpmItemModeloBasicoDietaId;
	}

	public void setMpmItemModeloBasicoDietaId(
			MpmItemModeloBasicoDietaId mpmItemModeloBasicoDietaId) {
		this.mpmItemModeloBasicoDietaId = mpmItemModeloBasicoDietaId;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public boolean isFrequenciaPadrao() {
		return frequenciaPadrao;
	}

	public void setFrequenciaPadrao(boolean frequenciaPadrao) {
		this.frequenciaPadrao = frequenciaPadrao;
	}

	public Integer getTipoItemDietaSeq() {
		return tipoItemDietaSeq;
	}

	public void setTipoItemDietaSeq(Integer tipoItemDietaSeq) {
		this.tipoItemDietaSeq = tipoItemDietaSeq;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public void setCampoAlteradoFormularioItem(boolean campoAlteradoFormularioItem) {
		this.campoAlteradoFormularioItem = campoAlteradoFormularioItem;
	}

	public boolean isCampoAlteradoFormularioItem() {
		return campoAlteradoFormularioItem;
	}

	public void setTipoItemDietaSeqEdicao(Integer tipoItemDietaSeqEdicao) {
		this.tipoItemDietaSeqEdicao = tipoItemDietaSeqEdicao;
	}

	public Integer getTipoItemDietaSeqEdicao() {
		return tipoItemDietaSeqEdicao;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

}
