package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.model.MptTipoIntercorrencia;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterCadastroTiposIntercorrentesController extends ActionController {	
	
	private static final String MENSAGEM_TIPO_INTERCORRENTE_EDITADA_COM_SUCESSO = "MENSAGEM_TIPO_INTERCORRENTE_EDITADA_COM_SUCESSO";

	private static final String MENSAGEM_TIPO_INTERCORRENTE_GRAVADO_COM_SUCESSO = "MENSAGEM_TIPO_INTERCORRENTE_GRAVADO_COM_SUCESSO";

	private static final String DESCRICAO_EXISTE = "DESCRICAO_EXISTE";

	private static final String CAMPO_OBRIGATORIO_TIPO_INTERCORRENTE = "CAMPO_OBRIGATORIO_TIPO_INTERCORRENTE";

	private static final String POSSUI_VINCULO = "POSSUI_VINCULO";

	/**
	 * 
	 */
	private static final long serialVersionUID = -2827042912162177173L;

	private static final String PAGE_PESQUISAR_TIPOS_INTERCORRENTES = "procedimentoterapeutico-manterCadastroTiposIntercorrentesList";
	
	private Boolean situacao;
	
	private boolean emEdicao;
	
	private MptTipoIntercorrencia tipoIntercorrente;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacede;
	
	public void iniciar(){
		if(tipoIntercorrente != null && tipoIntercorrente.getSeq() != null){
			if(tipoIntercorrente.getIndSituacao().isAtivo()){
				this.situacao = true;
			}else{
				this.situacao = false;
			}
			this.emEdicao = true;
		}else{
			tipoIntercorrente = new MptTipoIntercorrencia();
			this.situacao = true;
			this.emEdicao = false;
		}
	}
	
	public String salvar() throws ApplicationBusinessException{
		if(emEdicao){
			if(situacao){
				alterarTipoIntercorrente();
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_TIPO_INTERCORRENTE_EDITADA_COM_SUCESSO);
				return PAGE_PESQUISAR_TIPOS_INTERCORRENTES;
			}else if(!situacao && this.procedimentoTerapeuticoFacede.verificarVinculoPorTipoIntercorrente(this.tipoIntercorrente.getSeq())){
				apresentarMsgNegocio(Severity.ERROR, POSSUI_VINCULO);
				return null;
			}
			else{
				openDialog("modalPossuiVinculoWG");
			}
		}else{
			if(this.tipoIntercorrente.getDescricao() == null || this.tipoIntercorrente.getDescricao().length() == 0){
				apresentarMsgNegocio(Severity.ERROR, CAMPO_OBRIGATORIO_TIPO_INTERCORRENTE);
				return null;
			}else if(this.procedimentoTerapeuticoFacede.verificarDescricaoExistente(this.tipoIntercorrente.getDescricao())){
				apresentarMsgNegocio(Severity.ERROR, DESCRICAO_EXISTE);
				return null;
			}else{
				this.procedimentoTerapeuticoFacede.salvarTipoIntercorrente(this.tipoIntercorrente, this.situacao, this.emEdicao);
				tipoIntercorrente = null;
				apresentarMsgNegocio(Severity.INFO, MENSAGEM_TIPO_INTERCORRENTE_GRAVADO_COM_SUCESSO);
				return PAGE_PESQUISAR_TIPOS_INTERCORRENTES;
			}
		}
		return null;
	}
	
	public void alterarTipoIntercorrente() throws ApplicationBusinessException{
		this.procedimentoTerapeuticoFacede.salvarTipoIntercorrente(this.tipoIntercorrente, this.situacao, this.emEdicao);
		tipoIntercorrente = null;
	}
	
	public String cancelar(){
		tipoIntercorrente = new MptTipoIntercorrencia();
		this.situacao = true;
		return PAGE_PESQUISAR_TIPOS_INTERCORRENTES;
	}
	
	public String redirecionar(){
		return PAGE_PESQUISAR_TIPOS_INTERCORRENTES;
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

	public MptTipoIntercorrencia getTipoIntercorrente() {
		return tipoIntercorrente;
	}

	public void setTipoIntercorrente(MptTipoIntercorrencia tipoIntercorrente) {
		this.tipoIntercorrente = tipoIntercorrente;
	}	
}
