package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.model.PdtInstrPorEquip;
import br.gov.mec.aghu.model.PdtInstrPorEquipId;
import br.gov.mec.aghu.model.PdtInstrumental;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class InstrumentosEquipamentosCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}


	private static final long serialVersionUID = -9080940970337693983L;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	private Boolean ativo;
	private Integer codigo;
	private PdtInstrumental instrumento;
	private PdtEquipamento equipamento;
	private List<PdtEquipamento> equipamentos;
	private PdtInstrPorEquip instrumentoEquipamento;
	private List<PdtInstrPorEquip> instrumentosEquipamentos;
	private Boolean ativaBotaoGravarEquipamento;
	private Boolean ativaCrudEquipamento;
	
	private final String PAGE_LIST_INSTRUMENTAL = "instrumentosList";
	
	public void inicio() {
	 

	 

		instrumentoEquipamento = new PdtInstrPorEquip();
		if (instrumento != null && this.instrumento.getSeq() != null) { 			
 			carregarListaEquipamentosParaInstrumento();
			if (instrumento.getIndSituacao().equals(DominioSituacao.A)) {
				ativo = true;
			} else {
				ativo = false;
			}
			ativaCrudEquipamento = Boolean.TRUE;
		} else {
			instrumento = new PdtInstrumental();
			codigo = null;
			ativo = true;
			ativaCrudEquipamento = Boolean.FALSE;
		}
		equipamento = null;
		ativaBotaoGravarEquipamento = Boolean.TRUE;
	
	}
	
	
	public String gravarInstrumento() {		
		try {
			if (ativo) {
				instrumento.setIndSituacao(DominioSituacao.A);
			} else {
				instrumento.setIndSituacao(DominioSituacao.I);
			}
			String mensagemSucesso = blocoCirurgicoCadastroApoioFacade.persistirPdtInstrumental(instrumento);			
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);	
			return null;
		}
		ativaCrudEquipamento = Boolean.TRUE;
		return null;
	}
	
	public String cancelar() {
		codigo = null;
		ativo = true;
		this.setInstrumento(new PdtInstrumental());
		instrumentosEquipamentos= null;
		return PAGE_LIST_INSTRUMENTAL;
	}
	
	public List<PdtEquipamento> listarEquipamentos(String descricao) {
		return this.returnSGWithCount(blocoCirurgicoProcDiagTerapFacade.listarPdtEquipamentoAtivoPorDescricao(descricao),listarEquipamentosCount(descricao));
	}
	
	public Long listarEquipamentosCount(String descricao) {
		return blocoCirurgicoProcDiagTerapFacade.listarPdtEquipamentoAtivoPorDescricaoCount(descricao);
	}
	
	public void carregarListaEquipamentosParaInstrumento() {
		instrumentosEquipamentos = blocoCirurgicoCadastroApoioFacade.listarPdtInstrPorEquip(instrumento.getSeq());
		blocoCirurgicoCadastroApoioFacade.refreshPdtInstrPorEquip(instrumentosEquipamentos);
	}
	
	public String gravarEquipamento() {
		instrumentoEquipamento.setId(new PdtInstrPorEquipId(equipamento.getSeq(),instrumento.getSeq()));
		try {
			String mensagemSucesso = blocoCirurgicoCadastroApoioFacade.persistirPdtInstrPorEquip(instrumentoEquipamento);
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
			instrumentoEquipamento = new PdtInstrPorEquip();
			equipamento = null;
			desativarBotaoGravarEquipamento();
			carregarListaEquipamentosParaInstrumento();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);	
		}
		return null;
	}
	
	
	public String excluirEquipamento(Short eqpSeq) {
		equipamento = blocoCirurgicoProcDiagTerapFacade.obterPdtEquipamentoPorSeq(eqpSeq);
		instrumentoEquipamento = blocoCirurgicoCadastroApoioFacade.obterPdtInstrPorEquipPorId(new PdtInstrPorEquipId(equipamento.getSeq(),instrumento.getSeq()));
		String mensagemSucesso = blocoCirurgicoCadastroApoioFacade.removerPdtInstrPorEquip(instrumentoEquipamento);
		apresentarMsgNegocio(Severity.INFO,mensagemSucesso);		
		instrumentoEquipamento = new PdtInstrPorEquip();
		equipamento = null;
		carregarListaEquipamentosParaInstrumento();
		return null;
	}
	
	public void ativarBotaoGravarEquipamento() {
		ativaBotaoGravarEquipamento = Boolean.FALSE;
	}
	
	public void desativarBotaoGravarEquipamento() {
		ativaBotaoGravarEquipamento = Boolean.TRUE;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public PdtInstrumental getInstrumento() {
		return instrumento;
	}

	public void setInstrumento(PdtInstrumental instrumento) {
		this.instrumento = instrumento;
	}

	public PdtEquipamento getEquipamento() {
		return equipamento;
	}

	public void setEquipamento(PdtEquipamento equipamento) {
		this.equipamento = equipamento;
	}

	public List<PdtEquipamento> getEquipamentos() {
		return equipamentos;
	}

	public void setEquipamentos(List<PdtEquipamento> equipamentos) {
		this.equipamentos = equipamentos;
	}

	public PdtInstrPorEquip getInstrumentoEquipamento() {
		return instrumentoEquipamento;
	}

	public void setInstrumentoEquipamento(PdtInstrPorEquip instrumentoEquipamento) {
		this.instrumentoEquipamento = instrumentoEquipamento;
	}

	public List<PdtInstrPorEquip> getInstrumentosEquipamentos() {
		return instrumentosEquipamentos;
	}

	public void setInstrumentosEquipamentos(List<PdtInstrPorEquip> instrumentosEquipamentos) {
		this.instrumentosEquipamentos = instrumentosEquipamentos;
	}

	public void setAtivaBotaoGravarEquipamento(
			Boolean ativaBotaoGravarEquipamento) {
		this.ativaBotaoGravarEquipamento = ativaBotaoGravarEquipamento;
	}

	public Boolean getAtivaBotaoGravarEquipamento() {
		return ativaBotaoGravarEquipamento;
	}

	public void setAtivaCrudEquipamento(Boolean ativaCrudEquipamento) {
		this.ativaCrudEquipamento = ativaCrudEquipamento;
	}

	public Boolean getAtivaCrudEquipamento() {
		return ativaCrudEquipamento;
	}

}