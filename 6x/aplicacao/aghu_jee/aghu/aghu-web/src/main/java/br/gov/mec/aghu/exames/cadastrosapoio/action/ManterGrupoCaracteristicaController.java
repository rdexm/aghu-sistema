package br.gov.mec.aghu.exames.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica.Fields;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterGrupoCaracteristicaController extends ActionController {

	private static final long serialVersionUID = 5866914031897724836L;

	private static final String MANTER_GRUPO_CARACTERISTICA = "manterGrupoCaracteristica";
	
	private static final Fields[] inners = {AelGrupoResultadoCaracteristica.Fields.SERVIDOR};
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private AelGrupoResultadoCaracteristica grupoResultadoCaracteristica;
	
	private DominioOperacaoBanco operacao;
	
	private String voltarPara;
	private boolean iniciouTela;
		

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

		if(iniciouTela){
			return null;
		}
		
		if(grupoResultadoCaracteristica != null && grupoResultadoCaracteristica.getSeq() != null) {
			grupoResultadoCaracteristica = cadastrosApoioExamesFacade.obterAelGrupoResultadoCaracteristica(grupoResultadoCaracteristica.getSeq(),
																											inners, null);
			this.operacao = DominioOperacaoBanco.UPD;

			if(grupoResultadoCaracteristica == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
		} else {
			limpar();
			this.operacao = DominioOperacaoBanco.INS;
		}
		
		iniciouTela = true;
		return null;
	
	}
	
	public String gravar() {
		
		try {
			
			if(DominioOperacaoBanco.INS == this.operacao) {
				cadastrosApoioExamesFacade.inserirAelGrupoResultadoCaracteristica(grupoResultadoCaracteristica);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_GRUPO_CARACTERISTICA", grupoResultadoCaracteristica.getDescricao());

			} else {
				cadastrosApoioExamesFacade.atualizarAelGrupoResultadoCaracteristica(grupoResultadoCaracteristica);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_GRUPO_CARACTERISTICA", grupoResultadoCaracteristica.getDescricao());
			}
			
			return cancelar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		this.grupoResultadoCaracteristica = null;
		iniciouTela = false;
		
		if (StringUtils.isBlank(this.voltarPara)) {
		   this.voltarPara = MANTER_GRUPO_CARACTERISTICA;
		}
		
		return voltarPara;
	}
	
	public void limpar() {
		this.grupoResultadoCaracteristica = new AelGrupoResultadoCaracteristica();
		this.grupoResultadoCaracteristica.setSituacao(DominioSituacao.A);
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AelGrupoResultadoCaracteristica getGrupoResultadoCaracteristica() {
		return grupoResultadoCaracteristica;
	}

	public void setGrupoResultadoCaracteristica(
			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica) {
		this.grupoResultadoCaracteristica = grupoResultadoCaracteristica;
	}
}