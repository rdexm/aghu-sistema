package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.vo.ProcedimentoReanimacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller Incluir/Editar um diagnostico
 * 
 * @author marcelo.corati
 * 
 */
public class ProcedimentoReanimacaoController  extends ActionController  {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6589546988357451478L;
	private final String PAGE_PESQUISA = "procedimentoReanimacaoList"; 
	private boolean permManterDiagnostico;
	private boolean edit;
	private Integer cidSeq;
	
	private boolean indSituacao;
	private Integer seq;
	private String descricao;
	private AbsComponenteSanguineo componente;
	
	private AfaMedicamento medicamento;
		
	@EJB
	private IPacienteFacade pacienteFacade;	
	
	@PostConstruct
	public void init() {
		begin(conversation);
		//this.permManterDiagnostico = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterDiagnostico", "alterar");
	}
	
	
	public void inicio() {
		// caso seja um cadastro novo
		if (getSeq() == null) {
			setEdit(false);
			setIndSituacao(true);
			descricao = null;
			componente = null;
			medicamento = null;

		// caso seja edição	
		}else{
			// busca dados e popula na tela
			setEdit(true);
			ProcedimentoReanimacaoVO procReanimacao = pacienteFacade.obterProcReanimacao(getSeq());
			descricao = procReanimacao.getDescricao();
			String ativo = "A";
			setIndSituacao(false);
			if(ativo.equals(procReanimacao.getIndSituacao())){
				setIndSituacao(true);	
			}
			componente = null;
			if(procReanimacao.getCompCodigo() != null){
				componente  = pacienteFacade.obterComponentePorId(procReanimacao.getCompCodigo());
			}
			medicamento = null;
			if(procReanimacao.getMatCodigo() != null){
				medicamento = pacienteFacade.obterMedicamentoPorId(procReanimacao.getMatCodigo());
			}
		}
	
	}
	
	public String gravar() {
		if((componente != null && medicamento != null) || (componente == null && medicamento == null)){
			apresentarMsgNegocio(Severity.ERROR,"MCO-00578");
			return null;
		}else{
			try {
				DominioSituacao situacao = DominioSituacao.I;
					if(indSituacao){
						situacao = DominioSituacao.A;
					}
					
					this.pacienteFacade.persistirProcedimentoReanimacao(seq,componente != null ? componente.getCodigo() : null, medicamento != null ? medicamento.getCodigo() : null, descricao, situacao);
					if (!isEdit()) {
						apresentarMsgNegocio(Severity.INFO,
								"MENSAGEM_PROC_REANIM_INSERIDO_SUCESSO");
					} else {
						apresentarMsgNegocio(Severity.INFO,
								"MENSAGEM_PROC_REANIM_ALTERADO_SUCESSO");
					}
					
					limpar();
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		return PAGE_PESQUISA;
		}
	}

	
	public String cancelar(){
		limpar();
		return PAGE_PESQUISA;
	}
	
	public List<AbsComponenteSanguineo> obterComponentes(String objPesquisa){
		return  this.returnSGWithCount(this.pacienteFacade.listarComponentesSuggestion(objPesquisa),obterComponentesCount(objPesquisa));
	}
	
	public Long obterComponentesCount(String objPesquisa){
		return this.pacienteFacade.listarComponentesSuggestionCount(objPesquisa);
	}
	
	public List<AfaMedicamento> obterMedicamentos(String objPesquisa){
		return  this.returnSGWithCount(this.pacienteFacade.listarMedicamentosSuggestion(objPesquisa),obterMedicamentosCount(objPesquisa));
		
	}
	
	public Long obterMedicamentosCount(String objPesquisa){
		return this.pacienteFacade.listarMedicamentosSuggestionCount(objPesquisa);
	
	}

	private  void limpar(){
		seq = null;
		indSituacao = true;
		descricao = null;
		componente = null;
		medicamento = null;
	}

	public boolean isPermManterDiagnostico() {
		return permManterDiagnostico;
	}

	public void setPermManterDiagnostico(boolean permManterDiagnostico) {
		this.permManterDiagnostico = permManterDiagnostico;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public boolean isIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public AbsComponenteSanguineo getComponente() {
		return componente;
	}


	public void setComponente(AbsComponenteSanguineo componente) {
		this.componente = componente;
	}


	public AfaMedicamento getMedicamento() {
		return medicamento;
	}


	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}
	
}
