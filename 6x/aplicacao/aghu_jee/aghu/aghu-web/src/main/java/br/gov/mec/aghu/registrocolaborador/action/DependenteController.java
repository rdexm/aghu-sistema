package br.gov.mec.aghu.registrocolaborador.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapDependentesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class DependenteController extends ActionController {

	private static final long serialVersionUID = -4774747586915324080L;
	
	private static final String PESQUISAR_DEPENDENTE = "pesquisarDependente";

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;	
	
	private RapDependentes dependente;

	private RapServidores servidor;
	
	private boolean editando;


	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public String iniciar() {
	 


		if (editando){
			Enum[] fetchArgsLeftJoin = {RapDependentes.Fields.PACIENTE};
			this.dependente   = registroColaboradorFacade.obterDependente(dependente.getId(), null, fetchArgsLeftJoin);

			if(dependente == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return PESQUISAR_DEPENDENTE;
			}
		}else {
			this.dependente   = new RapDependentes();
			this.dependente.setIndSituacao(DominioSituacao.A);			
		}
		return null;
	
	}
	
	/**
	 * Salva inclusão ou alteração de registro.
	 */
	public String salvar() {

		try {
			// atualiza prontuario
			if ( dependente.getPaciente() != null ) {				
			   this.dependente.setPacProntuario(dependente.getPaciente().getProntuario());
			}
			
			if (editando) {
				registroColaboradorFacade.alterar(this.dependente, servidor);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERACAO_DEPENDENTE");
				
			} else {
				RapDependentesId  id = new RapDependentesId();
				id.setPesCodigo(servidor.getPessoaFisica().getCodigo());								
				this.dependente.setId(id);				
				registroColaboradorFacade.salvarDependente(this.dependente);				
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_DEPENDENTE");
			}
			
			return cancelarEdicao();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}


	/**
	 * Limpa formulário de pesquisa e volta para pesquisa.
	 */
	public String cancelarEdicao() {
		this.editando = false;
		this.dependente = null;
		return PESQUISAR_DEPENDENTE;
	}


	/**
	 * Método para indicar quando o servidor pode cadastrar dependentes.
	 * Utilizada na interface "pesquisarDependente" para saber quando renderizar
	 * o botão "Adicionar"
	 * 
	 * @return {@code true}, para servidor que pode cadastrar dependente e
	 *         {@code false} para o caso contrário
	 */
	public Boolean permiteAdicionarDependente() {

		if (this.servidor == null) {
			return false;
		}
		
		if (this.dependente.getPessoaFisica().temDependentes() || this.servidor.getVinculo().getIndDependente() == DominioSimNao.S) {

			if (this.servidor.getIndSituacao() == DominioSituacaoVinculo.P) {
				if (this.servidor.getDtFimVinculo().after(new Date())) {
					return true;
				}
				return false;
			} 
			if (this.servidor.getIndSituacao() == DominioSituacaoVinculo.I) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public List<AipPacientes> pesquisarPacientes(String objParam){
		return pacienteFacade.obterPacientesPorProntuarioOuCodigo((String) objParam);
	}
	
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapDependentes getDependente() {
		return dependente;
	}

	public void setDependente(RapDependentes dependente) {
		this.dependente = dependente;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}
}