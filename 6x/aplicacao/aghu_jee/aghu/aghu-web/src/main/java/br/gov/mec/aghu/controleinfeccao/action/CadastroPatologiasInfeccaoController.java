package br.gov.mec.aghu.controleinfeccao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciDuracaoMedidaPreventiva;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroPatologiasInfeccaoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 4202837711360328052L;

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;

	private MciPatologiaInfeccao patologiaInfeccao = new MciPatologiaInfeccao();

	private Boolean situacao;

	private boolean emEdicao;

	private String voltarPara;
	
	//integracao com estoria #41035
	private boolean permConsultaTela = false;

	public void iniciar() {
	 

	 

		if (!this.emEdicao) {
			this.situacao = true; // Padrão é ativado
			this.patologiaInfeccao.setHigienizacaoMaos(true); // Higienização é
																// padrão
		} else {
			this.situacao = this.patologiaInfeccao.getSituacao().isAtivo();
		}
	
	}
	

	/**
	 * Gravar
	 */
	public String gravar() {
		this.patologiaInfeccao.setSituacao(DominioSituacao.getInstance(this.situacao));
		try {
			this.controleInfeccaoFacade.persistirMciPatologiaInfeccao(this.patologiaInfeccao);

			if (patologiaInfeccao.getSeq() != null && this.emEdicao) { // EDIÇÃO
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_PATOLOGIA", patologiaInfeccao.getDescricao());
			} else { // INCLUSÃO
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_PATOLOGIA", patologiaInfeccao.getDescricao());
			}

			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Método que realiza a ação do botão cancelar
	 */
	public String cancelar() {
		final String retorno = this.voltarPara;

		this.patologiaInfeccao = new MciPatologiaInfeccao();
		this.patologiaInfeccao.setHigienizacaoMaos(true);
		this.situacao = true;
		this.emEdicao = false;
		this.voltarPara = null;
		this.permConsultaTela = false;

		return retorno;
	}

	/*
	 * Pesquisas SuggestionBox
	 */

	public List<MciDuracaoMedidaPreventiva> pesquisarDuracaoMedidaPreventiva(String parametro) {
		return this.returnSGWithCount(this.controleInfeccaoFacade.pesquisarDuracaoMedidaPreventivaPatologiaInfeccao(parametro),pesquisarDuracaoMedidaPreventivaCount(parametro));
	}

	public Long pesquisarDuracaoMedidaPreventivaCount(String parametro) {
		return this.controleInfeccaoFacade.pesquisarDuracaoMedidaPreventivaPatologiaInfeccaoCount(parametro);
	}

	public List<MciTopografiaInfeccao> pesquisarTopografiaInfeccao(String parametro) {
		return this.returnSGWithCount(this.controleInfeccaoFacade.pesquisarTopografiaInfeccaoPatologiaInfeccao(parametro),pesquisarTopografiaInfeccaoCount(parametro));
	}

	public Long pesquisarTopografiaInfeccaoCount(String parametro) {
		return this.controleInfeccaoFacade.pesquisarTopografiaInfeccaoPatologiaInfeccaoCount(parametro);
	}

	/*
	 * Getters e Setters
	 */

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public MciPatologiaInfeccao getPatologiaInfeccao() {
		return patologiaInfeccao;
	}

	public void setPatologiaInfeccao(MciPatologiaInfeccao patologiaInfeccao) {
		this.patologiaInfeccao = patologiaInfeccao;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public boolean isPermConsultaTela() {
		return permConsultaTela;
	}

	public void setPermConsultaTela(boolean permConsultaTela) {
		this.permConsultaTela = permConsultaTela;
	}

}