package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacaoVersaoLaudo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterMascarasLaudosController extends ActionController {

	private static final long serialVersionUID = -2551099217942199282L;

	private static final String PESQUISA_MASCARAS_LAUDOS = "exames-pesquisaMascarasLaudos";

	private static final String MASCARA_EXAMES_EDITOR = "exames-mascaraExamesEditor";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	private String voltarPara;

	private AelVersaoLaudo versaoLaudo;

	// Campos
	private VAelExameMatAnalise exameMatAnalise;
	
	private boolean iniciouTela;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}


	public String iniciar() {
	 

		if(iniciouTela){
			return null;
		}

		if (versaoLaudo != null && versaoLaudo.getId() != null) { 
			this.versaoLaudo = examesFacade.obterVersaoLaudoPorChavePrimaria(versaoLaudo.getId());

			if(versaoLaudo == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return voltar();
			}
			
			// Seta VAelExameMatAnalise
			AelExamesMaterialAnalise examesMaterialAnalise = this.examesFacade.obterAelExamesMaterialAnalisePorId(new AelExamesMaterialAnaliseId(versaoLaudo.getId().getEmaExaSigla(), versaoLaudo.getId().getEmaManSeq()));
			this.exameMatAnalise = this.cadastrosApoioExamesFacade.buscarVAelExameMatAnalisePorExameMaterialAnalise(examesMaterialAnalise);

		} else { 

			versaoLaudo = new AelVersaoLaudo();
			versaoLaudo.setImprimeNomeExame(true); // Valor padrão é marcado!
			versaoLaudo.setExameMaterialAnalise(obterExamesMaterialAnalise());
			versaoLaudo.setSituacao(DominioSituacaoVersaoLaudo.E); // Valor padrão é em construção!
			exameMatAnalise = null;
		}
		
		iniciouTela = true;
		
		return null;
	
	}

	public List<VAelExameMatAnalise> obterExameMaterialAnalise(String objPesquisa) {
		return this.returnSGWithCount(cadastrosApoioExamesFacade.buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnalise((String) objPesquisa),obterExameMaterialAnaliseCount(objPesquisa));
	}
	
	public Long obterExameMaterialAnaliseCount(String objPesquisa) {
		return cadastrosApoioExamesFacade.buscaVAelExameMatAnalisePelaSiglaDescViewExaMatAnaliseCount((String) objPesquisa);
	}

	public String confirmar() {

		try {

			String mensagem = null;
			
			if (versaoLaudo.getId() == null) {
				mensagem = "MENSAGEM_SUCESSO_INSERIR_MASCARA_CAMPO_LAUDO";
				versaoLaudo.setServidor(null);
				versaoLaudo.setCriadoEm(null);
				versaoLaudo.setExameMaterialAnalise(obterExamesMaterialAnalise());
				
			} else {
				mensagem = "MENSAGEM_SUCESSO_ALTERAR_MASCARA_CAMPO_LAUDO";
			}

			versaoLaudo.setNomeDesenho(versaoLaudo.getNomeDesenho().trim());
			cadastrosApoioExamesFacade.persistirVersaoLaudo(versaoLaudo);
			//Recuperar a versãoLaudo atualizada para evitar OPTIMISTIC LOCK
			this.versaoLaudo = examesFacade.obterVersaoLaudoPorChavePrimaria(versaoLaudo.getId());
			apresentarMsgNegocio(Severity.INFO, mensagem, versaoLaudo.getNomeDesenho());

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	private AelExamesMaterialAnalise obterExamesMaterialAnalise() {
		if (this.exameMatAnalise != null) {
			return this.examesFacade.obterAelExamesMaterialAnalisePorId(this.exameMatAnalise.getId());
		}
		return null;
	}

	public String voltar() {
		versaoLaudo = null;
		iniciouTela = false;
		
		if(voltarPara != null){
			return voltarPara;
		}
		
		return PESQUISA_MASCARAS_LAUDOS;
	}

	public String desenharMascara() {
		return MASCARA_EXAMES_EDITOR;
	}

	
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	} 

	public AelVersaoLaudo getVersaoLaudo() {
		return versaoLaudo;
	}

	public void setVersaoLaudo(AelVersaoLaudo versaoLaudo) {
		this.versaoLaudo = versaoLaudo;
	}

	public VAelExameMatAnalise getExameMatAnalise() {
		return exameMatAnalise;
	}

	public void setExameMatAnalise(VAelExameMatAnalise exameMatAnalise) {
		this.exameMatAnalise = exameMatAnalise;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}

}