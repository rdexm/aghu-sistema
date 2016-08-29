package br.gov.mec.aghu.exames.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterCaracteristicaResultadoController extends ActionController {

	private static final long serialVersionUID = 4202837711360328052L;

	private static final String MANTER_CARACTERISTICA_RESULTADO = "manterCaracteristicaResultado";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private AelResultadoCaracteristica resultadoCaracteristica;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		
		if (resultadoCaracteristica != null && resultadoCaracteristica.getSeq() != null) {
			
			this.resultadoCaracteristica = this.examesFacade.obterAelResultadoCaracteristicaId(resultadoCaracteristica.getSeq());

			if(resultadoCaracteristica == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
		} else {
			resultadoCaracteristica = new AelResultadoCaracteristica();
			this.resultadoCaracteristica.setIndSituacao(DominioSituacao.A); // O valor padrão para uma situação de exame é ativo  
		}
		
		return null;
	
	}

	public String confirmar() {

		try {
			
			if (resultadoCaracteristica.getSeq() != null) {

				this.cadastrosApoioExamesFacade.atualizarAelResultadoCaracteristica(resultadoCaracteristica);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_CARACTERISTICA_RESULT", this.resultadoCaracteristica.getDescricao());

			} else {
				this.cadastrosApoioExamesFacade.inserirAelResultadoCaracteristica(resultadoCaracteristica);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_CARACTERISTICA_RESULT", this.resultadoCaracteristica.getDescricao());
			}
			
			return cancelar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} 
	}

	public String cancelar() {
		resultadoCaracteristica = null;
		return MANTER_CARACTERISTICA_RESULTADO;
	}

	public AelResultadoCaracteristica getResultadoCaracteristica() {
		return resultadoCaracteristica;
	}

	public void setResultadoCaracteristica(
			AelResultadoCaracteristica resultadoCaracteristica) {
		this.resultadoCaracteristica = resultadoCaracteristica;
	}
}