package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterGrupoControleController extends ActionController {

	private static final long serialVersionUID = -5887289084727183643L;

	private static final String PESQUISAR_GRUPO_CONTROLE = "pesquisarGrupoControle";

	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;

	private EcpGrupoControle ecpGrupoControle;
	private boolean alterar;
	
	private List<EcpItemControle> itensAssociados;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(ecpGrupoControle != null && ecpGrupoControle.getSeq() != null){
			ecpGrupoControle = cadastrosBasicosControlePacienteFacade.obterGrupoControle(ecpGrupoControle.getSeq());

			if (ecpGrupoControle == null) {
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			itensAssociados = this.cadastrosBasicosControlePacienteFacade.verificaReferenciaItemControle(ecpGrupoControle);
			alterar = true;
			
		} else {
			ecpGrupoControle = new EcpGrupoControle();
			ecpGrupoControle.setSituacao(DominioSituacao.A);
		}
		
		return null;
	
	}

	public String cancelar() {
		this.alterar = false;
		itensAssociados = null;
		this.ecpGrupoControle = null;
		return PESQUISAR_GRUPO_CONTROLE;
	}

	public String salvar() {

		try {
			if (isAlterar()) {
				this.cadastrosBasicosControlePacienteFacade.alterarGrupoControle(this.ecpGrupoControle);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERACAO_MODELO_BASICO");

			} else {
				this.cadastrosBasicosControlePacienteFacade.inserirGrupoControle(this.ecpGrupoControle);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INCLUSAO_GRUPO_CONTROLE");
			}

			return cancelar();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	// getters e setters
	public EcpGrupoControle getEcpGrupoControle() {
		return ecpGrupoControle;
	}

	public void setEcpGrupoControle(EcpGrupoControle ecpGrupoControle) {
		this.ecpGrupoControle = ecpGrupoControle;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public List<EcpItemControle> getItensAssociados() {
		return itensAssociados;
	}
}