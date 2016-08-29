package br.gov.mec.aghu.registrocolaborador.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.RapInstituicaoQualificadora;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapQualificacoesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class GraduacaoServidorController extends ActionController {

	private static final String PESQUISAR_GRADUACAO_SERVIDOR = "pesquisarGraduacaoServidor";
	private static final Enum[] fetchArgsInnerJoin = {RapQualificacao.Fields.TIPO_QUALIFICACAO};
	private static final Enum[] fetchArgsLeftJoin = {RapQualificacao.Fields.INSTITUICAO_QUALIFICADORA};
	
	private static final long serialVersionUID = 5748262630099938585L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	/**
	 * Objetos do formulário para edição, inclusão ou exclusão.
	 */
	private RapQualificacao qualificacao;
	private RapServidores servidorQualificacao;
	private boolean edicao;
	
	private String voltarPara;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;


	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(edicao){
			
			qualificacao = registroColaboradorFacade.obterQualificacao(qualificacao.getId(), fetchArgsInnerJoin, fetchArgsLeftJoin);

			if(qualificacao == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		} else {
			qualificacao = new RapQualificacao();
		}
		
		return null;
	
	} 

	public String salvar() {

		try {
			if (edicao) {
				RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
				registroColaboradorFacade.alterar(qualificacao, servidorLogado);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_GRADUACAO_SERVIDOR");
				
			} else {
				this.qualificacao.setId(new RapQualificacoesId(servidorQualificacao.getPessoaFisica()));
				registroColaboradorFacade.incluir(qualificacao);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_GRADUACAO_SERVIDOR");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return cancelar() ;
	}

	public String cancelar(){
		servidorQualificacao = null;
		qualificacao = null;
		edicao = false;
		
		if(voltarPara != null){
			return voltarPara;
			
		} else {
			return PESQUISAR_GRADUACAO_SERVIDOR;
		}
	}
	
	/**
	 * Retorna uma lista de Tipo Qualificação
	 * @param param
	 * @return List<RapTipoQualificacao>
	 */
	public List<RapTipoQualificacao> buscarCursos(String param){
		return cadastrosBasicosFacade.pesquisarCursoGraduacao(param);
	} 

	/**
	 * Retorna uma de Instituições Qualificadoras
	 */
	public List<RapInstituicaoQualificadora> pesquisarInstituicao(String param){
		return this.cadastrosBasicosFacade.pesquisarInstituicao(param);
	}

	// getters & setters

	public RapQualificacao getQualificacao() {
		return qualificacao;
	}

	public void setQualificacao(RapQualificacao qualificacao) {
		this.qualificacao = qualificacao;
	}

	public RapServidores getServidorQualificacao() {
		return servidorQualificacao;
	}

	public void setServidorQualificacao(RapServidores servidorQualificacao) {
		this.servidorQualificacao = servidorQualificacao;
	} 

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}
}