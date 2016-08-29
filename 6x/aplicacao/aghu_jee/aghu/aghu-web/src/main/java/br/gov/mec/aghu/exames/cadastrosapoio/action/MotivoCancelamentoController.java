package br.gov.mec.aghu.exames.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class MotivoCancelamentoController extends ActionController {

	private static final long serialVersionUID = 7371540843593020017L;

	private static final String MOTIVO_CANCELAMENTO_LIST = "motivoCancelamentoList";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private AelMotivoCancelaExames motivoCancelamento;

	private Boolean indRetornaAExecutar;
	private Boolean indUsoLaboratorio;
	private Boolean indPermiteIncluirResultado;
	private Boolean indUsoColeta;
	private Boolean indPermiteComplemento;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

		if (this.motivoCancelamento != null && motivoCancelamento.getSeq() != null) {
			this.motivoCancelamento = this.examesFacade.obterMotivoCancelamentoPeloId(this.motivoCancelamento.getSeq());

			if(motivoCancelamento == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			indRetornaAExecutar = this.motivoCancelamento.getIndRetornaAExecutar().isSim();
			indUsoLaboratorio = this.motivoCancelamento.getIndUsoLaboratorio().isSim();
			indPermiteIncluirResultado = this.motivoCancelamento.getIndPermiteIncluirResultado();
			indUsoColeta = this.motivoCancelamento.getIndUsoColeta().isSim();
			indPermiteComplemento = this.motivoCancelamento.getIndPermiteComplemento().isSim();
			
		} else {
			this.motivoCancelamento =  new AelMotivoCancelaExames();
			indRetornaAExecutar = false;
			indUsoLaboratorio = true;
			indPermiteIncluirResultado = false;
			indUsoColeta = false;
			indPermiteComplemento = false;
			this.motivoCancelamento.setIndSituacao(DominioSituacao.A);
		}
		
		return null;
	
	}

	public String gravar() {
		
		try {
			
			boolean create = this.motivoCancelamento.getSeq() == null;
			
			if(indRetornaAExecutar){
				this.motivoCancelamento.setIndRetornaAExecutar(DominioSimNao.S);
			}else{
				this.motivoCancelamento.setIndRetornaAExecutar(DominioSimNao.N);
			}
			
			if(indUsoLaboratorio){
				this.motivoCancelamento.setIndUsoLaboratorio(DominioSimNao.S);
			}else{
				this.motivoCancelamento.setIndUsoLaboratorio(DominioSimNao.N);
			}
			
			if(indPermiteIncluirResultado){
				this.motivoCancelamento.setIndPermiteIncluirResultado(true);
			}else{
				this.motivoCancelamento.setIndPermiteIncluirResultado(false);
			}
			
			if(indUsoColeta){
				this.motivoCancelamento.setIndUsoColeta(DominioSimNao.S);
			}else{
				this.motivoCancelamento.setIndUsoColeta(DominioSimNao.N);
			}
			
			if(indPermiteComplemento){
				this.motivoCancelamento.setIndPermiteComplemento(DominioSimNao.S);					
			}else{
				this.motivoCancelamento.setIndPermiteComplemento(DominioSimNao.N);
			}

			this.cadastrosApoioExamesFacade.persistMotivoCancelamento(motivoCancelamento);

			if (create) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INCLUSAO_MOTIVO_CANCELAMENTO", motivoCancelamento.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERACAO_MOTIVO_CANCELAMENTO", motivoCancelamento.getDescricao());
			}

			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		this.motivoCancelamento=null;
		return MOTIVO_CANCELAMENTO_LIST;
	}

	public AelMotivoCancelaExames getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(AelMotivoCancelaExames motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public Boolean getIndRetornaAExecutar() {
		return indRetornaAExecutar;
	}

	public void setIndRetornaAExecutar(Boolean indRetornaAExecutar) {
		this.indRetornaAExecutar = indRetornaAExecutar;
	}

	public Boolean getIndUsoLaboratorio() {
		return indUsoLaboratorio;
	}

	public void setIndUsoLaboratorio(Boolean indUsoLaboratorio) {
		this.indUsoLaboratorio = indUsoLaboratorio;
	}

	public Boolean getIndPermiteIncluirResultado() {
		return indPermiteIncluirResultado;
	}

	public void setIndPermiteIncluirResultado(Boolean indPermiteIncluirResultado) {
		this.indPermiteIncluirResultado = indPermiteIncluirResultado;
	}

	public Boolean getIndUsoColeta() {
		return indUsoColeta;
	}

	public void setIndUsoColeta(Boolean indUsoColeta) {
		this.indUsoColeta = indUsoColeta;
	}

	public Boolean getIndPermiteComplemento() {
		return indPermiteComplemento;
	}

	public void setIndPermiteComplemento(Boolean indPermiteComplemento) {
		this.indPermiteComplemento = indPermiteComplemento;
	}
}