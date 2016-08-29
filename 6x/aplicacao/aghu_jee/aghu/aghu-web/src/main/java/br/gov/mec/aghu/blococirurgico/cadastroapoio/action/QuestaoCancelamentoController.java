package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcQuestao;
import br.gov.mec.aghu.model.MbcValorValidoCanc;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class QuestaoCancelamentoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8248924165930625469L;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	//item detalhe principal
	private MbcMotivoCancelamento motivoCancelamento;
	
	//item filho
	private MbcQuestao mbcQuestao = new MbcQuestao();
	
	//lista de MBC_QUESTOES
	private List<MbcQuestao> questoesList = new LinkedList<MbcQuestao>();
	
	private MbcValorValidoCanc valorValido = new MbcValorValidoCanc();

	private static final String QUESTAO_CANCELAMENTO = "questaoCancelamento";
			
	private List<MbcValorValidoCanc> valoresValidosList = new LinkedList<MbcValorValidoCanc>();
	
	private String voltarPara; 
	
	// Flags que determinam comportamento da tela
	private Boolean emEdicaoItem = Boolean.FALSE;
	private Boolean emEdicaoValorValidoItem = Boolean.FALSE;
	
	// Parâmetros da conversação
	private Short seq;
	
	private Boolean situacaoCheck = Boolean.TRUE;
	private Boolean situacaoValorCheck = Boolean.TRUE;
	private Boolean exibirSliderValorValido = Boolean.FALSE;
	
	
	public void iniciar() {
	 

	 

		if(this.seq != null) {
			this.motivoCancelamento = 
				this.blocoCirurgicoCadastroApoioFacade.obterMbcMotivoCancelamento(this.seq);
		}
		if(!this.exibirSliderValorValido) {
			if(this.motivoCancelamento != null && this.motivoCancelamento.getSeq() != null) {
				this.questoesList = this.blocoCirurgicoFacade.listarMbcQuestoes(this.seq);
			}
		}else {
			if(this.mbcQuestao != null && this.mbcQuestao.getId() != null) {
				this.valoresValidosList = 
					this.blocoCirurgicoFacade.listarMbcValorValidoCancPorQuestao(
							this.mbcQuestao.getId().getMtcSeq(),
							this.mbcQuestao.getId().getSeqp());
			}
		}
	
	}
	
	
	
	public void confirmar() {
		try {
			this.mbcQuestao.setSituacao(DominioSituacao.getInstance(this.situacaoCheck));
			
			if(this.emEdicaoItem) {
				this.blocoCirurgicoCadastroApoioFacade.atualizarMbcQuestao(
						this.mbcQuestao);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ALTERACAO_QUESTAO_CANCELAMENTO", 
						this.mbcQuestao.getDescricao());
			}else {
				this.mbcQuestao.setMotivoCancelamento(this.motivoCancelamento);
				this.blocoCirurgicoCadastroApoioFacade.inserirMbcQuestao(
						this.mbcQuestao);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_INCLUSAO_QUESTAO_CANCELAMENTO", 
						this.mbcQuestao.getDescricao());
			}
			this.limpar();
			this.iniciar();
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	
	public void confirmarItem() {
		try {
			this.valorValido.setSituacao(DominioSituacao.getInstance(this.situacaoValorCheck));
			
			if(this.emEdicaoValorValidoItem) {
				this.blocoCirurgicoCadastroApoioFacade
					.atualizarMbcValorValidoCanc(
							this.valorValido);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ALTERACAO_QUESTAO_CANCELAMENTO_VALOR_VALIDO", 
						this.valorValido.getValor());
			}else {
				this.valorValido.setQuestao(this.mbcQuestao);
				this.blocoCirurgicoCadastroApoioFacade
					.inserirMbcValorValidoCanc(
							this.valorValido);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_INCLUSAO_QUESTAO_CANCELAMENTO_VALOR_VALIDO", 
						this.valorValido.getValor());
			}
			this.limparCamposValorValido();
			this.iniciar();
		}catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	
	public void limpar() {
		this.mbcQuestao = new MbcQuestao();
		this.emEdicaoItem = Boolean.FALSE;
		this.situacaoCheck = Boolean.TRUE;
	}
	
	
	public void limparCamposValorValido() {
		this.valorValido = new MbcValorValidoCanc();
		this.emEdicaoValorValidoItem = Boolean.FALSE;
		this.situacaoValorCheck = Boolean.TRUE;
	}
	
	public boolean isItemSelecionado(final Object item){
		if(item instanceof MbcQuestao) {
			if(this.mbcQuestao != null 
					&& this.mbcQuestao.equals(item)){
				return true;
			}
		}
		if(item instanceof MbcValorValidoCanc) {
			if(this.valorValido != null 
					&& this.valorValido.equals(item)){
				return true;
			}
		}
		return false;
	}
	
	
	
	public void editarItem() {
		this.situacaoCheck = this.mbcQuestao.getSituacao().isAtivo();
		this.emEdicaoItem = Boolean.TRUE;
	}
	
	public void cancelarEdicao() {
		this.limpar();
	}
	

	public void editarValorValidoItem() {
		this.situacaoValorCheck = this.valorValido.getSituacao().isAtivo();
		this.emEdicaoValorValidoItem = Boolean.TRUE;
	}
	
	
	
	public void cancelarValorValidoEdicao() {
		this.limparCamposValorValido();
		iniciar();
	}
	
	
	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		if(this.exibirSliderValorValido) {
			this.limparCamposValorValido();
			this.limpar();
			this.exibirSliderValorValido = Boolean.FALSE;
			return QUESTAO_CANCELAMENTO;
		}
		return this.voltarPara;
	}

	
	
	public String detalharValorValido() {
		return QUESTAO_CANCELAMENTO;
	}
	
	
	/** GET/SET **/
	public String obterSimNao(Boolean valor) {
		return DominioSimNao.getInstance(valor).getDescricao();
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getEmEdicaoItem() {
		return emEdicaoItem;
	}

	public void setEmEdicaoItem(Boolean emEdicaoItem) {
		this.emEdicaoItem = emEdicaoItem;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public Boolean getSituacaoCheck() {
		return situacaoCheck;
	}

	public void setSituacaoCheck(Boolean situacaoCheck) {
		this.situacaoCheck = situacaoCheck;
	}

	public MbcMotivoCancelamento getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(MbcMotivoCancelamento motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public MbcQuestao getMbcQuestao() {
		return mbcQuestao;
	}

	public void setMbcQuestao(MbcQuestao mbcQuestao) {
		this.mbcQuestao = mbcQuestao;
	}

	public List<MbcQuestao> getQuestoesList() {
		return questoesList;
	}

	public void setQuestoesList(List<MbcQuestao> questoesList) {
		this.questoesList = questoesList;
	}

	public Boolean getExibirSliderValorValido() {
		return exibirSliderValorValido;
	}

	public void setExibirSliderValorValido(Boolean exibirSliderValorValido) {
		this.exibirSliderValorValido = exibirSliderValorValido;
	}

	public MbcValorValidoCanc getValorValido() {
		return valorValido;
	}

	public void setValorValido(MbcValorValidoCanc valorValido) {
		this.valorValido = valorValido;
	}

	public Boolean getSituacaoValorCheck() {
		return situacaoValorCheck;
	}

	public void setSituacaoValorCheck(Boolean situacaoValorCheck) {
		this.situacaoValorCheck = situacaoValorCheck;
	}

	public List<MbcValorValidoCanc> getValoresValidosList() {
		return valoresValidosList;
	}

	public void setValoresValidosList(
			List<MbcValorValidoCanc> valoresValidosList) {
		this.valoresValidosList = valoresValidosList;
	}

	public Boolean getEmEdicaoValorValidoItem() {
		return emEdicaoValorValidoItem;
	}

	public void setEmEdicaoValorValidoItem(Boolean emEdicaoValorValidoItem) {
		this.emEdicaoValorValidoItem = emEdicaoValorValidoItem;
	}

}
