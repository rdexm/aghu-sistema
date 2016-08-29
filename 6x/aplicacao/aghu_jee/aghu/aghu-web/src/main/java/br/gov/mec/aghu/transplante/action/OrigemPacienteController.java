package br.gov.mec.aghu.transplante.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MtxOrigens;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class OrigemPacienteController extends ActionController {

	
	private static final long serialVersionUID = -8194798745495396555L;
	private static final String PAGE_ORIGENS_PACIENTES_LIST = "origensPacientesList";
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private MtxOrigens mtxOrigens = new MtxOrigens();
	private Boolean modoEdicao = Boolean.FALSE;
	private Boolean indSituacao = Boolean.FALSE;
	private String mensagem;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**Realiza a inserção ou update de um registro na tabela**/
	public String gravar(){
		
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		try {
			mtxOrigens.setDescricao(mtxOrigens.getDescricao().trim());
			mtxOrigens.setServidor(servidor);
			mtxOrigens.setSituacao(DominioSituacao.getInstance(this.indSituacao));
			if(modoEdicao == false){
				mensagem = "MENSAGEM_SUCESSO_INCLUSAO_ORIGEM_PACIENTE";
				mtxOrigens.setCriadoEm(new Date());
			}else{
				mensagem = "MENSAGEM_SUCESSO_EDICAO_ORIGEM_PACIENTE";
			}
			transplanteFacade.validarInputOrigemPaciente(mtxOrigens);
			transplanteFacade.validarInclusaoOrigemPaciente(mtxOrigens);
			transplanteFacade.gravarAtualizarOrigemPaciente(mtxOrigens);
			this.apresentarMsgNegocio(Severity.INFO, mensagem);
			limparInclusao();
			return voltarPagPesquisa();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public void limparInclusao(){
		this.mtxOrigens = new MtxOrigens();
		this.indSituacao = null;
		this.modoEdicao = false;
	}
	
	/**Volta para a pagina de pesquisa**/
	public String voltarPagPesquisa() {
		limparInclusao();
		return PAGE_ORIGENS_PACIENTES_LIST;
	}
	
	//Gets e Sets
	public MtxOrigens getMtxOrigens() {
		return mtxOrigens;
	}

	public void setMtxOrigens(MtxOrigens mtxOrigens) {
		this.mtxOrigens = mtxOrigens;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}
}
