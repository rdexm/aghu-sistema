package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.agendamento.business.IBlocoCirurgicoAgendamentoFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcDescricaoPadrao;
import br.gov.mec.aghu.model.MbcDescricaoPadraoId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class DescricaoPadraoCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3566582839368074664L;
	
	private static final String DESCRICAO_PADRAO_LIST = "cadastroDescricaoPadraoList";
	
	@EJB
	private IBlocoCirurgicoAgendamentoFacade blocoCirurgicoAgendamentoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	private MbcDescricaoPadrao descricaoPadrao;
	
	private boolean ativo;

	private Integer seqp;
	
	private Short espSeq;
	
	private final Integer maxRegitrosEsp = 100; 
	
	private final Integer maxRegitrosProc = 100;
	
	public void iniciar() {
	 

	 

		this.ativo = true;
		if(seqp != null && espSeq !=null)  {
			descricaoPadrao = blocoCirurgicoAgendamentoFacade.obterDescricaoPadraoById(seqp, espSeq);
			this.ativo = false;
		} else {
			resetarDescricaoPadrao();
		}
	
	}
	
	
	
	//Metodo para Suggestion Box de especialidades
	public List<AghEspecialidades> pesquisarEspecialidades(String objPesquisa){
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadePrincipalAtivaPorNomeOuSigla((String)objPesquisa, this.maxRegitrosEsp),pesquisarEspecialidadesPrincipaisCount(objPesquisa)); 
	}
	
	public Integer pesquisarEspecialidadesPrincipaisCount(String objPesquisa){
		return this.aghuFacade.pesquisarEspecialidadePrincipalAtivaPorNomeOuSiglaCount((String)objPesquisa);
	}
	
	//Metodo para Suggestion Box de Procedimentos cirurgicos
	public List<MbcProcedimentoCirurgicos> obterProcedimentoCirurgicos(String objPesquisa){
		return this.returnSGWithCount(this.blocoCirurgicoFacade.listarProcedimentoCirurgicos(objPesquisa, this.maxRegitrosProc),obterProcedimentoCirurgicosCount(objPesquisa));
	}
	
	public Long obterProcedimentoCirurgicosCount(String objPesquisa){
		return this.blocoCirurgicoFacade.listarProcedimentoCirurgicosCount(objPesquisa);
	}
		
	private void resetarDescricaoPadrao() {
		descricaoPadrao = new MbcDescricaoPadrao();
		MbcDescricaoPadraoId id = new MbcDescricaoPadraoId();
		descricaoPadrao.setAghEspecialidades(null);
		descricaoPadrao.setDescricaoTecPadrao(null);
		descricaoPadrao.setMbcProcedimentoCirurgicos(null);
		descricaoPadrao.setId(id);
		seqp = null;
	}
	
	
	/**
	 * Método chamado ao cancelar a tela de cadastro de descricao Padrao
	 */
	public String voltar() {
		descricaoPadrao = new MbcDescricaoPadrao();
		this.setEspSeq(null);
		this.setSeqp(null);		
		return DESCRICAO_PADRAO_LIST;
	}
	
	
	public String gravar() {
		Boolean novo = this.getSeqp() == null;
		try {
			
			this.blocoCirurgicoAgendamentoFacade.persistirMbcDescricaopadrao(this.getDescricaoPadrao());
			
			this.apresentarMsgNegocio(Severity.INFO,
					novo ? "MENSAGEM_SUCESSO_GRAVAR_DESCRICAO_PADRAO" : "MENSAGEM_SUCESSO_ALTERACAO_DESCRICAO_PADRAO",
					this.getDescricaoPadrao().getTitulo());
		} catch (final BaseException e) {
			if(novo){
				this.getDescricaoPadrao().getId().setSeqp(null); 
			}
			apresentarExcecaoNegocio(e);
		}
		return DESCRICAO_PADRAO_LIST;
	}
	
	public void limpar() {
		resetarDescricaoPadrao();
	}
	
	public MbcDescricaoPadrao getDescricaoPadrao() {
		return descricaoPadrao;
	}

	public void setDescricaoPadrao(MbcDescricaoPadrao DescricaoPadrao) {
		this.descricaoPadrao = DescricaoPadrao;
	}

	
	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}


	public Short getEspSeq() {
		return espSeq;
	}


	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	
}
