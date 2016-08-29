package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.action.PesquisaCampoLaudoPaginatorController;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;


public class ManterCampoLaudoController extends ActionController {

	private static final long serialVersionUID = -7612140741672639668L;

	private static final String PESQUISA_CAMPO_LAUDO = "exames-pesquisaCampoLaudo";
	private static final String MANTER_SINONIMO_CAMPO_LAUDO = "exames-manterSinonimoCampoLaudo";
	private static final String MANTER_VALOR_NORMALIDADE_CAMPO = "exames-manterValorNormalidadeCampo";
	private static final String RESULTADO_PADRAO_CRUD = "exames-resultadoPadraoCRUD";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private Integer seq;
	private String voltarPara; 
	
	private AelCampoLaudo campoLaudo;
	
	@Inject
	private PesquisaCampoLaudoPaginatorController pesquisaCampoLaudoPaginatorController;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 


		if (seq != null) {
	
			campoLaudo = this.examesFacade.obterCampoLaudoPorSeq(seq);

			if(campoLaudo == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return voltar();
			}
				
		} else {
			this.campoLaudo = new AelCampoLaudo();
			this.campoLaudo.setPermiteDigitacao(true); 
			this.campoLaudo.setSituacao(DominioSituacao.A);

		}
		
		return null;
	
	}

	public List<AelGrupoResultadoCodificado> pesquisarGrupoResultadoCodificado(String objPesquisa) {
		return this.examesFacade.pesquisarGrupoResultadoCodificadoPorSeqDescricao(objPesquisa);
	}
	
	public List<AelGrupoResultadoCaracteristica> pesquisarGrupoResultadoCaracteristica(String objPesquisa) {
		return this.examesFacade.pesquisarGrupoResultadoCaracteristicaPorSeqDescricao(objPesquisa);
	}
	
	public String confirmar() {
		
		try {
			
			String mensagem = null;
			if (campoLaudo.getSeq() != null) {
				mensagem = "MENSAGEM_SUCESSO_ALTERAR_CAMPO_LAUDO";
			} else {
				mensagem = "MENSAGEM_SUCESSO_INSERIR_CAMPO_LAUDO";
			}
			
			// Define valor padrão dos atributos
			campoLaudo.setDividePorMil(false);
			campoLaudo.setFluxo(false);
			campoLaudo.setPertenceApac(false);
			campoLaudo.setDividePorMil(false);

			campoLaudo = cadastrosApoioExamesFacade.persistirCampoLaudo(campoLaudo);
		
			this.apresentarMsgNegocio(Severity.INFO, mensagem, campoLaudo.getNome());

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String manterSinonimos(){
		return MANTER_SINONIMO_CAMPO_LAUDO;
	}
	
	public String manterValoresNormalidade(){
		return MANTER_VALOR_NORMALIDADE_CAMPO;
	}
	
	public String manterResultadoPadrao(){
		return RESULTADO_PADRAO_CRUD;
	}
	
	/**
	 * Determina a exibição do "Botão de Valores Normalidade"
	 * O botão de valores normalidade só será exibido quando a tela estiver no modo edição 
	 * e o tipo de laudo conter N (Numérico), A (Alfanumérico) e E (Expressão). 
	 */
	public boolean exibirBotaoValoresNormalidade(){
		if(campoLaudo == null){
			return false;
		}
		return this.verificarTiposCampoLaudo(this.campoLaudo.getTipoCampo(), DominioTipoCampoCampoLaudo.N, DominioTipoCampoCampoLaudo.A, DominioTipoCampoCampoLaudo.E);
	}

	/**
	 * Determina a exibição do "Botão de Resultado Padrão"
	 * O botão de resultado padrão só será exibido quando a tela estiver no modo edição 
	 * e o tipo de laudo conter N (Numérico), A (Alfanumérico) ou C (Codificado)
	 * @return
	 */
	public boolean exibirBotaoResultadoPadrao(){
		if(campoLaudo == null){
			return false;
		}
		return this.verificarTiposCampoLaudo(this.campoLaudo.getTipoCampo(), DominioTipoCampoCampoLaudo.N, DominioTipoCampoCampoLaudo.A, DominioTipoCampoCampoLaudo.C);
	}
	
	/**
	 * Testa a ocorrência de um tipo de campo laudo em um conjunto de tipo campo laudo 
	 */
	private boolean verificarTiposCampoLaudo(final DominioTipoCampoCampoLaudo tipoCampo, final DominioTipoCampoCampoLaudo...conjunto){

		if(campoLaudo == null){
			return false;
		}
		boolean retorno = false;
	
		if(campoLaudo.getSeq() != null && this.campoLaudo.getTipoCampo() != null){
			for (DominioTipoCampoCampoLaudo item : conjunto) {
				if(item.equals(tipoCampo)){
					retorno = true;
					break;
				}
			}
		}
		
		return retorno;
	}

	public String voltar() {
		pesquisaCampoLaudoPaginatorController.setSeq(null);	
		campoLaudo = null;
		
		if(voltarPara != null){
			return voltarPara;
		}
		
		return PESQUISA_CAMPO_LAUDO;
	}
	
	
	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

}