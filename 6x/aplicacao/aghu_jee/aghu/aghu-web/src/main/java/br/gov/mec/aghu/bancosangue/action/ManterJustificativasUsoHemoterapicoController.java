package br.gov.mec.aghu.bancosangue.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;


public class ManterJustificativasUsoHemoterapicoController extends ActionController {

	private static final long serialVersionUID = -8375780982198651306L;

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;

	
	// suggestion box (desabilitado)
	private AbsComponenteSanguineo componenteSanguineo;
	
	// suggestion box (desabilitado)
	private AbsProcedHemoterapico procedimentoHemoterapico;

	// suggestion box 
	private AbsGrupoJustificativaComponenteSanguineo grupoJustificativaComponenteSanguineo;
	
	// parametros da tela (form)
	private AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo; 
	
	// check box
	private Boolean ativa = Boolean.TRUE;
	
	// combo descricao livre
	private DominioSimNao descricaoLivre;
	
	private String origemRequisicao;
	
	private Boolean modoEdicao;
	
	
	/**
	 * Redirecionamentos
	 */
	private enum EnumTargetManterJustificativasUsoHemoterapicoController {
		// tela de pesquisa
		PESQUISAR("bancodesangue-pesquisarJustificativasUsoHemoterapico"),
		
		// tela de pesquisa da estória #6400
		GRUPOS("bancodesangue-pesquisarGrupoJustifAssocJustificativa");
		
		private final String descricao;

		private EnumTargetManterJustificativasUsoHemoterapicoController(
				String descricao) {
			this.descricao = descricao;
		}
	}
	
	/**
	 * Mensagens
	 */
	private enum JustificativasUsoHemoterapicoMessages {
		MENSAGEM_SUCESSO_ALTERAR_JUSTIFICATIVAS_USO_HEMOTERAPICO,
		MENSAGEM_SUCESSO_GRAVAR_JUSTIFICATIVAS_USO_HEMOTERAPICO;		
	}
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Executado quando a tela é acessada
	 */
	public String iniciar() {
	 		
		if(justificativaComponenteSanguineo != null && justificativaComponenteSanguineo.getSeq() != null) {
			justificativaComponenteSanguineo = bancoDeSangueFacade.obterAbsJustificativaComponenteSanguineoPorSeq(justificativaComponenteSanguineo.getSeq(),
					AbsJustificativaComponenteSanguineo.Fields.COMPONENTE_SANGUINEO,
					AbsJustificativaComponenteSanguineo.Fields.GRUPO_JUSTIFICATIVA,
					AbsJustificativaComponenteSanguineo.Fields.PROCEDIMENTO_HEMOTERAPICO);

			if(justificativaComponenteSanguineo == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			carregarDadosEdicao();
			
		} else {
			limpar();
		}
		
		return null;
	
	}
	
	
	/**
	 * Carrega o form com os dados do registro selecionado para edição
	 */
	private void carregarDadosEdicao() {
		if(justificativaComponenteSanguineo != null) {
			setAtiva(justificativaComponenteSanguineo.getSituacao().isAtivo());
			if(justificativaComponenteSanguineo.getDescricaoLivre()) {
				setDescricaoLivre(DominioSimNao.S);
			} else {
				setDescricaoLivre(DominioSimNao.N);
			}
			if(justificativaComponenteSanguineo.getGrupoJustificativaComponenteSanguineo() != null) {
				setGrupoJustificativaComponenteSanguineo(justificativaComponenteSanguineo.getGrupoJustificativaComponenteSanguineo());
			}
			if(justificativaComponenteSanguineo.getProcedimentoHemoterapico() != null) {
				setProcedimentoHemoterapico(justificativaComponenteSanguineo.getProcedimentoHemoterapico());
			}
			if(justificativaComponenteSanguineo.getComponenteSanguineo() != null) {
				setComponenteSanguineo(justificativaComponenteSanguineo.getComponenteSanguineo());
			}
		}
		setModoEdicao(Boolean.TRUE);
	}

	
	
	/**
	 * Grava ou atualiza registro
	 */
	public String gravar() {
		
		boolean edicao = false;
		
		try {
			
			if(getAtiva()) {
				getJustificativaComponenteSanguineo().setSituacao(DominioSituacao.A);
			} else {
				getJustificativaComponenteSanguineo().setSituacao(DominioSituacao.I);
			}

			getJustificativaComponenteSanguineo().setDescricaoLivre(getDescricaoLivre().isSim());
			getJustificativaComponenteSanguineo().setGrupoJustificativaComponenteSanguineo(getGrupoJustificativaComponenteSanguineo());

			if(getJustificativaComponenteSanguineo() != null && getJustificativaComponenteSanguineo().getSeq() != null) {
				edicao = true;
			}
			
			if(getJustificativaComponenteSanguineo().getComponenteSanguineo() == null) {
				getJustificativaComponenteSanguineo().setComponenteSanguineo(getComponenteSanguineo());
			}
			if(getJustificativaComponenteSanguineo().getProcedimentoHemoterapico() == null) {
				getJustificativaComponenteSanguineo().setProcedimentoHemoterapico(getProcedimentoHemoterapico());
			}
			
			bancoDeSangueFacade.persistirJustificativaComponenteSanguineo(getJustificativaComponenteSanguineo());
			
			if(edicao) {
				this.apresentarMsgNegocio(Severity.INFO, 
						JustificativasUsoHemoterapicoMessages.MENSAGEM_SUCESSO_ALTERAR_JUSTIFICATIVAS_USO_HEMOTERAPICO.toString());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, 
						JustificativasUsoHemoterapicoMessages.MENSAGEM_SUCESSO_GRAVAR_JUSTIFICATIVAS_USO_HEMOTERAPICO.toString());
			}

			return cancelar();

		} catch(BaseException e){
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public void limpar() {
		setJustificativaComponenteSanguineo(null);
		setGrupoJustificativaComponenteSanguineo(null);
		setModoEdicao(Boolean.FALSE);
		setAtiva(Boolean.TRUE);
	}
	
	public String cancelar() {
		limpar();
		return EnumTargetManterJustificativasUsoHemoterapicoController.PESQUISAR.descricao;
	}
	

	/**
	 * Redireciona para tela de pesquisa da estoria #6400
	 */
	public String grupos() {
		return EnumTargetManterJustificativasUsoHemoterapicoController.GRUPOS.descricao;
	}
	
	
	/**
	 * Suggestion box de componente sanguineo
	 */
	public List<AbsGrupoJustificativaComponenteSanguineo> pesquisarGrupoJustificativaComponenteSanguineo(String param){
		return bancoDeSangueFacade.pesquisarGrupoJustificativaComponenteSanguineo(param);
	}
	
	public AbsJustificativaComponenteSanguineo getJustificativaComponenteSanguineo() {
		if (justificativaComponenteSanguineo == null) {
			justificativaComponenteSanguineo = new AbsJustificativaComponenteSanguineo();
		}
		return justificativaComponenteSanguineo;
	}

	public void setJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo) {
		this.justificativaComponenteSanguineo = justificativaComponenteSanguineo;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getAtiva() {
		return ativa;
	}

	public void setAtiva(Boolean ativa) {
		this.ativa = ativa;
	}

	public DominioSimNao getDescricaoLivre() {
		return descricaoLivre;
	}

	public void setDescricaoLivre(DominioSimNao descricaoLivre) {
		this.descricaoLivre = descricaoLivre;
	}

	public AbsGrupoJustificativaComponenteSanguineo getGrupoJustificativaComponenteSanguineo() {
		return grupoJustificativaComponenteSanguineo;
	}

	public void setGrupoJustificativaComponenteSanguineo(AbsGrupoJustificativaComponenteSanguineo grupoJustificativaComponenteSanguineo) {
		this.grupoJustificativaComponenteSanguineo = grupoJustificativaComponenteSanguineo;
	}

	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineo(AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	}

	public AbsProcedHemoterapico getProcedimentoHemoterapico() {
		return procedimentoHemoterapico;
	}

	public void setProcedimentoHemoterapico(AbsProcedHemoterapico procedimentoHemoterapico) {
		this.procedimentoHemoterapico = procedimentoHemoterapico;
	}

	public String getOrigemRequisicao() {
		return origemRequisicao;
	}

	public void setOrigemRequisicao(String origemRequisicao) {
		this.origemRequisicao = origemRequisicao;
	}
}