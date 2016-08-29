package br.gov.mec.aghu.exames.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterMaterialAnaliseController extends ActionController {

	private static final long serialVersionUID = 2505601180808326492L;

	private static final String MANTER_MATERIAL_ANALISE_PESQUISA = "manterMaterialAnalisePesquisa";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	private AelMateriaisAnalises material;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(material != null && material.getSeq() != null) {
			material = examesFacade.obterMaterialAnalisePeloId(material.getSeq());
			if(material == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		} else {
			limpar();
		}
		
		return null;
	
	}
	
	public String gravar() {
		boolean criando = material.getSeq() == null;
		
		try {
			//Verifica preenchimento do campo 'Descrição'
			if(material.getDescricao().equals("")) {
				apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Descrição");
				return null;
			}
			
			cadastrosApoioExamesFacade.persistirMaterialAnalise(material);
			if(criando) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_MATERIAL_ANALISE", material.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_MATERIAL_ANALISE", material.getDescricao());
			}
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
			
		} catch(PersistenceException e) {
			if(criando) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CRIACAO_MATERIAL_ANALISE", material.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EDICAO_MATERIAL_ANALISE", material.getDescricao());
			}
			return null;
		}
		
		return cancelar();
	}
	
	public void limpar() {
		material = new AelMateriaisAnalises();
		material.setIndSituacao(DominioSituacao.A);
		material.setIndColetavel(true);
	}
	
	public String cancelar() {
		material = null;
		return MANTER_MATERIAL_ANALISE_PESQUISA;
	}
	
	public AelMateriaisAnalises getMaterial() {
		return material;
	}
	
	public void setMaterial(AelMateriaisAnalises material) {
		this.material = material;
	}
}
