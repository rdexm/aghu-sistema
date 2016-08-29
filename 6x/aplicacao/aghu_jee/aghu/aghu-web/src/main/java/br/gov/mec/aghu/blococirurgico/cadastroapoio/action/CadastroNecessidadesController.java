package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroNecessidadesController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final String CADASTRO_NECESSIDADE_LIST = "pesquisaNecessidades";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3288102063913966552L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	private Boolean desabilitarCampos = false;

	private MbcNecessidadeCirurgica necessidade;
	private Boolean situacaoNecessidade;
	
	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
	 

	 

		
		if(necessidade!=null && necessidade.getSeq()!=null){
			desabilitarCampos = true;
			situacaoNecessidade = necessidade.getSituacao().isAtivo();
		}else{
			desabilitarCampos = false;
			necessidade = new MbcNecessidadeCirurgica();
			situacaoNecessidade = true;
			necessidade.setIndExigeDescSolic(true);
		}
	
	}
	

	public String confirmar(){
		try {

			boolean inclusao = necessidade.getSeq()==null; 

			if(inclusao){
				necessidade.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), new Date()));
			}
			
			necessidade.setSituacao(DominioSituacao.getInstance(this.situacaoNecessidade));
			
			this.blocoCirurgicoCadastroApoioFacade.persistirNecessidade(necessidade);

			if(inclusao){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_NECESSIDADE");
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_NECESSIDADE");
			}
			return CADASTRO_NECESSIDADE_LIST;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
		
	}
	
	
	public String cancelar(){
		return CADASTRO_NECESSIDADE_LIST;
	}

	/**
	 * Obtem unidade funcional ativa executora de cirurgias
	 */
	public List<AghUnidadesFuncionais> obterUnidadeExecutora(String filtro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadeFuncionalPorSeqDescricao((String)filtro, true),obterUnidadeExecutoraCount(filtro));
	}
	
	public Long obterUnidadeExecutoraCount(String filtro) {
        return this.aghuFacade.pesquisarUnidadeFuncionalPorSeqDescricaoCount((String)filtro, true);
    }
	
	/*
	 * Getters and Setters abaixo...
	 */
	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public Boolean getDesabilitarCampos() {
		return desabilitarCampos;
	}

	public void setDesabilitarCampos(Boolean desabilitarCampos) {
		this.desabilitarCampos = desabilitarCampos;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public Boolean getSituacaoNecessidade() {
		return situacaoNecessidade;
	}

	public void setSituacaoNecessidade(Boolean situacaoNecessidade) {
		this.situacaoNecessidade = situacaoNecessidade;
	}

	public MbcNecessidadeCirurgica getNecessidade() {
		return necessidade;
	}

	public void setNecessidade(MbcNecessidadeCirurgica necessidade) {
		this.necessidade = necessidade;
	}

}