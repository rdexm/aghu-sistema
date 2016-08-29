package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterPatologistaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3124300537037208760L;

	private static final String MANTER_PATOLOGISTA = "manterPatologista";

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@Inject @Paginator
	private DynamicDataModel<AelPatologista> dataModel;
	
	private AelPatologista selecionado;
	
	private Integer seq; 
	private String nome; 
	private DominioFuncaoPatologista funcao;
	private DominioSimNao permiteLibLaudo;
	private DominioSituacao situacao;
	private RapServidores servidor;
	private String nomeParaLaudo;
			
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.seq = null; 
		this.nome = null; 
		this.funcao = null;
		this.permiteLibLaudo = null;
		this.situacao = null;
		this.servidor = null;
		this.nomeParaLaudo = null;
	}

	public void excluir() {
		try {
			this.examesPatologiaFacade.excluirPatologista(selecionado.getSeq());
			dataModel.reiniciarPaginator();
			this.apresentarMsgNegocio( Severity.INFO,"MENSAGEM_AEL_PATOLOGISTA_EXCLUSAO_SUCESSO");
			selecionado = null;
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return MANTER_PATOLOGISTA;
	}
	
	public String editar(){
		return MANTER_PATOLOGISTA;
	}
	
	@Override
	public Long recuperarCount() {
		return this.examesPatologiaFacade.listarPatologistasCount( seq, nome, funcao, 
																   (permiteLibLaudo!=null?permiteLibLaudo.isSim():null), situacao, 
																   (servidor != null ? servidor.getId() : null),  
																   nomeParaLaudo);
	}

	@Override
	public List<AelPatologista> recuperarListaPaginada(final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc) {
		return this.examesPatologiaFacade.listarPatologistas(firstResult, maxResults, seq, nome, funcao, 
															 (permiteLibLaudo!=null?permiteLibLaudo.isSim():null), situacao, 
															 (servidor != null ? servidor.getId() : null), nomeParaLaudo);
	}

	public List<RapServidores> buscarServidor(String servidor) {
		return this.registroColaboradorFacade.pesquisarServidoresPorCodigoOuDescricao(servidor);
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioFuncaoPatologista getFuncao() {
		return funcao;
	}

	public void setFuncao(DominioFuncaoPatologista funcao) {
		this.funcao = funcao;
	}

	public DominioSimNao getPermiteLibLaudo() {
		return permiteLibLaudo;
	}

	public void setPermiteLibLaudo(DominioSimNao permiteLibLaudo) {
		this.permiteLibLaudo = permiteLibLaudo;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public String getNomeParaLaudo() {
		return nomeParaLaudo;
	}

	public void setNomeParaLaudo(String nomeParaLaudo) {
		this.nomeParaLaudo = nomeParaLaudo;
	}
	
	public DynamicDataModel<AelPatologista> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelPatologista> dataModel) {
	 this.dataModel = dataModel;
	}

	public AelPatologista getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelPatologista selecionado) {
		this.selecionado = selecionado;
	}
}