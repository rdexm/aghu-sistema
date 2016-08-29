package br.gov.mec.aghu.exames.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterRecipienteColetaController extends ActionController {

	private static final long serialVersionUID = -2638725325819599983L;

	private static final String MANTER_RECIPIENTE_COLETA_PESQUISA = "manterRecipienteColetaPesquisa";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private AelRecipienteColeta recipienteColeta;
	private Boolean ehAntiCoagulante = Boolean.FALSE;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
		
	public String iniciar() {
	 

		if(recipienteColeta != null && recipienteColeta.getSeq() != null) {
			recipienteColeta = examesFacade.obterRecipienteColetaPorId(this.recipienteColeta.getSeq());

			if(recipienteColeta == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			setEhAntiCoagulante(this.recipienteColeta.getIndAnticoag().isSim());
		} else {
			limpar();
		}
		
		return null;
	
	}
	
	public String gravar() {
		
		try {
			final boolean isSave = this.recipienteColeta.getSeq() == null;
			
			this.recipienteColeta.setIndAnticoag(DominioSimNao.getInstance(this.getEhAntiCoagulante()));
			this.cadastrosApoioExamesFacade.saveOrUpdateRecipienteColeta(recipienteColeta);
			
			if(isSave) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_RECIPIENTE_COLETA", this.recipienteColeta.getDescricao());
				
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_RECIPIENTE_COLETA", this.recipienteColeta.getDescricao());
			}
			
			return cancelar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		this.recipienteColeta = null;
		return MANTER_RECIPIENTE_COLETA_PESQUISA;
	}
	
	public void limpar() {
		this.recipienteColeta = new AelRecipienteColeta();
		this.recipienteColeta.setIndSituacao(DominioSituacao.A);
	}

	public AelRecipienteColeta getRecipienteColeta() {
		return recipienteColeta;
	}

	public void setRecipienteColeta(AelRecipienteColeta recipienteColeta) {
		this.recipienteColeta = recipienteColeta;
	}

	public void setEhAntiCoagulante(Boolean ehAntiCoagulante) {
		this.ehAntiCoagulante = ehAntiCoagulante;
	}

	public Boolean getEhAntiCoagulante() {
		return ehAntiCoagulante;
	}
}