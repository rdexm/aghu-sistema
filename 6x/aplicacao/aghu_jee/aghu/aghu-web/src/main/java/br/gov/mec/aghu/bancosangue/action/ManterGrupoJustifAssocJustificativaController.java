package br.gov.mec.aghu.bancosangue.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateFormatUtil;




public class ManterGrupoJustifAssocJustificativaController extends ActionController{

	private static final long serialVersionUID = 8859277329110153362L;

	private static final String PESQUISAR_GRUPO_JUSTIF_ASSOC_JUSTIFICATIVA = "bancodesangue-pesquisarGrupoJustifAssocJustificativa";

	private AbsGrupoJustificativaComponenteSanguineo grupoJustificativaComponenteSanguineo;
	
	private Boolean situacaoGrupo;
	
	private Boolean modoEdicao;
	
	private String dataCriado;
	
	private String dataAlterado;
	
	private String origemRequisicao;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	protected enum EnumManterGrupoJustificativaAssociado{
		MSG_GRUPO_JUSTIFICATIVA_ASSOCIADA_GRAVADA_SUCESSO,
		MSG_GRUPO_JUSTIFICATIVA_ASSOCIADA_ALTERADA_SUCESSO;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	
		if(grupoJustificativaComponenteSanguineo != null && grupoJustificativaComponenteSanguineo.getSeq() != null){
			
			grupoJustificativaComponenteSanguineo =	bancoDeSangueFacade.obterGrupoJustificativaPorId(grupoJustificativaComponenteSanguineo.getSeq());

			if(grupoJustificativaComponenteSanguineo == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			setSituacaoGrupo(getGrupoJustificativaComponenteSanguineo().getSituacao().isAtivo());
			setDataAlterado(DateFormatUtil.formataTimeStamp(getGrupoJustificativaComponenteSanguineo().getAlteradoEm()));
			setDataCriado(DateFormatUtil.formataTimeStamp(getGrupoJustificativaComponenteSanguineo().getCriadoEm()));
		} else {
			limpar();
		}
		
		return null;
	
	}
	
	private void limpar() {
		setSituacaoGrupo(Boolean.FALSE);
		setGrupoJustificativaComponenteSanguineo(new AbsGrupoJustificativaComponenteSanguineo());
		limparAlteradoPor();
		limparCriadoPor();
		setDataAlterado(null);		
		setDataCriado(null);
	}

	
	public void limparCriadoPor(){
		getGrupoJustificativaComponenteSanguineo().setServidor(null);
	}
	
	public void limparAlteradoPor(){
		getGrupoJustificativaComponenteSanguineo().setServidorAlterado(null);
	}
	
	public String cancelar() {
		limpar();
		return PESQUISAR_GRUPO_JUSTIF_ASSOC_JUSTIFICATIVA;
	}
	
	public String gravar() {
		try {
			if (getSituacaoGrupo()) {
				getGrupoJustificativaComponenteSanguineo().setSituacao(DominioSituacao.A);
			} else {
				getGrupoJustificativaComponenteSanguineo().setSituacao(DominioSituacao.I);
			}
			
			boolean atualizado = getGrupoJustificativaComponenteSanguineo().getSeq() != null;
			
			bancoDeSangueFacade.persistirGrupoJustificativaComponenteSanguineo(getGrupoJustificativaComponenteSanguineo());
			
			if (atualizado) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_JUSTIFICATIVAS_ASSOCIADAS");
			} else  {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_JUSTIFICATIVAS_ASSOCIADAS");
			}	
			
			return cancelar();
			
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public List<RapServidores> pesquisarListaServidores(Object parametro) {
		return this.registroColaboradorFacade.pesquisarServidores(parametro);
	}
	
	public AbsGrupoJustificativaComponenteSanguineo getGrupoJustificativaComponenteSanguineo() {
		if(grupoJustificativaComponenteSanguineo == null){
			grupoJustificativaComponenteSanguineo = new AbsGrupoJustificativaComponenteSanguineo();
		}
		return grupoJustificativaComponenteSanguineo;
	}
														 
	public void setGrupoJustificativaComponenteSanguineo(AbsGrupoJustificativaComponenteSanguineo grupo) {
		this.grupoJustificativaComponenteSanguineo = grupo;
	}

	public Boolean getSituacaoGrupo() {
		return situacaoGrupo;
	}

	public void setSituacaoGrupo(Boolean situacaoGrupo) {
		this.situacaoGrupo = situacaoGrupo;
	}

	public IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}

	public void setBancoDeSangueFacade(IBancoDeSangueFacade bancoDeSangueFacade) {
		this.bancoDeSangueFacade = bancoDeSangueFacade;
	}
	
	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public String getDataCriado() {
		return dataCriado;
	}

	public void setDataCriado(String dataCriado) {
		this.dataCriado = dataCriado;
	}

	public String getDataAlterado() {
		return dataAlterado;
	}

	public void setDataAlterado(String dataAlterado) {
		this.dataAlterado = dataAlterado;
	}

	public String getOrigemRequisicao() {
		return origemRequisicao;
	}

	public void setOrigemRequisicao(String origemRequisicao) {
		this.origemRequisicao = origemRequisicao;
	}
}