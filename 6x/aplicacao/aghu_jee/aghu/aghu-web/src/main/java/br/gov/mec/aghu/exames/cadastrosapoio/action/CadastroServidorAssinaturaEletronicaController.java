package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelServidorUnidAssinaElet;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroServidorAssinaturaEletronicaController extends ActionController{


	private static final Log LOG = LogFactory.getLog(CadastroServidorAssinaturaEletronicaController.class);

	private static final long serialVersionUID = -505638071516993890L;		
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private SecurityController securityController;	
	
	
	private List<AelServidorUnidAssinaElet> listaServidorUnidAssinaElet;	
	
	private AelServidorUnidAssinaElet servidorUnidAssinaElet;	
	
	private AelUnidExecUsuario usuarioUnidadeExecutora;	
	
	private AghUnidadesFuncionais unidadeFuncional;		
	
	private RapServidores servidor;		
		
	private Boolean ativo;	
	
	private boolean habilitaBtGravarEAcaoAtivarInativar; 
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		habilitaBtGravarEAcaoAtivarInativar = securityController.usuarioTemPermissao("manterServidoresAssinaturaEletronica", "persistir");
	}

	public void iniciar() {
	 

		servidorUnidAssinaElet = new AelServidorUnidAssinaElet();		 
		pesquisar();
	
	}
			
	private void obterUnidadeFuncionalUsuario() {
		// Obtem o usuario da unidade executora
		RapServidores servidorLogado=null;
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
			//this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(servidorLogado.getId());
		} catch (ApplicationBusinessException e) {
			LOG.error("Não encontrou o servidor logado!", e);
		}
		

		// Resgata a unidade executora associada ao usuario
		if (this.usuarioUnidadeExecutora != null) {
			this.unidadeFuncional = this.usuarioUnidadeExecutora.getUnfSeq();
		}else {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVIDOR_LOGADO_SEM_UNIDADE_EXECUTORA", obterLoginUsuarioLogado());
		}
	}
	
	private void obterValorAtivo() {
		boolean existeAtivo = false;
		if(this.listaServidorUnidAssinaElet != null) {
			for(AelServidorUnidAssinaElet item : this.listaServidorUnidAssinaElet) {
				if(DominioSituacao.A.equals(item.getSituacao())) {
					existeAtivo = true;
					break;
				}
			}
		}
		if(existeAtivo) {
			this.ativo = false;
		} else {
			this.ativo = true;
		}
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String parametro) {		
		List<AghUnidadesFuncionais> listaUnidadeFuncional = this.aghuFacade.pesquisarUnidadeFuncionalPorSeqSigla(parametro);
		if(listaUnidadeFuncional.isEmpty()){
			listaUnidadeFuncional = this.aghuFacade.pesquisarUnidadeFuncionalPorDescricao(parametro);
		}
		return this.returnSGWithCount(listaUnidadeFuncional,pesquisarUnidadeFuncionalCount(parametro));
	}
	
	public Long pesquisarUnidadeFuncionalCount(String parametro) {		
		Long count = this.aghuFacade.pesquisarUnidadeFuncionalPorSeqSiglaCount(parametro);
		if(count == 0l){
			count = this.aghuFacade.pesquisarUnidadeFuncionalPorDescricaoCount(parametro);
		}
		return count;
	}
	
	public List<RapServidores> pesquisarServidor (String servidor) {		
		List<RapServidores> listaServidor = this.registroColaboradorFacade.pesquisarServidorPorVinculo(servidor);
		if(listaServidor.isEmpty()){
			listaServidor = this.registroColaboradorFacade.pesquisarServidorPorMatriculaNome(servidor);
		}
		return listaServidor;
	}
	
	public Long pesquisarServidorCount(String servidor) {		
		Long count = this.registroColaboradorFacade.pesquisarServidorPorVinculoCount(servidor);
		if(count == 0l){
			count = this.registroColaboradorFacade.pesquisarServidorPorMatriculaNomeCount(servidor);
		}
		return count;
	}
	
	public void pesquisar() {		
		if(unidadeFuncional==null) {	
			obterUnidadeFuncionalUsuario();  
		}
		if(unidadeFuncional != null){
			this.listaServidorUnidAssinaElet = this.cadastrosApoioExamesFacade.pesquisarServidorUnidAssinaEletPorUnfSeq(unidadeFuncional.getSeq());
			obterValorAtivo();
		}
	}
		
	public void selecionarServidor(AelServidorUnidAssinaElet servidorUnidAssinaElet) {
		this.servidorUnidAssinaElet = servidorUnidAssinaElet;
		mudarStatusSituacao();
		obterValorAtivo();
	}
		
	public void mudarStatusSituacao() {			
		try {
			//Caso esteja ativando um item, desativa todos os outros ativos, pois deve existir apenas 1 ativo
			if(DominioSituacao.I.equals(servidorUnidAssinaElet.getSituacao())) {
				for(AelServidorUnidAssinaElet item : this.listaServidorUnidAssinaElet) {
					if(DominioSituacao.A.equals(item.getSituacao())) {
						this.cadastrosApoioExamesFacade.atualizarAelServidorUnidAssinaElet(item);
					}
				}
			}
			
			this.cadastrosApoioExamesFacade.atualizarAelServidorUnidAssinaElet(servidorUnidAssinaElet);
			if(servidorUnidAssinaElet.getSituacao().equals(DominioSituacao.I)){			
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INATIVACAO_SERV_ASS_ELET", servidorUnidAssinaElet.getServidor().getPessoaFisica().getNome());
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATIVACAO_SERV_ASS_ELET", servidorUnidAssinaElet.getServidor().getPessoaFisica().getNome());
			} 			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
		
	public void gravar() {
		try {
			
			this.cadastrosApoioExamesFacade.inserirAelServidorUnidAssinaElet(unidadeFuncional, servidor, ativo);
			
			//Caso criou um item ativo, desativa todos os outros ativos, pois deve existir apenas 1 ativo
			if(this.ativo && this.listaServidorUnidAssinaElet != null) {
				for(AelServidorUnidAssinaElet item : this.listaServidorUnidAssinaElet) {
					if(DominioSituacao.A.equals(item.getSituacao())) {
						this.cadastrosApoioExamesFacade.atualizarAelServidorUnidAssinaElet(item);
					}
				}
			}

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVACAO_SERV_ASS_ELET", servidor.getPessoaFisica().getNome());
			this.servidor = null;
			pesquisar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}	
	}	
	
	public void limparPesquisa() {
		this.listaServidorUnidAssinaElet = null;
		this.unidadeFuncional = null;	
		this.servidor = null;
		pesquisar();
	}	
	

	public IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	public void setExamesFacade(IExamesFacade examesFacade) {
		this.examesFacade = examesFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}
	
	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(
			AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}

	public List<AelServidorUnidAssinaElet> getListaServidorUnidAssinaElet() {
		return listaServidorUnidAssinaElet;
	}

	public void setListaServidorUnidAssinaElet(
			List<AelServidorUnidAssinaElet> listaServidorUnidAssinaElet) {
		this.listaServidorUnidAssinaElet = listaServidorUnidAssinaElet;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}


	public AelServidorUnidAssinaElet getServidorUnidAssinaElet() {
		return servidorUnidAssinaElet;
	}

	public void setServidorUnidAssinaElet(
			AelServidorUnidAssinaElet servidorUnidAssinaElet) {
		this.servidorUnidAssinaElet = servidorUnidAssinaElet;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public boolean isHabilitaBtGravarEAcaoAtivarInativar() {
		return habilitaBtGravarEAcaoAtivarInativar;
	}

	public void setHabilitaBtGravarEAcaoAtivarInativar(
			boolean habilitaBtGravarEAcaoAtivarInativar) {
		this.habilitaBtGravarEAcaoAtivarInativar = habilitaBtGravarEAcaoAtivarInativar;
	}
	
}
