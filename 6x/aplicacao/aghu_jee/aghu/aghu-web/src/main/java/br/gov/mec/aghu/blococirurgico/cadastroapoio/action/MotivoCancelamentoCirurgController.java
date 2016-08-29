package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcPerfilCancelamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class MotivoCancelamentoCirurgController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 4989265815380375127L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
		
	@EJB
	private ICascaFacade cascaFacade;
	
	//item pai
	private MbcMotivoCancelamento motivoCancelamento = new MbcMotivoCancelamento();
	
	//item filho
	private MbcPerfilCancelamento perfilCancelamento = new MbcPerfilCancelamento();
	
	private String voltarPara; // O padrão é voltar para interface de pesquisa
	
	// Flags que determinam comportamento da tela
	private Boolean emEdicao = Boolean.FALSE;
	
	// Parâmetros da conversação
	private Short seq;
	
	//item de exclusao
	private MbcPerfilCancelamento itemExclusao = new MbcPerfilCancelamento();
	
	//lista de MBC_EQUIPAMENTO_CIRG_POR_UNIDS
	private List<MbcPerfilCancelamento> perfilCancelamentoList = new LinkedList<MbcPerfilCancelamento>();
	
	private Boolean situacaoCheck = Boolean.TRUE;
	
	
	
	
	public void iniciar() {
	 

	 

		if(this.seq != null) {
			this.emEdicao = Boolean.TRUE;
			this.motivoCancelamento = 
				this.blocoCirurgicoCadastroApoioFacade.obterMbcMotivoCancelamento(this.seq);
			this.situacaoCheck = this.motivoCancelamento.getSituacao().isAtivo();
		}else {
			this.motivoCancelamento.setDestSr(Boolean.TRUE);
		}

		if(this.motivoCancelamento != null && this.motivoCancelamento.getSeq() != null) {
			this.perfilCancelamentoList = this.blocoCirurgicoCadastroApoioFacade
				.listarPerfisCancelamentos(this.motivoCancelamento.getSeq());
		}
	
	}
	
	
	public void confirmar() {
		try {
			if(this.situacaoCheck) {
				this.motivoCancelamento.setSituacao(DominioSituacao.A);
			}else {
				this.motivoCancelamento.setSituacao(DominioSituacao.I);
			}
			
			if(this.emEdicao) {
				this.blocoCirurgicoCadastroApoioFacade
					.atualizarMbcMotivoCancelamento(this.motivoCancelamento);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ALTERACAO_MOTIVOS_CANCELAMENTO", 
						this.motivoCancelamento.getDescricao());
			}else {
				this.blocoCirurgicoCadastroApoioFacade
					.inserirMbcMotivoCancelamento(
							this.motivoCancelamento);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_INCLUSAO_MOTIVOS_CANCELAMENTO", 
						this.motivoCancelamento.getDescricao());
				this.emEdicao = Boolean.TRUE;
			}
			this.iniciar();
		}catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	
	
	public void confirmarItem() {
		try {
			this.perfilCancelamento.setMbcMotivoCancelamento(this.motivoCancelamento);
			this.blocoCirurgicoCadastroApoioFacade
				.inserirMbcPerfilCancelamento(
						this.perfilCancelamento);
			this.emEdicao = Boolean.TRUE;
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_INCLUSAO_PERFIL_CANCELAMENTO", 
					this.perfilCancelamento.getId().getPerNome());
			this.limpar();
			this.iniciar();
		} 
		catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		catch (Exception e) {
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_ERRO_INCLUSAO_PERFIL_CANCELAMENTO", 
					this.perfilCancelamento.getId().getPerNome());
		}
	}
	
	
	
	public void excluir() {
		try {
			this.blocoCirurgicoCadastroApoioFacade
				.removerMbcPerfilCancelamento(this.itemExclusao);
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_EXCLUSAO_PERFIL_CANCELAMENTO", 
					this.itemExclusao.getId().getPerNome());
			this.limpar();
			this.iniciar();
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	
	public void limpar() {
		this.perfilCancelamento = new MbcPerfilCancelamento();
		this.situacaoCheck = Boolean.TRUE;
	}
	
	
	public boolean isItemSelecionado(final MbcPerfilCancelamento item){
		if(this.perfilCancelamento != null 
				&& this.perfilCancelamento.equals(item)){
			return true;
		}
		return false;
	}
	
	
	public List<Perfil> pesquisarPerfil(String parametro) throws ApplicationBusinessException {
		return this.cascaFacade.pesquisarPerfisSuggestionBox((String) parametro);
	}
	
	
	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		this.limpar();
		return this.voltarPara;
	}
	
	
	/** GET/SET **/
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
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

	public List<MbcPerfilCancelamento> getPerfilCancelamentoList() {
		return perfilCancelamentoList;
	}

	public void setPerfilCancelamentoList(
			List<MbcPerfilCancelamento> perfilCancelamentoList) {
		this.perfilCancelamentoList = perfilCancelamentoList;
	}

	public MbcMotivoCancelamento getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(MbcMotivoCancelamento motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public MbcPerfilCancelamento getPerfilCancelamento() {
		return perfilCancelamento;
	}

	public void setPerfilCancelamento(MbcPerfilCancelamento perfilCancelamento) {
		this.perfilCancelamento = perfilCancelamento;
	}

	public MbcPerfilCancelamento getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(MbcPerfilCancelamento itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

}
