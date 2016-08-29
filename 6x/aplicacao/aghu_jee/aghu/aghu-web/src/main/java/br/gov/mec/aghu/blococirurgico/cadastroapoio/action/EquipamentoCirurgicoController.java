package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcEquipamentoCirgPorUnid;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class EquipamentoCirurgicoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 8184573107688754567L;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	
	//item pai
	private MbcEquipamentoCirurgico equipamentoCirurgico = new MbcEquipamentoCirurgico();
	
	//item filho
	private MbcEquipamentoCirgPorUnid equipamentoCirurgicoPorUnid = new MbcEquipamentoCirgPorUnid();
	
	private final String PAGE_LIST_EQUIP_CIRUR =  "equipamentoCirurgicoList";
	
	// Flags que determinam comportamento da tela
	private Boolean emEdicao = Boolean.FALSE;
	private Boolean emEdicaoItem = Boolean.FALSE;
	
		
	//lista de MBC_EQUIPAMENTO_CIRG_POR_UNIDS
	private List<MbcEquipamentoCirgPorUnid> equipamentoCirgPorUnidList = new LinkedList<MbcEquipamentoCirgPorUnid>();
	
	private Boolean situacaoCheck = Boolean.TRUE;
	
	public void iniciar() {
	
		if(this.equipamentoCirurgico.getSeq() != null) {			
			
			this.emEdicao = Boolean.TRUE;		
			this.situacaoCheck = this.equipamentoCirurgico.getSituacao().isAtivo();
		}

		if(this.equipamentoCirurgico != null && this.equipamentoCirurgico.getSeq() != null) {
			//buscar a lista filha (MBC_EQUIPAMENTO_CIRG_POR_UNIDS pela coluna euu_seq)
			this.equipamentoCirgPorUnidList = this.blocoCirurgicoCadastroApoioFacade
				.listarEquipamentosCirgPorUnidade(this.equipamentoCirurgico);
		}
	
	}
	
	public void confirmar() {
		try {
			if(this.situacaoCheck) {
				this.equipamentoCirurgico.setSituacao(DominioSituacao.A);
			}else {
				this.equipamentoCirurgico.setSituacao(DominioSituacao.I);
			}
			if(this.emEdicao) {
				this.blocoCirurgicoCadastroApoioFacade
					.atualizarMbcEquipamentoCirurgico(
							this.equipamentoCirurgico);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ALTERACAO_EQUIPAMENTOS_CIRURGICOS", 
						this.equipamentoCirurgico.getDescricao());
			}else {
				this.blocoCirurgicoCadastroApoioFacade
					.inserirMbcEquipamentoCirurgico(
							this.equipamentoCirurgico);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_INCLUSAO_EQUIPAMENTOS_CIRURGICOS", 
						this.equipamentoCirurgico.getDescricao());
				this.emEdicao = Boolean.TRUE;
			}
			
			this.iniciar();
		}catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public void confirmarItem() {
		try {
			if(this.emEdicaoItem) {
				this.blocoCirurgicoCadastroApoioFacade
					.atualizarEquipamentoCirurgicoPorUnid(
							this.equipamentoCirurgicoPorUnid);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ALTERACAO_EQUIPAMENTOS_CIRURGICOS_POR_UNID", 
						this.equipamentoCirurgico.getDescricao());
			}else {
				this.equipamentoCirurgicoPorUnid.setMbcEquipamentoCirurgico(this.equipamentoCirurgico);
				this.blocoCirurgicoCadastroApoioFacade
					.inserirEquipamentoCirurgicoPorUnid(
							this.equipamentoCirurgicoPorUnid);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_INCLUSAO_EQUIPAMENTOS_CIRURGICOS_POR_UNID", 
						this.equipamentoCirurgico.getDescricao());
				this.emEdicao = Boolean.TRUE;
			}
			
			this.limpar();
			this.iniciar();
		}catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluir(MbcEquipamentoCirgPorUnid itemExclusao) {
		try {
			this.blocoCirurgicoCadastroApoioFacade.removerEquipamentoCirurgicoPorUnid(itemExclusao);
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_EQUIPAMENTOS_CIRURGICOS_POR_UNID");
			this.limpar();
			this.iniciar();
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public void limpar() {
		this.equipamentoCirurgicoPorUnid = new MbcEquipamentoCirgPorUnid();
		this.emEdicaoItem = Boolean.FALSE;
		this.situacaoCheck = Boolean.TRUE;
	}

	public void editar() {
		this.emEdicaoItem = Boolean.TRUE;
	}
	
	public void cancelarEdicao() {
		this.limpar();
	}
	
	//sugestion unidade funcional pesquisarUnidadeFuncionalPorSeqDescricao
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String parametro) {
		return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(parametro);
	}
	
	
	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {		
		this.equipamentoCirurgico  = new MbcEquipamentoCirurgico();
		equipamentoCirgPorUnidList = new LinkedList<MbcEquipamentoCirgPorUnid>();
		this.limpar();
		emEdicao = Boolean.FALSE;
		emEdicaoItem = Boolean.FALSE;
		situacaoCheck = Boolean.TRUE;
		return PAGE_LIST_EQUIP_CIRUR;
	}


	public MbcEquipamentoCirurgico getEquipamentoCirurgico() {
		return equipamentoCirurgico;
	}

	public void setEquipamentoCirurgico(MbcEquipamentoCirurgico equipamentoCirurgico) {
		this.equipamentoCirurgico = equipamentoCirurgico;
	}
	
	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public Boolean getEmEdicaoItem() {
		return emEdicaoItem;
	}

	public void setEmEdicaoItem(Boolean emEdicaoItem) {
		this.emEdicaoItem = emEdicaoItem;
	}

	public List<MbcEquipamentoCirgPorUnid> getEquipamentoCirgPorUnidList() {
		return equipamentoCirgPorUnidList;
	}

	public void setEquipamentoCirgPorUnidList(
			List<MbcEquipamentoCirgPorUnid> equipamentoCirgPorUnidList) {
		this.equipamentoCirgPorUnidList = equipamentoCirgPorUnidList;
	}

	public MbcEquipamentoCirgPorUnid getEquipamentoCirurgicoPorUnid() {
		return equipamentoCirurgicoPorUnid;
	}

	public void setEquipamentoCirurgicoPorUnid(
			MbcEquipamentoCirgPorUnid equipamentoCirurgicoPorUnid) {
		this.equipamentoCirurgicoPorUnid = equipamentoCirurgicoPorUnid;
	}

	public Boolean getSituacaoCheck() {
		return situacaoCheck;
	}

	public void setSituacaoCheck(Boolean situacaoCheck) {
		this.situacaoCheck = situacaoCheck;
	}

}
