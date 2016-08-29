package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirgId;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroTurnoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(CadastroTurnoController.class);

	private static final long serialVersionUID = 1938842027342597891L;


	@EJB
	private IBlocoCirurgicoCadastroApoioFacade cirurgiasFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	private MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
	private MbcHorarioTurnoCirg mbcHorarioTurnoCirgBusca = new MbcHorarioTurnoCirg();
	private MbcHorarioTurnoCirgId idDelecao;
	private MbcHorarioTurnoCirgId id;
	private List<MbcHorarioTurnoCirg> listaTurnos;
	
	private boolean emEdicao;
	private boolean pesquisou;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
		this.limparPesquisa();
	}

	public void pesquisar() {
		if(emEdicao){
			cancelarEdicao();
		}
		listaTurnos = cirurgiasFacade.buscarMbcHorarioTurnoCirg(this.getMbcHorarioTurnoCirgBusca());
		pesquisou = true;
	}
	
	public List<MbcTurnos> pesquisarTurnos(String objPesquisa) {
		return this.returnSGWithCount(cirurgiasFacade.pesquisarTiposTurno(objPesquisa),pesquisarTurnosCount(objPesquisa));
	}

	public Long pesquisarTurnosCount(String objPesquisa) {
		return cirurgiasFacade.pesquisarTiposTurnoCount(objPesquisa);
	}
	
	public List<AghUnidadesFuncionais> listarUnidades(String objPesquisa) {
		return this.returnSGWithCount(cirurgiasFacade.buscarUnidadesFuncionaisCirurgia(objPesquisa),listarUnidadesCount(objPesquisa));
	}
	
	public Long listarUnidadesCount(String objPesquisa) {
		return cirurgiasFacade.contarUnidadesFuncionaisCirurgia(objPesquisa);
	}
	
	public void limparPesquisa() {
		limparCadastro();
		this.setMbcHorarioTurnoCirgBusca(new MbcHorarioTurnoCirg());
		this.getMbcHorarioTurnoCirgBusca().setId(new MbcHorarioTurnoCirgId());
		this.getMbcHorarioTurnoCirgBusca().setAghUnidadesFuncionais(this.carregarUnidadeFuncionalInicial());
		listaTurnos = null;
		this.setEmEdicao(false);
		this.setPesquisou(false);
		
	}
	
	private void limparCadastro() {
		MbcHorarioTurnoCirg mbcHorarioTurnoCirg = new MbcHorarioTurnoCirg();
		MbcHorarioTurnoCirgId mbcHorarioTurnoCirgId = new MbcHorarioTurnoCirgId();
		mbcHorarioTurnoCirg.setId(mbcHorarioTurnoCirgId);
		this.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		this.setEmEdicao(false);
		id = null;
	}
	
	private AghUnidadesFuncionais carregarUnidadeFuncionalInicial() {
		try {
			return blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(this.buscarNomeMicro());
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	private String buscarNomeMicro(){
		try {
			return super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e1) {
			this.LOG.error("Exceção capturada:", e1);
			return null;
		}
	}

	public void cancelarEdicao() {
		limparCadastro();
		this.pesquisar();
	}
	
	public void posDeleteActionSbUnidade() {
		limparCadastro();
		listaTurnos = null;
		this.setPesquisou(false);
	}
	
	public void gravar() {
//		if (!validarCamposObrigatorios()) {
//			return;
//		}
		
		boolean novo = this.getMbcHorarioTurnoCirg().getId() == null || this.getMbcHorarioTurnoCirg().getId().getUnfSeq() == null;
		try {
			//Seta a unidade cirurgica
			if (novo) {
				getMbcHorarioTurnoCirg().setAghUnidadesFuncionais(getMbcHorarioTurnoCirgBusca().getAghUnidadesFuncionais());
				getMbcHorarioTurnoCirg().setId(new MbcHorarioTurnoCirgId());
				getMbcHorarioTurnoCirg().getId().setUnfSeq(getMbcHorarioTurnoCirgBusca().getAghUnidadesFuncionais().getSeq());
				getMbcHorarioTurnoCirg().getId().setTurno(getMbcHorarioTurnoCirg().getMbcTurnos().getTurno());
			}
			this.cirurgiasFacade.persistirMbcHorarioTurnoCirg(this.getMbcHorarioTurnoCirg(), this.buscarNomeMicro());
			
			
			this.apresentarMsgNegocio(Severity.INFO,
					novo ? "MENSAGEM_SUCESSO_GRAVAR_TURNO_UNIDADE_CIRURGICA" : "MENSAGEM_SUCESSO_ALTERAR_TURNO_UNIDADE_CIRURGICA",
					this.getMbcHorarioTurnoCirg().getMbcTurnos().getDescricao());
		} catch (final BaseException e) {
			if(novo){
				this.getMbcHorarioTurnoCirg().getId().setUnfSeq(null); 
				this.getMbcHorarioTurnoCirg().getId().setTurno(null);
			}
			apresentarExcecaoNegocio(e);
		}
		this.limparCadastro();
		this.pesquisar();
	}
	
//	private boolean validarCamposObrigatorios() {
//		boolean validou = true;
//		validou = validarSuggestionObrigatoria();
//		if(getMbcHorarioTurnoCirg().getHorarioInicial() == null) {
//			apresentarMsgNegocioToControlFromResourceBundle("hrInicio",Severity.ERROR, "CAMPO_OBRIGATORIO","LABEL_HORARIO_INICIAL_TURNO_UNIDADE_CIRURGICA");
//			validou = false;
//		}
//		if(getMbcHorarioTurnoCirg().getHorarioFinal() == null) {
//			apresentarMsgNegocioToControlFromResourceBundle("hrFim",Severity.ERROR, "CAMPO_OBRIGATORIO","LABEL_HORARIO_FINAL_TURNO_UNIDADE_CIRURGICA");
//			validou = false;
//		}
//		
//		return validou;
//	}
	
//	private boolean validarSuggestionObrigatoria() {
//		boolean validou = true;
//		if(getMbcHorarioTurnoCirgBusca().getAghUnidadesFuncionais() == null) {
//			apresentarMsgNegocioToControlFromResourceBundle("sbUnidadeBusca",Severity.ERROR, "CAMPO_OBRIGATORIO","LABEL_UNIDADE_CIRURGICA_TURNO");
//			validou = false;
//		}
//		return validou;
//	}

	public void editar(final MbcHorarioTurnoCirg horarioTurnoCirg) {
		this.setMbcHorarioTurnoCirg(horarioTurnoCirg);
		this.setEmEdicao(true);
	}
	
	public void excluir() {
		try {
			this.cirurgiasFacade.excluirMbcHorarioTurnoCirg(idDelecao);			
			// FIXME: alterado pela criação da tabela MBC_TURNOS
						
			for (MbcHorarioTurnoCirg turno : listaTurnos){
				if (turno.getId().equals(this.idDelecao)){
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_TURNO_UNIDADE_CIRURGICA", turno.getMbcTurnos().getDescricao());
					break;
				}
			}
			
			this.pesquisar();
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void setMbcHorarioTurnoCirg(MbcHorarioTurnoCirg mbcHorarioTurnoCirg) {
		this.mbcHorarioTurnoCirg = mbcHorarioTurnoCirg;
	}

	public MbcHorarioTurnoCirg getMbcHorarioTurnoCirg() {
		return mbcHorarioTurnoCirg;
	}

	public void setIdDelecao(MbcHorarioTurnoCirgId idDelecao) {
		this.idDelecao = idDelecao;
	}

	public MbcHorarioTurnoCirgId getIdDelecao() {
		return idDelecao;
	}

	public void setMbcHorarioTurnoCirgBusca(MbcHorarioTurnoCirg mbcHorarioTurnoCirgBusca) {
		this.mbcHorarioTurnoCirgBusca = mbcHorarioTurnoCirgBusca;
	}

	public MbcHorarioTurnoCirg getMbcHorarioTurnoCirgBusca() {
		return mbcHorarioTurnoCirgBusca;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public List<MbcHorarioTurnoCirg> getListaTurnos() {
		return listaTurnos;
	}

	public void setListaTurnos(List<MbcHorarioTurnoCirg> listaTurnos) {
		this.listaTurnos = listaTurnos;
	}

	public MbcHorarioTurnoCirgId getId() {
		return id;
	}

	public void setId(MbcHorarioTurnoCirgId id) {
		this.id = id;
	}

	public boolean isPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

}